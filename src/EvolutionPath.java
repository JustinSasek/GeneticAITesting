import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

public class EvolutionPath {
    private double mutationStrength;
    private double mutationProbability;
    private double selectionFactor;
    private double genetricDriftFactor;
    private int populationSize;
    private NeuralNetwork[] population;
    private int inputSize;
    private int outputSize;
    private int layerCount;
    private Trainable trainee;

    public EvolutionPath(int populationSize,
            int inputSize, int outputSize, int layerCount, Trainable trainee, String filename) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.layerCount = layerCount;
        this.trainee = trainee;
        initFromFile(filename);
    }

    public EvolutionPath(int populationSize,
            int inputSize, int outputSize, int layerCount, Trainable trainee) {
        this.populationSize = populationSize;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.layerCount = layerCount;
        this.trainee = trainee;
        init();
    }

    private EvolutionPath() {
    }

    private void init() {
        population = new NeuralNetwork[populationSize];
        for (NeuralNetwork n : population) {
            n = new NeuralNetwork(inputSize, outputSize, layerCount);
        }
    }

    public void initFromFile(String filename) {
        NeuralNetwork temp;
        try {
            ObjectInputStream input;
            input = new ObjectInputStream(new FileInputStream(filename));
            temp = (NeuralNetwork) input.readObject();
            input.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public int getPopulationSize() {
        return populationSize;
    }

    public double[] runNeuron(int index, double[] input) {
        return population[index].run(input);
    }

    public void setFitness(int index, double fitness) {
        population[index].setFitness(fitness);
    }

    public void runGeneration() {
        for (NeuralNetwork network : population) {
            double output[] = network.run(trainee.getInputs());
            network.setFitness(trainee.calculateFitness(output));
        }
        createNextGeneration();
    }

    private void createNextGeneration() {
        int geneticDriftCutoff = (int) (populationSize * (1 - genetricDriftFactor));
        int parentCutoff = (int) (selectionFactor * geneticDriftCutoff);

        NeuralNetwork[] parents = new NeuralNetwork[parentCutoff];

        // selection sort
        for (int i = 0; i < parents.length; i++) {
            double maxFitness = Double.MIN_VALUE;
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

        for (int i = 0; i < geneticDriftCutoff; i++) {
            int parentIndex = (int) (Math.random() * selectionFactor * geneticDriftCutoff
                    * i / geneticDriftCutoff);
            population[i] = parents[parentIndex].mutate(mutationStrength, mutationProbability);
        }
        for (int i = geneticDriftCutoff; i < populationSize; i++) {
            population[i] = new NeuralNetwork(inputSize, outputSize, layerCount);
        }
    }

    public double getMaxFitness() {
        double maxFitness = Double.MIN_VALUE;
        for (int j = 0; j < populationSize; j++) {
            maxFitness = Math.max(maxFitness, population[j].getFitness());
        }
        return maxFitness;
    }

    public NeuralNetwork getBestNetwork() {
        double maxFitness = Double.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < populationSize; i++) {
            if(population[i].getFitness() > maxFitness) {
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
                + ", populationSize=" + populationSize + ", inputSize=" + inputSize + ", outputSize=" + outputSize
                + ", layerCount=" + layerCount + "]";
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
        result = prime * result + inputSize;
        result = prime * result + outputSize;
        result = prime * result + layerCount;
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
        if (inputSize != other.inputSize)
            return false;
        if (outputSize != other.outputSize)
            return false;
        if (layerCount != other.layerCount)
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
        clone.inputSize = inputSize;
        clone.outputSize = outputSize;
        clone.layerCount = layerCount;

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