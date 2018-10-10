package webbrowser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * FXML Controller class
 *
 * @author andje22
 */
public class MainController implements Initializable {
        
    @FXML private TabPane tabPane;
    @FXML private Tab rootTab, emptyTab;
    
    private BrowserLoader browserLoader;
    private WebController webController;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        browserLoader = new BrowserLoader();
        webController = browserLoader.getBrowser();
        rootTab.setContent(webController);

        webController.succeeded.addListener((o) -> rootTab.setText(((SimpleBooleanProperty) o).get() == true
                ? webController.getTitle().length() > 18 ? webController.getTitle().substring(0, 18) + "..." 
                        : webController.getTitle() : "New Tab")
        );
        tabPane.getSelectionModel().selectedItemProperty().addListener(this::onTabSelection);
    }
    
    

    /**
     * Creates a new tab
     * @param observable
     * @param oldSelectedTab
     * @param newSelectedTab 
     */
    private void onTabSelection(ObservableValue<? extends Tab> observable, Tab oldSelectedTab, Tab newSelectedTab) {
        if (newSelectedTab == emptyTab) {
            Tab tab = new Tab();
            browserLoader = new BrowserLoader();
            WebController wc = browserLoader.getBrowser();
            tab.setContent(wc);
        
            wc.succeeded.addListener((o) -> tab.setText(((SimpleBooleanProperty) o).get() == true 
                        ? wc.getTitle().length() > 18 ? wc.getTitle().substring(0, 18) + "..." 
                                : wc.getTitle() : "New Tab")
            );

            final ObservableList<Tab> tabs = tabPane.getTabs();
            tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
            tabs.add(tabs.size() - 1, tab);
            tabPane.getSelectionModel().select(tab);
        }
    }
}
