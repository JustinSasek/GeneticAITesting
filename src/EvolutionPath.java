import java.util.Arrays;

public class EvolutionPath {
    private double mutationStrength;
    private double mutationProbability;
    private double selectionFactor;
    private double genetricDriftFactor;
    private int populationSize;
    private NeuralNetwork[] population;
    private int outputSize;
    private int height;
    private int width;
    private Trainable trainee;
    private int generationCount;

    public EvolutionPath(int populationSize, int height, int width, Trainable trainee,
            NeuralNetwork seed) {

        this.outputSize = trainee.getOutputSize();
        this.height = height;
        this.width = width;
        this.trainee = trainee;
        generationCount = 0;
        initFromSeed(seed);

    }

    public EvolutionPath(int populationSize, int height, int width, Trainable trainee) {
        this.populationSize = populationSize;
        this.outputSize = trainee.getOutputSize();
        this.height = height;
        this.width = width;
        this.trainee = trainee;
        generationCount = 0;
        initRandomly();
    }

    private EvolutionPath() {
    }

    private void initRandomly() {
        population = new NeuralNetwork[populationSize];
        for (int i = 0; i < populationSize; i++) {
            population[i] = new NeuralNetwork(outputSize, height, width);
        }
    }

    public void initFromSeed(NeuralNetwork seed) {
        createNextGenerationFromParents(new NeuralNetwork[] { seed });
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public double[] runNeuron(int index, double[] input) {
        return population[index].run(input);
    }

    public void setFitness(int index, double fitness) {
        population[index].setFitness(fitness);
    }

    public void runGeneration() {
        generationCount++;
        for (NeuralNetwork network : population) {
            network.setFitness(trainee.calculateFitness(network));
        }
        createNextGenerationFromParents(getParents());
    }

    private NeuralNetwork[] getParents() {
        int geneticDriftCutoff = (int) (populationSize * (1 - genetricDriftFactor));
        int parentCutoff = (int) (selectionFactor * geneticDriftCutoff);
        parentCutoff = Math.max(parentCutoff, 1);

        NeuralNetwork[] parents = new NeuralNetwork[parentCutoff];
        // selection sort
        for (int i = 0; i < parents.length; i++) {
            double maxFitness = Double.NEGATIVE_INFINITY;
            int maxFitnessIndex = 0;
            for (int j = 0; j < populationSize; j++) {
                if (population[j] != null && population[j].getFitness() > maxFitness) {
                    maxFitness = population[j].getFitness();
                    maxFitnessIndex = j;
                }
            }
            parents[i] = population[maxFitnessIndex];
            population[maxFitnessIndex] = null;
        }
        return parents;
    }

    private void createNextGenerationFromParents(NeuralNetwork[] parents) {
        if (parents.length == 0) {
            throw new IllegalArgumentException("Input lengthfor createNextGenerationFromParents method is 0");
        }
        int geneticDriftCutoff = (int) (populationSize * (1 - genetricDriftFactor));
        population[0] = parents[0];
        for (int i = 1; i < geneticDriftCutoff; i++) {
            int parentIndex = (int) (Math.random() * parents.length
                    * i / geneticDriftCutoff);
            population[i] = parents[parentIndex].mutate(mutationStrength, mutationProbability);
        }
        for (int i = geneticDriftCutoff; i < populationSize; i++) {
            population[i] = new NeuralNetwork(outputSize, height, width);
        }
    }

    public double getMaxFitness() {
        double maxFitness = Double.NEGATIVE_INFINITY;
        for (int j = 0; j < populationSize; j++) {
            maxFitness = Math.max(maxFitness, population[j].getFitness());
        }
        return maxFitness;
    }

    public NeuralNetwork getBestNetwork() {
        double maxFitness = Double.NEGATIVE_INFINITY;
        int maxIndex = 0;
        for (int i = 0; i < populationSize; i++) {
            if (population[i].getFitness() > maxFitness) {
                maxFitness = population[i].getFitness();
                maxIndex = i;
            }
        }
        return population[maxIndex].clone();
    }

    @Override
    public String toString() {
        return "EvolutionPath [mutationStrength=" + mutationStrength + ", mutationProbability=" + mutationProbability
                + ", selectionFactor=" + selectionFactor + ", genetricDriftFactor=" + genetricDriftFactor
                + ", populationSize=" + populationSize + ", outputSize=" + outputSize
                + ", layerCount=" + height + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(mutationStrength);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mutationProbability);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(selectionFactor);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(genetricDriftFactor);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + populationSize;
        result = prime * result + Arrays.hashCode(population);
        result = prime * result + outputSize;
        result = prime * result + height;
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
        EvolutionPath other = (EvolutionPath) obj;
        if (Double.doubleToLongBits(mutationStrength) != Double.doubleToLongBits(other.mutationStrength))
            return false;
        if (Double.doubleToLongBits(mutationProbability) != Double.doubleToLongBits(other.mutationProbability))
            return false;
        if (Double.doubleToLongBits(selectionFactor) != Double.doubleToLongBits(other.selectionFactor))
            return false;
        if (Double.doubleToLongBits(genetricDriftFactor) != Double.doubleToLongBits(other.genetricDriftFactor))
            return false;
        if (populationSize != other.populationSize)
            return false;
        if (!Arrays.equals(population, other.population))
            return false;
        if (outputSize != other.outputSize)
            return false;
        if (height != other.height)
            return false;
        return true;
    }

    @Override
    protected EvolutionPath clone() {
        EvolutionPath clone = new EvolutionPath();
        clone.mutationStrength = mutationStrength;
        clone.mutationProbability = mutationProbability;
        clone.selectionFactor = selectionFactor;
        clone.genetricDriftFactor = genetricDriftFactor;
        clone.populationSize = populationSize;
        clone.outputSize = outputSize;
        clone.height = height;
        clone.width = width;
        clone.trainee = trainee;

        clone.population = new NeuralNetwork[population.length];
        for (int i = 0; i < population.length; i++) {
            clone.population[i] = population[i].clone();
        }

        return clone;
    }

    public double getMutationStrength() {
        return mutationStrength;
    }

    public void setMutationStrength(double mutationStrength) {
        this.mutationStrength = mutationStrength;
    }

    public double getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public double getSelectionFactor() {
        return selectionFactor;
    }

    public void setSelectionFactor(double selectionFactor) {
        this.selectionFactor = selectionFactor;
    }

    public double getGenetricDriftFactor() {
        return genetricDriftFactor;
    }

    public void setGenetricDriftFactor(double genetricDriftFactor) {
        this.genetricDriftFactor = genetricDriftFactor;
    }

}