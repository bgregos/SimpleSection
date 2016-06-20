package myapp;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SimpleSection extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Overview.fxml")); //load gui config
        Scene scene = new Scene(root);

        //display the gui
        primaryStage.setScene(scene);
        primaryStage.setTitle("SimpleSection");
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() { //kill program on exit clicked
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
});
    }


 public static void main(String[] args) {
        launch(args);
    }
}
