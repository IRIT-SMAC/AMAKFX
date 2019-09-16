package fr.irit.smac.amak.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import fr.irit.smac.amak.tools.RunLaterHelper;
import fr.irit.smac.amak.ui.drawables.Drawable;
import fr.irit.smac.amak.ui.drawables.DrawableImage;
import fr.irit.smac.amak.ui.drawables.DrawablePoint;
import fr.irit.smac.amak.ui.drawables.DrawableRectangle;
import fr.irit.smac.amak.ui.drawables.DrawableString;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

/**
 * 
 * Vectorial UI: This class allows to create dynamic rendering with zoom and
 * move capacities
 * 
 * @author of original version (the Swing one) perles
 *
 */
public class VUI {
	/**
	 * The toolbar of the VUI.
	 */
	public ToolBar toolbar;
	
	/**
	 * The VUI explorer.
	 * @see VuiExplorer
	 */
	private VuiExplorer vuiExplorer;
	
	/**
	 * List of objects currently being drawn by the VUI
	 */
	private List<Drawable> drawables = new LinkedList<>();
	/**
	 * Lock to avoid concurrent modification on the list {@link #drawables}
	 */
	private ReentrantLock drawablesLock = new ReentrantLock();

	/**
	 * A static map to facilitate access to different instances of VUI
	 */
	private static Map<String, VUI> instances = new HashMap<>();

	/**
	 * The horizontal offset of the drawing zone. Used to allow the user to move the
	 * view.
	 */
	private double worldOffsetX;

	/**
	 * The vertical offset of the drawing zone. Used to allow the user to move the
	 * view.
	 */
	private double worldOffsetY;

	/**
	 * The last horizontal position of the mouse when dragging
	 */
	protected Double lastDragX;

	/**
	 * The last vertical position of the mouse when dragging
	 */
	protected Double lastDragY;

	/**
	 * The main panel of the VUI
	 */
	private BorderPane panel;

	/**
	 * The canvas on which all is drawn
	 */
	private Pane canvas;

	/**
	 * Label aiming at showing information about the VUI (zoom and offset)
	 */
	private Label statusLabel;

	/**
	 * The default value of the {@link #zoom}
	 */
	private double defaultZoom = 100;
	/**
	 * The default horizontal position of the view
	 */
	private double defaultWorldCenterX = 0;
	/**
	 * The default vertical position of the view
	 */
	private double defaultWorldCenterY = 0;
	/**
	 * The value of the zoom. 100 means 1/1 scale
	 */
	protected double zoom = defaultZoom;

	/**
	 * The horizontal position of the view
	 */
	private double worldCenterX = defaultWorldCenterX;

	/**
	 * The vertical position of the view
	 */
	private double worldCenterY = defaultWorldCenterY;

	/**
	 * Used to be sure that only one thread at the same time create a VUI
	 */
	private static ReentrantLock instanceLock = new ReentrantLock();

	/**
	 * Get the default VUI
	 * 
	 * @return the default VUI
	 */
	public static VUI get() {
		if(!instances.containsKey("Default"))
			MainWindow.addTabbedPanel("Default VUI", get("Default").getPanel());
		return get("Default");
	}

	/**
	 * Create or get a VUI.<br/>
	 * You have add its panel to the MainWindow yourself.
	 * 
	 * @param id
	 *            The unique id of the VUI
	 * @return The VUI with id "id"
	 */
	public static VUI get(String id) {
		instanceLock.lock();
		if (!instances.containsKey(id)) {
			VUI value = new VUI(id);
			instances.put(id, value);
			instanceLock.unlock();
			return value;
		}
		instanceLock.unlock();
		return instances.get(id);
	}

	/**
	 * Constructor of the VUI. This one is private as it can only be created through
	 * static method.
	 * 
	 * @param title
	 *            The title used for the vui
	 */
	private VUI(String title) {
		Semaphore done = new Semaphore(0);
		RunLaterHelper.runLater(() -> {
			panel = new BorderPane();

			toolbar = new ToolBar();
			statusLabel = new Label("status");
			statusLabel.setTextAlignment(TextAlignment.LEFT);
			toolbar.getItems().add(statusLabel);
			panel.setBottom(toolbar);

			Button resetButton = new Button("Reset");
			resetButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					zoom = defaultZoom;
					worldCenterX = defaultWorldCenterX;
					worldCenterY = defaultWorldCenterY;
					updateCanvas();
				}
			});
			toolbar.getItems().add(resetButton);

			canvas = new Pane();
			canvas.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
			// clip the canvas (avoid drawing outside of it)
			Rectangle clip = new Rectangle(0, 0, 0, 0);
			clip.widthProperty().bind(canvas.widthProperty());
			clip.heightProperty().bind(canvas.heightProperty());
			canvas.setClip(clip);
			
			canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					lastDragX = event.getX();
					lastDragY = event.getY();
				}
			});
			canvas.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					lastDragX = null;
					lastDragY = null;
				}
			});
			canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					try {
						double transX = screenToWorldDistance(event.getX() - lastDragX);
						double transY = screenToWorldDistance(event.getY() - lastDragY);
						worldCenterX += transX;
						worldCenterY += transY;
						worldOffsetX += transX;
						worldOffsetY += transY;
						lastDragX = event.getX();
						lastDragY = event.getY();
						updateCanvas();
					} catch (Exception ez) {
						// Catch exception occurring when mouse is out of the canvas
					}
				}
			});

			canvas.setOnScroll(new EventHandler<ScrollEvent>() {
				@Override
				public void handle(ScrollEvent event) {
					double wdx = screenToWorldDistance(canvas.getWidth() / 2 - event.getX());
					double wdy = screenToWorldDistance(canvas.getHeight() / 2 - event.getY());
					zoom += event.getDeltaY() / event.getMultiplierY() * 10;
					if (zoom < 10)
						zoom = 10;

					double wdx2 = screenToWorldDistance(canvas.getWidth() / 2 - event.getX());
					double wdy2 = screenToWorldDistance(canvas.getHeight() / 2 - event.getY());
					worldCenterX -= wdx2 - wdx;
					worldCenterY -= wdy2 - wdy;
					updateCanvas();
				}
			});

			panel.setCenter(canvas);
			
			//add VuiExplorer
			vuiExplorer = new VuiExplorer(this);
			panel.setLeft(vuiExplorer);
			Button veButton = new Button("VUI explorer");
			veButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					panel.setLeft(vuiExplorer);
				}
			});
			veButton.setTooltip(new Tooltip("Show the VUI explorer if it was hidden."));
			toolbar.getItems().add(veButton);
			
			done.release();
		});
		try {
			done.acquire();
		} catch (InterruptedException e) {
			System.err.println("Failed to make sure that the VUI is correctly initialized.");
			e.printStackTrace();
		}
	}

	/**
	 * Convert a distance in the world to its equivalent on the screen
	 * 
	 * @param d
	 *            the in world distance
	 * @return the on screen distance
	 */
	public double worldToScreenDistance(double d) {
		return d * getZoomFactor();
	}

	/**
	 * Convert a distance on the screen to its equivalent in the world
	 * 
	 * @param d
	 *            the on screen distance
	 * @return the in world distance
	 */
	public double screenToWorldDistance(double d) {
		return d / getZoomFactor();
	}

	/**
	 * Convert a X in the world to its equivalent on the screen
	 * 
	 * @param x
	 *            the X in world
	 *
	 * @return the X on screen distance
	 */
	public double worldToScreenX(double x) {
		return (x + getWorldOffsetX()) * getZoomFactor();
	}

	/**
	 * A value that must be multiplied to scale objects
	 * 
	 * @return the zoom factor
	 */
	public double getZoomFactor() {
		return zoom / 100;
	}

	/**
	 * Convert a Y in the world to its equivalent on the screen
	 * 
	 * @param y
	 *            the Y in world
	 *
	 * @return the Y on screen distance
	 */
	public double worldToScreenY(double y) {
		return (-y + getWorldOffsetY()) * getZoomFactor();
	}

	/**
	 * Convert a X on the screen to its equivalent in the world
	 * 
	 * @param x
	 *            the X on screen
	 *
	 * @return the X in the world distance
	 */
	public double screenToWorldX(double x) {
		return x / getZoomFactor() - getWorldOffsetX();
	}

	/**
	 * Convert a Y on the screen to its equivalent in the world
	 * 
	 * @param y
	 *            the Y on screen
	 *
	 * @return the Y in the world distance
	 */
	public double screenToWorldY(double y) {
		return -y / getZoomFactor() + getWorldOffsetY();
	}

	/**
	 * Add a drawable to the VUI.
	 * 
	 * @param d
	 *            the new drawable
	 */
	public void add(Drawable d) {
		d.setVUI(this);
		RunLaterHelper.runLater(()-> canvas.getChildren().add(d.getNode()));
		drawablesLock.lock();
		drawables.add(d);
		drawablesLock.unlock();
		updateCanvas();
	}
	
	/**
	 * Remove a drawable from the VUI.
	 * 
	 * @param d
	 *            the new drawable
	 */
	public void remove(Drawable d) {
		drawablesLock.lock();
		drawables.remove(d);
		drawablesLock.unlock();
		RunLaterHelper.runLater(()-> canvas.getChildren().remove(d.getNode()));
		updateCanvas();
	}
	
	/**
	 * Remove all drawables from the VUI.
	 */
	public void clear() {
		drawablesLock.lock();
		drawables.clear();
		RunLaterHelper.runLater(()->canvas.getChildren().clear());
		drawablesLock.unlock();
	}

	/**
	 * Refresh the canvas
	 */
	public void updateCanvas() {
		final double w = canvas.getWidth();
		final double h = canvas.getHeight();

		setWorldOffsetX(worldCenterX + screenToWorldDistance(w / 2));
		setWorldOffsetY(worldCenterY + screenToWorldDistance(h / 2));

		drawablesLock.lock();
		Collections.sort(drawables, (o1, o2) -> o1.getLayer() - o2.getLayer());
		for (Drawable d : drawables)
			RunLaterHelper.runLater(()-> d.onDraw());
		drawablesLock.unlock();

		RunLaterHelper.runLater(() -> {
			statusLabel.setText(String.format("Zoom: %.2f Center: (%.2f,%.2f)", zoom, worldCenterX, worldCenterY));
		});
		
		RunLaterHelper.runLater(()-> vuiExplorer.update(true));
	}

	/**
	 * Get the width of the canvas
	 * 
	 * @return the canvas width
	 */
	public double getCanvasWidth() {
		return canvas.getWidth();
	}

	/**
	 * Get the height of the canvas
	 * 
	 * @return the canvas height
	 */
	public double getCanvasHeight() {
		return canvas.getHeight();
	}

	/**
	 * Get the value that must be added to the X coordinate of in world object
	 * 
	 * @return the X offset
	 */
	public double getWorldOffsetX() {
		return worldOffsetX;
	}

	/**
	 * Set the value that must be added to the X coordinate of in world object
	 * 
	 * @param offsetX
	 *            the X offset
	 */
	public void setWorldOffsetX(double offsetX) {
		this.worldOffsetX = offsetX;
	}

	/**
	 * Get the value that must be added to the Y coordinate of in world object
	 * 
	 * @return the Y offset
	 */
	public double getWorldOffsetY() {
		return worldOffsetY;
	}

	/**
	 * Set the value that must be added to the Y coordinate of in world object
	 * 
	 * @param offsetY
	 *            the Y offset
	 */
	public void setWorldOffsetY(double offsetY) {
		this.worldOffsetY = offsetY;
	}

	/**
	 * Create a point and start rendering it
	 * 
	 * @param dx
	 *            the x coordinate
	 * @param dy
	 *            the y coordinate
	 * @return the point object
	 */
	public DrawablePoint createAndAddPoint(double dx, double dy) {
		DrawablePoint drawablePoint = new DrawablePoint(dx, dy);
		add(drawablePoint);
		return drawablePoint;
	}

	/**
	 * Create a rectangle and start rendering it
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 * @return the rectangle object
	 */
	public DrawableRectangle createAndAddRectangle(double x, double y, double w, double h) {
		DrawableRectangle d = new DrawableRectangle(x, y, w, h);
		add(d);
		return d;
	}

	/**
	 * Set the default configuration of the view
	 * 
	 * @param zoom
	 *            the initial zoom value
	 * @param worldCenterX
	 *            the initial X center value
	 * @param worldCenterY
	 *            the initial Y center value
	 */
	public void setDefaultView(double zoom, double worldCenterX, double worldCenterY) {
		this.zoom = zoom;
		this.worldCenterX = worldCenterX;
		this.worldCenterY = worldCenterY;
		this.defaultZoom = zoom;
		this.defaultWorldCenterX = worldCenterX;
		this.defaultWorldCenterY = worldCenterY;
	}

	/**
	 * Create an image and start rendering it
	 * 
	 * @param dx
	 *            the x coordinate
	 * @param dy
	 *            the y coordinate
	 * @param filename
	 *            the filename of the image
	 * @return the created image
	 */
	public DrawableImage createAndAddImage(double dx, double dy, String filename) {
		DrawableImage image = new DrawableImage(dx, dy, filename);
		add(image);
		return image;
	}

	/**
	 * Create a string and start rendering it
	 * 
	 * @param dx
	 *            the x coordinate
	 * @param dy
	 *            the y coordinate
	 * @param text
	 *            the text to display
	 * @return the created string
	 */
	public DrawableString createAndAddString(int dx, int dy, String text) {
		DrawableString ds = new DrawableString(dx, dy, text);
		add(ds);
		return ds;
	}

	public Pane getCanvas() {
		return canvas;
	}
	
	public BorderPane getPanel() {
		return panel;
	}
	
	public List<Drawable> getDrawables() {
		return drawables;
	}
}
