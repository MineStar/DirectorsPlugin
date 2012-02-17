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

import org.bukkit.entity.Player;

import de.minestar.director.Main;
import de.minestar.director.area.AreaHandler;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class ResetCommand extends AbstractCommand {

    private AreaHandler aHandler;

    public ResetCommand(String syntax, String arguments, String node, AreaHandler aHandler) {
        super(Main.NAME, syntax, arguments, node);
        this.aHandler = aHandler;
        this.description = "Resettet die Area";
    }

    @Override
    public void execute(String[] args, Player player) {

        String areaName = args[0];
        if (!aHandler.areaExists(areaName)) {
            PlayerUtils.sendError(player, pluginName, "Eine Area mit dem Namen '" + areaName + "' existiert nicht!");
            return;
        }
        // Reset the area
        PlayerUtils.sendSuccess(player, pluginName, aHandler.resetArea(areaName));
    }
}
