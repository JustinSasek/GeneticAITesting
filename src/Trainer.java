import java.util.Arrays;
import java.util.Scanner;

public class Trainer {
    private Trainable trainee;

    private double[] mutationStrength;

    private double[] mutationProbability;

    private double[] selectionFactor;

    private double[] genetricDriftFactor;

    private static final int populationSize = 500;
    private int inputSize;
    private int outputSize;
    private int layerCount;
    private final static double tuningFactor = 0.05; // percentage of total time
    private final static double tuningDuration = 30; // percentage of total time // sec
    private EvolutionPath mainEvolutionPath;

    private Scanner input;
    private boolean running;

    public Trainer(Trainable trainee, int layerCount) {
        this.mutationStrength = new double[] { .05, 0.2 };
        this.mutationProbability = new double[] { 0.05, 0.15 };
        this.selectionFactor = new double[] { 0.05, 0.25 };
        this.genetricDriftFactor = new double[] { 0.05, 0.4 };
        this.outputSize = trainee.getOutputSize();
        this.layerCount = layerCount;
        input = new Scanner(System.in);

        mainEvolutionPath = new EvolutionPath(populationSize, inputSize, outputSize, layerCount,
                trainee);
    }

    private void tune() {
        mainEvolutionPath.setMutationStrength((mutationStrength[1] - mutationStrength[0]) / 2);
        mainEvolutionPath.setMutationProbability((mutationProbability[1] - mutationProbability[0]) / 2);
        mainEvolutionPath.setSelectionFactor((selectionFactor[1] - selectionFactor[0]) / 2);
        mainEvolutionPath.setGenetricDriftFactor((genetricDriftFactor[1] - genetricDriftFactor[0]) / 2);

        long pathRuntime = (long) (tuningDuration / 4 / 20) * 1000000000;
        EvolutionPath bestPath = mainEvolutionPath.clone();

        double maxFitness = Double.MIN_VALUE;
        for (double ms = mutationStrength[0]; running && ms <= mutationStrength[1]; ms += (mutationStrength[1]
                - mutationStrength[0]) / 20) {
            EvolutionPath tempPath = bestPath.clone();
            tempPath.setMutationStrength(ms);

            long startTime = System.nanoTime();
            while (running() && System.nanoTime() - startTime <= pathRuntime) {
                tempPath.runGeneration();
            }
            double relMaxfitness = tempPath.getMaxFitness();
            if (relMaxfitness > maxFitness) {
                maxFitness = relMaxfitness;
                bestPath = tempPath;
            }
        }

        maxFitness = Double.MIN_VALUE;
        for (double mp = mutationProbability[0]; running && mp <= mutationProbability[1]; mp += (mutationProbability[1]
                - mutationProbability[0]) / 20) {
            EvolutionPath tempPath = bestPath.clone();
            tempPath.setMutationProbability(mp);

            long startTime = System.nanoTime();
            while (running() && System.nanoTime() - startTime <= pathRuntime) {
                tempPath.runGeneration();
            }
            double relMaxfitness = tempPath.getMaxFitness();
            if (relMaxfitness > maxFitness) {
                maxFitness = relMaxfitness;
                bestPath = tempPath;
            }
        }

        maxFitness = Double.MIN_VALUE;
        for (double sf = selectionFactor[0]; running && sf <= selectionFactor[1]; sf += (selectionFactor[1]
                - selectionFactor[0]) / 20) {
            EvolutionPath tempPath = bestPath.clone();
            tempPath.setSelectionFactor(sf);

            long startTime = System.nanoTime();
            while (running() && System.nanoTime() - startTime <= pathRuntime) {
                tempPath.runGeneration();
            }
            double relMaxfitness = tempPath.getMaxFitness();
            if (relMaxfitness > maxFitness) {
                maxFitness = relMaxfitness;
                bestPath = tempPath;
            }
        }

        maxFitness = Double.MIN_VALUE;
        for (double gd = genetricDriftFactor[0]; running && gd <= genetricDriftFactor[1]; gd += (genetricDriftFactor[1]
                - genetricDriftFactor[0]) / 20) {
            EvolutionPath tempPath = bestPath.clone();
            tempPath.setGenetricDriftFactor(gd);

            long startTime = System.nanoTime();
            while (running() && System.nanoTime() - startTime <= pathRuntime) {
                tempPath.runGeneration();
            }
            double relMaxfitness = tempPath.getMaxFitness();
            if (relMaxfitness > maxFitness) {
                maxFitness = relMaxfitness;
                bestPath = tempPath;
            }
        }

        mainEvolutionPath = bestPath;
    }

    private void train() {
        long trainingDuration = (long) (tuningDuration / tuningFactor * (1 - tuningFactor)) * 1000000000;
        long startTime = System.nanoTime();
        while (running() && System.nanoTime() - startTime <= trainingDuration) {
            mainEvolutionPath.runGeneration();
        }
    }

    public void run() {
        running = true;
        while (running) {
            tune();
            train();
        }
    }

    private boolean running() {
        if (!running) {
            return false;
        }
        if (input.hasNext()) {
            running = false;
            return false;
        }
        return true;
    }

    public NeuralNetwork getBestNetwork() {
        return mainEvolutionPath.getBestNetwork();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((trainee == null) ? 0 : trainee.hashCode());
        result = prime * result + Arrays.hashCode(mutationStrength);
        result = prime * result + Arrays.hashCode(mutationProbability);
        result = prime * result + Arrays.hashCode(selectionFactor);
        result = prime * result + Arrays.hashCode(genetricDriftFactor);
        result = prime * result + inputSize;
        result = prime * result + outputSize;
        result = prime * result + layerCount;
        result = prime * result + ((mainEvolutionPath == null) ? 0 : mainEvolutionPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Trainer other = (Trainer) obj;
        if (trainee == null) {
            if (other.trainee != null)
                return false;
        } else if (!trainee.equals(other.trainee))
            return false;
        if (!Arrays.equals(mutationStrength, other.mutationStrength))
            return false;
        if (!Arrays.equals(mutationProbability, other.mutationProbability))
            return false;
        if (!Arrays.equals(selectionFactor, other.selectionFactor))
            return false;
        if (!Arrays.equals(genetricDriftFactor, other.genetricDriftFactor))
            return false;
        if (inputSize != other.inputSize)
            return false;
        if (outputSize != other.outputSize)
            return false;
        if (layerCount != other.layerCount)
            return false;
        if (mainEvolutionPath == null) {
            if (other.mainEvolutionPath != null)
                return false;
        } else if (!mainEvolutionPath.equals(other.mainEvolutionPath))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Trainer [trainee=" + trainee + ", mutationStrength=" + Arrays.toString(mutationStrength)
                + ", mutationProbability=" + Arrays.toString(mutationProbability) + ", selectionFactor="
                + Arrays.toString(selectionFactor) + ", genetricDriftFactor=" + Arrays.toString(genetricDriftFactor)
                + ", inputSize=" + inputSize + ", outputSize=" + outputSize + ", layerCount=" + layerCount + "]";
    }

}