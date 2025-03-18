package com.example.rompecabezas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DBRecords extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "records.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_RECORDS = "records";

    public DBRecords(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_RECORDS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, tiempo INTEGER, imagen TEXT, fecha TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    public void insertarTiempo(long tiempo, String imagenUri, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tiempo", tiempo);
        values.put("imagen", imagenUri);
        values.put("fecha", fecha);
        db.insert(TABLE_RECORDS, null, values);
        db.close();
    }

    public ArrayList<Record> obtenerTiempos() {
        ArrayList<Record> tiempos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, tiempo, imagen, fecha FROM " + TABLE_RECORDS + " ORDER BY tiempo ASC", null);

        if (cursor.moveToFirst()) {
            do {
                tiempos.add(new Record(cursor.getInt(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tiempos;
    }

    public void eliminarTiempo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}