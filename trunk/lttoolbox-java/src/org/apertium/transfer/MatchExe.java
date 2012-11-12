package org.apertium.transfer;
/*
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


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import org.apertium.lttoolbox.Compression;
import org.apertium.lttoolbox.process.FSTProcessor;

/**
 * The container object that contains all states (and transitions betweem them)
 * @author Jacob Nordfalk
 */
public class MatchExe {

  /**
   * Initial state
   */
  private final int initial_id;

  final ByteBuffer data;
  //private final int[] byteBufferPositions;
  private ByteBuffer byteBufferPositions;

  //private final short[] final_state_to_symbol;
  final int number_of_states;
  final int decalage;

  /** We putting in final state output values (rule number) into the high bits of the state array index (final_state_to_symbol) */
  private static final int MAX_OUTPUT_SYMBOLS_POWS_OF_2 = 10; // Must be 9 (max 512 rules, max 8MB t1x.bin file) or 10 (max 1024 rules, max 4MB t1x.bin file)
  private static final int MAX_OUTPUT_SYMBOLS_SHIFT = 32 - MAX_OUTPUT_SYMBOLS_POWS_OF_2; // 32 bits in an int
  private static final int MAX_OUTPUT_SYMBOLS = (1 << MAX_OUTPUT_SYMBOLS_POWS_OF_2) - 1;  // 2047
  private static final int MAX_STATE_INDEX_NO = (1 << MAX_OUTPUT_SYMBOLS_SHIFT) - 1;

  public MatchExe(ByteBuffer in, int decalage, File cachedFile) throws IOException {
    data = in;
    this.decalage = decalage;
    //reading the initial state - set up initial node
    initial_id = Compression.multibyte_read(in);


    //reading the list of final states - not used
    int number_of_finals = Compression.multibyte_read(in);

    /* Discard the bytes
      * for the list of final states, since this implementation doesn't actually
      * use that list. But we need to advance the read pointer past the finals
      * list.
      */
    Compression.multibyte_skip(in, number_of_finals);


    //reading the transitions
    number_of_states = Compression.multibyte_read(in);

    // -----------------
    /*
    boolean canReadFromCache = false;
    int cacheFileSize = number_of_states*4 + 4; // one extra int to hold index of end of transducer
    if (cachedFile.canRead() && cachedFile.length() == cacheFileSize) {
      RandomAccessFile raf = new RandomAccessFile(cachedFile, "r");
      byteBufferPositions = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, cacheFileSize);
      canReadFromCache = true;
    } else {
      if (cachedFile.exists()) cachedFile.delete();
      cachedFile.getParentFile().mkdirs();
      if (cachedFile.canWrite()) {
        RandomAccessFile raf = new RandomAccessFile(cachedFile, "rw");
        byteBufferPositions = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, cacheFileSize);
      } else {
        byteBufferPositions = ByteBuffer.allocate(cacheFileSize); //int[number_of_statesl];
      }
    }

    if (FSTProcessor.DEBUG) {
      System.err.println("TransducerExe read states:"+number_of_states+ "  cachedFile="+cachedFile+" "+canReadFromCache+" "+byteBufferPositions);
    }


    if (canReadFromCache) {
      int lastPos = byteBufferPositions.getInt(number_of_states*4);
      input.position(lastPos); // Skip to end position
      return;
    }

    // No cache. Load and index the nodes

    */



    // -----------------
    int cacheFileSize = number_of_states*4 + 4; // one extra int to hold index of end of transducer

    // memory allocation
    byteBufferPositions = ByteBuffer.allocate(cacheFileSize); //int[number_of_statesl];
    //byteBufferPositions = new int[number_of_states];
    //final_state_to_symbol = new short[number_of_states];

    int current_state=0;
    for (int i=number_of_states; i>0; i--) {
      byteBufferPositions.putInt(current_state*4, in.position());
      skipNode(in);
      current_state++;
    }

    if (byteBufferPositions.getInt( (current_state-1)*4)>MAX_STATE_INDEX_NO) throw new IllegalStateException("Cannot hold state index value. File too large: "+in.position()+". Max possible value is "+MAX_STATE_INDEX_NO);


    // set up finals
   //  values for en_ca_t1x: {14=1, 41=2, 48=2, 55=2, 62=2, 69=2, 76=2, 83=2, 90=2, 97=2, 103=90, 106=90, 109=90, ...
   // ... 420739=211, 420741=213, 420743=215, 420745=215, 420747=215, 420749=216}
   // noOfFinals == number of rules


    int number_of_finals2=Compression.multibyte_read(in);  // == number_of_finals


    for (int i=0; i!=number_of_finals; i++) {
      int key=Compression.multibyte_read(in);
      int value=Compression.multibyte_read(in); // value == rule number (method nomber)
      if (value>MAX_OUTPUT_SYMBOLS) throw new IllegalStateException("Output symbol index value too large: "+value+". Max value is: "+MAX_OUTPUT_SYMBOLS);


      //final_state_to_symbol[key]=(short) value;
      byteBufferPositions.putInt(key*4, byteBufferPositions.getInt(key*4) | value<<MAX_OUTPUT_SYMBOLS_SHIFT);

//      System.err.println("node_list["+key+"] = " + value);
      //System.err.println("node_list["+key+"] = " + Arrays.toString(node_list[key]));
    }
  }


  int final_state_to_symbol(int index) {
    // final_state_to_symbol
    //return final_state_to_symbol[index];
    return byteBufferPositions.getInt(index*4)>>MAX_OUTPUT_SYMBOLS_SHIFT;
  }


  public int[] loadNode(int node_id) {
    int index = byteBufferPositions.getInt(node_id*4) & MAX_STATE_INDEX_NO;

    data.position( index );
    int number_of_local_transitions=Compression.multibyte_read(data);
    if (number_of_local_transitions>0) {
      int[] mynode = new int[number_of_local_transitions*2];
      int symbol=0;
      int n=0;
      for (int j=number_of_local_transitions; j>0; j--) {
        symbol+=Compression.multibyte_read(data)-decalage;
        int target_state=(node_id+Compression.multibyte_read(data))%number_of_states;
        mynode[n++]=symbol;
        mynode[n++]=target_state;
        //              System.err.println(current_state+ "( "+symbol+" "+(char)symbol+")  -> "+target_state);
      }
      return mynode;
    } else {
      // then it must be a final state - we handle that elsewhere
      return null;
    }
  }


  private void skipNode(ByteBuffer in) {
    int number_of_local_transitions=Compression.multibyte_read(in);
    for (int j=number_of_local_transitions; j>0; j--) {
      Compression.multibyte_skip(in);
      Compression.multibyte_skip(in);
    }
  }


  public int getInitial() {
    return initial_id;
  }


}