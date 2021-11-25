import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Rectangle2D;

public class FractalExplorer {
    private int displaySize;
    private JImageDisplay imageDisplay;
    private FractalGenerator fractalGenerator;
    private Rectangle2D.Double range;
    private JComboBox choice;
    private int rowsRemaining;
    private JButton buttonReset;
    private JButton buttonSave;

    private FractalExplorer(int displaySize) {
        this.displaySize = displaySize;//Задаём размер окна
        this.fractalGenerator = new Mandelbrot();
        this.range = new Rectangle2D.Double(0, 0, 0, 0);
        fractalGenerator.getInitialRange(this.range);
    }

    public static void main(String[] args) {
        FractalExplorer fractalExplorer = new FractalExplorer(800);
        fractalExplorer.createAndShowGUI();
        fractalExplorer.drawFractal();
    }

    // задание интерфейса
    public void createAndShowGUI() {
        JFrame frame = new JFrame("Fractal Generator");//Графический интерфейс
        buttonReset = new JButton("Reset");//Кнопка сброса
        buttonSave = new JButton("Save Image");
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JLabel ins = new JLabel("Fractal");

        choice = new JComboBox();
        choice.addItem(new Mandelbrot());
        choice.addItem(new Tricorn());
        choice.addItem(new BurningShip());
        choice.addActionListener(new ActionHandler());

        imageDisplay = new JImageDisplay(displaySize, displaySize);
        imageDisplay.addMouseListener(new MouseListener());

        buttonReset.setActionCommand("Reset");
        buttonReset.addActionListener(new ActionHandler());

        buttonSave.setActionCommand("Save Image");
        buttonSave.addActionListener(new ActionHandler());


        panel1.add(ins, BorderLayout.CENTER);
        panel1.add(choice, BorderLayout.CENTER);
        panel2.add(buttonSave, BorderLayout.CENTER);
        panel2.add(buttonReset, BorderLayout.CENTER);
        frame.setLayout(new BorderLayout());
        frame.add(imageDisplay, BorderLayout.CENTER);//Размещение изображения в центре
        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.SOUTH);//Размещение кнопки сброса снизу
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    // отрисовка фрактала в JImageDisplay
    private void drawFractal() {
        // отключаем интерфейс на момент рисования
        enableGUI(false);
        rowsRemaining = displaySize;
        for (int i = 0; i < displaySize; i++) {
            FractalWorker drawRow = new FractalWorker(i);
            drawRow.execute();//Заупскаем задачу в фоновом режиме
        }
    }
    // включение - отключение gui
    public void enableGUI(boolean b) {
        buttonSave.setEnabled(b);
        buttonReset.setEnabled(b);
        choice.setEnabled(b);
    }
    public class ActionHandler implements ActionListener {//Сброс диапазона к начальному

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Reset")) {
                // перерисовка фрактала
                fractalGenerator.getInitialRange(range);
                drawFractal();
            } else if (e.getActionCommand().equals("Save Image")) {
                // сохранение
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("PNG Images", "png");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int t = fileChooser.showSaveDialog(imageDisplay);
                if (t == JFileChooser.APPROVE_OPTION) {
                    try {
                        ImageIO.write(imageDisplay.image, "png", fileChooser.getSelectedFile());
                    } catch (NullPointerException | IOException ee) {
                        JOptionPane.showMessageDialog(imageDisplay, ee.getMessage(), "Cannot save image", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                fractalGenerator = (FractalGenerator) choice.getSelectedItem();
                range = new Rectangle2D.Double(0, 0, 0, 0);
                fractalGenerator.getInitialRange(range);
                drawFractal();
            }
        }
    }

    public class MouseListener extends MouseAdapter {//Внутренний класс для увеличения изображения при клике

        @Override
        public void mouseClicked(MouseEvent e) {
            double x = FractalGenerator.getCoord(range.x, range.x + range.width, displaySize, e.getX());
            double y = FractalGenerator.getCoord(range.y, range.y + range.width, displaySize, e.getY());
            fractalGenerator.recenterAndZoomRange(range, x, y, 0.5);
            drawFractal();
        }
    }

    public class FractalWorker extends SwingWorker<Object, Object> {
        private int yCoord;
        private int[] rgb;

        public  FractalWorker(int yCoord) {
            this.yCoord =yCoord;
        }
        @Override
        protected Object doInBackground() throws Exception {
            rgb = new int[displaySize];//Выделяем память под массив
            for (int i = 0; i < displaySize; i++) {
                int count = fractalGenerator.numIterations(FractalGenerator.getCoord(range.x, range.x + range.width, displaySize, i),
                        FractalGenerator.getCoord(range.y, range.y+range.width, displaySize, yCoord));
                if (count == -1)
                    rgb[i] = 0;
                else {
                    double hue = 0.7f + (float) count / 200f;
                    int rgbColor = Color.HSBtoRGB((float) hue, 1f, 1f);
                    rgb[i] = rgbColor;
                }
            }
            return null;
        }
        @Override
        protected void done() {
            for (int i = 0; i < displaySize; i++) {
                imageDisplay.drawPixel(i, yCoord, rgb[i]);
            }
            imageDisplay.repaint(0,0,yCoord,displaySize,1);//Указываем область для перерисовки
            rowsRemaining--;
            if (rowsRemaining == 0)
                enableGUI(true);
        }
    }
}
