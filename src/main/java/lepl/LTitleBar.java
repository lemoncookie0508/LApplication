package lepl;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;

public class LTitleBar extends AnchorPane {
    //필드
    private final LBase depend;

    private final ImageView titleIcon = new ImageView();
    //private final FlowPane titlePane = new FlowPane();
    private final Label title = new Label();

    private LExitButton exitButton;
    public LExitButton getExitButton() {
        return exitButton;
    }
    private LMaximizeButton maximizeButton;
    public LMaximizeButton getMaximizeButton() {
        return maximizeButton;
    }
    private LMinimizeButton minimizeButton;

    private double mouseX, mouseY;

    //생성자
    public LTitleBar(LBase depend) {
        this.depend = depend;

        setBackground(new Background(new BackgroundFill(
                Color.rgb(82, 82, 82),
                CornerRadii.EMPTY,
                Insets.EMPTY)));

        titleIcon.setPreserveRatio(true);
        titleIcon.setSmooth(true);
        titleIcon.setX(2);
        titleIcon.setLayoutY(1);
        getChildren().add(titleIcon);

        //titlePane.setAlignment(Pos.CENTER_LEFT);
        //titlePane.setHgap(0);
        //getChildren().add(titlePane);

        title.setFont(Font.loadFont(Constant.getResource("fonts/NotoSansKR-Bold.otf"), 13));
        title.setTextFill(Color.rgb(255, 255, 255));
        add(title);

        if (depend.isType(BaseType.EXIT)) {
            this.exitButton = new LExitButton();
            exitButton.setLayoutX(depend.getFirstSize().getWidth() - depend.getTitleHeight());
            exitButton.setLayoutY(0);
            getChildren().add(exitButton);
        }
        if (depend.isType(BaseType.MAXIMIZE)) {
            this.maximizeButton = new LMaximizeButton();
            maximizeButton.setLayoutX(depend.getFirstSize().getWidth() - depend.getTitleHeight() * (depend.isType(BaseType.EXIT) ? 2 : 1));
            maximizeButton.setLayoutY(0);
            getChildren().add(maximizeButton);
        }
        if (depend.isType(BaseType.MINIMIZE)) {
            this.minimizeButton = new LMinimizeButton();
            minimizeButton.setLayoutX(depend.getFirstSize().getWidth() - depend.getTitleHeight() * (1 + (depend.isType(BaseType.EXIT) ? 1 : 0) + (depend.isType(BaseType.MAXIMIZE) ? 1 : 0)));
            minimizeButton.setLayoutY(0);
            getChildren().add(minimizeButton);
        }

        setOnMouseMoved(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });
        setOnMouseReleased(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (depend.getStage().getY() <= 0) {
                    depend.getStage().setY(0);
                    if (!depend.isMaximized() && event.getScreenY() <= 0) {
                        depend.maximize();
                    }
                } else if (!depend.isMaximized() && event.getScreenX() <= 0) {
                    depend.halve(0);
                } else if (!depend.isMaximized() && event.getScreenX() >= Constant.SCREEN_SIZE.getWidth() - 1) {
                    depend.halve(1);
                }
            }
        });
        setOnMouseDragged(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (depend.isMaximized()) {
                    depend.normalize();
                    if (depend.isHalved()) depend.normalize();
                    depend.getStage().setX(event.getScreenX() - (event.getScreenX() * (depend.getStage().getWidth() - getButtonNumber() * depend.getTitleHeight()) / (Constant.SCREEN_SIZE.getWidth() - getButtonNumber() * depend.getTitleHeight())));
                    mouseX = event.getScreenX() - depend.getStage().getX();
                } else if (depend.isHalved()) {
                    double widthSave = depend.getStage().getWidth();
                    depend.normalize();
                    depend.getStage().setX(depend.getStage().getX() + event.getSceneX() - (event.getSceneX() * (depend.getStage().getWidth() - getButtonNumber() * depend.getTitleHeight()) / (widthSave - getButtonNumber() * depend.getTitleHeight())));
                    mouseX = event.getScreenX() - depend.getStage().getX();
                }
                else {
                    depend.getStage().setX(event.getScreenX() - mouseX);
                    depend.getStage().setY(Math.min(event.getScreenY(), Screen.getPrimary().getVisualBounds().getHeight()) - mouseY);
                }
            }
        });
    }

    //메소드
    public boolean add(Node node) {
        //return titlePane.getChildren().add(node);
        return getChildren().add(node);
    }

    public void setIcon(Image icon) {
        titleIcon.setImage(icon);
        titleIcon.setFitHeight(depend.getTitleHeight() - 0.5);
        //titlePane.setLayoutX(titleIcon.getImage().getWidth() * titleIcon.getFitHeight() / titleIcon.getImage().getHeight() + 6);
        title.setLayoutX(titleIcon.getImage().getWidth() * titleIcon.getFitHeight() / titleIcon.getImage().getHeight() + 6);
    }
    public void setIcon(String url) {
        setIcon(new Image(url));
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setButtonWidth(double width) {
        if (depend.isType(BaseType.EXIT)) {
        exitButton.setLayoutX(width - depend.getTitleHeight());
        }
        if (depend.isType(BaseType.MAXIMIZE)) {
            maximizeButton.setLayoutX(width - depend.getTitleHeight() * (depend.isType(BaseType.EXIT) ? 2 : 1));
        }
        if (depend.isType(BaseType.MINIMIZE)) {
            minimizeButton.setLayoutX(width - depend.getTitleHeight() * (1 + (depend.isType(BaseType.EXIT) ? 1 : 0) + (depend.isType(BaseType.MAXIMIZE) ? 1 : 0)));
        }

        //titlePane.setPrefWidth(width - getButtonNumber() * depend.getTitleHeight() - title.getLayoutX());
    }

    public int getButtonNumber() {
        return (depend.isType(BaseType.EXIT) ? 1 : 0) + (depend.isType(BaseType.MAXIMIZE) ? 1 : 0) + (depend.isType(BaseType.MINIMIZE) ? 1 : 0);
    }

    //중첩 클래스
    public class LExitButton extends Button {
        private final Image exitButtonBasicImage = new Image(Constant.getImageResource("exitButtonBasic.png"));
        private final Image exitButtonEnteredImage = new Image(Constant.getImageResource("exitButtonEntered.png"));
        private final Background basic;
        private final Background entered;

        //생성자
        public LExitButton() {
            double titleHeight = depend.getTitleHeight();
            setPrefSize(titleHeight, titleHeight);
            setFocusTraversable(false);

            basic = new Background(new BackgroundImage(
                    exitButtonBasicImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(titleHeight, titleHeight, false, false, false, false)
            ));
            entered = new Background(new BackgroundImage(
                    exitButtonEnteredImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(titleHeight, titleHeight, false, false, false, false)
            ));

            setBackground(basic);

            setOnMouseEntered(event -> {
                setBackground(entered);
                setCursor(Cursor.HAND);
            });
            setOnMouseExited(event -> {
                setBackground(basic);
                setCursor(Cursor.DEFAULT);
            });
            setOnAction(event -> {
                if (depend.isMain()) {
                    depend.closeRequest();
                } else {
                    depend.getStage().hide();
                }
            });
        }
    }

    public class LMaximizeButton extends Button {
        //필드
        private final Image maximizeButtonBasicImage = new Image(Constant.getImageResource("maximizeButtonBasic.png"));
        private final Image maximizeButtonEnteredImage = new Image(Constant.getImageResource("maximizeButtonEntered.png"));
        private final Image maximizeButtonMaximizedBasicImage = new Image(Constant.getImageResource("maximizeButtonMaximizedBasic.png"));
        private final Image maximizeButtonMaximizedEnteredImage = new Image(Constant.getImageResource("maximizeButtonMaximizedEntered.png"));

        private final Background basic;
        private final Background basicMaximized;

        //생성자
        public LMaximizeButton() {
            double titleHeight = depend.getTitleHeight();
            setPrefSize(titleHeight, titleHeight);
            setFocusTraversable(false);

            basic = new Background(new BackgroundImage(
                    maximizeButtonBasicImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(titleHeight, titleHeight, false, false, false, false)
            ));
            Background entered1 = new Background(new BackgroundImage(
                    maximizeButtonEnteredImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(titleHeight, titleHeight, false, false, false, false)
            ));
            basicMaximized = new Background(new BackgroundImage(
                    maximizeButtonMaximizedBasicImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(titleHeight, titleHeight, false, false, false, false)
            ));
            Background entered2 = new Background(new BackgroundImage(
                    maximizeButtonMaximizedEnteredImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(titleHeight, titleHeight, false, false, false, false)
            ));

            setBackground(basic);

            setOnMouseEntered(event -> {
                setBackground(depend.isMaximized() ? entered2 : entered1);
                setCursor(Cursor.HAND);
            });
            setOnMouseExited(event -> {
                setBackground(depend.isMaximized() ? basicMaximized : basic);
                setCursor(Cursor.DEFAULT);
            });
            setOnAction(event -> {
                if (depend.isMaximized()) {
                    depend.normalize();
                } else {
                    depend.maximize();
                }
            });
        }

        //메소드
        public void setIcon(boolean isMaximized) {
            setBackground(isMaximized ? basicMaximized : basic);
        }
    }

    public class LMinimizeButton extends Button {
        //필드
        private final Image minimizeButtonBasicImage = new Image(Constant.getImageResource("minimizeButtonBasic.png"));
        private final Image minimizeButtonEnteredImage = new Image(Constant.getImageResource("minimizeButtonEntered.png"));

        //생성자
        public LMinimizeButton() {
            double titleHeight = depend.getTitleHeight();
            setPrefSize(titleHeight, titleHeight);
            setFocusTraversable(false);

            Background basic = new Background(new BackgroundImage(
                    minimizeButtonBasicImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(titleHeight, titleHeight, false, false, false, false)
            ));
            Background entered = new Background(new BackgroundImage(
                    minimizeButtonEnteredImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(titleHeight, titleHeight, false, false, false, false)
            ));

            setBackground(basic);

            setOnMouseEntered(event -> {
                setBackground(entered);
                setCursor(Cursor.HAND);
            });
            setOnMouseExited(event -> {
                setBackground(basic);
                setCursor(Cursor.DEFAULT);
            });
            setOnAction(event -> {
                depend.getStage().setIconified(true);
            });
        }
    }
}
