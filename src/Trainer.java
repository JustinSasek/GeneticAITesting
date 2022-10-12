import java.util.Arrays;

public class Trainer {
    private Trainable trainee;

    private double[] mutationStrength;

    private double[] mutationProbability;

    private double[] selectionFactor;

    private double[] genetricDriftFactor;

    private static final int populationSize = 500;
    private final static double trainDuration = 30; // sec
    private final static double tuningDuration = 3; // tuning time // sec
    private EvolutionPath mainEvolutionPath;

    private boolean running;

    public Trainer(Trainable trainee, int height) {
        this(trainee, height, -1);
    }

    public Trainer(Trainable trainee, int height, int width) {
        this.mutationStrength = new double[] { .05, 0.1 };
        this.mutationProbability = new double[] { 0.002, 0.1 };
        this.selectionFactor = new double[] { 0.01, 0.25 };
        this.genetricDriftFactor = new double[] { 0.5, 0.9 };
        mainEvolutionPath = new EvolutionPath(populationSize, height, width,
                trainee);
    }

    private void tune() {
        System.out.println("Tuning started");
        mainEvolutionPath.setMutationStrength((mutationStrength[1] - mutationStrength[0]) / 2);
        mainEvolutionPath.setMutationProbability((mutationProbability[1] - mutationProbability[0]) / 2);
        mainEvolutionPath.setSelectionFactor((selectionFactor[1] - selectionFactor[0]) / 2);
        mainEvolutionPath.setGenetricDriftFactor((genetricDriftFactor[1] - genetricDriftFactor[0]) / 2);

        long pathRuntime =  (long) tuningDuration * 1000000000 / 4 / 20 ;
        EvolutionPath bestPath = mainEvolutionPath.clone();

        double maxFitness = Double.NEGATIVE_INFINITY;
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
            System.out.print("Tuning mutation strength - Best is: " + bestPath.getMutationStrength() + "          \r");
        }
        System.out.println();

        maxFitness = Double.NEGATIVE_INFINITY;
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
            System.out.print("Tuning mutation prob - Best is: " + bestPath.getMutationProbability() + "          \r");
        }
        System.out.println();

        maxFitness = Double.NEGATIVE_INFINITY;
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
            System.out.print("Tuning selection factor - Best is: " + bestPath.getSelectionFactor() + "          \r");
        }
        System.out.println();

        maxFitness = Double.NEGATIVE_INFINITY;
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
            System.out.print("Tuning genetic drift - Best is: " + bestPath.getGenetricDriftFactor() + "          \r");
        }
        System.out.println();

        if (maxFitness > mainEvolutionPath.getMaxFitness()) {
            mainEvolutionPath = bestPath;
        }
        System.out.println("Finished tuning");
    }

    private void train() {
        long trainingDuration = (long) trainDuration * 1000000000;
        long startTime = System.nanoTime();
        while (running() && System.nanoTime() - startTime <= trainingDuration) {
            mainEvolutionPath.runGeneration();
            System.out.print("Training gen " + mainEvolutionPath.getGenerationCount() + " - Best fitness is: " + mainEvolutionPath.getMaxFitness() + "          \r");
            // if(mainEvolutionPath.getGenerationCount() % 1000 == 0) {
            //     System.out.println(mainEvolutionPath.getBestNetwork());
            // }
        }
        System.out.println();
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

        try {
            if(System.in.available() != 0) {
                running = false;
                return false;
            }
        } 
        catch (Exception e) {
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
                + "]";
    }

}