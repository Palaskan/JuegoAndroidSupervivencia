package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

/**
 * Created by admin on 01/11/2017.
 */

public class HabilidadEspecial extends Modelo {
    public double velocidadX = 15;
    private Sprite sprite;
    public HabilidadEspecial(Context context, double x, double y, int orientacion) {
        super(context, x, y, 35, 35);
        ancho = 35;
        altura = 35;
        if (orientacion == Jugador.IZQUIERDA)
            velocidadX = velocidadX*-1;

        cDerecha = 6;
        cIzquierda = 6;
        cArriba = 6;
        cAbajo = 6;

        inicializar();
    }

    public void inicializar (){
        sprite= new Sprite(
                CargadorGraficos.cargarDrawable(context,
                        R.drawable.ataque_potenciado),
                ancho, altura,
                12, 4, true);
    }

    public void actualizar (long tiempo) {
        sprite.actualizar(tiempo);
    }

    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y- Nivel.scrollEjeY);
    }
}
