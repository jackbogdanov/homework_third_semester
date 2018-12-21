package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller {
    public static final String GET_FILTERS = "Get filters";
    public static final String FINISH = "Finish";
    public static final String SEND_IMAGE_TO_SERVER = "send to server";
    public static final String RUN_FILTER = "run filter";

    private File file;

    private String filterName;
    private Client duringClient;

    public static final int PORT = 5678;

    @FXML
    private TextField loadingEditText;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ImageView imageView;

    @FXML
    private SplitMenuButton filter_menu;

    @FXML
    void onLoadButtonClick(ActionEvent event) {
        loadImage(loadingEditText.getText());
    }

    @FXML
    void onStartButtonClick(ActionEvent event) {
        System.out.println("start!");
        duringClient= new Client(file, filterName, this);
        new Thread(duringClient).start();
    }

    @FXML
    void OnSplitMenuClick(ActionEvent event) {
        getFilters();
    }

    @FXML
    void OnStopButtonClick(ActionEvent event) {
        if (duringClient != null) {
            duringClient.stopDuringTask();
        }
    }

    @FXML
    void initialize() {
        duringClient = null;
        loadImage("images/default.bmp");
        filter_menu.setText("Choose filter!");
        filterName = "";
        getFilters();
    }


    private void loadImage(String path) {
        file = new File(path);
        System.out.println("load from: " + path);
        Image image = null;
        try {
            image = new Image(file.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        setImage(image);
    }

    private void getFilters() {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF(GET_FILTERS);
            int filtersCount = in.readInt();

            for (int i = 0; i < filtersCount; i++) {
                MenuItem item = new MenuItem(in.readUTF());
                item.setOnAction(event -> {
                    filterName = item.getText();
                    filter_menu.setText(filterName);
                });
                filter_menu.getItems().add(item);
            }

            System.out.println("adding finished");
            out.writeUTF(FINISH);
            socket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImage(Image image) {
        imageView.setImage(image);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

}
