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

import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeoutException;

public class MutableBloomierFilterTest extends AbstractBloomierFilterTest {

   private MutableBloomierFilter<Integer, Integer> uut;

   @Override
   protected Object createBloomFilter() throws TimeoutException {
      uut = new MutableBloomierFilter<Integer, Integer>(inBloomierFilter, inBloomierFilter.keySet().size() * 10, 10, 64,
                                                  10000);
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

   @Test
   public void modify() {
      Integer key = inBloomierFilter.keySet().iterator().next();
      uut.set(key, 10);
      Integer value = uut.get(key);

      assert value != null : "Error: False negative is not allowed with Bloomier Filter (key=" + key +
            ",expected value=10, value=" + value + ")";

      assert 10 == value.intValue() : "Error. The key " + key + " must be in the Bloomier Filter with value 10" +
            ", but the Bloomier Filter returned " + value;
   }

   @Test(expected = IllegalArgumentException.class)
   public void illegalModify() {
      Integer key = notInBloomierFilter.iterator().next();
      uut.set(key, 10);
      assert false : "Expected IllegalArgumentException!";
   }
}
