package linda.search.basic;

import java.util.Arrays;
import java.util.UUID;

import linda.Linda;
import linda.Tuple;
import linda.server.log.LogLevel;
import linda.server.log.Logger;
import linda.shm.ArrayListSync;

public class Searcher implements Runnable {

    private static Tuple activeRequest;
    private UUID id;
    private Linda linda;

    public Searcher(Linda linda) {
        this.linda = linda;
        this.id = UUID.randomUUID();
    }

    public void run() {
        Logger.log("Searcher " + id + " ready to comply.");

        if (activeRequest == null) {
            activeRequest = linda.read(new Tuple(Code.Request, UUID.class, String.class));
            Logger.log("New search request received from " + activeRequest.get(1), LogLevel.Debug);
        } else {
            Logger.log("Joining existing search request" + activeRequest.get(1), LogLevel.Debug);
        }

        UUID reqUUID = (UUID) activeRequest.get(1);
        String req = (String) activeRequest.get(2);
        Tuple tv;
        Logger.log("Looking for: " + req, LogLevel.Debug);
        while ((tv = linda.tryTake(new Tuple(Code.Value, String.class))) != null) {
            String val = (String) tv.get(1);
            int dist = getLevenshteinDistance(req, val);
            if (dist < 10) { // arbitrary
                Logger.log("Sent: " + val, LogLevel.Debug);
                linda.write(new Tuple(Code.Result, reqUUID, val, dist));
            }
        }
        linda.write(new Tuple(Code.Searcher, "done", reqUUID));
        Logger.log("Search " + reqUUID + " done.", LogLevel.Debug);
    }

    /*****************************************************************/

    /* Levenshtein distance is rather slow */
    /* Copied from https://www.baeldung.com/java-levenshtein-distance */
    static int getLevenshteinDistance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];
        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                            + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }
        return dp[x.length()][y.length()];
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

}
