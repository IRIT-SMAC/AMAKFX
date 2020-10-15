package fr.irit.smac.amak.ui.drawables;

import fr.irit.smac.amak.tools.RunLaterHelper;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class DrawableRectangle extends Drawable {
	public Rectangle rectangle;

	public DrawableRectangle(double dx, double dy, double width, double height) {
		super(dx+width/2, dy+height/2, width, height);
		rectangle = new Rectangle();
		defaultInit();
	}
	
	@Override
	public Drawable move(double dx, double dy) {
		return super.move(dx+getWidth()/2, dy+getHeight()/2);
	}

	@Override
	public void _onDraw() {
		rectangle.setX(left());
		rectangle.setY(bottom());
		rectangle.setWidth(getRenderedWidth());
		rectangle.setHeight(getRenderedHeight());
		if (strokeMode)
			rectangle.setFill(Color.TRANSPARENT);
		else
			rectangle.setFill(color);
	}

	@Override
	protected void _hide() {
		RunLaterHelper.runLater(() -> vui.getCanvas().getChildren().remove(rectangle));
	}

	@Override
	public void _show() {
		RunLaterHelper.runLater(() -> {
			if(!vui.getCanvas().getChildren().contains(rectangle))
				vui.getCanvas().getChildren().add(rectangle);
		});
	}

	@Override
	public Node getNode() {
		return rectangle;
	}

	@Override
	public ArrayList<Node> getNodes() {
		return null;
	}
}
