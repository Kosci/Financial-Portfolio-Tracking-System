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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import trunk.Model.CashAccount;
import trunk.Model.Portfolio;
import trunk.Model.UndoAction;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Daniel on 3/25/2016.
 */

public class CAView {
    /*
     * Code for saving cash accounts
     */
    public void saveCA(Portfolio p){
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
        if(port != null) {
            port.forEach(iter -> {
                iter.cashAccList = p.cashAccList;
            });
        }

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
     * GUI for Cash Accounts page
     */
    public Scene CashAccounts(final Stage primaryStage, Portfolio p, FPTS FPTS){
        Text title = new Text(p.loginID + "'s Cash Accounts");
        BorderPane border = FPTS.borderPane(primaryStage, p, title);

        VBox left = new VBox();

        ToolBar toolBar2 = new ToolBar();
        toolBar2.setOrientation(Orientation.VERTICAL);

        left.getChildren().add(toolBar2);
        left.setSpacing(10);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));

        final ObservableList<CashAccount> cashAccounts = FXCollections.observableArrayList(p.cashAccList);
        TableView table = new TableView();
        table.setEditable(true);

        vbox.getChildren().addAll(title, table);

        TableColumn name = new TableColumn("Account Name");
        TableColumn amount = new TableColumn("Balance");
        TableColumn date = new TableColumn("Date Created");

        name.setCellValueFactory(
                new PropertyValueFactory<CashAccount,String>("name")
        );
        amount.setCellValueFactory(
                new PropertyValueFactory<CashAccount,Double>("balance")
        );
        date.setCellValueFactory(
                new PropertyValueFactory<CashAccount, String>("date")
        );
        Label spacer = new Label(" ");
        Button add = new Button("Add Account");
        Button remove = new Button("Remove Account");
        Button deposit = new Button("Deposit Funds");
        Button withdrawal = new Button("Withdraw Funds");
        Button transfer = new Button("Transfer Funds");
        Button exportCA = new Button("Export Account");
        Button importCA = new Button("Import Account");

        toolBar2.getItems().addAll(spacer, add, remove, deposit, withdrawal, transfer, exportCA, importCA);

        transfer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Stage dialog = new Stage();
                dialog.initOwner(primaryStage);
                GridPane grid2 = new GridPane();
                grid2.setAlignment(Pos.CENTER);
                grid2.setHgap(10);
                grid2.setVgap(10);
                grid2.setPadding(new Insets(5, 10, 5, 10));

                TextField to = new TextField();
                grid2.add(new Label("Account to Transfer to: "),0,1);
                grid2.add(to,1,1);

                TextField from = new TextField();
                grid2.add(new Label("Account to Transfer from: "),0,2);
                grid2.add(from,1,2);

