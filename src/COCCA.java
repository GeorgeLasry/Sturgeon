import java.util.*;

public class COCCA {
    static void solve(Eval eval,
                      final Sturgeon sturgeon,
                      final byte[][] cipherArray,
                      final int wheelsNumberInSet,
                      int numberOfTopSets,
                      final WheelFunctionSet wheelFunctionSetToPreserve,
                      final long timeoutMillis) {

        final BestPartialKeys bestPartialKeys = new BestPartialKeys();
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
        runnables.add(() -> displayResultsThread(topFunctionSets, wheelFunctionSetToPreserve, bestPartialKeys, scores, startTimeMillis));

        do {
            for (final WheelFunctionSet wheelFunctionSet : topFunctionSets) {
                runnables.add(() -> computeResultsThread(eval, sturgeon, cipherArray, bestPartialKeys, wheelFunctionSetToPreserve, wheelFunctionSet, timeoutMillis, startTimeMillis));
            }
            if (runnables.size() >= Runnables.THREADS) {
                break;
            }
        } while (runnables.size() < Runnables.THREADS);
        runnables.runAll();
    }

    private static void computeResultsThread(Eval eval, Sturgeon sturgeon, byte[][] cipherArray, BestPartialKeys bestPartialKeys,
                                             WheelFunctionSet wheelFunctionSetToPreserve, WheelFunctionSet wheelFunctionSet, long timeoutMillis, long startTimeMillis) {
        Sturgeon ts = new Sturgeon(sturgeon.key.model);
        for (int w = 0; w < 10; w++) {
            if (wheelFunctionSetToPreserve.containsWheel(w, sturgeon.key.wheelSettings)) {
                int wheelFunction = WheelSetting.wheelFunction0to9(w, sturgeon.key.wheelSettings);
                CO.assignFunction(w, wheelFunction, ts);
                ts.key.wheelPositions[w] = sturgeon.key.wheelPositions[w];
            }
        }

        WheelFunctionSet union = wheelFunctionSet.union(wheelFunctionSetToPreserve);

        while (System.currentTimeMillis() - startTimeMillis < timeoutMillis) {

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

            bestPartialKeys.push(bestScoreForCycle, ts.key, wheelFunctionSet);

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
                            bestPartialKeys.push(bestScoreForCycle, ts.key, wheelFunctionSet);
                        }
                    }
                    ts.key.wheelPositions[w] = bestP;
                }
                if (!improved) {
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
                                    bestPartialKeys.push(bestScoreForCycle, ts.key, wheelFunctionSet);
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
                                    bestPartialKeys.push(bestScoreForCycle, ts.key, wheelFunctionSet);
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

    private static void displayResultsThread(ArrayList<WheelFunctionSet> topFunctionSets, WheelFunctionSet wheelFunctionSetToPreserve, BestPartialKeys bestPartialKeys, double[] scores, long startTimeMillis) {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<Result> results = bestPartialKeys.getAllIfChanged();
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
                            result.wheelFunctionSet,
                            result.key.getWheelPositionsString(result.wheelFunctionSet.union(wheelFunctionSetToPreserve)),
                            result.key.getWheelSettingsStringWithFills(result.wheelFunctionSet.union(wheelFunctionSetToPreserve)),
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
                class Summary {
                    int count = 0;
                    private double score;
                    private int f1;
                    private int f2;
                    private int f3;
                    private int w1;
                    private int w2;
                    private int w3;
                    private int p1;
                    private int p2;
                    private int p3;
                    private final Set<Integer> set1 = new TreeSet<>();
                    private final Set<Integer> set2 = new TreeSet<>();
                    private final Set<Integer> set3 = new TreeSet<>();
                }

                ArrayList<TreeSet<Integer>> positionsSet = new ArrayList<>();
                for (int w = 0; w < 10; w++) {
                    TreeSet<Integer> set = new TreeSet<>();
                    for (resultIndex = 0; resultIndex < results.size(); resultIndex++) {
                        if (positions[resultIndex][w] > -1) {
                            set.add(positions[resultIndex][w]);
                        }
                    }
                    positionsSet.add(set);
                }

                ArrayList<Summary> summaries = new ArrayList<>();
                for (int w1 = 0; w1 < 10; w1++) {
                    for (int p1 : positionsSet.get(w1)) {
                        for (int w2 = w1 + 1; w2 < 10; w2++) {
                            for (int p2 : positionsSet.get(w2)) {
                                Summary s = new Summary();

                                for (resultIndex = 0; resultIndex < results.size(); resultIndex++) {
                                    if (positions[resultIndex][w1] != p1 || positions[resultIndex][w2] != p2) {
                                        continue;
                                    }
                                    s.f1 = functions[resultIndex][w1];
                                    s.f2 = functions[resultIndex][w2];
                                    if (!s.set1.isEmpty() && !s.set1.contains(s.f1) && !s.set2.contains(s.f1)) {
                                        continue;
                                    } else if (!s.set2.isEmpty() && !s.set1.contains(s.f2) && !s.set2.contains(s.f2)) {
                                        continue;
                                    }
                                    s.set1.add(s.f1);
                                    s.set2.add(s.f2);
                                    s.score += resultsScores[resultIndex];
                                    s.count++;
                                }
                                if (s.count >= 2) {
                                    s.w1 = w1;
                                    s.w2 = w2;
                                    s.p1 = p1;
                                    s.p2 = p2;
                                    s.score /= s.count;
                                    summaries.add(s);
                                }
                            }
                        }
                    }
                }
                summaries.sort((o1, o2) -> {
                    if (o2.count != o1.count) {
                        return o2.count - o1.count;
                    }
                    return (int) (1_000_000 * (o2.score - o1.score));
                });
                for (Summary s : summaries) {
                    System.out.printf("Count: %2d (%6f) Wheels: %d-%d Position: %2d-%2d Functions: ", s.count, s.score, s.w1, s.w2, s.p1 + 1, s.p2 + 1);
                    for (Integer f : s.set1) {
                        System.out.printf(" %s", WheelFunctionSet.FUNCTION_STRING[f]);
                    }
                    System.out.print("/");
                    for (Integer f : s.set2) {
                        System.out.printf(" %s", WheelFunctionSet.FUNCTION_STRING[f]);
                    }
                    System.out.println();
                }

                summaries.clear();
                for (int w1 = 0; w1 < 10; w1++) {
                    for (int p1 : positionsSet.get(w1)) {
                        for (int w2 = w1 + 1; w2 < 10; w2++) {
                            for (int p2 : positionsSet.get(w2)) {
                                for (int w3 = w2 + 1; w3 < 10; w3++) {
                                    for (int p3 : positionsSet.get(w3)) {
                                        Summary s = new Summary();
                                        for (resultIndex = 0; resultIndex < results.size(); resultIndex++) {
                                            if (positions[resultIndex][w1] != p1 || positions[resultIndex][w2] != p2 || positions[resultIndex][w3] != p3) {
                                                continue;
                                            }
                                            s.f1 = functions[resultIndex][w1];
                                            s.f2 = functions[resultIndex][w2];
                                            s.f3 = functions[resultIndex][w3];
                                            if (!s.set1.isEmpty() && !s.set1.contains(s.f1) && !s.set2.contains(s.f1)) {
                                                continue;
                                            } else if (!s.set2.isEmpty() && !s.set1.contains(s.f2) && !s.set2.contains(s.f2)) {
                                                continue;
                                            }
                                            if (!s.set1.isEmpty() && !s.set1.contains(s.f1) && !s.set3.contains(s.f1)) {
                                                continue;
                                            } else if (!s.set3.isEmpty() && !s.set1.contains(s.f3) && !s.set3.contains(s.f3)) {
                                                continue;
                                            }
                                            if (!s.set3.isEmpty() && !s.set3.contains(s.f3) && !s.set2.contains(s.f3)) {
                                                continue;
                                            } else if (!s.set2.isEmpty() && !s.set3.contains(s.f2) && !s.set2.contains(s.f2)) {
                                                continue;
                                            }
                                            s.set1.add(s.f1);
                                            s.set2.add(s.f2);
                                            s.set3.add(s.f3);
                                            s.score += resultsScores[resultIndex];
                                            s.count++;
                                        }
                                        if (s.count >= 2) {
                                            s.w1 = w1;
                                            s.w2 = w2;
                                            s.w3 = w3;
                                            s.p1 = p1;
                                            s.p2 = p2;
                                            s.p3 = p3;
                                            s.score /= s.count;
                                            summaries.add(s);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                summaries.sort((o1, o2) -> {
                    if (o2.count != o1.count) {
                        return o2.count - o1.count;
                    }
                    return (int) (1_000_000 * (o2.score - o1.score));
                });
                for (Summary s : summaries) {
                    System.out.printf("Count: %2d (%6f) Wheels: %d-%d-%d Position: %2d-%2d-%2d Functions: ", s.count, s.score, s.w1, s.w2, s.w3, s.p1 + 1, s.p2 + 1, s.p3 + 1);
                    for (Integer f : s.set1) {
                        System.out.printf(" %s", WheelFunctionSet.FUNCTION_STRING[f]);
                    }
                    System.out.print("/");
                    for (Integer f : s.set2) {
                        System.out.printf(" %s", WheelFunctionSet.FUNCTION_STRING[f]);
                    }
                    System.out.print("/");
                    for (Integer f : s.set3) {
                        System.out.printf(" %s", WheelFunctionSet.FUNCTION_STRING[f]);
                    }
                    System.out.println();
                }
            }
        }
    }

    static void solveFourWheelsFunctionsAssigned(Eval eval,
                                                     final Sturgeon sturgeon,
                                                     final byte[][] cipherArray,
                                                     final WheelFunctionSet wheelFunctionSet,
                                                     double phase1score) {
        if (wheelFunctionSet.size() < 4) {
            throw new RuntimeException("Should have 4 functions (" + wheelFunctionSet.size() + ")");
        }

        final long startTimeMillis = System.currentTimeMillis();

        Runnables r = new Runnables();
        Runnables.THREADS = 60;
        for (int t = 0; t < Runnables.THREADS; t++) {

            final int task_ = t;
            r.add(() -> {
                final Sturgeon ts = new Sturgeon(sturgeon.key);

                ts.resetPositionsAndComputeMappings();
                ts.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);

                CO.solve(eval, ts, sturgeon, cipherArray, wheelFunctionSet, 20, startTimeMillis, task_, phase1score);
            });

        }
        for (; ; ) {
            r.runAll();
        }


    }

    static void solveEightWheelsWhileSweepingTwoWheelsPositions(Eval eval,
                                                                        final Sturgeon sturgeon,
                                                                        final byte[][] cipherArray,
                                                                        final WheelFunctionSet wheelFunctionSet,
                                                                        double phase1score) {
        if (wheelFunctionSet.size() != 2) {
            throw new RuntimeException("Should have 2 functions (" + wheelFunctionSet.size() + ")");
        }
        final ArrayList<Integer> functions = wheelFunctionSet.functions();
        final int w1 = WheelSetting.wheelFunction0to9(functions.get(0), sturgeon.key.wheelSettings);
        final int w2 = WheelSetting.wheelFunction0to9(functions.get(1), sturgeon.key.wheelSettings);
        final long startTimeMillis = System.currentTimeMillis();
        final WheelFunctionSet remaining = wheelFunctionSet.remaining();

        Runnables r = new Runnables();
        Runnables.THREADS = 8;
        int task = 0;

        for (int p1 = 0; p1 < Wheels.WHEEL_SIZES[w1]; p1++) {
            for (int p2 = 0; p2 < Wheels.WHEEL_SIZES[w2]; p2++) {
                final int p1_ = p1;
                final int p2_ = p2;
                final int task_ = task;
                r.add(() -> {
                    final Sturgeon ts = new Sturgeon(sturgeon.key);
                    ts.key.wheelPositions[w1] = p1_;
                    ts.key.wheelPositions[w2] = p2_;
                    if (eval == Eval.CLASS) {
                        ts.computeClassFreqRef(ReferenceText.languageFreq, remaining);
                    }

                    CO.solve(eval, ts, sturgeon, cipherArray, remaining, 20, startTimeMillis, task_, phase1score);
                });
                task++;
            }
        }
        for (; ; ) {
            r.runAll();
        }


    }

    static void solveFourWheelsFunctionsNotAssigned(Eval eval,
                                Sturgeon sturgeon,
                                final byte[][] cipherArray,
                                final WheelFunctionSet wheelFunctionSet,
                                double phase1score) {
        final ArrayList<Integer> functions = wheelFunctionSet.functions();
        final long startTimeMillis = System.currentTimeMillis();

        if (wheelFunctionSet.size() < 4) {
            throw new RuntimeException("Should have 4 functions (" + wheelFunctionSet.size() + ")");
        }
        Runnables r = new Runnables();
        Runnables.THREADS = 60;
        int task = 0;
        for (int w0 = 0; w0 < 10; w0++) {
            for (int w1 = 0; w1 < 10; w1++) {
                if (w1 == w0) {
                    continue;
                }
                for (int w2 = 0; w2 < 10; w2++) {
                    if (w2 == w0 || w2 == w1) {
                        continue;
                    }
                    for (int w3 = 0; w3 < 10; w3++) {
                        if (w3 == w0 || w3 == w1 || w3 == w2) {
                            continue;
                        }
                        final int w0_ = w0;
                        final int w1_ = w1;
                        final int w2_ = w2;
                        final int w3_ = w3;
                        final int task_ = task;
                        r.add(() -> {
                            final Sturgeon ts = new Sturgeon(sturgeon.key.model);
                            CO.assignFunction(w0_, functions.get(0), ts);
                            CO.assignFunction(w1_, functions.get(1), ts);
                            CO.assignFunction(w2_, functions.get(2), ts);
                            CO.assignFunction(w3_, functions.get(3), ts);

                            ts.resetPositionsAndComputeMappings();
                            ts.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);

                            CO.solve(eval, ts, sturgeon, cipherArray, wheelFunctionSet, 20, startTimeMillis, task_, phase1score);
                        });

                        task++;

                    }
                }
            }
        }
        for (; ; ) {
            r.runAll();
            System.out.print(".");
        }


    }



}
