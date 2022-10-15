import java.util.HashSet;

public class TestTrainee implements Trainable {
    private double[] inputs;
    private double[] expectedOutput;

    

    public TestTrainee() {
        inputs = new double[] {1, 12, 3.5, 4, 5, 3, 7, 8, 9, 10};
        expectedOutput = new double[]{6.4, 3.7, 8.6, 2.6, 5, 8, 2, 4.5, 6, 8};
    }

    @Override
    public double calculateFitness(GeneticNN nn) {
        double fitness = 0;
        HashSet<Double> seen = new HashSet<>();
        for (int i = 0; i < inputs.length; i++) {
            double output = nn.runWithDropouts(new double[]{inputs[i]}) [0];
            if(seen.contains(output)) {
                fitness -= 100;
            }
            fitness -= Math.abs(expectedOutput[i] - output);
            seen.add(output);
        }
        return fitness;
    }


    @Override
    public int getOutputSize() {
        return 1;
    }
    
}