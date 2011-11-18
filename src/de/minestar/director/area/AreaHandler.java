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
        return this.areaList.get(areaName.toLowerCase());
    }
    
    /**
     * areaExists(String areaName)
     * @param areaName : the name of the area
     * @return <b>true</b> : if the area exists <br> <b>false</b> : if the area does not exist
     */
    public boolean areaExists(String areaName) {
        return (this.getArea(areaName.toLowerCase()) != null);
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
        if(this.areaExists(newArea.getAreaName()))
            return false;
        
        this.areaList.put(newArea.getAreaName().toLowerCase(), newArea);
        return true;
    }
    
    /**
     * resetArea(String areaName)
     * @param areaName : Area to reset
     * @return The resultstring of what has happened
     */
    public String resetArea(String areaName) {
        if(!this.areaExists(areaName)) {
            return "Die Area existiert nicht!";
        }        
        return AreaDataHandler.resetArea(this.getArea(areaName));
    }
}
