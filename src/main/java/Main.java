import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	private static final Dimension2D DIM = new Dimension2D(1220, 680);
        
	public static void main(String[] args) {
		launch(args);
	}

	public Parent createContent() {
		final WebBrowser browser = new WebBrowser(DIM);
		return browser;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setResizable(true);
		Scene scene = new Scene(createContent());
		primaryStage.setTitle("Eric's Web Demo");
	//scene.setUserAgentStylesheet(getClass().getResource("style/template.css").toExternalForm());
		scene.getStylesheets().add("style/template.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
