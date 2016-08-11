/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filter3;

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
        String session = "english/english1";
        String meta = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\input\\meta_patterns_pmi";
        String out = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\output\\pmi\\patterns\\patterns_5";
       // String meta = "C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\indonesian\\jammin\\patterns\\meta";
        //String out = "C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\indonesian\\jammin\\patterns\\patterns_5";
       // String out = "C:\\Users\\Carlos\\Dropbox\\NTHU\\Research\\My paper\\Paper Submissions\\Experiments\\Reduce emotion patterns\\patterns19";
        int minFreq = 19;

        // For couting the PF
        //String patts = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Carlos\\english1\\output\\pmi\\patterns\\patterns5";
        //String emos = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\DATASET\\english\\jammin\\patterns\\6 Emotions";
        //String emos = "C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\spanish\\old";
        //String outPF = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\OUTPUT\\6emo";
        
        //String patts = "C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\indonesian\\jammin\\patterns\\patterns_5";
        //String emos = "C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\indonesian\\jammin\\emotions";

        //String outPF = "C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\indonesian\\jammin\\patterns\\5";
        
        //String patts = "C:\\Users\\Carlos\\Dropbox\\NTHU\\Research\\My paper\\Paper Submissions\\Experiments\\Reduce emotion patterns\\patterns19";
        //String emos = "C:\\Users\\Carlos\\Dropbox\\workspace\\weka experiments\\datasets\\training\\english\\6 classes\\emotions";
       
        //String outPF = "C:\\Users\\Carlos\\Dropbox\\workspace\\weka experiments\\datasets\\training\\english\\6 classes\\patterns\\19";

        // For counting the PF from dynamic file
        //String dynPath = "F:\\Jammin\\index_kw_en_frequencies_file";
        //String outPF2 = "C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\english\\jammin\\patterns\\5_dyn_20150216";

         selectFinalPatterns(meta, out, minFreq);
        //countPatternsPerEmotion(patts, emos, outPF);
       // countPatternsPerEmotionFromDynamic(dynPath, outPF2);

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
        //String patterns = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\output\\output_top50_bottom0\\final_patterns";
        // String patterns = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\output\\output_top50_bottom0\\final_patterns";
        String line, tokens[], patt, pattern = "";
        //Scanner scan;
        BufferedReader br;

        //ATTENTION
        // Fr 55, En 11, Sp 17
        // Get and merge the patterns
        // File folder = new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\french\\french1\\input\\meta_patterns3");
        // File folder = new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\input\\meta_patterns_pmi");
        //File folder = new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\spanish\\spanish2\\input\\meta_patterns_pmi");
        File folder = new File(metaPath);

        //File folder = new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\spanish\\spanish2\\input\\meta_patterns3\\test");
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
