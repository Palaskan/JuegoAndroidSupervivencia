package com.juegotematicalibre;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.juegotematicalibre.gestores.GestorAudio;
import com.juegotematicalibre.modelos.IconoVida;
import com.juegotematicalibre.modelos.Nivel;
import com.juegotematicalibre.modelos.controles.BotonDisparar;
import com.juegotematicalibre.modelos.controles.BotonDefender;
import com.juegotematicalibre.modelos.controles.BotonEspecial;
import com.juegotematicalibre.modelos.controles.Pad;


public class GameView extends SurfaceView implements SurfaceHolder.Callback  {
    private Pad pad;
    boolean iniciado = false;
    Context context;
    GameLoop gameloop;
    private BotonDefender botonDefender;
    private BotonDisparar botonDisparar;
    private BotonEspecial botonEspecial;
    public IconoVida[] iconosVida;
    public static int pantallaAncho;
    public static int pantallaAlto;
    public GestorAudio gestorAudio;
    public boolean juegoGanado;
    //nivel maximo = 6
    public int nivelMaximo = 6;

    private Nivel nivel;
    public int numeroNivel = 0;

    public GameView(Context context) {
        super(context);
        gestorAudio = GestorAudio.getInstancia();
        gestorAudio.reproducirMusicaInicio();
        gestorAudio.reproducirMusicaAmbiente();
        gestorAudio.registrarSonido(GestorAudio.SONIDO_MUERTE,
                R.raw.grito_muerte);
        gestorAudio.registrarSonido(GestorAudio.SONIDO_ESPADA,
                R.raw.sonido_espada);
        gestorAudio.registrarSonido(GestorAudio.SONIDO_YEAH,
                R.raw.mmm_yeah);
        gestorAudio.registrarSonido(GestorAudio.SONIDO_MUERTE_ZOMBIE,
                R.raw.zombie_die);
        gestorAudio.registrarSonido(GestorAudio.SONIDO_INVOCACION,
                R.raw.sonido_invocacion);
        gestorAudio.registrarSonido(GestorAudio.SONIDO_CORAZON,
                R.raw.latido_corazon);
        gestorAudio.registrarSonido(GestorAudio.SONIDO_CAJA,R.raw.sonido_caja);
        iniciado = true;

        getHolder().addCallback(this);
        setFocusable(true);

        this.context = context;
        gameloop = new GameLoop(this);
        gameloop.setRunning(true);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // valor a Binario
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        // Indice del puntero
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

        int pointerId  = event.getPointerId(pointerIndex);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                accion[pointerId] = ACTION_DOWN;
                x[pointerId] = event.getX(pointerIndex);
                y[pointerId] = event.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                accion[pointerId] = ACTION_UP;
                x[pointerId] = event.getX(pointerIndex);
                y[pointerId] = event.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                for(int i =0; i < pointerCount; i++){
                    pointerIndex = i;
                    pointerId  = event.getPointerId(pointerIndex);
                    accion[pointerId] = ACTION_MOVE;
                    x[pointerId] = event.getX(pointerIndex);
                    y[pointerId] = event.getY(pointerIndex);
                }
                break;
        }

