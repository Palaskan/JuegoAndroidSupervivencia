package com.juegotematicalibre.modelos.controles;

import android.content.Context;

import com.juegotematicalibre.GameView;
import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.modelos.Modelo;

/**
 * Created by jordansoy on 09/10/2017.
 */

public class Pad extends Modelo {

    public Pad(Context context) {
        super(context, GameView.pantallaAncho*0.15 , GameView.pantallaAlto*0.8 ,
                GameView.pantallaAlto, GameView.pantallaAncho);

        altura = 100;
        ancho = 100;
        imagen = CargadorGraficos.cargarDrawable(context, R.drawable.pad);
    }

    public boolean estaPulsado(float clickX, float clickY) {
        boolean estaPulsado = false;

        if (clickX <= (x + ancho / 2) && clickX >= (x - ancho / 2)
                && clickY <= (y + altura / 2) && clickY >= (y - altura / 2)

                ) {
            estaPulsado = true;
        }
        return estaPulsado;
    }

    public int getOrientacionX(
            float cliclX) {
        return (int) (x - cliclX);
    }

    public int getOrientacionY(
            float cliclY) {
        return (int) (y - cliclY);
    }

}

