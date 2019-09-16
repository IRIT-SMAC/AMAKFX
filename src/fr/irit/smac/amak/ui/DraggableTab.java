package fr.irit.smac.amak.ui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A DraggableTab is a tab that can be drag and dropped to rearrange the tab order in their TabPane. 
 * They can also be dropped outside of their TabPane, in order to put their content in a new window.
 * When closed, this window put the tab back in its original TabPane.
 * @author Hugo
 *
 */
public class DraggableTab extends Tab {
	
	private String title;
	
	private static Tab draggedTab;
	
	public DraggableTab(String title, Node pane) {
		super(title, pane);
		this.title = title;
		setupDragAction();
	}
	
	private void setupDragAction() {
		
		// move text to label graphic:
		// that way we have something that we can drag
        if (this.getText() != null && ! this.getText().isEmpty()) {
            Label label = new Label(this.getText(), this.getGraphic());
            this.setText(null);
            this.setGraphic(label);
        }
		
		Node graphic = this.getGraphic();
		
		setOnDragDetected(graphic);
		
		setOnDragOver(graphic);
		
		setOnDragDropped(graphic);
		
		setOnDragDone(graphic);
	}

	/**
	 * setup callback on the source of the drag action, for when the drag action is finished.
	 * @param graphic
	 */
	private void setOnDragDone(Node graphic) {
		graphic.setOnDragDone(e -> {
			if(draggedTab != null) {
				AnchorPane root = new AnchorPane();
				Scene newScene = new Scene(root, 230, 100);
				root.maxHeightProperty().bind(newScene.heightProperty());
				
				TabPane originalTabPane = this.getTabPane();
				this.getTabPane().getTabs().remove(this);
				Node content = this.getContent();
				root.getChildren().add(content);
				AnchorPane.setTopAnchor(content, 0.0);
				AnchorPane.setBottomAnchor(content, 0.0);
				AnchorPane.setRightAnchor(content, 0.0);
				AnchorPane.setLeftAnchor(content, 0.0);
				 
	            // New window (Stage)
	            Stage newWindow = new Stage();
	            newWindow.setTitle(title);
	            newWindow.setScene(newScene);
	            
	            newWindow.setOnCloseRequest(closeEvent -> {
					originalTabPane.getTabs().add(this);
					newWindow.close();
					closeEvent.consume();
	            });
	            
	            newWindow.show();
	            draggedTab = null;
	            e.consume();
			}
		});
	}

	/**
	 * setup callback for when a tab graphic is the target of a drag, and the drag end.
	 * @param graphic
	 */
	private void setOnDragDropped(Node graphic) {
		graphic.setOnDragDropped(e -> {
			int index = this.getTabPane().getTabs().indexOf(this) ;
            draggedTab.getTabPane().getTabs().remove(draggedTab);
            this.getTabPane().getTabs().add(index, draggedTab);
            draggedTab.getTabPane().getSelectionModel().select(draggedTab);
			draggedTab = null;
		});
	}

	/**
	 * setup callback for when a tab graphic is the target of a drag.
	 * @param graphic
	 */
	private void setOnDragOver(Node graphic) {
		graphic.setOnDragOver(e -> {
			// cannot drag onto itself
			if(draggedTab != null && draggedTab != this) {
				e.acceptTransferModes(TransferMode.MOVE);
			}
		});
	}

	/**
	 * setup callback for when a tab graphic is being dragged.
	 * @param graphic
	 */
	private void setOnDragDetected(Node graphic) {
		graphic.setOnDragDetected(e -> {
			Dragboard dragboard = graphic.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // dragboard must have some content, but we need it to be a Tab, which isn't supported (Tab is not Serializable)
            // So we just put arbitrary content in the dragbaord, and store our tab in a global variable
            
            // create new data format if it does not exist
    		if(DataFormat.lookupMimeType("DraggableTab") == null) {
    			new DataFormat("DraggableTab");
    		}
    		
            content.put(DataFormat.lookupMimeType("DraggableTab"), "This string is not meant to be used");
            dragboard.setContent(content);
            dragboard.setDragView(graphic.snapshot(null, null));
            draggedTab = this;
            e.consume();
		});
	}
}
