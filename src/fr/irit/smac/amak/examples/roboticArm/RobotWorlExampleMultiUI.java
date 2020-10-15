package fr.irit.smac.amak.examples.roboticArm;

import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Configuration;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.ui.AmasMultiUIWindow;
import fr.irit.smac.amak.ui.VUIMulti;

public class RobotWorlExampleMultiUI extends Amas<WorldExampleMultiUI> {

	public RobotExampleMutliUI robotExampleMutliUI;
	VUIMulti vuiErrorDispersion;


	public RobotWorlExampleMultiUI(AmasMultiUIWindow window, VUIMulti vui, WorldExampleMultiUI env, RobotController robotController, RobotArmManager robotArmManager, int jointsNb) {
		super(window, vui, env, Scheduling.DEFAULT);

		if(!Configuration.commandLineMode){
			vuiErrorDispersion = new VUIMulti("Error Dispersion");
			amasMultiUIWindow.addTabbedPanel(vuiErrorDispersion.title, vuiErrorDispersion.getPanel());
		}


		robotExampleMutliUI = new RobotExampleMutliUI(amasMultiUIWindow, this, 0, 0, jointsNb, robotController, robotArmManager);





	}

	public VUIMulti getVuiErrorDispersion(){
		return vuiErrorDispersion;
	}


	public void cycleCommandLine() {
		cycle++;
		robotExampleMutliUI.onDecideAndAct();
	}

	@Override
	protected void onRenderingInitialization() {

	}

	@Override
	protected void onInitialAgentsCreation() {



			
	}

	@Override
	protected void onSystemCycleEnd() {

	}
}
