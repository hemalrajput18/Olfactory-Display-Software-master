package Generator;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import Generator.Controllers.LaunchController;
import com.google.gson.Gson;
import javafx.application.Platform;
import processing.core.PApplet;
import processing.event.MouseEvent;
import static Generator.Main.main;

public class MazeGen extends PApplet {

    public static final int w = Integer.valueOf(main.properties.getProperty("CELL_WIDTH"));
    public static final int FPS = Integer.valueOf(main.properties.getProperty("FPS"));
    public static final int s = Integer.valueOf(main.properties.getProperty("SIZE"));

    static int rows, cols;
    static Stack<Cell> cell_stack = new Stack<Cell>();
    static int maze_endpoints[][] = {{-1, -1}, {-1, -1}};
    public static int mp[] = {-1, -1};
    Cell current;

    public static int visited_count = 1;
    public static Writer fw = null;

    public static final int noCells = s / w;

    public static int state = 0;
    public static List<Cell> decision_points = new ArrayList<>();
    public static List<JsonStructures.MazeStimuli> stimuliList = new ArrayList<>();

    double maze_difficulty = 0;
    public static double cellWidth = 0;
    public static double cellHeight = 0;
    public static double wallFraction = 0;
    public static String commType = "TCP/IP";
    public static String ip_address = "";
    public static int port = 0;

    public static Cell cellToEdit;

    public void settings() {
        size(s, s);
    }

    public void setup() {
        Cell.setWidth(w);

        try {
            fw = new OutputStreamWriter(new FileOutputStream("maze_output.txt"), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cols = floor(width / w);
        rows = floor(height / w);
        frameRate(FPS);
        Cell.cells = new Cell[rows][cols];
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                Cell cell = new Cell(i, j);
                Cell.cells[i][j] = cell;
            }
        }
        current = Cell.cells[0][0];//beginning
    }

    public void display() {
        for (int i = 0; i < Cell.cells.length; i++) {
            for (int j = 0; j < Cell.cells[i].length; j++) {
                show(Cell.cells[i][j]);
            }
        }
    }

