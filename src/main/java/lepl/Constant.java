package lepl;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class Constant {
    //상수
    public static final Rectangle2D SCREEN_SIZE = Screen.getPrimary().getBounds();

    public static final String PATH = "file:///" + System.getProperty("user.dir") + "/data/";
    public static final String PATH_IMAGE_FRAME = PATH + "images-frame/";
    public static final String PATH_IMAGE = PATH + "images/";
    public static final String PATH_FONT = PATH + "fonts/";
}