        procesarEventosTouch();
        return true;
    }

    int NO_ACTION = 0;
    int ACTION_MOVE = 1;
    int ACTION_UP = 2;
    int ACTION_DOWN = 3;
    int accion[] = new int[6];
    float x[] = new float[6];
    float y[] = new float[6];

    public void procesarEventosTouch(){
        boolean pulsacionPadMover = false;

        for(int i=0; i < 6; i++){
            if(accion[i] != NO_ACTION ) {

                if(accion[i] == ACTION_DOWN){
                    if(nivel.nivelPausado) {
                        nivel.nivelPausado = false;
                        if(nivel.ultimoNivel && juegoGanado){
                            juegoCompleto();
                        }
                    }

                }
                if (botonDisparar.estaPulsado(x[i], y[i])) {
                    if (accion[i] == ACTION_DOWN) {
                        nivel.botonDispararPulsado = true;
                    }
                }

                if (botonDefender.estaPulsado(x[i], y[i])) {
                    if (accion[i] == ACTION_DOWN) {
                        nivel.botonDefenderPulsado = true;
                    }
                }
                if (botonEspecial.estaPulsado(x[i], y[i])) {
                    if (accion[i] == ACTION_DOWN) {
                        nivel.botonEspecialPulsado = true;
                    }
                }

                if (pad.estaPulsado(x[i], y[i])) {
                    float orientacionX = pad.getOrientacionX(x[i]);
                    float orientacionY = pad.getOrientacionY(y[i]);

                    // Si almenosuna pulsacion está en el pad
                    if (accion[i] != ACTION_UP) {
                        pulsacionPadMover = true;
                        nivel.orientacionPadX = orientacionX;
                        nivel.orientacionPadY = orientacionY;
                    }
                }
            }
        }
        if(!pulsacionPadMover) {
            nivel.orientacionPadX = 0;
            nivel.orientacionPadY = 0;
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("Tecla","Tecla pulsada: "+keyCode);

        if( keyCode == 62) {
            nivel.botonDispararPulsado = true;
        }
        if( keyCode == 33) {
            nivel.botonEspecialPulsado = true;
        }
        if( keyCode == 34) {
            nivel.botonDefenderPulsado = true;
        }
        if( keyCode == 32) {
            nivel.orientacionPadX = -10.5f;
        }
        if( keyCode == 29) {
            nivel.orientacionPadX = 10.5f;
        }
        if( keyCode == 47) {
            nivel.orientacionPadY = -10.5f;
        }
        if( keyCode == 51) {
            nivel.orientacionPadY = 10.5f;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        if( keyCode == 32 || keyCode == 29) {
            nivel.orientacionPadX = 0;
        }
        if(keyCode == 47 || keyCode == 51){
            nivel.orientacionPadY = 0;
        }
        return super.onKeyDown(keyCode, event);
    }



    protected void inicializar() throws Exception {
        nivel = new Nivel(context,numeroNivel);
        nivel.gameView = this;
        pad = new Pad(context);
        botonDefender = new BotonDefender(context);
        botonDisparar = new BotonDisparar(context);
        botonEspecial = new BotonEspecial(context);
        iconosVida = new IconoVida[5];

        iconosVida[0] = new IconoVida(context, GameView.pantallaAncho*0.05,
                GameView.pantallaAlto*0.1);
        iconosVida[1] = new IconoVida(context, GameView.pantallaAncho*0.15,
                GameView.pantallaAlto*0.1);
        iconosVida[2] = new IconoVida(context, GameView.pantallaAncho*0.25,
                GameView.pantallaAlto*0.1);
        iconosVida[3] = new IconoVida(context, GameView.pantallaAncho*0.35,
                GameView.pantallaAlto*0.1);
        iconosVida[4] = new IconoVida(context, GameView.pantallaAncho*0.45,
                GameView.pantallaAlto*0.1);
    }

    public void actualizar(long tiempo) throws Exception {
        if (!nivel.nivelPausado) {
            nivel.actualizar(tiempo);
        }
    }

    protected void dibujar(Canvas canvas) {
        nivel.dibujar(canvas);
        if(!nivel.nivelPausado){
            pad.dibujar(canvas);
            botonDefender.dibujar(canvas);
            botonDisparar.dibujar(canvas);
            botonEspecial.dibujar(canvas);
            for(int i=0; i < nivel.jugador.vidas; i++)
                iconosVida[i].dibujar(canvas);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        pantallaAncho = width;
        pantallaAlto = height;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (iniciado) {
            iniciado = false;
            if (gameloop.isAlive()) {
                iniciado = true;
                gameloop = new GameLoop(this);
            }

            gameloop.setRunning(true);
            gameloop.start();
        } else {
            iniciado = true;
            gameloop = new GameLoop(this);
            gameloop.setRunning(true);
            gameloop.start();
        }
    }

    public void nivelCompleto() throws Exception {

        numeroNivel++;

        int vidas = nivel.jugador.vidas;
        nivel = new Nivel(context, numeroNivel);
        if(numeroNivel==nivelMaximo)
            nivel.ultimoNivel=true;
        nivel.gameView = this;
        nivel.jugador.vidas = vidas;
    }

    public void juegoCompleto() {
        gestorAudio.reproducirMusicaInicio();
        gestorAudio.pararMusicaAmbiente();
        ((InicioActivity)context).finish();
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        iniciado = false;

        boolean intentarDeNuevo = true;
        gameloop.setRunning(false);
        while (intentarDeNuevo) {
            try {
                gameloop.join();
                intentarDeNuevo = false;
            }
            catch (InterruptedException e) {
            }
        }
    }

}

