package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Canvas;

import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.gestores.GestorAudio;
import com.juegotematicalibre.graficos.Sprite;

import java.util.HashMap;

/**
 * Created by jordansoy on 09/10/2017.
 */

public class Jugador extends Modelo {
    public static final String PARADO_DERECHA = "Parado_derecha";
    public static final String PARADO_IZQUIERDA = "Parado_izquierda";
    public static final String CAMINANDO_DERECHA = "Caminando_derecha";
    public static final String CAMINANDO_IZQUIERDA = "Caminando_izquierda";
    public static final String DISPARANDO_DERECHA = "disparando_derecha";
    public static final String DISPARANDO_IZQUIERDA = "disparando_izquierda";
    public static final String HABILIDAD_DERECHA = "habilidad_derecha";
    public static final String HABILIDAD_IZQUIERDA = "habilidad_izquierda";
    public static final String DEFENDIENDO_DERECHA = "defendiendo_derecha";
    public static final String DEFENDIENDO_IZQUIERDA = "defendiendo_izquierda";

    public int orientacion;
    public static final int DERECHA = 1;
    public static final int IZQUIERDA = -1;


    double velocidadX;
    double velocidadY;
    public boolean disparando;
    public boolean defendiendo;
    public boolean especial;
    public boolean golpeado = false;
    private Sprite sprite;
    private HashMap<String,Sprite> sprites = new HashMap<String,Sprite> ();

    double xInicial;
    double yInicial;

    public int vidas;
    public int vidasMaximas;

    // ACTUAL
    public double msInmunidad = 0;

    public double msBloqueando = 0;

    public double msEspecial = 0;


    public Jugador(Context context, double xInicial, double yInicial) {
        super(context, 0, 0, 40, 40);
        vidas = 5;
        vidasMaximas = 5;
        // guardamos la posición inicial porque más tarde vamos a reiniciarlo
        this.xInicial = xInicial;
        this.yInicial = yInicial - altura/2;

        this.x =  this.xInicial;
        this.y =  this.yInicial;

        inicializar();
    }

    public void inicializar (){
        Sprite disparandoDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_atacarrig),
                ancho+8, altura+10,
                30, 10, false);
        sprites.put(DISPARANDO_DERECHA, disparandoDerecha);

        Sprite disparandoIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_atacarlef),
                ancho+8, altura+10,
                30, 10, false);
        sprites.put(DISPARANDO_IZQUIERDA, disparandoIzquierda);

        Sprite caminandoDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_andarrig),
                ancho, altura,
                10, 10, true);
        sprites.put(CAMINANDO_DERECHA, caminandoDerecha);

        Sprite caminandoIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_andarlef),
                ancho, altura,
                10, 10, true);
        sprites.put(CAMINANDO_IZQUIERDA, caminandoIzquierda);

        Sprite paradoDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_reposorig),
                ancho-5, altura,
                5, 10, true);
        sprites.put(PARADO_DERECHA, paradoDerecha);

        Sprite paradoIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_reposolef),
                ancho-5, altura,
                4, 10, true);
        sprites.put(PARADO_IZQUIERDA, paradoIzquierda);

        Sprite defendiendoDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_defendiendorig),
                ancho, altura,
                10, 4, false);
        sprites.put(DEFENDIENDO_DERECHA, defendiendoDerecha);

        Sprite defendiendoIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_defendiendolef),
                ancho, altura,
                10, 4, false);
        sprites.put(DEFENDIENDO_IZQUIERDA, defendiendoIzquierda);

        Sprite habilidadDerecha = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_habilidadrig),
                ancho, altura,
                16, 8, false);
        sprites.put(HABILIDAD_DERECHA, habilidadDerecha);

        Sprite habilidadIzquierda = new Sprite(
                CargadorGraficos.cargarDrawable(context, R.drawable.caballero_habilidadlef),
                ancho, altura,
                16, 8, false);
        sprites.put(HABILIDAD_IZQUIERDA, habilidadIzquierda);

