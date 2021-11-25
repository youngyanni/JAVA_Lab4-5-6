import java.awt.geom.Rectangle2D;
public class Mandelbrot extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;
    //Устанавливаем начальный диапазон
    @Override
    public void getInitialRange(Rectangle2D.Double range){
        range.x=-2;
        range.y=-1.5;
        range.height=3;
        range.width=3;
    }
    //Итеративная функция для фрактала Мандельброта
    @Override
    public int numIterations(double x, double y){
        double R = 0;
        double I = 0;
        int counter = 0;
        while (counter < MAX_ITERATIONS) {
            counter++;//Кол-во итераций
            double k = R * R - I*I+x;
            double m = 2 * R * I + y;
            R = k;
            I = m;
            if (R*R+I*I > 4)
                break;
        }
        if (counter == MAX_ITERATIONS)
            return -1;
        return counter;
    }
    @Override
    public String toString(){
        return "Mandelbrot";
    }
}


