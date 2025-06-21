import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class COAB {

    static void processBestKeys(Eval eval2, byte[][] cipherArray, BestKeys bestKeys, Sturgeon simulationSturgeon, long startTime, int task) {


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
                    Thread.sleep(task * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            WheelFunctionSet remaining = result.wheelFunctionSet.remaining();
            int[][] perms = Perms.PERMS[remaining.size()];
            Sturgeon ts = new Sturgeon(result.key);
//            System.out.printf("Testing %s %s %s %f\n", ts.key.getPermTypeString(), ts.key.getWheelSettingsString(WheelFunctionSet.SELECTION_XOR_ALL),
//                    ts.key.getWheelPositionsString(WheelFunctionSet.SELECTION_XOR_ALL), result.score);
            WheelSetting[] ws = ts.key.wheelSettings;
            ArrayList<WheelSetting> wheelSettings = new ArrayList<>();
            for (int w = 0; w < 10; w++) {
                if (remaining.containsWheel(w, ws)) {
                    wheelSettings.add(ws[w]);
                }
            }
            for (int index = 0; index < perms.length; index++) {
                int[] perm = perms[index % perms.length];
                int count = 0;
                for (int w = 0; w < 10; w++) {
                    if (remaining.containsWheel(w, ws)) {
                        ws[w] = wheelSettings.get(perm[count++]);
                    }
                }
                ts.computeMappings();
                CO.solve(eval2, ts, simulationSturgeon, cipherArray, remaining, 200, startTime, index, result.score);
            }
        }
    }

    private static void createBestKeys(Eval eval1, byte[][] cipherArray, BestKeys bestKeys, boolean defaultPerm,
                                       double thresholdChangeOfPermType) {
        final Sturgeon ts = new Sturgeon(Model.T52AB);
        final WheelSetting[] ws = ts.key.wheelSettings;
        final int[] wp = ts.key.wheelPositions;
        ts.computeClassFreqRef(ReferenceText.languageFreq, WheelFunctionSet.SELECTION_XOR_ALL);

        while (true) {
            if (defaultPerm) {
                ts.randomizeOrder();
                for (int w = 0; w < 10; w++) {
                    if (ws[w].getFunction() != WheelSetting.WheelFunction.XOR) {
                        continue;
                    }
                    wp[w] = CommonRandom.r.nextInt(Wheels.WHEEL_SIZES[w]);
                }
            } else {
                ts.randomize();
            }
            ts.computeClassFreqRef(ReferenceText.languageFreq, WheelFunctionSet.SELECTION_XOR_ALL);
            double bestScoreForCycle = CO.score(eval1, ts, cipherArray, null, WheelFunctionSet.SELECTION_XOR_ALL);

            boolean improved;
            do {
                improved = false;
                for (int w = 0; w < 10; w++) {
                    if (ws[w].getFunction() != WheelSetting.WheelFunction.XOR) {
                        continue;
                    }
                    int randomShift = CommonRandom.r.nextInt(Wheels.WHEEL_SIZES[w]);
                    int bestP = wp[w];
                    for (int p = 0; p < Wheels.WHEEL_SIZES[w]; p++) {
                        wp[w] = (p + randomShift) % Wheels.WHEEL_SIZES[w];
                        double score = CO.score(eval1, ts, cipherArray, null, WheelFunctionSet.SELECTION_XOR_ALL);
                        if (score > bestScoreForCycle) {
                            bestScoreForCycle = score;
                            bestP = wp[w];
                            improved = true;
                            bestKeys.push(score, ts.key, WheelFunctionSet.SELECTION_XOR_ALL);
                        }
                    }
                    wp[w] = bestP;
                }
                if (!improved) {
                    int randomShift = CommonRandom.r.nextInt(10);
                    outerloop:
                    for (int w1_ = 0; w1_ < 10; w1_++) {
                        int w1 = (w1_ + randomShift) % 10;
                        for (int w2_ = w1_ + 1; w2_ < 10; w2_++) {
                            int w2 = (w2_ + randomShift) % 10;
                            if (w1 == w2) {
                                continue;
                            }
                            if (ws[w1].getFunction() != WheelSetting.WheelFunction.XOR && ws[w2].getFunction() != WheelSetting.WheelFunction.XOR) {
                                continue;
                            }
                            if (ws[w1].getFunction() == WheelSetting.WheelFunction.XOR && ws[w2].getFunction() == WheelSetting.WheelFunction.XOR) {
                                CO.swapWheelsAndComputeMappings(ts, w1, w2);
                                double score = CO.score(eval1, ts, cipherArray, null, WheelFunctionSet.SELECTION_XOR_ALL);
                                if (score > bestScoreForCycle) {
                                    bestScoreForCycle = score;
                                    improved = true;
                                    bestKeys.push(score, ts.key, WheelFunctionSet.SELECTION_XOR_ALL);
                                    break outerloop;
                                } else {
                                    CO.swapWheelsAndComputeMappings(ts, w1, w2);
                                }
                            } else {
                                CO.swapWheelsAndComputeMappings(ts, w1, w2);
                                int w = ws[w1].getFunction() == WheelSetting.WheelFunction.XOR ? w1 : w2;
                                int origP = wp[w];
                                int bestP = origP;
                                double scoreBestP = CO.score(eval1, ts, cipherArray, null, WheelFunctionSet.SELECTION_XOR_ALL);
                                for (int p = 0; p < Wheels.WHEEL_SIZES[w]; p++) {
                                    wp[w] = p;
                                    double score = CO.score(eval1, ts, cipherArray, null, WheelFunctionSet.SELECTION_XOR_ALL);
                                    if (score > scoreBestP) {
                                        scoreBestP = score;
                                        bestP = p;
                                    }
                                }
                                if (scoreBestP > bestScoreForCycle) {
                                    bestScoreForCycle = scoreBestP;
                                    wp[w] = bestP;
                                    improved = true;
                                    bestKeys.push(scoreBestP, ts.key, WheelFunctionSet.SELECTION_XOR_ALL);
                                    break outerloop;
                                } else {
                                    wp[w] = origP;
                                    CO.swapWheelsAndComputeMappings(ts, w1, w2);
                                }
                            }
                        }
                    }
                }

                if (!defaultPerm && !improved && bestScoreForCycle > thresholdChangeOfPermType) {

                    WheelSetting[] keepPermWheelSettings = copyOfProgrammableWheelSettings(ts);

                    for (WheelSetting[] permWheelSettings : ProgrammablePermutations.validSettings()) {
                        updateProgrammableWheelSettingsComputeMappingsAndClassFreqsRef(ts, permWheelSettings, WheelFunctionSet.SELECTION_XOR_ALL);
                        double score = CO.score(eval1, ts, cipherArray, null, WheelFunctionSet.SELECTION_XOR_ALL);
                        if (score > bestScoreForCycle) {
                            bestScoreForCycle = score;
                            improved = true;
                            bestKeys.push(score, ts.key, WheelFunctionSet.SELECTION_XOR_ALL);
                            break;
                        }
                    }
                    if (!improved) {
                        updateProgrammableWheelSettingsComputeMappingsAndClassFreqsRef(ts, keepPermWheelSettings, WheelFunctionSet.SELECTION_XOR_ALL);
                    }
                }
            } while (improved);
            bestKeys.push(CO.score(eval1, ts, cipherArray, null, WheelFunctionSet.SELECTION_XOR_ALL), ts.key, WheelFunctionSet.SELECTION_XOR_ALL);

        }
    }

    static void solve(Eval eval1, Eval eval2, final Sturgeon simulationSturgeon, final byte[][] cipherArray,
                      final boolean defaultPerm,
                      double minScore,
                      final double thresholdChangeOfPermType) {

        final BestKeys bestKeys = new BestKeys(1000, minScore, false);

        final long startTime = System.currentTimeMillis();
        if (simulationSturgeon != null) {
            simulationSturgeon.computeClassFreqRef(ReferenceText.languageFreq, WheelFunctionSet.SELECTION_XOR_ALL);
            simulationSturgeon.key.score = CO.score(eval1, simulationSturgeon, cipherArray, null, WheelFunctionSet.SELECTION_XOR_ALL);
        }

        Runnables runnables = new Runnables();
        for (int t = 0; t < Runnables.THREADS / 2; t++) {
            runnables.add(() -> createBestKeys(eval1, cipherArray, bestKeys, defaultPerm, thresholdChangeOfPermType));
        }
        for (int t = 0; t < Runnables.THREADS / 2; t++) {
            final int task = t;
            runnables.add(() -> processBestKeys(eval2, cipherArray, bestKeys, simulationSturgeon, startTime, task));
        }

        runnables.runAll();
    }

    private static void updateProgrammableWheelSettingsComputeMappingsAndClassFreqsRef(Sturgeon taskSturgeon, WheelSetting[] permWheelSettings, WheelFunctionSet wheelFunctionSet) {
        int count = 0;
        for (int w = 0; w < 10; w++) {
            if (taskSturgeon.key.wheelSettings[w].getFunction() == WheelSetting.WheelFunction.PROGRAMMABLE_PERM_SWITCH) {
                taskSturgeon.key.wheelSettings[w] = permWheelSettings[count++];
            }
        }
        taskSturgeon.computeMappings();
        if (wheelFunctionSet != null) {
            taskSturgeon.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet);
        }
    }

    private static WheelSetting[] copyOfProgrammableWheelSettings(Sturgeon sturgeon) {
        WheelSetting[] keepPermWheelSettings = new WheelSetting[5];
        int count = 0;
        for (int w = 0; w < 10; w++) {
            if (sturgeon.key.wheelSettings[w].getFunction() == WheelSetting.WheelFunction.PROGRAMMABLE_PERM_SWITCH) {
                keepPermWheelSettings[count++] = sturgeon.key.wheelSettings[w];
            }
        }
        return keepPermWheelSettings;
    }

    static void bestT52ABSwitchType(Eval eval, Sturgeon sturgeon, byte[][] cipherArray, int top) {

        String origPermString = sturgeon.key.getSwitchWheelSettingsString();
        byte[] plain = new byte[cipherArray[0].length];

        Map<Integer, Double> mapScores = new HashMap<>();
        Map<Integer, String> mapDesc = new HashMap<>();
        int type = 0;

        int[][] perm5 = Perms.PERMS[5];
        if (eval == Eval.CLASS_IC || eval == Eval.CLASS) {

            for (WheelSetting[] ws : ProgrammablePermutations.validSettings()) {
                if (type % 10 == 0) {
                    System.out.print(".");
                }

                boolean original = false;
                for (int index = 0; index < perm5.length; index++) {
                    int[] perm = perm5[index % perm5.length];
                    int count = 0;
                    for (int w = 0; w < 10; w++) {
                        if (sturgeon.key.wheelSettings[w].getFunction() != WheelSetting.WheelFunction.XOR) {
                            sturgeon.key.wheelSettings[w] = ws[perm[count++]];
                        }
                    }
                    if (sturgeon.key.getSwitchWheelSettingsString().equals(origPermString)) {
                        original = true;
                        break;
                    }
                }

                int count = 0;
                for (int w = 0; w < 10; w++) {
                    if (sturgeon.key.wheelSettings[w].getFunction() != WheelSetting.WheelFunction.XOR) {
                        sturgeon.key.wheelSettings[w] = ws[count++];
                    }
                }
                sturgeon.computeMappings();
                sturgeon.computeClassFreqRef(ReferenceText.languageFreq, WheelFunctionSet.SELECTION_XOR_ALL);
                double score = CO.score(eval, sturgeon, cipherArray, null, WheelFunctionSet.SELECTION_XOR_ALL);
                mapScores.put(type, score);
                String permString = sturgeon.key.getSwitchWheelSettingsString();
                mapDesc.put(type, String.format("%f - type %3d - %s %s %s",
                        score,
                        type,
                        sturgeon.key.getWheelSettingsString(),
                        permString,
                        original ? "Original" : "        "
                ));
                type++;
            }
        } else {
            for (WheelSetting[] ws : ProgrammablePermutations.validSettings()) {
                if (type % 10 == 0) {
                    System.out.print(".");
                }
                for (int index = 0; index < perm5.length; index++) {
                    int[] perm = perm5[index % perm5.length];
                    int count = 0;
                    for (int w = 0; w < 10; w++) {
                        if (sturgeon.key.wheelSettings[w].getFunction() == WheelSetting.WheelFunction.PROGRAMMABLE_PERM_SWITCH) {
                            sturgeon.key.wheelSettings[w] = ws[perm[count++]];
                        }
                    }
                    boolean original = false;
                    if (sturgeon.key.getSwitchWheelSettingsString().equals(origPermString)) {
                        original = true;
                    }
                    sturgeon.computeMappings();

                    double score = CO.score(eval, sturgeon, cipherArray, plain, null);
                    mapScores.put(type * 120 + index, score);
                    mapDesc.put(type * 120 + index, String.format("%f - type %3d/%3d - %s %s %s",
                            score,
                            type,
                            index,
                            sturgeon.key.getSwitchWheelSettingsString(),
                            original ? "Original" : "        ",
                            Alphabet.toString(plain)
                    ));
                }
                type++;
            }
        }

        if (type % 10 == 0) {
            System.out.print(" computing done - now sorting\n");
        }

        ArrayList<Integer> listOfConfigs = new ArrayList<>(mapDesc.keySet());
        listOfConfigs.sort((o1, o2) -> Double.compare(mapScores.get(o2), mapScores.get(o1)));
        for (int i = 0; i < Math.min(top, listOfConfigs.size()); i++) {
            System.out.println(mapDesc.get(listOfConfigs.get(i)));
        }
    }
}
