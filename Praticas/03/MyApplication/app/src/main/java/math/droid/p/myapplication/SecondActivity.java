package math.droid.p.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class SecondActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng targetCoord;
    private static final int REQ_PERM_LOCATION = 1001;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mapView;
    private GoogleMap mMap;
    private Marker userMarker;

    private FusedLocationProviderClient fusedLocationClient;
    private DbHelper dbHelper;
    private int selectedLocationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        dbHelper = new DbHelper(this);
        selectedLocationId = getIntent()
                .getIntExtra(MainActivity.EXTRA_LOCATION_ID, -1);
        fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.mapView);
        MapsInitializer.initialize(getApplicationContext());
        Bundle mvBundle = savedInstanceState != null
                ? savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
                : null;
        mapView.onCreate(mvBundle);
        mapView.getMapAsync(this);

        findViewById(R.id.btn_macaubas)
                .setOnClickListener(v -> centerFixed("Macaúbas"));
        findViewById(R.id.btn_vicosa)
                .setOnClickListener(v -> centerFixed("Viçosa"));
        findViewById(R.id.btn_dpi)
                .setOnClickListener(v -> centerFixed("DPI/UFV"));

        Button btnAdd = findViewById(R.id.btn_add_marker);
        btnAdd.setOnClickListener(v -> {
            if (!hasLocationPermission()) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQ_PERM_LOCATION
                );
            } else {
                addUserMarker();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name, latitude, longitude FROM Location", null);
        while (c.moveToNext()) {
            String name = c.getString(0);
            double lat = c.getDouble(1);
            double lng = c.getDouble(2);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );

        }
        c.close();
        db.close();

        String initialName = getIntent().getStringExtra("LOCATION_NAME");
        centerFixed(initialName);
    }

    private void centerFixed(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, latitude, longitude FROM Location WHERE name = ?",
                new String[]{ name }
        );
        if (c.moveToFirst()) {

            selectedLocationId = c.getInt(0);

            targetCoord = new LatLng(c.getDouble(1), c.getDouble(2));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetCoord, 12f));
        }
        c.close();
        db.close();
    }

    private void centerFixedById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT latitude, longitude FROM Location WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
        if (c.moveToFirst()) {
            LatLng pos = new LatLng(c.getDouble(0), c.getDouble(1));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12f));
        }
        c.close();
        db.close();
    }

    private void addUserMarker() {
        CancellationTokenSource cts = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        cts.getToken()
                )
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location loc) {
                        if (loc == null || mMap == null) {
                            Toast.makeText(SecondActivity.this,
                                    "Não foi possível obter localização.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

                        if (userMarker != null) userMarker.remove();

                        userMarker = mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title("Minha localização atual")
                                .icon(BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_AZURE))
                        );
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12f));

                        // Agora calcula a distância usando a posição recém obtida
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        Cursor c = db.rawQuery(
                                "SELECT latitude, longitude FROM Location WHERE id = ?",
                                new String[]{ String.valueOf(selectedLocationId) }
                        );
                        if (c.moveToFirst()) {
                            float[] result = new float[1];
                            Location.distanceBetween(
                                    pos.latitude, pos.longitude,
                                    c.getDouble(0), c.getDouble(1),
                                    result
                            );
                            Toast.makeText(
                                    SecondActivity.this,
                                    "Distância: " + Math.round(result[0]) + " m",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                        c.close();
                        db.close();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SecondActivity.this,
                                "Erro ao obter localização: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perm, int[] res) {
        super.onRequestPermissionsResult(req, perm, res);
        if (req == REQ_PERM_LOCATION
                && res.length > 0
                && res[0] == PackageManager.PERMISSION_GRANTED) {
            addUserMarker();
        } else {
            Toast.makeText(this,
                    "Permissão negada.", Toast.LENGTH_SHORT).show();
        }
    }

    // Ciclo de vida MapView
    @Override protected void onResume()    { super.onResume(); mapView.onResume(); }
    @Override protected void onStart()     { super.onStart();  mapView.onStart(); }
    @Override protected void onPause()     { mapView.onPause(); super.onPause(); }
    @Override protected void onStop()      { super.onStop();   mapView.onStop(); }
    @Override protected void onDestroy()   { mapView.onDestroy(); super.onDestroy(); }
    @Override public    void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Bundle mb = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mb == null) {
            mb = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mb);
        }
        mapView.onSaveInstanceState(mb);
        super.onSaveInstanceState(outState);
    }
}
