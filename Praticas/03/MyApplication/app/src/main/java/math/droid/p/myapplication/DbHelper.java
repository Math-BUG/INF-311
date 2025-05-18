package math.droid.p.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "places.db";
    private static final int DB_VERSION = 1;

    public DbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // tabela Location
        db.execSQL(
                "CREATE TABLE Location (" +
                        "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  name TEXT," +
                        "  latitude REAL," +
                        "  longitude REAL" +
                        ");"
        );
        // tabela Logs
        db.execSQL(
                "CREATE TABLE Logs (" +
                        "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  msg TEXT," +
                        "  timestamp TEXT," +
                        "  id_location INTEGER," +
                        "  FOREIGN KEY(id_location) REFERENCES Location(id)" +
                        ");"
        );

        // popula Location com suas três coordenadas
        db.execSQL("INSERT INTO Location(name, latitude, longitude) VALUES" +
                "('Macaúbas',   -13.0120, -42.6871)," +
                "('DPI/UFV',  -20.7650, -42.8684)," +
                "('Viçosa',   -20.7565, -42.8782)" +
                ";"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //se não dá erro na classe kkk

    }


}
