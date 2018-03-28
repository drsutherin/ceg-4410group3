package ceg4410.wright.edu.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openHelpScreen(View view){
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }

    public void openStudentLobby(View view){
        Intent intent = new Intent(this, StudentLobby.class);
        startActivity(intent);
    }
}
