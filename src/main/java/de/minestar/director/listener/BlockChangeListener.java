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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import de.minestar.director.Main;
import de.minestar.director.area.Area;
import de.minestar.director.area.AreaHandler;
import de.minestar.director.database.DatabaseHandler;
import de.minestar.minestarlibrary.utils.PlayerUtils;

/**
 * Used to register all block changes and store them in the database, when they
 * are taken in the watched area
 * 
 * @author Meldanor
 * 
 */
public class BlockChangeListener implements Listener {

    private final DatabaseHandler dbHandler;
    private AreaHandler aHandler;

    public BlockChangeListener(DatabaseHandler dbHandler, AreaHandler aHandler) {
        this.dbHandler = dbHandler;
        this.aHandler = aHandler;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        boolean found = false;
        Block block = event.getBlock();
        Area foundArea = null;
        for (Area thisArea : aHandler.getAreas().values()) {
            if (thisArea.isBlockInArea(block)) {
                found = true;
                foundArea = thisArea;
                break;
            }
        }
        if (!found)
            return;

        Player player = event.getPlayer();
        if (!dbHandler.addBlockBreak(event.getBlock(), player.getName().toLowerCase(), foundArea.getAreaName())) {
            PlayerUtils.sendError(player, Main.NAME, "Fehler beim Speichern der Änderung!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        boolean found = false;
        Block block = event.getBlock();
        Area foundArea = null;
        for (Area thisArea : aHandler.getAreas().values()) {
            if (thisArea.isBlockInArea(block)) {
                found = true;
                foundArea = thisArea;
                break;
            }
        }

        if (!found)
            return;

        Player player = event.getPlayer();

        /**
         * BEGIN - NASTY FIX FOR DOUBLESTEPS, DAMN BUKKIT!!!
         */
        ItemStack inHand = player.getItemInHand();
        DirectorBlock newBlock = new DirectorBlock(event.getBlock());
        DirectorBlock oldBlock = new DirectorBlock(newBlock.getX(), newBlock.getY(), newBlock.getZ(), event.getBlockReplacedState().getTypeId(), event.getBlockReplacedState().getRawData(), event.getBlockPlaced().getWorld().getName());
        if (inHand.getTypeId() == Material.STEP.getId()) {
            byte data = (byte) inHand.getDurability();
            if (data != newBlock.getSubID()) {
                // increase y with one, because it's another subid
                newBlock.setY(newBlock.getY() + 1);
                // update the subid with the one in the players hand
                newBlock.setSubID(data);
                // normally we need to check agains liquids here, but bukkit is
                // not able to get the current blocktype/blockdata correctly...
                // result is: we set the old blocktype to air
                oldBlock.setID(0);
                oldBlock.setSubID((byte) 0);
            }
        }
        /**
         * END - NASTY FIX FOR DOUBLESTEPS, DAMN BUKKIT!!!
         */

        if (!dbHandler.addBlockPlace(newBlock, oldBlock, player.getName().toLowerCase(), foundArea.getAreaName())) {
            PlayerUtils.sendError(player, Main.NAME, "Fehler beim Speichern der Änderung!");
            event.setCancelled(true);
        }
    }
}