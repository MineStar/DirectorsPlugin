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

package de.minestar.director.area;

import java.util.TreeMap;

import de.minestar.director.database.DatabaseHandler;

public class AreaHandler {
    private TreeMap<String, Area> areaList;
    private DatabaseHandler dbHandler;

    public AreaHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.loadAllAreas();
    }
    
    private void loadAllAreas() {
        areaList = this.dbHandler.loadAreas();
    }

    /**
     * getArea(String areaName)
     * @param areaName : the name of the area
     * @return the Area,if there is an area with that name, otherwise null
     */
    public Area getArea(String areaName) {
        return this.areaList.get(areaName);
    }

    /**
     * getAreas()
     * @return a TreeMap with all Areas (Key : areaName, Value : Area)
     */
    public TreeMap<String, Area> getAreas() {
        return this.areaList;
    }
    
    /**
     * addArea(Area newArea)
     * @param newArea : Area to be added
     * @return <b>false</b> : if the areaname is already in use. <br><b>true</b> : if the area was added.
     */
    public boolean addArea(Area newArea) {
        if(this.getArea(newArea.getAreaName()) != null)
            return false;
        
        this.areaList.put(newArea.getAreaName(), newArea);
        return true;
    }
}
