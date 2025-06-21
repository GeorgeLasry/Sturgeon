import java.io.*;
import java.util.Objects;

public class Utils {

    private static final double MILLI = 1.0 / 1_000.0;
    private static final double MICRO = MILLI / 1_000.0;
    private static final double NANO = MICRO / 1_000.0;
    private static final double MINUTE = 60.0;
    private static final double HOUR = MINUTE * 60.0;
    private static final double DAY = HOUR * 24;
    private static final double MONTH = DAY * 31;
    private static final double YEAR = MONTH * 12;
    private static final double KYEAR = YEAR * 1_000;
    private static final double MYEAR = KYEAR * 1_000;

    public static final double FACTORIAL_10 = 3628800.0;
    public static final long ALL_ONES = ~0L;
    public static final int UNDEFINED = -1;

    public static String readFile(String ... fileName) {
        StringBuilder all = new StringBuilder();
        for (String fn :fileName) {
            File file = new File(fn);
            if (file.isDirectory()) {
                for (final File fileEntry : Objects.requireNonNull(file.listFiles())) {
                    try {
                        all.append(readFile(fileEntry.getCanonicalPath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (fn.endsWith(".txt")){
                all.append(readSingleFile(fn));
            }
        }
        return all.toString();
    }


    private static String readSingleFile(String fileName) {

        try {
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            if (fis.read(data) <= 0) {
                return null;
            }
            fis.close();
            return new String(data, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return null;
    }

    static double elapsedSec(long startNano) {
        return (System.nanoTime() - startNano) * NANO;
    }

    static String elapsedString(double seconds) {
        String elapsedStringBase = elapsedStringBase(seconds);
        String[] parts = elapsedStringBase.split(" ");
        return String.format("%3s %-8s", parts[0], parts[1]);

    }

    private static String elapsedStringBase(double seconds) {
        if (seconds < NANO) {
            return "<1 nanosec";
        }
        if (seconds < MICRO) {
            return String.format("%d nanosec", (int) (0.5 + seconds / NANO));
        }
        if (seconds < 10 * MICRO) {
            return String.format("%.1f musec", seconds / MICRO);
        }
        if (seconds < MILLI) {
            return String.format("%d musec", (int) (0.5 + seconds / MICRO));
        }
        if (seconds < 10 * MILLI) {
            return String.format("%.1f millisec", seconds / MILLI);
        }
        if (seconds < 1.0) {
            return String.format("%d millisec", (int) (0.5 + seconds / MILLI));
        }
        if (seconds < 10) {
            return String.format("%.1f sec", seconds);
        }
        if (seconds < MINUTE) {
            return String.format("%d sec", (int) (0.5 + seconds));
        }
        if (seconds < 10 * MINUTE) {
            return String.format("%.1f min", seconds / MINUTE);
        }
        if (seconds < HOUR) {
            return String.format("%d min", (int) (0.5 + seconds / MINUTE));
        }
        if (seconds < 10 * HOUR) {
            return String.format("%.1f hours", seconds / HOUR);
        }
        if (seconds < DAY) {
            return String.format("%d hours", (int) (0.5 + seconds / HOUR));
        }
        if (seconds < 10 * DAY) {
            return String.format("%.1f days", seconds / DAY);
        }
        if (seconds < MONTH) {
            return String.format("%d days", (int) (0.5 + seconds / DAY));
        }
        if (seconds < 6 * MONTH) {
            return String.format("%.1f months", seconds / MONTH);
        }
        if (seconds < YEAR) {
            return String.format("%d months", (int) (0.5 + seconds / MONTH));
        }
        if (seconds < 10 * YEAR) {
            return String.format("%.1f years", seconds / YEAR);
        }
        if (seconds < KYEAR) {
            return String.format("%d years", (int) (0.5 + seconds / YEAR));
        }
        if (seconds < 10 * KYEAR) {
            return String.format("%.1f K-years", seconds / KYEAR);
        }
        if (seconds < MYEAR) {
            return String.format("%d K-years", (int) (0.5 + seconds / KYEAR));
        }
        if (seconds < 10 * MYEAR) {
            return String.format("%.1f M-years", seconds / MYEAR);
        }
        return String.format("%d M-years", (int) (0.5 + seconds / MYEAR));

    }

    static int digit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        return -1;
    }
}
