package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

/**
 * Created by UO223663 on 18/10/2017.
 */

public abstract class Recolectable extends Modelo {
    protected Sprite sprite;
    public boolean debajo;
    public Recolectable(Context context, double x, double y,int alto, int ancho,boolean debajo) {
        super(context, x, y, alto,ancho);
        this.y =  y - altura/2;
        this.debajo = debajo;

        inicializar();
    }

    public void dibujar(Canvas canvas){
        if(!debajo)
            sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y-Nivel.scrollEjeY);
    }

    public abstract void inicializar();

    @Override
    public void actualizar(long tiempo) {
        sprite.actualizar(tiempo);
    }

}

