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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import trunk.Model.Equity;
import trunk.Model.Portfolio;
import trunk.Model.Watchlist;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Jeff Kotowicz on 4/5/2016.
 *
 * Provides a watchlist for equities and Market Averages
 */
public class WatchlistView {
    /*
     * Code for saving watchlist
     */
    public void saveWatchlist(Portfolio p){
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
            iter.watchlists = p.watchlists;
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
     * GUI for Watchlist page
     */
    public Scene ViewWatchlist(Stage primaryStage, Portfolio p, FPTS FPTS){
        Text title = new Text(p.loginID + "'s Watchlist");
        BorderPane border = FPTS.borderPane(primaryStage, p, title);
        VBox left = new VBox(5);

        ToolBar toolBar2 = new ToolBar();
        toolBar2.setOrientation(Orientation.VERTICAL);
        left.getChildren().addAll(toolBar2);
        left.setSpacing(10);

        final Label watchItems = new Label(p.loginID + "'s Watchlist");
        watchItems.setFont(new Font("Arial", 20));
        TableView watchList = new TableView();
        watchList.setEditable(true);
        final ObservableList<Watchlist> watch = FXCollections.observableArrayList(p.watchlists);

        TableColumn symbols = new TableColumn("Ticker Symbol");
        TableColumn lowtrigger = new TableColumn("Low Trigger");
        TableColumn hightrigger = new TableColumn("High Trigger");
        TableColumn price = new TableColumn("Per Share Price");
        TableColumn compare = new TableColumn("Compare");
        TableColumn lowtriggered = new TableColumn("Low Trigger");
        TableColumn hightriggered = new TableColumn("High Trigger");


        Label spacer = new Label(" ");


        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(watchItems, watchList);
        border.setLeft(left);

        symbols.setCellValueFactory(
                new PropertyValueFactory<Watchlist,String>("tickerSymbol")
        );
        lowtrigger.setCellValueFactory(
                new PropertyValueFactory<Watchlist,Double>("lowtrigger")
        );
        hightrigger.setCellValueFactory(
                new PropertyValueFactory<Watchlist,Double>("hightrigger")
        );
        price.setCellValueFactory(
                new PropertyValueFactory<Watchlist, Double>("pershareprice")
        );
        compare.setCellValueFactory(
                new PropertyValueFactory<Watchlist, String>("compare")
        );
        lowtriggered.setCellValueFactory(
                new PropertyValueFactory<Watchlist, String>("lowtriggered")
        );
        hightriggered.setCellValueFactory(
                new PropertyValueFactory<Watchlist, String>("hightriggered")
        );



        //watchList.setItems(equities1);
        watchList.getColumns().addAll(symbols, hightrigger, lowtrigger,price,compare, hightriggered, lowtriggered);
        watchList.setItems(watch);


        symbols.prefWidthProperty().bind(watchList.widthProperty().multiply(0.1));
        lowtrigger.prefWidthProperty().bind(watchList.widthProperty().multiply(0.075));
        hightrigger.prefWidthProperty().bind(watchList.widthProperty().multiply(0.15));


        Button add = new Button("Add Item");
        Button remove = new Button("Remove Item");
        Button high = new Button("Add High Trigger");
        Button low = new Button("Add Low Trigger");
        Button reset = new Button("Reset");


        //Action for adding equities
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                double price1 = 0;
                for(Equity e : p.equityList2){
                    if(!e.tickerSymbol.equals("")) {
                        choices.add(e.tickerSymbol);
                    } else{
                        choices.add(e.marketAverage.get(0));
                    }
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the equity you wish to add to the watchlist:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    for(Equity e : p.equityList2){
                        if(e.tickerSymbol.equals(result.get())) {
                            price1 = e.acquisitionPrice;
                        }
                    }
                    final double price = price1;
                    final Stage dialog2 = new Stage();
                    dialog2.initOwner(primaryStage);
                    GridPane grid2 = new GridPane();
                    grid2.setAlignment(Pos.CENTER);
                    grid2.setHgap(10);
                    grid2.setVgap(10);
                    grid2.setPadding(new Insets(5, 10, 5, 10));

                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("This is a Dialog"));
                    TextField low = new TextField();
                    TextField high = new TextField();

                    grid2.add(new Label("Low Trigger: "), 0, 1);
                    grid2.add(low, 1, 1);
                    grid2.add(new Label("High Trigger: "), 0, 2);
                    grid2.add(high, 1, 2);
                    Scene dialogScene = new Scene(grid2, 400, 400);
                    Button cancel = new Button("Cancel");
                    grid2.add(cancel, 1, 3);
                    Button submit = new Button("Submit");
                    grid2.add(submit, 0, 3);
                    cancel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            dialog2.close();
                        }
                    });
                    final Text error = new Text();
                    grid2.add(error, 1, 8);
                    submit.setOnAction(new EventHandler<ActionEvent>() { // this one
                        @Override
                        public void handle(ActionEvent event) {
                            Watchlist e = new Watchlist(result.get(), low.getText(), high.getText(), price, "","","");
                            p.watchlists.add(e);
                            e.visit(p);


                            dialog2.close();
                            Scene scene = ViewWatchlist(primaryStage, p, FPTS);
                            FPTS.stage.setScene(scene);
                        }
                    });

                    dialog2.setScene(dialogScene);
                    dialog2.show();
                }
            }
        });

        //Action for removing equities
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                for(Watchlist e : p.watchlists){
                    choices.add(e.tickerSymbol);
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the watchlist item you wish to remove:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    Watchlist w = null;
                    for(Watchlist e : p.watchlists){
                        if(e.tickerSymbol.equals(result.get())){
                            w = e;
                        }
                    }
                    p.watchlists.remove(w);

                    Scene scene = ViewWatchlist(primaryStage, p, FPTS);
                    FPTS.stage.setScene(scene);
                }
            }
        });

        high.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                for(Watchlist e : p.watchlists){
                    choices.add(e.tickerSymbol);
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the watchlist item you wish to add a high trigger to:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String lower = "";
                    for(Watchlist e : p.watchlists){
                        if(e.tickerSymbol.equals(result.get())){
                            lower = e.lowtrigger;
                        }
                    }
                    final String truelow = lower;

                    final Stage dialog2 = new Stage();
                    dialog2.initOwner(primaryStage);
                    GridPane grid2 = new GridPane();
                    grid2.setAlignment(Pos.CENTER);
                    grid2.setHgap(10);
                    grid2.setVgap(10);
                    grid2.setPadding(new Insets(5, 10, 5, 10));

                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("This is a Dialog"));
                    TextField high = new TextField();

                    grid2.add(new Label("High Trigger: "), 0, 2);
                    grid2.add(high, 1, 2);
                    Scene dialogScene = new Scene(grid2, 400, 400);
                    Button cancel = new Button("Cancel");
                    grid2.add(cancel, 1, 3);
                    Button submit = new Button("Submit");
                    grid2.add(submit, 0, 3);
                    cancel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            dialog2.close();
                        }
                    });
                    final Text error = new Text();
                    grid2.add(error, 1, 8);
                    submit.setOnAction(new EventHandler<ActionEvent>() { // this one
                        @Override
                        public void handle(ActionEvent event) {
                            if (high.getText().isEmpty()) {
                                error.setFill(Color.FIREBRICK);
                                error.setText("Fill in all fields");
                            } else if (!FPTS.isDouble(high.getText())) {
                                error.setFill(Color.FIREBRICK);
                                error.setText("Enter information in correct format");
                            }
                            else {
                                for (Watchlist e : p.watchlists) {
                                    if(!truelow.equals("")) {
                                        if (Double.parseDouble(high.getText()) < Double.parseDouble(truelow)) {
                                            error.setFill(Color.FIREBRICK);
                                            error.setText("High trigger can't be lower than low trigger");
                                            break;
                                        }
                                    }
                                    if (e.tickerSymbol.equals(result.get())) {
                                        e.hightrigger = high.getText().trim();
                                        e.visit(p);

                                        dialog2.close();
                                        Scene scene = ViewWatchlist(primaryStage, p, FPTS);
                                        FPTS.stage.setScene(scene);
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

        low.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                for(Watchlist e : p.watchlists){
                    choices.add(e.tickerSymbol);
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the watchlist item you wish to add a low trigger to:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String higher = "";
                    for(Watchlist e : p.watchlists){
                        if(e.tickerSymbol.equals(result.get())){
                            higher = e.hightrigger;
                        }
                    }
                    final String truehigh = higher;

                    final Stage dialog2 = new Stage();
                    dialog2.initOwner(primaryStage);
                    GridPane grid2 = new GridPane();
                    grid2.setAlignment(Pos.CENTER);
                    grid2.setHgap(10);
                    grid2.setVgap(10);
                    grid2.setPadding(new Insets(5, 10, 5, 10));

                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("This is a Dialog"));
                    TextField low = new TextField();
                    grid2.add(new Label("Low Trigger: "), 0, 1);
                    grid2.add(low, 1, 1);
                    Scene dialogScene = new Scene(grid2, 400, 400);
                    Button cancel = new Button("Cancel");
                    grid2.add(cancel, 1, 3);
                    Button submit = new Button("Submit");
                    grid2.add(submit, 0, 3);
                    cancel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            dialog2.close();
                        }
                    });
                    final Text error = new Text();
                    grid2.add(error, 1, 8);
                    submit.setOnAction(new EventHandler<ActionEvent>() { // this one
                        @Override
                        public void handle(ActionEvent event) {
                            if (low.getText().isEmpty()) {
                                error.setFill(Color.FIREBRICK);
                                error.setText("Fill in all fields");
                            } else if (!FPTS.isDouble(low.getText())) {
                                error.setFill(Color.FIREBRICK);
                                error.setText("Enter information in correct format");
                            }
                            else {
                                for (Watchlist e : p.watchlists) {
                                    if (truehigh != "") {
                                        if (Double.parseDouble(truehigh) < Double.parseDouble(low.getText())) {
                                            error.setFill(Color.FIREBRICK);
                                            error.setText("Low trigger can't be higher than high trigger");
                                            break;
                                        }
                                    }
                                    if (e.tickerSymbol.equals(result.get())) {
                                        e.lowtrigger = low.getText().trim();

                                        e.visit(p);

                                        dialog2.close();
                                        Scene scene = ViewWatchlist(primaryStage, p, FPTS);
                                        FPTS.stage.setScene(scene);
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
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                for(Watchlist e : p.watchlists){
                    choices.add(e.tickerSymbol);
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the watchlist item you wish to reset:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    for(Watchlist w : p.watchlists){
                        if(w.tickerSymbol.equals(result.get())) {
                            w.lowtriggered = "";
                            w.hightriggered = "";
                        }
                    }
                    Scene scene = ViewWatchlist(primaryStage, p, FPTS);
                    FPTS.stage.setScene(scene);
                }
            }
        });


        toolBar2.getItems().addAll(spacer, add, remove, high, low, reset);


        border.setCenter(vbox);
        Scene scene = new Scene(border, 1200, 700);
        return scene;
    }
}

