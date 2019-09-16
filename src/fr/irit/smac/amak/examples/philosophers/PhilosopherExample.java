package fr.irit.smac.amak.examples.philosophers;

import java.util.Random;

import fr.irit.smac.amak.Agent;
import fr.irit.smac.amak.ui.VUI;
import fr.irit.smac.amak.ui.drawables.Drawable;
import fr.irit.smac.amak.ui.drawables.DrawableRectangle;
import javafx.scene.paint.Color;

/**
 * This class represents a philosopher;
 * 
 * @author perles
 *
 */
public class PhilosopherExample extends Agent<PhilosophersAMASExample, TableExample> {

	/**
	 * The fork on the left of its plate
	 */
	private ForkExample left;
	/**
	 * The fork on the right of its plate
	 */
	private ForkExample right;
	/**
	 * The amount of time (in cycle) the philosopher haven't ate (while in state
	 * hungry)
	 */
	private double hungerDuration;
	/**
	 * The amount of eaten pastas
	 */
	private double eatenPastas;

	/**
	 * The id of the philosopher
	 */
	private int id;

	/**
	 * States philosophers can be in
	 * 
	 * @author perles
	 *
	 */
	public enum State {
		/**
		 * The philosopher is thinking. It essentially means that they are not hungry
		 * and not eating
		 */
		THINK,
		/**
		 * The philosopher is hungry. He wants to be in the state eating.
		 */
		HUNGRY,
		/**
		 * The philosopher has obtained the two forks and eat.
		 */
		EATING
	}

	/**
	 * The current state of the philosopher
	 */
	private State state = State.THINK;

	/**
	 * A rectangle meant to render the state and the location of the philosopher
	 */
	private DrawableRectangle drawableRectangle;
	/**
	 * The rendering of the left fork when handled by the philosopher
	 */
	private Drawable drawableLeftFork;
	/**
	 * The rendering of the right fork when handled by the philosopher
	 */
	private Drawable drawableRightFork;

	/**
	 * Constructor of the philosopher
	 * 
	 * @param id
	 *            the identifier of the philosopher
	 * @param amas
	 *            the corresponding MAS
	 * @param left
	 *            the left fork
	 * @param right
	 *            the right fork
	 */
	public PhilosopherExample(int id, PhilosophersAMASExample amas, ForkExample left, ForkExample right) {
		super(amas, id, left, right);
	}

	@Override
	public void onInitialization() {
		this.id = (int) params[0];
		this.left = (ForkExample) params[1];
		this.right = (ForkExample) params[2];
	}

	@Override
	protected void onRenderingInitialization() {
		double x = 100 * Math.cos(2 * Math.PI * id / this.amas.getEnvironment().getForks().length);
		double y = 100 * Math.sin(2 * Math.PI * id / this.amas.getEnvironment().getForks().length);
		drawableRectangle = VUI.get().createAndAddRectangle(x, y, 20, 20);
		drawableRectangle.setName("Philosopher "+getId());
		drawableLeftFork = VUI.get().createAndAddRectangle(x - 10, y, 5, 20).setColor(Color.BLACK).setStrokeOnly().hide().setShowInExplorer(false);
		drawableRightFork = VUI.get().createAndAddRectangle(x + 10, y, 5, 20).setColor(Color.BLACK).setStrokeOnly().hide().setShowInExplorer(false);

	}

	@Override
	protected void onPerceive() {
		// Nothing goes here as the perception of neighbors criticality is already made
		// by the framework
	}

	@Override
	protected void onDecideAndAct() {
		State nextState = state;
		switch (state) {
		case EATING:
			eatenPastas++;
			if (new Random().nextInt(101) > 50) {
				left.release(this);
				right.release(this);
				nextState = State.THINK;
			}
			break;
		case HUNGRY:
			hungerDuration++;
			if (getMostCriticalNeighbor(true) == this) {
				left.tryTake(this);
				right.tryTake(this);
				if (left.owned(this) && right.owned(this))
					nextState = State.EATING;

			} else {
				left.release(this);
				right.release(this);
			}
			break;
		case THINK:
			if (new Random().nextInt(101) > 50) {
				hungerDuration = 0;
				nextState = State.HUNGRY;
			}
			break;
		default:
			break;

		}

		state = nextState;
	}

	@Override
	protected double computeCriticality() {
		if (state == State.HUNGRY)
			return hungerDuration;
		return -1;
	}

	@Override
	public void onUpdateRender() {
		drawableRectangle.setInfo(
			"Philosopher "+getId()+" :\n"+
			"State : "+state+"\n"+
			"Eaten pastas : "+eatenPastas+"."
		);
		amas.plot.addData("Eaten pasta", id, eatenPastas);
		switch (state) {
		case EATING:
			drawableRectangle.setColor(Color.BLUE);
			break;
		case HUNGRY:
			drawableRectangle.setColor(Color.RED);
			break;
		case THINK:
			drawableRectangle.setColor(Color.GREEN);
			break;

		}
		if (left.owned(this)) {
			drawableLeftFork.show();
		} else {
			drawableLeftFork.hide();
		}
		if (right.owned(this)) {
			drawableRightFork.show();
		} else {
			drawableRightFork.hide();
		}
	}
}
