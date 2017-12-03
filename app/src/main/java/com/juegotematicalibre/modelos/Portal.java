package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

/**
 * Created by admin on 01/11/2017.
 */

public class Portal extends Modelo {
    private Sprite sprite;
    public Portal(Context context, double x, double y) {
        super(context, x, y, 50, 50);
        this.y = y - altura/2;
        sprite = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.portal),
                40, 40,
                10, 5, true);
    }


    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y-Nivel.scrollEjeY);
    }

    @Override
    public void actualizar(long tiempo) {
        sprite.actualizar(tiempo);
    }


}
