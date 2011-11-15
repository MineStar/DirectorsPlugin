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

import org.bukkit.block.Block;

import de.minestar.director.Main;

public class DatabaseHandler {

    private final DatabaseConnection conHandler;

    // PREPARED STATEMENTS
    private PreparedStatement addBlockChange;
    // /PREPARED STATEMENTS

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
                        + "`EventType` tinyint(1) DEFAULT NULL,"
                        + "`AreaName` varchar(255) DEFAULT NULL,"
                        + " PRIMARY KEY (`Id`)"
                        + ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");

        con.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `directorareadata` ("
                        + "`Id` int(11) NOT NULL AUTO_INCREMENT,"
                        + "`AreaName` varchar(255) DEFAULT NULL,"
                        + "`AreaWorld` int(11) DEFAULT NULL,"
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
        addBlockChange = con.prepareStatement("INSERT INTO directorblockdata" +
        		                              "(WorldName, BlockX, BlockY, BlockZ, NewBlockId, NewBlockData, OldBlockId, OldBlockData, DateTime, PlayerName, EventType, AreaName) " +
        		                              "VALUES(?,?,?,?,?,?,?,?,NOW(),?,?,?)");
        
        //@formatter:on
    }

    /**
     * Stores a block change to the database. This will appear when someone
     * broke the block or place another block at the air.
     * 
     * @param newBlock
     *            The new block data
     * @param currentBlock
     *            The block that was replaced by newBlock
     * @param playerName
     *            The player who changed the block
     * @param eventType
     *            The type
     * @param areaName
     *            The watched area name
     * @return True when it was succesfull.
     */
    public boolean addBlockChange(Block newBlock, Block currentBlock, String playerName, int eventType, String areaName) {

        try {
            addBlockChange.setString(1, newBlock.getWorld().toString().toLowerCase());
            addBlockChange.setInt(2, newBlock.getX());
            addBlockChange.setInt(3, newBlock.getY());
            addBlockChange.setInt(4, newBlock.getZ());
            addBlockChange.setInt(5, newBlock.getTypeId());
            addBlockChange.setInt(6, newBlock.getData());
            addBlockChange.setInt(7, currentBlock.getTypeId());
            addBlockChange.setInt(8, currentBlock.getData());
            addBlockChange.setString(9, playerName);
            addBlockChange.setInt(10, eventType);
            addBlockChange.setString(11, areaName);
            return addBlockChange.executeUpdate() == 1;
        } catch (Exception e) {
            Main.printToConsole("Error! Can't save a block change to database!");
            e.printStackTrace();
            return false;
        }
    }

}
