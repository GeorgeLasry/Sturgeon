import java.lang.reflect.Array;
import java.util.*;


class Stats {

    private static long quadgramStatsFlat[] = new long[32 * 32 * 32 * 32];
    private static int[] symbolClass = {
            0, 1, 1, 2,
            1, 2, 2, 3,
            1, 2, 2, 3,
            2, 3, 3, 4,
            1, 2, 2, 3,
            2, 3, 3, 4,
            2, 3, 3, 4,
            3, 4, 4, 5,
    };

    private static double computeIc(double[] freq) {
        double ic = 0;
        double count = 0;
        for (double f : freq) {
            ic += f * f;
            count += f;
        }
        return ic / (count * count);
    }

    static double[] computeFreq(int len, double[] freq, byte[] stream) {
        return computeFreq(len, freq, stream, null);
    }

    static double[] computeFreq(int len, double[] freq, byte[] stream, double[] freq2) {
        if (freq == null) {
            freq = new double[32];
        } else {
            Arrays.fill(freq, 0);
        }
        int count = 0;
        byte c;
        for (int i = 0; i < len; i++) {
            if ((c = stream[i]) != -1) {
                freq[c] += 1;
                count++;
            }
        }
        for (int i = 0; i < 32; i++) {
            freq[i] /= count;
        }

        if (freq2 != null) {
            for (int i = 0; i < 32; i++) {
                System.out.printf("%c %c %s %s %5.2f - %5.2f = %5.2f (%3.0f%%)\n",
                        Alphabet.ALPHABET_LETTERS_ARRAY[i],
                        Alphabet.BRITISH_ALPHABET_LETTERS_ARRAY[i],
                        (""+Alphabet.ALPHABET_FIGURES_ARRAY[i]).replaceAll("!", " "),
                        FiveBits.getString(i),
                        100.0*freq[i], 100.0*freq2[i], 100.0*(freq2[i] - freq[i]), 100.0*(freq2[i] - freq[i])/Math.max(freq[i], freq2[i]));
            }
            return freq;
        }

        return freq;
    }

    static void printfFreq(double[] freq, double[] freq2) {


        for (int i = 0; i < 32; i++) {
            System.out.printf("%c %c %s %s %5.2f - %5.2f = %5.2f (%3.0f%%)\n",
                    Alphabet.ALPHABET_LETTERS_ARRAY[i],
                    Alphabet.BRITISH_ALPHABET_LETTERS_ARRAY[i],
                    ("" + Alphabet.ALPHABET_FIGURES_ARRAY[i]).replaceAll("!", " "),
                    FiveBits.getString(i),
                    100.0 * freq[i], 100.0 * freq2[i], 100.0 * (freq2[i] - freq[i]), 100.0 * (freq2[i] - freq[i]) / Math.max(freq[i], freq2[i]));
        }
        System.out.println();

    }

    static double computeIc(int len, byte[] stream) {

        double[] freq = new double[32];

        int count = 0;
        byte c;

        for (int i = 0; i < len; i++) {
            if ((c = stream[i]) != -1){
                freq[c] += 1;
                count++;
            }
        }
        for (int i = 0; i < 32; i++) {
            freq[i] /= count;
        }
        return computeIc(freq);
    }

    public static double computeIcClasses(int len, byte[] stream) {

        double[] freq = new double[6];

        int count = 0;
        byte c;
        for (int i = 0; i < len; i++) {
            if ((c = stream[i]) != -1) {
                count++;
            }
        }
        double delta = 1.0 / count;
        for (int i = 0; i < len; i++) {
            if ((c = stream[i]) != -1) {
                freq[symbolClass[c]] += delta;
            }
        }
        return computeIc(freq);
    }

    public static double computeIc(int len, byte[] stream, byte[] ref) {

        double[] freq = new double[32];

        double delta = 1.0 / len;

        for (int i = 0; i < len; i++) {
            freq[stream[i] ^ ref[i]] += delta;
        }
        return computeIc(freq);
    }

