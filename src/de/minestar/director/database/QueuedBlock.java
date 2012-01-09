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
    public String time = "";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public QueuedBlock(DirectorBlock newBlock, DirectorBlock oldBlock, String playerName, String areaName) {
        this.newBlock = newBlock;
        this.oldBlock = oldBlock;
        this.playerName = playerName;
        this.areaName = areaName;
        Date date = new Date();
        this.time = dateFormat.format(date);
    }
    public void setBroken() {
        this.placed = false;
    }

    public DirectorBlock getNewBlock() {
        return newBlock;
    }

    public void setNewBlock(DirectorBlock newBlock) {
        this.newBlock = newBlock;
    }

    public DirectorBlock getOldBlock() {
        return oldBlock;
    }

    public void setOldBlock(DirectorBlock oldBlock) {
        this.oldBlock = oldBlock;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }
    public String getMode() {
        if (placed)
            return "p";
        else
            return "b";
    }
}
