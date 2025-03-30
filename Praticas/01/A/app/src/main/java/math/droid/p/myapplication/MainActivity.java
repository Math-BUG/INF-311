package math.droid.p.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText n1;
    private EditText n2;
    private TextView res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        n1 = findViewById(R.id.n1);
        n2 = findViewById(R.id.n2);
        res = findViewById(R.id.res);
    }

    public void somar(View view) {
        int v1 = Integer.parseInt(n1.getText().toString());
        int v2 = Integer.parseInt(n2.getText().toString());
        res.setText(String.valueOf(v1+v2));
    }

    public void subtrair(View view) {
        int v1 = Integer.parseInt(n1.getText().toString());
        int v2 = Integer.parseInt(n2.getText().toString());
        res.setText(String.valueOf(v1-v2));
    }

    public void multiplicar(View view) {
        int v1 = Integer.parseInt(n1.getText().toString());
        int v2 = Integer.parseInt(n2.getText().toString());
        res.setText(String.valueOf(v1*v2));
    }

    public void dividir(View view) {
        int v1 = Integer.parseInt(n1.getText().toString());
        int v2 = Integer.parseInt(n2.getText().toString());
        if (v2 == 0) {
            res.setText(getString(R.string.division_by_zero));
        } else {
            res.setText(String.valueOf((float) v1 / v2));
        }
    }

}