public class MessageKeyUnit {
    static final String MSG_KEY_LETTERS = "PSTUWXYZ";
    static final String MSG_KEY_LETTERS_NEUTRAL = "PZXUS";
    private static final boolean _X_ = true;
    private static final boolean ___ = false;
    // Message Key unit - T52c/ca
    private static final boolean[][] MK_T52C_AND_CA = {
            /*                    P    S    T    U    W    X    Y    Z */
            /*         | T01 */ {___, ___, ___, _X_, _X_, _X_, ___, _X_},
            /* Lever 1 | T06 */ {___, _X_, ___, ___, _X_, ___, _X_, _X_},
            /*         | T11 */ {___, ___, _X_, ___, ___, _X_, _X_, _X_},
            //----------------------------------------------------------
            /*         | T02 */ {___, ___, _X_, _X_, _X_, ___, _X_, ___},
            /* Lever 2 | T07 */ {_X_, ___, ___, _X_, ___, _X_, _X_, ___},
            /*         | T12 */ {___, _X_, ___, ___, _X_, _X_, _X_, ___},
            //----------------------------------------------------------
            /*         | T03 */ {_X_, _X_, _X_, ___, _X_, ___, ___, ___},
            /* Lever 3 | T08 */ {___, _X_, ___, _X_, _X_, ___, _X_, ___},
            /*         | T13 */ {___, ___, _X_, _X_, _X_, ___, ___, _X_},
            //----------------------------------------------------------
            /*         | T04 */ {_X_, ___, _X_, ___, ___, ___, _X_, _X_},
            /* Lever 4 | T09 */ {___, _X_, _X_, ___, _X_, ___, ___, _X_},
            /*         | T14 */ {_X_, _X_, _X_, ___, ___, _X_, ___, ___},
            //----------------------------------------------------------
            /*         | T05 */ {_X_, ___, ___, ___, _X_, _X_, _X_, ___},
            /* Lever 5 | T10 */ {_X_, ___, _X_, ___, ___, _X_, ___, _X_},
            /*         | T15 */ {_X_, ___, ___, _X_, ___, ___, _X_, _X_},
    };
    private static final int UNDEFINED = -1;
    private static final int[] MK_SWITCHES_POSITIONS_T52C_AND_CA = {UNDEFINED, 0, 3, 6, 9, 12, 1, 4, 7, 10, 13, 2, 5, 8, 11, 14};

    private static boolean select(Key key, int selectorNumber /* 1 to 15 */,
                                  boolean firstInput,
                                  boolean secondInput,
                                  boolean returnSecondOutput) {
        boolean active = isActive(key, selectorNumber);
        return (active ^ returnSecondOutput) ? secondInput : firstInput;
    }

    private static boolean isActive(Key key, int selectorNumber /* 1 to 15 */) {
        int row = MK_SWITCHES_POSITIONS_T52C_AND_CA[selectorNumber];
        int leverNumber = row / 3;
        int col = key.wheelMsgSettings[leverNumber];
        return MK_T52C_AND_CA[row][col];
    }

    static int msgKeyUnitOutput(Key key, int input) {
        if (key.wheelMsgSettings == null) {
            return input;
        }
        final boolean OFF = false;
        final boolean ON = true;

        boolean a = TenBits.isSet(Wheels.A, input);
        boolean b = TenBits.isSet(Wheels.B, input);
        boolean c = TenBits.isSet(Wheels.C, input);
        boolean d = TenBits.isSet(Wheels.D, input);
        boolean e = TenBits.isSet(Wheels.E, input);
        boolean f = TenBits.isSet(Wheels.F, input);
        boolean g = TenBits.isSet(Wheels.G, input);
        boolean h = TenBits.isSet(Wheels.H, input);
        boolean j = TenBits.isSet(Wheels.J, input);
        boolean k = TenBits.isSet(Wheels.K, input);

        boolean t1_1 = select(key, 1, a, b, OFF);
        boolean t1_2 = select(key, 1, a, b, ON);
        boolean t2_1 = select(key, 2, c, d, OFF);
        boolean t2_2 = select(key, 2, c, d, ON);
        boolean t3_1 = select(key, 3, e, f, OFF);
        boolean t3_2 = select(key, 3, e, f, ON);
        boolean t4_1 = select(key, 4, g, h, OFF);
        boolean t4_2 = select(key, 4, g, h, ON);
        boolean t5_1 = select(key, 5, j, k, OFF);
        boolean t5_2 = select(key, 5, j, k, ON);

        boolean t6_1 = select(key, 6, t1_2, t2_1, OFF);
        boolean t6_2 = select(key, 6, t1_2, t2_1, ON);
        boolean t7_1 = select(key, 7, t2_2, t3_1, OFF);
        boolean t7_2 = select(key, 7, t2_2, t3_1, ON);
        boolean t8_1 = select(key, 8, t3_2, t4_1, OFF);
        boolean t8_2 = select(key, 8, t3_2, t4_1, ON);
        boolean t9_1 = select(key, 9, t4_2, t5_1, OFF);
        boolean t9_2 = select(key, 9, t4_2, t5_1, ON);
        boolean t10_1 = select(key, 10, t5_2, t1_1, OFF);
        boolean t10_2 = select(key, 10, t5_2, t1_1, ON);

        a = select(key, 11, t10_2, t8_1, OFF);
        f = select(key, 11, t10_2, t8_1, ON);
        b = select(key, 12, t6_1, t8_2, OFF);
        g = select(key, 12, t6_1, t8_2, ON);
        c = select(key, 13, t6_2, t9_1, OFF);
        h = select(key, 13, t6_2, t9_1, ON);
        d = select(key, 14, t7_1, t9_2, OFF);
        j = select(key, 14, t7_1, t9_2, ON);
        e = select(key, 15, t7_2, t10_1, OFF);
        k = select(key, 15, t7_2, t10_1, ON);

        int symbol = 0;
        symbol = TenBits.setBit(Wheels.A, a, symbol);
        symbol = TenBits.setBit(Wheels.B, b, symbol);
        symbol = TenBits.setBit(Wheels.C, c, symbol);
        symbol = TenBits.setBit(Wheels.D, d, symbol);
        symbol = TenBits.setBit(Wheels.E, e, symbol);
        symbol = TenBits.setBit(Wheels.F, f, symbol);
        symbol = TenBits.setBit(Wheels.G, g, symbol);
        symbol = TenBits.setBit(Wheels.H, h, symbol);
        symbol = TenBits.setBit(Wheels.J, j, symbol);
        symbol = TenBits.setBit(Wheels.K, k, symbol);

        return symbol;
    }

