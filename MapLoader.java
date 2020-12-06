import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class MapLoader implements Runnable {
    private BlockingQueue<String> q;
    private HashMap<String, Integer> hm;
    Thread t;

    MapLoader(BlockingQueue<String> q, HashMap<String, Integer> hm) {
        this.q = q;
        this.hm = hm;
    }

    private static String[] addAndShift(String[] words, String newWord) {
        words[0] = words[1];
        words[1] = words[2];
        words[2] = newWord;
        return words;
    }

    public void run() {
        String[] threeWords = new String[3];
        String nextWord = "";
        boolean finished = false;
        int initCounter = 0;
        while(initCounter < 2) {
            // Fill in first 2 values from the queue to prep for adding to Map
            try {
                nextWord = q.take();
            } catch (InterruptedException e) {
                System.out.println("Failed to take from Queue");
            }
            if (nextWord.replaceAll("\\s+", "").equals("")) {
                // Skip any blank strings that make it into the Queue
                continue;
            }
            if (nextWord.equals("endofqueue")) {
                try {
                    // Short delay to all for QueueLoader to add more input if needed
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("Sleep interrupted");
                }
                if (q.size() == 0) {
                    // Stop processing if Queue is empty
                    System.out.println("File contains less than 3 words");
                    finished = true;
                    initCounter = 2;
                }
                // If "endofqueue" is actually a word in the file it will be processed
            }
            threeWords = addAndShift(threeWords, nextWord);
            initCounter++;
        }
        while (!finished) {
            try {
                nextWord = q.take();
            } catch (InterruptedException e) {
                System.out.println("Failed to take from Queue");
            }
            if (nextWord.replaceAll("\\s+", "").equals("")) {
                // Skip any blank strings that make it into the Queue
                continue;
            }
            if (nextWord.equals("endofqueue")) {
                try {
                    // Short delay to all for QueueLoader to add more input if needed
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("Sleep interrupted");
                }
                if (q.size() == 0) {
                    // Stop processing if Queue is empty
                    if (hm.size() == 0) {
                        // Check hm size, if 0 then file contained less than 3 words and there is no output
                        System.out.println("File contains less than 3 words");
                    }
                    break;
                }
                // If "endofqueue" is actually a word in the file it will be processed
            }
            threeWords = addAndShift(threeWords, nextWord);
            String threeWordsString = String.join(" ",threeWords);
            // Increment count by 1, if it doesn't exist yet use 0 as default value before adding 1
            hm.put(threeWordsString, hm.getOrDefault(threeWordsString,0) + 1);
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, "MapLoader");
            t.start();
        }
    }
}
