import java.util.ArrayList;

public class CribAttack {

    public static void main(String[] args) {

        Key key;
        //key = new Key("T52AB", Wheels.DEFAULT_WHEEL_POSITIONS, Wheels.DEFAULT_PROGRAMMABLE_PERM_AND_XOR_WHEELS);
        //key = new Key("T52AB", "30:46:69:03:36:59:01:55:19:41:", "III:8-10:IV:5-6:3-4:I:1-2:V:II:7-9");
        //key = new Key("T52AA", Wheels.DEFAULT_WHEEL_POSITIONS, "7:IV:9:I:5:II:1:III:3:V");
        //key = new Key("T52D", "30:46:69:03:36:59:01:55:19:41:", "III:8-10:IV:5-6:3-4:I:1-2:V:II:7-9");
        //key = new Key("T52D","14:24:32:32:39:55:14:47:50:43","4-7:5-9:III:II:6-10:2-8:1-3:V:I:IV");
        key = new Key("T52D", "14:20:59:34:61:48:53:56:50:38","I:II:6-8:1-5:3-9:III:V:IV:7-10:2-4");
        //key = new Key("T52C", "02:52:18:47:03:64:08:54:50:39", "1:IV:7:I:V:3:5:III:9:II"); //very slow!
        //key = new Key("T52CA", Wheels.DEFAULT_WHEEL_POSITIONS, "7:3:III:II:1:5:I:IV:V:9");
        //key = new Key("T52E", "49:62:69:14:22:27:38:42:34:11", "1:9:I:7:II:V:III:IV:3:5");
        //key = new Key("T52E", "49:62:69:14:22:27:38:42:34:11", "V:II:3:I:1:IV:9:5:III:7"); //"III:5:9:IV:1:I:3:II:V:7"
        //key = new Key("T52EE", Wheels.DEFAULT_WHEEL_POSITIONS, "7:3:III:II:1:5:I:IV:V:9");
        Sturgeon sturgeon = new Sturgeon(key);
        sturgeon.randomize();
        sturgeon.key.ktf = false;

        int cipherLength = 108;
        int cribLength = 18;
        int depth = 1;

        byte[][] cribArray = new byte[depth][cribLength];
        byte[][] cipherArray = new byte[depth][cipherLength];
        boolean[][] moves = new boolean[10][cipherLength];

        ReferenceText.create(false, "nils");

        //Scenario.nils1();
        //Scenario.nils4();
        //Scenario.nils5();
        //Scenario.nils6();
        //Scenario.nils7();
        //Scenario.nils9();



        //solve(sturgeon.key.model, cipherArray, cribArray, null, sturgeon.key.ktf, true);


        //System.exit(0);

        ReferenceText.create(false, "germanbook.txt");

        ReferenceText.simulationWithCrib(sturgeon, cipherLength, cribLength, depth, cribArray, cipherArray, moves, true, true);
        TestPerm.findValidSwitchesAB_D(cribArray, cipherArray, true);
        //System.exit(0);

        sturgeon.key.print("Encryption Key");
        boolean testDecryption = sturgeon.testDecryption(cribArray, cipherArray);
        System.out.printf("Testing decryption: %s\n", testDecryption ? "OK" : "Failed");

        //sturgeon.randomize();
        //Scenario.swedishFilmScenario(cipherLength, depth, cribArray, cipherArray);
        //Scenario.andresPlaintextCiphertextStepwise(cribArray, cipherArray);


        //Scenario.jfT52ca4(cribArray, cipherArray);// //"3,V,1,I,II,IV,5,III,7,9"
        //solve(sturgeon.key.model, cipherArray, cribArray, "3,V,1,I,II,IV,5,III,7,9".replaceAll(",", ":"), sturgeon.key.ktf, true);

        //Scenario.jfT52d(cribArray, cipherArray);
        //solve(sturgeon.key.model, cipherArray, cribArray, "III:1-7:V:2-5:IV:8-10:I:4-6:II:3-9", false, true);

        //String wheelSettingsString = Scenario.jfT52debug(cribArray, cipherArray);

        //solve(sturgeon.key.model, cipherArray, cribArray, sturgeon.key.getWheelSettingsString(), sturgeon.key.ktf, true);
        //solve(sturgeon.key.model, cipherArray, cribArray, null, sturgeon.key.ktf, true);
        //String wheelSettingsString = Scenario.jfT52e3(cribArray, cipherArray);
        //solve(Model.T52E, cipherArray, cribArray, wheelSettingsString, false, true);
        //timingDE(sturgeon.key.model, cipherArray, cribArray, moves, wheelSettingsString, sturgeon.key.ktf);

        //T52AB|IV:5-8:I:II:2-9:V:3-10:4-7:1-6:III|53:14:68:36:64:20:56:22:04:44
        //V/9TJBWOOZIV3Q9PGCZEPG/CJHIC//+DDJO/ZHCPCL/DBTUCG+Y4LGNZ//+9/HZSJJYUNWLC89/E8/UMSP+XQKHO8S33/VASNK3DXT/4RUU4ZBXPHZ


//        solve(Model.T52D, "YHZAMIDKQJD/OQLMWACNWQ3CBI4OMWNDVUJBJM+XBR8DVDULXZXWHS8LHBQHO9ZFOQZXX9+WIPV8GGQLL3MK//Y9JZDUB9HZJSI8K9L3FKSBGGBO",
//                "DEAR9EARTH+N89I9DO9SALUTE9THEE9WITH9MY9HAND+N8439THOUGH9REBELS9WOUND9THEE9WITH9THEIR9HORSES+S89HOOFS+++TIEIRY888",
//                "3-7:IV:2-10:5-9:V:III:1-8:I:4-6:II", true, false);
        //solve(sturgeon.key.model, cipherArray, cribArray, null, sturgeon.key.ktf, true);

        //solve(sturgeon.key.model, cipherArray, cribArray, sturgeon.key.getWheelSettingsString(), sturgeon.key.ktf, true);

        //timingDE(sturgeon.key.model, cipherArray, cribArray, moves, sturgeon.key.getWheelSettingsString(), sturgeon.key.ktf);
        specialDE(sturgeon.key.model, cipherArray, cribArray, moves, sturgeon.key.getWheelSettingsString(), sturgeon.key.ktf);

    }

