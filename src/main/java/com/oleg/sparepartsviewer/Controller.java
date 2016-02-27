package com.oleg.sparepartsviewer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML private TabPane main;
    @FXML private TextField importTextField;
    @FXML private TableView table;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private ListView importedListView;
    @FXML private MenuButton idColumnMenu;
    @FXML private MenuButton priceColumnMenu;
    @FXML private TextField nameTextField;
    @FXML private VBox vBox;

    private File selectedFile;
    DBWrapper dbWrapper;

    public Controller() throws SQLException, ClassNotFoundException, PropertyVetoException {
        dbWrapper = new DBWrapper();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> tables = dbWrapper.getExistTables();
                    importedListView.getItems().addAll(tables);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @FXML
    protected void browsePressed(){
        Stage stage = (Stage) main.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null){
            importTextField.setText(selectedFile.getPath());
            nameTextField.setText(selectedFile.getName());
            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String line = reader.readLine();
                String[] fields = line.split(";");
                List<MenuItem> idItems = new ArrayList<>(fields.length);
                for (String field : fields){
                    final MenuItem item = new MenuItem(field);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            idColumnMenu.setText(item.getText());
                        }
                    });
                    idItems.add(item);
                    idColumnMenu.setText(item.getText());

                }
                idColumnMenu.getItems().addAll(idItems);


                List<MenuItem> priceItems = new ArrayList<>(fields.length);
                for (String field : fields){
                    final MenuItem item = new MenuItem(field);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            priceColumnMenu.setText(item.getText());
                        }
                    });
                    priceItems.add(item);
                    priceColumnMenu.setText(item.getText());
                }
                priceColumnMenu.getItems().addAll(priceItems);
            } catch (Exception e) {
                showDialog("Error", e.getMessage());
            }
        }else{
            importTextField.setText("");
        }
    }

    @FXML
    protected void importPressed(){
        if (selectedFile == null){
            return;
        }

        try {
            dbWrapper.importCSV(selectedFile, nameTextField.getText(), idColumnMenu.getText(), priceColumnMenu.getText());
        } catch (Exception e) {
            showDialog("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void clickSearch(){
        String text = searchTextField.getText();
        try {
            List<String> tables = dbWrapper.getExistTables();
            for (String table : tables){

            }
        } catch (SQLException e) {
            showDialog("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showDialog(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
