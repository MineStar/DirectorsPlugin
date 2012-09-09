/*
 * Copyright (C) 2011 MineStar.de 
 * 
 * This file is part of DirectorPlugin.
 * 
 * DirectorPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * DirectorPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DirectorPlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.director.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.director.Core;
import de.minestar.director.area.Area;
import de.minestar.director.listener.DirectorBlock;
import de.minestar.director.threading.BatchRunnable;
import de.minestar.minestarlibrary.database.AbstractDatabaseHandler;
import de.minestar.minestarlibrary.database.AbstractMySQLHandler;
import de.minestar.minestarlibrary.database.DatabaseConnection;
import de.minestar.minestarlibrary.database.DatabaseType;
import de.minestar.minestarlibrary.database.DatabaseUtils;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class DatabaseHandler extends AbstractMySQLHandler {

    // How many BlockChanges are queued before stored in database
    private static final int QUEUE_BUFFER_SIZE = 100;

    // PREPARED STATEMENTS
    private PreparedStatement getAllAreas, addArea, addBlockChanges, getAreaBlocks;
    // /PREPARED STATEMENTS

    // THREADS
    private BatchRunnable batchThread;

    public List<QueuedBlock> queue = new LinkedList<QueuedBlock>();

    public DatabaseHandler(String pluginName, File SQLConfigFile) {
        super(pluginName, SQLConfigFile);
    }

//    public DatabaseHandler(String pluginName, File dataFolder) {
//        super(pluginName, dataFolder);
//        batchThread = new BatchRunnable(addBlockChanges);
//    }

//    @Override
//    protected DatabaseConnection createConnection(String pluginName, File dataFolder) throws Exception {
//        File configFile = new File(dataFolder, "sqlConfig.yml");
//        if (!configFile.exists()) {
//            DatabaseUtils.createDatabaseConfig(DatabaseType.MySQL, configFile, pluginName);
//            return null;
//        }
//        YamlConfiguration config = new YamlConfiguration();
//        config.load(configFile);
//        return new DatabaseConnection(pluginName, DatabaseType.MySQL, config);
//    }

    @Override
    protected void createStructure(String pluginName, Connection con) throws Exception {
        DatabaseUtils.createStructure(getClass().getResourceAsStream("/structure.sql"), con, pluginName);
    }

    @Override
    protected void createStatements(String pluginName, Connection con) throws Exception {
        getAllAreas = con.prepareStatement("SELECT * FROM directorareadata ORDER BY ID asc");

        addArea = con.prepareStatement("INSERT INTO directorareadata" + "(AreaName, AreaWorld, Chunk1X, Chunk1Z, Chunk2X, Chunk2Z, AreaOwner) " + "VALUES(?, ?, ?, ?, ?, ?, ?)");

        getAreaBlocks = con.prepareStatement("SELECT BlockX, BlockY, BlockZ, NewBlockId, NewBlockData, OldBlockId, OldBlockData, DateTime, EventType FROM directorblockdata WHERE AreaName = ?");

        // Create queue prepared statement
        // StringBuilder buffer = CharNumber of Head + 26 signs for each line
        StringBuilder sBuilder = new StringBuilder(170 + (QUEUE_BUFFER_SIZE * 26));
        sBuilder.append("INSERT INTO directorblockdata (WorldName, BlockX, BlockY, BlockZ, NewBlockId, NewBlockData, OldBlockId, OldBlockData, DateTime, PlayerName, EventType, AreaName) VALUES ");

        for (int i = 0; i < QUEUE_BUFFER_SIZE; ++i)
            sBuilder.append("(?,?,?,?,?,?,?,?,?,?,?,?),");
        sBuilder.setCharAt(sBuilder.length() - 1, ';');
        addBlockChanges = con.prepareStatement(sBuilder.toString());
    }

    /**
     * Stores a block change to the database. This will appear when someone
     * broke the block or place another block at the air. It buffers the block
     * change until the maximum size of queue is reached
     * 
     * @param newBlock
     *            The new block data
     * @param currentBlock
     *            The block that was replaced by newBlock
     * @param playerName
     *            The player who changed the block
     * @param areaName
     *            The watched area name
     * @return True when it was succesfull.
     */
    public boolean addBlockPlace(DirectorBlock newBlock, DirectorBlock oldBlock, String playerName, String areaName) {
        try {
            queue.add(new QueuedBlock(newBlock, oldBlock, playerName, areaName, true));

            checkQueue();

            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't save a block place to database! Player=" + playerName + ", Area=" + areaName + ", NewBlock=" + newBlock + ", OldBLock=" + oldBlock);
            return false;
        }
    }

    public boolean addBlockBreak(Block oldBlock, String playerName, String areaName) {

        try {
            DirectorBlock oldDirBlock = new DirectorBlock(oldBlock);
            DirectorBlock newBlock = new DirectorBlock(oldBlock.getX(), oldBlock.getY(), oldBlock.getZ(), 0, (byte) 0, oldBlock.getWorld().getName());
            QueuedBlock event = new QueuedBlock(newBlock, oldDirBlock, playerName, areaName, false);
            queue.add(event);

            checkQueue();

            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't save a block break to database! Player=" + playerName + ",Area=" + areaName + ",OldBLock=" + oldBlock);
            return false;
        }
    }

    private void checkQueue() {
        if (queue.size() >= QUEUE_BUFFER_SIZE)
            runQueue();
    }

    private void runQueue() {
        batchThread.copyList(queue);
        queue.clear();
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Core.getInstance(), batchThread, 1);
    }

    /**
     * Run when server is shutting down or
     */
    public void flushQueue() {
        // no elements in buffer
        if (queue.size() == 0)
            return;

        try {
            // 170 = Head, 150 estimated average length of one line
            StringBuilder sBuilder = new StringBuilder(170 + (queue.size() * 125));
            // Head of query
            sBuilder.append("INSERT INTO directorblockdata (WorldName, BlockX, BlockY, BlockZ, NewBlockId, NewBlockData, PlayerName, EventType, AreaName) VALUES ");

            DirectorBlock oldBlock = null;
            DirectorBlock newBlock = null;

            // fill query with data
            for (QueuedBlock event : queue) {
                oldBlock = event.getOldBlock();
                newBlock = event.getNewBlock();
                sBuilder.append('(');
                DatabaseUtils.appendSQLString(sBuilder, oldBlock.getWorldName());
                sBuilder.append(',');
                sBuilder.append(oldBlock.getX());
                sBuilder.append(",");
                sBuilder.append(oldBlock.getY());
                sBuilder.append(",");
                sBuilder.append(oldBlock.getZ());
                sBuilder.append(",");
                sBuilder.append(newBlock.getID());
                sBuilder.append(",");
                sBuilder.append(newBlock.getSubID());
                sBuilder.append(",");
                sBuilder.append(oldBlock.getID());
                sBuilder.append(",");
                sBuilder.append(oldBlock.getSubID());
                sBuilder.append(",");
                DatabaseUtils.appendSQLString(sBuilder, event.getTime());
                sBuilder.append(",");
                DatabaseUtils.appendSQLString(sBuilder, event.getPlayerName());
                sBuilder.append(",");
                DatabaseUtils.appendSQLString(sBuilder, event.getMode());
                sBuilder.append(",");
                DatabaseUtils.appendSQLString(sBuilder, event.getAreaName());
                sBuilder.append("),");
            }
            Statement s = dbConnection.getConnection().createStatement();
            s.executeUpdate(sBuilder.substring(0, sBuilder.length() - 1) + ';');

        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't flush the queue!");
        }
    }

    public boolean saveArea(Area newArea) {
        try {
            addArea.setString(1, newArea.getAreaName());
            addArea.setString(2, newArea.getWorldName());
            addArea.setInt(3, newArea.getMinChunk().x);
            addArea.setInt(4, newArea.getMinChunk().y);
            addArea.setInt(5, newArea.getMaxChunk().x);
            addArea.setInt(6, newArea.getMaxChunk().y);
            addArea.setString(7, newArea.getAreaOwner());
            return addArea.executeUpdate() == 1;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't save the area '" + newArea.getAreaName() + "' to database!");
            return false;
        }
    }

    public TreeMap<String, Area> loadAreas() {
        TreeMap<String, Area> map = new TreeMap<String, Area>();

        try {
            ResultSet set = getAllAreas.executeQuery();
            String areaName, worldName, areaOwner;
            int x1, z1, x2, z2;
            World world;
            Chunk chunk1, chunk2;

            while (set.next()) {
                areaName = set.getString("AreaName");
                worldName = set.getString("AreaWorld");
                areaOwner = set.getString("AreaOwner");
                x1 = set.getInt("Chunk1X");
                z1 = set.getInt("Chunk1Z");
                x2 = set.getInt("Chunk2X");
                z2 = set.getInt("Chunk2Z");

                // GET WORLD
                world = Bukkit.getServer().getWorld(worldName);
                if (world == null) {
                    ConsoleUtils.printError(Core.NAME, "Could not find world '" + worldName + "' for area '" + areaName + "'. Skipping this area.");
                    continue;
                }

                // GET CHUNKS
                chunk1 = world.getChunkAt(x1, z1);
                chunk2 = world.getChunkAt(x2, z2);
                if (chunk1 == null || chunk2 == null) {
                    ConsoleUtils.printError(Core.NAME, "Could not find both chunks for area '" + areaName + "'. Skipping this area.");
                    continue;
                }

                // PUT IN MAP
                map.put(areaName.toLowerCase(), new Area(areaName, areaOwner, worldName, chunk1, chunk2));
            }
        } catch (SQLException e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't load areas from database!");
        }

        return map;
    }

    public ResultSet getBlocks(String areaName) {
        try {
            getAreaBlocks.setString(1, areaName);
            return getAreaBlocks.executeQuery();
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't load blocks from the area '" + areaName + "'!");
            return null;
        }
    }

}
