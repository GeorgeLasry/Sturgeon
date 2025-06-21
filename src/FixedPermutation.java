public class FixedPermutation {
    public static final int[][] FIXED_PERMUTATION = compute();

    public static int[][] compute() {

        int[][] perms = new int[FiveBits.SPACE][];

        for (byte permControl = 0; permControl < FiveBits.SPACE; permControl++) {
            int[] perm = perms[permControl] = new int[]{0, 1, 2, 3, 4};

            // Switch if control is false

            boolean c1 = !FiveBits.isSet(0, permControl);
            boolean c2 = !FiveBits.isSet(1, permControl);
            boolean c3 = !FiveBits.isSet(2, permControl);
            boolean c4 = !FiveBits.isSet(3, permControl);
            boolean c5 = !FiveBits.isSet(4, permControl);


            if (c1) swap(perm, 1, 5);
            if (c2) swap(perm, 4, 5);
            if (c3) swap(perm, 3, 4);
            if (c4) swap(perm, 2, 3);
            if (c5) swap(perm, 1, 2);

        }
        return perms;

    }

    // i, j from 1 to 5
    private static void swap(int[] perm, int i, int j) {
        i--;
        j--;
        int temp = perm[i];
        perm[i] = perm[j];
        perm[j] = temp;
    }
}
