import java.util.ArrayList;
import java.util.Arrays;

public class COC {

    static void solve(Eval eval,
                      final Sturgeon sturgeon,
                      final byte[][] cipherArray,
                      final int wheelsNumberInSet,
                      int numberOfTopSets,
                      final WheelFunctionSet wheelFunctionSetToPreserve, double minScore) {

        final BestKeys bestKeys = new BestKeys(1000, minScore, true);
        final long startTimeMillis = System.currentTimeMillis();

        final ArrayList<WheelFunctionSet> topFunctionSets = WheelFunctionSet.topWheelFunctionSets(sturgeon.key.model,
                numberOfTopSets, wheelsNumberInSet, eval == Eval.CLASS_IC, wheelFunctionSetToPreserve, true);

        final double[] scores = new double[topFunctionSets.size()];

        int index = 0;
        for (WheelFunctionSet set : topFunctionSets) {
            final WheelFunctionSet union = set.union(wheelFunctionSetToPreserve);
            sturgeon.computeClassFreqRef(ReferenceText.languageFreq, union);
            double score = CO.score(eval, sturgeon, cipherArray, null, union);
            CO.printScore("Real " + set,
                    index,
                    sturgeon,
                    union,
                    startTimeMillis,
                    score,
                    0.0);
            scores[index++] = score;
        }

        System.out.println();

        Runnables runnables = new Runnables();
        Runnables.THREADS = 60;
        //runnables.add(() -> displayResultsThread(topFunctionSets, bestKeys, scores, startTimeMillis));

        int t = 0;
        while (runnables.size() < Runnables.THREADS) {
            for (final WheelFunctionSet wheelFunctionSet : topFunctionSets) {
                runnables.add(() -> computeResultsThread(eval, sturgeon, cipherArray, bestKeys, wheelFunctionSet, wheelFunctionSetToPreserve));
                int t_ = t++;
                runnables.add(() -> processResultsThread(Eval.NGRAMS, cipherArray, bestKeys, t_, 10000));
            }
        }
        runnables.runAll();
    }

