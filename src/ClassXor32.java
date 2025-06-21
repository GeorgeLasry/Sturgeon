import java.util.Arrays;

public class ClassXor32 {
    final private double[][] freqsRef = new double[FiveBits.SPACE][FiveBits.SPACE];

    final private int[][] freqs = new int[FiveBits.SPACE][FiveBits.SPACE];
    final private int[] counts = new int[FiveBits.SPACE];
    final private double[] sumProd = new double[FiveBits.SPACE];

    public void computeFreqRef(Sturgeon sturgeon, double[] languageFreq, WheelFunctionSet wheelFunctionSet) {
        SturgeonAlphabets.computeAlphabet(sturgeon.key, sturgeon.alphabet, sturgeon.inverseAlphabet, sturgeon.perms, null);
        for (double[] class32FreqRef : freqsRef) {
            Arrays.fill(class32FreqRef, 0.0);
        }
        for (int xorControl = 0; xorControl < FiveBits.SPACE; xorControl++) {
            int class32 = wheelFunctionSet.getClassXor32(xorControl);
            double[] freq32ref = freqsRef[class32];
            for (int permControl = 0; permControl < FiveBits.SPACE; permControl++) {
                int control = TenBits.bitmap(permControl, xorControl);
                byte[] alphabetAtControl = sturgeon.alphabet[control];
                for (int plainSymbol = 0; plainSymbol < FiveBits.SPACE; plainSymbol++) {
                    freq32ref[alphabetAtControl[plainSymbol]] += languageFreq[plainSymbol] / FiveBits.SPACE;
                }
            }
        }
    }

