package main;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lepl.*;

public class TestApplication extends LApplication {

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        LBase base = new LBase(960, 525, 20, new BaseType(BaseType.DEFAULT), primaryStage);
        //LBase base = new LBase(400, 670, 20, new BaseType(BaseType.DEFAULT), primaryStage);
        setIcon(primaryStage, base, new Image(Constant.PATH_IMAGE_FRAME + "appIcon.png"));
        setTitle(primaryStage, base, "LApplication");
        Scene scene = base.makeScene();
        primaryStage.setScene(scene);

        Button button = new Button();
        button.setLayoutY(425);
        button.setPrefWidth(100);
        button.setPrefHeight(100);
        base.add(button);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