    static void solve(Model model, String cipher, String plain, String wheelSettingsString, boolean ktf, boolean print) {
        byte[][] cribArray = new byte[1][];
        byte[][] cipherArray = new byte[1][];
        cipherArray[0] = Alphabet.fromStringBritish(cipher);
        cribArray[0] = Alphabet.fromStringBritish(plain);
        solve(model, cipherArray, cribArray, wheelSettingsString, ktf, print);

    }


    static void solve(final Model model, byte[][] cipherArray, byte[][] cribArray,
                      final String wheelSettingsString, final boolean ktf, final boolean print) {
        CribAttackSturgeon caStu;

        System.out.printf("%s\n%s\n%s\n%s\n", Alphabet.toString(cribArray[0]), Alphabet.toStringBritish(cipherArray[0]), wheelSettingsString == null ? "Wheel Settings Unknown" : wheelSettingsString, model);
        long startNano = System.nanoTime();
        CribAttackSturgeon.atomicCountSteps.lazySet(0L);
        CribAttackSturgeonDE.ac.lazySet(0L);

        if (model == Model.T52D || model == Model.T52E) {

            final ArrayList<String> validWheelSettings = new ArrayList<>();

            if (wheelSettingsString != null) {
                validWheelSettings.add(wheelSettingsString);
            } else if (model == Model.T52E) {
                validWheelSettings.add(Wheels.DEFAULT_PERM_AND_XOR_WHEELS);
            } else {
                for (String s : TestPerm.findValidSwitchesAB_D(cribArray, cipherArray, true)) {
                    validWheelSettings.add(s + Wheels.DEFAULT_XOR_WHEELS);
                }
            }

            Runnables r = new Runnables();
            Runnables.THREADS = 60;
            for (int t = 0; t < Runnables.THREADS; t++) {

                final int task = t;

                r.add(() -> {
                    long startNano2 = System.nanoTime();
                    int steps;
                    boolean[][] moves;


                    if (wheelSettingsString != null || ktf) {

                        // T52E, order known.
                        startNano2 = System.nanoTime();

                        final CribAttackSturgeon caStu2 = new CribAttackSturgeon(model, cribArray, cipherArray, wheelSettingsString, ktf);

                        steps = caStu2.processLength(true) - 1;
                        moves = new boolean[10][steps + 1];

                        for (int moveBitmap0 = (int) Math.pow(2, steps) - 1; moveBitmap0 >= 0; moveBitmap0--) {
                            if ((moveBitmap0 % Runnables.THREADS) != task) {
                                continue;
                            }
                            if ((moveBitmap0 % 50) == 1) {
                                System.out.print("\n");
                            }
                            if ((cribArray.length == 1)) {
                                System.out.print("#\n");
                            }

                            applyMoveBitmap(moveBitmap0, 0, caStu2.orderWheels, ktf, moves, steps, steps, -1);

                            for (int moveBitmap1 = (int) Math.pow(2, steps) - 1; moveBitmap1 >= 0; moveBitmap1--) {
                                if (applyMoveBitmap(moveBitmap1, 1, caStu2.orderWheels, ktf, moves, steps, steps, 0)) {
                                    caStu2.cribSolveFunctionsKnownDE(moves, print);
                                    if ((cribArray.length == 1)) {
                                        System.out.print(".");
                                    }
                                }
                            }
                        }
                        System.out.printf("\nLegacy Elapsed %s - Steps: %,d %d\n", Utils.elapsedString(Utils.elapsedSec(startNano2)),
                                CribAttackSturgeon.atomicCountSteps.longValue(), CribAttackSturgeon.countMatchAlgo);
                    } else {

                        for (String wh : validWheelSettings) {
                            final CribAttackSturgeonDE caStu1 = new CribAttackSturgeonDE(model, cribArray, cipherArray, wh);

                            steps = caStu1.processLength() - 1;
                            moves = new boolean[10][steps + 1];

                            for (int moveBitmap0 = (int) Math.pow(2, steps) - 1; moveBitmap0 >= 0; moveBitmap0--) {
                                if ((moveBitmap0 % Runnables.THREADS) != task) {
                                    continue;
                                }
                                if ((moveBitmap0 % 50) == 1) {
                                    System.out.print("\n");
                                }
                                if (cribArray.length == 1 || model == Model.T52E) {
                                    System.out.printf("[%d]", task);
                                } else {
                                    System.out.print("*");
                                }

                                applyMoveBitmap(moveBitmap0, 0, caStu1.orderWheels, ktf, moves, steps, steps, -1);
                                caStu1.cribSolveFunctionsUnknownDE(moves);

                                //caStu1.cribSolveFunctionsKnownDE(moves);

                            }
                        }
                        System.out.printf("\nNew Elapsed %s - Steps: %,d %d\n", Utils.elapsedString(Utils.elapsedSec(startNano2)), CribAttackSturgeonDE.ac.longValue(), CribAttackSturgeonDE.countMatchAlgo);
                    }
                });


            }
            r.runAll();

        } else if (wheelSettingsString != null) {
            caStu = new CribAttackSturgeon(model, cribArray, cipherArray, wheelSettingsString);
            caStu.cribSolveFunctionsKnownABandCAandC(print);
        } else if (model == Model.T52AB) {


            ArrayList<String> all = TestPerm.findValidSwitchesAB_D(cribArray, cipherArray, false);
            Runnables r = new Runnables();
            Runnables.THREADS = 60;

            for (int t = 0; t < Runnables.THREADS; t++) {
                final int task = t;
                r.add(() -> {
                    for (int index = task; index < all.size(); index += Runnables.THREADS) {
                        new CribAttackSturgeon(model, cribArray, cipherArray, CribAttackSturgeon.OPTIMAL_AB_XOR_ORDER + all.get(index)).cribSolveABandCAandC(false);

                        if (task == 0) {
                            System.out.print(".");
                        }
                    }
                });
            }

            r.runAll();


        } else { //C, CA. EE, AA - generic

            caStu = new CribAttackSturgeon(model, cribArray, cipherArray);
            caStu.cribSolveABandCAandC(print);

        }
        if (print) {
            double elapsedSec = Utils.elapsedSec(startNano);
            System.out.printf("\nElapsed %s - Steps: %,d\n", Utils.elapsedString(elapsedSec), CribAttackSturgeon.atomicCountSteps.longValue());
        }
    }

