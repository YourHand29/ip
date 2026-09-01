package yourhand;

import javafx.application.Application;
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
    private final YourHandEngine engine = new YourHandEngine();
    private final VBox messages = new VBox(10);
    private final TextField input = new TextField();

    @Override
    public void start(Stage stage) {
        messages.setPadding(new Insets(15));
        messages.setStyle("-fx-background-color: #f5f7fa;");
        ScrollPane conversation = new ScrollPane(messages);
        conversation.setFitToWidth(true);
        conversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        input.setPromptText("Enter a command, e.g. find book");
        input.setStyle("-fx-font-size: 14px; -fx-padding: 9px;");
        Button send = new Button("Send");
        send.setDefaultButton(true);
        send.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9px 18px; -fx-background-radius: 5px;");
        send.setOnAction(event -> submitCommand());
        input.setOnAction(event -> submitCommand());

        BorderPane root = new BorderPane(conversation);
        HBox inputArea = new HBox(10, input, send);
        inputArea.setPadding(new Insets(10));
        inputArea.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-width: 1 0 0 0;");
        HBox.setHgrow(input, Priority.ALWAYS);
        root.setBottom(inputArea);
        stage.setTitle("YourHand");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    private void submitCommand() {
        String command = input.getText().trim();
        if (command.isBlank()) {
            return;
        }
        addMessage(command, true);
        addMessage(engine.execute(command), false);
        input.clear();
    }

    private void addMessage(String text, boolean fromUser) {
        Label message = new Label(text.trim());
        message.setWrapText(true);
        message.setMaxWidth(430);
        message.setPadding(new Insets(9, 12, 9, 12));
        message.setStyle(fromUser
                ? "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 12px;"
                : "-fx-background-color: white; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;"
                + " -fx-border-radius: 12px; -fx-background-radius: 12px;");

        HBox row = new HBox(8);
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
        avatar.setFont(Font.font("System", FontWeight.BOLD, 11));
        avatar.setAlignment(Pos.CENTER);
        avatar.setMinSize(32, 32);
        avatar.setMaxSize(32, 32);
        avatar.setShape(new Circle(16));
        avatar.setStyle(fromUser
                ? "-fx-background-color: #2563eb;"
                : "-fx-background-color: #7c3aed;");
        return avatar;
    }
}
