package fr.irit.smac.amak.ui;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import fr.irit.smac.amak.tools.RunLaterHelper;
import fr.irit.smac.amak.ui.drawables.Drawable;
import fr.irit.smac.amak.ui.drawables.DrawableDefaultMini;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * A piece of GUI allowing to see and look for agents.
 * @author Hugo
 *
 */
public class VuiExplorer extends ScrollPane {

	private VUI vui = null;
	private VUIMulti vuiMulti = null;
	
	private VBox vbox;
	private TitledPane contextsPane;
	private VBox cpVBox;
	private TextField search;
	private CheckBox autoRefresh;

	public VuiExplorer(VUI vui) {
		this.vui = vui;

		this.setMaxWidth(Double.MAX_VALUE);
		this.setMaxHeight(Double.MAX_VALUE);

		vbox = new VBox();
		vbox.setFillWidth(true);
		this.setContent(vbox);

		// refresh, close, and collapseAll button
		HBox hboxButtons = new HBox();
		Button refresh = new Button("Refresh");
		refresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				update();
			}
		});
		Button close = new Button("Close");
		close.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				vui.getPanel().setLeft(null);
			}
		});
		Button collapseAll = new Button("Collapse all");
		collapseAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				collapseAll();
			}
		});
		hboxButtons.getChildren().addAll(refresh, close, collapseAll);
		
		// check box
		autoRefresh = new CheckBox("Auto refresh");
		autoRefresh.setTooltip(new Tooltip("Try to automatically refresh the VUI explorer when the VUI is updated."));
		
		// search bar
		search = new TextField();
		search.setPromptText("regular expression");
		// update list on change
		search.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				search.setStyle(null);
				try {
					update();
				} catch (PatternSyntaxException ex) {
					search.setStyle("-fx-border-color: red;");
				}
			}
		});

		cpVBox = new VBox();
		contextsPane = new TitledPane("Drawables", cpVBox);

		vbox.getChildren().addAll(hboxButtons, autoRefresh, search, contextsPane);
		update();
		
		// Add to vui
		RunLaterHelper.runLater(()->vui.getPanel().setLeft(this));

	}
	
	
	public VuiExplorer(VUIMulti vuiMlt) {
		this.vuiMulti = vuiMlt;

		this.setMaxWidth(Double.MAX_VALUE);
		this.setMaxHeight(Double.MAX_VALUE);

		vbox = new VBox();
		vbox.setFillWidth(true);
		this.setContent(vbox);

		// refresh, close, and collapseAll button
		HBox hboxButtons = new HBox();
		Button refresh = new Button("Refresh");
		refresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				update();
			}
		});
		Button close = new Button("Close");
		close.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				vuiMulti.getPanel().setLeft(null);
			}
		});
		Button collapseAll = new Button("Collapse all");
		collapseAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				collapseAll();
			}
		});
		hboxButtons.getChildren().addAll(refresh, close, collapseAll);
		
		// check box
		autoRefresh = new CheckBox("Auto refresh");
		autoRefresh.setTooltip(new Tooltip("Try to automatically refresh the VUI explorer when the VUI is updated."));
		
		// search bar
		search = new TextField();
		search.setPromptText("regular expression");
		// update list on change
		search.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				search.setStyle(null);
				try {
					update();
				} catch (PatternSyntaxException ex) {
					search.setStyle("-fx-border-color: red;");
				}
			}
		});

		cpVBox = new VBox();
		contextsPane = new TitledPane("Drawables", cpVBox);

		vbox.getChildren().addAll(hboxButtons, autoRefresh, search, contextsPane);
		update();
		
		// Add to vui
		RunLaterHelper.runLater(()->vuiMulti.getPanel().setLeft(this));

	}
	

	public void update(boolean auto) {
		if(auto && autoRefresh.isSelected()) {
			update();
		}
	}
	
	/**
	 * Update the list of context
	 */
	public void update() {
		List<Drawable> drawableList = null;
		if(vui != null) {
			drawableList = vui.getDrawables();
		}
		if(vuiMulti != null) {
			drawableList = vuiMulti.getDrawables();
		}
		// crude color sort
		drawableList.sort(new Comparator<Drawable>() {
			@Override
			public int compare(Drawable o1, Drawable o2) {
				Color c1 = (o1.getColor());
				Color c2 = (o2.getColor());
				double score1 = c1.getRed()*100 + c1.getGreen()*10 + c1.getBlue();
				double score2 = c2.getRed()*100 + c2.getGreen()*10 + c2.getBlue();
				return (int) ((score1 - score2)*10);
			}
		});
		cpVBox.getChildren().clear();
		Pattern p = Pattern.compile(search.getText());
		for(Drawable d : drawableList) {
			if(d.showInExplorer && d.isVisible()) {
				if(p.matcher(d.getInfo()).find()) {
					Drawable mini = d.getLinkedDrawable("mini");
					if(mini == null) {
						mini = new DrawableDefaultMini(d);
					}
					cpVBox.getChildren().add(mini.getNode());
					mini.onDraw();
				}
			}
		}
	}
	
	private void collapseAll() {
		List<Drawable> drawableList = null;
		if(vui != null) {
			drawableList = vui.getDrawables();
		}
		if(vuiMulti != null) {
			drawableList = vuiMulti.getDrawables();
		}
		for(Drawable d : drawableList) {
			if(d.showInExplorer && d.isVisible()) {
				Drawable mini = d.getLinkedDrawable("mini");
				if(mini == null) {
					mini = new DrawableDefaultMini(d);
				}
				mini.collapse();
				mini.onDraw();
			}
		}
	}
}
