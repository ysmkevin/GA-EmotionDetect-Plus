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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.*;

/**
 *
 * @author Carlos
 */
public class TFICF {

	private TFICF() {}

    public static void main(String[] args) throws FileNotFoundException, IOException {

        //String lang = "english";
        //String session = "english1";
       // String lang = "spanish";
       // String session = "spanish2";
        String type = "patterns";
		/*
			typeCode = 1 --> a * b * c 
			typeCode = 2 --> a * b
			typeCode = 3 --> b * c
			typeCode = 4 --> a^x * b*y *c^z
			typeCode = 5 --> a + b  + c
			else typeCode --> a + b
		*/
        int typeCode = 5;
		
//        if (type.equals("patterns")) {
//            typeCode = 1;
//        } else {
//            typeCode = 2;
//        }
        
		TreeMap<String, String> emoHashtag;
		System.out.println("Record Emotion and Its Hashtag...");
		emoHashtag = emoHashtag("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Laina\\[Datasets]\\Training Set\\category4_1");
		//System.out.println(TFICF.getKeysFromValue(emoHashtag, "joy"));

		TreeMap<String, Double> twnum;
		System.out.println("Running Tweet Number...");
		twnum = twnum("E:\\parallel_simple_emo8\\data\\tweetsnumber");

        TreeMap<String, TreeMap<String, Double>> tf;
        System.out.println("Running TF ...");
        tf = tf("C:\\Users\\Kevin\\Documents\\Codes IDEA\\KEVIN AFTER AMERICA\\Laina\\[Datasets]\\Training Set\\emo8",true);
       
        TreeMap<String, Double> icf;
        System.out.println("Running ICF");
        icf = icf(tf);

        TreeMap<String, Double> score3 = new TreeMap();
        System.out.println("Running SC3");
        //ATTENTION HERE
        score3 = score3("E:\\parallel_simple_emo8\\data\\meta_emo8", 1);
        TreeMap<String, TreeMap<String, Double>> tficf;
        System.out.println("Getting final score ...");
		String arg = args[0];
        tficf = tficf(tf, icf, score3, twnum, emoHashtag, typeCode, arg);
        print(tficf, "E:\\parallel_simple_emo8\\data\\tficf_x" + arg + "\\tficf_");      
    }
	
	// Added by Laina Farsiah
	public static TreeMap<String, String> emoHashtag(String path) throws IOException {
		TreeMap<String, String> emoHashtag = new TreeMap();
		
		String emo, hashtag;
		
		File folder = new File(path);
		Scanner scan;
		for (final File fileEntry : folder.listFiles()) {
			emo = fileEntry.getName();
			if (!fileEntry.isDirectory()) {
				try {
					scan = new Scanner(new File(fileEntry.getAbsolutePath()));
					//System.out.println(fileEntry.getAbsolutePath());
					while (scan.hasNext()) {
						hashtag = scan.nextLine();
						emoHashtag.put(hashtag.trim(), emo); 
					}
				} catch (FileNotFoundException ex) {
                    Logger.getLogger(TFICF.class.getName()).log(Level.SEVERE, null, ex);
                }
			}
		}
		return emoHashtag;
	}
	
