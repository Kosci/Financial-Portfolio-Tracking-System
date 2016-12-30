/**
 * Created by Daniel on 3/6/2016.
 */
package trunk.View;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DateTimeStringConverter;
import trunk.Control.UpdateEQ;
import trunk.Model.Portfolio;
import trunk.Model.UndoAction;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

//import trunk.Model.UndoAction;

//import svn.trunk.Model.Portfolio;

public class FPTS extends Application {
    private static ArrayList<Portfolio> portfolios = new ArrayList<Portfolio>();
    public Stage stage;
    int pindex = 0;
    public static String id;
    
    public static void main(String[] args) {
        //Deleting portfolio by command line
        if(args.length!= 0){
            int i = 0;
            while(i < args.length){
                if(args[i].equals("-delete")){
                    ArrayList<Portfolio> port = null;
                    try {
                        FileInputStream fileIn = new FileInputStream("myfile");
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        port = (ArrayList<Portfolio>) in.readObject();
                        in.close();
                        fileIn.close();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (Portfolio p : port) {
                        if (p.loginID.equals(args[i+1])) {
                            port.remove(p);
                            try {
                                FileOutputStream fos = new FileOutputStream("myfile");
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(port);
                                oos.close();
                                fos.close();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
                        }
                    }
                }
                i++;
            }
        }
        launch(args);
    }

    /*
     * Check if string is a double
     */
    public static boolean isDouble(String numString) {
        try {
            Double.parseDouble(numString);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /*
     * Check if string is an int
     */
    public static boolean isInt(String numString) {
        try {
            Integer.parseInt(numString);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /*
     * Check if string is a date
     */
    public static boolean isDate(String date){
        DateTimeStringConverter format = new DateTimeStringConverter("MM/dd/YYYY");
        try{
            format.fromString(date);
        } catch (Exception ex){
            return false;
        }
        return true;
    }

    /*
     * Code for adding a portfolio
     */
    public void addPortfolio(String username, String password){
        Portfolio portfolio = new Portfolio("","");
        portfolio.loginID = username;
        portfolio.password = password;
        ArrayList<Portfolio> port2 = null;
        try {
            FileInputStream fileIn = new FileInputStream("myfile");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            port2 = (ArrayList<Portfolio>)in.readObject();
            in.close();
            fileIn.close();
        } catch (ClassNotFoundException ee) {
            ee.printStackTrace();
        } catch (FileNotFoundException ee) {
            ee.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        }
        if(port2 != null) {
            portfolios = port2;
        }
        portfolios.add(pindex, portfolio);
        pindex++;
        try {
            FileOutputStream fos = new FileOutputStream("myfile");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(portfolios);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*
     *  Code for basic skeleton of border pane
     */
    public BorderPane borderPane(Stage primaryStage, Portfolio p, Text t){
        EquitiesView equitiesView = new EquitiesView();
        CAView caView = new CAView();
        simulationView simulationView = new simulationView();
        transactionView transactionView = new transactionView();
        WatchlistView watchlistView = new WatchlistView();
        BorderPane border = new BorderPane();

        primaryStage.setTitle(p.loginID + "'s Portfolio");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 10, 5, 10));

        Text title = t;
        title.setFont(Font.font("Calibri",FontWeight.NORMAL, 20));
        grid.add(title,0,0,2,1);

        Button watchList = new Button("Watchlist");
        Button equities = new Button("Equities");
        Button history = new Button("Transaction History");
        Button cas = new Button("Cash Accounts");
        Button logout = new Button("Logout");
        Button sim = new Button("Simulation");
        Button importP = new Button("Import Portfolio");
        Button exportP = new Button("Export Portfolio");
        Button undo = new Button("Undo");
        Button redo = new Button("Redo");

        Button update = new Button("Update");

        undo.setOnAction(new EventHandler<ActionEvent>() { // Sadaf
            @Override
            public void handle(ActionEvent event) {
                //code here

                UndoAction test = new UndoAction(p);
                test.handleCommand(primaryStage,FPTS.this,equitiesView,caView);


            }
        });

        redo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UndoAction test = new UndoAction(p);
                test.redo(primaryStage,FPTS.this,equitiesView,caView);
            }
        });

        logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                UndoAction test = new UndoAction(p);

                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Do you want to save changes to your portfolio?");

                ButtonType save = new ButtonType("Save changes");
                ButtonType nosave = new ButtonType("Quit without saving");
                ButtonType cancel = new ButtonType("Cancel");

                alert.getButtonTypes().setAll(save, nosave, cancel);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == save){
                    test.allclear();
                    caView.saveCA(p);
                    equitiesView.saveEquities(p);
                    transactionView.saveHistory(p);
                    watchlistView.saveWatchlist(p);
                    p.saveMAs(p);

                    Scene scene = login(primaryStage);
                    stage.setScene(scene);
                }
                else if(result.get() == nosave){
                    test.allclear();
                    Scene scene = login(primaryStage);
                    stage.setScene(scene);
                }
                else{
                    return;
                }
            }
        });

