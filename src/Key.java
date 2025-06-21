import java.util.*;

public class Key {
    // Stored by constructor or by resetPositionsAndComputeMappings().
    Model model = Model.T52AB;
    int[] wheelPositions = new int[Wheels.NUM_WHEELS];
    WheelSetting[] wheelSettings = new WheelSetting[Wheels.NUM_WHEELS]; // change to default.
    int[] wheelMsgSettings = null; // change to neutral.
    boolean ktf = false;
    double score;

    Key(Key key) {
        set(key.model, key.wheelSettings, key.wheelPositions, key.wheelMsgSettings);
        this.ktf = key.ktf;
    }
    Key(Model model, int[] wheelPositions, WheelSetting[] wheelSettings, int[] wheelMsgSettings) {
        set(model, wheelSettings, wheelPositions, wheelMsgSettings);
    }
    Key(String modelString, String wheelPositionsString, String wheelSettingsString, String wheelMsgSettingsString) {
        set(modelString, wheelPositionsString, wheelSettingsString, wheelMsgSettingsString);
    }
    Key(String modelString, String wheelPositionsString, String wheelSettingsString) {
        set(modelString, wheelPositionsString, wheelSettingsString, null);
    }
    Key(String modelString, String wheelPositionsString, String wheelSettingsString, boolean ktf) {
        set(modelString, wheelPositionsString, wheelSettingsString, null);
        this.ktf = ktf;
    }

    Key(String modelString, String wheelSettingsString) {
        set(modelString, null, wheelSettingsString, null);
    }

