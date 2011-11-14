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

import de.minestar.director.Main;

public class DatabaseHandler {

    private final DatabaseConnection connection;

    public DatabaseHandler(String host, int port, String database, String userName, String password) {
        connection = new DatabaseConnection(host, port, database, userName, password);
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
        connection.getConnection().setAutoCommit(false);
        checkTables();
    }

    private void checkTables() throws Exception {
        // @formatter:off
        connection.getConnection().createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `directorblockdata` ("
                        + "`Id` int(11) NOT NULL AUTO_INCREMENT,"
                        + "`WorldName` varchar(255) DEFAULT NULL,"
                        + "`BlockX` int(11) DEFAULT NULL,"
                        + "`BlockY` int(11) DEFAULT NULL,"
                        + "`BlockZ` int(11) DEFAULT NULL,"
                        + "`NewBlockId` int(11) DEFAULT NULL,"
                        + "`NewBlockData` int(11) DEFAULT NULL,"
                        + " `OldBlockId` int(11) DEFAULT NULL,"
                        + "`OldBlockData` int(11) DEFAULT NULL,"
                        + " `DateTime` datetime DEFAULT NULL,"
                        + "`PlayerName` varchar(255) DEFAULT NULL,"
                        + "`EventType` char(1) DEFAULT NULL,"
                        + "`AreaName` varchar(255) DEFAULT NULL,"
                        + " PRIMARY KEY (`Id`)"
                        + ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");

        connection.getConnection().createStatement().execute(
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

}
