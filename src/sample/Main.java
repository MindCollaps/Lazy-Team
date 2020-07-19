package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/sample.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(3000);
        }
        controller = loader.getController();
        Scene scene = new Scene(root);
        controller.init(new Stage(), primaryStage, scene);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
