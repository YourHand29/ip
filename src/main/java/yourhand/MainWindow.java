package yourhand;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/** Provides the graphical interface for YourHand. */
public class MainWindow extends Application {
    private final YourHandEngine engine = new YourHandEngine();
    private final TextArea conversation = new TextArea();
    private final TextField input = new TextField();

    @Override
    public void start(Stage stage) {
        conversation.setEditable(false);
        conversation.setWrapText(true);
        input.setPromptText("Enter a command, e.g. find book");
        Button send = new Button("Send");
        send.setOnAction(event -> submitCommand());
        input.setOnAction(event -> submitCommand());

        BorderPane root = new BorderPane(conversation);
        root.setBottom(new HBox(input, send));
        stage.setTitle("YourHand");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    private void submitCommand() {
        String command = input.getText().trim();
        if (command.isBlank()) {
            return;
        }
        conversation.appendText("> " + command + "\n");
        conversation.appendText(engine.execute(command));
        conversation.appendText("\n");
        input.clear();
    }
}
