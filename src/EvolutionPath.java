

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


    public EvolutionPath(double mutationStrength, double mutationProbability, double selectionFactor,
            double genetricDriftFactor, int populationSize,
            int inputSize, int outputSize, int layerCount) {
        this.mutationStrength = mutationStrength;
        this.mutationProbability = mutationProbability;
        this.selectionFactor = selectionFactor;
        this.genetricDriftFactor = genetricDriftFactor;
        this.populationSize = populationSize;
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.layerCount = layerCount;
    }

    private void init() {
        population = new NeuralNetwork[populationSize];
    }

    public void initFromFile(String filename) {

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

    public void createNextGeneration() { 
        int geneticDriftCutoff = (int) (populationSize * (1 - genetricDriftFactor));
        int parentCutoff = (int) (selectionFactor * geneticDriftCutoff);

        NeuralNetwork[] parents = new NeuralNetwork[parentCutoff];

        for(NeuralNetwork parent : parents) {
            double maxFitness = Double.MIN_VALUE;
            int maxFitnessIndex = 0;
            for(int i = 0; i < populationSize; i++) {
                if(population[i] != null && population[i].getFitness() > maxFitness) {
                    maxFitness = population[i].getFitness();
                    maxFitnessIndex = i;
                }
            }
            parent = population[maxFitnessIndex];
            population[maxFitnessIndex] = null;
        }

        for(int i = 0; i < geneticDriftCutoff; i++) {
            int parentIndex = (int) (Math.random() * selectionFactor * geneticDriftCutoff 
                    * i / geneticDriftCutoff);
            population[i] = parents[parentIndex].mutate(mutationStrength, mutationProbability);
        }
        for(int i = geneticDriftCutoff; i < populationSize; i++) {
            population[i] = new NeuralNetwork(inputSize, outputSize, layerCount);
        }
    }
    
}