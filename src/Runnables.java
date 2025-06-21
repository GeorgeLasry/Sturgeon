import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Runnables {

    static int THREADS = 60;

    private ArrayList<Runnable> runnables = new ArrayList<>();

    public void add(Runnable runnable){
        runnables.add(runnable);
    }

    public void runAll() {

        ExecutorService threadExecutor = Executors.newFixedThreadPool(THREADS);
        for (Runnable runnable : runnables) {
            threadExecutor.execute(runnable);
        }
        threadExecutor.shutdown();

        boolean finished = false;
        while (!finished) {
            try {
                finished = threadExecutor.awaitTermination(10000, TimeUnit.MICROSECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    void clear(){
        runnables.clear();
    }
    int size(){
        return runnables.size();
    }
}