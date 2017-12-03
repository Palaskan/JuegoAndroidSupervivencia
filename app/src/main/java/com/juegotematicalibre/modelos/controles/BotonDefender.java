package com.juegotematicalibre.modelos.controles;

import android.content.Context;

import com.juegotematicalibre.GameView;
import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.modelos.Modelo;

/**
 * Created by jordansoy on 09/10/2017.
 */

public class BotonDefender extends Modelo {

    public BotonDefender(Context context) {
        super(context, GameView.pantallaAncho*0.7 , GameView.pantallaAlto*0.8,
                GameView.pantallaAlto, GameView.pantallaAncho);

        altura = 70;
        ancho = 70;
        imagen = CargadorGraficos.cargarDrawable(context, R.drawable.buttondefend);
    }

    public boolean estaPulsado(float clickX, float clickY) {
        boolean estaPulsado = false;

        if (clickX <= (x + ancho / 2) && clickX >= (x - ancho / 2)
                && clickY <= (y + altura / 2) && clickY >= (y - altura / 2)) {
            estaPulsado = true;
        }
        return estaPulsado;
    }

}

