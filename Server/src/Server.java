import filters.BlurFilter;
import filters.IFilter;
import filters.RemoveBlueFilter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static final String GET_FILTERS = "Get filters";
    public static final String FINISH = "Finish";
    public static final String SEND_IMAGE_TO_SERVER = "send to server";
    public static final String RUN_FILTER = "run filter";
    public static final int PORT = 5678;

    private ServerSocket serverSocket;
    private ArrayList<IFilter> filters;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            filters = new ArrayList<>();
            filters.add(new BlurFilter());
            filters.add(new RemoveBlueFilter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServe() {
        while (true) {
            try {
                System.out.println("wait new client");
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new ServerTask(socket, this));
                thread.start();
                System.out.println("new client added");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<IFilter> getFilters(){
        return filters;
    }

    public IFilter getFilterByName(String name) {
        for (IFilter f : filters) {
            if (f.getFilterName().equals(name)) {
                return f;
            }
        }

        System.out.println("Can't find filter!");
        return null;
    }
}
