import sun.java2d.pipe.DrawImage;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JImageDisplay extends JComponent{
    public  BufferedImage image;
    // объявление изображения и его параметров
    JImageDisplay(int w,int h) {
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Dimension dem = new Dimension(w, h);
        super.setPreferredSize(dem);
    }
    // метод отрисовки изображения
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage (image, 0, 0, image.getWidth(), image.getHeight(), null);
        }
    // очистка изображения (устанавливает все пиксели в черный цвет)
        public void clearImage(){
        for (int w=0;w<image.getWidth();w++){
            for(int h=0;h<image.getHeight();h++){
                drawPixel(w,h,0);
            }
        }
        }
    // задание цвета конкретному пикселю
        public void drawPixel(int x,int y,int rgbColor){
            image.setRGB(x,y,rgbColor);
        }
    }


