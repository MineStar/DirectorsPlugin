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

import de.minestar.director.database.QueuedBlock;
import de.minestar.director.listener.DirectorBlock;

public class BatchRunnable implements Runnable {

    LinkedList<QueuedBlock> queue = null;

    PreparedStatement batch = null;

    public BatchRunnable(PreparedStatement statement) {
        this.queue = new LinkedList<QueuedBlock>();
        this.updateStatement(statement);
    }

    @SuppressWarnings("unchecked")
    public void copyList(List<QueuedBlock> list) {
        queue = (LinkedList<QueuedBlock>) ((LinkedList<QueuedBlock>) list).clone();
    }

    public void updateStatement(PreparedStatement statement) {
        this.batch = statement;
    }

    public void run() {
        this.runQueue();

    }

    private void runQueue() {
        try {
            int pos = 0;
            for (QueuedBlock event : queue) {
                DirectorBlock oldBlock = event.getOldBlock();
                DirectorBlock newBlock = event.getNewBlock();
                batch.setString(pos + 1, oldBlock.getWorldName());
                batch.setInt(pos + 2, oldBlock.getX());
                batch.setInt(pos + 3, oldBlock.getY());
                batch.setInt(pos + 4, oldBlock.getZ());
                batch.setInt(pos + 5, newBlock.getID());
                batch.setInt(pos + 6, newBlock.getSubID());
                batch.setInt(pos + 7, oldBlock.getID());
                batch.setInt(pos + 8, oldBlock.getSubID());
                batch.setString(pos + 9, event.time);
                batch.setString(pos + 10, event.getPlayerName());
                batch.setString(pos + 11, event.getMode());
                batch.setString(pos + 12, event.getAreaName());
                pos += 12;
            }
            batch.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
