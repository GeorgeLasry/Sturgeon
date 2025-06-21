import java.util.Random;

public class CommonRandom {
    public static Random r = new Random(0);

    public static void randomSeed(){
        r.setSeed(System.currentTimeMillis());
    }
    public static void seed(long seed){
        r.setSeed(seed);
    }
}
