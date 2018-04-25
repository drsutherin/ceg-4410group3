package ceg4410.wright.edu.myapplication;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import org.w3c.dom.Text;

public class StudentMain extends AppCompatActivity {

    private static final String API_KEY= "AIzaSyB_ZJ9ZHoFpIPixUQjNDguZg0FO5MSPKu4";

     TextView selectedLanguage;
     TextView toTranslate;
     TextView translated;
     TextView classroomName;
     Button translateButton;
     private String tempTranslate;
     private TranslateOptions options;
     private String lang;   //the language code rep. the selected lang
    private String srcLang;
    public String classroomNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        lang = "de";

        Bundle bundle = getIntent().getExtras();
        lang = bundle.getString("lang_code");
        classroomNameString = bundle.getString("classroom");
        srcLang = "en";

        toTranslate = (TextView)findViewById( R.id.toTranslate);
        toTranslate.setText("Do not go gentle into that good night,\n" +
                "Old age should burn and rave at close of day;\n" +
                "Rage, rage against the dying of the light.");

        translated = (TextView)findViewById( R.id.translated);

        selectedLanguage = (TextView)findViewById( R.id.selectedLang);
        selectedLanguage.setText("Selected Language: "+lang);

        classroomName = (TextView)findViewById(R.id.classroom_name);
        classroomName.setText(classroomNameString);

        translateButton = (Button) findViewById(R.id.translateButton);

        translateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                translateText(lang);
            }
        });

    }

    public String getTextToTranslate(){
        return toTranslate.getText().toString();
    }

    public void translateText(final String langCode){
        final TextView textView = (TextView) translated;
        final Handler textViewHandler = new Handler();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                options = TranslateOptions.newBuilder()
                        .setApiKey(API_KEY)
                        .build();

                Translate translate = options.getService();

                final Translation translation =
                        translate.translate(getTextToTranslate(),
                                Translate.TranslateOption.targetLanguage(langCode),
                                Translate.TranslateOption.sourceLanguage(srcLang));

                String lg = "from: "+srcLang+" to: "+langCode;
                Log.i("Language:", lg );

                textViewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (textView != null) {
                            textView.setText(translation.getTranslatedText());
                        }
                    }
                });
                return null;
            }
        }.execute();

    }

}
