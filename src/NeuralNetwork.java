import java.util.Arrays;

public class NeuralNetwork {
    private int inputSize;
    private int outputSize;
    private int midHeight;
    private int width;
    private double[][][] middleWeights;
    private boolean[][] middleDropouts;
    private double dropoutFactor;
    private double fitness;
    private static double maxWeight = 1;
    private static double minWeight = -1;

    public NeuralNetwork(int outputSize, int height, int width, double dropoutFactor) {
        this.outputSize = outputSize;
        this.midHeight = height - 2;
        this.width = width;
        this.dropoutFactor = dropoutFactor;
        fitness = Double.NEGATIVE_INFINITY;
    }

    private NeuralNetwork() {
    }

    private void initWeights(int inputSize) {
        if (inputSize <= 0) {
            throw new IllegalArgumentException("Input size to inputSize function is invalid: " + inputSize);
        }
        this.inputSize = inputSize;

        if (width == -1) {
            width = Math.max(inputSize, 5);
        }
        middleWeights = new double[midHeight + 1][width][width + 1];
        middleWeights[0] = new double[width][inputSize + 1];
        middleWeights[midHeight] = new double[outputSize][width + 1];
        middleDropouts = new boolean[midHeight + 1][width];
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
    }

    private void selectDropouts() {
        for (boolean[] layerDropouts : middleDropouts) {
            Arrays.fill(layerDropouts, false);
            int need = (int) (layerDropouts.length * dropoutFactor);
            need = Math.max(need, 1);
            for (int i = 0; i < layerDropouts.length; i++) {
                if (Math.random() < (double) need / (layerDropouts.length - i)) {
                    layerDropouts[i] = true;
                    need--;
                }
            }
        }
    }

    public double[] run(double[] input) {
        return run(input, true);
    }

    public double[] runWithDropouts(double[] input) {
        return run(input, true);
    }

    private double[] run(double[] input, boolean dropouts) {
        if (input.length == 0) {
            throw new IllegalArgumentException("Input length for run function is 0");
        }
        if (middleWeights == null) {
            initWeights(input.length);
            generateRandomWeights();
            selectDropouts();
        } else if (input.length != inputSize) {
            throw new IllegalArgumentException(
                    "invalid input length: " + input.length + " when supposed to be: " + inputSize);
        }
        double[] activations = Arrays.copyOf(input, input.length);
        for (int layerIndex = 0; layerIndex < middleWeights.length; layerIndex++) {
            double[][] layerWeights = middleWeights[layerIndex];
            double[] temp = new double[layerWeights.length];
            for (int i = 0; i < layerWeights.length; i++) {
                if (dropouts && middleDropouts[layerIndex][i]) {
                    continue;
                }
                int count = 0;
                for (int j = 0; j < layerWeights[i].length - 1; j++) {

                    if (!(dropouts && layerIndex != 0 && middleDropouts[layerIndex - 1][j])) {
                            temp[i] += activations[j] * layerWeights[i][j];
                            
                            count ++;
                    } 

                }
                temp[i] *= (layerWeights[i].length - 1) / count;
                temp[i] += layerWeights[i][layerWeights[i].length-1];
                temp[i] = Math.max(temp[i], 0); // activation function
            }
            activations = temp;
        }
        return activations;
    }

