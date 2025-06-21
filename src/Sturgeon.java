import java.util.*;

class Sturgeon {

    // Convention: for flexible perfm of T52AB and T52D, the switches are in their order of definition in key.wheelSettings.

    // When looking at control bitmap, low 5 are for perm, high 5 for xor

    Key key;
    final int[] wheelCurrentPositions = new int[Wheels.NUM_WHEELS];

    static boolean RESTART_ON_NULL = false;

    // Computed at resetPositionsAndComputeMappings() time. Do not change outside resetPositionsAndComputeMappings.
    final int[] controlMapping = new int[TenBits.SPACE];
    final byte[][] alphabet = new byte[TenBits.SPACE][FiveBits.SPACE];// [perm(low5) | xor(high5)][input];
    final byte[][] inverseAlphabet = new byte[TenBits.SPACE][FiveBits.SPACE];// [perm(low5) | xor(high5)][input];

    final int[][] perms = FixedPermutation.compute();
    private final int[] srLogic = new int[TenBits.SPACE];

    private final ClassXor32 class32 = new ClassXor32();
    private final Class1024 class1024 = new Class1024();

    Sturgeon(Key key) {
        this.key = new Key(key);
        resetPositionsAndComputeMappings();
    }
    Sturgeon(Model model) {
        this(Key.defaultKey(model));
    }

    public void bitAnalysisAB(byte[][] cipherArray) {
        double[][][][] ref = class1024.bitAnalysis(this, cipherArray, null);
        cipherArray[0] = Arrays.copyOf(cipherArray[0], 3000);
        //WheelFunctionSet s = WheelFunctionSet.SELECTION_XOR_ALL;
        WheelFunctionSet s = new WheelFunctionSet(WheelFunctionSet.XOR_I, WheelFunctionSet.XOR_II, WheelFunctionSet.XOR_V);
        System.out.println(key.getWheelSettingsString(s));
        System.out.printf("%s %7.4f\n", key.getWheelPositionsString(s), class1024.bitAnalysis(this, cipherArray, ref)[0][0][0][0]);
        double best = Double.MAX_VALUE;
        for (int w = 0; w < 10; w++) {
            if (!s.containsWheel(w, key.wheelSettings)) {
                continue;
            }
            int bestP = 0;
            key.wheelPositions[w] = 0;
            double[][][][] dummy = class1024.bitAnalysis(this, cipherArray, ref);
            best = dummy[0][0][0][0];
            for (int p = 1; p < Wheels.WHEEL_SIZES[w]; p++) {
                key.wheelPositions[w] = p;
                dummy = class1024.bitAnalysis(this, cipherArray, ref);
                double score = dummy[0][0][0][0];
                //System.out.printf("%s %7.4f\n", key.getWheelPositionsString(s), score);

                if (score < best) {
                    bestP = p;
                    best = score;
                }
            }
            key.wheelPositions[w] = bestP;
        }
        System.out.printf("%s %7.4f\n", key.getWheelPositionsString(s), best);
    }

    void randomize() {

        StringBuilder wheelsStr = new StringBuilder();
        if (key.model.hasProgrammablePermSwitches()) {
            WheelSetting[] set = ProgrammablePermutations.validSettings()[CommonRandom.r.nextInt(ProgrammablePermutations.validSettings().length)];
            for (WheelSetting ws : set) {
                if (wheelsStr.length() > 0) {
                    wheelsStr.append(":");
                }
                wheelsStr.append(ws.toString());

            }
            wheelsStr.append(Wheels.DEFAULT_XOR_WHEELS);
        } else {
            wheelsStr.append(Wheels.DEFAULT_PERM_AND_XOR_WHEELS);
        }
        String[] wheelsStrArray = wheelsStr.toString().split(":");

        for (int i = 0; i < (10 - 1); i++) {
            int val = i + CommonRandom.r.nextInt(10 - i);
            String temp = wheelsStrArray[i];
            wheelsStrArray[i] = wheelsStrArray[val];
            wheelsStrArray[val] = temp;
        }

        for (int w = 0; w < 10; w++) {
            key.wheelSettings[w] = WheelSetting.parse(wheelsStrArray[w], key.model);
        }

        randomPositions();

        resetPositionsAndComputeMappings();

    }
    Sturgeon randomizeXor(int mode) {
        // Mode 0 - random
        // 2
        // 3

        if (!key.model.hasProgrammablePermSwitches()) {
            throw new RuntimeException("Not supported for model " + key.model);
        }

        int swaps2 = mode == 2 ? 1 : (mode == 0 ? 20 : 0);
        int swaps3 = mode == 3 ? 1 : 0;
        for (int i = 0; i < swaps2; i++) {

            int w1 = -1, w2 = -1;
            int start = CommonRandom.r.nextInt(10);
            for (int w = 0; w < 10; w++) {
                w1 = (w + start) % 10;
                if (key.wheelSettings[w1].getFunction() == WheelSetting.WheelFunction.XOR) {
                    break;
                }
            }
            start = CommonRandom.r.nextInt(10);
            for (int w = 0; w < 10; w++) {
                w2 = (w + start) % 10;
                if (w2 != w1 && key.wheelSettings[w2].getFunction() == WheelSetting.WheelFunction.XOR) {
                    break;
                }
            }
            WheelSetting temp = key.wheelSettings[w1];
            key.wheelSettings[w1] = key.wheelSettings[w2];
            key.wheelSettings[w2] = temp;

        }

        for (int i = 0; i < swaps3; i++) {

            int w1 = -1, w2 = -1, w3 = -1;
            int start = CommonRandom.r.nextInt(10);
            for (int w = 0; w < 10; w++) {
                w1 = (w + start) % 10;
                if (key.wheelSettings[w1].getFunction() == WheelSetting.WheelFunction.XOR) {
                    break;
                }
            }
            start = CommonRandom.r.nextInt(10);
            for (int w = 0; w < 10; w++) {
                w2 = (w + start) % 10;
                if (w2 != w1 && key.wheelSettings[w2].getFunction() == WheelSetting.WheelFunction.XOR) {
                    break;
                }
            }
            start = CommonRandom.r.nextInt(10);
            for (int w = 0; w < 10; w++) {
                w3 = (w + start) % 10;
                if (w3 != w1 && w3 != w2 && key.wheelSettings[w3].getFunction() == WheelSetting.WheelFunction.XOR) {
                    break;
                }
            }
            WheelSetting temp = key.wheelSettings[w1];
            key.wheelSettings[w1] = key.wheelSettings[w2];
            key.wheelSettings[w2] = key.wheelSettings[w3];
            key.wheelSettings[w3] = temp;

        }


        resetPositionsAndComputeMappings();
        return this;
    }
    void randomizeOrder() {

        for (int i = 0; i < 20; i++) {

            int w1 = CommonRandom.r.nextInt(10);

            int w2;
            do {
                w2 = CommonRandom.r.nextInt(10);
            } while (w1 == w2);


            WheelSetting temp = key.wheelSettings[w1];
            key.wheelSettings[w1] = key.wheelSettings[w2];
            key.wheelSettings[w2] = temp;

        }
        computeMappings();

    }
    void randomizeOrderExcept(WheelFunctionSet wheelFunctionSetToPreserve) {

        for (int i = 0; i < 20; i++) {

            int w1;
            do {
                w1 = CommonRandom.r.nextInt(10);
            } while (wheelFunctionSetToPreserve.containsWheel(w1, key.wheelSettings));



            int w2;
            do {
                w2 = CommonRandom.r.nextInt(10);
            } while (w1 == w2 || wheelFunctionSetToPreserve.containsWheel(w2, key.wheelSettings));


            WheelSetting temp = key.wheelSettings[w1];
            key.wheelSettings[w1] = key.wheelSettings[w2];
            key.wheelSettings[w2] = temp;

        }
        computeMappings();

    }
    Sturgeon randomizePerm() {


        if (!key.model.hasProgrammablePermSwitches()) {
            throw new RuntimeException("Not supported for model " + key.model);
        }


        for (int i = 0; i < 20; i++) {

            int w1 = -1, w2 = -1;
            int start = CommonRandom.r.nextInt(10);
            for (int w = 0; w < 10; w++) {
                w1 = (w + start) % 10;
                if (key.wheelSettings[w1].getFunction() != WheelSetting.WheelFunction.XOR) {
                    break;
                }
            }
            start = CommonRandom.r.nextInt(10);
            for (int w = 0; w < 10; w++) {
                w2 = (w + start) % 10;
                if (w2 != w1 && key.wheelSettings[w2].getFunction() != WheelSetting.WheelFunction.XOR) {
                    break;
                }
            }
            WheelSetting temp = key.wheelSettings[w1];
            key.wheelSettings[w1] = key.wheelSettings[w2];
            key.wheelSettings[w2] = temp;

        }

        resetPositionsAndComputeMappings();
        return this;
    }
    void randomPositions() {
        for (int w = 0; w < 10; w++) {
            key.wheelPositions[w] = CommonRandom.r.nextInt(Wheels.WHEEL_SIZES[w]);
        }
    }
    void zeroPositions() {
        Arrays.fill(key.wheelPositions, 0);
    }