    static String getWheelPositionsString(int[] positions) {
        StringBuilder string = new StringBuilder();
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            if (w != 0) {
                string.append(":");
            }
            int display = positions[w] + 1;
            if (display < 10) {
                string.append("0").append(display);
            } else {
                string.append(display);
            }
        }
        return string.toString();
    }

    static Key defaultKey(Model model) {
        return switch (model) {
            case T52C, T52CA ->
                //key = new Key(model.toString(), SturgeonStatic.DEFAULT_WHEEL_POSITIONS, SturgeonStatic.DEFAULT_PERM_AND_XOR_WHEELS, SturgeonStatic.MSG_KEY_LETTERS_NEUTRAL);
                    new Key(model.toString(), Wheels.DEFAULT_WHEEL_POSITIONS, Wheels.DEFAULT_PERM_AND_XOR_WHEELS);
            case T52AB, T52D ->
                    new Key(model.toString(), Wheels.DEFAULT_WHEEL_POSITIONS, Wheels.DEFAULT_PROGRAMMABLE_PERM_AND_XOR_WHEELS);
            case T52E, T52EE, T52AA ->
                    new Key(model.toString(), Wheels.DEFAULT_WHEEL_POSITIONS, Wheels.DEFAULT_PERM_AND_XOR_WHEELS);
            default -> null;
        };
    }

    void alphabetAnalysis() {
        byte[][] alphabet = new byte[TenBits.SPACE][FiveBits.SPACE];

        if (model.hasProgrammablePermSwitches()) {
            WheelSetting[] switches = new WheelSetting[5];
            int nSwitches = 0;
            for (WheelSetting setting : wheelSettings) {
                if (setting.getFunction() == WheelSetting.WheelFunction.PROGRAMMABLE_PERM_SWITCH) {
                    switches[nSwitches++] = setting;
                }
            }
            int[][] perms = new int[FiveBits.SPACE][5];
            ProgrammablePermutations.compute(switches, perms);
            for (int control = 0; control < TenBits.SPACE; control++) {
                for (byte input = 0; input < FiveBits.SPACE; input++) {
                    byte output = SturgeonAlphabets.computeAlphabetEntry(input, control, perms);
                    alphabet[control][input] = output;
                }
            }
        } else {
            int[] precomputedSRLogic = SRLogic.precomputedSRLogic(this);
            for (int control = 0; control < TenBits.SPACE; control++) {
                for (byte input = 0; input < FiveBits.SPACE; input++) {
                    byte output = SturgeonAlphabets.computeAlphabetEntry(input, model.hasSRLogic() ? precomputedSRLogic[control] : control, FixedPermutation.FIXED_PERMUTATION);
                    alphabet[control][input] = output;
                }
            }


            if (model.hasSRLogic()) {
                Set<Integer> controlSet = new TreeSet<>();
                for (int control = 0; control < TenBits.SPACE; control++) {
                    controlSet.add(precomputedSRLogic[control]);
                }
                System.out.printf("Control Set size = %d\n", controlSet.size());
            }

        }


        Set<String> alphabetSet = new TreeSet<>();
        int[][] counts = new int[FiveBits.SPACE][FiveBits.SPACE];
        for (int control = 0; control < TenBits.SPACE; control++) {
            StringBuilder s = new StringBuilder();
            for (byte input = 0; input < FiveBits.SPACE; input++) {
                byte output = alphabet[control][input];
                s.append("<").append(output).append(">");
                counts[input][output]++;
            }
            alphabetSet.add(s.toString());
        }
        System.out.printf("Alphabet Set size = %d\n", alphabetSet.size());


        for (byte input = 0; input < FiveBits.SPACE; input++) {
            System.out.printf("%2d: ", input);
            for (byte output = 0; output < FiveBits.SPACE; output++) {

                System.out.printf("%2d, ", counts[input][output]);
            }
            System.out.println();
        }


    }


    String getWheelSettingsString() {
        StringBuilder string = new StringBuilder();
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            if (w != 0) {
                string.append(":");
            }
            string.append(wheelSettings[w].toString());
        }
        return string.toString();
    }

    String getWheelSettingsStringFixedSize() {
        StringBuilder string = new StringBuilder();
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            if (w != 0) {
                string.append(":");
            }
            string.append(String.format("%-4s", wheelSettings[w].toString()));
        }
        return string.toString();
    }

    String getSwitchWheelSettingsString() {
        StringBuilder string = new StringBuilder();
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            if (wheelSettings[w].getFunction() != WheelSetting.WheelFunction.XOR) {
                if (!string.isEmpty()) {
                    string.append(":");
                }
                string.append(wheelSettings[w].toString());
            }
        }
        return string.toString();
    }
    String getWheelPositionsString() {
        return getWheelPositionsString(this.wheelPositions);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(String.format("%s ", getModelString()));
        s.append(String.format("%s ", getWheelPositionsString()));
        s.append(String.format("%s ", getWheelSettingsString()));
        if (model.hasMessageKeyUnit()) {
            s.append(String.format("%s ", getWheelMsgSettingsString()));
        }
        if (model.hasIrregularStepping()) {
            s.append(String.format("%s ", ktf ? "KTF" : ""));
        }

        return s.toString();
    }
    void print(String desc) {
       System.out.printf("Key %s: %s\n", desc, this);
    }

    private void set(Model model, WheelSetting[] wheelSettings, int[] wheelPositions, int[] wheelMsgSettings) {
        if (model != null) {
            this.model = model;
        }
        if (wheelPositions != null) {
            System.arraycopy(wheelPositions, 0, this.wheelPositions, 0, Wheels.NUM_WHEELS);
        }
        if (wheelSettings != null) {
            System.arraycopy(wheelSettings, 0, this.wheelSettings, 0, Wheels.NUM_WHEELS);
            assertWheelSettings();
        }
        if (wheelMsgSettings != null) {
            if (this.wheelMsgSettings == null) {
                this.wheelMsgSettings = Arrays.copyOf(wheelMsgSettings, Wheels.NUM_MSG_SETTINGS);
            } else {
                System.arraycopy(wheelMsgSettings, 0, this.wheelMsgSettings, 0, Wheels.NUM_MSG_SETTINGS);
            }
        } else {
            this.wheelMsgSettings = null;
        }
    }
    private void set(String modelString, String wheelPositionsString, String wheelSettingsString,
                     String wheelMsgSettingsString) {
        if (modelString != null) {
            parseModelString(modelString);
        }
        if (wheelPositionsString != null) {
            parseWheelPositionsString(wheelPositionsString);
        }
        if (wheelSettingsString != null) {
            parseWheelSettingsString(wheelSettingsString);
            assertWheelSettings();
        }
        if (wheelMsgSettingsString != null) {
            parseWheelMsgSettingsString(wheelMsgSettingsString);
        } else {
            this.wheelMsgSettings = null;
        }
    }

    private void assertWheelSettings() {
        int[] countXor = new int[Wheels.NUM_WHEELS / 2];
        int[] countPermSimple = new int[Wheels.NUM_WHEELS / 2];
        int[] countPermFlexible = new int[Wheels.NUM_WHEELS + 1];
        for (WheelSetting s : wheelSettings) {
            switch (s.getFunction()) {
                case PROGRAMMABLE_PERM_SWITCH:
                    countPermFlexible[s.getJunction1()]++;
                    countPermFlexible[s.getJunction2()]++;
                    if (countPermFlexible[s.getJunction1()] > 1 || countPermFlexible[s.getJunction2()] > 1) {
                        throw new RuntimeException("Invalid Wheel Settings: " + getWheelSettingsString()
                                + " Setting: " + s + " in conflict with other wheel setting");
                    }
                    break;
                case FIXED_PERM_SWITCH:
                    countXor[s.getFunctionIndex0to4()]++;
                    if (countXor[s.getFunctionIndex0to4()] > 1) {
                        throw new RuntimeException("Invalid Wheel Settings: " + getWheelSettingsString()
                                + " Setting: " + s + " appears more than once");
                    }
                    break;
                case XOR:
                    countPermSimple[s.getFunctionIndex0to4()]++;
                    if (countPermSimple[s.getFunctionIndex0to4()] > 1) {
                        throw new RuntimeException("Invalid Wheel Settings: " + getWheelSettingsString()
                                + " Setting: " + s + " appears more than once");
                    }
                    break;
            }
        }

    }
    private String getModelString() {
        return model.toString();
    }
    public String getWheelMsgSettingsString() {
        if (wheelMsgSettings == null) {
            return MessageKeyUnit.MSG_KEY_LETTERS_NEUTRAL;
        }
        StringBuilder string = new StringBuilder();
        for (int w = 0; w < Wheels.NUM_MSG_SETTINGS; w++) {
            string.append(MessageKeyUnit.MSG_KEY_LETTERS.charAt(wheelMsgSettings[w]));
        }
        return string.toString();
    }
    private void parseModelString(String modelString) {
        this.model = Model.parse(modelString);
    }
    private void parseWheelPositionsString(String wheelPositionsString) {
        boolean fromSimulator = wheelPositionsString.contains(",");
        String[] split = wheelPositionsString.split("[:,]");
        if (split.length != Wheels.NUM_WHEELS && !wheelPositionsString.isEmpty()) {
            throw new RuntimeException("Should have exactly 10 wheel positions - found: " + split.length
                    + " - " + wheelPositionsString);
        }
        for (int wi = 0; wi < Wheels.NUM_WHEELS; wi++) {
            int w = fromSimulator? 9 - wi : wi;
            if (wheelPositionsString.isEmpty() || split[wi].equals("--") || split[wi].equals("..")) {
                wheelPositions[w] = 0;
            } else {
                int val = Integer.parseInt(split[wi]);
                if (val < 1 || val > Wheels.WHEEL_SIZES[w]) {
                    throw new RuntimeException("Invalid position for wheel: " + Wheels.WHEEL_LETTERS.charAt(w)
                            + " - found: " + val + " - should be between 1 to " + Wheels.WHEEL_SIZES[w]
                            + " - " + wheelPositionsString);
                }
                wheelPositions[w] = val - 1;
            }
        }
    }
    private void parseWheelSettingsString(String wheelOrderString) {
        String[] split = wheelOrderString.split(":");
        if (split.length != Wheels.NUM_WHEELS) {
            throw new RuntimeException("Should have exactly 10 wheel settings - found: " + split.length
                    + " - " + wheelOrderString);
        }
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            wheelSettings[w] = WheelSetting.parse(split[w], this.model);
            if (wheelSettings[w].getFunction() == WheelSetting.WheelFunction.FIXED_PERM_SWITCH) {
                if (model == Model.T52AB || model == Model.T52D) {
                    throw new RuntimeException("T52AB and T52D require permutation of the type x-y");
                }
            } else if (wheelSettings[w].getFunction() == WheelSetting.WheelFunction.PROGRAMMABLE_PERM_SWITCH) {
                if (model != Model.T52AB && model != Model.T52D) {
                    throw new RuntimeException("Only T52AB and T52D all permutation of the type x-y");
                }
            }
        }
    }
    void parseWheelMsgSettingsString(String wheelMsgSettingsString) {
        if (!model.hasMessageKeyUnit()) {
            throw new RuntimeException("Msg key setting: " + wheelMsgSettingsString + " - not supported for model "
                    + model);
        }
        if (wheelMsgSettingsString == null) {
            wheelMsgSettings = null;
            return;
        }
        if (wheelMsgSettingsString.length() != Wheels.NUM_MSG_SETTINGS) {
            throw new RuntimeException("Should have exactly 5 msg key settings - found: "
                    + wheelMsgSettingsString.length() + " - " + wheelMsgSettingsString);
        }

        wheelMsgSettings = new int[Wheels.NUM_MSG_SETTINGS];
        for (int i = 0; i < Wheels.NUM_MSG_SETTINGS; i++) {
            int val = MessageKeyUnit.MSG_KEY_LETTERS.indexOf(wheelMsgSettingsString.charAt(i));
            if (val == -1) {
                throw new RuntimeException("Wrong msg key settings at pos: "
                        + i + " - " + wheelMsgSettingsString);
            }
            wheelMsgSettings[i] = val;
        }

    }
    Key setScore(double score) {
        this.score = score;
        return this;
    }

    String getWheelPositionsString(WheelFunctionSet wheelFunctionSet) {
        StringBuilder string = new StringBuilder();
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            if (w != 0) {
                string.append(":");
            }
            if (wheelFunctionSet.containsWheel(w, wheelSettings)) {
                int display = wheelPositions[w] + 1;
                if (display < 10) {
                    string.append("0").append(display);
                } else {
                    string.append(display);
                }
            } else if (wheelSettings[w].getFunction() == WheelSetting.WheelFunction.XOR) {
                string.append("--");
            } else {
                string.append("..");
            }
        }
        return string.toString();
    }
    String getWheelSettingsStringWithFills(WheelFunctionSet wheelFunctionSet) {
        StringBuilder string = new StringBuilder();
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            if (w != 0) {
                string.append(":");
            }

            if (wheelFunctionSet.containsWheel(w, wheelSettings)) {
                string.append(wheelSettings[w].toString());
            } else if (wheelSettings[w].getFunction() == WheelSetting.WheelFunction.XOR) {
                string.append("--------".substring(0, wheelSettings[w].toString().length()));
            } else {
                string.append("........".substring(0, wheelSettings[w].toString().length()));
            }
            while (string.length() < (w * 4 + 3)) {
                string.append(' ');
            }
        }
        return string.toString();
    }
    String getWheelSettingsString(WheelFunctionSet wheelFunctionSet) {
        StringBuilder string = new StringBuilder();
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            if (wheelFunctionSet.containsWheel(w, wheelSettings)) {
                if (!string.isEmpty()) {
                    string.append(":");
                }
                string.append(wheelSettings[w].toString());
            }
        }
        return string.toString();
    }

    String getPermTypeString() {
        ArrayList<String> list = new ArrayList<>();
        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            if (wheelSettings[w].getFunction() != WheelSetting.WheelFunction.XOR) {
                list.add(wheelSettings[w].toString());
            }
        }
        Collections.sort(list);
        StringBuilder string = new StringBuilder();
        for (String s : list) {
            if (!string.isEmpty()) {
                string.append(":");
            }
            string.append(s);
        }
        return string.toString();
    }



    public static void main(String[] args) {
        String modelString = "T52AB";
        String wheelPositionsString = "1:2:3:4:5:6:7:8:9:10";

        String wheelSettingsString = "1-2:3-4:5-6:7-8:9-10" + Wheels.DEFAULT_XOR_WHEELS;
        Key key = new Key(modelString, wheelPositionsString, wheelSettingsString, null);
        key.print("TestAB1");
        key = new Key(modelString, wheelSettingsString);
        key.print("TestAB2");

        modelString = "T52CA";
        wheelSettingsString = Wheels.DEFAULT_PERM_AND_XOR_WHEELS;
        String wheelMsgSettingsString = "UWXYZ";
        key = new Key(modelString, wheelPositionsString, wheelSettingsString, wheelMsgSettingsString);
        key.print("TestCA1");

        modelString = "T52C";
        wheelSettingsString = Wheels.DEFAULT_PERM_AND_XOR_WHEELS;
        wheelMsgSettingsString = "PSTUW";
        key = new Key(modelString, wheelPositionsString, wheelSettingsString, wheelMsgSettingsString);
        key.print("TestC1 ");

        modelString = "T52D";
        wheelSettingsString = "1-2:3-4:5-6:7-8:9-10" + Wheels.DEFAULT_XOR_WHEELS;
        key = new Key(modelString, wheelPositionsString, wheelSettingsString);
        key.print("TestD1 ");

        modelString = "T52E";
        wheelSettingsString = "1:3:5:7:9" + Wheels.DEFAULT_XOR_WHEELS;
        key = new Key(modelString, wheelPositionsString, wheelSettingsString);
        key.print("TestE1 ");

        modelString = "T52EE";
        wheelSettingsString = "1:3:5:7:9" + Wheels.DEFAULT_XOR_WHEELS;
        key = new Key(modelString, wheelPositionsString, wheelSettingsString);
        key.print("TestEE1 ");
    }

}

