package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

/**
 * Created by jordansoy on 09/10/2017.
 */

public class DisparoJugadorX extends Disparo{

    public DisparoJugadorX(Context context, double xInicial, double yInicial, int orientacion) {
        super(context, xInicial, yInicial, orientacion);
        ancho = 50;
        velocidadX = 10;
        altura = 50;
        if (orientacion == Jugador.IZQUIERDA)
            velocidadX = velocidadX*-1;
        tiempoVida= 75;
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

