package com.juegotematicalibre.modelos;

import android.graphics.drawable.Drawable;

/**
 * Created by jordansoy on 09/10/2017.
 */

public class Tile {
    public static final int PASABLE = 0;
    public static final int SOLIDO = 1;
    public static final int RELENTIZABLE= 2;
    public static final int ROMPIBLE=3;

    public int tipoDeColision; // PASABLE o SOLIDO
//ancho 40 alto 32
    public static int ancho = 40;
    public static int altura = 32;

    public Drawable imagen;

    public Tile(Drawable imagen, int tipoDeColision)
    {
        this.imagen = imagen ;
        this.tipoDeColision = tipoDeColision;
    }

}
