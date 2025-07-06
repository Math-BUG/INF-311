package math.droid.p.myapplication;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ManagementActivity extends AppCompatActivity {

    private SQLiteHelper dbHelper;
    private ListView lvCheckIns;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        dbHelper = new SQLiteHelper(this);
        lvCheckIns = findViewById(R.id.lvCheckIns);
        loadList();

        lvCheckIns.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = list.get(position);
                confirmDelete(item);
                return true;
            }
        });
    }

    private void loadList() {
        list = dbHelper.getAllPlaceNames();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        lvCheckIns.setAdapter(adapter);
    }

    private void confirmDelete(final String place) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Deseja excluir '"+place+"'?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("checkins", "place=?",
                                new String[]{place});
                        loadList();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
