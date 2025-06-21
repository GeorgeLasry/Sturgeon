import java.util.Set;
import java.util.TreeSet;

class SturgeonControl {


    static int computeControlMappingEntry(Key key, int wheelBitmap) {

        // compute wheelSelections from key.model, key.wheelSettings and key.wheelMsgSettings
        // - For ab/d
        // -- For XOR - look at I to V
        // -- For FIXED_PERM_SWITCH - look at sorted X-Y, and their assignment. Build the equivalent of 1,3,5,7,9.
        // - For e
        // -- full order given in key.wheelSettings (1,3,5,7,9 and I to V)
        // - For c/ca - combine
        // -- wheel scramble from key.wheelMsgSettings
        // -- then order (1,3,5,7,9 and I to V) from key.wheelSettings

        if (key.model.hasMessageKeyUnit()) {
            wheelBitmap = MessageKeyUnit.msgKeyUnitOutput(key, wheelBitmap);
        }

        int outputXorBitmap = 0;
        int outputPermBitmap = 0;
        int countProgrammablePerm = 0;

        for (int w = 0; w < Wheels.NUM_WHEELS; w++) {
            boolean inputBitValue = TenBits.isSet(w, wheelBitmap);
            switch (key.wheelSettings[w].getFunction()) {
                case XOR:
                    if (inputBitValue) {
                        outputXorBitmap |= FiveBits.BITS[key.wheelSettings[w].getFunctionIndex0to4()];
                    }
                    break;
                case FIXED_PERM_SWITCH:
                    if (inputBitValue) {
                        outputPermBitmap |= FiveBits.BITS[key.wheelSettings[w].getFunctionIndex0to4()];
                    }
                    break;
                case PROGRAMMABLE_PERM_SWITCH:
                    if (inputBitValue) {
                        outputPermBitmap |= FiveBits.BITS[countProgrammablePerm];
                    }
                    countProgrammablePerm++;
                    break;
            }
        }
        return TenBits.bitmap(outputPermBitmap, outputXorBitmap);
    }

    static void computeControlMapping(Key key, int[] controlMapping) {
        for (int wheelBitmap = 0; wheelBitmap < TenBits.SPACE; wheelBitmap++) {
            controlMapping[wheelBitmap] = computeControlMappingEntry(key, wheelBitmap);
        }
    }

}
