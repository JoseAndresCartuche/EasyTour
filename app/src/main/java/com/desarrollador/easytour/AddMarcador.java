package com.desarrollador.easytour;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.desarrollador.modelo.Foto;
import com.desarrollador.modelo.Market;
import com.desarrollador.sqlite.OperacionesBaseDatos;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

//para ACTUALIZACION de localizacion cada cierto tiempo (OnCreate o onStart,)
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddMarcador extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String LOGTAG = "android-localizacion";

    private Location lastLocation;
    private GoogleApiClient apiClient;
    private EditText titulo;
    private EditText latitud;
    private EditText longitud;
    private EditText calle;
    private EditText descrip;
    private Button btnActUbicacion;
    private Button btnGuardarMarket;

    private OperacionesBaseDatos manejo;
    int cont=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marcador);

        apiClient = new GoogleApiClient.Builder(this)
                   .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        titulo = (EditText) findViewById(R.id.txtTitulo);
        latitud = (EditText) findViewById(R.id.txtLatitud);
        longitud = (EditText) findViewById(R.id.txtLongitud);
        calle = (EditText) findViewById(R.id.txtCalles);
        descrip = (EditText) findViewById(R.id.txtDescrip);
        btnActUbicacion = (Button) findViewById(R.id.btnLocalizar);
        btnGuardarMarket = (Button) findViewById(R.id.btnStopSave);

        // Construimos el GoogleApi client para encontrar la CALLE
        buildGoogleApiClient();

        manejo = new OperacionesBaseDatos(AddMarcador.this);
        manejo.open();

        //Implementamos el evento click del botón
        btnActUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });

        //Implementamos el evento click del botón
        btnGuardarMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titulo.getText().toString();
                Double lat = Double.parseDouble(latitud.getText().toString());
                Double longi = Double.parseDouble(longitud.getText().toString());
                String calles = calle.getText().toString();
                String descr = descrip.getText().toString().replace('\n', ' ');
                Market nuevo = new Market(title, lat, longi, calles, descr);
                String resul= "";
                resul= manejo.insertarMarket(nuevo);
                if(resul != null){
                    Toast.makeText(AddMarcador.this, resul +"\nMarcador Guardado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AddMarcador.this, "NO REGISTRADO!!!", Toast.LENGTH_LONG).show();
                }
                limpiar();
                manejo.close();

            }
        });
    }

    private void mockData(int c) {
        manejo.getDb().beginTransaction();

        ArrayList<Market> listaMarkets = new ArrayList<>();
//        listaMarkets.add(new Market("Mitad del Mundo", -0.002228, -78.455847,"Manuel Cordova Galarza", "Descripcion texto full"));
//        listaMarkets.add(new Market("Monumento a Simón Bolivar", -3.994906, -79.204753, "18 de Noviembre", "Descripcion texto full"));
//        listaMarkets.add(new Market("Monumento a Bernardo Valdivieso", -3.996690, -79.201663, "18 de Noviembre", "Descripcion texto full"));
        listaMarkets.add(new Market("Iglesia Catedral de Loja",-3.996502, -79.201101, "Bernardo Valdivieso", "Descripcion texto full"));
        listaMarkets.add(new Market("Puerta de la ciudad", -3.989611, -79.204002, "Avenida Universitaria","Descripcion texto full"));
        listaMarkets.add(new Market("Zoológico de Loja", -3.957685, -79.217387, "Avenida 8 de Diciembre","Descripcion texto full"));
        listaMarkets.add(new Market("Parque Recreacional Jipiro", -3.971925, -79.203506, "Velsco Ibarra","Descripcion texto full"));
        listaMarkets.add(new Market("Plaza de la Independencia \"San Sebastian\"", -4.001565, -79.201324, "Calle Mercadillo","Descripcion texto full"));
        listaMarkets.add(new Market("Iglesia de San Sebastian", -4.002117, -79.201486, "Bolivar","Descripcion texto full"));
        listaMarkets.add(new Market("Parque Infantil - Bernabé Ruiz", -4.004924, -79.200322, "González Suárez","Descripcion texto full"));

        ArrayList<Foto> listFotos = new ArrayList<>();
        listFotos.add(new Foto(listaMarkets.get(1).titulo, "Mitad del Mundo.jpg"));
        listFotos.add(new Foto(listaMarkets.get(2).titulo, "monumento Simón Bolivar.jpg"));
        listFotos.add(new Foto(listaMarkets.get(3).titulo, "Monumento a Bernardo Valdivieso.jpg"));
        listFotos.add(new Foto(listaMarkets.get(4).titulo, "Catedral de Loja.jpg"));
        listFotos.add(new Foto(listaMarkets.get(5).titulo, "Puerta de la ciudad.jpg"));
        listFotos.add(new Foto(listaMarkets.get(6).titulo, "Zoológico de Loja.jpg"));
        listFotos.add(new Foto(listaMarkets.get(7).titulo, "Parque Recreacional Jipiro.jpg"));
        listFotos.add(new Foto(listaMarkets.get(8).titulo, "Plaza-San-Sebastian-Loja.jpg"));
        listFotos.add(new Foto(listaMarkets.get(9).titulo, "Iglesia de San Sebastian.jpg"));
        listFotos.add(new Foto(listaMarkets.get(10).titulo, "Parque Infantil - Bernabé Ruiz.jpg"));

        if(c==1){
            for (Market elemento:listaMarkets) {
                String result =manejo.insertarMarket(elemento);

                if(result != null){
                    Toast.makeText(AddMarcador.this, "Marcador Guardado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AddMarcador.this, "NO REGISTRADO!!! Markets Prueba "+ elemento.titulo, Toast.LENGTH_LONG).show();
                    break;
                }
            }
            /*
            for (Foto elemento:listFotos) {
                String result =manejo.insertarFoto(elemento);
                if(result != null){
                    Toast.makeText(AddMarcador.this, "Foto Guardado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AddMarcador.this, "NO REGISTRADO!!! Foto Prueba", Toast.LENGTH_LONG).show();
                    break;
                }
            }
            */
        }else{
            System.out.println("NO REGISTRADO!!! Foto Prueba");
            Toast.makeText(AddMarcador.this, "Marcadores Definidos NO Guardados", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.
        Log.e(LOGTAG, "La conexión de GPS falló");
        Log.i(LOGTAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Conectado correctamente a Google Play Services
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {
            if(apiClient.isConnected()){
                Log.d("ApiCliente", "Connected");
            }
            else{
                Log.d("ApiCliente", "Disconnected");
            }
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            updateUI();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Goog le Play Services
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        apiClient.connect();
    }

    private void updateUI() {
        if (lastLocation != null) {
            latitud.setText(String.valueOf(lastLocation.getLatitude()));
            longitud.setText(String.valueOf(lastLocation.getLongitude()));
            this.setStreetLocation(lastLocation);
            Log.d("Location", "Created");
        } else {
            latitud.setText("Desconocida");
            longitud.setText("Desconocida");
            Log.d("Location", "No se pudo obtener la ubicación. Asegúrese de que la ubicación está habilitada en el dispositivo");
        }
    }

    private void limpiar() {
        titulo.setText("");
        latitud.setText("Desconocida");
        longitud.setText("Desconocida");
        calle.setText("");
        descrip.setText("");

    }

    private void setStreetLocation(Location loc) {
        //Obtener la direcci—n de la calle a partir de la latitud y la longitud
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (!list.isEmpty()) {
                Address address = list.get(0);
                calle.setText(address.getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Creating google api client object
     * Para obtener la dirreccion
     * */
    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(apiClient != null){
            Log.d("ApiCliente", "Created");
        }
        else{
            Log.d("ApiCliente", "Null");
        }
    }

    /**
     * Método para verificar los servicios de Google Play en el dispositivo
     * */
    /*
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);
                this.lastLocation = lastLocation;
                if (lastLocation != null) {
                    Log.d("Location", "Created");
                } else {
                    Log.d("Location", "No se pudo obtener la ubicación. Asegúrese de que la ubicación está habilitada en el dispositivo");
                }
                updateUI();
            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.
                Toast.makeText(AddMarcador.this, "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onStart() {
        apiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        apiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        apiClient.connect();
        super.onRestart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //checkPlayServices();
    }


}

