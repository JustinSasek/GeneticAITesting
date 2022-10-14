import java.util.Arrays;

public class Trainer {
    private Trainable trainee;

    private double[] mutationStrength;

    private double[] mutationProbability;

    private double[] selectionFactor;

    private double[] genetricDriftFactor;

    private static double dropoutFactor = 0.3;
    private static final int populationSize = 500;
    private final static double trainDuration = 40; // sec
    private final static double tuningDuration = 20; // tuning time // sec
    private EvolutionPath mainEvolutionPath;

    private boolean running;

    public Trainer(Trainable trainee, int height) {
        this(trainee, height, -1);
    }

    public Trainer(Trainable trainee, int height, int width) {
        this.mutationStrength = new double[] { .05, 2 };
        this.mutationProbability = new double[] { 0.01, 0.1 };
        this.selectionFactor = new double[] { 0.01, 0.25 };
        this.genetricDriftFactor = new double[] { 0.5, 0.9 };
        mainEvolutionPath = new EvolutionPath(populationSize, height, width, dropoutFactor,
                trainee);
    }

    private void tune() {
        System.out.println("Tuning started");
        mainEvolutionPath.setMutationStrength((mutationStrength[1] - mutationStrength[0]) / 2);
        mainEvolutionPath.setMutationProbability((mutationProbability[1] - mutationProbability[0]) / 2);
        mainEvolutionPath.setSelectionFactor((selectionFactor[1] - selectionFactor[0]) / 2);
        mainEvolutionPath.setGenetricDriftFactor((genetricDriftFactor[1] - genetricDriftFactor[0]) / 2);

        long pathRuntime = (long) tuningDuration * 1000000000 / 4 / 20;
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
        }
        System.out.println("Tuning mutation strength - Best is: "
                + Math.round(bestPath.getMutationStrength() * 10000) / 10000.0 + "          \r");

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
        }
        System.out.println(
                "Tuning mutation prob - Best is: " + Math.round(bestPath.getMutationProbability() * 10000) / 10000.0);

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
        }
        System.out.println(
                "Tuning selection factor - Best is: " + Math.round(bestPath.getSelectionFactor() * 10000) / 10000.0);

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
        }
        System.out.println(
                "Tuning genetic drift - Best is: " + Math.round(bestPath.getGenetricDriftFactor() * 10000) / 10000.0);

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
            if (mainEvolutionPath.getGenerationCount() % 100 == 0) {
                System.out.print("Training gen " + mainEvolutionPath.getGenerationCount() + " - Best fitness is: "
                        + Math.round(mainEvolutionPath.getMaxFitness() * 10000) / 10000.0 + "          \r");
                // System.out.println(mainEvolutionPath.getBestNetwork());
            }
        }
        System.out.println();
    }

    public void run() {
        long startTime = System.nanoTime();
        running = true;
        while (running) {
            tune();
            train();
        }
        NeuralNetwork bestNN = mainEvolutionPath.getBestNetwork();
        int sec = (int) (startTime / 1000000000);
        int min = sec / 60;
        int hour = min / 60;
        System.out.println("Trainer finished after "
                + hour + ":"
                + min % 60 + ":"
                + sec % 60
                + " - Max Fitness = " + Math.round(bestNN.getFitness() * 10000) / 10000.0);
    }

    private boolean running() {
        if (!running) {
            return false;
        }

        try {
            if (System.in.available() != 0) {
                running = false;
                return false;
            }
        } catch (Exception e) {
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