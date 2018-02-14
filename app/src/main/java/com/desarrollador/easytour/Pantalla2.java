package com.desarrollador.easytour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;

public class Pantalla2 extends AppCompatActivity {

    Button planos;
    Button lugares;
    Button lugares_cercanos;
    Button rutas;
    Button AgregarMarket;
    Button AgregarRuta;
    Button ListaMarcadores;

    Animation fade_in, fade_out;
    ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla2);

        viewFlipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper1);
        fade_in  = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fade_out  = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        viewFlipper.setInAnimation(fade_in);
        viewFlipper.setOutAnimation(fade_out);

        //set auto flipping
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(2000);
        viewFlipper.startFlipping();

        AgregarMarket = (Button) findViewById(R.id.btnAddMarket);
        AgregarRuta = (Button) findViewById(R.id.btnAddRute);
        planos = (Button) findViewById(R.id.planos);
        lugares = (Button) findViewById(R.id.lugares);
        lugares_cercanos = (Button) findViewById(R.id.lugares_cercanos);
        rutas = (Button) findViewById(R.id.rutas);
        ListaMarcadores = (Button) findViewById(R.id.btnLista);

        AgregarMarket.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                AbrirMarket();
            }
        });

        AgregarRuta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                AbrirRuta();
            }
        });

        planos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                AbrirMapa();
            }
        });


        lugares.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                ejecutar_lugares();
            }
        });

        lugares_cercanos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                ejecutar_lugares_cercanos();
            }
        });

        rutas.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                ejecutar_rutas();
            }
        });

        ListaMarcadores.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                ejecutar_Lista();
            }
        });
    }

    public void AbrirMarket(){
        Intent i1 = new Intent(this,AddMarcador.class);
        startActivity(i1);
    }
    
    public void AbrirRuta(){
        Intent i2 = new Intent(this,AddRuta.class);
        startActivity(i2);
    }
    
    public void AbrirMapa(){
        Intent i3 = new Intent(this,MapaEncontrado.class);
        startActivity(i3);
    }
    
    public void ejecutar_lugares(){
        Intent i4 = new Intent(this,Lugares.class);
        startActivity(i4);
    }
    
    public void ejecutar_lugares_cercanos(){
        Intent i5 = new Intent(this,Lugares_Cercanos.class);
        startActivity(i5);
    }
    
    public void ejecutar_rutas(){
        Intent i6 = new Intent(this,Rutas.class);
        startActivity(i6);
    }

    public void ejecutar_Lista(){
        Intent i7 = new Intent(this,ListaMarcadores.class);
        startActivity(i7);
    }
}