    private static void timingDE(final Model model, byte[][] cipherArray, byte[][] cribArray, final boolean[][] moves,
                                 final String wheelSettingsString, final boolean ktf) {
        CribAttackSturgeon caStu;

        long startNano = System.nanoTime();
        double allSettingsSec;
        long skipped;

        if (model != Model.T52D && model != Model.T52E) {
            throw new RuntimeException("Timing tests not supported for model " + model);
        }


        caStu = new CribAttackSturgeon(model, cribArray, cipherArray, wheelSettingsString, ktf);

        System.out.print("\n\nMoves of two wheels FULLY KNOWN, wheels settings FULLY KNOWN\n");
        caStu.cribSolveFunctionsKnownDE(moves, true);

        allSettingsSec = printTiming(true, caStu, 0, startNano, 0, 0);

        if (caStu.key.model == Model.T52D) {
            System.out.print("\n\nMoves of two wheels FULLY KNOWN, wheels settings PARTIALLY KNOWn\n");
            startNano = System.nanoTime();
            CribAttackSturgeon.resetCribSearchCounters();

            caStu.cribSolveFunctionsPartitionKnownD(moves, true);

            printTiming(false, caStu, 0, startNano, 0, allSettingsSec);
        }

        System.out.print("\n\nMoves of two wheels PARTIALLY KNOWN, wheels settings FULLY KNOWN\n");

        int steps;

        skipped = 0;
        steps = (cipherArray.length == 1) ? 5 : caStu.processLength(true) - 1;
        startNano = System.nanoTime();
        CribAttackSturgeon.resetCribSearchCounters();

        for (int moveBitmap0 = 0; moveBitmap0 < Math.pow(2, steps); moveBitmap0++) {
            applyMoveBitmap(moveBitmap0, 0, caStu.orderWheels, caStu.key.ktf, moves, steps, caStu.processLength(true) - 1, -1);
            for (int moveBitmap1 = 0; moveBitmap1 < Math.pow(2, steps); moveBitmap1++) {
                if (applyMoveBitmap(moveBitmap1, 1, caStu.orderWheels, caStu.key.ktf, moves, steps, caStu.processLength(true) - 1, 0)) {
                    caStu.cribSolveFunctionsKnownDE(moves, true);
                } else {
                    skipped++;
                }
                if (caStu.key.model == Model.T52E && cipherArray.length == 1) {
                    System.out.print(".");
                }
            }
            if (caStu.key.model == Model.T52E && cipherArray.length == 1) {
                System.out.print("\n");
            }
        }

        allSettingsSec = printTiming(true, caStu, steps, startNano, skipped, 0);

        if (caStu.key.model == Model.T52D) {

            System.out.print("\n\nMoves of two wheels PARTIALLY KNOWN, wheels settings PARTIALLY KNOWN\n");

            CribAttackSturgeon.resetCribSearchCounters();
            startNano = System.nanoTime();
            skipped = 0;
            steps = cipherArray.length == 1 ? 5 : caStu.processLength(false) - 1;

            for (int moveBitmap0 = 0; moveBitmap0 < Math.pow(2, steps); moveBitmap0++) {
                applyMoveBitmap(moveBitmap0, 0, caStu.orderWheels, caStu.key.ktf, moves, steps, caStu.processLength(false) - 1, -1);
                for (int moveBitmap1 = 0; moveBitmap1 < Math.pow(2, steps); moveBitmap1++) {
                    if (applyMoveBitmap(moveBitmap1, 1, caStu.orderWheels, caStu.key.ktf, moves, steps, caStu.processLength(false) - 1, 0)) {
                        caStu.cribSolveFunctionsPartitionKnownD(moves, true);
                    } else {
                        skipped++;
                    }
                }
            }

            printTiming(false, caStu, steps, startNano, skipped, allSettingsSec);
        }


    }


