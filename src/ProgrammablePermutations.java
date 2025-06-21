import java.util.*;

public class ProgrammablePermutations {
    private static final int UNDEFINED = -1;
    private static WheelSetting[][] VALID_PROGRAMMABLE_PERM_WHEEL_SETTINGS;
    private static final int[][] JUNCTION_PAIRS = { // [line] [left col/right col]
            {1, 10},
            {8, 9},
            {6, 7},
            {4, 5},
            {2, 3},
    };
    private static final int LEFT_SIDE = 0;
    private static final int RIGHT_SIDE = 1;
    private static final int LEFT_JUNCTION = 0;
    private static final int RIGHT_JUNCTION = 1;

    static boolean compute(WheelSetting[] switches, int[][] perms) {

        for (int[] p : perms) {
            Arrays.fill(p, UNDEFINED);
        }

        int[][] junctionStates = new int[Wheels.NUM_WHEELS + 1][2]; // [junction number][left/right side]

        for (int permControl = 0; permControl < FiveBits.SPACE; permControl++) {
            for (int[] j : junctionStates) {
                Arrays.fill(j, UNDEFINED);
            }
            for (int pairIndex = 0; pairIndex < JUNCTION_PAIRS.length; pairIndex++) {
                junctionStates[JUNCTION_PAIRS[pairIndex][LEFT_JUNCTION]][LEFT_SIDE] = pairIndex;
            }

            boolean ready;
            do {
                for (int controlIndex = 0; controlIndex < 5; controlIndex++) {
                    boolean controlBit = FiveBits.isSet(controlIndex, permControl);
                    int junction1 = switches[controlIndex].getJunction1();
                    int junction2 = switches[controlIndex].getJunction2();
                    final int junction1LeftSide = junctionStates[junction1][LEFT_SIDE];
                    if (junction1LeftSide != UNDEFINED) {
                        if (controlBit) {
                            junctionStates[junction1][RIGHT_SIDE] = junction1LeftSide;
                        } else {
                            junctionStates[junction2][RIGHT_SIDE] = junction1LeftSide;
                        }
                    }
                    final int junction2LeftSide = junctionStates[junction2][LEFT_SIDE];
                    if (junction2LeftSide != UNDEFINED) {
                        if (controlBit) {
                            junctionStates[junction2][RIGHT_SIDE] = junction2LeftSide;
                        } else {
                            junctionStates[junction1][RIGHT_SIDE] = junction2LeftSide;
                        }
                    }
                }
                for (int[] junctionPair : JUNCTION_PAIRS) {
                    if (junctionStates[junctionPair[LEFT_JUNCTION]][RIGHT_SIDE] != UNDEFINED) {
                        junctionStates[junctionPair[RIGHT_JUNCTION]][LEFT_SIDE] =
                                junctionStates[junctionPair[LEFT_JUNCTION]][RIGHT_SIDE];
                    }
                }

                ready = true;
                for (int pairIndex = 0; pairIndex < JUNCTION_PAIRS.length; pairIndex++) {
                    int rightSide = junctionStates[JUNCTION_PAIRS[pairIndex][RIGHT_JUNCTION]][RIGHT_SIDE];
                    if (rightSide == UNDEFINED) {
                        ready = false;
                        break;
                    } else {
                        perms[permControl][pairIndex] = rightSide;
                    }
                }
            } while (!ready);
        }


        for (int inputIndex = 0; inputIndex < 5; inputIndex++) {
            int outputUnique = -1;
            boolean unique = true;
            for (int permControl = 0; permControl < FiveBits.SPACE; permControl++) {
                int output = perms[permControl][inputIndex];
                if (outputUnique == -1) {
                    outputUnique = output;
                } else if (output != outputUnique) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                return false;
            }
        }
        return true;
    }

