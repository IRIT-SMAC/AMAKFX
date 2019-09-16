package fr.irit.smac.amak;

import fr.irit.smac.amak.Amas.ExecutionPolicy;

/**
 * This class is used to define global configuration BEFORE calling any other
 * classes/methods of the framework.
 * 
 * @author Alexandre Perles
 *
 */
public class Configuration {
	/**
	 * The maximal number of threads that can be executed simultaneously
	 */
	public static int allowedSimultaneousAgentsExecution = 1;
	/**
	 * The execution policy refers to the synchronization between agents execution.
	 * ONE_PHASE means that agents can realize simultaneous cycles and wait for each
	 * other after the action. TWO_PHASES means that agents perceive simultaneously
	 * then wait for each others and then decide and act simultaneously and finally
	 * wait for each others.
	 */
	public static ExecutionPolicy executionPolicy = ExecutionPolicy.ONE_PHASE;

	/**
	 * By default, mas made with amak are meant to be executed on a graphical
	 * environment. However, some may need to execute a mas without any GUI. To
	 * guarantee the well functioning of this option, you have to check that you
	 * only render stuff in methods such as onUpdaterRender
	 * onRenderingInitialization... Also, do not forget to set your schedulers to
	 * HIDDEN or DEFAULT
	 */
	public static boolean commandLineMode = false;
	
	/**
	 * By default AMAK will wait for all graphical update to finish before
	 * moving on to the next cycle. This will likely slow down your amas, but 
	 * you'll be able to see it in real time.
	 * If you set it to false, you have to make sure that the GUI can keep up,
	 * or else you may experience freeze on the GUI.
	 */
	public static boolean waitForGUI = true;
	
	
	/**
	 * By default AMAK will wait for 1 sec before updating the plots
	 */
	public static double plotMilliSecondsUpdate = 1000;
}
