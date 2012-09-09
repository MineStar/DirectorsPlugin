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

package de.minestar.director.area;

import java.awt.Point;

import org.bukkit.Chunk;
import org.bukkit.craftbukkit.CraftChunk;

public class DirectorChunkSnapshot {
    private final int x;
    private final int z;
    private final String worldname;
    private final byte[] blockData;

    public static DirectorChunkSnapshot getSnapshot(Chunk chunk) {
        // TODO: Updaten!
//        net.minecraft.server.Chunk nativeChunk = ((CraftChunk) chunk).getHandle();        
        byte[] data = new byte[81920];
//        nativeChunk.getData(data, 0, 0, 0, 16, 128, 16, 0);
        return new DirectorChunkSnapshot(chunk.getX(), chunk.getZ(), chunk.getWorld().getName(), data);
    }

    public byte[] getAllData() {
        return this.blockData;
    }
    
    public DirectorChunkSnapshot(int x, int z, String worldname, byte[] blockData) {
        this.x = x;
        this.z = z;
        this.worldname = worldname;
        this.blockData = blockData;
    }

    public String getWorldname() {
        return this.worldname;
    }

    public Point getCoordinates() {
        return new Point(this.x, this.z);
    }

    public int getBlockTypeId(int x, int y, int z) {
        return this.blockData[(x << 11 | z << 7 | y)] & 0xFF;
    }

    public int getBlockData(int x, int y, int z) {
        int off = (x << 10 | z << 6 | y >> 1) + 32768;
        return (y & 0x1) == 0 ? this.blockData[off] & 0xF : this.blockData[off] >> 4 & 0xF;
    }
}
