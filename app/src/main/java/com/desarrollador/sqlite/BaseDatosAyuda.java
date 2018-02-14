package com.desarrollador.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.desarrollador.sqlite.Tablas_SQL.Marcadores;
import com.desarrollador.sqlite.Tablas_SQL.Fotos;
import com.desarrollador.sqlite.Tablas_SQL.Rutas;
import com.desarrollador.sqlite.Tablas_SQL.Coordenadas;
import com.desarrollador.sqlite.Tablas_SQL.Planos;


/**
 * Clase que administra la conexión de la base de datos y su estructuración
 */
public class BaseDatosAyuda extends SQLiteOpenHelper {

    private static final String NOMBRE_BASE_DATOS = "EasyTour.db";

    private static final int VERSION_ACTUAL = 1;
    private BaseDatosAyuda ayuda;
    private SQLiteDatabase sqLiteDatabase;

    private final Context contexto;

    interface Tablas {
        String PLANOS = "Planos";
        String FOTOS = "Fotos";
        String MARCADOR = "Marcadores";
        String RUTA = "Rutas";
        String COORDENADAS = "Coordenas";
    }

    interface Referencias {
        String TITULO_MARKET = String.format("REFERENCES %s(%s)",
                Tablas.MARCADOR, Marcadores.TITULO);

        String TITULO_RUTA = String.format("REFERENCES %s(%s)",
                Tablas.RUTA, Rutas.TITULO_RUTA);
    }

    public BaseDatosAyuda(Context contexto) {
        super(contexto, NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }


    public void onOpen() {
        ayuda = new BaseDatosAyuda(contexto);
        sqLiteDatabase=ayuda.getWritableDatabase();
/*
        super.onOpen(sqLiteDatabase);
        if (!sqLiteDatabase.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sqLiteDatabase.setForeignKeyConstraintsEnabled(true);
            } else {
                sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
            }
        }
*/
    }

    public void onClose(SQLiteDatabase db){
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                "%s TEXT PRIMARY KEY," +
                "%s NUMERIC UNIQUE NOT NULL," +
                "%s NUMERIC UNIQUE NOT NULL," +
                "%s TEXT NOT NULL," +
                "%s TEXT NOT NULL)",
                Tablas.MARCADOR, Marcadores.TITULO, Marcadores.LATITUD, Marcadores.LONGITUD, Marcadores.CALLES, Marcadores.DESCRIPCION));

        db.execSQL("INSERT INTO "+Tablas.MARCADOR +"("+Marcadores.TITULO+","+Marcadores.LATITUD
                +","+Marcadores.LONGITUD+","+Marcadores.CALLES+","+Marcadores.DESCRIPCION+") VALUES" +
                "('Mitad del Mundo', -0.002228, -78.455847,'Manuel Cordova Galarza', 'Descripcion texto full')");

        db.execSQL("INSERT INTO "+Tablas.MARCADOR +"("+Marcadores.TITULO+","+Marcadores.LATITUD
                +","+Marcadores.LONGITUD+","+Marcadores.CALLES+","+Marcadores.DESCRIPCION+") VALUES" +
                "('Monumento a Simón Bolivar', -3.994906, -79.204753, '18 de Noviembre', 'Descripcion texto full')");

        db.execSQL("INSERT INTO "+Tablas.MARCADOR +"("+Marcadores.TITULO+","+Marcadores.LATITUD
                +","+Marcadores.LONGITUD+","+Marcadores.CALLES+","+Marcadores.DESCRIPCION+") VALUES" +
                "('Monumento a Bernardo Valdivieso', -3.996690, -79.201663, '18 de Noviembre', 'Descripcion texto full')");

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                "%s TEXT NOT NULL," +
                "%s TEXT NOT NULL " +
//                " CONSTRAINT `FK_Market`\n" +
//                " FOREIGN KEY (`TituloMarker`)\n" +
                " %s)",
                Tablas.FOTOS, Fotos.TITULO_MARKET, Fotos.FOTOS, Referencias.TITULO_MARKET));

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                "%s TEXT PRIMARY KEY NOT NULL)",
                Tablas.RUTA, Rutas.TITULO_RUTA));

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                "%s TEXT NOT NULL," +
                "%s NUMERIC NOT NULL," +
                "%s NUMERIC NOT NULL " +
//                "CONSTRAINT `FK_Ruta`\n" +
//                " FOREIGN KEY (`TituloRuta`)\n" +
//                " REFERENCES `Rutas` (`TituloRuta`)\n" +
//                " ON DELETE NO ACTION\n" +
//                " ON UPDATE NO ACTION)",
//                "%s)",
                ")",
//                Tablas.COORDENADAS, Coordenadas.TITULORUTA, Coordenadas.LATITUD, Coordenadas.LONGITUD, Referencias.TITULO_RUTA));
                Tablas.COORDENADAS, Coordenadas.TITULORUTA, Coordenadas.LATITUD, Coordenadas.LONGITUD));
// %s(%s)

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                "%s TEXT UNIQUE, " +
                "%s TEXT UNIQUE " +
//                " CONSTRAINT `FK_Markets`\n" +
//                " FOREIGN KEY (`TituloMarket`)\n" +
//                " REFERENCES `Marcadores` (`Titulo`)\n" +
//                " ON DELETE NO ACTION\n" +
//                " ON UPDATE NO ACTION,\n" +

//                " CONSTRAINT `FK_Rutas`\n" +
//                " FOREIGN KEY (`TituloRuta`)\n" +
//                " REFERENCES `Rutas` (`TituloRuta`)\n" +
//                " ON DELETE NO ACTION\n" +
//                " ON UPDATE NO ACTION)",
//                "%s, %s)",
                ")",
//                Tablas.PLANOS, Planos.TITULO_MARKET, Planos.TITULO_RUTA, Referencias.TITULO_MARKET, Referencias.TITULO_RUTA));
                Tablas.PLANOS, Planos.TITULO_MARKET, Planos.TITULO_RUTA));


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Tablas.MARCADOR);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.FOTOS);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.RUTA);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.COORDENADAS);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.PLANOS);
        onCreate(db);
    }

}