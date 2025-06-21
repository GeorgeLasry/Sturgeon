import java.io.*;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by owner on 14/03/14.
 */
public class GutenbergParser {


    private byte[] bytesIn = new byte[50000000];


    private int counter;
    private boolean debug = false;
    private int lastPercent;
    private long startTime;
    private String currFile = null;

    public GutenbergParser() {

        startTime = System.currentTimeMillis();
    }

    private GutenbergParser(boolean debug) {
        this.debug = debug;
    }

    private void unzip(String zipFilePath) {
        ZipInputStream zipIn;
        try {
            zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                if (!entry.isDirectory()) {
                    if (debug)
                        System.out.printf("Zip File = %s - %d\n", entry.getName(), scanZipFile(entry.getName(), zipIn));
                    scanZipFile(entry.getName(), zipIn);
                } else {
                    if (debug) System.out.printf("Zip Dir = %s\n", entry.getName());
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
        } catch (FileNotFoundException ex) {
            System.err.printf("File %s not found\n", zipFilePath);
        } catch (IOException ex) {
            System.err.printf("File %s - IO Exception \n", zipFilePath);
        }

    }

    private long scanZipFile(String name, ZipInputStream zipIn) {

        BufferedInputStream buffIn = new BufferedInputStream(zipIn);
        long len = 0;
        try {

            int read;
            while ((read = buffIn.read(bytesIn)) != -1) {
                len += read;
                processFile(name, bytesIn, len);

            }
        } catch (IOException ex) {
            System.err.print("Path - IO Exception\n");
        }
        return len;
    }

    private void scanGZipFile(String zipFilePath) {

        GZIPInputStream zipIn = null;
        try {
            zipIn = new GZIPInputStream(new FileInputStream(zipFilePath));
        } catch (IOException ex) {
            System.err.print("GZIP exc 1\n");
        }
        long len = 0;
        try {
            int read;
            while ((read = Objects.requireNonNull(zipIn).read(bytesIn)) != -1) {
                len += read;
                processFile(zipFilePath, bytesIn, len);
            }
        } catch (IOException ex) {
            System.err.print("GZIP exc 2\n");
        }
    }


    private void processFile(String name, byte[] in, long len) {
        if (name.matches(".*90[0-9][0-9]\\.txt")) {
            System.out.printf("%,9d %s\n", len, name);
        }
    }

    private void traverse(File dir) {
        counter = 0;
        lastPercent = -1;

        listFileTree(dir, 0, "");

    }

    private void listFileTree(File dir, int depth, String prefix) {
        String lastZipFile = "dummy";
        for (File entry : Objects.requireNonNull(dir.listFiles())) {
            if (entry.isFile()) {
                String filename = entry.getName().toLowerCase();
                counter++;
                String newZipFile = filename.replace(".zip", "").replace(".gz", "");
                if (newZipFile.contains(lastZipFile) || lastZipFile.contains(newZipFile)) {
                    //System.out.printf("Skip %s \n",entry.getName());
                    continue;
                } else {
                    lastZipFile = newZipFile;
                    //System.out.printf("Parse %s \n",entry.getName());
                }
                if (filename.endsWith(".zip") || filename.endsWith(".ZIP")) {
                    try {
                        if (debug) System.out.printf("Zip %s \n", filename);
                        currFile = entry.getAbsolutePath();
                        unzip(entry.getCanonicalPath());
                    } catch (IOException ex) {
                        System.err.print("listTree - IO Exception \n");
                    }
                } else if (filename.endsWith(".gz") || filename.endsWith(".GZ")) {
                    try {
                        if (debug) System.out.printf("GZip %s \n", filename);
                        currFile = entry.getAbsolutePath();
                        scanGZipFile(entry.getCanonicalPath());
                    } catch (IOException ex) {
                        System.err.print("listTree - IO Exception \n");
                    }
                } else {
                    if (debug) System.out.printf("Not zip %s\n", entry.getName());
                }
            } else {
                if (debug) System.out.printf("Dir %s\n", entry.getName());

                if (debug)
                    System.out.printf("\nLevel %d - %s (%d - %d%%) \n", depth, prefix + "/" + entry.getName(), counter, Math.max(0, counter / 642 - 1));
                int percent = Math.max(0, counter / 642 - 1);
                if (percent > lastPercent) {
                    System.out.printf("%d%% - %d books/sec\n", percent, counter * 1000 / (System.currentTimeMillis() - startTime + 1));
                    lastPercent = percent;
                }
                listFileTree(entry, depth + 1, prefix + "/" + entry.getName());


            }
        }
    }

    public static void main(String[] args) {
        GutenbergParser gutenberg = new GutenbergParser(false);
        //String badIcDelta = "C:\\Users\\owner\\Documents\\Vuze Downloads\\pgdvd072006\\1\\4\\9\\8\\14982\\14982-8.zip";
        File root = new File("D:\\pgdvd042010");

        //File root = new File("C:\\Users\\owner\\Documents\\Vuze Downloads\\pgdvd072006\\");
        //File root = new File("C:\\Users\\owner\\Documents\\Vuze Downloads\\pgdvd042010\\");

        gutenberg.traverse(root);


    }

}
