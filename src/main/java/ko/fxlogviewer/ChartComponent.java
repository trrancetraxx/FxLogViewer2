package ko.fxlogviewer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class ChartComponent extends Pane implements Initializable {

    private AnchorPane view;

    @FXML
    private LineChart<String, Number> lineCharts;

    @FXML
    private ChoiceBox<String> firstChoiceBox;

    @FXML
    private ChoiceBox<String> secondChoiceBox;
    @FXML
    private ChoiceBox<String> thirdChoiceBox;

    @FXML
    private Button closeGraphButton;

    ArrayList<String> columns;
    ArrayList<String[]> data;
    ChartComponent component;

    final XYChart.Series<String, Number> series1 = new XYChart.Series<>();
    XYChart.Series<String, Number> series2 = new XYChart.Series<>();
    XYChart.Series<String, Number> series3 = new XYChart.Series<>();

    GuiController xSingleton = GuiController.getSingleton();

    public ChartComponent(ArrayList<String> _columns, ArrayList<String[]> _data) {
        this.columns = _columns;
        this.data = _data;

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ChartComponent.fxml"));
        try {
            fxmlLoader.setController(this);
            view = (AnchorPane) fxmlLoader.load();
            view.prefWidthProperty().bind(this.widthProperty());
            view.prefHeightProperty().bind(this.heightProperty());
            firstChoiceBox.getSelectionModel().select(1);
        } catch (IOException ex) {

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Application error");
            alert.setContentText("Error: " + ex.getMessage());

        }
        getChildren().add(view);
    }

    @FXML
    public void updateFirstChoiceBox(@SuppressWarnings("unused") ActionEvent event) {

        int index = columns.indexOf(firstChoiceBox.getValue());
        lineCharts.getData().remove(series1);
        if (index < 1)
            return;

        series1.getData().clear();
        series1.setName(firstChoiceBox.getValue());

        ArrayList<Data<String, Number>> ex = new ArrayList<>();

        for (String[] datum : data) ex.add(new Data<>(datum[0], Float.valueOf(datum[index])));
        series1.getData().setAll(ex);
        lineCharts.getData().add(series1);
    }

    @FXML
    public void updateSecondChoiceBox(@SuppressWarnings("unused") ActionEvent event) {
        int index = columns.indexOf(secondChoiceBox.getValue());
        lineCharts.getData().remove(series2);
        if (index < 1)
            return;
        series2 = new XYChart.Series<>();
        series2.setName(secondChoiceBox.getValue());
        for (String[] datum : data)
            series2.getData().add(new Data<>(String.valueOf(datum[0]),
                    Double.valueOf(datum[index])));
        lineCharts.getData().add(series2);

    }


    @FXML
    public void updateThirdChoiceBox(@SuppressWarnings("unused") ActionEvent event) {
        int index = columns.indexOf(thirdChoiceBox.getValue());

        lineCharts.getData().remove(series3);
        if (index < 1)
            return;
        series3 = new XYChart.Series<>();
        series3.setName(thirdChoiceBox.getValue());
        for (String[] datum : data)
            series3.getData().add(new Data<>(String.valueOf(datum[0]),
                    Double.valueOf(datum[index])));
        lineCharts.getData().add(series3);
    }

    @FXML
    private void closeGraph(@SuppressWarnings("unused") ActionEvent event) {
        GuiController.getSingleton().removeChart(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lineCharts.setCreateSymbols(false);
        this.firstChoiceBox.setItems(FXCollections.observableArrayList(columns));
        this.secondChoiceBox.setItems(FXCollections.observableArrayList(columns));
        this.thirdChoiceBox.setItems(FXCollections.observableArrayList(columns));

        firstChoiceBox.setOnAction(this::updateFirstChoiceBox);
        secondChoiceBox.setOnAction(this::updateSecondChoiceBox);
        thirdChoiceBox.setOnAction(this::updateThirdChoiceBox);
        closeGraphButton.setOnAction(this::closeGraph);

        Tooltip mousePositionToolTip = new Tooltip("");
        lineCharts.setOnMouseMoved((EventHandler<? super MouseEvent>) event -> {

            try {

                Node node = (Node) event.getSource();
                Axis<String> xAxis = lineCharts.getXAxis();
                Axis<Number> yAxis = lineCharts.getYAxis();

                Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                double xx = xAxis.sceneToLocal(mouseSceneCoords).getX();
                double yy = yAxis.sceneToLocal(mouseSceneCoords).getY();

                String x = xAxis.getValueForDisplay(xx);

                if (xx < 0 || yy < 0 || x == null) {
                    mousePositionToolTip.hide();
                    return;
                }

                String text = "Date & Time: " + x + "\n";

                int index1 = columns.indexOf(firstChoiceBox.getValue());
                int index2 = columns.indexOf(secondChoiceBox.getValue());
                int index3 = columns.indexOf(thirdChoiceBox.getValue());

                if (index1 > 0) {
                    for (String[] datum : data) {
                        if (x.equals(datum[0])) {
                            text += firstChoiceBox.getValue() + " : " + datum[index1] + "\n";
                            break;
                        }
                    }
                }
                if (index2 > 0) {
                    for (String[] datum : data) {
                        if (x.equals(datum[0])) {
                            text += secondChoiceBox.getValue() + " : " + datum[index2] + "\n";
                            break;
                        }
                    }
                }
                if (index3 > 0) {
                    for (String[] datum : data) {
                        if (x.equals(datum[0])) {
                            text += thirdChoiceBox.getValue() + " : " + datum[index3] + "\n";
                            break;
                        }
                    }
                }

                mousePositionToolTip.setText(text);
                mousePositionToolTip.show(node, event.getScreenX() + 15, event.getScreenY() + 15);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        lineCharts.setOnMouseExited((EventHandler<? super MouseEvent>) event -> mousePositionToolTip.hide());

        lineCharts.getStyleClass().add("thick-chart");
    }

}