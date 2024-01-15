package implementationofpaint.GUI;

import implementationofpaint.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.awt.event.KeyEvent.*;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    private int figureType = 3;
    private Shape selectedShape;
    private final ArrayList<Shape> shapeList = new ArrayList<>(), selectedShapes = new ArrayList<>();
    private Rectangle selectionBox;
    private boolean drawMode = false, selectMode = false, isColorKeyPressed = false;
    private final Set<Integer> pressedKeySet = new HashSet<>();
    private Point2D startPoint, endPoint;

    /**
     * Constructor of DrawPanel class which sets default look of component, create listeners and change visibility of Panel.
     * As arguments constructor accepts width and height of panel.
     * @param width width of the drawPanel
     * @param height height of the drawPanel
     */
    public DrawPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black, 4, false));
        setFocusable(true);
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                repaint();
            }
        });
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        setVisible(true);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        drawOnPanel(graphics);
        if (isShapeChangePossible())
            changeShapeInTheList();
        else if (isColorChangePossible())
            changeColor(graphics);
        if (selectionBox != null)
            drawSelectionBox(graphics);
    }
    private boolean isColorChangePossible(){
        return (isColorKeyPressed && selectedShape != null);
    }
    private boolean isShapeChangePossible() {
        return (pressedKeySet.contains(VK_C) && pressedKeySet.contains(VK_CONTROL));
    }

    /**
     * Method is used to paint all the shapes which are stored in shapeList on the graphic panel of component.
     * @param graphics graphics of the drawPanel
     */
    private void drawOnPanel(Graphics2D graphics) {
        for (Shape shape : shapeList) {
            if (shape != selectedShape || !isColorKeyPressed()) {
                graphics.setColor(Color.BLUE);
                graphics.draw(shape);
                graphics.fill(shape);
            }
        }
    }

    /**
     * Method is generating default selection box on graphical panel of the component.
     * @param graphics graphics of drawPanel
     */
    private void drawSelectionBox(Graphics2D graphics) {
        graphics.setColor(new Color(0, 0, 255, 50));
        graphics.fill(selectionBox);
        graphics.setColor(Color.BLUE);
        graphics.draw(selectionBox);
    }

   /**
     * Method creates shape based on figure type variable and adds it to the shapeList
     */
    private void createShape() {
        Shape currentShape;
        switch (figureType) {
            case ButtonConst.RECTANGLE -> currentShape = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX() - startPoint.getX(), endPoint.getY() - startPoint.getY());
            case ButtonConst.CIRCLE -> currentShape = new Ellipse2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX() - startPoint.getX(), endPoint.getX() - startPoint.getX());
            case ButtonConst.LINE -> currentShape = new Line2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
            case ButtonConst.ELLIPSE -> currentShape = new Ellipse2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX() - startPoint.getX(), endPoint.getY() - startPoint.getY());
            default -> currentShape = null;
        }
        shapeList.add(currentShape);
    }

    private void changeColor(Graphics2D graphics) {
        graphics.setColor(Color.RED);
        graphics.fill(selectedShape);
    }


    /**
     * Method check if arguments x and y match any of the figure from shapeList.
     * @param x point on the panel placed on the X's axi
     * @param y point on the panel placed on the Y's axi
     * @return If the arguments match a shape, this shape is return. If there is no such a shape the method returns null
     */
    private Shape selectShape(int x, int y) {
        for (Shape shape : shapeList)
            if (shape.getBounds2D().contains(x,y))
                return shape;
        return null;
    }

    /**
     * Method checks if selectedShapes is empty. If not determines vector of move for selectedShapes.
     * Invokes move method for every shape that is contained by selectedShapes.
     */
    private void moveShapesInTheBox() {
        if (selectedShapes.isEmpty())
            return;
        int deltaX = (int) (endPoint.getX() - startPoint.getX());
        int deltaY = (int) (endPoint.getY() - startPoint.getY());
        for (Shape shape : selectedShapes) {
            moveShape(shape, deltaX, deltaY);
        }
    }

    /**
     * Method moves shape based on its figure instance. It moves the shape accordingly to the vector.
     * @param shape shape to move
     * @param deltaX vector's first parameter
     * @param deltaY vector's second parameter
     */
    private void moveShape(Shape shape, int deltaX, int deltaY) {
        if (shape instanceof RectangularShape recShape)
            recShape.setFrame(recShape.getX() + deltaX, recShape.getY() + deltaY, recShape.getWidth(), recShape.getHeight());
        else if (shape instanceof Line2D line)
            line.setLine(line.getX1() + deltaX, line.getY1() + deltaY, line.getX2() + deltaX, line.getY2() + deltaY);
        else  {
            Rectangle boundOfEllipse = selectedShape.getBounds();
            selectedShape = new Ellipse2D.Double(boundOfEllipse.getX() + deltaX, boundOfEllipse.getY() + deltaY, boundOfEllipse.getWidth(), boundOfEllipse.getHeight());
        }

    }

    private void changeShapeInTheList() {
        int indexOfModifiedShape = shapeList.indexOf(selectedShape);
        if (indexOfModifiedShape != -1) {
            shapeList.remove(indexOfModifiedShape);
            selectedShape = createModifiedShape(selectedShape);
            shapeList.add(selectedShape);
            repaint();
        }
    }

    /**
     * Method creates modified shape which is based on the old shape.
     * @param previousShape that will be replaced by new shape
     * @return Method returns modified shape
     */
    private Shape createModifiedShape(Shape previousShape) {
        if (previousShape instanceof Rectangle2D rectangle)
            return new Ellipse2D.Double(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        else if (previousShape instanceof Ellipse2D ellipse)
            return new Rectangle2D.Double(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight());
        else
            return previousShape;
    }

//----------------- KEYBOARD LISTENERS --------------

    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Method implements handling for keyPresses whenever the select mode of drawPanel is on.
     * @param keyEvent the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (isSelectMode())
            handlingOfKeys(keyEvent);
        pressedKeySet.add(keyEvent.getKeyCode());
        repaint();
    }

    /**
     * Method implements behaviour whenever key is released.
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == VK_C)
            setColorKeyPressed(false);
        pressedKeySet.remove(e.getKeyCode());
        repaint();
    }

    /**
     * Method that handel button press by user.
     * @param e event of button being pressed
     */
    private void  handlingOfKeys(KeyEvent e) {
        switch (e.getKeyCode()) {
            case VK_UP -> moveShape(selectedShape,0,-5);
            case VK_DOWN -> moveShape(selectedShape,0, 5);
            case VK_RIGHT -> moveShape(selectedShape, 5,0);
            case VK_LEFT -> moveShape(selectedShape,-5, 0);
            case VK_C -> setColorKeyPressed(true);
            case VK_CONTROL -> {}
            default -> System.out.println("Unsupported key was pressed");
        }
        pressedKeySet.add(e.getKeyCode());
    }

