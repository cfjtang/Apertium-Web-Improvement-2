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

import engine.subtitles.Subtitle;
import engine.subtitles.Subtitles;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class SubtitleTableModelListener implements TableModelListener {

    private JTable table;
    private Subtitles subtitles;

    public SubtitleTableModelListener(JTable table, Subtitles subtitles) {
        this.table = table;
        this.subtitles = subtitles;
        table.getModel().addTableModelListener(this);
    }

    public final void setSubtitles(Subtitles subtitles) {
        this.subtitles = subtitles;
    }

    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel) e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);

        if (column == 3 && subtitles != null) {
            Subtitle s = (Subtitle) subtitles.get(row - 1);
            String take = data.toString();
            take = take.replaceAll(" \\| ", "<br/>");
            String[] lines = take.split("<br/>");
            ArrayList<String> alines = new ArrayList<String>();
            for (int i = 0; i < lines.length; i++) {
                alines.add(lines[i] + "<br/>");
            }
            s.setLines(alines);
            s.setTake(data.toString());
        }
    }
}
