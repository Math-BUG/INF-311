package math.droid.p.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "checkin_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CHECKIN = "Checkin";
    private static final String TABLE_CATEGORIA = "Categoria";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCategoria = "CREATE TABLE " + TABLE_CATEGORIA + " (" +
                "idCategoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL" +
                ");";
        db.execSQL(createCategoria);

        String[] categorias = {"Restaurante","Bar","Cinema","Universidade","Estádio","Parque","Outros"};
        for (String cat : categorias) {
            db.execSQL("INSERT INTO " + TABLE_CATEGORIA + " (nome) VALUES ('" + cat + "');");
        }

        String createCheckin = "CREATE TABLE " + TABLE_CHECKIN + " (" +
                "Local TEXT PRIMARY KEY, " +
                "qtdVisitas INTEGER NOT NULL, " +
                "cat INTEGER NOT NULL, " +
                "latitude TEXT NOT NULL, " +
                "longitude TEXT NOT NULL, " +
                "CONSTRAINT fkey0 FOREIGN KEY (cat) REFERENCES " + TABLE_CATEGORIA + "(idCategoria)" +
                ");";
        db.execSQL(createCheckin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIA);
        onCreate(db);
    }

    public ArrayList<String> getAllPlaceNames() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CHECKIN,
                new String[]{"Local"}, null, null, null, null, null);
        while (c.moveToNext()) {
            list.add(c.getString(c.getColumnIndex("Local")));
        }
        c.close();
        return list;
    }

    public Cursor getAllCategories() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_CATEGORIA,
                new String[]{"idCategoria","nome"},
                null, null, null, null, "nome ASC");
    }

    public void updateVisitCount(String local, int newCount) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CHECKIN + " SET qtdVisitas = ? WHERE Local = ?",
                new Object[]{newCount, local});
    }

    public int getVisitCount(String local) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("Checkin",
                new String[]{"qtdVisitas"},
                "Local = ?", new String[]{local},
                null, null, null);
        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(c.getColumnIndex("qtdVisitas"));
        }
        c.close();
        return count;
    }

    public int getCategoryId(String nome) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("Categoria",
                new String[]{"idCategoria"},
                "nome = ?", new String[]{nome},
                null, null, null);
        int id = -1;
        if (c.moveToFirst()) {
            id = c.getInt(c.getColumnIndex("idCategoria"));
        }
        c.close();
        return id;
    }
}