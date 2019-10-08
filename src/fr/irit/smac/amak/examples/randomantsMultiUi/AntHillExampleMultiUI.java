package fr.irit.smac.amak.examples.randomantsMultiUi;

import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.tools.RunLaterHelper;
import fr.irit.smac.amak.ui.AmasMultiUIWindow;
import fr.irit.smac.amak.ui.VUI;
import fr.irit.smac.amak.ui.VUIMulti;
import fr.irit.smac.amak.ui.drawables.DrawableString;

public class AntHillExampleMultiUI extends Amas<WorldExampleMultiUI> {

	private DrawableString antsCountLabel;

	public AntHillExampleMultiUI(AmasMultiUIWindow window, VUIMulti vui, WorldExampleMultiUI env) {
		super(window, vui, env, Scheduling.DEFAULT);
	}

	@Override
	protected void onRenderingInitialization() {
		vuiMulti.createAndAddImage(20, 20, "file:Resources/ant.png").setFixed().setLayer(10).setShowInExplorer(false);
		antsCountLabel = (DrawableString) vuiMulti.createAndAddString(45, 25, "Ants count").setFixed().setLayer(10).setShowInExplorer(false);
	}

	@Override
	protected void onInitialAgentsCreation() {
		for (int i = 0; i < 50; i++) {
			new AntExampleMutliUI(amasMultiUIWindow, this, 0, 0);
		}
			
	}

	@Override
	protected void onSystemCycleEnd() {
		RunLaterHelper.runLater(()->antsCountLabel.setText("Ants count: " + getAgents().size()));
	}
}
