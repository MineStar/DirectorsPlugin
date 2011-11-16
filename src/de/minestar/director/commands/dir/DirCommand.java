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

import de.minestar.director.commands.Command;
import de.minestar.director.commands.SuperCommand;

public class DirCommand extends SuperCommand {

    public DirCommand(String syntax, String arguments, String node, Command[] subCommands) {
        super(syntax, arguments, node, subCommands);
    }

    @Override
    public void execute(String[] args, Player player) {
        // Do Nothing
    }

}
