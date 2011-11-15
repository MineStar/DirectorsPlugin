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

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

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
        // TODO Add blockChange handling conected to dbHandler
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        // TODO Add blockChange handling conected to dbHandler
    }

}
