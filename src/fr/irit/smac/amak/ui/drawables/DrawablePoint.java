package fr.irit.smac.amak.ui.drawables;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;

/**
 * Drawable to point things on the VUI, use a '+' icon as graphical representation.
 * @author Hugo
 *
 */
public class DrawablePoint extends Drawable {

	private SVGPath svg = new SVGPath();
	
	public DrawablePoint(double dx, double dy) {
		super(dx, dy, 0.5, 0.5);
		svg.setContent("M24 10h-10v-10h-4v10h-10v4h10v10h4v-10h10z");
		getNode().addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dispatchEvent(event);
			}
		});
	}

	@Override
	public void _onDraw() {
		svg.setFill(color);
		svg.setScaleX(getRenderedWidth());
		svg.setScaleY(getRenderedHeight());
		// the render has an offset, 10 look like a good value 
		svg.setTranslateX(left()-10);
		svg.setTranslateY(top()-10);
	}

	@Override
	protected void _hide() {
		svg.setVisible(false);
	}

	@Override
	public void _show() {
		svg.setVisible(true);
	}

	@Override
	public Node getNode() {
		return svg;
	}
	
	@Override
	protected void onMouseExited(MouseEvent event) {
		svg.setScaleX(getRenderedWidth());
		svg.setScaleY(getRenderedHeight());
	}

	@Override
	protected void onMouseEntered(MouseEvent event) {
		svg.setScaleX(getRenderedWidth()*1.5);
		svg.setScaleY(getRenderedHeight()*1.5);
	}

}
