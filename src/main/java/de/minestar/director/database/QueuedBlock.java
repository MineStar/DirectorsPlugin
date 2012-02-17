/*
 * Copyright (C) 2012 MineStar.de 
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

import java.text.SimpleDateFormat;
import java.util.Date;

import de.minestar.director.listener.DirectorBlock;

public class QueuedBlock {

    private DirectorBlock newBlock, oldBlock;
    private String playerName, areaName;

    private boolean placed = true;
    private String time;

    private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static String BROKEN = "b";
    private final static String PLACED = "p";

    public QueuedBlock(DirectorBlock newBlock, DirectorBlock oldBlock, String playerName, String areaName, boolean placed) {
        this.newBlock = newBlock;
        this.oldBlock = oldBlock;
        this.playerName = playerName;
        this.areaName = areaName;
        Date date = new Date();
        this.time = DATE_FORMATTER.format(date);
        this.placed = placed;
    }

    public DirectorBlock getNewBlock() {
        return newBlock;
    }

    public DirectorBlock getOldBlock() {
        return oldBlock;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getAreaName() {
        return areaName;
    }

    public boolean isPlaced() {
        return placed;
    }

    public String getMode() {
        return placed ? PLACED : BROKEN;
    }

    public String getTime() {
        return time;
    }

}
