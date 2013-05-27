package com.bpodgursky.hubris.event;

public interface EventListener<E> {
  public void process(E event);
}
