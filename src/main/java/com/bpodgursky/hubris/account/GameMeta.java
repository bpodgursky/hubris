package com.bpodgursky.hubris.account;

public class GameMeta {
  private String name;
  private Integer id;

  public GameMeta(String name, Integer id) {
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

  public Integer getId() {
    return id;
  }
}
