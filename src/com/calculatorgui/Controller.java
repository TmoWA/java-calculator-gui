package com.calculatorgui;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Stack;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField resultField;
    @FXML
    private TextField expressionField;

    private LinkedList<String> reversePolishNotation = new LinkedList<>();
    private LinkedList<String> expressionList = new LinkedList<>();

    private SimpleStringProperty expressionProperty = new SimpleStringProperty("");
    private SimpleStringProperty resultProperty = new SimpleStringProperty("");

    private Stack<String> output = new Stack<>();
    private Stack<String> operators = new Stack<>();

    private StringBuilder currentNumber;
    private boolean isCurrentNumberEmpty;
    private boolean needToClearExpression;

    private DecimalFormat numberFormat = new DecimalFormat("#,##0.###########");

    private double result = 0;

    @FXML
    private void initialize() {
        resultField.textProperty().bind(resultProperty);
        expressionField.textProperty().bind(expressionProperty);

        resultProperty.setValue("0");
        currentNumber = new StringBuilder();
        isCurrentNumberEmpty = true;
    }

    private void clearExpressionIfNecessary() {
        if(needToClearExpression) {
            expressionProperty.setValue("");
            needToClearExpression = false;
        }
    }

    @FXML
    private void addition() {
        if(!isCurrentNumberEmpty) {
            expressionList.add(currentNumber.toString());
            expressionList.add("+");
            expressionProperty.setValue(expressionProperty.getValue() +
                    numberFormat.format(Double.parseDouble(currentNumber.toString()))
                    + " + ");
            currentNumber = new StringBuilder();
            isCurrentNumberEmpty = true;
        }
    }

    @FXML
    private void subtraction() {
        if(!isCurrentNumberEmpty) {
            expressionList.add(currentNumber.toString());
            expressionList.add("-");
            expressionProperty.setValue(expressionProperty.getValue() +
                    numberFormat.format(Double.parseDouble(currentNumber.toString()))
                    + " - ");
            currentNumber = new StringBuilder();
            isCurrentNumberEmpty = true;
        }
    }

    @FXML
    private void multiplication() {
        if(!isCurrentNumberEmpty) {
            expressionList.add(currentNumber.toString());
            expressionList.add("*");
            expressionProperty.setValue(expressionProperty.getValue() +
                    numberFormat.format(Double.parseDouble(currentNumber.toString()))
                    + " * ");
            currentNumber = new StringBuilder();
            isCurrentNumberEmpty = true;
        }
    }

    @FXML
    private void division() {
        if(!isCurrentNumberEmpty) {
            expressionList.add(currentNumber.toString());
            expressionList.add("/");
            expressionProperty.setValue(expressionProperty.getValue() +
                    numberFormat.format(Double.parseDouble(currentNumber.toString()))
                    + " ÷ ");
            currentNumber = new StringBuilder();
            isCurrentNumberEmpty = true;
        }
    }

    @FXML
    private void modulus() {
        if(!isCurrentNumberEmpty) {
            expressionList.add(currentNumber.toString());
            expressionList.add("%");
            expressionProperty.setValue(expressionProperty.getValue() + currentNumber.toString() + " % ");

            currentNumber = new StringBuilder();
            isCurrentNumberEmpty = true;
        }
    }

    @FXML
    private void squareNumber() {
        if(!isCurrentNumberEmpty) {
            expressionList.add("" + Double.parseDouble(currentNumber.toString()) * Double.parseDouble(currentNumber.toString()));
            expressionProperty.setValue(expressionProperty.getValue() +
                    numberFormat.format(Double.parseDouble(currentNumber.toString()))
                    + "^2 ");
            currentNumber = new StringBuilder();
            isCurrentNumberEmpty = true;

            processEquation();
        }
    }

    @FXML
    private void squareRoot() {
        if(!isCurrentNumberEmpty) {
            expressionList.add("" + Math.sqrt(Double.parseDouble(currentNumber.toString())));
            expressionProperty.setValue(expressionProperty.getValue() +
                    "sqrt(" + currentNumber.toString() + ") ");

            currentNumber = new StringBuilder();
            isCurrentNumberEmpty = true;

            processEquation();
        }
    }

    @FXML
    private void oneDividedByX() {
        if(!isCurrentNumberEmpty) {
            expressionList.add("" + 1 / Double.parseDouble(currentNumber.toString()));
            expressionProperty.setValue(expressionProperty.getValue()
                    + " (1 / " + numberFormat.format(Double.parseDouble(currentNumber.toString())) + ") ");
            currentNumber = new StringBuilder();
            isCurrentNumberEmpty = true;

            processEquation();
        }
    }

    @FXML
    private void clear() {
        currentNumber = new StringBuilder();
        reversePolishNotation.clear();
        expressionList.clear();
        expressionProperty.setValue("");
        isCurrentNumberEmpty = true;
        result = 0;
        resultProperty.setValue("" + 0);
    }

    @FXML
    private void clearEntry() {
        currentNumber = new StringBuilder();
        resultProperty.setValue(currentNumber.toString());
    }

    @FXML
    private void backspace() {
        if(currentNumber.length() >= 1) {
            currentNumber.setLength(currentNumber.length() - 1);
            resultProperty.setValue(currentNumber.toString());
        }
    }

    private int getOperatorPrecedence(String operator1, String operator2) {
        int precedence = -1;

        switch(operator1) {
            case "(":
            case ")":
                break;
            case "^":
                if(operator2.matches("[\\(\\)]")) {
                    precedence = -1;
                } else if (operator2.equals("^")) {
                    precedence = 0;
                } else if (operator2.matches("[\\+\\*/\\-%]")) {
                    precedence = 1;
                }
                break;
            case "*":
            case "/":
            case "%":
                if(operator2.matches("[\\(\\)^]")) {
                    precedence = -1;
                } else if (operator2.equals("*") || operator2.equals("/") || operator2.contentEquals("%")) {
                    precedence = 0;
                } else if (operator2.matches("[\\+\\-]")) {
                    precedence = 1;
                }
                break;
            case "+":
            case "-":
                if(operator2.equals("-") || operator2.equals("+")) {
                    precedence = 0;
                }
                break;
            default:
                break;
        }

        return precedence;
    }

    private void convertToReversePolishNotation() {
        /* This method is based on the Shunting-yard algorithm for converting
        expressions to the Reverse Polish notation form.

        The following is a convenience wiki link for more details:
        https://en.wikipedia.org/wiki/Shunting-yard_algorithm#The_algorithm_in_detail
         */

        while(!expressionList.isEmpty()) {

            String token = expressionList.pollFirst();
            if(token.matches("-?\\d+(\\.\\d+)?")) { //token is a number
                output.push(token);
                reversePolishNotation.add(token);
            } else { // token is not a number and must be an operator
                while(!operators.empty()
                        && getOperatorPrecedence(token, operators.peek()) <= 0
                        && !token.equals("^")) {
                    output.push(operators.peek());
                    reversePolishNotation.add(operators.pop());
                }

                operators.push(token);
            }
        }

        while(!operators.empty()) { //emptying the stack of operators
            output.push(operators.peek());
            reversePolishNotation.add(operators.pop());
        }
    }

    @FXML
    private void processEquation() {
        if(!isCurrentNumberEmpty) {
            expressionList.add(currentNumber.toString());
            expressionProperty.setValue(expressionProperty.getValue() +
                    numberFormat.format(Double.parseDouble(currentNumber.toString()))
                    + " = ");
            currentNumber = new StringBuilder();
            isCurrentNumberEmpty = true;
        }

        if(expressionList == null || expressionList.isEmpty()) {
            return;
        }

        if (expressionList.get(expressionList.size() - 1).matches("[\\*\\+\\-/%^]")){
            //The last character in the expression is an operator, which could indicate something like "1 +" which is not a valid expression.
            return;
        }

        if(expressionList.size() == 1) {
            result = Double.parseDouble(expressionList.pollFirst());
            resultProperty.setValue("" + numberFormat.format(result));
            expressionList = new LinkedList<>();
            needToClearExpression = true;
            return;
        }

        convertToReversePolishNotation();

        while(reversePolishNotation.size() > 1) {
            String operator = "";
            double subresult;
            for (int i = 0; i < reversePolishNotation.size(); i++) {
                if(reversePolishNotation.get(i).matches("[\\*\\+\\-/%^]")) {
                    operator = reversePolishNotation.get(i);

                    switch(operator) {
                        case "+":
                            subresult = Double.parseDouble(reversePolishNotation.get(i-2)) + Double.parseDouble(reversePolishNotation.get(i-1));
                            reversePolishNotation.set(i, "" + subresult);
                            reversePolishNotation.remove(i - 1);
                            reversePolishNotation.remove(i - 2);
                            break;
                        case "-":
                            subresult = Double.parseDouble(reversePolishNotation.get(i-2)) - Double.parseDouble(reversePolishNotation.get(i-1));
                            reversePolishNotation.set(i, "" + subresult);
                            reversePolishNotation.remove(i - 1);
                            reversePolishNotation.remove(i - 2);
                            break;
                        case "*":
                            subresult = Double.parseDouble(reversePolishNotation.get(i-2)) * Double.parseDouble(reversePolishNotation.get(i-1));
                            reversePolishNotation.set(i, "" + subresult);
                            reversePolishNotation.remove(i - 1);
                            reversePolishNotation.remove(i - 2);
                            break;
                        case "/":
                            if(Double.parseDouble(reversePolishNotation.get(i-1)) == 0) {
                                reversePolishNotation.clear();
                                resultProperty.setValue("Can't divide by 0");
                                needToClearExpression = true;
                                expressionList = new LinkedList<>();
                                return;
                            } else {
                                subresult = Double.parseDouble(reversePolishNotation.get(i-2)) / Double.parseDouble(reversePolishNotation.get(i-1));
                                reversePolishNotation.set(i, "" + subresult);
                                reversePolishNotation.remove(i - 1);
                                reversePolishNotation.remove(i - 2);
                            }
                            break;
                        case "%":
                            subresult = Double.parseDouble(reversePolishNotation.get(i-2)) % Double.parseDouble(reversePolishNotation.get(i-1));
                            reversePolishNotation.set(i, "" + subresult);
                            reversePolishNotation.remove(i - 1);
                            reversePolishNotation.remove(i - 2);
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
        }

        result = Double.parseDouble(reversePolishNotation.pollFirst());
        resultProperty.setValue("" + numberFormat.format(result));
        expressionList = new LinkedList<>();
        needToClearExpression = true;
    }

    @FXML
    private void buttonOneClicked() {
        clearExpressionIfNecessary();

        currentNumber.append('1');
        isCurrentNumberEmpty = false;
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonTwoClicked() {
        clearExpressionIfNecessary();

        currentNumber.append('2');
        isCurrentNumberEmpty = false;
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonThreeClicked() {
        clearExpressionIfNecessary();

        currentNumber.append('3');
        isCurrentNumberEmpty = false;
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonFourClicked() {
        clearExpressionIfNecessary();

        currentNumber.append('4');
        isCurrentNumberEmpty = false;
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonFiveClicked() {
        clearExpressionIfNecessary();

        currentNumber.append('5');
        isCurrentNumberEmpty = false;
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonSixClicked() {
        clearExpressionIfNecessary();

        currentNumber.append('6');
        isCurrentNumberEmpty = false;
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonSevenClicked() {
        clearExpressionIfNecessary();

        currentNumber.append('7');
        isCurrentNumberEmpty = false;
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonEightClicked() {
        clearExpressionIfNecessary();

        currentNumber.append('8');
        isCurrentNumberEmpty = false;
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonNineClicked() {
        clearExpressionIfNecessary();

        currentNumber.append('9');
        isCurrentNumberEmpty = false;
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonZeroClicked() {
        clearExpressionIfNecessary();

        if(isCurrentNumberEmpty) {
            currentNumber.append('0');
            isCurrentNumberEmpty = false;
        } else if(currentNumber.charAt(0) != '0') {
            //Prevents unnecessary leading 0s
            currentNumber.append('0');
        }

        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

    @FXML
    private void buttonPeriodClicked() {
        clearExpressionIfNecessary();

        if(isCurrentNumberEmpty) {
            currentNumber.append('0');
            currentNumber.append('.');
            isCurrentNumberEmpty = false;

            resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())) + ".");
            return;
        }

        if(!currentNumber.toString().contains(".")) {
            currentNumber.append('.');
            resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())) + ".");
        }
    }

    @FXML
    private void swapSign() {
        if(!isCurrentNumberEmpty) {
            if(currentNumber.charAt(0) == '-') {
                currentNumber.deleteCharAt(0);
            } else {
                currentNumber.insert(0, '-');
            }
        }
        resultProperty.setValue(numberFormat.format(Double.parseDouble(currentNumber.toString())));
    }

}
