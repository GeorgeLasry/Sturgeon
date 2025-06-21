import java.util.Arrays;

public class Wheels {
    static final int NUM_WHEELS = 10;
    static final int NUM_MSG_SETTINGS = 5;
    // 73* 71* 69* 67* 65* 64* 61* 59* 53* 47 = 893,622,318,929,520,960
    static final int[] WHEEL_SIZES = {73, 71, 69, 67, 65, 64, 61, 59, 53, 47};
    static final int WHEEL_SIZE_0_M_1 = WHEEL_SIZES[0] - 1;
    static final int WHEEL_SIZE_1_M_1 = WHEEL_SIZES[1] - 1;
    static final int WHEEL_SIZE_2_M_1 = WHEEL_SIZES[2] - 1;
    static final int WHEEL_SIZE_3_M_1 = WHEEL_SIZES[3] - 1;
    static final int WHEEL_SIZE_4_M_1 = WHEEL_SIZES[4] - 1;
    static final int WHEEL_SIZE_5_M_1 = WHEEL_SIZES[5] - 1;
    static final int WHEEL_SIZE_6_M_1 = WHEEL_SIZES[6] - 1;
    static final int WHEEL_SIZE_7_M_1 = WHEEL_SIZES[7] - 1;
    static final int WHEEL_SIZE_8_M_1 = WHEEL_SIZES[8] - 1;
    static final int WHEEL_SIZE_9_M_1 = WHEEL_SIZES[9] - 1;
    static final int[] WHEEL_SIZES_M_1 = {WHEEL_SIZE_0_M_1, WHEEL_SIZE_1_M_1, WHEEL_SIZE_2_M_1, WHEEL_SIZE_3_M_1, WHEEL_SIZE_4_M_1, WHEEL_SIZE_5_M_1, WHEEL_SIZE_6_M_1, WHEEL_SIZE_7_M_1, WHEEL_SIZE_8_M_1, WHEEL_SIZE_9_M_1};

    static final String WHEEL_LETTERS = "ABCDEFGHJK";
    static final int A = 0; //WHEEL_LETTERS.indexOf('A');
    static final int B = 1; //WHEEL_LETTERS.indexOf('B');
    static final int C = 2; //WHEEL_LETTERS.indexOf('C');
    static final int D = 3; //WHEEL_LETTERS.indexOf('D');
    static final int E = 4; //WHEEL_LETTERS.indexOf('E');
    static final int F = 5; //WHEEL_LETTERS.indexOf('F');
    static final int G = 6; //WHEEL_LETTERS.indexOf('G');
    static final int H = 7; //WHEEL_LETTERS.indexOf('H');
    static final int J = 8; //WHEEL_LETTERS.indexOf('J');
    static final int K = 9; //WHEEL_LETTERS.indexOf('K');

    static final int[] WHEEL_STEP_CAM_POS = {25, 24, 23, 23, 22, 22, 20, 20, 18, 16};
    static final String DEFAULT_XOR_WHEELS = ":I:II:III:IV:V";
    static final String DEFAULT_PERM_WHEELS = "1:3:5:7:9";
    static final String DEFAULT_PERM_AND_XOR_WHEELS = DEFAULT_PERM_WHEELS + DEFAULT_XOR_WHEELS;
    static final String DEFAULT_WHEEL_POSITIONS = "01:01:01:01:01:01:01:01:01:01";

