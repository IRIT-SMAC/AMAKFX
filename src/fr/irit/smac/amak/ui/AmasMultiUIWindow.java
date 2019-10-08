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
public class AmasMultiUIWindow extends Stage{
//	/**
//	 * The window itself
//	 */
//	public Stage stage;
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
	 * Create the frame.
	 * 
	 * @throws InstanceAlreadyExistsException
	 *             if the MainWindow has already been instantiated. This constructor
	 *             should be used by the Application of JavaFX only.
	 */
	public AmasMultiUIWindow(String title) {

		RunLaterHelper.runLater(() -> {	
			
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
		this.setTitle(title);
		Scene scene = new Scene(root, 450, 300);
		//stage = primaryStage;
		this.setScene(scene);
		this.setOnCloseRequest(new EventHandler<WindowEvent>() {
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

		this.show();
			
		});	

	}

//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		
//	}

	/**
	 * Add an action when the JavaFX app close.
	 * 
	 * @param onClose
	 *            The action to be executed when the window is closed
	 */
	public void addOnCloseAction(Runnable onClose) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { onClose.run(); }
		});
	}
	
//	@Override
//	public void stop() throws Exception {
//		super.stop();
//		System.exit(0);
//	}

	/**
	 * Change the icon of the window
	 * 
	 * @param filename
	 *            The filename of the icon
	 */
	public  void setWindowIcon(String filename) {
		RunLaterHelper.runLater(() -> this.getIcons().add(new Image(filename)));
	}

	/**
	 * Change the title of the main window
	 * 
	 * @param title
	 *            The new title
	 */
	public void setWindowTitle(String title) {
		RunLaterHelper.runLater(() -> this.setTitle(title));
	}
	
	/**
	 * Add a button in the menu options
	 * 
	 * @param title
	 *            The title of the button
	 * @param event
	 *            The action to be executed
	 */
	public  void addOptionsItem(String title, EventHandler<ActionEvent> event) {
		MenuItem menuItem = new MenuItem(title);
		menuItem.setOnAction(event);
		RunLaterHelper.runLater(() -> addToMenu("Options", menuItem));
	}

	/**
	 * Add a tool in the toolbar.
	 * 
	 * @param tool
	 */
	public  void addToolbar(Node tool) {
		RunLaterHelper.runLater(() -> toolbarPanel.getItems().add(tool));
	}

	/**
	 * Set a panel to the left
	 * 
	 * @param panel
	 *            The panel
	 */
	public void setLeftPanel(Node panel) {
		RunLaterHelper.runLater(() -> organizationPane.setLeft(panel));
	}

	/**
	 * Set a panel to the right
	 * 
	 * @param panel
	 *            The panel
	 */
	public void setRightPanel(Node panel) {
		RunLaterHelper.runLater(() -> organizationPane.setRight(panel));
	}

	/**
	 * Return the unique instance of MainWindow, may create it.
	 * 
	 * @return instance
	 */
	
	


	/**
	 * Add a panel with a tab
	 * 
	 * @param title
	 *            The title of the tab
	 * @param panel
	 *            The panel to add
	 */
	public void addTabbedPanel(String title, Node panel) {
		Tab t = new DraggableTab(title, panel);
		RunLaterHelper.runLater(() -> tabbedPanel.getTabs().add(t));
	}
	
	/**
	 * Add a {@link MenuItem} to a {@link Menu}. May create the menu and add it to the menu bar.
	 * @param menuName the name of the menu where the item will be added.
	 * @param item the item to be added.
	 */
	public void addToMenu(String menuName, MenuItem item) {
		//instance();
		if( !menus.containsKey(menuName) ) {
			Menu m = new Menu(menuName);
			menus.put(menuName,m);
			RunLaterHelper.runLater(() -> menuBar.getMenus().add(m));
		}
		RunLaterHelper.runLater(() -> menus.get(menuName).getItems().add(item));
	}
}