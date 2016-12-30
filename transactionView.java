package trunk.View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import trunk.Model.*;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Daniel on 3/25/2016.
 */
public class transactionView {

    /*
     * Code for saving transaction history
     */
    public void saveHistory(Portfolio p){
        ArrayList<Portfolio> port = null;
        try {
            FileInputStream fileIn = new FileInputStream("myfile");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            port = (ArrayList<Portfolio>) in.readObject();
            in.close();
            fileIn.close();
        } catch (ClassNotFoundException ee) {
            ee.printStackTrace();
        } catch (FileNotFoundException ee) {
            ee.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        }
        port.forEach(iter -> {
            iter.cashAccountHistory = p.cashAccountHistory;
            iter.equityHistory = p.equityHistory;
        });

        try
        {
            FileOutputStream fileOut =
                    new FileOutputStream("myfile");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(port);
            out.close();
            fileOut.close();
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }
    /*
     * GUI for Transactions page
     */
    public Scene Transactions(final Stage primaryStage, Portfolio p, FPTS FPTS){
        Text title = new Text(p.loginID + "'s Transaction History");
        BorderPane border = FPTS.borderPane(primaryStage, p, title);
        VBox left = new VBox();

        ToolBar toolBar2 = new ToolBar();
        toolBar2.setOrientation(Orientation.VERTICAL);

        left.getChildren().add(toolBar2);
        left.setSpacing(10);
        Label spacer = new Label(" ");
        Button importCA = new Button("Import Account History");
        Button importEQ = new Button("Import Equity History");
        Button exportCA = new Button("Export Account History");
        Button exportEQ = new Button("Export Equity History");

        VBox histories = new VBox();
        VBox eqhistory = new VBox();
        TableView eqTable = new TableView();
        eqTable.setEditable(true);

        TableColumn type = new TableColumn("Type");
        TableColumn action = new TableColumn("Action");
        TableColumn equity = new TableColumn("Equity");
        TableColumn date1 = new TableColumn("Date");
        TableColumn shares = new TableColumn("Amount Transferred");


        type.setCellValueFactory(
                new PropertyValueFactory<History,String>("type")
        );
        action.setCellValueFactory(
                new PropertyValueFactory<History,String>("action")
        );
        equity.setCellValueFactory(
                new PropertyValueFactory<History,String>("equityName")
        );
        date1.setCellValueFactory(
                new PropertyValueFactory<History,String>("date1")
        );
        shares.setCellValueFactory(
                new PropertyValueFactory<History,Double>("shares")
        );

        ArrayList<Portfolio> port = null;
        try {
            FileInputStream fileIn = new FileInputStream("myfile");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            port = (ArrayList<Portfolio>) in.readObject();
            in.close();
            fileIn.close();
        } catch (ClassNotFoundException ee) {
            ee.printStackTrace();
        } catch (FileNotFoundException ee) {
            ee.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        }
        port.forEach(account -> {
            account.equityHistory = p.equityHistory;
        });

        Portfolio temp = null;



        for (Portfolio n : port) {
            temp = n;
        }


        final ObservableList<History> equities = FXCollections.observableArrayList(temp.equityHistory);


        eqTable.setItems(equities);
        eqTable.getColumns().addAll(type,action,equity,date1,shares);


        final Label label = new Label("Equity History");
        label.setFont(new Font("Arial", 20));
        eqhistory.getChildren().addAll(label, eqTable);


        VBox cahistory = new VBox();
        TableView caTable = new TableView();
        eqTable.setEditable(true);

        TableColumn type2 = new TableColumn("Type");
        TableColumn action2 = new TableColumn("Action");
        TableColumn ca = new TableColumn("Cash Account");
        TableColumn date2 = new TableColumn("Date");
        TableColumn amount2 = new TableColumn("Amount Transferred");

        type2.setCellValueFactory(
                new PropertyValueFactory<History,String>("type")
        );
        action2.setCellValueFactory(
                new PropertyValueFactory<History,String>("action")
        );
        ca.setCellValueFactory(
                new PropertyValueFactory<History,String>("cashAccName")
        );
        date2.setCellValueFactory(
                new PropertyValueFactory<History,String>("date2")
        );
        amount2.setCellValueFactory(
                new PropertyValueFactory<History,Double>("transferAmount2")
        );

        try {
            FileInputStream fileIn = new FileInputStream("myfile");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            port = (ArrayList<Portfolio>) in.readObject();
            in.close();
            fileIn.close();
        } catch (ClassNotFoundException ee) {
            ee.printStackTrace();
        } catch (FileNotFoundException ee) {
            ee.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        }
        port.forEach(account -> {
            account.cashAccountHistory = p.cashAccountHistory;
        });


        final ObservableList<History> cas = FXCollections.observableArrayList(p.cashAccountHistory);
        caTable.setItems(cas);
        caTable.getColumns().addAll(type2,action2,ca,amount2,date2);
        type2.prefWidthProperty().bind(caTable.widthProperty().multiply(0.1));
        action2.prefWidthProperty().bind(caTable.widthProperty().multiply(0.1));
        ca.prefWidthProperty().bind(caTable.widthProperty().multiply(0.30));
        final Label label2 = new Label("Cash Account History");
        label2.setFont(new Font("Arial", 20));
        cahistory.getChildren().addAll(label2, caTable);

        histories.getChildren().addAll(eqhistory,cahistory);
        toolBar2.getItems().addAll(spacer, importCA, importEQ, exportCA, exportEQ);

        importCA.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String csv = null;
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                File file = fileChooser.showOpenDialog(FPTS.stage);
                if(file != null){
                    Equity e = null;
                    History h = new History("","",e,"",0,0);
                    h.importCAHistory(file.toString(),p);
                    Scene scene = Transactions(primaryStage,p, FPTS);
                    FPTS.stage.setScene(scene);
                }
            }
        });
        importEQ.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String csv = null;
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                File file = fileChooser.showOpenDialog(FPTS.stage);
                if(file != null){
                    Equity e = null;
                    History h = new History("","",e,"",0,0);
                    h.importEQHistory(file.toString(),p);
                    Scene scene = Transactions(primaryStage,p, FPTS);
                    FPTS.stage.setScene(scene);
                }
            }
        });
        //Action for exporting equities
        exportEQ.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Stage dialog = new Stage();
                dialog.initOwner(primaryStage);
                GridPane grid2 = new GridPane();
                grid2.setAlignment(Pos.CENTER);
                grid2.setHgap(10);
                grid2.setVgap(10);
                grid2.setPadding(new Insets(5, 10, 5, 10));

                TextField file = new TextField();
                grid2.add(new Label("Filename: "),0,1);
                grid2.add(file,1,1);

                Scene dialogScene = new Scene(grid2, 400, 400);
                dialog.setScene(dialogScene);
                dialog.show();
                Button cancel = new Button("Cancel");
                grid2.add(cancel,1,7);
                Button submit = new Button("Submit");
                grid2.add(submit,0,7);
                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        dialog.close();
                    }
                });
                submit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Equity e = null;
                        History h = new History("","",e,"",0,0);
                        h.exportEquityHistory(file.getText(),p);
                        dialog.close();
                    }
                });
            }
        });
        //Action for exporting a cash account
        exportCA.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Stage dialog = new Stage();
                dialog.initOwner(primaryStage);
                GridPane grid2 = new GridPane();
                grid2.setAlignment(Pos.CENTER);
                grid2.setHgap(10);
                grid2.setVgap(10);
                grid2.setPadding(new Insets(5, 10, 5, 10));

                TextField file = new TextField();
                grid2.add(new Label("Filename: "),0,1);
                grid2.add(file,1,1);

                Scene dialogScene = new Scene(grid2, 400, 400);
                dialog.setScene(dialogScene);
                dialog.show();
                Button cancel = new Button("Cancel");
                grid2.add(cancel,1,7);
                Button submit = new Button("Submit");
                grid2.add(submit,0,7);
                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        dialog.close();
                    }
                });
                submit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Equity e = null;
                        History h = new History("","",e,"",0,0);
                        h.exportCAHistory(file.getText(),p);
                        dialog.close();
                    }
                });
            }
        });
        border.setCenter(histories);
        border.setLeft(left);
        Scene scene = new Scene(border, 1200, 700);
        return scene;
    }
}
