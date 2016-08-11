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
import static filter3.TFICF.icf;
import static filter3.TFICF.tf;
import static filter3.TFICF.*;
import static filter3.TFICF.print_weighted;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import simpleclassifier.PFICFRankClassifier.*;
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
    
    //training
    public final double bestAcc=0.0;
    
    private static FileOutputStream fstream;
    private static BufferedWriter out;
    
    public WeightingMechanism( double a_targetAmount )
    {
        if( a_targetAmount < 0 || a_targetAmount > 99 )
        {
            throw new IllegalArgumentException(
                "Change amount must be between 0 to 99 percent." );
        }
        m_targetAmount = a_targetAmount;
    }
    
    @Override
    public double evaluate ( IChromosome a_subject ) 
    {
        // CHROMOSOME of weights of TF, IDF, DIV //
        double[] answer = {0,0,0};
        double maxTF=1.0;
        double maxIDF=1.99;
        double maxDIV=1.0;
        
        double dontstuck=0.0; // just to force evaluate is running, think of poissible errors to fix those stuff
        
        // identifier of the changedAmount
        double changeAmountTF = weightChangeTF(a_subject);
        double changeAmountIDF = weightChangeIDF(a_subject);
        double changeAmountDIV = weightChangeDIV(a_subject);
        
        // todo : how are you going  to use this?
        double weightTF = getTotalNumberOfWeight(a_subject);
        double weightIDF = getTotalNumberOfWeight(a_subject);
        double weightDIV = getTotalNumberOfWeight(a_subject);
        
  
        
        // todo : here is the different ( to eliminate all the bad results, to achieve something new )
        double changeDifferenceTF = Math.abs(m_targetAmount - changeAmountTF);
        double changeDifferenceIDF = Math.abs(m_targetAmount - changeAmountIDF);
        double changeDifferenceDIV = Math.abs(m_targetAmount - changeAmountDIV);
        
        
        // todo : modify a new idea here, it determines the value of the weight set 
        double fitnessTF = ( maxTF - changeDifferenceTF );
        double fitnessIDF = ( maxIDF - changeDifferenceIDF );
        double fitnessDIV = ( maxDIV - changeDifferenceDIV );
        
        answer[0]=fitnessTF;
        answer[1]=fitnessIDF;
        answer[2]=fitnessDIV;
        
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
        
        
        try {
            tficf_weighted = tficf_weighted(tf,icf,score3,typeCode,answer[0],answer[1],answer[2]);
            print_weighted(tficf_weighted,DestinationFile);
        } catch (IOException ex) {
            Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File file = new File(ClassifierFile);
        try {
            fstream = new FileOutputStream(file + "_sentiment");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WeightingMechanism.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        readTweets(ClassifierFile, 1);
        printStats();
        
        
        return dontstuck;
    }

    public static double weightChangeTF( IChromosome a_potentialSolution )
    {
        double weightTF = getWeightsAtGene( a_potentialSolution, 0 );
        return weightTF;
    }
    

    public static double weightChangeIDF( IChromosome a_potentialSolution )
    {
        double tresholdMIN = 1.14;
        double weightIDF = getWeightsAtGene( a_potentialSolution, 1 );
        
        return weightIDF;
    }
    
    public static double weightChangeDIV( IChromosome a_potentialSolution )
    {
        double weightDIV = getWeightsAtGene( a_potentialSolution, 2 );
       
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
        double targetAmount = 90; // accuracy hope is 90
        
        FitnessFunction myFunc = new WeightingMechanism( targetAmount );
        conf.setFitnessFunction( myFunc );

        Gene[] weightGenes = new Gene[3];

        weightGenes[0] = new DoubleGene(conf, 0, 3 );  // TF
        weightGenes[1] = new DoubleGene(conf, 0, 2 );  // IDF
        weightGenes[2] = new DoubleGene(conf, 0, 1 );  // Div
        
        Chromosome baseChromosome = new Chromosome(conf, weightGenes );
        conf.setSampleChromosome( baseChromosome );

        // Finally, we need to tell the Configuration object how many
        // Chromosomes we want in our population. The more Chromosomes,
        // the larger the number of potential solutions (which is good
        // for finding the answer), but the longer it will take to evolve
        // the population each round. We'll set the population size to
        // 500 here.
        // --------------------------------------------------------------
        conf.setPopulationSize( 10 );
        
        
        Genotype population = Genotype.randomInitialGenotype(conf);
        population.evolve();
        
        IChromosome bestSolution = population.getFittestChromosome();

        for( int i = 0; i < 1 ; i++ )
        {
            System.out.println("Attempting to evolve of evolution gene number "+i);
            population.evolve();
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

            System.out.println("Classified: " + count);
            System.out.println("Correct: " + right);
            System.out.println("Accuracy: " + ((double) right / (double) count));

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
            //  File folder = new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\french\\french1\\output\\tficf\\patterns\\unic\\25");
            // File folder = new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\spanish\\spanish2\\output\\tficf\\patterns\\unic\\9");
            

            File folder = new File("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Genetic Algo\\6 emotions tficf weighted");
            // folder = new File("C:\\Users\\Kevin\\Documents\\Codes IDEA\\REFERENCE ORIGINAL CODE\\patterns\\6emos");
            //File folder = new File("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Genetic Algo\\6 emotions tficf ORIGINAL CARLOS");
            
            // File folder = new File("C:\\Users\\Carlos\\Dropbox\\workspace\\filter2\\hadoop\\SentiLingua\\english\\english1\\output\\tficf\\patterns\\unic_many\\mix\\5\\");
            // File folder = new File("C:\\Users\\Carlos\\Dropbox\\workspace\\weka experiments\\datasets\\training\\english\\6 classes\\patterns\\final\\87\\");
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
                            //System.out.println("Score for "+ fileEntry.getName().split("_")[1]+" "+finalScore);
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

    public static void printStats() {
        Iterator<ClassStats> itEmo = stats.values().iterator();
        System.out.println("\n\n");
        while (itEmo.hasNext()) {
            ClassStats cs = itEmo.next();
            System.out.println(cs.getName() + "\t" + (double) cs.getCorrect() / (double) cs.getCount());
            
        }
    }

}
