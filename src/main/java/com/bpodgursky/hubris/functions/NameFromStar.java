package com.bpodgursky.hubris.functions;

import com.bpodgursky.hubris.universe.Star;
import com.google.common.base.Function;

public class NameFromStar implements Function<Star, String> {
  @Override
  public String apply(Star star) {
    return star.getName();
  }
}
