package com.desarrollador.modelo;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desarrollador on 3/1/17.
 */

public class AgregarRuta {
    public String tituloRuta;
    public ArrayList<LatLng> points = new ArrayList<>();
    //agregar una lista de coordenadas (LatLang)

    public AgregarRuta(String tituloRuta, ArrayList<LatLng> coordenadas) {
        this.tituloRuta=tituloRuta;
        points=coordenadas;
    }

}
