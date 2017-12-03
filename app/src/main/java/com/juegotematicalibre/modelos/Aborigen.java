package com.juegotematicalibre.modelos;

import android.content.Context;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

/**
 * Created by admin on 23/10/2017.
 */

public class Aborigen extends Enemigo{



    public Aborigen(Context context, double xInicial, double yInicial) {
        super(context, xInicial, yInicial,64,64);

        velocidadX = 1 + Math.random()*0.5;
        velocidadY = velocidadX;
        captacion = 250;
        vida = 3;
        cDerecha = 27;
        cIzquierda = 27;
        cArriba = 32;
        cAbajo = 32;
    }

    public void inicializar (){
        Sprite caminandoDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemy2runright),
                ancho, altura,
                6, 4, true);
        sprites.put(CAMINANDO_DERECHA, caminandoDerecha);

        Sprite caminandoIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemy2run),
                ancho, altura,
                6, 4, true);
        sprites.put(CAMINANDO_IZQUIERDA, caminandoIzquierda);

        Sprite muerteDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemy2dieright),
                ancho, altura,
                10, 6, false);
        sprites.put(MUERTE_DERECHA, muerteDerecha);

        Sprite muerteIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemy2die),
                ancho, altura,
                10, 6, false);
        sprites.put(MUERTE_IZQUIERDA, muerteIzquierda);


        sprite = caminandoDerecha;
    }

    @Override
    public Disparo disparar(long milisegundos,int orientacion) {
        if (milisegundos - milisegundosDisparo> cadenciaDisparo
                + Math.random()* cadenciaDisparo) {

            milisegundosDisparo = milisegundos;
            int i = (Math.random() <= 0.75) ? 1 : 2;
            if(i == 1)
                return new DisparoAborigen(context, x, y,orientacion);
            else{
                return new HechizoAborigen(context, x, y,orientacion);
            }
        }
        return null;
    }

}
