package fr.irit.smac.amak.examples.roboticArm;



import fr.irit.smac.amak.Configuration;
import fr.irit.smac.amak.ui.AmasMultiUIWindow;
import fr.irit.smac.amak.ui.VUIMulti;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class RobotLaunchExampleMultiUI extends Application{



	public static void main (String[] args) {


		Application.launch(args);
		
	
	}

	@Override
	public void start(Stage primaryStage) throws Exception {


        // Set AMAK configuration before creating an AMOEBA
        Configuration.multiUI=true;
        Configuration.commandLineMode = false;
        Configuration.allowedSimultaneousAgentsExecution = 1;
        Configuration.waitForGUI = true;
        Configuration.plotMilliSecondsUpdate = 20000;

        AmasMultiUIWindow window = new AmasMultiUIWindow("Robot Arm");
        WorldExampleMultiUI env = new WorldExampleMultiUI(window);
        VUIMulti vui = new VUIMulti("Robot");



        double robotSegments[] = new double[ROBOT_PARAMS.jointsNumber];
        double incLength = ROBOT_PARAMS.length/ROBOT_PARAMS.jointsNumber;

        for(int i = 0; i<ROBOT_PARAMS.jointsNumber; i++){
            robotSegments[i] = incLength;
        }

        RobotController robotController = new RobotController(ROBOT_PARAMS.jointsNumber);
        RobotArmManager robotArmManager = new RobotArmManager(ROBOT_PARAMS.jointsNumber, robotSegments, robotController, ROBOT_PARAMS.numberOfCyclesForTheExperiment);
        RobotWorlExampleMultiUI robot = new RobotWorlExampleMultiUI(window, vui, env, robotController, robotArmManager, ROBOT_PARAMS.jointsNumber);
        robotArmManager.maxError = 2*ROBOT_PARAMS.length;

			
	}

	
	@Override
	public void stop() throws Exception {
		super.stop();
		System.exit(0);
	}
}
