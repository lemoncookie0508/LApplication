package lepl;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.awt.*;
import java.io.InputStream;

public class Constant {
    //상수
    public static final Rectangle2D SCREEN_SIZE = Screen.getPrimary().getBounds();
    public static final Insets SCREEN_INSETS = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getConfigurations()[0]);

    public static InputStream getResource(String filepath) {
        return Constant.class.getClassLoader().getResourceAsStream(filepath);
    }
    public static InputStream getImageResource(String filename) {
        return getResource("images/" + filename);
    }
}
