package Generator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import processing.core.PApplet;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main extends Application {

    public static FXMLLoader controller;
    public static MazeGen mazeGen;
    public static Main main = new Main();
    public static final Properties properties = new Properties();
    private static final String propFileName = "config.properties";
    public static File UnityFilePath;


    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = ApplicationUtils.switchWindow("Layouts/LaunchView.fxml", getClass());
    }

    private void getProperties() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                System.out.println("Loading properties");
                properties.load(inputStream);
                System.out.println("Properties loaded");
            } else {
                throw new FileNotFoundException("Property file '" + propFileName + "' not found in classpath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        boolean validDirectory = false;

        while(!validDirectory){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.showSaveDialog(null);
            UnityFilePath = fileChooser.getSelectedFile();
            System.out.println("Directory selected: " + UnityFilePath.getPath());

            File temp = new File(UnityFilePath.getPath() + "/Assets/Resources");

            if((UnityFilePath = new File(UnityFilePath.getPath()+"/Assets/Resources")).exists()){
                validDirectory = true;
                System.out.println("Valid Directory Selected");
            } else {
                System.out.println("Invalid Directory Selected");
            }
        }


        String[] processingArgs = {"Maze Generator"};
        main.getProperties();
        mazeGen = new MazeGen();
        PApplet.runSketch(processingArgs, mazeGen);
        launch(args);
    }
}