    static double diffClass(int len, byte[] stream, byte[] ref) {


        long sum = 0;
        for (int i = 0; i < len; i++) {
            if (stream[i] == -1 || ref[i] == -1) {
                continue;
            }
            sum += symbolClass[stream[i] ^ ref[i]];
            //sum += (symbolClass[stream[i] ^ referenceText[i]] % 2 == 0) ? 1 : 0;
            //sum += symbolClass[stream[i] ^ referenceText[i]] * 2;
            //sum += Math.abs(symbolClass[stream[i]] - symbolClass[referenceText[i]]);
            //sum += (stream[i] == referenceText[i]) ? 1 : 0;
        }

        return (1.0 * (len * 5 - sum)) / (len * 5);
        //return (1.0 * (len * 5 * 2 - sum)) / (len * 5 * 2);
        //return (1.0 * (len * 5 * 3 - sum)) / (len * 5 * 3);
        //return (1.0 * sum) / len;
    }

    static double matchStreamWithFreq(int len, byte[] stream1, double[] freq2) {
        len = Math.min(stream1.length, len);
        double[] freq1 = new double[32];
        computeFreq(len, freq1, stream1);
        return matchTwoFreqs(freq1, freq2);
    }

    private static double matchTwoFreqs(double[] freq1, double[] freq2) {

        if (freq2 != null) {
            double sum = 0.0;
            for (int i = 0; i < freq1.length; i++) {
                sum += freq1[i] * freq2[i];
            }
            return sum;
        }

        return computeIc(freq1);

    }

    private static long evalQuad(byte[] plaintext, int start, int plaintextLength) {

        if (plaintextLength < 4 + start) {
            return 0;
        }

        // for performance we always have l1,l2 and l3 on hand, and lookup each time only for l4. This saves
        // access to arrays.
        int l1 = plaintext[start];
        int l2 = plaintext[start+1];
        int l3 = plaintext[start+2];
        long val = 0;
        for (int i = start + 3; i < plaintextLength; i++) {

            int l4 = plaintext[i];
            val += quadgramStatsFlat[(((((l1 << 5) + l2) << 5) + l3) << 5) + l4];

            l1 = l2;
            l2 = l3;
            l3 = l4;
        }
        return val / (plaintextLength - 3);
    }

    static long evalQuad(byte[][] plaintext, int start, int plaintextLength) {
        int score = 0;
        for (byte[] plain : plaintext) {
            score += evalQuad(plain, start, plaintextLength);
        }
        return score / plaintext.length;
    }

    private static long evalQuad(byte[] plaintext, int start, int plaintextLength, long[] scores) {

        if (plaintextLength < 4 + start) {
            return 0;
        }

        // for performance we always have l1,l2 and l3 on hand, and lookup each time only for l4. This saves
        // access to arrays.
        int l1 = plaintext[start];
        int l2 = plaintext[start+1];
        int l3 = plaintext[start+2];
        long val = 0;
        long diff;
        for (int i = start + 3; i < plaintextLength; i++) {

            int l4 = plaintext[i];
            diff = quadgramStatsFlat[(((((l1 << 5) + l2) << 5) + l3) << 5) + l4];
            val += diff;
            scores[i] += diff;
            l1 = l2;
            l2 = l3;
            l3 = l4;
        }
        return val / (plaintextLength - 3);
    }


    static long evalQuad(byte[][] plaintext, int start, int plaintextLength, long[] scores) {
        int score = 0;
        Arrays.fill(scores, 0);
        for (byte[] plain : plaintext) {
            score += evalQuad(plain, start, plaintextLength, scores);
        }
        long min = 100000000;
        long max = 0;
        for (int i = 0; i < plaintextLength; i++) {
            long s = scores[i];
            if (score == 0) {
                continue;
            }
            min = Math.min(min, s);
            max = Math.max(max, s);
        }
        for (int i = 0; i < plaintextLength; i++) {
            long s = scores[i];
            if (score == 0) {
                continue;
            }
            scores[i] = 10*(scores[i] - min)/(max - min);
        }

        return score / plaintext.length;
    }

