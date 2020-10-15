package fr.irit.smac.amak.examples.roboticArm;

import fr.irit.smac.amak.Environment;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.ui.AmasMultiUIWindow;

public class WorldExampleMultiUI extends Environment {
	public WorldExampleMultiUI(AmasMultiUIWindow window, Object...params) {
		super(window, Scheduling.DEFAULT, params);
	}

	private int width;
	private int height;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public void onInitialization() {
		this.width = 800;
		this.height = 600;
	}

}
