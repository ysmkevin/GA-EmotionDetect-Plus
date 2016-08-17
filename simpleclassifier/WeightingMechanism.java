/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleclassifier;

/**
 *
 * @author Kevin
 */
import org.jgap.*;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
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
import java.util.Scanner;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static simpleclassifier.PFICFRankClassifier.printStats;
import static simpleclassifier.PFICFRankClassifier.readTweets;

public class WeightingMechanism extends FitnessFunction {
    //GA
    private final double m_targetAmount;
    
    //paths
    public final String pathTF="C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\DATASET\\english\\English_old\\htend\\6 classes\\patterns\\pmi\\pmi2";
    public final String pathDIV="C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Carlos\\english1\\input\\meta_patterns_pmi\\normal_run";
    public final String DestinationFile="C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Genetic Algo\\6 emotions tficf weighted\\tficf_";
    public final String ClassifierFile="C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Carlos\\annotated\\soccer\\carlos";
    public final String GATrainingDataFile="C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Genetic Algo\\6 emotions tficf GA\\tficf_";
    
    //classifier
    private static int count =0;
    private static TreeMap<String, Integer> mood = new TreeMap();
    private static TreeMap<String, ClassStats> stats = new TreeMap();
    private static int right = 0;
    private static int wrong = 0;
    private static int angerC = 0;
    private static int hopeC = 0;
    private static int sadnessC = 0;
    private static int fearC = 0;
    private static int supriseC = 0;
    private static int joyC = 0;
    
    //training
    public final double bestAcc=0.0;
    
    private static FileOutputStream fstream;
    private static BufferedWriter out;
    
    public WeightingMechanism( double a_targetAmount )
    {
        if( a_targetAmount < 0 || a_targetAmount > 2 )
        {
            throw new IllegalArgumentException(
                "Change amount must be between 0 to 2 precision." );
        }
        m_targetAmount = a_targetAmount;
    }
    
    @Override
    public double evaluate ( IChromosome a_subject ) 
    {
        
        double fitness=0.0; 
  
        String type = "patterns";
        int typeCode;

        if (type.equals("patterns")) {
            typeCode = 1;
        } else {
            typeCode = 2;
        }
        
        System.out.println("EVALUATE!");
        TreeMap<String, TreeMap<String, Double>> tf;
        tf = tf(pathTF,true);
        
        TreeMap<String, Double> icf;
        icf = icf(tf);

        TreeMap<String, Double> score3 = new TreeMap();
        score3=score3(pathDIV,1);
        
        TreeMap<String, TreeMap<String, Double>> tficf_weighted;
        
        double tfw = (double) a_subject.getGene(0).getAllele();
        double idfw = (double) a_subject.getGene(1).getAllele();
        double divw = (double) a_subject.getGene(2).getAllele();
        
        System.out.println("Current Chromosome TF "+tfw);
        System.out.println("Current Chromosome IDF "+idfw);
        System.out.println("Current Chromosome Div "+divw);
    
        tfw=1.0;
        idfw=1.0;
        divw=1.0;

        File outweight = new File("C:\\Users\\Kevin\\Documents\\Resultz\\weights");
        FileWriter fw;
        try {
            fw = new FileWriter(outweight,true);
            fw.append("\n"+tfw+" "+idfw+" "+divw+"\n");
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            tficf_weighted = tficf_weighted(tf,icf,score3,typeCode,tfw,idfw,divw);
            print_weighted(tficf_weighted,DestinationFile);
        } catch (IOException ex) {
            Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
        }

        int i=0;
        double accuracy=0.0;
        Double[] resultz = new Double[6];
        
        File file = new File(ClassifierFile);
        try {
            fstream = new FileOutputStream(file + "_sentiment");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));
            accuracy = readTweets(ClassifierFile, 1, accuracy);
            out.close();
            printStats(resultz);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Chromosome Accuracy : "+accuracy);
        