    //Main program execution and state machine
    public void draw() {
        background(51);

        //uncommenting the code below will let you see the maze generation in real time. this will slow down the generation
        //display();

        switch (state) {
            case 0:
                display();
                if (maze_endpoints[0][0] != -1 && maze_endpoints[0][1] != -1 && maze_endpoints[1][0] != -1 && maze_endpoints[1][1] != -1) {
                    current = Cell.cells[maze_endpoints[0][0]][maze_endpoints[0][1]];
                    state = 1;
                }
                break;
            case 1:

                Thread t = new Thread(() -> {
                    Platform.runLater(() -> {
                        LaunchController c = Main.controller.getController();
                        c.updateGenerated(Math.round((double) visited_count / (Cell.cells.length * Cell.cells[0].length) * 100));
                    });
                });

                t.start();

                //recursive back tracker
                highlight(current);
                if (visited_count != rows * cols) {
                    //generated maze
                    current.visited = true;
                    Cell next = current.checkNeighbors(cols, rows);
                    if (next != null) {
                        next.visited = true;
                        visited_count++;
                        cell_stack.push(current);
                        removeWalls(current, next);
                        current = next;
                    } else if (cell_stack.size() > 0) {
                        current = cell_stack.pop();
                    }
                } else {
                    state = 2;
                }
                break;
            case 2:
                //maze generated
                //output maze to ascii text file
                Cell start = Cell.cells[maze_endpoints[0][0]][maze_endpoints[0][1]];
                Cell end = Cell.cells[maze_endpoints[1][0]][maze_endpoints[1][1]];

                mark(start);
                mark(end);

                findDecisionPoints();

                maze_difficulty = decision_points.size();

                Platform.runLater(() ->
                        ((LaunchController) (Main.controller.getController())).updateDifficulty(maze_difficulty)
                );

                state = 3;
                break;
            case 3:
                display();
                break;
            case 4:
                generateMazeASCII();
                Gson gson = new Gson();
                String gsonString = gson.toJson(Cell.cells);

                try {
                    Writer json = new OutputStreamWriter(new FileOutputStream("maze_data.json"), StandardCharsets.UTF_8);
                    json.write(gsonString);
                    json.close();
                    fw.close();
                    copyMazeFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                exit();
                break;
        }
    }

    public void addVisualStimulus(int x, int y) {
        Cell selectedCell = Cell.cells[x][y];
        setFill(123, 121, 333, 100, selectedCell);
        selectedCell.setStimulusType(Cell.StimulusType.VISUAL);
    }

    public void addOlfactoryStimulus(int x, int y) {
        Cell selectedCell = Cell.cells[x][y];
        setFill(0, 256, 126, 100, selectedCell);
        selectedCell.setStimulusType(Cell.StimulusType.OLFACTORY);
    }

    public void addAuditoryStimulus(int x, int y) {
        Cell selectedCell = Cell.cells[x][y];
        setFill(1, 1, 33, 100, selectedCell);
        selectedCell.setStimulusType(Cell.StimulusType.AUDIO);
    }

    public void addVisualStimulusArea(int x, int y, int x1, int y1) {
        Cell c;
        for(int i = x; i <= x1; i++){
            for(int j=y; j <= y1; j++){
                c = Cell.cells[i][j];
                c.setStimulusType(Cell.StimulusType.VISUAL);
                setFill(123, 121, 333, 100, c);
            }
        }
    }

    public void addOlfactoryStimulusArea(int x, int y, int x1, int y1) {
        Cell c;
        for(int i = x; i <= x1; i++){
            for(int j=y; j <= y1; j++){
                c = Cell.cells[i][j];
                c.setStimulusType(Cell.StimulusType.OLFACTORY); 
                setFill(0, 256, 126, 100, c);
            }
        }
    }

    public void addAuditoryStimulusArea(int x, int y, int x1, int y1) {
        Cell c;
        for(int i = x; i <= x1; i++){
            for(int j=y; j <= y1; j++){
                c = Cell.cells[i][j];
                c.setStimulusType(Cell.StimulusType.AUDIO);
                setFill(1, 1, 33, 100, c);
            }
        }
    }

    private void findDecisionPoints() {

        Cell start = Cell.cells[maze_endpoints[0][0]][maze_endpoints[0][1]];
        Cell end = Cell.cells[maze_endpoints[1][0]][maze_endpoints[1][1]];

        clearDecisionPoints();

        for (Cell[] row : Cell.cells) {
            for (Cell cell : row) {
                boolean a = cell.walls[0];
                boolean b = cell.walls[1];
                boolean c = cell.walls[2];
                boolean d = cell.walls[3];
                if ((!a && !b && !c && d) || (!a && !b && c && !d) || (!a && b && !c && !d) || (a && !b && !c && !d)) {
                    if (cell != start && cell != end) {
                        decision_points.add(cell);
                        mark(0, 255, 0, 100, cell);
                    }
                } else if (cell != start && cell != end) {
                    show(cell);
                }
            }
        }
    }

    private void clearDecisionPoints(){
        for(Cell c : decision_points) highlight(c);
        decision_points.clear();
    }

    private void handleWallCreate(int x, int y) {
        if (cellToEdit == null) {
            cellToEdit = Cell.cells[x][y];
        } else {
            Cell cell_1 = cellToEdit;
            Cell cell_2 = Cell.cells[x][y];
        }
    }

    private void handleWallDelete(int x, int y) {
        if (cellToEdit == null) {
            cellToEdit = Cell.cells[x][y];
        } else {
            Cell cell_1 = cellToEdit;
            Cell cell_2 = Cell.cells[x][y];

            int x1 = cell_1.i;
            int y1 = cell_1.j;

            int x2 = cell_2.i;
            int y2 = cell_2.j;

            int x_dis = x1 - x2;     // x displacement
            int y_dis = y1 - y2;     // y displacement
            int a = Math.abs(x_dis); // horizontal distance between cells
            int b = Math.abs(y_dis); // vertical distance between cells

            if (a == 1 && b == 0) {
                if (x_dis == -1) {
                    cell_1.walls[1] = false;
                    cell_2.walls[3] = false;
                } else {
                    cell_1.walls[3] = false;
                    cell_2.walls[1] = false;
                }
            } else if (a == 0 && b == 1) {
                if (y_dis == -1) {
                    cell_1.walls[2] = false;
                    cell_2.walls[0] = false;
                } else {
                    cell_1.walls[0] = false;
                    cell_2.walls[2] = false;
                }
            } else {
                if (x_dis < 0 && y_dis < 0) {
                    for (int i = x1; i <= x1 + a; i++) {
                        for (int j = y1; j <= y1 + b; j++) {
                            Cell temp_cell = Cell.cells[i][j];
                            temp_cell.walls[0] = false;
                            temp_cell.walls[1] = false;
                            temp_cell.walls[2] = false;
                            temp_cell.walls[3] = false;
                        }
                    }
                } else if (x_dis > 0 && y_dis > 0) {
                    for (int i = x1; i >= x1 - a; i--) {
                        for (int j = y1; j >= y1 - b; j--) {
                            Cell temp_cell = Cell.cells[i][j];
                            temp_cell.walls[0] = false;
                            temp_cell.walls[1] = false;
                            temp_cell.walls[2] = false;
                            temp_cell.walls[3] = false;
                        }
                    }
                }
            }
            findDecisionPoints();
            cellToEdit = null;
        }
    }

    private void handleStimulusCreation(int x, int y) {
        LaunchController.x = x;
        LaunchController.y = y;
        LaunchController c = Main.controller.getController();

        Platform.runLater(() -> {
            c.updateLabel();
            try {
                ApplicationUtils.switchWindow("Layouts/StimulusSelectionView.fxml", getClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleEndpointCreation(int x, int y) {
        if (x == 0 || y == 0 || x == cols - 1 || y == rows - 1) {
            Cell selectedCell = Cell.cells[x][y];
            if ((x != maze_endpoints[0][0] && y != maze_endpoints[0][1]) || (x != maze_endpoints[1][0] && y != maze_endpoints[1][1])) {
                if (maze_endpoints[0][0] == -1 && maze_endpoints[0][1] == -1) {
                    mark(selectedCell);
                    maze_endpoints[0][0] = x;
                    maze_endpoints[0][1] = y;
                } else if (x != maze_endpoints[0][0] && y != maze_endpoints[0][1]) {
                    mark(selectedCell);
                    maze_endpoints[1][0] = x;
                    maze_endpoints[1][1] = y;
                }

                if (y == 0) {
                    selectedCell.walls[0] = false; //top
                } else if (y == rows - 1) {
                    selectedCell.walls[2] = false; //bottom
                } else if (x == 0) {
                    selectedCell.walls[3] = false; //left
                } else if (x == cols - 1) {
                    selectedCell.walls[1] = false; //right
                }
            }
        }
    }

    public void handleStimulusAreaCreation(int x1, int y1, int x2, int y2){
        Cell c;

        int temp;
        if(x1 > x2){
            temp = x1;
            x1 = x2;
            x2 = temp;
        }

        if(y1 > y2){
            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        for(int i = x1; i <= x2; i++){
            for(int j=y1; j <= y2; j++){
                c = Cell.cells[i][j];
                setFill(0,0,0,100,c);
            }
        }

        LaunchController.x = x1;
        LaunchController.y = y1;
        LaunchController.x1 = x2;
        LaunchController.y1 = y2;

        Platform.runLater(() -> {
            try {
                ApplicationUtils.switchWindow("Layouts/StimulusSelectionView.fxml", getClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static String removeExtension(String s) {

        String separator = System.getProperty("file.separator");
        String filename;

        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }

        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;

        return filename.substring(0, extensionIndex);
    }

    public void mousePressed(MouseEvent evt) {
        int x = mouseX / w;
        int y = mouseY / w;
        System.out.println("Col: " + x + " Row: " + y);

        if (evt.getCount() == 2 && state == 3 &&
                LaunchController.currentMode == LaunchController.Mode.DefStim) {
            handleStimulusCreation(x, y);
            display();
        } else if (evt.getCount() == 1 && state == 3) {
            switch (LaunchController.currentMode) {
                case CreateWalls:
                    break;
                case DeleteWalls:
                    handleWallDelete(x, y);
                    break;
                case DefStimArea:
                    if(mp[0] == -1 && mp[1] == -1){
                        mp[0] = x;
                        mp[1] = y;
                    } else {
                        handleStimulusAreaCreation(mp[0], mp[1], x, y);
                    }
                    break;
            }
            display();
        } else if (state == 0) {
            handleEndpointCreation(x, y);
            display();
        }
    }

    private static void copyAssets(){
        System.out.println("Copying assets to: " + Main.UnityFilePath);
        File source = null;
        File destination = null;
        for(JsonStructures.MazeStimuli s : stimuliList){
            source = new File(s.getResourcePath());
            switch (s.getType()){
                case "AST":
                    destination = new File(Main.UnityFilePath.getPath() + "\\Audio\\" + source.getName());
                    break;
                case "VST":
                    destination = new File(Main.UnityFilePath.getPath() + "\\Images\\" + source.getName());
                    break;
            }

            if(source != null && destination != null){
                try{
                    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    s.setResourcePath(removeExtension(source.getName()));
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static void copyMazeFile(){
        File source = new File("maze_output.txt");
        File destination = new File(Main.UnityFilePath.getPath() + "\\Mazes\\maze_output.txt");
        if(source != null && destination != null){
            try{
                Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private static void generateMazeASCII() {
        try {
            Gson g = new Gson();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

            JsonStructures.MazeData md = new JsonStructures.MazeData(noCells, noCells, cellWidth, cellHeight, wallFraction,commType,ip_address,port);

            fw.write("* Maze map configuration\n\n");

            fw.write(g.toJson(md) + "\n\n\n");

            char[] empty_row = new char[4 * cols + 1];
            for (int a = 0; a < empty_row.length; a++) empty_row[a] = ' ';

            fw.write(" ===\n\n");

            String top_count = "*  ";
            for (int j = 0; j < cols; j++) {
                String str_j = "00" + (j + 1);
                String[] b = str_j.split("");
                top_count += b[b.length - 3] + b[b.length - 2] + b[b.length - 1];
                if (j != cols - 1) top_count += " ";
            }

            top_count += "\n";

            fw.write(top_count + "\n");

            for (int i = 0; i < Cell.cells.length; i++) {
                final int k = i;

                Callable<String> top_gen = () -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    boolean isFirstCorner = true;
                    for (int j = 0; j < Cell.cells[k].length; j++) {
                        Cell c = Cell.cells[j][k];
                        boolean top = c.walls[0];
                        if (isFirstCorner) {
                            stringBuilder.append(" +");
                            isFirstCorner = false;
                        }
                        stringBuilder.append(top ? "---" : "   ");
                        stringBuilder.append("+");
                    }
                    return stringBuilder.toString();
                };

                Callable<String> left_right_gen = () -> {
                    char[] row = empty_row;
                    for (int a = 0; a < row.length; a++) row[a] = ' ';
                    for (int j = 0; j < Cell.cells[k].length; j++) {
                        Cell c = Cell.cells[j][k];
                        boolean left = c.walls[3];
                        row[4 * j] = ((left) ? '|' : ' ');
                    }
                    row[row.length - 1] = '|';
                    return new String(row);
                };

                List<Callable<String>> callables = new ArrayList<>();
                callables.add(top_gen);
                callables.add(left_right_gen);
                List<Future<String>> result = executor.invokeAll(callables);
                String top_string = result.get(0).get();
                String right_string = result.get(1).get();

                String str_i = "00" + String.valueOf(i + 1);
                String[] a = str_i.split("");

                char[] temp = right_string.toCharArray();

                fw.write(" " + top_string + "\n");
                fw.write(a[a.length - 3] + " " + right_string + "\n");
                fw.write(a[a.length - 2] + " " + right_string + "\n");
                fw.write(a[a.length - 1] + " " + right_string + "\n");
            }
            String bottom_string = "  +";
            for (int p = 0; p < cols; p++) {
                if (!Cell.cells[p][cols - 1].walls[2]) {
                    bottom_string += "   +";
                } else {
                    bottom_string += "---+";
                }
            }

            fw.write(bottom_string + "\n\n");
            fw.write(" ===\n\n");

            copyAssets();

            fw.write("* Stimuli JSON\n\n");
            fw.write("{ \"Items\":");
            fw.write(g.toJson(stimuliList) + "}\n\n");
            fw.write(" ===\n\n");
            fw.write("* Configuration data JSON\n\n");

            Point start = new Point(maze_endpoints[0][0], maze_endpoints[0][1]);

            fw.write("{ \"start\":" + g.toJson(start) + "}" + "\n\n");
            fw.write(" ===");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void removeWalls(Cell a, Cell b) {
        int y_diff = a.getColumnIndex() - b.getColumnIndex();
        int x_diff = a.getRowIndex() - b.getRowIndex();
        if (x_diff == 1) {
            a.walls[0] = false; //top disabled
            b.walls[2] = false; //bottom disabled
        } else if (x_diff == -1) {
            a.walls[2] = false; //bottom disabled
            b.walls[0] = false; //top disabled
        }

        if (y_diff == 1) {
            a.walls[3] = false; //left disabled
            b.walls[1] = false; //right disabled
        } else if (y_diff == -1) {
            a.walls[1] = false; //right disabled
            b.walls[3] = false; //left disabled
        }
    }

    public void highlight(Cell c) {
        int x = c.i * w;
        int y = c.j * w;
        noStroke();
        this.setFill(0, 0, 255, 100, c);
        rect(x, y, w, w);
    }

    public void mark(int a, int b, int c, int d, Cell cl) {
        int x = cl.i * w;
        int y = cl.j * w;
        noStroke();
        this.setFill(a, b, c, d, cl);
        rect(x, y, w, w);
    }

    public void mark(Cell c) {
        int x = c.i * w;
        int y = c.j * w;
        noStroke();
        this.setFill(255, 0, 0, 100, c);
        rect(x, y, w, w);
    }

    public void setFill(int a, int b, int c, int d, Cell cl) {
        cl.current_fill[0] = a;
        cl.current_fill[1] = b;
        cl.current_fill[2] = c;
        cl.current_fill[3] = d;
    }

    public void show(Cell c) {
        int x = c.i * w;
        int y = c.j * w;

        //draws cells
        stroke(255);
        if (c.walls[0]) line(x, y, x + w, y); //top
        if (c.walls[1]) line(x + w, y, x + w, y + w);//right
        if (c.walls[2]) line(x + w, y + w, x, y + w);//bottom
        if (c.walls[3]) line(x, y + w, x, y);//left

        if (c.visited) {
            noStroke();
            fill(
                    c.current_fill[0],
                    c.current_fill[1],
                    c.current_fill[2],
                    c.current_fill[3])
            ;
            rect(x, y, w, w);
        }

    }

}