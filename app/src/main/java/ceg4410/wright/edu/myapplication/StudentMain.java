package ceg4410.wright.edu.myapplication;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import static java.lang.Thread.sleep;

import org.w3c.dom.Text;

import java.io.*;


public class StudentMain extends AppCompatActivity {

    private static final String API_KEY= "AIzaSyB_ZJ9ZHoFpIPixUQjNDguZg0FO5MSPKu4";

    TextView selectedLanguage;
    TextView toTranslate;
    TextView translated;
    Button translateButton;
    private String tempTranslate;
    private TranslateOptions options;
    private String lang;   //the language code rep. the selected lang
    private String srcLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        lang = "de";

        Bundle bundle = getIntent().getExtras();
        lang = bundle.getString("lang_code");
        srcLang = "en";

        toTranslate = (TextView)findViewById( R.id.toTranslate);
        toTranslate.setText("Do not go gentle into that good night,\n" +
                "Old age should burn and rave at close of day;\n" +
                "Rage, rage against the dying of the light.");

        translated = (TextView)findViewById( R.id.translated);

        selectedLanguage = (TextView)findViewById( R.id.selectedLang);
        selectedLanguage.setText("Selected Language: "+lang);

        translateButton = (Button) findViewById(R.id.translateButton);


        translateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                translateText(toTranslate.toString(), lang);
            }
        });

        Runnable connectionHandler = new ConnectionHandler();
        new Thread(connectionHandler).start();

    }

    public String getTextToTranslate(){
        return toTranslate.getText().toString();
    }

    public void translateText(final String toTranslate, final String langCode){
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
                        translate.translate(toTranslate,
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

    private class ConnectionHandler implements Runnable {
        private DatagramSocket broadcastSocket;
        private byte[] broadcastBuffer;
        private DatagramPacket broadcastPacket;
        private Socket server;

        @Override
        public void run() {
            try {
                System.out.println("Running Connection Handler");
                // Setup the broadcast socket
                broadcastSocket = new DatagramSocket(4445);
                broadcastBuffer = new byte[1000];
                broadcastPacket = new DatagramPacket(broadcastBuffer, broadcastBuffer.length);

                System.out.println("Listening for broadcast packet...");
                // Receive the broadcast packet
                broadcastSocket.receive(broadcastPacket);
                System.out.println("Broadcast packet received!");

                // The data contained in the broadcast packet should be the port number
                // which the server is using for client connections.
                byte[] data = broadcastPacket.getData();
                InetAddress serverIP = broadcastPacket.getAddress();

                System.out.println("Closing broadcast socket...");
                broadcastSocket.close();

                // Convert byte array to int to obtain the server port
                int serverPort = data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);

                System.out.println("Attempting to connect to the server...");
                server = new Socket(serverIP, serverPort);
                System.out.println("Connection Established!");

                DataInputStream dataIn = new DataInputStream(server.getInputStream());

                while (true) {
                    if (dataIn.available() > 0) {
                        System.out.println("Attempting to read data...");
                        System.out.print("Data: ");

                        // This is the text sent from the teacher
                        String text = dataIn.readUTF();

                        System.out.println(text);
                        System.out.println();

                        translateText(text, lang);
                    }
                }

            } catch (IOException e) {
                System.out.println("An exception occurred");
            }
        }

    } // ConnectionHandler

}