    static void buildQuadGrams(byte[] input, int len) {

        for (int i = 0; i < len - 3; i++) {
            int l1 = input[i];
            int l2 = input[i + 1];
            int l3 = input[i + 2];
            int l4 = input[i + 3];

            int index = (((((l1 << 5) + l2) << 5) + l3) << 5) + l4;
            quadgramStatsFlat[index]++;
        }

        long minVal = Long.MAX_VALUE;
        for (int l1 = 0; l1 < 32; l1++) {
            for (int l2 = 0; l2 < 32; l2++) {
                for (int l3 = 0; l3 < 32; l3++) {
                    for (int l4 = 0; l4 < 32; l4++) {
                        int index = (((((l1 << 5) + l2) << 5) + l3) << 5) + l4;
                        long val = quadgramStatsFlat[index];
                        if ((val > 0) && (val < minVal)) {
                            minVal = val;
                        }
                    }
                }
            }
        }

        final HashMap<String, Long> map = new HashMap<>();
        for (int l1 = 0; l1 < 32; l1++) {
            for (int l2 = 0; l2 < 32; l2++) {
                for (int l3 = 0; l3 < 32; l3++) {
                    for (int l4 = 0; l4 < 32; l4++) {
                        int index = (((((l1 << 5) + l2) << 5) + l3) << 5) + l4;
                        long val = quadgramStatsFlat[index];
                        if (val > 0) {
                            quadgramStatsFlat[index] = (long) (10000.0 * Math.log((1.0 * val) / (1.0 * minVal)));
                        }
                    }
                }
            }
        }

    }

    static double[] computeBigramsFreq(int len, double[] freq, byte[] input) {

        if (freq == null) {
            freq = new double[36];
        } else {
            Arrays.fill(freq, 0.0);
        }
        double delta = 1.0 / (len-1);
        for (int i = 0; i < len - 1; i++) {
            int l1 = symbolClass[input[i]];
            int l2 = symbolClass[input[i + 1]];

            int index = l1 * 6 + l2;
            freq[index] += delta;
        }

        return freq;

    }
    static double matchWithBigramsFreq(int len, byte[] input, double[] freq2) {
        len = Math.min(input.length, len);
        computeBigramsFreq(len, freq2, input);
        return computeMatch(freq2, freq2);
    }

    static double computeIcClass(int len, byte[] input) {

        double[] freq = new double[36];

        for (int i = 0; i < len - 1; i++) {
            if (input[i] == -1 || input[i + 1] == -1) {
                continue;
            }
            int l1 = symbolClass[input[i]];
            int l2 = symbolClass[input[i + 1]];

            int index = l1 * 6 + l2;
            freq[index] += 1;
        }
        for (int i = 0; i < 36; i++) {
            freq[i] /= len;
        }
        return computeIc(freq);
    }
    private static double computeMatch(double[] freq1, double[] freq2) {

        double match = 0;
        for (int i = 0; i < freq1.length; i++) {
            match += Math.pow(freq1[i]-freq2[i], 2);
        }
        return match * 10000000.0;
    }

    static void printfFreq(double[] freq) {


        for (int i = 0; i < 32; i++) {
            System.out.printf("%c %c %s %s %5.1f%%\n",
                    Alphabet.ALPHABET_LETTERS_ARRAY[i],
                    Alphabet.BRITISH_ALPHABET_LETTERS_ARRAY[i],
                    ("" + Alphabet.ALPHABET_FIGURES_ARRAY[i]).replaceAll("!", " "),
                    FiveBits.getString(i),
                    100.0 * freq[i]);
        }
        System.out.println();
    }
}
