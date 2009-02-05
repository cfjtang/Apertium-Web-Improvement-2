/*
 * Copyright (C) 2008-2009 Enrique Benimeli Bofarull <ebenimeli.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package apertiumsubtitletranslator;

import engine.subtitles.Subtitles;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class SubtitleTableModel extends DefaultTableModel {

    //private String[] columnNames = {"ID", "Time", "Source", "Translation"};
    private String[][] data;

    private Subtitles subtitles;


    public SubtitleTableModel(String [][] data, String[] columnNames, Subtitles subtitles) {
        super(data, columnNames);
        this.subtitles = subtitles;

        //this.data = data;
        //this.columnNames = columnNames;

    }


    /*
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;

    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return Object.class;
    }
     */


    @Override
    public boolean isCellEditable(int row, int col) {
        if (col < 3) {
            return false;
        } else {
            return true;
        }
    }

    public void setValueAt(String value, int row, int col) {
        data[row][col] = value;
        //System.out.println("data[" + row + "][" + col + "] = " + value);
        fireTableCellUpdated(row, col);
    }

   
}

