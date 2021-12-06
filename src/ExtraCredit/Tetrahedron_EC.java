package ExtraCredit;

import WireframeModel.Polygon3D;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * tetrahedron is defined as an array of faces (polygon objects)
 */
public class Tetrahedron_EC {
    private ShadedPolygon[] polygons;

    /**
     * constructor
     * @param p an array of polygons
     */
    public Tetrahedron_EC(ShadedPolygon... p) { polygons = p; }

    /**
     * Pass a graphics object down for each polygon --> each individual polygon draws itself
     * draw a tetrahedron by drawing the individual faces
     * @param g a graphics object
     */
    public void render(Graphics g) {
        for (ShadedPolygon poly : polygons) {
            poly.render(g);
        }
    }

    /**
     * to rotate tetrahedron, rotate each face (polygon)
     * [first time I ran w/ rotations, many shapes drew over each other and some seemed to disappear while rotating]
     * After rotating each face, to adjust display, draw faces in the order of how close they are to viewer
     * so back faces are not visible or drawn over front face
     * @param xDegrees degrees to rotate over x-axis
     * @param yDegrees degrees to rotate over y-axis
     * @param zDegrees degrees to rotate over z-axis
     */
    public void rotate(double xDegrees, double yDegrees, double zDegrees) {
        for (ShadedPolygon poly : polygons) poly.rotate(xDegrees, yDegrees, zDegrees);

        // sort each polygon according to its depth (closer face gets drawn last)
        Arrays.sort(polygons, new Comparator<ShadedPolygon>() {
            @Override
            public int compare(ShadedPolygon o1, ShadedPolygon o2) {
                /**
                 * take smaller val of both faces' average depth from viewer
                 */
                return o2.getAverageDepth() - o1.getAverageDepth() < 0 ? 1 : -1;
            }
        });
    }
}

