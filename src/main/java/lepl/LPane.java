package lepl;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;

public class LPane extends AnchorPane {
    //필드
    private LBase depend;

    private double scale = 1;
    public double getScale() {
        return scale;
    }

    private Translate maximizeTranslate = new Translate();
    private Scale maximizeScale = new Scale();
    private Translate halveTranslate = new Translate(0,0);
    private Scale halveScale = new Scale(1,1);

    //생성자
    public LPane(LBase depend) {
        this.depend = depend;

        setLayoutY(depend.getTitleHeight() + 1);
        setPrefWidth(depend.getFirstSize().getWidth());
        setPrefHeight(depend.getFirstSize().getHeight());
    }

    //메소드
    public boolean add(Node node) {
        return getChildren().add(node);
    }
    public boolean remove(Node node) {
        return getChildren().remove(node);
    }

    public void scale(double scale) {
        getTransforms().add(new Scale(scale / this.scale, scale / this.scale));
        this.scale = scale;
    }
    public void maximizeScale() {
        if (depend.isHalved()) normalizeScale();
        if (depend.getFirstSize().getHeight() / depend.getFirstSize().getWidth() > (Constant.SCREEN_SIZE.getHeight() - depend.getTitleHeight()) / Constant.SCREEN_SIZE.getWidth()) {
            maximizeScale.setX(((Constant.SCREEN_SIZE.getHeight() - depend.getTitleHeight()) / depend.getFirstSize().getHeight()) / scale);
            maximizeScale.setY(((Constant.SCREEN_SIZE.getHeight() - depend.getTitleHeight()) / depend.getFirstSize().getHeight()) / scale);
            maximizeTranslate.setX(((Constant.SCREEN_SIZE.getWidth() - (depend.getFirstSize().getWidth() * (scale * maximizeScale.getX()))) / 2) / (scale * maximizeScale.getX()));
        } else {
            maximizeScale.setX((Constant.SCREEN_SIZE.getWidth() / depend.getFirstSize().getWidth()) / scale);
            maximizeScale.setY((Constant.SCREEN_SIZE.getWidth() / depend.getFirstSize().getWidth()) / scale);
            maximizeTranslate.setY(((Constant.SCREEN_SIZE.getHeight() - depend.getTitleHeight() - depend.getFirstSize().getHeight() * (scale * maximizeScale.getX())) / 2) / (scale * maximizeScale.getX()));
        }
        getTransforms().add(maximizeScale);
        getTransforms().add(maximizeTranslate);
    }
    public void scaleDuringHalved(double width) {
        getTransforms().remove(halveTranslate);
        getTransforms().remove(halveScale);
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        if (depend.getFirstSize().getHeight() / depend.getFirstSize().getWidth() > (visualBounds.getHeight() - depend.getTitleHeight()) / width) {
            halveScale.setX(((visualBounds.getHeight() - depend.getTitleHeight()) / depend.getFirstSize().getHeight()) / scale);
            halveScale.setY(((visualBounds.getHeight() - depend.getTitleHeight()) / depend.getFirstSize().getHeight()) / scale);
            halveTranslate.setX(((width - (depend.getFirstSize().getWidth() * (scale * halveScale.getX()))) / 2) / (scale * halveScale.getX()));
            halveTranslate.setY(0);
        } else {
            halveScale.setX((width / depend.getFirstSize().getWidth()) / scale);
            halveScale.setY((width / depend.getFirstSize().getWidth()) / scale);
            halveTranslate.setX(0);
            halveTranslate.setY(((visualBounds.getHeight() - depend.getTitleHeight() - depend.getFirstSize().getHeight() * (scale * halveScale.getX())) / 2) / (scale * halveScale.getX()));
        }
        getTransforms().add(halveScale);
        getTransforms().add(halveTranslate);
    }
    public void refreshHalveScale() {
        getTransforms().add(halveScale);
        getTransforms().add(halveTranslate);
    }
    public void normalizeScale() {
        if (depend.isMaximized()) {
            getTransforms().remove(maximizeTranslate);
            getTransforms().remove(maximizeScale);
        }
        if (depend.isHalved()) {
            getTransforms().remove(halveScale);
            getTransforms().remove(halveTranslate);
        }
    }
}
