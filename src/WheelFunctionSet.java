import java.util.ArrayList;

public class WheelFunctionSet {
    final static int PERM_1 = 0;
    final static int PERM_3 = PERM_1 + 1;
    final static int PERM_5 = PERM_3 + 1;
    final static int PERM_7 = PERM_5 + 1;
    final static int PERM_9 = PERM_7 + 1;
    final static int XOR_I = PERM_9 + 1;
    final static int XOR_II = XOR_I + 1;
    final static int XOR_III = XOR_II + 1;
    final static int XOR_IV = XOR_III + 1;
    final static int XOR_V = XOR_IV + 1;
    final static String[] FUNCTION_STRING = {"1", "3", "5", "7", "9", "I", "II", "III", "IV", "V",};

    final static WheelFunctionSet SELECTION_PERM_ALL = new WheelFunctionSet(PERM_1, PERM_3, PERM_5, PERM_7, PERM_9);
    final static WheelFunctionSet SELECTION_XOR_ALL = new WheelFunctionSet(XOR_I, XOR_II, XOR_III, XOR_IV, XOR_V);
    final static WheelFunctionSet SELECTION_XOR_II = new WheelFunctionSet(XOR_II);
    final static WheelFunctionSet SELECTION_ALL = new WheelFunctionSet(PERM_1, PERM_3, PERM_5, PERM_7, PERM_9, XOR_I, XOR_II, XOR_III, XOR_IV, XOR_V);
    final static WheelFunctionSet SELECTION_NONE = new WheelFunctionSet();
    final static WheelFunctionSet SELECTION_C_7_9_I_IV = new WheelFunctionSet(PERM_7, PERM_9, XOR_I, XOR_IV);
    final static WheelFunctionSet SELECTION_CA_3_7_III_V = new WheelFunctionSet(PERM_3, PERM_5, XOR_III, XOR_V);
    final static WheelFunctionSet SELECTION_CA_3_7_II_III = new WheelFunctionSet(PERM_3, PERM_7, XOR_II, XOR_III);

    public int selectionBitmap;

    WheelFunctionSet(int... wheelsFunction) {
        selectionBitmap = 0;
        for (int wheelFunction : wheelsFunction) {
            if (wheelFunction >= 0 && wheelFunction <= 9) {
                selectionBitmap |= (0x1 << wheelFunction);
            }
        }
    }
    WheelFunctionSet(WheelFunctionSet wheelFunctionSet) {
        setSelectionBitmap(wheelFunctionSet.selectionBitmap);
    }
    private WheelFunctionSet setSelectionBitmap(int selectionBitmap) {
        this.selectionBitmap = selectionBitmap;
        return this;
    }
    ArrayList<Integer> functions(){
        ArrayList<Integer> functions = new ArrayList<>();
        for (int function = 0; function < 10; function++) {
            if (((selectionBitmap >> function) & 0x1 ) == 0x1) {
                functions.add(function);
            }
        }
        return functions;
    }
    int size(){
        int count = 0;
        for (int function = 0; function < 10; function++) {
            if (((selectionBitmap >> function) & 0x1 ) == 0x1) {
                count++;
            }
        }
        return count;
    }
    WheelFunctionSet random(int count) {
        do {
            selectionBitmap = CommonRandom.r.nextInt(1024);
        } while (size() != count);
        return this;
    }
    WheelFunctionSet remaining() {
        WheelFunctionSet others = new WheelFunctionSet();
        others.selectionBitmap = (~selectionBitmap) & 0x3ff;
        return others;
    }
    WheelFunctionSet union(WheelFunctionSet other) {
        WheelFunctionSet union = new WheelFunctionSet();
        union.selectionBitmap = selectionBitmap | other.selectionBitmap;
        return union;
    }
    boolean equals(WheelFunctionSet other) {
        return selectionBitmap == other.selectionBitmap;
    }

    int getClassXor32(int xorControl) {
        return xorControl & (selectionBitmap >> 5);
    }
    int getClass1024(int controlBitmap) {
        return controlBitmap & selectionBitmap;
    }

