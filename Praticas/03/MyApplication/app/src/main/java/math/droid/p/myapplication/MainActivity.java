package math.droid.p.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.Instant;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_LOCATION_ID = "math.droid.p.myapplication.LOCATION_ID";

    private DbHelper dbHelper;

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

        dbHelper = new DbHelper(this);

        findViewById(R.id.textView).setOnClickListener(v ->
                logAndOpen("Macaúbas", "Minha Casa – Macaúbas")
        );

        findViewById(R.id.textView2).setOnClickListener(v ->
                logAndOpen("Viçosa", "Minha Casa – Viçosa")
        );

        findViewById(R.id.textView3).setOnClickListener(v ->
                logAndOpen("DPI/UFV", "DPI S2 (DPI/UFV)")
        );

        findViewById(R.id.textViewReport).setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
        });
    }

    private void logAndOpen(String locationName, String toastLabel) {

        Toast.makeText(this, toastLabel, Toast.LENGTH_SHORT).show();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id FROM Location WHERE name = ?",
                new String[]{ locationName }
        );
        int idLoc = -1;
        if (c.moveToFirst()) {
            idLoc = c.getInt(0);
        }
        c.close();

        if (idLoc != -1) {

            ContentValues cv = new ContentValues();
            cv.put("msg", locationName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cv.put("timestamp", Instant.now().toString());
            }
            cv.put("id_location", idLoc);
            db.insert("Logs", null, cv);

            Intent it = new Intent(this, SecondActivity.class);
            it.putExtra(EXTRA_LOCATION_ID, idLoc);
            it.putExtra("LOCATION_NAME", locationName);
            startActivity(it);
        } else {
            Toast.makeText(this,
                    "Erro: local não cadastrado no banco.",
                    Toast.LENGTH_LONG).show();
        }

        db.close();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
