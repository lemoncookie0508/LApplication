package lepl;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNDECORATED);
    }

    public static void setIcon(Stage primaryStage, LBase base, Image image) {
        primaryStage.getIcons().add(image);
        base.setTitleIcon(image);
    }
    public static void setIcon(Stage primaryStage, LBase base, String url) {
        setIcon(primaryStage, base, new Image(url));
    }

    public static void setTitle(Stage primaryStage, LBase base, String title) {
        primaryStage.setTitle(title);
        base.setTitle(title);
    }
}
