package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ProgressBar;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {
    private String filterName;
    private Socket socket;
    private BufferedImage readImage;
    private DataOutputStream out;
    private DataInputStream in;
    private Controller controller;
    private double progress;
    private ProgressBar progressBar;

    private boolean isStopped;

    public Client(File file, String filterName, Controller controller) {
        try {
            socket = new Socket(InetAddress.getLocalHost(), Controller.PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            readImage = ImageIO.read(file);
            progressBar = controller.getProgressBar();
            progressBar.setProgress(0);
            isStopped = false;

            progress = 0;
            this.controller = controller;
            this.filterName = filterName;
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        System.out.println("runned");
        sendFile();
        applyFilter();
        closeServer();
        stopDuringTask();
        System.out.println("CLIENT TASK FINISHED!");
    }

    private void sendFile() {

        if (isStopped || filterName.equals("")) {
            isStopped = true;
            return;
        }

        try {
            out.writeUTF(Controller.SEND_IMAGE_TO_SERVER);

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
                    progressBar.setProgress(progress / (readImage.getHeight() * readImage.getWidth() * 2));
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
            out.writeUTF(Controller.RUN_FILTER);
            out.writeUTF(filterName);

            for (int i = 0; i < readImage.getWidth(); i++) {
                for (int j = 0; j < readImage.getHeight(); j++) {
                    if (isStopped) {
                        return;
                    }
                    progress++;
                    readImage.setRGB(i, j, in.readInt());
                    progressBar.setProgress(progress / (readImage.getHeight() * readImage.getWidth() * 2));
                }
            }
            controller.setImage(SwingFXUtils.toFXImage(readImage, null));
            System.out.println("All finished");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeServer() {
        try {
            out.writeUTF(Controller.FINISH);
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
