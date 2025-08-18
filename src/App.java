
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Timer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import java.util.*;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.w3c.dom.Node;

public class App {

    static int cols;
    static int rows;

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        cols = 60;
        rows = 40;
        Font title_font = new Font("Monospaced", Font.BOLD, 12);


        JPanel buttonpanel_Maze_Algorithm = new JPanel();
        buttonpanel_Maze_Algorithm.setLayout(new BoxLayout(buttonpanel_Maze_Algorithm, BoxLayout.Y_AXIS));
        JLabel maze_label = new JLabel(" Maze Creation Algorithms ");
        maze_label.setFont(title_font);
        JButton prim = new JButton(" Prim's Maze Algorithm      ");

        
        buttonpanel_Maze_Algorithm.add(maze_label);
        buttonpanel_Maze_Algorithm.add(Box.createVerticalStrut(20));
       
        buttonpanel_Maze_Algorithm.add(prim);
        buttonpanel_Maze_Algorithm.add(Box.createVerticalStrut(20));

        JPanel buttonpanel_Solver_Algorithm = new JPanel();
        buttonpanel_Solver_Algorithm.setLayout(new BoxLayout(buttonpanel_Solver_Algorithm, BoxLayout.Y_AXIS));
        JLabel solver_label = new JLabel("  Maze Solver Algorithms ");
        solver_label.setFont(title_font);
        JButton DFS = new JButton(" Depth First Search Solver    ");
        JButton BFS = new JButton(" Breadth First Search Solver");

        buttonpanel_Solver_Algorithm.add(solver_label);
        buttonpanel_Solver_Algorithm.add(Box.createVerticalStrut(20));
        buttonpanel_Solver_Algorithm.add(DFS);
        buttonpanel_Solver_Algorithm.add(Box.createVerticalStrut(20));
        buttonpanel_Solver_Algorithm.add(BFS);

        maze panel = new maze();
        
        JSplitPane button_organizer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonpanel_Maze_Algorithm,buttonpanel_Solver_Algorithm);
        JSplitPane pane_organizer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, button_organizer, panel);
        
        frame.setTitle("Maze Visualizer/Solver");
        frame.add(pane_organizer);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // shows the full maze completed instead of making it
        //panel.GenerateMaze();
        
    }
}

class wall {

    int wall_x;
    int wall_y;
    int opposite_x;
    int opposite_y;

    public wall(int x, int y, int x2, int y2) {
        this.wall_x = x;
        this.wall_y = y;
        this.opposite_x = x2;
        this.opposite_y = y2;
    }
}

interface MazeGenerator {

    public void GenerateMaze();

    public void initialize();

    public boolean step();

}

class maze extends JPanel {

    int cols = 60;
    int rows = 40;
    int maze_arr[][] = new int[cols][rows];
    wall currentwall;
    ArrayList<wall> wall_coordinates = new ArrayList<wall>();
    DFS_Solver solver_DFS = new DFS_Solver(maze_arr, cols, rows);
    BFS_Solver solver_BFS = new BFS_Solver(maze_arr, cols, rows);
    MazeGenerator generator;

