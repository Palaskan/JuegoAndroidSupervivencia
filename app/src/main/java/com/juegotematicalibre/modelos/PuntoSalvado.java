package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;

/**
 * Created by admin on 27/10/2017.
 */

public class PuntoSalvado extends Modelo {

    public PuntoSalvado(Context context, double x, double y) {
        super(context, x, y, 32, 32);

        imagen = CargadorGraficos.cargarDrawable(context, R.drawable.punto_salvado);
    }


    public void dibujar(Canvas canvas){
        int yArriva = (int)  y - altura - Nivel.scrollEjeY;
        int xIzquierda = (int) x - ancho / 2 - Nivel.scrollEjeX;

        imagen.setBounds(xIzquierda, yArriva, xIzquierda
                + ancho, yArriva + altura);
        imagen.draw(canvas);


    }
}
