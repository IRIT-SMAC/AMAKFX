package fr.irit.smac.amak.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.InstanceAlreadyExistsException;

import fr.irit.smac.amak.Information;
import fr.irit.smac.amak.tools.RunLaterHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This window is the main one of an AMAS developed using AMAK. It contains a
 * toolbar panel and various spaces for panels
 * 
 * @author of the original version (the Swing one) Alexandre Perles, Marcillaud
 *         Guilhem
 *
 */
public class MainWindow extends Application {
	/**
	 * The window itself
	 */
	public Stage stage;
	/**
	 * The panel which contains the toolbar
	 */
	public ToolBar toolbarPanel;

	/**
	 * The main pane of AMAK
	 */
	public BorderPane organizationPane;
	
	/**
	 * The menu bar of the window
	 */
	public MenuBar menuBar;
	/**
	 * The menus
	 */
	public HashMap<String, Menu> menus = new HashMap<String, Menu>();
	/**
	 * The panel in which panels with tab can be added
	 */
	public TabPane tabbedPanel;
	/**
	 * For an AMAK process it can only be one instance of MainWindow
	 */
	protected static MainWindow instance;
	/**
	 * Lock present to avoid the creation of a MainWindow while another is creating
	 */
	protected static ReentrantLock instanceLock = new ReentrantLock();
	protected static Object startEnded = new Object();

	/**
	 * Create the frame.
	 * 
	 * @throws InstanceAlreadyExistsException
	 *             if the MainWindow has already been instantiated. This constructor
	 *             should be used by the Application of JavaFX only.
	 */
	public MainWindow() throws InstanceAlreadyExistsException {
		super();
		if (instance == null) {
			instance = this;
		} else {
			throw new InstanceAlreadyExistsException();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		synchronized (startEnded) {
			VBox root = new VBox();
			
			// Creation of the menu bar (Top)
			menuBar = new MenuBar();
			root.getChildren().add(menuBar);
			
			// Border organization
			organizationPane = new BorderPane();
			organizationPane.setMinSize(200, 200); //that way we avoid 0 size, which can cause problems
			root.getChildren().add(organizationPane);
			VBox.setVgrow(organizationPane, Priority.ALWAYS);
			
			// Creation of scene
			primaryStage.setTitle("AMAK");
			Scene scene = new Scene(root, 450, 300);
			stage = primaryStage;
			stage.setScene(scene);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					Platform.exit();
				}
			});
	
			// Creation of the toolbar (Bottom)
			toolbarPanel = new ToolBar();
			organizationPane.setBottom(toolbarPanel);
	
			// Creation of the right part of the split pane (Center Right)
			tabbedPanel = new TabPane();
			organizationPane.setCenter(tabbedPanel);
	
			// Creation of the close menu item
			MenuItem menuItem = new MenuItem("Close");
			menuItem.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					System.exit(0);
				}
			});
			addToMenu("Options", menuItem);
	
			menuBar.getMenus().add(new Menu("AMAKFX v" + Information.VERSION));
	
			stage.show();
			
			startEnded.notify();
		}
	}

	/**
	 * Add an action when the JavaFX app close.
	 * 
	 * @param onClose
	 *            The action to be executed when the window is closed
	 */
	public static void addOnCloseAction(Runnable onClose) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { onClose.run(); }
		});
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		System.exit(0);
	}

	/**
	 * Change the icon of the window
	 * 
	 * @param filename
	 *            The filename of the icon
	 */
	public static void setWindowIcon(String filename) {
		RunLaterHelper.runLater(() -> instance().stage.getIcons().add(new Image(filename)));
	}

	/**
	 * Change the title of the main window
	 * 
	 * @param title
	 *            The new title
	 */
	public static void setWindowTitle(String title) {
		RunLaterHelper.runLater(() -> instance().stage.setTitle(title));
	}
	
	/**
	 * Add a button in the menu options
	 * 
	 * @param title
	 *            The title of the button
	 * @param event
	 *            The action to be executed
	 */
	public static void addOptionsItem(String title, EventHandler<ActionEvent> event) {
		MenuItem menuItem = new MenuItem(title);
		menuItem.setOnAction(event);
		RunLaterHelper.runLater(() -> MainWindow.addToMenu("Options", menuItem));
	}

	/**
	 * Add a tool in the toolbar.
	 * 
	 * @param tool
	 */
	public static void addToolbar(Node tool) {
		RunLaterHelper.runLater(() -> instance().toolbarPanel.getItems().add(tool));
	}

	/**
	 * Set a panel to the left
	 * 
	 * @param panel
	 *            The panel
	 */
	public static void setLeftPanel(Node panel) {
		RunLaterHelper.runLater(() -> instance().organizationPane.setLeft(panel));
	}

	/**
	 * Set a panel to the right
	 * 
	 * @param panel
	 *            The panel
	 */
	public static void setRightPanel(Node panel) {
		RunLaterHelper.runLater(() -> instance().organizationPane.setRight(panel));
	}

	/**
	 * Return the unique instance of MainWindow, may create it.
	 * 
	 * @return instance
	 */
	public static MainWindow instance() {
		if(!isInstance()) {
			instanceLock.lock();
			if(!isInstance()) {
				Thread ui = new Thread(new Runnable() {
					@Override
					public void run() {
						Application.launch(MainWindow.class);
					}
				});
				ui.start();
				try {
					synchronized (startEnded) {
						startEnded.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.err.println("Failure at start : Cannot be sure that the MainWindow is correctly launched. Exit.");
					System.exit(1);
				}
			}
			instanceLock.unlock();
		}
		return instance;
	}
	
	/**
	 * Indicate if an instance of MainWindow exist.
	 * 
	 * @return true if an instance of MainWindow exist.
	 */
	public static boolean isInstance() {
		return (instance != null);
	}

	/**
	 * Add a panel with a tab
	 * 
	 * @param title
	 *            The title of the tab
	 * @param panel
	 *            The panel to add
	 */
	public static void addTabbedPanel(String title, Node panel) {
		Tab t = new DraggableTab(title, panel);
		RunLaterHelper.runLater(() -> instance().tabbedPanel.getTabs().add(t));
	}
	
	/**
	 * Add a {@link MenuItem} to a {@link Menu}. May create the menu and add it to the menu bar.
	 * @param menuName the name of the menu where the item will be added.
	 * @param item the item to be added.
	 */
	public static void addToMenu(String menuName, MenuItem item) {
		instance();
		if( !instance.menus.containsKey(menuName) ) {
			Menu m = new Menu(menuName);
			instance.menus.put(menuName,m);
			RunLaterHelper.runLater(() -> instance.menuBar.getMenus().add(m));
		}
		RunLaterHelper.runLater(() -> instance.menus.get(menuName).getItems().add(item));
	}
}