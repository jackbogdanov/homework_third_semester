import filters.IFilter;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerTask implements Runnable {

    private Socket socket;
    private boolean isRun;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    BufferedImage image;

    public ServerTask(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            isRun = true;
            image = null;
        } catch (IOException e) {
            finish();
        }
    }

    @Override
    public void run() {
        while (isRun) {
            getCommand();
        }

        System.out.println("SERVER TASK FINISHED!");
    }

    private void getCommand() {
        try {
            String command = in.readUTF();

            switch (command) {
                case Server.GET_FILTERS:
                    sendAvailableFiltersInfo();
                    break;
                case Server.SEND_IMAGE_TO_SERVER:
                    readImage();
                    break;
                case Server.FINISH:
                    finish();
                    break;
                case Server.RUN_FILTER:
                    System.out.println("filter runned");
                    runFilter();
                    break;
                default:
                    System.out.println("unknown command");
                    break;
            }
        } catch (IOException e) {
            finish();
        }
    }


    private void readImage() {
        try {
            int width = in.readInt();
            System.out.println("Width " + width);
            int height = in.readInt();
            System.out.println("Height " + height);
            int type = in.readInt();
            System.out.println("type " + type);
            image = new BufferedImage(width, height, type);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    image.setRGB(i, j, in.readInt());
                }
            }

            System.out.println("FINISHED");
        } catch (IOException e) {
            finish();
        }
    }

    private void sendAvailableFiltersInfo() {
        ArrayList<IFilter> filters = server.getFilters();
        try {
            int filtersCount = filters.size();
            out.writeInt(filtersCount);

            for (IFilter filter : filters) {
                out.writeUTF(filter.getFilterName());
            }

        } catch (IOException e) {
            finish();
        }
    }

    private void runFilter(){
        if (image != null) {
            try {
                IFilter filter = server.getFilterByName(in.readUTF());
                if (filter != null) {
                    for (int i = 0; i < image.getWidth(); i++) {
                        for (int j = 0; j < image.getHeight(); j++) {
                            out.writeInt(filter.getPixelColor(image, i, j).getRGB());
                        }
                    }
                }

                System.out.println("FILTERING FINISHED!");

            } catch (IOException e) {
                finish();
            }
        } else {
            System.out.println("Image not loaded!");
        }

    }

    private void finish() {
        try {
            isRun = false;
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
