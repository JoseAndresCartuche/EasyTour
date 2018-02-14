package com.desarrollador.easytour.listaMarcadores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.desarrollador.easytour.AddMarcador;
import com.desarrollador.easytour.DetalleMarcadores.MarkerDetailActivity;
import com.desarrollador.easytour.R;
import com.desarrollador.easytour.listaCategorias.CategoryCursorAdapter;
import com.desarrollador.modelo.Categoria;
import com.desarrollador.sqlite.BaseDatosAyuda;
import com.desarrollador.sqlite.OperacionesBaseDatos;
import com.desarrollador.sqlite.Tablas_SQL;

import java.util.ArrayList;

/**
 * Vista para la lista de marcadores obtenidos de la Base de Datos SQLite
 */
public class ListMarkFragment extends Fragment {
    private OperacionesBaseDatos mMarkerBD;

    private ListView mMarkerList;
    private MarksCursorAdapter mMarksAdapter;
    private CategoryCursorAdapter mCatAdapter;
    private FloatingActionButton mAddMarker;
    private Spinner cmbCategorias;

    private Categoria mCategoria;

    public ListMarkFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static ListMarkFragment newInstance() {
        return new ListMarkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_mark, container, false);

        // Instancia de helper
        mMarkerBD = new OperacionesBaseDatos(getActivity());
        mMarkerBD.open();

        // Referencias UI
        mMarkerList = (ListView) root.findViewById(R.id.mark_list);
        mMarksAdapter = new MarksCursorAdapter(getActivity(), null);
        mAddMarker = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        cmbCategorias = (Spinner) getActivity().findViewById(R.id.Cmb_Categoria);

        ArrayList<Categoria> listCategory = mMarkerBD.getAllCategory();
        listCategory.add(0, new Categoria(0, "Todo", null));

        mCatAdapter = new CategoryCursorAdapter(getActivity(), listCategory);
        mCatAdapter.setDropDownViewResource(R.layout.appbar_filter_list);

        cmbCategorias.setAdapter(mCatAdapter);

        cmbCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //... Acciones al seleccionar una opción de la lista
                mCategoria = mCatAdapter.getItem(i);
                int itemId = mCategoria.getId();
                loadMarkers(itemId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //... Acciones al no existir ningún elemento seleccionado
                loadMarkers();
            }
        });

        // Setup
        mMarkerList.setAdapter(mMarksAdapter);

        // Eventos
        mMarkerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentMarkerItem = (Cursor) mMarksAdapter.getItem(i);
                int currentMarkerId = currentMarkerItem.getInt(
                        currentMarkerItem.getColumnIndex(Tablas_SQL.Marcadores._ID));
                showDetailMarker(currentMarkerId);
            }
        });

        mAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddScreen();
            }
        });


        //getActivity().deleteDatabase(mMarkerBDHelper.NOMBRE_BASE_DATOS);

        // Carga de datos
        loadMarkers();

        return root;
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditLawyerActivity.REQUEST_ADD_LAWYER:
                    showSuccessfullSavedMessage();
                    loadLawyers();
                    break;
                case REQUEST_UPDATE_DELETE_LAWYER:
                    loadLawyers();
                    break;
            }
        }
    }*/

    private void loadMarkers() {
        new MarkersLoadTask().execute();
    }

    private void loadMarkers(int idCategory) {
        if (idCategory == 0) {
            loadMarkers();
        }
        else {
            new MarkersByCategoryLoadTask().execute(idCategory);
        }

    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddMarcador.class);
        intent.putExtra(Tablas_SQL.ColumnCategoria._ID, mCategoria.getId()) ;
        startActivity(intent);
    }

    private void showDetailMarker(int markerId) {
        Intent intent = new Intent(getActivity(), MarkerDetailActivity.class);
        intent.putExtra(Tablas_SQL.ColumnaMarket._ID, markerId);
        startActivity(intent);
        //startActivityForResult(intent, REQUEST_UPDATE_DELETE_LAWYER);
    }

    private class MarkersLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mMarkerBD.getAllMarkets();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                mMarksAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
            }
        }
    }

    private class MarkersByCategoryLoadTask extends AsyncTask<Integer, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Integer... integers) {
            int id = integers[0].intValue();
            return mMarkerBD.getMarkersByCategory(id);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                mMarksAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                mMarksAdapter.swapCursor(null);
            }
        }
    }
}
