package yourhand;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;


/** Provides the graphical interface for YourHand. */
public class MainWindow extends Application {
    private static final int MESSAGE_SPACING = 10;
    private static final int CONTENT_PADDING = 15;
    private static final int INPUT_PADDING = 10;
    private static final int MESSAGE_HORIZONTAL_PADDING = 12;
    private static final int MESSAGE_VERTICAL_PADDING = 9;
    private static final int MESSAGE_MAX_WIDTH = 430;
    private static final int AVATAR_SIZE = 32;
    private static final int AVATAR_RADIUS = AVATAR_SIZE / 2;
    private static final int AVATAR_FONT_SIZE = 11;
    private static final int MESSAGE_CORNER_RADIUS = 12;
    private static final int BUTTON_CORNER_RADIUS = 5;
    private static final int BUTTON_HORIZONTAL_PADDING = 18;
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;
    private static final String USER_COLOR = "#2563eb";
    private static final String CHATBOT_COLOR = "#7c3aed";

    private final YourHandEngine engine = new YourHandEngine();
    private final VBox messages = new VBox(MESSAGE_SPACING);
    private final TextField input = new TextField();
    private final ScrollPane conversation = new ScrollPane(messages);
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        messages.setPadding(new Insets(CONTENT_PADDING));
        messages.setStyle("-fx-background-color: #f5f7fa;");
        conversation.setFitToWidth(true);
        conversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversation.setStyle(null);

        input.setPromptText("Enter a command, e.g. find book");
        input.setStyle("-fx-font-size: 14px; -fx-padding: " + MESSAGE_VERTICAL_PADDING + "px;");
        Button send = new Button("Send");
        send.setDefaultButton(true);
        send.setStyle("-fx-background-color: " + USER_COLOR + "; -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: " + MESSAGE_VERTICAL_PADDING + "px "
                + BUTTON_HORIZONTAL_PADDING + "px; -fx-background-radius: " + BUTTON_CORNER_RADIUS + "px;");
        send.setOnAction(event -> submitCommand());
        input.setOnAction(event -> submitCommand());

        BorderPane root = new BorderPane(conversation);
        HBox inputArea = new HBox(10, input, send);
        inputArea.setPadding(new Insets(INPUT_PADDING));
        inputArea.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-width: 1 0 0 0;");
        HBox.setHgrow(input, Priority.ALWAYS);
        root.setBottom(inputArea);
        stage.setTitle("YourHand");
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
        addWelcomeBanner();
        scrollToBottom();
    }

    private void submitCommand() {
        String command = input.getText().trim();
        if (command.isBlank()) {
            return;
        }
        addMessage(command, true);
        addMessage(engine.execute(command), false);
        input.clear();
        scrollToBottom();
        if (command.equalsIgnoreCase("bye")) {
            stage.close();
        }
    }

    private void addWelcomeBanner() {
        addMessage("Selamat Datang 早上好! YourHand 为你服务\n"
                + "What are you here for?", false);
    }

    private void scrollToBottom() {
        Platform.runLater(() -> conversation.setVvalue(1.0));
    }

    private void addMessage(String text, boolean fromUser) {
        Label message = new Label(text.trim());
        message.setWrapText(true);
        message.setMaxWidth(MESSAGE_MAX_WIDTH);
        message.setPadding(new Insets(MESSAGE_VERTICAL_PADDING, MESSAGE_HORIZONTAL_PADDING,
                MESSAGE_VERTICAL_PADDING, MESSAGE_HORIZONTAL_PADDING));
        message.setStyle(fromUser
                ? "-fx-background-color: " + USER_COLOR + "; -fx-text-fill: white; -fx-background-radius: "
                + MESSAGE_CORNER_RADIUS + "px;"
                : "-fx-background-color: white; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;"
                + " -fx-border-radius: " + MESSAGE_CORNER_RADIUS + "px; -fx-background-radius: "
                + MESSAGE_CORNER_RADIUS + "px;");

        HBox row = new HBox(MESSAGE_HORIZONTAL_PADDING);
        row.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        Label avatar = createAvatar(fromUser);
        if (fromUser) {
            row.getChildren().addAll(message, avatar);
        } else {
            row.getChildren().addAll(avatar, message);
        }
        messages.getChildren().add(row);
    }

    private Label createAvatar(boolean fromUser) {
        String initials = fromUser ? "U" : "YH";
        Label avatar = new Label(initials);
        avatar.setTextFill(Color.WHITE);
        avatar.setFont(Font.font("System", FontWeight.BOLD, AVATAR_FONT_SIZE));
        avatar.setAlignment(Pos.CENTER);
        avatar.setMinSize(AVATAR_SIZE, AVATAR_SIZE);
        avatar.setMaxSize(AVATAR_SIZE, AVATAR_SIZE);
        avatar.setShape(new Circle(AVATAR_RADIUS));
        avatar.setStyle(fromUser
                ? "-fx-background-color: " + USER_COLOR + ";"
                : "-fx-background-color: " + CHATBOT_COLOR + ";");
        return avatar;
    }
}
