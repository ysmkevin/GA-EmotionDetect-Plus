/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import test.*;
import simpleclassifier.RegularExpressions;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Carlos
 */
public class NetworkBuilderWords {

    private static TreeMap<String, Integer> words = new TreeMap();
    private static TreeMap<String, Integer> edges = new TreeMap();
    private static TreeMap<String, Integer> edges2 = new TreeMap();
    private static TreeMap<String, String> tokenized = new TreeMap();
    //private static 

    // private static
    public static void main(String[] args) throws FileNotFoundException, IOException {

        int numTweets = 466347;
        int minCount = 1;

   
        String factTweets = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\DATASET\\english\\all_news";
        String emoTweets = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\DATASET\\english\\all_emos";
        String minusNetwork = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\DATASET\\english\\network.net";

        /* for (String patt : words.keySet()) {
         System.out.printf("%d - %s\n", words.get(patt), patt);
         }*/
        
        System.out.println("Processing ...");
        //loadWords(tweets, counts, numTweets, minCount);
        //processTweetsTokenize(tweets, counts, numTweets, minCount);
        //System.out.println("Saving Graph");
        //saveNetwork("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + lang + "\\" + session + "\\output\\networks\\" + type + "\\" + emotion + "_network.net");
        //preProcess("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\nonduplicates");
        minusNetworks(emoTweets, factTweets, minusNetwork, numTweets, minCount, 0.0);

        System.out.println("Done");
    }

    public static void loadWords(String path, String path2, int number, int minCount) {

        TreeMap<String, Integer> temps = new TreeMap();
        try {
            String line, tokens[], word;
            int count = 0;
            int count2 = 0;

            Scanner scan = new Scanner(new File(path), "UTF-8");

            FileOutputStream fstream = null;
            fstream = new FileOutputStream(path2);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));

            while (count2 != number && scan.hasNext()) {
                line = scan.nextLine();

                tokens = line.toLowerCase().split("\t");

                for (int i = 1; i < tokens.length; i++) {
                    word = tokens[i];
                    word = processWord(word);


                    // Updating words
                    if (temps.containsKey(word)) {
                        temps.put(word, temps.get(word) + 1);

                    } else {

                        temps.put(word, 1);
                    }

                }
                count2++;

                System.out.println("Processed " + count2);

            }
            for (Map.Entry<String, Integer> entry : entriesSortedByValuesDec(temps)) {
                count++;
                if (entry.getValue() >= minCount) {
                    line = entry.getValue() + "\t" + entry.getKey();
                    words.put(entry.getKey(), count);
                    out.write(line);
                    out.newLine();
                } else {
                    break;
                }

            }

            // remove low frequency words

