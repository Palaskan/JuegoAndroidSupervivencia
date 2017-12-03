package com.juegotematicalibre;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.juegotematicalibre.gestores.GestorAudio;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pantalla completa, sin título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Context c = getApplicationContext();
        GestorAudio gestorAudio = GestorAudio.getInstancia(c, R.raw.mistery,R.raw.zombies);
        gestorAudio.reproducirMusicaInicio();
        gestorAudio.pararMusicaAmbiente();
    }

    @Override
    protected void onPause() {
        if (GestorAudio.getInstancia() != null){
            GestorAudio.getInstancia().pararMusicaAmbiente();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (GestorAudio.getInstancia() != null){
            GestorAudio.getInstancia().reproducirMusicaAmbiente();
        }
        super.onResume();
    }


    public void jugar(View v){
        Intent actividadJuego = new Intent(this,InicioActivity.class);
        startActivity(actividadJuego);
    }
}