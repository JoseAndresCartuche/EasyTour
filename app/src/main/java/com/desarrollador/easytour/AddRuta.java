package com.desarrollador.easytour;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarrollador.modelo.AgregarRuta;
import com.desarrollador.modelo.Coordenada;
import com.desarrollador.sqlite.OperacionesBaseDatos;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static com.desarrollador.easytour.Constants.lTiempo_Intervalo;

public class AddRuta extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<Status> {

    private static final String TAG = AddRuta.class.getSimpleName();

    private static final String LOCATION_KEY = "location-key";
    private static final String ACTIVITY_KEY = "activity-key";

    // Location API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mLastLocation;

    // Activity Recognition API
    private ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    @DrawableRes
    private int mImageResource = R.drawable.ic_question;

    // UI
    private TextView mLatitud;
    private TextView mLongitud;
    private TextView tCoordenadas;
    private ImageView mDectectedActivityIcon;

    // Códigos de petición
    public static final int REQUEST_LOCATION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 2;

    private EditText titulo;
    private Button btnGuardar;

    private OperacionesBaseDatos manejo;

    ArrayList<LatLng> points = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ruta);

        titulo = (EditText) findViewById(R.id.txtTitulo);
        // Referencias UI
        mLatitud = (TextView) findViewById(R.id.txtLatitud);
        mLongitud = (TextView) findViewById(R.id.txtLongitud);
        tCoordenadas = (TextView) findViewById(R.id.txtCoordenadas);
        mDectectedActivityIcon = (ImageView) findViewById(R.id.iv_activity_icon);
        btnGuardar = (Button) findViewById(R.id.btnStopSave);

        // Establecer punto de entrada para la API de ubicación
        buildGoogleApiClient();

        // Crear configuración de peticiones
        createLocationRequest();

        // Crear opciones de peticiones
        buildLocationSettingsRequest();

        // Verificar ajustes de ubicación actuales
        checkLocationSettings();

        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        //Centramos el texto de la caja de coordenadas
        tCoordenadas.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tCoordenadas.setText("Latitud\t\t\tLongitud\n");
        updateValuesFromBundle(savedInstanceState);

        manejo = new OperacionesBaseDatos(AddRuta.this);
        manejo.open();

        //Implementamos el evento click del botón
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //parar y guardar
                stopLocationUpdates();
                guardarRuta();
                limpiar();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Protegemos la ubicación actual antes del cambio de configuración
        outState.putParcelable(LOCATION_KEY, mLastLocation);
        outState.putInt(ACTIVITY_KEY, mImageResource);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "El usuario permitió el cambio de ajustes de ubicación.");
                        processLastLocation();
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, "El usuario no permitió el cambio de ajustes de ubicación");
                        break;
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_LONG).show();
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .enableAutoManage(this, this)
                .build();
    }

    /**
     * Metodo con el cual se asigna el intervalo para el SEGUIMIENTO
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(lTiempo_Intervalo)
                .setFastestInterval(Constants.lActualizar_Intervalo_Fastest)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                .setSmallestDisplacement(200);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient, mLocationSettingsRequest
                );

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Los ajustes de ubicación satisfacen la configuración.");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.d(TAG, "Los ajustes de ubicación no satisfacen la configuración. " +
                                    "Se mostrará un diálogo de ayuda.");
                            status.startResolutionForResult(
                                    AddRuta.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "El Intent del diálogo no funcionó.");
                            // Sin operaciones
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Los ajustes de ubicación no son apropiados.");
                        break;
                }
            }
        });
    }

    /**
     * @param savedInstanceState
     * Almacena los valores de la localización y reconocimiento de la actividad
     * Cuando el dispositivo esta bloquedado o la pantalla gira se debe almacenar los datos
     * Y eso es lo que hace este metodo
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //actualizacion de la ubicación
            if (savedInstanceState.containsKey(LOCATION_KEY)) {
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
                updateLocationUI();
            }
            //reconocimiento de la actividad
            if (savedInstanceState.containsKey(ACTIVITY_KEY)) {
                mImageResource = savedInstanceState.getInt(ACTIVITY_KEY);
                updateRecognitionUI();
            }
        }
    }

    /**
     * Metodo con el cual se trae la localización
     */
    private void updateLocationUI() {
        mLatitud.setText(String.valueOf(mLastLocation.getLatitude()));
        mLongitud.setText(String.valueOf(mLastLocation.getLongitude()));

        Double latitud, longitud;
        latitud = Double.valueOf((String) mLatitud.getText());
        longitud = Double.valueOf((String) mLongitud.getText());
        points=puntosRuta(latitud, longitud);
    }

    /**
     * Metodo que identifica la aactividad
     */
    private void updateRecognitionUI() {
        //rconocimiento de la actividad
        mDectectedActivityIcon.setImageResource(mImageResource);
    }

    private void stopActivityUpdates() {
        //detiene el reconocimiento
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Metodo con el cual se detiene la actualización de la localización
     */
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi
                .removeLocationUpdates(mGoogleApiClient, this);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void getLastLocation() {
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            } else {
                if(mGoogleApiClient.isConnected()){
                    Log.d("ApiCliente", "Connected");
                }
                else{
                    Log.d("ApiCliente", "Disconnected");
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//                puntosRuta(mLastLocation);
            }
        } else {
            manageDeniedPermission();
        }
    }

    private void processLastLocation() {
        getLastLocation();
        if (mLastLocation != null) {
            updateLocationUI();
        }
    }

    private void startActivityUpdates() {
        //empieza la con el reconocimiento de la ACTIVIDAD
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.lIntervalo_Reconocimiento_Actividad,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    private void startLocationUpdates() {
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            } else {
                if(mGoogleApiClient.isConnected()){
                    Log.d("ApiCliente", "Connected");
                }
                else{
                    Log.d("ApiCliente", "Disconnected");
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }

        } else {
            manageDeniedPermission();
        }
    }

    /**
     * Metodo que se usa para los permisos denegados
     * Controla la solicitud del permiso en el ELSE
     */
    private void manageDeniedPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Aquí muestras confirmación explicativa al usuario
            // por si rechazó los permisos anteriormente
            Toast.makeText (AddRuta.this,"Ud. no ha permitido el servicio de localización.",Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Obtenemos la última ubicación al ser la primera vez
        processLastLocation();
        // Iniciamos las actualizaciones de ubicación
        startLocationUpdates();
        // Y también las de reconocimiento de actividad
        startActivityUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Conexión suspendida");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Error de conexión con el código:" + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, String.format("Nueva ubicación: Lat %s, Lon %s",
                location.getLatitude(), location.getLongitude()));
        mLastLocation = location;
        updateLocationUI();
    }


    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "Detección de actividad iniciada");

        } else {
            Log.e(TAG, "Error al iniciar/remover la detección de actividad: "
                    + status.getStatusMessage());
        }
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(Constants.ACTIVITY_KEY, -1);
            mImageResource = Constants.getActivityIcon(type);
            updateRecognitionUI();
        }

    }

    /**
     * @param lttd
     * @param lngtd
     * @return List <LatLng>
     * Con esta función se prevee dibujar la agregar_ruta seguida por el usuario
     */
    //revisar los Datos que recoge (5 min) tiempo en Constants
    public ArrayList<LatLng> puntosRuta(Double lttd, Double lngtd) {
      //lati--longi
        points.add(new LatLng(lttd, lngtd));
        tCoordenadas.setText("Latitud\t\t\tLongitud\n");
        for (LatLng coordenada: points) {
            //revisar cuantas veces agrega... la nueva posicion con respecto a la(s) anterior(es)
            //o agregar los datos a tCoordenadas cuando RETORNA
            tCoordenadas.setText(tCoordenadas.getText()+""+coordenada.latitude + "\t\t\t"+coordenada.longitude+"\n");
        }
        return points;
    }

    public void guardarRuta(){
//        BaseDatosAyuda bda = new BaseDatosAyuda(AddRuta.this);
//        SQLiteDatabase db = null;
//        bda.onOpen(db);
        //GUARDAMOS el marcador
//        OperacionesBaseDatos manejo = new OperacionesBaseDatos(AddRuta.this);
//        manejo.getDb().beginTransaction();
        String title = titulo.getText().toString();
        for (LatLng coordenada: points) {
            Log.d(TAG + "Puntos a guardar", coordenada.latitude+ ", "+ coordenada.longitude);
        }
        AgregarRuta nuevo = new AgregarRuta(title, points);
        String coor="";
        String resul= manejo.insertarRuta(nuevo);
        if(resul != null){
            for (LatLng punto: points){
                Coordenada coordenada = new Coordenada(title, punto.latitude, punto.longitude);
                coor= manejo.insertarCoordenadas(coordenada);
            }
            if(coor != null){
                Toast.makeText(AddRuta.this, "Ruta Guardada", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(AddRuta.this, "Error al insertar en TABLA COORDENAS!!!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(AddRuta.this, "Error al insertar en TABLA RUTAS!!!", Toast.LENGTH_LONG).show();
        }
//        bda.onClose(db);

        manejo.close();
    }

    public void limpiar(){
        titulo.setText("");
        tCoordenadas.setText("");
    }

/*
    @Override
    protected void onStart() {
        super.onStart();

    }
*/
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            stopActivityUpdates();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            startActivityUpdates();
        }
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, intentFilter);
    }
}
