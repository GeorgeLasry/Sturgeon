import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class CribAttackSturgeon  extends Sturgeon{


    private static final String OPTIMAL_C_ORDER = "7:IV:9:I:5:II:1:III:3:V";
    private static final String OPTIMAL_CA_ORDER = "7:3:III:II:1:5:I:IV:V:9";
    public static final String OPTIMAL_E_ORDER = "III:5:9:IV:1:I:3:II:V:7";
    public static final String OPTIMAL_AB_XOR_ORDER = "III:IV:II:V:I:";


    public static AtomicLong atomicCountSteps = new AtomicLong(0L);
    private static double bestIc = 0.00;
    static int countMatchAlgo = 0;
    private static int countMatchAlgoButNotFull = 0;

    static long[] visits = new long[10];
    static long[] contradictingForcedMovesArray = new long[10];
    static long[] nonContradictingForcedMovesArray = new long[10];
    static long contradictingForcedMoves;
    static long nonContradictingForcedMoves;
    static long[] contradictingPatternsArray = new long[10];
    static long[] nonContradictingPatternsArray = new long[10];

    private final Sturgeon decryptSturgeon;

    private long[][][] allWheelsPosPatternsSingleFunction;
    private long[][][][] allWheelsPosPatternsSingleFunctionFunctionC;
    private long[][][][] allWheelsPosPatternsAllFunctions;
    private long[][][][][] allWheelsPosPatternsAllFunctionsC;
    private long[][] wheelPosPatterns;
    private long[][][] wheelPosPatternsC;
    private boolean[][] camState;
    private int[] wheelFunctions;
    private final int[] wheelsUsed;
    public int[] orderWheels;

    private boolean print;
    public int processLength;


    private final byte[][] cribArray;
    private final byte[][] cipherArray;


    private static String optimalOrderWheelsString(Model model) {
        if (model == Model.T52CA) {
            return OPTIMAL_CA_ORDER;
        } else if (model == Model.T52C) {
            return OPTIMAL_C_ORDER;
        } else if (model == Model.T52EE) {
            return OPTIMAL_E_ORDER;
        } else if (model == Model.T52AA) {
            return OPTIMAL_AB_XOR_ORDER + Wheels.DEFAULT_PERM_WHEELS;
        }
        return "";
    }

    CribAttackSturgeon(Model model, byte[][] cribArray, byte[][] cipherArray, String wheelsString, boolean ktf) {
        this(new Key(model.toString(),
                     Wheels.DEFAULT_WHEEL_POSITIONS,
                     wheelsString == null ? optimalOrderWheelsString(model) : wheelsString, ktf),
                cribArray,
                cipherArray);
        decryptSturgeon.key.ktf = key.ktf = ktf;

    }
    CribAttackSturgeon(Model model, byte[][] cribArray, byte[][] cipherArray, String wheelsString) {
        this(model, cribArray, cipherArray, wheelsString, false);
    }
    CribAttackSturgeon(Model model, byte[][] cribArray, byte[][] cipherArray) {
        this(model, cribArray, cipherArray, null);
    }

    private CribAttackSturgeon(Key key, byte[][] cribArray, byte[][] cipherArray) {
        super(key);

        this.cribArray = cribArray;
        this.cipherArray = cipherArray;
        int MAX_CRIB_LENGTH = 50;

        final int[][] validWheelBitmaps = new int[MAX_CRIB_LENGTH][];

        for (int i = 0; i < MAX_CRIB_LENGTH; i++) {
            getValidWheelBitmaps(cribArray, cipherArray, i, validWheelBitmaps, -1, -1, false);
        }

        if (key.model == Model.T52C) {
            allWheelsPosPatternsSingleFunctionFunctionC = allWheelsPosPatternsSingleFunctionFunctionC(validWheelBitmaps, MAX_CRIB_LENGTH);
            allWheelsPosPatternsAllFunctionsC = allWheelsPosPatternsAllFunctionsC(validWheelBitmaps, MAX_CRIB_LENGTH);
            wheelPosPatternsC = new long[11][2][MAX_CRIB_LENGTH];
            Arrays.fill(wheelPosPatternsC[0][0], Utils.ALL_ONES);
            Arrays.fill(wheelPosPatternsC[0][1], Utils.ALL_ONES);
        } else {
            allWheelsPosPatternsSingleFunction = allWheelsPosPatternsSingleFunction(validWheelBitmaps, MAX_CRIB_LENGTH);
            allWheelsPosPatternsAllFunctions = allWheelsPosPatternsAllFunctions(validWheelBitmaps, MAX_CRIB_LENGTH);
            wheelPosPatterns = new long[11][MAX_CRIB_LENGTH];
            Arrays.fill(wheelPosPatterns[0], Utils.ALL_ONES);
        }

        if (key.model.hasIrregularStepping()) {
            camState = new boolean[10][MAX_CRIB_LENGTH];
            wheelFunctions = new int[10];
        }
        wheelsUsed = new int[10];
        Arrays.fill(wheelsUsed, Utils.UNDEFINED);
        orderWheels = new int[10];
        if (key.model == Model.T52AB) {
            int wi = 0;
            for (String ows : OPTIMAL_AB_XOR_ORDER.split(":")) {
                for (int w = 0; w < 10; w++) {
                    if (ows.equals(key.wheelSettings[w].toString())) {
                        orderWheels[wi++] = w;
                        break;
                    }
                }
            }
            for (int w = 0; w < 10; w++) {
                if (key.wheelSettings[w].getFunction() != WheelSetting.WheelFunction.XOR) {
                    orderWheels[wi++] = w;
                }
            }

        } else if (key.model == Model.T52D || key.model == Model.T52E) {
            orderWheels = Orders.bestWheelOrderDE(key.model, key.wheelSettings, key.ktf, true);

            //orderWheels = new int[]{0,1,2,3};

        } else {
           int wi = 0;
            for (String ows : optimalOrderWheelsString(key.model).split(":")) {
                for (int w = 0; w < 10; w++) {
                    if (ows.equals(key.wheelSettings[w].toString())) {
                        orderWheels[wi++] = w;
                        break;
                    }
                }
            }
        }

        decryptSturgeon = new Sturgeon(new Key(key.model.toString(), Wheels.DEFAULT_WHEEL_POSITIONS, key.getWheelSettingsString()));
    }

    static void resetCribSearchCounters() {
        atomicCountSteps.lazySet(0L);
        Arrays.fill(visits, 0);
        Arrays.fill(contradictingPatternsArray, 0);
        Arrays.fill(nonContradictingPatternsArray, 0);
        Arrays.fill(contradictingForcedMovesArray, 0);
        Arrays.fill(nonContradictingForcedMovesArray, 0);
        nonContradictingForcedMoves = contradictingForcedMoves = countMatchAlgoButNotFull = countMatchAlgo = 0;
    }

    void cribSolveABandCAandC(final boolean print) {

        this.print = print;
        processLength = processLength(false);
        Arrays.fill(wheelsUsed, Utils.UNDEFINED);
        if (key.model == Model.T52C) {
            cribSolveC(0);
        } else {
            cribSolveABandCA(0);
        }
    }
    void cribSolveFunctionsKnownDE(boolean[][] moves, boolean print) {

        this.print = print;
        processLength = processLength(true);
        Arrays.fill(wheelPosPatterns[0], Utils.ALL_ONES);

        cribSolveFunctionsKnownDE(0, moves);

    }

    boolean cribSolveFunctionsKnownDESpecial(boolean[][] moves, int processLength, boolean print) {

        this.print = print;
        this.processLength = processLength;
        Arrays.fill(wheelPosPatterns[0], Utils.ALL_ONES);

        return cribSolveFunctionsKnownDESpecial(0, moves);

    }


    void cribSolveFunctionsPartitionKnownD(boolean[][] moves, boolean print) {

        this.print = print;
        processLength = processLength(false);
        Arrays.fill(wheelPosPatterns[0], Utils.ALL_ONES);
        Arrays.fill(wheelFunctions, Utils.UNDEFINED);

        cribSolveFunctionsPartitionKnownD(0, moves);

    }
    void cribSolveFunctionsKnownABandCAandC(boolean print) {

        this.print = print;
        processLength = processLength(true);

        if (key.model != Model.T52C) {
            cribSolveFunctionsKnownABandCA(0);
        } else {
            cribSolveFunctionsKnownC(0);
        }

    }

    private int depth() {
        return cribArray.length;
    }

    int numberOfProgrammableSwitchTypes() {
        if (key.model.hasProgrammablePermSwitches()) {
            return TestPerm.findValidSwitchesAB_D(cribArray, cipherArray, depth() > 1).size();
        }
        return 1;
    }

    private void cribSolveABandCA(final int wheelIndexInOrder) {
        visits[wheelIndexInOrder]++;

        final long[] CURRENT_WHEEL_POS_PATTERNS = wheelPosPatterns[wheelIndexInOrder];
        final int nextWheelIndexInOrder = wheelIndexInOrder + 1;
        final long[] NEXT_WHEEL_POS_PATTERNS = wheelPosPatterns[nextWheelIndexInOrder];

        int cribPos;
        int wheelPos;
        for (int w = 0; w < 10; w++) {

            if (isWheelUsed(wheelsUsed, w, wheelIndexInOrder)) {
                continue;
            }
            wheelsUsed[wheelIndexInOrder] = w;

            final int WHEEL_SIZE = Wheels.WHEEL_SIZES[w];
            final int WHEEL_SIZE_MINUS_ONE = WHEEL_SIZE - 1;
            final long[][] WHEEL_POS_PATTERNS = allWheelsPosPatternsAllFunctions[w][wheelIndexInOrder];
            for (key.wheelPositions[w] = 0; key.wheelPositions[w] < WHEEL_SIZE; key.wheelPositions[w]++) {

                if (print && wheelIndexInOrder == 0 && cribArray.length <= 2) {
                    if (key.wheelPositions[w] == 0) {
                        System.out.printf("%s = %s: ", key.wheelSettings[wheelIndexInOrder].toString(), Wheels.WHEEL_LETTERS.charAt(w));
                    }
                    System.out.printf("%d,", key.wheelPositions[w] + 1);

                    if (key.wheelPositions[w] == WHEEL_SIZE_MINUS_ONE) {
                        System.out.println();
                    }
                }


                for (cribPos = 0, wheelPos = key.wheelPositions[w];
                     cribPos < processLength && ((NEXT_WHEEL_POS_PATTERNS[cribPos] = CURRENT_WHEEL_POS_PATTERNS[cribPos] & WHEEL_POS_PATTERNS[cribPos][wheelPos]) != 0);
                     cribPos++, wheelPos = (wheelPos == WHEEL_SIZE_MINUS_ONE) ? 0 : wheelPos + 1);

                if (cribPos < processLength) {
                    continue;
                }

                if (nextWheelIndexInOrder < Wheels.NUM_WHEELS) {
                    cribSolveABandCA(nextWheelIndexInOrder);
                } else {


                    for (int index = 0; index < 10; index++) {
                        w = wheelsUsed[index];
                        decryptSturgeon.key.wheelSettings[w] = key.wheelSettings[index];
                        decryptSturgeon.key.wheelPositions[w] = key.wheelPositions[w];
                    }

                    decryptSturgeon.computeMappings();
                    if (decryptSturgeon.testDecryption(cribArray, cipherArray)) {
                        printSolution(decryptSturgeon, null);
                        countMatchAlgo++;
                    } else {
                        countMatchAlgoButNotFull++;
                    }


                }
            }

            atomicCountSteps.getAndAdd(WHEEL_SIZE);

        }
    }
    private void cribSolveC(final int wheelIndexInOrder) {
        visits[wheelIndexInOrder]++;

        final long[][] CURRENT_WHEEL_POS_PATTERNS = wheelPosPatternsC[wheelIndexInOrder];
        final int nextWheelIndexInOrder = wheelIndexInOrder + 1;
        final long[][] NEXT_WHEEL_POS_PATTERNS = wheelPosPatternsC[nextWheelIndexInOrder];

        boolean validWheelStartPos;
        int wheelStartPos;
        int cribPos;
        int wheelPos;
        long val0, val1;
        for (int w = 0; w < 10; w++) {

            if (isWheelUsed(wheelsUsed, w, wheelIndexInOrder)) {
                continue;
            }

            final int WHEEL_SIZE = Wheels.WHEEL_SIZES[w];
            final int WHEEL_SIZE_MINUS_ONE = WHEEL_SIZE - 1;
            final long[][][] WHEEL_POS_PATTERNS = allWheelsPosPatternsAllFunctionsC[w][wheelIndexInOrder];
            for (wheelStartPos = 0; wheelStartPos < WHEEL_SIZE; wheelStartPos++) {

                if (wheelIndexInOrder == 0 && cribArray.length <= 2 && print) {
                    if (wheelStartPos == 0) {
                        System.out.printf("%d: ", w);
                    }
                    System.out.printf("%d,", wheelStartPos);

                    if (wheelStartPos == WHEEL_SIZE_MINUS_ONE) {
                        System.out.println();
                    }
                }

                validWheelStartPos = true;
                for (cribPos = 0, wheelPos = wheelStartPos;
                     cribPos < processLength;
                     cribPos++, wheelPos = (wheelPos == WHEEL_SIZE_MINUS_ONE) ? 0 : wheelPos + 1) {

                    val0 = CURRENT_WHEEL_POS_PATTERNS[0][cribPos] & WHEEL_POS_PATTERNS[cribPos][0][wheelPos];
                    val1 = CURRENT_WHEEL_POS_PATTERNS[1][cribPos] & WHEEL_POS_PATTERNS[cribPos][1][wheelPos];
                    if (val0 == 0 && val1 == 0) {
                        validWheelStartPos = false;
                        break;
                    }
                    NEXT_WHEEL_POS_PATTERNS[0][cribPos] = val0;
                    NEXT_WHEEL_POS_PATTERNS[1][cribPos] = val1;
                }

                if (!validWheelStartPos) {
                    continue;
                }
                wheelsUsed[wheelIndexInOrder] = w;
                key.wheelPositions[w] = wheelStartPos;

                if (nextWheelIndexInOrder < Wheels.NUM_WHEELS) {
                    cribSolveC(nextWheelIndexInOrder);
                } else {

                    for (int ww = 0; ww < 10; ww++) {
                        decryptSturgeon.key.wheelSettings[wheelsUsed[ww]] = key.wheelSettings[ww];
                        decryptSturgeon.key.wheelPositions[wheelsUsed[ww]] = key.wheelPositions[wheelsUsed[ww]];
                    }
                    decryptSturgeon.computeMappings();
                    if (decryptSturgeon.testDecryption(cribArray, cipherArray)) {
                        printSolution(decryptSturgeon, null);
                        countMatchAlgo++;
                    } else {
                        countMatchAlgoButNotFull++;
                    }
                }
            }
            atomicCountSteps.getAndAdd(WHEEL_SIZE);
        }
    }
    private boolean cribSolveFunctionsPartitionKnownD(final int wi, final boolean[][] moves) {

        if (key.ktf && cribArray.length > 1) {
            throw new RuntimeException("KTF and depth not compatible");
        }

        visits[wi]++;

        final int W = orderWheels[wi];

        final int WHEEL_SIZE = Wheels.WHEEL_SIZES[W];
        final int WHEEL_SIZE_MINUS_ONE = WHEEL_SIZE - 1;
        final long[] CURRENT_WHEEL_POS_PATTERNS = wheelPosPatterns[wi];
        final long[] NEXT_WHEEL_POS_PATTERNS = wheelPosPatterns[wi + 1];
        final boolean[] CAM_STATE_W = camState[W];

        final int WHEEL_STEP_CAM_POS = Wheels.WHEEL_STEP_CAM_POS[W];
        final int[] WHEEL_INT_PATTERNS_AT_POSITIONS = Wheels.INT_PATTERNS[W];
        final boolean[] MOVES_W = moves[W];

        if (!isMoveForcedDE(key.ktf, orderWheels, wi)) {
            if (key.ktf) {
                MOVES_W[0] = shouldMoveDE(true, (byte) 0, orderWheels, wi, camState, 0);
                for (int cribPos = 1; cribPos < processLength; cribPos++) {
                    MOVES_W[cribPos] = shouldMoveDE(true, cribArray[0][cribPos - 1], orderWheels, wi, camState, cribPos);
                }
            } else {
                for (int cribPos = 0; cribPos < processLength; cribPos++) {
                    MOVES_W[cribPos] = shouldMoveDE(false, (byte) 0, orderWheels, wi, camState, cribPos);
                }
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
            if (key.wheelSettings[W].getFunction() != key.wheelSettings[wf].getFunction()) {
                continue;
            }

            if (isWheelUsed(wheelFunctions, wf, wi)) {
                continue;
            }
            final long[][] WHEEL_POS_PATTERNS = allWheelsPosPatternsAllFunctions[W][wf];


            for (wheelStartPos = 0; wheelStartPos < WHEEL_SIZE; wheelStartPos++) {
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
                    contradictingPatternsArray[wi]++;
                    continue;
                }
                nonContradictingPatternsArray[wi]++;

                if (!forcedMovesValidSoFar(orderWheels, wi, moves, processLength)) {
                    contradictingForcedMovesArray[wi]++;
                    contradictingForcedMoves++;
                    continue;
                }
                nonContradictingForcedMoves++;
                nonContradictingForcedMovesArray[wi]++;

                key.wheelPositions[W] = wheelStartPos;
                wheelFunctions[wi] = wf;
                int nextWi = wi + 1;
                if (nextWi < Wheels.NUM_WHEELS) {
                    success |= cribSolveFunctionsPartitionKnownD(nextWi, moves);
                } else {

                    System.arraycopy(key.wheelPositions, 0, decryptSturgeon.key.wheelPositions, 0, 10);
                    for (int ww = 0; ww < 10; ww++) {
                        decryptSturgeon.key.wheelSettings[orderWheels[ww]] = key.wheelSettings[wheelFunctions[ww]];
                    }
                    decryptSturgeon.computeMappings();
                    if (decryptSturgeon.testDecryption(cribArray, cipherArray)) {
                        printSolution(decryptSturgeon, moves);
                        countMatchAlgo++;
                        success = true;
                    } else {
                        countMatchAlgoButNotFull++;
                    }

                }
            }
            atomicCountSteps.getAndAdd(WHEEL_SIZE);
        }

        return success;

    }
    private boolean cribSolveFunctionsKnownDE(int wi, boolean[][] moves) {

        if (key.ktf && cribArray.length > 1) {
            throw new RuntimeException("KTF and depth not compatible");
        }

        visits[wi]++;

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

        if (!isMoveForcedDE(key.ktf, orderWheels, wi)) {
            if (key.ktf) {
                MOVES_W[0] = shouldMoveDE(true, (byte) 0, orderWheels, wi, camState, 0);
                for (int cribPos = 1; cribPos < processLength; cribPos++) {
                    MOVES_W[cribPos] = shouldMoveDE(true, cribArray[0][cribPos - 1], orderWheels, wi, camState, cribPos);
                }
            } else {
                for (int cribPos = 0; cribPos < processLength; cribPos++) {
                    MOVES_W[cribPos] = shouldMoveDE(false, (byte) 0, orderWheels, wi, camState, cribPos);
                }
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
            if (wi == 0) {
                //System.out.print(".");
            }
//            if (wheelStartPos == 5 && wi == 0) {
//                return false;
//            }
            atomicCountSteps.getAndIncrement();
            validWheelStartPos = true;
            wheelPos = wheelStartPos;
            wheelStepCamPos = wheelPos + WHEEL_STEP_CAM_POS;
            if (wheelStepCamPos >= WHEEL_SIZE) {
                wheelStepCamPos -= WHEEL_SIZE;
            }

            for (cribPos = 0; cribPos < processLength; cribPos++) {
                if (wi == 0) {
                    //System.out.print("-");
                }
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
                contradictingPatternsArray[wi]++;
                continue;
            }
            nonContradictingPatternsArray[wi]++;

            if (!forcedMovesValidSoFar(orderWheels, wi, moves, processLength)) {
                contradictingForcedMovesArray[wi]++;
                contradictingForcedMoves++;
                continue;
            }
            nonContradictingForcedMoves++;
            nonContradictingForcedMovesArray[wi]++;

            key.wheelPositions[W] = wheelStartPos;
            int nextWi = wi + 1;

            if (nextWi < Wheels.NUM_WHEELS) {
                success |= cribSolveFunctionsKnownDE(nextWi, moves);
            } else {
                if (testDecryption(cribArray, cipherArray)) {
                    printSolution(this, moves);
                    countMatchAlgo++;
                    success = true;
                } else {
                    countMatchAlgoButNotFull++;
                }
            }

            /*
            if (nextWi < orderWheels.length) {
                success |= cribSolveFunctionsKnownDE(nextWi, moves);
            } else {
                String entry = String.format("%02d-%02d-%02d-%02d %s\n",
                        key.wheelPositions[orderWheels[0]],
                        key.wheelPositions[orderWheels[1]],
                        key.wheelPositions[orderWheels[2]],
                        key.wheelPositions[orderWheels[3]],
                        movesString(moves[orderWheels[0]], 8));
                //String entry = movesString(moves[orderWheels[0]], processLength);

                for (int x = 0; x < processLength; x++) {
                    if (TenBits.count((int) NEXT_WHEEL_POS_PATTERNS[x]) > 1) {
                        System.out.printf("%s %d\n", entry, NEXT_WHEEL_POS_PATTERNS[x]);
                    }
                }

                map.add(entry);
                atomicCountSteps.getAndIncrement();
                success = true;
            }
            */
        }

        return success;

    }
    private boolean cribSolveFunctionsKnownDESpecial(int wi, boolean[][] moves) {

        if (key.ktf && cribArray.length > 1) {
            throw new RuntimeException("KTF and depth not compatible");
        }

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

        if (!isMoveForcedDE(key.ktf, orderWheels, wi)) {
            if (key.ktf) {
                MOVES_W[0] = shouldMoveDE(true, (byte) 0, orderWheels, wi, camState, 0);
                for (int cribPos = 1; cribPos < processLength; cribPos++) {
                    MOVES_W[cribPos] = shouldMoveDE(true, cribArray[0][cribPos - 1], orderWheels, wi, camState, cribPos);
                }
            } else {
                for (int cribPos = 0; cribPos < processLength; cribPos++) {
                    MOVES_W[cribPos] = shouldMoveDE(false, (byte) 0, orderWheels, wi, camState, cribPos);
                }
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
                continue;
            }

            if (!forcedMovesValidSoFar(orderWheels, wi, moves, processLength)) {
                continue;
            }

            key.wheelPositions[W] = wheelStartPos;
            int nextWi = wi + 1;

            if (nextWi < Wheels.NUM_WHEELS) {
                success |= cribSolveFunctionsKnownDESpecial(nextWi, moves);
                if (success) {
                    return true;
                }
            } else {
                if (testDecryption(cribArray, cipherArray)) {
                    return true;
                    //success = true;
                }
            }

        }

        return success;

    }
    private void cribSolveFunctionsKnownABandCA(int wi) {
        visits[wi]++;

        final int W = orderWheels[wi];

        final int WHEEL_SIZE = Wheels.WHEEL_SIZES[W];
        final int WHEEL_SIZE_MINUS_ONE = WHEEL_SIZE - 1;
        final long[][] WHEEL_POS_PATTERNS = allWheelsPosPatternsSingleFunction[W];
        final long[] CURRENT_WHEEL_POS_PATTERNS = wheelPosPatterns[wi];
        final long[] NEXT_WHEEL_POS_PATTERNS = wheelPosPatterns[wi + 1];

        boolean validWheelStartPos;
        int wheelStartPos;
        int cribPos;
        int wheelPos;
        long val;
        for (wheelStartPos = 0; wheelStartPos < WHEEL_SIZE; wheelStartPos++) {
            validWheelStartPos = true;
            for (cribPos = 0, wheelPos = wheelStartPos;
                 cribPos < processLength;
                 cribPos++, wheelPos = (wheelPos == WHEEL_SIZE_MINUS_ONE) ? 0 : wheelPos + 1) {
                if ((val = CURRENT_WHEEL_POS_PATTERNS[cribPos] & WHEEL_POS_PATTERNS[cribPos][wheelPos]) == 0) {
                    validWheelStartPos = false;
                    break;
                }
                NEXT_WHEEL_POS_PATTERNS[cribPos] = val;
            }

            if (!validWheelStartPos) {
                continue;
            }

            key.wheelPositions[W] = wheelStartPos;
            int nextWi = wi + 1;
            if (nextWi < Wheels.NUM_WHEELS) {
                cribSolveFunctionsKnownABandCA(nextWi);
            } else if (testDecryption(cribArray, cipherArray)) {
                printSolution(this, null);
                countMatchAlgo++;
            } else {
                countMatchAlgoButNotFull++;
            }
        }
        atomicCountSteps.getAndAdd(WHEEL_SIZE);


    }
    private void cribSolveFunctionsKnownC(int wi) {
        visits[wi]++;

        final int W = orderWheels[wi];

        final int WHEEL_SIZE = Wheels.WHEEL_SIZES[W];
        final int WHEEL_SIZE_MINUS_ONE = WHEEL_SIZE - 1;
        final long[][][] WHEEL_POS_PATTERNS = allWheelsPosPatternsSingleFunctionFunctionC[W];
        final long[][] CURRENT_WHEEL_POS_PATTERNS = wheelPosPatternsC[wi];
        final long[][] NEXT_WHEEL_POS_PATTERNS = wheelPosPatternsC[wi + 1];

        boolean validWheelStartPos;
        int wheelStartPos;
        int cribPos;
        int wheelPos;
        long val0, val1;
        for (wheelStartPos = 0; wheelStartPos < WHEEL_SIZE; wheelStartPos++) {
            if (wi == 0 && print) {
                System.out.print(".");
            }
            validWheelStartPos = true;
            wheelPos = wheelStartPos;
            for (cribPos = 0; cribPos < processLength; cribPos++) {
                val0 = CURRENT_WHEEL_POS_PATTERNS[0][cribPos] & WHEEL_POS_PATTERNS[cribPos][0][wheelPos];
                val1 = CURRENT_WHEEL_POS_PATTERNS[1][cribPos] & WHEEL_POS_PATTERNS[cribPos][1][wheelPos];
                if (val0 == 0 && val1 == 0) {
                    validWheelStartPos = false;
                    break;
                }
                NEXT_WHEEL_POS_PATTERNS[0][cribPos] = val0;
                NEXT_WHEEL_POS_PATTERNS[1][cribPos] = val1;
                wheelPos = (wheelPos == WHEEL_SIZE_MINUS_ONE) ? 0 : wheelPos + 1;
            }

            if (!validWheelStartPos) {
                continue;
            }

            key.wheelPositions[W] = wheelStartPos;
            int nextWi = wi + 1;
            if (nextWi < Wheels.NUM_WHEELS) {
                cribSolveFunctionsKnownC(nextWi);
            } else if (testDecryption(cribArray, cipherArray)) {
                printSolution(this, null);
                countMatchAlgo++;
            } else {
                countMatchAlgoButNotFull++;
            }
        }
        atomicCountSteps.getAndAdd(WHEEL_SIZE);


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
        decryptSturgeon.key.print(String.format("Match: %,6d (False: %,6d) IC: %7f Steps: %,15d %s %s", countMatchAlgo, countMatchAlgoButNotFull, ic, atomicCountSteps.longValue(),
                (moves != null) ? movesString(moves[orderWheels[0]], processLength) : "", (moves != null) ? movesString(moves[orderWheels[1]], processLength) : ""));

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
            System.out.printf("Out:  %s\n", Alphabet.toStringFormatted(plain, true));
            //System.out.printf("%s\n", Alphabet.toStringFormatted(plain, false));
            System.out.print("\n");
        }
    }


    private static boolean isMoveForcedDE(boolean ktf, int[] wheels, int wi) {

        // The first two
        if (wi < 2) {
            return true;
        }
        // With ktf, the steppings of A, B, C and D are not the same.
        if (ktf) {
            return false;
        }
        // A to D, if at least (another) one of A to D is in the first two
        return (wheels[wi] <= Wheels.D) && (wheels[0] <= Wheels.D || wheels[1] <= Wheels.D);
    }
    private boolean forcedMovesValidSoFar(int[] wheels, int lastWheelIndex, boolean[][] moves, int processLength) {
        for (int secondWheelIndex = 0; secondWheelIndex <= lastWheelIndex; secondWheelIndex++) {
            if (!isMoveForcedDE(key.ktf, wheels, secondWheelIndex)) {
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

    public int processLength(boolean functionsKnown) {
        return Math.min(getBestProcessLengthRaw(functionsKnown), cribArray[0].length);
    }

    private int getBestProcessLengthRaw(boolean functionsKnown) {

        int depth = depth();

        if (functionsKnown) {
            switch (key.model) {
                case T52D -> {
                    if (key.ktf) {
                        return 10;
                    }
                    return depth == 1 ? 11 : 10;
                }
                case T52E -> {
                    return depth == 1 ? 9 : 8;
                }
                case T52C -> {
                    return depth == 1 ? 15 : 12; // bare minimum 14 -; depth = 2 bare minimum = 11
                }
                case T52EE, T52CA -> {
                    return depth == 1 ? 18 : 9; // bare minimum 12 -; depth = 2 bare minimum = 8,
                }
                case T52AA, T52AB -> {
                    return depth == 1 ? 14 : 10; // bare minimum 13; depth 9
                }
            }
        }

        switch (key.model) {
            case T52AA, T52AB -> {
                return depth == 1 ? 18 : 12; // bare minimum 14; depth 10
            }
            case T52C -> {
                return depth == 1 ? 40 : 25; // bare minimum 25-30 - after 40 no gain; depth = 2 bare minimum = 15, optimal 25
            }
            case T52CA -> {
                return depth == 1 ? 50 : 18; // bare minimum = 20; after 50 - no gain
            }
            case T52EE -> {
                return depth == 1 ? 50 : 12; // bare minimum 18, ideal 70; depth bare min 9, ideal 15
            }
            case T52D -> {
                if (key.ktf) {
                    return 15;
                }
                return depth == 1 ? 15 : 12;
            }
            case T52E -> {
                if (key.ktf) {
                    return 12;
                }
                return depth == 1 ? 12 : 8;
            }
        }

        return -1;
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

    private boolean isWheelUsed(int[] wheels, int w, int length) {
        for (int index = 0; index < length; index++) {
            if (wheels[index] == w) {
                return true;
            }
        }
        return false;
    }

    static long[][][] allWheelsPosPatternsSingleFunction(int[][] validWheelBitmaps, int processLength) {

        long[][][] wheelPosPatterns = new long[10][processLength][];


        for (int w = 0; w < 10; w++) {
            final int[] WHEEL_INT_PATTERNS_AT_POSITIONS = Wheels.INT_PATTERNS[w];
            final int WHEEL_INT_PATTERN = TenBits.BITS[w];
            for (int cribPos = 0; cribPos < processLength; cribPos++) {
                wheelPosPatterns[w][cribPos] = new long[Wheels.WHEEL_SIZES[w]];

                for (int wheelPos = 0; wheelPos < Wheels.WHEEL_SIZES[w]; wheelPos++) {
                    long posPattern = 0;
                    if (validWheelBitmaps[cribPos].length <= 32) {
                        for (int validPattern : validWheelBitmaps[cribPos]) {
                            posPattern <<= 1;
                            if ((validPattern & WHEEL_INT_PATTERN) == WHEEL_INT_PATTERNS_AT_POSITIONS[wheelPos]) {
                                posPattern |= 0x1;
                            }
                        }
                    } else {
                        posPattern = 0x1f;
                    }
                    wheelPosPatterns[w][cribPos][wheelPos] = posPattern;
                }
            }
        }
        return wheelPosPatterns;
    }

    private static long[][][][] allWheelsPosPatternsSingleFunctionFunctionC(int[][] validWheelBitmaps, int processLength) {

        long[][][][] wheelPosPatterns = new long[10][processLength][][];


        for (int w = 0; w < 10; w++) {
            final int[] WHEEL_INT_PATTERNS_AT_POSITIONS = Wheels.INT_PATTERNS[w];
            final int WHEEL_INT_PATTERN = TenBits.BITS[w];
            for (int cribPos = 0; cribPos < processLength; cribPos++) {
                wheelPosPatterns[w][cribPos] = new long[2][Wheels.WHEEL_SIZES[w]];
                for (int wheelPos = 0; wheelPos < Wheels.WHEEL_SIZES[w]; wheelPos++) {
                    for (int start = 0; start <= 64; start += 64) {
                        long posPattern = 0;
                        for (int i = start; i < Math.min(start + 64, validWheelBitmaps[cribPos].length); i++) {
                            int validPattern = validWheelBitmaps[cribPos][i];
                            posPattern <<= 1;
                            if ((validPattern & WHEEL_INT_PATTERN) == WHEEL_INT_PATTERNS_AT_POSITIONS[wheelPos]) {
                                posPattern |= 0x1;
                            }
                        }
                        wheelPosPatterns[w][cribPos][start / 64][wheelPos] = posPattern;
                    }
                }
            }
        }
        return wheelPosPatterns;
    }

    static long[][][][] allWheelsPosPatternsAllFunctions(int[][] validWheelBitmaps, int processLength) {

        long[][][][] wheelPosPatterns = new long[10][10][processLength][];

        for (int w = 0; w < 10; w++) {
            final boolean[] WHEEL_PATTERNS_AT_POSITIONS = Wheels.PATTERNS[w];
            for (int wf = 0; wf < 10; wf++) {
                final int WHEEL_FUNCTION_INT_PATTERN = TenBits.BITS[wf];
                for (int cribPos = 0; cribPos < processLength; cribPos++) {
                    wheelPosPatterns[w][wf][cribPos] = new long[Wheels.WHEEL_SIZES[w]];

                    for (int wheelPos = 0; wheelPos < Wheels.WHEEL_SIZES[w]; wheelPos++) {
                        long posPattern = 0;
                        if (validWheelBitmaps[cribPos].length <= 32) {
                            for (int validPattern : validWheelBitmaps[cribPos]) {
                                posPattern <<= 1;
                                if (((validPattern & WHEEL_FUNCTION_INT_PATTERN) != 0x0) == WHEEL_PATTERNS_AT_POSITIONS[wheelPos]) {
                                    posPattern |= 0x1;
                                }
                            }
                        } else {
                            posPattern = 0x1f;
                        }
                        wheelPosPatterns[w][wf][cribPos][wheelPos] = posPattern;
                    }
                }
            }
        }
        return wheelPosPatterns;
    }

    private static long[][][][][] allWheelsPosPatternsAllFunctionsC(int[][] validWheelBitmaps, int processLength) {
        long[][][][][] wheelPosPatterns = new long[10][10][processLength][][];

        for (int w = 0; w < 10; w++) {
            final boolean[] WHEEL_PATTERNS_AT_POSITIONS = Wheels.PATTERNS[w];
            for (int wf = 0; wf < 10; wf++) {
                final int WHEEL_FUNCTION_INT_PATTERN = TenBits.BITS[wf];
                for (int cribPos = 0; cribPos < processLength; cribPos++) {
                    wheelPosPatterns[w][wf][cribPos] = new long[2][Wheels.WHEEL_SIZES[w]];

                    for (int wheelPos = 0; wheelPos < Wheels.WHEEL_SIZES[w]; wheelPos++) {
                        for (int start = 0; start <= 64; start += 64) {
                            long posPattern = 0;
                            for (int i = start; i < Math.min(start + 64, validWheelBitmaps[cribPos].length); i++) {
                                int validPattern = validWheelBitmaps[cribPos][i];
                                posPattern <<= 1;
                                if (((validPattern & WHEEL_FUNCTION_INT_PATTERN) != 0x0) == WHEEL_PATTERNS_AT_POSITIONS[wheelPos]) {
                                    posPattern |= 0x1;
                                }
                            }
                            wheelPosPatterns[w][wf][cribPos][start / 64][wheelPos] = posPattern;
                        }
                    }
                }

            }
        }
        return wheelPosPatterns;
    }

    private static boolean shouldMoveDE(boolean ktf, byte lastPlain, int[] wheels, int wi, boolean[][] camState, int i) {

        /*
         boolean stepK ;
            boolean stepJ = !a || k;
            boolean stepH = !k || j;
            boolean stepG = !j || !h;
            boolean stepD = !f || !e;


            if (!ktf) {
                stepK = e || !d;
                stepF = h || g;
                stepE = !g || f;
                stepC = stepB = stepA = stepD;
            } else {

                stepK = a || !b;
                stepF = !lastZ || h || g;
                stepE = !lastZ || !g || f;
                stepC = !d || e;
                stepB = lastZ || !c || d;
                stepA = lastZ || b || c;

                lastZ = FiveBits.isSet(2, plain);
            }
        */


        switch (wheels[wi]) {
            case 3 -> {
                boolean e = camState[4][i];
                boolean f = camState[5][i];
                return !f || !e;
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
            default -> {
            }
        }

        if (!ktf) {
            switch (wheels[wi]) {
                case 0, 1, 2 -> {
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
                case 9 -> {
                    boolean d = camState[3][i];
                    boolean e = camState[4][i];
                    return e || !d;
                }
                default -> throw new RuntimeException("!!!");
            }
        } else {
            if (lastPlain == -1) {
                throw new RuntimeException("KTF modes does not all unknown symbols");
            }
            boolean lastZ = FiveBits.isSet(2, lastPlain);

            switch (wheels[wi]) {
                case 0 -> {
                    boolean b = camState[1][i];
                    boolean c = camState[2][i];
                    return lastZ || b || c;
                }
                case 1 -> {
                    boolean c = camState[2][i];
                    boolean d = camState[3][i];
                    return lastZ || !c || d;
                }
                case 2 -> {
                    boolean d = camState[3][i];
                    boolean e = camState[4][i];
                    return !d || e;
                }
                case 4 -> {
                    boolean f = camState[5][i];
                    boolean g = camState[6][i];
                    return !lastZ || !g || f;
                }
                case 5 -> {
                    boolean g = camState[6][i];
                    boolean h = camState[7][i];
                    return !lastZ || h || g;
                }
                case 9 -> {
                    boolean a = camState[0][i];
                    boolean b = camState[1][i];
                    return a || !b;
                }
                default -> throw new RuntimeException("!!!");
            }
        }

    }

}