    double ic(Sturgeon sturgeon, byte[][] cipherArray, WheelFunctionSet wheelFunctionSet) {

        final int depth = cipherArray.length;
        final int len = cipherArray[0].length;

        int xorW0 = -1, xorW1 = -1, xorW2 = -1, xorW3 = -1, xorW4 = -1;

        for (int w = 0; w < 10; w++) {
            if (sturgeon.key.wheelSettings[w].getFunction() == WheelSetting.WheelFunction.XOR) {
                switch (sturgeon.key.wheelSettings[w].getFunctionIndex0to4()) {
                    case 0:
                        xorW0 = w;
                        break;
                    case 1:
                        xorW1 = w;
                        break;
                    case 2:
                        xorW2 = w;
                        break;
                    case 3:
                        xorW3 = w;
                        break;
                    case 4:
                        xorW4 = w;
                        break;
                }
            }
        }

        final boolean[] patterns0 = Wheels.PATTERNS[xorW0];
        final boolean[] patterns1 = Wheels.PATTERNS[xorW1];
        final boolean[] patterns2 = Wheels.PATTERNS[xorW2];
        final boolean[] patterns3 = Wheels.PATTERNS[xorW3];
        final boolean[] patterns4 = Wheels.PATTERNS[xorW4];
        final int wheelSizeM1_0 = Wheels.WHEEL_SIZES_M_1[xorW0];
        final int wheelSizeM1_1 = Wheels.WHEEL_SIZES_M_1[xorW1];
        final int wheelSizeM1_2 = Wheels.WHEEL_SIZES_M_1[xorW2];
        final int wheelSizeM1_3 = Wheels.WHEEL_SIZES_M_1[xorW3];
        final int wheelSizeM1_4 = Wheels.WHEEL_SIZES_M_1[xorW4];

        int wheelPos0 = sturgeon.key.wheelPositions[xorW0];
        int wheelPos1 = sturgeon.key.wheelPositions[xorW1];
        int wheelPos2 = sturgeon.key.wheelPositions[xorW2];
        int wheelPos3 = sturgeon.key.wheelPositions[xorW3];
        int wheelPos4 = sturgeon.key.wheelPositions[xorW4];

        byte in;
        int class32;

        Arrays.fill(counts, 0);
        for (int[] freq32 : freqs) {
            Arrays.fill(freq32, 0);
        }
        for (int i = 0; i < len; i++) {

            if (Sturgeon.RESTART_ON_NULL) {
                if (cipherArray[0][i] == -1) {
                    wheelPos0 = sturgeon.key.wheelPositions[xorW0];
                    wheelPos1 = sturgeon.key.wheelPositions[xorW1];
                    wheelPos2 = sturgeon.key.wheelPositions[xorW2];
                    wheelPos3 = sturgeon.key.wheelPositions[xorW3];
                    wheelPos4 = sturgeon.key.wheelPositions[xorW4];
                    continue;
                }
            }

            class32 = wheelFunctionSet.getClassXor32(FiveBits.bitmap(patterns0[wheelPos0], patterns1[wheelPos1], patterns2[wheelPos2], patterns3[wheelPos3], patterns4[wheelPos4]));


            for (byte[] cipher : cipherArray) {
                in = cipher[i];
                if (in != -1) {
                    counts[class32]++;
                    freqs[class32][in]++;
                }
            }

            wheelPos0 = (wheelPos0 == wheelSizeM1_0) ? 0 : wheelPos0 + 1;
            wheelPos1 = (wheelPos1 == wheelSizeM1_1) ? 0 : wheelPos1 + 1;
            wheelPos2 = (wheelPos2 == wheelSizeM1_2) ? 0 : wheelPos2 + 1;
            wheelPos3 = (wheelPos3 == wheelSizeM1_3) ? 0 : wheelPos3 + 1;
            wheelPos4 = (wheelPos4 == wheelSizeM1_4) ? 0 : wheelPos4 + 1;

        }

        double score = 0;
        for (int v = 0; v < FiveBits.SPACE; v++) {
            double vCount = counts[v];
            if (vCount <= 1) {
                continue;
            }
            int[] freqs32v = freqs[v];

            double vSum = 0;
            for (int i = 0; i < FiveBits.SPACE; i++) {
                int vi = freqs32v[i];
                vSum += vi * (vi - 1);
            }
            score += 1.0 * vSum / (vCount - 1);
        }

        return score / (len * depth);

    }
    double score(Sturgeon sturgeon, byte[][] cipherArray, WheelFunctionSet wheelFunctionSet) {

        int xorW0 = -1, xorW1 = -1, xorW2 = -1, xorW3 = -1, xorW4 = -1;

        for (int w = 0; w < 10; w++) {
            if (sturgeon.key.wheelSettings[w].getFunction() == WheelSetting.WheelFunction.XOR) {
                switch (sturgeon.key.wheelSettings[w].getFunctionIndex0to4()) {
                    case 0:
                        xorW0 = w;
                        break;
                    case 1:
                        xorW1 = w;
                        break;
                    case 2:
                        xorW2 = w;
                        break;
                    case 3:
                        xorW3 = w;
                        break;
                    case 4:
                        xorW4 = w;
                        break;
                }
            }
        }

        final boolean[] patterns0 = Wheels.PATTERNS[xorW0];
        final boolean[] patterns1 = Wheels.PATTERNS[xorW1];
        final boolean[] patterns2 = Wheels.PATTERNS[xorW2];
        final boolean[] patterns3 = Wheels.PATTERNS[xorW3];
        final boolean[] patterns4 = Wheels.PATTERNS[xorW4];
        final int wheelSizeM1_0 = Wheels.WHEEL_SIZES_M_1[xorW0];
        final int wheelSizeM1_1 = Wheels.WHEEL_SIZES_M_1[xorW1];
        final int wheelSizeM1_2 = Wheels.WHEEL_SIZES_M_1[xorW2];
        final int wheelSizeM1_3 = Wheels.WHEEL_SIZES_M_1[xorW3];
        final int wheelSizeM1_4 = Wheels.WHEEL_SIZES_M_1[xorW4];

        final int len = cipherArray[0].length;
        byte in;
        int xorControl;

        Arrays.fill(sumProd, 0.0);
        Arrays.fill(counts, 0);

        int wheelPos0 = sturgeon.key.wheelPositions[xorW0];
        int wheelPos1 = sturgeon.key.wheelPositions[xorW1];
        int wheelPos2 = sturgeon.key.wheelPositions[xorW2];
        int wheelPos3 = sturgeon.key.wheelPositions[xorW3];
        int wheelPos4 = sturgeon.key.wheelPositions[xorW4];

        for (int i = 0; i < len; i++) {


            if (Sturgeon.RESTART_ON_NULL) {
                if (cipherArray[0][i] == -1) {
                    wheelPos0 = sturgeon.key.wheelPositions[xorW0];
                    wheelPos1 = sturgeon.key.wheelPositions[xorW1];
                    wheelPos2 = sturgeon.key.wheelPositions[xorW2];
                    wheelPos3 = sturgeon.key.wheelPositions[xorW3];
                    wheelPos4 = sturgeon.key.wheelPositions[xorW4];
                    continue;
                }
            }

            xorControl = wheelFunctionSet.getClassXor32(FiveBits.bitmap(patterns0[wheelPos0], patterns1[wheelPos1], patterns2[wheelPos2], patterns3[wheelPos3], patterns4[wheelPos4]));

            double[] freqs32refXorControl = freqsRef[xorControl];

            for (byte[] cipher : cipherArray) {
                in = cipher[i];
                if (in != -1) {
                    sumProd[xorControl] += freqs32refXorControl[in];
                    counts[xorControl]++;
                }
            }

            wheelPos0 = (wheelPos0 == wheelSizeM1_0) ? 0 : wheelPos0 + 1;
            wheelPos1 = (wheelPos1 == wheelSizeM1_1) ? 0 : wheelPos1 + 1;
            wheelPos2 = (wheelPos2 == wheelSizeM1_2) ? 0 : wheelPos2 + 1;
            wheelPos3 = (wheelPos3 == wheelSizeM1_3) ? 0 : wheelPos3 + 1;
            wheelPos4 = (wheelPos4 == wheelSizeM1_4) ? 0 : wheelPos4 + 1;

        }

        double score = 0;
        for (xorControl = 0; xorControl < FiveBits.SPACE; xorControl++) {
            double count = counts[xorControl];
            if (count > 0) {
                score += sumProd[xorControl] / count;
            }
        }
        return score;

    }


}
