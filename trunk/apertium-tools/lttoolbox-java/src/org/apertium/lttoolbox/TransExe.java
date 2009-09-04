package org.apertium.lttoolbox;/*
 * Copyright (C) 2005 Universitat d'Alacant / Universidad de Alicante
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


import org.apertium.lttoolbox.Alphabet;
import org.apertium.lttoolbox.Node;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransExe {

    /**
     * Initial state
     */
    int initial_id;
    /**
     * Node list
     */
    List<Node> node_list;
    /**
     * Set of final nodes
     */
    Set<Node> finals;

    TransExe() {
    }

    void TransExe(TransExe te) {
        initial_id = te.initial_id;
        node_list = te.node_list;
        finals = te.finals;
    }

    void read(FileInputStream input, Alphabet alphabet) throws IOException {
        TransExe new_t = this;
        new_t.initial_id = Compression.multibyte_read(input);
        int finals_size = Compression.multibyte_read(input);

        int base = 0;

        Set<Integer> myfinals = new HashSet<Integer>();

        while (finals_size > 0) {
            finals_size--;
            base += Compression.multibyte_read(input);
            myfinals.add(base);
        }

        base = Compression.multibyte_read(input);

        int number_of_states = base;
        int current_state = 0;

        // new_t.node_list.resize(number_of_states);

        for (Integer it : myfinals) {
            new_t.finals.add(new_t.node_list.get(it));
        }

        while (number_of_states > 0) {
            int number_of_local_transitions = Compression.multibyte_read(input);
            int tagbase = 0;
            Node mynode = new_t.node_list.get(current_state);

            while (number_of_local_transitions > 0) {
                number_of_local_transitions--;
                tagbase += Compression.multibyte_read(input);
                int state = (current_state + input.read()) % base;
                int i_symbol = alphabet.decode(tagbase).first;
                int o_symbol = alphabet.decode(tagbase).second;

                mynode.addTransition(i_symbol, o_symbol, new_t.node_list.get(state));
            }
            number_of_states--;
            current_state++;
        }
    }

    void unifyFinals() {

        node_list.add(new Node());

        Node newfinal = node_list.get(node_list.size() - 1);

        for (Node it : finals) {

            it.addTransition(0, 0, newfinal);
        }

        finals.clear();
        finals.add(newfinal);
    }

    public Node getInitial() {
        return node_list.get(initial_id);
    }

    Set<Node> getFinals() {
        return finals;
    }
}
