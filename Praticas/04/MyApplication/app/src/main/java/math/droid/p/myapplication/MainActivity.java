package math.droid.p.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    // Sensores e valores
    private SensorManager sensorManager;
    private Sensor lightSensor, proximitySensor;
    private float latestLightValue = 0f;
    private float latestProximityValue = 0f;

    // Helpers de hardware
    private LanternaHelper lanternaHelper;
    private MotorHelper motorHelper;

    // Controles de UI
    private SwitchCompat switchFlash;
    private SwitchCompat switchVibe;
    private Button btnClassify;

    // Activity Result API
    private ActivityResultLauncher<Intent> classifyLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa UI
        switchFlash = findViewById(R.id.switchFlash);
        switchVibe  = findViewById(R.id.switchVibe);
        btnClassify = findViewById(R.id.btnClassify);

        // Inicializa helpers
        lanternaHelper = new LanternaHelper(this);
        motorHelper   = new MotorHelper(this);

        // Inicializa sensores
        sensorManager   = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor     = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Configura Activity Result
        classifyLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        boolean lightLow = data.getBooleanExtra("classify_light_low", false);
                        boolean proxFar  = data.getBooleanExtra("classify_prox_far", false);

                        // Atualiza estados
                        if (lightLow) lanternaHelper.ligar(); else lanternaHelper.desligar();
                        switchFlash.setChecked(lightLow);
                        if (proxFar) motorHelper.iniciarVibracao(); else motorHelper.pararVibracao();
                        switchVibe.setChecked(proxFar);
                    }
                }
        );

        // Listener do botão
        btnClassify.setOnClickListener(v -> sendToClassifier());
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                latestLightValue = event.values[0];
            } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                latestProximityValue = event.values[0];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorListener);
        lanternaHelper.desligar();
        motorHelper.pararVibracao();
    }

    private void sendToClassifier() {
        Intent intent = new Intent("com.seuapp.CLASSIFY");
        intent.putExtra("sensor_light", latestLightValue);
        intent.putExtra("sensor_proximity", latestProximityValue);
        classifyLauncher.launch(intent);
    }
}
