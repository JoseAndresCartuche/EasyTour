package com.desarrollador.sqlite;

import android.provider.BaseColumns;

import java.util.UUID;

/**
 * Clase que establece los latitud a usar en la base de datos
 */
public class Tablas_SQL {
    public static abstract class ColumnaMarket implements BaseColumns {
        public static final String TITULO = "Titulo";
        public static final String LATITUD = "Latitud";
        public static final String LONGITUD = "Longitud";
        public static final String CALLES = "Calles";
        public static final String DESCRIPCION = "Descripcion";
        public static final String IMAGEN = "Imagen";
        public static final String ID_CATEGORIA = "id_categoria";
    }

    public static abstract class ColumnasFotos implements BaseColumns {
        public static final String TITULO_MARKET = "TituloMarket";
        public static final String FOTOS = "Foto";
    }

    public static abstract class ColumnaRutas implements BaseColumns {
        public static final String TITULO_RUTA = "TituloRuta";
    }

    public static abstract class ColumnaCoordenadas implements BaseColumns {
        public static final String ID_R = "ID_RutaR";
        public static final String TITULORUTA = "TituloRuta";
        public static final String LATITUD = "Latitud";
        public static final String LONGITUD = "Longitud";
    }

    public static abstract class ColumnaPlanos {
        public static final String TITULO_MARKET = "TituloMarket";
        public static final String TITULO_RUTA = "TituloRuta";
    }

    public static abstract class ColumnCategoria implements BaseColumns {
        public static final String NAME = "Name";
        public static final String ICON = "Icon";
    }

    public static class Marcadores extends ColumnaMarket {
        public static String generarIdProducto() {
            return "PRO-" + UUID.randomUUID().toString();
        }
    }

    public static class Fotos extends ColumnasFotos {
        // Métodos auxiliares
    }

    public static class Rutas extends ColumnaRutas {
        public static String generarIdRUTAS() {
            return "CLI-" + UUID.randomUUID().toString();
        }
    }

    public static class Coordenadas extends ColumnaCoordenadas {
        public static String tomarIdRuta() {
            return "FP-" + Rutas.generarIdRUTAS();
        }
    }

    public static class Planos extends ColumnaPlanos {
        public static String generarIdPlanos() {
            return "CP-" + UUID.randomUUID().toString();
        }
    }

    private Tablas_SQL() {
    }

}
