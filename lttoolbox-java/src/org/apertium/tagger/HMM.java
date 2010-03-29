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

package org.apertium.tagger;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import org.apertium.lttoolbox.Compression;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author jimregan
 */
public class HMM {
    class IntVector {
        List<Integer> nodes;
        IntVector () {
            nodes = new ArrayList<Integer>();
        }
    }

    private double ZERO = 1e-10;
    private TaggerData td;
    private int eos;
    private boolean debug;
    private boolean show_sf;
    private boolean null_flush;

   /**
    * Used to set the end-of-sentence tag
    * @param t the end-of-sentence tag
    */
    void set_eos(int t) {
        eos = t;
    }

   /**
    * Used to set the debug flag
    */
    void set_debug (boolean d) {
        debug = d;
    }

   /**
    * Used to set the show superficial forms flag 
    */
    void set_show_sf (boolean sf) {
        show_sf = sf;
    }

   /**
    * Reads the ambiguity classes from the stream received as input
    * @param in the input stream
    */
    void read_ambiguity_classes (InputStream in) throws IOException {
        boolean eof = false;
        while (!eof) {
            int ntags = Compression.multibyte_read(in);
            if (ntags == -1) {
                eof = true;
                break;
            }
            
            Set<Integer> ambiguity_class = new HashSet<Integer>();

            for (; ntags != 0; ntags--) {
                ambiguity_class.add(Compression.multibyte_read(in));
            }

            if (ambiguity_class.size() != 0) {
                td.getOutput().add(ambiguity_class);
            }
        }
        td.setProbabilities(td.getTagIndex().size(), td.getOutput().size());
    }

   /**
    * Writes the ambiguity classes to the stream received as
    * a parameter
    * @param o the output stream
    */
    void write_ambiguity_classes (OutputStream o) throws IOException {
        for (int i=0; i!=td.getOutput().size(); i++) {
            Set<Integer> ac = td.getOutput().get(i);
            Compression.multibyte_write(ac.size(), o);
            for (int it : ac) {
                Compression.multibyte_write(it, o);
            }
        }
    }

   /**
    * Reads the probabilities (matrices a and b) from the stream
    * received as a parameter
    * @param in the input stream
    */
    void read_probabilities (InputStream in) throws IOException {
        td.read(in);
    }

   /**
    * Writes the probabilities (matrices a and b) to the stream
    * received as a parameter
    * @param out the output stream
    */
    void write_probabilities (OutputStream out) throws IOException {
        td.write(out);
    }

   /**
    * Initializes the transtion (a) and emission (b) probabilities
    * from an untagged input text by means of Kupiec's method
    * @param is the input stream with the untagged corpus to process
    */
    void init_probabilities_kupiec (InputStream is) throws IOException {
        int N = td.getN();
        int M = td.getM();
        int i, j, k, k1, k2, nw=0;
        /**
         * M = Number of ambiguity classes
         */
        double[] classes_occurrences = new double[M];
        double[][] classes_pair_occurrences = new double[M][M];
        /**
         * N = Number of tags (states)
         */
        double[] tags_estimate = new double[N];
        double[][] tags_pair_estimate = new double[N][N];

        Collection output = td.getOutput();

        MorphoStream lexmorfo = new MorphoStream (is, true, td);
        TaggerWord word = new TaggerWord();

        for (k=0; k<M; k++) {
            classes_occurrences[k] = 1;
            for (k2=0; k2<M; k2++)
                classes_pair_occurrences[k][k2] = 1;
        }

        Set<Integer> tags = new HashSet<Integer>();
        tags.add(eos);
        k1=output.get(tags);
        classes_occurrences[k]++;

        word = lexmorfo.get_next_word();
        while (word!=null) {
            if (++nw%10000==0)
                System.err.println('.');

            tags = word.get_tags();

            if (tags.size()==0) {
                tags = td.getOpenClass();
            } else if (output.has_not(tags)) {
                String errors;
                errors = "A new ambiguity class was found. I cannot continue.\n";
                errors+= "Word '"+word.get_superficial_form()+"' not found in the dictionary.\n";
                errors+= "New ambiguity class: "+word.get_string_tags()+"\n";
                errors+= "Take a look at the dictionary and at the training corpus. Then, retrain.";
                fatal_error(errors);
            }

            k2=output.get(tags);

            classes_occurrences[k1]++;
            classes_pair_occurrences[k1][k2]++;
            word=lexmorfo.get_next_word();
            k1=k2;
        }

        // Estimation of the number of time each tags occurs in the training text
        for (i=0; i<N; i++) {
            tags_estimate[i] = 0;
            for (k=0; k<M; k++) {
                if (output.get(k).contains(i)) {
                    tags_estimate[i] += classes_occurrences[k]/output.get(k).size();
                }
            }
        }

        //Estimation of the number of times each tag pair occurs
        for (i=0; i<N; i++)
            for (j=0; j<N; j++)
                tags_pair_estimate[i][j] = 0;

        Set<Integer> tags1, tags2;
        for (k1=0; k1<M; k1++) {
            tags1=output.get(k1);
            for (k2=0; k2<M; k2++) {
                tags2 = output.get(k2);
                double noccurrences = classes_pair_occurrences[k1][k2]/(double) (tags1.size()*tags2.size());
                for (Integer itag1 : tags1.toArray(new Integer[tags1.size()]))
                    for (Integer itag2 : tags2.toArray(new Integer[tags2.size()]))
                        tags_pair_estimate[itag1][itag2]+=noccurrences;
            }
        }

        //a[i][j] estimation
        double sum;
        double[][] tmpA = td.getA();
        for (i=0; i<N; i++) {
            sum=0;
            for (j=0; j<N; j++)
                sum+=tags_pair_estimate[i][j];

            for (j=0; j<N; j++) {
                if (sum>0) {
                    tmpA[i][j] = tags_pair_estimate[i][j]/sum;
                } else {
                    tmpA[i][j] = 0;
                }
            }
        }
        td.setA(tmpA);

        //b[i][k] estimation
        double[][] tmpB = td.getB();
        for (i=0; i<N; i++) {
            for (k=0; k<M; k++) {
                if (output.get(k).contains(i)) {
                    if (tags_estimate[i]>0)
                        tmpB[i][k] = (classes_occurrences[k]/output.get(k).size())/tags_estimate[i];
                    else
                        tmpB[i][k] = 0;
                }
            }
        }
        td.setB(tmpB);

        System.err.println();
    }

