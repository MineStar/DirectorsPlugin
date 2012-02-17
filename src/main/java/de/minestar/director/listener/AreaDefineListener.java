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

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.minestar.director.Main;
import de.minestar.director.area.Area;
import de.minestar.director.area.AreaHandler;
import de.minestar.director.database.DatabaseHandler;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class AreaDefineListener implements Listener {

    private HashSet<String> inSelectMode = new HashSet<String>();
    private HashMap<String, Block[]> selection = new HashMap<String, Block[]>();
    private DatabaseHandler dbHandler;
    private AreaHandler aHandler;

    public AreaDefineListener(DatabaseHandler dbHandler, AreaHandler aHandler) {
        this.dbHandler = dbHandler;
        this.aHandler = aHandler;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        String playerName = player.getName().toLowerCase();
        // if player havn't used command "cselect" before
        if (!event.hasBlock() || !inSelectMode.contains(playerName))
            return;

        // //////////////////////////////////////
        // BEGIN : WORKAROUND FOR DOUBLESTEPS
        if (event.hasBlock() && event.isBlockInHand()) {
            boolean found = false;
            Block block = event.getClickedBlock();
            Area foundArea = null;
            for (Area thisArea : aHandler.getAreas().values()) {
                if (thisArea.isBlockInArea(block)) {
                    found = true;
                    foundArea = thisArea;
                    break;
                }
            }
            if (found) {
                if (player.getItemInHand().getTypeId() == Material.STEP.getId()) {
                    Block relative = event.getClickedBlock();
                    if (event.getBlockFace() == BlockFace.UP && relative.getTypeId() == Material.STEP.getId() && relative.getData() == player.getItemInHand().getDurability()) {
                        DirectorBlock oldBlock = new DirectorBlock(relative);
                        DirectorBlock newBlock = new DirectorBlock(oldBlock.getX(), oldBlock.getY(), oldBlock.getZ(), Material.DOUBLE_STEP.getId(), relative.getData(), relative.getWorld().getName());
                        if (!dbHandler.addBlockPlace(newBlock, oldBlock, player.getName().toLowerCase(), foundArea.getAreaName())) {
                            PlayerUtils.sendError(player, Main.NAME, "Fehler beim Speichern der Änderung!");
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
        // END : WORKAROUND FOR DOUBLESTEPS
        // //////////////////////////////////////

        // get selections
        Block[] corners = selection.get(playerName);
        if (corners == null)
            corners = new Block[2];

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            corners[0] = event.getClickedBlock();
            PlayerUtils.sendSuccess(player, Main.NAME, "1. Block selektiert!");
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            corners[1] = event.getClickedBlock();
            PlayerUtils.sendSuccess(player, Main.NAME, "2. Block selektiert!");
        }

        // store selections
        selection.put(playerName, corners);
    }

    /**
     * @return Return true when player is NOW in selection mode(he wasn't it
     *         before!)
     */
    public boolean switchSelectionMode(String playerName) {
        playerName = playerName.toLowerCase();
        if (!inSelectMode.remove(playerName)) {
            inSelectMode.add(playerName);
            return true;
        } else
            return false;
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        boolean found = false;
        Block block = event.getBlockClicked();
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
        DirectorBlock newBlock = new DirectorBlock(event.getBlockClicked().getRelative(event.getBlockFace()));
        int id = Material.WATER.getId();
        if (player.getItemInHand().getTypeId() == Material.LAVA_BUCKET.getId())
            id = Material.LAVA.getId();

        newBlock.setID(id);
        newBlock.setSubID((byte) 8);
        DirectorBlock oldBlock = new DirectorBlock(newBlock.getX(), newBlock.getY(), newBlock.getZ(), 0, (byte) 0, event.getBlockClicked().getWorld().getName());

        if (!dbHandler.addBlockPlace(newBlock, oldBlock, player.getName().toLowerCase(), foundArea.getAreaName())) {
            PlayerUtils.sendError(player, Main.NAME, "Fehler beim Speichern der Änderung!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        boolean found = false;
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
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
        if (!dbHandler.addBlockBreak(event.getBlockClicked().getRelative(event.getBlockFace()), player.getName().toLowerCase(), foundArea.getAreaName())) {
            PlayerUtils.sendError(player, Main.NAME, "Fehler beim Speichern der Änderung!");
            event.setCancelled(true);
        }
    }

    public Chunk[] getCorners(String playerName) {
        playerName = playerName.toLowerCase();
        Block[] corners = selection.get(playerName);
        if (corners == null || corners[0] == null || corners[1] == null)
            return null;
        else
            return new Chunk[]{corners[0].getChunk(), corners[1].getChunk()};
    }

}
