package fr.irit.smac.amak.ui.drawables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.irit.smac.amak.tools.RunLaterHelper;
import fr.irit.smac.amak.ui.VUI;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * A drawable is an object that can be drawn by the {@link VUI} system
 * 
 * @author of original version (the Swing one) Alexandre Perles
 *
 */
public abstract class Drawable {
	
	/**
	 * If this drawable should be shown in the vui explorer.
	 */
	public boolean showInExplorer = true;

	/**
	 * Default style applied to drawable node.
	 */
	protected static String defaultStyle = "-fx-stroke: black; -fx-stroke-width: 1;";
	
	/**
	 * Linked drawables will receive the same event when using dispatchEvent.
	 */
	private HashMap<String, Drawable> linkedDrawable = new HashMap<String, Drawable>();
	
	/**
	 * The horizontal position of the object
	 */
	protected double x;
	/**
	 * The vertical position of the object
	 */
	private double y;
	/**
	 * The width of the object
	 */
	private double width;
	
	/**
	 * The real height
	 */
	protected double height;

	/**
	 * Does only the border must be displayed ?
	 */
	protected boolean strokeMode = false;

	/**
	 * The color of the object
	 */
	protected Color color = Color.BLACK;
	
	/**
	 * The VUI on which the object is drawn
	 */
	protected VUI vui;
	
	/**
	 * The order of drawing. An higher layer is drawn on top of the other.
	 */
	protected int layer = 0;
	
	/**
	 * The angle of rotation of the object
	 */
	private double angle;
	
	/**
	 * A fixed object doesn't move with the view. It can be used for HUD
	 */
	private boolean fixed = false;
	
	/**
	 * Must the object be drawn ?
	 */
	private boolean visible = true;
	
	/**
	 * Is the drawable expanded ?
	 * @see Drawable#onMouseClick(MouseEvent)
	 * @see Drawable#expand()
	 * @see Drawable#collapse()
	 */
	private boolean expanded = false;
	
	/**
	 * If relevant, the name of the drawable, usually it's the name
	 * of the agent represented by this drawable.
	 */
	private String name;
	
	/**
	 * If relevant, additional info on the drawable, usually it's the
	 * state of the agent represented by this drawable.
	 */
	private String info;

	/**
	 * Constructor of the object
	 * 
	 * @param vui
	 *            the VUI on which the object must be drawn
	 * @param dx
	 *            the x real position
	 * @param dy
	 *            the y real position
	 * @param width
	 *            the real width
	 * @param height
	 *            the real height
	 */
	protected Drawable(double dx, double dy, double width, double height) {
		move(dx, dy);
		setWidth(width);
		setHeight(height);
	}
	
