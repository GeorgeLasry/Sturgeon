enum WheelSetting {
    W1("1"), W3("3"), W5("5"), W7("7"), W9("9"),
    I("I"), II("II"), III("III"), IV("IV"), V("V"),
    W1_2("1-2"), W1_3("1-3"), W1_4("1-4"), W1_5("1-5"), W1_6("1-6"), W1_7("1-7"), W1_8("1-8"), W1_9("1-9"), W1_10("1-10"),
    W2_3("2-3"), W2_4("2-4"), W2_5("2-5"), W2_6("2-6"), W2_7("2-7"), W2_8("2-8"), W2_9("2-9"), W2_10("2-10"),
    W3_4("3-4"), W3_5("3-5"), W3_6("3-6"), W3_7("3-7"), W3_8("3-8"), W3_9("3-9"), W3_10("3-10"),
    W4_5("4-5"), W4_6("4-6"), W4_7("4-7"), W4_8("4-8"), W4_9("4-9"), W4_10("4-10"),
    W5_6("5-6"), W5_7("5-7"), W5_8("5-8"), W5_9("5-9"), W5_10("5-10"),
    W6_7("6-7"), W6_8("6-8"), W6_9("6-9"), W6_10("6-10"),
    W7_8("7-8"), W7_9("7-9"), W7_10("7-10"),
    W8_9("8-9"), W8_10("8-10"),
    W9_10("9-10");
    private final String string;
    private final WheelFunction function;
    private final int functionIndex0to4;
    private final int junction1;
    private final int junction2;
    WheelSetting(String string) {
        this.string = string;
        if (string.matches("[13579]")) {
            function = WheelFunction.FIXED_PERM_SWITCH;
            functionIndex0to4 = "13579".indexOf(string);
            junction1 = junction2 = -1;
        } else if (string.matches("\\d-\\d+")) {
            function = WheelFunction.PROGRAMMABLE_PERM_SWITCH;
            junction1 = Integer.parseInt(string.substring(0, 1));
            junction2 = Integer.parseInt(string.substring(2));
            functionIndex0to4 = -1;
        } else if (string.matches("[IV]*")) {
            function = WheelFunction.XOR;
            switch (string) {
                case "I" -> functionIndex0to4 = 0;
                case "II" -> functionIndex0to4 = 1;
                case "III" -> functionIndex0to4 = 2;
                case "IV" -> functionIndex0to4 = 3;
                case "V" -> functionIndex0to4 = 4;
                default -> throw new RuntimeException("Invalid Roman wheel setting");
            }
            junction1 = junction2 = -1;
        } else {
            throw new RuntimeException("Invalid wheel setting");
        }
    }

    public static WheelSetting parse(String string, Model model) {
        for (WheelSetting setting : WheelSetting.values()) {
            if (string.equals(setting.toString())) {
                if (setting.function == WheelFunction.PROGRAMMABLE_PERM_SWITCH && !model.hasProgrammablePermSwitches()) {
                    throw new RuntimeException("Invalid wheel setting: " + string + " ; x-y notation not supported for: "
                            + model);
                }
                if (setting.function == WheelFunction.FIXED_PERM_SWITCH && model.hasProgrammablePermSwitches()) {
                    throw new RuntimeException("Invalid wheel setting: " + string + " ; x-y notation mandatory for: "
                            + model);
                }
                return setting;
            }
        }
        throw new RuntimeException("Invalid wheel setting (not found): " + string);
    }

    static int wheelFunction0to9(int w, WheelSetting[] wheelSettings) {
        if (wheelSettings[w].getFunction() == WheelFunction.XOR) {
            return wheelSettings[w].getFunctionIndex0to4() + 5;
        }
        if (wheelSettings[w].getFunction() == WheelFunction.FIXED_PERM_SWITCH) {
            return wheelSettings[w].getFunctionIndex0to4();
        }

        int permFunctionIndex = 0;
        for (int wi = 0; wi < 10; wi++) {
            if (wheelSettings[wi].getFunction() == WheelFunction.PROGRAMMABLE_PERM_SWITCH) {
                if (wi == w) {
                    return permFunctionIndex;
                }
                permFunctionIndex++;
            }
        }

        return -1;

    }

    static int wheelWithFunction0to9(WheelSetting[] wheelSettings, int function) {

        for (int w = 0; w < 10; w++) {
            if (function == wheelFunction0to9(w, wheelSettings)) {
                return w;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return string;
    }

    public WheelFunction getFunction() {
        return function;
    }

    public int getFunctionIndex0to4() {
        if (functionIndex0to4 == -1) {
            throw new RuntimeException("Not expecting this");
        }
        return functionIndex0to4;
    }

    public int getJunction1() {
        return junction1;
    }

    public int getJunction2() {
        return junction2;
    }

    enum WheelFunction {
        XOR, FIXED_PERM_SWITCH, PROGRAMMABLE_PERM_SWITCH
    }
}