    public maze() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1200, 800));
        setOpaque(true);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                maze_arr[i][j] = 1;
            }
        }
        generator = new Prims_Algorithm_Maze(cols, rows, maze_arr, wall_coordinates, w -> currentwall = w);
        generator.initialize();
        startdraw();
    }

    public void GenerateMaze(){
        if(generator!=null){
            generator.GenerateMaze();
        }
    }

    public void step_by_stepMaze(){
        generator.step();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) (g);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        // Maze drawing logic
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (maze_arr[i][j] == 0) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
                }
            }
        }
        if (currentwall != null) {
            g2d.setColor(Color.red);
            g2d.fillRect(currentwall.wall_x * cellWidth, currentwall.wall_y * cellHeight, cellWidth, cellHeight);
            g2d.setColor(Color.blue);
            g2d.fillRect(currentwall.opposite_x * cellWidth, currentwall.opposite_y * cellHeight, cellWidth, cellHeight);
        }

        // DFS drawing logic
        // g2d.setColor(Color.gray);
        // for(Point p : solver_DFS.visitedNodes()){
        //     g2d.fillRect(p.x * cellWidth, p.y * cellHeight, cellWidth, cellHeight);
        // }
        // Point current = solver_DFS.getNode();
        // if(current!=null){
        //     g2d.setColor(Color.yellow);
        //     g2d.fillRect(current.x * cellWidth, current.y * cellHeight, cellWidth, cellHeight);
        // }
        // if(solver_DFS.isDone()){
        //     g2d.setColor(Color.green);
        //     for(Point p: solver_DFS.getCurrentPath()){
        //     g2d.fillRect(p.x * cellWidth, p.y * cellHeight, cellWidth, cellHeight);
        //     }
        // }
        g2d.setColor(Color.gray);
        for (Point p : solver_BFS.visitedNodes()) {
            g2d.fillRect(p.x * cellWidth, p.y * cellHeight, cellWidth, cellHeight);
        }
        Point current = solver_BFS.getNode();
        if (current != null) {
            g2d.setColor(Color.yellow);
            g2d.fillRect(current.x * cellWidth, current.y * cellHeight, cellWidth, cellHeight);
        }
        if (solver_BFS.isDone()) {
            g2d.setColor(Color.green);
            for (Point p : solver_BFS.getCurrentPath()) {
                g2d.fillRect(p.x * cellWidth, p.y * cellHeight, cellWidth, cellHeight);
            }
        }

    }

    private boolean solverstarted = false;

    // timer for each drawing step
    public void startdraw() {
        Timer MazeGeneration_Timer = new Timer(25, e -> {

            if (wall_coordinates.isEmpty()) {

                // if (!solverstarted) {
                //     solverstarted=true;
                //     currentwall = null;
                //     repaint();
                //     ((Timer) e.getSource()).stop();
                //     Timer Solver_Timer = new Timer(25, r -> {
                //         if (!solver_DFS.isDone()) {
                //             solver_DFS.step_by_stepSolver();
                //             repaint();
                //         } else {
                //             ((Timer) r.getSource()).stop();
                //         }
                //     });
                //     Solver_Timer.start();
                // }
                if (!solverstarted) {
                    solverstarted = true;
                    currentwall = null;
                    repaint();
                    ((Timer) e.getSource()).stop();

                    Timer Solver_Timer = new Timer(25, r -> {
                        if (!solver_BFS.isDone()) {
                            solver_BFS.step_by_stepSolver();
                            repaint();
                        } else {
                            ((Timer) r.getSource()).stop();
                        }
                    });
                    Solver_Timer.start();
                }
            }
            step_by_stepMaze();
        });

        MazeGeneration_Timer.start();
    }

}

class Prims_Algorithm_Maze implements MazeGenerator {

    int cols;
    int rows;
    int maze_arr[][];
    Consumer<wall> setCurrentWall;
    ArrayList<wall> wall_coordinates;

    public Prims_Algorithm_Maze(int cols, int rows, int maze_arr[][], ArrayList<wall> wall_coordinates ,Consumer<wall> setCurrentWall) {
        this.cols = cols;
        this.rows = rows;
        this.maze_arr = maze_arr;
        this.wall_coordinates=wall_coordinates;
        this.setCurrentWall = setCurrentWall;
    }

    public void GenerateMaze() {
        initialize();
        while(step()){

        }
    }

    // checks coordinates if in boudns
    public boolean inbounds(int x, int y) {
        return x >= 0 && x < this.maze_arr.length && y >= 0 && y < this.maze_arr[0].length;
    }

    public void initialize() {
        // initial maze setup
        this.maze_arr[1][1] = 0;
        // setting the beginning of prims algorithm

        if (inbounds(1, 2) && maze_arr[1][2] == 1) {
            wall_coordinates.add(new wall(1, 2, 1, 3));
        }

        if (inbounds(2, 1) && maze_arr[2][1] == 1) {
            wall_coordinates.add(new wall(2, 1, 3, 1));
        }

    }

