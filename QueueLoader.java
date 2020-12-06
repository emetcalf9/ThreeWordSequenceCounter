import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class QueueLoader implements Runnable {
    private BlockingQueue<String> q;
    private Scanner s;
    private Thread t;

    QueueLoader(BlockingQueue<String> q, Scanner s) {
        this.q = q;
        this.s = s;
    }

    public void run() {
        while (s.hasNext()) {
            //Read in next line, remove punctuation, and add to the queue
            String[] nextLine = s.nextLine().replaceAll("[\\u2018\\u2019]", "'")
                    .replaceAll("[\\u201C\\u201D]", "\"").replaceAll("[\"\\p{Punct}&&[^']]+", " ")
                    .replaceAll("'","").toLowerCase().split("\\s+");
            for (String word:nextLine) {
                try {
                    q.put(word);
                } catch (InterruptedException e) {
                    System.out.println("Failed to add to Queue");
                }
            }
        }
        try {
            // Signal that no more input is coming to trigger counting
            q.put("endofqueue");
        } catch (InterruptedException e) {
            System.out.println("Failed to add to Queue");
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread (this, "QueueLoader");
            t.start();
        }
    }
}