        for (i=0;i<resultz.length;i++)
        {
            System.out.println("Chromosome Precisions : "+resultz[i]);
        }
        
        
        return fitness;
    }

    public static double weightChangeTF( IChromosome a_potentialSolution )
    {
        double weightTF = getWeightsAtGene( a_potentialSolution, 0 );
        System.out.println("weighTF" +weightTF);
        return weightTF;
    }
    

    public static double weightChangeIDF( IChromosome a_potentialSolution )
    {
        double weightIDF = getWeightsAtGene( a_potentialSolution, 1 );
        System.out.println("weighIDF" +weightIDF);
        return weightIDF;
    }
    
    public static double weightChangeDIV( IChromosome a_potentialSolution )
    {
        double weightDIV = getWeightsAtGene( a_potentialSolution, 2 );
        System.out.println("weightDIV" +weightDIV);
        return weightDIV;
    }

     /**
     * Retrieves the number of coins represented by the given potential
     * solution at the given gene position.
     *
     * @param a_potentialSolution The potential solution to evaluate.
     * @param a_position The gene position to evaluate.
     * @return the number of coins represented by the potential solution
     *         at the given gene position.
     */
    public static double getWeightsAtGene (IChromosome a_potentialSolution, double a_position )
    {
        Double numWeight=(Double) a_potentialSolution.getGene((int) a_position).getAllele();
        return numWeight.doubleValue();
    }

    public static double getTotalNumberOfWeight( IChromosome a_potentialsolution )
    {
        double totalWeights=0;
        int numberOfGenes = a_potentialsolution.size();
        for( int i = 0; i < numberOfGenes; i++ )
        {
            totalWeights += getWeightsAtGene( a_potentialsolution, i );
        }
        return totalWeights;
    }

    public static void main(String[] args) throws Exception{
     
     
        Configuration conf = new DefaultConfiguration();
        //double targetAmount = Double.parseDouble(args[0]);
        double targetAmount = 2;
        
        FitnessFunction myFunc = new WeightingMechanism( targetAmount );
        conf.setFitnessFunction( myFunc );

        Gene[] weightGenes = new Gene[3];

        weightGenes[0] = new DoubleGene(conf, 1, 1 );  // TF
        weightGenes[1] = new DoubleGene(conf, 1.44, 1.99 );  // IDF
        weightGenes[2] = new DoubleGene(conf, 1, 1 );  // Div
       
        Chromosome baseChromosome = new Chromosome(conf, weightGenes );
        conf.setSampleChromosome( baseChromosome );

        conf.setPopulationSize( 1 );

        Genotype population = Genotype.randomInitialGenotype(conf);
        //population.evolve();
        
        IChromosome bestSolution = population.getFittestChromosome();
        System.out.println("bestSol "+bestSolution);
        
        for( int i = 0; i < 1 ; i++ )
        {
            System.out.println("Attempting to evolve of evolution gene number "+i);
            //population.evolve();
        }

        System.out.println( "The best solution contained the following: " );
        System.out.println(WeightingMechanism.getWeightsAtGene(bestSolution, 0 ) + " TF." );
        System.out.println(WeightingMechanism.getWeightsAtGene(bestSolution, 1 ) + " IDF." );
        System.out.println(WeightingMechanism.getWeightsAtGene(bestSolution, 2 ) + " Div." );


        System.out.println( "For a total of " +
            WeightingMechanism.weightChangeTF(bestSolution) + " TF, " +
            WeightingMechanism.weightChangeIDF(bestSolution) + " IDF, and "+ 
            WeightingMechanism.weightChangeDIV(bestSolution) + " DIV");
            }

    
    public static double readTweets(String path, int mode, double accuracy) {
        try {

            //Scanner scan = new Scanner(new File(path), "UTF-8");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
            String line, emo1 = "", emo2 = "", tokens[];
            
            File out = new File("C:\\Users\\Kevin\\Documents\\Resultz\\weights");
            FileWriter fw = new FileWriter(out,true);

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

            System.out.println("Classified: " + count);
            System.out.println("Correct: " + right);
            accuracy = (double)right / (double) count;
            System.out.println(accuracy);
            System.out.println("Accuracy from readTweets acc var: " + accuracy);
            fw.append("\nAccuracy: "+accuracy+"\n");
            fw.close();
            
            //reset back the counter for next classification
            right=0;
            count=0;
            wrong=0;
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PFICFRankClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return accuracy;
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
            Iterator<Map.Entry> it = map.iterator();
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
                        right++;
                        correct = true;
                        cs1.incCorrect();
                    } else {
                        cs1.incWrong();
                    }

                    if (emo2.equals(guess1.getKey()) || emo2.equals(guess2.getKey())) {
                        if (!correct) {
                            right++;
                            correct = true;
                        }
                        cs2.incCorrect();
                    } else {
                        cs2.incWrong();
                    }

                    if (!correct) {
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

    public static Double[] printStats(Double[] resultz) throws FileNotFoundException, IOException {
        Iterator<ClassStats> itEmo = stats.values().iterator();
        System.out.println("\n\n");
        File out = new File("C:\\Users\\Kevin\\Documents\\Resultz\\weights");
        FileWriter fw = new FileWriter(out,true);
        int iteration=0;
        while (itEmo.hasNext()) {
            ClassStats cs = itEmo.next();
            resultz[iteration]=(double)cs.getCorrect() / (double) cs.getCount();
            System.out.println(cs.getName() + "\t" + (double) cs.getCorrect() / (double) cs.getCount());
            fw.append("\n"+cs.getName() + "\t\n" + (double) cs.getCorrect() / (double) cs.getCount());
            iteration++;
        }
        fw.close();
        return resultz;
    }
    
    public static void saveStats() {
        Iterator<ClassStats> itEmo = stats.values().iterator();
        System.out.println("\n\n");
        while (itEmo.hasNext()) {
            ClassStats cs = itEmo.next();
            System.out.println(cs.getName() + "\t" + (double) cs.getCorrect() / (double) cs.getCount());
            
        }
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
                        //System.out.println(Original+"origin and total of "+Total);
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
                for (Map.Entry<String, Double> entry : entriesSortedByValuesDesc(tfidfEmo)) {
                    line = entry.getKey() + "\t" + entry.getValue();
                    System.out.println(line);
                    out.write(line);
                    out.newLine();
                }
                out.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fstream.close();
                } catch (IOException ex) {
                    Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
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
                    Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
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
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(fileEntry.getAbsolutePath()), "UTF8"));
                    while ((line = br.readLine()) != null) {
                        tokens = line.split("\t");
                        patt = tokens[0].trim();
                        if (Integer.parseInt(tokens[1]) >= minFreq) {
                            ratio = Math.log10(Double.parseDouble(tokens[scIndex]));
                            patts.put(patt, ratio);
                        }
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        System.out.println("Number of patterns: " + patts.size());
        return patts;
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
    
}
