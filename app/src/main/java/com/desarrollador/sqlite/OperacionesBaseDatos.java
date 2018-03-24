package com.desarrollador.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.Nullable;

import com.desarrollador.modelo.AgregarRuta;
import com.desarrollador.modelo.Categoria;
import com.desarrollador.modelo.Coordenada;
import com.desarrollador.modelo.Foto;
import com.desarrollador.modelo.Market;
import com.desarrollador.modelo.Plano;

import com.desarrollador.sqlite.BaseDatosAyuda.Tablas;

import com.desarrollador.sqlite.Tablas_SQL.Marcadores;
import com.desarrollador.sqlite.Tablas_SQL.Fotos;
import com.desarrollador.sqlite.Tablas_SQL.Rutas;
import com.desarrollador.sqlite.Tablas_SQL.Coordenadas;
import com.desarrollador.sqlite.Tablas_SQL.Planos;
import com.desarrollador.sqlite.Tablas_SQL.ColumnCategoria;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Clase auxiliar que implementa a {@link BaseDatosAyuda} para llevar a cabo el CRUD
 * sobre las entidades existentes.
 */
public final class OperacionesBaseDatos {

    private BaseDatosAyuda bd_helper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
//    private static OperacionesBaseDatos instancia = new OperacionesBaseDatos();


    public OperacionesBaseDatos(Context c) {
        context=c;
    }
///revisar
    public OperacionesBaseDatos open() {
        if (bd_helper == null) {
            bd_helper = new BaseDatosAyuda(context);
        }
        sqLiteDatabase= bd_helper.getWritableDatabase();
        return this;
    }

    public void close(){

        bd_helper.close();
    }

    // [OPERACIONES_Planos]
    public Cursor obtenerPlanos() {
        SQLiteDatabase db = bd_helper.getReadableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        builder.setTables(PLANOS_JOIN_MARCADORES_Y_RUTAS);

        //el segundo valor al final (si es null proyecto el contenido de la tabla PLANOS luego del JOIN)
        return builder.query(db, null, null, null, null, null, null);
    }

    public Cursor obtenerPlanoPorTituloMarket(String tituloMarcadorPlano) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        String selection = String.format("%s=?", Planos.TITULO_MARKET);
        String[] selectionArgs = {tituloMarcadorPlano};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(PLANOS_JOIN_MARCADORES_Y_RUTAS);

        String[] proyeccion = {
                Tablas.PLANOS + "." + Planos.TITULO_MARKET,
                Planos.TITULO_RUTA,
                Rutas.TITULO_RUTA,
                Coordenadas.LATITUD,
                Coordenadas.LONGITUD};

        return builder.query(db, proyeccion, selection, selectionArgs, null, null, null);
    }

    public String insertarPlanos(Plano planoDraw) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Planos.TITULO_MARKET, planoDraw.tituloMarcador);
        valores.put(Planos.TITULO_RUTA, planoDraw.tituloRuta);

        // Insertar cabecera
        db.insertOrThrow(Tablas.PLANOS, null, valores);

        return (String) valores.get("Titulo");
    }

    public boolean actualizarPlano(Plano planoActual) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Planos.TITULO_MARKET, planoActual.tituloMarcador);
        valores.put(Planos.TITULO_RUTA, planoActual.tituloRuta);

        String whereClause = String.format("%s=?", Planos.TITULO_MARKET);
        String[] whereArgs = {planoActual.tituloMarcador};

        int resultado = db.update(Tablas.PLANOS, valores, whereClause, whereArgs);

        return resultado > 0;
    }

    public boolean eliminarPlano(String tituloMarcador) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        String whereClause = Planos.TITULO_MARKET + "=?";
        String[] whereArgs = {tituloMarcador};

        int resultado = db.delete(Tablas.PLANOS, whereClause, whereArgs);

        return resultado > 0;
    }
    // [/OPERACIONES_CABECERA_PEDIDO]

    // [OPERACIONES_MARCADORES]
    public Market getMarketById(int idMarket) {
        SQLiteDatabase db = bd_helper.getReadableDatabase();
        Market market = null;
        String selection = String.format("%s=?", Tablas_SQL.ColumnaMarket._ID);
        String[] selectionArgs = {String.valueOf(idMarket)};

        Cursor cursor = db.query(Tablas.MARCADOR, null, selection, selectionArgs, null, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(Tablas_SQL.ColumnaMarket._ID));
                String titulo = cursor.getString(cursor.getColumnIndex(Tablas_SQL.ColumnaMarket.TITULO));
                double latitud = cursor.getDouble(cursor.getColumnIndex(Tablas_SQL.ColumnaMarket.LATITUD));
                double longitud = cursor.getDouble(cursor.getColumnIndex(Tablas_SQL.ColumnaMarket.LONGITUD));
                String calles = cursor.getString(cursor.getColumnIndex(Tablas_SQL.ColumnaMarket.CALLES));
                String descripcion = cursor.getString(cursor.getColumnIndex(Tablas_SQL.ColumnaMarket.DESCRIPCION));
                String imagePath = cursor.getString(cursor.getColumnIndex(Tablas_SQL.ColumnaMarket.IMAGEN));
                Categoria cat = getCategoriaById(cursor.getInt(cursor.getColumnIndex(Tablas_SQL.ColumnaMarket.ID_CATEGORIA)));
                market = new Market(id, titulo, latitud, longitud, calles, descripcion, imagePath, cat);
            }
        }
        cursor.close();
        return market;
