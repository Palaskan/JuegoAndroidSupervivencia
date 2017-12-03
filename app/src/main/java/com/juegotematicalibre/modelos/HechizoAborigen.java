package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

/**
 * Created by jordansoy on 09/10/2017.
 */

public class HechizoAborigen extends Disparo{
    private Sprite sprite;
    private int orientacion;

    public HechizoAborigen(Context context, double xInicial, double yInicial, int orientacion) {
        super(context, xInicial, yInicial, orientacion);
        ancho = 40;
        altura = 40;
        velocidadX = 11;
        if (orientacion == Jugador.IZQUIERDA)
            velocidadX = velocidadX*-1;
        this.orientacion = orientacion;
        cDerecha = 10;
        cIzquierda = 10;
        cArriba = 6;
        cAbajo = 6;

        inicializar();
    }

    public void inicializar (){
            if(orientacion == Jugador.IZQUIERDA)
                sprite= new Sprite(
                        CargadorGraficos.cargarDrawable(context,
                                R.drawable.hechizo_aborigenlef),
                        ancho, altura,
                        2, 2, true);
            else{
                sprite= new Sprite(
                        CargadorGraficos.cargarDrawable(context,
                                R.drawable.hechizo_aborigenrig),
                        ancho, altura,
                        2, 2, true);
            }



    }

    public void actualizar (long tiempo) {
        sprite.actualizar(tiempo);
    }

    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y- Nivel.scrollEjeY);
    }
}

