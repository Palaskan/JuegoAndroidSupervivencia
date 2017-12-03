package com.juegotematicalibre.modelos;

import android.content.Context;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

/**
 * Created by admin on 23/10/2017.
 */

public class Zombie extends Enemigo{
    public Zombie(Context context, double xInicial, double yInicial) {
        super(context, xInicial, yInicial,40,40);
        velocidadX = 0.2;
        velocidadY = -0.2;
        captacion = 300;
        vXIni = 1.5 + Math.random()*1;
        vYIni = vXIni;
        cDerecha = 15;
        cIzquierda = 15;
        cArriba = 20;
        cAbajo = 20;
    }

    public void inicializar (){

        Sprite caminandoDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemyrunright),
                ancho, altura,
                6, 4, true);
        sprites.put(CAMINANDO_DERECHA, caminandoDerecha);

        Sprite caminandoIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemyrun),
                ancho, altura,
                6, 4, true);
        sprites.put(CAMINANDO_IZQUIERDA, caminandoIzquierda);

        Sprite muerteDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemydieright),
                ancho, altura,
                10, 8, false);
        sprites.put(MUERTE_DERECHA, muerteDerecha);

        Sprite muerteIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.enemydie),
                ancho, altura,
                10, 8, false);
        sprites.put(MUERTE_IZQUIERDA, muerteIzquierda);


        sprite = caminandoDerecha;
    }

    @Override
    public Disparo disparar(long milisegundos, int orientacion) {
        return null;
    }

}