/*
        SQLiteDatabase db = bd_helper.getReadableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        builder.setTables(JOIN_MARCADORES_Y_FOTOS);
        String where ="Fotos.Titulo = Fotos.TituloMarket";
        String group_by ="Fotos.Titulo";
        //el segundo valor al final (si es null proyecto el contenido de la tabla PLANOS luego del JOIN)
        String[] whereArgs = {};//coordenadasMarket
        //.
        return builder.query(db, null, where, whereArgs, group_by, null, null);
        //String sql = String.format("SELECT * FROM %s", Tablas.MARCADOR);

//        return db.rawQuery(sql, null);
*/
    }

    public ArrayList<Market> ListaMarkets() {
        ArrayList<Market> listMarkets= new ArrayList<>();
//        String[] columns = new String[]{
//                Tablas_SQL.ColumnaMarket.TITULO,
//                Tablas_SQL.ColumnaMarket.LATITUD,
//                Tablas_SQL.ColumnaMarket.LONGITUD,
//                Tablas_SQL.ColumnaMarket.CALLES,
//                Tablas_SQL.ColumnaMarket.DESCRIPCION,
//                Tablas_SQL.ColumnaMarket.ID_CATEGORIA};
        Cursor cursor = sqLiteDatabase.query(Tablas.MARCADOR, null, null, null, null, null, null);
        cursor.moveToFirst();
        do {
            listMarkets.add(new Market(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),
                    cursor.getDouble(3), cursor.getString(4),
                    cursor.getString(5), cursor.getString(6), getCategoriaById(cursor.getInt(7))));
        }while(cursor.moveToNext());
        return listMarkets;
    }

    public Cursor getAllMarkets() {
        Cursor cursor = bd_helper.getReadableDatabase().query(Tablas.MARCADOR, null, null, null,
                null, null, null);
        return cursor;
    }

    public Cursor getMarkersByCategory(int idCategory) {
        String selection = String.format("%s=?", Tablas_SQL.ColumnaMarket.ID_CATEGORIA);
        String[] selectionArgs = {String.valueOf(idCategory)};
        Cursor cursor = bd_helper.getReadableDatabase().query(Tablas.MARCADOR, null, selection, selectionArgs,
                null, null, null);
        return cursor;
    }

    public long insertarMarket(Market market) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(Tablas_SQL.ColumnaMarket.TITULO,market.getTitulo());
        contentValues.put(Tablas_SQL.ColumnaMarket.LATITUD,market.getLatitud());
        contentValues.put(Tablas_SQL.ColumnaMarket.LONGITUD,market.getLongitud());
        contentValues.put(Tablas_SQL.ColumnaMarket.CALLES,market.getCalles());
        contentValues.put(Tablas_SQL.ColumnaMarket.DESCRIPCION,market.getDescripcion());
        contentValues.put(Tablas_SQL.ColumnaMarket.IMAGEN, market.getImagePath());
        contentValues.put(Tablas_SQL.ColumnaMarket.ID_CATEGORIA,market.getCategoria().getId());

        return sqLiteDatabase.insert(Tablas.MARCADOR,null,contentValues);
