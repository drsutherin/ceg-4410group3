/*
 *  Much of this code was shamelessly copied from
 *  http://stacktips.com/tutorials/android/speech-to-text-in-android
 *  by Don Miller
 *  Network code by Joshua Luckenbill
 *
 */

package ceg4410.wright.edu.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class TeacherMain extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ArrayList<String> result;
    private CEG4410_Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        server = new CEG4410_Server();
        //server.connectionList = new ArrayList(100);
        Runnable broadcastHandler = null;
        try {
            broadcastHandler = server.new BroadcastHandler(server.convertIntToBytes(5000), InetAddress.getByName("255.255.255.255"), 4445);
        } catch (Exception e) {

        }
        new Thread(broadcastHandler).start();
        Runnable connectionHandler = server.new ConnectionHandler();
        new Thread(connectionHandler).start();

        mVoiceInputTv = findViewById(R.id.voiceInput);
        mVoiceInputTv.setMovementMethod(new ScrollingMovementMethod());
        Button mSpeakBtn = findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
    }


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk to the phone!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException ignored) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));
                    System.out.println("Converting speech to text...");
                    String text = mVoiceInputTv.getText().toString();
                    System.out.println("Speech: " + text);
                    try {
                        for (int i = 0; i < server.outputStreamList.size(); i++) {
                            server.outputStreamList.get(i).writeUTF(text);
                            server.outputStreamList.get(i).flush();
                        }
                    } catch (Exception e) {
                        // Handle Exception
                    }
                }
                break;
            }

        }
    }

    public class CEG4410_Server {

        private ArrayList<Socket> connectionList;
        private ArrayList<DataOutputStream> outputStreamList;

        public CEG4410_Server() {
            connectionList = new ArrayList<Socket>();
            outputStreamList = new ArrayList<DataOutputStream>();
        }

        public ArrayList<Socket> getConnectionList() {
            return connectionList;
        }

        public ArrayList<DataOutputStream> getOutputStreamList() {
            return outputStreamList;
        }

        private byte[] convertIntToBytes(int value) {
            return new byte[] {
                    (byte)(value >> 24), // First set 8 bits (MSB)
                    (byte)(value >> 16), // Second set of 8 bits
                    (byte)(value >> 8),  // Third set of 8 bits
                    (byte)value};        // Fourth set of 8 (LSB)
        }

        public class Broadcast {

            private DatagramSocket socket;
            private DatagramPacket packet;

            Broadcast() throws SocketException {
                socket = new DatagramSocket();
                socket.setBroadcast(true);
            }

            void constructMessage(byte[] broadcastMessage, InetAddress broadcastAddress, int broadcastPort) {
                packet = new DatagramPacket(broadcastMessage, broadcastMessage.length, broadcastAddress, broadcastPort);
            }

            void broadcast() throws IOException {
                socket.send(packet);
            }

            void stopBroadcast() {
                socket.close();
            }
        } // Broadcast

        private class BroadcastHandler implements Runnable {

            private byte[] broadcastMessage;
            private InetAddress broadcastAddress;
            private int broadcastPort;

            BroadcastHandler(byte[] broadcastMessage, InetAddress broadcastAddress, int broadcastPort) {
                this.broadcastMessage = broadcastMessage;
                this.broadcastAddress = broadcastAddress;
                this.broadcastPort = broadcastPort;
            }

            @Override
            public void run() {
                try {
                    Broadcast broadcast = new Broadcast();
                    broadcast.constructMessage(broadcastMessage, broadcastAddress, broadcastPort);
                    while (true) {
                        broadcast.broadcast();
                        sleep(500);
                    }
                    //broadcast.stopBroadcast();
                } catch (IOException | InterruptedException e) {
                    // TODO: Exception handling
                }
            }
        } // BroadcastHandler

        private class ConnectionHandler implements Runnable {

            @Override
            public void run() {
                try {
                    connectionList.add(new ServerSocket(5000).accept());
                    outputStreamList.add(new DataOutputStream(connectionList.get(connectionList.size() - 1).getOutputStream()));
//                    DataOutputStream dataOut = new DataOutputStream(s.getOutputStream());
//                    while (true) {
//                        try {
//                            sleep(1000);
//                        } catch (Exception e) {
//
//                        }
//                        System.out.println("Attempting to print test string...");
//                        dataOut.writeUTF("This is a test string");
//                        dataOut.flush();
//                        System.out.println("Test string was printed...");
//                    }

                } catch (IOException e) {
                    // TODO: Exception handling
                }
            }

        } // ConnectionHandler

    } // CEG4410_Server

}
