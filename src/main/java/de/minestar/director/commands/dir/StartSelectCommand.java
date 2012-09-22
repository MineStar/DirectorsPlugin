/*
 * Copyright (C) 2012 MineStar.de 
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.minestar.director.Core;
import de.minestar.director.data.LoadedBlockChange;
import de.minestar.director.threading.BlockSetFromFileThread;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class StartSelectCommand extends AbstractExtendedCommand {

    public static Map<Player, Location> startMap = new HashMap<Player, Location>();

    public StartSelectCommand(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        Location loc = startMap.get(player);
        if (loc == null) {
            startMap.put(player, player.getLocation());
            PlayerUtils.sendInfo(player, pluginName, "Startpunkt markiert! Nochmal eingeben mit Area name um zu starten!");
        } else {
            if (args.length == 0) {
                PlayerUtils.sendError(player, pluginName, "Kein Areaname angegeben!");
                return;
            }
            PlayerUtils.sendInfo(player, pluginName, "Lade Daten...");
            Queue<LoadedBlockChange> loadedBlocks = loadArea(player, args[0]);
            if (loadedBlocks == null)
                return;

            int xD = loc.getBlockX() - player.getLocation().getBlockX();
            int zD = loc.getBlockZ() - player.getLocation().getBlockZ();

            BlockSetFromFileThread thread = new BlockSetFromFileThread(player.getWorld(), loadedBlocks, player.getLocation().getBlockX(), player.getLocation().getBlockZ(), xD, zD);
            int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), thread, 20, 1);
            thread.setID(taskID);
            PlayerUtils.sendSuccess(player, pluginName, "Starte mit dem Bau!");
        }
    }

    private Queue<LoadedBlockChange> loadArea(Player player, String areaName) {
        Queue<LoadedBlockChange> queue = new LinkedList<LoadedBlockChange>();

        File areaFile = new File(Core.getInstance().getDataFolder(), areaName + ".area");
        if (!areaFile.exists()) {
            PlayerUtils.sendError(player, pluginName, "Area kann nicht gefunden werden!");
            return null;
        }
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(areaFile));
            String line = "";
            String[] split;
            int x, y, z;
            int toID, toData;
            int fromID, fromData;
            while ((line = bReader.readLine()) != null) {
                split = line.split(";");
                x = Integer.valueOf(split[0]);
                y = Integer.valueOf(split[1]);
                z = Integer.valueOf(split[2]);
                fromID = Integer.valueOf(split[3]);
                fromData = Integer.valueOf(split[4]);

                toID = Integer.valueOf(split[5]);
                toData = Integer.valueOf(split[6]);
                queue.add(new LoadedBlockChange(x, y, z, fromID, fromData, toID, toData));
            }
            bReader.close();
        } catch (Exception e) {
            PlayerUtils.sendError(player, pluginName, "Fehler beim laden der Datei!");
            e.printStackTrace();
        }
        return queue;
    }

}
