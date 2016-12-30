package trunk.View;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import trunk.Control.*;
import trunk.Model.Portfolio;

/**
 * Created by Daniel on 3/25/2016.
 */
public class simulationView {
    //GUI for simulations
    public Scene simulation(final Stage primaryStage, Portfolio p, FPTS FPTS) {
        Text title = new Text("Simulation");
        BorderPane border = FPTS.borderPane(primaryStage,p,title);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 10, 5, 10));
        title.setFont(Font.font("Calibri", FontWeight.NORMAL, 20));
        grid.add(title,0,0,2,1);

        Label type = new Label("Simulation Type:");
        grid.add(type,0,1);
        ComboBox typeBox = new ComboBox();
        typeBox.getItems().addAll(
                "Bull Market",
                "Bear Market",
                "No Growth"
        );
        grid.add(typeBox,1,1);

        Label intervals = new Label("Interval:");
        grid.add(intervals,0,2);
        ComboBox intervalBox = new ComboBox();
        intervalBox.getItems().addAll(
                "Days",
                "Months",
                "Years"
        );
        grid.add(intervalBox,1,2);

        Label steps = new Label("Steps: ");
        grid.add(steps,0,3);
        TextField s = new TextField();
        grid.add(s,1,3);

        Label per = new Label("Percentage: ");
        grid.add(per,0,4);
        TextField pp = new TextField();
        grid.add(pp,1,4);

        Label sbs = new Label("Step By Step ");
        CheckBox cb = new CheckBox();
        grid.addRow(5, sbs, cb);

        Button submit = new Button("Submit");
        grid.add(submit,1,6);
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(typeBox.getSelectionModel().isEmpty() || intervalBox.getSelectionModel().isEmpty() || s.getText().isEmpty() || pp.getText().isEmpty()){
                    Text error = new Text("Fill in all fields");
                    error.setFill(Color.RED);
                    grid.add(error,0,8);
                }
                if(!FPTS.isInt(pp.getText()) || !FPTS.isDouble(s.getText())){
                    Text error = new Text("Correctly fill in all fields");
                    error.setFill(Color.RED);
                    grid.add(error,0,10);
                }
                else {
                    String type = typeBox.getSelectionModel().getSelectedItem().toString();
                    String interval = intervalBox.getSelectionModel().getSelectedItem().toString();
                    int time = 365;

                    final NumberAxis yAxis = new NumberAxis();
                    final NumberAxis xAxis = new NumberAxis();
                    final LineChart<Number,Number> lineChart =
                            new LineChart<Number,Number>(xAxis,yAxis);

                    lineChart.setTitle("Portfolio Value Simulation");

                    XYChart.Series series1 = new XYChart.Series();
                    series1.setName("Portfolio Value");


                    if(interval.equals("Days")){
                        time = 1;
                    }
                    else if(interval.equals("Months")){
                        time = 30;
                    }
                    else if(interval.equals("Years")){
                        time = 365;
                    }



                    if (type.equals("Bull Market")) {
                        BullMarket bullMarket = new BullMarket();
                        double[] val = bullMarket.simulate(p,Double.parseDouble(pp.getText().trim()),time,Integer.parseInt(s.getText().trim()));
                        series1.getData().add(new XYChart.Data(0, val[0]));

                        if (cb.isSelected()) {
                            Button next = new Button("> Next Step");
                            grid.add(next, 0, 7);
                            Button skip = new Button(">> Final Step");
                            grid.add(skip, 1, 7);
                            VBox data = new VBox();
                            lineChart.getData().add(series1);
                            border.setBottom(lineChart);

                            next.setOnAction(new EventHandler<ActionEvent>() {
                                int i = 1;
                                public void handle(ActionEvent e) {
                                    series1.getData().add(new XYChart.Data(i, val[i]));
                                    if (i + 1 < val.length)
                                        i++;
                                }
                            });

                            skip.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    for (int i = 1; i < val.length; i++) {
                                        series1.getData().add(new XYChart.Data(i, val[i]));
                                    }
                                }
                            });

                        }
                        else {
                            for (int i = 1; i < val.length; i++) {
                                series1.getData().add(new XYChart.Data(i, val[i]));
                            }
                        }

                    }

                    else if (type.equals("Bear Market")) {
                        BearMarket bearMarket = new BearMarket();
                        double[] val = bearMarket.simulate(p,Double.parseDouble(pp.getText().trim()),time,Integer.parseInt(s.getText().trim()));

                        series1.getData().add(new XYChart.Data(0, val[0]));

                        if (cb.isSelected()) {
                            Button next = new Button("Next Step");
                            grid.add(next, 0, 7);
                            Button skip = new Button(">> Final Step");
                            grid.add(skip, 1, 7);
                            VBox data = new VBox();
                            lineChart.getData().add(series1);
                            border.setBottom(lineChart);

                            next.setOnAction(new EventHandler<ActionEvent>() {
                                int i = 1;
                                public void handle(ActionEvent e) {
                                    series1.getData().add(new XYChart.Data(i, val[i]));
                                    if (i + 1 < val.length)
                                        i++;
                                }
                            });

                            skip.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    for (int i = 1; i < val.length; i++) {
                                        series1.getData().add(new XYChart.Data(i, val[i]));
                                    }
                                }
                            });

                        }
                        else {
                            for (int i = 1; i < val.length; i++) {
                                series1.getData().add(new XYChart.Data(i, val[i]));
                            }
                        }

                    }

                    else if (type.equals("No Growth")) {
                        NoGrowth noGrowth = new NoGrowth();
                        double[] val = noGrowth.simulate(p,Double.parseDouble(pp.getText().trim()),time,Integer.parseInt(s.getText().trim()));

                        series1.getData().add(new XYChart.Data(0, val[0]));

                        if (cb.isSelected()) {
                            Button next = new Button("Next Step");
                            grid.add(next, 0, 7);
                            Button skip = new Button(">> Final Step");
                            grid.add(skip, 1, 7);
                            VBox data = new VBox();
                            lineChart.getData().add(series1);
                            border.setBottom(lineChart);

                            next.setOnAction(new EventHandler<ActionEvent>() {
                                int i = 1;
                                public void handle(ActionEvent e) {
                                    series1.getData().add(new XYChart.Data(i, val[i]));
                                    if (i + 1 < val.length)
                                        i++;
                                }
                            });

                            skip.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    for (int i = 1; i < val.length; i++) {
                                        series1.getData().add(new XYChart.Data(i, val[i]));
                                    }
                                }
                            });

                        }
                        else {
                            for (int i = 1; i < val.length; i++) {
                                series1.getData().add(new XYChart.Data(i, val[i]));
                            }
                        }

                    }
                }
            }
        });

        border.setCenter(grid);
        Scene scene = new Scene(border, 1200, 700);
        return scene;
    }
}
