package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.graficos.Sprite;

import java.util.HashMap;

/**
 * Created by jordansoy on 09/10/2017.
 */


public abstract class Enemigo extends Modelo {
    public int estado = ACTIVO;
    public static final int ACTIVO = 1;
    public static final int INACTIVO = 0;
    public static final int ELIMINAR = -1;
    protected int cadenciaDisparo = 3000;
    protected long milisegundosDisparo = 0;
    public int vida = 1;


    public static final String CAMINANDO_DERECHA = "Caminando_derecha";
    public static final String CAMINANDO_IZQUIERDA = "caminando_izquierda";
    public static final String MUERTE_DERECHA = "muerte_derecha";
    public static final String MUERTE_IZQUIERDA = "muerte_izquierda";


    protected Sprite sprite;
    protected HashMap<String,Sprite> sprites = new HashMap<String,Sprite> ();

    public double velocidadX = 1.2;
    public double velocidadY = -1.2;
    public double vXIni = 1.2;
    public double vYIni = -1.2;
    public double xIni;
    public double yIni;
    public int captacion = 0;

    public Enemigo(Context context, double xInicial, double yInicial,int alto,int ancho) {
        super(context, 0, 0, alto, ancho);
        this.xIni = xInicial;
        this.yIni = yInicial - altura/2;

        this.x = xIni;
        this.y = yIni;



        inicializar();
    }

    public void destruir (){
        velocidadX = 0;
        velocidadY = 0;
        estado = INACTIVO;
    }

    public abstract void inicializar ();

    public void actualizar (long tiempo) {
        boolean finSprite = sprite.actualizar(tiempo);

        if ( estado == INACTIVO && finSprite == true){
            estado = ELIMINAR;
        }

        if (estado == INACTIVO){
            if (velocidadX > 0)
                sprite = sprites.get(MUERTE_DERECHA);
            else
                sprite = sprites.get(MUERTE_IZQUIERDA);

        } else {
            if (velocidadX > 0) {
                sprite = sprites.get(CAMINANDO_DERECHA);
            }
            if (velocidadX < 0) {
                sprite = sprites.get(CAMINANDO_IZQUIERDA);
            }
        }
    }

    public void girarX(){
        velocidadX = velocidadX*-1;
    }
    public void girarY(){
        velocidadY = velocidadY*-1;
    }

    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX, (int) y-Nivel.scrollEjeY);
    }

    public abstract Disparo disparar(long milisegundos, int orientacion);

    public void restablecerPosicionInicial(){
        this.x = xIni;
        this.y = yIni;
    }

}
