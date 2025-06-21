import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

enum Eval {NGRAMS, IC, CLASS, CLASS_IC}

public class CO {

    static AtomicLong evaluations = new AtomicLong(0);
    static ReadWriteLock bestScoreEverLock = new ReentrantReadWriteLock();
    static double bestScoreEver = 0.0;


    public static void main(String[] args) {

        CommonRandom.randomSeed();
        Runnables.THREADS = 60;
        //ReferenceText.create(false, "klaus.txt");
        //ReferenceText.create(false, "shakespeare.txt");
        ReferenceText.create(false);
        //ReferenceText.create(false, "nils", "scientific");

        int depth = 1;
        byte[][] plainArray = new byte[depth][];
        byte[][] cipherArray = new byte[depth][];

        Sturgeon sturgeon;

        //sturgeon = Scenario.preparedScenario(39, 0, 477, plainArray, cipherArray);// 6 needs 1000+, 7 needs 800, all the rest 500 with standard config
        sturgeon = Scenario.preparedScenario(39, 0, 10000, plainArray, cipherArray);// 6 needs 1000+, 7 needs 800, all the rest 500 with standard config


        //sturgeon = ReferenceText.simulation(Model.T52AB, depth, 10000, plainArray, cipherArray, true, true);//
        //sturgeon = ReferenceText.simulation(Model.T52C, depth, 1400, plainArray, cipherArray, true, false);// CA 1200-1400; c: 800-1000
        //sturgeon = ReferenceText.simulation(Model.T52CA, depth, 1400, plainArray, cipherArray, true, false);// CA 1200-1400; c: 800-1000


        //printStats(sturgeon, cipherArray, plainArray, new WheelFunctionSet(WheelFunctionSet.PERM_7, WheelFunctionSet.XOR_III, WheelFunctionSet.XOR_IV, WheelFunctionSet.XOR_V));
        //printStats(sturgeon, cipherArray, plainArray, WheelFunctionSet.SELECTION_CA_3_7_II_III.union(WheelFunctionSet.SELECTION_C_7_9_I_IV));
        //printStats(sturgeon, cipherArray, plainArray, WheelFunctionSet.SELECTION_C_7_9_I_IV);
        printStats(sturgeon, cipherArray, plainArray, WheelFunctionSet.SELECTION_XOR_ALL);

        //System.exit(0);
        //permuteWheels(Eval.NGRAMS, sturgeon, sturgeon, WheelFunctionSet.SELECTION_ALL, cipherArray) ;

        //benchmark(Eval.CLASS, sturgeon, cipherArray, new WheelFunctionSet(WheelFunctionSet.XOR_III, WheelFunctionSet.XOR_V, WheelFunctionSet.PERM_3, WheelFunctionSet.PERM_5), 100000000, false, true);

        // Don't know anything... start with a default selection.
        //AB

        COAB.solve(Eval.CLASS, Eval.NGRAMS, sturgeon, cipherArray, true, 0.033, 0.03);

        //C/CA


        //COCCA.solveFourWheelsFunctionsNotAssigned(Eval.CLASS, sturgeon, cipherArray, WheelFunctionSet.SELECTION_C_7_9_I_IV, 0.0);

        //COCCA.solveFourWheelsFunctionsAssigned(Eval.CLASS, sturgeon, cipherArray, WheelFunctionSet.SELECTION_CA_3_7_III_V, 1.043);
        //solveT52CandCA4WheelsSameSelection(Eval.CLASS, sturgeon, cipherArray, WheelFunctionSet.SELECTION_C_7_9_I_IV, 1.043);
        //COCCA.solveT52CandCA4Wheels(Eval.CLASS, sturgeon, cipherArray, WheelFunctionSet.SELECTION_C_7_9_I_IV, 1.043);
        //solveT52CandCA4WheelsSameSelection(Eval.CLASS, sturgeon, cipherArray, topFunctionSets.get(0), 1.043);
        //solveT52CandCAPositionsSweep2Wheels(Eval.NGRAMS, sturgeon, cipherArray, new WheelFunctionSet(WheelFunctionSet.PERM_3, WheelFunctionSet.PERM_5), 0.045);

        //COCCA.solve(Eval.CLASS, sturgeon, cipherArray, 6, 15, WheelFunctionSet.SELECTION_NONE, 1300000);
        //COCCA.solve(Eval.CLASS, sturgeon, cipherArray, 4, 1, WheelFunctionSet.SELECTION_NONE, 1300000);
        //COCCA.solve(Eval.CLASS, sturgeon, cipherArray, 4, 15, WheelFunctionSet.SELECTION_CA_3_7_III_V, 1300000);

        //COC.solve(Eval.CLASS, sturgeon, cipherArray, 4, 1, WheelFunctionSet.SELECTION_NONE,1.04);


        COCCA.solve(Eval.CLASS, sturgeon, cipherArray, 4, 10,
                new WheelFunctionSet(-1,
//                        WheelFunctionSet.PERM_1,
//                        WheelFunctionSet.PERM_3,
//                        WheelFunctionSet.PERM_5,
//                        WheelFunctionSet.PERM_7,
//                        WheelFunctionSet.PERM_9,
//                        WheelFunctionSet.XOR_I,
//                        WheelFunctionSet.XOR_II,
//                        WheelFunctionSet.XOR_III,
//                        WheelFunctionSet.XOR_IV,
//                        WheelFunctionSet.XOR_V,
                        -1),
                1300000);


        COCCA.solve(Eval.NGRAMS, sturgeon, cipherArray, 6, 10,
                new WheelFunctionSet(-1,
//                        WheelFunctionSet.PERM_1,
                        WheelFunctionSet.PERM_3,
                        WheelFunctionSet.PERM_5,
//                        WheelFunctionSet.PERM_7,
//                        WheelFunctionSet.PERM_9,
//                        WheelFunctionSet.XOR_I,
//                        WheelFunctionSet.XOR_II,
                          WheelFunctionSet.XOR_III,
//                        WheelFunctionSet.XOR_IV,
                          WheelFunctionSet.XOR_V,
                        -1),
                1300000);


        // Know the settings of some wheels (and perm types),

//        solveWithWheelsShuffles(Eval.NGRAMS, sturgeon, cipherArray,
//                new WheelFunctionSet(-1,
////                        WheelFunctionSet.XOR_I,
//                        WheelFunctionSet.XOR_II,
//                        WheelFunctionSet.XOR_III,
////                        WheelFunctionSet.XOR_IV,
//                        WheelFunctionSet.XOR_V,
//                        WheelFunctionSet.PERM_1,
//                        WheelFunctionSet.PERM_3,
//                        WheelFunctionSet.PERM_5,
////                        WheelFunctionSet.PERM_7,
////                        WheelFunctionSet.PERM_9,
//                        -1
//                ),
//                200);


//        // know all about some or all wheels - settings and order. Knows the perm types
//        solve(Eval.IC, new Sturgeon(sturgeon.key), sturgeon, cipherArray,
//                new WheelFunctionSet(
//                        WheelFunctionSet.PERM_1,
//                        WheelFunctionSet.PERM_3,
//                        WheelFunctionSet.PERM_5,
//                        WheelFunctionSet.PERM_7,
//                        WheelFunctionSet.PERM_9,
//                        WheelFunctionSet.XOR_I,
//                        WheelFunctionSet.XOR_II,
//                        WheelFunctionSet.XOR_III,
//                        WheelFunctionSet.XOR_IV,
//                        WheelFunctionSet.XOR_V,
//                        -1
//                ),
//
//                50000, System.currentTimeMillis(), 0, 0);


        // Only if you know all but might change the types.
        //COAB.bestT52ABSwitchType(Eval.NGRAMS, sturgeon, cipherArray, 50);


        //solvePositions(Eval.NGRAMS, sturgeon, cipherArray, 50000, 0.06);

        //benchmark(Eval.CLASS, sturgeon, cipherArray, WheelFunctionSet.SELECTION_XOR_ALL, 100_000, true, true);
    }


