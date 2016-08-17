package simpleGa;

public class Individual {

    static int defaultGeneLength = 64;
    private double[] genes = new double[3];
    private double tfGene;
    private double idfGene;
    private double divGene;
    // Cache
    private double fitness = 0;

    // Create a random individual
    public void generateIndividual() {
        for (int i = 0; i < size(); i++) {
            
            double tfGene = (double) Math.round(Math.random());
            double idfGene = (double) Math.round(Math.random());
            double divGene = (double) Math.round(Math.random());
            System.out.println("GENE NUMBER ===== "+i);
            
            genes[0]=tfGene;
            genes[1]=idfGene;
            genes[2]=divGene;
            
            System.out.println("tf "+genes[0]);
            System.out.println("idf "+genes[1]);
            System.out.println("div "+genes[2]);
        }
    }

    /* Getters and setters */
    // Use this if you want to create individuals with different gene lengths
    public static void setDefaultGeneLength(int length) {
        defaultGeneLength = length;
    }
    
    public double getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, double value) {
        genes[index] = value;
        fitness = 0;
    }

    /* Public methods */
    public int size() {
        return genes.length;
    }

    public double getFitness() {
        if (fitness == 0) {
            fitness = FitnessCalc.getFitness(this);
        }
        return fitness;
    }

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += getGene(i);
        }
        return geneString;
    }
}