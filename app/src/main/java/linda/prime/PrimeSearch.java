package linda.prime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.server.log.LogLevel;
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
    private int k;

    public EliminationCallback(Linda linda, Tuple pattern, boolean[] isPrime, int k) {
      this.linda = linda;
      this.pattern = pattern;
      this.isPrime = isPrime;
      this.k = k;
    }

    public void call(Tuple t) {
      // remove all the multiples of the prime number
      int prime = (int) t.get(0);

      if (isPrime[prime + 1] && prime + 1 <= k) {
        linda.eventRegister(eventMode.READ, eventTiming.FUTURE, pattern, this);
        linda.write(new Tuple(prime + 1));
      }

      for (int i = prime * prime; i <= k; i += prime) {
        isPrime[i] = false;
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
  private static Integer[] sequentialSearch(int k) {
    ArrayList<Integer> primes = new ArrayList<Integer>();
    boolean[] isPrime = new boolean[k + 1];
    for (int i = 2; i <= k; i++) {
      isPrime[i] = true;
    }

    for (int i = 2; i <= k; i++) {
      if (isPrime[i]) {
        primes.add(i);
        for (int j = i * i; j <= k; j += i) {
          isPrime[j] = false;
        }
      }
    }
    return (Integer[]) primes.toArray(Integer[]::new);
  }

  /**
   * Determines the prime numbers in the range [2, n] using multiple threads.
   * 
   * @param k the upper bound of the range.
   * @return
   */
  private static Integer[] parallelSearch(int k) {
    Linda lindaPrimes = new CentralizedLinda();

    boolean[] isPrime = new boolean[k + 1];
    for (int i = 2; i <= k; i++) {
      isPrime[i] = true;
    }

    // register the callback
    Tuple pattern = new Tuple(Integer.class);
    lindaPrimes.eventRegister(eventMode.READ, eventTiming.FUTURE, pattern,
        new EliminationCallback(lindaPrimes, pattern, isPrime, k));

    lindaPrimes.write(new Tuple(2));

    // for (Integer i = 2; i <= k; i++) {
    // if (isPrime[i]) {
    // lindaPrimes.write(new Tuple(i));
    // lindaPrimes.eventRegister(eventMode.READ, eventTiming.FUTURE, pattern,
    // new EliminationCallback(lindaPrimes, pattern, isPrime, k));
    // }
    // }

    return lindaPrimes.readAll(new Tuple(Integer.class)).stream().map(tuple -> tuple.get(0)).toArray(Integer[]::new);
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java linda.prime.PrimeSearch <n>");
      System.exit(1);
    }
    // Logger.setShowPrefix(false);
    Logger.setEmojiSupport(false);

    int k = Integer.parseInt(args[0]);
    Integer[] primes;

    long start = System.currentTimeMillis();
    primes = PrimeSearch.sequentialSearch(k);
    Logger.log(new ArrayList<Integer>(Arrays.asList(primes)).toString());
    long end = System.currentTimeMillis();
    Logger.log("Sequential search took " + (end - start) + " ms.", LogLevel.Info);

    start = System.currentTimeMillis();
    primes = PrimeSearch.parallelSearch(k);
    Logger.log(new ArrayList<Integer>(Arrays.asList(primes)).toString());
    end = System.currentTimeMillis();
    Logger.log("Parallel search took " + (end - start) + " ms.", LogLevel.Info);
  }
}
