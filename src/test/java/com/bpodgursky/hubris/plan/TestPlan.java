package com.bpodgursky.hubris.plan;


import com.bpodgursky.hubris.plan.orders.AddToList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import junit.framework.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestPlan {

  @org.junit.Test
  public void testPlan() throws Exception {

    Plan plan = new Plan(null, null);

    List<String> target = Lists.newArrayList();

    Order a = new AddToList(Collections.<Order>emptySet(), "A", target);

    Order b = new AddToList(Sets.newHashSet(a), "B", target);

    Order c = new AddToList(Sets.newHashSet(b), "C", target);

    plan.schedule(Collections.singleton(c));

    plan.tick(null);

    Assert.assertEquals(Arrays.asList("A"), target);

    plan.tick(null);

    Assert.assertEquals(Arrays.asList("A", "B"), target);

    plan.tick(null);

    Assert.assertEquals(Arrays.asList("A", "B", "C"), target);

  }
}
