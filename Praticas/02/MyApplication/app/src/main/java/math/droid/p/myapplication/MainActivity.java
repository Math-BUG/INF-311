package math.droid.p.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText; // Certifique-se de que o material design está incluído
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etNome, etIdade, etPeso, etAltura;
    private TextInputLayout tilNome, tilIdade, tilPeso, tilAltura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilNome = findViewById(R.id.tilNome);
        tilIdade = findViewById(R.id.tilIdade);
        tilPeso = findViewById(R.id.tilPeso);
        tilAltura = findViewById(R.id.tilAltura);

        etNome = findViewById(R.id.etNome);
        etIdade = findViewById(R.id.etIdade);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);

        // Referência para o botão que envia os dados
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recuperar os dados digitados
                String nome = etNome.getText().toString();
                String idadeStr = etIdade.getText().toString();
                String pesoStr  = etPeso.getText().toString();
                String alturaStr = etAltura.getText().toString();

                // Validação básica para evitar conversões com string vazia
                if (nome.isEmpty() || idadeStr.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty()) {
                    return;
                }

                // Conversão para os tipos adequados
                int idade = Integer.parseInt(idadeStr);
                double peso = Double.parseDouble(pesoStr);
                double altura = Double.parseDouble(alturaStr);

                // Criação do Intent para ir para a ReportActivity e envio dos dados
                Intent it = new Intent(MainActivity.this, ReportActivity.class);
                it.putExtra("nome", nome);
                it.putExtra("idade", idade);
                it.putExtra("peso", peso);
                it.putExtra("altura", altura);


                it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(it);
            }
        });
    }
}