            out.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PFICFGraphBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkBuilderWords.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkBuilderWords.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void processTweets(String tweetsFile, int number) {
        try {
            String line, tokens[], edge, prev, word;
            int count = 0;
            int count2 = 0;
            int c, p;
            Scanner scan = new Scanner(new File(tweetsFile), "UTF-8");
            while (count2 != number && scan.hasNext()) {
                line = scan.nextLine();
                prev = "";
                tokens = line.toLowerCase().split("\t");

                for (int i = 1; i < tokens.length; i++) {
                    word = tokens[i];

                    word = processWord(word);

                    if (words.containsKey(word)) {
                        //words.put(word, words.get(word) + 1);


                        // update edges
                        if (prev.length() != 0) {
                            c = words.get(word);
                            p = words.get(prev);
                            edge = p + " " + c;
                            // System.out.println("Edge " + prev + " ---> " + word);
                            if (edges.containsKey(edge)) {
                                edges.put(edge, edges.get(edge) + 1);

                            } else {
                                edges.put(edge, 1);
                            }
                        }
                        prev = word;
                    } else {
                        prev = "";
                    }
                }
                count2++;
                System.out.println("Processed " + count2);

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PFICFGraphBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loadWordsTokenize(String path, int minCount) {
        words = new TreeMap();
        TreeMap<String, Integer> temps = new TreeMap();
        try {
            String line, tokens[], word;
            int count = 0;
            int count2 = 0;

            Iterator<String> it = tokenized.values().iterator();

            FileOutputStream fstream = null;
            fstream = new FileOutputStream(path);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));

            while (it.hasNext()) {
                line = it.next();

                tokens = line.toLowerCase().split("\t");

                for (int i = 1; i < tokens.length; i++) {
                    word = tokens[i];
                    word = processWord(word);

                    // Updating words
                    if (temps.containsKey(word)) {
                        temps.put(word, temps.get(word) + 1);

                    } else {
                        temps.put(word, 1);
                    }

                }
                count2++;

                // System.out.println("Processed " + count2);

            }
            for (Map.Entry<String, Integer> entry : entriesSortedByValuesDec(temps)) {
                count++;
                if (entry.getValue() >= minCount) {
                    line = entry.getValue() + "\t" + entry.getKey();
                    words.put(entry.getKey(), count);
                    out.write(line);
                    out.newLine();
                } else {
                    break;
                }
            }
            // remove low frequency words

            out.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PFICFGraphBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkBuilderWords.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkBuilderWords.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static int processTweetsTokenize(String tweetsFile, String output, int number, int minCount) throws UnsupportedEncodingException, IOException {
        try {
            edges = new TreeMap();
            edges2 = new TreeMap();
            tokenized = new TreeMap();

            String line, tokens[], edge, edge2, prev, word;
            int count = 0;
            int count2 = 0;
            int c, p;
            Scanner scan = new Scanner(new File(tweetsFile), "UTF-8");

            FileOutputStream fstream=null;
            
            fstream = new FileOutputStream(tweetsFile + "_tokenized");
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));
            
         
            
            // put in treemap to avoid repeats
            while (tokenized.size() != number && scan.hasNext()) {
                line = tokenize(scan.nextLine().toLowerCase().trim());
                tokenized.put(line, line);
            }
            System.out.println("Unique tweets: " + tokenized.size());
            loadWordsTokenize(output, minCount);
            Iterator<String> it = tokenized.values().iterator();
            
            // tokenize
            while (it.hasNext()) {
                line = it.next();
                prev = "";
                tokens = line.toLowerCase().split("\t");

                for (int i = 1; i < tokens.length; i++) {
                    word = tokens[i];
                    word = processWord(word);

                    if (words.containsKey(word)) {
                        //words.put(word, words.get(word) + 1);
                        // update edges
                        if (prev.length() != 0) {
                            c = words.get(word);
                            p = words.get(prev);
                            edge = p + " " + c;
                            edge2 = prev + " " + word;
                            //System.out.println("Edge " + prev + " ---> " + word);
                            if (edges.containsKey(edge)) {
                                edges.put(edge, edges.get(edge) + 1);
                                edges2.put(edge2, edges2.get(edge2) + 1);
                            } else {
                                edges.put(edge, 1);
                                edges2.put(edge2, 2);
                            }
                        }
                        prev = word;
                    } else {
                        prev = "";
                    }
                }
                out.write(line+"\n");
                
                count2++;
                
            }
            System.out.println("Processed " + count2);
            return count2;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PFICFGraphBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public static TreeMap<String, Double> rankEdges() {
        // Ranked edges
        TreeMap<String, Double> rankedEdges = new TreeMap();

        boolean maxFlag = false;
        double max = 0.0;

        for (Map.Entry<String, Integer> entry : entriesSortedByValuesDec(edges2)) {
            if (!maxFlag) {
                max = entry.getValue();
                maxFlag = true;
            }
            rankedEdges.put(entry.getKey(), (double) entry.getValue() / (double) max);

        }

        return rankedEdges;
    }

    public static void minusNetworks(String path1, String path2, String output, int numTweets, int minCount, double diffTh) {
        try {
            TreeMap<String, Double> rEdges1, rEdges2;
            TreeMap<String, Double> fEdges = new TreeMap();

            int count;

            System.out.println("Processing " + path1);
            // ATTENTION change for emotions
            processTweetsTokenize(path1, path1 + "_counts",466347, minCount);
            rEdges1 = rankEdges();
            System.out.println("Edges in graph 1 " + rEdges1.size());

            int c = 0;
            for (Map.Entry<String, Double> entry : entriesSortedByValuesDec(rEdges1)) {
                if (c == 10) {
                    break;
                }
                System.out.println(entry.getValue() + "\t" + entry.getKey());
                c++;
            }


            System.out.println("Processing " + path2);
            // ATTENTION change for news
            processTweetsTokenize(path2, path2 + "_counts",34592, minCount);
            rEdges2 = rankEdges();
            System.out.println("Edges in graph 2 " + rEdges2.size());
            c = 0;
            for (Map.Entry<String, Double> entry : entriesSortedByValuesDec(rEdges2)) {
                if (c == 10) {
                    break;
                }
                System.out.println(entry.getValue() + "\t" + entry.getKey());
                c++;
            }

            words = new TreeMap();
            edges = new TreeMap();

            Iterator<String> it = rEdges1.keySet().iterator();
            String edge, tokens[];
            double value;
            count = 0;
            while (it.hasNext()) {
                edge = it.next();
                if (rEdges2.containsKey(edge)) {
                    value = rEdges1.get(edge) - rEdges2.get(edge);
                    if (value >= diffTh) {
                        fEdges.put(edge, value);
                        tokens = edge.split(" ");
                        count = processEdge(tokens[0], tokens[1], count);
                    }
                } else {
                    if (rEdges1.get(edge) >= diffTh) {
                        fEdges.put(edge, rEdges1.get(edge));
                        tokens = edge.split(" ");
                        count = processEdge(tokens[0], tokens[1], count);
                    }
                }
            }

            FileOutputStream fstream = null;
            FileOutputStream fstream2 = null;

            fstream = new FileOutputStream(path1 + "_minused");
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));

            for (Map.Entry<String, Double> entry : entriesSortedByValuesDec(fEdges)) {

                out.write(entry.getValue() + "\t" + entry.getKey());
                out.newLine();
            }
            out.close();
            
            saveNetwork(output);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NetworkBuilderWords.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkBuilderWords.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkBuilderWords.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void saveNetwork(String path) {



        FileOutputStream fstream = null;
        FileOutputStream fstream2 = null;
        try {
            fstream = new FileOutputStream(path);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));

            fstream2 = new FileOutputStream(path + "_vertices");
            BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(fstream2, "UTF8"));

            Iterator<String> it1 = words.keySet().iterator();
            TreeMap<String, Integer> temp;
            int count = 0;
            String line;
            // String patt;

            //Write the vertices names
            out.write("*Vertices " + words.size());
            out.newLine();
            for (Map.Entry<String, Integer> entry : entriesSortedByValues(words)) {
                count++;
                line = entry.getValue() + " \"" + entry.getKey() + "\" 0.0 0.0 0.0";

                out.write(line);
                out.newLine();

                out2.write(entry.getValue() + "\t" + entry.getKey());
                out2.newLine();
            }
            out2.close();

            //Write the Edges
            out.write("*Arcs ");
            out.newLine();
            boolean maxFlag = false;
            double max = 0.0;
            for (Map.Entry<String, Integer> entry : entriesSortedByValuesDec(edges)) {
                if (!maxFlag) {
                    max = entry.getValue();
                    maxFlag = true;
                }
                out.write(entry.getKey() + " " + (double) entry.getValue() / (double) max);
                out.newLine();

            }


            out.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(PFICFGraphBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PFICFGraphBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(PFICFGraphBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e2, Map.Entry<K, V> e1) {
                int res = e2.getValue().compareTo(e1.getValue());
                return res != 0 ? res : 1; // Special fix to preserve items with equal values
            }
        });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValuesDec(Map<K, V> map) {
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

    public static String processWord(String word) {

        word = word.toLowerCase();

        // If HT
        if (word.charAt(0) == '#') {
            word = "<hashtag>";
        }

        //If URL
        if (word.contains("http") || word.contains("https")) {
            word = "<url>";
        }

        //If mini URL
        if (word.length() > 3 && word.substring(0, 3).equals("co/")) {
            word = "<minurl>";
        }


        //If user mention
        if (word.charAt(0) == '@') {
            word = "<usermention>";
        }

        return word;
    }

    public static String tokenize(String line) {

        line = line.replaceAll(RegularExpressions.umRegex, "<usermention>");
        line = line.replaceAll(RegularExpressions.htRegex, "<hashtag>");
        line = line.replaceAll(RegularExpressions.urlRegEx, "<url>");
        

        String tokenized = "";

        String token;
        StringTokenizer tokenizer;


        String delimiters = RegularExpressions.token_delimRegex;
        String punctRegex = RegularExpressions.punctRegex;

        String prev = "a";
        String charSeq = "";

        // System.out.println("line: "+line);
        tokenizer = new StringTokenizer(line, delimiters, true);
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            // System.out.println("TOKEN "+token);
            if (token.matches(punctRegex) && !prev.matches(punctRegex)) {
                charSeq = token;
            }
            if (token.matches(punctRegex) && prev.matches(punctRegex)) {
                charSeq += token;
            }
            if (!token.matches(punctRegex) && prev.matches(punctRegex)) {
                tokenized += charSeq + "\t";
                charSeq = "";

            }
            if (!token.matches(punctRegex) && !token.matches("\\s")) {
                tokenized += token + "\t";
            }
            prev = token;

        }
        if (charSeq.length() > 0) {
            tokenized += charSeq + "\t";
        }
        //System.out.println(tokenized);
        return tokenized;
    }

    public static void preProcess(String path) {
        try {
            Scanner scan = new Scanner(new File(path), "UTF-8");

            String line, tokens[];

            FileOutputStream fstream = null;
            fstream = new FileOutputStream(path + "_processed");
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));

            while (scan.hasNext()) {
                line = scan.nextLine().toLowerCase().split("\t")[1].trim();

                tokens = line.split(" ");

                if (tokens.length > 3) {
                    out.write(line);
                    out.newLine();
                }
            }
        } catch (FileNotFoundException ex) {

            Logger.getLogger(NetworkBuilderWords.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkBuilderWords.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int processEdge(String prev, String word, int count) {
        int c;
        int p;
        String edge;

        // First if the words are not already in the list with its index then add them
        if (!words.containsKey(prev)) {
            count++;
            words.put(prev, count);
        }
        if (!words.containsKey(word)) {
            count++;
            words.put(word, count);
        }


        c = words.get(word);
        p = words.get(prev);
        edge = p + " " + c;

        //System.out.println("Edge " + prev + " ---> " + word);
        if (edges.containsKey(edge)) {
            edges.put(edge, edges.get(edge) + 1);

        } else {
            edges.put(edge, 1);

        }

        return count;
    }
}
