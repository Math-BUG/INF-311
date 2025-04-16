package math.droid.p.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        // Inicializa os componentes do layout
        TextView tvReport = findViewById(R.id.tvReport);
        Button btnBack = findViewById(R.id.btnBack);

        // Recupera os dados enviados pela MainActivity
        Intent intent = getIntent();
        String nome = intent.getStringExtra("nome");
        int idade = intent.getIntExtra("idade", 0);
        double peso = intent.getDoubleExtra("peso", 0);
        double altura = intent.getDoubleExtra("altura", 0);

        // Cálculo do IMC
        double imc = peso / (altura * altura);
        String classificacao = classificaIMC(imc);

        // Monta o relatório
        String relatorio = "Nome: " + nome +
                "\nIdade: " + idade +
                "\nPeso: " + peso + " kg" +
                "\nAltura: " + altura + " m" +
                "\nIMC: " + String.format(Locale.ENGLISH, "%.2f", imc) + "kg/m\u00B2" +
                "\nClassificação: " + classificacao;
        tvReport.setText(relatorio);

        // Configura o botão para retornar à MainActivity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String classificaIMC(double imc) {
        if (imc < 18.5) {
            return "Abaixo do Peso";
        } else if (imc < 25) {
            return "Saudável";
        } else if (imc < 30) {
            return "Sobrepeso";
        } else if (imc < 35) {
            return "Obesidade Grau I";
        } else if (imc < 40) {
            return "Obesidade Grau II (severa)";
        } else {
            return "Obesidade Grau III (mórbida)";
        }
    }
}