// animación actual
        sprite = paradoDerecha;
    }

    public void procesarOrdenes (float orientacionPadX,float orientacionPadY, boolean disparar, boolean defender,boolean especial) {


        if (disparar){
            disparando = true;
            // preparar los sprites, no son bucles hay que reiniciarlos
            sprites.get(DISPARANDO_DERECHA).setFrameActual(0);
            sprites.get(DISPARANDO_IZQUIERDA).setFrameActual(0);
        }
        if(defender){
            defendiendo();
            sprites.get(DEFENDIENDO_DERECHA).setFrameActual(0);
            sprites.get(DEFENDIENDO_IZQUIERDA).setFrameActual(0);
            //los sprites de defenderse
        }
        if(especial){
            habilidadEspecial();
            sprites.get(HABILIDAD_DERECHA).setFrameActual(0);
            sprites.get(HABILIDAD_IZQUIERDA).setFrameActual(0);
        }


        if (orientacionPadX > 10) {
            if(orientacionPadY> 10){
                velocidadX = -3.5*0.701;
                velocidadY = -3.5*0.701;
            }else if(orientacionPadY<-10){
                velocidadX = -3.5*0.701;
                velocidadY = 3.5*0.701;
            }
            else{
                velocidadX = -3.5;
            }
            orientacion = IZQUIERDA;
        } else if (orientacionPadX < -10 ){
            if(orientacionPadY>10){
                velocidadX = 3.5*0.701;
                velocidadY = -3.5*0.701;
            }
            else if(orientacionPadY<-10){
                velocidadX = 3.5*0.701;
                velocidadY = 3.5*0.701;
            }
            else {
                velocidadX = 3.5;
            }
            orientacion = DERECHA;
        } else {
            velocidadX = 0;
        }

        if (orientacionPadY > 10) {
            if(orientacionPadX> 1){
                velocidadX = -3.5*0.701;
                velocidadY = -3.5*0.701;
                orientacion = IZQUIERDA;
            }else if(orientacionPadX<-10){
                velocidadX = 3.5*0.701;
                velocidadY = -3.5*0.701;
                orientacion = DERECHA;
            }
            else{
                velocidadY = -3.5;
            }
        } else if (orientacionPadY < -10 ){
            if(orientacionPadX>10){
                velocidadX = -3.5*0.701;
                velocidadY = 3.5*0.701;
                orientacion = IZQUIERDA;
            }
            else if(orientacionPadX<-10){
                velocidadX = 3.5*0.701;
                velocidadY = 3.5*0.701;
                orientacion = DERECHA;
            }
            else {
                velocidadY = 3.5;
            }
        } else {
            velocidadY = 0;
        }

    }


    public void actualizar (long tiempo) {
        if(msInmunidad > 0){
            msInmunidad -= tiempo;
        }
        if(msBloqueando >0){
            msBloqueando -= tiempo;
        }
        if(msEspecial >0){
            msEspecial -= tiempo;
        }

        boolean finSprite = sprite.actualizar(tiempo);

        // Deja de estar golpeado, cuando lo estaba y se acaba el sprite
        if (golpeado && finSprite){
            golpeado = false;
        }

        if(disparando && finSprite){
            disparando = false;
        }
        if(defendiendo && finSprite){
                defendiendo = false;

        }
        if(especial && finSprite){
            //if(msEspecial <=0)
                especial = false;
        }


        if (velocidadX > 0){
            sprite = sprites.get(CAMINANDO_DERECHA);
        }
        if (velocidadX < 0 ){
            sprite = sprites.get(CAMINANDO_IZQUIERDA);
        }
        if (velocidadX == 0 ){
            if (orientacion == DERECHA){
                sprite = sprites.get(PARADO_DERECHA);
            } else if (orientacion == IZQUIERDA) {
                sprite = sprites.get(PARADO_IZQUIERDA);
            }
        }
        if(velocidadY > 0 || velocidadY < 0){
            if(orientacion == IZQUIERDA)
                sprite = sprites.get(CAMINANDO_IZQUIERDA);
            else
                sprite = sprites.get(CAMINANDO_DERECHA);
        }


        if (disparando){
            if (orientacion == DERECHA){
                sprite = sprites.get(DISPARANDO_DERECHA);
            } else if (orientacion == IZQUIERDA) {
                sprite = sprites.get(DISPARANDO_IZQUIERDA);
            }
        }

        if(defendiendo){
            if (orientacion == DERECHA){
                sprite = sprites.get(DEFENDIENDO_DERECHA);
            } else if (orientacion == IZQUIERDA) {
                sprite = sprites.get(DEFENDIENDO_IZQUIERDA);
            }
        }

        if(especial){
            if (orientacion == DERECHA){
                sprite = sprites.get(HABILIDAD_DERECHA);
            } else if (orientacion == IZQUIERDA) {
                sprite = sprites.get(HABILIDAD_IZQUIERDA);
            }
        }




    }

    public void dibujar(Canvas canvas){
        sprite.dibujarSprite(canvas, (int) x - Nivel.scrollEjeX , (int) y - Nivel.scrollEjeY,msInmunidad > 0);
    }

    public void restablecerPosicionInicial(){
        vidas = vidasMaximas;
        golpeado = false;
        msInmunidad = 0;

        this.x = xInicial;
        this.y = yInicial;
        orientacion = IZQUIERDA;
    }

    public int golpeado(){
        if(!defendiendo) {
            if (msInmunidad <= 0) {
                if (vidas > 0) {
                    vidas--;
                    msInmunidad = 3000;
                    golpeado = true;

                }
            }
        }
        return vidas;
    }

    public int golpeadoFuerte(){
        if(!defendiendo) {
            if (msInmunidad <= 0) {
                if (vidas > 0) {
                    vidas -= 2;
                    msInmunidad = 3000;
                    golpeado = true;

                }
            }
        }
        return vidas;
    }

    public void defendiendo(){
        msBloqueando = 300;
        defendiendo = true;
    }


    public void habilidadEspecial(){
        especial = true;
        msEspecial = 20000;
    }


}
