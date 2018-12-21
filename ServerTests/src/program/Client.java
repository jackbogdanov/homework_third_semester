package program;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ProgressBar;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {
    public static final String GET_FILTERS = "Get filters";
    public static final String FINISH = "Finish";
    public static final String SEND_IMAGE_TO_SERVER = "send to server";
    public static final String RUN_FILTER = "run filter";

    private String filterName;
    private Socket socket;
    private BufferedImage readImage;
    private DataOutputStream out;
    private DataInputStream in;

    private int id;
    private long[] results;
    //private Controller controller;
    private double progress;
    //private ProgressBar progressBar;

    private boolean isStopped;

    public Client(File file, String filterName, int id, long[] results) {
        try {
            socket = new Socket(InetAddress.getLocalHost(), 5678);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            readImage = ImageIO.read(file);
            //progressBar = controller.getProgressBar();
            //progressBar.setProgress(0);
            isStopped = false;

            this.results = results;
            this.id = id;

            progress = 0;
            //this.controller = controller;
            this.filterName = filterName;
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        sendFile();
        applyFilter();
        closeServer();
        stopDuringTask();
        if (results != null) {
            results[id] = System.currentTimeMillis() - startTime;
        }
    }

    private void sendFile() {

        if (isStopped) {
            return;
        }

        try {
            out.writeUTF(SEND_IMAGE_TO_SERVER);

            out.writeInt(readImage.getWidth());
            System.out.println("sended width " + readImage.getWidth());
            out.writeInt(readImage.getHeight());
            System.out.println("sended height " + readImage.getHeight());
            out.writeInt(readImage.getType());
            System.out.println("sended type " + readImage.getType());

            for (int i = 0; i < readImage.getWidth(); i++) {
                for (int j = 0; j < readImage.getHeight(); j++) {
                    if (isStopped) {
                        return;
                    }
                    progress++;
                    out.writeInt(readImage.getRGB(i, j));
                    //progressBar.setProgress(progress / (readImage.getHeight() * readImage.getWidth() * 2));
                }
            }

            System.out.println("finished sending!");
        } catch (IOException e) {
        }
    }
    
    private void applyFilter() {
        if (isStopped) {
            return;
        }

        try {
            out.writeUTF(RUN_FILTER);
            out.writeUTF(filterName);

            for (int i = 0; i < readImage.getWidth(); i++) {
                for (int j = 0; j < readImage.getHeight(); j++) {
                    if (isStopped) {
                        return;
                    }
                    progress++;
                    readImage.setRGB(i, j, in.readInt());
                    //progressBar.setProgress(progress / (readImage.getHeight() * readImage.getWidth() * 2));
                }
            }
            //controller.setImage(SwingFXUtils.toFXImage(readImage, null));
            System.out.println("All finished");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeServer() {
        try {
            out.writeUTF(FINISH);
        } catch (IOException e) {
        }
    }

    public void stopDuringTask() {
        try {
            isStopped = true;
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
        }
    }
}
