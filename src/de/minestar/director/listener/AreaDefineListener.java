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
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class AreaDefineListener extends PlayerListener {

    private HashSet<String> inSelectMode = new HashSet<String>();
    private HashMap<String, Block[]> selection = new HashMap<String, Block[]>();

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        String playerName = event.getPlayer().getName().toLowerCase();
        // if player havn't used command "cselect" before
        if (!event.hasBlock() || !inSelectMode.contains(playerName))
            return;

        // get selections
        Block[] corners = selection.get(playerName);
        if (corners == null)
            corners = new Block[2];

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            corners[0] = event.getClickedBlock();
        else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            corners[1] = event.getClickedBlock();

        // store selections
        selection.put(playerName, corners);
    }

    /**
     * @return Return true when player is NOW in selection mode(he wasn't it before!)
     */
    public boolean switchSelectionMode(String playerName) {
        playerName = playerName.toLowerCase();
        if (!inSelectMode.remove(playerName)) {
            inSelectMode.add(playerName);
            return true;
        } else
            return false;
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
