package com.desarrollador.easytour;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.io.InputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Lugares_Cercanos extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String LOGTAG = "android-localizacion";
    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    private Location lastLocation;
    private GoogleApiClient apiClient;
    private TextView localizadoGPS;
    private Button btnLocaliza;

    private EditText lugar1, lugar2, lugar3, lugar4;

    public static final int REQUEST_LOCATION = 1;
    private String googleAPIKey = "AIzaSyDKSRG89GUDygIVuVEkSfPn1EB_Mfx7xyw";
    DefaultHttpClient client;
    HttpResponse res;
    HttpGet req;
    InputStream in;
    JSONObject jsonobj;
    JSONArray resarray;
    String requesturl;
    HttpEntity jsonentity;
    Double pLat, pLon;

    final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares__cercanos);

        apiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addOnConnectionFailedListener(this)
                .build();

        // Construimos el GoogleApi client para encontrar los lugar1
//        buildGoogleApiClient();

        localizadoGPS = (TextView) findViewById(R.id.ubicaCoor);
        btnLocaliza = (Button) findViewById(R.id.localiza);

        lugar1 = (EditText) findViewById(R.id.lugar1);
        lugar2 = (EditText) findViewById(R.id.lugar2);
        lugar3 = (EditText) findViewById(R.id.lugar3);
        lugar4 = (EditText) findViewById(R.id.lugar4);

        btnLocaliza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Lugares_Cercanos.this, "Buscando...", Toast.LENGTH_LONG).show();
                updateUI();
            }
        });

    }

    public void Places() {
        requesturl = "https://maps.googleapis.com/maps/api/place/search/json?radius=500&sensor=false&key=" + googleAPIKey + "&location=" + pLat + "," + pLon;

        System.out.println("Request " + requesturl);
        client = new DefaultHttpClient();
        System.out.println("hello");

        req = new HttpGet(requesturl);
        System.out.println("hello");

        try {
            res = client.execute(req);
            StatusLine status = res.getStatusLine();
            int code = status.getStatusCode();
            System.out.println(code);
            if (code != 200) {
                System.out.println("Request Has not succeeded");
                finish();
            }

            jsonentity = res.getEntity();
            in = jsonentity.getContent();

            jsonobj = new JSONObject(convertStreamToString(in));


            resarray = jsonobj.getJSONArray("results");

            if (resarray.length() == 0) {
                Toast.makeText(Lugares_Cercanos.this, "No se ha encontro lugar1.", Toast.LENGTH_LONG).show();
            } else {
                int len = resarray.length();
                for (int j = 0; j < len; j++) {
                    Toast.makeText(getApplicationContext(), resarray.getJSONObject(j).getString("name"), Toast.LENGTH_LONG).show();
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder jsonstr = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String t = line + "";
                jsonstr.append(t);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonstr.toString();
    }

    private void updateUI() {
        if (lastLocation != null) {
            localizadoGPS.setText(
                    String.valueOf(lastLocation.getLatitude()) + ", " + String.valueOf(lastLocation.getLongitude())
            );
            pLat = lastLocation.getLatitude();
            pLon = lastLocation.getLongitude();
//            this.setPlacesLocation(lastLocation);
            Log.d("Location", "Created");
        } else {
            localizadoGPS.setText("Ubicación Desconocida");
            Log.d("Location", "No se pudo obtener la ubicación. Asegúrese de que la ubicación está habilitada en el dispositivo");
        }
        //Places();
    }

    private void processLastLocation() {
        guessCurrentPlace();
        if (lastLocation != null) {
            updateUI();
        }
    }
    private void guessCurrentPlace() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            if(apiClient.isConnected()){
                Log.d("ApiCliente", "Connected");
            }
            else{
                Log.d("ApiCliente", "Disconnected");
            }
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        }

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(apiClient, null);
        result.setResultCallback( new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult( PlaceLikelihoodBuffer likelyPlaces ) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    System.out.println(String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                    lugar1.setText(likelyPlaces.get(0).getPlace().getName());
                    lugar2.setText(likelyPlaces.get(1).getPlace().getName());
                    lugar3.setText(likelyPlaces.get(2).getPlace().getName());
                    lugar4.setText(likelyPlaces.get(3).getPlace().getName());
                }
                /*
                PlaceLikelihood placeLikelihood = likelyPlaces.get( 2 );
                String content = "";
                if( placeLikelihood != null && placeLikelihood.getPlace() != null && !TextUtils.isEmpty( placeLikelihood.getPlace().getName() ) ){
                    content = String.valueOf(placeLikelihood.getPlace().getName());
                    System.out.println("lugares "+content);
                }
                System.out.println("Preba valores "+content);
                if( placeLikelihood != null )
                    content += "Porcentaje de estar aquí: " + (int) ( placeLikelihood.getLikelihood() * 100 ) + "%";
                */
                likelyPlaces.release();
            }
        });
    }
/*
    private void setPlacesLocation(Location loc) {
        //Obtener la direcci—n de la calle a partir de la latitud y la longitud
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Place> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 4);
            if (!list.isEmpty()) {
                Place address = list.get(0);
                calle.setText(address.getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    private void limpiar() {
        localizadoGPS.setText("Ubicación Desconocida");
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
            processLastLocation();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(apiClient != null){
            Log.d("ApiCliente", "Created");
        }else{
            Log.d("ApiCliente", "Null");
        }
    }



    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Goog le Play Services
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        apiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    protected void onStop() {
        if( apiClient != null && apiClient.isConnected() ) {
            apiClient.disconnect();
        }
        super.onStop();
    }

}
