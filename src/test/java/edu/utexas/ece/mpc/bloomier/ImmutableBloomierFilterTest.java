/*
 * Copyright (c) 2011, The University of Texas at Austin
 * Produced in the Mobile and Pervasive Computing Lab
 * Originally written by Evan Grim
 * 
 * All rights reserved.
 * 
 * See included LICENSE.txt for licensing details
 * 
 */

package edu.utexas.ece.mpc.bloomier;

import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ImmutableBloomierFilterTest extends AbstractBloomierFilterTest {
   ImmutableBloomierFilter<Integer, Integer> uut;

   @Override
   protected Object createBloomFilter() throws TimeoutException {
      uut = new ImmutableBloomierFilter<Integer, Integer>(inBloomierFilter, inBloomierFilter.keySet().size() * 10, 10, 64,
                                                          Integer.class, 10000);
      return uut;
   }

   @Override
   public void clean() {
      uut = null;
      System.gc();
   }

   @Override
   protected Integer getValueOf(Integer key) {
      return uut.get(key);
   }
}
