package com.desarrollador.sqlite;

import java.util.UUID;

/**
 * Clase que establece los latitud a usar en la base de datos
 */
public class Tablas_SQL {
    interface ColumnaMarket {
        String TITULO = "Titulo";
        String LATITUD = "Latitud";
        String LONGITUD = "Longitud";
        String CALLES = "Calles";
        String DESCRIPCION = "Descripcion";
    }

    interface ColumnasFotos {
        String TITULO_MARKET = "TituloMarket";
        String FOTOS = "Foto";
    }

    interface ColumnaRutas {
        String TITULO_RUTA = "TituloRuta";
    }

    interface ColumnaCoordenadas {
        String ID_R = "ID_RutaR";
        String TITULORUTA = "TituloRuta";
        String LATITUD = "Latitud";
        String LONGITUD = "Longitud";
    }

    interface ColumnaPlanos {
        String TITULO_MARKET = "TituloMarket";
        String TITULO_RUTA = "TituloRuta";
    }

    public static class Marcadores implements ColumnaMarket {
        public static String generarIdProducto() {
            return "PRO-" + UUID.randomUUID().toString();
        }
    }

    public static class Fotos implements ColumnasFotos {
        // Métodos auxiliares
    }

    public static class Rutas implements ColumnaRutas {
        public static String generarIdRUTAS() {
            return "CLI-" + UUID.randomUUID().toString();
        }
    }

    public static class Coordenadas implements ColumnaCoordenadas {
        public static String tomarIdRuta() {
            return "FP-" + Rutas.generarIdRUTAS();
        }
    }

    public static class Planos implements ColumnaPlanos {
        public static String generarIdPlanos() {
            return "CP-" + UUID.randomUUID().toString();
        }
    }

    private Tablas_SQL() {
    }

}
