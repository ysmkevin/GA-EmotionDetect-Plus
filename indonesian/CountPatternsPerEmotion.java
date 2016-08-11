package indonesian;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import simpleclassifier.RegularExpressions;

/**
 *
 * @author Carlos
 */
public class CountPatternsPerEmotion {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // For getting the final patterns
        String out = "K:\\Codes\\parallel_simple_emo8\\data\\final_patterns";
        String meta = "K:\\Codes\\parallel_simple_emo8\\data\\meta_emo8";
        int minFreq = 100;

        // For couting the PF
        String patts = "K:\\Codes\\parallel_simple_emo8\\data\\final_patterns";
        String emos = "K:\\Codes\\parallel_simple_emo8\\data\\emo8_split\\" + args[0];
        String outPF = "K:\\Codes\\parallel_simple_emo8\\data\\emo8_patterns";
        
        //selectFinalPatterns(meta, out, minFreq);
        countPatternsPerEmotion(patts, emos, outPF);
        //countPatternsPerEmotionFromDynamic(dynPath, outPF2);

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

    public static void selectFinalPatterns(String metaPath, String outPath, int minFreq) {
        String line, tokens[], patt, pattern = "";
        //Scanner scan;
        BufferedReader br;

        //ATTENTION
        // Fr 55, En 11, Sp 17
        // Get and merge the patterns
        File folder = new File(metaPath);

        String cwRegex = RegularExpressions.cwRegex;
        String delimRegex = RegularExpressions.delimRegex;

        String htRegex = RegularExpressions.htRegex;
        String umRegex = RegularExpressions.umRegex;
        String urlRegex = RegularExpressions.urlRegEx;
        String minurlRegex = RegularExpressions.urlRegEx;

        TreeMap<String, String> patts = new TreeMap();
        try {
            for (final File fileEntry : folder.listFiles()) {
                //If it is not a directory, hence is a file
                if (!fileEntry.isDirectory()) {

                    //scan = new Scanner(new File(fileEntry.getAbsolutePath()), "UTF-8");
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(fileEntry.getAbsolutePath()), "UTF8"));
                    while ((line = br.readLine()) != null) {
                        // line = scan.nextLine();
                        tokens = line.split("\t");
                        patt = tokens[0].trim();

                        if (Integer.parseInt(tokens[2]) >= minFreq) {
                            System.out.println(patt);
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

                            patts.put(pattern, patt);
                        }
                    }

                }

            }

            System.out.println("Number of patterns: " + patts.size());
            //ATTENTION
            FileOutputStream fstream = new FileOutputStream(outPath);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));
            Iterator<String> it3 = patts.keySet().iterator();
            while (it3.hasNext()) {
                out.write(it3.next());
                out.newLine();
            }
            out.close();
            System.out.println("SAVED!");

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static TreeMap<String, String> loadPatts(String in) {
        BufferedReader br = null;;
        TreeMap<String, String> patts = new TreeMap();
        try {
            String line;
            int count = 0;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF8"));
            while ((line = br.readLine()) != null) {
                line = line.trim();
                patts.put(line, line);
                count++;
            }
            System.out.println("Loaded " + count + " patterns");
            return patts;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static void countPatternsPerEmotion(String pattPath, String emosPath, String outPath) {
        System.out.println("Using patterns: "+pattPath);
        System.out.println("Using emotions: "+emosPath);
        BufferedReader br;
        File folder = new File(emosPath);
        Matcher matcher;

        int max = 0;
        TreeMap<String, String> patts = loadPatts(pattPath);

        String line, tokens[], patt, pattern = "";

        String cwRegex = RegularExpressions.cwRegex;

        String delimRegex = RegularExpressions.delimRegex;

        String hashtagRegex = RegularExpressions.htRegex;

        String userRegex = RegularExpressions.umRegex;

        String urlRegex = RegularExpressions.urlRegEx;

        String minurlRegex = RegularExpressions.urlRegEx;

        try {
            for (final File fileEntry : folder.listFiles()) {
                //If it is not a directory, hence is a file
                if (!fileEntry.isDirectory()) {

                    //scan = new Scanner(new File(fileEntry.getAbsolutePath()), "UTF-8");
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(fileEntry.getAbsolutePath()), "UTF8"));
                    System.out.println(fileEntry.getAbsolutePath());

                    //create file
                    FileOutputStream fstream = new FileOutputStream(outPath + "\\out_" + fileEntry.getName());

                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));

                    TreeMap<String, Integer> countByEmotion = new TreeMap();

                    while ((line = br.readLine()) != null) {
                        // line = scan.nextLine();
                        //System.out.println(line);
                        line = line.toLowerCase().trim();
                        Iterator<String> it = patts.keySet().iterator();
                        int count = 0;
                        //System.out.println(line);
                        while (it.hasNext()) {
                            pattern = it.next();

                            matcher = Pattern.compile(pattern).matcher(line);

                            while (matcher.find()) {

                                count++;
                                //  System.out.println(patts.get(pattern)+" "+matcher.group());
                            }

                            pattern = pattern.replace(cwRegex, "CW");
                            pattern = pattern.replace(delimRegex, " ");
                            pattern = pattern.replace(hashtagRegex, "<hashtag>");
                            pattern = pattern.replace(userRegex, "<usermention>");
                            pattern = pattern.replace(urlRegex, "<url>");
                            pattern = pattern.replace(minurlRegex, "<minurl>");

                            if (countByEmotion.containsKey(pattern)) {
                                countByEmotion.put(pattern, countByEmotion.get(pattern) + count);
                            } else {
                                countByEmotion.put(pattern, count);
                            }

                            count = 0;
                        }

                    }

                    Iterator<String> it2 = countByEmotion.keySet().iterator();
                    while (it2.hasNext()) {
                        patt = it2.next();
                        if (countByEmotion.get(patt) > max) {
                            max = countByEmotion.get(patt);
                        }
                        line = patt + "\t" + countByEmotion.get(patt);
                        out.write(line);
                        out.newLine();
                    }
                    out.close();

                }

                System.out.println("Max TF " + max);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void countPatternsPerEmotionFromDynamic(String inPath, String outPath) {
        BufferedReader br = null;
        BufferedWriter out = null;
        TreeMap<String, String> emotions = new TreeMap();
        try {
            String line, tokens[], emotion, pattern, pf;
            
            int count = 0;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(inPath), "UTF8"));
            while ((line = br.readLine()) != null) {
                line = line.trim();
                tokens = line.split("\t");

                emotion = tokens[2];
                pattern = tokens[1];
                pf = tokens[5];

                //
                FileOutputStream fstream;
                if (!emotion.contains("/")) {
                    if (!emotions.containsKey(emotion)) {
                        System.out.println(emotion);
                        fstream = new FileOutputStream(outPath + "\\out_" + emotion, false);
                        if (count != 0) {
                            out.close();
                        }
                        out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));
                        emotions.put(emotion, emotion);
                    }

                    out.write(pattern+"\t"+pf);
                    out.newLine();
                    count++;
                }
                
            }
            System.out.println("Loaded " + count + " patterns in " + emotions.size() + " emotions");
            out.close();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(CountPatternsPerEmotion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
