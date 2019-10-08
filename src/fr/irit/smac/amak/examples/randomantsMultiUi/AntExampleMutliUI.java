package fr.irit.smac.amak.examples.randomantsMultiUi;

import fr.irit.smac.amak.Agent;
import fr.irit.smac.amak.ui.AmasMultiUIWindow;
import fr.irit.smac.amak.ui.VUI;
import fr.irit.smac.amak.ui.drawables.DrawableImage;

public class AntExampleMutliUI extends Agent<AntHillExampleMultiUI, WorldExampleMultiUI> {
	
	
	
	private boolean dead = false; 
	
	/**
	 * X coordinate of the ant in the world
	 */
	public double dx;
	/**
	 * Y coordinate of the ant in the world
	 */
	public double dy;
	/**
	 * Angle in radians
	 */
	private double angle = Math.random() * Math.PI * 2;
	private DrawableImage image;

	/**
	 * Constructor of the ant
	 * 
	 * @param amas
	 *            the amas the ant belongs to
	 * @param startX
	 *            Initial X coordinate
	 * @param startY
	 *            Initial Y coordinate
	 */
	public AntExampleMutliUI(AmasMultiUIWindow window, AntHillExampleMultiUI amas, double startX, double startY) {
		super(window, amas, startX, startY);
	}
	@Override
	public void onInitialization() {
		dx = (double) params[0];
		dy = (double) params[1];
	}

	@Override
	protected void onRenderingInitialization() {
		image =  getAmas().getVUIMulti().createAndAddImage(dx, dy, "file:resources/ant.png");
		image.setName("Ant "+getId());
	}

	/**
	 * Move in a random direction
	 */
	@Override
	protected void onDecideAndAct() {

		double random = amas.getEnvironment().getRandom().nextGaussian();
		angle += random * 0.1;
		dx += Math.cos(angle);
		dy += Math.sin(angle);
		while (dx >= getAmas().getEnvironment().getWidth() / 2)
			dx -= getAmas().getEnvironment().getWidth();
		while (dy >= getAmas().getEnvironment().getHeight() / 2)
			dy -= getAmas().getEnvironment().getHeight();
		while (dx < -getAmas().getEnvironment().getWidth() / 2)
			dx += getAmas().getEnvironment().getWidth();
		while (dy < -getAmas().getEnvironment().getHeight() / 2)
			dy += getAmas().getEnvironment().getHeight();

		if (amas.getEnvironment().getRandom().nextDouble() < 0.001) {
			dead = true;
			destroy();
		}

		if (amas.getEnvironment().getRandom().nextDouble() < 0.001) {
			new AntExampleMutliUI(getAmas().amasMultiUIWindow, getAmas(), dx, dy);
		}
	}

	@Override
	public void onUpdateRender() {
		image.move(dx, dy);
		image.setAngle(angle);
		image.setInfo("Ant "+getId()+"\nPosition "+dx+" "+dy+"\nAngle "+angle);
		if(dead) {
			image.setFilename("file:Resources/ant_dead.png");
			image.setInfo("Ant "+getId()+"\nPosition "+dx+" "+dy+"\nAngle "+angle+"\nDead");
		}
	}
}
