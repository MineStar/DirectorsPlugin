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

package de.minestar.director.data;

public class LoadedBlockChange {

    private int x;
    private int y;
    private int z;

    private int fromID;
    private int fromData;

    private int toID;
    private int toData;

    public LoadedBlockChange(int x, int y, int z, int fromID, int fromData, int toID, int toData) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.fromID = fromID;
        this.fromData = fromData;
        this.toID = toID;
        this.toData = toData;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getFromID() {
        return fromID;
    }

    public int getFromData() {
        return fromData;
    }

    public int getToID() {
        return toID;
    }

    public int getToData() {
        return toData;
    }

}