   /**
    * Initializes the transtion (a) and emission (b) probabilities
    * from a tagged input text by means of the expected-likelihood
    * estimate (ELE) method
    * @param ftagged the input stream with the tagged corpus to process
    * @param funtagged the same corpus to process but untagged
    */
    void init_probabilities_from_tagged_text (InputStream ftagged, InputStream funtagged) throws IOException {
        int i, j, k, nw = 0;
        int N = td.getN();
        int M = td.getM();
        double tags_pair[][] = new double[N][N];
        double emission[][] = new double[N][M];

        MorphoStream stream_tagged = new MorphoStream(ftagged, true, td);
        MorphoStream stream_untagged = new MorphoStream(funtagged, true, td);

        TaggerWord word_tagged = new TaggerWord();
        TaggerWord word_untagged = new TaggerWord();
        Collection output = td.getOutput();

        Set<Integer> tags = new HashSet<Integer>();

        for (i=0; i<N; i++)
            for (j=0; j<N; j++)
                tags_pair[i][j] = 0;
        for (k=0; k<M; k++)
            for (i=0; i<N; k++)
                if (output.get(k).contains(i))
                    emission[i][k] = 0;

        Integer tag1, tag2;
        tag1 = eos;

        // FIXME check get_next_word()
        word_tagged = stream_tagged.get_next_word();
        word_untagged = stream_untagged.get_next_word();
        while (word_tagged != null) {
            System.err.print(word_tagged);
            System.err.println(" -- " + word_untagged);

            if (!word_untagged.get_superficial_form().equals(word_tagged.get_superficial_form())) {
                System.err.println();
                System.err.println("Tagged text (.tagged) and analyzed text (.untagged) streams are not aligned.");
                System.err.println("Take a look at tagged text (.tagged).");
                System.err.println("Perhaps this is caused by a multiword unit that is not a multiword unit in one of the two files.");
                System.err.println(word_tagged + " -- " + word_untagged);
                System.exit(1);
            }

            if (++nw%100==0) {
                System.err.print(".");
                System.err.flush();
            }

            tag2 = tag1;

            if (word_untagged==null) {
                System.err.println("word_untagged==NULL");
                System.exit(1);
            }

            if (word_tagged.get_tags().size()==0) // Unknown word
                tag1 = -1;
            else if (word_tagged.get_tags().size()>1) // Ambiguous word
                System.err.println("Error in tagged text. An ambiguous word was found: " + word_tagged.get_superficial_form());
            else
                tag1 = word_tagged.get_tags().iterator().next();

            if ((tag1>=0) && (tag2>=0))
                tags_pair[tag2][tag1]++;

            if (word_untagged.get_tags().size()==0)
                tags = td.getOpenClass();
            else if (output.has_not(word_untagged.get_tags())) {
                String errors;
                errors = "A new ambiguity class was found. I cannot continue.\n";
                errors+= "Word '"+word_untagged.get_superficial_form()+"' not found in the dictionary.\n";
                errors+= "New ambiguity class: "+word_untagged.get_string_tags()+"\n";
                errors+= "Take a look at the dictionary, then retrain.";
                fatal_error(errors);
            } else {
                tags = word_untagged.get_tags();
            }

            k = output.get(tags);
            if (tag1>=0)
                emission[tag1][k]++;

            word_tagged=stream_tagged.get_next_word();
            word_untagged=stream_untagged.get_next_word();
        }

        //Estimate of a[i][j]
        for (i=0; i<N; i++) {
            double sum=0;
            for(j=0; j<N; j++)
                sum += tags_pair[i][j]+1.0;
            for(j=0; j<N; j++) {
                td.setAElement(i, j, (tags_pair[i][j]+1.0)/sum);
            }
        }

        //Estimate of b[i][k]
        for(i=0; i<N; i++) {
        int nclasses_appear=0;
        double times_appear=0.0;
        for(k=0; k<M; k++)  {
            if (output.get(k).contains(i))  {
                nclasses_appear++;
                times_appear+=emission[i][k];
            }
        }	
        for(k=0; k<M; k++)  {
            if (output.get(k).contains(i))
                td.setBElement(i, k, (emission[i][k]+(1.0/nclasses_appear))/(times_appear+1.0));
            }
        }

        System.err.println();
    }

