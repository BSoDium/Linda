package linda.search.basic;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

import java.util.ArrayList;
import java.util.Collection;

public class NombresPremiers {
    public static void main(String[] args) {
       
    }
    public int[] sequentielle(int k) {
        boolean[] prime = new boolean[k+1];
        for(int i = 2; i <= k; i++){
            prime[i] = true;
        }

        int i, count = 0;
        int[] primeNumbers = new int[k];
        for (i = 2; i * i <= k; i++) {               
            if (prime[i]) {
                primeNumbers[count++] = i;
                for (int j = i * i; j <= k; j += i) {
                    prime[j] = false;
                }
            }
        }

        for (; i <= k; i++) {
            if (prime[i]) {
                primeNumbers[count++] = i;
            }
        }
        return primeNumbers;
    }
    public void parallelisation(int k) {
    
    }
}