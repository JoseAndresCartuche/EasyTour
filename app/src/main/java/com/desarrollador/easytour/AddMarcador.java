package com.desarrollador.easytour;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.desarrollador.easytour.listaCategorias.CategoryCursorAdapter;
import com.desarrollador.easytour.utils.PermissionUtils;
import com.desarrollador.modelo.Categoria;
import com.desarrollador.modelo.Market;
import com.desarrollador.sqlite.OperacionesBaseDatos;
import com.desarrollador.sqlite.Tablas_SQL;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

//para ACTUALIZACION de localizacion cada cierto tiempo (OnCreate o onStart,)
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddMarcador extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    /**
     * Código de petición para solicitar permiso de camara.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    /**
     * Código de petición para solicitar permiso de locacion.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    /**
     * Bandera que indica si un permiso solicitado se ha negado después de regresar en
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    /**
     * Variable que guarda el permiso negado después de regresar en
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private int iPermissionCode = -1;

    private final String CARPETA_RAIZ = "YOUMAP";
    private final String CARPETA_IMAGEN = CARPETA_RAIZ + File.separator + "MarkerPhotos";

    private CategoryCursorAdapter mCatAdapter;

    private Categoria currentCategory;

    // Views
    private Button mButtonOpenImage;
    private ImageView mImageView;
    //private TextView mTextView;
    public String path = "";

    final int COD_FOTO = 20;
    final int COD_SELECCIONA = 10;


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String LOGTAG = "android-localizacion";

    private Location lastLocation;
    private GoogleApiClient apiClient;
    private EditText titulo;
//    private EditText latitud;
//    private EditText longitud;
    private TextView calle;
    private EditText descrip;
    private Button btnActUbicacion;
    private Button btnGuardarMarket;
    private Spinner cmbCategorias;

    private OperacionesBaseDatos manejo;
    int cont=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marcador);

        int id = getIntent().getIntExtra(Tablas_SQL.ColumnCategoria._ID, 0);

        apiClient = new GoogleApiClient.Builder(this)
                   .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mButtonOpenImage = (Button) findViewById(R.id.foto);
        mImageView = (ImageView) findViewById(R.id.imagenId);

        titulo = (EditText) findViewById(R.id.txtTitulo);
//        latitud = (EditText) findViewById(R.id.txtLatitud);
//        longitud = (EditText) findViewById(R.id.txtLongitud);
        calle = (TextView) findViewById(R.id.TextCalle);
        descrip = (EditText) findViewById(R.id.txtDescrip);
        btnActUbicacion = (Button) findViewById(R.id.btnLocalizar);
        btnGuardarMarket = (Button) findViewById(R.id.btnStopSave);

        cmbCategorias = (Spinner) findViewById(R.id.cmbCategoria_addMarker);

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
                if (currentCategory != null && currentCategory.getId() != 0) {
                    manejo.open();
                    String title = titulo.getText().toString();
                    Double lat = Double.parseDouble(String.valueOf(lastLocation.getLatitude()));
                    Double longi = Double.parseDouble(String.valueOf(lastLocation.getLongitude()));
                    String calles = calle.getText().toString();
                    String descr = descrip.getText().toString().replace('\n', ' ');
                    Market nuevo = new Market(title, lat, longi, calles, descr, path, currentCategory);
                    long resul = manejo.insertarMarket(nuevo);
                    if(resul != -1){
                        Toast.makeText(AddMarcador.this, resul +"\nMarcador Guardado", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(AddMarcador.this, "NO REGISTRADO!!!", Toast.LENGTH_LONG).show();
                    }
                    limpiar();
                    manejo.close();
                }
                else {
                    Toast.makeText(AddMarcador.this, "Por favor elija una categoria", Toast.LENGTH_LONG).show();
                }
            }
        });

        ArrayList<Categoria> listCategory = manejo.getAllCategory();
        listCategory.add(0, new Categoria(0, "Selecciona una Opcion", null));

        mCatAdapter = new CategoryCursorAdapter(this, listCategory);
        //mCatAdapter.setDropDownViewResource(R.layout.appbar_filter_list);

        cmbCategorias.setAdapter(mCatAdapter);

        cmbCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //... Acciones al seleccionar una opción de la lista
                currentCategory = mCatAdapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //... Acciones al no existir ningún elemento seleccionado
            }
        });

        cmbCategorias.setSelection(id);

        mButtonOpenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Inicio();
            }
        });

        manejo.close();
    }

    private void Inicio() {

        final CharSequence[] opciones = {"Tomar Foto", "Cargar Imagen", "Cancelar"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(AddMarcador.this);
        alertOpciones.setTitle("Seleccione una Opcion");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (opciones[i].equals("Tomar Foto")) {
                    Tomarfoto();
                    Toast.makeText(getApplication(), "Tomar Foto", Toast.LENGTH_SHORT).show();
                } else {
                    if (opciones[i].equals("Cargar Imagen")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent, "Seleccione la Aplicacion"), COD_SELECCIONA);
                    } else {
                        dialogInterface.dismiss();
                    }
                }

            }
        });
        alertOpciones.show();

    }

    private boolean checkCameraPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if ( ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // El permiso para acceder a la camara no se encuentra.
                // Pide solicitar que se activen los permisos de camara
                PermissionUtils.requestPermission(this, CAMERA_PERMISSION_REQUEST_CODE ,
                        android.Manifest.permission.CAMERA, true);
                return false;
            } else {
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    private void Tomarfoto() {
        if (checkCameraPermission())
        {
            //Creamos una carpeta en la memeria del terminal
            File imagesFolder = new File( Environment.getExternalStorageDirectory(), CARPETA_IMAGEN);
            if(!imagesFolder.exists()) {
                if(!imagesFolder.mkdirs()) {
                    Toast.makeText(this, R.string.toast_err_directory, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String nombreImagen = (System.currentTimeMillis()/1000) + ".jpg";
            File fileImagen = new File(imagesFolder, nombreImagen);
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", fileImagen);
            //boolean creada = fileImagen.exists();
            //File imagen = new File(path);
            path = fileImagen.getAbsolutePath();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, COD_FOTO);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*if (resultCode == RESULT_OK){
            Uri path = data.getData();
            mImageView.setImageURI(path);
        }*/

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case COD_SELECCIONA :
                    Uri mipath = data.getData();
                    mImageView.setImageURI(mipath);
                    break;

                case COD_FOTO:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String s, Uri uri) {
                                    Log.i("Ruta de almacenamiento", "Path: "+path );
                                }
                            });
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    mImageView.setImageBitmap(bitmap);
                    //mTextView.setVisibility(View.GONE);
                    break;

            }
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
                    LOCATION_PERMISSION_REQUEST_CODE);
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
//            latitud.setText(String.valueOf(lastLocation.getLatitude()));
//            longitud.setText(String.valueOf(lastLocation.getLongitude()));
            this.setStreetLocation(lastLocation);
            Log.d("Location", "Created");
        } else {
//            latitud.setText("Desconocida");
//            longitud.setText("Desconocida");
            Log.d("Location", "No se pudo obtener la ubicación. Asegúrese de que la ubicación está habilitada en el dispositivo");
        }
    }

    private void limpiar() {
        titulo.setText("");
//        latitud.setText("Desconocida");
//        longitud.setText("Desconocida");
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        iPermissionCode = requestCode;

        if ( requestCode == CAMERA_PERMISSION_REQUEST_CODE ) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.CAMERA)) {
                // Toma la foto usando la camara.
                Tomarfoto();
            } else {
                // Muestra el diálogo de error de permiso faltante cuando los fragmentos se reanudan.
                mPermissionDenied = true;
            }
        }

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                //Toast.makeText(AddMarcador.this, "Permiso denegado", Toast.LENGTH_LONG).show();
                mPermissionDenied = true;
            }
        }
    }

    /**
     * Esta es la versión orientada a fragmentos de onResume() que puede anular para realizar
     * operaciones en la Actividad en el mismo punto donde se reanudan sus fragmentos.
     */
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // El permiso no fue otorgado, muestra el cuadro de diálogo de error.
            int idMessage = -1;
            boolean finishActivity = false;
            switch (iPermissionCode)
            {
                case CAMERA_PERMISSION_REQUEST_CODE:
                    idMessage = R.string.camera_permission_denied;
                    finishActivity = false;
                    break;
                case LOCATION_PERMISSION_REQUEST_CODE:
                    idMessage = R.string.location_permission_denied;
                    finishActivity = false;
                    break;
            }
            showMissingPermissionError(finishActivity, idMessage);
            mPermissionDenied = false;
        }
    }

    /**
     * Muestra un cuadro de diálogo con un mensaje de error que explica que falta
     * el permiso de camara.
     */
    private void showMissingPermissionError(boolean finishActivity, int idMessage) {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(finishActivity, idMessage).show(getSupportFragmentManager(), "dialog");
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
        apiClient.connect();
    }

    @Override
    protected void onPause() {
        apiClient.disconnect();
        super.onPause();
    }
}