   /**
    * Applies the forbid and enforce rules found in tagger specification.
    * To do so the transition matrix is modified by introducing null probabilities
    * in the involved transitions.
    */
    void apply_rules () {
        ArrayList<TForbidRule> forbid_rules = td.getForbidRules();
        ArrayList<TEnforceAfterRule> enforce_rules = td.getEnforceRules();
        int N = td.getN();
        int i, j, j2;
        boolean found;

        for (i=0; i < forbid_rules.size(); i++) {
            td.setAElement(forbid_rules.get(i).tagi, forbid_rules.get(i).tagj, ZERO);
        }

        for (i=0; i < enforce_rules.size(); i++) {
            for (j=0; j<N; j++) {
                found = false;
                for (j2=0; j2 < enforce_rules.get(i).tagsj.size(); j2++) {
                    if (enforce_rules.get(i).tagsj.get(j2)==j) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    td.setAElement(enforce_rules.get(i).tagi, j, ZERO);
            }
        }

        // Normalize probabilities
        for(i=0; i<N; i++) {
            double sum=0;
            for(j=0; j<N; j++)
                sum += td.getA()[i][j];
            for(j=0; j<N; j++) {
                if (sum>0)
                    td.setAElement(i, j, td.getA()[i][j]/sum);
                else
                    td.setAElement(i, j, 0);
            }
        }
    }

   /**
    * Reads the expanded dictionary received as a parameter and calculates
    * the set of ambiguity classes that the tagger will manage.
    * @param fdic the input stream with the expanded dictionary to read
    */
    void read_dictionary (InputStream fdic) throws IOException {
        int i, k, nw=0;
        TaggerWord word = new TaggerWord();
        Set<Integer> tags = new HashSet<Integer>();
        Collection output = td.getOutput();

        MorphoStream morpho_stream = new MorphoStream(fdic, true, td);

        word = morpho_stream.get_next_word();

        while (word != null) {
            if (++nw%10000==0) {
                System.err.println(".");
                System.err.flush();
            }

            tags = word.get_tags();

            if (tags.size()>0)
                k = output.get(tags);

            word = morpho_stream.get_next_word();
        }
        System.err.println();

        // OPEN AMBIGUITY CLASS
        // It contains all tags that are not closed.
        // Unknown words are assigned the open ambiguity class
        k=output.get(td.getOpenClass());

        int N = td.getTagIndex().size();

        // Create ambiguity class holding one single tag for each tag.
        // If not created yet
        for(i = 0; i != N; i++) {
            Set<Integer> amb_class = new HashSet<Integer>();
            amb_class.add(i);
            k = output.get(amb_class);
        }

        int M = output.size();

        System.err.println(N + " states and " + M + " ambiguity classes");
        td.setProbabilities(N, M);
    }

    /**
     * Filters ambiguity classes
     * @param in Stream to filter
     * @param out Output
     * @throws IOException
     */
    void filter_ambiguity_classes (InputStream in, OutputStream out) throws IOException {
        Set<Set<Integer>> ambiguity_classes = new HashSet<Set<Integer>>();
        MorphoStream morpho_stream = new MorphoStream (in, true, td);

        TaggerWord word = morpho_stream.get_next_word();

        while (word != null) {
            Set<Integer> tags = word.get_tags();

            if (tags.size()>0) {
                if (!ambiguity_classes.contains(tags)) {
                    ambiguity_classes.add(tags);
                    word.outputOriginal(out);
                }
            }
            word = morpho_stream.get_next_word();
        }
    }

    void train (InputStream ftxt) throws IOException {
          int i, j, k, t, len, nw = 0;
          TaggerWord word = new TaggerWord();
          Integer tag;
          Set<Integer> tags = new HashSet<Integer>();
          Set<Integer> pretags = new HashSet<Integer>();
          Map<Integer, Double> gamma = new HashMap<Integer, Double>();
          Map<Integer, Map<Integer, Double>> alpha = new HashMap<Integer, Map<Integer, Double>>();
          Map<Integer, Map<Integer, Double>> beta = new HashMap<Integer, Map<Integer, Double>>();
          Map<Integer, Map<Integer, Double>> xsi = new HashMap<Integer, Map<Integer, Double>>();
          Map<Integer, Map<Integer, Double>> phi = new HashMap<Integer, Map<Integer, Double>>();
          double prob, loli;
          ArrayList<Set<Integer>> pending = new ArrayList<Set<Integer>>();
          Collection output = td.getOutput();

          int ndesconocidas = 0;

          MorphoStream morpho_stream = new MorphoStream (ftxt, true, td);

          loli = 0;
          tag = eos;
          tags.add(tag);
          pending.add(tags);

          // alpha[0].clear();
          // alpha[0][tag] = 1;
          alpha.put(0, new HashMap<Integer, Double>(tag, 1));

          word = morpho_stream.get_next_word();

          while (word != null) {
              if (++nw%10000==0)
                  System.err.println(".");

              pretags = pending.get(pending.size()-1);

              tags = word.get_tags();

              if (tags.size()==0) {
                  tags = td.getOpenClass();
                  ndesconocidas++;
              }

              if (output.has_not(tags)) {
                  String errors;
                  errors = "A new ambiguity class was found. I cannot continue.\n";
                  errors+= "Word '"+word.get_superficial_form()+"' not found in the dictionary.\n";
                  errors+= "New ambiguity class: "+word.get_string_tags()+"\n";
                  errors+= "Take a look at the dictionary, then retrain.";
                  fatal_error(errors);
              }

              k = output.get(tags);
              len = pending.size();
              
          }
    }

    void tagger (InputStream in, OutputStream out, boolean show_all_good_first) throws IOException {
        int i, j, k, nw;
        TaggerWord word = new TaggerWord();
        Integer tag;

        Set<Integer> tags = new HashSet<Integer>();
        Set<Integer> pretags = new HashSet<Integer>();

        double prob, loli, x;

        int N = td.getN();
        double[][] alpha = new double[2][N];
        IntVector[][] best = new IntVector[2][N];

        ArrayList<TaggerWord> wpend = new ArrayList<TaggerWord>();
        int nwpend;

        MorphoStream morpho_stream = new MorphoStream(in, debug, td);
        morpho_stream.setNullFlush(null_flush);

        Collection output = td.getOutput();

        loli = nw = 0;

        //Initialization
        tags.add(eos);
        alpha[0][eos] = 1;

        word = morpho_stream.get_next_word();

        while (word != null) {
            wpend.add(word);
            nwpend = wpend.size();

            pretags = tags; // Tags from the previous word

            tags = word.get_tags();

            if (tags.size()==0) // This is an unknown word
                tags = td.getOpenClass();

            if (output.has_not(tags)) {
                if (debug) {
                    String errors;
                    	errors = "A new ambiguity class was found. \n";
                        errors+= "Retraining the tagger is neccessary to take it into account.\n";
                        errors+= "Word '"+word.get_superficial_form()+"'.\n";
                        errors+= "New ambiguity class: "+word.get_string_tags()+"\n";
                        System.err.print(errors);
                }
                tags = find_similar_ambiguity_class(tags);
            }

            k = output.get(tags);  //Ambiguity class the word belongs to

            clear_array_double(alpha[nwpend%2], N);
            clear_array_vector(best[nwpend%2], N);

            //Induction
            for (Integer itag : tags) {
                i = itag;
                for (Integer jtag : pretags) {
                    j=jtag;
                    x = alpha[1-nwpend%2][j]*td.getA()[j][i]*td.getB()[i][k];
                    if (alpha[nwpend%2][i]<=x) {
                        if (nwpend>1) {
                            best[nwpend%2][i] = best[1-nwpend%2][j];
                        }
                        best[nwpend%2][i].nodes.add(i);
                        alpha[nwpend%2][i] = x;
                    }
                }
            }

            //Backtracking
            if (tags.size()==1) {
                tag = tags.iterator().next();

                prob = alpha[nwpend%2][tag];

                if (prob>0)
                    loli -= Math.log(prob);
                else {
                    if (debug)
                        System.err.println("Problem with word '"+word.get_superficial_form()+"' "+word.get_string_tags());
                }
                for (int t=0; t<best[nwpend%2][tag].nodes.size(); t++) {
                    if (show_all_good_first) {
                        String micad = wpend.get(t).get_all_chosen_tag_first(best[nwpend%2][tag].nodes.get(t), td.getTagIndex().get("TAG_kEOF"));
                        out.write(micad.getBytes());
                    } else {
                        String micad = wpend.get(t).get_lexical_form(best[nwpend%2][tag].nodes.get(t), td.getTagIndex().get("TAG_kEOF"));
                        out.write(micad.getBytes());
                    }
                }

                alpha[0][tag] = 1;
            }

            if (morpho_stream.getEndOfFile()) {
                if (null_flush) {
                    out.write(0x00);
                }
                out.flush();
                morpho_stream.setEndOfFile(false);
            }
            word = morpho_stream.get_next_word();
        }

        if (tags.size()>1 && debug) {
            String errors;
            errors = "The text to disambiguate has finished, but there are ambiguous words that have not been disambiguated.\n";
            errors = "This message should never appear. If you are reading this ..... this is very bad news.\n";
            System.err.print("Error: "+errors);
        }
    }

    void print_A() {
        int i, j;
        System.out.println("TRANSITION MATRIX (A)");
        System.out.println("-------------------------------");
        for (i=0; i!=td.getN();i++)
            for (j=0; j!=td.getN(); j++) {
                System.out.print("A[" + i + "][" + j + "] = " + td.getA()[i][j]);
            }
    }

    void print_B() {
        int i, k;
        System.out.println("EMISSION MATRIX (B)");
        System.out.println("-------------------------------");
        for (i=0; i!=td.getN();i++)
            for (k=0; k!=td.getM(); k++) {
                Collection output = td.getOutput();
                if (output.get(k).contains(i))
                    System.out.print("B[" + i + "][" + k + "] = " + td.getB()[i][k]);
            }
    }

   /**
    * Prints the ambiguity classes.
    */
    void print_ambiguity_classes () {
        Set<Integer> ambiguity_class = new HashSet<Integer>();
        System.out.println("AMBIGUITY CLASSES");
        System.out.println("-------------------------------");
        for (int i=0; i != td.getM(); i++) {
            ambiguity_class = td.getOutput().get(i);
            System.out.print(i + ": ");
            for (Integer it : ambiguity_class) {
                System.out.print(it + " ");
            }
            System.out.println();
        }
    }

   /**
    * This method returns a known ambiguity class that is a subset of
    * the one received as a parameter. This is useful when a new
    * ambiguity class is found because of changes in the morphological
    * dictionary used by the MT system.
    * @param c set of tags (ambiguity class)
    * @return a known ambiguity class
    */
    Set<Integer> find_similar_ambiguity_class(Set<Integer> c) {
        int size_ret = -1;
        Set<Integer> ret = td.getOpenClass();
        boolean skip_class;
        Collection output = td.getOutput();

        for (int k=0; k<td.getM(); k++) {
            if ((output.get(k).size()>size_ret) && (output.get(k).size()<c.size())) {
                skip_class = false;
                for (Integer it : output.get(k)) {
                    if (c.contains(it)) {
                        skip_class = true;
                        break;
                    }
                }
                if (!skip_class) {
                    size_ret = output.get(k).size();
                    ret = output.get(k);
                }
            }
        }
        return ret;
    }

   /**
    * Used to set the null_flush flag
    */
    void setNullFlush (boolean nf) {
        null_flush = nf;
    }

   /**
    * Helper method - prints an error message and exits
    * @param err The string to print
    */
    private void fatal_error (String err) {
        System.err.println(err);
        System.exit(1);
    }

    /**
     * Make all array positions equal to zero
     * @param a the array
     * @param l length of the array a
     */
    void clear_array_double(double a[], int l) {
        for(int i=0; i<l; i++)
            a[i]=0.0;
    }

    void clear_array_vector(IntVector a[], int l) {
        for(int i=0; i<l; i++)
            a[i]=null;
    }
}
