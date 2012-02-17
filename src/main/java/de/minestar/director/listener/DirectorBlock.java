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

package de.minestar.director.listener;

import org.bukkit.block.Block;

public class DirectorBlock {
    private int x, y, z;
    private int ID;
    private byte SubID;
    private String worldName;

    public DirectorBlock(int x, int y, int z, int ID, byte SubID, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ID = ID;
        this.SubID = SubID;
        this.worldName = worldName;
    }

    public DirectorBlock(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.ID = block.getTypeId();
        this.SubID = block.getData();
        this.worldName = block.getWorld().getName();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public byte getSubID() {
        return SubID;
    }

    public void setSubID(byte subID) {
        SubID = subID;
    }

    public String getWorldName() {
        return worldName;
    }
}