    synchronized static WheelSetting[][] validSettings() {
        if (VALID_PROGRAMMABLE_PERM_WHEEL_SETTINGS == null) {
            VALID_PROGRAMMABLE_PERM_WHEEL_SETTINGS = validProgrammablePermWheelSettings(false, false);
        }
        return VALID_PROGRAMMABLE_PERM_WHEEL_SETTINGS;
    }
    private static boolean includes(int val, int[] vals, int len) {
        for (int i = 0; i < len; i++) {
            int v = vals[i];
            if (v == val) {
                return true;
            }
        }
        return false;
    }

    private static WheelSetting wheelSetting(int a, int b) {
        a++;
        b++;
        try {
            return WheelSetting.parse(a + "-" + b, Model.T52AB);
        } catch (RuntimeException r) {
            return null;
        }
    }

    private static WheelSetting[][] validProgrammablePermWheelSettings(boolean extended, boolean print) {

        Sturgeon sturgeon = new Sturgeon(Model.T52AB);
        WheelSetting[][] valid = new WheelSetting[extended ? 544 : 360][5];
        WheelSetting ws;

        Set<String> found = new TreeSet<>();

        int[] zeroCounts = new int[25];
        int count = 0;
        int[] vals = new int[10];
        for (int i0 = 0; i0 < 10; i0++) {
            vals[0] = i0;
            for (int i1 = i0 + 1; i1 < 10; i1++) {
                vals[1] = i1;
                ws = wheelSetting(i0, i1);
                if (ws == null) {
                    continue;
                }
                sturgeon.key.wheelSettings[0] = ws;
                for (int i2 = i0 + 1; i2 < 10; i2++) {
                    if (includes(i2, vals, 2)) {
                        continue;
                    }
                    vals[2] = i2;
                    for (int i3 = i2 + 1; i3 < 10; i3++) {
                        if (includes(i3, vals, 3)) {
                            continue;
                        }
                        vals[3] = i3;
                        ws = wheelSetting(i2, i3);
                        if (ws == null) {
                            continue;
                        }
                        sturgeon.key.wheelSettings[1] = ws;
                        for (int i4 = i2 + 1; i4 < 10; i4++) {
                            if (includes(i4, vals, 4)) {
                                continue;
                            }
                            vals[4] = i4;
                            for (int i5 = i4 + 1; i5 < 10; i5++) {
                                if (includes(i5, vals, 5)) {
                                    continue;
                                }
                                vals[5] = i5;
                                ws = wheelSetting(i4, i5);
                                if (ws == null) {
                                    continue;
                                }
                                sturgeon.key.wheelSettings[2] = ws;
                                for (int i6 = i4 + 1; i6 < 10; i6++) {
                                    if (includes(i6, vals, 6)) {
                                        continue;
                                    }
                                    vals[6] = i6;
                                    for (int i7 = i6 + 1; i7 < 10; i7++) {
                                        if (includes(i7, vals, 7)) {
                                            continue;
                                        }
                                        vals[7] = i7;
                                        ws = wheelSetting(i6, i7);
                                        if (ws == null) {
                                            continue;
                                        }
                                        sturgeon.key.wheelSettings[3] = ws;
                                        for (int i8 = i6 + 1; i8 < 10; i8++) {
                                            if (includes(i8, vals, 8)) {
                                                continue;
                                            }
                                            vals[8] = i8;
                                            for (int i9 = i8 + 1; i9 < 10; i9++) {
                                                if (includes(i9, vals, 9)) {
                                                    continue;
                                                }
                                                vals[9] = i9;
                                                ws = wheelSetting(i8, i9);
                                                if (ws == null) {
                                                    continue;
                                                }
                                                sturgeon.key.wheelSettings[4] = ws;

                                                WheelSetting[] switches = new WheelSetting[5];
                                                int nSwitches = 0;
                                                for (WheelSetting setting : sturgeon.key.wheelSettings) {
                                                    if (setting.getFunction() == WheelSetting.WheelFunction.PROGRAMMABLE_PERM_SWITCH) {
                                                        switches[nSwitches++] = setting;
                                                    }
                                                }
                                                if (nSwitches != 5) {
                                                    throw new RuntimeException("Not enough/too many programmable perm switches (need 5): " + nSwitches);
                                                }
                                                int[][] perms = new int[32][5];
                                                String s = "";
                                                for (int w = 0; w < 5; w++) {
                                                    s += sturgeon.key.wheelSettings[w] + ":";
                                                }

                                                if (compute(switches, perms)) {

                                                    Set<Integer> uniques = new TreeSet<>();
                                                    int[][] c = new int[5][5];
                                                    for (int i = 0; i < 32; i++) {
                                                        int[] p = perms[i];
                                                        int bitmap = 0;
                                                        for (int j = 0; j < 5; j++) {
                                                            c[p[j]][j]++;
                                                            bitmap *= 5;
                                                            bitmap += p[j];
                                                        }
                                                        uniques.add(bitmap);
                                                    }
                                                    int zeros = 0;
                                                    for (int i = 0; i < 5; i++) {
                                                        for (int j = 0; j < 5; j++) {
                                                            if (c[i][j] == 0) {
                                                                zeros++;
                                                            }
                                                        }
                                                    }
                                                    if (print) {
                                                        System.out.println();
                                                        System.out.println(s);
                                                        for (int i = 0; i < 5; i++) {
                                                            for (int j = 0; j < 5; j++) {
                                                                System.out.printf("%3d", c[i][j]);
                                                            }
                                                            System.out.println();
                                                        }
                                                        System.out.printf("Zeros: %d\n", zeros);
                                                        System.out.printf("Unique perms: %d\n\n", uniques.size());
                                                    }
                                                    if (uniques.size() >= 30 || extended) {
                                                        found.add(s);
                                                        System.arraycopy(sturgeon.key.wheelSettings, 0, valid[count], 0, 5);
                                                        count++;

                                                        zeroCounts[zeros]++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        if (print) {
            System.out.printf("T52AB has %d valid switch types\n", count);
            for (int zeros = 0; zeros < 25; zeros++) {
                if (zeroCounts[zeros] > 0) {
                    System.out.printf("%2d zeros: %3d types\n", zeros, zeroCounts[zeros]);
                }
            }
        }

//        Set<String> jerva = jerva();
//
//        System.out.printf("Jerva: %d\n", jerva.size());
//        System.out.printf("George: %d\n", found.size());
//
//
//        System.exit(0);
        return valid;
    }

    private static Set<String> jerva() {
        Set<String> settings = new TreeSet<>();
        String f = Utils.readFile("t52permutations.txt");

        boolean newLine = false;
        ArrayList<String> list = new ArrayList<>();
        for (String line : f.split("\r\n")) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.contains("-") && line.replaceAll("[^0-9- ]*", "").length() == line.length()) {
                line = line.replaceAll(" ", "");
                list.add(line);
                newLine = false;
                if (list.size() == 5) {
                    String missing = null;
                    List<String> newList = new ArrayList<>();
                    for (String e : list) {
                        if (e.startsWith("-")) {
                            if (missing == null) {
                                throw new RuntimeException("No missing!");
                            }
                            missing += e;
                            continue;
                        }
                        if (e.length() > 4) {
                            missing = e.split("-")[0];
                            newList.add(e.substring(e.indexOf("-") + 1));
                            continue;
                        }
                        newList.add(e);
                    }
                    if (missing != null) {
                        newList.add(missing);
                    }
                    Set<String> set = new TreeSet<>();
                    for (String e : newList) {

                        String[] parts = e.split("-");
                        int i1 = Integer.parseInt(parts[0]);
                        int i2 = Integer.parseInt(parts[1]);
                        if (i1 > i2) {
                            set.add("" + i2 + "-" + i1);
                        } else {
                            set.add("" + i1 + "-" + i2);
                        }
                    }


                    String s = "";
                    for (String e : set) {
                        s += e + ":";
                    }

                    settings.add(s);
                }
            } else if (!newLine) {
                newLine = true;
                list.clear();
            }
        }
        return settings;
    }

}
