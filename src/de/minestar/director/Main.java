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

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.minestar.director.area.AreaHandler;
import de.minestar.director.commands.Command;
import de.minestar.director.commands.CommandList;
import de.minestar.director.commands.dir.AreaSaveCommand;
import de.minestar.director.commands.dir.DirCommand;
import de.minestar.director.commands.dir.ResetCommand;
import de.minestar.director.commands.dir.SelectCommand;
import de.minestar.director.database.DatabaseHandler;
import de.minestar.director.listener.AreaDefineListener;
import de.minestar.director.listener.BlockChangeListener;

public class Main extends JavaPlugin {

    private static DatabaseHandler dbHandler;
    
    private static AreaHandler areaHandler;

    private AreaDefineListener adListener;

    private CommandList cmdList;

    public static void printToConsole(String msg) {
        System.out.println("[ DirectorsPlugin ] : " + msg);
    }

    @Override
    public void onDisable() {
        dbHandler.closeConnection();
        dbHandler = null;
        adListener = null;
        cmdList = null;

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

        // INIT AREAHANDLER , GeMoschen
        areaHandler = new AreaHandler(dbHandler);
        
        // INIT COMMANDLIST , GeMoschen
        initCommandList();
        
        printToConsole(getDescription().getVersion() + " is enabled!");
    }

    private void initCommandList() {
        //@formatter:off
        Command[] commands = {
                new DirCommand("/dir","","", new Command[] {
                    new AreaSaveCommand("save","<Name>","directorsplugin.save",adListener),
                    new SelectCommand("select","","directorsplugin.select",adListener),
                    new ResetCommand("reset","<Name>","directorsplugin.reset")
            })
        };
        cmdList = new CommandList(commands);
        //@formatter:on
    }
    
    private boolean initDatabase() {

        try {

            File f = new File("plugins/DirectorsPlugin/");
            f.mkdirs();
            f = new File(f.getAbsolutePath() + "/sqlconfig.yml");              
            YamlConfiguration sqlConfig = new YamlConfiguration();
            
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
            
            sqlConfig = new YamlConfiguration();
            sqlConfig.load(f);
            dbHandler = new DatabaseHandler(sqlConfig.getString("host"), sqlConfig.getInt("port"), sqlConfig.getString("database"), sqlConfig.getString("username"), sqlConfig.getString("password"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        cmdList.handleCommand(sender, label, args);
        return true;
    }
    
    public static AreaHandler getAreaHandler() {
        return areaHandler;
    }
    
    public static DatabaseHandler getDatabaseHandler() {
        return dbHandler;
    }
}
