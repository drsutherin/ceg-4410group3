package ceg4410.wright.edu.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class StudentLobby extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String[] languages = new String[]{
            "de",   //german
            "es",   //spanish
            "gd",   //gaelic
            "it",   //italian
            "fr",   //french
            "pt",   //portuguese
            "da"     //danish
    };
    private String lingo;
    Spinner lang_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lobby);

        lingo ="de";    //set default lang

        lang_spinner =(Spinner) findViewById(R.id.spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.language_array,android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_spinner.setAdapter(adapter);
        lang_spinner.setOnItemSelectedListener(this);
    }

    public void openHelpScreen(View view){
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        lingo = languages[pos];
        Log.i("SET:", "Language code:"+lingo);

    }

    public void onNothingSelected(AdapterView<?> parent){
        //TODO: interface callback
    }
}
