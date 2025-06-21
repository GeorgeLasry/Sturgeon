import flanagan.math.FourierTransform;
import flanagan.plot.PlotGraph;

import java.util.Arrays;


public class FourierTransformExample {

    public static void main(String[] args){



        int nPoints = 1024 * 1024 * 4;  // number of points
        int messageLength = nPoints;

        boolean similar = true;
        boolean fixed = false;
        int nPeaks = 15;

        int messages = (nPoints + messageLength - 1)/messageLength;

        CommonRandom.seed(System.currentTimeMillis());
        long settingsSeed = (similar ? 1 : 0) * System.currentTimeMillis() + 1 * 0;

        ReferenceText.create(false, "shakespeare.txt");

        byte[] cipher = new byte[messages * messageLength];

        final Model model = Model.T52E;
        Sturgeon sturgeon = simulation(model, messages, messageLength, cipher, settingsSeed, true);

        //sturgeon.cycles(1_000_000_000);

        int segLength = nPoints;
        double low = 1.0 / 75;
        double high = 1/45.0;
        double[] ydata = new double[nPoints];



        double pointsPerCycle = 1;
        double deltaT = 1.0D/pointsPerCycle;

        for(int i= 0; i< nPoints; i++){
            ydata[i] = cipher[i];
        }



        // Obtain Power spectrum
        FourierTransform ft0 = new FourierTransform(ydata);
        //    private String[] windowNames = new String[]{"no windowing applied", "Rectangular (square, box-car)", "Bartlett (triangular)", "Welch", "Hann (Hanning)", "Hamming", "Kaiser", "Gaussian"};
        //ft0.setHann();
        ft0.setSegmentLength(segLength);
        ft0.setDeltaT(deltaT);
        double[][] powerSpectrum = ft0.powerSpectrum();

        // Plot power spectrum
        //ft0.plotPowerSpectrum(1/30.0, 1/6.0);
        ft0.plotPowerSpectrum(low, high, "ref");


        int[] peaks = peaks(nPeaks, powerSpectrum, low, high, null);

        //ft0.plotPowerSpectrum();

        double[] ydata2 = new double[nPoints];
        Sturgeon sturgeon2 = simulation(model, messages, messageLength, cipher, settingsSeed, fixed);
        for(int i= 0; i< nPoints; i++){
            ydata2[i] = cipher[i];
        }
        FourierTransform ft1 = new FourierTransform(ydata2);
        //    private String[] windowNames = new String[]{"no windowing applied", "Rectangular (square, box-car)", "Bartlett (triangular)", "Welch", "Hann (Hanning)", "Hamming", "Kaiser", "Gaussian"};
        //ft1.setHann();
        ft1.setSegmentLength(segLength);
        ft1.setDeltaT(deltaT);
        double[][] powerSpectrum2 = ft1.powerSpectrum();
        ft1.plotPowerSpectrum(low, high, "ciphertext-only");

        peaks(nPeaks, powerSpectrum2, low, high, peaks);

        double sum = 0;
        for (int i = 0; i < powerSpectrum[0].length / 5; i++) {
            if (powerSpectrum[0][i] < low || powerSpectrum[0][i] > high) {
                continue;
            }
            double delta = Double.MAX_VALUE;
            for (int k = -1 ; k <= 1; k++) {
                delta = Math.min(Math.pow(powerSpectrum[1][i + k] - powerSpectrum2[1][i], 2) / (powerSpectrum[1][i + k] * powerSpectrum2[1][i]), delta);
            }
            sum += delta;
        }
        System.out.println(sturgeon.key.getWheelSettingsStringFixedSize());
        System.out.println(sturgeon2.key.getWheelSettingsStringFixedSize());
        System.out.println(sum);

        double fA =  73. / (1. - (36./64.)*(36./65.));
        double fB =  71. / (1. - (36./64.)*(36./65.));
        double fC =  69. / (1. - (36./64.)*(36./65.));
        double fD =  67. / (1. - (36./64.)*(36./65.));
        double fE =  65. / (1. - (33./61.)*(28./64.));
        double fF =  64. / (1. - (25./59.)*(28./61.));
        double fG =  61. / (1. - (28./53.)*(34./59.));
        double fH =  59. / (1. - (26./47.)*(25./53.));
        double fJ =  53. / (1. - (37./73.)*(19./47.));
        double fK =  47. / (1. - (29./65.)*(31./67.));

        double[] f = {fA, fB, fC, fD, fE, fF, fG, fH, fJ, fK};
        for (double ff : f) {
            System.out.printf("%8.3f %8.6f\n", ff, 1.0/ff);
        }

    }