    private static void specialDE(final Model model, byte[][] cipherArray, byte[][] cribArray, final boolean[][] moves,
                                 final String wheelSettingsString, final boolean ktf) {
        CribAttackSturgeon caStu;

        long startNano = System.nanoTime();
        double allSettingsSec;
        long skipped;

        if (model != Model.T52D && model != Model.T52E) {
            throw new RuntimeException("Timing tests not supported for model " + model);
        }


        caStu = new CribAttackSturgeon(model, cribArray, cipherArray, wheelSettingsString, ktf);
        caStu.randomizeOrder();


        System.out.print("\n\nMoves of two wheels PARTIALLY KNOWN, wheels settings FULLY KNOWN\n");

        int steps;

        skipped = 0;

        int good = 0;
        int processLength = 10;
        startNano = System.nanoTime();
        CribAttackSturgeon.resetCribSearchCounters();

        //good += caStu.cribSolveFunctionsKnownDESpecial(moves, processLength, false) ? 1 : 0;

        int tested = 0;
        int potential = 0;
        for (int moveBitmap0 = 0; moveBitmap0 < Math.pow(2, processLength - 1); moveBitmap0++) {
            applyMoveBitmap(moveBitmap0, 0, caStu.orderWheels, caStu.key.ktf, moves, processLength - 1, processLength - 1, -1);
            for (int moveBitmap1 = 0; moveBitmap1 < Math.pow(2, processLength - 1); moveBitmap1++) {
                potential++;
                if (applyMoveBitmap(moveBitmap1, 1, caStu.orderWheels, caStu.key.ktf, moves, processLength - 1, processLength - 1, 0)) {
                    tested++;
                    boolean success = caStu.cribSolveFunctionsKnownDESpecial(moves, processLength, false);
                    good += success ? 1 : 0;
                } else {
                    skipped++;
                }
            }
            System.out.printf("%d / %d - %d\n", tested, potential, good);

        }

        System.out.println(good);
        allSettingsSec = printTiming(true, caStu, processLength - 1, startNano, skipped, 0);




    }


