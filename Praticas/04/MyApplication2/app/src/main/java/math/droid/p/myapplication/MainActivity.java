package math.droid.p.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView tvLightValue;
    private TextView tvProxValue;
    private Button btnReturn;

    private float light;
    private float prox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtém referências de UI
        tvLightValue = findViewById(R.id.tvLightValue);
        tvProxValue  = findViewById(R.id.tvProxValue);
        btnReturn    = findViewById(R.id.btnReturn);

        // Recebe extras da Intent
        Intent intent = getIntent();
        light = intent.getFloatExtra("sensor_light", 0f);
        prox  = intent.getFloatExtra("sensor_proximity", 0f);

        // Exibe valores
        tvLightValue.setText("Luz: " + light + " lx");
        tvProxValue .setText("Proximidade: " + prox  + " cm");

        // Botão de retorno — calcula classificação e devolve ao App A
        btnReturn.setOnClickListener(v -> {
            boolean lightLow = light < 20.0f;
            boolean proxFar  = prox  >  3.0f;

            Intent result = new Intent();
            result.putExtra("classify_light_low", lightLow);
            result.putExtra("classify_prox_far",  proxFar);
            setResult(Activity.RESULT_OK, result);
            finish();
        });
    }
}