    static int[] peaks(int n, double[][] p, double low, double high, int[] other) {

        int[] peaks = new int[n];
        final int length = p[0].length;
        boolean[] used = new boolean[length];
        for (int z = 0; z < n; z++) {

            int max = -1;
            for (int i = 0; i < length; i++) {
                if (p[0][i] < low || p[0][i] > high) {
                    continue;
                }
                if (used[i]) {
                    continue;
                }
                if (max == -1 || p[1][i] > p[1][max]) {
                    max = i;
                }
            }
            if (max == -1) {
                break;
            }
            for (int i = 0; i < length; i++) {
                if (used[i]) {
                    continue;
                }
                for (int k = 2 ; k < 10; k++) {
                    if ((Math.abs(p[0][i] - k * p[0][max]) < 0.0001) || (k * Math.abs(p[0][i] - p[0][max]) < 0.0001)) {
                        markAroundAsUsed(p[1], length, used, i);
                    }
                }
            }


            peaks[z] = max;
            int found = -1;
            if (other != null) {
                for (int x = 0; x < n; x++) {
                    int peak = other[x];
                    for (int k = 2 ; k < 10; k++) {
                        if (Math.abs(p[0][peak] - k * p[0][max]) < 0.0001) {
                            found = x;
                            break;
                        }
                        if (k * Math.abs(p[0][peak] - p[0][max]) < 0.0001) {
                            found = x;
                            break;
                        }
                    }
                }
            }
            System.out.printf("[%12.6f = %8.6f%s]: ", p[1][max], p[0][max], found >= 0? String.format(" **%2d**", found) : "       ");
            for (int k = 1; k < 20; k++) {
                System.out.printf("[%2dx: %8.3f] ", k, k / p[0][max]);
            }
            System.out.println();
            markAroundAsUsed(p[1], length, used, max);
        }
        return peaks;

    }

    private static void markAroundAsUsed(double[] values, int length, boolean[] used, int peak) {
        used[peak] = true;
        for (int i = peak + 1; i < length; i++) {
            if (used[i]) {
                break;
            }
            if (values[i] < values[i - 1]) {
                used[i] = true;
            } else {
                break;
            }
        }
        for (int i = peak - 1; i >= 0; i--) {
            if (used[i]) {
                break;
            }
            if (values[i] < values[i + 1]) {
                used[i] = true;
            } else {
                break;
            }
        }
    }

    static boolean first = true;

    static Sturgeon simulation(Model model, int segments, int len, byte[] cipher, long settingsSeed, boolean fixed) {
        if (settingsSeed == 0) {
            CommonRandom.seed(System.currentTimeMillis());
        } else {
            CommonRandom.seed(settingsSeed);

        }

        Sturgeon sturgeon = new Sturgeon(model);
//        if (first) {
//            sturgeon.key.ktf = true;
//            first = false;
//        }
        if (model.hasProgrammablePermSwitches()) {
            sturgeon.randomize();
        } else {
            sturgeon.randomizeOrder();
        }

//        if (first) {
//            sturgeon = new Sturgeon(new Key(model.toString(), "1-2:3-4:5-6:7-8:9-10:I:II:III:IV:V"));
//            first = false;
//        } else {
//            sturgeon = new Sturgeon(new Key(model.toString(), "1-2:3-4:5-6:7-8:9-10:I:II:III:IV:V"));
//        }
        System.out.println(sturgeon.key.toString());
        if (len == 0 || len > ReferenceText.referenceText.length()) {
            len = ReferenceText.referenceText.length();
        }
        CommonRandom.r.setSeed(System.currentTimeMillis());

        int pos = len == ReferenceText.referenceText.length() ? 0 : CommonRandom.r.nextInt(ReferenceText.referenceText.length() - len * segments);


        for (int d = 0; d < segments; d++) {

            byte[] plainSegment = Alphabet.fromString(ReferenceText.referenceText.substring(pos + len *d, pos + len * (d + 1)));
            if (fixed) {
                Arrays.fill(plainSegment, (byte) 0);
            }

            CommonRandom.r.setSeed(System.nanoTime());
            sturgeon.randomPositions();
            byte[] cipherSegment = new byte[len];
            sturgeon.encryptDecrypt(plainSegment, cipherSegment, len, null, true);
            System.arraycopy(cipherSegment, 0, cipher, len *d, len);
        }
        return sturgeon;
    }
}