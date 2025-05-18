package math.droid.p.myapplication;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ReportActivity extends ListActivity {

    private DbHelper helper;
    private long[] logIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new DbHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, msg || ' - ' || timestamp AS display FROM Logs ORDER BY id",
                null
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1
        );

        logIds = new long[c.getCount()];
        int idx = 0;
        while (c.moveToNext()) {
            logIds[idx] = c.getLong(c.getColumnIndex("id"));
            adapter.add(c.getString(c.getColumnIndex("display")));
            idx++;
        }
        c.close();
        db.close();

        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT loc.latitude, loc.longitude " +
                        "FROM Logs log INNER JOIN Location loc ON log.id_location = loc.id " +
                        "WHERE log.id = ?",
                new String[]{ String.valueOf(logIds[position]) }
        );
        if (c.moveToFirst()) {
            double lat = c.getDouble(0);
            double lng = c.getDouble(1);
            Toast.makeText(
                    this,
                    "Lat: " + lat + " , Long: " + lng,
                    Toast.LENGTH_LONG
            ).show();
        }
        c.close();
        db.close();
    }
}
