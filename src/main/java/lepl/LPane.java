package lepl;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class LPane extends AnchorPane {
    //필드
    private LBase defend;

    private ImageView background = new ImageView();

    private double scale = 1;
    public double getScale() {
        return scale;
    }
    private double scaleSave;

    private int resizeMode = 0;

    private double pressX;
    private double pressWidth;

    private Translate maximizeTranslate = new Translate();

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
            if (defend.isResizable() && !defend.isMaximized()) {
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
                case 1:
                    defend.getStage().setX(Math.min(event.getScreenX(), pressX + pressWidth - defend.getSmallestWidth()));
                    defend.scale((pressX - event.getScreenX() + pressWidth) / defend.getFirstSize().getWidth());
                    break;
                case 2:
                    defend.scale((event.getSceneY() - defend.getTitleHeight()) / defend.getFirstSize().getHeight());
                    break;
                case 3:
                    defend.scale(event.getSceneX() / defend.getFirstSize().getWidth());
                    break;
                case 4:
                    defend.getStage().setX(Math.min(
                            ((pressX - event.getScreenX() + pressWidth) / defend.getFirstSize().getWidth() > (event.getSceneY() - defend.getTitleHeight()) / defend.getFirstSize().getHeight() ? event.getScreenX() : pressX + pressWidth - defend.getStage().getWidth()),
                            pressX + pressWidth - defend.getSmallestWidth()));
                    defend.scale(Math.max(
                            (pressX - event.getScreenX() + pressWidth) / defend.getFirstSize().getWidth(),
                            (event.getSceneY() - defend.getTitleHeight()) / defend.getFirstSize().getHeight()
                    ));
                    break;
                case 5:
                    defend.scale(Math.max(
                            (event.getSceneY() - defend.getTitleHeight()) / defend.getFirstSize().getHeight(),
                            event.getSceneX() / defend.getFirstSize().getWidth()));
                    break;
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
        scaleSave = this.scale;
        if (defend.getFirstSize().getHeight() / defend.getFirstSize().getWidth() > (Constant.SCREEN_SIZE.getHeight() - defend.getTitleHeight()) / Constant.SCREEN_SIZE.getWidth()) {
            scale((Constant.SCREEN_SIZE.getHeight() - defend.getTitleHeight()) / defend.getFirstSize().getHeight());
            maximizeTranslate.setX(((Constant.SCREEN_SIZE.getWidth() - (defend.getFirstSize().getWidth() * scale)) / 2) / scale);
        } else {
            scale(Constant.SCREEN_SIZE.getWidth() / defend.getFirstSize().getWidth());
            maximizeTranslate.setY(((Constant.SCREEN_SIZE.getHeight() - defend.getTitleHeight() - defend.getFirstSize().getHeight() * scale) / 2) / scale);
        }
        getTransforms().add(maximizeTranslate);
    }
    public void normalizeScale() {
        getTransforms().remove(maximizeTranslate);
        scale(scaleSave);
    }
}
