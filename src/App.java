
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.Box;

public class App {

    static int cols;
    static int rows;
    

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        cols = 60;
        rows = 40;
        maze panel = new maze();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.generateMaze();
    }
}

class wall{
    int wall_x;
    int wall_y;
    int opposite_x;
    int opposite_y;
    public wall(int x, int y,int x2, int y2 ){
        this.wall_x=x;
        this.wall_y=y;
        this.opposite_x=x2;
        this.opposite_y=y2;
    }
}

class maze extends JPanel {

    int cols = 60;
    int rows = 40;
    int maze_arr[][] = new int[cols][rows];
    wall currentwall;
    ArrayList<wall> wall_coordinates = new ArrayList<wall>();

    public maze() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1200, 800));
        setOpaque(true);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                maze_arr[i][j] = 1;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) (g);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (maze_arr[i][j] == 0) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
                }
            }
        }
        if(currentwall!=null){
            g2d.setColor(Color.red);
            g2d.fillRect(currentwall.wall_x*cellWidth, currentwall.wall_y*cellWidth, cellWidth,cellHeight);
            g2d.setColor(Color.blue);
            g2d.fillRect(currentwall.opposite_x*cellWidth, currentwall.opposite_y*cellHeight, cellWidth, cellHeight);
        }
    }


    
    public void step_by_stepMaze() {
        //maze generation after initial setup
        if (wall_coordinates == null || wall_coordinates.isEmpty()) {
            return;
        }
        
            int randindex = (int) (Math.random() * wall_coordinates.size());
            wall w = wall_coordinates.get(randindex);
            int x = w.wall_x;
            int y = w.wall_y;
            currentwall=w;
            if(maze_arr[w.opposite_x][w.opposite_y]==1){
                maze_arr[x][y]=0;
                maze_arr[w.opposite_x][w.opposite_y]=0;
                if (inbounds(w.opposite_x+2, w.opposite_y) && maze_arr[w.opposite_x+2][w.opposite_y] == 1) {
                    wall_coordinates.add(new wall(w.opposite_x+1,w.opposite_y, w.opposite_x+2,w.opposite_y));
                }
                if (inbounds(w.opposite_x-2, w.opposite_y) && maze_arr[w.opposite_x-2][w.opposite_y] == 1) {
                    wall_coordinates.add(new wall(w.opposite_x-1,w.opposite_y, w.opposite_x-2,w.opposite_y));
                }
                if (inbounds(w.opposite_x, w.opposite_y+2) && maze_arr[w.opposite_x][w.opposite_y+2] == 1) {
                    wall_coordinates.add(new wall(w.opposite_x,w.opposite_y+1, w.opposite_x,w.opposite_y+2));
                }
                if (inbounds(w.opposite_x, w.opposite_y-2) && maze_arr[w.opposite_x][w.opposite_y-2] == 1) {
                    wall_coordinates.add(new wall(w.opposite_x,w.opposite_y-1, w.opposite_x,w.opposite_y-2));
                }
            } 
            
            wall_coordinates.remove(randindex);
        
        repaint();
    }

    // initial maze setup
    public void generateMaze() {

        this.maze_arr[1][1] = 0;
        // setting the beginning of prims algorithm
        
        if (inbounds(1, 2) && maze_arr[1][2] == 1) {
            wall_coordinates.add(new wall(1,2,1,3));
        }
        
        if (inbounds(2, 1) && maze_arr[2][1] == 1) {
            wall_coordinates.add(new wall(2,1,3,1));
        }
        startdraw();
    }


    // timer for each drawing step
    public void startdraw() {
        Timer timer = new Timer(50, e -> {
            step_by_stepMaze();
            if (wall_coordinates.isEmpty()) {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    // checks coordinates if in boudns
    public boolean inbounds(int x, int y) {
        return x >= 0 && x < this.maze_arr.length && y >= 0 && y < this.maze_arr[0].length;
    }
}
