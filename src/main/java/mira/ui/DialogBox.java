package mira.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one user or Mira message with a compact speaker avatar.
 */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML = "/view/DialogBox.fxml";

    @FXML
    private Label speaker;

    @FXML
    private Label dialog;

    @FXML
    private Label avatarText;

    /**
     * Loads the reusable dialog layout and fills in its message details.
     *
     * @param text Message to display.
     * @param isUser Whether the message was entered by the user.
     */
    private DialogBox(String text, boolean isUser) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(DIALOG_BOX_FXML));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box.", exception);
        }

        dialog.setText(text);
        if (isUser) {
            speaker.setText("You");
            avatarText.setText("YOU");
            getStyleClass().add("user-dialog");
        } else {
            speaker.setText("Mira");
            avatarText.setText("M");
            getStyleClass().add("mira-dialog");
            flip();
        }
    }

    /**
     * Creates a dialog aligned to the user's side of the window.
     *
     * @param text User message to display.
     * @return A user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, true);
    }

    /**
     * Creates a dialog aligned to Mira's side of the window.
     *
     * @param text Mira response to display.
     * @return A Mira dialog box.
     */
    public static DialogBox getMiraDialog(String text) {
        return new DialogBox(text, false);
    }

    /**
     * Places Mira's avatar before her message and aligns both to the left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
