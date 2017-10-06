package com.eet.pma.maria.quiz;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private int id_respostes[]={
            R.id.resposta1, R.id.resposta2, R.id.resposta3, R.id.resposta4
    };
    private String[] totes_preg;
    private TextView texto_pregunta;
    private RadioGroup rd;
    private Button button_check, button_ant;

    private int resposta_correcta;
    private int pregunta_actual;
    private boolean[] n_res_correct;
    private int[] resp_usuari;


    //cuando la aplicacion va a morir
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //volem guardar la pregunta acual, la resposta actual, vector on tenim si la resposta apretada es correcte, i la resposta de l'usuari
        //Log.i("lifecycle","onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putInt("Resposta_correcta",resposta_correcta);
        outState.putInt("Pregunta_actual",pregunta_actual);
        outState.putBooleanArray("N_resp_correcta",n_res_correct);
        outState.putIntArray("Reposta_usuari",resp_usuari);
    }


    //sobrecargar todos los metodos
    @Override
    protected void onStop() {
        //Log.i("lifecycle","onStop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        //Log.i("lifecycle","onStart");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        //Log.i("lifecycle","onDestroy");
        super.onDestroy();
    }

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i("lifecycle","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        texto_pregunta = (TextView) findViewById(R.id.texto_pregunta); //variable TextView
        rd = (RadioGroup) findViewById(R.id.respostes_btn);
        button_check = (Button) findViewById(R.id.btn_comprovar);
        button_ant = (Button) findViewById(R.id.btn_abans);

        totes_preg = getResources().getStringArray(R.array.totes_les_preguntes); //cridem a la matriu de totes les preguntes (string[])

        if(savedInstanceState == null){
            //primera vegada que fem servir l'app
            inicialitza();

        }else{
            Bundle state = savedInstanceState;
            resposta_correcta = state.getInt("Resposta_correcta");
            pregunta_actual = state.getInt("Pregunta_actual");
            n_res_correct = state.getBooleanArray("N_resp_correcta");
            resp_usuari = state.getIntArray("Reposta_usuari");
            mostrarPregunta();
        }

        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprovarResposta();

                if(pregunta_actual < totes_preg.length - 1){
                    pregunta_actual++;
                    mostrarPregunta();
                } else{
                    ResultatRespostes();
                }
            }
        });

        button_ant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprovarResposta();
                if(pregunta_actual > 0){
                    pregunta_actual--;
                    mostrarPregunta();
                }
            }
        });
    }

    private void inicialitza() {
        n_res_correct = new boolean[totes_preg.length]; //array on diu si cada pregunta es correcte o no
        resp_usuari = new int[totes_preg.length];
        for(int i = 0; i < resp_usuari.length; i++){
            resp_usuari[i] = -1; //inicializamos el array de las respuestas del usuario a -1 porque si ponemos 0, serà respuesta 0
        }
        pregunta_actual = 0;
        mostrarPregunta();
    }

    private void ResultatRespostes() {
        int correctes = 0, incorrectes = 0, sensecontestar = 0;
        for(int i = 0; i < totes_preg.length;i++){ //recorre el array (es como un bucle normal)
            if(n_res_correct[i])correctes++;
            else if(resp_usuari[i]==-1) sensecontestar++;
            else incorrectes++;
        }
        String missatge = String.format("Correctes: %d\nIncorrectes: %d\nNo contestades: %d",
                correctes,incorrectes,sensecontestar) ;
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //constructor de quadres de diàleg
        builder.setTitle(R.string.resultats);
        builder.setMessage(missatge);
        builder.setCancelable(false); //perquè si tenim el quadre de diàleg obert, no poguem tirar enrere
        builder.setPositiveButton(R.string.ACABA, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.tornar_a_empezar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //borrar les respostes marcades per poder fer el quiz un altre cop
                inicialitza();

            }
        });
        builder.create().show(); //crea un quadre de diàleg i el mostra
    }

    private void comprovarResposta() {
        int id = rd.getCheckedRadioButtonId();//pedirle al RadioGroup cual de los botones estan activados (-1 = cuando no hay ninguno apretado)
        int solucio = -1;

        for(int j = 0; j < id_respostes.length; j++){
            if(id_respostes[j] == id) solucio = j;
        }

        n_res_correct[pregunta_actual] = (solucio == resposta_correcta); //posem a 1 si la resposta apretada es correcte
        //if(solucio != -1) n_res_correct[solucio]=true; PETAAA!
        resp_usuari[pregunta_actual] = solucio;
    }

    private void mostrarPregunta() {
        String p = totes_preg[pregunta_actual];
        String[] parts = p.split(";"); //divideix i guarda les diferents parts de la pregunta (marcats per ;)

        rd.clearCheck(); //para que no quede marcada la opción de la pregunta anterior

        texto_pregunta.setText(parts[0]); //poner como texto la pregunta que ponemos en el strings.xml

        for(int i = 0; i < id_respostes.length; i++){
            RadioButton rb = (RadioButton) findViewById(id_respostes[i]);
            String resposta = parts[i+1]; //per posar quina resposta és correcta
            if(resposta.charAt(0) == '*'){  //buscar la diferència entre charArt i contains
                resposta_correcta = i;
                resposta = resposta.substring(1); //guarda el string desde el caracter 1, porque en 0 hay el asterisco
            }
            rb.setText(resposta); //surt el text de resposta que està a l'array (separat per comes però que ja ho hem separat anteriorment)
            if(resp_usuari[pregunta_actual] == i){
                rb.setChecked(true); //perque es quedi marcada la pregunta que haviem contestat previament
            }
        }

        if(pregunta_actual == 0){
            button_ant.setVisibility(View.GONE);
        } else{
            button_ant.setVisibility(View.VISIBLE);
        }

        //CANVIAR EL BOTÓ DE "COMPROVAR" PER EL BOTÓ "ACABA"
        if (pregunta_actual == totes_preg.length - 1){
            button_check.setText(R.string.ACABA);
        }else{
            button_check.setText(R.string.SEGÜENT);
        }
    }
}
