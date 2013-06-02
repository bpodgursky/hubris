package com.bpodgursky.hubris.util;

public interface Filter<A> {
  public boolean isAccept(A item);
}