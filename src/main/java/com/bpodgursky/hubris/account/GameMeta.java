package com.bpodgursky.hubris.account;

import java.io.Serializable;

public class GameMeta implements Serializable {
  private static final long serialVersionUID = 1L;

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
