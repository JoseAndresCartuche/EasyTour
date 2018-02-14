package com.desarrollador.easytour;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.desarrollador.modelo.Market;
import com.desarrollador.sqlite.OperacionesBaseDatos;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapaEncontrado extends FragmentActivity implements OnMapReadyCallback {

    private static final String LOGTAG = "android-localizacion";
    private GoogleMap mMap;
    private UiSettings mUiSettings;

    private ArrayList<Market> listMarkets = new ArrayList<>();
    private ArrayList<LatLng> puntos = new ArrayList<>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //VISUALIZACION BASEEEEEEEE
    private OperacionesBaseDatos visualizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_encontrado);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        visualizacion = new OperacionesBaseDatos(this);
        visualizacion.open();
        //Cursor cursor = visualizacion.obtenerMarkets();
        listMarkets = visualizacion.ListaMarkets();
        if(listMarkets.isEmpty()==true){
            Toast.makeText(MapaEncontrado.this, "Sin registro", Toast.LENGTH_LONG).show();
            visualizacion.close();
        }else{
            visualizacion.close();
        }
        //Nos aseguramos de que existe al menos un registro
/*
        if (cursor !=null) {
            if(cursor.moveToFirst()){
                listMarkets.add(new Market(cursor.getString(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4)));
            }
            for(Market elemento: listMarkets){
                LatLng localizado = new LatLng(elemento.latitud, elemento.longitud);
                Log.d("Marcadores---", elemento.titulo +", "+localizado.latitude+ ", "+ localizado.longitude);
            }

            visualizacion.close();
            cursor.close();
        }else{
            Toast.makeText(MapaEncontrado.this, "Sin registro", Toast.LENGTH_LONG).show();
            visualizacion.close();
            cursor.close();
        }
*/
        //mostrarRuta();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //BOTON ATRAS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();


        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Log.e(LOGTAG, "Se requiere permiso del gps del usuario para mostrar la ubicación");
        }*/

        //Controles UI
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        //mUiSettings.setMyLocationButtonEnabled(true); //activa boton ubicacion actual

        //Coordenadas del AddMarcador
        if(listMarkets.isEmpty()==false){
            for(Market elemento: listMarkets){
                LatLng localizado = new LatLng(elemento.latitud, elemento.longitud);
                Log.d("Marcadores", localizado.latitude+ ", "+ localizado.longitude);
                MarkerOptions marcador = new MarkerOptions()
                        .title(elemento.titulo)
                        .position(localizado)
                        .snippet("Calle: " + elemento.calles
                                +"Descripción: " + elemento.descripcion);
                mMap.addMarker(marcador);
            CameraUpdate mCamara = CameraUpdateFactory.newLatLng(localizado);
            mMap.moveCamera(mCamara);
                //mMap.animateCamera(CameraUpdateFactory.zoomIn());
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizado, 8));//15
                //CENTRA el marcador en el mapa y con nivel de... 18
            }
        }

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(localizado));
        //todo el mapa ubica en centro el marcador

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Log.e(LOGTAG, "Se requiere permiso del gps del usuario para mostrar la ubicación");
        }
    }

    /*
    private void setMarker(LatLng position, String titulo, String info) {
        // Agregamos marcadores para indicar sitios de interéses.
        Marker myMaker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(titulo)  //Agrega un titulo al marcador
                .snippet(info)   //Agrega información detalle relacionada con el marcador
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))); //Color del marcador
    }
    */

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MapaEncontrado Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    /**
     * Debe mostrar una lina por ruta por lo que en algun punto se debe restablecer
     */
    private void mostrarRuta()
    {
        //Dibujo con Lineas
        PolylineOptions lineas = new PolylineOptions();
        lineas.addAll(puntos);
        lineas.width(3);
        lineas.color(Color.RED);
//revisar los puntos agregados
        mMap.addPolyline(lineas);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


}