	/**
	 * If you wish to use some default settings for your drawable.<br/>
	 * Must be called AFTER the node for your drawable has been created.
	 */
	protected void defaultInit() {
		getNode().setStyle(defaultStyle);
		
		getNode().addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dispatchEvent(event);
			}
		});
	}
	
	/**
	 * Compute the width as it must be displayed on screen. Given the zoom factor,
	 * the width displayed can be different than the real width.
	 * 
	 * @return the width
	 */
	public double getRenderedWidth() {
		if (isFixed())
			return width;
		else
			return vui.worldToScreenDistance(width);
	}

	/**
	 * Set the real width of the object
	 * 
	 * @param width
	 *            The new width
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Compute the height as it must be displayed on screen. Given the zoom factor,
	 * the height displayed can be different than the real height.
	 * 
	 * @return the width
	 */
	public double getRenderedHeight() {
		if (isFixed())
			return height;
		else
			return vui.worldToScreenDistance(height);
	}

	/**
	 * Set the real height of the object
	 * 
	 * @param height
	 *            The new height
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Get the real width
	 * 
	 * @return the real width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Get the real height
	 * 
	 * @return the real height
	 */
	public double getHeight() {
		return height;
	}

	

	/**
	 * Getter for the fixed attribute
	 * 
	 * @return if the obejct is fixed
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * Getter for the angle attribute
	 * 
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * Getter for the layer attribute
	 * 
	 * @return the layer
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * Set the layer and update
	 * 
	 * @param layer
	 *            the new layer
	 * @return the object for chained methods
	 */
	public Drawable setLayer(int layer) {
		this.layer = layer;
		return this;
	}

	/**
	 * Set the new angle
	 * 
	 * @param angle2
	 *            the new angle
	 * @return the object for chained methods
	 */
	public Drawable setAngle(double angle2) {
		this.angle = angle2;
		return this;
	}

	/**
	 * Draw the object if visible and if on screen
	 * 
	 */
	public void onDraw() {
		if (isVisible()) {
			_onDraw();
		}
	}

	/**
	 * Method that must be overrided to draw
	 */
	public abstract void _onDraw();

	/**
	 * Set the associated VUI
	 * 
	 * @param vectorialUI
	 */
	public void setVUI(VUI vectorialUI) {
		vui = vectorialUI;
	}

	/**
	 * Get the top y coordinate
	 * 
	 * @return the top y coordinate
	 */
	public double top() {
		if (isFixed())
			return y - height / 2;
		else
			return vui.worldToScreenY(y - height / 2);
	}

	/**
	 * Get the left x coordinate
	 * 
	 * @return the left x coordinate
	 */
	public double left() {
		if (isFixed())
			return x - width / 2;
		else
			return vui.worldToScreenX(x - width / 2);
	}

	/**
	 * Get the bottom y coordinate
	 * 
	 * @return the bottom y coordinate
	 */
	public double bottom() {
		if (isFixed())
			return y + height / 2;
		else
			return vui.worldToScreenY(y + height / 2);
	}

	/**
	 * Get the right x coordinate
	 * 
	 * @return the right x coordinate
	 */
	public double right() {
		if (isFixed())
			return x + width / 2;
		else
			return vui.worldToScreenX(x + width / 2);
	}

	/**
	 * Only draw the border of the object
	 * 
	 * @return the object for chained methods
	 */
	public Drawable setStrokeOnly() {
		strokeMode = true;
		return this;
	}

	/**
	 * 
	 * @param color
	 * @return the object for chained methods
	 */
	public Drawable setColor(Color color) {
		if (color == this.color)
			return this;
		this.color = color;
		return this;
	}
	
	/**
	 * The color of the drawable.
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * 
	 * @param dx
	 * @param dy
	 * @return the object for chained methods
	 */
	public Drawable move(double dx, double dy) {
		if (x == dx && y == dy)
			return this;
		this.x = dx;
		this.y = dy;
		return this;
	}

	/**
	 * 
	 * @return the object for chained methods
	 */
	public Drawable setFixed() {
		this.fixed = true;
		return this;
	}

	/**
	 * 
	 * @return the object for chained methods
	 */
	public Drawable show() {
		return this.setVisible(true);
	}
	
	protected abstract void _hide();

	/**
	 * 
	 * @return
	 */
	public Drawable hide() {
		_hide();
		return this.setVisible(false);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}
	
	public abstract void _show();

	/**
	 * 
	 * @param visible
	 * @return the object for chained methods
	 */
	public Drawable setVisible(boolean visible) {
		this.visible = visible;
		if (visible)
			_show();
		else
			_hide();
		return this;
	}
	
	/**
	 * The graphical element that is displayed
	 * @return
	 */
	public abstract Node getNode();
	
	/**
	 * Remove the drawable from its VUI
	 */
	public void delete() {
		vui.remove(this);
	}

	/**
	 * Get the linked drawable or null if it does not exist.
	 * @param name name of the linked drawable
	 * @return the linked drawable or null
	 */
	public Drawable getLinkedDrawable(String name) {
		Drawable ret = null;
		if(linkedDrawable.containsKey(name)) {
			ret = linkedDrawable.get(name);
		}
		return ret;
	}
	
	/**
	 * Add a drawable to the list of linked drawables.<br/>
	 * The relation is not symmetrical.
	 * @param name
	 * @param drawable
	 */
	public void addLinkedDrawable(String name, Drawable drawable) {
		linkedDrawable.put(name, drawable);
	}
	
	/**
	 * Return the list of linked drawables. <br/>
	 * Linked drawables will receive the same event when using dispatchEvent.
	 */
	public List<Drawable> getLinkedDrawables(){
		return new ArrayList<Drawable>(linkedDrawable.values());
	}
	
	/**
	 * Used by dispatchEvent. Override if you want to register more event with the dispatchEvent
	 * @param event
	 */
	protected void onEvent(Event event) {
		switch (event.getEventType().getName()) {
		case "MOUSE_CLICKED":
			onMouseClick((MouseEvent)event);
			break;
		case "MOUSE_ENTERED":
			onMouseEntered((MouseEvent)event);
			break;
		case "MOUSE_EXITED":
			onMouseExited((MouseEvent)event);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Called when onEvent receive a MOUSE_EXITED event.
	 * @param event
	 */
	protected void onMouseExited(MouseEvent event) {
		getNode().setStyle(defaultStyle);
	}

	/**
	 * Called when onEvent receive a MOUSE_ENTERED event.
	 * @param event
	 */
	protected void onMouseEntered(MouseEvent event) {
		getNode().setStyle("-fx-stroke: black; -fx-stroke-width: 3;");
	}

	/**
	 * Called when onEvent receive a MOUSE_CLICKED event.
	 * @param event
	 */
	protected void onMouseClick(MouseEvent event) {
		if(expanded) {
			collapse();
		} else {
			expand();
		}
	}

	/**
	 * Dispatch an event to all linked drawable, and this drawable.
	 * @param event
	 */
	public void dispatchEvent(Event event) {
		for(Drawable d : getLinkedDrawables()) {
			d.onEvent(event);
		}
		onEvent(event);
	}
	
	/**
	 * If this drawable should be shown in the vui explorer.
	 */
	public Drawable setShowInExplorer(boolean showInExplorer) {
		this.showInExplorer = showInExplorer;
		return this;
	}
	
	/**
	 * If relevant, the name of the drawable, usually it's the name
	 * of the agent represented by this drawable.
	 */
	public String getName() {
		return name == null ? toString() : name;
	}
	
	/**
	 * If relevant, additional info on the drawable, usually it's the
	 * state of the agent represented by this drawable.
	 */
	public String getInfo() {
		return info == null ? toString() : info;
	}
	
	/**
	 * If relevant, the name of the drawable, usually it's the name
	 * of the agent represented by this drawable.
	 */
	public Drawable setName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * If relevant, additional info on the drawable, usually it's the
	 * state of the agent represented by this drawable.
	 */
	public Drawable setInfo(String info) {
		this.info = info;
		return this;
	}
	
	/**
	 * Action performed if drawable is clicked while collapsed.<br/>
	 * By default do nothing
	 * @see Drawable#collapse()
	 */
	public void expand() {
		expanded = true;
	}
	
	/**
	 * Action performed if drawable is clicked while expanded.
	 * @see Drawable#expand()
	 */
	public void collapse() {
		expanded = false;
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
	/**
	 * Set the drawable on top of all others
	 * @return
	 */
	public Drawable toFront() {
		RunLaterHelper.runLater(()-> getNode().toFront());
		return this;
	}
}
