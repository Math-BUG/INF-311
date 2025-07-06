package math.droid.p.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private SQLiteHelper dbHelper;
    private ListView lvReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        dbHelper = new SQLiteHelper(this);
        lvReport = findViewById(R.id.lvReport);
        loadReport();
    }

    private void loadReport() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT place, COUNT(*) as visits " +
                "FROM checkins GROUP BY place " +
                "ORDER BY visits DESC";
        Cursor c = db.rawQuery(query, null);

        List<Map<String, String>> data = new ArrayList<>();
        while (c.moveToNext()) {
            Map<String, String> item = new HashMap<>();
            item.put("place", c.getString(c.getColumnIndex("place")));
            item.put("visits", String.valueOf(c.getInt(c.getColumnIndex("visits"))));
            data.add(item);
        }
        c.close();

        String[] from = {"place", "visits"};
        int[] to = {android.R.id.text1, android.R.id.text2};
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2, from, to);
        lvReport.setAdapter(adapter);
    }
}
