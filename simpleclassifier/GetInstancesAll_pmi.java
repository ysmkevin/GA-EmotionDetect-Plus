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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
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
public class GetInstancesAll_pmi {

    private static String session;
    //private static TreeMap<String, Integer> patterns = new TreeMap();
    private static TreeMap<String, Integer> instancesCount = new TreeMap();
    private static TreeMap<String, TreeMap<String, Integer>> patternsAndInts = new TreeMap();
    private static TreeMap<String, Integer> patterns = new TreeMap();
    private static TreeMap<String, Double> patternsReliability = new TreeMap();
    private static TreeMap<String, TreeMap<String, Double>> pmis = new TreeMap();
    private static double maxPMI = Double.NEGATIVE_INFINITY;

    public static void main(String[] args) {

        //ATTENTION
        session = "english/english1";

        getInstances("CW_CW_HW");
        getInstances("CW_HW_CW");
        getInstances("HW_CW_CW");

        getInstances("HW_HW_CW");
        getInstances("HW_CW_HW");
        getInstances("CW_HW_HW");

        getInstances("CW_HW");
        getInstances("HW_CW");
       
        getPatterns("CW_CW_HW");
        getPatterns("CW_HW_CW");
        getPatterns("HW_CW_CW");

        getPatterns("HW_HW_CW");
        getPatterns("HW_CW_HW");
        getPatterns("CW_HW_HW");
        
        getPatterns("CW_HW");
        getPatterns("HW_CW");
       

        //computePMI();
        //computeReliability();

        System.out.println("Total patterns " + patternsAndInts.size());
        System.out.println("Total instances " + instancesCount.size());

    }

