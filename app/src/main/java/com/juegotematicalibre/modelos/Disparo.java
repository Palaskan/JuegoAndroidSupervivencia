package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

/**
 * Created by admin on 23/10/2017.
 */

public abstract class Disparo extends Modelo{
    private Sprite sprite;
    public double velocidadX = 0;
    public double velocidadY = 0;
    protected int an;
    protected int al;
    public double tiempoVida=-1;

    public Disparo(Context context, double xInicial, double yInicial, int orientacion) {
        super(context, xInicial, yInicial, 35, 35);

        if (orientacion == Jugador.IZQUIERDA)
            velocidadX = velocidadX*-1;

        cDerecha = 12;
        cIzquierda = 12;
        cArriba = 6;
        cAbajo = 6;

        inicializar();
    }

    public abstract void inicializar ();

    public void actualizar (long tiempo) {
        sprite.actualizar(tiempo);
    }

    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y- Nivel.scrollEjeY);
    }
}
