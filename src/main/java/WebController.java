import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * This is a basic web-browser demo using javafx.
 *
 * Created by Eric Canull on 6/23/17.
 */
public class WebController extends BorderPane implements Initializable {

    @FXML private WebView webView;
    @FXML private WebEngine webEngine;
    @FXML private ComboBox<String> addressBox;
    @FXML private TextField searchField;
    @FXML private Button backButton;
    @FXML private Button forwardButton;
    @FXML private Button proceedButton;
    @FXML private Button searchButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeBrowser();
    }

    /**
     * Initialize the browser GUI and add event handlers
     */
    private void initializeBrowser() {
        backButton.setText("\uD83E\uDC78");
        backButton.setOnAction(this::backButtonListener);

        forwardButton.setText("\u2794");
        forwardButton.setDefaultButton(true);
        forwardButton.setOnAction(this::forwardButtonListener);

        proceedButton.setText("Go");
        proceedButton.setOnAction(this::proceedButtonListener);

        addressBox.setItems(FXCollections.observableArrayList());
        addressBox.setValue("http://www.google.com");
        addressBox.setOnAction(this::proceedButtonListener);
        addressBox.setEditable(true);

        searchField.setPromptText("🔍" + "Search");

        searchButton.setText("🔍");
        searchButton.setDefaultButton(true);
        searchButton.setOnAction(this::searchButtonListener);

        webEngine = webView.getEngine();
        //webEngine.setUserStyleSheetLocation(getClass().getResource("/style/style.css").toString());
        webEngine.load(addressBox.getValue());
        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
        webEngine.getLoadWorker().stateProperty().addListener(this::stateChangeListener);

        final WebHistory history = webEngine.getHistory();
        history.getEntries().addListener(this::historyListener);
    }

    /**
     * Maintains web history for returning to a previous URL or vis-versa.
     * @param changeValue A website page URL
     */
    public void historyListener(ListChangeListener.Change<? extends WebHistory.Entry> changeValue) {
        changeValue.next();
        // Removes url from
        for (WebHistory.Entry entry : changeValue.getRemoved()) {
            addressBox.getItems().remove(entry.getUrl());
            //System.out.print("Removed url: ");
            //System.out.println(entry.getUrl());
        }
        for (WebHistory.Entry entry : changeValue.getAddedSubList()) {
            //System.out.print("Added url: ");
            addressBox.getItems().add(entry.getUrl());
            //System.out.println(entry.getUrl());
        }
    }

    /**
     * Updates the progress bar loading of an URL and resources.
     * @param ov
     * @param old_val
     * @param new_val
     */
    public void progressBarListener(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
        //System.out.println(new_val.toString());
        progressBar.setProgress(new_val.doubleValue());
    }

    /**
     * Updates the status label loading of an URL and resources.
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void stateChangeListener(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
        setWebpageTheme(false); //set website html dark theme
        String output = newValue.toString().toLowerCase();
        statusLabel.setText("Status: " + output);
    }

    /**
     * Updates the address box URL when website is changed.
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void urlChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        addressBox.setValue(newValue);
    }

    /**
     * Use javaScript command to cycle forward in web history after forward button is pressed.
     * @param event
     */
    public void forwardButtonListener(ActionEvent event) {
        webEngine.executeScript("history.forward()");
    }

    /**
     * Use javaScript command to cycle backward in web history after forward button is pressed.
     * @param event
     */
    private void backButtonListener(ActionEvent event) {
        webEngine.executeScript("history.back()");
    }

    /**
     * Search the web using the contents of search field and google search.
     * @param event
     */
    private void searchButtonListener(ActionEvent event) {
        String google = "http://www.google.com/search?q=" + searchField.getText();
        webEngine.load(google.startsWith("http://") || google.startsWith("https://")
                ? google : "http://" + google);
    }

    /**
     * Request URL in the address box after the proceed button has been pressed.
     * @param event
     */
    private void proceedButtonListener(ActionEvent event) {
        String url = addressBox.valueProperty().getValue();
        webEngine.load(url.startsWith("http://") || url.startsWith("https://")
                ? url : "http://" + url);
    }

    /**
     * Sets the dark html theme on all websites.
     * @param succeeded
     */
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
     * @param enabled
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

