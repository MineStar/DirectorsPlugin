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

package de.minestar.director;

import java.io.File;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.director.area.AreaDataHandler;
import de.minestar.director.database.DatabaseHandler;
import de.minestar.director.listener.AreaDefineListener;
import de.minestar.director.listener.BlockChangeListener;

public class Main extends JavaPlugin {

    private DatabaseHandler dbHandler;

    private AreaDefineListener adListener;

    public static void printToConsole(String msg) {
        System.out.println("[ DirectorsPlugin ] : " + msg);
    }

    @Override
    public void onDisable() {
        dbHandler.closeConnection();
        dbHandler = null;
        adListener = null;

        printToConsole("Disabled!");
    }

    @Override
    public void onEnable() {

        // when we don't have a connection to the database, the whole plugin
        // can't work
        if (!initDatabase()) {
            printToConsole("------------------------------------------");
            printToConsole("- COULD NOT CONNECT TO DIRECTOR DATABASE -");
            printToConsole("------------------------------------------");
            return;
        }

        // Register event listener
        PluginManager pm = getServer().getPluginManager();
        BlockListener bListener = new BlockChangeListener(dbHandler);
        pm.registerEvent(Type.BLOCK_BREAK, bListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_PLACE, bListener, Priority.Normal, this);

        adListener = new AreaDefineListener();
        pm.registerEvent(Type.PLAYER_INTERACT, adListener, Priority.Normal, this);

        printToConsole(getDescription().getVersion() + " is enabled!");
    }

    private boolean initDatabase() {

        try {

            File f = new File("plugins/DirectorsPlugin/");
            f.mkdirs();
            f = new File(f.getAbsolutePath() + "/sqlconfig.yml");
            FileConfiguration sqlConfig = getConfig();

            if (!f.exists()) {
                printToConsole("Can't find sql configuration!");
                printToConsole("Create an empty configuration file at plugins/DirectorsPlugin/sqlconfig.yml");
                f.createNewFile();
                sqlConfig.set("host", "host");
                sqlConfig.set("port", "port");
                sqlConfig.set("database", "database");
                sqlConfig.set("username", "userName");
                sqlConfig.set("password", "passwort");
                sqlConfig.save(f);
                return false;
            }
            sqlConfig.load(f);
            dbHandler = new DatabaseHandler(sqlConfig.getString("host"), sqlConfig.getInt("port"), sqlConfig.getString("database"), sqlConfig.getString("username"), sqlConfig.getString("password"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        Player p = (Player) sender;
        // SELECTION MODE
        if (label.startsWith("dselect") && UtilPermissions.playerCanUseCommand(p, "directorsplugin.select")) {
            if (adListener.switchSelectionMode(p.getName()))
                p.sendMessage("Du bist nun im Selektionsmodus fuer Director!");
            else
                p.sendMessage("Du bist nicht mehr im Selektionsmodus fuer Director!");
            // SAVE AREA
        } else if (label.startsWith("dsave") && UtilPermissions.playerCanUseCommand(p, "directorsplugin.save")) {

            if (args.length == 0) {
                p.sendMessage("Du musst einen Namen angegeben!");
                return false;
            }
            String areaName = args[0];
            if (AreaDataHandler.areaExists(areaName)) {
                p.sendMessage("Ein Area mit dem Namen '" + areaName + "' existiert bereits!");
                return false;
            }
            // ToDo: Check if an existing area is inside the new area(areas
            // musn't intersect!)

            Chunk[] selection = adListener.getCorners(areaName);
            if (selection == null) {
                p.sendMessage("Du musst zwei Bloecke auswaehlen!");
                return false;
            }
            p.sendMessage(AreaDataHandler.saveArea(areaName, selection[0], selection[1]));
            // RESET AREA
        } else if (label.startsWith("dreset") && UtilPermissions.playerCanUseCommand(p, "directorsplugin.reset")) {
            if (args.length == 0) {
                p.sendMessage("Du musst einen Namen angegeben!");
                return false;
            }
            String areaName = args[0];
            if (AreaDataHandler.areaExists(areaName)) {
                p.sendMessage("Ein Area mit dem Namen '" + areaName + "' existiert bereits!");
                return false;
            }
            // ToDo: Reset the area
        }

        return true;
    }
}
