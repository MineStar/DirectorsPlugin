/*
 * Copyright (C) 2011 MineStar.de 
 * 
 * This file is part of MineStarLibrary.
 * 
 * MineStarLibrary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * MineStarLibrary is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MineStarLibrary.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.director.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandList {

    // The commands are stored in this list. The key indicates the
    // commandssyntax and the argument counter
    private HashMap<String, Command> commandList;

    /**
     * Creates an array where the commands are stored in and add them all to the
     * HashMap
     * 
     * @param commands
     *            A list of all commands the plugin is using
     */
    public CommandList(Command[] commands) {
        initCommandList(commands);
    }

    /**
     * Use this methode in your java plugins method 'onCommand' to handle all
     * commands
     * 
     * @param sender
     * @param label
     * @param args
     */
    public void handleCommand(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player))
            return;

        Player player = (Player) sender;

        if (!label.startsWith("/"))
            label = "/" + label;

        // looking for
        Command cmd = commandList.get(label + "_" + args.length);
        if (cmd != null)
            cmd.run(args, player);
        else {
            cmd = commandList.get(label);
            if (cmd != null)
                cmd.run(args, player);
            else {
                player.sendMessage(ChatColor.RED + "PLACEHOLDER");

                // FIND RELATED COMMANDS
                LinkedList<Command> cmdList = new LinkedList<Command>();
                for (Entry<String, Command> entry : commandList.entrySet()) {
                    if (entry.getKey().startsWith(label))
                        cmdList.add(entry.getValue());
                }

                // PRINT SYNTAX
                for (Command command : cmdList)
                    player.sendMessage(ChatColor.GRAY + command.getSyntax()
                            + " " + command.getArguments());
            }
        }
    }

    /**
     * Stores the commands from the array to a HashMap. The key is generated by
     * the followning: <br>
     * <code>syntax_numberOfArguments</code> <br>
     * Example: /warp create_1 (because create has one argument)
     * 
     * @param cmds
     *            The array list for commands
     */
    private void initCommandList(Command[] cmds) {

        commandList = new HashMap<String, Command>();
        for (Command cmd : cmds) {
            String key = "";
            // when the command has a variable count of arguments or
            // when the command has sub commands
            if (cmd instanceof ExtendedCommand || cmd instanceof SuperCommand)
                key = cmd.getSyntax();
            // a normal command(no subcommands/fix argument count)
            else
                key = cmd.getSyntax() + "_" + cmd.getArgumentCount();

            commandList.put(key, cmd);
        }
    }
}
