package boredbot;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.regex.Matcher;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot"})
public class BoredBot extends Bot {
  @Value("${slackBotToken}")
  private String slackToken;

  @Override
  public String getSlackToken() {
      return slackToken;
  }

  @Override
  public Bot getSlackBot() {
      return this;
  }

  private String[] questions = {
    "Do you feel stressed today?",
    "Do you feel tired?",
    "Do you feel restless?",
    "Do you feel a bit down?"
  };
  private String[] suggestions = {
    "Do some meaningful work! Perhaps a side project?",
    "How about reading a book? It might comfort you and recharge your energy levels!", 
    "Maybe watching a video is a good idea. Turn your brain off for a while and get some rest.",
    "If you're not feeling too tired, doing some exercise would clear your head and make you feel better!",
    "If you're feeling too stressed, it might be a good idea to do some meditation!"
  };
  private boolean[] answers = {
    false,
    false,
    false,
    false 
  };
  private String GetSuggestion() {
    int score = 0;
    for(int i = 0; i < answers.length; i++) {
      if(answers[i]) score++; 
    }
    return suggestions[score];
  }
  private int questionCount = 0;

  @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE}, next = "RaiseQuestion")
  public void onReceiveDM(WebSocketSession session, Event event) {
      startConversation(event, "RaiseQuestion");   // start conversation
      reply(session, event, new Message("Hi, I am " + slackService.getCurrentUser().getName() + ", and I can help you if you feel a bit bored!"));
  }

  @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
  public void RaiseQuestion(WebSocketSession session, Event event) {
    if(questions.length > questionCount) {
      if (event.getText().contains("yes")) {
        answers[questionCount] = true;
      }
      else {
        answers[questionCount] = false;
      }
      String question = questions[questionCount];
      reply(session, event, new Message(question));
      questionCount++;
    }
    else {
        reply(session, event, new Message("Ok! This is what I suggest: "));
        reply(session, event, new Message(GetSuggestion()));
        reply(session, event, new Message("Ofcourse this is only a suggestion, don't feel like you have to do anything you don't want to do!"));
      questionCount = 0;
      stopConversation(event);
    }
  }
  public static void main(String[] args) {
    SpringApplication.run(BoredBot.class, args);
  }
}