    private static int selectWithDebug(Key key, int selectorNumber /* 1 to 15 */,
                                       int firstInput,
                                       int secondInput,
                                       boolean returnSecondOutput) {
        boolean active = isActive(key, selectorNumber);
        return (active ^ returnSecondOutput) ? secondInput : firstInput;
    }

    public static String[] msgKeyUnitOutputDebug(Key key) {

        final boolean OFF = false;
        final boolean ON = true;


        int a = Wheels.A;
        int b = Wheels.B;
        int c = Wheels.C;
        int d = Wheels.D;
        int e = Wheels.E;
        int f = Wheels.F;
        int g = Wheels.G;
        int h = Wheels.H;
        int j = Wheels.J;
        int k = Wheels.K;

        int t1_1 = selectWithDebug(key, 1, a, b, OFF);
        int t1_2 = selectWithDebug(key, 1, a, b, ON);
        int t2_1 = selectWithDebug(key, 2, c, d, OFF);
        int t2_2 = selectWithDebug(key, 2, c, d, ON);
        int t3_1 = selectWithDebug(key, 3, e, f, OFF);
        int t3_2 = selectWithDebug(key, 3, e, f, ON);
        int t4_1 = selectWithDebug(key, 4, g, h, OFF);
        int t4_2 = selectWithDebug(key, 4, g, h, ON);
        int t5_1 = selectWithDebug(key, 5, j, k, OFF);
        int t5_2 = selectWithDebug(key, 5, j, k, ON);

        int t6_1 = selectWithDebug(key, 6, t1_2, t2_1, OFF);
        int t6_2 = selectWithDebug(key, 6, t1_2, t2_1, ON);
        int t7_1 = selectWithDebug(key, 7, t2_2, t3_1, OFF);
        int t7_2 = selectWithDebug(key, 7, t2_2, t3_1, ON);
        int t8_1 = selectWithDebug(key, 8, t3_2, t4_1, OFF);
        int t8_2 = selectWithDebug(key, 8, t3_2, t4_1, ON);
        int t9_1 = selectWithDebug(key, 9, t4_2, t5_1, OFF);
        int t9_2 = selectWithDebug(key, 9, t4_2, t5_1, ON);
        int t10_1 = selectWithDebug(key, 10, t5_2, t1_1, OFF);
        int t10_2 = selectWithDebug(key, 10, t5_2, t1_1, ON);

        a = selectWithDebug(key, 11, t10_2, t8_1, OFF);
        f = selectWithDebug(key, 11, t10_2, t8_1, ON);
        b = selectWithDebug(key, 12, t6_1, t8_2, OFF);
        g = selectWithDebug(key, 12, t6_1, t8_2, ON);
        c = selectWithDebug(key, 13, t6_2, t9_1, OFF);
        h = selectWithDebug(key, 13, t6_2, t9_1, ON);
        d = selectWithDebug(key, 14, t7_1, t9_2, OFF);
        j = selectWithDebug(key, 14, t7_1, t9_2, ON);
        e = selectWithDebug(key, 15, t7_2, t10_1, OFF);
        k = selectWithDebug(key, 15, t7_2, t10_1, ON);

        String[] ret = new String[5];
        ret[0] = key.getWheelMsgSettingsString();

        StringBuilder s = new StringBuilder();
        for (int selector = 1; selector <= 15 ; selector++) {
            if (isActive(key, selector)) {
                if (!s.isEmpty()) {
                    s.append(", ");
                }
                s.append("T").append(selector);
            }
        }
        ret[1] = s.toString();

        s.setLength(0);
        for (int w = 0; w < 10; w++) {
            s.append(Wheels.WHEEL_LETTERS.charAt(w));
        }
        ret[2] = s.toString();

        s.setLength(0);
        int[] reorderedWheels = {a, b, c, d, e, f, g, h, j, k};
        for (int w = 0; w < 10; w++) {
            s.append(Wheels.WHEEL_LETTERS.charAt(reorderedWheels[w]));
        }
        ret[3] = s.toString();

        s.setLength(0);
        for (int function = 0; function < 10; function++) {
            s.append(Wheels.WHEEL_LETTERS.charAt(reorderedWheels[WheelSetting.wheelWithFunction0to9(key.wheelSettings, function)]));
        }
        ret[4] = s.toString();

        return ret;

    }
}
