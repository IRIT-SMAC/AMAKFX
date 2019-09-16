package fr.irit.smac.amak.examples.randomants;

import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.tools.RunLaterHelper;
import fr.irit.smac.amak.ui.VUI;
import fr.irit.smac.amak.ui.drawables.DrawableString;

public class AntHillExample extends Amas<WorldExample> {

	private DrawableString antsCountLabel;

	public AntHillExample(WorldExample env) {
		super(env, Scheduling.DEFAULT);
	}

	@Override
	protected void onRenderingInitialization() {
		VUI.get().createAndAddImage(20, 20, "file:Resources/ant.png").setFixed().setLayer(10).setShowInExplorer(false);
		antsCountLabel = (DrawableString) VUI.get().createAndAddString(45, 25, "Ants count").setFixed().setLayer(10).setShowInExplorer(false);
	}

	@Override
	protected void onInitialAgentsCreation() {
		for (int i = 0; i < 50; i++)
			new AntExample(this, 0, 0);
	}

	@Override
	protected void onSystemCycleEnd() {
		RunLaterHelper.runLater(()->antsCountLabel.setText("Ants count: " + getAgents().size()));
	}
}
