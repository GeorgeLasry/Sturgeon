public class Partition10 {

    static final int[][][] PARTITIONS = createPartitions();

    static void getSelection(int partitionIndex, int type, int permIndex, int[] selection) {
        for (int r = 0; r < 5 ; r++) {
            selection[r] = PARTITIONS[partitionIndex][type][Perms.PERMS[5][permIndex][r]];
        }
    }
    static void getSelection(int partitionIndex, int type, int[] selection) {
        System.arraycopy(PARTITIONS[partitionIndex][type], 0, selection, 0, 5);
    }
    static void getPermutation(int partitionIndex, int perm5Index0, int perm5Index1, int[] selection) {
        for (int i = 0; i < 5; i++) {
            selection[Perms.PERMS[5][perm5Index0][i]] = PARTITIONS[partitionIndex][0][i];
            selection[Perms.PERMS[5][perm5Index1][i] + 5 ] = PARTITIONS[partitionIndex][1][i];
        }
    }
    static void getPermutation(int permutationIndex,  int[] selection) {
        int perm5Index0 = permutationIndex % 120;
        permutationIndex /= 120;
        int perm5Index1 = permutationIndex % 120;
        permutationIndex /= 120;
        int partitionIndex = permutationIndex % 252;
        permutationIndex /= 252;
        getPermutation(partitionIndex, perm5Index0, perm5Index1, selection);
    }

    private static int[][][]  createPartitions() {
        int[][][] partitions = new int[252][2][5];
        int count = 0;

        for (int symbol = 0; symbol < 1024; symbol++) {

            if (TenBits.count(symbol) != 5) {
                continue;
            }
            int[][] partition = partitions[count];
            int c0 = 0, c1 = 0;
            for (int b = 0; b < 10; b++) {
                if (TenBits.isSet(b, symbol)) {
                    partition[1][c1++] = b;
                } else {
                    partition[0][c0++] = b;
                }
            }
            count++;
        }
        return partitions;
    }

    public static void main(String[] args) {
        System.out.printf("%d \n", PARTITIONS.length);
        int[] selection = new int[5];
        int partition = CommonRandom.r.nextInt(PARTITIONS.length);
        getSelection(partition, 0, selection);
        System.out.printf("%d %d %d %d %d\n", selection[0], selection[1], selection[2], selection[3], selection[4]);
        getSelection(partition, 0, CommonRandom.r.nextInt(Perms.PERMS[5].length), selection);
        System.out.printf("%d %d %d %d %d\n", selection[0], selection[1], selection[2], selection[3], selection[4]);
        getSelection(partition, 1,  selection);
        System.out.printf("%d %d %d %d %d\n", selection[0], selection[1], selection[2], selection[3], selection[4]);
        getSelection(partition, 1, CommonRandom.r.nextInt(Perms.PERMS[5].length), selection);
        System.out.printf("%d %d %d %d %d\n", selection[0], selection[1], selection[2], selection[3], selection[4]);
    }

}
