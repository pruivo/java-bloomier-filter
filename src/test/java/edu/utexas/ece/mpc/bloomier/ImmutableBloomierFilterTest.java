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
      if (uut == null) {
         uut = new ImmutableBloomierFilter<Integer, Integer>(inBloomierFilter, M_VALUE, K_VALUE, Q_VALUE, Integer.class,
                                                             TIMEOUT, HASH_SEED);
      }
      return uut;
   }

   @Override
   protected Integer getValueOf(Integer key) {
      return uut.get(key);
   }
}
