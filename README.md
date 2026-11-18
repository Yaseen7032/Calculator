# Everyday Calculator

A dependency-free Java Swing calculator for addition, subtraction, multiplication, and division. Expressions support parentheses, decimals, unary signs, and standard operator precedence. The display updates with a preview as you type.

## Requirements

- Java Development Kit (JDK) 17 or newer

## Compile and run

From the project directory in PowerShell:

```powershell
javac -d out src/CalculatorApp.java
java -cp out CalculatorApp
```

Use the on-screen keys or type an expression. Press Enter to evaluate, Backspace to delete, and Escape to clear.

## Run the expression tests

```powershell
javac -d out src/CalculatorApp.java src/CalculatorAppTest.java
java -cp out CalculatorAppTest
```