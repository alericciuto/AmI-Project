package com.example.kmapp;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Runnable;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
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

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import android.speech.tts.TextToSpeech;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kmapp.ResponseMessage2;

public class ConversationActivity extends AppCompatActivity implements AIListener {

    private AIService aiService;
    private int conversation_enable=0;
    private int speech_error_limiter;
    private TextToSpeech TTS;
    private String city = "";
    private String arg = "";
    private int conv_progress = 0;
    private int false_end=0;

    private RecyclerView recyclerView;
    List<ResponseMessage2> responseMessageList;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TTS = ((MyApplication) getApplicationContext()).getTTS();
        setContentView(R.layout.activity_conversation);

        recyclerView = findViewById(R.id.recyclerView);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(messageAdapter);

        final AIConfiguration config = new AIConfiguration("b23a9af0092b49de8bb3976eb20f33ad",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        speech_error_limiter=0;
        activationCall();
    }

    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = recyclerView.getAdapter().getItemCount();
        return (pos >= numItems);
    }

    protected void activationCall(){
        aiService.startListening();
        Handler a = new Handler();
        a.postDelayed(new Runnable() {
            @Override
            public void run() {
                TTS.speak("Time to wake up", TextToSpeech.QUEUE_FLUSH, null, null);
                while(TTS.isSpeaking()){}
            }
        }, 1000);
    }

    public void onResult(final AIResponse response) {
        speech_error_limiter = 0;
        false_end = 0;
        Result result = response.getResult();

        ResponseMessage2 responseMessage = new ResponseMessage2(result.getResolvedQuery(), true);
        responseMessageList.add(responseMessage);
        messageAdapter.notifyDataSetChanged();
        if (!isLastVisible())
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

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
            String reply = "Your conversation is still running!";
            handleResult(reply);
            return;
        }
        //conversazione non iniziata e domanda non di ingresso
        else if(conversation_enable==0 && result.getAction().compareTo("input.welcome")!=0){
            String reply = "You have to start the conversation first!";
            handleResult(reply);
            return;
        }
        //conversazione iniziata e domanda di uscita
        else if(conversation_enable==1 && result.getAction().compareTo("input.quitting")==0){
            String reply = result.getFulfillment().getSpeech();
            handleExit(reply);
            return;
        }
        //conversazione intermedia
        else if(conversation_enable==1 && result.getAction().compareTo("input.welcome")!=0){
            if (result.getAction().compareTo("choiceWeather") == 0) {
                if(result.getParameters().size()==0){
                    handleResult("You have to specify the city");
                }
                else {
                    city = result.getFulfillment().getSpeech();
                    conv_progress = conv_progress + 1;
                    false_end = 0;
                    Meteo meteo = new Meteo(new StringBuilder(), new StringBuilder(), city);
                    meteo.execute();

                    while(!meteo.isReady());

                    handleResult( meteo.getDataParsed() );
                    meteo.setReady( false );
                }
            }
            else if (result.getAction().compareTo("choiceNews") == 0) {
                if(result.getParameters().size()==0){
                    handleResult("You have to specify the topic");
                }
                else{
                    arg = result.getFulfillment().getSpeech();
                    conv_progress = conv_progress + 1;
                    false_end = 0;
                    News news = new News(new StringBuilder(), new StringBuilder(), arg);
                    news.execute();

                    while(!news.isReady());

                    handleResult( news.getDataParsed() );
                    news.setReady( false );
                }
            }
            else if(result.getAction().compareTo("choiceMusic") == 0){
                conv_progress = conv_progress + 1;
                false_end = 0;
                ((MyApplication) this.getApplication()).setMusicBack(true);
                Intent intent = new Intent(this, Player.class);
                startActivity(intent);
            }
            else {
                if (result.getAction().compareTo("choiceTrivia") == 0) {
                    conv_progress = conv_progress+1;
                    false_end=1;
                }
                ResponseMessage.ResponseSpeech r = null;
                List<String> sb = new ArrayList<>();
                String reply;
                if (result.getFulfillment().getSpeech().compareTo("") != 0) {
                    sb.add(result.getFulfillment().getSpeech());
                    reply = sb.stream().collect(Collectors.joining("\n"));
                } else {
                    int l = result.getFulfillment().getMessages().size();
                    for (int i = 0; i < l; i++) {
                        r = (ResponseMessage.ResponseSpeech) result.getFulfillment().getMessages().get(i);
                        sb.addAll(r.getSpeech());
                    }
                    reply = sb.stream().collect(Collectors.joining("\n"));
                }

                handleResult(reply);
                return;
            }
        }
    }
    protected void handleResult(final String reply){
        final Handler a = new Handler();
        a.post(new Runnable() {
            @Override
            public void run() {
                ResponseMessage2 responseMessage = new ResponseMessage2(reply, false);
                responseMessageList.add(responseMessage);
                messageAdapter.notifyDataSetChanged();
                if (!isLastVisible())
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
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
                        if(conv_progress>=5 && false_end==0)  {
                            handleExit("Time to focus on the road!");
                        }
                        else{
                            aiService.startListening();
                        }
                    }
                }, 800);
            }
        }, 800);
    }
    protected void handleExit(final String reply) {
        final Handler a = new Handler();
        a.post(new Runnable() {
            @Override
            public void run() {
                ResponseMessage2 responseMessage = new ResponseMessage2(reply, false);
                responseMessageList.add(responseMessage);
                messageAdapter.notifyDataSetChanged();
                if (!isLastVisible())
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });
        conversation_enable=0;
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
                }, 800);
            }
        }, 800);
    }
    protected void endActivity() {
        ((MyApplication) this.getApplication()).setRestartDetect(true);
        this.finish();
    }

    @Override
    public void onError(final AIError error) {
        if(speech_error_limiter==1){
            Intent intent = new Intent(this, MusicPostConversation.class);
            startActivity(intent);
            //this.finish();
        }
        else{
            speech_error_limiter= speech_error_limiter+1;
            final String reply = "Speech input error.";
            final Handler a = new Handler();
            a.post(new Runnable() {
                @Override
                public void run() {
                    ResponseMessage2 responseMessage = new ResponseMessage2(reply, false);
                    responseMessageList.add(responseMessage);
                    messageAdapter.notifyDataSetChanged();
                    if (!isLastVisible())
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
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
    public void onResume(){
        super.onResume();

        if(((MyApplication) this.getApplication()).getStartLocation()){
            ((MyApplication) this.getApplication()).setStartLocation(false);
            NearbyPlaces placesToRest = new NearbyPlaces( new GpsTracker( getApplicationContext() ) );
            placesToRest.execute();

            while(!placesToRest.isDone());
            String places = placesToRest.getDataParsed();
            handleExit(places);

        }
        else if(((MyApplication) this.getApplication()).getMusicBack()){
            ((MyApplication) this.getApplication()).setMusicBack(false);
            handleResult( "What else can I do for you?" );
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
