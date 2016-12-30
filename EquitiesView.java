package trunk.View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DateTimeStringConverter;
import trunk.Control.HistoryInvoker;
import trunk.Control.UpdateEQ;
import trunk.Model.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

/**
 * Created by Daniel on 3/25/2016.
 */
public class EquitiesView {
    /*
     * Code for saving equities
     */
    public void saveEquities(Portfolio p){
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
            iter.equityList1 = p.equityList1;
            iter.equityList2 = p.equityList2;
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
     * Code for filtering equities
     */
    public void filter(FilteredList<Equity> filteredData, FilteredList<Equity> filteredData2, TextField tickerFilter, TextField nameFilter, TextField maFilter, String newValue){
        filteredData.setPredicate(equity -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            boolean exists = false;
            if(equity.getMarketAverage().isEmpty() && maFilter.getText().equals("")){
                exists = true;
            }

            for(String s : equity.getMarketAverage()){
                if(s.toLowerCase().contains(maFilter.getText().toLowerCase())){
                    exists = true;
                }
            }
            if(equity.getTickerSymbol().toLowerCase().contains(tickerFilter.getText().toLowerCase())
                    && equity.getName().toLowerCase().contains(nameFilter.getText().toLowerCase())
                    && exists
                    ){
                return true;
            }
            else{
                return false;
            }
        });
        filteredData2.setPredicate(equity -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            boolean exists = false;
            if(equity.getMarketAverage().isEmpty() && maFilter.getText().equals("")){
                exists = true;
            }

            for(String s : equity.getMarketAverage()){
                if(s.toLowerCase().contains(maFilter.getText().toLowerCase())){
                    exists = true;
                }
            }
            if(equity.getTickerSymbol().toLowerCase().contains(tickerFilter.getText().toLowerCase())
                    && equity.getName().toLowerCase().contains(nameFilter.getText().toLowerCase())
                    && exists
                    ){
                return true;
            }
            else{
                return false;
            }
        });
    }

    /*
     * GUI for Equities page
     */
    public Scene ViewEquities(Stage primaryStage, Portfolio p, FPTS FPTS){
        Text title = new Text(p.loginID + "'s Equities");
        BorderPane border = FPTS.borderPane(primaryStage, p, title);
        VBox left = new VBox(5);

        ToolBar toolBar2 = new ToolBar();
        toolBar2.setOrientation(Orientation.VERTICAL);
        Label Search = new Label("Search Equities");
        left.setVgrow(Search, Priority.ALWAYS);
        Search.setMaxHeight(Double.MAX_VALUE);
        left.getChildren().addAll(toolBar2, Search);
        left.setSpacing(10);

        final Label ownedE = new Label(p.loginID + "'s Equities");
        ownedE.setFont(new Font("Arial", 20));
        final Label availableE = new Label("Available Equities");
        availableE.setFont(new Font("Arial", 20));
        TableView tableOwnedE = new TableView();
        tableOwnedE.setEditable(true);

        TableColumn symbols = new TableColumn("Ticker Symbol");
        TableColumn names = new TableColumn("Names");
        TableColumn shares = new TableColumn("Shares");
        TableColumn salePrice = new TableColumn("Sale Price");
        TableColumn acquisitionDate = new TableColumn("Acquisition Date");
        TableColumn MarketAverage = new TableColumn("Market Average");
        TableColumn totalValue = new TableColumn("Total Value");

        TableColumn symbols2 = new TableColumn("Ticker Symbol");
        TableColumn names2 = new TableColumn("Names");
        TableColumn salePrice2 = new TableColumn("Sale Price");
        TableColumn MarketAverage2 = new TableColumn("Market Average");

        Label spacer = new Label(" ");

        TableView tableAvailableE = new TableView();
        tableAvailableE.setEditable(true);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(ownedE, tableOwnedE, availableE, tableAvailableE);
        border.setLeft(left);
        final ObservableList<Equity> equities1 = FXCollections.observableArrayList(p.equityList1);
        final ObservableList<Equity> equities2 = FXCollections.observableArrayList(p.equityList2);

        symbols.setCellValueFactory(
                new PropertyValueFactory<Equity,String>("tickerSymbol")
        );
        names.setCellValueFactory(
                new PropertyValueFactory<Equity,String>("name")
        );
        shares.setCellValueFactory(
                new PropertyValueFactory<Equity,Integer>("shares")
        );
        salePrice.setCellValueFactory(
                new PropertyValueFactory<Equity,Double>("acquisitionPrice")
        );
        acquisitionDate.setCellValueFactory(
               new PropertyValueFactory<Equity,Date>("acquisitionDate")
        );
        MarketAverage.setCellValueFactory(
                new PropertyValueFactory<Equity,String>("MA")
        );
        totalValue.setCellValueFactory(
                new PropertyValueFactory<Equity, Double>("total")
        );


        symbols2.setCellValueFactory(
                new PropertyValueFactory<Equity,String>("tickerSymbol")
        );
        names2.setCellValueFactory(
                new PropertyValueFactory<Equity,String>("name")
        );
        salePrice2.setCellValueFactory(
                new PropertyValueFactory<Equity,Double>("acquisitionPrice")
        );
        MarketAverage2.setCellValueFactory(
                new PropertyValueFactory<Equity,String>("MA")
        );


        tableOwnedE.setItems(equities1);
        tableOwnedE.getColumns().addAll(symbols, names, shares, salePrice, acquisitionDate, MarketAverage, totalValue);
        symbols.prefWidthProperty().bind(tableOwnedE.widthProperty().multiply(0.1));
        names.prefWidthProperty().bind(tableOwnedE.widthProperty().multiply(0.35));
        shares.prefWidthProperty().bind(tableOwnedE.widthProperty().multiply(0.05));
        salePrice.prefWidthProperty().bind(tableOwnedE.widthProperty().multiply(0.075));
        acquisitionDate.prefWidthProperty().bind(tableOwnedE.widthProperty().multiply(0.1));
        MarketAverage.prefWidthProperty().bind(tableOwnedE.widthProperty().multiply(0.15));
        totalValue.prefWidthProperty().bind(tableOwnedE.widthProperty().multiply(0.125));

        tableAvailableE.setItems(equities2);
        tableAvailableE.getColumns().addAll(symbols2, names2, salePrice2, MarketAverage2);
        symbols2.prefWidthProperty().bind(tableAvailableE.widthProperty().multiply(0.1));
        names2.prefWidthProperty().bind(tableAvailableE.widthProperty().multiply(0.35));
        salePrice2.prefWidthProperty().bind(tableAvailableE.widthProperty().multiply(0.075));
        MarketAverage2.prefWidthProperty().bind(tableAvailableE.widthProperty().multiply(0.125));

        TableView mas = new TableView();
        final ObservableList<Equity> market = FXCollections.observableArrayList(p.marketAverages);

        TableColumn salePrice3 = new TableColumn("Sale Price");
        TableColumn MarketAverage3 = new TableColumn("Market Average");
        TableColumn shares3 = new TableColumn("Shares");

        salePrice3.setCellValueFactory(
                new PropertyValueFactory<Equity,Double>("acquisitionPrice")
        );
        MarketAverage3.setCellValueFactory(
                new PropertyValueFactory<Equity,String>("MA")
        );
        shares3.setCellValueFactory(
                new PropertyValueFactory<Equity, Double>("shares")
        );

        mas.setItems(market);
        mas.getColumns().addAll(MarketAverage3,salePrice3,shares3);

        Button add = new Button("Add Equity");
        Button remove = new Button("Remove Equity");
        Button importEq = new Button("Import Equity");
        Button exportEq = new Button("Export Equity");


        VBox.setVgrow(Search, Priority.ALWAYS);
        Search.setMaxHeight(Double.MAX_VALUE);

        Button addShares = new Button("Add Shares");
        Button sellShares = new Button("Sell Shares");
        Button addSharestoMA = new Button("Add Shares to MA");

        Button goback = new Button("View Portfolio");

        //Action for importing equities
        importEq.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                File file = fileChooser.showOpenDialog(FPTS.stage);
                if(file != null){
                    Equity e = new Equity("","",0,0,"",null);
                    e.importEQ(file.toString(),p);
                    UndoAction add = new UndoAction("Import", "Equity", null,null,0,null,file.toString());
                    add.redo.clear();
                    add.addToQueue(add);
                    try{
                        UpdateEQ updateEQ = new UpdateEQ();
                        updateEQ.updateEquities(p, p.equityList2);
                    }catch(Exception ex){
                        ex.getMessage();
                    }
                    Scene scene = ViewEquities(primaryStage,p, FPTS);
                    FPTS.stage.setScene(scene);
                }
            }
        });

        //Action for exporting equities
        exportEq.setOnAction(new EventHandler<ActionEvent>() {
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
                        Equity e = new Equity("","",0,0,"",null);
                        e.exportEQ(file.getText(),p);
                        dialog.close();
                    }
                });
            }
        });

        //Action for going back to portfolio
        goback.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Scene scene = FPTS.portfolio(primaryStage,p);
                FPTS.stage.setScene(scene);
            }
        });

        //Action for adding equities
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

                VBox dialogVbox = new VBox(20);
                dialogVbox.getChildren().add(new Text("This is a Dialog"));
                TextField tickerSymbol = new TextField();
                grid2.add(new Label("Ticker Symbol: "),0,1);
                grid2.add(tickerSymbol,1,1);
                TextField names = new TextField();
                grid2.add(new Label("Names: "),0,2);
                grid2.add(names,1,2);

                TextField numShares = new TextField();
                grid2.add(new Label("Number of Shares: "),0,3);
                grid2.add(numShares,1,3);
                TextField salePrice = new TextField();
                grid2.add(new Label("Sale Price: "),0,4);
                grid2.add(salePrice,1,4);
                TextField acquisitionDate = new TextField();
                grid2.add(new Label("Aquisition Date (MM/dd/YYYY): "),0,5);
                grid2.add(acquisitionDate,1,5);
                VBox SIlist = new VBox(2);
                HBox buttonList = new HBox(30);
                GridPane sectorIndex = new GridPane();
                Button addSectorIndex = new Button("add");
                Button removeSectorIndex = new Button("remove");
                buttonList.getChildren().addAll(addSectorIndex, removeSectorIndex);
                sectorIndex.add(buttonList,0,0);
                sectorIndex.add(SIlist,0,1);
                addSectorIndex.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event)  {
                        TextField newField = new TextField();
                        SIlist.getChildren().add(newField);}
                });
                removeSectorIndex.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event)  {
                        SIlist.getChildren().remove(0,1);}
                });
                grid2.add(new Label("Sector/Index: "),0,6);
                grid2.add(sectorIndex,1,6);

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
                final Text error = new Text();
                grid2.add(error, 1, 8);
                submit.setOnAction(new EventHandler<ActionEvent>() { // this one
                    @Override
                    public void handle(ActionEvent event) {

                        if(tickerSymbol.getText().isEmpty() || numShares.getText().isEmpty() || acquisitionDate.getText().isEmpty() ||
                                salePrice.getText().isEmpty() || sectorIndex.getChildren().isEmpty()){
                            error.setFill(Color.FIREBRICK);
                            error.setText("Fill in all fields");
                        }
                        else if(!FPTS.isDouble(salePrice.getText()) || !FPTS.isInt(numShares.getText()) || !FPTS.isDate(acquisitionDate.getText()))
                        {
                            error.setFill(Color.FIREBRICK);
                            error.setText("Enter information in correct format");
                        }
                        else if(Integer.parseInt(numShares.getText()) < 1){
                            error.setFill(Color.FIREBRICK);
                            error.setText("Enter valid number of shares");
                        }
                        else{
                            ArrayList SIarray = new ArrayList();
                            SIlist.getChildren().forEach((SI) -> {
                                SIarray.add(((TextField)SI).getText());
                            });
                            Equity e = new Equity(tickerSymbol.getText(),names.getText(),Integer.parseInt(numShares.getText()),Double.parseDouble(salePrice.getText()),
                                    acquisitionDate.getText(),SIarray);
                            e.addEquity(tickerSymbol.getText(), names.getText(), Integer.parseInt(numShares.getText()),
                                    Double.parseDouble(salePrice.getText()), acquisitionDate.getText() , SIarray,false,p);


                            UndoAction add = new UndoAction("Add", "Equity",null,null,0,e,"");
                            add.redo.clear();
                            add.addToQueue(add);


                            dialog.close();
                            Scene scene = ViewEquities(primaryStage,p, FPTS);
                            FPTS.stage.setScene(scene);

                        }

                    }

                });
            }
        });

        //Action for removing equities
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                ArrayList<String> cashAccts = new ArrayList<>();
                for(Equity e : p.equityList1){
                    if(!e.tickerSymbol.equals("")) {
                        choices.add(e.tickerSymbol);
                    }
                }
                for(CashAccount c : p.cashAccList){
                    cashAccts.add(c.name);
                }

                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the equity you wish to remove:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    ChoiceDialog<String> addCash = new ChoiceDialog<>("None", cashAccts);
                    addCash.setContentText("Choose cash account to add equity value to:");
                    Optional<String> cashAcct = addCash.showAndWait();
                    Equity e = new Equity("","",0,0,"",null);
                    for (Equity n : p.equityList1) {
                        if (n.getTickerSymbol().equals(result.get())) {
                            e = n;
                        }
                    }


                    UndoAction add = new UndoAction("Remove", "Equity",null,null,0,e,"");
                    add.redo.clear();
                    add.addToQueue(add);
                    e.removeEquity(result.get(), cashAcct.get(),p);
                }

                Scene scene = ViewEquities(primaryStage,p, FPTS);
                FPTS.stage.setScene(scene);

            }
        });


        toolBar2.getItems().addAll(spacer, add, remove, importEq, exportEq, addShares, sellShares,addSharestoMA,mas);

        addShares.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                for(Equity e : p.equityList2){
                    if(!e.tickerSymbol.equals("")) {
                        choices.add(e.tickerSymbol);
                    } else{
                        choices.add(e.marketAverage.get(0));
                    }
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the equity you wish to add shares to:");



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

                    grid2.add(new Label("Add Shares to " + result.get() + ": "),0,1);
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
                                error.setText("Enter valid number of shares");
                            }
                            else{
                                int numShares = Integer.parseInt(shares.getText());
                                double tot = 0;
                                for(Equity stuff : p.equityList2){
                                    if (stuff.getTickerSymbol().equals(result.get())) {
                                        tot = numShares * stuff.acquisitionPrice;
                                        ArrayList<String> cashAccts = new ArrayList<>();
                                        for(CashAccount c : p.cashAccList){
                                            cashAccts.add(c.name);
                                        }
                                        ChoiceDialog<String> addCash = new ChoiceDialog<>("None", cashAccts);
                                        addCash.setContentText("Choose cash account to pay for shares:");
                                        Optional<String> cashAcct = addCash.showAndWait();
                                        CashAccount c = new CashAccount("","",0,"",null);

                                        if(cashAcct.isPresent()) {
                                            for(CashAccount acct : p.cashAccList) {
                                                if(acct.name.equals(cashAcct.get())) {
                                                    if(acct.balance >= tot) {
                                                        c.updateBalance(cashAcct.get(), tot, false, p);
                                                        Equity e = new Equity("","",0,0,"",null);
                                                        DateTimeStringConverter format = new DateTimeStringConverter("MM/dd/YYYY");
                                                        Date date = new Date();
                                                        stuff.acquisitionDate = format.toString(date);
                                                        e.addEquity(stuff.tickerSymbol,stuff.name,numShares,stuff.acquisitionPrice,
                                                                stuff.acquisitionDate,stuff.getMarketAverage(),false,p);
                                                        addCash.close();
                                                        dialog2.close();
                                                        Scene scene = ViewEquities(primaryStage,p, FPTS);
                                                        FPTS.stage.setScene(scene);
                                                    } else{
                                                        error.setFill(Color.FIREBRICK);
                                                        error.setText("Account contains too little funds.");
                                                    }
                                                }
                                            }

                                        }
                                        else{
                                            error.setFill(Color.FIREBRICK);
                                            error.setText("Select a cash account");
                                        }
                                        addCash.close();
                                        dialog2.close();
                                        Scene scene = ViewEquities(primaryStage,p, FPTS);
                                        FPTS.stage.setScene(scene);

                                    }
                                    else if(stuff.tickerSymbol.equals("")
                                            && stuff.marketAverage.get(0).equals(result.get())){
                                        ArrayList<String> cashAccts = new ArrayList<>();
                                        for(CashAccount c : p.cashAccList){
                                            cashAccts.add(c.name);
                                        }
                                        ChoiceDialog<String> addCash = new ChoiceDialog<>("None", cashAccts);
                                        addCash.setContentText("Choose cash account to pay for shares:");
                                        Optional<String> cashAcct = addCash.showAndWait();
                                        CashAccount c = new CashAccount("","",0,"",null);
                                        tot = numShares * stuff.acquisitionPrice;
                                        if(cashAcct.isPresent()) {
                                            for(CashAccount acct : p.cashAccList) {
                                                if(acct.name.equals(cashAcct.get())) {
                                                    if(acct.balance >= tot) {
                                                        c.updateBalance(cashAcct.get(), tot, false, p);
                                                        Equity e = new Equity("","",0,0,"",null);
                                                        e.addEquity(stuff.tickerSymbol,stuff.name,numShares,stuff.acquisitionPrice,
                                                                stuff.acquisitionDate,stuff.getMarketAverage(),false,p);
                                                        addCash.close();
                                                        dialog2.close();
                                                        Scene scene = ViewEquities(primaryStage,p, FPTS);
                                                        FPTS.stage.setScene(scene);
                                                    } else{
                                                        error.setFill(Color.FIREBRICK);
                                                        error.setText("Account contains too little funds.");
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            error.setFill(Color.FIREBRICK);
                                            error.setText("Select a cash account");
                                        }
                                        addCash.close();
                                        dialog2.close();
                                        Scene scene = ViewEquities(primaryStage,p, FPTS);
                                        FPTS.stage.setScene(scene);
                                    }
                                }


                                dialog2.close();
                                Scene scene = ViewEquities(primaryStage,p, FPTS);
                                FPTS.stage.setScene(scene);
                            }
                        }
                    });

                    dialog2.setScene(dialogScene);
                    dialog2.show();
                }
            }
        });
        sellShares.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                for(Equity e : p.equityList1){
                    if(!e.tickerSymbol.equals("")) {
                        choices.add(e.tickerSymbol);
                    } else{
                        choices.add(e.marketAverage.get(0));
                    }
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the equity you wish to sell shares from:");


                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    int num1 = 0;
                    for (Equity stuff : p.equityList1) {
                        if (stuff.getTickerSymbol().equals(result.get())) {
                            num1 = stuff.shares;
                        }
                        else if(stuff.tickerSymbol.equals("")
                                && stuff.marketAverage.get(0).equals(result.get())){
                            num1 = stuff.shares;
                        }
                    }
                    final int num = num1;
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

                    grid2.add(new Label("Number of Shares to Sell: "), 0, 1);
                    grid2.add(shares, 1, 1);
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
                            if (shares.getText().isEmpty()) {
                                error.setFill(Color.FIREBRICK);
                                error.setText("Fill in all fields");
                            } else if (!FPTS.isInt(shares.getText())) {
                                error.setFill(Color.FIREBRICK);
                                error.setText("Enter information in correct format");
                            }
                            int numShares = Integer.parseInt(shares.getText());
                            if (numShares > num || numShares < 0){
                                error.setFill(Color.FIREBRICK);
                                error.setText("Invalid value");
                            }
                            else {
                                Equity e = null;
                                double tot = 0;
                                double amount = 0;
                                for (Equity stuff : p.equityList1) {
                                    if (stuff.getTickerSymbol().equals(result.get())) {
                                        tot = numShares * stuff.acquisitionPrice;
                                        stuff.shares -= numShares;
                                        amount = -(numShares * stuff.acquisitionPrice);
                                        e = stuff;
                                    }
                                    else if(stuff.tickerSymbol.equals("")
                                            && stuff.marketAverage.get(0).equals(result.get())){
                                        tot = numShares * stuff.acquisitionPrice;
                                        stuff.shares -= numShares;
                                        amount = -(numShares * stuff.acquisitionPrice);
                                        e = stuff;
                                    }
                                }

                                ArrayList<String> cashAccts = new ArrayList<>();
                                for(CashAccount c : p.cashAccList){
                                    cashAccts.add(c.name);
                                }
                                ChoiceDialog<String> addCash = new ChoiceDialog<>("None", cashAccts);
                                addCash.setContentText("Choose cash account to add shares to:");
                                Optional<String> cashAcct = addCash.showAndWait();
                                CashAccount c = new CashAccount("","",0,"",null);
                                if(!cashAcct.equals("None")) {
                                    for(CashAccount acct : p.cashAccList) {
                                        if(acct.name.equals(cashAcct.get())) {
                                            if(acct.balance >= tot) {
                                                c.updateBalance(cashAcct.get(), tot, true, p);

                                                dialog2.close();
                                                Scene scene = ViewEquities(primaryStage,p, FPTS);
                                                FPTS.stage.setScene(scene);
                                            } else{
                                                error.setFill(Color.FIREBRICK);
                                                error.setText("Account contains too little funds.");
                                            }
                                        }
                                    }
                                }

                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();

                                HistoryInvoker test = new HistoryInvoker();
                                test.setLog(new LogEquityHistory(), p, new History("Sell Shares", "Equity", e,dateFormat.format(date),amount,-numShares));
                                test.logAction();

                                dialog2.close();
                                Scene scene = ViewEquities(primaryStage, p, FPTS);
                                FPTS.stage.setScene(scene);

                            }

                        }

                    });

                    dialog2.setScene(dialogScene);
                    dialog2.show();
                }
            }
        });
        addSharestoMA.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<String> choices = new ArrayList<>();
                for(Equity e : p.marketAverages){
                    if(!e.tickerSymbol.equals("")) {
                        choices.add(e.tickerSymbol);
                    } else{
                        choices.add(e.marketAverage.get(0));
                    }
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
                dialog.setContentText("Choose the market average you wish to add shares to:");

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

                    grid2.add(new Label("Add Shares to " + result.get() + ": "),0,1);
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
                                error.setText("Enter valid number of shares");
                            }
                            else{
                                int numShares = Integer.parseInt(shares.getText());
                                for(Equity e : p.marketAverages){
                                    if(e.marketAverage.contains(result.get())){
                                        e.shares += numShares;
                                    }
                                }

                                dialog2.close();
                                Scene scene = ViewEquities(primaryStage,p, FPTS);
                                FPTS.stage.setScene(scene);
                            }
                        }
                    });

                    dialog2.setScene(dialogScene);
                    dialog2.show();
                }
            }
        });

        TextField tickerFilter = new TextField();
        tickerFilter.setPromptText("Enter ticker symbol");

        TextField nameFilter = new TextField();
        nameFilter.setPromptText("Enter name");

        TextField maFilter = new TextField();
        maFilter.setPromptText("Enter market average");

        FilteredList<Equity> filteredData = new FilteredList<>(equities1);
        FilteredList<Equity> filteredData2 = new FilteredList<>(equities2);

        tickerFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filter(filteredData,filteredData2,tickerFilter,nameFilter,maFilter,newValue);
        });

        nameFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filter(filteredData,filteredData2,tickerFilter,nameFilter,maFilter,newValue);
        });

        maFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filter(filteredData,filteredData2,tickerFilter,nameFilter,maFilter,newValue);
        });


        // Wrap the FilteredList in a SortedList.
        FilteredList<Equity> sortedData = new FilteredList<Equity>(filteredData);
        FilteredList<Equity> sortedData2 = new FilteredList<Equity>(filteredData2);


        // Add sorted (and filtered) data to the table.
        tableOwnedE.setItems(sortedData);
        tableAvailableE.setItems(sortedData2);

        VBox bottom = new VBox();
        HBox searchBoxes = new HBox();
        searchBoxes.getChildren().addAll(tickerFilter,nameFilter,maFilter);

        bottom.getChildren().addAll(Search,searchBoxes);
        border.setCenter(vbox);
        border.setBottom(bottom);
        Scene scene = new Scene(border, 1200, 750);
        return scene;
    }
}
