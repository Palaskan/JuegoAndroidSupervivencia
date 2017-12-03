package com.juegotematicalibre.modelos;

import android.content.Context;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;

/**
 * Created by jordansoy on 10/10/2017.
 */

public class IconoVida extends Modelo {

    public IconoVida(Context context, double x, double y) {
        super(context, x, y, 40,40);
        imagen = CargadorGraficos.cargarDrawable(context, R.drawable.espada);
    }
}

