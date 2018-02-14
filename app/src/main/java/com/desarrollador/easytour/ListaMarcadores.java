package com.desarrollador.easytour;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.desarrollador.sqlite.OperacionesBaseDatos;
import com.desarrollador.sqlite.Tablas_SQL;


public class ListaMarcadores extends AppCompatActivity {
    private OperacionesBaseDatos manejo;

    private ListView listView;
    private SimpleCursorAdapter adapter;

    final String[] from = new String[]{
            Tablas_SQL.Marcadores.TITULO,
            Tablas_SQL.Marcadores.LATITUD,
            Tablas_SQL.Marcadores.LONGITUD,
            Tablas_SQL.Marcadores.CALLES,
            Tablas_SQL.Marcadores.DESCRIPCION};

    final int[] to = new int[]{R.id.titulo, R.id.latitud, R.id.longitud, R.id.calle, R.id.desc};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_datos_visu);

        manejo = new OperacionesBaseDatos(this);
        manejo.open();

        Cursor cursor = manejo.obtenerMarkets();

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new SimpleCursorAdapter(this, R.layout.activity_lista_marcadores, cursor, from, to, 0);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);

/*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewid) {
                TextView tituloTv = (TextView) view.findViewById(R.id.titulo);
                TextView descTv = (TextView) view.findViewById(R.id.desc);
                TextView Lat = (TextView) view.findViewById(R.id.latitud);

                String ti = tituloTv.getText().toString();
                String de = descTv.getText().toString();
                String latid = Lat.getText().toString();

                Intent modify_intent = new Intent(ListaMarcadores.this, ModificarUbica.class);
                modify_intent.putExtra("title", ti);
                modify_intent.putExtra("desc", de);
                modify_intent.putExtra("latitud", latid);

                startActivity(modify_intent);
            }
        });
*/
    }



}