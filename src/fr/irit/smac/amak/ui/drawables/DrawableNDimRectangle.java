package fr.irit.smac.amak.ui.drawables;

import fr.irit.smac.amak.tools.RunLaterHelper;
import fr.irit.smac.amak.ui.VUI;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.ArrayList;

public class DrawableNDimRectangle extends Drawable {

	public ArrayList<Rectangle> rectangles = new ArrayList<>();
	public ArrayList<Pair<Double,Double>> ranges = new ArrayList<>();
	public ArrayList<Pair<Double,Double>> maxima = new ArrayList<>();


	public DrawableNDimRectangle(int dimensions) {
		super(0, 0, 1, 1);
		for(int i=0;i<dimensions;i++){
			rectangles.add( new Rectangle());
		}
		defaultInit();


	}

	public void setRangesAndMaxima(ArrayList<Pair<Double,Double>> rges, ArrayList<Pair<Double,Double>> max){

		ranges = rges;
		maxima = max;

	}

	@Override
	public Drawable move(double dx, double dy) {
		return super.move(dx+getWidth()/2, dy+getHeight()/2);
	}



	private double positionTransformation(double value, Pair<Double,Double> minMax, double UImax){

		return (value*UImax  - UImax*minMax.getKey())/(minMax.getValue() - minMax.getKey());

		// UImin = 0 --> (UImax*value - UImax*Rmin ) / (Rmax - Rmin)

	}

	private double distanceTransformation(double value, Pair<Double,Double> minMax, double UImax){

		return (value*UImax)/(minMax.getValue() - minMax.getKey());

		// UImin = 0 --> (UImax*value - UImax*Rmin ) / (Rmax - Rmin)

	}

	@Override
	public void _onDraw() {

		if(ranges.size()>0){
			for(int i = 0;i<rectangles.size();i++){

				double left;
				double bottom;
				double width;
				double height;

				width = distanceTransformation(ranges.get(i).getValue()-ranges.get(i).getKey(), maxima.get(i),200);
				height = 40/ranges.size();
				left = positionTransformation(ranges.get(i).getValue(), maxima.get(i),200);
				bottom = (50*3/ranges.size())*(double)i-1;


				if (!isFixed()){
					if(vui != null){
						left = vui.worldToScreenX(left);
						bottom = vui.worldToScreenY(bottom);
						width = vui.worldToScreenDistance(width);
						height = vui.worldToScreenDistance(height);
					}else{
						left = vuiMulti.worldToScreenX(left);
						bottom = vuiMulti.worldToScreenY(bottom);
						width = vuiMulti.worldToScreenDistance(width);
						height = vuiMulti.worldToScreenDistance(height);
					}
				}

				rectangles.get(i).setX(left);
				rectangles.get(i).setY(bottom);
				rectangles.get(i).setWidth(width);
				rectangles.get(i).setHeight(height);




				if (strokeMode)
					rectangles.get(i).setFill(Color.TRANSPARENT);
				else
					rectangles.get(i).setFill(color);
			}
		}






	}

	@Override
	protected void _hide() {
		for(Rectangle rectangle:rectangles){
			if(vui!=null){
				RunLaterHelper.runLater(() -> vui.getCanvas().getChildren().remove(rectangle));
			}else{
				RunLaterHelper.runLater(() -> vuiMulti.getCanvas().getChildren().remove(rectangle));
			}

		}

	}

	@Override
	public void _show() {
		for(Rectangle rectangle:rectangles){
			if(vui!=null){
				RunLaterHelper.runLater(() -> {
					if(!vui.getCanvas().getChildren().contains(rectangle))
						vui.getCanvas().getChildren().add(rectangle);
				});
			}else{
				RunLaterHelper.runLater(() -> {
					if(!vuiMulti.getCanvas().getChildren().contains(rectangle))
						vuiMulti.getCanvas().getChildren().add(rectangle);
				});
			}

		}

	}

	@Override
	public Node getNode() {
		return null;
	}

	public ArrayList<Node> getNodes() {
		ArrayList<Node> nodes = new ArrayList<>();
		for(Rectangle rectangle : rectangles){
			nodes.add(rectangle);
		}
		return nodes;
	}
}
