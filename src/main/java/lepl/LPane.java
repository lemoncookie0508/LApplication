package lepl;

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;

public class LPane extends AnchorPane {
    //필드
    private LBase defend;

    private ImageView background = new ImageView();

    private double scale = 1;
    public double getScale() {
        return scale;
    }

    private int resizeMode = 0;

    private double pressX;
    private double pressWidth;

    private Translate maximizeTranslate = new Translate();
    private Scale maximizeScale = new Scale();
    private Translate halveTranslate = new Translate();
    private Scale halveScale = new Scale();

    //생성자
    public LPane(LBase defend, String path) {
        this.defend = defend;
        this.background.setImage(new Image(path));

        setPrefWidth(defend.getFirstSize().getWidth()) ;
        setPrefHeight(defend.getFirstSize().getHeight());

        background.setLayoutX(0);
        background.setLayoutY(0);
        background.setFitWidth(defend.getFirstSize().getWidth());
        background.setFitHeight(defend.getFirstSize().getHeight());
        add(background);

        setOnMousePressed(event -> {
            pressX = defend.getStage().getX();
            pressWidth = defend.getStage().getWidth();
        });
        setOnMouseMoved(event -> {
            if (defend.isResizable() && !defend.isMaximized() && !defend.isHalved()) {
                double rp = 3;
                double width = defend.getStage().getWidth();
                double height = defend.getStage().getHeight();
                if (event.getSceneY() >= (height - rp)) {
                    if (event.getX() <= rp) {
                        setCursor(Cursor.SW_RESIZE);
                        resizeMode = 4;
                        return;
                    }
                    else if (event.getSceneX() >= (width - rp)) {
                        setCursor(Cursor.SE_RESIZE);
                        resizeMode = 5;
                        return;
                    }
                    setCursor(Cursor.S_RESIZE);
                    resizeMode = 2;
                }
                else if (event.getSceneX() <= rp) {
                    setCursor(Cursor.W_RESIZE);
                    resizeMode = 1;
                }
                else if (event.getSceneX() >= (width - rp)) {
                    setCursor(Cursor.E_RESIZE);
                    resizeMode = 3;
                }
                else {
                    setCursor(Cursor.DEFAULT);
                    resizeMode = 0;
                }
            }
            else {
                setCursor(Cursor.DEFAULT);
                resizeMode = 0;
            }
        });
        setOnMouseDragged(event -> {
            switch (resizeMode) {
                case 1 -> {
                    defend.getStage().setX(Math.min(event.getScreenX(), pressX + pressWidth - defend.getSmallestWidth()));
                    defend.scale((pressX - event.getScreenX() + pressWidth) / defend.getFirstSize().getWidth());
                }
                case 2 -> defend.scale((event.getSceneY() - defend.getTitleHeight()) / defend.getFirstSize().getHeight());
                case 3 -> defend.scale(event.getSceneX() / defend.getFirstSize().getWidth());
                case 4 -> {
                    defend.getStage().setX(Math.min(
                            ((pressX - event.getScreenX() + pressWidth) / defend.getFirstSize().getWidth() > (event.getSceneY() - defend.getTitleHeight()) / defend.getFirstSize().getHeight() ? event.getScreenX() : pressX + pressWidth - defend.getStage().getWidth()),
                            pressX + pressWidth - defend.getSmallestWidth()));
                    defend.scale(Math.max(
                            (pressX - event.getScreenX() + pressWidth) / defend.getFirstSize().getWidth(),
                            (event.getSceneY() - defend.getTitleHeight()) / defend.getFirstSize().getHeight()
                    ));
                }
                case 5 -> defend.scale(Math.max(
                        (event.getSceneY() - defend.getTitleHeight()) / defend.getFirstSize().getHeight(),
                        event.getSceneX() / defend.getFirstSize().getWidth()));
            }
        });
    }

    //메소드
    public boolean add(Node node) {
        return getChildren().add(node);
    }

    public void setBackground(Image image) {
        background.setImage(image);
    }

    public void scale(double scale) {
        getTransforms().add(new Scale(scale / this.scale, scale / this.scale));
        this.scale = scale;
    }
    public void maximizeScale() {
        if (defend.isHalved()) normalizeScale();
        if (defend.getFirstSize().getHeight() / defend.getFirstSize().getWidth() > (Constant.SCREEN_SIZE.getHeight() - defend.getTitleHeight()) / Constant.SCREEN_SIZE.getWidth()) {
            maximizeScale.setX(((Constant.SCREEN_SIZE.getHeight() - defend.getTitleHeight()) / defend.getFirstSize().getHeight()) / scale);
            maximizeScale.setY(((Constant.SCREEN_SIZE.getHeight() - defend.getTitleHeight()) / defend.getFirstSize().getHeight()) / scale);
            maximizeTranslate.setX(((Constant.SCREEN_SIZE.getWidth() - (defend.getFirstSize().getWidth() * (scale * maximizeScale.getX()))) / 2) / (scale * maximizeScale.getX()));
        } else {
            maximizeScale.setX((Constant.SCREEN_SIZE.getWidth() / defend.getFirstSize().getWidth()) / scale);
            maximizeScale.setY((Constant.SCREEN_SIZE.getWidth() / defend.getFirstSize().getWidth()) / scale);
            maximizeTranslate.setY(((Constant.SCREEN_SIZE.getHeight() - defend.getTitleHeight() - defend.getFirstSize().getHeight() * (scale * maximizeScale.getX())) / 2) / (scale * maximizeScale.getX()));
        }
        getTransforms().add(maximizeScale);
        getTransforms().add(maximizeTranslate);
    }
    public void halveScale() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        if (defend.getFirstSize().getHeight() / defend.getFirstSize().getWidth() > (visualBounds.getHeight() - defend.getTitleHeight()) / (visualBounds.getWidth() / 2)) {
            halveScale.setX(((visualBounds.getHeight() - defend.getTitleHeight()) / defend.getFirstSize().getHeight()) / scale);
            halveScale.setY(((visualBounds.getHeight() - defend.getTitleHeight()) / defend.getFirstSize().getHeight()) / scale);
            halveTranslate.setX((((visualBounds.getWidth() / 2) - (defend.getFirstSize().getWidth() * (scale * halveScale.getX()))) / 2) / (scale * halveScale.getX()));
        } else {
            halveScale.setX(((visualBounds.getWidth() / 2) / defend.getFirstSize().getWidth()) / scale);
            halveScale.setY(((visualBounds.getWidth() / 2) / defend.getFirstSize().getWidth()) / scale);
            halveTranslate.setY(((visualBounds.getHeight() - defend.getTitleHeight() - defend.getFirstSize().getHeight() * (scale * halveScale.getX())) / 2) / (scale * halveScale.getX()));
        }
        getTransforms().add(halveScale);
        getTransforms().add(halveTranslate);
    }
    public void normalizeScale() {
        if (defend.isMaximized()) {
            getTransforms().remove(maximizeTranslate);
            getTransforms().remove(maximizeScale);
        }
        if (defend.isHalved()) {
            getTransforms().remove(halveTranslate);
            getTransforms().remove(halveScale);
        }
    }
}
