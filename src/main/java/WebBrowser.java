import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;

public class WebBrowser extends BorderPane {
      
    private final WebView webView;
    private final WebEngine webEngine;
    private final ComboBox<String> addressBox;
    private final TextField searchField;
    private final Button backButton;
    private final Button forwardButton;
    private final Button proceedButton;
    private final Button searchButton;
    private final ProgressBar progressBar;
    private final Label statusLabel;
    
    public WebBrowser(final Dimension2D dimension) {
        
        this.setMinSize(dimension.getWidth(), dimension.getHeight());
        this.setPrefSize(dimension.getWidth(), dimension.getHeight());
        
        backButton = new Button("\uD83E\uDC78");
        backButton.setOnAction(this::backButtonListener);
        
        forwardButton = new Button("\u2794");
        forwardButton.setDefaultButton(true);
        forwardButton.setOnAction(this::forwardButtonListener);
        
        proceedButton = new Button("Go");
        proceedButton.setOnAction(this::proceedButtonListener);
        
        final HBox buttonGroup = new HBox();
        buttonGroup.setSpacing(5);
        buttonGroup.setPadding(new Insets(10, 5, 10, 5));
        buttonGroup.getChildren().addAll(backButton, forwardButton, proceedButton);
        
        addressBox = new ComboBox<>();
        addressBox.setItems(FXCollections.observableArrayList());
        addressBox.setValue("http://www.google.com");
        addressBox.setOnAction(this::proceedButtonListener);
        addressBox.setEditable(true);
        addressBox.setMaxWidth(Double.MAX_VALUE);
        
        searchField = new TextField();
        searchField.setPromptText("\uD83D\uDD0D Search");
        
        searchButton = new Button("\uD83D\uDD0D");
        searchButton.setDefaultButton(true);
        searchButton.setOnAction(this::searchButtonListener);
        
        statusLabel = new Label("Status: ");
        progressBar = new ProgressBar(0);
        
        webView = new WebView();
        webEngine = webView.getEngine();
     //   webEngine.setUserStyleSheetLocation(getClass().getResource("/style/style.css").toString());
        webEngine.load(addressBox.getValue());
        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
        webEngine.getLoadWorker().stateProperty().addListener(this::stateChangeListener);
      //  webEngine.locationProperty().addListener(this::urlChangeListener);
        
        final WebHistory history = webEngine.getHistory();
        history.getEntries().addListener(this::historyListener);
        
        final GridPane root = new GridPane();
        root.getStyleClass().add("pane");
        GridPane.setConstraints(buttonGroup,  0, 0, 1, 1, HPos.LEFT,   VPos.CENTER, Priority.NEVER,  Priority.NEVER);
        GridPane.setConstraints(addressBox,   1, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(searchField,  2, 0, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.NEVER,  Priority.NEVER);
        GridPane.setConstraints(searchButton, 3, 0, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.NEVER,  Priority.NEVER);
        GridPane.setConstraints(webView,      0, 1, 4, 1, HPos.LEFT,   VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(statusLabel,  0, 2, 1, 1, HPos.LEFT,   VPos.CENTER, Priority.NEVER,  Priority.NEVER);
        GridPane.setConstraints(progressBar,  3, 2, 3, 1, HPos.RIGHT,  VPos.CENTER, Priority.NEVER,  Priority.NEVER);
        
        GridPane.setMargin(addressBox,   new Insets(5, 0, 5, 0));
        GridPane.setMargin(searchField,  new Insets(5, 5, 5, 5));
        GridPane.setMargin(searchButton, new Insets(5, 8, 5, 0));
        GridPane.setMargin(statusLabel,  new Insets(5, 0, 5, 5));
        GridPane.setMargin(progressBar,  new Insets(5, 5, 5, 5));
        
        root.addRow(0, buttonGroup, addressBox, searchField, searchButton);
        root.addRow(1, webView);
        root.addRow(2,statusLabel, progressBar);
        
        this.getStyleClass().add("pane");
        this.setCenter(root);
    }
    
    public void historyListener(Change<? extends Entry> changeValue) {
        changeValue.next();
        for (Entry entry : changeValue.getRemoved()) {
            addressBox.getItems().remove(entry.getUrl());
            System.out.print("Removed url: ");
            System.out.println(entry.getUrl());
        }
        for (Entry entry : changeValue.getAddedSubList()) {
            System.out.print("Added url: ");
            addressBox.getItems().add(entry.getUrl());
            System.out.println(entry.getUrl());
        }
    }
    
    public void progressBarListener(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
        System.out.println("E");
        System.out.println(new_val.toString());
        progressBar.setProgress(new_val.doubleValue());
    }
    
    private void stateChangeListener(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
        setWebpageTheme(false);
        String output = newValue.toString().toLowerCase();
        statusLabel.setText("Status: " + output);
    }
    
    private void urlChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        addressBox.setValue(newValue);
    }
    
    public void forwardButtonListener(ActionEvent event) {
        webEngine.executeScript("history.forward()");
    }
    
    private void backButtonListener(ActionEvent event) {
        webEngine.executeScript("history.back()");
    }
    
    private void searchButtonListener(ActionEvent event) {
        String google = "http://www.google.com/search?q=" + searchField.getText();
        webEngine.load(google.startsWith("http://") || google.startsWith("https://")
                ? google : "http://" + google);
    }
    
    private void proceedButtonListener(ActionEvent event) {
        String url = addressBox.valueProperty().getValue();
        webEngine.load(url.startsWith("http://") || url.startsWith("https://")
                ? url : "http://" + url);
    }
    
    private void setWebpageTheme(Boolean succeeded) {
        // Can safely access DOM and set styles.
        if (succeeded == true) {
            // This gives the DOM Document for the web page.
            NodeList htmlTags = webEngine.getDocument().getElementsByTagName("*");
            Attr newAttr = null;
            for (int i = 0; i < htmlTags.getLength(); i++) {
                newAttr = webEngine.getDocument().createAttribute("style");
                newAttr.setValue("background-color: #222; border-color: #333; color: #bbb; ");
                htmlTags.item(i).getAttributes().setNamedItem(newAttr);
            }
        }
        enableFirebug(false);
    }
    
    /**
     * Enables debugging a webEngine.
     */
    private void enableFirebug(Boolean enabled) {
        if (enabled) {
            try (Scanner scanner = new Scanner(new FileInputStream(new File("firebug-lite.js")), "UTF-8")) {
                String script = scanner.useDelimiter("\\Z").next();
                webEngine.executeScript(script);
            } catch (IOException e) {
            }
        }
    }
}