    void resetPositionsAndComputeMappings() {
        System.arraycopy(key.wheelPositions, 0, wheelCurrentPositions, 0, Wheels.NUM_WHEELS);
        computeMappings();
    }
    void computeMappings() {
        SturgeonAlphabets.computeAlphabet(key, alphabet, inverseAlphabet, perms, srLogic);
        SturgeonControl.computeControlMapping(key, controlMapping);
    }
    void encryptDecrypt(byte[] input, byte[] output, int len, boolean[][] moves, boolean encrypt) {

        if (key.model.hasIrregularStepping()) {
            encryptDecryptIrregularStepping(input, output, len, moves, encrypt);
            return;
        }
        if (moves != null) {
            for (int w = 0; w < 10; w++) {
                Arrays.fill(moves[w], true);
            }
        }

        int wheelPos0 = key.wheelPositions[0];
        int wheelPos1 = key.wheelPositions[1];
        int wheelPos2 = key.wheelPositions[2];
        int wheelPos3 = key.wheelPositions[3];
        int wheelPos4 = key.wheelPositions[4];
        int wheelPos5 = key.wheelPositions[5];
        int wheelPos6 = key.wheelPositions[6];
        int wheelPos7 = key.wheelPositions[7];
        int wheelPos8 = key.wheelPositions[8];
        int wheelPos9 = key.wheelPositions[9];

        byte in, out;
        int wheelsBitmap, control;
        for (int i = 0; i < len; i++) {
            if (RESTART_ON_NULL && !encrypt) {
                if (input[i] == -1) {
                    wheelPos0 = key.wheelPositions[0];
                    wheelPos1 = key.wheelPositions[1];
                    wheelPos2 = key.wheelPositions[2];
                    wheelPos3 = key.wheelPositions[3];
                    wheelPos4 = key.wheelPositions[4];
                    wheelPos5 = key.wheelPositions[5];
                    wheelPos6 = key.wheelPositions[6];
                    wheelPos7 = key.wheelPositions[7];
                    wheelPos8 = key.wheelPositions[8];
                    wheelPos9 = key.wheelPositions[9];
                    output[i] = -1;
                    continue;
                }
            }

            wheelsBitmap = Wheels.getWheelsBitmap(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
            control = controlMapping[wheelsBitmap];

            in = input[i];
            out = (in == -1) ? -1 : (encrypt ? alphabet[control][in] : inverseAlphabet[control][in]);
            output[i] = out;

            wheelPos0 = (wheelPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelPos0 + 1;
            wheelPos1 = (wheelPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelPos1 + 1;
            wheelPos2 = (wheelPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelPos2 + 1;
            wheelPos3 = (wheelPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelPos3 + 1;
            wheelPos4 = (wheelPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelPos4 + 1;
            wheelPos5 = (wheelPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelPos5 + 1;
            wheelPos6 = (wheelPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelPos6 + 1;
            wheelPos7 = (wheelPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelPos7 + 1;
            wheelPos8 = (wheelPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelPos8 + 1;
            wheelPos9 = (wheelPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelPos9 + 1;

        }
        updateCurrentPositions(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
    }
    private void updateCurrentPositions(int wheelPos0, int wheelPos1, int wheelPos2, int wheelPos3, int wheelPos4, int wheelPos5, int wheelPos6, int wheelPos7, int wheelPos8, int wheelPos9) {
        wheelCurrentPositions[0] = wheelPos0;
        wheelCurrentPositions[1] = wheelPos1;
        wheelCurrentPositions[2] = wheelPos2;
        wheelCurrentPositions[3] = wheelPos3;
        wheelCurrentPositions[4] = wheelPos4;
        wheelCurrentPositions[5] = wheelPos5;
        wheelCurrentPositions[6] = wheelPos6;
        wheelCurrentPositions[7] = wheelPos7;
        wheelCurrentPositions[8] = wheelPos8;
        wheelCurrentPositions[9] = wheelPos9;
    }
    void encryptDepth(byte[][] input, byte[][] output, int len, boolean[][] moves, boolean print) {

        if (key.ktf && input.length > 1) {
            throw new RuntimeException("Depth encryption not relevant for KTF");
        }

        int[][] validControls = new int[len][];

        int wheelPos0 = key.wheelPositions[0];
        int wheelPos1 = key.wheelPositions[1];
        int wheelPos2 = key.wheelPositions[2];
        int wheelPos3 = key.wheelPositions[3];
        int wheelPos4 = key.wheelPositions[4];
        int wheelPos5 = key.wheelPositions[5];
        int wheelPos6 = key.wheelPositions[6];
        int wheelPos7 = key.wheelPositions[7];
        int wheelPos8 = key.wheelPositions[8];
        int wheelPos9 = key.wheelPositions[9];

        int wheelStepCamPos0 = 0, wheelStepCamPos1 = 0, wheelStepCamPos2 = 0, wheelStepCamPos3 = 0, wheelStepCamPos4 = 0,
                wheelStepCamPos5 = 0, wheelStepCamPos6 = 0, wheelStepCamPos7 = 0, wheelStepCamPos8 = 0, wheelStepCamPos9 = 0;

        if (key.model.hasIrregularStepping()) {
            wheelStepCamPos0 = (wheelPos0 + Wheels.WHEEL_STEP_CAM_POS[0]) % Wheels.WHEEL_SIZES[0];
            wheelStepCamPos1 = (wheelPos1 + Wheels.WHEEL_STEP_CAM_POS[1]) % Wheels.WHEEL_SIZES[1];
            wheelStepCamPos2 = (wheelPos2 + Wheels.WHEEL_STEP_CAM_POS[2]) % Wheels.WHEEL_SIZES[2];
            wheelStepCamPos3 = (wheelPos3 + Wheels.WHEEL_STEP_CAM_POS[3]) % Wheels.WHEEL_SIZES[3];
            wheelStepCamPos4 = (wheelPos4 + Wheels.WHEEL_STEP_CAM_POS[4]) % Wheels.WHEEL_SIZES[4];
            wheelStepCamPos5 = (wheelPos5 + Wheels.WHEEL_STEP_CAM_POS[5]) % Wheels.WHEEL_SIZES[5];
            wheelStepCamPos6 = (wheelPos6 + Wheels.WHEEL_STEP_CAM_POS[6]) % Wheels.WHEEL_SIZES[6];
            wheelStepCamPos7 = (wheelPos7 + Wheels.WHEEL_STEP_CAM_POS[7]) % Wheels.WHEEL_SIZES[7];
            wheelStepCamPos8 = (wheelPos8 + Wheels.WHEEL_STEP_CAM_POS[8]) % Wheels.WHEEL_SIZES[8];
            wheelStepCamPos9 = (wheelPos9 + Wheels.WHEEL_STEP_CAM_POS[9]) % Wheels.WHEEL_SIZES[9];
        }

        int depth = Math.min(input.length, output.length);
        byte in, out;
        int wheelsBitmap, control;

        boolean lastZ = false;

        for (int i = 0; i < len; i++) {

            wheelsBitmap = Wheels.getWheelsBitmap(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
            control = controlMapping[wheelsBitmap];

            for (int d = 0; d < depth; d++) {
                in = input[d][i];
                out = in != -1 ? alphabet[control][in] : -1;
                output[d][i] = out;
            }

            // We don't return the validControls, but we print if print = true;
            if (print) {
                getValidWheelBitmaps(input, output, i, validControls, wheelsBitmap, control, true);
            }

            if (key.model.hasIrregularStepping()) {
                boolean a = Wheels.PATTERNS0[wheelStepCamPos0];
                boolean b = Wheels.PATTERNS1[wheelStepCamPos1];
                boolean c = Wheels.PATTERNS2[wheelStepCamPos2];
                boolean d = Wheels.PATTERNS3[wheelStepCamPos3];
                boolean e = Wheels.PATTERNS4[wheelStepCamPos4];
                boolean f = Wheels.PATTERNS5[wheelStepCamPos5];
                boolean g = Wheels.PATTERNS6[wheelStepCamPos6];
                boolean h = Wheels.PATTERNS7[wheelStepCamPos7];
                boolean j = Wheels.PATTERNS8[wheelStepCamPos8];
                boolean k = Wheels.PATTERNS9[wheelStepCamPos9];

                boolean stepK;
                boolean stepJ = !a || k;
                boolean stepH = !k || j;
                boolean stepG = !j || !h;
                boolean stepF;
                boolean stepE;
                boolean stepD = !f || !e;
                boolean stepC;
                boolean stepB;
                boolean stepA;

                if (!key.ktf) {
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

                    if (input[0][i] == -1) {
                        throw new RuntimeException("KTF modes does not all unknown symbols");
                    }
                    lastZ = FiveBits.isSet(2, input[0][i]);
                }

                if (moves != null) {
                    moves[Wheels.A][i] = stepA;
                    moves[Wheels.B][i] = stepB;
                    moves[Wheels.C][i] = stepC;
                    moves[Wheels.D][i] = stepD;
                    moves[Wheels.E][i] = stepE;
                    moves[Wheels.F][i] = stepF;
                    moves[Wheels.G][i] = stepG;
                    moves[Wheels.H][i] = stepH;
                    moves[Wheels.J][i] = stepJ;
                    moves[Wheels.K][i] = stepK;
                }
                if (stepA) {
                    wheelPos0 = (wheelPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelPos0 + 1;
                    wheelStepCamPos0 = (wheelStepCamPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelStepCamPos0 + 1;
                }
                if (stepB) {
                    wheelPos1 = (wheelPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelPos1 + 1;
                    wheelStepCamPos1 = (wheelStepCamPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelStepCamPos1 + 1;
                }
                if (stepC) {
                    wheelPos2 = (wheelPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelPos2 + 1;
                    wheelStepCamPos2 = (wheelStepCamPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelStepCamPos2 + 1;
                }
                if (stepD) {
                    wheelPos3 = (wheelPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelPos3 + 1;
                    wheelStepCamPos3 = (wheelStepCamPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelStepCamPos3 + 1;
                }

                if (stepE) {
                    wheelPos4 = (wheelPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelPos4 + 1;
                    wheelStepCamPos4 = (wheelStepCamPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelStepCamPos4 + 1;
                }
                if (stepF) {
                    wheelPos5 = (wheelPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelPos5 + 1;
                    wheelStepCamPos5 = (wheelStepCamPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelStepCamPos5 + 1;
                }
                if (stepG) {
                    wheelPos6 = (wheelPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelPos6 + 1;
                    wheelStepCamPos6 = (wheelStepCamPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelStepCamPos6 + 1;
                }
                if (stepH) {
                    wheelPos7 = (wheelPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelPos7 + 1;
                    wheelStepCamPos7 = (wheelStepCamPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelStepCamPos7 + 1;
                }
                if (stepJ) {
                    wheelPos8 = (wheelPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelPos8 + 1;
                    wheelStepCamPos8 = (wheelStepCamPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelStepCamPos8 + 1;
                }
                if (stepK) {
                    wheelPos9 = (wheelPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelPos9 + 1;
                    wheelStepCamPos9 = (wheelStepCamPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelStepCamPos9 + 1;
                }
            } else {
                wheelPos0 = (wheelPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelPos0 + 1;
                wheelPos1 = (wheelPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelPos1 + 1;
                wheelPos2 = (wheelPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelPos2 + 1;
                wheelPos3 = (wheelPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelPos3 + 1;
                wheelPos4 = (wheelPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelPos4 + 1;
                wheelPos5 = (wheelPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelPos5 + 1;
                wheelPos6 = (wheelPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelPos6 + 1;
                wheelPos7 = (wheelPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelPos7 + 1;
                wheelPos8 = (wheelPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelPos8 + 1;
                wheelPos9 = (wheelPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelPos9 + 1;
            }
        }
        updateCurrentPositions(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);

    }
    boolean testDecryption(byte[][] plainArray, byte[][] cipherArray) {
        int depth = plainArray.length;
        for (int d = 0; d < depth; d++) {
            int len = Math.min(plainArray[d].length, cipherArray[d].length);
            byte[] plain = new byte[len];
            encryptDecrypt(cipherArray[d], plain, len, null, false);
            for (int i = 0; i < len; i++) {
                if (plainArray[d][i] != -1 && cipherArray[d][i] != -1 && plain[i] != plainArray[d][i]) {
                    return false;
                }
            }
        }
        return true;
    }
    private void encryptDecryptIrregularStepping(byte[] input, byte[] output, int len, boolean[][] moves, boolean encrypt) {

        int wheelPos0 = key.wheelPositions[0];
        int wheelPos1 = key.wheelPositions[1];
        int wheelPos2 = key.wheelPositions[2];
        int wheelPos3 = key.wheelPositions[3];
        int wheelPos4 = key.wheelPositions[4];
        int wheelPos5 = key.wheelPositions[5];
        int wheelPos6 = key.wheelPositions[6];
        int wheelPos7 = key.wheelPositions[7];
        int wheelPos8 = key.wheelPositions[8];
        int wheelPos9 = key.wheelPositions[9];

        int wheelStepCamPos0 = (key.wheelPositions[0] + Wheels.WHEEL_STEP_CAM_POS[0]) % Wheels.WHEEL_SIZES[0];
        int wheelStepCamPos1 = (key.wheelPositions[1] + Wheels.WHEEL_STEP_CAM_POS[1]) % Wheels.WHEEL_SIZES[1];
        int wheelStepCamPos2 = (key.wheelPositions[2] + Wheels.WHEEL_STEP_CAM_POS[2]) % Wheels.WHEEL_SIZES[2];
        int wheelStepCamPos3 = (key.wheelPositions[3] + Wheels.WHEEL_STEP_CAM_POS[3]) % Wheels.WHEEL_SIZES[3];
        int wheelStepCamPos4 = (key.wheelPositions[4] + Wheels.WHEEL_STEP_CAM_POS[4]) % Wheels.WHEEL_SIZES[4];
        int wheelStepCamPos5 = (key.wheelPositions[5] + Wheels.WHEEL_STEP_CAM_POS[5]) % Wheels.WHEEL_SIZES[5];
        int wheelStepCamPos6 = (key.wheelPositions[6] + Wheels.WHEEL_STEP_CAM_POS[6]) % Wheels.WHEEL_SIZES[6];
        int wheelStepCamPos7 = (key.wheelPositions[7] + Wheels.WHEEL_STEP_CAM_POS[7]) % Wheels.WHEEL_SIZES[7];
        int wheelStepCamPos8 = (key.wheelPositions[8] + Wheels.WHEEL_STEP_CAM_POS[8]) % Wheels.WHEEL_SIZES[8];
        int wheelStepCamPos9 = (key.wheelPositions[9] + Wheels.WHEEL_STEP_CAM_POS[9]) % Wheels.WHEEL_SIZES[9];

        boolean lastZ = false;
        byte in, out;
        int wheelsBitmap;
        for (int i = 0; i < len; i++) {

            wheelsBitmap = Wheels.getWheelsBitmap(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
            wheelsBitmap = controlMapping[wheelsBitmap];

            in = input[i];
            if (in == -1) {
                out = -1;
            } else {
                out = encrypt ? alphabet[wheelsBitmap][in] : inverseAlphabet[wheelsBitmap][in];
            }
            output[i] = out;

            boolean a = Wheels.PATTERNS0[wheelStepCamPos0];
            boolean b = Wheels.PATTERNS1[wheelStepCamPos1];
            boolean c = Wheels.PATTERNS2[wheelStepCamPos2];
            boolean d = Wheels.PATTERNS3[wheelStepCamPos3];
            boolean e = Wheels.PATTERNS4[wheelStepCamPos4];
            boolean f = Wheels.PATTERNS5[wheelStepCamPos5];
            boolean g = Wheels.PATTERNS6[wheelStepCamPos6];
            boolean h = Wheels.PATTERNS7[wheelStepCamPos7];
            boolean j = Wheels.PATTERNS8[wheelStepCamPos8];
            boolean k = Wheels.PATTERNS9[wheelStepCamPos9];

            boolean stepK;
            boolean stepJ = !a || k;
            boolean stepH = !k || j;
            boolean stepG = !j || !h;
            boolean stepF;
            boolean stepE;
            boolean stepD = !f || !e;
            boolean stepC;
            boolean stepB;
            boolean stepA;

            if (!key.ktf) {
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

                byte plain = encrypt ? in : out;
                if (plain == -1) {
                    throw new RuntimeException("KTF modes does not all unknown symbols");
                }
                lastZ = FiveBits.isSet(2, plain);
            }

            if (stepA) {
                wheelPos0 = (wheelPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelPos0 + 1;
                wheelStepCamPos0 = (wheelStepCamPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelStepCamPos0 + 1;
            }
            if (stepB) {
                wheelPos1 = (wheelPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelPos1 + 1;
                wheelStepCamPos1 = (wheelStepCamPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelStepCamPos1 + 1;
            }
            if (stepC) {
                wheelPos2 = (wheelPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelPos2 + 1;
                wheelStepCamPos2 = (wheelStepCamPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelStepCamPos2 + 1;
            }
            if (stepD) {
                wheelPos3 = (wheelPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelPos3 + 1;
                wheelStepCamPos3 = (wheelStepCamPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelStepCamPos3 + 1;
            }
            if (stepE) {
                wheelPos4 = (wheelPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelPos4 + 1;
                wheelStepCamPos4 = (wheelStepCamPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelStepCamPos4 + 1;
            }
            if (stepF) {
                wheelPos5 = (wheelPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelPos5 + 1;
                wheelStepCamPos5 = (wheelStepCamPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelStepCamPos5 + 1;
            }
            if (stepG) {
                wheelPos6 = (wheelPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelPos6 + 1;
                wheelStepCamPos6 = (wheelStepCamPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelStepCamPos6 + 1;
            }
            if (stepH) {
                wheelPos7 = (wheelPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelPos7 + 1;
                wheelStepCamPos7 = (wheelStepCamPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelStepCamPos7 + 1;
            }
            if (stepJ) {
                wheelPos8 = (wheelPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelPos8 + 1;
                wheelStepCamPos8 = (wheelStepCamPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelStepCamPos8 + 1;
            }
            if (stepK) {
                wheelPos9 = (wheelPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelPos9 + 1;
                wheelStepCamPos9 = (wheelStepCamPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelStepCamPos9 + 1;
            }

//                wheelPos0 = (wheelPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelPos0 + 1;
//                wheelStepCamPos0 = (wheelStepCamPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelStepCamPos0 + 1;
//                wheelPos1 = (wheelPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelPos1 + 1;
//                wheelStepCamPos1 = (wheelStepCamPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelStepCamPos1 + 1;
//                wheelPos2 = (wheelPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelPos2 + 1;
//                wheelStepCamPos2 = (wheelStepCamPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelStepCamPos2 + 1;
//                wheelPos3 = (wheelPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelPos3 + 1;
//                wheelStepCamPos3 = (wheelStepCamPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelStepCamPos3 + 1;
//                wheelPos4 = (wheelPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelPos4 + 1;
//                wheelStepCamPos4 = (wheelStepCamPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelStepCamPos4 + 1;
//                wheelPos5 = (wheelPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelPos5 + 1;
//                wheelStepCamPos5 = (wheelStepCamPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelStepCamPos5 + 1;
//                wheelPos6 = (wheelPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelPos6 + 1;
//                wheelStepCamPos6 = (wheelStepCamPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelStepCamPos6 + 1;
//                wheelPos7 = (wheelPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelPos7 + 1;
//                wheelStepCamPos7 = (wheelStepCamPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelStepCamPos7 + 1;
//                wheelPos8 = (wheelPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelPos8 + 1;
//                wheelStepCamPos8 = (wheelStepCamPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelStepCamPos8 + 1;
//
//                wheelPos9 = (wheelPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelPos9 + 1;
//                wheelStepCamPos9 = (wheelStepCamPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelStepCamPos9 + 1;



            if (moves != null) {
                moves[Wheels.A][i] = stepA;
                moves[Wheels.B][i] = stepB;
                moves[Wheels.C][i] = stepC;
                moves[Wheels.D][i] = stepD;
                moves[Wheels.E][i] = stepE;
                moves[Wheels.F][i] = stepF;
                moves[Wheels.G][i] = stepG;
                moves[Wheels.H][i] = stepH;
                moves[Wheels.J][i] = stepJ;
                moves[Wheels.K][i] = stepK;
            }
        }
        updateCurrentPositions(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
    }
    void debugEncrypt(byte[] input, byte[] output, int len) {


        int wheelPos0 = key.wheelPositions[0];
        int wheelPos1 = key.wheelPositions[1];
        int wheelPos2 = key.wheelPositions[2];
        int wheelPos3 = key.wheelPositions[3];
        int wheelPos4 = key.wheelPositions[4];
        int wheelPos5 = key.wheelPositions[5];
        int wheelPos6 = key.wheelPositions[6];
        int wheelPos7 = key.wheelPositions[7];
        int wheelPos8 = key.wheelPositions[8];
        int wheelPos9 = key.wheelPositions[9];

        int wheelStepCamPos0 = (key.wheelPositions[0] + Wheels.WHEEL_STEP_CAM_POS[0]) % Wheels.WHEEL_SIZES[0];
        int wheelStepCamPos1 = (key.wheelPositions[1] + Wheels.WHEEL_STEP_CAM_POS[1]) % Wheels.WHEEL_SIZES[1];
        int wheelStepCamPos2 = (key.wheelPositions[2] + Wheels.WHEEL_STEP_CAM_POS[2]) % Wheels.WHEEL_SIZES[2];
        int wheelStepCamPos3 = (key.wheelPositions[3] + Wheels.WHEEL_STEP_CAM_POS[3]) % Wheels.WHEEL_SIZES[3];
        int wheelStepCamPos4 = (key.wheelPositions[4] + Wheels.WHEEL_STEP_CAM_POS[4]) % Wheels.WHEEL_SIZES[4];
        int wheelStepCamPos5 = (key.wheelPositions[5] + Wheels.WHEEL_STEP_CAM_POS[5]) % Wheels.WHEEL_SIZES[5];
        int wheelStepCamPos6 = (key.wheelPositions[6] + Wheels.WHEEL_STEP_CAM_POS[6]) % Wheels.WHEEL_SIZES[6];
        int wheelStepCamPos7 = (key.wheelPositions[7] + Wheels.WHEEL_STEP_CAM_POS[7]) % Wheels.WHEEL_SIZES[7];
        int wheelStepCamPos8 = (key.wheelPositions[8] + Wheels.WHEEL_STEP_CAM_POS[8]) % Wheels.WHEEL_SIZES[8];
        int wheelStepCamPos9 = (key.wheelPositions[9] + Wheels.WHEEL_STEP_CAM_POS[9]) % Wheels.WHEEL_SIZES[9];

        boolean lastZ = false;

        byte in, out;
        int wheelsBitmap, controlBitmap;

        System.out.printf("\n==================================================================================================\nModel: %s %s\n\n", key.model.toString(), key.ktf ? "WITH KTF" : "");

        System.out.print("Key in George notation is always A to K.\n");
        System.out.printf("%s %s %s\n\n", key.getWheelPositionsString(), key.getWheelSettingsString(), key.model.hasMessageKeyUnit() ? key.getWheelMsgSettingsString() : "");


        System.out.print("Format according to Geoff Sullivan's simulator:\n");
        System.out.print("Einstellungen is from A to K (careful!!) - in *.key file and 'Inner Key' dialog.\n");
        System.out.print("Grundstellung is from K to A - in *.key file, in main window and in 'Message Key' dialog.\n");
        System.out.print("All printouts below here follow Geoff Sullivan format\n");

        if (key.model.hasMessageKeyUnit()) {
            String[] msgKeyUnitDebug = MessageKeyUnit.msgKeyUnitOutputDebug(key);

            System.out.println();
            System.out.printf("Message key unit settings     %s (active: %s)\n", msgKeyUnitDebug[0], msgKeyUnitDebug[1]);
            System.out.printf("Before message key unit:      %s\n", msgKeyUnitDebug[2]);
            System.out.printf("After message key unit:       %s\n", msgKeyUnitDebug[3]);
            System.out.printf("After main key settings unit: %s\n", msgKeyUnitDebug[4]);
        }

        /*
        [T52d]
Einstellungen=6,8,1,2,5,7,III,4,10,IV,II,I,3,9,V
Grundstellung=12,49,36,1,36,8,45,9,34,57
         */
        System.out.println();
        System.out.printf("[%s]\nEinstellungen=", key.model.toString().toLowerCase().replaceAll("t", "T"));
        for (int w = 0; w < 10; w++) {
            System.out.printf("%s", key.wheelSettings[w].toString().replaceAll("-", ","));
            if (w != 9) {
                System.out.print(",");
            }
        }
        System.out.println();

        System.out.print("Grundstellung=");
        for (int w = 9; w >= 0; w--) {
            System.out.printf("%02d", wheelCurrentPositions[w] + 1);
            if (w != 0) {
                System.out.print(",");
            }
        }
        System.out.println();
        System.out.println();

        for (int i = 0; i < len; i++) {

            wheelsBitmap = Wheels.getWheelsBitmap(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
            controlBitmap = controlMapping[wheelsBitmap];

            in = input[i];
            out = alphabet[controlBitmap][in];
            output[i] = out;

            if (key.model.hasMessageKeyUnit()) {
                String[] msgKeyUnitDebug = MessageKeyUnit.msgKeyUnitOutputDebug(key);

                System.out.printf("Before message key unit:      %s\n", msgKeyUnitDebug[2]);
                System.out.printf("                              %s\n", TenBits.getString(wheelsBitmap));
                System.out.printf("After message key unit:       %s\n", msgKeyUnitDebug[3]);
                System.out.printf("                              %s\n", TenBits.getString(MessageKeyUnit.msgKeyUnitOutput(key, wheelsBitmap)));
                System.out.printf("After main key settings unit: %s\n", msgKeyUnitDebug[4]);
                System.out.printf("                              %s\n", TenBits.getString(controlBitmap));
            }


            if (key.model.hasSRLogic()) {

                byte xorSRin = TenBits.high(controlBitmap);
                byte permSRin = TenBits.low(controlBitmap);

                System.out.printf("\nSR Logic input:  (13579) = %s (I-II-III-IV-V) = %s\n", FiveBits.getString(permSRin), FiveBits.getString(xorSRin));
                controlBitmap = srLogic[controlBitmap];
            }

            byte xor = TenBits.high(controlBitmap);
            byte perm = TenBits.low(controlBitmap);
            byte interim = (byte) (in ^ xor);
            StringBuilder permString = permString(perms[perm]);


            if (key.model.hasSRLogic()) {
                System.out.printf("SR Logic output: (Perm)  = %s           (XOR) = %s\n\n", FiveBits.getString(perm), FiveBits.getString(xor));
            }

            System.out.print("Symbol           12345  Swedish(British)  \n");
            printChar("Plaintext:     ", in);
            printChar("Add:           ", xor);
            printChar("               ", interim);
            System.out.printf("                 %s =        (%s)\n", permString.toString(), FiveBits.getString(perm));
            printChar("Output:        ", out);

            boolean a = Wheels.PATTERNS0[wheelStepCamPos0];
            boolean b = Wheels.PATTERNS1[wheelStepCamPos1];
            boolean c = Wheels.PATTERNS2[wheelStepCamPos2];
            boolean d = Wheels.PATTERNS3[wheelStepCamPos3];
            boolean e = Wheels.PATTERNS4[wheelStepCamPos4];
            boolean f = Wheels.PATTERNS5[wheelStepCamPos5];
            boolean g = Wheels.PATTERNS6[wheelStepCamPos6];
            boolean h = Wheels.PATTERNS7[wheelStepCamPos7];
            boolean j = Wheels.PATTERNS8[wheelStepCamPos8];
            boolean k = Wheels.PATTERNS9[wheelStepCamPos9];

            boolean stepK = true;
            boolean stepJ = true;
            boolean stepH = true;
            boolean stepG = true;
            boolean stepF = true;
            boolean stepE = true;
            boolean stepD = true;
            boolean stepC = true;
            boolean stepB = true;
            boolean stepA = true;

            if (key.model.hasIrregularStepping()) {

                if (!key.ktf) {
                    stepK = e || !d;
                    stepJ = !a || k;
                    stepH = !k || j;
                    stepG = !j || !h;
                    stepF = h || g;
                    stepE = !g || f;
                    stepC = stepB = stepA = stepD = !f || !e;

                } else {
                    stepK = a || !b;
                    stepJ = !a || k;
                    stepH = !k || j;
                    stepG = !j || !h;
                    stepF = !lastZ || h || g;
                    stepE = !lastZ || !g || f;
                    stepD = !f || !e;
                    stepC = !d || e;
                    stepB = lastZ || !c || d;
                    stepA = lastZ || b || c;
                    if (out == -1) {
                        throw new RuntimeException("KTF modes does not allow unknown symbols");
                    }
                }
                System.out.println();
                System.out.printf("Cam:  K:%-5s, J:%-5s, H:%-5s, G:%-5s, F:%-5s, E:%-5s, D:%-5s, C:%-5s, B:%-5s, A:%-5s, LastZ : %-5s\n",
                        k, j, h, g, f, e, d, c, b, a, lastZ);
                System.out.printf("Move: K:%-5s, J:%-5s, H:%-5s, G:%-5s, F:%-5s, E:%-5s, D:%-5s, C:%-5s, B:%-5s, A:%-5s,\n",
                        stepK, stepJ, stepH, stepG, stepF, stepE, stepD, stepC, stepB, stepA);

                lastZ = FiveBits.isSet(2, in);
            }
            if (stepA) {
                wheelPos0 = (wheelPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelPos0 + 1;
                wheelStepCamPos0 = (wheelStepCamPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelStepCamPos0 + 1;
            }
            if (stepB) {
                wheelPos1 = (wheelPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelPos1 + 1;
                wheelStepCamPos1 = (wheelStepCamPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelStepCamPos1 + 1;
            }
            if (stepC) {
                wheelPos2 = (wheelPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelPos2 + 1;
                wheelStepCamPos2 = (wheelStepCamPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelStepCamPos2 + 1;
            }
            if (stepD) {
                wheelPos3 = (wheelPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelPos3 + 1;
                wheelStepCamPos3 = (wheelStepCamPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelStepCamPos3 + 1;
            }
            if (stepE) {
                wheelPos4 = (wheelPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelPos4 + 1;
                wheelStepCamPos4 = (wheelStepCamPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelStepCamPos4 + 1;
            }
            if (stepF) {
                wheelPos5 = (wheelPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelPos5 + 1;
                wheelStepCamPos5 = (wheelStepCamPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelStepCamPos5 + 1;
            }
            if (stepG) {
                wheelPos6 = (wheelPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelPos6 + 1;
                wheelStepCamPos6 = (wheelStepCamPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelStepCamPos6 + 1;
            }
            if (stepH) {
                wheelPos7 = (wheelPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelPos7 + 1;
                wheelStepCamPos7 = (wheelStepCamPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelStepCamPos7 + 1;
            }
            if (stepJ) {
                wheelPos8 = (wheelPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelPos8 + 1;
                wheelStepCamPos8 = (wheelStepCamPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelStepCamPos8 + 1;
            }
            if (stepK) {
                wheelPos9 = (wheelPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelPos9 + 1;
                wheelStepCamPos9 = (wheelStepCamPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelStepCamPos9 + 1;
            }

            updateCurrentPositions(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);

            for (int w = 9; w >= 0; w--) {
                System.out.printf("%02d", wheelCurrentPositions[w] + 1);
                if (w != 0) {
                    System.out.print(",");
                }
            }
            System.out.println();
            System.out.println();
        }
        System.out.printf("Plaintext:  Swedish notation:  %s\n", Alphabet.toString(input));
        System.out.printf("            British notation:  %s\n", Alphabet.toStringBritish(input));
        System.out.printf("Ciphertext: Swedish notation:  %s\n", Alphabet.toString(output));
        System.out.printf("            British notation:  %s\n", Alphabet.toStringBritish(output));

    }
    void moveStatsDE(int len, int[][] stats, int window) {
        if (!key.model.hasIrregularStepping()) {
            throw new RuntimeException("No support for " + key.model);
        }
        for (int w = 0; w < 10; w++) {
            stats[w] = new int[(int) Math.pow(2, window)];
        }

        int wheelPos0 = key.wheelPositions[0];
        int wheelPos1 = key.wheelPositions[1];
        int wheelPos2 = key.wheelPositions[2];
        int wheelPos3 = key.wheelPositions[3];
        int wheelPos4 = key.wheelPositions[4];
        int wheelPos5 = key.wheelPositions[5];
        int wheelPos6 = key.wheelPositions[6];
        int wheelPos7 = key.wheelPositions[7];
        int wheelPos8 = key.wheelPositions[8];
        int wheelPos9 = key.wheelPositions[9];


        int wheelStepCamPos0 = (wheelPos0 + Wheels.WHEEL_STEP_CAM_POS[0]) % Wheels.WHEEL_SIZES[0];
        int wheelStepCamPos1 = (wheelPos1 + Wheels.WHEEL_STEP_CAM_POS[1]) % Wheels.WHEEL_SIZES[1];
        int wheelStepCamPos2 = (wheelPos2 + Wheels.WHEEL_STEP_CAM_POS[2]) % Wheels.WHEEL_SIZES[2];
        int wheelStepCamPos3 = (wheelPos3 + Wheels.WHEEL_STEP_CAM_POS[3]) % Wheels.WHEEL_SIZES[3];
        int wheelStepCamPos4 = (wheelPos4 + Wheels.WHEEL_STEP_CAM_POS[4]) % Wheels.WHEEL_SIZES[4];
        int wheelStepCamPos5 = (wheelPos5 + Wheels.WHEEL_STEP_CAM_POS[5]) % Wheels.WHEEL_SIZES[5];
        int wheelStepCamPos6 = (wheelPos6 + Wheels.WHEEL_STEP_CAM_POS[6]) % Wheels.WHEEL_SIZES[6];
        int wheelStepCamPos7 = (wheelPos7 + Wheels.WHEEL_STEP_CAM_POS[7]) % Wheels.WHEEL_SIZES[7];
        int wheelStepCamPos8 = (wheelPos8 + Wheels.WHEEL_STEP_CAM_POS[8]) % Wheels.WHEEL_SIZES[8];
        int wheelStepCamPos9 = (wheelPos9 + Wheels.WHEEL_STEP_CAM_POS[9]) % Wheels.WHEEL_SIZES[9];

        int[] moveBitMap = new int[10];

        boolean[][] stopStop = new boolean[10][10];
        boolean lastZ = false;
        for (int i = 0; i < len; i++) {

            boolean a = Wheels.PATTERNS0[wheelStepCamPos0];
            boolean b = Wheels.PATTERNS1[wheelStepCamPos1];
            boolean c = Wheels.PATTERNS2[wheelStepCamPos2];
            boolean d = Wheels.PATTERNS3[wheelStepCamPos3];
            boolean e = Wheels.PATTERNS4[wheelStepCamPos4];
            boolean f = Wheels.PATTERNS5[wheelStepCamPos5];
            boolean g = Wheels.PATTERNS6[wheelStepCamPos6];
            boolean h = Wheels.PATTERNS7[wheelStepCamPos7];
            boolean j = Wheels.PATTERNS8[wheelStepCamPos8];
            boolean k = Wheels.PATTERNS9[wheelStepCamPos9];

            boolean stepK;
            boolean stepJ = !a || k;
            boolean stepH = !k || j;
            boolean stepG = !j || !h;
            boolean stepF;
            boolean stepE;
            boolean stepD = !f || !e;
            boolean stepC;
            boolean stepB;
            boolean stepA;

            if (!key.ktf) {
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

                lastZ = CommonRandom.r.nextBoolean();
            }

            boolean[] move = {stepA, stepB, stepC, stepD, stepE, stepF, stepG, stepH, stepJ, stepK};

            for (int w1 = 0; w1 < 10; w1++) {
                for (int w2 = w1 + 1; w2 < 10; w2++) {
                    if (!move[w1] && !move[w2]) {
                        stopStop[w1][w2] = stopStop[w2][w1] = true;
                    }
                }
            }
            for (int w = 0; w < 10; w++) {
                moveBitMap[w] <<= 1;
                moveBitMap[w] &= ~(0x1 << window);
            }

            if (stepA) {
                wheelPos0 = (wheelPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelPos0 + 1;
                wheelStepCamPos0 = (wheelStepCamPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelStepCamPos0 + 1;
                moveBitMap[Wheels.A] |= 0x1;
            }
            if (stepB) {
                wheelPos1 = (wheelPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelPos1 + 1;
                wheelStepCamPos1 = (wheelStepCamPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelStepCamPos1 + 1;
                moveBitMap[Wheels.B] |= 0x1;
            }
            if (stepC) {
                wheelPos2 = (wheelPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelPos2 + 1;
                wheelStepCamPos2 = (wheelStepCamPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelStepCamPos2 + 1;
                moveBitMap[Wheels.C] |= 0x1;
            }
            if (stepD) {
                wheelPos3 = (wheelPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelPos3 + 1;
                wheelStepCamPos3 = (wheelStepCamPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelStepCamPos3 + 1;
                moveBitMap[Wheels.D] |= 0x1;
            }

            if (stepE) {
                wheelPos4 = (wheelPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelPos4 + 1;
                wheelStepCamPos4 = (wheelStepCamPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelStepCamPos4 + 1;
                moveBitMap[Wheels.E] |= 0x1;
            }
            if (stepF) {
                wheelPos5 = (wheelPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelPos5 + 1;
                wheelStepCamPos5 = (wheelStepCamPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelStepCamPos5 + 1;
                moveBitMap[Wheels.F] |= 0x1;
            }
            if (stepG) {
                wheelPos6 = (wheelPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelPos6 + 1;
                wheelStepCamPos6 = (wheelStepCamPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelStepCamPos6 + 1;
                moveBitMap[Wheels.G] |= 0x1;
            }
            if (stepH) {
                wheelPos7 = (wheelPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelPos7 + 1;
                wheelStepCamPos7 = (wheelStepCamPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelStepCamPos7 + 1;
                moveBitMap[Wheels.H] |= 0x1;
            }
            if (stepJ) {
                wheelPos8 = (wheelPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelPos8 + 1;
                wheelStepCamPos8 = (wheelStepCamPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelStepCamPos8 + 1;
                moveBitMap[Wheels.J] |= 0x1;
            }
            if (stepK) {
                wheelPos9 = (wheelPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelPos9 + 1;
                wheelStepCamPos9 = (wheelStepCamPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelStepCamPos9 + 1;
                moveBitMap[Wheels.K] |= 0x1;
            }
            if (len >= window) {
                for (int w = 0; w < 10; w++) {
                    stats[w][moveBitMap[w]]++;
                }
            }
        }
        for (int w = 0; w < 10; w++) {
            System.out.printf("%d %10d %f\n", w, stats[w][0], 1. * stats[w][0] / len);
        }

        for (int w1 = 0; w1 < 10; w1++) {
            System.out.printf("%s: ", "ABCDEFGHJK".charAt(w1));
            for (int w2 = 0; w2 < 10; w2++) {
                if (w1 == w2) {
                    continue;
                }
                if (!stopStop[w1][w2]) {
                    System.out.printf("%s", "ABCDEFGHJK".charAt(w2));
                }
            }
            System.out.printf("\n");
        }

        return;
    }
    public void cycles(int len) {

        int wheelPos0 = key.wheelPositions[0];
        int wheelPos1 = key.wheelPositions[1];
        int wheelPos2 = key.wheelPositions[2];
        int wheelPos3 = key.wheelPositions[3];
        int wheelPos4 = key.wheelPositions[4];
        int wheelPos5 = key.wheelPositions[5];
        int wheelPos6 = key.wheelPositions[6];
        int wheelPos7 = key.wheelPositions[7];
        int wheelPos8 = key.wheelPositions[8];
        int wheelPos9 = key.wheelPositions[9];

        int wheelStepCamPos0 = (key.wheelPositions[0] + Wheels.WHEEL_STEP_CAM_POS[0]) % Wheels.WHEEL_SIZES[0];
        int wheelStepCamPos1 = (key.wheelPositions[1] + Wheels.WHEEL_STEP_CAM_POS[1]) % Wheels.WHEEL_SIZES[1];
        int wheelStepCamPos2 = (key.wheelPositions[2] + Wheels.WHEEL_STEP_CAM_POS[2]) % Wheels.WHEEL_SIZES[2];
        int wheelStepCamPos3 = (key.wheelPositions[3] + Wheels.WHEEL_STEP_CAM_POS[3]) % Wheels.WHEEL_SIZES[3];
        int wheelStepCamPos4 = (key.wheelPositions[4] + Wheels.WHEEL_STEP_CAM_POS[4]) % Wheels.WHEEL_SIZES[4];
        int wheelStepCamPos5 = (key.wheelPositions[5] + Wheels.WHEEL_STEP_CAM_POS[5]) % Wheels.WHEEL_SIZES[5];
        int wheelStepCamPos6 = (key.wheelPositions[6] + Wheels.WHEEL_STEP_CAM_POS[6]) % Wheels.WHEEL_SIZES[6];
        int wheelStepCamPos7 = (key.wheelPositions[7] + Wheels.WHEEL_STEP_CAM_POS[7]) % Wheels.WHEEL_SIZES[7];
        int wheelStepCamPos8 = (key.wheelPositions[8] + Wheels.WHEEL_STEP_CAM_POS[8]) % Wheels.WHEEL_SIZES[8];
        int wheelStepCamPos9 = (key.wheelPositions[9] + Wheels.WHEEL_STEP_CAM_POS[9]) % Wheels.WHEEL_SIZES[9];


        Map<String, Long> lastPosition = new TreeMap<>();
        for (long i = 0; i < len; i++) {


            boolean a = Wheels.PATTERNS0[wheelStepCamPos0];
            boolean b = Wheels.PATTERNS1[wheelStepCamPos1];
            boolean c = Wheels.PATTERNS2[wheelStepCamPos2];
            boolean d = Wheels.PATTERNS3[wheelStepCamPos3];
            boolean e = Wheels.PATTERNS4[wheelStepCamPos4];
            boolean f = Wheels.PATTERNS5[wheelStepCamPos5];
            boolean g = Wheels.PATTERNS6[wheelStepCamPos6];
            boolean h = Wheels.PATTERNS7[wheelStepCamPos7];
            boolean j = Wheels.PATTERNS8[wheelStepCamPos8];
            boolean k = Wheels.PATTERNS9[wheelStepCamPos9];

            boolean stepK;
            boolean stepJ = !a || k;
            boolean stepH = !k || j;
            boolean stepG = !j || !h;
            boolean stepF;
            boolean stepE;
            boolean stepD = !f || !e;
            boolean stepC;
            boolean stepB;
            boolean stepA;

            stepK = e || !d;
            stepF = h || g;
            stepE = !g || f;
            stepC = stepB = stepA = stepD;

            if (stepA) {
                wheelPos0 = (wheelPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelPos0 + 1;
                wheelStepCamPos0 = (wheelStepCamPos0 == Wheels.WHEEL_SIZE_0_M_1) ? 0 : wheelStepCamPos0 + 1;
            }
            if (stepB) {
                wheelPos1 = (wheelPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelPos1 + 1;
                wheelStepCamPos1 = (wheelStepCamPos1 == Wheels.WHEEL_SIZE_1_M_1) ? 0 : wheelStepCamPos1 + 1;
            }
            if (stepC) {
                wheelPos2 = (wheelPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelPos2 + 1;
                wheelStepCamPos2 = (wheelStepCamPos2 == Wheels.WHEEL_SIZE_2_M_1) ? 0 : wheelStepCamPos2 + 1;
            }
            if (stepD) {
                wheelPos3 = (wheelPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelPos3 + 1;
                wheelStepCamPos3 = (wheelStepCamPos3 == Wheels.WHEEL_SIZE_3_M_1) ? 0 : wheelStepCamPos3 + 1;
            }
            if (stepE) {
                wheelPos4 = (wheelPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelPos4 + 1;
                wheelStepCamPos4 = (wheelStepCamPos4 == Wheels.WHEEL_SIZE_4_M_1) ? 0 : wheelStepCamPos4 + 1;
            }
            if (stepF) {
                wheelPos5 = (wheelPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelPos5 + 1;
                wheelStepCamPos5 = (wheelStepCamPos5 == Wheels.WHEEL_SIZE_5_M_1) ? 0 : wheelStepCamPos5 + 1;
            }
            if (stepG) {
                wheelPos6 = (wheelPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelPos6 + 1;
                wheelStepCamPos6 = (wheelStepCamPos6 == Wheels.WHEEL_SIZE_6_M_1) ? 0 : wheelStepCamPos6 + 1;
            }
            if (stepH) {
                wheelPos7 = (wheelPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelPos7 + 1;
                wheelStepCamPos7 = (wheelStepCamPos7 == Wheels.WHEEL_SIZE_7_M_1) ? 0 : wheelStepCamPos7 + 1;
            }
            if (stepJ) {
                wheelPos8 = (wheelPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelPos8 + 1;
                wheelStepCamPos8 = (wheelStepCamPos8 == Wheels.WHEEL_SIZE_8_M_1) ? 0 : wheelStepCamPos8 + 1;
            }
            if (stepK) {
                wheelPos9 = (wheelPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelPos9 + 1;
                wheelStepCamPos9 = (wheelStepCamPos9 == Wheels.WHEEL_SIZE_9_M_1) ? 0 : wheelStepCamPos9 + 1;
            }

            //String s = String.format("%02d:%02d:%02d:%02d:%02d:%02d:%02d:%02d:%02d:%02d", wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
            //String s = String.format("%02d:%02d:%02d:%02d:%02d:%02d", wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
            String s = String.format("%02d:%02d:%02d:%02d", wheelPos4, wheelPos5, wheelPos6, wheelPos7 );

            long last = lastPosition.getOrDefault(s, -1L);
            lastPosition.put(s, i);
            if (last != -1L){
                //System.out.printf("%,12d %,12d %,12d %s\n", i - last, 73L * 71  * 69 * 67 * 65 * 64 * 61 * 59 * 53 * 47, i, s);
                System.out.printf("%,12d %,12d %,12d %s\n", i - last, (long) 65 * 64 * 61 * 59 , i, s);

            }


        }
    }

    void computeClassFreqRef(double[] languageFreq, WheelFunctionSet wheelFunctionSet) {

        if (wheelFunctionSet.containsOnlyXor()) {
            class32.computeFreqRef(this, languageFreq, wheelFunctionSet);
        } else {
            class1024.computeFreqRef(this, languageFreq, wheelFunctionSet);
        }
    }
    double classScore(byte[][] cipherArray, WheelFunctionSet wheelFunctionSet, boolean ic) {
        if (key.model.hasIrregularStepping()) {
            throw new RuntimeException("not relevant for T52D or E");
        }
        if (wheelFunctionSet.containsOnlyXor()) {
            return ic ? class32.ic(this, cipherArray, wheelFunctionSet) : class32.score(this, cipherArray, wheelFunctionSet);
        }
        return ic ? class1024.ic(this, cipherArray, wheelFunctionSet) : class1024.score(this, cipherArray, wheelFunctionSet);
    }

    int getValidWheelBitmaps(byte[][] input, byte[][] output, int cribPosition, int[][] validWheelBitmaps, int origWheelsBitmap, int origControl, boolean print) {

        int validWheelBitmapsCounts = 0;
        int depth = Math.min(input.length, output.length);

        StringBuilder bitmapString = new StringBuilder();

        byte in, out;
        int[] controls = new int[TenBits.SPACE];
        for (int wheelBitmap = 0; wheelBitmap < TenBits.SPACE; wheelBitmap++) {
            boolean bmGood = true;
            for (int d = 0; d < depth; d++) {
                if (input[d] == null || output[d] == null) {
                    continue;
                }
                if (cribPosition >= input[d].length || cribPosition >= output[d].length) {
                    continue;
                }
                in = input[d][cribPosition];
                out = output[d][cribPosition];
                if (in == -1 || out == -1) {
                    continue;
                }
                if (alphabet[controlMapping[wheelBitmap]][in] != out) {
                    bmGood = false;
                    break;
                }
            }
            if (bmGood) {
                if (print) {
                    bitmapString.append(TenBits.getString(wheelBitmap)).append(" ");
                }
                controls[validWheelBitmapsCounts++] = wheelBitmap;
            }
        }

        validWheelBitmaps[cribPosition] = Arrays.copyOf(controls, validWheelBitmapsCounts);
        if (print) {

            if (!key.model.hasSRLogic()) {

                boolean[] xs = new boolean[FiveBits.SPACE];
                boolean[] ps = new boolean[FiveBits.SPACE];
                for (int v : validWheelBitmaps[cribPosition]) {
                    int x = 0, xi = 0;
                    int p = 0, pi = 0;
                    for (int w = 0; w < 10; w++) {
                        boolean state = TenBits.isSet(w, v);
                        if (key.wheelSettings[w].getFunction() == WheelSetting.WheelFunction.XOR) {
                            x = FiveBits.setBit(xi++, state, x);
                        } else {
                            p = FiveBits.setBit(pi++, state, p);
                        }
                    }
                    xs[x] = true;
                    ps[p] = true;
                }
                int cx = 0;
                for (boolean xi : xs) {
                    cx += xi ? 1 : 0;
                }
                int cp = 0;
                for (boolean pi : ps) {
                    cp += pi ? 1 : 0;
                }

                System.out.printf("[%3d] [%s => %s] <I:%s O:%s> <All: %3d Xor: %2d Perm: %2d> <%s>\n",
                        cribPosition,
                        FiveBits.getString(input[0][cribPosition]),
                        FiveBits.getString(output[0][cribPosition]),
                        TenBits.getString(origWheelsBitmap),
                        TenBits.getString(origControl),
                        validWheelBitmapsCounts, cx, cp,
                        bitmapString.toString());
            } else {
                System.out.printf("[%3d] [%s => %s] <I:%s O:%s> <All: %3d> <%s>\n",
                        cribPosition,
                        FiveBits.getString(input[0][cribPosition]),
                        FiveBits.getString(output[0][cribPosition]),
                        TenBits.getString(origWheelsBitmap),
                        TenBits.getString(origControl),
                        validWheelBitmapsCounts,
                        bitmapString.toString());
            }
        }
        return validWheelBitmapsCounts;
    }
    boolean[] getDepthOptions(byte[][] plainArray, byte[][] cipherArray, int cribPosition, int index) {

        boolean[] valid = new boolean[FiveBits.SPACE];
        int depth = Math.min(plainArray.length, cipherArray.length);

        for (int wheelBitmap = 0; wheelBitmap < TenBits.SPACE; wheelBitmap++) {
            boolean bmGood = true;
            for (int d = 0; d < depth; d++) {
                if (d == index) {
                    continue;
                }
                if (plainArray[d] == null || cipherArray[d] == null) {
                    continue;
                }
                if (cribPosition >= plainArray[d].length || cribPosition >= cipherArray[d].length) {
                    continue;
                }
                if (cribPosition >= plainArray[index].length || cribPosition >= cipherArray[index].length) {
                    continue;
                }
                byte plain = plainArray[d][cribPosition];
                byte cipher = cipherArray[d][cribPosition];
                if (plain == -1 || cipher == -1) {
                    continue;
                }
                if (alphabet[wheelBitmap][plain] != cipher) {
                    bmGood = false;
                    break;
                }
            }
            if (bmGood) {
                valid[inverseAlphabet[wheelBitmap][cipherArray[index][cribPosition]]] = true;
            }
        }
        return valid;
    }
    boolean[] checkDepth(byte[][] plainArray, byte[][] cipherArray, int except) {

        int len = 0;
        for (int d = 0; d < plainArray.length; d++) {
            if (plainArray[d] == null || cipherArray[d] == null) {
                continue;
            }
            len = Math.max(Math.min(plainArray[d].length, cipherArray[d].length), len);
        }

        boolean[] validPositions = new boolean[len];
        int depth = Math.min(plainArray.length, cipherArray.length);

        for (int cribPosition = 0; cribPosition < len; cribPosition++) {
            for (int wheelBitmap = 0; wheelBitmap < TenBits.SPACE; wheelBitmap++) {
                boolean bmGood = true;
                for (int d = 0; d < depth; d++) {
                    if (plainArray[d] == null || cipherArray[d] == null) {
                        continue;
                    }

                    if (d == except) {
                        continue;
                    }
                    if (cribPosition >= plainArray[d].length || cribPosition >= cipherArray[d].length) {
                        continue;
                    }
                    byte plain = plainArray[d][cribPosition];
                    byte cipher = cipherArray[d][cribPosition];
                    if (plain == -1 || cipher == -1) {
                        continue;
                    }
                    if (alphabet[wheelBitmap][plain] != cipher) {
                        bmGood = false;
                        break;
                    }
                }
                if (bmGood) {
                    validPositions[cribPosition] = true;
                }
            }
        }
        return validPositions;
    }
    private static void moveStats() {

        Sturgeon sturgeon = new Sturgeon(Model.T52D);
        sturgeon.key.ktf = false;
        sturgeon.randomize();
        int[][] moveStats = new int[10][];
        sturgeon.moveStatsDE(10000000, moveStats, 4);
    }

    ArrayList<Integer> uniqueControls() {

        ArrayList<Integer> uniqueControls = new ArrayList<>();
        Set<String> alphabetSet = new TreeSet<>();
        int[][] counts = new int[FiveBits.SPACE][FiveBits.SPACE];
        for (int control = 0; control < TenBits.SPACE; control++) {
            StringBuilder s = new StringBuilder();
            for (byte input = 0; input < FiveBits.SPACE; input++) {
                byte output = alphabet[control][input];
                s.append("<").append(output).append(">");
                counts[input][output]++;
            }
            if (alphabetSet.contains(s.toString())) {
                continue;
            }
            alphabetSet.add(s.toString());
            uniqueControls.add(control);
        }

        return uniqueControls;


    }

    final private static boolean[][][][] affects = affects();
    private static boolean[][][][] affects() {
        boolean[][][][] affects = new boolean[2][2][10][10];
        for (boolean ktf : new boolean[] {false, true} ) {
            for (boolean firstWheelCamState : new boolean[] {false, true} ) {
                for (int firstWheel = 0; firstWheel < 10; firstWheel++) {
                    for (int secondWheel = 0; secondWheel < 10; secondWheel++) {
                        affects[ktf ? 1 : 0][firstWheelCamState ? 1 : 0][firstWheel][secondWheel] =
                                firstWheelCamForcesSecondWheelStepping(ktf, firstWheel, firstWheelCamState, secondWheel);
                    }
                }
            }
        }
        return affects;
    }
    static boolean firstWheelCamForcesSecondWheelSteppingCached(boolean ktf, int firstWheel, boolean firstWheelCamState, int secondWheel) {
        return affects[ktf ? 1 : 0][firstWheelCamState ? 1 : 0][firstWheel][secondWheel];
    }
    private static boolean firstWheelCamForcesSecondWheelStepping(boolean ktf, int firstWheel, boolean firstWheelCamState, int secondWheel) {

        switch (secondWheel) {

            case Wheels.D:
                //return !f || !e;
                return (!firstWheelCamState && (firstWheel == Wheels.F)) || (!firstWheelCamState && (firstWheel == Wheels.E));
            case Wheels.E:
                //return !g || f;
                return (!firstWheelCamState && (firstWheel == Wheels.G)) || (firstWheelCamState && (firstWheel == Wheels.F));
            case Wheels.F:
                //return h || g;
                return (firstWheelCamState && (firstWheel == Wheels.H)) || (firstWheelCamState && (firstWheel == Wheels.G));
            case Wheels.G:
                //return !j || !h;
                return (!firstWheelCamState && (firstWheel == Wheels.J)) || (!firstWheelCamState && (firstWheel == Wheels.H));
            case Wheels.H:
                //return !k || j;
                return (!firstWheelCamState && (firstWheel == Wheels.K)) || (firstWheelCamState && (firstWheel == Wheels.J));
            case Wheels.J:
                //return k || !a;
                return (firstWheelCamState && (firstWheel == Wheels.K)) || (!firstWheelCamState && (firstWheel == Wheels.A));
            default:
                break;
        }

        if (!ktf) {
            switch (secondWheel) {
                case Wheels.A:
                case Wheels.B:
                case Wheels.C:
                    //return !f || !e;
                    return (!firstWheelCamState && (firstWheel == Wheels.F)) || (!firstWheelCamState && (firstWheel == Wheels.E));
                case Wheels.K:
                    //return e || !d;
                    return (firstWheelCamState && (firstWheel == Wheels.E)) || (!firstWheelCamState && (firstWheel == Wheels.D));
                default:
                    throw new RuntimeException("Invalid second " + secondWheel);
            }
        } else {

            switch (secondWheel) {
                case Wheels.A:
                    //return b || c;
                    return (firstWheelCamState && (firstWheel == Wheels.B)) || (firstWheelCamState && (firstWheel == Wheels.C));
                case Wheels.B:
                    //return !c || d;
                    return (!firstWheelCamState && (firstWheel == Wheels.C)) || (firstWheelCamState && (firstWheel == Wheels.D));
                case Wheels.C:
                    //return !d || e;
                    return (!firstWheelCamState && (firstWheel == Wheels.D)) || (firstWheelCamState && (firstWheel == Wheels.E));
                case Wheels.K:
                    //return a || !b;
                    return (firstWheelCamState && (firstWheel == Wheels.A)) || (!firstWheelCamState && (firstWheel == Wheels.B));
                default:
                    throw new RuntimeException("Invalid second " + secondWheel);
            }
        }

    }

    private void printChar(String name, byte in) {
        char sw = Alphabet.ALPHABET_LETTERS_ARRAY[in];
        char bp = Alphabet.BRITISH_ALPHABET_LETTERS_ARRAY[in];
        if (sw != bp) {
            System.out.printf("%s  %s =  %c       (%c)\n", name, FiveBits.getString(in), sw, bp);
        } else {
            System.out.printf("%s  %s =  %c\n", name, FiveBits.getString(in), sw);
        }
    }
    private StringBuilder permString(int[] perm) {
        StringBuilder permString;
        permString = new StringBuilder();
        for (int k = 0; k < 5; k++) {
            permString.append(perm[k] + 1);
        }
        return permString;
    }
    private static void testGeoffSimulator() {
        //String plainString = "HELLOTHISISTHEFIRSTTESTMESSAGE";
        String plainString = "ACACACACACACACACACACACAC";

        byte[] plain = Alphabet.fromStringBritish(plainString);


        System.out.printf("%s\n", plainString);
        System.out.printf("%s\n", Alphabet.toStringBritish(plain));
        byte[] cipher = new byte[plain.length];


        Key keySullivanD = new Key("T52D",
                "12,49,36,1,36,8,45,9,34,57",
                "6-8:1-2:5-7:III:4-10:IV:II:I:3-9:V");
        Sturgeon sturgeon = new Sturgeon(keySullivanD);
        sturgeon.key.ktf = true;
        //decryptSturgeon.randomize();
        sturgeon.key.print("simulation");
        sturgeon.debugEncrypt(plain, cipher, plain.length);

        byte[][] p = {plain};
        byte[][] c = {cipher};
        boolean[][] moves = new boolean[10][plain.length];
        sturgeon.encryptDepth(p, c, plain.length, moves, true);

        System.out.printf("%s\n", Alphabet.toStringBritish(plain));
        System.out.printf("%s\n", Alphabet.toStringBritish(cipher));

        //CribAttack.solve(sturgeon, p, c, moves, true);
    }
    private static void testForGeoff() {
        String plainString = "3335MBBL53335Q";
        //String plainString = "3335MBBL53335QRV54BB3555MBBL5QRV35QTC35LANG4B35333333IS335NEIN53333333333333333333333354Z3KR5MNKP533334QPE5U5WWXO5QEPP4V";

        byte[] plain = Alphabet.fromString(plainString);
        byte[] cipher = new byte[plain.length];

        String positions = "09:01:06:41:36:46:31:25:16:06";
        String wheels13579 = "IV:1:7:II:3:9:III:V:5:I";
        String wheels = "IV:1-2:7-8:II:3-4:9-10:III:V:5-6:I";
        //String wheels = SturgeonStatic.DEFAULT_PROGRAMMABLE_PERM_AND_XOR_WHEELS;
        Key keyab = new Key("T52AB", positions, wheels);
        Key keyc = new Key("T52C", positions, wheels13579, "UWXYZ");
        Key keyca = new Key("T52CA", positions, wheels13579, "UTPYW");
        Key keyd = new Key("T52D", positions, wheels);
        Key keye = new Key("T52E", positions, wheels13579);

        Sturgeon sturgeon;

        sturgeon = new Sturgeon(keyab);
        sturgeon.debugEncrypt(plain, cipher, plain.length);

        sturgeon = new Sturgeon(keyc);
        //sturgeon.debugEncrypt(plain, cipher, plain.length);

        sturgeon = new Sturgeon(keyca);
        //sturgeon.debugEncrypt(plain, cipher, plain.length);

        sturgeon = new Sturgeon(keyd);
        //sturgeon.debugEncrypt(plain, cipher, plain.length);

        sturgeon.key.ktf = true;
        //sturgeon.debugEncrypt(plain, cipher, plain.length);

        sturgeon = new Sturgeon(keye);
        //sturgeon.debugEncrypt(plain, cipher, plain.length);

        sturgeon.key.ktf = true;
        //sturgeon.debugEncrypt(plain, cipher, plain.length);
    }


    public static void main(String[] args) {
        //testGeoffSimulator();
        //moveStats();
        testForGeoff();
    }

}
