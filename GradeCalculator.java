import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Assignment 4
 * Program generates JavaFX Application to calculate a student's grade in a course according to Queen's course grading scheme
 * (Didn't use SceneBuilder because I wanted to understand/practice the code behind the UI. Sorry there's so much code!)
 * @author Amy Tang 20130856
 * @version 1.0
 * August 12, 2019
 */
public class GradeCalculator extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Queen's Course Grade Calculator");
        BorderPane borderPane = new BorderPane();
        GridPane gridPane = createGradePane();

        addUIControls(gridPane);  //add TextFields and Labels to the GridPane
        addButton(borderPane);  //add the Button to the BorderPane under the GridPane

        borderPane.setCenter(gridPane);  //place GridPane in the Center block of BorderPane

        Scene scene = new Scene(borderPane, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }  //end start

    private List<TextField> textList = new ArrayList<>();  //global ArrayList for use between addUIControls (load TextFields) and addButton (pull from TextFields)

    /**
     * Method adds a button and the calculator functionality behind clicking it
     * @param borderPane the border pane the button is set on
     */
    private void addButton(BorderPane borderPane) throws NumberFormatException {
        Button calculateButton = new Button("Calculate!");
        calculateButton.setDefaultButton(true);
        calculateButton.setPrefSize(100, 40);
        //lambda function for button click
        calculateButton.setOnAction((event) -> {
            try {
                ArrayList<Float> nums = new ArrayList<>();
                for (TextField t : textList) {
                    if (t.getText() == null) {  //boxes must be filled with input
                        throw new NumberFormatException("Input must be in String format.");
                    } else {
                        String input = t.getText();
                        float numInput = Float.parseFloat(input.trim());  //convert from String to Float
                        nums.add(numInput);
                    }
                }
                //decimal format
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);  //2 decimal places max.
                //GradeCalculatorException call
                for (int i = 0; i < nums.size(); i+=3) {  //loop through ArrayList of TextField Float values
                    // (order in List: your score, total, weight, repeat; where i=0 is your score, i=1 is total, i=2 is weight, i=3 is next row)
                    if (nums.get(i) > nums.get(i+1)) {  //if your score > total
                        throw new GradeCalculatorException("Your score must be less or equal to total score.");
                    }
                }
                //percentage of current grade accounted for
                float percentageGrade = 0;
                for (int i = 2; i < nums.size(); i += 3) {  //start at weight, increment to next weight
                    percentageGrade += nums.get(i);
                }
                if (percentageGrade > 100) {  //if total percentage is greater than 100
                    throw new GradeCalculatorException("Total percentage of evaluations must be less or equal to 100.");
                }
                System.out.println("Your current grades account for " + df.format(percentageGrade) + "% of your overall grade.");
                //current average
                float currentAvg;
                float numerator = 0;
                float denominator = 0;
                for (int i = 0; i < nums.size(); i += 3) {  //start at your score, increment to next row (your score)
                    numerator += nums.get(i);  //numerator is sum of your score
                    denominator += nums.get(i + 1);  //denominator is sum of total score
                }
                currentAvg = numerator / denominator * 100;
                System.out.println("Your current average is " + df.format(currentAvg) + "%.");
                //current grade
                float currentGrade = 0;
                for (int i = 0; i < nums.size(); i += 3) {  //start at your score, increment to next your score
                    float totalSoFar = nums.get(i) / nums.get(i + 1) * nums.get(i + 2);  //your score/total*weight
                    currentGrade += totalSoFar;
                }
                System.out.println("Your current grade so far is " + df.format(currentGrade) + "%.");
                //to achieve desired grades
                float[] desiredGrades = {90, 85, 80, 75, 73, 70, 67, 63, 60, 57, 53, 50, 49};  //list of minimum % to hit each grade level
                String[] letterGrades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F"};   //corresponding letter grade for each level
                float[] gpaScale = {(float) 4.3, (float) 4.0, (float) 3.7, (float) 3.3, (float) 3.0, (float) 2.7,    //corresponding GPA for each level
                        (float) 2.3, (float) 2.0, (float) 1.7, (float) 1.3, (float) 1.0, (float) 0.7, (float) 0.0};  //casted to float

                for (int i = 0; i < desiredGrades.length; i++) {  //all above arrays are same length, so desiredGrades.length works for letterGrades and gpaScale
                    float whatYouNeed = (desiredGrades[i] - currentGrade) / (100 - percentageGrade) * 100;
                    System.out.println("You need " + df.format(whatYouNeed) + "% on the remaining evaluations to get an "
                            + letterGrades[i] + " or " + gpaScale[i] + " GPA in the course.");  //i corresponds to equivalent grade representation across arrays
                }
            }
            catch (NumberFormatException nfe) {
                System.out.println("Please enter only numbers. Make sure you fill up all spaces. Total score must be greater than 0.");
            }
            catch (GradeCalculatorException gce) {
                System.out.println("Oops! Your achieved score must be less or equal to total score. Or, make sure the total percentage of evaluations are less or equal to 100.");
            }
        });  //end lambda
        borderPane.setBottom(calculateButton);
        BorderPane.setAlignment(calculateButton, Pos.CENTER);
    }  //end method

    /**
     * Method creates the grid for clean interface of text boxes
     * @return GridPane that serves as basis for text boxes and labels
     */
    private GridPane createGradePane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(40, 40, 40, 40));
        gridPane.setGridLinesVisible(false);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        //assignment column
        ColumnConstraints assignmentColumn = new ColumnConstraints(100, 100, Double.MAX_VALUE);  //size of column 1
        assignmentColumn.setHalignment(HPos.CENTER);
        assignmentColumn.setHgrow(Priority.NEVER);  //doesn't stretch
        //your score
        ColumnConstraints myScoreColumn = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        myScoreColumn.setHalignment(HPos.CENTER);
        myScoreColumn.setHgrow(Priority.ALWAYS);  //text boxes stretch when application is stretched
        //total score
        ColumnConstraints totalScoreColumn = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        totalScoreColumn.setHalignment(HPos.CENTER);
        totalScoreColumn.setHgrow(Priority.ALWAYS);
        //percentage weight
        ColumnConstraints percentWeight = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        percentWeight.setHalignment(HPos.CENTER);
        percentWeight.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(assignmentColumn, myScoreColumn, totalScoreColumn, percentWeight);  //add to gridPane

        return gridPane;
    }  //end method

    /**
     * Method builds the text boxes and labels that make up user interface
     * @param gridPane the GridPane UI is added to
     */
    private void addUIControls(GridPane gridPane) {
        //column headers
        gridPane.add(new Label("My Score"), 1, 0);
        gridPane.add(new Label("Total Score"), 2, 0);
        gridPane.add(new Label("Percentage Weight"), 3, 0);
        //labels
        int col0 = 0;
        for (int row = 1; row < 6; row++) {
            Label label = new Label("Evaluation: ");
            gridPane.add(label, col0, row);
        }
        //text fields
        for (int row = 1; row <6; row++) {   //start by filling up TextFields row by row, so content can be referenced by indexing
                                            // where i=1 is your score, i=2 is total, i=2 is weight, i=3 is next;
                                            // which can't be done if done column by column in the case a new row is added
            for (int col = 1; col < 4; col++) {
                TextField textField = new TextField();
                textField.setPrefHeight(40);
                GridPane.setConstraints(textField, col, row);
                gridPane.add(textField, col, row);
                textList.add(textField);
            }  //end loop
        }  //end loop
    }  //end method

    public static void main(String[] args) {
        launch(args);
    }
}  //end program
