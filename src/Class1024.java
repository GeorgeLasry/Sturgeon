import java.util.Arrays;

public class Class1024 {
    final private double[][] freqsRef = new double[TenBits.SPACE][FiveBits.SPACE];

    final private int[][] freqs = new int[TenBits.SPACE][FiveBits.SPACE];
    final private int[] counts = new int[TenBits.SPACE];
    final private double[] sumProd = new double[TenBits.SPACE];

    void computeFreqRef(Sturgeon sturgeon, double[] languageFreq, WheelFunctionSet wheelFunctionSet) {
        SturgeonAlphabets.computeAlphabet(sturgeon.key, sturgeon.alphabet, sturgeon.inverseAlphabet, sturgeon.perms, null);
        for (double[] class1024FreqRef : freqsRef) {
            Arrays.fill(class1024FreqRef, 0.0);
        }
        for (int xorControl = 0; xorControl < FiveBits.SPACE; xorControl++) {
            for (int permControl = 0; permControl < FiveBits.SPACE; permControl++) {
                int control = TenBits.bitmap(permControl, xorControl);
                int class1024 = wheelFunctionSet.getClass1024(control);
                double[] freq1024ref = freqsRef[class1024];
                for (int plainSymbol = 0; plainSymbol < FiveBits.SPACE; plainSymbol++) {
                    int cipherSymbol = sturgeon.alphabet[control][plainSymbol];
                    freq1024ref[cipherSymbol] += languageFreq[plainSymbol] / FiveBits.SPACE;
                }
            }
        }
    }
    double score(Sturgeon sturgeon, byte[][] cipherArray, WheelFunctionSet wheelFunctionSet) {

        int wheelPos0 = sturgeon.key.wheelPositions[0];
        int wheelPos1 = sturgeon.key.wheelPositions[1];
        int wheelPos2 = sturgeon.key.wheelPositions[2];
        int wheelPos3 = sturgeon.key.wheelPositions[3];
        int wheelPos4 = sturgeon.key.wheelPositions[4];
        int wheelPos5 = sturgeon.key.wheelPositions[5];
        int wheelPos6 = sturgeon.key.wheelPositions[6];
        int wheelPos7 = sturgeon.key.wheelPositions[7];
        int wheelPos8 = sturgeon.key.wheelPositions[8];
        int wheelPos9 = sturgeon.key.wheelPositions[9];


        Arrays.fill(sumProd, 0.0);
        Arrays.fill(counts, 0);

        byte in;
        int wheelsBitmap, class1024;
        for (int i = 0; i < cipherArray[0].length; i++) {
            if (Sturgeon.RESTART_ON_NULL) {
                if (cipherArray[0][i] == -1) {
                    wheelPos0 = sturgeon.key.wheelPositions[0];
                    wheelPos1 = sturgeon.key.wheelPositions[1];
                    wheelPos2 = sturgeon.key.wheelPositions[2];
                    wheelPos3 = sturgeon.key.wheelPositions[3];
                    wheelPos4 = sturgeon.key.wheelPositions[4];
                    wheelPos5 = sturgeon.key.wheelPositions[5];
                    wheelPos6 = sturgeon.key.wheelPositions[6];
                    wheelPos7 = sturgeon.key.wheelPositions[7];
                    wheelPos8 = sturgeon.key.wheelPositions[8];
                    wheelPos9 = sturgeon.key.wheelPositions[9];
                    continue;
                }
            }

            wheelsBitmap = Wheels.getWheelsBitmap(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
            class1024 = wheelFunctionSet.getClass1024(sturgeon.controlMapping[wheelsBitmap]);

            double[] freqs1024refForControl = freqsRef[class1024];

            for (byte[] cipher : cipherArray) {
                in = cipher[i];
                if (in != -1) {
                    sumProd[class1024] += freqs1024refForControl[in];
                    counts[class1024]++;
                }
            }

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

        double score = 0;
        for (class1024 = 0; class1024 < TenBits.SPACE; class1024++) {
            double count = counts[class1024];
            if (count > 0) {
                score += sumProd[class1024] / count;
            }
        }
        return score;

    }
    double ic(Sturgeon sturgeon, byte[][] cipherArray, WheelFunctionSet wheelFunctionSet) {

        int wheelPos0 = sturgeon.key.wheelPositions[0];
        int wheelPos1 = sturgeon.key.wheelPositions[1];
        int wheelPos2 = sturgeon.key.wheelPositions[2];
        int wheelPos3 = sturgeon.key.wheelPositions[3];
        int wheelPos4 = sturgeon.key.wheelPositions[4];
        int wheelPos5 = sturgeon.key.wheelPositions[5];
        int wheelPos6 = sturgeon.key.wheelPositions[6];
        int wheelPos7 = sturgeon.key.wheelPositions[7];
        int wheelPos8 = sturgeon.key.wheelPositions[8];
        int wheelPos9 = sturgeon.key.wheelPositions[9];


        for (int[] freq1024 : freqs) {
            Arrays.fill(freq1024, 0);
        }
        Arrays.fill(counts, 0);

        int depth = cipherArray.length;
        int len = cipherArray[0].length;

        byte in;
        int wheelsBitmap, class1024;
        for (int i = 0; i < cipherArray[0].length; i++) {
            if (Sturgeon.RESTART_ON_NULL) {
                if (cipherArray[0][i] == -1) {
                    wheelPos0 = sturgeon.key.wheelPositions[0];
                    wheelPos1 = sturgeon.key.wheelPositions[1];
                    wheelPos2 = sturgeon.key.wheelPositions[2];
                    wheelPos3 = sturgeon.key.wheelPositions[3];
                    wheelPos4 = sturgeon.key.wheelPositions[4];
                    wheelPos5 = sturgeon.key.wheelPositions[5];
                    wheelPos6 = sturgeon.key.wheelPositions[6];
                    wheelPos7 = sturgeon.key.wheelPositions[7];
                    wheelPos8 = sturgeon.key.wheelPositions[8];
                    wheelPos9 = sturgeon.key.wheelPositions[9];
                    continue;
                }
            }

            wheelsBitmap = Wheels.getWheelsBitmap(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
            class1024 = wheelFunctionSet.getClass1024(sturgeon.controlMapping[wheelsBitmap]);

            for (byte[] cipher : cipherArray) {
                in = cipher[i];
                if (in != -1) {
                    freqs[class1024][in]++;
                    counts[class1024]++;
                }
            }

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

        double score = 0;
        for (int c1024 = 0; c1024 < TenBits.SPACE; c1024++) {
            double vCount = counts[c1024];
            if (vCount <= 1) {
                continue;
            }
            int[] freqs1024v = freqs[c1024];

            double vSum = 0;
            for (int symbol = 0; symbol < FiveBits.SPACE; symbol++) {
                int vi = freqs1024v[symbol];
                vSum += vi * (vi - 1);
            }
            score += vSum / (vCount - 1);
        }

        return score / (len * depth);

    }


    double[][][][] bitAnalysis(Sturgeon sturgeon, byte[][] cipherArray, double[][][][] ref) {

        int wheelPos0 = sturgeon.key.wheelPositions[0];
        int wheelPos1 = sturgeon.key.wheelPositions[1];
        int wheelPos2 = sturgeon.key.wheelPositions[2];
        int wheelPos3 = sturgeon.key.wheelPositions[3];
        int wheelPos4 = sturgeon.key.wheelPositions[4];
        int wheelPos5 = sturgeon.key.wheelPositions[5];
        int wheelPos6 = sturgeon.key.wheelPositions[6];
        int wheelPos7 = sturgeon.key.wheelPositions[7];
        int wheelPos8 = sturgeon.key.wheelPositions[8];
        int wheelPos9 = sturgeon.key.wheelPositions[9];


        for (int[] freq1024 : freqs) {
            Arrays.fill(freq1024, 0);
        }
        Arrays.fill(counts, 0);

        for (int i = 0; i < cipherArray[0].length; i++) {
            if (Sturgeon.RESTART_ON_NULL) {
                if (cipherArray[0][i] == -1) {
                    wheelPos0 = sturgeon.key.wheelPositions[0];
                    wheelPos1 = sturgeon.key.wheelPositions[1];
                    wheelPos2 = sturgeon.key.wheelPositions[2];
                    wheelPos3 = sturgeon.key.wheelPositions[3];
                    wheelPos4 = sturgeon.key.wheelPositions[4];
                    wheelPos5 = sturgeon.key.wheelPositions[5];
                    wheelPos6 = sturgeon.key.wheelPositions[6];
                    wheelPos7 = sturgeon.key.wheelPositions[7];
                    wheelPos8 = sturgeon.key.wheelPositions[8];
                    wheelPos9 = sturgeon.key.wheelPositions[9];
                    continue;
                }
            }

            int wheelsBitmap = Wheels.getWheelsBitmap(wheelPos0, wheelPos1, wheelPos2, wheelPos3, wheelPos4, wheelPos5, wheelPos6, wheelPos7, wheelPos8, wheelPos9);
            int control = sturgeon.controlMapping[wheelsBitmap];

            for (byte[] cipher : cipherArray) {
                int in = cipher[i];
                if (in != -1) {
                    freqs[control][in]++;
                    counts[control]++;
                }
            }

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

        int[][] controlBitCounts = new int[10][2];
        double[][][][] controlBitsToCipherBitsFreqs = new double[10][5][2][2];
        for (int control = 0; control < TenBits.SPACE; control++) {
            double vCount = counts[control];
            if (vCount == 0) {
                continue;
            }
            int[] freqs1024v = freqs[control];
            for (int symbol = 0; symbol < FiveBits.SPACE; symbol++) {
                int f = freqs1024v[symbol];
                for (int controlBit = 0; controlBit < 10; controlBit++) {
                    int controlBitState = TenBits.isSet(controlBit, control) ? 1 : 0;
                    controlBitCounts[controlBit][controlBitState] += f;
                    for (int cipherBit = 0; cipherBit < 5; cipherBit++) {
                        int cipherBitState = FiveBits.isSet(cipherBit, symbol) ? 1 : 0;
                        controlBitsToCipherBitsFreqs[controlBit][cipherBit][controlBitState][cipherBitState] += f;
                    }
                }
            }
        }


        for (int controlBit = 0; controlBit < 10; controlBit++) {
            for (int controlBitState = 0; controlBitState < 2; controlBitState++) {
                if (ref == null) {
                    System.out.printf("%d/%d: ", controlBit, controlBitState);
                }
                for (int cipherBit = 0; cipherBit < 5; cipherBit++) {
                    for (int cipherBitState = 0; cipherBitState < 2; cipherBitState++) {
                        controlBitsToCipherBitsFreqs[controlBit][cipherBit][controlBitState][cipherBitState] /= controlBitCounts[controlBit][controlBitState];
                    }
                    if (ref == null) {

                        double score = Math.abs(controlBitsToCipherBitsFreqs[controlBit][cipherBit][controlBitState][0] - 0.5);
                        if (Math.abs(score) > 0.025 || !sturgeon.key.model.equals(Model.T52AB)) {
                            System.out.printf(" %7.4f", score);
                        } else {
                            System.out.print("        ");
                        }
                    }
                }
                if (ref == null) {
                    System.out.println();
                }
            }
            if (ref == null) {
                System.out.println();
            }
        }
        if (ref == null) {
            System.out.println();

            for (int controlBit = 0; controlBit < 10; controlBit++) {
                double sumScore = 0.0;
                System.out.printf("%d:   ", controlBit);
                for (int cipherBit = 0; cipherBit < 5; cipherBit++) {
                    double score = Math.abs(controlBitsToCipherBitsFreqs[controlBit][cipherBit][0][0] - 0.5) +
                            Math.abs(controlBitsToCipherBitsFreqs[controlBit][cipherBit][1][0] - 0.5);
                    sumScore += score;
                    if (Math.abs(score) > 0.05 || !sturgeon.key.model.equals(Model.T52AB)) {
                        System.out.printf(" %7.4f", score);
                    } else {
                        System.out.print("        ");
                    }

                }
                System.out.printf(" == %7.4f \n", sumScore / 5);

            }
        } else {
            double score = 0.0;
            for (int controlBit = 0; controlBit < 10; controlBit++) {
                for (int controlBitState = 0; controlBitState < 2; controlBitState++) {
                    for (int cipherBit = 0; cipherBit < 5; cipherBit++) {
                        for (int cipherBitState = 0; cipherBitState < 2; cipherBitState++) {
                            double diff =  Math.abs(controlBitsToCipherBitsFreqs[controlBit][cipherBit][controlBitState][cipherBitState] -
                                    ref[controlBit][cipherBit][controlBitState][cipherBitState]);
                            score += diff;
                        }
                    }
                }
            }
            controlBitsToCipherBitsFreqs[0][0][0][0] = score;
        }
        return controlBitsToCipherBitsFreqs;
    }

}
