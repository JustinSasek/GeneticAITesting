import javax.swing.KeyStroke;

public class Trainer {
    private Trainable trainee;

    private double[] mutationStrength;
    private double bestMutationStrength;

    private double[] mutationProbability;
    private double bestMutationProbability;

    private double[] selectionFactor;
    private double bestSelectionFactor;

    private double[] genetricDriftFactor;
    private double bestGenetricDriftFactor;

    private int populationSize = 500;
    private int inputSize;
    private int outputSize;
    private int layerCount;
    private int runTime; //sec
    private final static double tuningFactor = 0.05; //percentage of total time
    private EvolutionPath mainEvolutionPath;

    private boolean running = false;
    

    public Trainer(Trainable trainee, int outputSize, int layerCount) {
     this.mutationStrength = new double[]{.05,0.2};
        this.mutationProbability = new double[]{0.05, 0.15};
        this.selectionFactor = new double[]{0.05, 0.25};
        this.genetricDriftFactor = new double[]{0.05, 0.4};
        this.outputSize = outputSize;
        this.layerCount = layerCount;

        mainEvolutionPath = new EvolutionPath(bestMutationStrength, bestMutationProbability, 
                bestSelectionFactor, bestGenetricDriftFactor, populationSize, inputSize, outputSize, layerCount);
    }

    private void tune() {
        bestMutationStrength = (mutationStrength[1] - mutationStrength[0]) / 2;
        bestMutationProbability = (mutationProbability[1] - mutationProbability[0]) / 2;
        bestSelectionFactor = (selectionFactor[1] - selectionFactor[0]) / 2;
        bestGenetricDriftFactor = (genetricDriftFactor[1] - genetricDriftFactor[0]) / 2;

        for(double ms = mutationStrength[0]; ms <= mutationStrength[1]; ms += (mutationStrength[1] - mutationStrength[0]) / 20)
        mainEvolutionPath = new EvolutionPath(bestMutationStrength, bestMutationProbability, 
                bestSelectionFactor, bestGenetricDriftFactor, populationSize, inputSize, outputSize, layerCount);
    }

    public void start() {
        
    }

    private void checkIfEnded() {
        
        running = false;
    }

    public NeuralNetwork getBestNetwork() {
        return null;
    }
    
}