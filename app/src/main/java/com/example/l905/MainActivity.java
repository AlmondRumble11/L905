package com.example.l905;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;

import java.io.FileNotFoundException;
import java.text.DateFormat;

import java.text.SimpleDateFormat;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Context context;
    private  String nimi;
    private  ListView elokuvat;
    private String ajankohta;
    private String minimi;
    private  String elokuvan_nimi;
    private EditText hakutulos;
    private EditText aika;
    private EditText pvm;
    private Spinner teatteriValikko;
    private SearchView haku;
    private TeatteriLista tl = TeatteriLista.getInstance();
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        teatteriValikko = (Spinner) findViewById(R.id.teatteriValikko);

        tl.lueXML();//saadaan teatterien tiedot

        //dopdown menun tekeminen annetuista tiedoista
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tl.getTeatterilista());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teatteriValikko.setAdapter(adapter);

        //elokuva tietojen hankkiminen
        nimi = teatteriValikko.getSelectedItem().toString();
        pvm = (EditText) findViewById(R.id.paivamaara);
        ajankohta = (String) pvm.getText().toString();


        haku = (SearchView) findViewById(R.id.hakukentta);
    }

    public void ilmotus(String s){
        Toast.makeText(MainActivity.this, s,Toast.LENGTH_SHORT).show();
    }
    // tapahtuu kun painaa haku nappia
    public void nappi(View v) {
        //aloitus ajat jos käyttjäjä ei antanut mitään aikaväliä
        String[] ajat={"00","00"};


        nimi = teatteriValikko.getSelectedItem().toString();
        pvm = (EditText) findViewById(R.id.paivamaara);
        aika = (EditText) findViewById(R.id.ajankohta);
        minimi = (String)aika.getText().toString();
        ajankohta = (String) pvm.getText().toString();

        //elokuvan_nimi = haku.getQuery().toString();
        hakutulos = (EditText)findViewById(R.id.editText);
        elokuvan_nimi = hakutulos.getText().toString();
        try {

            //jos käyttäjä on jättänyt  pvm antamatta
            if (ajankohta.isEmpty()) {
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = new Date();
                System.out.println(dateFormat.format(date));
                ajankohta = dateFormat.format(date);

            }
            if (minimi.isEmpty()) {

                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                DateFormat aika = new SimpleDateFormat("dd.MM.yyyy");
                Date date = new Date();
                System.out.println(dateFormat.format(date));

                //aloitus ajankohta on nykyinen aika
                if(ajankohta.contains(aika.format(date))) {
                    ajat[0] = dateFormat.format(date);
                }else {
                    ajat[0] = "00:00";
                }
                //lopetus ajankoha on nyt päivän viimeinen minuutti
                ajat[1] = "23:59";

            } else {
                //jos käyttäjä on osannut hommansa ja antanut tiedot
                ajat = minimi.split("-");
            }
            System.out.println("pvm " + ajankohta);

            //luetaan tiedosto halutulta päivämäärältä
            tl.luePvm(nimi, ajankohta);

            //saadaan listviewi käyttöön
            elokuvat = (ListView) findViewById(R.id.listview);

            //muodostetaan lista halutun ajankohdan elokuvista
            tl.tulostaElokuvat(ajat[0], ajat[1], elokuvan_nimi);

            //tulostetaan elokuvalista aikavälillä käyttäjälle
            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, tl.getElokuvalista());
            elokuvat.setAdapter(arrayAdapter); //tulostetaan listviewiin elokuvalistan sisätlämt tiedot
            pvm.setText("");
            aika.setText("");
            hakutulos.setText("");


            if (tl.getElokuvalista().size() == 0){
                ilmotus("Sorry no movies this time");
            }
            haku.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    arrayAdapter.getFilter().filter(s);
                    return false;
                }
            });

    }catch (ArrayIndexOutOfBoundsException aioobe){//tulee jos käyttäj ei osaa antaa oikeassa formaatissa aikaväliä
            ilmotus("Wrong timespan format");
            pvm.setText("");
            aika.setText("");
            hakutulos.setText("");
            elokuvat.setAdapter(null);
        }
    }
}

