package WireframeModel;

import java.awt.*;

public class PointConverter {
    private static double scale = 1.0;
    private static double zoomFactor = 1.2;

    public static void zoomIn() {
        scale *= zoomFactor;
    }

    public static void zoomOut() {
        scale /= zoomFactor;
    }

    /**
     * every point in can be scaled by a certain factor (away from center of the axis @ (0, 0, 0)
     * account for (0, 0) on canvas being top right by shifting every point based on display width and height so center
     * of screen can represent (0, 0)
     * @param point3D a point in 3d given to us by the input before modifications
     * @return 2d point after being scaled and shifted based on display window size
     */
    public static Point convertPoint(Point3D point3D) {
        double x3d = point3D.x * scale;
        double y3d = point3D.y * scale;
        int x2d = (int) (Display.WIDTH / 2 + x3d);
        int y2d = (int) (Display.WIDTH / 2 - y3d);
        return new Point(x2d, y2d);
    }

    /**
     * to rotate around an axis, we must shift the point on the plane excluding that axis
     * (ie. rotation around z axis changed x, y coord of point)
     * every point is a vector beginning at (0, 0) to (x, y) w/ direction (theta) and magnitude (distance)
     * rotate a point by adjusting its direction and updating the vector endpoint using its magnitude * direction
     * x = radius * cos(theta), y = radius * sin(theta)
     * we need to find current theta and radius of the vector respective to the plane we are rotating about,
     * increment theta by (input as degrees) r radians, then update vector endpoints using the above formula
     * current theta = tan^-1(y/x) ... (opp/adj), and radius = sqrt(x^2 + y^2) ... sum of squares of x, y components
     * convert degrees to radians using pi/180 conversion ratio ... coords of point p = new vector endpoint
     *
     * **theta z & y can be switched as long as they correlate to correct sin/cos**
     * @param p a point to rotate
     * @param degrees the amount to rotate by in degrees (0 ... 360)
     */
    public static void rotateAxisX(Point3D p, double degrees) {
        double radius = Math.sqrt(Math.pow(p.y, 2) + Math.pow(p.z, 2));     // magnitude of vector on yz plane
        double theta = Math.atan2(p.z, p.y);    // tan t = z/y .. t = tan^-1(z/y) ... z = opposite, y = adjacent
        theta += Math.PI/180 * degrees;
        p.z = radius * Math.sin(theta);
        p.y = radius * Math.cos(theta);
    }

    public static void rotateAxisY(Point3D p, double degrees) {
        double radius = Math.sqrt(Math.pow(p.x, 2) + Math.pow(p.z, 2));     // rotate on xz plane
        double theta = Math.atan2(p.z, p.x);
        theta += Math.PI/180 * degrees;
        p.x = radius * Math.cos(theta);
        p.z = radius * Math.sin(theta);
    }

    public static void rotateAxisZ(Point3D p, double degrees) {
        double radius = Math.sqrt(Math.pow(p.x, 2) + Math.pow(p.y, 2));     // rotate on xy plane
        double theta = Math.atan2(p.y, p.x);
        theta += Math.PI/180 * degrees;
        p.x = radius * Math.cos(theta);
        p.y = radius * Math.sin(theta);
    }
}
