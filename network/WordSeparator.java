/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class WordSeparator {

    public static void main(String[] args) {

       // String valuesPath = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\french\\french1\\output\\networks\\words\\ranks\\minused_tabs";
        
        String valuesPath = "C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\indonesian\\jammin\\minused_tab_cc1";
        
       // String valuesPath = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\output\\networks\\words\\ranks\\minused_tab";
        
        //String outputPath = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\french\\french1\\output\\networks\\words\\lists\\hw";
        
        String outputPath = "C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\indonesian\\jammin\\pw";
        
      //  String outputPath = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\output\\networks\\words\\lists\\hw";

        // eigenvector centrality
        /*int valueCol = 2;
        double th = 0.001;*/
        //eng, esp = 0.0054  fr = 0.007

        //clustering coeff
        int valueCol = 2;
        double th = 0.03;
        //eng fr 0.5 es 0.33
        
        int minFreq = 2;
        
        selectWords(valuesPath, outputPath, valueCol, th, minFreq);

    }

    public static void selectWords(String inPath, String outPath, int col, double th, int minFreq) {

        TreeMap<String, Double> list = new TreeMap();

        try {
            String line, tokens[];
            double value;
            int count = 0;

            Scanner scan = new Scanner(new File(inPath), "UTF-8");

            // skip first line (headers)
            line = scan.nextLine();

            while (scan.hasNext()) {
                line = scan.nextLine();

                tokens = line.split("\t");
                value = Double.parseDouble(tokens[col]);
                if (value > th /*&& Integer.parseInt(tokens[9]) >= minFreq*/) {
                    if (list.containsKey(tokens[1])) {
                        System.out.println("Repeated " + tokens[1] + " value " + value + " ---- " + list.get(tokens[1]));

                    }
                    list.put(tokens[1], value);
                    //System.out.printf("%s -> %f \n", tokens[1], value);
                    count++;
                }
            }
            System.out.println(count);
            System.out.println("No. of selected tokens = " + list.size());

            FileOutputStream fstream = null;

            fstream = new FileOutputStream(outPath);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));


            for (Map.Entry<String, Double> entry : entriesSortedByValues(list)) {
              
                    //  System.out.printf("%s\t%f\n", entry.getKey(), patts.get(entry.getKey()), entry.getValue());
                    line = entry.getValue()/1 + "\t" + entry.getKey();
                    out.write(line);
                    out.newLine();
                
            }

            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RankVertices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WordSeparator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordSeparator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    

    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                int res = e2.getValue().compareTo(e1.getValue());
                return res != 0 ? res : 1; // Special fix to preserve items with equal values
            }
        });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
