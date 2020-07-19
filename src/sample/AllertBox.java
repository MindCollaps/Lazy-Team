package sample;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class AllertBox {

    private Stage allertBox;
    Modality modality;
    int returnValue = -1;
    String returnValueS;
    final int allertBoxWidth = 150;
    final int allertBoxHeight = 150;

    public AllertBox(Scene sceneToShow, Modality modality) {
        this.modality = modality;
    }

    //even reutrns 0 od reuturns 1
    public int displayEvenOd(String title, String windowsTitle, String even0, String od1, boolean closeProgramOnExit) {
        allertBox = createDefaultStage(windowsTitle);

        Label label = new Label(title);
        label.setId("labelMessage");

        Button buttEven = new Button(even0);
        buttEven.setId("buttonGreen");
        Button buttOd = new Button(od1);
        buttOd.setId("buttonRed");

        VBox layout = new VBox();
        layout.setSpacing(20);
        HBox l = new HBox();
        l.setSpacing(15);

        l.getChildren().addAll(buttEven, buttOd);
        l.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(label, l);
        layout.setAlignment(Pos.CENTER);
        layout.setId("allertBox");
        layout.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
        allertBox.setScene(createDefaultScene(layout));

        buttEven.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                returnValue = 0;
                allertBox.close();
            }
        });

        buttOd.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                returnValue = 1;
                allertBox.close();
            }
        });

        allertBox.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            }
        });

        allertBox.showAndWait();
        return returnValue;
    }

    public String displayTextField(String title, String windowsTitle, String buttonOk, boolean closeProgramOnExit) {
        allertBox = createDefaultStage(windowsTitle);

        Label label = new Label(title);
        label.setId("labelMessage");

        Button okButton = new Button(buttonOk);
        okButton.setId("buttonGreen");
        TextField txtField = new TextField("Text");

        VBox layout = new VBox();
        layout.setId("allertBox");
        layout.setSpacing(20);
        layout.getChildren().addAll(label, txtField, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
        allertBox.setScene(createDefaultScene(layout));

        okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                returnValueS = txtField.getText();
                allertBox.close();
            }
        });

        txtField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    returnValueS = txtField.getText();
                    allertBox.close();
                }
            }
        });

        allertBox.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            }
        });

        allertBox.showAndWait();
        return returnValueS;
    }

    public void displayMessage(String windowsTitle, String message, String buttonMessage, String buttonSryle, boolean closeProgramOnExit) {
        allertBox = createDefaultStage(windowsTitle);
        Label label = new Label(message);
        label.setId("labelMessage");

        Button okButton = new Button(buttonMessage);
        okButton.setId(buttonSryle);

        VBox layout = new VBox();
        layout.setSpacing(20);
        layout.getChildren().addAll(label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setId("allertBox");
        layout.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));

        allertBox.setScene(createDefaultScene(layout));

        okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                allertBox.close();
            }
        });

        allertBox.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            }
        });

        allertBox.showAndWait();
    }

    private Stage createDefaultStage(String windowTitle){
        allertBox = new Stage();
        allertBox.setAlwaysOnTop(true);
        allertBox.setTitle(windowTitle);
        allertBox.initModality(modality);
        allertBox.setResizable(false);
        allertBox.setMinHeight(allertBoxHeight);
        allertBox.setMinWidth(allertBoxWidth);
        allertBox.initStyle(StageStyle.TRANSPARENT);
        return allertBox;
    }

    private Scene createDefaultScene(Parent layout){
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("sample/style.css");
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }

    public void close(){
        allertBox.close();
    }
}
