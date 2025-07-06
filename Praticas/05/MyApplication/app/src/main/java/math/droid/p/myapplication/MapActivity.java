package math.droid.p.myapplication;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dbHelper = new SQLiteHelper(this);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        loadMarkers();
    }

    private void loadMarkers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("checkins", null, null, null, null, null, null);
        while (c.moveToNext()) {
            String place = c.getString(c.getColumnIndex("place"));
            double lat = c.getDouble(c.getColumnIndex("latitude"));
            double lng = c.getDouble(c.getColumnIndex("longitude"));
            LatLng coord = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions()
                    .position(coord)
                    .title(place)
                    .snippet("Lat:"+lat+" Lng:"+lng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 12));
        }
        c.close();
    }
}