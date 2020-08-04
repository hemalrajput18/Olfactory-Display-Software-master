package Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Cell {
    public int i,j; //column and row number
    public static transient int w;
    public StimulusType stimulusType = StimulusType.NONE;
    public boolean[] walls = {true,true,true,true};//top, right, bottom, left
    public transient boolean visited = false;
    public transient int[] current_fill = {255,0,255,100};
    public transient static Cell[][] cells;
    
    public enum StimulusType{
        VISUAL,AUDIO,OLFACTORY,NONE
    }

    Cell(int i, int j){
        this.i = i;
        this.j = j;
    }

    public void setStimulusType(StimulusType s){
        this.stimulusType = s;
    }

    public static void setWidth(int width){
        w = width;
    }

    public int getColumnIndex(){return this.i;}

    public int getRowIndex(){return this.j;}

    public Cell checkNeighbors(int max_cols, int max_rows){
        List<Cell> neighbors = new ArrayList<Cell>();
        if(isCellValid(i,j+1,max_cols,max_rows) && !cells[i][j+1].visited)
            neighbors.add(cells[i][j+1]);

        if(isCellValid(i+1,j,max_cols,max_rows) && !cells[i+1][j].visited)
            neighbors.add(cells[i+1][j]);

        if(isCellValid(i,j-1,max_cols,max_rows) && !cells[i][j-1].visited)
            neighbors.add(cells[i][j-1]);

        if(isCellValid(i-1,j,max_cols,max_rows) && !cells[i-1][j].visited)
            neighbors.add(cells[i-1][j]);

        if(neighbors.size() > 0){
            int randomIndex = new Random().nextInt(neighbors.size());
            return neighbors.get(randomIndex);
        }

        return null;
    }

    private static boolean isCellValid(int x, int y, int max_cols, int max_rows){
        return (x < 0 || y < 0 || x > max_cols-1 || y > max_rows-1) ? false : true;
    }

}