    static void swapWheelsAndComputeMappings(Sturgeon sturgeon, int w1, int w2) {
        WheelSetting ws = sturgeon.key.wheelSettings[w1];
        sturgeon.key.wheelSettings[w1] = sturgeon.key.wheelSettings[w2];
        sturgeon.key.wheelSettings[w2] = ws;
        sturgeon.computeMappings();
    }

    static void printScore(String desc, int task, Sturgeon sturgeon, WheelFunctionSet wheelFunctionSet, long start, double newScore, double origScore) {
        System.out.printf("[%4d] [%-30s] [%8f (%8f)] [%s] [%40s] [%s] [%s] [%s] [%s] [Elapsed = %,5.1f sec Evals: %,10d Rate %,5dk/sec]\n",
                task,
                desc,
                newScore,
                origScore,
                sturgeon.key.getWheelPositionsString(wheelFunctionSet),
                sturgeon.key.getWheelSettingsStringWithFills(wheelFunctionSet),
                sturgeon.key.getPermTypeString(),
                sturgeon.key.getWheelPositionsString(),
                sturgeon.key.getWheelSettingsString(),
                sturgeon.key.getWheelMsgSettingsString(),
                (System.currentTimeMillis() - start) / 1000.0,
                evaluations.longValue(),
                evaluations.longValue() / (System.currentTimeMillis() - start + 1)
        );
    }

