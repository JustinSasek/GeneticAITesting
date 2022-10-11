public class Client {
    public static void main(String[] args) { 
        System.out.println("Trainer started");
        Trainable trainee = new TestTrainee();
        Trainer trainer = new Trainer(trainee, 10, 10);
        trainer.run();
        NeuralNetwork output = trainer.getBestNetwork();
        System.out.println("Trainer finished - Max Fitness = " + output.getFitness());
        double[] vals = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int i = 0; i < vals.length; i++) {
            System.out.println(output.run(new double[]{vals[i]})[0]);
        }
        
    }
    
}
