package edu.utexas.ece.mpc.bloomier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * // TODO: Document this
 *
 * @author Pedro Ruivo
 * @since 1.0
 */
public abstract class AbstractBloomierFilterTest {

   private static final int NUMBER_OF_KEYS = 10000;

   protected static final int M_VALUE = NUMBER_OF_KEYS * 5;
   protected static final int K_VALUE = 10;
   protected static final int Q_VALUE = 5 * 8; //5 bytes
   protected static final int TIMEOUT = 10000; //10 seconds
   protected static final long HASH_SEED = System.nanoTime();

   protected final Map<Integer, Integer> inBloomierFilter = new HashMap<Integer, Integer>();
   protected final Set<Integer> notInBloomierFilter = new HashSet<Integer>();
   private boolean alreadySetUp = false;

   protected synchronized void setUpMaps() {
      if (alreadySetUp) {
         return ;
      }

      Random random = new Random(System.nanoTime());

      int numberOfKeysAdded = 0;

      while (numberOfKeysAdded < NUMBER_OF_KEYS) {
         int key = random.nextInt();

         if (inBloomierFilter.containsKey(key)) {
            continue;
         }
         inBloomierFilter.put(key, random.nextInt());
         numberOfKeysAdded++;
      }

      numberOfKeysAdded = 0;
      while (numberOfKeysAdded < NUMBER_OF_KEYS) {
         int key = random.nextInt();

         if (notInBloomierFilter.contains(key) || inBloomierFilter.containsKey(key)) {
            continue;
         }
         notInBloomierFilter.add(key);
         numberOfKeysAdded++;
      }

      alreadySetUp = true;
   }

   protected abstract Integer getValueOf(Integer key);

   protected abstract Object createBloomFilter() throws TimeoutException;

   @Before
   public void setUp() throws Exception {
      setUpMaps();
      createBloomFilter();
   }

   @Test
   public void member() {
      Map.Entry<Integer, Integer> entry = inBloomierFilter.entrySet().iterator().next();
      Integer key = entry.getKey();
      Integer expectedValue = entry.getValue();
      Integer value = getValueOf(key);

      assert value != null : "Error: False negative is not allowed with Bloomier Filter (key=" + key +
            ",expected value=" + expectedValue + ", value=" + value + ")";

      assert value.intValue() == expectedValue.intValue() : "Error: Wrong value returned (key=" + key +
            ",expected value=" + expectedValue + ", value=" + value + ")";
   }

   @Test
   public void notMember() {
      Integer key = notInBloomierFilter.iterator().next();
      Integer value = getValueOf(key);

      assert value == null : "Error: false positive detected. (key=" + key +
            ",expected value=null, value=" + value + ")";
   }


   @Test
   public void checkFalseNegative() {
      int falseNegativeCount = 0;
      int wrongValueCount = 0;

      for (Map.Entry<Integer, Integer> entry : inBloomierFilter.entrySet()) {
         Integer key = entry.getKey();
         Integer expectedValue = entry.getValue();
         Integer value = getValueOf(key);

         if (value == null) {
            falseNegativeCount++;
            System.out.println("False negative detected! (key=" + key +
                                     ",expected value=" + expectedValue + ", value=" + value + ")");
         } else if (value.intValue() != expectedValue.intValue()) {
            wrongValueCount++;
            System.out.println("Wrong value detected!(key=" + key +
                                     ",expected value=" + expectedValue + ", value=" + value + ")");
         }
      }

      assert falseNegativeCount == 0 : "False Negatives are not expected to happen (" + falseNegativeCount + " detected!)";
      assert wrongValueCount == 0 : "Wrong Values are not expected to happen (" + wrongValueCount + " detected!)";
   }

   @Test
   public void checkFalsePositive() {
      int falsePositiveCount = 0;

      for (Integer key : notInBloomierFilter) {

         if (getValueOf(key) != null) {
            falsePositiveCount++;
         }
      }
      //false positive can happen
      System.out.println("Results: Number of false positive=" + falsePositiveCount + ", number of keys checked=" +
                               notInBloomierFilter.size());
   }
}
