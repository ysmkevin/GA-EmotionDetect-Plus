/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleclassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedSet;
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
public class PFICFRankClassifier {

    private static int count = 0;
    private static int right = 0;
    private static int wrong = 0;
    private static TreeMap<String, ClassStats> stats = new TreeMap();
    private static FileOutputStream fstream;
    private static BufferedWriter out;
    private static TreeMap<String, Integer> mood = new TreeMap();
    private static double maxRaw = 0.0;
    private static double minRaw = 0.0;
    private static double maxSig = 0.0;
    private static double minSig = 0.0;

    // read a tweet and classify it
    public static void readTweets(String path, int mode) {
        try {

            //Scanner scan = new Scanner(new File(path), "UTF-8");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
            String line, emo1 = "", emo2 = "", tokens[];

            int count = 0;

            //while (scan.hasNext()) {
            while ((line = br.readLine()) != null) {

                ArrayList<String> words = new ArrayList();
                //line = scan.nextLine();
                tokens = line.split("\t");
                line = tokens[0].trim();

                if (tokens.length < 2) {
                    continue;
                }
                if (mode == 1) {
                    emo1 = tokens[1].trim();
                    if (tokens.length > 2) {
                        emo2 = tokens[2].trim();
                    } else {
                        emo2 = emo1;
                    }
                }

                //THIS IS TO CHANGE TO 6 EMOS
                if (emo1.equals("sarcasm") || emo1.equals("ambiguous") || emo1.equals("fact")) {
                    continue;
                }

                if (emo1.equals("disgust")) {
                    emo1 = "anger";
                }

                if (emo1.equals("anticipation")) {
                    emo1 = "hope";
                }

                if (emo1.equals("trust")) {
                    emo1 = "hope";
                }

                if (emo2.equals("sarcasm") || emo2.equals("ambiguous") || emo2.equals("fact")) {
                    continue;
                }

                if (emo2.equals("disgust")) {
                    emo2 = "anger";
                }

                if (emo2.equals("anticipation")) {
                    emo2 = "hope";
                }

                if (emo2.equals("trust")) {
                    emo2 = "hope";
                }

                eval(line, emo1, emo2, 1, mode, path);
                count++;

            }

            File out = new File("C:\\Users\\Kevin\\Documents\\Resultz\\weights");
            FileWriter w = new FileWriter(out,true);
            w.write("Accuracy: " + ((double) right / (double) count)+"\n");
            w.close();
            
            System.out.println("Classified: " + count);
            System.out.println("Correct: " + right);
            System.out.println("Accuracy: " + ((double) right / (double) count));
            
            //reset back the counter for next classification
            right=0;
            count=0;
            
            

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void eval(String tweet, String emo1, String emo2, int type, int mode, String path) throws FileNotFoundException {

        tweet = tweet.toLowerCase();

        String line, tokens[], patt, pattern = "";
        Scanner scan;
        double score;

        String cwRegex = RegularExpressions.cwRegex;
        String delimRegex = RegularExpressions.delimRegex;

        String htRegex = RegularExpressions.htRegex;
        String umRegex = RegularExpressions.umRegex;
        String urlRegex = RegularExpressions.urlRegEx;
        String minurlRegex = RegularExpressions.urlRegEx;

        TreeMap<String, Double> patts = new TreeMap();
        TreeMap<Double, String> classi = new TreeMap();
        TreeMap<String, Double> temp = new TreeMap();

        int words = tweet.split("\\s").length;
        int patterns2 = 0;
        int patterns3 = 0;
        boolean once = false;

        BufferedReader br;
        // for patterns
        if (type == 1) {
            File folder = new File("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Genetic Algo\\6 emotions tficf weighted");
            for (final File fileEntry : folder.listFiles()) {
                //If it is not a directory, hence is a file
                if (!fileEntry.isDirectory()) {
                    try {
                        br = new BufferedReader(new InputStreamReader(new FileInputStream(fileEntry.getAbsolutePath()), "UTF8"));
                        // System.out.println("Comparing to "+fileEntry.getAbsolutePath());
                        double finalScore = 0.0;
                        int pos = 0;
                        while ((line = br.readLine()) != null) {
                            pos++;

                            tokens = line.split("\t");
                            patt = tokens[0].trim();

                            score = (double) pos;
                            tokens = patt.split(" ");

                            pattern = delimRegex;
                            for (String t : tokens) {

                                if (t.equals(".+")) {
                                    t = cwRegex;
                                }
                                if (t.equals("<hashtag>")) {
                                    t = htRegex;
                                }
                                if (t.equals("<usermention>")) {
                                    t = umRegex;
                                }
                                if (t.equals("<url>")) {
                                    t = urlRegex;
                                }
                                if (t.equals("<minurl>")) {
                                    t = minurlRegex;
                                }

                                pattern += t + delimRegex;
                            }

                            //System.out.println(tweet);
                            Matcher matcher = Pattern.compile(pattern).matcher(tweet);
                            while (matcher.find()) {

                                finalScore += score;
                                if (!once) {

                                    if (tokens.length == 2) {
                                        patterns2++;
                                    }
                                    if (tokens.length == 3) {
                                        patterns3++;
                                    }
                                }
                            }

                            patts.put(pattern, score);

                        }
                        once = true;
                        if (finalScore != Double.NEGATIVE_INFINITY && finalScore != Double.POSITIVE_INFINITY) {
                            System.out.println("Score for "+ fileEntry.getName().split("_")[1]+" "+finalScore);
                            temp.put(fileEntry.getName().split("_")[1], finalScore);
                            //classi.put(finalScore, fileEntry.getName().split("_")[1]);
                            
                            
                        } else {
                            break;
                        }
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }

        }

        if (temp.size() > 1) {
            count++;
            boolean correct = false;

            SortedSet map = entriesSortedByValues(temp);

            ClassStats cs1, cs2;

            //Iterator<String> it = classi.descendingMap().values().iterator();
            Iterator<Entry> it = map.iterator();
            Map.Entry<String, Double> guess1 = it.next();
            Map.Entry<String, Double> guess2 = it.next();
            Map.Entry<String, Double> guess3 = it.next();
            Map.Entry<String, Double> guess4 = it.next();
            // System.out.println("---------------------------------------------------");
            //   System.out.println(tweet + "  || " + emo1 + " " + emo2);

            System.out.println(tweet + "\t" + guess1.getKey() + "\t" + guess2.getKey() );

            try {
                out.write(tweet + "\t" + guess1.getKey() + "\t" + guess2.getKey());
                out.newLine();
            } catch (IOException ex) {
                Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (mood.containsKey(guess1.getKey())) {
                mood.put(guess1.getKey(), mood.get(guess1.getKey()) + 1);
            } else {
                mood.put(guess1.getKey(), 1);
            }

            if (mood.containsKey(guess2.getKey())) {
                mood.put(guess2.getKey(), mood.get(guess2.getKey()) + 1);
            } else {
                mood.put(guess2.getKey(), 1);
            }

            if (mode == 1) {
                try {
                    //out.write(tweet + "\t" + emo1 + "\t" + emo2 + "\t" + guess1.getKey() + "\t" + guess2.getKey());

                    // Stats for emo1
                    if (stats.containsKey(emo1)) {
                        cs1 = stats.get(emo1);
                    } else {
                        cs1 = new ClassStats(emo1);
                    }
                    cs1.incCount();

                    //stats for emo2
                    if (stats.containsKey(emo2)) {
                        cs2 = stats.get(emo2);
                    } else {
                        cs2 = new ClassStats(emo2);
                    }
                    cs2.incCount();

                    if (emo1.equals(guess1.getKey()) || emo1.equals(guess2.getKey())) {
                        //System.out.print("Right");
                        //out.write("\tright");
                        right++;
                        correct = true;
                        cs1.incCorrect();
                    } else {
                        cs1.incWrong();
                    }

                    if (emo2.equals(guess1.getKey()) || emo2.equals(guess2.getKey())) {
                        if (!correct) {
                            //System.out.print("Right");
                            //out.write("\tright");
                            right++;
                            correct = true;
                        }
                        cs2.incCorrect();
                    } else {
                        cs2.incWrong();
                    }

                    if (!correct) {
                        //System.out.print("Wrong");
                        //out.write("\twrong");
                        wrong++;

                    }

                    stats.put(emo1, cs1);
                    stats.put(emo2, cs2);

                    out.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                        int res = e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1; // Special fix to preserve items with equal values
                    }
                });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValuesDesc(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(Map.Entry<K, V> e2, Map.Entry<K, V> e1) {
                        int res = e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1; // Special fix to preserve items with equal values
                    }
                });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    public static void printStats() throws FileNotFoundException, IOException {
        Iterator<ClassStats> itEmo = stats.values().iterator();
        System.out.println("\n\n");
        
        File out = new File("C:\\Users\\Kevin\\Documents\\Resultz\\weights");
        FileWriter fw = new FileWriter(out,true);
        while (itEmo.hasNext()) {
            ClassStats cs = itEmo.next();
            //System.out.println(cs.getName() + "\t" + (double) cs.getCorrect() / (double) cs.getCount());
            fw.append("\n"+cs.getName() + "\t\n" + (double) cs.getCorrect() / (double) cs.getCount());
            System.out.println("Saved in "+out);
        }
        fw.write("\n");
        fw.close();
    }


    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {

       // THe file to classify
        //File file = new File("C:\\Users\\Kevin\\Documents\\annotated\\mixed");
        
        //File file = new File("C:\\Users\\Kevin\\Documents\\annotated\\products\\carlos");
        
        File file = new File("C:\\Users\\Kevin\\Documents\\annotated\\soccer\\carlos");
        
        fstream = new FileOutputStream(file + "_sentiment");
        

        out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));
          
        readTweets(file.getPath(), 1);

        out.close();

        printStats();
    }
}
