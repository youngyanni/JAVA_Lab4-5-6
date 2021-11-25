import java.awt.geom.Rectangle2D;
public class Tricorn extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;
    //Устанавливаем начальный диапазон
    @Override
    public void getInitialRange(Rectangle2D.Double range){
        range.x=-2;
        range.y=-2;
        range.height=4;
        range.width=4;
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
            double m = (-2) * R * I + y;
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
        return "Tricorn";
    }
}
