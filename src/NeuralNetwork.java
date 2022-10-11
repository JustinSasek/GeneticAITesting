import java.util.Arrays;

public class NeuralNetwork {
    private int inputSize;
    private int outputSize;
    private int midHeight;
    private int width;
    private double[][][] middleWeights;
    private double[][] outputWeights;
    private double fitness;
    private static double maxWeight = 1;
    private static double minWeight = -1;

    public NeuralNetwork(int outputSize, int height, int width) {
        this.outputSize = outputSize;
        this.midHeight = height - 2;
        this.width = width;
        fitness = Double.NEGATIVE_INFINITY;
    }

    private NeuralNetwork() {
    }

    private void initWeights(int inputSize) {
        if(inputSize <= 0) {
            throw new IllegalArgumentException("Input size to inputSize function is invalid: " + inputSize);
        }
        this.inputSize = inputSize;

        if (width == -1) {
            width = Math.max(inputSize, 5);
        }
        middleWeights = new double[midHeight][width][width + 1];
        middleWeights[0] = new double[width][inputSize + 1];
        outputWeights = new double[outputSize][width + 1];
        generateRandomWeights();
    }

    public static NeuralNetwork loadFromFile(String filename) {
        return null;
    }

    public static void saveToFile(NeuralNetwork nn, String filename) {
        return;
    }

    private void generateRandomWeights() {
        for (double[][] layerWeights : middleWeights) {
            for (double[] nueronWeights : layerWeights) {
                for (int weightIndex = 0; weightIndex < nueronWeights.length; weightIndex++) {
                    nueronWeights[weightIndex] = Math.random() * (maxWeight - minWeight) - minWeight;
                }
            }
        }
        for (double[] nueronWeights : outputWeights) {
            for (int weightIndex = 0; weightIndex < nueronWeights.length; weightIndex++) {
                nueronWeights[weightIndex] = Math.random() * (maxWeight - minWeight) - minWeight;
            }
        }
    }

    public double[] run(double[] input) {
        if(input.length == 0) {
            throw new IllegalArgumentException("Input length for run function is 0");
        }
        if (middleWeights == null) {
            initWeights(input.length);
        } else if (input.length != inputSize) {
            throw new IllegalArgumentException("invalid input length: " + input.length + " when supposed to be: " + inputSize);
        }
        double[] activations = Arrays.copyOf(input, input.length);
        for (double[][] layerWeights : middleWeights) {

            double[] temp = new double[layerWeights.length];
            for (int i = 0; i < layerWeights.length; i++) {
                for (int j = 0; j < layerWeights[i].length; j++) {

                    if (j == layerWeights[i].length - 1) { // bias
                        temp[i] += layerWeights[i][j];
                    } else {
                        temp[i] += activations[j] * layerWeights[i][j];
                        temp[i] = Math.max(temp[i], 0); // activation function
                    }

                }
            }
            activations = temp;
        }
        double[] output = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j <= activations.length; j++) {
                if (j == activations.length) {
                    output[i] += outputWeights[i][j];
                } else {
                    output[i] += activations[j] * outputWeights[i][j];
                }
            }
        }
        return output;
    }

    public NeuralNetwork mutate(double strength, double probability) {
        NeuralNetwork clone = clone();
        if (middleWeights != null) {
            for (double[][] layerWeights : clone.middleWeights) {
                for (double[] nueronWeights : layerWeights) {
                    for (int weightIndex = 0; weightIndex < nueronWeights.length; weightIndex++) {
                        if (Math.random() < probability) {
                            nueronWeights[weightIndex] += strength * (2 * Math.random() - 1);
                            maxWeight = Math.max(maxWeight, nueronWeights[weightIndex]);
                            minWeight = Math.min(minWeight, nueronWeights[weightIndex]);
                        }
                    }
                }
            }
        }
        if(clone == null) {
            System.out.println("mutate output null");
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
        NeuralNetwork clone = new NeuralNetwork();
        clone.outputSize = outputSize;
        clone.midHeight = midHeight;
        clone.width = width;
        clone.fitness = fitness;

        if (middleWeights != null) {
            clone.initWeights(inputSize);
            for (int layerIndex = 0; layerIndex < middleWeights.length; layerIndex++) {
                for (int i = 0; i < middleWeights[layerIndex].length; i++) {

                    clone.middleWeights[layerIndex][i] = 
                    Arrays.copyOf(middleWeights[layerIndex][i],
                            middleWeights[layerIndex][i].length);
                }
            }
        }

        if (outputWeights != null) {
            for (int i = 0; i < outputSize; i++) {
                clone.outputWeights[i] = Arrays.copyOf(outputWeights[i], outputWeights[i].length);
            }
        }
        return clone;
    }

    @Override
    public String toString() {
        return "NeuralNetwork [inputSize=" + inputSize + ", outputSize=" + outputSize + ", middleLayers=" + midHeight
                + ", middleWeights=" + Arrays.toString(middleWeights) + ", outputWeights="
                + Arrays.toString(outputWeights) + ", fitness=" + fitness + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + inputSize;
        result = prime * result + outputSize;
        result = prime * result + midHeight;
        result = prime * result + Arrays.deepHashCode(middleWeights);
        result = prime * result + Arrays.deepHashCode(outputWeights);
        long temp;
        temp = Double.doubleToLongBits(fitness);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        NeuralNetwork other = (NeuralNetwork) obj;
        if (inputSize != other.inputSize)
            return false;
        if (outputSize != other.outputSize)
            return false;
        if (midHeight != other.midHeight)
            return false;
        if (!Arrays.deepEquals(middleWeights, other.middleWeights))
            return false;
        if (!Arrays.deepEquals(outputWeights, other.outputWeights))
            return false;
        if (Double.doubleToLongBits(fitness) != Double.doubleToLongBits(other.fitness))
            return false;
        return true;
    }

}