    private static final String[] PIN_PATTERNS = {
            //////// 1234567890123456789012345678901234567890123456789012345678901234567890123
            /*A 73/25*/ ".xx.xxx...x..xx..x..xxxx..xxx.x.x.xxx..xx..xxx.xx.....xxx...xxxx.x......x",
            /*B 71/24*/ ".xxxx..xxxxxx...x...x.x..xx.xxxxx.....xxx...xx.xxx.....xx.x.x.xxx.....x",
            /*C 69/23*/ ".xxxxx.x.xx..x.xx....xx.x....xxxx.xxx.xxx...x..x..xxx....xx....xx...x",
            /*D 67/23*/ ".xx.x.x...xxx.....xx.x....x..xxx..xxx..x.xxx...xx..x..x.xxxx..x...x",
            /*E 65/22*/ ".x...x...x.xxxxxxx.x.xxxxxx..x.x..xx.x....x.xxxxx..xxx.x.x.xx...x",
            /*F 64/22*/ ".x.x.xxxxx....x.xxxx..x.xx.xxxx.xx..xxx....x....xxx.xxxxxx...x.x",
            /*G 61/20*/ ".xxxx...xx..xx...x.xxxxx.x..x..xxx.x.xx.x.xx.x....xx.x..x.xxx",
            /*H 59/20*/ ".x..xxxxx...xxxx.x..xxxx...xx.x...xx.x.xx...xxxxxx..xx.x.xx",
            /*J 53/18*/ ".x.xx.xx..xx.x.xx...xxx...xxx..x.xxx.x....xxxx...x.xx",
            /*K 47/16*/ ".x....xxxxx...x..x.xxx..x.x.xxx..xxx.xxxx...xxx"
    };
    /*
    01011100101011100111011110001110100001111100010
                                   .x....xxxxx...x.
    .x.xxx..x.x.xxx..xxx.xxxx...xxx

    00111000111001011101000011110001011010110110011010110
                                       .x.xx.xx..xx.x.xx.
    ..xxx...xxx..x.xxx.x....xxxx...x.xx
    11110001101000110101100011111100110101101001111100011110100
                                           .x..xxxxx...xxxx.x..
    xxxx...xx.x...xx.x.xx...xxxxxx..xx.x.xx
    1111010010011101011010110100001101001011101111000110011000101
                                             .xxxx...xx..xx...x.x
    xxxx.x..x..xxx.x.xx.x.xx.x....xx.x..x.xxx
    1011011110110011100001000011101111110001010101011111000010111100
                                              .x.x.xxxxx....x.xxxx..
    x.xx.xxxx.xx..xxx....x....xxx.xxxxxx...x.x
    11111001010011010000101111100111010101100010100010001011111110101
                                               .x...x...x.xxxxxxx.x.x
    xxxxx..x.x..xx.x....x.xxxxx..xxx.x.x.xx...x
    0001001110011100101110001100100101111001000101101010001110000011010
                                                .xx.x.x...xxx.....xx.x.
    ...x..xxx..xxx..x.xxx...xx..x..x.xxxx..x...x
    010000111101110111000100100111000011000011000101111101011001011000011
                                                  .xxxxx.x.xx..x.xx....xx
    .x....xxxx.xxx.xxx...x..x..xxx....xx....xx...x
    01101111100000111000110111000001101010111000001011110011111100010001010
                                                   .xxxx..xxxxxx...x...x.x.
    .xx.xxxxx.....xxx...xx.xxx.....xx.x.x.xxx.....x
    0111010101110011001110110000011100011110100000010110111000100110010011110
                                                    .xx.xxx...x..xx..x..xxxx.
    .xxx.x.x.xxx..xx..xxx.xx.....xxx...xxxx.x......x


    47:"01011100101011100111011110001110100001111100010";
    53:"00111000111001011101000011110001011010110110011010110";
    59:"11110001101000110101100011111100110101101001111100011110100";
    61:"1111010010011101011010110100001101001011101111000110011000101";
    64:"1011011110110011100001000011101111110001010101011111000010111100";
    65:"11111001010011010000101111100111010101100010100010001011111110101";
    67:"0001001110011100101110001100100101111001000101101010001110000011010";
    69:"010000111101110111000100100111000011000011000101111101011001011000011";
    71:"01101111100000111000110111000001101010111000001011110011111100010001010";
    73:"0111010101110011001110110000011100011110100000010110111000100110010011110";


                */
    static final boolean[][] PATTERNS = parsePatterns(PIN_PATTERNS);
    static final boolean[] PATTERNS0 = PATTERNS[0];
    static final boolean[] PATTERNS1 = PATTERNS[1];
    static final boolean[] PATTERNS2 = PATTERNS[2];
    static final boolean[] PATTERNS3 = PATTERNS[3];
    static final boolean[] PATTERNS4 = PATTERNS[4];
    static final boolean[] PATTERNS5 = PATTERNS[5];
    static final boolean[] PATTERNS6 = PATTERNS[6];
    static final boolean[] PATTERNS7 = PATTERNS[7];
    static final boolean[] PATTERNS8 = PATTERNS[8];
    static final boolean[] PATTERNS9 = PATTERNS[9];

