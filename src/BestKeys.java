
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BestKeys {

    private final ReadWriteLock lock;
    final ArrayList<Result> bestResults;
    private final int maxNumberOfResults;
    private final Set<Double> scoresSet = new HashSet<>();
    private boolean allowDuplicates;

    private final double minScore;
    BestKeys(int maxNumberOfResults, double minScore, boolean allowDuplicates) {
        lock = new ReentrantReadWriteLock();
        bestResults = new ArrayList<>();
        this.maxNumberOfResults = maxNumberOfResults;
        this.minScore = minScore;
        this.allowDuplicates = allowDuplicates;
    }

    public void push(double score, Key key, WheelFunctionSet wheelFunctionSet) {
        lock.readLock().lock();
        if (!allowDuplicates) {
            for (Result be : bestResults) {
                if (be.score == score) {
                    lock.readLock().unlock();
                    return;
                }
            }
        }
        int size = bestResults.size();
        boolean shouldPush = ((size < maxNumberOfResults) || (score > bestResults.get(size - 1).score)) && !scoresSet.contains(score) && score > minScore;
        lock.readLock().unlock();

        if (shouldPush) {
            lock.writeLock().lock();
            size = bestResults.size();
            shouldPush = (size < maxNumberOfResults) || (score > bestResults.get(size - 1).score);
            if (shouldPush) {
                //System.out.printf("Pushing %f - Range was = %f - %f - size %,d\n", score, size > 0 ? bestResults.get(0).score : 0.0, size > 0 ? bestResults.get(size - 1).score : 0.0, size);
                while (bestResults.size() >= maxNumberOfResults && score > bestResults.get(bestResults.size() - 1).score) {
                    bestResults.remove(bestResults.size() - 1);
                }
                bestResults.add(new Result(score, key, wheelFunctionSet));
                bestResults.sort((o1, o2) -> (int) (1_000_000 * (o2.score - o1.score)));
                //scoresSet.add(score);
                if (scoresSet.size() > 10000) {
                    //System.out.printf("Clearing cache ..... in queue %,d\n", bestResults.size());
                    scoresSet.clear();
                }
            }
            lock.writeLock().unlock();

        }
    }

    public Result getNextResult() {
        lock.readLock().lock();
        if (bestResults.isEmpty()) {
            lock.readLock().unlock();
            return null;
        }
        lock.readLock().unlock();

        lock.writeLock().lock();
        if (bestResults.isEmpty()) {
            lock.writeLock().unlock();
            return null;
        }
        Result result = bestResults.get(0);
        bestResults.remove(0);
        lock.writeLock().unlock();

        return result;
    }

    public ArrayList<Result> getResults() {
        lock.readLock().lock();
        ArrayList<Result> r = new ArrayList<>(bestResults);
        lock.readLock().unlock();



        return r;
    }

}