        importP.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                File file = fileChooser.showOpenDialog(stage);
                if(file != null){
                    p.importP(file.toString(),p);
                }
            }
        });

        exportP.setOnAction(new EventHandler<ActionEvent>() {
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
                        Portfolio pInfo = new Portfolio("","");
                        pInfo.exportP(file.getText(),p);
                        dialog.close();
                    }
                });
            }
        });

        VBox topContainer = new VBox(2);  //Creates a container to hold all Menu Objects.
        HBox buttonBar = new HBox(5);  //Creates our tool-bar to hold the buttons.

        Label space = new Label(" ");
        Label space2 = new Label(" ");
        Label spacer = new Label("   ");
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMaxWidth(Double.MAX_VALUE);
        Label spacer2 = new Label("   ");
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        spacer2.setMaxWidth(Double.MAX_VALUE);

        topContainer.getChildren().add(buttonBar);
        border.setTop(buttonBar);

        watchList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene scene = watchlistView.ViewWatchlist(primaryStage,p,FPTS.this);
                stage.setScene(scene);
            }
        });
        equities.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene scene = equitiesView.ViewEquities(primaryStage,p,FPTS.this);
                stage.setScene(scene);
            }
        });
        history.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene scene = transactionView.Transactions(primaryStage,p,FPTS.this);
                stage.setScene(scene);
            }
        });
        cas.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene scene = caView.CashAccounts(primaryStage,p,FPTS.this);
                stage.setScene(scene);
            }
        });
        sim.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene scene = simulationView.simulation(primaryStage, p, FPTS.this);
                stage.setScene(scene);
            }
        });
        update.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    UpdateEQ updateEQ = new UpdateEQ();
                    updateEQ.updateEquities(p, p.equityList2);
                }catch(Exception ex){
                    ex.getMessage();
                }
            }
        });

        buttonBar.getChildren().addAll(space, importP, exportP, spacer2, watchList, equities, history, cas,
                sim, update, spacer, undo, redo, logout, space2);
        
        border.setCenter(grid);
        return border;
    }
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        Scene login = login(primaryStage);

        primaryStage.setScene(login);
        primaryStage.show();
    }


    /*
     * GUI for login page
     */
    public Scene login(final Stage primaryStage){
        primaryStage.setTitle("FPTS");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 10, 5, 10));

        Text title = new Text("Financial Portfolio Tracking System");
        title.setFont(Font.font("Calibri",FontWeight.NORMAL, 20));
        grid.add(title,0,0,2,1);

        Label user = new Label("Username:");
        grid.add(user,0,1);
        final TextField username = new TextField();
        grid.add(username,1,1);

        Label pass = new Label("Password:");
        grid.add(pass,0,2);
        PasswordField password = new PasswordField();
        grid.add(password,1,2);

        Button login = new Button("Login");
        grid.add(login,0,4);

        Button register = new Button("Register");
        grid.add(register,1,4);

        ArrayList<Portfolio> port = null;
        try {
            FileInputStream fileIn = new FileInputStream("myfile");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            port = (ArrayList<Portfolio>) in.readObject();
            in.close();
            fileIn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ArrayList<Portfolio> ports = port;

        final Text error = new Text();
        grid.add(error, 1, 6);
        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(username.getText().trim().isEmpty() || password.getText().trim().isEmpty()){
                    error.setFill(Color.FIREBRICK);
                    error.setText("Fill in all fields");
                }
                else {
                    if(ports != null) {
                        for (Portfolio p : ports) {
                            if (p.loginID.equals(username.getText().trim()) && p.password.equals(password.getText().trim())) {
                                Scene scene = portfolio(primaryStage, p);
                                stage.setScene(scene);
                            }
                        }
                    }
                    error.setFill(Color.FIREBRICK);
                    error.setText("Invalid username or password");
                }
            }
        });

        register.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene scene = register(primaryStage);
                stage.setScene(scene);
            }
        });

        Scene scene = new Scene(grid, 1200, 700);
        return scene;
    }


    //GUI for user registration page
    public Scene register(final Stage primaryStage){
        Portfolio portfolio = new Portfolio("","");

        primaryStage.setTitle("Registration");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 10, 5, 10));

        Text title = new Text("Enter your Information");
        title.setFont(Font.font("Calibri",FontWeight.NORMAL, 20));
        grid.add(title,0,0,2,1);

        Label user = new Label("Username:");
        grid.add(user,0,1);
        final TextField username = new TextField();
        grid.add(username,1,1);

        Label pass = new Label("Password:");
        grid.add(pass,0,2);
        PasswordField password = new PasswordField();
        grid.add(password,1,2);

        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Yes");
        rb1.setToggleGroup(group);

        RadioButton rb2 = new RadioButton("No");
        rb2.setToggleGroup(group);

        Label imp = new Label("Import Portfolio?");
        grid.add(imp,0,3);
        grid.add(rb1,0,4);
        grid.add(rb2,1,4);

        final Text error = new Text();
        grid.add(error, 1, 6);
        Button register = new Button("Register");
        grid.add(register,0,5);
        register.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                final String name = username.getText().trim();
                if(username.getText().trim().isEmpty() || password.getText().trim().isEmpty()){
                    error.setFill(Color.FIREBRICK);
                    error.setText("Fill in all fields");

                }
                else {
                    boolean usertaken = false;
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
                        for (Portfolio p : port) {
                            if (p.loginID.equals(username.getText().trim())) {
                                error.setFill(Color.FIREBRICK);
                                error.setText("Username is already taken");
                                usertaken = true;
                            }
                        }
                    }
                    if(usertaken == false) {
                        final Stage dialog = new Stage();
                        dialog.initOwner(primaryStage);
                        addPortfolio(username.getText().trim(),password.getText().trim());

                        try{
                            File file = new File("encryptedAccounts");
                            FileWriter fwriter = new FileWriter(file.getAbsoluteFile());
                            BufferedWriter bwriter = new BufferedWriter(fwriter);

                            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
                            SecretKey myDesKey = keygenerator.generateKey();

                            Cipher desCipher;
                            desCipher = Cipher.getInstance("DES");
                            for (Portfolio p : port) {
                                byte[] user = p.loginID.getBytes("UTF8");
                                byte[] pass = p.password.getBytes("UTF8");
                                desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
                                byte[] userEncrypted = desCipher.doFinal(user);
                                byte[] passEncrypted = desCipher.doFinal(pass);

                                String username = new String(userEncrypted);
                                String password = new String(passEncrypted);

                                bwriter.write(username);
                                bwriter.write(password);

                            }
                            bwriter.close();
                        }catch(Exception ex)
                        {
                            System.out.println("Exception");
                        }
                        if(rb1.isSelected()){
                            ArrayList<Portfolio> port2 = null;
                            try {
                                FileInputStream fileIn = new FileInputStream("myfile");
                                ObjectInputStream in = new ObjectInputStream(fileIn);
                                port2 = (ArrayList<Portfolio>) in.readObject();
                                in.close();
                                fileIn.close();
                            } catch (ClassNotFoundException e1) {
                                e1.printStackTrace();
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            final ArrayList<Portfolio> ports = port2;
                            Portfolio p = null;
                            for(Portfolio portfolio1 : ports){
                                if(portfolio1.loginID.equals(name)){
                                    p = portfolio1;
                                    break;
                                }
                            }

                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Open Resource File");
                            File file = fileChooser.showOpenDialog(stage);
                            if(file != null){
                                EquitiesView equitiesView = new EquitiesView();
                                CAView caView = new CAView();
                                transactionView transactionView = new transactionView();
                                WatchlistView watchlistView = new WatchlistView();

                                p.importP(file.toString(),p);
                                caView.saveCA(p);
                                equitiesView.saveEquities(p);
                                transactionView.saveHistory(p);
                                watchlistView.saveWatchlist(p);
                                p.saveMAs(p);
                            }
                            Scene scene = login(primaryStage);
                            stage.setScene(scene);
                        }
                        else{
                            Scene scene = login(primaryStage);
                            stage.setScene(scene);
                        }

                    }
                }
            }
        });

        Button cancel = new Button("Cancel");
        grid.add(cancel,1,5);
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Scene scene = login(primaryStage);
                stage.setScene(scene);
            }
        });

        Scene scene = new Scene(grid, 1200, 700);
        return scene;
    }


    //GUI for portfolio
    public Scene portfolio(final Stage primaryStage, Portfolio p){
        Text title = new Text("Successfully logged into " + p.loginID + "'s Portfolio");
        BorderPane border = borderPane(primaryStage, p, title);
        Scene scene = new Scene(border, 1200, 700);
        return scene;
    }
}


