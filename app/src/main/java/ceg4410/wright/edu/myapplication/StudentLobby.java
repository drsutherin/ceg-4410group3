package ceg4410.wright.edu.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

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



    public ArrayList<String> classrooms= new ArrayList<>();




    private String[] langStringArray;
    public String lingo;
    public String selectedClassroom;
    Spinner lang_spinner;
    Spinner classroom_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lobby);

        langStringArray = getResources().getStringArray(R.array.language_array);
        classrooms.add("Mobile Computing");
        classrooms.add("Art History");
        classrooms.add("Soft Skills 101");

        lingo ="de";    //set default lang
        selectedClassroom = "Mobile Computing";

        lang_spinner =(Spinner) findViewById(R.id.spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.language_array,android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_spinner.setAdapter(adapter);
        lang_spinner.setOnItemSelectedListener(this);

        classroom_spinner =(Spinner) findViewById(R.id.classroom_spinner);
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,classrooms);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        classroom_spinner.setAdapter(spinnerAdapter);
        classroom_spinner.setOnItemSelectedListener(this);

    }

    public void openHelpScreen(View view){
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }

    public void enterClassroom(View view){
        Intent intent = new Intent(this, StudentMain.class);
        intent.putExtra("lang_code", lingo);
        intent.putExtra("classroom", selectedClassroom);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spinner) {
            lingo = languages[pos];
            Log.i("SET:", "Language code:" + lingo);

        }
        else if(spinner.getId()==R.id.classroom_spinner){
            //I suspect it won't just return a string but hey maybe
            String room = (String) spinner.getSelectedItem();

        }
    }


    public String getLanguageString(String langCode){
        int index = java.util.Arrays.asList(languages).indexOf(langCode);
        String selectedLangString = langStringArray[index];

        return selectedLangString;
    }

    public void onNothingSelected(AdapterView<?> parent){
        //TODO: interface callback
    }
}
