package fr.irit.smac.amak.tools;

import javafx.application.Platform;

public class RunLaterHelper {
	/**
	 * Run the runnable on the JavaFX thread if we're not on the javaFX thread.
	 * @param runnable
	 */
	public static void runLater(Runnable runnable) {
		if (Platform.isFxApplicationThread()) {
			runnable.run();
		} else {
			Platform.runLater(runnable);
		}
	}

}