    public NeuralNetwork mutate(double strength, double probability) {
        NeuralNetwork clone = clone();
        if (middleWeights != null) {
            for (int layerIndex = 0; layerIndex < clone.middleWeights.length; layerIndex++) {
                for (int i = 0; i < clone.middleWeights[layerIndex].length; i++) {
                    double[] nueronWeights = clone.middleWeights[layerIndex][i];
                    for (int j = 0; j < nueronWeights.length; j++) {
                        if (j == nueronWeights.length - 1 || !middleDropouts[layerIndex][j]) {
                            if (Math.random() < probability) {
                                nueronWeights[j] += strength * (2 * Math.random() - 1);
                                maxWeight = Math.max(maxWeight, nueronWeights[j]);
                                minWeight = Math.min(minWeight, nueronWeights[j]);
                            }
                        }
                    }
                }
            }
        }
        if (clone == null) {
            System.err.println("mutate output null");
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
        clone.dropoutFactor = dropoutFactor;

        if (middleWeights != null) {
            clone.initWeights(inputSize);
            selectDropouts();
            for (int layerIndex = 0; layerIndex < middleWeights.length; layerIndex++) {
                for (int i = 0; i < middleWeights[layerIndex].length; i++) {
                    clone.middleWeights[layerIndex][i] = Arrays.copyOf(middleWeights[layerIndex][i],
                            middleWeights[layerIndex][i].length);
                }
            }
        }

        return clone;
    }

    @Override
    public String toString() {
        StringBuffer output = new StringBuffer(
                "NeuralNetwork [inputSize=" + inputSize + ", outputSize=" + outputSize + ", midHeight=" + midHeight
                        + ", width=" + width + ", fitness=" + fitness + ", minWeight=" + minWeight + ", maxWeight="
                        + maxWeight + ", middleWeights=\n");
        for (int layer = 0; layer < middleWeights.length; layer++) {
            for (int i = 0; i < middleWeights[layer].length; i++) {
                output.append("[");
                for (int j = 0; j < middleWeights[layer][i].length; j++) {
                    output.append(Math.round(middleWeights[layer][i][j] * 1000) / 1000.0);
                    if (j != middleWeights[layer][i].length - 1) {
                        output.append(", ");
                    }
                }
                output.append("]");
                if (i != middleWeights[layer].length - 1) {
                    output.append(",    ");
                }
            }
            output.append("\n\n");
        }
        return output.toString();
    }

    public String toString(double[] input) {
        if (input.length == 0) {
            throw new IllegalArgumentException("Input length for run function is 0");
        }
        if (middleWeights == null) {
            initWeights(input.length);
            generateRandomWeights();
            selectDropouts();
        } else if (input.length != inputSize) {
            throw new IllegalArgumentException(
                    "invalid input length: " + input.length + " when supposed to be: " + inputSize);
        }
        StringBuffer output = new StringBuffer(
                "NeuralNetwork [inputSize=" + inputSize + ", outputSize=" + outputSize + ", midHeight=" + midHeight
                        + ", width=" + width + ", fitness=" + fitness + ", minWeight=" + minWeight + ", maxWeight="
                        + maxWeight + ", layers=\n\n");

        double[] activations = Arrays.copyOf(input, input.length);

        output.append("[");
        for (int i = 0; i < input.length; i++) {
            output.append(Math.round(input[i] * 1000) / 1000.0);
            if (i != input.length - 1) {
                output.append(", ");
            }
        }
        output.append("] \n\n");

        for (double[][] layerWeights : middleWeights) {

            double[] temp = new double[layerWeights.length];
            for (int i = 0; i < layerWeights.length; i++) {
                for (int j = 0; j < layerWeights[i].length; j++) {

                    if (j == layerWeights[i].length - 1) { // bias
                        temp[i] += layerWeights[i][j];
                    } else {
                        temp[i] += activations[j] * layerWeights[i][j];
                    }

                }
                temp[i] = Math.max(temp[i], 0); // activation function
            }
            output.append("[");
            for (int i = 0; i < temp.length; i++) {
                output.append(Math.round(temp[i] * 1000) / 1000.0);
                if (i != temp.length - 1) {
                    output.append(", ");
                }
            }
            output.append("] \n\n");
            activations = temp;
        }
        return output.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + inputSize;
        result = prime * result + outputSize;
        result = prime * result + midHeight;
        result = prime * result + Arrays.deepHashCode(middleWeights);
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
        if (Double.doubleToLongBits(fitness) != Double.doubleToLongBits(other.fitness))
            return false;
        return true;
    }

}
