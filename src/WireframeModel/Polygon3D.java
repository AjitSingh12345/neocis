package WireframeModel;
import java.awt.geom.Ellipse2D;

import java.awt.*;

public class Polygon3D {

    private Color color;
    private Point3D[] points;

    /**
     * "Point3D..." means u can enter param as any number of arg w/o fixed size & it can be used as array
     * Constructor for 3D polygon object using points in 3D
     * define color and make deep copy of all points to hold in a points array
     * @param p3d, an array of 3d points
     */
    public Polygon3D(Point3D... p3d) {
        color = color.CYAN;
        points = new Point3D[p3d.length];   // HAVE TO MAKE DEEP COPY
        for (int i = 0; i < points.length; i++) {
            Point3D p = p3d[i];
            points[i] = new Point3D(p.x, p.y, p.z);
        }
    }

    /**
     * draw wireframe model of given graphics object
     * for each point in polygon draw an elipse, and draw line to next point in the polygon
     * connect last point to first point (this will draw the face in given order of vertices)
     * @param g, a graphics object
     */
    public void render(Graphics g) {
        g.setColor(Color.CYAN);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < points.length; i++) {
            Point p1 = PointConverter.convertPoint(points[i]);
            Point p2 = PointConverter.convertPoint(points[(i+1)%points.length]);
            Ellipse2D e = new Ellipse2D.Double(p1.x - 4, p1.y - 4, 8, 8);
            g2.fill(e);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        g.setColor(color);
    }

    /**
     * rotate each polygon a specified amount on the x, y, z, axis by rotating each individual point over each axis
     * @param xDegrees
     * @param yDegrees
     * @param zDegrees
     */
    public void rotate(double xDegrees, double yDegrees, double zDegrees) {
        for (Point3D p : points) {
            PointConverter.rotateAxisX(p, xDegrees);
            PointConverter.rotateAxisY(p, yDegrees);
            PointConverter.rotateAxisZ(p, zDegrees);
        }
    }

    /**
     * finds the average depth on the face (polygon) in relation to the viewer
     * @return the avg z value of the face
     */
    public double getAverageDepth() {
        // avg = (sum of all Z vals) / (# of points in this polygon)
        double sum = 0.0;
        for (Point3D p : points) sum += p.z;
        return sum / points.length;
    }
}
