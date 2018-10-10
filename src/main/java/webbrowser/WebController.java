package webbrowser;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a basic web-browser demo using javafx.
 *
 * Created by Eric Canull on 6/23/17.
 */
public class WebController extends BorderPane {
   
//    private static final String DEFAULT_URL = "https://localhost:9443/dashboard/";
     private static final String DEFAULT_URL = "https://www.google.com";
    
       
    @FXML private WebView webView;
    @FXML private ComboBox<String> addressBox;
    @FXML private Button  backButton, forwardButton, proceedButton, menuButton;
    @FXML private TextField searchField;
   // @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    
    public SimpleBooleanProperty succeeded = new SimpleBooleanProperty();
    
    private WebEngine webEngine;

    public WebController() {
        initialize();
        
    }

    /**
     * Initialize the browser GUI and add event handlers
     */
    private void initialize() {
        try {
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(WebController.class.getResource("/fxml/FXMLWebController.fxml")); 
            loader.setController(this);
            loader.setRoot(this);
            loader.load();

        } catch (IOException ex) {
            Logger.getLogger(WebController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        backButton.setOnAction(this::backButtonAction);
        forwardButton.setOnAction(this::forwardButtonAction);
        proceedButton.setOnAction(this::proceedButtonAction);
        searchField.setOnKeyPressed(this::onKeyPressed);

        addressBox.setItems(FXCollections.observableArrayList());
        addressBox.setValue(DEFAULT_URL);
        addressBox.setOnAction(this::proceedButtonAction);

        webEngine = webView.getEngine();
        // webEngine.setUserStyleSheetLocation(getClass().getResource("/style/style.css").toString());
        webEngine.load(addressBox.getValue());
        //progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
        webEngine.getLoadWorker().stateProperty().addListener(this::stateChangeListener);

        final WebHistory history = webEngine.getHistory();
        history.getEntries().addListener(this::historyListener);
        
        loadTabTextField(DEFAULT_URL);
        
        succeeded.addListener(this::onPageLoaded);

    }
    
     private void loadTabTextField(String url) {
       URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException ex) {
            Logger.getLogger(WebController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String path = uri.getPath();
        
        System.out.println(path);
       
    }
    
    public String getTitle() {
        Document doc = webEngine.getDocument();
        NodeList heads = doc.getElementsByTagName("head");
        String titleText = webEngine.getLocation(); // use location if page does not define a title
        return getFirstElement(heads)
                .map(h -> h.getElementsByTagName("title"))
                .flatMap(this::getFirstElement)
                .map(Node::getTextContent).orElse(titleText);
    }

    private Optional<Element> getFirstElement(NodeList nodeList) {
        if (nodeList.getLength() > 0 && nodeList.item(0) instanceof Element) {
            return Optional.of((Element) nodeList.item(0));
        }
        return Optional.empty();
    }

    private void historyListener(ListChangeListener.Change<? extends WebHistory.Entry> changeValue) {
        changeValue.next();

        // Removes url from address box
        changeValue.getRemoved().forEach((entry) -> addressBox.getItems().remove(entry.getUrl()));

        // Add url to address box
        changeValue.getAddedSubList().stream().map((entry) -> {
            addressBox.setValue(entry.getUrl());
            return entry;
        }).forEachOrdered((entry) -> addressBox.getItems().add(entry.getUrl()));
    }
    
    /**
     * Updates the status label loading of an URL and resources.
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void stateChangeListener(ObservableValue<? extends Object> observable, Object ov, Object nv) {
        succeeded.set(nv.toString().equalsIgnoreCase("SUCCEEDED"));
        
        // update status label
        statusLabel.setText("Status: " + nv.toString().toLowerCase());
    }

    /**
     * Use javaScript command to cycle forward in web history 
     * after forward button is pressed.
     * @param event
     */
    private void forwardButtonAction(ActionEvent event) {
        webEngine.executeScript("history.forward()");
    }

    /**
     * Use javaScript command to cycle backward in web history 
     * after back button is pressed.
     * @param event
     */
    private void backButtonAction(ActionEvent event) {
        webEngine.executeScript("history.back()");
    }
    
    private void onKeyPressed(KeyEvent evt){
        if (evt.getCode().equals(KeyCode.ENTER) 
                && !searchField.getText().trim().isEmpty())  {
            onSearchAction();
        }
    }
  
    /**
     * Search the web using the contents of search field and google search.
     * @param event
     */
    private void onSearchAction() {
        String googleSearch = "https://www.google.com/search?q=" + searchField.getText();
        webEngine.load(googleSearch);
    }

    /**
     * Request URL in the address box after the proceed button has been pressed.
     * @param event
     */
    private void proceedButtonAction(ActionEvent event) {
        String url = addressBox.valueProperty().getValue();
        webEngine.load(url.startsWith("http://") || url.startsWith("https://")
                ? url : "http://" + url);
    }

    /**
     * Sets dark theme elements on current URL.
     * @param succeeded
     */
    private void setWebpageTheme(boolean enable) {
        
        if(enable) {
        // Can safely access DOM and set styles.
      
            // This gives the DOM Document for the web page.
            NodeList htmlTags = webEngine.getDocument().getElementsByTagName("*");
            
//            Attr newAttr =webEngine.getDocument().createAttribute("style");
            for (int i = 0; i < htmlTags.getLength(); i++) {
              Attr newAttr = webEngine.getDocument().createAttribute("style");
                newAttr.setValue(
                          "background-color: #171a1d; "
                        + "border-color: #43462b; "
                        + "color: #bbb;");
                htmlTags.item(i).getAttributes().setNamedItem(newAttr);
            }
        }
    }
    
    private void onPageLoaded(ObservableValue<? extends Boolean> observable, boolean ov, boolean nv) {
        if (succeeded.get() == true) {
            setWebpageTheme(false);
        }
    }
    
     /**
     * Quick one-liner that delimits using the end of file character and returns
     * the whole input stream as a String. Use for small files.
     *
     * @param inputStream byte input stream.
     * @return String a file or JSON text
     */
    private static String streamToString(InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            return scanner.useDelimiter("\\Z").next();
        }
    }
    
    /**
     * Enables Firebug for debugging a webEngine.
     * @param engine the webEngine for which debugging is to be enabled.
     */
   @FXML private void enableFirebug(ActionEvent event) {
            InputStream file = this.getClass().getResourceAsStream("/firebug/firebug-script.js");
            final String script = streamToString(file);
            webEngine.executeScript(script);
    }

   
    
    /**
     * Enables debugging a webEngine.
     * @param enabled
     */
//    private void enableFirebug(Boolean enabled) {
//        if (enabled) {
//            
//            
//            try (Scanner scanner = new Scanner(new FileInputStream(
//                    new File("firebug-lite.js")), "UTF-8")) {
//
//                String script = scanner.useDelimiter("\\Z").next();
//                webEngine.executeScript(script);
//            } catch (IOException ex) {
//                Logger.getLogger(WebController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }


//            private void enableFirebug(Boolean enabled) {
//          
//                final String DATA_PREFIX = "data:text/css;charset=utf-8;base64,";
//
//                    String url = get();
//                    String dataUrl;
//                    if (url == null || url.length() <= 0) {
//                        dataUrl = null;
//                    } else if (url.startsWith(DATA_PREFIX)) {
//                        dataUrl = url;
//                    } else if (url.startsWith("file:") ||
//                               url.startsWith("jar:")  ||
//                               url.startsWith("data:"))
//                    {
//                        try {
//                            URLConnection conn = URLs.newURL(url).openConnection();
//                            conn.connect();
//
//                            BufferedInputStream in =
//                                    new BufferedInputStream(conn.getInputStream());
//                            byte[] inBytes = readFully(in);
//                            String out = Base64.getMimeEncoder().encodeToString(inBytes);
//                            dataUrl = DATA_PREFIX + out;
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    } else {
//                        throw new IllegalArgumentException("Invalid stylesheet URL");
//                    }
//            }
}
