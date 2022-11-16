package lepl;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;

import java.util.HashMap;
import java.util.Map;

public class LBase extends AnchorPane {
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
        if (isType(BaseType.MAXIMIZE)) titleBar.getMaximizeButton().setDisable(!maximizable);
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
    public void setTitleBar(LTitleBar titleBar) {
        if (this.titleBar != null) getChildren().remove(this.titleBar);
        getChildren().add(0, this.titleBar = titleBar);
    }
    public LTitleBar getTitleBar() {
        return titleBar;
    }

    private LPane mainPane;
    public LPane getMainPane() {
        return mainPane;
    }
    private LExitDialog exitDialog;

    protected HashMap<KeyCodeCombination, ShortcutHandler> keyboardShortcuts = new HashMap<>() {{
        put(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN),
                () -> fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));
    }};

    //생성자
    public LBase(double width, double height, double titleHeight, BaseType baseType) {
        this.firstSize = new Rectangle(width, height);
        this.titleHeight = titleHeight;
        this.baseType = baseType;

        setWidth(width);
        setHeight(height + titleHeight);
        setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        setOnKeyPressed(e -> {
            for (Map.Entry<KeyCodeCombination, ShortcutHandler> shortcut : keyboardShortcuts.entrySet()) {
                if (shortcut.getKey().match(e)) shortcut.getValue().handle();
            }
        });

        getChildren().add(mainPane = new LPane(this));
        setTitleBar(new LTitleBar(this));

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
                    if (event.getSceneY() > titleHeight) {
                        if (isHalved) {
                            if (event.getSceneX() >= (stageWidth - rp) && halveSide == 0) {
                                setCursor(Cursor.E_RESIZE);
                                resizeMode = 6;
                            } else if (event.getSceneX() <= rp && halveSide == 1) {
                                setCursor(Cursor.W_RESIZE);
                                resizeMode = 7;
                            } else {
                                setCursor(Cursor.DEFAULT);
                                resizeMode = 0;
                            }
                        } else {
                            if (event.getSceneY() >= (stageHeight - rp)) {
                                if (event.getX() <= rp) {
                                    setCursor(Cursor.SW_RESIZE);
                                    resizeMode = 4;
                                    return;
                                } else if (event.getSceneX() >= (stageWidth - rp)) {
                                    setCursor(Cursor.SE_RESIZE);
                                    resizeMode = 5;
                                    return;
                                }
                                setCursor(Cursor.S_RESIZE);
                                resizeMode = 2;
                            } else if (event.getSceneX() <= rp) {
                                setCursor(Cursor.W_RESIZE);
                                resizeMode = 1;
                            } else if (event.getSceneX() >= (stageWidth - rp)) {
                                setCursor(Cursor.E_RESIZE);
                                resizeMode = 3;
                            } else {
                                setCursor(Cursor.DEFAULT);
                                resizeMode = 0;
                            }
                        }
                    } else {
                        setCursor(Cursor.DEFAULT);
                        resizeMode = 0;
                    }
                } else {
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
            if (event.getButton().equals(MouseButton.PRIMARY) && isResizable && !isMaximized) {
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
            }
        });
    }
    public LBase(double width, double height, double titleHeight, BaseType baseType, Stage primaryStage) {
        this(width, height, titleHeight, baseType);
        this.primaryStage = primaryStage;
        exitDialog = new LExitDialog();
        EventHandler<WindowEvent> closeRequestHandler = windowEvent -> {
            windowEvent.consume();
            closeRequest();
        };
        primaryStage.setOnCloseRequest(closeRequestHandler);
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

    public void closeRequest() { //클로즈 리퀘스트 시. 이걸 바꿔야 함
        showExitDialog();
    }
    public void exit() { //정말 끝내기
        System.exit(0);
    }

    public final void showExitDialog() {
        exitDialog.show(primaryStage,
                (primaryStage.getWidth() - exitDialog.width) / 2 + primaryStage.getX(),
                (primaryStage.getHeight() - exitDialog.height) / 2 + primaryStage.getY()
        );
    }

    //메소드
    public boolean add(Node node) {
        titleBar.toFront();
        return mainPane.add(node);
    }
    public boolean remove(Node node) {
        return mainPane.remove(node);
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

    public void setPaneBackground(Background background) {
        mainPane.setBackground(background);
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
            if (isType(BaseType.MAXIMIZE)) titleBar.getMaximizeButton().setIcon(true);
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
            if (isType(BaseType.MAXIMIZE)) titleBar.getMaximizeButton().setIcon(false);
        } else if (isHalved) {
            mainPane.normalizeScale();
            isHalved = false;
            getStage().setWidth(mainPane.getScale() * firstSize.getWidth());
            getStage().setHeight(mainPane.getScale() * firstSize.getHeight() + titleHeight);
        }
        titleBar.setButtonWidth(getStage().getWidth());
        if (getStage().getY() < 0) {
            getStage().setY(0);
        }
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
            message.setFont(Font.loadFont(Constant.getResource("fonts/NotoSansKR-Regular.otf"), 14));
            message.setWrappingWidth(width);
            message.setLayoutX(0);
            message.setLayoutY(45);
            yes.setFont(Font.loadFont(Constant.getResource("fonts/NotoSansKR-Regular.otf"), 12));
            yes.setLayoutX((double) 110 / 3);
            yes.setLayoutY(85);
            yes.setPrefWidth(65);
            yes.setPrefHeight(30);
            yes.setOnAction(e -> {
                exit();
            });
            no.setFont(Font.loadFont(Constant.getResource("fonts/NotoSansKR-Regular.otf"), 12));
            no.setLayoutX(((double) 110 / 3) * 2 + 65);
            no.setLayoutY(85);
            no.setPrefWidth(65);
            no.setPrefHeight(30);
            no.setOnAction(e -> hide());

            pane.getChildren().add(message);
            pane.getChildren().add(yes);
            pane.getChildren().add(no);
            getContent().add(pane);
        }

        //함수
        @Override
        public void show(Window window, double v, double v1) {
            no.requestFocus();
            super.show(window, v, v1);
        }
    }
}
