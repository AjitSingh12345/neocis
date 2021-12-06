package WireframeModel;
import java.io.*;

import javafx.scene.canvas.Canvas;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Our display will be a Canvas [JFrame used for drawing]
 */
public class Display extends Canvas {
    private JFrame frame;
    private static String title = "Wireframe Model";
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    private static boolean running  = false;
    private static final String INPUT_PATH = "C:\\Users\\asing\\IdeaProjects\\neocis\\Data\\object.txt";

    private Tetrahedron tetra;

    // click values from my mouse
    private Mouse mouse;
    public static final int LEFT_CLICK = 1;
    public static final int RIGHT_CLICK = 3;
    public static final int SCROLL_CLICK = 2;
    public static final int NO_CLICK = -1;
    int dx, dy; // for rotation purposes

    /**
     * Display constructor
     * Every display has a JFrame (drawing) and mouse (rotation)
     */
    public Display() {
        frame = new JFrame();
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // must add mouse listeners to a frame
        mouse = new Mouse();
        frame.addMouseListener(mouse);
        frame.addMouseMotionListener(mouse);
        frame.addMouseWheelListener(mouse);
    }

    public static void main(String[] args) {
        Display display = new Display();
        display.frame.setTitle(title);
        display.frame.pack();
        display.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display.frame.setLocationRelativeTo(null); // sets location to center of screen
        display.frame.setResizable(false);
        display.frame.setVisible(true);

        display.init();
    }

    /**
     * reads in & processes input
     * use first 2 lines to define vertex and face count ... each face is individual polygon
     * tetrahedron to be displayed is an array of polygons
     * Use hashmap to map id's to Point3D objects
     * for each face make a new Polygon3D and add to add to pass as param for tetra
     */
    public void init() {
        double sf = 100.0;  // scale factor for all inputted coordinates

        /**
         * vertex_count = line 1
         * face_count = line 2
         * for (vertex_count) map.add(id, point)
         * for (face_count) tetra.addPolygon(new Polygon(map.get(id) for all ids in face))
         */
        HashMap<Integer, Point3D> map = new HashMap<Integer, Point3D>(); // maps id: point
        File file = new File(INPUT_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            String[] dim = line.split(",");
            int vertex_count = Integer.parseInt(dim[0]);
            int face_count = Integer.parseInt(dim[1]);
            for (int i = 0; i < vertex_count; i++) {
                line = br.readLine();
                int id = Character.getNumericValue(line.charAt(0));
                line = line.substring(2, line.length()); // only left w/ x, y, z
                double[] point = Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray();
                Point3D p = new Point3D(point[0]*sf, point[1]*sf, point[2]*sf);
                map.put(id, p);
            }
            Polygon3D[] faces = new Polygon3D[face_count]; // will be passed as param for tetra
            for (int i = 0; i < face_count; i++) {
                line = br.readLine();
                int[] vert = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
                Point3D[] face = new Point3D[vert.length];  // each face has vert.length vertices
                for (int j = 0; j < vert.length; j++) {
                    face[j] = map.get(vert[j]);     // id --> Point3D
                }
                faces[i] = new Polygon3D(face);     // pass all vertices for face to a new Polygon3D object
            }
            this.tetra = new Tetrahedron(faces);    // all faces of tetra
        } catch (IOException e) {
            e.printStackTrace();
        }

        // while user doesnt close window ... continuously render & update
        running = true;
        while (running) {
            update();
            render();
        }
    }

    /**
     * create a graphics object to display on canvas
     * pass the graphics object to tetrahedron to render (draw) figure
     *
     */
    private void render() {
        // set buffer for graphics
        BufferStrategy bs = frame.getBufferStrategy();
        if (bs == null) {
            frame.createBufferStrategy(3);
            return;     // you dont want to draw anything if bs is not set up yet
        }

        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        tetra.render(g);

        /**
         * improves efficiency of program by using less resources
         * disposes graphic once done w/ it before its eligible for garbage collection
         */
        g.dispose();
        bs.show();
    }

    int x_start, y_start;   // track x & y starting position

    /**
     * Update the canvas & refresh window, here we perform rotations
     * when mouse is clicked, define starting x and y --> keep track of how much the mouse has moved from
     * its start position using dx, dy and rotate accordingly
     * once mouse is not being clicked, dont rotate but continue to update x, y start vals
     *
     * when user holds down left click ... rotate on x and y axis
     * when user holds down left click ... rotate on z axis
     * when user scrolls upwards ... zoom out
     * when user scrolls downwards ... zoom in
     */
    private void update() {
        final double ms = 2.0;      // rotation buffer for more control & percision
        int x = mouse.getX();
        int y = mouse.getY();

        /**
         * when moving mouse horiz (x-axis), rotate about the y-axis, thus dx is a param for the rotaiton about the
         * y-axis [same concept when rotating about the x-axis using dy]
         * rotate by the magnitude of mouse dragging across screen
         * unknown why -dy is the right direction for rotating about x-axis
         */
        if (mouse.getButton() == LEFT_CLICK) {
            dx = x - x_start;
            dy = y - y_start;
            tetra.rotate(-dy/ms, dx/ms, 0);
        } else if (mouse.getButton() == RIGHT_CLICK) {
            dx = x - x_start;
            dy = y - y_start;
            tetra.rotate( 0, 0, dx/ms);
        }

        // need to reset scroll once done scrolling otherwise it infinitely zooms in/out on 1 scroll
        if (mouse.isScrollingUp()) {
            PointConverter.zoomOut();
        } else if (mouse.isScrollingDown()) {
            PointConverter.zoomIn();
        }
        mouse.resetScroll();

        // while running, update x, y starting positions
        x_start = x;
        y_start = y;
    }
}
