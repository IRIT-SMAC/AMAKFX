package fr.irit.smac.amak.ui.drawables;

import javafx.scene.Node;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class DrawableString extends Drawable {
	private Text textZone;

	public DrawableString(double dx, double dy, String text) {
		super(dx+0.5, dy+0.5, 1, 1);
		textZone = new Text(text);
	}

	@Override
	public void _onDraw() {
		textZone.setFill(color);
		textZone.setX(left());
		textZone.setY(top());
	}

	public void setText(String text) {
		textZone.setText(text);
	}

	@Override
	protected void _hide() {
		textZone.setVisible(false);
	}

	@Override
	public void _show() {
		textZone.setVisible(true);
	}

	@Override
	public Node getNode() {
		return textZone;
	}

	@Override
	public ArrayList<Node> getNodes() {
		return null;
	}
}
