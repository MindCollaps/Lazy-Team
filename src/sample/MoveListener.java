package sample;

import javafx.scene.Node;
import javafx.stage.Stage;

public class MoveListener {

    private double xOffset = 0;
    private double yOffset = 0;

    Stage stage;
    Node node;

    public MoveListener(Node node, Stage stage) {
        this.stage = stage;
        this.node = node;
        start();
    }

    public void start(){
        node.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        node.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
}
