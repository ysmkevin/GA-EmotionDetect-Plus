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
import java.io.Writer;
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
import simpleclassifier.RegularExpressions;
import org.jgap.*;
import org.jgap.impl.*;

public class TFICF {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidConfigurationException {

        String lang = "english";
        String session = "english1";
        String type = "patterns";
        int typeCode;

        if (type.equals("patterns")) {
            typeCode = 1;
        } else {
            typeCode = 2;
        }

        TreeMap<String, TreeMap<String, Double>> tf;
        System.out.println("Running TF ...");
        //tf = tf("C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\english\\English_old\\htend\\6 classes\\patterns\\pmi\\pmi2");
        //tf = tf("C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\english\\English_old\\htend\\6 classes\\"+type+"\\unic\\2\\5");
        //tf = tf("C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\english\\jammin\\patterns\\5_dyn_20150216");
        //tf = tf("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\DATASET\\english\\English_old\\htend\\6 classes\\patterns\\pmi\\pmi2",true);
        tf = tf("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Carlos\\patterns\\6emos",true);

        //tf = tf("C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\indonesian\\jammin\\patterns\\5");
        //tf = tf("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\output\\frequencies");
        //tf = tf("C:\\Users\\Carlos\\Dropbox\\workspace\\weka experiments\\datasets\\training\\english\\6 classes\\patterns\\87");
        //tf = tf("C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\spanish\\old\\emotions\\6 classes\\patterns\\unic\\160");
        //tf = tf("C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\french\\old\\emotions\\6 classes\\patterns\\unic\\2\\5");
        
        TreeMap<String, Double> icf;
        System.out.println("Running ICF");
        icf = icf(tf);

        TreeMap<String, Double> score3 = new TreeMap();
        System.out.println("Running SC3");
        
        //ATTENTION HERE
        score3 = score3("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Carlos\\english1\\input\\meta_patterns_pmi\\normal_run", 1);
        //score3 = score3("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Carlos\\english1\\input\\meta_patterns_pmi\\2", 1);
        //score3 = score3("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\" + lang + "\\" + session + "\\input\\meta_patterns_pmi\\2", 1);
        //score3 = score3("C:\\Users\\Carlos\\Dropbox\\workspace\\TweetsDatasets\\crawler_project\\indonesian\\jammin\\patterns\\meta",1);
        
        //TreeMap<String, TreeMap<String, Double>> tficf;
        TreeMap<String, TreeMap<String, Double>> tficf_weighted;
        
        System.out.println("Getting final score ...");
        
        ///
        /// TODO: Chromosom for Fitness values of w1 w2 w3 here ///
        ///
        double initChrome = 0.0;
        //initChrome=initChrome();
        
        
        double tfTres;
        double icfTres;
        double divTres;
        
        tfTres = randomWithRange(0.0,1.0);
        icfTres = randomWithRange(1.14,1.99);
        divTres = randomWithRange(0.0,1.0);
        
        double weight1=tfTres;
        double weight2=icfTres;
        double weight3=divTres;
        
        
        System.out.println(tfTres);
        System.out.println(icfTres);
        System.out.println(divTres);
        ///run normal
        //tficf = tficf(tf, icf, score3, typeCode);
        //print(tficf, "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Genetic Algo\\6 emotions tficf ORIGINAL CARLOS\\tficf_");
        
        System.out.println("Then run weighted\n");
        ///run weighted
        tficf_weighted = tficf_weighted(tf,icf,score3,typeCode,weight1,weight2,weight3);
        print_weighted(tficf_weighted, "C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Genetic Algo\\6 emotions tficf weighted\\tficf_");
        
        ///TODO: Function to compare weighted and print
        /// if Converge >0.05 or 100x, keep running
        /// save answer
        
        ///run for another test seed
        ///run 10 times
        
        ///select the best one
        ///the best one can use to do testing data
        ///extra for good visual : detailed info for converged and normal ones 
        
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

    public static double randomWithRange(double min, double max)
    {
        double range = (max - min);     
        return (double)(Math.random() * range) + min;
    }
    
