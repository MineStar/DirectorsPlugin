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

package de.minestar.director.commands.dir;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import de.minestar.director.Main;
import de.minestar.director.area.Area;
import de.minestar.director.area.AreaHandler;
import de.minestar.director.database.DatabaseHandler;
import de.minestar.director.listener.AreaDefineListener;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class AreaSaveCommand extends AbstractCommand {

    private AreaDefineListener adListener;
    private AreaHandler aHandler;
    private DatabaseHandler dbHandler;

    public AreaSaveCommand(String syntax, String arguments, String node, AreaDefineListener adListener, DatabaseHandler dbHandler, AreaHandler aHandler) {
        super(Main.NAME, syntax, arguments, node);
        this.adListener = adListener;
        this.dbHandler = dbHandler;
        this.aHandler = aHandler;
        this.description = "Erzeugt eine Area die von Director ueberwacht wird.";
    }

    @Override
    public void execute(String[] args, Player player) {

        String areaName = args[0];
        if (aHandler.areaExists(areaName)) {
            PlayerUtils.sendError(player, pluginName, "Ein Area mit dem Namen '" + areaName + "' existiert bereits!");
            return;
        }

        Chunk[] selection = adListener.getCorners(player.getName());
        if (selection == null) {
            PlayerUtils.sendError(player, pluginName, "Du musst zwei Bloecke auswaehlen!");
            return;
        }

        // CREATE AREA
        Area newArea = new Area(areaName, player.getName(), selection[0].getWorld().getName(), selection[0], selection[1]);

        // Check if an existing area is inside the new area
        for (Area otherArea : aHandler.getAreas().values()) {
            if (otherArea.intersectsArea(newArea)) {
                PlayerUtils.sendError(player, pluginName, "Area '" + otherArea.getAreaName() + "' schneidet die neue Area!");
                return;
            }
        }

        // ADD AREA
        aHandler.addArea(newArea);
        // SAVE AREA TO DB
        dbHandler.saveArea(newArea);

        // SAVE AREA TO FILE AND SEND MESSAGE
        PlayerUtils.sendSuccess(player, pluginName, aHandler.saveArea(newArea.getAreaName(), selection[0], selection[1]));
    }
}
