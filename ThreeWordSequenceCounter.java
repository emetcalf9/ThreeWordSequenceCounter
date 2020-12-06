import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ThreeWordSequenceCounter {

    public static void main(String[] args) {
        if (args.length == 0) {
            Scanner s = new Scanner(System.in);
            System.out.println("Processing from standard input");
            processText(s);
        } else {
            for (String filename : args) {
                String filepath = System.getProperty("user.dir") + File.separator + filename;
                File file = new File(filepath);
                try {
                    Scanner s = new Scanner(file);
                    System.out.println("Processing " + file.getName());
                    processText(s);
                    System.out.println();
                } catch (FileNotFoundException e) {
                    System.out.println("File " + file.getName() + " does not exist");
                    System.out.println();
                }
            }
        }
    }

    private static void processText(Scanner s) {
        BlockingQueue<String> q = new ArrayBlockingQueue<>(500);
        HashMap<String, Integer> wordCounts = new HashMap<>();
        QueueLoader ql = new QueueLoader(q, s);
        MapLoader ml = new MapLoader(q, wordCounts);
        ql.start();
        ml.start();
        try {
            ml.t.join();
        } catch (InterruptedException e) {
            System.out.println("Failed to join threads");
        }

        // After processing all lines, find top 100 sequences
        for (int i = 0; i < 100; i++) {
            if (wordCounts.size() == 0) {
                // End early if there are less than 100 unique 3 word sequences
                break;
            }
            // Find the maximum value in wordCounts map, and then get the Key
            String maxValueKey = Collections.max(wordCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            System.out.println(wordCounts.get(maxValueKey) + " - " + maxValueKey);
            // Remove the max value after outputting so we can find the next max value
            wordCounts.remove(maxValueKey);
        }
        s.close();
    }
}
