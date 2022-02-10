package lepl;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;

public class LBase extends VBox {
    //필드
    private Stage primaryStage = null;

    private final double titleHeight;
    public double getTitleHeight() {
        return titleHeight;
    }

    private final Rectangle firstSize;
    public Rectangle getFirstSize() {
        return firstSize;
    }

    private final BaseType baseType;
    public boolean isType(int baseType) {
        return  (this.baseType.baseType() & baseType) != 0;
    }
    public boolean isType(BaseType baseType) {
        return  (this.baseType.baseType() & baseType.baseType()) != 0;
    }

    private boolean isResizable = true;
    @Override
    public boolean isResizable() {
        return isResizable;
    }
    public void setResizable(boolean resizable) {
        isResizable = resizable;
    }

    private int resizeMode = 0;
    private double pressX;
    private double pressWidth;

    private boolean isMaximizable = true;
    public boolean isMaximizable() {
        return isMaximizable;
    }
    public void setMaximizable(boolean maximizable) {
        isMaximizable = maximizable;
        titleBar.getMaximizeButton().setDisable(!maximizable);
    }

    private boolean isCanHalve = true;
    public boolean isCanHalve() {
        return isCanHalve;
    }
    public void setCanHalve(boolean canHalve) {
        isCanHalve = canHalve;
    }

    private int halveSide = 0;

    private double smallestWidth = 150;
    public void setSmallestWidth(double smallestWidth) {
        this.smallestWidth = smallestWidth;
    }
    public double getSmallestWidth() {
        return smallestWidth;
    }

    private boolean isMaximized = false;
    public boolean isMaximized() {
        return isMaximized;
    }

    private boolean isHalved = false;
    public boolean isHalved() {
        return isHalved;
    }

    private LTitleBar titleBar;
    private LPane mainPane;
    private LExitDialog exitDialog;

