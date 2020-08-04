package Generator;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ApplicationUtils {
    private static double xOffset = 0;
    private static double yOffset = 0;

    public static Stage currentStage;

    public static FXMLLoader switchWindow(String URL, Class c) throws Exception{
        FXMLLoader loader = new FXMLLoader(c.getResource(URL));
        Parent root = loader.load();
        currentStage = new Stage();
        currentStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.show();
        enableMovingWindow(root, currentStage);
        return loader;
    }

    public static void closeCurrentWindow(Event event){
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    private static void enableMovingWindow(Parent parent, Stage stage) {
        parent.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        parent.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public static Alert AlertWindow(String title, String header, String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
        return alert;
    }
}
