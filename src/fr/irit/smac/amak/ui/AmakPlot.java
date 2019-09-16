package fr.irit.smac.amak.ui;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import fr.irit.smac.amak.Configuration;
import fr.irit.smac.amak.tools.RunLaterHelper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * A convenient class to easily create and edit JFreeChart plot in AMAK.
 * @author Hugo
 *
 */
public class AmakPlot {
	
	public enum ChartType {LINE, BAR;}
	
	/* STATIC */
	/**
	 * False by default. Improve performance of charts in big dataset (>5000).
	 * At the price a lower quality and fidelity.
	 * @see org.jfree.chart.renderer.xy.SamplingXYLineRenderer
	 */
	public static boolean useSamplingRenderer = false;
	
	public static void add(AmakPlot chart) {
		MainWindow.addTabbedPanel(chart.name, new ChartViewer(chart.chart));
	}
	/* ----- */
	
	private String name;
	private XYSeriesCollection seriesCollection;
	private JFreeChart chart;
	
	/**
	 * Whether or not a delayed notification for update has been sent.
	 */
	private boolean notifySent = false;
	
	/**
	 * Create a chart
	 * @param name the name of the chart, used as the tab name.
	 * @param chartType {@link ChartType#LINE} or {@link ChartType#BAR}
	 * @param xAxisLabel label for the x (horizontal) axis 
	 * @param yAxisLabel label for the y (vertical) axis
	 * @param autoAdd automatically make an {@link AmakPlot#add(AmakPlot)} call ?
	 */
	public AmakPlot(String name, ChartType chartType, String xAxisLabel, String yAxisLabel, boolean autoAdd) {
		this.name = name;
		seriesCollection = new XYSeriesCollection();
		switch (chartType) {
		case BAR:
			chart = ChartFactory.createXYBarChart(name, xAxisLabel, false, yAxisLabel, seriesCollection);
			break;
		case LINE:
			chart = ChartFactory.createXYLineChart(name, xAxisLabel, yAxisLabel, seriesCollection);
			if(useSamplingRenderer) {
				chart.getXYPlot().setRenderer(new SamplingXYLineRenderer());
			}
			XYPlot plot = (XYPlot)chart.getPlot();
			plot.setDomainGridlinesVisible(true);
	        plot.setDomainGridlinePaint(Color.lightGray);
	        plot.setRangeGridlinePaint(Color.lightGray);
			break;
		default:
			System.err.println("AmakPlot : unknow ChartType \""+chartType+"\".");
			break;
		}
		chart.setAntiAlias(false);
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		if(autoAdd) {
			add(this);
		}
	}
	
	/**
	 * Create a chart and add it to the main window.
	 * @param name the name of the chart, used as the tab name.
	 * @param chartType {@link ChartType#LINE} or {@link ChartType#BAR}
	 * @param xAxisLabel label for the x (horizontal) axis 
	 * @param yAxisLabel label for the y (vertical) axis
	 */
	public AmakPlot(String name, ChartType chartType, String xAxisLabel, String yAxisLabel) {
		this(name, chartType, xAxisLabel, yAxisLabel, true);
	}
	
	
	/**
	 * Create a chart out of a JFreeChart.
	 * Make sure that your chart use an {@link XYSeriesCollection} as dataset.
	 * @param name the name of the chart, used as the tab name.
	 * @param chart the {@link JFreeChart} using a {@link XYSeriesCollection} for dataset.
	 * @param autoAdd automatically make an {@link AmakPlot#add(AmakPlot)} call ?
	 */
	public AmakPlot(String name, JFreeChart chart, boolean autoAdd) {
		this.name = name;
		this.seriesCollection = (XYSeriesCollection) chart.getXYPlot().getDataset();
		this.chart = chart;
		add(this);
	}
	
	/**
	 * Create a chart out of a JFreeChart and add it to the main window.
	 * Make sure that your chart use an {@link XYSeriesCollection} as dataset.
	 * @param name the name of the chart, used as the tab name.
	 * @param chart the {@link JFreeChart} using a {@link XYSeriesCollection} for dataset.
	 */
	public AmakPlot(String name, JFreeChart chart) {
		this(name, chart, true);
	}
	
	public String getName() {
		return name;
	}
	
	public XYSeriesCollection getSeriesCollection() {
		return seriesCollection;
	}
	
	public JFreeChart getChart() {
		return chart;
	}
	
	synchronized private void resetNotifySent() {
		notifySent = false;
	}
	
	synchronized private void setNotifySent() {
		notifySent = true;
	}
	
	synchronized private boolean getNotifySent() {
		return notifySent;
	}
	
	/**
	 * Add or update a data point
	 * There might be a slight delay before updating the GUI, to improve performance at high speed.
	 * @param seriesName
	 * @param x horizontal axis, if already exist, will be overwritten 
	 * @param y vertical axis
	 * @param notify if true will update the GUI.
	 */
	public void addData(Comparable seriesName, Number x, Number y, boolean notify) {
		if(notify) {
			seriesCollection.setNotify(false);
			// Only update chart every 500ms if needed
			if(!getNotifySent()) {
				setNotifySent();
				Timeline tl = new Timeline(new KeyFrame(
						Duration.millis(Configuration.plotMilliSecondsUpdate), 
						ae -> {seriesCollection.setNotify(true);; resetNotifySent();}));
				tl.play();
			}
			
			RunLaterHelper.runLater(() -> {
				if(seriesCollection.getSeriesIndex(seriesName) == -1) {
					seriesCollection.addSeries(new XYSeries(seriesName));
				}
				seriesCollection.getSeries(seriesName).addOrUpdate(x, y);
			});
		} else {
			seriesCollection.setNotify(false);
			if(seriesCollection.getSeriesIndex(seriesName) == -1) {
				seriesCollection.addSeries(new XYSeries(seriesName));
			}
			seriesCollection.getSeries(seriesName).addOrUpdate(x, y);
		}
	}
	
	/**
	 * Add or update a data point
	 * There might be a slight delay before updating the GUI, to improve performance at high speed.
	 * @param seriesName
	 * @param x horizontal axis, if already exist, will be overwritten 
	 * @param y vertical axis
	 */
	public void addData(Comparable seriesName, Number x, Number y) {
		addData(seriesName, x, y, true);
	}
}
