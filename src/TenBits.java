class TenBits{
    static final int SPACE = 1024;
    private static final int BIT0 = 0x1;
    private static final int BIT1 = BIT0 << 1;
    private static final int BIT2 = BIT1 << 1;
    private static final int BIT3 = BIT2 << 1;
    private static final int BIT4 = BIT3 << 1;
    private static final int BIT5 = BIT4 << 1;
    private static final int BIT6 = BIT5 << 1;
    private static final int BIT7 = BIT6 << 1;
    private static final int BIT8 = BIT7 << 1;
    private static final int BIT9 = BIT8 << 1;
    static final int[] BITS = {BIT0, BIT1, BIT2, BIT3, BIT4, BIT5, BIT6, BIT7, BIT8, BIT9};

    static int bitmap(boolean o0, boolean o1, boolean o2, boolean o3, boolean o4,
                      boolean o5, boolean o6, boolean o7, boolean o8, boolean o9) {
        int output = 0;
        output |= o0 ? BIT0 : 0x0;
        output |= o1 ? BIT1 : 0x0;
        output |= o2 ? BIT2 : 0x0;
        output |= o3 ? BIT3 : 0x0;
        output |= o4 ? BIT4 : 0x0;
        output |= o5 ? BIT5 : 0x0;
        output |= o6 ? BIT6 : 0x0;
        output |= o7 ? BIT7 : 0x0;
        output |= o8 ? BIT8 : 0x0;
        output |= o9 ? BIT9 : 0x0;
        return output;
    }

    static boolean isSet(int bit, int symbol) {
        switch (bit) {
            case 0:
                return (symbol & BIT0) == BIT0;
            case 1:
                return (symbol & BIT1) == BIT1;
            case 2:
                return (symbol & BIT2) == BIT2;
            case 3:
                return (symbol & BIT3) == BIT3;
            case 4:
                return (symbol & BIT4) == BIT4;
            case 5:
                return (symbol & BIT5) == BIT5;
            case 6:
                return (symbol & BIT6) == BIT6;
            case 7:
                return (symbol & BIT7) == BIT7;
            case 8:
                return (symbol & BIT8) == BIT8;
            case 9:
                return (symbol & BIT9) == BIT9;
            default:
                throw new RuntimeException("Invalid functionIndex0to9: " + bit);
        }
    }

    static int setBit(int bit, boolean bitVal, int symbol) {
        if (!bitVal) {
            switch (bit) {
                case 0:
                    return symbol & ~BIT0;
                case 1:
                    return symbol & ~BIT1;
                case 2:
                    return symbol & ~BIT2;
                case 3:
                    return symbol & ~BIT3;
                case 4:
                    return symbol & ~BIT4;
                case 5:
                    return symbol & ~BIT5;
                case 6:
                    return symbol & ~BIT6;
                case 7:
                    return symbol & ~BIT7;
                case 8:
                    return symbol & ~BIT8;
                case 9:
                    return symbol & ~BIT9;

                default:
                    throw new RuntimeException("Invalid functionIndex0to4: " + bit);
            }
        }
        switch (bit) {
            case 0:
                return symbol | BIT0;
            case 1:
                return symbol | BIT1;
            case 2:
                return symbol | BIT2;
            case 3:
                return symbol | BIT3;
            case 4:
                return symbol | BIT4;
            case 5:
                return symbol | BIT5;
            case 6:
                return symbol | BIT6;
            case 7:
                return symbol | BIT7;
            case 8:
                return symbol | BIT8;
            case 9:
                return symbol | BIT9;

            default:
                throw new RuntimeException("Invalid functionIndex0to9: " + bit);
        }
    }

    static String getString(int symbol) {

        StringBuilder s = new StringBuilder();
        for (int bit = 0; bit < 10; bit++) {
            s.append(isSet(bit, symbol) ? "1" : "0");
        }
        return s.toString();

    }

    static int count(int symbol) {
        int count = 0;
        while (symbol != 0) {
            if ((symbol & 0x1) == 0x1) {
                count++;
            }
            symbol >>>= 1;
        }
        return count;
    }

    static int bitmap(int low, int high) {
        return (high << 5) | low;
    }

    static byte low(int bitmap) {
        return (byte) (bitmap & 0x1f);
    }

    static byte high(int bitmap) {
        return (byte) (bitmap >> 5);
    }
}
