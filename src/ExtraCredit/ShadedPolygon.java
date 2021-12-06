package ExtraCredit;

import WireframeModel.Point3D;
import WireframeModel.PointConverter;
import WireframeModel.Polygon3D;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ShadedPolygon {
    private Color color;
    private Point3D[] points;

    /**
     * "Point3D..." means u can enter param as any number of arg w/o fixed size & it can be used as array
     * Constructor for 3D polygon object using points in 3D
     * define color and make deep copy of all points to hold in a points array
     * @param p3d, an array of 3d points
     */
    public ShadedPolygon(Point3D... p3d) {
        color = color.CYAN;
        points = new Point3D[p3d.length];   // HAVE TO MAKE DEEP COPY
        for (int i = 0; i < points.length; i++) {
            Point3D p = p3d[i];
            points[i] = new Point3D(p.x, p.y, p.z);
        }
    }

    /**
     * draw shaded model of given graphics object
     * for each point in polygon add it to a polygon object, and fill using assign color function
     * assign color using dot product between z-axis and normal vector of the polygon (face)
     * @param g, a graphics object
     */
    public void render(Graphics g) {
        Polygon poly = new Polygon();

        for (int i = 0; i < points.length; i++) {
            Point p = PointConverter.convertPoint(points[i]);
            poly.addPoint(p.x, p.y);
        }

        g.setColor(assignShade());
        g.fillPolygon(poly);
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

    /**
     * find normal to a plane
     * a = p1, b = p2, c = p3
     * ab = p2-p1 ... a vector in the plane
     * ac = p3-p1 ... a vector in the plane
     * normal to this plane is the cross product n = ab X ac
     */
    public double[] findNormal(Point3D[] p) {
        Point3D a = p[0];
        Point3D b = p[1];
        Point3D c = p[2];
        double[] vecA = {b.x-a.x, b.y-a.y, b.z-a.z};
        double[] vecB = {c.x-a.x, c.y-a.y, c.z-a.z};
        double[] cross_prod = new double[3];
        cross_prod[0] = vecA[1] * vecB[2] - vecA[2] - vecB[1];
        cross_prod[1] = vecA[2] * vecB[0] - vecA[0] - vecB[2];
        cross_prod[2] = vecA[0] * vecB[1] - vecA[1] - vecB[0];
        return cross_prod;
    }

    /**
     * find angle of a vector with respect to z-axis by taking the dot product
     * z-axis vector is (0, 0, 1)
     * a * b = |a| |b| cos t ... where t is angle between two vectors and || is magnitude of a vector
     * a.x*b.x + a.y*b.y + a.z*b.z = sqrt(a.x^2 + a.y^2 + a.z^2) * sqrt(b.x^2 + b.y^2 + b.z^2) * cos t
     * cos t = (a.x*b.x + a.y*b.y + a.z*b.z) / (sqrt(a.x^2 + a.y^2 + a.z^2) * sqrt(b.x^2 + b.y^2 + b.z^2))
     * t = cos^-1(above)
     * @param vector a vector in x,y,z plane
     * @return the angle in degrees
     */
    public double angleWithZAxis(double[] vector) {
        double angle = 0;
        double[] zVec = {0.0, 0.0, 1.0};
        double dotProd = zVec[0]*vector[0] + zVec[1]*vector[1] + zVec[2]*vector[2];
        double magSum = Math.sqrt(Math.pow(zVec[0], 2) + Math.pow(zVec[1], 2) + Math.pow(zVec[2], 2))
                * Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
        angle = Math.acos(dotProd/magSum);
        return angle * 180/Math.PI;
    }

    /**
     * GOAL:
     * based on the dot product between the z-axis and the normal vector of this polygon, determine color to fill face
     *
     * METHOD:
     * we will assign min to be 00005F (perpendicular) and max to be 0000FF (parallel)
     * the shade_factor will be the angle between our normal and the z-axis ... mod 180
     * scale will be from 0 ... 180 where 90 = max and 0, 180 = min ... the angle will move on this scale
     * if angle > 90 ... angle = 180-angle --> angle become dist from z axis in degrees, always between 0 ... 90
     * color_diff = max_color - min_color (size of range of colors)
     * new color = (dist/90) * color_diff + min --> take the fractional component of how far we are from being
     * parallel and add that % of color_diff to the min_color (being parallel to z-axis)
     *
     */
    public Color assignShade() {
        String perp = "00005F";
        String para = "0000FF";
        double shade_factor = angleWithZAxis(findNormal(points));
        shade_factor %= 180;
        if (shade_factor >= 90) shade_factor = 180 - shade_factor;

        int min = Integer.parseInt(perp, 16);
        int max = Integer.parseInt(para, 16);
        int diff = max - min;
        int new_color = (int)((shade_factor/90) * diff + min);
        return Color.decode("#" + Integer.toHexString(new_color));
    }
}
