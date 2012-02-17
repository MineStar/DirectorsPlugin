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

package de.minestar.director.threading;

import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import de.minestar.director.Main;
import de.minestar.director.database.QueuedBlock;
import de.minestar.director.listener.DirectorBlock;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class BatchRunnable implements Runnable {

    private List<QueuedBlock> queue = new LinkedList<QueuedBlock>();

    private PreparedStatement batch;

    public BatchRunnable(PreparedStatement statement) {
        this.batch = statement;
    }

    public void copyList(List<QueuedBlock> list) {
        queue = Lists.newLinkedList(list);
    }

    public void run() {
        this.runQueue();
    }

    private void runQueue() {
        try {
            int pos = 1;
            for (QueuedBlock event : queue) {
                DirectorBlock oldBlock = event.getOldBlock();
                DirectorBlock newBlock = event.getNewBlock();
                batch.setString(pos++, oldBlock.getWorldName());
                batch.setInt(pos++, oldBlock.getX());
                batch.setInt(pos++, oldBlock.getY());
                batch.setInt(pos++, oldBlock.getZ());
                batch.setInt(pos++, newBlock.getID());
                batch.setInt(pos++, newBlock.getSubID());
                batch.setInt(pos++, oldBlock.getID());
                batch.setInt(pos++, oldBlock.getSubID());
                batch.setString(pos++, event.getTime());
                batch.setString(pos++, event.getPlayerName());
                batch.setString(pos++, event.getMode());
                batch.setString(pos++, event.getAreaName());
            }
            batch.executeUpdate();
            queue.clear();
        } catch (Exception e) {
            ConsoleUtils.printException(e, Main.NAME, "Can't store the queued blocks!");
        }
    }
}