    public static void getInstances(String meta_pattern) {
        try {
            int W;

            String regexpPWs = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\DATASET\\english\\pw";
          //  String regexpPWs = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\input\\pw";
            //String regexpHWs = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\output\\networks\\words\\lists\\hw";
           // String regexpHWs = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\spanish\\spanish2\\output\\networks\\words\\lists\\hw";
            //String regexpHWs = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\output\\networks\\words\\lists\\hw";
            String regexpHWs = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\DATASET\\english\\cw";
            // String regexpHWs = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\input\\hw_noht";


            // String regexpPWs = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\input\\pw";
            //String regexpHWs = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\input\\hw";

            //String regexpPWs = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\spanish\\spanish2\\input\\pw";
            //String regexpHWs = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\spanish\\spanish2\\input\\hw";


            String pattern, cleanToken, token, tokens[], pattTokens[], line;
            String separator = " ";
            int current;
            String regExp;

            pattTokens = meta_pattern.split("_");
            W = pattTokens.length;


            //ATTENTION
            // Scanner scan = new Scanner(new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\input\\spanish_tokenized"), "UTF-8");
            
           // Scanner scan = new Scanner(new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\input\\english_tokenized2"), "UTF-8");

             //Scanner scan = new Scanner(new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\spanish\\spanish2\\input\\spanish_tokenized2"), "UTF-8");
             
           // String file = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\"+session+"\\input\\spanish_tokenized2";
            String file = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Carlos\\english1\\input\\english_tokenized2";
            
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            
            //FileOutputStream fstream = new FileOutputStream("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\input\\instances_pmi\\2\\instances_" + meta_pattern);
            
            FileOutputStream fstream = new FileOutputStream("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\OUTPUT\\english\\instances_" + meta_pattern);
                
            // FileOutputStream fstream = new FileOutputStream("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\input\\instances2\\instances_" + meta_pattern);
            //FileOutputStream fstream = new FileOutputStream("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\spanish\\spanish2\\input\\instances2\\test\\instances_" + meta_pattern);

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF-8"));

            TreeMap<String, Integer> instances = new TreeMap();

            TreeMap<String, Double> hws = new TreeMap();
            TreeMap<String, Double> pws = new TreeMap();
            TreeMap<String, Double> ws = new TreeMap();

            load(hws, regexpHWs);
            load(pws, regexpPWs);

           while ((line = br.readLine()) != null) {

                tokens = line.toLowerCase().split("\t");

                for (int i = 1; i < (tokens.length - W) + 1; i++) {
                    pattern = "";
                    for (int j = 0; j < W; j++) {
                        current = i + j;
                        token = tokens[current];
                        cleanToken = processWord(token);
                        /* if(token.matches("\\p{P}+")){
                         separator = "";
                         }else{
                         separator = " ";
                         }*/

                        if (pattTokens[j].equals("CW")) {
                            ws = pws;
                            //info = "psych/";
                        } else {
                            ws = hws;
                        }
                        if (ws.containsKey(token.trim())) {
                            // pattern += info + token + " ";
                            pattern += separator + cleanToken;
                        } else {
                            break;
                        }

                        if ((j + 1) == W) {
                            pattern = pattern.trim();
                            if (!instances.containsKey(pattern)) {
                                instances.put(pattern, 1);
                            } else {
                                instances.put(pattern, instances.get(pattern) + 1);
                            }
                        }
                    }

                }

            }

            /*Iterator<String> it = instances.keySet().iterator();
             while (it.hasNext()) {
             pattern = it.next();
             out.write(pattern + "\t" + instances.get(pattern));
             out.newLine();
             }*/

            for (Map.Entry<String, Integer> entry : entriesSortedByValues(instances)) {
                line = entry.getKey() + "\t" + entry.getValue();
                // System.out.println(line);
                out.write(line);
                out.newLine();
            }

            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getPatterns(String meta_pattern) {

        String line, tokens[];
        String cwRegex = ".+";

        TreeMap<String, Integer> localPatterns = new TreeMap();
        TreeMap<String, TreeMap<String, Integer>> localWords = new TreeMap();

        int index;

        //Determine the positions of the CWs
        tokens = meta_pattern.split("_");
        TreeMap<Integer, Integer> pos = new TreeMap();
        int count = 0;
        for (String token : tokens) {
            if (token.equals("CW")) {
                pos.put(count, count);
            }
            count++;
        }

        String separator = " ";
        try {
            //ATTENTION
           
            //Scanner scan = new Scanner(new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\input\\instances2\\instances_" + meta_pattern), "UTF-8");

            //Scanner scan = new Scanner(new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\spanish\\spanish2\\input\\instances2\\test\\instances_" + meta_pattern), "UTF-8");

             //String file = "C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\input\\instances_pmi\\2\\instances_" + meta_pattern;
            String file = "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\OUTPUT\\english\\instances_" + meta_pattern;
            
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            
            
            //FileOutputStream fstream = new FileOutputStream("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\input\\meta_patterns_pmi\\2\\meta_" + meta_pattern);
            FileOutputStream fstream = new FileOutputStream("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\OUTPUT\\english\\final_output\\meta_" + meta_pattern);
         
           
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF-8"));

            while ((line = br.readLine()) != null) {

                String pattern = "";
                String pw_inst = "";
               
                tokens = line.toLowerCase().split("\t");
                int intsCount = Integer.parseInt(tokens[1]);
                tokens = regexReservedWords(tokens[0]).split("\\s");
                Iterator<Integer> it = pos.values().iterator();

                // temp to put all the instances instantiated by the specific pattern.
                TreeMap<String, Integer> temp = new TreeMap();
                // temp used to put all the patterns than instantiate a specific instance
                TreeMap<String, String> temp2 = new TreeMap();
                while (it.hasNext()) {
                    index = it.next();
                    pw_inst += tokens[index] + " ";

                    tokens[index] = cwRegex;
                }
                pw_inst = pw_inst.trim();

                temp.put(pw_inst, intsCount);
                for (int i = 0; i < tokens.length; i++) {

                    /*   if (tokens[i].matches("\\p{P}+")) {
                     separator = "";
                     } else {
                     separator = " ";
                     }*/
                    pattern += separator + tokens[i];
                }
                pattern = pattern.trim();

                // Add pattern to the instances list
                if (!instancesCount.containsKey(pw_inst)) {

                    instancesCount.put(pw_inst, intsCount);
                } else {
                    instancesCount.put(pw_inst, instancesCount.get(pw_inst) + intsCount);
                }

                // add patterns to the list
                if (!localPatterns.containsKey(pattern)) {
                    localPatterns.put(pattern, intsCount);

                } else {
                    localPatterns.put(pattern, localPatterns.get(pattern) + intsCount);
                    temp = localWords.get(pattern);

                    temp.put(pw_inst, intsCount);

                }
                localWords.put(pattern, temp);
            }

            for (Map.Entry<String, Integer> entry : entriesSortedByValues(localPatterns)) {
                TreeMap<String, Integer> temp = localWords.get(entry.getKey());
                line = entry.getKey() + "\t" + temp.size() + "\t";

                line += entry.getValue() + "\t";
                for (Map.Entry<String, Integer> entry2 : entriesSortedByValues(temp)) {
                    line += entry2.getKey() + " " + entry2.getValue() + "\t";
                }
                // System.out.println(line);
                out.write(line);
                out.newLine();
            }

            patternsAndInts.putAll(localWords);
            patterns.putAll(localPatterns);

            out.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void computePMI() {
        System.out.println("Computing PMIs ...");
        System.out.println("Displaying first 10 pmis ...");
        int c = 0;
        Iterator<String> itP = patternsAndInts.keySet().iterator();

        while (itP.hasNext()) {
            String pattern = itP.next();
            TreeMap<String, Integer> insts = patternsAndInts.get(pattern);
            TreeMap<String, Double> temp = new TreeMap();
            Iterator<String> itI = insts.keySet().iterator();
            while (itI.hasNext()) {
                String inst = itI.next();
                // frequency of pattern and instance together
                double val1 = (double) insts.get(inst);
                // frequency of the instance with any pattern
                double val2 = (double) instancesCount.get(inst);
                // frequency of the pattern with any instance
                double val3 = (double) patterns.get(pattern);
                // calculate the discounting factor
                double disc1 = val1 / (val1 + 1);
                double disc2 = Math.min(val2, val3) / (Math.min(val2, val3) + 1);
                double disc = disc1 * disc2;
                // calculate the pmi between the insance and the pattern.
                if (val2 == 0.0 || val3 == 0.0) {
                    System.out.println("WARNING: Division by 0");
                }
                //double pmi = Math.log(val1 / (val2 * val3))*disc;
                 double pmi = Math.log(val1 / (val2 * val3));
                if (c <= 10) {
                    System.out.printf("PMI(%s,%s)=%f\n", pattern, inst, pmi);
                    c++;
                }
                // get the max pmi
                if (pmi > maxPMI && pmi != 0) {
                    System.out.printf("New max %f\n", pmi);
                    maxPMI = pmi;
                }
                // Store the pmi with that instance in the temporal list
                temp.put(inst, pmi);
            }
            pmis.put(pattern, temp);
        }

        System.out.println("Max PMI " + maxPMI);

    }

    public static void computeReliability() {
        System.out.println("Computing Reliability ...");
        FileOutputStream fstream = null;
        try {
            //fstream = new FileOutputStream("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + session + "\\output\\pmi\\patterns_pmi_discounted2");
            fstream = new FileOutputStream("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\OUTPUT\\english\\kevin_instance\\patterns_pmi_kevin_test");
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF-8"));
            Iterator<String> itP = pmis.keySet().iterator();
            double totInsts = (double) instancesCount.size();
            System.out.println("|I| = " + totInsts);
            while (itP.hasNext()) {
                String pattern = itP.next();
                double sum = 0.0;
                TreeMap<String, Double> temp = pmis.get(pattern);
                Iterator<Double> itPMI = temp.values().iterator();
                while (itPMI.hasNext()) {
                  
                        sum += itPMI.next() / maxPMI;
                   
                }
                double rel = sum / totInsts;
                patternsReliability.put(pattern, rel);


            }

            String line;
            for (Map.Entry<String, Double> entry : entriesSortedByValues(patternsReliability)) {

                line = entry.getKey() + "\t" + entry.getValue();

                // System.out.println(line);
                out.write(line);
                out.newLine();
            }

            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(GetInstancesAll_pmi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void load(TreeMap<String, Double> map, String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(
                path.toString()));

        String tokens[], line;
        while ((line = reader.readLine()) != null) {

            tokens = line.split("\t");

            map.put(tokens[1].trim(), Double.parseDouble(tokens[0]));


        }
        reader.close();
    }

    public static String regexReservedWords(String token) {
        String newToken = "";
        for (int i = 0; i < token.length(); i++) {
            if (Character.toString(token.charAt(i)).matches(
                    "(\\^|\\$|\\*|\\(|\\)|\\+|\\[|\\]|\\{|\\}|\\||\\.|\\?|\\\\)")) {
                newToken += "\\" + token.charAt(i);
            } else {
                newToken += token.charAt(i);
            }

        }
        return newToken;
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

    public static String processWord(String word) {

        word = word.toLowerCase();

        // If HT
        if (word.charAt(0) == '#') {
            word = "<hashtag>";
        }

        //If URL
        if (word.contains("http") || word.contains("https") || word.contains("://")) {
            word = "<url>";
        }

        //If user mention
        if (word.charAt(0) == '@') {
            word = "<usermention>";
        }
        
         //If mini URL
        if (word.length() > 3 && word.substring(0, 3).equals("co/")) {
            word = "<minurl>";
        }
        return word;
    }
    
    
}
