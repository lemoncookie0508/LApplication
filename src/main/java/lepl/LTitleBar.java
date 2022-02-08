package lepl;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;

public class LTitleBar extends AnchorPane {
    //필드
    private final LBase defend;

    private final ImageView titleIcon = new ImageView();
    private final Label title = new Label();

    private LExitButton exitButton;
    private LMaximizeButton maximizeButton;
    public LMaximizeButton getMaximizeButton() {
        return maximizeButton;
    }
    private LMinimizeButton minimizeButton;

    private double mouseX, mouseY;

    //생성자
    public LTitleBar(LBase defend) {
        this.defend = defend;

        setBackground(new Background(new BackgroundFill(
                Color.rgb(82, 82, 82),
                CornerRadii.EMPTY,
                Insets.EMPTY)));

        titleIcon.setPreserveRatio(true);
        titleIcon.setSmooth(true);
        titleIcon.setLayoutX(1);
        titleIcon.setLayoutY(1);
        add(titleIcon);

        title.setFont(Font.loadFont(Constant.PATH_FONT + "CookieRun Bold.ttf", 13));
        title.setLayoutY(-0.5);
        title.setTextFill(Color.rgb(255, 255, 255));
        add(title);

        if (defend.isType(BaseType.EXIT)) {
            this.exitButton = new LExitButton();
            exitButton.setLayoutX(defend.getFirstSize().getWidth() - defend.getTitleHeight());
            exitButton.setLayoutY(0);
            add(exitButton);
        }
        if (defend.isType(BaseType.MAXIMIZE)) {
            this.maximizeButton = new LMaximizeButton();
            maximizeButton.setLayoutX(defend.getFirstSize().getWidth() - defend.getTitleHeight() * (defend.isType(BaseType.EXIT) ? 2 : 1));
            maximizeButton.setLayoutY(0);
            add(maximizeButton);
        }
        if (defend.isType(BaseType.MINIMIZE)) {
            this.minimizeButton = new LMinimizeButton();
            minimizeButton.setLayoutX(defend.getFirstSize().getWidth() - defend.getTitleHeight() * (1 + (defend.isType(BaseType.EXIT) ? 1 : 0) + (defend.isType(BaseType.MAXIMIZE) ? 1 : 0)));
            minimizeButton.setLayoutY(0);
            add(minimizeButton);
        }

        setOnMouseMoved(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });
        setOnMouseReleased(event -> {
            if (defend.getStage().getY() <= 0) {
                defend.getStage().setY(0);
                if (!defend.isMaximized() && event.getScreenY() <= 0) {
                    defend.maximize();
                }
            }
        });
        setOnMouseDragged(event -> {
            if (defend.isMaximized()) {
                defend.normalize();
                defend.getStage().setX(event.getScreenX() - (event.getScreenX() * (defend.getStage().getWidth() - getButtonNumber() * defend.getTitleHeight()) / (Constant.SCREEN_SIZE.getWidth() - getButtonNumber() * defend.getTitleHeight())));
                mouseX = event.getScreenX() - defend.getStage().getX();
            } else {
                defend.getStage().setX(event.getScreenX() - mouseX);
                defend.getStage().setY(Math.min(event.getScreenY(), Screen.getPrimary().getVisualBounds().getHeight()) - mouseY);
            }
        });
    }

    //메소드
    public boolean add(Node node) {
        return getChildren().add(node);
    }

    public void setIcon(Image icon) {
        titleIcon.setImage(icon);
        titleIcon.setFitHeight(defend.getTitleHeight());
        title.setLayoutX(titleIcon.getImage().getWidth() * titleIcon.getFitHeight() / titleIcon.getImage().getHeight() + 5);
    }
    public void setIcon(String url) {
        setIcon(new Image(url));
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setButtonWidth(double width) {
        if (defend.isType(BaseType.EXIT)) {
        exitButton.setLayoutX(width - defend.getTitleHeight());
        }
        if (defend.isType(BaseType.MAXIMIZE)) {
            maximizeButton.setLayoutX(width - defend.getTitleHeight() * (defend.isType(BaseType.EXIT) ? 2 : 1));
        }
        if (defend.isType(BaseType.MINIMIZE)) {
            minimizeButton.setLayoutX(width - defend.getTitleHeight() * (1 + (defend.isType(BaseType.EXIT) ? 1 : 0) + (defend.isType(BaseType.MAXIMIZE) ? 1 : 0)));
        }

        title.setPrefWidth(width - getButtonNumber() * defend.getTitleHeight() - title.getLayoutX());
    }

    public int getButtonNumber() {
        return (defend.isType(BaseType.EXIT) ? 1 : 0) + (defend.isType(BaseType.MAXIMIZE) ? 1 : 0) + (defend.isType(BaseType.MINIMIZE) ? 1 : 0);
    }

    //중첩 클래스
    public class LExitButton extends Button {
        //필드
        private static final Image exitButtonBasicImage = new Image(Constant.PATH_IMAGE_FRAME + "exitButtonBasic.png");
        private static final Image exitButtonEnteredImage = new Image(Constant.PATH_IMAGE_FRAME + "exitButtonEntered.png");

        //생성자
        public LExitButton() {
            double titleHeight = defend.getTitleHeight();
            setPrefSize(titleHeight, titleHeight);

            Background basic = new Background(new BackgroundImage(
                    exitButtonBasicImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(titleHeight, titleHeight, false, false, false, false)
            ));
            Background entered = new Background(new BackgroundImage(
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
                defend.close();
            });
        }
    }

    public class LMaximizeButton extends Button {
        //필드
        private static final Image maximizeButtonBasicImage = new Image(Constant.PATH_IMAGE_FRAME + "maximizeButtonBasic.png");
        private static final Image maximizeButtonEnteredImage = new Image(Constant.PATH_IMAGE_FRAME + "maximizeButtonEntered.png");
        private static final Image maximizeButtonMaximizedBasicImage = new Image(Constant.PATH_IMAGE_FRAME + "maximizeButtonMaximizedBasic.png");
        private static final Image maximizeButtonMaximizedEnteredImage = new Image(Constant.PATH_IMAGE_FRAME + "maximizeButtonMaximizedEntered.png");

        private final Background basic;
        private final Background basicMaximized;

        //생성자
        public LMaximizeButton() {
            double titleHeight = defend.getTitleHeight();
            setPrefSize(titleHeight, titleHeight);

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
                setBackground(defend.isMaximized() ? entered2 : entered1);
                setCursor(Cursor.HAND);
            });
            setOnMouseExited(event -> {
                setBackground(defend.isMaximized() ? basicMaximized : basic);
                setCursor(Cursor.DEFAULT);
            });
            setOnAction(event -> {
                if (defend.isMaximized()) {
                    defend.normalize();
                } else {
                    defend.maximize();
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
        private static final Image minimizeButtonBasicImage = new Image(Constant.PATH_IMAGE_FRAME + "minimizeButtonBasic.png");
        private static final Image minimizeButtonEnteredImage = new Image(Constant.PATH_IMAGE_FRAME + "minimizeButtonEntered.png");

        //생성자
        public LMinimizeButton() {
            double titleHeight = defend.getTitleHeight();
            setPrefSize(titleHeight, titleHeight);

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
                defend.getStage().setIconified(true);
            });
        }
    }
}
