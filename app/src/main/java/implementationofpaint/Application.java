package implementationofpaint;

import javax.swing.*;

import implementationofpaint.GUI.DrawPanel;

import java.awt.*;

public class Application {
    private final static int mutualPanelSizes = 5;
    private final JFrame paintFrame = new JFrame();
    private final JPanel buttonPanel = new JPanel();
    private DrawPanel drawPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Application());
    }

    /**
     * Constructor for application class. Invokes series of command which set up application.
     */
    public Application() {
        paintFrameInitialization();
        drawPanelInitialization();
        buttonPanelInitialization();
        addComponentsToFrame();
        displayApplication();
    }

    /**
     * Method sets up application frame with default settings.
     */
    private void paintFrameInitialization() {
        paintFrame.setLayout(new BorderLayout());
        paintFrame.setTitle("Drawing Interface");
        paintFrame.setSize(500, 500);
        paintFrame.setResizable(false);
        paintFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Initialization of drawPanel. Initialization involves calling the constructor of DrawPanel class.
     */
    private void drawPanelInitialization() {
        drawPanel = new DrawPanel(paintFrame.getWidth()/mutualPanelSizes * 4, paintFrame.getHeight());
    }

    /**
     * Method initialize default buttonPanel. Method is setting the size of panel, layout, visibility and generate buttons for panel.
     */
    private void buttonPanelInitialization() {
        buttonPanel.setPreferredSize(new Dimension(paintFrame.getWidth()/mutualPanelSizes, paintFrame.getHeight()));
        buttonPanel.setLayout(new GridLayout(0,1));
        for (int i = 0;  i < ButtonConst.NUMBER_OF_BUTTONS; i++)
            buttonPanel.add(generateButtons(i));
        buttonPanel.setVisible(true);
    }

    /**
     * Method is creating buttons based on buttonNumber passed as argument. Every button has the same size. After visual implementation also action listener is
     * set for a button.
     * @param buttonNumber - number defining what type of button it is
     * @return Method is returning adres in memory of the button.
     */
    private JButton generateButtons(int buttonNumber) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(buttonPanel.getWidth(), buttonPanel.getHeight()));
        switch (buttonNumber) {
            case ButtonConst.RECTANGLE -> button.setText("Rectangle");
            case ButtonConst.CIRCLE -> button.setText("Circle");
            case ButtonConst.LINE -> button.setText("Line");
            case ButtonConst.ELLIPSE -> button.setText("Ellipse");
            case ButtonConst.SELECT -> button.setText("Select");
        }
        addButtonListener(button);
        button.setVisible(true);
        return button;
    }

    /**
     * Adding action listener for every button in buttonPanel. The action send to drawPanel is determined based on the text that is on the button.
     * @param button - instance of button class whose listener is set at the moment
     */
    private void addButtonListener(JButton button) {
        button.addActionListener(e -> {
            drawPanel.setDrawMode(true);
            switch (button.getText()) {
                case "Rectangle" -> drawPanel.setFigureType(ButtonConst.RECTANGLE);
                case "Circle" -> drawPanel.setFigureType(ButtonConst.CIRCLE);
                case "Line" -> {
                    drawPanel.setDrawMode(false);
                    drawPanel.setFigureType(ButtonConst.LINE);
                }
                case "Ellipse" -> drawPanel.setFigureType(ButtonConst.ELLIPSE);
                case "Select" -> {
                    drawPanel.setDrawMode(false);
                    drawPanel.setSelectMode();
                    drawPanel.setFigureType(ButtonConst.SELECT);
                }
            }
        });
    }

    /**
     * Method invokes adding every single component to application frame.
     */
    private void addComponentsToFrame() {
        paintFrame.add(buttonPanel, BorderLayout.EAST);
        paintFrame.add(drawPanel, BorderLayout.CENTER);
    }

    /**
     * Method sets visibility of frame to true. Tantamount to displaying application to the user.
     */
    private void displayApplication() {
        paintFrame.setVisible(true);

    }

}