    //생성자
    public LBase(double width, double height, double titleHeight, BaseType baseType) {
        this.firstSize = new Rectangle(width, height);
        this.titleHeight = titleHeight;
        this.baseType = baseType;

        setWidth(width);
        setHeight(height + titleHeight);
        setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().add(titleBar = new LTitleBar(this));
        getChildren().add(mainPane = new LPane(this, Constant.PATH_IMAGE_FRAME + "background.png"));

        setOnMousePressed(event -> {
            pressX = getStage().getX();
            pressWidth = getStage().getWidth();
        });
        setOnMouseMoved(event -> {
            if (isResizable) {
                double rp = 3;
                double stageWidth = getStage().getWidth();
                double stageHeight = getStage().getHeight();
                if (!isMaximized) {
                    if (isHalved) {
                        if (event.getSceneX() >= (stageWidth - rp) && halveSide == 0) {
                            setCursor(Cursor.E_RESIZE);
                            resizeMode = 6;
                        }
                        else if (event.getSceneX() <= rp && halveSide == 1) {
                            setCursor(Cursor.W_RESIZE);
                            resizeMode = 7;
                        }
                        else {
                            setCursor(Cursor.DEFAULT);
                            resizeMode = 0;
                        }
                    } else {
                        if (event.getSceneY() >= (stageHeight - rp)) {
                            if (event.getX() <= rp) {
                                setCursor(Cursor.SW_RESIZE);
                                resizeMode = 4;
                                return;
                            }
                            else if (event.getSceneX() >= (stageWidth - rp)) {
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
                        else if (event.getSceneX() >= (stageWidth - rp)) {
                            setCursor(Cursor.E_RESIZE);
                            resizeMode = 3;
                        }
                        else {
                            setCursor(Cursor.DEFAULT);
                            resizeMode = 0;
                        }
                    }
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
                    getStage().setX(Math.min(event.getScreenX(), pressX + pressWidth - getSmallestWidth()));
                    scale((pressX - event.getScreenX() + pressWidth) / getFirstSize().getWidth());
                }
                case 2 -> scale((event.getSceneY() - getTitleHeight()) / getFirstSize().getHeight());
                case 3 -> scale(event.getSceneX() / getFirstSize().getWidth());
                case 4 -> {
                    getStage().setX(Math.min(
                            ((pressX - event.getScreenX() + pressWidth) / getFirstSize().getWidth() > (event.getSceneY() - getTitleHeight()) / getFirstSize().getHeight() ? event.getScreenX() : pressX + pressWidth - getStage().getWidth()),
                            pressX + pressWidth - getSmallestWidth()));
                    scale(Math.max(
                            (pressX - event.getScreenX() + pressWidth) / getFirstSize().getWidth(),
                            (event.getSceneY() - getTitleHeight()) / getFirstSize().getHeight()
                    ));
                }
                case 5 -> scale(Math.max(
                        (event.getSceneY() - getTitleHeight()) / getFirstSize().getHeight(),
                        event.getSceneX() / getFirstSize().getWidth()));
                case 6 -> scaleDuringHalved(event.getScreenX());
                case 7 -> {
                    getStage().setX(Math.min(event.getScreenX(), Constant.SCREEN_SIZE.getWidth() - smallestWidth));
                    scaleDuringHalved(Constant.SCREEN_SIZE.getWidth() - event.getScreenX());
                }
            }
        });
    }
    public LBase(double width, double height, double titleHeight, BaseType baseType, Stage primaryStage) {
        this(width, height, titleHeight, baseType);
        this.primaryStage = primaryStage;
        exitDialog = new LExitDialog();
    }
    public LBase(double width, double height) {
        this(width, height, 20, new BaseType(BaseType.DEFAULT));
    }
    public LBase(double width, double height, double titleHeight) {
        this(width, height, titleHeight, new BaseType(BaseType.DEFAULT));
    }
    public LBase(double width, double height, BaseType baseType) {
        this(width, height, 20, baseType);
    }

    //메소드
    public boolean add(Node node) {
        return mainPane.add(node);
    }

    public Scene makeScene() {
        return new Scene(this, firstSize.getWidth(), firstSize.getHeight() + titleHeight);
    }

    public Stage getStage() {
        if (primaryStage == null) {
            return (Stage) getScene().getWindow();
        } else {
            return primaryStage;
        }
    }

    public boolean isMain() {
        return exitDialog != null;
    }

    public void setTitleIcon(Image icon) {
        titleBar.setIcon(icon);
    }
    public void setTitleIcon(String url) {
        setTitleIcon(new Image(url));
    }

    public void setTitle(String title) {
        titleBar.setTitle(title);
    }

    public void setPaneBackground(Image image) {
        mainPane.setBackground(image);
    }

    public void scale(double scale) {
        scale = Math.max(scale, smallestWidth / firstSize.getWidth());
        mainPane.scale(scale);
        titleBar.setButtonWidth(firstSize.getWidth() * scale);
        getStage().setWidth(firstSize.getWidth() * scale);
        getStage().setHeight(firstSize.getHeight() * scale + titleHeight);
    }
    public void scaleDuringHalved(double width) {
        width = Math.max(width, smallestWidth);
        mainPane.scaleDuringHalved(width);
        titleBar.setButtonWidth(width);
        getStage().setWidth(width);
    }

    public void maximize() {
        if (isMaximizable) {
            isMaximized = true;
            getStage().setMaximized(true);
            titleBar.getMaximizeButton().setIcon(true);
            titleBar.setButtonWidth(Constant.SCREEN_SIZE.getWidth());
            mainPane.maximizeScale();
            getStage().setX(0);
            getStage().setY(0);
        }
    }
    public void halve(int halveSide) {
        if (isCanHalve) {
            this.halveSide = halveSide;
            isHalved = true;
            getStage().setY(0);
            mainPane.scaleDuringHalved(Constant.SCREEN_SIZE.getWidth() / 2);
            switch (halveSide) {
                case 0 -> getStage().setX(0);
                case 1 -> getStage().setX(Constant.SCREEN_SIZE.getWidth() / 2);
            }
            titleBar.setButtonWidth(Constant.SCREEN_SIZE.getWidth() / 2);
            getStage().setWidth(Constant.SCREEN_SIZE.getWidth() / 2);
            getStage().setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        }
    }
    public void normalize() {
        if (isMaximized) {
            mainPane.normalizeScale();
            if (isHalved) mainPane.refreshHalveScale();
            isMaximized = false;
            getStage().setMaximized(false);
            titleBar.getMaximizeButton().setIcon(false);
        } else if (isHalved) {
            mainPane.normalizeScale();
            isHalved = false;
            getStage().setWidth(mainPane.getScale() * firstSize.getWidth());
            getStage().setHeight(mainPane.getScale() * firstSize.getHeight());
        }
        titleBar.setButtonWidth(getStage().getWidth());
        if (getStage().getY() < 0) {
            getStage().setY(0);
        }
    }

    public void close() {
        if (isMain()) {
            Stage stage = getStage();
            exitDialog.show(stage, (stage.getWidth() - exitDialog.width) / 2 + stage.getX(), (stage.getHeight() - exitDialog.height) / 2 + stage.getY());
        } else {
            getStage().hide();
        }
    }
    public void exit() {
        System.exit(0);
    }

    //중첩 클래스
    private class LExitDialog extends Popup {
        //필드
        public final double width = 240;
        public final double height = 126;

        private Text message = new Text("정말 종료하시겠습니까?");
        private Button yes = new Button("예");
        private Button no = new Button("아니오");

        //생성자
        private LExitDialog() {
            setAutoHide(true);
            AnchorPane pane = new AnchorPane();
            pane.setPrefSize(width, height);
            pane.setBackground(new Background(new BackgroundFill(Color.rgb(190, 190, 190), CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setBorder(new Border(new BorderStroke(Color.rgb(0,0,0), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

            message.setTextAlignment(TextAlignment.CENTER);
            message.setWrappingWidth(width);
            message.setLayoutX(0);
            message.setLayoutY(45);
            yes.setLayoutX((double) 110 / 3);
            yes.setLayoutY(85);
            yes.setPrefWidth(65);
            yes.setPrefHeight(30);
            yes.setOnAction(actionEvent -> exit());
            no.setLayoutX(((double) 110 / 3) * 2 + 65);
            no.setLayoutY(85);
            no.setPrefWidth(65);
            no.setPrefHeight(30);
            no.setOnAction(actionEvent -> hide());

            pane.getChildren().add(message);
            pane.getChildren().add(yes);
            pane.getChildren().add(no);
            getContent().add(pane);
        }

        //함수
        @Override
        public void show(Window window, double v, double v1) {
            super.show(window, v, v1);
            yes.requestFocus();
        }
    }
}
