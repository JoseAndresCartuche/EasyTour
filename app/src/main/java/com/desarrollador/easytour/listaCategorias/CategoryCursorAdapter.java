package com.desarrollador.easytour.listaCategorias;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.desarrollador.easytour.R;
import com.desarrollador.modelo.Categoria;
import com.desarrollador.sqlite.Tablas_SQL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Adapdator de las Categorias
 */
public class CategoryCursorAdapter extends ArrayAdapter<Categoria> {

    private ArrayList<Categoria> categoryList;

    public CategoryCursorAdapter(@NonNull Context context, @NonNull ArrayList<Categoria> categoryList) {
        super(context, R.layout.appbar_filter_list);
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public void clear() {
        categoryList.clear();
    }

    @Override
    public void addAll(@NonNull Collection<? extends Categoria> collection) {
        categoryList.addAll(collection);
    }

    @Override
    public Categoria getItem(int position) {
        return categoryList.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            convertView = inflater.inflate(R.layout.appbar_filter_list, parent,
                    false);
        }
        // Referencias UI
        TextView tvNameCategory = (TextView) convertView.findViewById(R.id.tv_categoria);

        Categoria categoria = this.getItem(position);

        // Obtener values
        if (categoria != null) {
            String nameCategory = categoria.getNombre();

            // Establecerlos en los UI
            tvNameCategory.setText(nameCategory);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position,convertView,parent);
    }

    //    public CategoryCursorAdapter(Context context, Cursor c) {
//        super(context, c, 0);
//    }
//
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        return inflater.inflate(R.layout.appbar_filter_list, parent, false);
//    }
//
//    @Override
//    public void bindView(View view, Context context, Cursor cursor) {
//        // Referencias UI
//        TextView tvNameCategory = (TextView) view.findViewById(R.id.tv_categoria);
//
//        // Obtener values
//        String nameCategory = cursor.getString(cursor.getColumnIndex(Tablas_SQL.ColumnCategoria.NAME));
//
//        // Establecerlos en los UI
//        tvNameCategory.setText(nameCategory);
//    }
}
