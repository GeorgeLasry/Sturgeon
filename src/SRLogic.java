public class SRLogic {
    private static final boolean _X_ = true;
    private static final boolean ___ = false;
    // SR logic for T52c
    private static final boolean[][] SR_T52C = {
            /*           1     3   5    7    9     I   II   III  IV   V */
            /* SR01 */ {_X_, _X_, ___, ___, ___, _X_, ___, _X_, ___, ___},
            /* SR02 */ {___, _X_, _X_, ___, ___, ___, _X_, ___, _X_, ___},
            /* SR03 */ {___, ___, _X_, _X_, ___, ___, ___, _X_, ___, _X_},
            /* SR04 */ {___, ___, ___, _X_, _X_, _X_, ___, ___, _X_, ___},
            /* SR05 */ {_X_, ___, ___, ___, _X_, ___, _X_, ___, ___, _X_},
            //----------------------------------|-------------------------
            /* SR06 */ {___, ___, _X_, _X_, ___, ___, _X_, ___, _X_, ___},
            /* SR07 */ {___, _X_, _X_, ___, ___, ___, ___, _X_, ___, _X_},
            /* SR08 */ {_X_, _X_, ___, ___, ___, ___, _X_, ___, ___, _X_},
            /* SR09 */ {_X_, ___, ___, ___, _X_, _X_, ___, _X_, ___, ___},
            /* SR10 */ {___, ___, ___, _X_, _X_, _X_, ___, ___, _X_, ___},
    };
    private static final int[] PRECOMPUTED_SR_LOGIC_T52C = precomputeSRLogic(SR_T52C, "SR for T52C");
    // SR logic for T52ca
    private static final boolean[][] SR_T52CA = {
            /*           1     3   5    7    9     I   II   III  IV   V */
            /* SR01 */ {___, ___, ___, _X_, _X_, _X_, ___, ___, _X_, ___},
            /* SR02 */ {_X_, _X_, _X_, _X_, ___, ___, ___, ___, ___, ___},
            /* SR03 */ {___, ___, ___, _X_, ___, _X_, _X_, ___, ___, _X_},
            /* SR04 */ {_X_, ___, ___, ___, _X_, ___, _X_, ___, _X_, ___},
            /* SR05 */ {_X_, ___, ___, ___, _X_, ___, ___, _X_, ___, _X_},
            //----------------------------------|-------------------------
            /* SR06 */ {_X_, _X_, ___, ___, ___, ___, _X_, ___, ___, _X_},
            /* SR07 */ {___, ___, ___, ___, ___, _X_, _X_, _X_, _X_, ___},
            /* SR08 */ {___, _X_, _X_, ___, _X_, ___, ___, ___, _X_, ___},
            /* SR09 */ {___, ___, _X_, _X_, ___, _X_, ___, _X_, ___, ___},
            /* SR10 */ {___, _X_, _X_, ___, ___, ___, ___, _X_, ___, _X_},
    };
    private static final int[] PRECOMPUTED_SR_LOGIC_T52CA = precomputeSRLogic(SR_T52CA, "SR for T52CA");
    // SR logic for T52e - SR01-05 are for permutations; SR06-10 for XOR.
    private static final boolean[][] SR_T52E = {
            /*           1     3   5    7    9     I   II   III  IV   V */
            /* SR01 */ {_X_, _X_, ___, ___, ___, ___, ___, _X_, ___, _X_},
            /* SR02 */ {___, ___, ___, ___, ___, _X_, _X_, _X_, _X_, ___},
            /* SR03 */ {___, ___, _X_, _X_, ___, ___, ___, _X_, ___, _X_},
            /* SR04 */ {_X_, _X_, _X_, ___, ___, ___, _X_, ___, ___, ___},
            /* SR05 */ {_X_, ___, ___, _X_, _X_, ___, _X_, ___, ___, ___},
            //----------------------------------|-------------------------
            /* SR06 */ {___, ___, _X_, _X_, ___, ___, _X_, ___, _X_, ___},
            /* SR07 */ {___, _X_, _X_, ___, _X_, ___, ___, ___, ___, _X_},
            /* SR08 */ {_X_, _X_, ___, ___, ___, _X_, ___, ___, _X_, ___},
            /* SR09 */ {___, ___, ___, ___, _X_, _X_, ___, _X_, ___, _X_},
            /* SR10 */ {___, ___, ___, _X_, _X_, _X_, ___, ___, _X_, ___},
    };
    private static final int[] PRECOMPUTED_SR_LOGIC_T52E = precomputeSRLogic(SR_T52E, "SR for T52E");

    private static int applySRLogic(boolean[][] srLogic, int control) {
        byte permControl = 0;
        byte xorControl = 0;

        for (int sr = 0; sr < 5; sr++) {
            if (isSRLogicActive(srLogic[sr], control)) {
                permControl |= FiveBits.BITS[sr];
            }
        }
        for (int sr = 5; sr < srLogic.length; sr++) {
            if (isSRLogicActive(srLogic[sr], control)) {
                xorControl |= FiveBits.BITS[sr - 5];
            }
        }
        return TenBits.bitmap(permControl, xorControl);
    }

    private static boolean isSRLogicActive(boolean[] srLogicEntry, int control) {
        boolean active = false;
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            if (srLogicEntry[w] && TenBits.isSet(w, control)) {
                active ^= true;
            }
        }
        return active;
    }

    private static int[] precomputeSRLogic(boolean[][] srLogic, String desc) {
        int[] precomputedSRLogic = new int[TenBits.SPACE];
        for (int control = 0; control < TenBits.SPACE; control++) {
            precomputedSRLogic[control] = applySRLogic(srLogic, control);
        }
        //SRLogicStats(srLogic, desc);
        return precomputedSRLogic;
    }

    private static void SRLogicStats(boolean[][] srLogic, String desc) {
        int[] xors = new int[FiveBits.SPACE];
        int[] perms = new int[FiveBits.SPACE];
        for (int input = 0; input < TenBits.SPACE; input++) {
            int output = applySRLogic(srLogic, input);
            xors[TenBits.high(output)]++;
            perms[TenBits.low(output)]++;
        }

        System.out.printf("SR Logic stats for %s\n", desc);
        for (int perm = 0; perm < FiveBits.SPACE; perm++) {
            if (perms[perm] > 0) {
                System.out.printf("Perm output %s = %2d\n", FiveBits.getString(perm), perms[perm]);
            }
        }
        for (int xor = 0; xor < FiveBits.SPACE; xor++) {
            if (perms[xor] > 0) {
                System.out.printf("Xor  output %s = %2d\n", FiveBits.getString(xor), xors[xor]);
            }
        }
    }

    static void getSRWheels(Key key, int sr, int[] srWheels) {
        boolean[] srLogic;
        if (key.model == Model.T52C) {
            srLogic = SR_T52C[sr];
        } else if (key.model == Model.T52CA) {
            srLogic = SR_T52CA[sr];
        } else if (key.model == Model.T52E || key.model == Model.T52EE) {
            srLogic = SR_T52E[sr];
        } else {
            return;
        }
        int count = 0;
        for (int w = 0; w < 10; w++) {
            if (srLogic[w]) {
                srWheels[count++] = w;
            }
        }
    }

    static int[] precomputedSRLogic(Key key) {
        int[] precomputedSRLogic;
        precomputedSRLogic = null;
        if (key.model == Model.T52C) {
            precomputedSRLogic = PRECOMPUTED_SR_LOGIC_T52C;
        } else if (key.model == Model.T52CA) {
            precomputedSRLogic = PRECOMPUTED_SR_LOGIC_T52CA;
        } else if (key.model == Model.T52E || key.model == Model.T52EE) {
            precomputedSRLogic = PRECOMPUTED_SR_LOGIC_T52E;
        }
        return precomputedSRLogic;
    }
}
