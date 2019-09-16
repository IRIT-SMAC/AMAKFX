package fr.irit.smac.amak.ui;

import java.util.HashMap;
import java.util.Set;

import fr.irit.smac.amak.Scheduler;
import fr.irit.smac.amak.tools.RunLaterHelper;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Runner to control manually the execution of the mas
 * 
 * @author Alexandre Perles
 *
 */
public class SchedulerToolbar extends VBox {

	/**
	 * The slider which controls the speed
	 */
	private Slider runController;
	
	private Label label;

	/**
	 * The scheduler to which the toolbar is associated
	 */
	private Scheduler scheduler;

	/**
	 * Constructor of the toolbar
	 * 
	 * @param title
	 *            The title of the toolbar
	 * @param scheduler
	 *            The scheduler to which the toolbar is associated
	 * 
	 */
	public SchedulerToolbar(String title, Scheduler scheduler) {
		super();
		label = new Label(title);
		label.setLabelFor(getSlider());
		this.scheduler = scheduler;
		this.scheduler.setOnStop(s -> getSlider().setValue(1));
		this.scheduler.addOnChange(s -> {
			if (s.isRunning()) {
				switch (s.getSleep()) {
				case 1000:
					getSlider().setValue(2);
					break;
				case 100:
					getSlider().setValue(3);
					break;
				case 20:
					getSlider().setValue(4);
					break;
				case 10:
					getSlider().setValue(5);
					break;
				case 2:
					getSlider().setValue(6);
					break;
				case 0:
					getSlider().setValue(7);
					break;
				default:
					getSlider().setValue(1);
				}
			} else {
				getSlider().setValue(1);
			}
		});
		RunLaterHelper.runLater(() -> getChildren().addAll(label, getSlider()));
	}

	/**
	 * Get or create the slider component
	 * 
	 * @return the slider
	 */
	public Slider getSlider() {
		if (runController == null) {
			runController = new Slider(0, 7, 1);
			runController.setOrientation(Orientation.HORIZONTAL);

			final HashMap<Double, Label> labelTable = new HashMap<>();
			labelTable.put(0d, new Label("Step"));
			labelTable.put(1d, new Label("Stop"));
			labelTable.put(2d, new Label("x1"));
			labelTable.put(3d, new Label("x10"));
			labelTable.put(4d, new Label("x50"));
			labelTable.put(5d, new Label("x100"));
			labelTable.put(6d, new Label("x500"));
			labelTable.put(7d, new Label("MAX"));
			
			runController.setLabelFormatter(new StringConverter<Double>() {
				@Override
				public String toString(Double n) {
					return labelTable.get(n).getText();
				}
				
				@Override
				public Double fromString(String string) {
					Set<Double> keySet = labelTable.keySet();
					for (Double d : keySet) {
						if (labelTable.get(d).getText() == string)
							return d;
					}
					
					return null;
				}
			});

			runController.setShowTickMarks(true);
			runController.setShowTickLabels(true);
			runController.setMajorTickUnit(1);
			runController.setPrefWidth(200);
			runController.valueProperty().addListener(l -> {
				switch ((int) runController.getValue()) {
				case 0:
					scheduler.step();
					break;
				case 2:
					scheduler.startWithSleep(1000);
					break;
				case 3:
					scheduler.startWithSleep(100);
					break;
				case 4:
					scheduler.startWithSleep(20);
					break;
				case 5:
					scheduler.startWithSleep(10);
					break;
				case 6:
					scheduler.startWithSleep(2);
					break;
				case 7:
					scheduler.startWithSleep(0);
					break;
				case 1:
				default:
					scheduler.stop();
					break;
				}
			});
		}
		return runController;
	}

}
