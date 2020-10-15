package fr.irit.smac.amak.ui.drawables;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class DrawableDefaultMini extends Drawable {
	
	private Drawable original;
	
	protected Rectangle rectangle;
	protected StackPane stack;
	protected Label label;
	
	public DrawableDefaultMini(Drawable original) {
		super(0, 0, 0, 0);
		this.original = original;
		rectangle = new Rectangle();
		rectangle.setStyle(defaultStyle);
		stack = new StackPane();
		label = new Label(original.getName());
		rectangle.widthProperty().bind(label.widthProperty());
		rectangle.heightProperty().bind(label.heightProperty());
		stack.getChildren().addAll(label, rectangle);
		
		original.addLinkedDrawable("mini", this);
		this.addLinkedDrawable("original", original);
		
		defaultInit();
	}
	
	@Override
	protected void defaultInit() {
		getNode().addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dispatchEvent(event);
			}
		});
	}

	@Override
	public void _onDraw() {
		color = new Color(
					original.getColor().getRed(), 
					original.getColor().getGreen(), 
					original.getColor().getBlue(), 
					0.5
				);
		if (strokeMode)
			rectangle.setFill(Color.TRANSPARENT);
		else
			rectangle.setFill(color);
		
		if(isExpanded()) {
			label.setText(original.getInfo());
		} else {
			label.setText(original.getName());
		}
		label.autosize();
		
	}

	@Override
	protected void _hide() {
	}
	
	@Override
	public void _show() {
	}

	@Override
	public Node getNode() {
		return stack;
	}

	@Override
	public ArrayList<Node> getNodes() {
		return null;
	}

	@Override
	protected void onMouseEntered(MouseEvent event) {
		rectangle.setStyle("-fx-stroke: black; -fx-stroke-width: 3;");
	}
	
	@Override
	protected void onMouseExited(MouseEvent event) {
		rectangle.setStyle(defaultStyle);
	}
	
	@Override
	public void expand() {
		super.expand();
		label.setText(original.getInfo());
		onDraw();
	}
	
	@Override
	public void collapse() {
		super.collapse();
		label.setText(original.getName());
		onDraw();
	}
}
