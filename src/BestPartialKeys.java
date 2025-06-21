import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BestPartialKeys {

    private final ReadWriteLock lock;
    private final Map<String, Result> bestResults;
    private boolean changed = false;

    BestPartialKeys() {
        lock = new ReentrantReadWriteLock();
        bestResults = new HashMap<>();
    }

    public void push(double score, Key key, WheelFunctionSet wheelFunctionSet) {
        String mapKey = wheelFunctionSet.toString();
        lock.readLock().lock();
        Result result = bestResults.get(mapKey);
        boolean shouldChange = result == null || score > result.score;
        lock.readLock().unlock();
        if (shouldChange) {
            lock.writeLock().lock();
            result = bestResults.get(mapKey);
            shouldChange = result == null || score > result.score;
            if (shouldChange) {
                changed = true;
                bestResults.put(mapKey, new Result(score, key, wheelFunctionSet));
            }
            lock.writeLock().unlock();
        }
    }

    public ArrayList<Result> getAllIfChanged() {
        lock.readLock().lock();
        boolean shouldReturn = changed;
        lock.readLock().unlock();
        if (shouldReturn) {
            ArrayList<Result> sortedResults = null;
            lock.writeLock().lock();
            if (changed) {
                sortedResults = new ArrayList<>(bestResults.values());
                changed = false;
            }
            lock.writeLock().unlock();
            if (sortedResults != null) {
                sortedResults.sort((o1, o2) -> (int) (1000000 * (o2.score - o1.score)));
            }
            return sortedResults;
        }
        return null;
    }


}
