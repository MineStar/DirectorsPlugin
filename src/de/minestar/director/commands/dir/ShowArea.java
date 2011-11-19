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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.minestar.director.area.Area;
import de.minestar.director.area.AreaHandler;
import de.minestar.director.commands.Command;

public class ShowArea extends Command {

    private AreaHandler aHandler;

    private HashMap<String, LinkedList<int[]>> borders = new HashMap<String, LinkedList<int[]>>();

    public ShowArea(String syntax, String arguments, String node, AreaHandler aHandler) {
        super(syntax, arguments, node);
        this.aHandler = aHandler;
        this.description = "Zeigt die Area mit einem GlowstoneRing an";
    }

    @Override
    public void execute(String[] args, Player player) {

        String areaName = args[0];
        if (!aHandler.areaExists(args[0].toLowerCase())) {
            player.sendMessage("Es existiert keine Area namens '" + areaName + "'!");
            return;
        }
        Area area = aHandler.getArea(areaName);
        LinkedList<int[]> list = borders.get(areaName);
        int x1 = (int) area.getRectangle().getMinX();
        int z1 = (int) area.getRectangle().getMinY();
        int x2 = (int) area.getRectangle().getMaxX();
        int z2 = (int) area.getRectangle().getMaxY();
        int y = player.getLocation().getBlockY();
        int[] data = null;
        Block block = null;
        World world = player.getServer().getWorld(area.getWorldName());

        // restore blocks
        if (list != null) {
            for (; x1 <= x2; ++x1) {
                for (; z1 <= z2; ++z1) {
                    block = world.getBlockAt(x1, y, z1);
                    data = list.removeFirst();
                    block.setTypeId(data[0]);
                    block.setData((byte) data[1]);
                }
            }

        }
        // try to load data from file
        else if ((list = loadDataFromFile(areaName)) != null) {
            for (; x1 <= x2; ++x1) {
                for (; z1 <= z2; ++z1) {
                    block = world.getBlockAt(x1, y, z1);
                    data = list.removeFirst();
                    block.setTypeId(data[0]);
                    block.setData((byte) data[1]);
                }
            }
        }
        // set glow stone line
        else {
            list = new LinkedList<int[]>();
            for (; x1 <= x2; ++x1) {
                for (; z1 <= z2; ++z1) {
                    // save old data
                    block = world.getBlockAt(x1, y, z1);
                    data = new int[2];
                    data[0] = block.getTypeId();
                    data[1] = block.getData();
                    block.setType(Material.GLOWSTONE);
                    list.add(data);
                }
            }
            saveDataToFile(areaName, list);
            borders.put(areaName, list);
        }
    }

    private LinkedList<int[]> loadDataFromFile(String areaName) {
        File f = new File("plugins/DirectorsPlugin/borders/" + areaName + ".data");
        if (!f.exists())
            return null;
        try {
            LinkedList<int[]> data = new LinkedList<int[]>();
            BufferedReader bReader = new BufferedReader(new FileReader(f));
            String line = "";
            String[] split = null;
            int[] a = new int[2];
            while ((line = bReader.readLine()) != null && !line.trim().isEmpty()) {
                split = line.split(",");
                a[0] = Integer.parseInt(split[0]);
                a[1] = Integer.parseInt(split[1]);
                data.add(a);
            }
            bReader.close();
            f.delete();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    // store all data from the blocks in a file to prevent information loose
    // because
    private void saveDataToFile(String areaName, LinkedList<int[]> list) {
        try {
            BufferedWriter bWriter = new BufferedWriter(new FileWriter("plugins/DirectorsPlugin/borders/" + areaName + ".data"));
            for (int[] data : list) {
                bWriter.write(data[0] + "," + data[1]);
                bWriter.newLine();
            }
            bWriter.flush();
            bWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