//----------------- MOUSE LISTENERS -------------------

    /**
     * Listener for mouse click. If selectMode is true we check if the point of the click is inside any of the shapes.
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (isSelectMode()) {
            selectedShape = selectShape(e.getX(), e.getY());
            requestFocusInWindow();
        }
    }

    /**
     * Method prints to the console information about the fact that mouse button is pressed.
     * On this event also we take to point of the mouse press as starting point for possible future shape.
     * If the select mode is on it starts to create rectangle of selection box.
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("Mouse button is pressed");
        startPoint = e.getPoint();
        if (isSelectMode()) {
            selectionBox = new Rectangle((Point) startPoint);
            selectedShapes.clear();
        }
    }

    /**
     * Method implements behavior of the application whenever the mouse button is released.
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("Mouse Released");
        endPoint = e.getPoint();
        if (isDrawMode())
            createShape();
        else if (isSelectMode()) {
            moveShapesInTheBox();
            selectionBox = null;
            selectedShapes.clear();
        }
        repaint();
    }

    /**
     * Whenever e event happens to the console it's printed that we have entered DrawPanel.
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("Entered DrawPanel");
    }

    /**
     * Whenever e event happens to the console it's printed that we have left DrawPanel.
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println("Exited DrawPanel");
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    /**
     * Method implements behaviour of drawPanel, when the mouse is dragged across it. The behaviour depends on the mod that is currently selected.
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (isSelectMode()) {
            Point endPoint = e.getPoint();
            selectionBox.setBounds(
                    (int) Math.min(startPoint.getX(), endPoint.getX()),
                    (int) Math.min(startPoint.getY(), endPoint.getY()),
                    (int) Math.abs(endPoint.getX() - startPoint.getX()),
                    (int) Math.abs(endPoint.getY() - startPoint.getY())
            );
            selectedShapes.clear();
            for (Shape shape : shapeList) {
                if (selectionBox.intersects(shape.getBounds()))
                    selectedShapes.add(shape);
            }
        }
        else if (!isDrawMode() && e.getPoint().getX() < this.getWidth()) {
            endPoint = e.getPoint();
            figureType = ButtonConst.LINE;
            createShape();
            startPoint = endPoint;
        }
        repaint();
    }

    /**
     * Method is implementing the behavior whenever position of mouse roll is changed and the selectMode is true.
     * Changed position of mouse roll influence the size of the shape that is selected
     * @param e the event to be processed
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int rotation = e.getWheelRotation();
        if (isSelectMode()) {
            if (selectedShape instanceof RectangularShape recShape)
                recShape.setFrame(recShape.getX(), recShape.getY(), recShape.getWidth() + rotation, recShape.getHeight() + rotation);
            else  {
                Rectangle boundOfEllipse = selectedShape.getBounds();
                selectedShape = new Ellipse2D.Double(boundOfEllipse.getX(), boundOfEllipse.getY(), boundOfEllipse.getWidth() + rotation, boundOfEllipse.getHeight() + rotation);
            }
        }
        repaint();
    }

//----------------- GETTERS AND SETTERS -------------

    public boolean isDrawMode() {
        return drawMode;
    }
    public void setDrawMode(boolean drawMode) {
        this.drawMode = drawMode;
    }
    public void setFigureType(int shape) {
        this.figureType = shape;
    }
    public boolean isSelectMode() {
        return selectMode;
    }

    /**
     * Method is changing the state of selectMode. If the selectMode is true then it send a request for focus in drawPanel
     */
    public void setSelectMode() {
        this.selectMode = !selectMode;
        if (isSelectMode())
            requestFocusInWindow();
        repaint();
    }
    public boolean isColorKeyPressed() {
        return isColorKeyPressed;
    }
    public void setColorKeyPressed(boolean colorKeyPressed) {
        isColorKeyPressed = colorKeyPressed;
    }

//-------------------------------------------------
}