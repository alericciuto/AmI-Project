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

import android.speech.tts.TextToSpeech;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kmapp.ResponseMessage2;

public class ConversationActivity extends AppCompatActivity implements AIListener {

    private AIService aiService;
    private int conversation_enable=0;
    private int speech_error_limiter;
    private TextToSpeech TTS;
    public String city = "";
    private int conv_progress = 0;
    private int false_end=0;
    private String news = "";

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
                TTS.speak("Keep Me Awake", TextToSpeech.QUEUE_FLUSH, null, null);
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
            conversation_enable=0;
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
                    city = result.getFulfillment().getSpeech().replace(" ", "%20");
                    conv_progress = conv_progress + 1;
                    false_end = 0;
                    Handler a = new Handler();
                    a.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fetchMeteoData p = new fetchMeteoData();
                            p.execute();
                        }
                    }, 2000);
                }
            }
            else if (result.getAction().compareTo("choiceNews") == 0) {
                if(result.getParameters().size()==0){
                    handleResult("You have to specify the topic");
                }
                else{
                    news = result.getFulfillment().getSpeech().replace(" ", "%20");
                    conv_progress = conv_progress+1;
                    false_end=0;
                    Handler a = new Handler();
                    a.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fetchNewsData p = new fetchNewsData();
                            p.execute();
                        }
                    }, 2000);
                }
            }
            else {
                if (result.getAction().compareTo("choiceTrivia") == 0) {
                    conv_progress = conv_progress+1;
                    false_end=1;
                }
                ResponseMessage.ResponseSpeech r = null;
                StringBuilder sb = new StringBuilder();
                String reply;
                if (result.getFulfillment().getSpeech().compareTo("") != 0) {
                    sb.append(result.getFulfillment().getSpeech());
                    reply = sb.toString();
                } else {
                    int l = result.getFulfillment().getMessages().size();
                    for (int i = 0; i < l; i++) {
                        r = (ResponseMessage.ResponseSpeech) result.getFulfillment().getMessages().get(i);
                        sb.append(r.getSpeech().toString());
                        sb.append("\n");
                    }
                    reply = sb.toString();
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
                }, 600);
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
    public void onListeningStarted() {}
    @Override
    public void onListeningCanceled() {}
    @Override
    public void onListeningFinished() {}
    @Override
    public void onAudioLevel(final float level) {}


    public class fetchMeteoData extends AsyncTask<Void,Void,Void> {
        String data ="";
        String dataParsed = "";
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID=5b223f593e42fa8af1b7229cdd7e3f23");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while(line != null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONObject jo = new JSONObject(data);

                JSONObject tmp_main = ((JSONObject) jo.get("main"));
                Double temp = (Double)tmp_main.get("temp") - 273.15;
                Double tempMin = (Double)tmp_main.get("temp_min") - 273.15;
                Double tempMax = (Double)tmp_main.get("temp_max") - 273.15;

                JSONObject tmp_weather = ((JSONArray) jo.get("weather")).getJSONObject(0);

                dataParsed = "In " + city + " the weather is caracterized by " + tmp_weather.get("description") + "\n" +
                        "Currently there are : " + String.format("%.0f", temp) +" grades" + "\n" +
                        "The minimum temperature is : " + String.format("%.0f", tempMin) +" grades" + "\n" +
                        "The maximum temperature is : " + String.format("%.0f", tempMax) +" grades" + "\n" +
                        "Do you want to do something else?";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            handleResult(dataParsed);
        }
    }

    public class fetchNewsData extends AsyncTask<Void,Void,Void> {
        String data ="";
        String dataParsed = "";
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://newsapi.org/v2/everything?q=" + news + "&sortBy=popularity&apiKey=3dc5347cbb25465885aab935b3aae6c0");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while(line != null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONObject jo = new JSONObject(data);

                JSONArray art = (JSONArray)jo.get("articles");
                int totRes = art.length();
                int num_article = (int)(Math.random()*totRes);
                JSONObject article = ((JSONArray) jo.get("articles")).getJSONObject(num_article);
                String description = (String)article.get("description");
                String j = (String)((JSONObject)article.get("source")).get("name");
                String[] des = description.split("\\. ");
                dataParsed = (String)article.get("title") + "\n" +
                        des[0] + "\n" +
                        "To continue go to: " + "\n" + (String)((JSONObject)article.get("source")).get("name") + "\n" +
                        "Do you want to do something else?";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            handleResult(dataParsed);
        }
    }
}
