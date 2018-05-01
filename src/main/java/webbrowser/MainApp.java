package webbrowser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.text.Font;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        
        // Load custom fonts used in css stylesheet
        Font.loadFont(MainApp.class.getResource("/fonts/OpenSans-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(MainApp.class.getResource("/fonts/FiraCode-Regular.ttf").toExternalForm(), 10);
        
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/FXMLWebController.fxml")));
        stage.setTitle("Fx Browser Demo");
        stage.setScene(scene);
        stage.show();
    }

    /* @param args the command line arguments */
    public static void main(String[] args) {
        launch(args);
    }
}
