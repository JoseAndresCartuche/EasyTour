package com.desarrollador.easytour.DetalleMarcadores;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.desarrollador.easytour.R;
import com.desarrollador.sqlite.Tablas_SQL;

public class MarkerDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_marker);
        setSupportActionBar(toolbar);

        int id = getIntent().getIntExtra(Tablas_SQL.ColumnaMarket._ID, 0);

        MarkerDetailFragment fragment = (MarkerDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.marker_detail_container);
        if (fragment == null) {
            fragment = MarkerDetailFragment.newInstance(id);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.marker_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marker_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
