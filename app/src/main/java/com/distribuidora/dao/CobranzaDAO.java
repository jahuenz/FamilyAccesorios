package com.distribuidora.dao;

import java.util.ArrayList;
import java.util.List;
import com.distribuidora.model.Cobranza;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CobranzaDAO extends DBHelper {

    private SQLiteDatabase mDB;
    public static final String TABLA = "COBRANZA";

    public static final String ID               = "id";
    public static final String FECHA            = "fecha";
    public static final String IMPORTE          = "importe";
    public static final String ID_VALOR         = "id_valor";
    public static final String ITEM             = "item";
    public static final String FECHA_EMISION    = "fecha_emision";
    public static final String NUMERO           = "numero";
    public static final String FECHA_VENCIMIENTO = "fecha_vencimiento";
    public static final String NOTA             = "nota";
    public static final String ID_CLIENTE       = "id_cliente";
    public static final String ID_USUARIO       = "id_usuario";

    public static final int ID_INDEX               = 0;
    public static final int FECHA_INDEX            = 1;
    public static final int IMPORTE_INDEX          = 2;
    public static final int ID_VALOR_INDEX         = 3;
    public static final int ITEM_INDEX             = 4;
    public static final int FECHA_EMISION_INDEX    = 5;
    public static final int NUMERO_INDEX           = 6;
    public static final int FECHA_VENCIMIENTO_INDEX = 7;
    public static final int NOTA_INDEX             = 8;
    public static final int ID_CLIENTE_INDEX       = 9;
    public static final int ID_USUARIO_INDEX       = 10;

    public static final String CREATE =
        "CREATE TABLE " + TABLA + " (" +
        ID + " INTEGER PRIMARY KEY NOT NULL, " +
        FECHA + " TEXT, " +
        IMPORTE + " REAL, " +
        ID_VALOR + " INTEGER DEFAULT 0, " +
        ITEM + " INTEGER DEFAULT 1, " +
        FECHA_EMISION + " TEXT, " +
        NUMERO + " TEXT, " +
        FECHA_VENCIMIENTO + " TEXT, " +
        NOTA + " TEXT, " +
        ID_CLIENTE + " INTEGER NOT NULL, " +
        ID_USUARIO + " INTEGER NOT NULL, " +
        "FOREIGN KEY(" + ID_CLIENTE + ") REFERENCES CLIENTE (id) " +
        "FOREIGN KEY(" + ID_USUARIO + ") REFERENCES USUARIO (id))";

    public CobranzaDAO(Context context) {
        super(context);
        this.mDB = getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    private void abrirDB() {
        if (!mDB.isOpen()) {
            mDB = getWritableDatabase();
        }
    }

    public List<Cobranza> ObtenerCobros() {
        List<Cobranza> cobros = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLA;
        abrirDB();
        Cursor cursor = mDB.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cobros.add(cursorACobranza(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        mDB.close();
        return cobros;
    }

    public Cobranza obtenerCobro(long id) {
        String selectQuery = "SELECT * FROM " + TABLA + " WHERE id=" + id;
        abrirDB();
        Cursor cursor = mDB.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Cobranza cobro = cursorACobranza(cursor);
        cursor.close();
        mDB.close();
        return cobro;
    }

    private Cobranza cursorACobranza(Cursor cursor) {
        Cobranza cobro = new Cobranza();
        cobro.setId(cursor.getLong(ID_INDEX));
        cobro.setFecha(cursor.getString(FECHA_INDEX));
        cobro.setImporte(cursor.getDouble(IMPORTE_INDEX));
        cobro.setId_valor(cursor.getInt(ID_VALOR_INDEX));
        cobro.setItem(cursor.getInt(ITEM_INDEX));
        cobro.setFecha_emision(cursor.getString(FECHA_EMISION_INDEX));
        cobro.setNumero(cursor.getString(NUMERO_INDEX));
        cobro.setFecha_vencimiento(cursor.getString(FECHA_VENCIMIENTO_INDEX));
        cobro.setNota(cursor.getString(NOTA_INDEX));
        cobro.setId_cliente(cursor.getInt(ID_CLIENTE_INDEX));
        cobro.setId_usuario(cursor.getInt(ID_USUARIO_INDEX));
        return cobro;
    }
}
