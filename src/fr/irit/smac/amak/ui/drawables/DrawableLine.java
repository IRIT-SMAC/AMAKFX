package fr.irit.smac.amak.ui.drawables;

import fr.irit.smac.amak.Configuration;
import javafx.scene.Node;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class DrawableLine extends Drawable {
	Line line;

	public DrawableLine(double dx, double dy, double tx, double ty) {
		super(0, 0, 0, 0);
		line = new Line(dx, dy, tx, ty);
		defaultInit();

	}

	public void move(double dx, double dy, double tx, double ty) {
		line.setStartX(dx);
		line.setStartY(dy);
		line.setEndX(tx);
		line.setEndY(ty);
	}

	@Override
	public void _onDraw() {
		line.setFill(color);
		if (!isFixed()) {
			if(Configuration.multiUI){
				line.setStartX(vuiMulti.worldToScreenX(line.getStartX()));
				line.setStartY(vuiMulti.worldToScreenY(line.getStartY()));
				line.setEndX(vuiMulti.worldToScreenX(line.getEndX()));
				line.setEndY(vuiMulti.worldToScreenY(line.getEndY()));
			}else{
				line.setStartX(vui.worldToScreenX(line.getStartX()));
				line.setStartY(vui.worldToScreenY(line.getStartY()));
				line.setEndX(vui.worldToScreenX(line.getEndX()));
				line.setEndY(vui.worldToScreenY(line.getEndY()));
			}

		}
	}



	@Override
	protected void _hide() {
		line.setVisible(false);
	}

	@Override
	public void _show() {
		line.setVisible(true);
	}

	@Override
	public Node getNode() {
		return line;
	}

	@Override
	public ArrayList<Node> getNodes() {
		return null;
	}
}
