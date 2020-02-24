package com.dav.appregistrodatospollos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {//name table ventas
        sqLiteDatabase.execSQL("CREATE TABLE tb_detalles("+
                "Id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "fecha TEXT,"+
                "granja TEXT,"+
                "galpon TEXT,"+
                "galponero TEXT,"+
                "mortalidad TEXT,"+
                "alimento TEXT,"+
                "peso TEXT,"+
                "veterinario TEXT"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void borrarRegistros(SQLiteDatabase db) {
        db.execSQL("DELETE FROM tb_detalles");
    }
}
