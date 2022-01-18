package linda.prime;

import java.util.ArrayList;
import java.util.Arrays;

import linda.Callback;
import linda.Linda;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.server.log.Logger;
import linda.shm.CentralizedLinda;

/**
 * This class is a toolbox for prime number search using the sieve of
 * Eratosthenes.
 */
public class PrimeSearch {

  private static class EliminationCallback implements Callback {
    private Linda linda;
    private Tuple pattern;
    private boolean[] isPrime;
    private Integer k;

    public EliminationCallback(Linda linda, Tuple pattern, boolean[] isPrime, Integer k) {
      this.linda = linda;
      this.pattern = pattern;
      this.isPrime = isPrime;
      this.k = k;
    }

    public void call(Tuple t) {
      // remove all the multiples of the prime number
      Integer i = (Integer) t.get(0);

      if (i + 1 <= Math.sqrt(k)) {
        linda.eventRegister(eventMode.TAKE, eventTiming.FUTURE, pattern, this);
        linda.write(new Tuple(i + 1));
      }

      if (isPrime[i]) {
        for (Integer j = i * i; j <= k; j += i) {
          isPrime[j] = false;
        }
      }

    }
  }

  /**
   * Determines sequentially (and therefore slowly) the prime numbers in the range
   * [2, n] using the sieve of Eratosthenes.
   * 
   * @param k the upper bound of the range.
   * @return the prime numbers in the range [2, n].
   */
  private static Integer[] sequentialSearch(Integer k) {
    ArrayList<Integer> primes = new ArrayList<Integer>();
    boolean[] isPrime = new boolean[k + 1];
    for (Integer i = 2; i <= k; i++) {
      isPrime[i] = true;
    }

    for (Integer i = 2; i <= k; i++) {
      if (isPrime[i]) {
        primes.add(i);
        for (Integer j = i * i; j <= k; j += i) {
          isPrime[j] = false;
        }
      }
    }
    return (Integer[]) primes.toArray(new Integer[primes.size()]);
  }

  /**
   * Determines the prime numbers in the range [2, n] using multiple threads.
   * 
   * @param k the upper bound of the range.
   * @return
   */
  private static Integer[] parallelSearch(Integer k) {
    Linda linda = new CentralizedLinda();

    boolean[] isPrime = new boolean[k + 1];
    for (Integer i = 2; i <= k; i++) {
      isPrime[i] = true;
    }

    // register the callback
    Tuple pattern = new Tuple(Integer.class);
    linda.eventRegister(eventMode.TAKE, eventTiming.FUTURE, pattern,
        new EliminationCallback(linda, pattern, isPrime, k));
    linda.write(new Tuple(2));

    ArrayList<Integer> output = new ArrayList<>();
    for (Integer i = 0; i < isPrime.length; i++) {
      if (isPrime[i]) {
        output.add(i);
      }
    }

    return output.toArray(new Integer[output.size()]);
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java linda.prime.PrimeSearch <n>");
      System.exit(1);
    }
    // Logger.setShowPrefix(false);
    // Logger.setEmojiSupport(false);
    // Logger.setMinPriority(LogLevel.Log);

    Integer k = Integer.parseInt(args[0]);
    Integer[] primes;

    long start = System.currentTimeMillis();
    primes = PrimeSearch.sequentialSearch(k);
    Logger.debug(new ArrayList<Integer>(Arrays.asList(primes)).toString());
    long end = System.currentTimeMillis();
    Logger.info("Sequential search took " + (end - start) + " ms.");

    start = System.currentTimeMillis();
    primes = PrimeSearch.parallelSearch(k);
    Logger.debug(new ArrayList<Integer>(Arrays.asList(primes)).toString());
    end = System.currentTimeMillis();
    Logger.info("Parallel search took " + (end - start) + " ms.");
  }
}
