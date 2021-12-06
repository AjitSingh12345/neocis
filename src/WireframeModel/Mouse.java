package WireframeModel;

import java.awt.event.*;

/**
 * Keep track of mouse's current position on screen, the scrolling direction and which button is being pressed
 */
public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {
    private int mouseX = -1;
    private int mouseY = -1;
    private int mouseButton = -1;
    private int scroll = 0;

    public boolean isScrollingUp() {
        return scroll == -1;
    }

    public boolean isScrollingDown() {
        return scroll == 1;
    }

    public void resetScroll() {
        scroll = 0;
    }

    public int getX() {
        return mouseX;
    }

    public int getY() {
        return mouseY;
    }

    public int getButton() {
        return mouseButton;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseButton = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * gets called whenever mouse is dragged across screen
     * we can use this to track mouse's current x, y position in real time
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scroll = e.getWheelRotation();
    }
}