	// Added by Laina Farsiah
	public static TreeMap<String, Double> twnum(String path) throws IOException{
        TreeMap<String, Double> twnum = new TreeMap();
        BufferedReader reader = new BufferedReader(new FileReader(path.toString()));

        String tokens[], line;
        while ((line = reader.readLine()) != null) {
            tokens = line.split("\t");
            //map.put(tokens[1].trim(), Double.parseDouble(tokens[0]));
			twnum.put(tokens[0].trim(), Double.parseDouble(tokens[1]));
            //System.out.println(tokens[0] + "\t" + tokens[1] );
        }
        return twnum;
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
                    System.out.println(fileEntry.getAbsolutePath());

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
       // Scanner scan;
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
                    System.out.println("Checking file "+fileEntry.getAbsolutePath());
                    //while (scan.hasNextLine()) {
                    while ((line = br.readLine()) != null) {
                       // line = scan.nextLine();
                      //  System.out.println(line);
                        tokens = line.split("\t");
                        patt = tokens[0].trim();
                        //System.out.println("Checking patt "+patt);
                        if (Integer.parseInt(tokens[1]) >= minFreq) {
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

    public static TreeMap<String, TreeMap<String, Double>> tficf(TreeMap<String, TreeMap<String, Double>> tf, TreeMap<String, Double> icf, TreeMap<String, Double> score3, TreeMap<String, Double> twnum, TreeMap<String, String> emoHashtag, int type, String arg) {
        TreeMap<String, TreeMap<String, Double>> tfidf = new TreeMap(tf);

        Iterator<String> it = tfidf.keySet().iterator();
        
        int c = 0;
        TreeMap<String,String> notFound = new TreeMap();
        while (it.hasNext()) {
            String emotion = it.next();
			double x = 1,y = 1,z = 1;
 			if(emotion.equals("fear") || emotion.equals("surprise")) {
				x = Double.parseDouble(arg); y = 1; z = 1;
				//System.out.println(emotion + " = " + emoHashtag.get(emotion));
			} 
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
                        //tfidfEmo.put(patt, (tfidfEmo.get(patt)/twnum.get(emotion)) * icf.get(patt) * score3.get(patt.trim()));
						tfidfEmo.put(patt, tfidfEmo.get(patt) * icf.get(patt) * score3.get(patt.trim()));
                    }
                } else if (type == 2) {
                    tfidfEmo.put(patt, tfidfEmo.get(patt) * icf.get(patt));
				} else if (type == 3) {
                    tfidfEmo.put(patt, icf.get(patt) * score3.get(patt.trim()));
				} else if (type == 4) {
                    if (score3.get(patt.trim()) == null) {
                        System.out.println("Cannot find " + patt);
                        notFound.put(patt, patt);
                    } else {
                        //tfidfEmo.put(patt, (tfidfEmo.get(patt)/twnum.get(emotion)) * icf.get(patt) * score3.get(patt.trim()));
						tfidfEmo.put(patt, Math.pow(tfidfEmo.get(patt), x) * Math.pow(icf.get(patt), y) * Math.pow(score3.get(patt.trim()), z));
                    }
                } else if (type == 5) {
                    if (score3.get(patt.trim()) == null) {
                        System.out.println("Cannot find " + patt);
                        notFound.put(patt, patt);
                    } else {
                        //tfidfEmo.put(patt, (tfidfEmo.get(patt)/twnum.get(emotion)) + icf.get(patt) + score3.get(patt.trim()));
						//if (tfidfEmo.get(patt) > 0) {
						tfidfEmo.put(patt, (x * tfidfEmo.get(patt)) + (y * icf.get(patt)) + (z * score3.get(patt.trim())));
						//} else {
						//	tfidfEmo.put(patt, tfidfEmo.get(patt));
						//}
                    }
				} else {
					tfidfEmo.put(patt, tfidfEmo.get(patt) + icf.get(patt));
				}
            }
        }
        System.out.println("Could not find patterns: "+notFound.size());
        return tfidf;
    }

    public static void print(TreeMap<String, TreeMap<String, Double>> tfidf, String path) {
        Iterator<String> it = tfidf.keySet().iterator();
        while (it.hasNext()) {
            FileOutputStream fstream = null;
            try {
                String emotion = it.next();
                TreeMap<String, Double> tfidfEmo = tfidf.get(emotion);
                System.out.println("Saving tfidc for emotion "+emotion);
                fstream = new FileOutputStream(path + emotion);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, "UTF8"));

                String line;
                for (Map.Entry<String, Double> entry : entriesSortedByValues(tfidfEmo)) {
                    line = entry.getKey() + "\t" + entry.getValue();
                    // System.out.println(line);
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
	
	//Added by Laina Farsiah
	public static List<Object> getKeysFromValue(Map<?, ?> hm, Object value) {
		List <Object> list = new ArrayList<Object>();
		for (Object o:hm.keySet()){
			if (hm.get(o).equals(value)) {
				list.add(o);
			}
		}
		return list;
	}
}
