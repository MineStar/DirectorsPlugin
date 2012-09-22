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

package de.minestar.director.threading;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.server.Packet53BlockChange;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.minestar.director.Core;
import de.minestar.director.data.LoadedBlockChange;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class BlockSetFromFileThread implements Runnable {

    private final static int BPT = 20;

    private Queue<LoadedBlockChange> blocks;
    private World world;

    private int id = Integer.MAX_VALUE;

    private int x, y, z = 0;
    private int newId, oldId = 0;
    private byte newData, oldData = 0;

    private Block block = null;

    private CraftWorld cWorld;

    private List<Block> setBlocks = new LinkedList<Block>();

    private int xD;
    private int zD;

    private int startX;
    private int startZ;

    public BlockSetFromFileThread(World world, Queue<LoadedBlockChange> blocks, int startX, int startZ, int xD, int zD) {
        this.blocks = blocks;
        this.world = world;
        this.cWorld = ((CraftWorld) world);
        this.xD = xD;
        this.zD = zD;
        this.startX = startX;
        this.startZ = startZ;
    }

    @Override
    public void run() {
        primitiveSetting();
    }

    public void setID(int id) {
        if (this.id == Integer.MAX_VALUE)
            this.id = id;
        else
            ConsoleUtils.printError(Core.NAME, "id was already set for thread!");
    }

    private void primitiveSetting() {
        try {
            for (int i = 0; i < BPT; ++i) {

                if (blocks.isEmpty()) {
                    Bukkit.getScheduler().cancelTask(id);
                    for (Player player : Bukkit.getOnlinePlayers())
                        PlayerUtils.sendSuccess(player, Core.NAME, "Der Bau ist abgeschlossen!");
                    ConsoleUtils.printInfo(Core.NAME, "Area wurde fertiggestellt!");
                    return;
                }
                LoadedBlockChange loadedBlock = blocks.poll();
                x = loadedBlock.getX();
                y = loadedBlock.getY();
                z = loadedBlock.getZ();
                newId = loadedBlock.getToID();
                newData = (byte) loadedBlock.getToData();
                oldId = loadedBlock.getFromID();
                oldData = (byte) loadedBlock.getFromData();
                block = world.getBlockAt(startX + (xD * x), y, startZ + (zD * z));
                block.setTypeIdAndData(newId, newData, false);
                setBlocks.add(block);
            }
            net.minecraft.server.World nativeWorld = ((CraftWorld) world).getHandle();
            for (Block setBlock : setBlocks) {
                x = setBlock.getX();
                y = setBlock.getY();
                z = setBlock.getZ();
                nativeWorld.notify(x, y, z);
                nativeWorld.applyPhysics(x, y, z, setBlock.getTypeId());
                for (Player player : Bukkit.getOnlinePlayers())
                    ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet53BlockChange(x, y, z, cWorld.getHandle()));
            }
            setBlocks.clear();
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't read ResultSet!");
        }
    }
}
