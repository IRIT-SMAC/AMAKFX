package fr.irit.smac.amak.ui.drawables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.irit.smac.amak.tools.Log;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class DrawableImage extends Drawable {

	private String filename;
	private ImageView image;
	private static Map<String, Image> loadedImages = new HashMap<>();

	public DrawableImage(double dx, double dy, String filename) {
		super(dx, dy, 0, 0);
		image = new ImageView(new Image(filename));
		this.setFilename(filename);
		defaultInit();
	}

	private Image loadByFilename(String filename) throws NullPointerException, IllegalArgumentException {
		if (!loadedImages.containsKey(filename)) {
			loadedImages.put(filename, new Image(filename));
		}
		return loadedImages.get(filename);
	}

	public void setFilename(String filename) {
		this.filename = filename;
		try {
			image.setImage(loadByFilename(this.filename));
		} catch (NullPointerException | IllegalArgumentException e) {
			Log.defaultLog.error("AMAK", "Can't find/load the file %s", this.filename);
			try {
				image.setImage(loadByFilename("file:Resources/unavailable.png"));
			} catch (NullPointerException | IllegalArgumentException e1) {
				Log.defaultLog.fatal("AMAK", "Can't load resources belonging to AMAK. Bad things may happen.");
			}
		}
		setWidth(this.image.getFitWidth());
		setHeight(this.image.getFitHeight());
	}

	@Override
	public void _onDraw() {
		image.setX(left());
		image.setY(top());
		image.setFitWidth(getRenderedWidth());
		image.setFitHeight(getRenderedHeight());
	}
	
	@Override
	protected void _hide() {
		image.setVisible(false);
	}

	@Override
	public void _show() {
		image.setVisible(true);
	}

	@Override
	public Node getNode() {
		return image;
	}

	@Override
	public ArrayList<Node> getNodes() {
		return null;
	}

	@Override
	protected void onMouseEntered(MouseEvent event) {
		image.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
	}
}