    public static TreeMap<String, TreeMap<String, Double>> tf(String path, boolean replaceWC) {
        TreeMap<String, TreeMap<String, Double>> tf = new TreeMap();

        File folder = new File(path);
        Scanner scan;
        String line, tokens[], pattern;

        double max = 0.0;

        for (final File fileEntry : folder.listFiles()) {
            //If it is not a directory, hence is a file
            if (!fileEntry.isDirectory()) {
                try {
                    scan = new Scanner(new File(fileEntry.getAbsolutePath()));
                    //System.out.println(fileEntry.getAbsolutePath());

                    TreeMap<String, Double> patts = new TreeMap();

                    while (scan.hasNext()) {
                        line = scan.nextLine();
                        tokens = line.split("\t");
                        if (Double.parseDouble(tokens[1]) > max) {
                            max = Double.parseDouble(tokens[1]);
                        }
                        if(replaceWC){
                            pattern = tokens[0].replace("CW", ".+");
                        }else{
                            pattern = tokens[0];
                        }
                        patts.put(pattern, Math.log10(Double.parseDouble(tokens[1]) + 1));
                        // patts.put(tokens[1], Math.sqrt(Double.parseDouble(tokens[0])));
                    }
                    tf.put(fileEntry.getName().split("_")[1], patts);

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        
        System.out.println("Max TF " + max);

        return tf;
    }

    public static TreeMap<String, Double> icf(TreeMap<String, TreeMap<String, Double>> tf) {
        TreeMap<String, Double> icf = new TreeMap();

        Iterator<String> it = tf.keySet().iterator();
        while (it.hasNext()) {
            String emotion = it.next();
            TreeMap<String, Double> tfEmo = tf.get(emotion);
            Iterator<String> itTF = tfEmo.keySet().iterator();
            while (itTF.hasNext()) {
                String patt = itTF.next();
                double exists;
                if (tfEmo.get(patt) == 0) {
                    exists = 0.0;
                } else {
                    exists = 1.0;
                }

                if (icf.containsKey(patt)) {
                    icf.put(patt, icf.get(patt) + exists);
                } else {
                    icf.put(patt, exists);
                }
            }
        }

        Iterator<String> itICF = icf.keySet().iterator();
        while (itICF.hasNext()) {
            String patt = itICF.next();
            double val;
            if (icf.get(patt) != 0) {
                val = 6.0 / ((double) icf.get(patt));
            } else {
                val = 0.0;
            }
            //double val = Math.log10(1 + 6.0 / ((double) icf.get(patt) + 1.0));
            icf.put(patt, val);
        }

        return icf;
    }

    public static TreeMap<String, Double> score3(String path, int scIndex) {

        String line, tokens[], patt, pattern = "";
        BufferedReader br;
        int minFreq = 0;
        double ratio;

        // Get and merge the patterns
        File folder = new File(path);

        TreeMap<String, Double> patts = new TreeMap();

        for (final File fileEntry : folder.listFiles()) {
            //If it is not a directory, hence is a file
            if (!fileEntry.isDirectory()) {
                try {
                    //scan = new Scanner(new File(fileEntry.getAbsolutePath()), "UTF-8");
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(fileEntry.getAbsolutePath()), "UTF8"));
                    //System.out.println("Checking file "+fileEntry.getAbsolutePath());
                    //while (scan.hasNextLine()) {
                    while ((line = br.readLine()) != null) {
                       // line = scan.nextLine();
                        //System.out.println(line);
                        tokens = line.split("\t");
                        patt = tokens[0].trim();
                        //System.out.println("Checking patt "+patt);
                        if (Integer.parseInt(tokens[1]) >= minFreq) {
                            //System.out.println("suceed this parseintline");
                            ratio = Math.log10(Double.parseDouble(tokens[scIndex]));
                            // ratio = (Double.parseDouble(tokens[2]) / Double.parseDouble(tokens[1]));
                            patts.put(patt, ratio);
                        }
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        System.out.println("Number of patterns: " + patts.size());

        return patts;
    }

    public static TreeMap<String, TreeMap<String, Double>> tficf(TreeMap<String, TreeMap<String, Double>> tf, TreeMap<String, Double> icf, TreeMap<String, Double> score3, int type) throws FileNotFoundException, IOException {
        TreeMap<String, TreeMap<String, Double>> tfidf = new TreeMap(tf);
        Iterator<String> it = tfidf.keySet().iterator();
        int c = 0;
        
        File weight_res = new File("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Genetic Algo\\weighted experiment\\weight");
        FileOutputStream is = new FileOutputStream(weight_res);
        OutputStreamWriter write = new OutputStreamWriter(is);    
        Writer w = new BufferedWriter(write);
        //w.write("TF\tIDF\tDiv\tTotal\n");
        
        Double Total;
        TreeMap<String,String> notFound = new TreeMap();
        while (it.hasNext()) {
            String emotion = it.next();
            TreeMap<String, Double> tfidfEmo = tfidf.get(emotion);
            Iterator<String> itTF = tfidfEmo.keySet().iterator();
            while (itTF.hasNext()) {
                String patt = itTF.next();
                // if (score3.containsKey(patt.trim())) {
                if (type == 1) {
                    if (score3.get(patt.trim()) == null) {
                        System.out.println("Cannot find " + patt);
                        notFound.put(patt, patt);
                    } else {
                        
                        Total=Math.pow(tfidfEmo.get(patt),2)* Math.pow(icf.get(patt),3 )* Math.pow(score3.get(patt.trim()),0.5);
                        w.write(tfidfEmo.get(patt) +"\t"+ icf.get(patt)+"\t"+ score3.get(patt.trim())+"\t"+tfidfEmo.get(patt) * icf.get(patt) * score3.get(patt.trim())+"\n");
                        //w.write(Math.pow(tfidfEmo.get(patt),2)+"\t"+ Math.pow(icf.get(patt),3)+"\t"+ Math.pow(score3.get(patt.trim()),0.5)+"\t" +Total+"\n\n");
                        /*
                        System.out.println("+====================================================================================+");
                        System.out.println("ORIGINAL TF value : "+ tfidfEmo.get(patt) +" IDF : "+ icf.get(patt)+" div : "+ score3.get(patt.trim()));
                        System.out.println("Here for test : TF value : "+ Math.pow(tfidfEmo.get(patt),2)+" IDF : "+ Math.pow(icf.get(patt),3)+" div : "+ Math.pow(score3.get(patt.trim()),0.5));
                        System.out.println("total :"+tfidfEmo.get(patt) * icf.get(patt) * score3.get(patt.trim()));
                        System.out.println("+====================================================================================+");
                        */
                        tfidfEmo.put(patt, tfidfEmo.get(patt) * icf.get(patt) * score3.get(patt.trim()));
                    }
                } else {
                    tfidfEmo.put(patt, tfidfEmo.get(patt) * icf.get(patt));
                }
                /* } // tfidfEmo.put(patt, icf.get(patt));
                 else {
                 System.out.println(patt + " not found in score3");
                 }*/
            }
        }
        System.out.println("Could not find patterns: "+notFound.size()+" patterns.");
        return tfidf;
    }
    
    public static TreeMap<String, TreeMap<String, Double>> tficf_weighted (TreeMap<String, TreeMap<String, Double>> tf, TreeMap<String, Double> icf, TreeMap<String, Double> score3, int type, double  weight1, double weight2, double weight3) throws FileNotFoundException, IOException {
        TreeMap<String, TreeMap<String, Double>> tfidf_weight = new TreeMap(tf);
        Iterator<String> it = tfidf_weight.keySet().iterator();
        Double Total;
        Double Original;
        
        File weight_res = new File("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Genetic Algo\\weighted experiment\\weight");
        FileOutputStream is = new FileOutputStream(weight_res);
        OutputStreamWriter write = new OutputStreamWriter(is);    
        Writer w = new BufferedWriter(write);
        
        
        
        TreeMap<String,String> notFound = new TreeMap();
        while (it.hasNext()) {
            String emotion = it.next();
            TreeMap<String, Double> tfidfEmo = tfidf_weight.get(emotion);
            Iterator<String> itTF = tfidfEmo.keySet().iterator();
            while (itTF.hasNext()) {
                String patt = itTF.next();
                if (type == 1) {
                    if (score3.get(patt.trim()) == null) {
                        System.out.println("Cannot find " + patt);
                        notFound.put(patt, patt);
                    } else {
                        Original=(tfidfEmo.get(patt)*icf.get(patt)*score3.get(patt));
                        Total= (Math.pow(tfidfEmo.get(patt),weight1)) * (Math.pow(icf.get(patt),weight2)) * (Math.pow(score3.get(patt.trim()),weight3));
                        w.write(tfidfEmo.get(patt) +"\t"+ icf.get(patt)+"\t"+ score3.get(patt.trim())+"\t"+tfidfEmo.get(patt) * icf.get(patt) * score3.get(patt.trim())+"\n");
                        tfidfEmo.put(patt, Total);
                        //System.out.println("tficf_origin   "+Original);
                    }
                    // tfidfEmo.put(patt, tfidfEmo.get(patt) * icf.get(patt));
                } else {
                    tfidfEmo.put(patt, tfidfEmo.get(patt) * icf.get(patt));
                }
            }
        }
        System.out.println("Could not find patterns: "+notFound.size());
        return tfidf_weight;
    }
    
    public static void print(TreeMap<String, TreeMap<String, Double>> tfidf, String path) {
        Iterator<String> it = tfidf.keySet().iterator();
        while (it.hasNext()) {
            FileOutputStream fstream = null;
            try {
                String emotion = it.next();
                TreeMap<String, Double> tfidfEmo = tfidf.get(emotion);
                //System.out.println("Saving tfidc for emotion "+emotion);
                fstream = new FileOutputStream(path + emotion);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));

                String line;
                for (Map.Entry<String, Double> entry : entriesSortedByValues(tfidfEmo)) {
                    line = entry.getKey() + "\t" + entry.getValue();
                    //System.out.println(line);
                    out.write(line);
                    out.newLine();
                }
                out.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fstream.close();
                } catch (IOException ex) {
                    Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static void print_weighted(TreeMap<String, TreeMap<String, Double>> tfidf_weight, String path) {
        Iterator<String> it = tfidf_weight.keySet().iterator();
        while (it.hasNext()) {
            FileOutputStream fstream = null;
            try {
                String emotion = it.next();
                TreeMap<String, Double> tfidfEmo = tfidf_weight.get(emotion);
                //System.out.println("Saving tfidc for emotion "+emotion);
                fstream = new FileOutputStream(path + emotion);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));

                String line;
                for (Map.Entry<String, Double> entry : entriesSortedByValues(tfidfEmo)) {
                    line = entry.getKey() + "\t" + entry.getValue();
                    //System.out.println(line);
                    out.write(line);
                    out.newLine();
                }
                out.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fstream.close();
                } catch (IOException ex) {
                    Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
    
    
    public static double initChrome() throws InvalidConfigurationException
    {
        int numEvolutions=100;
        Configuration gaConf = new DefaultConfiguration();
        gaConf.getGeneticOperators().remove(0);
        gaConf.setPreservFittestIndividual(true);
        gaConf.setKeepPopulationSizeConstant(false);
        int chromeSize = 4;

        IChromosome weightChrom = new Chromosome(gaConf, new BooleanGene(gaConf), chromeSize);
        
        gaConf.setSampleChromosome(weightChrom);
        gaConf.setPopulationSize(20);
        //gaConf.setFitnessFunction(new Weightor());
        
          //
          // Completely initialize the population with custom code.
          // Notice that we assign the double number of Genes to
          // each other Chromosome.
          // ------------------------------------------------------
          
          int populationSize = gaConf.getPopulationSize();
          Population pop = new Population(gaConf, populationSize);

          for (int i = 0; i < populationSize; i++) {
            int mult;
            // Every second Chromosome has double the number of Genes.
            // -------------------------------------------------------
            if (i % 2 == 0) {
              mult = 1;
            }
            else {
              mult = 2;
            }
            
            Gene[] sampleGenes = weightChrom.getGenes();
            Gene[] newGenes = new Gene[sampleGenes.length * mult];
            RandomGenerator generator = gaConf.getRandomGenerator();

            for (int j = 0; j < newGenes.length; j = j + mult) {
              // We use the newGene() method on each of the genes in the
              // sample Chromosome to generate our new Gene instances for
              // the Chromosome we're returning. This guarantees that the
              // new Genes are setup with all of the correct internal state
              // for the respective gene position they're going to inhabit.
              // ----------------------------------------------------------
              newGenes[j] = sampleGenes[j / mult].newGene();
              
              
              // Set the gene's value (allele) to a random value.
              // ------------------------------------------------
              
              newGenes[j].setToRandomValue(generator);
              
              if (mult > 1) {
                System.out.println("Hoge");
                newGenes[j + 1] = sampleGenes[j / 2].newGene();
                // Set the gene's value (allele) to a random value.
                // ------------------------------------------------
                newGenes[j + 1].setToRandomValue(generator);

              }
            }
            IChromosome chrom = Chromosome.randomInitialChromosome(gaConf);
            chrom.setGenes(newGenes);
            pop.addChromosome(chrom);
          }

          // Now we need to construct the Genotype. This could otherwise be
          // accomplished more easily by writing
          // "Genotype genotype = Genotype.randomInitialGenotype(...)"
          Genotype genotype = new Genotype(gaConf, pop);
          int progress = 0;
          int percentEvolution = numEvolutions / 100;
          for (int i = 0; i < numEvolutions; i++) {
            genotype.evolve();
            System.out.println("Gene Evolved.");
            // Print progress.
            // ---------------
            if (percentEvolution > 0 && i % percentEvolution == 0) {
              progress++;
              IChromosome fittest = genotype.getFittestChromosome();
              double fitness = fittest.getFitnessValue();
              System.out.println("Fittest Chromosome has value " + fitness);
            }
          }
          IChromosome fittest = genotype.getFittestChromosome();
          System.out.println("Fittest Chromosome has value " +
                             fittest.getFitnessValue());
        
        
        return chromeSize;
    }
}

