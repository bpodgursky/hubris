package com.bpodgursky.hubris.account;

public class GameMeta {
  private String name;
  private long id;

  public GameMeta(String name, long id) {
    this.name = name;
    this.id = id;
  }

  @Override
  public String toString() {
    return "GameMeta [name=" + name + ", id=" + id + "]";
  }

  public String getName() {
    return name;
  }

  public long getId() {
    return id;
  }
}
