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
   protected void createBloomFilter(Map<Integer, Integer> map) throws TimeoutException {
      uut = new ImmutableBloomierFilter<Integer, Integer>(map, map.keySet().size() * 10, 10, 64,
                                                          Integer.class, 10000);
   }

   @Override
   protected Integer getValueOf(Integer key) {
      return uut.get(key);
   }
}
