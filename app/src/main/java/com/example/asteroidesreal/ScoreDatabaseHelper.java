package com.example.asteroidesreal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ScoreDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "scores.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_SCORES = "scores";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PUNTAJE = "puntaje";
    private static final String COLUMN_FECHA = "fecha";
    private static final String COLUMN_NOMBRE = "nombre";

    public ScoreDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_SCORES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PUNTAJE + " INTEGER NOT NULL, " +
                COLUMN_FECHA + " INTEGER NOT NULL, " +
                COLUMN_NOMBRE + " TEXT NOT NULL" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Agregar columna nombre si no existe
            db.execSQL("ALTER TABLE " + TABLE_SCORES + " ADD COLUMN " + COLUMN_NOMBRE + " TEXT DEFAULT 'Jugador'");
        }
    }

    public void insertScore(int puntaje, String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PUNTAJE, puntaje);
        values.put(COLUMN_FECHA, System.currentTimeMillis());
        values.put(COLUMN_NOMBRE, nombre);
        db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    public List<Score> getAllScores() {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORES,
                new String[]{COLUMN_ID, COLUMN_PUNTAJE, COLUMN_FECHA, COLUMN_NOMBRE},
                null, null, null, null,
                COLUMN_PUNTAJE + " DESC", "100"); // Top 100 puntajes

        if (cursor.moveToFirst()) {
            do {
                Score score = new Score(
                        cursor.getLong(0),
                        cursor.getInt(1),
                        cursor.getLong(2),
                        cursor.getString(3)
                );
                scores.add(score);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scores;
    }

    public int getHighestScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORES,
                new String[]{"MAX(" + COLUMN_PUNTAJE + ")"},
                null, null, null, null, null);
        int highestScore = 0;
        if (cursor.moveToFirst()) {
            highestScore = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return highestScore;
    }
}

