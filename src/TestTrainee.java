public class TestTrainee implements Trainable {
    private double[] inputs;
    private double[] expectedOutput;

    

    public TestTrainee() {
        inputs = new double[] {1, 12, 3.5, 4, 5, 3, 7, 8, 9, 10};
        expectedOutput = new double[]{6.4, 3.7, 8.6, 2.6, 5, 8, 2, 4.5, 6, 8};
    }

    @Override
    public double calculateFitness(NeuralNetwork nn) {
        double fitness = 0;
        for (int i = 0; i < inputs.length; i++) {
            double output = nn.run(new double[]{inputs[i]})[0];
            fitness -= Math.abs(expectedOutput[i] - output);
        }
        return fitness;
    }


    @Override
    public int getOutputSize() {
        return 1;
    }
    
}