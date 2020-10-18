package com.example.androidlabs.models;

public class Message {

  public static String text;
  public Type type;

  public Message(String messageText, Type type) {
    this.text = messageText;
    this.type = type;
  }

  public String getText() {
    return text;
  }

  public Type getType() {
    return type;
  }

  public enum Type {
    SENT,
    RECEIVED
  }
}
