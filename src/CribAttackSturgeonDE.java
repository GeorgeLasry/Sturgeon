import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class CribAttackSturgeonDE extends Sturgeon{

    public static AtomicLong ac = new AtomicLong(0L);
    private static double bestIc = 0.00;

    static int countMatchAlgo = 0;
    private static final int countMatchAlgoButNotFull = 0;

    static long[] visits = new long[10];
    static long[] contradictingForcedMovesArray = new long[10];
    static long[] nonContradictingForcedMovesArray = new long[10];
    static long contradictingForcedMoves;
    static long nonContradictingForcedMoves;
    static long[] contradictingPatternsArray = new long[10];
    static long[] nonContradictingPatternsArray = new long[10];

    private final long[][][] allWheelsPosPatternsSingleFunction;
    private final long[][][][] allWheelsPosPatternsAllFunctions;
    private final int[] wheelFunctions;
    private final long[][] wheelPosPatterns;
    private final boolean[][] camState;
    public int[] orderWheels;

    public int processLength;

    private final byte[][] cribArray;
    private final byte[][] cipherArray;

    private final Sturgeon decryptSturgeon;


    CribAttackSturgeonDE(Model model, byte[][] cribArray, byte[][] cipherArray, String wheelsString) {
        this(new Key(model.toString(),
                     Wheels.DEFAULT_WHEEL_POSITIONS,
                     wheelsString),
                cribArray,
                cipherArray);

    }

    private CribAttackSturgeonDE(Key key, byte[][] cribArray, byte[][] cipherArray) {
        super(key);

        this.cribArray = cribArray;
        this.cipherArray = cipherArray;
        int MAX_CRIB_LENGTH = 50;

        final int[][] validWheelBitmaps = new int[MAX_CRIB_LENGTH][];

        for (int i = 0; i < MAX_CRIB_LENGTH; i++) {
            getValidWheelBitmaps(cribArray, cipherArray, i, validWheelBitmaps, -1, -1, false);
        }

        allWheelsPosPatternsSingleFunction = CribAttackSturgeon.allWheelsPosPatternsSingleFunction(validWheelBitmaps, MAX_CRIB_LENGTH);
        allWheelsPosPatternsAllFunctions = CribAttackSturgeon.allWheelsPosPatternsAllFunctions(validWheelBitmaps, MAX_CRIB_LENGTH);
        wheelPosPatterns = new long[11][MAX_CRIB_LENGTH];
        Arrays.fill(wheelPosPatterns[0], Utils.ALL_ONES);
        camState = new boolean[10][MAX_CRIB_LENGTH];
        wheelFunctions = new int[10];
        Arrays.fill(wheelFunctions, Utils.UNDEFINED);

        orderWheels = new int[]{3, 2, 1, 0, 9, 8, 7, 6, 5, 4};

        decryptSturgeon = new Sturgeon(new Key(key.model.toString(), Wheels.DEFAULT_WHEEL_POSITIONS, key.getWheelSettingsString()));

    }

    static void resetCribSearchCounters() {
        ac.lazySet(0L);
        /*
        Arrays.fill(visits, 0);
        Arrays.fill(contradictingPatternsArray, 0);
        Arrays.fill(nonContradictingPatternsArray, 0);
        Arrays.fill(contradictingForcedMovesArray, 0);
        Arrays.fill(nonContradictingForcedMovesArray, 0);
        nonContradictingForcedMoves = contradictingForcedMoves = countMatchAlgoButNotFull = countMatchAlgo = 0;
        */
    }

    void cribSolveFunctionsKnownDE(boolean[][] moves) {

        processLength = processLength();
        Arrays.fill(wheelPosPatterns[0], Utils.ALL_ONES);
        cribSolveFunctionsKnownDE(0, moves);

    }
    void cribSolveFunctionsUnknownDE(boolean[][] moves) {

        processLength = processLength();
        Arrays.fill(wheelPosPatterns[0], Utils.ALL_ONES);
        cribSolveFunctionsUnknownDE(0, moves);

    }

    private int depth() {
        return cribArray.length;
    }

    private boolean cribSolveFunctionsKnownDE(int wi, boolean[][] moves) {

        if (key.ktf) {
            throw new RuntimeException("Not applicable to KTF");
        }

        //visits[wi]++;

        final int W = orderWheels[wi];

        final int WHEEL_SIZE = Wheels.WHEEL_SIZES[W];
        final int WHEEL_SIZE_MINUS_ONE = WHEEL_SIZE - 1;
        final long[][] WHEEL_POS_PATTERNS = allWheelsPosPatternsSingleFunction[W];
        final long[] CURRENT_WHEEL_POS_PATTERNS = wheelPosPatterns[wi];
        final long[] NEXT_WHEEL_POS_PATTERNS = wheelPosPatterns[wi + 1];
        final boolean[] CAM_STATE_W = camState[W];

        final int WHEEL_STEP_CAM_POS = Wheels.WHEEL_STEP_CAM_POS[W];
        final int[] WHEEL_INT_PATTERNS_AT_POSITIONS = Wheels.INT_PATTERNS[W];
        final boolean[] MOVES_W = moves[W];

        if (!isMoveForcedDE(wi)) {
            for (int cribPos = 0; cribPos < processLength; cribPos++) {
                MOVES_W[cribPos] = shouldMoveDE(W, camState, cribPos);
            }
        }

        boolean success = false;

        boolean validWheelStartPos;
        int wheelStartPos;
        int cribPos;
        int wheelPos;
        int wheelStepCamPos;
        long val;
        for (wheelStartPos = 0; wheelStartPos < WHEEL_SIZE; wheelStartPos++) {

            if (wi == 0 && (depth() == 1 || (depth() < 3 && key.model == Model.T52E))) {
                System.out.print("-");
            }

            ac.getAndIncrement();
            validWheelStartPos = true;
            wheelPos = wheelStartPos;
            wheelStepCamPos = wheelPos + WHEEL_STEP_CAM_POS;
            if (wheelStepCamPos >= WHEEL_SIZE) {
                wheelStepCamPos -= WHEEL_SIZE;
            }

            for (cribPos = 0; cribPos < processLength; cribPos++) {
                if ((val = CURRENT_WHEEL_POS_PATTERNS[cribPos] & WHEEL_POS_PATTERNS[cribPos][wheelPos]) == 0) {
                    validWheelStartPos = false;
                    break;
                }

                NEXT_WHEEL_POS_PATTERNS[cribPos] = val;
                CAM_STATE_W[cribPos] = WHEEL_INT_PATTERNS_AT_POSITIONS[wheelStepCamPos] > 0;

                if (MOVES_W[cribPos]) {
                    wheelPos = (wheelPos == WHEEL_SIZE_MINUS_ONE) ? 0 : wheelPos + 1;
                    wheelStepCamPos = (wheelStepCamPos == WHEEL_SIZE_MINUS_ONE) ? 0 : wheelStepCamPos + 1;
                }
            }

            if (!validWheelStartPos) {
                //contradictingPatternsArray[wi]++;
                continue;
            }
            //nonContradictingPatternsArray[wi]++;

            if (!forcedMovesValidSoFar(orderWheels, wi, moves, processLength)) {
                //contradictingForcedMovesArray[wi]++;
                //contradictingForcedMoves++;
                continue;
            }
           // nonContradictingForcedMoves++;
            //nonContradictingForcedMovesArray[wi]++;

            key.wheelPositions[W] = wheelStartPos;
            int nextWi = wi + 1;

            if (nextWi == 4) {
                int steps = processLength - 1;
                for (int moveBitmap1 = (int) Math.pow(2, steps) - 1; moveBitmap1 >= 0; moveBitmap1--) {
                    if (applyMoveBitmap(moveBitmap1, moves, steps)) {
                        success |= cribSolveFunctionsKnownDE(nextWi, moves);
                    }
                }
            } else if (nextWi < Wheels.NUM_WHEELS) {
                success |= cribSolveFunctionsKnownDE(nextWi, moves);
            } else {
                if (testDecryption(cribArray, cipherArray)) {
                    printSolution(this, moves);
                    //countMatchAlgo++;
                    success = true;
                } else {
                    //countMatchAlgoButNotFull++;
                }
            }
        }

        return success;

    }


    private boolean isWheelUsed(int[] wheels, int w, int length) {
        for (int index = 0; index < length; index++) {
            if (wheels[index] == w) {
                return true;
            }
        }
        return false;
    }

    private boolean cribSolveFunctionsUnknownDE(int wi, boolean[][] moves) {

        if (key.ktf) {
            throw new RuntimeException("Not applicable to KTF");
        }

        //visits[wi]++;

        final int W = orderWheels[wi];

        final int WHEEL_SIZE = Wheels.WHEEL_SIZES[W];
        final int WHEEL_SIZE_MINUS_ONE = WHEEL_SIZE - 1;
        final long[] CURRENT_WHEEL_POS_PATTERNS = wheelPosPatterns[wi];
        final long[] NEXT_WHEEL_POS_PATTERNS = wheelPosPatterns[wi + 1];
        final boolean[] CAM_STATE_W = camState[W];

        final int WHEEL_STEP_CAM_POS = Wheels.WHEEL_STEP_CAM_POS[W];
        final int[] WHEEL_INT_PATTERNS_AT_POSITIONS = Wheels.INT_PATTERNS[W];
        final boolean[] MOVES_W = moves[W];

        if (!isMoveForcedDE(wi)) {
            for (int cribPos = 0; cribPos < processLength; cribPos++) {
                MOVES_W[cribPos] = shouldMoveDE(W, camState, cribPos);
            }
        }

        boolean success = false;

        boolean validWheelStartPos;
        int wheelStartPos;
        int cribPos;
        int wheelPos;
        int wheelStepCamPos;
        long val;

        for (int wf = 0; wf < 10; wf++) {

            if (wi == 0 && (depth() == 1 || key.model == Model.T52E)) {
                System.out.printf("%d", wf);
            }
            if (isWheelUsed(wheelFunctions, wf, wi)) {
                continue;
            }
            final long[][] WHEEL_POS_PATTERNS = allWheelsPosPatternsAllFunctions[W][wf];

            for (wheelStartPos = 0; wheelStartPos < WHEEL_SIZE; wheelStartPos++) {

                ac.getAndIncrement();
                validWheelStartPos = true;
                wheelPos = wheelStartPos;
                wheelStepCamPos = wheelPos + WHEEL_STEP_CAM_POS;
                if (wheelStepCamPos >= WHEEL_SIZE) {
                    wheelStepCamPos -= WHEEL_SIZE;
                }

                for (cribPos = 0; cribPos < processLength; cribPos++) {
                    if ((val = CURRENT_WHEEL_POS_PATTERNS[cribPos] & WHEEL_POS_PATTERNS[cribPos][wheelPos]) == 0) {
                        validWheelStartPos = false;
                        break;
                    }

                    NEXT_WHEEL_POS_PATTERNS[cribPos] = val;
                    CAM_STATE_W[cribPos] = WHEEL_INT_PATTERNS_AT_POSITIONS[wheelStepCamPos] > 0;

                    if (MOVES_W[cribPos]) {
                        wheelPos = (wheelPos == WHEEL_SIZE_MINUS_ONE) ? 0 : wheelPos + 1;
                        wheelStepCamPos = (wheelStepCamPos == WHEEL_SIZE_MINUS_ONE) ? 0 : wheelStepCamPos + 1;
                    }
                }

                if (!validWheelStartPos) {
                    //contradictingPatternsArray[wi]++;
                    continue;
                }
                //nonContradictingPatternsArray[wi]++;

                if (!forcedMovesValidSoFar(orderWheels, wi, moves, processLength)) {
                    //contradictingForcedMovesArray[wi]++;
                    //contradictingForcedMoves++;
                    continue;
                }
                //nonContradictingForcedMoves++;
                //nonContradictingForcedMovesArray[wi]++;

                key.wheelPositions[W] = wheelStartPos;
                wheelFunctions[wi] = wf;
                int nextWi = wi + 1;

                if (nextWi == 4) {
                    int steps = processLength - 1;
                    for (int moveBitmap1 = (int) Math.pow(2, steps) - 1; moveBitmap1 >= 0; moveBitmap1--) {
                        if (applyMoveBitmap(moveBitmap1, moves, steps)) {
                            success |= cribSolveFunctionsUnknownDE(nextWi, moves);
                        }
                    }
                } else if (nextWi < Wheels.NUM_WHEELS) {
                    success |= cribSolveFunctionsUnknownDE(nextWi, moves);
                } else {
                    System.arraycopy(key.wheelPositions, 0, decryptSturgeon.key.wheelPositions, 0, 10);
                    for (int ww = 0; ww < 10; ww++) {
                        decryptSturgeon.key.wheelSettings[orderWheels[ww]] = key.wheelSettings[wheelFunctions[ww]];
                    }
                    decryptSturgeon.computeMappings();
                    if (decryptSturgeon.testDecryption(cribArray, cipherArray)) {
                        printSolution(decryptSturgeon, moves);
                        //countMatchAlgo++;
                        success = true;
                    } else {
                        //countMatchAlgoButNotFull++;
                    }
                }
            }
        }
        return success;

    }


    private static boolean applyMoveBitmap(int moveBitmap, boolean[][] moves, int totalMoves) {
        int pp0 = moveBitmap;
        for (int i = 0; i < totalMoves; i++) {
            moves[Wheels.K][i] = (pp0 & 0x1) == 0x1;
            pp0 >>= 1;
        }

        for (int p = 0; p < totalMoves; p++) {
            if (!moves[Wheels.K][p] && !moves[Wheels.A][p]) {
                return false;
            }
        }

        return true;
    }

    private void printSolution(Sturgeon decryptSturgeon, boolean[][] moves) {


        boolean good = false;
        boolean better = false;
        double ic = 0.0;
        for (byte[] cipher : cipherArray) {
            int len = cipher.length;
            byte[] plain = new byte[len];
            decryptSturgeon.encryptDecrypt(cipher, plain, cipher.length, null, false);
            ic = Stats.computeIc(len, plain);
            if (ic >= bestIc) {
                if (ic > bestIc) {
                    bestIc = ic;
                    better = true;
                }
                good = true;
                break;
            }
        }
        if (!good) {
            return;
        }

        System.out.println();
        decryptSturgeon.key.print(String.format("Match: %,6d (False: %,6d) IC: %7f Steps: %,15d %s %s", countMatchAlgo, countMatchAlgoButNotFull, ic, ac.longValue(),
                (moves != null) ? movesString(moves[orderWheels[0]], processLength) : "", (moves != null) ? movesString(moves[orderWheels[4]], processLength) : ""));

        if (!better) {
            return;
        }
        int depth = cipherArray.length;
        for (int d = 0; d < depth; d++) {
            int len = Math.max(cipherArray[d].length, cribArray[d].length);
            byte[] plain = new byte[len];
            Arrays.fill(plain, (byte) Utils.UNDEFINED);
            byte[] cipher = new byte[len];
            Arrays.fill(cipher, (byte) Utils.UNDEFINED);

            decryptSturgeon.encryptDecrypt(cipherArray[d], plain, cipherArray[d].length, null, false);

            System.out.printf("IC:   %f\n", Stats.computeIc(len, plain));
            System.out.printf("Freq: %f\n", Stats.matchStreamWithFreq(len, plain, ReferenceText.languageFreq));

            System.out.printf("In:   %s\n", Alphabet.toString(cribArray[d]));
            System.out.printf("In:   %s\n", Alphabet.toString(cipherArray[d]));
            decryptSturgeon.encryptDecrypt(cribArray[d], cipher, cribArray[d].length, null, true);

            for (int i = 0; i < cipherArray[d].length; i++) {
                if (cipherArray[d][i] != Utils.UNDEFINED) {
                    cipher[i] = cipherArray[d][i];
                }
            }
            System.out.printf("Out:  %s\n", Alphabet.toString(cipher));

            for (int i = 0; i < cribArray[d].length; i++) {
                if (cribArray[d][i] != Utils.UNDEFINED) {
                    plain[i] = cribArray[d][i];
                }
            }
            System.out.printf("Out:  %s\n", Alphabet.toString(plain));
            System.out.printf("Out:  %s\n", Alphabet.toStringBritish(plain));
            System.out.printf("Out:  %s\n", Alphabet.toStringFormatted(plain, true));
            //System.out.printf("%s\n", Alphabet.toStringFormatted(plain, false));
            System.out.print("\n");
        }
    }

    private static boolean isMoveForcedDE(int wi) {
        return wi < 5;
    }
    private boolean forcedMovesValidSoFar(int[] wheels, int lastWheelIndex, boolean[][] moves, int processLength) {
        for (int secondWheelIndex = 0; secondWheelIndex <= lastWheelIndex; secondWheelIndex++) {
            if (!isMoveForcedDE(secondWheelIndex)) {
                continue;
            }
            int secondWheel = wheels[secondWheelIndex];
            boolean[] secondWheelMoves = moves[secondWheel];
            for (int firstWheelIndex = 0; firstWheelIndex <= lastWheelIndex; firstWheelIndex++) {

                if (firstWheelIndex == secondWheelIndex) {
                    continue;
                }
                if (firstWheelIndex != lastWheelIndex && secondWheelIndex != lastWheelIndex) {
                    continue;
                }
                int firstWheel = wheels[firstWheelIndex];
                boolean[] firstWheelCamStates = camState[firstWheel];
                if (firstWheelCamForcesSecondWheelSteppingCached(key.ktf, firstWheel, false, secondWheel)) {
                    // We do not check the last entry!!!!
                    for (int cribPos = 0; cribPos < processLength - 1; cribPos++) {
                        if (!secondWheelMoves[cribPos] && !firstWheelCamStates[cribPos]) {
                            return false;
                        }
                    }
                } else if (firstWheelCamForcesSecondWheelSteppingCached(key.ktf, firstWheel, true, secondWheel)) {
                    for (int cribPos = 0; cribPos < processLength - 1; cribPos++) {
                        if (!secondWheelMoves[cribPos] && firstWheelCamStates[cribPos]) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public int processLength() {
        return Math.min(getBestProcessLengthRaw(), cribArray[0].length);
    }

    private int getBestProcessLengthRaw() {

        int depth = depth();

        return switch (key.model) {
            case T52D -> depth == 1 ? 11 : 10;
            case T52E -> 9;
            default -> -1;
        };
    }

    private static String movesString(boolean[] move, int steps) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < steps; i++) {
            if (move[i]) {
                s.append("1");
            } else {
                s.append("0");
            }
        }
        return s.toString();
    }

    private static boolean shouldMoveDE(int w, boolean[][] camState, int i) {

        switch (w) {
            case 0, 1, 2, 3 -> {
                boolean e = camState[4][i];
                boolean f = camState[5][i];
                return !f || !e;
            }
            case 4 -> {
                boolean f = camState[5][i];
                boolean g = camState[6][i];
                return !g || f;
            }
            case 5 -> {
                boolean g = camState[6][i];
                boolean h = camState[7][i];
                return h || g;
            }
            case 6 -> {
                boolean h = camState[7][i];
                boolean j = camState[8][i];
                return !j || !h;
            }
            case 7 -> {
                boolean j = camState[8][i];
                boolean k = camState[9][i];
                return !k || j;
            }
            case 8 -> {
                boolean k = camState[9][i];
                boolean a = camState[0][i];
                return k || !a;
            }
            case 9 -> {
                boolean d = camState[3][i];
                boolean e = camState[4][i];
                return e || !d;
            }
            default -> throw new RuntimeException("!!!");
        }
    }

}
