package Generator.Controllers;

import Generator.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static Generator.MazeGen.mp;

public class StimulusSelectionController implements Initializable {

    private FileChooser fileChooser;
    private File selectedFile;

    public static JsonStructures.MazeStimuli setObjectProperties(Cell.StimulusType type){
        int x = LaunchController.x;
        int y = LaunchController.y;
        double x_offset = 0.0;
        double z_offset = 0.0;
        JsonStructures.Point3D size = new JsonStructures.Point3D(1, 1, 1);
        JsonStructures.Point2D position = new JsonStructures.Point2D(x + 1, y + 1);

        if(LaunchController.currentMode == LaunchController.Mode.DefStimArea){
            int x1 = LaunchController.x1;
            int y1 = LaunchController.y1;

            double x_mid = ((x+x1)/2.0) + 1;
            double y_mid = ((y+y1)/2.0) + 1;

            size = new JsonStructures.Point3D(Math.abs(x-x1) + 1, 1, Math.abs(y-y1) + 1);
            position = new JsonStructures.Point2D((int)x_mid, (int)y_mid);
            x_offset = x_mid - Math.floor(x_mid);
            z_offset = y_mid - Math.floor(y_mid);
            UpdateMaze(type,x,y,x1,y1);
        } else {
            UpdateMaze(type,x,y);
        }

        JsonStructures.MazeStimuli toReturn = new JsonStructures.MazeStimuli(position,size);
        toReturn.setXZOffset(x_offset,z_offset);
        return toReturn;
    }

    public static void UpdateMaze(Cell.StimulusType stimType, int... points){
        if(points.length == 4){
            int x1 = points[0];
            int y1 = points[1];
            int x2 = points[2];
            int y2 = points[3];
            switch(stimType){
                case AUDIO:
                    Main.mazeGen.addAuditoryStimulusArea(x1,y1,x2,y2);
                    break;
                case VISUAL:
                    Main.mazeGen.addVisualStimulusArea(x1,y1,x2,y2);
                    break;
                case OLFACTORY:
                    Main.mazeGen.addOlfactoryStimulusArea(x1,y1,x2,y2);
                    break;
            }
        } else {
            int x1 = points[0];
            int y1 = points[1];
            switch(stimType){
                case AUDIO:
                    Main.mazeGen.addAuditoryStimulus(x1,y1);
                    break;
                case VISUAL:
                    Main.mazeGen.addVisualStimulus(x1,y1);
                    break;
                case OLFACTORY:
                    Main.mazeGen.addOlfactoryStimulus(x1,y1);
                    break;
            }
        }

    }

    @FXML
    private void NewAudioStimulus(ActionEvent event) {
        JsonStructures.MazeStimuli mazeStimuli = setObjectProperties(Cell.StimulusType.AUDIO);

        fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(ApplicationUtils.currentStage);

        try {
            long time_duration = 100l;
            mazeStimuli.setType("AST");
            mazeStimuli.setSTName("test_stim");
            mazeStimuli.setResourcePath(selectedFile.getPath());
            mazeStimuli.setMetaData(time_duration);
            MazeGen.stimuliList.add(mazeStimuli);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mp[0] = -1;
        mp[1] = -1;
        ApplicationUtils.closeCurrentWindow(event);
    }

    @FXML
    private void NewVisualStimulus(ActionEvent event) {
        try {
            ApplicationUtils.switchWindow("../Layouts/CellView.fxml", getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ApplicationUtils.closeCurrentWindow(event);
    }

    @FXML
    private void NewOlfactoryStimulus(ActionEvent event) {

        JsonStructures.MazeStimuli mazeStimuli = setObjectProperties(Cell.StimulusType.OLFACTORY);

        try {
            String temp_channel = "1";
            long time_duration = 100l; //milliseconds
            mazeStimuli.setType("OST");
            mazeStimuli.setSTName(temp_channel);
            mazeStimuli.setMetaData(time_duration);
            MazeGen.stimuliList.add(mazeStimuli);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mp[0] = -1;
        mp[1] = -1;
        ApplicationUtils.closeCurrentWindow(event);
    }

    @FXML
    public void Cancel(ActionEvent event) {

        for(int i = LaunchController.x; i <= LaunchController.x1; i++){
            for(int j=LaunchController.y; j <= LaunchController.y1; j++){
                Main.mazeGen.highlight(Cell.cells[i][j]);
            }
        }

        LaunchController.x = 0;
        LaunchController.y = 0;
        LaunchController.x1 = 0;
        LaunchController.y1 = 0;

        mp[0] = -1;
        mp[1] = -1;

        ApplicationUtils.closeCurrentWindow(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