    static void processResultsThread(Eval eval, byte[][] cipherArray, BestKeys bestKeys, int task, long timeoutMillis) {


        try {
            Thread.sleep(task * 5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            Result result = bestKeys.getNextResult();
            if (result == null) {
                //System.out.printf("no keys ....\n");
                try {
                    Thread.sleep(task * 100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            WheelFunctionSet wheelFunctionSetToPreserve = result.wheelFunctionSet;
            WheelFunctionSet wheelFunctionSet = wheelFunctionSetToPreserve.remaining();
            //System.out.printf("%2d Processing %s Set: %s Preserve: %s %f\n", task, result.key.getWheelSettingsString(), wheelFunctionSet, wheelFunctionSetToPreserve, result.score);
            Sturgeon sturgeon = new Sturgeon(result.key.model);
            for (int w = 0; w < 10; w++) {
                if (wheelFunctionSetToPreserve.containsWheel(w, result.key.wheelSettings)) {
                    int wheelFunction = WheelSetting.wheelFunction0to9(w, result.key.wheelSettings);
                    CO.assignFunction(w, wheelFunction, sturgeon);
                    sturgeon.key.wheelPositions[w] = result.key.wheelPositions[w];
                }
            }
            int len = cipherArray[0].length;
            byte[] plain = new byte[len];

            WheelFunctionSet union = wheelFunctionSet.union(wheelFunctionSetToPreserve);

            long startTimeMillis = System.currentTimeMillis();

            double phase1Score = result.score;

            while (System.currentTimeMillis() - startTimeMillis < timeoutMillis) {

                sturgeon.randomizeOrderExcept(wheelFunctionSetToPreserve);
                for (int w = 0; w < 10; w++) {
                    if (wheelFunctionSetToPreserve.containsWheel(w, sturgeon.key.wheelSettings)) {
                        continue;
                    }
                    sturgeon.key.wheelPositions[w] = CommonRandom.r.nextInt(Wheels.WHEEL_SIZES[w]);
                }

                if (eval == Eval.CLASS) {
                    sturgeon.computeClassFreqRef(ReferenceText.languageFreq, union);
                }

                double bestScoreForCycle = CO.score(eval, sturgeon, cipherArray, null, union);

                //bestKeys.push(bestScoreForCycle, sturgeon.key, wheelFunctionSet);

                displayIfBest(cipherArray, task, startTimeMillis, phase1Score, wheelFunctionSet, sturgeon, plain, bestScoreForCycle, "2");
                boolean improved;
                do {
                    improved = false;
                    for (int w = 0; w < 10; w++) {
                        if (!wheelFunctionSet.containsWheel(w, sturgeon.key.wheelSettings)) {
                            continue;
                        }
                        int randomShift = CommonRandom.r.nextInt(Wheels.WHEEL_SIZES[w]);
                        int bestP = sturgeon.key.wheelPositions[w];
                        for (int p = 0; p < Wheels.WHEEL_SIZES[w]; p++) {
                            sturgeon.key.wheelPositions[w] = (p + randomShift) % Wheels.WHEEL_SIZES[w];
                            double newScore = CO.score(eval, sturgeon, cipherArray, null, union);
                            if (newScore > bestScoreForCycle) {
                                bestScoreForCycle = newScore;
                                bestP = sturgeon.key.wheelPositions[w];
                                improved = true;
                                //bestKeys.push(bestScoreForCycle, sturgeon.key, wheelFunctionSet);
                                displayIfBest(cipherArray, task, startTimeMillis, phase1Score, wheelFunctionSet, sturgeon, plain, bestScoreForCycle, "3");
                            }
                        }
                        sturgeon.key.wheelPositions[w] = bestP;
                    }
                    if (!improved) {
                        // swap two wheels
                        int randomShift = CommonRandom.r.nextInt(10);
                        outerloop:
                        for (int w1_ = 0; w1_ < 10; w1_++) {
                            int w1 = (w1_ + randomShift) % 10;
                            if (wheelFunctionSetToPreserve.containsWheel(w1, sturgeon.key.wheelSettings)) {
                                continue;
                            }
                            for (int w2_ = 0; w2_ < 10; w2_++) {
                                int w2 = (w2_ + randomShift) % 10;
                                if (w1 == w2 || wheelFunctionSetToPreserve.containsWheel(w2, sturgeon.key.wheelSettings)) {
                                    continue;
                                }
                                if (!wheelFunctionSet.containsWheel(w1, sturgeon.key.wheelSettings) && !wheelFunctionSet.containsWheel(w2, sturgeon.key.wheelSettings)) {
                                    continue;
                                }
                                if (eval == Eval.CLASS_IC && wheelFunctionSet.containsWheel(w1, sturgeon.key.wheelSettings) && wheelFunctionSet.containsWheel(w2, sturgeon.key.wheelSettings)) {
                                    continue;
                                }
                                if (wheelFunctionSet.containsWheel(w1, sturgeon.key.wheelSettings) && wheelFunctionSet.containsWheel(w2, sturgeon.key.wheelSettings)) {

                                    CO.swapWheelsAndComputeMappings(sturgeon, w1, w2);

                                    double newScore = CO.score(eval, sturgeon, cipherArray, null, union);

                                    if (newScore > bestScoreForCycle) {
                                        bestScoreForCycle = newScore;
                                        improved = true;
                                        //bestKeys.push(bestScoreForCycle, sturgeon.key, wheelFunctionSet);
                                        displayIfBest(cipherArray, task, startTimeMillis, phase1Score, wheelFunctionSet, sturgeon, plain, bestScoreForCycle, "4");
                                        break outerloop;
                                    } else {
                                        CO.swapWheelsAndComputeMappings(sturgeon, w1, w2);
                                    }

                                } else {

                                    CO.swapWheelsAndComputeMappings(sturgeon, w1, w2);

                                    int w = wheelFunctionSet.containsWheel(w1, sturgeon.key.wheelSettings) ? w1 : w2;

                                    int origP = sturgeon.key.wheelPositions[w];
                                    int bestP = origP;
                                    double scoreBestP = CO.score(eval, sturgeon, cipherArray, null, union);

                                    for (int pos = 0; pos < Wheels.WHEEL_SIZES[w]; pos++) {
                                        sturgeon.key.wheelPositions[w] = pos;
                                        double score = CO.score(eval, sturgeon, cipherArray, null, union);
                                        if (score > scoreBestP) {
                                            scoreBestP = score;
                                            bestP = pos;
                                        }
                                    }

                                    if (scoreBestP > bestScoreForCycle) {
                                        bestScoreForCycle = scoreBestP;
                                        sturgeon.key.wheelPositions[w] = bestP;
                                        improved = true;
                                        //bestKeys.push(bestScoreForCycle, sturgeon.key, wheelFunctionSet);
                                        displayIfBest(cipherArray, task, startTimeMillis, phase1Score, wheelFunctionSet, sturgeon, plain, bestScoreForCycle, "5");
                                        break outerloop;
                                    } else {
                                        sturgeon.key.wheelPositions[w] = origP;
                                        CO.swapWheelsAndComputeMappings(sturgeon, w1, w2);
                                    }
                                }
                            }
                        }
                    }

                } while (improved);
            }


        }

   }

    private static void displayIfBest(byte[][] cipherArray, int task, long startTimeMillis, double phase1Score, WheelFunctionSet wheelFunctionSet, Sturgeon sturgeon, byte[] plain, double newScore, String desc) {
        CO.bestScoreEverLock.readLock().lock();
        boolean better = newScore > CO.bestScoreEver;
        CO.bestScoreEverLock.readLock().unlock();
        if (better) {
            CO.bestScoreEverLock.writeLock().lock();
            CO.bestScoreEver = newScore;

            CO.printScore(desc, task, sturgeon, wheelFunctionSet, startTimeMillis, newScore, phase1Score);

            sturgeon.encryptDecrypt(cipherArray[0], plain, cipherArray[0].length, null, false);
            System.out.printf("%s\n", Alphabet.toStringFormatted(plain, true));

            CO.bestScoreEverLock.writeLock().unlock();
        }
    }

    private static void computeResultsThread(Eval eval, Sturgeon sturgeon, byte[][] cipherArray, BestKeys bestKeys,
                                             WheelFunctionSet wheelFunctionSet, WheelFunctionSet wheelFunctionSetToPreserve) {
        Sturgeon ts = new Sturgeon(sturgeon.key.model);
        for (int w = 0; w < 10; w++) {
            if (wheelFunctionSetToPreserve.containsWheel(w, sturgeon.key.wheelSettings)) {
                int wheelFunction = WheelSetting.wheelFunction0to9(w, sturgeon.key.wheelSettings);
                CO.assignFunction(w, wheelFunction, ts);
                ts.key.wheelPositions[w] = sturgeon.key.wheelPositions[w];
            }
        }

        WheelFunctionSet union = wheelFunctionSet.union(wheelFunctionSetToPreserve);

        while (true) {

            ts.randomizeOrderExcept(wheelFunctionSetToPreserve);
            for (int w = 0; w < 10; w++) {
                if (wheelFunctionSetToPreserve.containsWheel(w, ts.key.wheelSettings)) {
                    continue;
                }
                ts.key.wheelPositions[w] = CommonRandom.r.nextInt(Wheels.WHEEL_SIZES[w]);
            }

            if (eval == Eval.CLASS) {
                ts.computeClassFreqRef(ReferenceText.languageFreq, union);
            }

            double bestScoreForCycle = CO.score(eval, ts, cipherArray, null, union);

            bestKeys.push(bestScoreForCycle, ts.key, wheelFunctionSet);


            boolean improved;
            do {
                improved = false;
                for (int w = 0; w < 10; w++) {
                    if (!wheelFunctionSet.containsWheel(w, ts.key.wheelSettings)) {
                        continue;
                    }
                    int randomShift = CommonRandom.r.nextInt(Wheels.WHEEL_SIZES[w]);
                    int bestP = ts.key.wheelPositions[w];
                    for (int p = 0; p < Wheels.WHEEL_SIZES[w]; p++) {
                        ts.key.wheelPositions[w] = (p + randomShift) % Wheels.WHEEL_SIZES[w];
                        double newScore = CO.score(eval, ts, cipherArray, null, union);
                        if (newScore > bestScoreForCycle) {
                            bestScoreForCycle = newScore;
                            bestP = ts.key.wheelPositions[w];
                            improved = true;
                            for (int i = 0; i < 10; i++) {
                                bestKeys.push(bestScoreForCycle, ts.key, wheelFunctionSet);
                            }
                        }
                    }
                    ts.key.wheelPositions[w] = bestP;
                }
                if (!improved) {
                    // swap two wheels
                    int randomShift = CommonRandom.r.nextInt(10);
                    outerloop:
                    for (int w1_ = 0; w1_ < 10; w1_++) {
                        int w1 = (w1_ + randomShift) % 10;
                        if (wheelFunctionSetToPreserve.containsWheel(w1, ts.key.wheelSettings)) {
                            continue;
                        }
                        for (int w2_ = 0; w2_ < 10; w2_++) {
                            int w2 = (w2_ + randomShift) % 10;
                            if (w1 == w2 || wheelFunctionSetToPreserve.containsWheel(w2, ts.key.wheelSettings)) {
                                continue;
                            }
                            if (!wheelFunctionSet.containsWheel(w1, ts.key.wheelSettings) && !wheelFunctionSet.containsWheel(w2, ts.key.wheelSettings)) {
                                continue;
                            }
                            if (eval == Eval.CLASS_IC && wheelFunctionSet.containsWheel(w1, ts.key.wheelSettings) && wheelFunctionSet.containsWheel(w2, ts.key.wheelSettings)) {
                                continue;
                            }
                            if (wheelFunctionSet.containsWheel(w1, ts.key.wheelSettings) && wheelFunctionSet.containsWheel(w2, ts.key.wheelSettings)) {

                                CO.swapWheelsAndComputeMappings(ts, w1, w2);

                                double newScore = CO.score(eval, ts, cipherArray, null, union);

                                if (newScore > bestScoreForCycle) {
                                    bestScoreForCycle = newScore;
                                    improved = true;
                                    for (int i = 0; i < 10; i++)
                                        bestKeys.push(bestScoreForCycle, ts.key, wheelFunctionSet);
                                    break outerloop;
                                } else {
                                    CO.swapWheelsAndComputeMappings(ts, w1, w2);
                                }

                            } else {

                                CO.swapWheelsAndComputeMappings(ts, w1, w2);

                                int w = wheelFunctionSet.containsWheel(w1, ts.key.wheelSettings) ? w1 : w2;

                                int origP = ts.key.wheelPositions[w];
                                int bestP = origP;
                                double scoreBestP = CO.score(eval, ts, cipherArray, null, union);

                                for (int pos = 0; pos < Wheels.WHEEL_SIZES[w]; pos++) {
                                    ts.key.wheelPositions[w] = pos;
                                    double score = CO.score(eval, ts, cipherArray, null, union);
                                    if (score > scoreBestP) {
                                        scoreBestP = score;
                                        bestP = pos;
                                    }
                                }

                                if (scoreBestP > bestScoreForCycle) {
                                    bestScoreForCycle = scoreBestP;
                                    ts.key.wheelPositions[w] = bestP;
                                    improved = true;
                                    for (int i = 0; i < 10; i++)
                                        bestKeys.push(bestScoreForCycle, ts.key, wheelFunctionSet);
                                    break outerloop;
                                } else {
                                    ts.key.wheelPositions[w] = origP;
                                    CO.swapWheelsAndComputeMappings(ts, w1, w2);
                                }
                            }
                        }
                    }
                }

            } while (improved);
        }
    }

    private static void displayResultsThread(ArrayList<WheelFunctionSet> topFunctionSets, BestKeys bestKeys, double[] scores, long startTimeMillis) {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<Result> results = bestKeys.getResults();
            if (results != null) {
                System.out.println();
                double elapsedSec = (System.currentTimeMillis() - startTimeMillis) / 1000.0;
                System.out.printf("Elapsed: %s (rate: %,.0f/second)\n", Utils.elapsedString(elapsedSec), CO.evaluations.longValue() / elapsedSec);
                int[][] positions = new int[results.size()][10];
                int[][] functions = new int[results.size()][10];
                double[] resultsScores = new double[results.size()];
                int resultIndex = 0;
                for (Result result : results) {

                    int index1 = 0;
                    for (WheelFunctionSet top : topFunctionSets) {
                        if (top.equals(result.wheelFunctionSet)) {
                            break;
                        }
                        index1++;
                    }
                    if (index1 == topFunctionSets.size()) {
                        throw new RuntimeException("The selected set is not in the top ones " + result.wheelFunctionSet);
                    }
                    double refScore = scores[index1];
                    System.out.printf("[%2d] %f %s %-30s %s %s %s\n",
                            index1,
                            result.score,
                            (Math.abs(result.score - refScore) < 0.000001) ? "===" : ((result.score < refScore) ? "   " : "!!!"),
                            result.wheelFunctionSet.toString(),
                            result.key.getWheelPositionsString(result.wheelFunctionSet),
                            result.key.getWheelSettingsStringWithFills(result.wheelFunctionSet),
                            result.key.getWheelSettingsString());

                    Arrays.fill(positions[resultIndex], -1);
                    Arrays.fill(functions[resultIndex], -1);
                    for (int w = 0; w < 10; w++) {
                        if (result.wheelFunctionSet.containsWheel(w, result.key.wheelSettings)) {
                            positions[resultIndex][w] = result.key.wheelPositions[w];
                            functions[resultIndex][w] = WheelSetting.wheelFunction0to9(w, result.key.wheelSettings);
                        }
                    }
                    resultsScores[resultIndex] = result.score;
                    resultIndex++;
                }

            }
        }
    }


}
