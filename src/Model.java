import java.util.Arrays;


enum Model {
    UNKNOWN("Unknown"),
    T52AB("T52AB"),
    T52AA("T52AA"),
    T52C("T52C"),
    T52CA("T52CA"),
    T52D("T52D"),
    T52E("T52E"),
    T52EE("T52EE");
    final String string;

    Model(String string) {
        this.string = string;
    }

    public static Model parse(String string) {
        for (Model model : Model.values()) {
            if (string.equals(model.string)) {
                return model;
            }
        }
        throw new RuntimeException("Invalid model:" + string);
    }

    boolean hasSRLogic(){
        return this == T52C || this == T52CA || this == T52E || this == T52EE;
    }
    boolean hasProgrammablePermSwitches(){
        return this == T52AB || this == T52D;
    }
    boolean hasIrregularStepping(){
        return this == T52D || this == T52E;
    }
    boolean hasMessageKeyUnit(){
        return this == T52C || this == T52CA;
    }
    @Override
    public String toString() {
        return string;
    }
}