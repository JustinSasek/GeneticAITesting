public class Client {
    public static void main(String[] args) { 
        System.out.println("Trainer starting");
        Trainable trainee = new TestTrainee();
        Trainer trainer = new Trainer(trainee, 3);
        trainer.run();
        NeuralNetwork output = trainer.getBestNetwork();
    }
    
}