    private static void randomizeOrder(Sturgeon sturgeon, WheelFunctionSet wheelFunctionSet) {


        for (int i = 0; i < 100; i++) {

            int w1 = -1, w2 = -1;
            int start = CommonRandom.r.nextInt(10);
            for (int w = 0; w < 10; w++) {
                w1 = (w + start) % 10;
                if (wheelFunctionSet.containsWheel(w1, sturgeon.key.wheelSettings)) {
                    break;
                }
            }
            start = CommonRandom.r.nextInt(10);
            for (int w = 0; w < 10; w++) {
                w2 = (w + start) % 10;
                if (w2 != w1 && wheelFunctionSet.containsWheel(w2, sturgeon.key.wheelSettings)) {
                    break;
                }
            }
            WheelSetting temp = sturgeon.key.wheelSettings[w1];
            sturgeon.key.wheelSettings[w1] = sturgeon.key.wheelSettings[w2];
            sturgeon.key.wheelSettings[w2] = temp;

        }
        sturgeon.computeMappings();
    }

    private static void solveWithWheelsShuffles(Eval eval, Sturgeon sturgeon, byte[][] cipherArray, WheelFunctionSet wheelFunctionSet, int timeout) {
        long overallStart = System.currentTimeMillis();
        bestScoreEver = 0;
        Runnables r = new Runnables();
        for (int t = 0; t < 100; t++) {
            final int task_ = t;
            r.add(() -> {
                Set<String> set = new TreeSet<>();
                while (true) {
                    Sturgeon stu = new Sturgeon(sturgeon.key);
                    randomizeOrder(stu, wheelFunctionSet);

                    //stu.randomize();

                    String wheelSettingsKey = stu.key.getWheelSettingsString(wheelFunctionSet);
                    if (set.contains(wheelSettingsKey)) {
                        continue;
                    }
                    set.add(wheelSettingsKey);
                    solve(eval, stu, sturgeon, cipherArray, wheelFunctionSet, timeout, overallStart, task_, 0.0);
                }
            });
        }
        r.runAll();


    }

    private static void solvePositions(Eval eval, Sturgeon sturgeon, byte[][] cipherArray, int timeout, double phase1Score) {
        long overallStart = System.currentTimeMillis();
        bestScoreEver = 0;
        Runnables r = new Runnables();
        for (int t = 0; t < 12; t++) {
            final int task_ = t;
            r.add(() -> {
                while (true) {
                    Sturgeon stu = new Sturgeon(sturgeon.key);
                    solve(eval, stu, sturgeon, cipherArray, WheelFunctionSet.SELECTION_ALL, timeout, overallStart, task_, phase1Score);
                }
            });
        }
        r.runAll();


    }

    static double score(Eval eval, Sturgeon sturgeon, byte[][] cipherArray, byte[] plain, WheelFunctionSet wheelFunctionSet) {
        evaluations.incrementAndGet();
        if (eval == Eval.CLASS || eval == Eval.CLASS_IC) {
            return sturgeon.classScore(cipherArray, wheelFunctionSet, eval == Eval.CLASS_IC);
        }

        int len = cipherArray[0].length;
        if (plain == null) {
            plain = new byte[len];
        }
        int depth = cipherArray.length;

        double newScore = 0;
        for (byte[] cipher : cipherArray) {
            sturgeon.encryptDecrypt(cipher, plain, len, null, false);

            switch (eval) {
                case IC -> newScore += Stats.computeIc(len, plain);
                case NGRAMS -> newScore += Stats.matchStreamWithFreq(len, plain, ReferenceText.languageFreq);
            }
        }
        return newScore / depth;
    }

