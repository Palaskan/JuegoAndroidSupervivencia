package com.juegotematicalibre.gestores;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;

import com.juegotematicalibre.R;

import java.util.HashMap;

/**
 * Created by UO223663 on 04/10/2017.
 */

public class GestorAudio implements OnPreparedListener {

    public static final int SONIDO_MUERTE = 0;
    public static final int SONIDO_ESPADA = 1;
    public static final int SONIDO_YEAH = 2;
    public static final int SONIDO_MUERTE_ZOMBIE =3;
    public static final int SONIDO_INVOCACION = 4;
    public static final int SONIDO_CAJA = 5;
    public static final int SONIDO_CORAZON = 6;
    // Pool de sonidos, para efectos de sonido.
    // Suele fallar el utilizar ficheros de sonido demasiado grandes
    private SoundPool poolSonidos;
    private HashMap<Integer, Integer> mapSonidos;
    private Context contexto;
    // Media Player para bucle de sonido de fondo.
    private MediaPlayer sonidoAmbiente;
    private MediaPlayer sonidoInicio;
    private AudioManager gestorAudio;


    private static GestorAudio instancia = null;

    public static GestorAudio getInstancia(Context contexto,int idInicio,
                                           int idMusicaAmbiente) {
        synchronized (GestorAudio.class) {
            if (instancia == null) {
                instancia = new GestorAudio();
                instancia.initSounds(contexto, idInicio,idMusicaAmbiente);
            }
            return instancia;
        }
    }

    public static GestorAudio getInstancia() {
        return instancia;
    }

    private GestorAudio() {
    }

    public void initSounds(Context contexto, int idInicio, int idMusicaAmbiente) {
        this.contexto = contexto;
        poolSonidos = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        mapSonidos = new HashMap<Integer, Integer>();
        gestorAudio = (AudioManager) contexto
                .getSystemService(Context.AUDIO_SERVICE);
        sonidoAmbiente = MediaPlayer.create(contexto, idMusicaAmbiente);
        sonidoAmbiente.setLooping(true);
        sonidoAmbiente.setVolume(0.15f, 0.15f);
        sonidoInicio = MediaPlayer.create(contexto, idInicio);
        sonidoInicio.setLooping(true);
        sonidoInicio.setVolume(0.3f, 0.3f);
    }

    public void reproducirMusicaAmbiente() {
        try {
            if (!sonidoAmbiente.isPlaying()) {
                try {
                    sonidoAmbiente.setOnPreparedListener(this);
                    sonidoAmbiente.prepareAsync();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    public void reproducirMusicaInicio() {
        try {
            if (!sonidoInicio.isPlaying()) {
                try {
                    sonidoInicio.setOnPreparedListener(this);
                    sonidoInicio.prepareAsync();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    public void pararMusicaAmbiente() {
        if (sonidoAmbiente.isPlaying()) {
            sonidoAmbiente.stop();
        }
    }

    public void pararMusicaInicio() {
        if (sonidoInicio.isPlaying()) {
            sonidoInicio.stop();
        }
    }

    public void registrarSonido(int index, int SoundID) {
        mapSonidos.put(index, poolSonidos.load(contexto, SoundID, 1));
    }

    public void reproducirSonido(int index) {
        float volumen =
                gestorAudio.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumen =
                volumen / gestorAudio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        poolSonidos.play(
                (Integer) mapSonidos.get(index),
                volumen, volumen, 1, 0, 1f);
    }


    public void onPrepared(MediaPlayer mp){
        mp.start();
    }


    }
