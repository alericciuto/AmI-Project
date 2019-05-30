package com.example.kmapp;


import java.lang.Runnable;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;

import com.google.gson.JsonElement;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.speech.tts.TextToSpeech;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ConversationActivity extends AppCompatActivity implements AIListener {

    private TextView resultTextView;
    private AIService aiService;
    private int conversation_enable=0;
    private int speech_error_limiter;
    private TextToSpeech TTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TTS = MainActivity.tts;
        setContentView(R.layout.activity_conversation);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        final AIConfiguration config = new AIConfiguration("b23a9af0092b49de8bb3976eb20f33ad",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        speech_error_limiter=0;
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.US);
                    Toast.makeText(getApplicationContext(), "Text To Speech Initialized", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activationCall();
    }

    protected void activationCall(){
        Handler a = new Handler();
        a.post(new Runnable() {
            @Override
            public void run() {
                aiService.startListening();
            }
        });
        a.postDelayed(new Runnable() {
            @Override
            public void run() {
                TTS.speak("Lifesaver Protocol", TextToSpeech.QUEUE_FLUSH, null, null);
                while(TTS.isSpeaking()){}
            }
        }, 600);
    }

    public void onResult(final AIResponse response) {
        speech_error_limiter = 0;
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }
        //conversazione da iniziare
        if(conversation_enable==0 && result.getAction().compareTo("input.welcome")==0){
            conversation_enable=1;
            handleResult(result.getFulfillment().getSpeech());
            return;
        }
        //conversazione iniziata
        else if(conversation_enable==1 && result.getAction().compareTo("input.welcome")==0){
            resultTextView.setText("Your conversation is still running!");
            String reply = "Your conversation is still running!";
            handleResult(reply);
            return;
        }
        //conversazione non iniziata e domanda non di ingresso
        else if(conversation_enable==0 && result.getAction().compareTo("input.welcome")!=0){
            String reply = "You have to start the conversation first!";
            resultTextView.setText(reply);
            handleResult(reply);
            return;
        }
        //conversazione iniziata e domanda di uscita
        else if(conversation_enable==1 && result.getAction().compareTo("input.quitting")==0){
            conversation_enable=0;
            String reply = result.getFulfillment().getSpeech();
            handleExit(reply);
            return;
        }
        //conversazione intermedia
        else if(conversation_enable==1 && result.getAction().compareTo("input.welcome")!=0){
            ResponseMessage.ResponseSpeech r = null;
            StringBuilder sb = new StringBuilder();
            String reply, reply2;
            if(result.getFulfillment().getSpeech().compareTo("")!=0){
                sb.append(result.getFulfillment().getSpeech());
                reply = sb.toString();
            }
            else{
                int l= result.getFulfillment().getMessages().size();
                for(int i=0; i<l; i++){
                    r = (ResponseMessage.ResponseSpeech)result.getFulfillment().getMessages().get(i);
                    sb.append(r.getSpeech().toString());
                    sb.append("\n");
                }
                reply2 = "Query:" + result.getResolvedQuery() +"\n" + sb.toString();
                resultTextView.setText(reply2);
                reply = sb.toString();
            }

            handleResult(reply);
            return;
        }
    }
    protected void handleResult(final String reply){
        final Handler a = new Handler();
        a.post(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText(reply);
            }
        });
        a.postDelayed(new Runnable() {
            @Override
            public void run() {
                TTS.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null);
                a.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        while(TTS.isSpeaking()){}
                        aiService.startListening();
                    }
                }, 600);
            }
        }, 800);
    }
    protected void handleExit(final String reply) {
        final Handler a = new Handler();
        a.post(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText(reply);
            }
        });
        a.postDelayed(new Runnable() {
            @Override
            public void run() {
                TTS.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null);
                a.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        while(TTS.isSpeaking()){}
                        endActivity();
                    }
                }, 600);
            }
        }, 800);
    }
    protected void endActivity() {
        this.finish();
    }

    @Override
    public void onError(final AIError error) {
        if(speech_error_limiter==1){
            Intent intent = new Intent(this, MusicPostConversation.class);
            startActivity(intent);
            this.finish();
        }
        else{
            speech_error_limiter= speech_error_limiter+1;
            final String reply = error.toString();
            final Handler a = new Handler();
            a.post(new Runnable() {
                @Override
                public void run() {
                    resultTextView.setText(reply);
                }
            });
            a.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TTS.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null);
                    a.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            while(TTS.isSpeaking()){}
                            aiService.startListening();
                        }
                    }, 600);
                }
            }, 600);
        }
    }
    @Override
    public void onListeningStarted() {}
    @Override
    public void onListeningCanceled() {}
    @Override
    public void onListeningFinished() {}
    @Override
    public void onAudioLevel(final float level) {}
}