    /*
        Find positions for a subset of wheels (and optionally, their function), not changing the others.
        Not changing the function unless achieving a minimal score.
     */
    static void solve(Eval eval, Sturgeon sturgeon, Sturgeon real, byte[][] cipherArray, WheelFunctionSet wheelFunctionSet, long timeoutMs, long overallStart, int task, double phase1Score) {

        sturgeon.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);

        long start = System.currentTimeMillis();

        int len = cipherArray[0].length;
        byte[] plain = new byte[len];
        String realPlainS = "";
        double realScore = 0.0;
        if (real != null) {
            bestScoreEverLock.writeLock().lock();
            real.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);
            bestScoreEverLock.writeLock().unlock();
            realScore = score(eval, real, cipherArray, plain, wheelFunctionSet);
            real.encryptDecrypt(cipherArray[0], plain, len, null, false);
            realPlainS = Alphabet.toStringFormatted(plain, true);
        }

        do {

            for (int w = 0; w < 10; w++) {
                if (wheelFunctionSet.containsWheel(w, sturgeon.key.wheelSettings)) {
                    sturgeon.key.wheelPositions[w] = CommonRandom.r.nextInt(Wheels.WHEEL_SIZES[w]);
                }
            }

            double bestScoreForCycle = score(eval, sturgeon, cipherArray, plain, wheelFunctionSet);
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
                        double newScore = score(eval, sturgeon, cipherArray, plain, wheelFunctionSet);
                        if (newScore > bestScoreForCycle) {
                            bestScoreForCycle = newScore;
                            bestP = sturgeon.key.wheelPositions[w];
                            improved = true;
                            bestScoreEverLock.readLock().lock();
                            boolean better = newScore > bestScoreEver;
                            bestScoreEverLock.readLock().unlock();
                            if (better) {
                                bestScoreEverLock.writeLock().lock();
                                bestScoreEver = newScore;

                                printScore("1", task, sturgeon, wheelFunctionSet, overallStart, newScore, phase1Score);
                                if (real != null) {
                                    printScore("R", task, real, wheelFunctionSet, overallStart, realScore, real.key.score);
                                }
                                sturgeon.encryptDecrypt(cipherArray[0], plain, len, null, false);
                                System.out.printf("%s\n", Alphabet.toStringFormatted(plain, true));
                                if (real != null) {
                                    System.out.printf("%s\n", realPlainS);
                                }
                                bestScoreEverLock.writeLock().unlock();
                            }
                        }
                    }
                    sturgeon.key.wheelPositions[w] = bestP;
                }

                if (!improved && bestScoreForCycle > phase1Score) {
                    int[] bestPositions = new int[10];
                    outer:
                    for (int w1 = 0; w1 < 10; w1++) {
                        if (!wheelFunctionSet.containsWheel(w1, sturgeon.key.wheelSettings)) {
                            continue;
                        }

                        for (int w2 = w1 + 1; w2 < 10; w2++) {
                            if (!wheelFunctionSet.containsWheel(w2, sturgeon.key.wheelSettings)) {
                                continue;
                            }

                            System.arraycopy(sturgeon.key.wheelPositions, 0, bestPositions, 0, 10);
                            for (int p1 = 0; p1 < Wheels.WHEEL_SIZES[w1]; p1++) {
                                sturgeon.key.wheelPositions[w1] = p1;
                                for (int p2 = 0; p2 < Wheels.WHEEL_SIZES[w2]; p2++) {
                                    sturgeon.key.wheelPositions[w2] = p2;

                                    double newScore = score(eval, sturgeon, cipherArray, plain, wheelFunctionSet);
                                    if (newScore > bestScoreForCycle) {
                                        //System.out.printf("[%d] Helpful %f -> %f \n", task, bestScoreForCycle, newScore);
                                        bestScoreForCycle = newScore;
                                        improved = true;
                                        bestScoreEverLock.readLock().lock();
                                        boolean better = newScore > bestScoreEver;
                                        bestScoreEverLock.readLock().unlock();
                                        if (better) {
                                            bestScoreEverLock.writeLock().lock();
                                            bestScoreEver = newScore;
                                            printScore("2", task, sturgeon, wheelFunctionSet, overallStart, newScore, phase1Score);
                                            if (real != null) {
                                                printScore("R", task, real, wheelFunctionSet, overallStart, realScore, real.key.score);
                                            }
                                            sturgeon.encryptDecrypt(cipherArray[0], plain, len, null, false);
                                            System.out.printf("%s\n", Alphabet.toStringFormatted(plain, true));
                                            if (real != null) {
                                                System.out.printf("%s\n", realPlainS);
                                            }
                                            bestScoreEverLock.writeLock().unlock();
                                        }
                                        break outer;

                                    }
                                }
                            }
                            System.arraycopy(bestPositions, 0, sturgeon.key.wheelPositions, 0, 10);

                            //                          }
                        }
                    }
                    if (!improved && bestScoreForCycle < bestScoreEver) {
                        //System.out.printf("[%d] Not helpful %f %s\n", task, bestScoreForCycle, sturgeon.key.getWheelPositionsString(wheelFunctionSet) );
                    }
                }

            } while (improved);
        } while (System.currentTimeMillis() - start < timeoutMs);
    }

    /*
        Print all details on scenario, including expected scores.
     */
    private static void printStats(Sturgeon sturgeon, byte[][] cipherArray, byte[][] plainArray, WheelFunctionSet wheelFunctionSet) {
        System.out.print("\nScenario details:\n\n");
        System.out.printf("Key:               %s\n", sturgeon.key.toString());

        sturgeon.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);
        int depth = cipherArray.length;
        for (int d = 0; d < depth; d++) {
            byte[] plain = plainArray[d];
            byte[] cipher = cipherArray[d];
            int len = cipher.length;
            byte[] decrypt = new byte[len];

            sturgeon.encryptDecrypt(cipher, decrypt, len, null, false);
            System.out.printf("Length:            %d\n", cipherArray[d].length);
            System.out.printf("Cipher:            %s\n", Alphabet.toString(cipher, len));
            if (plain.length > 0) {
                System.out.printf("Plain:             %s\n", Alphabet.toString(plain));
            }
            System.out.printf("Decryption:        %s\n", Alphabet.toString(decrypt));
            if (plain.length > 0) {
                System.out.printf("Plain:             %s\n", Alphabet.toStringFormatted(plain, true));
            }
            System.out.printf("Decryption:        %s\n", Alphabet.toStringFormatted(decrypt, true));

            System.out.printf("%s\n\n", Alphabet.formatGerman(decrypt));  System.out.println();
            System.out.print("Initial positions: ");
            for (int i = 0; i < 10; i++) System.out.printf("%02d:", sturgeon.key.wheelPositions[i] + 1);
            System.out.println();
            System.out.print("final   positions: ");
            for (int i = 0; i < 10; i++) System.out.printf("%02d:", sturgeon.wheelCurrentPositions[i] + 1);
            System.out.println();

        }
        byte[] plain = new byte[50_000_000];
        System.out.printf("Ngram:             %f\n", score(Eval.NGRAMS, sturgeon, cipherArray, plain, wheelFunctionSet));
        System.out.printf("IC:                %f\n", score(Eval.IC, sturgeon, cipherArray, plain, wheelFunctionSet));
        if (wheelFunctionSet != null) {
            System.out.printf("ClassIC:           %f (%s)\n", score(Eval.CLASS_IC, sturgeon, cipherArray, plain, wheelFunctionSet), wheelFunctionSet);
            System.out.printf("Class:             %f (%s)\n", score(Eval.CLASS, sturgeon, cipherArray, plain, wheelFunctionSet), wheelFunctionSet);
        }

        final ArrayList<WheelFunctionSet> topFunctionSets = WheelFunctionSet.topWheelFunctionSets(sturgeon.key.model,
                10, 4, false, null, true);

        System.out.println("Class score and classIC scores on top function sets");

        int index = 0;
        for (WheelFunctionSet set : topFunctionSets) {
            sturgeon.computeClassFreqRef(ReferenceText.languageFreq, set);
            double scoreClass = score(Eval.CLASS, sturgeon, cipherArray, null, set);
            double scoreIcClass = score(Eval.CLASS_IC, sturgeon, cipherArray, null, set);
            printScore("Real " + set,
                    index++,
                    sturgeon,
                    set,
                    System.currentTimeMillis(),
                    scoreClass,
                    scoreIcClass);
        }

    }

    /*
        Measure speed for scoring
     */
    private static void benchmark(Eval eval, Sturgeon sturgeon, byte[][] cipherArray, WheelFunctionSet wheelFunctionSet, int count, boolean reset, boolean score) {
        sturgeon.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);
        int len = cipherArray[0].length;
        byte[] plain = new byte[len];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            if (reset) {
                sturgeon.resetPositionsAndComputeMappings();
            }
            if (score) {
                score(eval, sturgeon, cipherArray, plain, wheelFunctionSet);
            }
            if (i % 10000 == 0 && i > 0) {
                long time = System.currentTimeMillis();
                System.out.printf("%s Length = %d Computed: %,10d, %,10d/ sec\n", eval, len, i, (int) (1000 * (i / (time - startTime))));
            }
        }
        System.out.print("Computed\n");
    }

    private static void permuteWheels(final Eval eval, final Sturgeon sturgeon, final Sturgeon simulationSturgeon, final WheelFunctionSet wheelFunctionSet, final byte[][] cipherArray) {
        final long startTime = System.currentTimeMillis();
        final WheelSetting[] ws = sturgeon.key.wheelSettings;
        final int len = cipherArray[0].length;

        bestScoreEver = 0.0;
        String realPlainS1 = "";
        double realScore1 = 0.0;
        if (simulationSturgeon != null) {
            byte[] plain1 = new byte[len];
            simulationSturgeon.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);
            realScore1 = score(eval, simulationSturgeon, cipherArray, plain1, wheelFunctionSet);
            simulationSturgeon.encryptDecrypt(cipherArray[0], plain1, len, null, false);
            realPlainS1 = Alphabet.toStringFormatted(plain1, true);
        }
        final String realPlainS = realPlainS1;
        final double realScore = realScore1;
        Runnables r = new Runnables();
        for (int t = 0; t < 10; t++) {
            int task = t;
            r.add(() -> {

                Sturgeon ts = new Sturgeon(sturgeon.key);
                ts.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);

                byte[] plain = new byte[len];
                int[] perm = new int[10];


                for (int index = task; index < Perms.PERMS[5].length * Perms.PERMS[5].length * Partition10.PARTITIONS.length; index += 10) {
                    Partition10.getPermutation(index, perm);
                    for (int w = 0; w < 10; w++) {
                        ts.key.wheelSettings[w] = ws[perm[w]];
                    }
                    ts.computeMappings();
                    //ts.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);
                    double newScore = score(eval, ts, cipherArray, null, wheelFunctionSet);
                    bestScoreEverLock.readLock().lock();
                    boolean better = newScore > bestScoreEver;
                    bestScoreEverLock.readLock().unlock();
                    if (better) {
                        bestScoreEverLock.writeLock().lock();
                        better = newScore > bestScoreEver;
                        if (better) {
                            bestScoreEver = newScore;
                        }
                        bestScoreEverLock.writeLock().unlock();
                        if (better) {
                            printScore("" + index, 0, ts, wheelFunctionSet, startTime, newScore, 0.0);
                            if (simulationSturgeon != null) {
                                printScore("", 0, simulationSturgeon, wheelFunctionSet, startTime, realScore, 0.0);
                            }
                            ts.encryptDecrypt(cipherArray[0], plain, len, null, false);
                            System.out.printf("%s\n", Alphabet.toStringFormatted(plain, true));
                            if (simulationSturgeon != null) {
                                System.out.printf("%s\n", realPlainS);
                            }
                        }
                    }
                }
            });
        }
        r.runAll();
    }

    static void assignFunction(int wheel, int wheelFunction, Sturgeon sturgeon) {

        if (sturgeon.key.wheelSettings[wheel].getFunction() == WheelSetting.WheelFunction.PROGRAMMABLE_PERM_SWITCH) {
            throw new RuntimeException("Cannot reassign Programmable Perm Switch " + wheel);
        }
        if (WheelSetting.wheelFunction0to9(wheel, sturgeon.key.wheelSettings) != wheelFunction) {
            for (int w = 0; w < 10; w++) {
                if (WheelSetting.wheelFunction0to9(w, sturgeon.key.wheelSettings) == wheelFunction) {
                    WheelSetting temp = sturgeon.key.wheelSettings[w];
                    sturgeon.key.wheelSettings[w] = sturgeon.key.wheelSettings[wheel];
                    sturgeon.key.wheelSettings[wheel] = temp;
                }
            }
        }
        if (WheelSetting.wheelFunction0to9(wheel, sturgeon.key.wheelSettings) != wheelFunction) {
            throw new RuntimeException("Should not happen: " + sturgeon.key.toString());
        }
    }
}


