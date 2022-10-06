public class Trainer {
    private Trainable trainee;
    private double[] mutationStrength;
    private double[] mutationProbability;
    private double[] selectionFactor;
    private double[] genetricDriftFactor;
    private int populationSize;
    private int inputSize;
    private int outputSize;
    private int middleLayers;
    private int runTime; //sec
    private int tuningFactor; //percentage of total time

    public Trainer(Trainable trainee, int populationSize, int inputSize, int outputSize, int middleLayers,
            int runTime, int tuningFactor) {
        this(new double[]{.05,0.2}, new double[]{0.05, 0.15}, new double[]{0.05, 0.25}, new double[]{0.05, 0.4}, populationSize, inputSize, outputSize, middleLayers,
     runTime, tuningFactor);
    }

    public Trainer(double[] mutationStrength, double[] mutationProbability, double[] selectionFactor,
            double[] genetricDriftFactor, int populationSize, int inputSize, int outputSize, int middleLayers,
            int runTime, int tuningFactor) {
        this.mutationStrength = mutationStrength;
        this.mutationProbability = mutationProbability;
        this.selectionFactor = selectionFactor;
        this.genetricDriftFactor = genetricDriftFactor;
        this.populationSize = populationSize;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.middleLayers = middleLayers;
        this.runTime = runTime;
        this.tuningFactor = tuningFactor;
    }

    private void 

    
}