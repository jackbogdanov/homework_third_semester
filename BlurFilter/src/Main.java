import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        try {
            ThreadManager manager = new ThreadManager(ImageIO.read(new File("res/ball.bmp")));

            manager.startTreads(8, ThreadManager.HORIZONTAL_MODE);

            manager.saveToFile("result");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
