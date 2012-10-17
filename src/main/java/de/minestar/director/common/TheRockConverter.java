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

package de.minestar.director.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TheRockConverter {

    public static void main(String[] args) {
        System.out.println("File to convert: ");
        Scanner scanner = new Scanner(System.in);
        String pathToFile = scanner.nextLine();

        try {
            File file = new File(pathToFile);
            if (!file.exists()) {
                System.out.println("File" + pathToFile + " not existing");
                scanner.close();
                return;
            }
            System.out.print("X Coordinate of reference block: ");
            int xRef = scanner.nextInt();
            System.out.print("Z Coordinate of reference block: ");
            int zRef = scanner.nextInt();
            scanner.close();
            System.out.println("Start converting...");
            readFile(file, xRef, zRef);
            System.out.println("Finished converting...");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong!");
            scanner.close();
        }
    }

    private static void readFile(File file, int xRef, int zRef) throws Exception {
        BufferedReader bReader = new BufferedReader(new FileReader(file));
        String line = "";
        String[] split = null;
        int x, z;
        Pattern P = Pattern.compile(";");
        BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(file.getParentFile(), "output.txt")));
        while ((line = bReader.readLine()) != null) {
            split = P.split(line);
            if (split.length == 0)
                continue;
            x = Integer.parseInt(split[0]);
            x = x - xRef;
            split[0] = Integer.toString(x);
            z = Integer.parseInt(split[2]);
            z = z - zRef;
            split[2] = Integer.toString(z);
            bWriter.append(toLine(split));
            bWriter.newLine();
        }

        bWriter.close();
        bReader.close();
    }
    private static String toLine(String[] split) {
        StringBuilder sBuilder = new StringBuilder();

        int i = 0;
        for (; i < split.length - 1; ++i) {
            sBuilder.append(split[i]);
            sBuilder.append(';');

        }
        sBuilder.append(split[i]);
        return sBuilder.toString();
    }
}