    static final int[][] INT_PATTERNS = computeIntPatterns();
    static final int[] INT_PATTERNS0 = INT_PATTERNS[0];
    static final int[] INT_PATTERNS1 = INT_PATTERNS[1];
    static final int[] INT_PATTERNS2 = INT_PATTERNS[2];
    static final int[] INT_PATTERNS3 = INT_PATTERNS[3];
    static final int[] INT_PATTERNS4 = INT_PATTERNS[4];
    static final int[] INT_PATTERNS5 = INT_PATTERNS[5];
    static final int[] INT_PATTERNS6 = INT_PATTERNS[6];
    static final int[] INT_PATTERNS7 = INT_PATTERNS[7];
    static final int[] INT_PATTERNS8 = INT_PATTERNS[8];
    static final int[] INT_PATTERNS9 = INT_PATTERNS[9];

    private static final String DEFAULT_PROGRAMMABLE_PERM_WHEELS = "1-2:3-4:5-6:7-8:9-10";
    static final String DEFAULT_PROGRAMMABLE_PERM_AND_XOR_WHEELS = DEFAULT_PROGRAMMABLE_PERM_WHEELS + DEFAULT_XOR_WHEELS;

    private static int[][] computeIntPatterns() {
        int[][] intPatterns = new int[NUM_WHEELS][];

        for (int w = 0; w < NUM_WHEELS; w++) {
            intPatterns[w] = new int[PATTERNS[w].length];
            for (int p = 0; p < PATTERNS[w].length; p++) {
                intPatterns[w][p] = PATTERNS[w][p] ? TenBits.BITS[w] : 0;
            }
        }
        return intPatterns;
    }

    private static boolean[][] parsePatterns(String[] patternString) {
        boolean[][] patterns = new boolean[patternString.length][];

        for (int p = 0; p < patternString.length; p++) {
            String s = patternString[p];
            int len = s.length();
            patterns[p] = new boolean[len];
            int count1 = 0;
            for (int i = 0; i < len; i++) {
                patterns[p][i] = (s.charAt(i) == 'X') || (s.charAt(i) == 'x') || (s.charAt(i) == '1');
                count1 += patterns[p][i] ? 1 : 0;
            }
            //System.out.printf("Wheel %c %2d has %2d crosses (half is %2.1f)\n", WHEEL_LETTERS.charAt(p), WHEEL_SIZES[p], count1, WHEEL_SIZES[p] * 0.5);
        }

        
        return patterns;
    }

    static int getWheelsBitmap(int wheelPos0, int wheelPos1, int wheelPos2, int wheelPos3, int wheelPos4, int wheelPos5, int wheelPos6, int wheelPos7, int wheelPos8, int wheelPos9) {
        return INT_PATTERNS0[wheelPos0] | INT_PATTERNS1[wheelPos1] | INT_PATTERNS2[wheelPos2] | INT_PATTERNS3[wheelPos3]
                | INT_PATTERNS4[wheelPos4] | INT_PATTERNS5[wheelPos5] | INT_PATTERNS6[wheelPos6] | INT_PATTERNS7[wheelPos7]
                | INT_PATTERNS8[wheelPos8] | INT_PATTERNS9[wheelPos9];
    }
}
