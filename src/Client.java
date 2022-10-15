public class Client {
    public static void main(String[] args) { 
        System.out.println("Trainer started");
        Trainable trainee = new TestTrainee();
        Trainer trainer = new Trainer(trainee, 5, 10);
        trainer.run();
        GeneticNN output = trainer.getBestNetwork();
        double[] vals = new double[]{1, 12, 3.5, 4, 5, 3, 7, 8, 9, 10};
        System.out.println(output);
        for (int i = 0; i < vals.length; i++) {
            System.out.println(output.toString(new double[]{vals[i]}));
        }
        
    }
    
}
