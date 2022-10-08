public interface Trainable {
    public double calculateFitness(double[] output);
    public double[] getInputs();
    public int getOutputSize();
}
