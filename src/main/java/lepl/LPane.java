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

    private Translate maximizeTranslate = new Translate();
    private Scale maximizeScale = new Scale();
    private Translate halveTranslate = new Translate(0,0);
    private Scale halveScale = new Scale(1,1);

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
    public void scaleDuringHalved(double width) {
        getTransforms().remove(halveTranslate);
        getTransforms().remove(halveScale);
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        if (defend.getFirstSize().getHeight() / defend.getFirstSize().getWidth() > (visualBounds.getHeight() - defend.getTitleHeight()) / width) {
            halveScale.setX(((visualBounds.getHeight() - defend.getTitleHeight()) / defend.getFirstSize().getHeight()) / scale);
            halveScale.setY(((visualBounds.getHeight() - defend.getTitleHeight()) / defend.getFirstSize().getHeight()) / scale);
            halveTranslate.setX(((width - (defend.getFirstSize().getWidth() * (scale * halveScale.getX()))) / 2) / (scale * halveScale.getX()));
            halveTranslate.setY(0);
        } else {
            halveScale.setX((width / defend.getFirstSize().getWidth()) / scale);
            halveScale.setY((width / defend.getFirstSize().getWidth()) / scale);
            halveTranslate.setX(0);
            halveTranslate.setY(((visualBounds.getHeight() - defend.getTitleHeight() - defend.getFirstSize().getHeight() * (scale * halveScale.getX())) / 2) / (scale * halveScale.getX()));
        }
        getTransforms().add(halveScale);
        getTransforms().add(halveTranslate);
    }
    public void refreshHalveScale() {
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
