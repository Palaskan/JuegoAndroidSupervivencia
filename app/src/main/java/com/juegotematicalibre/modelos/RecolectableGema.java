package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

/**
 * Created by admin on 27/10/2017.
 */

public class RecolectableGema extends Recolectable {
    public RecolectableGema(Context context, double x, double y,boolean debajo) {
        super(context, x, y, 32, 32,debajo);
    }

    @Override
    public void inicializar() {
        sprite = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.gem),
                ancho, altura,
                8, 8, true);
    }

}
