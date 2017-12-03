package com.juegotematicalibre.modelos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.juegotematicalibre.GameView;
import com.juegotematicalibre.InicioActivity;
import com.juegotematicalibre.R;
import com.juegotematicalibre.gestores.CargadorGraficos;
import com.juegotematicalibre.gestores.GestorAudio;
import com.juegotematicalibre.gestores.Utilidades;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Nivel {
    public static int scrollEjeX = 0;
    public static int scrollEjeY = 0;
    public float orientacionPadX = 0;
    public float orientacionPadY = 0;
    public boolean botonDefenderPulsado = false;
    public boolean botonDispararPulsado = false;
    public boolean botonEspecialPulsado = false;
    public GameView gameView;
    private Context context = null;
    private int numeroNivel;
    private Fondo fondo;
    private Tile[][] mapaTiles;
    public Jugador jugador;
    public boolean inicializado;
    private List<Enemigo> enemigos;
    private List<Disparo> disparosJugador;
    private List<HabilidadEspecial> habilidadesEspeciales;
    private List<Disparo> disparosAborigen;
    private List<Recolectable> recolectables;
    private List<PuntoSalvado> puntosSalvado;
    public Bitmap mensaje ;
    public Bitmap ganador ;
    public boolean nivelPausado;
    public boolean ultimoNivel;
    private double msEspecial = 0;
    private List<Map<String,Object>> recolectablesOcultos;
    private GestorAudio gestorAudio;

    private boolean juegoGanado;

    private Portal portal;

    public Nivel(Context context, int numeroNivel) throws Exception {
        inicializado = false;
        gestorAudio = GestorAudio.getInstancia();

        this.context = context;
        this.numeroNivel = numeroNivel;
        inicializar();

        inicializado = true;
    }

    public void inicializar()throws Exception {
        ganador= CargadorGraficos.cargarBitmap(context,R.drawable.you_win);
        mensaje = CargadorGraficos.cargarBitmap(context, R.drawable.description);
        nivelPausado = true;
        disparosJugador = new LinkedList<Disparo>();
        disparosAborigen = new LinkedList<Disparo>();
        recolectables = new LinkedList<Recolectable>();
        puntosSalvado = new LinkedList<PuntoSalvado>();
        recolectablesOcultos = new LinkedList<Map<String, Object>>();
        habilidadesEspeciales = new LinkedList<HabilidadEspecial>();
        enemigos = new LinkedList<Enemigo>();
        fondo = new Fondo(context,CargadorGraficos.cargarBitmap(context,
                R.drawable.capa0), 0);
        inicializarMapaTiles();
        scrollEjeX = 0;
        scrollEjeY = altoMapaTiles()*Tile.altura-(int)(GameView.pantallaAlto*0.7);
    }

    private void inicializarMapaTiles() throws Exception {
        InputStream is = context.getAssets().open(numeroNivel+".txt");
        int anchoLinea;

        List<String> lineas = new LinkedList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        {
            String linea = reader.readLine();
            anchoLinea = linea.length();
            while (linea != null)
            {
                lineas.add(linea);
                if (linea.length() != anchoLinea)
                {
                    Log.e("ERROR", "Dimensiones incorrectas en la línea");
                    throw new Exception("Dimensiones incorrectas en la línea.");
                }
                linea = reader.readLine();
            }
        }

        // Inicializar la matriz
        mapaTiles = new Tile[anchoLinea][lineas.size()];
        // Iterar y completar todas las posiciones
        for (int y = 0; y < altoMapaTiles(); ++y) {
            for (int x = 0; x < anchoMapaTiles(); ++x) {
                char tipoDeTile = lineas.get(y).charAt(x);//lines[y][x];
                mapaTiles[x][y] = inicializarTile(tipoDeTile,x,y);
            }
        }
    }

    private Tile inicializarTile(char codigoTile,int x, int y) {
        int xCentroAbajoTileE;
        int yCentroAbajoTileE;
        Drawable imagen = CargadorGraficos.cargarDrawable(context, R.drawable.suelo);
        switch (codigoTile) {
            case 'Z':
                // Enemigo
                // Posición centro abajo
                xCentroAbajoTileE = x * Tile.ancho + Tile.ancho/2;
                yCentroAbajoTileE = y * Tile.altura + Tile.altura;
                enemigos.add(new Zombie(context,xCentroAbajoTileE,yCentroAbajoTileE));

                return new Tile(imagen, Tile.PASABLE);
            case 'B':
                // Enemigo
                // Posición centro abajo
                xCentroAbajoTileE = x * Tile.ancho + Tile.ancho/2;
                yCentroAbajoTileE = y * Tile.altura + Tile.altura;
                enemigos.add(new Aborigen(context,xCentroAbajoTileE,yCentroAbajoTileE));

                return new Tile(imagen, Tile.PASABLE);
            case 'P':
                int xCentroAbajoTileP = x * Tile.ancho + Tile.ancho/2;
                int yCentroAbajoTileP = y * Tile.altura + Tile.altura;
                portal = new Portal(context,xCentroAbajoTileP,yCentroAbajoTileP);

                return new Tile(imagen, Tile.PASABLE);
            case '1':
                // Jugador
                // Posicion centro abajo
                int xCentroAbajoTile = x * Tile.ancho + Tile.ancho/2;
                int yCentroAbajoTile = y * Tile.altura + Tile.altura;
                jugador = new Jugador(context,xCentroAbajoTile,yCentroAbajoTile);

                return new Tile(imagen,Tile.PASABLE);
            case '#':
                // bloque de musgo, no se puede pasar
                return new Tile(
                        CargadorGraficos.cargarDrawable(context, R.drawable.rock)
                        , Tile.SOLIDO);
            case 'R':
                Map<String,Object> map = new HashMap<String,Object>();
                int xCentroAbajoTileR = x * Tile.ancho + Tile.ancho/2;
                int yCentroAbajoTileR = y * Tile.altura + Tile.altura;
                Recolectable gema = new RecolectableGema(context,xCentroAbajoTileR,yCentroAbajoTileR,true);
                recolectables.add(gema);
                map.put("x",x);
                map.put("y",y);
                map.put("gema",gema);
                recolectablesOcultos.add(map);
                return new Tile(
                        CargadorGraficos.cargarDrawable(context, R.drawable.caja)
                        , Tile.ROMPIBLE);
            case '^':
                // bloque de musgo, no se puede pasar
                return new Tile(
                        CargadorGraficos.cargarDrawable(context, R.drawable.tree)
                        , Tile.SOLIDO);
            case 'A':
                int xCentroAbajoTileA = x * Tile.ancho + Tile.ancho/2;
                int yCentroAbajoTileA = y * Tile.altura + Tile.altura;
                puntosSalvado.add(new PuntoSalvado(context,xCentroAbajoTileA,yCentroAbajoTileA));

                return new Tile(imagen, Tile.PASABLE);
            case 'C':
                return new Tile(
                        CargadorGraficos.cargarDrawable(context, R.drawable.lago)
                        , Tile.RELENTIZABLE);
            default:
                //cualquier otro caso
                return new Tile(imagen, Tile.PASABLE);
        }
    }

    public void actualizar (long tiempo) throws Exception {
        if (inicializado) {
            for(Disparo disparoJugador: disparosJugador){
                disparoJugador.actualizar(tiempo);
            }
            for(HabilidadEspecial esp : habilidadesEspeciales)
                esp.actualizar(tiempo);

            long t = System.currentTimeMillis();
            for(Enemigo enemigo: enemigos){
                if(gameView.nivelMaximo/2 <= gameView.numeroNivel){
                    enemigo.vXIni = Math.abs(jugador.velocidadX) - 1;
                    enemigo.vYIni = Math.abs(jugador.velocidadY) - 1;
                }
                enemigo.actualizar(tiempo);
                if(jugadorCercaX(enemigo)!= 1 || jugadorCercaY(enemigo)!=1 ||
                        (enemigo.velocidadY == 0 || enemigo.velocidadX==0) ){
                    enemigo.velocidadX = enemigo.vXIni;
                    enemigo.velocidadY = enemigo.vYIni;
                    enemigo.velocidadX = enemigo.velocidadX * jugadorCercaX(enemigo);
                    enemigo.velocidadY = enemigo.velocidadY * jugadorCercaY(enemigo);
                }
                if(Math.abs(enemigo.y - jugador.y)<10 && Math.abs(enemigo.x - jugador.x)<10){
                    enemigo.velocidadX =0;
                    enemigo.velocidadY = 0;
                }
                if(Math.abs(enemigo.y - jugador.y)<10){
                    enemigo.velocidadY = 0;
                }
                if(Math.abs(enemigo.x - jugador.x)<10){
                    enemigo.velocidadX=0;
                }

                if(jugadorDelante(enemigo)){
                    Disparo disparoA;
                    if(enemigo.velocidadX>0)
                        disparoA= enemigo.disparar(t,1);
                    else{
                        disparoA= enemigo.disparar(t,-1);
                    }
                    if(disparoA != null)
                        disparosAborigen.add(disparoA);
                }

            }
            for(Disparo disparoAborigen : disparosAborigen){
                disparoAborigen.actualizar(tiempo);
            }

            jugador.procesarOrdenes(orientacionPadX,orientacionPadY,botonDispararPulsado,botonDefenderPulsado,botonEspecialPulsado);
            if (botonDefenderPulsado) {
                botonDefenderPulsado = false;
            }

            if (botonDispararPulsado) {
                disparosJugador.add(new DisparoJugadorX(context,jugador.x,jugador.y, jugador.orientacion));
                disparosJugador.add(new DisparoJugadorY(context,jugador.x,jugador.y));
                gestorAudio.reproducirSonido(GestorAudio.SONIDO_ESPADA);
                botonDispararPulsado = false;
            }
            if(msEspecial <=0) {
                if (botonEspecialPulsado) {
                    msEspecial = jugador.msEspecial;
                    gestorAudio.reproducirSonido(GestorAudio.SONIDO_INVOCACION);
                    habilidadesEspeciales.add(new HabilidadEspecial(context, jugador.x, jugador.y, jugador.orientacion));
                    botonEspecialPulsado = false;
                }
            }
            if (msEspecial > 0) {
                msEspecial -= tiempo;
                botonEspecialPulsado = false;
            }

            for(Recolectable r :recolectables){
                r.actualizar(tiempo);
            }
            jugador.actualizar(tiempo);
            if(portal!= null)
                portal.actualizar(tiempo);
            aplicarReglasMovimiento();
        }
    }

    private boolean jugadorDelante(Enemigo enemigo){
        if(enemigo.velocidadX >0 && enemigo.x < jugador.x &&
                Math.abs(enemigo.y - jugador.y)<20 && Math.abs(enemigo.x - jugador.x)<300 ){

            return true;
        }
        if(enemigo.velocidadX <0 && enemigo.x > jugador.x &&
            Math.abs(enemigo.y - jugador.y)<20 && Math.abs(enemigo.x - jugador.x)<300){

            return true;
        }
        return false;
    }


    private int jugadorCercaX(Enemigo enemigo){
        if(enemigo.velocidadX <0 && enemigo.x < jugador.x && Math.abs(enemigo.x - jugador.x)<enemigo.captacion){
            return -1;
        }
        if(enemigo.velocidadX >0 && enemigo.x > jugador.x && Math.abs(enemigo.x - jugador.x)<enemigo.captacion){
            return -1;
        }
        return 1;
    }

    private int jugadorCercaY(Enemigo enemigo){
        if(enemigo.velocidadY <0 && enemigo.y < jugador.y &&
                Math.abs(enemigo.y - jugador.y)<enemigo.captacion){
            return -1;
        }
        if(enemigo.velocidadY >0 && enemigo.y > jugador.y &&
                Math.abs(enemigo.y - jugador.y)<enemigo.captacion){
            return -1;
        }
        return 1;
    }

    private void aplicarReglasMovimiento() throws Exception {

        int tileXJugadorIzquierda
                = (int) (jugador.x - (jugador.ancho / 2 - 1)) / Tile.ancho;
        int tileXJugadorDerecha
                = (int) (jugador.x + (jugador.ancho / 2 - 1)) / Tile.ancho;

        int tileYJugadorInferior
                = (int) (jugador.y + (jugador.altura / 2 - 1)) / Tile.altura;
        int tileYJugadorCentro
                = (int) jugador.y / Tile.altura;
        int tileYJugadorSuperior
                = (int) (jugador.y - (jugador.altura / 2 - 1)) / Tile.altura;



        for (Iterator<Enemigo> iterator = enemigos.iterator(); iterator.hasNext();) {
            Enemigo enemigo = iterator.next();

            if (enemigo.estado == Enemigo.ELIMINAR){
                iterator.remove();
                continue;
            }

            if(enemigo.estado != Enemigo.ACTIVO)
                continue;

            int tileXEnemigoIzquierda =
                    (int)(enemigo.x - ( enemigo.ancho/2 - 1)) / Tile.ancho;
            int tileXEnemigoDerecha =
                    (int)(enemigo.x + (enemigo.ancho/2 -1))/ Tile.ancho ;

            int tileYEnemigoInferior =
                    (int) (enemigo.y  + (enemigo.altura/2 - 1)) / Tile.altura;
            int tileYEnemigoCentro =
                    (int) enemigo.y  / Tile.altura;
            int tileYEnemigoSuperior =
                    (int) (enemigo.y  - (enemigo.altura/2 - 1)) / Tile.altura;

            int rango = 4;
            if (tileXJugadorIzquierda - rango < tileXEnemigoIzquierda &&
                    tileXJugadorIzquierda + rango > tileXEnemigoIzquierda){

                if(jugador.colisiona(enemigo)){

                    if(jugador.golpeado() <= 0) {
                        nivelPausado = true;
                        gameView.numeroNivel = 0;
                        mensaje = CargadorGraficos.cargarBitmap(context, R.drawable.you_lose);
                        jugador.restablecerPosicionInicial();
                        for(Enemigo e:enemigos){
                            e.restablecerPosicionInicial();
                        }
                        scrollEjeX = 0;
                        scrollEjeY = altoMapaTiles()*Tile.altura-(int)(GameView.pantallaAlto*0.7);
                        return;
                    }
                    else {
                        if (jugador.vidas == 1){
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_CORAZON);
                        }
                    }
                }
                if(portal!= null && enemigo.colisiona(portal)){
                    enemigo.destruir();
                    iterator.remove();
                    break;
                }
            }


            if(enemigo.velocidadX > 0){
                //  Solo una condicion para pasar:  Tile delante libre, el de abajo solido
                if (tileXEnemigoDerecha + 1 <= anchoMapaTiles() - 1 &&
                        (mapaTiles[tileXEnemigoDerecha + 1][tileYEnemigoInferior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoDerecha + 1][tileYEnemigoCentro].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoDerecha + 1][tileYEnemigoSuperior].tipoDeColision ==
                                Tile.PASABLE)) {

                    enemigo.x += enemigo.velocidadX;

                    // Sino, me acerco al borde del que estoy
                } else if (tileXEnemigoDerecha + 1 <= anchoMapaTiles() - 1 ) {

                    int TileEnemigoDerecho = tileXEnemigoDerecha*Tile.ancho + Tile.ancho ;
                    double distanciaX = TileEnemigoDerecho - (enemigo.x +  enemigo.ancho/2);

                    if( distanciaX  > 0) {
                        double velocidadNecesaria = Math.min(distanciaX, enemigo.velocidadX);
                        enemigo.x += velocidadNecesaria;
                    } else {
                        enemigo.girarX();
                    }

                    // No hay Tile, o es el final del mapa
                } else {
                    enemigo.girarX();
                }
            }

            if(enemigo.velocidadX < 0){
                // Solo una condición para pasar: Tile izquierda pasable y suelo solido.
                if (tileXEnemigoIzquierda - 1 >= 0 &&
                        (mapaTiles[tileXEnemigoIzquierda-1][tileYEnemigoInferior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoIzquierda-1][tileYEnemigoCentro].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXEnemigoIzquierda-1][tileYEnemigoSuperior].tipoDeColision ==
                                Tile.PASABLE )) {

                    enemigo.x += enemigo.velocidadX;

                    // Solido / borde del tile acercarse.
                } else if (tileXEnemigoIzquierda -1  >= 0 ) {

                    int TileEnemigoIzquierdo= tileXEnemigoIzquierda*Tile.ancho ;
                    double distanciaX =  (enemigo.x -  enemigo.ancho/2) - TileEnemigoIzquierdo;

                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Utilidades.proximoACero(-distanciaX, enemigo.velocidadX);
                        enemigo.x += velocidadNecesaria;
                    } else {
                        enemigo.girarX();
                    }
                } else {
                    enemigo.girarX();
                }
            }


            if(enemigo.velocidadY < 0){
                // Tile superior PASABLE
                // Podemos seguir moviendo hacia arriba
                if (tileYEnemigoSuperior-1 >= 0 &&
                        (mapaTiles[tileXEnemigoIzquierda][tileYEnemigoSuperior-1].tipoDeColision
                                == Tile.PASABLE
                        && mapaTiles[tileXEnemigoDerecha][tileYEnemigoSuperior-1].tipoDeColision
                        == Tile.PASABLE)){

                    enemigo.y +=  enemigo.velocidadY;

                    // Tile superior != de PASABLE
                    // O es un tile SOLIDO, o es el TECHO del mapa
                } else {

                    // Si en el propio tile del jugador queda espacio para
                    // subir más, subo
                    int TileEnemigoBordeSuperior = (tileYEnemigoSuperior)*Tile.altura;
                    double distanciaY =  (enemigo.y - enemigo.altura/2) - TileEnemigoBordeSuperior;

                    if( distanciaY  > 0) {
                        enemigo.y += Utilidades.proximoACero(-distanciaY, enemigo.velocidadY);

                    } else {
                        enemigo.girarY();
                    }

                }
            }

            // Hacia abajo
            if (enemigo.velocidadY >= 0) {
                // Tile inferior PASABLE
                // Podemos seguir moviendo hacia abajo
                if (tileYEnemigoInferior + 1 <= altoMapaTiles() - 1 &&
                        (mapaTiles[tileXEnemigoIzquierda][tileYEnemigoInferior + 1].tipoDeColision
                                == Tile.PASABLE
                        && mapaTiles[tileXEnemigoDerecha][tileYEnemigoInferior + 1].tipoDeColision
                        == Tile.PASABLE)) {
                    // si los dos están libres cae

                    enemigo.y += enemigo.velocidadY;
                    // Tile inferior SOLIDO
                    // El ULTIMO, es un caso especial

                } else if (tileYEnemigoInferior + 1 <= altoMapaTiles() - 1 &&
                        (mapaTiles[tileXEnemigoIzquierda][tileYEnemigoInferior + 1].tipoDeColision
                                == Tile.SOLIDO ||
                                mapaTiles[tileXEnemigoDerecha][tileYEnemigoInferior + 1].tipoDeColision ==
                                        Tile.SOLIDO ||
                                mapaTiles[tileXEnemigoIzquierda][tileYEnemigoInferior + 1].tipoDeColision
                                        == Tile.RELENTIZABLE ||
                                mapaTiles[tileXEnemigoDerecha][tileYEnemigoInferior + 1].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXEnemigoIzquierda][tileYEnemigoInferior + 1].tipoDeColision
                                        == Tile.RELENTIZABLE ||
                                mapaTiles[tileXEnemigoDerecha][tileYEnemigoInferior + 1].tipoDeColision ==
                                        Tile.RELENTIZABLE)) {

                    // Con que uno de los dos sea solido ya no puede caer
                    // Si en el propio tile del jugador queda espacio para bajar más, bajo
                    int TileEnemigoBordeInferior =
                            tileYEnemigoInferior * Tile.altura + Tile.altura;

                    double distanciaY =
                            TileEnemigoBordeInferior - (enemigo.y + enemigo.altura / 2);

                    if (distanciaY > 0) {
                        enemigo.y += Math.min(distanciaY, enemigo.velocidadY);

                    } else {
                        enemigo.girarY();
                    }
                }
            }



        }
        if(jugador.defendiendo){
            jugador.velocidadX = 0;
            jugador.velocidadY = 0;
        }
        else if(jugador.especial){
            jugador.velocidadX = 0;
            jugador.velocidadY = 0;
        }
        else {
            // Hacia arriba
            if (jugador.velocidadY < 0) {
                // Tile superior PASABLE
                // Podemos seguir moviendo hacia arriba
                if (tileYJugadorSuperior - 1 >= 0 &&
                        (mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior - 1].tipoDeColision
                                == Tile.PASABLE
                                && mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior - 1].tipoDeColision
                                == Tile.PASABLE ||
                                mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior - 1].tipoDeColision
                                        == Tile.RELENTIZABLE
                                || mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior - 1].tipoDeColision
                                == Tile.RELENTIZABLE)) {
                    if(mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior].tipoDeColision
                            == Tile.RELENTIZABLE
                            || mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior].tipoDeColision
                            == Tile.RELENTIZABLE){
                        jugador.y += jugador.velocidadY/2;
                    }
                    else{
                        jugador.y += jugador.velocidadY;
                    }


                    // Tile superior != de PASABLE
                    // O es un tile SOLIDO, o es el TECHO del mapa
                } else {

                    // Si en el propio tile del jugador queda espacio para
                    // subir más, subo
                    int TileJugadorBordeSuperior = (tileYJugadorSuperior) * Tile.altura;
                    double distanciaY = (jugador.y - jugador.altura / 2) - TileJugadorBordeSuperior;

                    if (distanciaY > 0) {
                        jugador.y += Utilidades.proximoACero(-distanciaY, jugador.velocidadY);

                    } else {
                        jugador.velocidadY = 0;
                    }

                }
            }

            // Hacia abajo
            if (jugador.velocidadY >= 0) {
                // Tile inferior PASABLE
                // Podemos seguir moviendo hacia abajo
                // NOTA - El ultimo tile es especial (caer al vacío )
                if (tileYJugadorInferior + 1 <= altoMapaTiles() - 1 &&
                        (mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior + 1].tipoDeColision
                                == Tile.PASABLE
                                && mapaTiles[tileXJugadorDerecha][tileYJugadorInferior + 1].tipoDeColision
                                == Tile.PASABLE ||
                                mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior + 1].tipoDeColision
                                == Tile.RELENTIZABLE
                                || mapaTiles[tileXJugadorDerecha][tileYJugadorInferior + 1].tipoDeColision
                                == Tile.RELENTIZABLE)
                        ) {

                    if(mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior].tipoDeColision
                            == Tile.RELENTIZABLE
                            || mapaTiles[tileXJugadorDerecha][tileYJugadorInferior].tipoDeColision
                            == Tile.RELENTIZABLE){
                        jugador.y += jugador.velocidadY/2;
                    }
                    else{
                        jugador.y += jugador.velocidadY;
                    }

                    // Tile inferior SOLIDO
                    // El ULTIMO, es un caso especial

                } else if (tileYJugadorInferior + 1 <= altoMapaTiles() - 1 &&
                        (mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior + 1].tipoDeColision
                                == Tile.SOLIDO ||
                                mapaTiles[tileXJugadorDerecha][tileYJugadorInferior + 1].tipoDeColision ==
                                        Tile.SOLIDO ||mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior + 1].tipoDeColision
                                == Tile.ROMPIBLE ||
                                mapaTiles[tileXJugadorDerecha][tileYJugadorInferior + 1].tipoDeColision ==
                                        Tile.ROMPIBLE)) {

                    // Con que uno de los dos sea solido ya no puede caer
                    // Si en el propio tile del jugador queda espacio para bajar más, bajo
                    int TileJugadorBordeInferior =
                            tileYJugadorInferior * Tile.altura + Tile.altura;

                    double distanciaY =
                            TileJugadorBordeInferior - (jugador.y + jugador.altura / 2);

                    if (distanciaY > 0) {
                        jugador.y += Math.min(distanciaY, jugador.velocidadY);

                    } else {
                        // Toca suelo, nos aseguramos de que está bien
                        jugador.y = TileJugadorBordeInferior - jugador.altura / 2;
                        jugador.velocidadY = 0;
                    }

                }
            }


            // derecha o parado
            if (jugador.velocidadX > 0) {
                // Tengo un tile delante y es PASABLE
                // El tile de delante está dentro del Nivel
                if (tileXJugadorDerecha + 1 <= anchoMapaTiles() - 1 &&
                        tileYJugadorInferior <= altoMapaTiles() - 1 &&
                        (mapaTiles[tileXJugadorDerecha + 1][tileYJugadorInferior].tipoDeColision ==
                                Tile.PASABLE &&
                                mapaTiles[tileXJugadorDerecha + 1][tileYJugadorCentro].tipoDeColision ==
                                        Tile.PASABLE &&
                                mapaTiles[tileXJugadorDerecha + 1][tileYJugadorSuperior].tipoDeColision ==
                                        Tile.PASABLE &&
                                mapaTiles[tileXJugadorDerecha][tileYJugadorInferior].tipoDeColision ==
                                        Tile.PASABLE &&
                                mapaTiles[tileXJugadorDerecha][tileYJugadorCentro].tipoDeColision ==
                                        Tile.PASABLE &&
                                mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior].tipoDeColision ==
                                        Tile.PASABLE ||
                                mapaTiles[tileXJugadorDerecha+1][tileYJugadorInferior].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorDerecha+1][tileYJugadorCentro].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorDerecha+1][tileYJugadorSuperior].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorDerecha][tileYJugadorInferior].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorDerecha][tileYJugadorCentro].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior].tipoDeColision ==
                                        Tile.RELENTIZABLE)) {

                    if(mapaTiles[tileXJugadorDerecha][tileYJugadorInferior].tipoDeColision ==
                            Tile.RELENTIZABLE ||
                            mapaTiles[tileXJugadorDerecha][tileYJugadorCentro].tipoDeColision ==
                                    Tile.RELENTIZABLE ||
                            mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior].tipoDeColision ==
                                    Tile.RELENTIZABLE){
                        jugador.x += jugador.velocidadX/2;
                    }
                    else{
                        jugador.x += jugador.velocidadX;
                    }


                    // No tengo un tile PASABLE delante
                    // o es el FINAL del nivel o es uno SOLIDO
                } else if (tileXJugadorDerecha <= anchoMapaTiles() - 1 &&
                        tileYJugadorInferior <= altoMapaTiles() - 1 &&
                        mapaTiles[tileXJugadorDerecha][tileYJugadorInferior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXJugadorDerecha][tileYJugadorCentro].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXJugadorDerecha][tileYJugadorSuperior].tipoDeColision ==
                                Tile.PASABLE) {

                    // Si en el propio tile del jugador queda espacio para
                    // avanzar más, avanzo
                    int TileJugadorBordeDerecho = tileXJugadorDerecha * Tile.ancho + Tile.ancho;
                    double distanciaX = TileJugadorBordeDerecho - (jugador.x + jugador.ancho / 2);

                    if (distanciaX > 0) {
                        double velocidadNecesaria = Math.min(distanciaX, jugador.velocidadX);
                        jugador.x += velocidadNecesaria;
                    } else {
                        // Opcional, corregir posición
                        jugador.x = TileJugadorBordeDerecho - jugador.ancho / 2;
                    }
                }
            }

            // izquierda
            if (jugador.velocidadX <= 0) {
                // Tengo un tile detrás y es PASABLE
                // El tile de delante está dentro del Nivel
                if (tileXJugadorIzquierda - 1 >= 0 &&
                        tileYJugadorInferior < altoMapaTiles() - 1 &&
                        (mapaTiles[tileXJugadorIzquierda - 1][tileYJugadorInferior].tipoDeColision ==
                                Tile.PASABLE &&
                                mapaTiles[tileXJugadorIzquierda - 1][tileYJugadorCentro].tipoDeColision ==
                                        Tile.PASABLE &&
                                mapaTiles[tileXJugadorIzquierda - 1][tileYJugadorSuperior].tipoDeColision ==
                                        Tile.PASABLE &&
                                mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior].tipoDeColision ==
                                        Tile.PASABLE &&
                                mapaTiles[tileXJugadorIzquierda][tileYJugadorCentro].tipoDeColision ==
                                        Tile.PASABLE &&
                                mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior].tipoDeColision ==
                                        Tile.PASABLE ||
                                mapaTiles[tileXJugadorIzquierda - 1][tileYJugadorInferior].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorIzquierda - 1][tileYJugadorCentro].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorIzquierda - 1][tileYJugadorSuperior].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorIzquierda][tileYJugadorCentro].tipoDeColision ==
                                        Tile.RELENTIZABLE ||
                                mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior].tipoDeColision ==
                                        Tile.RELENTIZABLE)) {
                    if(mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior].tipoDeColision ==
                            Tile.RELENTIZABLE ||
                            mapaTiles[tileXJugadorIzquierda][tileYJugadorCentro].tipoDeColision ==
                                    Tile.RELENTIZABLE ||
                            mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior].tipoDeColision ==
                                    Tile.RELENTIZABLE ){
                        jugador.x += jugador.velocidadX/2;
                    }
                    else{
                        jugador.x += jugador.velocidadX;
                    }


                    // No tengo un tile PASABLE detrás
                    // o es el INICIO del nivel o es uno SOLIDO
                } else if (tileXJugadorIzquierda >= 0 && tileYJugadorInferior <= altoMapaTiles() - 1 &&
                        mapaTiles[tileXJugadorIzquierda][tileYJugadorInferior].tipoDeColision
                                == Tile.PASABLE &&
                        mapaTiles[tileXJugadorIzquierda][tileYJugadorCentro].tipoDeColision
                                == Tile.PASABLE &&
                        mapaTiles[tileXJugadorIzquierda][tileYJugadorSuperior].tipoDeColision
                                == Tile.PASABLE) {

                    // Si en el propio tile del jugador queda espacio para
                    // avanzar más, avanzo
                    int TileJugadorBordeIzquierdo = tileXJugadorIzquierda * Tile.ancho;
                    double distanciaX = (jugador.x - jugador.ancho / 2) - TileJugadorBordeIzquierdo;

                    if (distanciaX > 0) {
                        double velocidadNecesaria = Utilidades.proximoACero(-distanciaX, jugador.velocidadX);
                        jugador.x += velocidadNecesaria;
                    } else {
                        // Opcional, corregir posición
                        jugador.x = TileJugadorBordeIzquierdo + jugador.ancho / 2;
                    }
                }
            }
        }



        for (Iterator<HabilidadEspecial> iterator = habilidadesEspeciales.iterator(); iterator.hasNext();) {

            HabilidadEspecial habilidadEspecial = iterator.next();

            int tileXHabilidad = (int)habilidadEspecial.x / Tile.ancho ;

            int tileYHabilidadInferior =
                    (int) (habilidadEspecial.y  + habilidadEspecial.cAbajo) / Tile.altura;
            int tileYHabilidadSuperior =
                    (int) (habilidadEspecial.y  - habilidadEspecial.cArriba)  / Tile.altura;

            for(Enemigo enemigo : enemigos){
                if (habilidadEspecial.colisiona(enemigo)){
                    if(enemigo instanceof Zombie)
                        gestorAudio.reproducirSonido(GestorAudio.SONIDO_MUERTE_ZOMBIE);
                    else
                        gestorAudio.reproducirSonido(GestorAudio.SONIDO_MUERTE);
                    enemigo.destruir();
                }
            }

            //derecha
            if(habilidadEspecial.velocidadX > 0){
                // Tiene delante un tile pasable, puede avanzar.

                if (tileXHabilidad+1 <= anchoMapaTiles()-1 &&
                        mapaTiles[tileXHabilidad+1][tileYHabilidadInferior].tipoDeColision
                                == Tile.PASABLE &&
                        mapaTiles[tileXHabilidad+1][tileYHabilidadSuperior].tipoDeColision
                                == Tile.PASABLE ||
                        mapaTiles[tileXHabilidad+1][tileYHabilidadInferior].tipoDeColision
                                == Tile.RELENTIZABLE &&
                                mapaTiles[tileXHabilidad+1][tileYHabilidadSuperior].tipoDeColision
                                        == Tile.RELENTIZABLE){
                    habilidadEspecial.x +=  habilidadEspecial.velocidadX;

                } else if (tileXHabilidad <= anchoMapaTiles() - 1){
                    int TileDisparoBordeDerecho = tileXHabilidad*Tile.ancho + Tile.ancho ;

                    double distanciaX =
                            TileDisparoBordeDerecho - (habilidadEspecial.x +  habilidadEspecial.cDerecha);
                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Math.min(distanciaX, habilidadEspecial.velocidadX);
                        habilidadEspecial.x += velocidadNecesaria;

                    } else {
                        if(mapaTiles[tileXHabilidad+1][tileYHabilidadInferior].imagen != CargadorGraficos.cargarDrawable(context,R.drawable.tree) &&
                                mapaTiles[tileXHabilidad+1][tileYHabilidadSuperior].imagen != CargadorGraficos.cargarDrawable(context,R.drawable.tree)) {
                            mapaTiles[tileXHabilidad + 1][tileYHabilidadInferior].imagen
                                    = CargadorGraficos.cargarDrawable(context, R.drawable.suelo);
                            mapaTiles[tileXHabilidad + 1][tileYHabilidadInferior].tipoDeColision = Tile.PASABLE;
                            mapaTiles[tileXHabilidad + 1][tileYHabilidadSuperior].imagen
                                    = CargadorGraficos.cargarDrawable(context, R.drawable.suelo);
                            mapaTiles[tileXHabilidad + 1][tileYHabilidadSuperior].tipoDeColision = Tile.PASABLE;
                        }
                        iterator.remove();
                        continue;
                    }
                }
            }


            // izquierda
            if (habilidadEspecial.velocidadX <= 0){
                if (tileXHabilidad-1 >= 0 &&
                        tileYHabilidadSuperior < altoMapaTiles()-1 &&
                        mapaTiles[tileXHabilidad-1][tileYHabilidadSuperior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXHabilidad-1][tileYHabilidadInferior].tipoDeColision ==
                                Tile.PASABLE ||
                        mapaTiles[tileXHabilidad-1][tileYHabilidadSuperior].tipoDeColision ==
                                Tile.RELENTIZABLE &&
                                mapaTiles[tileXHabilidad-1][tileYHabilidadInferior].tipoDeColision ==
                                        Tile.RELENTIZABLE){

                    habilidadEspecial.x +=  habilidadEspecial.velocidadX;
                    // No tengo un tile PASABLE detras
                    // o es el INICIO del nivel o es uno SOLIDO
                } else if(tileXHabilidad >= 0 ){
                    // Si en el propio tile del jugador queda espacio para
                    // avanzar más, avanzo
                    int TileHabilidadBordeIzquierdo = tileXHabilidad*Tile.ancho ;
                    double distanciaX =
                            (habilidadEspecial.x - habilidadEspecial.cIzquierda) - TileHabilidadBordeIzquierdo ;
                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Utilidades.proximoACero(-distanciaX, habilidadEspecial.velocidadX);
                        habilidadEspecial.x += velocidadNecesaria;
                    } else {
                        if(mapaTiles[tileXHabilidad-1][tileYHabilidadInferior].imagen != CargadorGraficos.cargarDrawable(context,R.drawable.tree) &&
                                mapaTiles[tileXHabilidad-1][tileYHabilidadSuperior].imagen != CargadorGraficos.cargarDrawable(context,R.drawable.tree))
                        {
                            mapaTiles[tileXHabilidad - 1][tileYHabilidadInferior].imagen
                                    = CargadorGraficos.cargarDrawable(context, R.drawable.suelo);
                            mapaTiles[tileXHabilidad - 1][tileYHabilidadInferior].tipoDeColision = Tile.PASABLE;
                            mapaTiles[tileXHabilidad - 1][tileYHabilidadSuperior].imagen
                                    = CargadorGraficos.cargarDrawable(context, R.drawable.suelo);
                            mapaTiles[tileXHabilidad - 1][tileYHabilidadSuperior].tipoDeColision = Tile.PASABLE;
                        }
                        iterator.remove();
                        continue;
                    }
                }
            }
        }




        for (Iterator<Disparo> iterator = disparosJugador.iterator(); iterator.hasNext();) {

            Disparo disparoJugador = iterator.next();

            int tileXDisparo = (int)disparoJugador.x / Tile.ancho ;
            int tileYDisparo = (int) disparoJugador.y/Tile.altura;

            int tileYDisparoInferior =
                    (int) (disparoJugador.y  + disparoJugador.cAbajo) / Tile.altura;
            int tileYDisparoSuperior =
                    (int) (disparoJugador.y  - disparoJugador.cArriba)  / Tile.altura;

            int tileXDisparoIzquierdo =
                    (int) (disparoJugador.x - disparoJugador.cIzquierda)/Tile.ancho;
            int tileXDisparoCentro =
                    (int) (disparoJugador.x)/Tile.ancho;
            int tileXDisparoDerecho =
                    (int) (disparoJugador.x + disparoJugador.cDerecha)/Tile.ancho;

            for(Enemigo enemigo : enemigos){
                if (disparoJugador.colisiona(enemigo)) {
                    enemigo.vida--;
                    if (enemigo.vida == 0){
                        if(enemigo instanceof Zombie)
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_MUERTE_ZOMBIE);
                        else if(enemigo instanceof Aborigen)
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_MUERTE);
                        enemigo.destruir();
                    }
                    else if(enemigo.vida>0){
                        gestorAudio.reproducirSonido(GestorAudio.SONIDO_MUERTE);
                    }
                    disparoJugador.tiempoVida= -75;
                    iterator.remove();
                    break;
                }
            }
            if(disparoJugador.tiempoVida<=0 && disparoJugador.tiempoVida>-75){
                iterator.remove();
                continue;
            }
            //derecha
            if(disparoJugador.velocidadX > 0){
                // Tiene delante un tile pasable, puede avanzar.

                if (tileXDisparo+1 <= anchoMapaTiles()-1 &&
                        mapaTiles[tileXDisparo+1][tileYDisparoInferior].tipoDeColision
                                == Tile.PASABLE &&
                        mapaTiles[tileXDisparo+1][tileYDisparoSuperior].tipoDeColision
                                == Tile.PASABLE ){
                    disparoJugador.x +=  disparoJugador.velocidadX;

                } else if (tileXDisparo <= anchoMapaTiles() - 1){
                    int TileDisparoBordeDerecho = tileXDisparo*Tile.ancho + Tile.ancho ;

                    double distanciaX =
                            TileDisparoBordeDerecho - (disparoJugador.x +  disparoJugador.cDerecha);
                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Math.min(distanciaX, disparoJugador.velocidadX);
                        disparoJugador.x += velocidadNecesaria;

                    } else {
                        if(mapaTiles[tileXDisparo+1][tileYDisparoInferior].tipoDeColision
                                == Tile.ROMPIBLE){
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_CAJA);
                            mapaTiles[tileXDisparo+1][tileYDisparoInferior].tipoDeColision = Tile.PASABLE;
                            mapaTiles[tileXDisparo+1][tileYDisparoInferior].imagen = CargadorGraficos.cargarDrawable(context,R.drawable.suelo);
                            noDebajo(tileXDisparo+1,tileYDisparoInferior);
                        }
                        if(mapaTiles[tileXDisparo+1][tileYDisparoSuperior].tipoDeColision
                                == Tile.ROMPIBLE){
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_CAJA);
                            mapaTiles[tileXDisparo+1][tileYDisparoSuperior].tipoDeColision = Tile.PASABLE;
                            mapaTiles[tileXDisparo+1][tileYDisparoSuperior].imagen = CargadorGraficos.cargarDrawable(context,R.drawable.suelo);
                            noDebajo(tileXDisparo+1,tileYDisparoSuperior);
                        }
                        iterator.remove();
                        continue;
                    }
                }
            }


            // izquierda
            if (disparoJugador.velocidadX < 0){
                if (tileXDisparo-1 >= 0 &&
                        tileYDisparoSuperior < altoMapaTiles()-1 &&
                        mapaTiles[tileXDisparo-1][tileYDisparoSuperior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXDisparo-1][tileYDisparoInferior].tipoDeColision ==
                                Tile.PASABLE){

                    disparoJugador.x +=  disparoJugador.velocidadX;
                    // No tengo un tile PASABLE detras
                    // o es el INICIO del nivel o es uno SOLIDO
                } else if(tileXDisparo >= 0 ){
                    // Si en el propio tile del jugador queda espacio para
                    // avanzar más, avanzo
                    int TileDisparoBordeIzquierdo = tileXDisparo*Tile.ancho ;
                    double distanciaX =
                            (disparoJugador.x - disparoJugador.cIzquierda) - TileDisparoBordeIzquierdo ;
                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Utilidades.proximoACero(-distanciaX, disparoJugador.velocidadX);
                        disparoJugador.x += velocidadNecesaria;
                    } else {
                        if(mapaTiles[tileXDisparo-1][tileYDisparoInferior].tipoDeColision
                                == Tile.ROMPIBLE){
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_CAJA);
                            mapaTiles[tileXDisparo-1][tileYDisparoInferior].tipoDeColision = Tile.PASABLE;
                            mapaTiles[tileXDisparo-1][tileYDisparoInferior].imagen = CargadorGraficos.cargarDrawable(context,R.drawable.suelo);
                            noDebajo(tileXDisparo-1,tileYDisparoInferior);
                        }
                        if(mapaTiles[tileXDisparo-1][tileYDisparoSuperior].tipoDeColision
                                == Tile.ROMPIBLE){
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_CAJA);
                            mapaTiles[tileXDisparo-1][tileYDisparoSuperior].tipoDeColision = Tile.PASABLE;
                            mapaTiles[tileXDisparo-1][tileYDisparoSuperior].imagen = CargadorGraficos.cargarDrawable(context,R.drawable.suelo);
                            noDebajo(tileXDisparo-1,tileYDisparoSuperior);
                        }
                        iterator.remove();
                        continue;
                    }
                }
            }

            // Hacia arriba
            if (disparoJugador.velocidadY < 0) {
                // Tile superior PASABLE
                // Podemos seguir moviendo hacia arriba
                if (tileYDisparoSuperior - 1 >= 0 &&
                        (mapaTiles[tileXDisparoIzquierdo][tileYDisparoSuperior - 1].tipoDeColision
                                == Tile.PASABLE
                                && mapaTiles[tileXDisparoDerecho][tileYDisparoSuperior - 1].tipoDeColision
                                == Tile.PASABLE)) {
                    disparoJugador.y += disparoJugador.velocidadY;


                    // Tile superior != de PASABLE
                    // O es un tile SOLIDO, o es el TECHO del mapa
                } else {

                    // Si en el propio tile del jugador queda espacio para
                    // subir más, subo
                    int TileDisparoBordeSuperior = (tileYDisparoSuperior) * Tile.altura;
                    double distanciaY = (disparoJugador.y - disparoJugador.cArriba) - TileDisparoBordeSuperior;

                    if (distanciaY > 0) {
                        disparoJugador.y += Utilidades.proximoACero(-distanciaY, disparoJugador.velocidadY);
                    } else {
                        if(mapaTiles[tileXDisparoIzquierdo][tileYDisparoSuperior-1].tipoDeColision
                                == Tile.ROMPIBLE){
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_CAJA);
                            mapaTiles[tileXDisparoIzquierdo][tileYDisparoSuperior-1].tipoDeColision = Tile.PASABLE;
                            mapaTiles[tileXDisparoIzquierdo][tileYDisparoInferior-1].imagen = CargadorGraficos.cargarDrawable(context,R.drawable.suelo);
                            noDebajo(tileXDisparoIzquierdo,tileYDisparoInferior-1);
                        }
                        if(mapaTiles[tileXDisparoDerecho][tileYDisparoSuperior-1].tipoDeColision
                                == Tile.ROMPIBLE){
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_CAJA);
                            mapaTiles[tileXDisparoDerecho][tileYDisparoSuperior-1].tipoDeColision = Tile.PASABLE;
                            mapaTiles[tileXDisparoDerecho][tileYDisparoSuperior-1].imagen = CargadorGraficos.cargarDrawable(context,R.drawable.suelo);
                            noDebajo(tileXDisparoDerecho,tileYDisparoSuperior-1);
                        }
                        iterator.remove();
                        continue;
                    }

                }
            }



        }
        for (Iterator<Disparo> iterator = disparosAborigen.iterator(); iterator.hasNext();) {

            Disparo disparoAborigen = iterator.next();

            int tileXDisparo = (int)disparoAborigen.x / Tile.ancho ;
            int tileYDisparo = (int) disparoAborigen.y/Tile.altura;

            int tileYDisparoInferior =
                    (int) (disparoAborigen.y  + disparoAborigen.cAbajo) / Tile.altura;
            int tileYDisparoSuperior =
                    (int) (disparoAborigen.y  - disparoAborigen.cArriba)  / Tile.altura;


            if (disparoAborigen.colisiona(jugador)){
                if(disparoAborigen instanceof HechizoAborigen){
                    if (jugador.golpeadoFuerte() <= 0) {
                        nivelPausado = true;
                        mensaje = CargadorGraficos.cargarBitmap(context, R.drawable.you_lose);
                        jugador.restablecerPosicionInicial();
                        for (Enemigo e : enemigos) {
                            e.restablecerPosicionInicial();
                        }
                        scrollEjeX = 0;
                        scrollEjeY = altoMapaTiles() * Tile.altura - (int) (GameView.pantallaAlto * 0.7);
                        return;
                    }
                    else{
                        if(jugador.vidas==1){
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_CORAZON);
                        }
                    }
                }
                else {
                    if (jugador.golpeado() <= 0) {
                        nivelPausado = true;
                        mensaje = CargadorGraficos.cargarBitmap(context, R.drawable.you_lose);
                        jugador.restablecerPosicionInicial();
                        for (Enemigo e : enemigos) {
                            e.restablecerPosicionInicial();
                        }
                        scrollEjeX = 0;
                        scrollEjeY = altoMapaTiles() * Tile.altura - (int) (GameView.pantallaAlto * 0.7);
                        return;
                    }
                    else{
                        if(jugador.vidas==1) {
                            gestorAudio.reproducirSonido(GestorAudio.SONIDO_CORAZON);
                        }
                    }
                }
                iterator.remove();
                break;
            }


            //derecha
            if(disparoAborigen.velocidadX > 0){
                // Tiene delante un tile pasable, puede avanzar.

                if (tileXDisparo+1 <= anchoMapaTiles()-1 &&
                        mapaTiles[tileXDisparo+1][tileYDisparoInferior].tipoDeColision
                                == Tile.PASABLE &&
                        mapaTiles[tileXDisparo+1][tileYDisparoSuperior].tipoDeColision
                                == Tile.PASABLE ||
                        mapaTiles[tileXDisparo+1][tileYDisparoInferior].tipoDeColision
                                == Tile.RELENTIZABLE &&
                                mapaTiles[tileXDisparo+1][tileYDisparoSuperior].tipoDeColision
                                        == Tile.RELENTIZABLE){
                    disparoAborigen.x +=  disparoAborigen.velocidadX;
                } else if (tileXDisparo <= anchoMapaTiles() - 1){
                    int TileDisparoBordeDerecho = tileXDisparo*Tile.ancho + Tile.ancho ;

                    double distanciaX =
                            TileDisparoBordeDerecho - (disparoAborigen.x +  disparoAborigen.cDerecha);
                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Math.min(distanciaX, disparoAborigen.velocidadX);
                        disparoAborigen.x += velocidadNecesaria;

                    } else {
                        iterator.remove();
                        continue;
                    }
                }
            }


            // izquierda
            if (disparoAborigen.velocidadX <= 0){
                if (tileXDisparo-1 >= 0 &&
                        tileYDisparoSuperior < altoMapaTiles()-1 &&
                        mapaTiles[tileXDisparo-1][tileYDisparoSuperior].tipoDeColision ==
                                Tile.PASABLE &&
                        mapaTiles[tileXDisparo-1][tileYDisparoInferior].tipoDeColision ==
                                Tile.PASABLE ||
                        mapaTiles[tileXDisparo-1][tileYDisparoSuperior].tipoDeColision ==
                                Tile.RELENTIZABLE &&
                                mapaTiles[tileXDisparo-1][tileYDisparoInferior].tipoDeColision ==
                                        Tile.RELENTIZABLE){

                    disparoAborigen.x +=  disparoAborigen.velocidadX;
                    // No tengo un tile PASABLE detras
                    // o es el INICIO del nivel o es uno SOLIDO
                } else if(tileXDisparo >= 0 ){
                    // Si en el propio tile del jugador queda espacio para
                    // avanzar más, avanzo
                    int TileDisparoBordeIzquierdo = tileXDisparo*Tile.ancho ;
                    double distanciaX =
                            (disparoAborigen.x - disparoAborigen.cIzquierda) - TileDisparoBordeIzquierdo ;
                    if( distanciaX  > 0) {
                        double velocidadNecesaria =
                                Utilidades.proximoACero(-distanciaX, disparoAborigen.velocidadX);
                        disparoAborigen.x += velocidadNecesaria;
                    } else {

                        iterator.remove();
                        continue;
                    }
                }
            }
        }

        for (Iterator<Recolectable> iterator = recolectables.iterator(); iterator.hasNext();) {

            Recolectable recolectable = iterator.next();

            if (jugador.colisiona(recolectable)){
                if(!recolectable.debajo) {
                    if (jugador.vidas < jugador.vidasMaximas) {
                        gestorAudio.reproducirSonido(GestorAudio.SONIDO_YEAH);
                        jugador.vidas++;
                    }
                    iterator.remove();
                    continue;
                }
            }
        }
        for (Iterator<PuntoSalvado> iterator = puntosSalvado.iterator(); iterator.hasNext();) {

            PuntoSalvado puntoSalvado = iterator.next();

            if(jugador.colisiona(puntoSalvado)){
                if(jugador.xInicial < jugador.x) {
                    jugador.xInicial = jugador.x;
                    jugador.yInicial = jugador.y;
                }
            }
        }

        if(enemigos.size()==0) {
            if(ultimoNivel){
                nivelPausado = true;
                juegoGanado = true;
                gameView.juegoGanado=true;
            }else
                gameView.nivelCompleto();
        }
    }

    private void noDebajo(int x,int y){
        for(Recolectable r:recolectables){
            for(Map<String,Object> m : recolectablesOcultos){
                if((int)m.get("x") == x && (int)m.get("y") == y &&
                        ((Recolectable)m.get("gema")).x == r.x && ((Recolectable)m.get("gema")).y == r.y){
                    r.debajo = false;
                    return;
                }
            }

        }
    }

    public void dibujar (Canvas canvas) {
        if(inicializado) {
            fondo.dibujar(canvas);


            for(Recolectable recolectable : recolectables)
                recolectable.dibujar(canvas);

            dibujarTiles(canvas);
            for(Recolectable recolectable : recolectables)
                recolectable.dibujar(canvas);
            for(Disparo disparoJugador: disparosJugador)
                disparoJugador.dibujar(canvas);
            for(Disparo disparoAborigen: disparosAborigen)
                disparoAborigen.dibujar(canvas);
            for(PuntoSalvado puntoSalvado : puntosSalvado)
                puntoSalvado.dibujar(canvas);
            for(HabilidadEspecial esp : habilidadesEspeciales)
                esp.dibujar(canvas);
            if(portal!=null)
                portal.dibujar(canvas);
            jugador.dibujar(canvas);

            for(Enemigo enemigo: enemigos)
                enemigo.dibujar(canvas);

            if (nivelPausado && !juegoGanado){
                // la foto mide 480x320
                Rect orgigen = new Rect(0,0 ,
                        480,320);

                Paint efectoTransparente = new Paint();
                efectoTransparente.setAntiAlias(true);

                Rect destino = new Rect((int)(GameView.pantallaAncho/2 - 480/2),
                        (int)(GameView.pantallaAlto/2 - 320/2),
                        (int)(GameView.pantallaAncho/2 + 480/2),
                        (int)(GameView.pantallaAlto/2 + 320/2));

                canvas.drawBitmap(mensaje,orgigen,destino, null);
            }

            if(ultimoNivel && juegoGanado){
                // la foto mide 480x320
                Rect orgigen = new Rect(0,0 ,
                        480,320);

                Paint efectoTransparente = new Paint();
                efectoTransparente.setAntiAlias(true);

                Rect destino = new Rect((int)(GameView.pantallaAncho/2 - 480/2),
                        (int)(GameView.pantallaAlto/2 - 320/2),
                        (int)(GameView.pantallaAncho/2 + 480/2),
                        (int)(GameView.pantallaAlto/2 + 320/2));

                canvas.drawBitmap(ganador,orgigen,destino, null);
            }
        }
    }


    private void dibujarTiles(Canvas canvas){
        // Calcular que tiles serán visibles en la pantalla
        // La matriz de tiles es más grande que la pantalla

        int tileXJugador = (int) jugador.x / Tile.ancho;
        int izquierda = (int) (tileXJugador - tilesEnDistanciaX(jugador.x - scrollEjeX));
        izquierda = Math.max(0,izquierda); // Que nunca sea < 0, ej -1

        if ( jugador .x  < anchoMapaTiles()* Tile.ancho - GameView.pantallaAncho*0.3 )
            if( jugador .x - scrollEjeX > GameView.pantallaAncho * 0.7 ){
                fondo.mover((int) jugador .x - scrollEjeX - GameView.pantallaAncho* 0.7 +1);
                scrollEjeX = (int) ((jugador .x ) - GameView.pantallaAncho* 0.7);
            }
        if ( jugador .y  < altoMapaTiles()* Tile.altura - GameView.pantallaAlto*0.3 )
            if( jugador .y - scrollEjeY > GameView.pantallaAlto * 0.7 ){
                fondo.mover((int) jugador .y - scrollEjeY - GameView.pantallaAlto* 0.7 +1);
                scrollEjeY = (int) ((jugador .y ) - GameView.pantallaAlto* 0.7);
            }


        if ( jugador .x  > GameView.pantallaAncho*0.3 )
            if( jugador .x - scrollEjeX < GameView.pantallaAncho *0.3 ){
                fondo.mover((int) jugador .x - scrollEjeX - GameView.pantallaAncho*0.3 +1);
                scrollEjeX = (int)(jugador .x - GameView.pantallaAncho*0.3);
            }

        if ( jugador .y  > GameView.pantallaAlto*0.3 )
            if( jugador .y - scrollEjeY < GameView.pantallaAlto *0.3 ){
                fondo.mover((int) jugador .y - scrollEjeY - GameView.pantallaAlto*0.3 +1);
                scrollEjeY = (int)(jugador .y - GameView.pantallaAlto*0.3);
            }

        int derecha = izquierda +
                GameView.pantallaAncho / Tile.ancho + 1;

        // el ultimo tile visible, no puede superar el tamaño del mapa
        derecha = Math.min(derecha, anchoMapaTiles() - 1);


        for (int y = 0; y < altoMapaTiles() ; ++y) {
            for (int x = izquierda; x <= derecha; ++x) {
                if (mapaTiles[x][y].imagen != null) {
                    // Calcular la posición en pantalla correspondiente
                    // izquierda, arriba, derecha , abajo
                    mapaTiles[x][y].imagen.setBounds(
                            (x  * Tile.ancho) - scrollEjeX,
                            y * Tile.altura - scrollEjeY,
                            (x * Tile.ancho) + Tile.ancho - scrollEjeX,
                            y * Tile.altura + Tile.altura - scrollEjeY);
                    mapaTiles[x][y].imagen.draw(canvas);
                }
            }
        }
    }

    private float tilesEnDistanciaX(double distanciaX){
        return (float) distanciaX/Tile.ancho;
    }

    public int anchoMapaTiles(){
        return mapaTiles.length;
    }

    public int altoMapaTiles(){
        return mapaTiles[0].length;
    }


}

