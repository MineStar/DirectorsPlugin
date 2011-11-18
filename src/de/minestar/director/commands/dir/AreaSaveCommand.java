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
import de.minestar.director.area.AreaDataHandler;
import de.minestar.director.commands.Command;
import de.minestar.director.listener.AreaDefineListener;

public class AreaSaveCommand extends Command {

    private AreaDefineListener adListener;

    public AreaSaveCommand(String syntax, String arguments, String node, AreaDefineListener adListener) {
        super(syntax, arguments, node);
        this.adListener = adListener;
        this.description = "Erzeugt eine Area die von Director ueberwacht wird.";
    }

    @Override
    public void execute(String[] args, Player player) {
        if (args.length == 0) {
            player.sendMessage("Du musst einen Namen angegeben!");
            return;
        }
        String areaName = args[0];
        if (Main.getAreaHandler().areaExists(areaName)) {
            player.sendMessage("Ein Area mit dem Namen '" + areaName + "' existiert bereits!");
            return;
        }

        Chunk[] selection = adListener.getCorners(areaName);
        if (selection == null) {
            player.sendMessage("Du musst zwei Bloecke auswaehlen!");
            return;
        }
        
        // CREATE AREA , GeMoschen
        Area newArea = new Area(areaName, player.getName(), selection[0].getWorld().getName(), selection[0], selection[1]);

        // Check if an existing area is inside the new area        
        for(Area otherArea :  Main.getAreaHandler().getAreas().values()) {
            if(otherArea.intersectsArea(newArea)) {
                player.sendMessage("Area '" + otherArea.getAreaName() + "' schneidet die neue Area!");
                return;
            }
        }
        
        // ADD AREA , GeMoschen
        Main.getAreaHandler().addArea(newArea);
        // SAVE AREA TO DB , GeMoschen
        Main.getDatabaseHandler().saveArea(newArea);
        
        // SAVE AREA TO FILE AND SEND MESSAGE
        player.sendMessage(AreaDataHandler.saveArea(newArea.getAreaName(), selection[0], selection[1]));
    }
}
