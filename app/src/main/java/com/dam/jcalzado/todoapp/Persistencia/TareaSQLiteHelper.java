package com.dam.jcalzado.todoapp.Persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TareaSQLiteHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NOMBRE = "BDTareas";
    private static final String TABLA_TAREAS = "CREATE TABLE Tareas (id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, prioridad TEXT, estado TEXT, deadline TEXT)";

    public TareaSQLiteHelper(Context context) {
        super(context, DB_NOMBRE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_TAREAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminamos la versión anterior de la tabla.
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_TAREAS);
        // Se crea la nueva versión de la tabla.
        onCreate(db);
    }
}