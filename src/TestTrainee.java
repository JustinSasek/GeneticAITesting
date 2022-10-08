public class TestTrainee implements Trainable {
    private double[] inputs;
    private double[] expectedOutput;

    

    public TestTrainee() {
        inputs = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        expectedOutput = new double[]{6.4, 3.7, 8.6, 2.6};
    }

    @Override
    public double calculateFitness(double[] output) {
        double fitness = 0;
        for (int i = 0; i < output.length; i++) {
            fitness -= Math.abs(expectedOutput[i] - output[i]);
        }
        return fitness;
    }

    @Override
    public double[] getInputs() {
        return inputs;
    }

    @Override
    public int getOutputSize() {
        return expectedOutput.length;
    }
    
}