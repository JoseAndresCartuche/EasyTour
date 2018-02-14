package com.desarrollador.easytour.DetalleMarcadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarrollador.easytour.R;
import com.desarrollador.modelo.Market;
import com.desarrollador.sqlite.OperacionesBaseDatos;
import com.squareup.picasso.Picasso;

/**
 * Vista para el detalle del marcador
 */
public class MarkerDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MARKER_ID = "markerID";

    // TODO: Rename and change types of parameters
    private int mMarkerId;

    private OperacionesBaseDatos mMarkerBD;
    private Market currentMarket;

    private CollapsingToolbarLayout mCollapsingView;
    private FloatingActionButton mLocationButton;
    private ImageView mPhotoView;
    private TextView mTitleView;
    private TextView mStreetView;
    private TextView mLocationView;
    private TextView mDescriptionView;
    private TextView mCategoryView;

    public MarkerDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param markerId Id to marker
     * @return A new instance of fragment ContactDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarkerDetailFragment newInstance(int markerId) {
        MarkerDetailFragment fragment = new MarkerDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MARKER_ID, markerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMarkerId = getArguments().getInt(ARG_MARKER_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_marker_detail, container, false);

        mCollapsingView = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
        mPhotoView = (ImageView) getActivity().findViewById(R.id.iv_photoMarker);
        mTitleView = (TextView) root.findViewById(R.id.tv_title_marker);
        mLocationView = (TextView) root.findViewById(R.id.tv_location_marker);
        mStreetView = (TextView) root.findViewById(R.id.tv_streets_marker);
        mDescriptionView = (TextView) root.findViewById(R.id.tv_description_marker);
        mCategoryView = (TextView) root.findViewById(R.id.tv_category_marker);

        mLocationButton = (FloatingActionButton) getActivity().findViewById(R.id.fabLocation);

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLocation();
            }
        });

        // Instancia de helper
        mMarkerBD = new OperacionesBaseDatos(getActivity());
        mMarkerBD.open();

        loadMarker();

        return root;
    }

    /**
     * Cargar los detalles del marcador usando tarea asincrónica
     */
    private void loadMarker() {
        new GetMarkerByIdTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Mostrar los datos del marcador en la Actividad
     * @param market El Marcador a mostrar
     */
    private void showContact(Market market) {
        mCollapsingView.setTitle(market.getTitulo());
        //Log.d("CDActivity", contact.getName());
        if(market.getImagePath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(market.getImagePath());
            mPhotoView.setImageBitmap(bitmap);
        }
        mTitleView.setText(market.getTitulo());
        mStreetView.setText(market.getCalles());
        mLocationView.setText(String.format("%f %f", market.getLatitud(), market.getLongitud()));
        mDescriptionView.setText(market.getDescripcion());
        mCategoryView.setText(market.getCategoria().getNombre());
    }

    private void showLoadError() {
        Toast.makeText(getActivity(), R.string.err_load_info, Toast.LENGTH_SHORT).show();
    }

    /**
     * Método para enviar la ubicación del marcador y trazar la ruta en google maps.
     */
    private void sendLocation() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + currentMarket.getLatitud() + "," +
                currentMarket.getLongitud());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        // Verifica si existe la aplicación de google maps.
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    /**
     * Clase para realizar tarea asincrónica al contacto. Obtener el contacto por su ID en segundo
     * plano
     */
    private class GetMarkerByIdTask extends AsyncTask<Void, Void, Market> {

        @Override
        protected Market doInBackground(Void... voids) {
            return mMarkerBD.getMarketById(mMarkerId);
        }

        @Override
        protected void onPostExecute(Market market) {
            if (market != null) {
                currentMarket = market;
                showContact(market);
            } else {
                showLoadError();
            }
        }
    }

}