                TextField num = new TextField();
                grid2.add(new Label("Amount to Transfer: "),0,3);
                grid2.add(num,1,3);
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
                        CashAccount c1 = new CashAccount("","",0,"",null);
                        CashAccount c2 = new CashAccount("","",0,"",null);
                        for(CashAccount c : p.cashAccList){
                            if(c.getName().equals(to.getText().trim())){
                                c1 = c;
                                break;
                            }
                        }
                        for(CashAccount c : p.cashAccList){
                            if(c.getName().equals(from.getText().trim())){
                                c2 = c;
                                break;
                            }
                        }
                        final Text error = new Text();
                        grid2.add(error,1,5);
                        if(Double.parseDouble(num.getText()) > c2.balance){
                            error.setFill(Color.FIREBRICK);
                            error.setText("Cannot transfer more than the account's balance.");
                        }
                        else if(Double.parseDouble(num.getText()) < 0){
                            error.setFill(Color.FIREBRICK);
                            error.setText("Enter a valid amount of money");
                        }
                        else {
                            CashAccount c = new CashAccount("","",0,"",null);
                            c.transfer(c1, c2, Double.parseDouble(num.getText()),p);

                            UndoAction transferCA = new UndoAction("Transfer", "CashAccount", c1, c2, Double.parseDouble(num.getText()),null,"");
                            transferCA.addToQueue(transferCA);

                            dialog.close();
                            Scene scene = CashAccounts(primaryStage, p, FPTS);
                            FPTS.stage.setScene(scene);
                        }
                    }
                });
            }
        });
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();

                for(CashAccount c : p.cashAccList){
                    choices.add(c.name);
                }

                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);

                dialog.setContentText("Choose the cash account you wish to remove:");

                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()) {
                    CashAccount c = new CashAccount("","",0,"",null);
                    for (CashAccount n : p.cashAccList) {
                        if (n.getName().equals(result.get())) {
                            c = n;
                        }
                    }
                    c.deleteCashAccount(result.get(),p);

                    UndoAction add = new UndoAction("Remove", "CashAccount", c,null,0,null,"");
                    add.redo.clear();
                    add.addToQueue(add);

                    Scene scene = CashAccounts(primaryStage,p, FPTS);
                    FPTS.stage.setScene(scene);
                }
            }
        });

        //Action for importing cash account
        importCA.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                File file = fileChooser.showOpenDialog(FPTS.stage);
                if(file != null){
                    ArrayList<CashAccount> existingAccts = new ArrayList<CashAccount>();
                    CashAccount c = new CashAccount("","",0,"",p);
                    existingAccts = c.importCA(file.toString(),p);
                    UndoAction add = new UndoAction("Import", "CashAccount", null,null,0,null,file.toString());
                    add.redo.clear();
                    add.addToQueue(add);

                    for(CashAccount exAcct : existingAccts){
                        Text title = new Text("Account " + exAcct.name + " already exists");
                        final Stage dialog = new Stage();
                        dialog.initOwner(primaryStage);
                        GridPane grid2 = new GridPane();
                        grid2.setAlignment(Pos.CENTER);
                        grid2.setHgap(10);
                        grid2.setVgap(10);
                        grid2.setPadding(new Insets(5, 10, 5, 10));
                        grid2.add(title, 0, 0);

                        Button ignore = new Button("Overwrite");
                        Button addbtn = new Button("Add to Balance");
                        Button cancel = new Button("Cancel");

                        grid2.add(ignore, 0, 1);
                        grid2.add(addbtn, 0, 2);
                        grid2.add(cancel, 0, 3);
                        ignore.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                CashAccount c = new CashAccount("", "", 0, "", p);
                                c.addCashAccount(exAcct.name, exAcct.balance, exAcct.dateCreated, p);

                                Scene scene = CashAccounts(primaryStage, p, FPTS);

                                FPTS.stage.setScene(scene);
                                dialog.close();
                            }
                        });
                        addbtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                CashAccount c = new CashAccount("", "", 0, "", null);
                                c.updateBalance(exAcct.name, exAcct.balance, true, p);
                                Scene scene = CashAccounts(primaryStage, p, FPTS);

                                FPTS.stage.setScene(scene);
                                dialog.close();
                                dialog.close();
                            }
                        });
                        cancel.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                dialog.close();
                            }
                        });
                        Scene dialogScene = new Scene(grid2, 300, 200);
                        dialog.setScene(dialogScene);
                        dialog.show();
                    }

                    Scene scene = CashAccounts(primaryStage,p, FPTS);
                    FPTS.stage.setScene(scene);
                }
            }
        });

        //Action for depositing funds to a cash account
        deposit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                for(CashAccount e : p.cashAccList){
                    if(!e.name.equals("")) {
                        choices.add(e.name);
                    }
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the cash account to deposit to:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    final Stage dialog2 = new Stage();
                    dialog2.initOwner(primaryStage);
                    GridPane grid2 = new GridPane();
                    grid2.setAlignment(Pos.CENTER);
                    grid2.setHgap(10);
                    grid2.setVgap(10);
                    grid2.setPadding(new Insets(5, 10, 5, 10));

                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("This is a Dialog"));
                    TextField shares = new TextField();

                    grid2.add(new Label("Deposit to " + result.get() + ": "),0,1);
                    grid2.add(shares,1,1);
                    Scene dialogScene = new Scene(grid2, 400, 400);
                    Button cancel = new Button("Cancel");
                    grid2.add(cancel,1,3);
                    Button submit = new Button("Submit");
                    grid2.add(submit,0,3);
                    cancel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {dialog2.close();}
                    });
                    final Text error = new Text();
                    grid2.add(error, 1, 8);
                    submit.setOnAction(new EventHandler<ActionEvent>() { // this one
                        @Override
                        public void handle(ActionEvent event) {
                            if(shares.getText().isEmpty()){
                                error.setFill(Color.FIREBRICK);
                                error.setText("Fill in all fields");
                            }
                            else if(!FPTS.isInt(shares.getText()))
                            {
                                error.setFill(Color.FIREBRICK);
                                error.setText("Enter information in correct format");
                            }
                            else if(Integer.parseInt(shares.getText()) < 1){
                                error.setFill(Color.FIREBRICK);
                                error.setText("Enter valid number");
                            }
                            else{
                                int numShares = Integer.parseInt(shares.getText());
                                for(CashAccount e : p.cashAccList){
                                    if(e.name.equals(result.get())){
                                        e.balance += numShares;
                                    }
                                }

                                dialog2.close();
                                Scene scene = CashAccounts(primaryStage,p, FPTS);
                                FPTS.stage.setScene(scene);
                            }
                        }
                    });

                    dialog2.setScene(dialogScene);
                    dialog2.show();
                }

            }
        });

        //Action for withdrawing funds from a cash account
        withdrawal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                for(CashAccount e : p.cashAccList){
                    if(!e.name.equals("")) {
                        choices.add(e.name);
                    }
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the cash account to withdraw from:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    final Stage dialog2 = new Stage();
                    dialog2.initOwner(primaryStage);
                    GridPane grid2 = new GridPane();
                    grid2.setAlignment(Pos.CENTER);
                    grid2.setHgap(10);
                    grid2.setVgap(10);
                    grid2.setPadding(new Insets(5, 10, 5, 10));

                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("This is a Dialog"));
                    TextField amount = new TextField();

                    grid2.add(new Label("Withdraw from " + result.get() + ": "),0,1);
                    grid2.add(amount,1,1);
                    Scene dialogScene = new Scene(grid2, 400, 400);
                    Button cancel = new Button("Cancel");
                    grid2.add(cancel,1,3);
                    Button submit = new Button("Submit");
                    grid2.add(submit,0,3);
                    cancel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {dialog2.close();}
                    });
                    final Text error = new Text();
                    grid2.add(error, 1, 8);
                    submit.setOnAction(new EventHandler<ActionEvent>() { // this one
                        @Override
                        public void handle(ActionEvent event) {
                            if(amount.getText().isEmpty()){
                                error.setFill(Color.FIREBRICK);
                                error.setText("Fill in all fields");
                            }
                            else if(!FPTS.isInt(amount.getText()))
                            {
                                error.setFill(Color.FIREBRICK);
                                error.setText("Enter information in correct format");
                            }
                            else if(Integer.parseInt(amount.getText()) < 0){
                                error.setFill(Color.FIREBRICK);
                                error.setText("Enter valid number");
                            }
                            else{
                                int amountInt = Integer.parseInt(amount.getText());
                                for(CashAccount e : p.cashAccList){
                                    if(e.name.equals(result.get())){
                                        if(e.balance > amountInt) {
                                            e.balance -= amountInt;

                                            dialog2.close();
                                            Scene scene = CashAccounts(primaryStage,p, FPTS);
                                            FPTS.stage.setScene(scene);
                                        } else{
                                            error.setFill(Color.FIREBRICK);
                                            error.setText("Cannot overdraw account");
                                        }
                                    }
                                }
                            }
                        }
                    });

                    dialog2.setScene(dialogScene);
                    dialog2.show();
                }
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
                        CashAccount c = new CashAccount("","",0,"",null);
                        c.exportCA(file.getText(),p);
                        dialog.close();
                    }
                });
            }
        });

        //Action for adding a cash account
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Stage dialog = new Stage();
                dialog.initOwner(primaryStage);
                GridPane grid2 = new GridPane();
                grid2.setAlignment(Pos.CENTER);
                grid2.setHgap(10);
                grid2.setVgap(10);
                grid2.setPadding(new Insets(5, 10, 5, 10));
                Label name = new Label("Name:");
                grid2.add(name,0,1);
                final TextField CAname = new TextField();
                grid2.add(CAname,1,1);

                Label amount = new Label("Balance:");
                grid2.add(amount,0,2);
                TextField Iamount = new TextField();
                grid2.add(Iamount,1,2);

                Label date = new Label("Date:");
                grid2.add(date,0,3);
                TextField CAdate = new TextField();
                grid2.add(CAdate,1,3);

                final Text error = new Text();
                grid2.add(error, 1, 6);
                Button submit = new Button("Submit");
                grid2.add(submit,1,4);
                submit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        boolean same = false;
                        for(CashAccount c : p.cashAccList){
                            if(c.getName().equals(CAname.getText())){
                                same = true;
                                break;
                            }
                        }
                        if(Double.parseDouble(Iamount.getText()) > 0) {
                            if (!same) {
                                CashAccount c = new CashAccount("", "", 0, "", p);
                                c.addCashAccount(CAname.getText(), Double.parseDouble(Iamount.getText()), CAdate.getText(), p);
                                CashAccount temp = null;
                                for (CashAccount n : p.cashAccList) {
                                    if (n.getName().equals(CAname.getText())) {
                                        temp = n;
                                    }
                                }
                                UndoAction add = new UndoAction("Add", "CashAccount", temp,null,0,null,"");
                                add.redo.clear();
                                add.addToQueue(add);

                                Scene scene = CashAccounts(primaryStage, p, FPTS);

                                FPTS.stage.setScene(scene);
                                dialog.close();
                            } else if (same) {
                                dialog.close();
                                Text title = new Text("Account already exists");
                                final Stage dialog = new Stage();
                                dialog.initOwner(primaryStage);
                                GridPane grid2 = new GridPane();
                                grid2.setAlignment(Pos.CENTER);
                                grid2.setHgap(10);
                                grid2.setVgap(10);
                                grid2.setPadding(new Insets(5, 10, 5, 10));
                                grid2.add(title, 0, 0);

                                Button ignore = new Button("Overwrite");
                                Button add = new Button("Add to Balance");
                                Button cancel = new Button("Cancel");

                                grid2.add(ignore, 0, 1);
                                grid2.add(add, 0, 2);
                                grid2.add(cancel, 0, 3);
                                ignore.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        CashAccount c = new CashAccount("", "", 0, "", p);
                                        c.addCashAccount(CAname.getText(), Double.parseDouble(Iamount.getText()), CAdate.getText(), p);

                                        Scene scene = CashAccounts(primaryStage, p, FPTS);

                                        FPTS.stage.setScene(scene);
                                        dialog.close();
                                    }
                                });
                                add.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        CashAccount c = new CashAccount("", "", 0, "", null);
                                        c.updateBalance(CAname.getText(), Double.parseDouble(Iamount.getText()), true, p);
                                        Scene scene = CashAccounts(primaryStage, p, FPTS);

                                        FPTS.stage.setScene(scene);
                                        dialog.close();
                                        dialog.close();
                                    }
                                });
                                cancel.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        dialog.close();
                                    }
                                });
                                Scene dialogScene = new Scene(grid2, 300, 200);
                                dialog.setScene(dialogScene);
                                dialog.show();
                            }
                        } else {
                            error.setFill(Color.FIREBRICK);
                            error.setText("Enter a valid amount of money");
                        }
                    }
                });

                Scene dialogScene = new Scene(grid2, 300, 200);
                dialog.setScene(dialogScene);
                dialog.show();
            };
        });


        table.setItems(cashAccounts);
        table.getColumns().addAll(name, amount, date);
        name.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        amount.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        date.prefWidthProperty().bind(table.widthProperty().multiply(0.075));

        border.setCenter(vbox);
        border.setLeft(left);
        Scene scene = new Scene(border, 1200, 700);
        return scene;
    };
}
