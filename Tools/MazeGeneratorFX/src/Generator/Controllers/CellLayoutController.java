package Generator.Controllers;

import Generator.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CellLayoutController implements Initializable {

    private String imgPath;
    private Rectangle selectedSurface;
    private int x = Generator.Controllers.LaunchController.x;
    private int y = Generator.Controllers.LaunchController.y;
    @FXML
    Rectangle FrontWallBox;

    @FXML
    Rectangle BackWallBox;

    @FXML
    Rectangle RightWallBox;

    @FXML
    Rectangle LeftWallBox;

    @FXML
    Rectangle CeilingBox;

    @FXML
    Rectangle FloorBox;

    @FXML
    Button AddButton;

    @FXML
    TextField ImagePath;

    @FXML
    public void Add(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(ApplicationUtils.currentStage);
        imgPath = selectedFile.toString();
        ImagePath.setText(imgPath);
    }

    private void selectSurface(Rectangle r){
        initBoxes();
        r.setFill(Color.GREEN);
        selectedSurface = r;
    }

    @FXML
    private void frontWallClick(){
        if(!FrontWallBox.isDisabled()){
            selectSurface(FrontWallBox);
        }
    }

    @FXML
    private void rightWallClick(){
        if(!RightWallBox.isDisabled()){
            selectSurface(RightWallBox);
        }
    }

    @FXML
    private void backWallClick(){
        if(!BackWallBox.isDisabled()){
            selectSurface(BackWallBox);
        }
    }

    @FXML
    private void leftWallClick(){
        if(!LeftWallBox.isDisabled()){
            selectSurface(LeftWallBox);
        }
    }

    @FXML
    private void ceilingClick(){
        selectSurface(CeilingBox);
    }

    @FXML
    private void floorClick(){
        selectSurface(FloorBox);
    }

    @FXML
    public void Confirm(ActionEvent event){
        Main.mazeGen.addVisualStimulus(x, y);
        try {
            JsonStructures.MazeStimuli mazeStimuli = new JsonStructures.MazeStimuli(
                    "VST",
                    new JsonStructures.Point2D(x + 1, y + 1),
                    new JsonStructures.Point3D(1, 1, 1),
                    imgPath,
                    "test_stim"
            );

            if(selectedSurface == FrontWallBox){
                mazeStimuli.setMetaData("FRONT");
            } else if(selectedSurface == RightWallBox){
                mazeStimuli.setMetaData("RIGHT");
            } else if(selectedSurface == BackWallBox){
                mazeStimuli.setMetaData("BACK");
            } else if(selectedSurface == LeftWallBox){
                mazeStimuli.setMetaData("LEFT");
            } else if(selectedSurface == CeilingBox){
                mazeStimuli.setMetaData("CEILING");
            } else {
                mazeStimuli.setMetaData("FLOOR");
            }

            MazeGen.stimuliList.add(mazeStimuli);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApplicationUtils.closeCurrentWindow(event);
    }

    @FXML
    private void Cancel(ActionEvent event){
        ApplicationUtils.closeCurrentWindow(event);
    }

    private void initBoxes(){
        Cell cell = Cell.cells[x][y];
        Color S_Color = Color.BLUE;
        Color D_Color = Color.GRAY;
        //front
        if(!cell.walls[0]){
            FrontWallBox.setFill(D_Color);
            FrontWallBox.setDisable(true);
        } else {
            FrontWallBox.setFill(S_Color);
        }

        //right
        if(!cell.walls[1]){
            RightWallBox.setFill(D_Color);
            RightWallBox.setDisable(true);
        } else {
            RightWallBox.setFill(S_Color);
        }

        //back
        if(!cell.walls[2]){
            BackWallBox.setFill(D_Color);
            BackWallBox.setDisable(true);
        } else {
            BackWallBox.setFill(S_Color);
        }

        //left
        if(!cell.walls[3]){
            LeftWallBox.setFill(D_Color);
            LeftWallBox.setDisable(true);
        } else {
            LeftWallBox.setFill(S_Color);
        }

        CeilingBox.setFill(S_Color);
        FloorBox.setFill(S_Color);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initBoxes();
    }
}
