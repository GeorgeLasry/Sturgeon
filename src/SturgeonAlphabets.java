public class SturgeonAlphabets {
    static byte computeAlphabetEntry(byte input, int control, int[][] perms) {
        return FiveBits.computeSwitchesPerm((byte) (input ^ TenBits.high(control)), perms[TenBits.low(control)]);
    }

    static void computeAlphabet(Key key, byte[][] alphabet, byte[][] inverseAlphabet, int[][] perms, int[] srLogic) {

        if (key.model.hasProgrammablePermSwitches()) {
            WheelSetting[] switches;
            switches = new WheelSetting[5];
            int nSwitches = 0;
            for (WheelSetting setting : key.wheelSettings) {
                if (setting.getFunction() == WheelSetting.WheelFunction.PROGRAMMABLE_PERM_SWITCH) {
                    switches[nSwitches++] = setting;
                }
            }

            ProgrammablePermutations.compute(switches, perms);
            for (int control = 0; control < TenBits.SPACE; control++) {
                byte[] alphabetControl = alphabet[control];
                byte[] inverseAlphabetControl = inverseAlphabet[control];
                int[] perm = perms[TenBits.low(control)];
                int xor = TenBits.high(control);
                for (byte input = 0; input < FiveBits.SPACE; input++) {
                    byte output = FiveBits.computeSwitchesPerm((byte) (input ^ xor), perm);
                    alphabetControl[input] = output;
                    inverseAlphabetControl[output] = input;
                }
            }
        } else if (key.model.hasSRLogic()){
            int[] precomputedSRLogic = SRLogic.precomputedSRLogic(key);
            if (srLogic != null) {
                System.arraycopy(precomputedSRLogic, 0, srLogic, 0, TenBits.SPACE);
            }
            for (int control = 0; control < TenBits.SPACE; control++) {
                int controlAfterSRLogic = precomputedSRLogic[control];
                byte[] alphabetControl = alphabet[control];
                byte[] inverseAlphabetControl = inverseAlphabet[control];
                for (byte input = 0; input < FiveBits.SPACE; input++) {
                    byte output = computeAlphabetEntry(input, controlAfterSRLogic, FixedPermutation.FIXED_PERMUTATION);
                    alphabetControl[input] = output;
                    inverseAlphabetControl[output] = input;
                }
            }
        } else {
            for (int control = 0; control < TenBits.SPACE; control++) {
                byte[] alphabetControl = alphabet[control];
                byte[] inverseAlphabetControl = inverseAlphabet[control];
                for (byte input = 0; input < FiveBits.SPACE; input++) {
                    byte output = computeAlphabetEntry(input, control, FixedPermutation.FIXED_PERMUTATION);
                    alphabetControl[input] = output;
                    inverseAlphabetControl[output] = input;
               }
            }
        }
    }

}