    private boolean intersectsWith(WheelFunctionSet other) {
        return (selectionBitmap & other.selectionBitmap) != 0x0;
    }
    static ArrayList<WheelFunctionSet> topWheelFunctionSets(Model model, int tops, int count, boolean ic, WheelFunctionSet wheelFunctionSetToPreserve, boolean print) {

        wheelFunctionSetToPreserve = wheelFunctionSetToPreserve == null ? new WheelFunctionSet() : wheelFunctionSetToPreserve;
        Sturgeon sturgeon = new Sturgeon(model);
        byte[][] plainArray = new byte[1][];
        plainArray[0] = Alphabet.fromString(ReferenceText.referenceText.substring(0, Math.min(ReferenceText.referenceText.length(), 1_000_000)));
        int len = plainArray[0].length;
        byte[][] cipherArray = new byte[1][len];
        sturgeon.encryptDecrypt(plainArray[0], cipherArray[0], len, null, true);

        if (print) {
            sturgeon.key.print("Key");
            System.out.println("Top function set selections (computed on reference language) - sorted by " + (ic ? "classIC" : "Class"));
        }
        double[] allScores = new double[1024];
        ArrayList<Integer> allSelectionBitmaps = new ArrayList<>();
        for (int selectionBitmap = 0; selectionBitmap < 1024; selectionBitmap++) {
            WheelFunctionSet wheelFunctionSet = new WheelFunctionSet();
            if (TenBits.count(selectionBitmap) != count) {
                continue;
            }
            wheelFunctionSet.setSelectionBitmap(selectionBitmap);

            if (wheelFunctionSet.intersectsWith(wheelFunctionSetToPreserve)) {
                continue;
            }
            sturgeon.computeClassFreqRef(ReferenceText.languageFreq, wheelFunctionSet.union(wheelFunctionSetToPreserve));
            double score = sturgeon.classScore(cipherArray, wheelFunctionSet.union(wheelFunctionSetToPreserve), ic);
            if (Math.abs(score - 1.0) < 0.000001) {
                continue;
            }
            allScores[selectionBitmap] = score;
            allSelectionBitmaps.add(selectionBitmap);
            if (print) {
                System.out.print(".");
            }
        }
        if (print) {
            System.out.println();
        }
        allSelectionBitmaps.sort((selectionBitmap1, selectionBitmap2) -> Double.compare(allScores[selectionBitmap2], allScores[selectionBitmap1]));

        ArrayList<WheelFunctionSet> topFunctionSets = new ArrayList<>();
        for (int k = 0; k < Math.min(tops, allSelectionBitmaps.size()); k++) {
            int selectionBitmap = allSelectionBitmaps.get(k);
            WheelFunctionSet functionSet = new WheelFunctionSet().setSelectionBitmap(selectionBitmap);
            String s = functionSet.toString();
            double score2 = sturgeon.classScore(cipherArray, functionSet.union(wheelFunctionSetToPreserve), !ic);
            if (print) {
                System.out.printf("[%2d] Count %d %s %f %f %s\n", k, count, TenBits.getString(selectionBitmap), allScores[selectionBitmap], score2, s);
            }
            topFunctionSets.add(functionSet);
        }
        if (print) {
            System.out.println();
        }

        return topFunctionSets;

    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int functions = 0; functions < 10; functions++) {
            if (TenBits.isSet(functions, selectionBitmap)) {
                s.append(FUNCTION_STRING[functions]).append(", ");
            }
        }
        if (s.toString().endsWith(", ")) {
            s = new StringBuilder(s.substring(0, s.length() - 2));
        }
        return s.toString();
    }

    boolean containsWheel(int w, WheelSetting[] wheelSettings) {
        return TenBits.isSet(WheelSetting.wheelFunction0to9(w, wheelSettings), selectionBitmap);
    }
    boolean containsOnlyXor() {
        return (selectionBitmap > SELECTION_PERM_ALL.selectionBitmap) && ((selectionBitmap & SELECTION_PERM_ALL.selectionBitmap) == 0x0);
    }

}
