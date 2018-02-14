package com.desarrollador.easytour.listaMarcadores;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.desarrollador.easytour.R;
import com.desarrollador.sqlite.Tablas_SQL.ColumnaMarket;


/**
 * Adaptador de abogados
 */
public class MarksCursorAdapter extends CursorAdapter {

    public MarksCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_mark, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Referencias UI.
        TextView tvNameMarker = (TextView) view.findViewById(R.id.mark_name);
        //final ImageView markIcon = (ImageView) view.findViewById(R.id.mark_icon);

        // Get valores.
        String markerTitle = cursor.getString(cursor.getColumnIndex(ColumnaMarket.TITULO));
       //String markIcon = cursor.getString(cursor.getColumnIndex(Marcadores.LATITUD));

        tvNameMarker.setText(markerTitle);
    }

}