    public boolean step() {
        //maze generation after initial setup
        if (wall_coordinates == null || wall_coordinates.isEmpty()) {
            return false;
        }

        int randindex = (int) (Math.random() * wall_coordinates.size());
        wall w = wall_coordinates.get(randindex);
        int x = w.wall_x;
        int y = w.wall_y;
        setCurrentWall.accept(w);
        if (maze_arr[w.opposite_x][w.opposite_y] == 1) {
            maze_arr[x][y] = 0;
            maze_arr[w.opposite_x][w.opposite_y] = 0;
            if (inbounds(w.opposite_x + 2, w.opposite_y) && maze_arr[w.opposite_x + 2][w.opposite_y] == 1) {
                wall_coordinates.add(new wall(w.opposite_x + 1, w.opposite_y, w.opposite_x + 2, w.opposite_y));
            }
            if (inbounds(w.opposite_x - 2, w.opposite_y) && maze_arr[w.opposite_x - 2][w.opposite_y] == 1) {
                wall_coordinates.add(new wall(w.opposite_x - 1, w.opposite_y, w.opposite_x - 2, w.opposite_y));
            }
            if (inbounds(w.opposite_x, w.opposite_y + 2) && maze_arr[w.opposite_x][w.opposite_y + 2] == 1) {
                wall_coordinates.add(new wall(w.opposite_x, w.opposite_y + 1, w.opposite_x, w.opposite_y + 2));
            }
            if (inbounds(w.opposite_x, w.opposite_y - 2) && maze_arr[w.opposite_x][w.opposite_y - 2] == 1) {
                wall_coordinates.add(new wall(w.opposite_x, w.opposite_y - 1, w.opposite_x, w.opposite_y - 2));
            }
        }

        wall_coordinates.remove(randindex);
        return true;
    }

}

class Point {

    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Object obj) {
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }

    public int hashCode() {
        return Objects.hash(x, y);
    }
}

interface SolverInterface {

    boolean isDone();

    void step_by_stepSolver();

    List<Point> visitedNodes();

    Point getNode();

    List<Point> getCurrentPath();
}

abstract class Solver implements SolverInterface {

    int maze_arr[][];
    int cols;
    int rows;

    public Solver(int maze[][], int cols, int rows) {
        this.maze_arr = maze;
        this.cols = cols;
        this.rows = rows;
    }

    public abstract void step_by_stepSolver();

    public abstract boolean isDone();

    // checks coordinates if in boudns
    public boolean inbounds(int x, int y) {
        return x >= 0 && x < this.maze_arr.length && y >= 0 && y < this.maze_arr[0].length;
    }

}

class DFS_Solver extends Solver {

    List<Point> visitedNodes = new ArrayList<Point>();
    Point currentNode;
    List<Point> getCurrentPath = new ArrayList<Point>();
    Stack<Point> Nodes = new Stack<Point>();
    Map<Point, Point> parentMap = new HashMap<Point, Point>();
    Point endPoint = new Point(cols - 1, rows - 1);

    public DFS_Solver(int[][] maze, int cols, int rows) {
        super(maze, cols, rows);
        Point start = new Point(1, 1);
        Nodes.add(start);
        this.currentNode = start;
    }

    public void step_by_stepSolver() {
        if (Nodes.isEmpty()) {
            return;
        } else {
            Point current = Nodes.pop();
            this.currentNode = current;
            visitedNodes.add(current);
            Point up = new Point(current.x - 1, current.y);
            if (!visitedNodes.contains(up) && !Nodes.contains(up)) {
                if (inbounds(current.x - 1, current.y) && maze_arr[current.x - 1][current.y] == 0) {
                    Nodes.push(up);
                    parentMap.put(up, current);
                }
            }
            Point down = new Point(current.x + 1, current.y);
            if (!visitedNodes.contains(down) && !Nodes.contains(down)) {
                if (inbounds(current.x + 1, current.y) && maze_arr[current.x + 1][current.y] == 0) {
                    Nodes.push(down);
                    parentMap.put(down, current);
                }
            }
            Point left = new Point(current.x, current.y - 1);
            if (!visitedNodes.contains(left) && !Nodes.contains(left)) {
                if (inbounds(current.x, current.y - 1) && maze_arr[current.x][current.y - 1] == 0) {
                    Nodes.push(left);
                    parentMap.put(left, current);
                }
            }
            Point right = new Point(current.x, current.y + 1);
            if (!visitedNodes.contains(right) && !Nodes.contains(right)) {
                if (inbounds(current.x, current.y + 1) && maze_arr[current.x][current.y + 1] == 0) {
                    Nodes.push(right);
                    parentMap.put(right, current);
                }
            }

        }
    }

