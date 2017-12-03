package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;

/**
 * Created by jordansoy on 09/10/2017.
 */

public class DisparoJugadorY extends Disparo{

    public DisparoJugadorY(Context context, double xInicial, double yInicial) {
        super(context, xInicial, yInicial,1);
        ancho = 50;
        altura = 50;
        velocidadY = -10;
        tiempoVida= 70;
        cDerecha = 15;
        cIzquierda = 15;
        cArriba = 15;
        cAbajo = 15;

        inicializar();
    }

    public void inicializar (){
        imagen =
                CargadorGraficos.cargarDrawable(context,
                        R.drawable.invisible);
    }

    public void actualizar (long tiempo) {
        if(tiempoVida>0)
            tiempoVida -= tiempo;
    }

    public void dibujar(Canvas canvas){
        int yArriva = (int)  y - altura / 2;
        int xIzquierda = (int) x - ancho / 2;

        imagen.setBounds(xIzquierda, yArriva, xIzquierda
                + ancho, yArriva + altura);
        imagen.draw(canvas);
    }

}

