class FiveBits {

    static final int SPACE = 32;
    private static final int BIT0 = 0x1;
    private static final int BIT1 = BIT0 << 1;
    private static final int BIT2 = BIT1 << 1;
    private static final int BIT3 = BIT2 << 1;
    private static final int BIT4 = BIT3 << 1;

    static final int[] BITS = {BIT0, BIT1, BIT2, BIT3, BIT4};
    private static final int UNDEFINED = -1;

    private final static boolean[][] IS_CACHE_SET = computeCache();

    private static boolean[][] computeCache() {
        boolean[][] isSetCache = new boolean[SPACE][5];
        for (int symbol = 0; symbol < SPACE; symbol++) {
            for (int bit = 0; bit < 5; bit++) {
                isSetCache[symbol][bit] = isSetNoCache(bit, symbol);
            }
        }
        return isSetCache;
    }

    static boolean isSet(int bit, int symbol) {
        return IS_CACHE_SET[symbol][bit];
    }

    static byte bitmap(boolean o0, boolean o1, boolean o2, boolean o3, boolean o4) {
        byte output = 0;
        output |= (byte) (o0 ? BIT0 : 0x0);
        output |= (byte) (o1 ? BIT1 : 0x0);
        output |= (byte) (o2 ? BIT2 : 0x0);
        output |= (byte) (o3 ? BIT3 : 0x0);
        output |= (byte) (o4 ? BIT4 : 0x0);
        return output;
    }

    private static boolean isSetNoCache(int bit, int symbol) {
        return switch (bit) {
            case 0 -> (symbol & BIT0) == BIT0;
            case 1 -> (symbol & BIT1) == BIT1;
            case 2 -> (symbol & BIT2) == BIT2;
            case 3 -> (symbol & BIT3) == BIT3;
            case 4 -> (symbol & BIT4) == BIT4;
            default -> throw new RuntimeException("Invalid bit index: " + bit);
        };
    }


    static int setBit(int bit, boolean bitVal, int symbol) {
        if (!bitVal) {
            return switch (bit) {
                case 0 -> symbol & ~BIT0;
                case 1 -> symbol & ~BIT1;
                case 2 -> symbol & ~BIT2;
                case 3 -> symbol & ~BIT3;
                case 4 -> symbol & ~BIT4;
                default -> throw new RuntimeException("Invalid functionIndex0to4: " + bit);
            };
        }
        return switch (bit) {
            case 0 -> symbol | BIT0;
            case 1 -> symbol | BIT1;
            case 2 -> symbol | BIT2;
            case 3 -> symbol | BIT3;
            case 4 -> symbol | BIT4;
            default -> throw new RuntimeException("Invalid functionIndex0to4: " + bit);
        };
    }

    static boolean compareBitCounts(byte b1, byte b2) {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (isSet(i, b1)) {
                count++;
            }
            if (isSet(i, b2)) {
                count--;
            }
        }
        return count == 0;
    }

    static String getString(int symbol) {
        StringBuilder s = new StringBuilder();
        for (int bit = 0; bit < 5; bit++) {
            s.append(isSet(bit, symbol) ? "1" : "0");
        }
        return s.toString();
    }

    static int count(int symbol) {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (isSet(i, symbol)) {
                count++;
            }
        }
        return count;
    }

    public static byte computeSwitchesPerm(byte input, int[] perm) {

        boolean[] isCacheSet = IS_CACHE_SET[input];
        byte output = 0;
        int p;
        for (int b = 0; b < 5; b++) {
            if (((p = perm[b]) != UNDEFINED) && isCacheSet[p]) {
                output |= (byte) BITS[b];
            }
        }
        return output;

    }
}