    public boolean isDone() {
        return currentNode.equals(endPoint) || Nodes.isEmpty();
    }

    public List<Point> visitedNodes() {
        return visitedNodes;
    }

    public Point getNode() {
        return currentNode;
    }

    public List<Point> getCurrentPath() {
        List<Point> path = new ArrayList<>();
        if (!isDone() || !currentNode.equals(endPoint)) {
            return path;
        }
        Point step = endPoint;
        while (step != null) {
            path.add(0, step);
            step = parentMap.get(step);
        }
        return path;
    }

}

class BFS_Solver extends Solver {

    List<Point> visitedNodes = new ArrayList<Point>();
    Point currentNode;
    List<Point> getCurrentPath = new ArrayList<Point>();
    ArrayList<Point> Nodes = new ArrayList<Point>();
    Map<Point, Point> parentMap = new HashMap<Point, Point>();
    Point endPoint = new Point(cols - 1, rows - 1);

    public BFS_Solver(int[][] maze, int cols, int rows) {
        super(maze, cols, rows);
        Point start = new Point(1, 1);
        Nodes.add(start);
        this.currentNode = start;
    }

    public void step_by_stepSolver() {
        if (Nodes.isEmpty()) {
            return;
        } else {
            Point current = Nodes.remove(0);
            this.currentNode = current;
            visitedNodes.add(current);
            Point up = new Point(current.x - 1, current.y);
            if (!visitedNodes.contains(up) && !Nodes.contains(up)) {
                if (inbounds(current.x - 1, current.y) && maze_arr[current.x - 1][current.y] == 0) {
                    Nodes.add(up);
                    parentMap.put(up, current);
                }
            }
            Point down = new Point(current.x + 1, current.y);
            if (!visitedNodes.contains(down) && !Nodes.contains(down)) {
                if (inbounds(current.x + 1, current.y) && maze_arr[current.x + 1][current.y] == 0) {
                    Nodes.add(down);
                    parentMap.put(down, current);
                }
            }
            Point left = new Point(current.x, current.y - 1);
            if (!visitedNodes.contains(left) && !Nodes.contains(left)) {
                if (inbounds(current.x, current.y - 1) && maze_arr[current.x][current.y - 1] == 0) {
                    Nodes.add(left);
                    parentMap.put(left, current);
                }
            }
            Point right = new Point(current.x, current.y + 1);
            if (!visitedNodes.contains(right) && !Nodes.contains(right)) {
                if (inbounds(current.x, current.y + 1) && maze_arr[current.x][current.y + 1] == 0) {
                    Nodes.add(right);
                    parentMap.put(right, current);
                }
            }

        }
    }

    public boolean isDone() {
        return currentNode.equals(endPoint) || Nodes.isEmpty();
    }

    public List<Point> visitedNodes() {
        return visitedNodes;
    }

    public Point getNode() {
        return this.currentNode;
    }

    public List<Point> getCurrentPath() {

        List<Point> path = new ArrayList<>();
        if (!isDone() || !currentNode.equals(endPoint)) {
            return path;
        }
        Point step = endPoint;
        while (step != null) {
            path.add(0, step);
            step = parentMap.get(step);
        }
        return path;
    }

}

class Djikstra_Solver extends Solver {

    List<Point> visitedNodes;
    Point currentNode;
    List<Point> getCurrentPath;

    public Djikstra_Solver(int[][] maze, int cols, int rows) {
        super(maze, cols, rows);
    }

    public void step_by_stepSolver() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Point> visitedNodes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point getNode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Point> getCurrentPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

class AStar_Solver extends Solver {

    List<Point> visitedNodes;
    Point currentNode;
    List<Point> getCurrentPath;

    public AStar_Solver(int[][] maze, int cols, int rows) {
        super(maze, cols, rows);
    }

    public void step_by_stepSolver() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Point> visitedNodes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point getNode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Point> getCurrentPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
