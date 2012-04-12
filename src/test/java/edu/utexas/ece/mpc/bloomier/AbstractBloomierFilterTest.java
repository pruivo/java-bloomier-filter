package edu.utexas.ece.mpc.bloomier;

import org.junit.Before;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
   private static final NumberFormat MEM_FMT = new DecimalFormat("##,###.####");

   private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

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
         boolean putInBloomierFilter = random.nextInt(100) > 50;

         if (putInBloomierFilter) {
            if (inBloomierFilter.containsKey(key)) {
               continue;
            }
            inBloomierFilter.put(key, random.nextInt());
         } else {
            if (notInBloomierFilter.contains(key)) {
               continue;
            }
            notInBloomierFilter.add(key);
         }

         numberOfKeysAdded++;
      }

      if (inBloomierFilter.isEmpty()) {
         inBloomierFilter.put(random.nextInt(), random.nextInt());
      }

      if (notInBloomierFilter.isEmpty()) {
         notInBloomierFilter.add(random.nextInt());
      }

      alreadySetUp = true;
   }

   protected long getMemoryUsed() {
      //in bytes!
      System.gc();
      return memoryMXBean.getHeapMemoryUsage().getUsed();
   }

   protected String mem2String(long memory) {
      double val = memory;
      int mag = 0;
      while (val > 1024) {
         val = val / 1024;
         mag++;
      }

      String formatted = MEM_FMT.format(val);
      switch (mag) {
         case 0:
            return formatted + " bytes";
         case 1:
            return formatted + " kb";
         case 2:
            return formatted + " Mb";
         case 3:
            return formatted + " Gb";
         default:
            return "WTF?";
      }
   }

   protected abstract Integer getValueOf(Integer key);

   protected abstract void createBloomFilter(Map<Integer, Integer> map) throws TimeoutException;

   @Before
   public void setUp() throws Exception {
      setUpMaps();

      long memUsedBefore = getMemoryUsed();
      createBloomFilter(inBloomierFilter);
      long memUsedAfter = getMemoryUsed();
      System.out.println("Memory used before the Bloomier Filter creation is " + mem2String(memUsedBefore) +
                               ", and after is " + mem2String(memUsedAfter));
   }

   @Test
   public void member() {
      Map.Entry<Integer, Integer> entry = inBloomierFilter.entrySet().iterator().next();
      Integer key = entry.getKey();
      Integer expectedValue = entry.getValue();
      Integer value = getValueOf(key);

      assert value == null : "Error: False negative is not allowed with Bloomier Filter (key=" + key +
            ",expected value=" + expectedValue + ")";

      assert value.intValue() == expectedValue.intValue() : "Error. The key " + key + " must be in the Bloomier Filter with value " +
            expectedValue + ", but the Bloomier Filter returned " + value;
   }

   @Test
   public void notMember() {
      Integer key = notInBloomierFilter.iterator().next();
      Integer value = getValueOf(key);

      assert value == null : "Error. The key " + key + " should *not* be in the Bloomier Filter but it returned " + value;
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
            System.out.println("False negative detected! key=" + key + ", expected value=" + expectedValue);
         } else if (value.intValue() != expectedValue.intValue()) {
            wrongValueCount++;
            System.out.println("Wrong value detected! key=" + key + ", expected value=" + expectedValue +
                                     ", obtained value=" + value);
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
            System.err.println("False positive detected! key=" + key);
         }
      }
      //false positive can happen
      System.out.println("Results: Number of false positive=" + falsePositiveCount + ", number of keys checked=" +
                               notInBloomierFilter.size());
   }
}
