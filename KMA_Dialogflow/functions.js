const functions = require('firebase-functions');
const {WebhookClient} = require('dialogflow-fulfillment');
const { dialogflow } = require('actions-on-google');

// initialise DB connection
const admin = require('firebase-admin');
admin.initializeApp({
  credential: admin.credential.applicationDefault(),
  databaseURL: 'ws://lifesaver-protocol-494e2.firebaseio.com/',
});

var c_answer = "default";
var category = "";
var dir = 'results';
process.env.DEBUG = 'dialogflow:debug';

exports.dialogflowFirebaseFulfillment = functions.https.onRequest((request, response) => {
  const agent = new WebhookClient({ request, response });
  console.log('Dialogflow Request headers: ' + JSON.stringify(request.headers));
  console.log('Dialogflow Request body: ' + JSON.stringify(request.body));
  
  
  function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
  async function handleTrivia(agent){
    handlePref(agent);
    await sleep(2000);
    return admin.database().ref(dir).once("value").then((snapshot) => {
      var j = Math.floor(Math.random() * 10);
      var dom_dir = j.toString() + "/question";
      var corr_dir = j.toString() + "/correct_answer";
      var incorr_dir = j.toString() + "/incorrect_answers";
      var question = snapshot.child(dom_dir).val();
      agent.add(question + "\n");
      c_answer = snapshot.child(corr_dir).val();
      var answ= [];
      answ.push(snapshot.child(incorr_dir).forEach(function(childSnapshot) {
        var item = childSnapshot.val();
        answ.push(item);
      }));
      //la forEach aggiunge un false alla coda dell'array
      //la pop() rimuove il "false"
      answ.pop();
      answ.push(c_answer);
      for (var i = answ.length - 1; i > 0; i--) {
        j = Math.floor(Math.random() * (i + 1));
        x = answ[i];
        answ[i] = answ[j];
        answ[j] = x;
      }
      var a = answ.join("\n");
      agent.add(`The possible answers are :` + "\n" + a + "\n\n" + `Give your answer.`);
    });
  } 
  function handleTriviaAnswer(agent){
    const answer = agent.parameters.answer;
    if(answer.toLowerCase() == c_answer.toLowerCase()){
      agent.add(`Well done!`);
    }
    else{
      agent.add(`Wrong! The correct answer is ` + c_answer);
    }
    agent.add(`What else do you want to do?`);
  }
  function handlePref(agent){
    return admin.database().ref('userInfo').once("value").then((snapshot) => {
      var pref = snapshot.child("prefInfo").val();
      var obj2 = pref.split(" ");
      var check_category = 0;
      while(check_category != 1){
        if(pref == ""){
          dir = 'results';
        }
        else{
          category = Math.floor(Math.random() * 24);
          for(var index in obj2){
            if(category == obj2[index]){
              check_category=1; 
              dir = 'results'+category.toString();
            }
          }
        }
      }
    });
  }
  
  function handleWeather(agent){
    const city = agent.parameters.geocity;
    agent.add(city);
  }
  function handleNews(agent){
    const news = agent.parameters.news;
    agent.add(news);
  }
  function handleWelcome(agent){
    return admin.database().ref('userInfo').once("value").then((snapshot) => {
      var name = snapshot.child("name").val();
      agent.add(`Hi ` + name + `, here is what you can ask me:` + "\n"+
                `1.    Let's have a trivia`+ "\n" + 
                `2.    Weather of a city` + "\n" + 
                `3.    News about something` + "\n" +
                `4.    End this conversation`);
    });
  }
  // Run the proper function handler based on the matched Dialogflow intent name
  let intentMap = new Map();
  intentMap.set('Default Welcome Intent', handleWelcome);
  intentMap.set('choiceTrivia', handleTrivia);
  intentMap.set('choiceTrivia - answer', handleTriviaAnswer);
  intentMap.set('choiceWeather', handleWeather);
  intentMap.set('choiceNews', handleNews);
  agent.handleRequest(intentMap);
});