import java.util.ArrayList;

public class Perms {

    public static int[][] PERMS1 = perms(1);
    public static int[][] PERMS2 = perms(2);
    public static int[][] PERMS3 = perms(3);
    public static int[][] PERMS4 = perms(4);
    public static int[][] PERMS5 = perms(5);
    public static int[][] PERMS6 = perms(6);
    public static int[][] PERMS7 = perms(7);
    public static int[][] PERMS8 = perms(8);
    public static int[][] PERMS9 = perms(9);
    public static int[][] PERMS10 = perms(10);

    public static int[][][] PERMS = new int[][][]{null, PERMS1, PERMS2, PERMS3, PERMS4, PERMS5, PERMS6, PERMS7, PERMS8, PERMS9, PERMS10};

    public static int[][] perms(int n) {
        ArrayList<int[]> allPerms = new ArrayList<>();
        ArrayList<Integer> left = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            left.add(i);
        }
        perm(n, new int[] {}, left, allPerms);
        int[][] perms = new int[allPerms.size()][];
        for (int i = 0; i < allPerms.size(); i++) {
            perms[i] = allPerms.get(i);
        }
        return perms;
    }
    private static void perm(int n, int[] perm, ArrayList<Integer> left, ArrayList<int[]>allPerms) {
        if (perm.length == n) {
            allPerms.add(perm);
            return;
        }

        for (int i = 0; i < left.size(); i++) {
            int[] newPerm = new int[perm.length + 1];
            System.arraycopy(perm, 0, newPerm, 0, perm.length);
            newPerm[perm.length] = left.get(i);
            ArrayList<Integer> newLeft = new ArrayList<>(left);
            newLeft.remove(i);
            perm(n, newPerm, newLeft, allPerms);
        }
    }

    public static void main(String[] a) {
        int[][] p10 = perms(10);
        return;
    }
}
