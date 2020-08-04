package Generator.Controllers;

import Generator.ApplicationUtils;
import Generator.Main;
import Generator.MazeGen;
import com.jfoenix.controls.JFXButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LaunchController implements Initializable {

    public static int x;
    public static int y;
    public static int x1;
    public static int y1;

    private FileChooser fileChooser;
    private File selectedFile;
    public static Mode currentMode = Mode.None;

    public enum Mode{
        CreateWalls,
        DeleteWalls,
        DefStimArea,
        DefStim,
        None
    }

    @FXML
    TextField width_tb;

    @FXML
    TextField height_tb;

    @FXML
    TextField cellWidth_tb;

    @FXML
    TextField cellHeight_tb;

    @FXML
    TextField wallFraction_tb;

    @FXML
    TextField fileOutputPath;

    @FXML
    Label ip_address_label;

    @FXML
    Label port_label;

    @FXML
    TextField ip_address_tb;

    @FXML
    TextField port_tb;

    @FXML
    ChoiceBox commChoice;

    //TODO: Implement me
    @FXML
    JFXButton AddWallsButton;

    //TODO: Implement me
    @FXML
    JFXButton RemoveWallsButton;

    //TODO: Implement me
    @FXML
    JFXButton DefineStimAreaButton;

    @FXML
    Label SelectedCellLabel;

    @FXML
    Label diff;

    @FXML
    Label generated;

    public void updateLabel(){
        SelectedCellLabel.setText(x + ", " + y);
    }

    public void updateDifficulty(double d){diff.setText(String.valueOf(d));}

    public void updateGenerated(double x){generated.setText(x + "%");}

    @FXML
    private void DefStimulusArea(ActionEvent event){
        if(commChoice.getValue() == null){
            ApplicationUtils.AlertWindow("Application failed to generate stimulus area",
                    "Incorrect settings",
                    "Please select the communication protocol before creating stimuli",
                    Alert.AlertType.INFORMATION);
        } else {
            currentMode = Mode.DefStimArea;
        }
    }

    @FXML
    private void DeleteWalls(ActionEvent event){
        //While in this mode the user should be able to select two cells or a corner to corner range and delete the walls
        //in that range.
        currentMode = Mode.DeleteWalls;
    }

    @FXML
    private void CreateWalls(ActionEvent event){
        //While in this mode the user should be able to create new walls in the maze.
        currentMode = Mode.CreateWalls;
    }

    @FXML
    private void CreateStimulus(ActionEvent event){
        currentMode = Mode.DefStim;
    }

    @FXML
    private void ClearMode(ActionEvent event){
        currentMode = Mode.None;
    }

    @FXML
    private void FinishedButton(ActionEvent event){
        if(wallFraction_tb.getText().isEmpty() || cellWidth_tb.getText().isEmpty() || cellHeight_tb.getText().isEmpty()){
            ApplicationUtils.AlertWindow("Application failed to generate maze","Incorrect input","Please fill out all fields", Alert.AlertType.INFORMATION);
        } else {
            try{
                MazeGen.wallFraction = Double.valueOf(wallFraction_tb.getText());
                MazeGen.cellWidth = Double.valueOf(cellWidth_tb.getText());
                MazeGen.cellHeight = Double.valueOf(cellHeight_tb.getText());
                MazeGen.commType = String.valueOf(commChoice.getValue());
                MazeGen.ip_address = String.valueOf(ip_address_tb.getText());
                MazeGen.port = Integer.valueOf(port_tb.getText());
                MazeGen.state = 4;
            }catch (Exception e){
                System.out.println("Failed to parse arguments");
                ApplicationUtils.AlertWindow("Application failed to generate maze","Incorrect input","Please fill out all fields", Alert.AlertType.INFORMATION);
                e.printStackTrace();
            }
        }
    }

    private void makeNumericOnly(TextField tf){
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d+(\\.\\d{0,2})?|\\.?\\d{1,2})$*")) {
                tf.setText(newValue.replaceAll("[^(\\d+(\\.\\d{0,2})?|\\.?\\d{1,2})$]", ""));
            }
        });
    }

    private void setTCPOptionsVisible(boolean visible){
        port_label.setVisible(visible);
        port_tb.setVisible(visible);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeNumericOnly(width_tb);
        makeNumericOnly(height_tb);
        makeNumericOnly(cellHeight_tb);
        makeNumericOnly(cellWidth_tb);
        makeNumericOnly(wallFraction_tb);

        width_tb.setText(String.valueOf(MazeGen.noCells));
        height_tb.setText(String.valueOf(MazeGen.noCells));

        commChoice.getItems().add("TCP/IP");
        commChoice.getItems().add("MIDI");
        commChoice.setValue("TCP/IP");

        setTCPOptionsVisible(true);

        fileOutputPath.setText(Main.UnityFilePath.toString() + "\\maze.txt");

        commChoice.getSelectionModel().selectedIndexProperty().addListener((observableValue,number,number2)->{
            if(!commChoice.getValue().equals("TCP/IP")){
                setTCPOptionsVisible(true);
                ip_address_label.setText("IP Address");
                ip_address_tb.setText("127.0.0.1");
            } else {
                setTCPOptionsVisible(false);
                ip_address_label.setText("MiDi Device Name");
                ip_address_tb.setText("USB MIDI Interface");
            }

        });
    }
}
