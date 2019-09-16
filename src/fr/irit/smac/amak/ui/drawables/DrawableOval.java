package fr.irit.smac.amak.ui.drawables;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class DrawableOval extends Drawable {
	Ellipse ellipse;
	
	public DrawableOval(double dx, double dy, double width, double height) {
		super(dx, dy, width, height);
		ellipse = new Ellipse();
		defaultInit();
	}

	@Override
	public void _onDraw() {
		double renderedWidth = getRenderedWidth();
		double renderedHeigth = getRenderedHeight();
		ellipse.setCenterX(left()+renderedWidth/2);
		ellipse.setCenterY(top()+renderedHeigth/2);
		ellipse.setRadiusX(renderedWidth*2);
		ellipse.setRadiusY(renderedHeigth*2);
		if (strokeMode)
			ellipse.setFill(Color.TRANSPARENT);
		else
			ellipse.setFill(color);
	}

	@Override
	protected void _hide() {
		ellipse.setVisible(false);
	}

	@Override
	public void _show() {
		ellipse.setVisible(true);
	}

	@Override
	public Node getNode() {
		return ellipse;
	}
}