/*
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        // Generar Pk
//        String idProducto = com.cristian.sqlite.Tablas_SQL.Marcadores.generarIdProducto();

        valores.put(Marcadores.TITULO, market.titulo);
        valores.put(Marcadores.LATITUD, market.latitud);
        valores.put(Marcadores.LONGITUD, market.longitud);
        valores.put(Marcadores.CALLES, market.calles);
        valores.put(Marcadores.DESCRIPCION, market.descripcion);
//        return db.insert(Tablas.MARCADOR, null, valores);
        return db.insertOrThrow(Tablas.MARCADOR, null, valores) > 0 ? market.titulo : null;
*/
    }

    public boolean actualizarMarket(Market market) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Marcadores.TITULO, market.getTitulo());
        valores.put(Marcadores.LATITUD, market.getLatitud());
        valores.put(Marcadores.LONGITUD, market.getLongitud());
        valores.put(Marcadores.CALLES, market.getCalles());
        valores.put(Marcadores.DESCRIPCION, market.getDescripcion());

        String whereClause = String.format("%s=?", Marcadores.TITULO);
        String[] whereArgs = {market.getTitulo()};

        int resultado = db.update(Tablas.MARCADOR, valores, whereClause, whereArgs);

        return resultado > 0;
    }

    public boolean eliminarMarcador(String tituloMarket) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        String whereClause = String.format("%s=?", Marcadores.TITULO);
        String[] whereArgs = {tituloMarket};

        int resultado = db.delete(Tablas.MARCADOR, whereClause, whereArgs);

        return resultado > 0;
    }
    // [/OPERACIONES_MARCADORES]

    // [OPERACIONES_FOTOS]
    public Cursor obtenerFotos() {
        SQLiteDatabase db = bd_helper.getReadableDatabase();

        String sql = String.format("SELECT * FROM %s", Tablas.FOTOS);

        return db.rawQuery(sql, null);
    }

    public Cursor obtenerFotosPorTituloMarket(String Foto_x_TituloMarcador) {
        SQLiteDatabase db = bd_helper.getReadableDatabase();

        String sql = String.format("SELECT * FROM %s WHERE %s=?",
                Tablas.FOTOS, Planos.TITULO_MARKET);

        String[] selectionArgs = {Foto_x_TituloMarcador};

        return db.rawQuery(sql, selectionArgs);

    }

    public String insertarFoto(Foto foto) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Fotos.TITULO_MARKET, foto.tituloMarcador);
        valores.put(Fotos.FOTOS, foto.fotos);

        db.insertOrThrow(Tablas.FOTOS, null, valores);
