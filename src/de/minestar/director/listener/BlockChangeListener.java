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

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import de.minestar.director.Main;
import de.minestar.director.area.Area;
import de.minestar.director.database.DatabaseHandler;

/**
 * Used to register all block changes and store them in the database, when they
 * are taken in the watched area
 * 
 * @author Meldanor
 * 
 */
public class BlockChangeListener extends BlockListener {

    private final DatabaseHandler dbHandler;

    public BlockChangeListener(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        boolean found = false;
        Block block = event.getBlock();
        Area foundArea = null;
        for (Area thisArea : Main.getAreaHandler().getAreas().values()) {
            System.out.println("checking: " + thisArea.getAreaName());
            if(thisArea.isBlockInArea(block)) {
                found = true;
                foundArea = thisArea;
              System.out.println("found");
                break;
            }
            System.out.println("not found");
        }

        if (!found)
            return;
        
        if (!dbHandler.addBlockBreak(event.getBlock(), event.getPlayer().getName().toLowerCase(), foundArea.getAreaName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Fehler beim Speichern der Änderung!");
            event.setCancelled(true);
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        boolean found = false;
        Block block = event.getBlock();
        Area foundArea = null;
        for (Area thisArea : Main.getAreaHandler().getAreas().values()) {
            System.out.println("checking: " + thisArea.getAreaName());
            if(thisArea.isBlockInArea(block)) {
                found = true;
                foundArea = thisArea;
              System.out.println("found");
                break;
            }
            System.out.println("not found");
        }

        if (!found)
            return;

        if (!dbHandler.addBlockPlace(event.getBlockPlaced(), event.getBlockReplacedState().getBlock(), event.getPlayer().getName().toLowerCase(), foundArea.getAreaName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Fehler beim Speichern der Änderung!");
            event.setCancelled(true);
        }
    }
}