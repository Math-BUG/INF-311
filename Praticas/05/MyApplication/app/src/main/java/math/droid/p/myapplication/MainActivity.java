package math.droid.p.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_LOCATION = 100;

    private SQLiteHelper dbHelper;
    private AutoCompleteTextView edtPlace;
    private Spinner spnCategory;
    private Button btnCheckIn;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLocationPermission();

        dbHelper = new SQLiteHelper(this);
        edtPlace = findViewById(R.id.edtPlace);
        spnCategory = findViewById(R.id.spnCategory);
        btnCheckIn = findViewById(R.id.btnCheckIn);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ArrayAdapter<String> autoAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Home", "Work", "Gym", "Park"}
        );
        edtPlace.setAdapter(autoAdapter);

        Cursor catCursor = dbHelper.getAllCategories();
        String[] from = {"nome"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter dbAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                catCursor,
                from,
                to,
                0
        );
        dbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(dbAdapter);

        btnCheckIn.setOnClickListener(v -> {
            Toast.makeText(this, "Clique detectado!", Toast.LENGTH_SHORT).show();
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            saveCheckIn(
                                    edtPlace.getText().toString(),
                                    spnCategory.getSelectedItem().toString(),
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                        } else {
                            Toast.makeText(this, "Localização nula", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,
                                "Erro ao obter localização: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão de localização concedida", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveCheckIn(String place, String category, double lat, double lng) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Local", place);
        cv.put("cat", dbHelper.getCategoryId(category));
        cv.put("latitude", String.valueOf(lat));
        cv.put("longitude", String.valueOf(lng));
        long rows = db.update(
                "Checkin",
                cv,
                "Local = ?",
                new String[]{place}
        );
        if (rows == 0) {
            cv.put("qtdVisitas", 1);
            db.insert("Checkin", null, cv);
        } else {
            int current = dbHelper.getVisitCount(place) + 1;
            dbHelper.updateVisitCount(place, current);
        }
        Toast.makeText(this, "Check‑in salvo!", Toast.LENGTH_SHORT).show();
    }
}
