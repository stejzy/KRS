package linguistic.summary.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;

public class Gui extends Application {

    public static void main(String[] args){
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/input.fxml")));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Podsumowania lingwistyczne relacyjnych baz danych");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/KSR.png")).toExternalForm()));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
