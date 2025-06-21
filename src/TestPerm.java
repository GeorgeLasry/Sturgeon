import java.util.ArrayList;

public class TestPerm {
    static ArrayList<String> findValidSwitchesAB_D(byte[][] plainArray, byte[][] cipherArray, boolean print) {

        if (print) {
            System.out.print("T52AB Switch Test: ");
        }

        int len = plainArray[0].length;
        ArrayList<String> matching = new ArrayList<>();

        Sturgeon sturgeon = new Sturgeon(Model.T52AB);
        int[][] validControl = new int[len][];

        for (WheelSetting[] ws : ProgrammablePermutations.validSettings()) {
            boolean good = true;
            System.arraycopy(ws, 0, sturgeon.key.wheelSettings, 0, 5);
            sturgeon.resetPositionsAndComputeMappings();
            for (int i = 0; i < len; i++) {
                if (sturgeon.getValidWheelBitmaps(plainArray, cipherArray, i, validControl, -1, -1, false) == 0) {
                    good = false;
                    break;
                }
            }
            if (good) {
                String switchSettings = sturgeon.key.getSwitchWheelSettingsString();
                matching.add(switchSettings);
            }

        }
        if (print) {
            System.out.printf("Matching %d (of %d)\n", matching.size(), ProgrammablePermutations.validSettings().length);
            for (String switchSettings : matching) {
                System.out.printf(" %s ", switchSettings);
                if ("1-2:3-4:5-6:7-8:9-10".equals(switchSettings)) {
                    boolean t52c = testPermC(plainArray, cipherArray, false);
                    boolean t52ca = testPermCA(plainArray, cipherArray, false);
                    boolean t52e = testPermE(plainArray, cipherArray, false);
                    if (!t52e) {
                        if (t52c && t52ca) {
                            System.out.print("Could also be T52C or T52CA");
                        } else if (t52c) {
                            System.out.print("Could also be T52C");
                        } else if (t52ca) {
                            System.out.print("Could also be T52CA");
                        }
                    } else {
                        if (t52c && t52ca) {
                            System.out.print("Could also be T52C or T52CA or T52E");
                        } else if (t52c) {
                            System.out.print("Could also be T52C or T52E");
                        } else if (t52ca) {
                            System.out.print("Could also be T52CA or T52E");
                        } else {
                            System.out.print("Could also be T52E");
                        }
                    }
                }
                System.out.println();
            }
            System.out.printf("Matching %d (of %d)\n", matching.size(), ProgrammablePermutations.validSettings().length);

        }
        return matching;
    }
    static boolean testPermC(byte[][] plainArray, byte[][] cipherArray, boolean print) {

        if (print) {
            System.out.print("T52C Switch Test:  ");
        }

        int len = plainArray[0].length;

        Sturgeon sturgeon = new Sturgeon(Model.T52C);
        int[][] validControl = new int[len][];

        for (int i = 0; i < len; i++) {
            if (sturgeon.getValidWheelBitmaps(plainArray, cipherArray, i, validControl, -1, -1, false) == 0) {
                if (print) {
                    System.out.print("Not T52C\n");
                }
                return false;
            }
        }
        if (print) {
            System.out.print("Can be T52C\n");
        }
        return true;
    }
    static boolean testPermCA(byte[][] plainArray, byte[][] cipherArray, boolean print) {

        if (print) {
            System.out.print("T52CA Switch Test: ");
        }
        int len = plainArray[0].length;

        Sturgeon sturgeon = new Sturgeon(Model.T52CA);
        int[][] validControl = new int[len][];


        for (int i = 0; i < len; i++) {
            if (sturgeon.getValidWheelBitmaps(plainArray, cipherArray, i, validControl, -1, -1, false) == 0) {
                if (print) {
                    System.out.print("Not T52CA\n");
                }
                return false;
            }
        }

        if (print) {
            System.out.print("Can be T52CA\n");
        }
        return true;

    }
    static boolean testPermE(byte[][] plainArray, byte[][] cipherArray, boolean print) {

        if (print) {
            System.out.print("T52E Switch Test: ");
        }
        int len = plainArray[0].length;

        Sturgeon sturgeon = new Sturgeon(Model.T52E);
        int[][] validControl = new int[len][];

        for (int i = 0; i < len; i++) {
            if (sturgeon.getValidWheelBitmaps(plainArray, cipherArray, i, validControl, -1, -1, false) == 0) {
                if (print) {
                    System.out.print("Not T52E\n");
                }
                return false;
            }
        }
        if (print) {
            System.out.print("Can be T52E\n");
        }
        return true;

    }

}