    private static double printTiming(boolean simple, CribAttackSturgeon caStu, int steps, long startNano, long skipped, double allSettingsRefSec) {
        final int numberOfSwitchTypes = caStu.numberOfProgrammableSwitchTypes();
        double elapsedSec = Utils.elapsedSec(startNano);
        double allSettingsSec;
        double oneSettingsSec;
        if (simple) {
            oneSettingsSec = elapsedSec * countMoveOptions(caStu.orderWheels, caStu.key.ktf, caStu.processLength - steps);
            allSettingsSec = oneSettingsSec * numberOfSwitchTypes * Utils.FACTORIAL_10;
        } else {
            allSettingsSec = elapsedSec * countMoveOptions(caStu.orderWheels, caStu.key.ktf, caStu.processLength - steps) * numberOfSwitchTypes * Partition10.PARTITIONS.length;
            oneSettingsSec = 0.0;
        }

        System.out.printf("%s: Round with %2d/%2d unknown moves: %s, Total for all: %s (%d switch options), %s Contradicting moves: %,d (%,d valid), Move options tested: %,d (%,d skipped), Steps: %,d\n\n",
                simple ? "Simple " : "Complex",
                steps,
                caStu.processLength,
                Utils.elapsedString(elapsedSec),
                Utils.elapsedString(allSettingsSec),
                numberOfSwitchTypes,
                simple ? String.format("One setting: %s,", Utils.elapsedString(oneSettingsSec)) :
                        String.format("Factor:    %5d,         ", (int) (allSettingsRefSec / allSettingsSec)),

                CribAttackSturgeon.contradictingForcedMoves,
                CribAttackSturgeon.nonContradictingForcedMoves,
                CribAttackSturgeon.visits[0],
                skipped,
                CribAttackSturgeon.atomicCountSteps.longValue()
        );
        for (int x = 0; x < 10; x++) {
            if (CribAttackSturgeon.contradictingForcedMovesArray[x] > 0) {
                System.out.printf("[%d: %,d P(%,d - %,d) M(%,d - %,d)] ", x, CribAttackSturgeon.visits[x],
                        CribAttackSturgeon.contradictingPatternsArray[x],
                        CribAttackSturgeon.nonContradictingPatternsArray[x],
                        CribAttackSturgeon.contradictingForcedMovesArray[x],
                        CribAttackSturgeon.nonContradictingForcedMovesArray[x]);
            } else {
                System.out.printf("[%d: %,d P(%,d - %,d)] ", x, CribAttackSturgeon.visits[x],
                        CribAttackSturgeon.contradictingPatternsArray[x],
                        CribAttackSturgeon.nonContradictingPatternsArray[x]);
            }
        }
        System.out.println();
        return allSettingsSec;
    }

    private static double countMoveOptions(int[] wheels, boolean ktf, int processLength) {
        return Math.pow(moveOptionsPerStep(wheels[0], wheels[1], ktf), processLength - 1);
    }

    private static int moveOptionsPerStep(int w, int otherW, boolean ktf) {
        if (!ktf && ((w == Wheels.K && otherW == Wheels.J) || (w == Wheels.J && otherW == Wheels.K))) {
            return 4;
        }
        return 3;
    }

    static boolean applyMoveBitmap(int moveBitmap, int wi, int[] wheels, boolean ktf, boolean[][] moves, int STEPS, int totalMoves, int otherWi) {
        int pp0 = moveBitmap;
        for (int i = 0; i < STEPS; i++) {
            moves[wheels[wi]][i] = (pp0 & 0x1) == 0x1;
            pp0 >>= 1;
        }
        if (!ktf && wheels[wi] <= Wheels.D) {
            for (int w = 0; w <= Wheels.D; w++) {
                if (w == wheels[wi]) {
                    continue;
                }
                System.arraycopy(moves[wheels[wi]], 0, moves[w], 0, totalMoves);
            }
        }

        if (otherWi != -1) {
            if (moveOptionsPerStep(wheels[wi], wheels[otherWi], ktf) == 4) {
                return true;
            }
            for (int p = 0; p < totalMoves; p++) {
                if (!moves[wheels[wi]][p] && !moves[wheels[otherWi]][p]) {
                    return false;
                }
            }
        }
        return true;
    }

}
