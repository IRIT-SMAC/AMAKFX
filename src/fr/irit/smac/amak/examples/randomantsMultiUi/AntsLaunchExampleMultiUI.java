package fr.irit.smac.amak.examples.randomantsMultiUi;

import fr.irit.smac.amak.Configuration;
import fr.irit.smac.amak.ui.AmasMultiUIWindow;
import fr.irit.smac.amak.ui.MainWindow;
import fr.irit.smac.amak.ui.VUIMulti;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AntsLaunchExampleMultiUI extends Application{

	
	public static void main (String[] args) {
		
		
		Application.launch(args);
		
	
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Configuration.multiUI=true;
		Configuration.commandLineMode =false;
		
		
		AmasMultiUIWindow window = new AmasMultiUIWindow("Random Ants Multi UI 1");
		AmasMultiUIWindow window2 = new AmasMultiUIWindow("Random Ants Multi UI 2");
		
		
		WorldExampleMultiUI env = new WorldExampleMultiUI(window);
		WorldExampleMultiUI env2 = new WorldExampleMultiUI(window2);
		

		AntHillExampleMultiUI ants = new AntHillExampleMultiUI(window, new VUIMulti("Ants VUI 1"), env);
		AntHillExampleMultiUI ants2 = new AntHillExampleMultiUI(window2, new VUIMulti("Ants VUI 2"), env2);
		
		startTask(ants, 500, 10);
		
		startTask(ants2, 250, 30);
		
		
		
			
	}
	
	public void startTask(AntHillExampleMultiUI amas, long wait, int cycles) 
    {
        // Create a Runnable
        Runnable task = new Runnable()
        {
            public void run()
            {
                runTask(amas, wait, cycles);
            }
        };
 
        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
    }
	
	public void runTask(AntHillExampleMultiUI amas, long wait, int cycles) 
    {
        for(int i = 0; i < cycles; i++) 
        {
            try
            {
                // Get the Status
                final String status = "Processing " + i + " of " + cycles;
                 
                // Update the Label on the JavaFx Application Thread        
                Platform.runLater(new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                    	amas.cycle();
                    	System.out.println(status);
                    }
                });
         
                Thread.sleep(wait);
            }
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
    }   

	
	@Override
	public void stop() throws Exception {
		super.stop();
		System.exit(0);
	}
}
