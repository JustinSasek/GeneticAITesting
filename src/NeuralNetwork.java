import java.util.Arrays;

public class NeuralNetwork {
    private int inputSize;
    private int outputSize;
    private int middleLayers;
    private double[][][] middleWeights;
    private double[][] outputWeights;
    private double fitness;

    public NeuralNetwork(int inputSize, int outputSize, int middleLayers) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.middleLayers = middleLayers;
        middleWeights = new double[middleLayers][inputSize][inputSize];
        outputWeights = new double[outputSize][inputSize];
        fitness = 0;
        generateRandomWeights();
    }

    private void generateRandomWeights() {

    }

    public double[] run(double[] input) {
        if(input.length != inputSize) {
            System.err.println("invalid input length");
            return null;
        }
        double[] lastLayer = Arrays.copyOf(input, inputSize);
        for(double[][] layerWeights : middleWeights) {
            double[] temp = new double[inputSize];
            for(int nueronIndex = 0; nueronIndex < inputSize; nueronIndex++) {
                for(int lastNueronIndex = 0; lastNueronIndex < inputSize; lastNueronIndex++) {
                    temp[nueronIndex] += lastLayer[lastNueronIndex] * layerWeights[nueronIndex][lastNueronIndex];
                }
                temp[nueronIndex] = Math.max(temp[nueronIndex], 0);  //activation function
            }
            lastLayer = temp;
        }
        double[] output = new double[outputSize];
        for(int nueronIndex = 0; nueronIndex < outputSize; nueronIndex++) {
            for(int lastNueronIndex = 0; lastNueronIndex < inputSize; lastNueronIndex++) {
                output[nueronIndex] += lastLayer[lastNueronIndex] * outputWeights[nueronIndex][lastNueronIndex];
            }
        }
        return output;
    }


    public NeuralNetwork mutate(double strength, double probability) {
        NeuralNetwork clone = clone();
        for(double[][] layerWeights : clone.middleWeights) {
            for(double[] nueronWeights : layerWeights) {
                for(int weightIndex = 0; weightIndex < nueronWeights.length; weightIndex++) {
                    if(Math.random() < probability) {
                        nueronWeights[weightIndex] += strength  *  (2 * Math.random() - 1);
                    }
                }
            }
        }
        return clone;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public NeuralNetwork clone() {
        NeuralNetwork clone = new NeuralNetwork(inputSize, outputSize, middleLayers);
        for(int layerIndex = 0; layerIndex < middleLayers; layerIndex++) {
            for(int neuronIndex = 0; neuronIndex < inputSize; neuronIndex++) {
                clone.middleWeights[layerIndex][neuronIndex] = Arrays.copyOf(middleWeights[layerIndex][neuronIndex], inputSize);
            }
        }
        for(int neuronIndex = 0; neuronIndex < outputSize; neuronIndex++) {
            clone.outputWeights[neuronIndex] = Arrays.copyOf(outputWeights[neuronIndex], outputSize);
        }
        return clone;
    }
}


