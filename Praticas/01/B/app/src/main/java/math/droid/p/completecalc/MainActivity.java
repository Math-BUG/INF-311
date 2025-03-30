package math.droid.p.completecalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText etDisplay;
    private String currentInput = "";
    private double operand1 = 0;
    private String operator = "";
    private boolean hasResult = false; // indica se o último resultado já foi exibido

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    @NonNull
    private String FormatResult(String s){
        double value = Double.parseDouble(s);
        if(value % 1 ==0) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etDisplay = findViewById(R.id.etDisplay);

        // Tornando o visor não editável pelo usuário
        etDisplay.setFocusable(false);
        etDisplay.setClickable(false);
        etDisplay.setFocusableInTouchMode(false);

        // Configura os botões numéricos (0-9)
        int[] numericButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9
        };

        View.OnClickListener numericListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                String digit = btn.getText().toString();
                // Só limpa o display se o resultado anterior estiver sendo exibido
                // e nenhum operador tiver sido definido para a nova operação.
                if (hasResult && operator.isEmpty()) {
                    currentInput = "";
                    hasResult = false;
                }
                currentInput += digit;
                etDisplay.setText(currentInput);
            }
        };

        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(numericListener);
        }

        // Configura os botões de operadores (+, -, *, /)
        int[] operatorButtons = { R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide };
        View.OnClickListener operatorListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                String op = btn.getText().toString();
                // Se já existe um número digitado e nenhum operador foi definido ainda,
                // registra o primeiro operando e adiciona o operador à expressão.
                if (!currentInput.isEmpty() && operator.isEmpty()) {
                    try {
                        operand1 = Double.parseDouble(currentInput);
                    } catch(NumberFormatException e) {
                        etDisplay.setText(getString(R.string.error));
                        currentInput = "";
                        return;
                    }
                    operator = op;
                    currentInput += op;
                    etDisplay.setText(currentInput);
                } else if (hasResult) {
                    // Se o último resultado foi exibido, utiliza-o como primeiro operando para a nova operação
                    operator = op;
                    currentInput = etDisplay.getText().toString() + op;
                    etDisplay.setText(currentInput);
                    hasResult = false;
                }
            }
        };

        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(operatorListener);
        }

        // Botão do ponto decimal "."
        findViewById(R.id.btnDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasResult) {
                    currentInput = "";
                    hasResult = false;
                }
                // Permite inserir o ponto apenas se o número atual (após o último operador) não tiver já um "."
                String[] parts = currentInput.split("[+\\-*/]");
                String lastNumber = parts.length > 0 ? parts[parts.length - 1] : "";
                if (!lastNumber.contains(".") && !lastNumber.isEmpty()) {
                    currentInput += ".";
                    etDisplay.setText(currentInput);
                }
            }
        });

        // Botão "C" para limpar o visor e reiniciar a memória da calculadora
        findViewById(R.id.btnC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentInput = "";
                operator = "";
                operand1 = 0;
                etDisplay.setText("");
                hasResult = false;
            }
        });

        // Botão de backspace ("<<") para remover o último caractere digitado
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentInput.isEmpty()) {
                    char lastChar = currentInput.charAt(currentInput.length() - 1);
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    etDisplay.setText(currentInput);
                    if (isOperator(lastChar)) {
                        operator = ""; // Remove o operador se o último caractere era um operador
                    }
                    // Se o usuário apagar o operador, podemos considerar que o resultado não está mais "fixo"
                    hasResult = false;
                }
            }
        });


        // Botão "=" que realiza o cálculo da expressão
        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!operator.isEmpty()){
                    // Localiza a posição do operador e verifica se existe um segundo operando
                    int opIndex = currentInput.indexOf(operator);
                    if (opIndex != -1 && opIndex < currentInput.length() - 1) {
                        String secondOperandStr = currentInput.substring(opIndex + 1);
                        try {
                            double operand2 = Double.parseDouble(secondOperandStr);
                            String result = String.valueOf(0);
                            // Impede divisão por zero
                            if (operator.equals("/") && operand2 == 0) {
                                etDisplay.setText(getString(R.string.error));
                                currentInput = "";
                                operator = "";
                                hasResult = true;
                                return;
                            }
                            switch (operator) {
                                case "+":
                                    result = String.valueOf( operand1 + operand2);
                                    break;
                                case "-":
                                    result = String.valueOf(operand1 - operand2);
                                    break;
                                case "*":
                                    result = String.valueOf(operand1 * operand2);
                                    break;
                                case "/":
                                    result = String.valueOf(operand1 / operand2);
                                    break;
                            }
                            result = FormatResult(result);
                            etDisplay.setText(String.valueOf(result));
                            // O resultado pode ser reutilizado em uma nova operação
                            currentInput = String.valueOf(result);
                            operator = "";
                            hasResult = true;
                        } catch (NumberFormatException e) {
                            etDisplay.setText(getString(R.string.error));
                            currentInput = "";
                            operator = "";
                            hasResult = true;
                        }
                    }
                }
            }
        });
    }
}
