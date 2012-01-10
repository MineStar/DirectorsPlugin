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

import de.minestar.director.Main;
import de.minestar.director.area.Area;
import de.minestar.director.listener.DirectorBlock;
import de.minestar.director.threading.BatchRunnable;

public class DatabaseHandler {

    // How many BlockChanges are queued before stored in database
    private static final int QUEUE_BUFFER_SIZE = 5;

    private final DatabaseConnection conHandler;

    // PREPARED STATEMENTS
    private PreparedStatement getAllAreas, addArea, addBlockChanges;
    // /PREPARED STATEMENTS

    // THREADS
    private BatchRunnable batchThread;

    public List<QueuedBlock> queue = new LinkedList<QueuedBlock>();

    public DatabaseHandler(String host, int port, String database, String userName, String password) {

        conHandler = new DatabaseConnection(host, port, database, userName, password);

        // Delete login information
        host = null;
        port = 0;
        database = null;
        userName = null;
        password = null;
        System.gc();

        try {
            init();
        } catch (Exception e) {
            Main.printToConsole("ERROR! Can't init databasehandler!");
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        checkTables();
        createStatements();
        batchThread = new BatchRunnable(addBlockChanges);
    }

    /**
     * Check if the tables are existing and if not, they will be created here.
     * Place all logic concerning tables here!
     * 
     * @throws Exception
     */
    private void checkTables() throws Exception {
        Connection con = conHandler.getConnection();
        // @formatter:off
        con.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `directorblockdata` ("
                        + "`Id` int(11) NOT NULL AUTO_INCREMENT,"
                        + "`WorldName` varchar(255) DEFAULT NULL,"
                        + "`BlockX` int(11) DEFAULT NULL,"
                        + "`BlockY` int(11) DEFAULT NULL,"
                        + "`BlockZ` int(11) DEFAULT NULL,"
                        + "`NewBlockId` int(11) DEFAULT NULL,"
                        + "`NewBlockData` int(11) DEFAULT NULL,"
                        + "`OldBlockId` int(11) DEFAULT NULL,"
                        + "`OldBlockData` int(11) DEFAULT NULL,"
                        + "`DateTime` datetime DEFAULT NULL,"
                        + "`PlayerName` varchar(255) DEFAULT NULL,"
                        + "`EventType` char(1) DEFAULT NULL,"
                        + "`AreaName` varchar(255) DEFAULT NULL,"
                        + " PRIMARY KEY (`Id`)"
                        + ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");

        con.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `directorareadata` ("
                        + "`Id` int(11) NOT NULL AUTO_INCREMENT,"
                        + "`AreaName` varchar(255) DEFAULT NULL,"
                        + "`AreaWorld` varchar(255) DEFAULT NULL,"
                        + "`Chunk1X` int(11) DEFAULT NULL,"
                        + "`Chunk1Z` int(11) DEFAULT NULL,"
                        + "`Chunk2X` int(11) DEFAULT NULL,"
                        + "`Chunk2Z` int(11) DEFAULT NULL,"
                        + "`AreaOwner` varchar(255) DEFAULT NULL,"
                        + " PRIMARY KEY (`Id`)"
                        + ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");
        // @formatter:on
    }

    public void closeConnection() {
        conHandler.closeConnection();
    }

    /**
     * Initiate all prepared statements they were needed often by the plugin.
     * Put here the sql statement logic
     * 
     * @throws Exception
     */
    private void createStatements() throws Exception {

        Connection con = conHandler.getConnection();
        //@formatter:off
       
        getAllAreas = con.prepareStatement("SELECT * FROM directorareadata ORDER BY ID asc");
        
        addArea = con.prepareStatement("INSERT INTO directorareadata" +
                                        "(AreaName, AreaWorld, Chunk1X, Chunk1Z, Chunk2X, Chunk2Z, AreaOwner) " +
                                        "VALUES(?, ?, ?, ?, ?, ?, ?)");
        
        // Create queue prepared statement
        // StringBuilder buffer = CharNumber of Head + 26 signs for each line
        StringBuilder sBuilder = new StringBuilder(170 + (QUEUE_BUFFER_SIZE * 26));
        sBuilder.append("INSERT INTO directorblockdata (WorldName, BlockX, BlockY, BlockZ, NewBlockId, NewBlockData, OldBlockId, OldBlockData, DateTime, PlayerName, EventType, AreaName) VALUES ");
        
        for ( int i = 0 ; i < QUEUE_BUFFER_SIZE ; ++i)
            sBuilder.append("(?,?,?,?,?,?,?,?,?,?,?,?),");
        addBlockChanges = con.prepareStatement(sBuilder.substring(0, sBuilder.length()-1) + ";");
        //@formatter:on
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
            Main.printToConsole("Error! Can't save a block change to database!");
            e.printStackTrace();
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
            Main.printToConsole("Error! Can't save a block break to database!");
            e.printStackTrace();
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
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), batchThread, 1);
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
            sBuilder.append("INSERT INTO directorblockdata (WorldName, BlockX, BlockY, BlockZ, NewBlockId, NewBlockData, OldBlockId, OldBlockData, DateTime, PlayerName, EventType, AreaName) VALUES ");

            DirectorBlock oldBlock = null;
            DirectorBlock newBlock = null;

            // fill query with data
            for (QueuedBlock event : queue) {
                oldBlock = event.getOldBlock();
                newBlock = event.getNewBlock();
                sBuilder.append('(');
                appendString(sBuilder, oldBlock.getWorldName());
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
                appendString(sBuilder, event.getTime());
                sBuilder.append(",");
                appendString(sBuilder, event.getPlayerName());
                sBuilder.append(",");
                appendString(sBuilder, event.getMode());
                sBuilder.append(",");
                appendString(sBuilder, event.getAreaName());
                sBuilder.append("),");
            }
            Statement s = conHandler.getConnection().createStatement();
            s.executeUpdate(sBuilder.substring(0, sBuilder.length() - 1) + ';');

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Used for setting a String in a MySQL Query.
     * 
     * @param sBuilder
     * @param string
     */
    private void appendString(StringBuilder sBuilder, String string) {
        sBuilder.append('\'');
        sBuilder.append(string);
        sBuilder.append('\'');
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
            Main.printToConsole("Error! Can't save the area '" + newArea.getAreaName() + "' to database!");
            e.printStackTrace();
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
                    Main.printToConsole("Could not find world '" + worldName + "' for area '" + areaName + "'. Skipping this area.");
                    continue;
                }

                // GET CHUNKS
                chunk1 = world.getChunkAt(x1, z1);
                chunk2 = world.getChunkAt(x2, z2);
                if (chunk1 == null || chunk2 == null) {
                    Main.printToConsole("Could not find both chunks for area '" + areaName + "'. Skipping this area.");
                    continue;
                }

                // PUT IN MAP
                map.put(areaName.toLowerCase(), new Area(areaName, areaOwner, worldName, chunk1, chunk2));
            }
        } catch (SQLException e) {
            Main.printToConsole("Error! Can't load areas from database!");
            e.printStackTrace();
        }

        return map;
    }
}