//return String.format("%s#%d", foto.tituloMarcador, foto.fotos);
        return String.format("%s, %d", foto.tituloMarcador, foto.fotos);

    }

    public boolean actualizarFotos(Foto foto) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Fotos.TITULO_MARKET, foto.tituloMarcador);
        valores.put(Fotos.FOTOS, foto.fotos);

        String selection = String.format("%s=? AND %s=?",
                Fotos.TITULO_MARKET, Fotos.FOTOS);
        final String[] whereArgs = {foto.tituloMarcador, String.valueOf(foto.fotos)};
        //revisar de que forma se guardan para segun eso guardar/actualizar/eliminar
        int resultado = db.update(Tablas.FOTOS, valores, selection, whereArgs);

        return resultado > 0;
    }

    public boolean eliminarFoto(String tituloMarket, int secuencia) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        String selection = String.format("%s=?", Fotos.FOTOS);
        String[] whereArgs = {tituloMarket, String.valueOf(secuencia)};
        //revisar de que forma se guardan para segun eso guardar/actualizar/eliminar
        int resultado = db.delete(Tablas.FOTOS, selection, whereArgs);

        return resultado > 0;
    }
    // [/OPERACIONES_FOTOS]

    // [OPERACIONES_Rutas]
    public ArrayList<AgregarRuta> ListaRutas() {
        ArrayList<AgregarRuta> listMarkets= new ArrayList<>();
        String[] columns = new String[]{
                Tablas_SQL.ColumnaRutas.TITULO_RUTA
                };
        Cursor cursor = sqLiteDatabase.query(Tablas.RUTA,columns,null,null,null,null,null);
        cursor.moveToFirst();
        ArrayList<Coordenada> listCoordenadas= new ArrayList<>();
        listCoordenadas=ListaCoordenadas();
        ArrayList<LatLng> listpuntos= new ArrayList<>();
        do {
            listMarkets.add(new AgregarRuta(cursor.getString(0), listpuntos));
        }while(cursor.moveToNext());
        return listMarkets;
    }

    public Cursor obtenerRutas() {
        String[] columns = new String[]{
                Tablas_SQL.ColumnaRutas.TITULO_RUTA};
        Cursor cursor = sqLiteDatabase.query(Tablas.RUTA,columns,null,null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
/*
        SQLiteDatabase db = bd_helper.getReadableDatabase();

        String sql = String.format("SELECT * FROM %s", Tablas.RUTA);

        return db.rawQuery(sql, null);
*/
    }

    @Nullable
    public String insertarRuta(AgregarRuta ruta) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(Tablas_SQL.ColumnaRutas.TITULO_RUTA, ruta.tituloRuta);
        return sqLiteDatabase.insert(Tablas.RUTA,null,contentValues)> 0 ? ruta.tituloRuta : null;
/*
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Rutas.TITULO_RUTA, ruta.tituloRuta);

        return db.insertOrThrow(Tablas.RUTA, null, valores) > 0 ? ruta.tituloRuta : null;
*/
    }

    public boolean actualizarRuta(AgregarRuta ruta) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Rutas.TITULO_RUTA, ruta.tituloRuta);

        String whereClause = String.format("%s=?", Marcadores.TITULO);
        final String[] whereArgs = {ruta.tituloRuta, ruta.tituloRuta};

        int resultado = db.update(Tablas.MARCADOR, valores, whereClause, whereArgs);

        return resultado > 0;
    }

    public boolean eliminarRuta(String tituloRuta) {
        SQLiteDatabase db = bd_helper.getWritableDatabase();

        String whereClause = String.format("%s=?", Rutas.TITULO_RUTA);
        final String[] whereArgs = {tituloRuta};

        int resultado = db.delete(Tablas.RUTA, whereClause, whereArgs);

        return resultado > 0;
    }
    // [/OPERACIONES_Ruta]

    // [OPERACIONES_Coordenada]
    public ArrayList<Coordenada> ListaCoordenadas() {
        ArrayList<Coordenada> listPuntos= new ArrayList<>();
        String[] columns = new String[]{
                Tablas_SQL.ColumnaCoordenadas.LATITUD,
                Tablas_SQL.ColumnaCoordenadas.LONGITUD};
        //REVISAR la condicion para asociar a cada RUTA (mira si se agrega la columna TITULO_RUTA)
        Cursor cursor = sqLiteDatabase.query(Tablas.COORDENADAS,columns,null,null,null,null,null);
        cursor.moveToFirst();
        do {
            listPuntos.add(new Coordenada(cursor.getString(0), cursor.getDouble(1), cursor.getDouble(2)));
        }while(cursor.moveToNext());
        return listPuntos;
    }

    public Cursor Coordenadas() {
        String[] columns = new String[]{Tablas_SQL.ColumnaCoordenadas.ID_R,Tablas_SQL.ColumnaCoordenadas.TITULORUTA,
                Tablas_SQL.ColumnaCoordenadas.LATITUD,Tablas_SQL.ColumnaCoordenadas.LONGITUD};
        Cursor cursor = sqLiteDatabase.query(Tablas.COORDENADAS,columns,null,null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
/*
        SQLiteDatabase db = bd_helper.getReadableDatabase();

        String sql = String.format("SELECT * FROM %s", Tablas.COORDENADAS);

        return db.rawQuery(sql, null);
*/
    }

    public String insertarCoordenadas(Coordenada coordenadas) {
//        SQLiteDatabase db = bd_helper.getWritableDatabase();
        sqLiteDatabase = bd_helper.getWritableDatabase();

        // Generar Pk
//        String idFormaPago = Coordenada.tomarIdRuta();

        ContentValues valores = new ContentValues();
        valores.put(Coordenadas.TITULORUTA, coordenadas.tituloRuta);
        valores.put(Coordenadas.LATITUD, coordenadas.latitud);
        valores.put(Coordenadas.LONGITUD, coordenadas.longitud);

        return sqLiteDatabase.insertOrThrow(Tablas.COORDENADAS, null, valores) > 0 ? coordenadas.tituloRuta : null;
    }

    public boolean actualizarCoordenadas(Coordenada coordenadas) {
//        SQLiteDatabase db = bd_helper.getWritableDatabase();
        sqLiteDatabase = bd_helper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Coordenadas.TITULORUTA, coordenadas.tituloRuta);
        valores.put(Coordenadas.LATITUD, coordenadas.latitud);
        valores.put(Coordenadas.LONGITUD, coordenadas.longitud);

        String whereClause = String.format("%s=?", Coordenadas.TITULORUTA);
        String[] whereArgs = {coordenadas.tituloRuta};

        int resultado = sqLiteDatabase.update(Tablas.COORDENADAS, valores, whereClause, whereArgs);

        return resultado > 0;
    }

    public boolean eliminarCoordenas(String tituloRuta) {
//        SQLiteDatabase db = bd_helper.getWritableDatabase();
        sqLiteDatabase = bd_helper.getWritableDatabase();

        String whereClause = String.format("%s=?", Coordenadas.TITULORUTA);
        String[] whereArgs = {tituloRuta};

        int resultado = sqLiteDatabase.delete(Tablas.COORDENADAS, whereClause, whereArgs);
        if(resultado>0){
            eliminarRuta(tituloRuta);
        }

        return resultado > 0;
    }
    // [/OPERACIONES_COORDENADAS]


    public ArrayList<Categoria> getAllCategory() {
        SQLiteDatabase db = bd_helper.getReadableDatabase();
        ArrayList<Categoria> lista = new ArrayList<>();
//        String[] columns = new String[]{
//                ColumnCategoria._ID,
//                ColumnCategoria.NAME};

        Cursor cursor = db.query(Tablas.CATEGORIAS, null, null, null, null, null, null);

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Categoria ct = new Categoria(cursor.getInt(cursor.getColumnIndex(ColumnCategoria._ID)),
                        cursor.getString(cursor.getColumnIndex(ColumnCategoria.NAME)),
                        cursor.getString(cursor.getColumnIndex(ColumnCategoria.ICON)));
                lista.add(ct);
            }
            while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    public Categoria getCategoriaById(int idCategoria) {
        SQLiteDatabase db = bd_helper.getReadableDatabase();
        String selection = String.format("%s=?", ColumnCategoria._ID);
        String[] selectionArgs = {String.valueOf(idCategoria)};
        Categoria categoria = null;

        Cursor cursor = db.query(Tablas.CATEGORIAS, null, selection, selectionArgs, null, null, null);

        if(cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                categoria = new Categoria(cursor.getInt(cursor.getColumnIndex(ColumnCategoria._ID)),
                        cursor.getString(cursor.getColumnIndex(ColumnCategoria.NAME)),
                        cursor.getString(cursor.getColumnIndex(ColumnCategoria.ICON)));
            }
        }

        cursor.close();
        return categoria;
    }


    public SQLiteDatabase getDb() {
        return bd_helper.getWritableDatabase();
    }

    private static final String JOIN_MARCADORES_Y_FOTOS = "Marcadores " +
            "INNER JOIN Fotos " +
            "ON Marcadores.Titulo = Fotos.TituloMarket ";
    private static final String coordenadasMarket = "SELECT Latitud || \", \" || Longitud FROM " +
            "Coordenadas INNER JOIN Fotos ON Fotos.Titulo = Fotos.TituloMarket";

    private static final String PLANOS_JOIN_MARCADORES_Y_RUTAS = "Planos " +
            "INNER JOIN Marcadores " +
            "ON Planos.TituloMarket = Marcadores.Titulo " +
            "INNER JOIN Rutas " +
            "ON Planos.TituloRuta = Rutas.TituloRuta";

    //lo que va a proyecta de Marcadores, PLANOS y Rutas
    private final String[] proyJoinPlanoMarketRuta = new String[]{
            Tablas.PLANOS + "." + Planos.TITULO_RUTA,
            Planos.TITULO_MARKET,
            Rutas.TITULO_RUTA,
            Marcadores.TITULO};

}
