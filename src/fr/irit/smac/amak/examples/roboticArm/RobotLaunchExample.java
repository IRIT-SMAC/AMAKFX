package fr.irit.smac.amak.examples.roboticArm;
import fr.irit.smac.amak.Configuration;

public class RobotLaunchExample{



	public static void main (String[] args)  {

		start();

	}


	public static void start() {


        // Set AMAK configuration before creating an AMOEBA
        Configuration.multiUI=true;
        Configuration.commandLineMode = true;
        Configuration.allowedSimultaneousAgentsExecution = 1;
        Configuration.waitForGUI = false;
        Configuration.plotMilliSecondsUpdate = 20000;

        double robotSegments[] = new double[ROBOT_PARAMS.jointsNumber];
        double incLength = ROBOT_PARAMS.length/ROBOT_PARAMS.jointsNumber;

        for(int i = 0;i<ROBOT_PARAMS.jointsNumber;i++){
            robotSegments[i] = incLength;
        }


        RobotController robotController = new RobotController(ROBOT_PARAMS.jointsNumber);
        RobotArmManager robotArmManager = new RobotArmManager(ROBOT_PARAMS.jointsNumber, robotSegments, robotController, ROBOT_PARAMS.numberOfCyclesForTheExperiment);
        RobotWorlExampleMultiUI robot = new RobotWorlExampleMultiUI(null, null, null, robotController, robotArmManager, ROBOT_PARAMS.jointsNumber);
        robotArmManager.maxError = 2*ROBOT_PARAMS.length;

        while(!robotArmManager.stopExperiment){
            robot.cycleCommandLine();
        }

	}
	



	

}
