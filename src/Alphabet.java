class Alphabet {


    private final static String BRITISH_ALPHABET_LETTERS = "/E4A9SIU3DRJNFCKTZLWHYPQOBG+MXV8";
    //      private final static String ALPHABET_LETTERS = "6E2A5SIU1DRJNFCKTZLWHYPQOBG4MXV3";
    private final static String BRITISH_ALPHABET_FIGURES = "i3n-_'87rd4j,f:(5+)2h6019?g!./=!";

    final static char[] BRITISH_ALPHABET_LETTERS_ARRAY = BRITISH_ALPHABET_LETTERS.toCharArray();


    private final static String ALPHABET_LETTERS = "6E2A5SIU1DRJNFCKTZLWHYPQOBG4MXV3";
    private final static String ALPHABET_FIGURES = "!3!-!'87!w4b,Ü:(5+)2Ä6019?Ö!./=!";

    final static char[] ALPHABET_LETTERS_ARRAY = ALPHABET_LETTERS.toCharArray();
    final static char[] ALPHABET_FIGURES_ARRAY = ALPHABET_FIGURES.toCharArray();

    private static final String ACCENTED_IN = "ÈÉÌÙÒÀËÁÖÆËÜÃÞÔÂÄÍÛÓŠØŮĚŇÏÇÑÍÀÇÈÌÅÁßŔÚΜÝˆ`^ΆΛÊÉĄÎŐČŽÂªªºŽŃΆΛΛΗΦΟΡΆΘĘŹÐÖŻÕŘÁĚŠĎŤˇיÒÀÉÀ";
    private static final String ACCENTED_TR = "EEIUOAEAOAEUAPOAAIUOSOUENICNIACEIAASRUPYZZZAGEEAIOCZAAAOZNZZZZZZZZZZZZOZORAESDTZZOAEA";
    private final static String LEGAL_PLAIN_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + " \n\r" + "/|0123456789.*:;,!?=(){}[]<>@~&Æ#$£+-_–%" + "\'\"\t\\";
    private final static String LETTER_TELEP_ALPHABET_SWEDISH = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "521" + "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + "$$$$";
    private final static String SHIFT_TELEP_ALPHABET_SWEDISH = "$$$$$$$$$$$$$$$$$$$$$$$$$$" + "521" + "XXPQWERTYUIOMMCMNJBVKLKLKLKLGGGGHHHZAAAF" + "SS5X";

    private static final char SWEDISH_CR = '1';
    private static final char SWEDISH_NL = '2';
    private static final char SWEDISH_LS = '3';
    private static final char SWEDISH_FS = '4';
    private static final char SWEDISH_SP = '5';
    private static final char SWEDISH_BL = '6';

    private static final char BRITISH_CR = '3';
    private static final char BRITISH_NL = '4';
    private static final char BRITISH_LS = '8';
    private static final char BRITISH_LS2 = '-';
    private static final char BRITISH_FS = '+';
    private static final char BRITISH_FS2 = '5';
    private static final char BRITISH_SP = '9';
    private static final char BRITISH_SP2 = '.';
    private static final char BRITISH_BL = '/';

    private static final char CHAR_INVALID = '!';
    private static final char CHR_SP = ' ';
    private static final char CHR_LS = '>';
    private static final char CHR_FS = '<';
    private static final char CHR_NL = '\n';

    private static final byte INVALID = -1;
    private static final byte CR = (byte) ALPHABET_LETTERS.indexOf(SWEDISH_CR);
    private static final byte NL = (byte) ALPHABET_LETTERS.indexOf(SWEDISH_NL);
    private static final byte LS = (byte) ALPHABET_LETTERS.indexOf(SWEDISH_LS);
    private static final byte FS = (byte) ALPHABET_LETTERS.indexOf(SWEDISH_FS);
    private static final byte SP = (byte) ALPHABET_LETTERS.indexOf(SWEDISH_SP);
    private static final byte BL = (byte) ALPHABET_LETTERS.indexOf(SWEDISH_BL);


    static String formatGerman(byte[] plainStream) {
        String s = toStringFormatted(plainStream, true);
        s = s.replaceAll("<", " ");
        s = s.replaceAll(">", " ");
        s = s.replaceAll("[ ]+", " ");
        s = s.replaceAll(" ,", ",");
        s = s.replaceAll(" \\.", ".");
        s = s.replaceAll(" :", ":");
        s = s.replaceAll("\\.-", ".-\r\n");
        s = s.replaceAll("=", "=\r\n");
        return s;
    }


    static String toStringFormatted(byte[] plainStream, boolean oneLine) {
        StringBuilder s = new StringBuilder();
        boolean figureShift = false;

        boolean lastWasCR = false;
        for (byte b : plainStream) {
            if (b == INVALID) {
                s.append(CHAR_INVALID);
                continue;
            }
            char c = ALPHABET_LETTERS_ARRAY[b];
            switch (c) {
                case SWEDISH_FS -> {
                    if (oneLine) {
                        s.append(CHR_FS);
                    } else if (!figureShift) {
                        s.append(CHR_SP);
                    }
                    figureShift = true;
                }
                case SWEDISH_LS -> {
                    if (oneLine) {
                        s.append(CHR_LS);
                    } else if (figureShift) {
                        s.append(CHR_SP);
                    }
                    figureShift = false;
                }
                case SWEDISH_CR -> {
                    if (oneLine) {
                        s.append(CHR_SP);
                    } else {
                        s.append(CHR_NL);
                    }
                }
                case SWEDISH_NL -> {
                    if (lastWasCR) {
                        s.setLength(s.length() - 1);
                        if (oneLine) {
                            s.append("\\n");
                        } else {
                            s.append(CHR_NL);
                        }
                    } else {
                        if (oneLine) {
                            s.append(CHR_SP);
                        } else {
                            s.append(CHR_NL);
                        }
                    }
                }
                case SWEDISH_SP, SWEDISH_BL -> s.append(CHR_SP);
                default -> {
                    if (figureShift) {
                        s.append(ALPHABET_FIGURES.charAt(b));
                    } else {
                        s.append(ALPHABET_LETTERS.charAt(b));
                    }
                }
            }
            lastWasCR = (c == SWEDISH_CR);
        }
        if (!oneLine) {
            return s.toString()
                    .replaceAll(" \\.", ".")
                    .replaceAll(" ,",",")
                    .replaceAll("[ ]+"," ")
                    .replaceAll("OE", "Ö")
                    .replaceAll("UE", "Ü")
                    .replaceAll("AE", "Ä");
        } else {
            return s.toString()
                    .replaceAll("OE", "Öe")
                    .replaceAll("UE", "Üe")
                    .replaceAll("AE", "Äe");
        }
    }

    static String convertToStringWithoutSpecialCharacters(String input) {
        StringBuilder output = new StringBuilder();
        input = input.toUpperCase();
        for (char c : input.toCharArray()) {
            if (c == CHAR_INVALID) {
                output.append(CHAR_INVALID);
            } else {
                output.append((ALPHABET_LETTERS.indexOf(c) == -1) ? CHAR_INVALID : c);
            }
        }

        return output.toString();
    }

    static String convertToStringSpecialCharacters(String input) {
        StringBuilder output = new StringBuilder();
        input = input.toUpperCase();
        for (char c : input.toCharArray()) {
            if (c == CHAR_INVALID) {
                output.append(CHAR_INVALID);
            } else {
                output.append((ALPHABET_FIGURES.indexOf(c) == -1) ? CHAR_INVALID : ALPHABET_LETTERS_ARRAY[ALPHABET_FIGURES.indexOf(c)]);
            }
        }
        return output.toString();
    }

    static byte[] fromString(String text, int len) {
        text = text.replaceAll("[ \\n\\t]", "").toUpperCase();
        len = Math.min(text.length(), len);
        byte[] converted = new byte[len];
        for (int i = 0; i < len; i++) {
            converted[i] = (byte) Alphabet.ALPHABET_LETTERS.indexOf(text.charAt(i));
        }
        return converted;
    }

    static byte[] fromString(String text) {
        text = text.replaceAll("[ \\n]", "").toUpperCase();
        int len = text.length();
        byte[] converted = new byte[len];
        boolean figure = false;
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            byte index;
            if (c == CHR_FS) {
                figure = true;
                index = FS;
            } else if (c == CHR_LS) {
                figure = false;
                index = LS;
            } else if (c == CHR_SP) {
                index = SP;
            } else if (figure) {
                index = (byte) Alphabet.ALPHABET_FIGURES.indexOf(c);
            } else {
                index = (byte) Alphabet.ALPHABET_LETTERS.indexOf(c);
            }

            converted[i] = index;
        }
        return converted;
    }

    static byte[] fromStringBritish(String text) {
        text = text.replaceAll("[ \\n]", "").toUpperCase();
        int len = text.length();
        byte[] converted = new byte[len];
        for (int i = 0; i < len; i++) {
            converted[i] = (byte) Alphabet.BRITISH_ALPHABET_LETTERS.indexOf(text.charAt(i));
        }
        return converted;
    }

    static String toString(byte[] stream) {
        StringBuilder s = new StringBuilder();
        for (byte c : stream) {
            s.append(c == INVALID ? CHAR_INVALID : ALPHABET_LETTERS_ARRAY[c]);
        }
        return s.toString();
    }

    static String toString(byte[] stream, int pos, int len) {
        StringBuilder s = new StringBuilder();
        int currLen = Math.min(stream.length - pos, len);
        for (int i = 0; i < currLen; i++) {
            byte c = stream[pos + i];
            s.append(c == INVALID ? CHAR_INVALID : ALPHABET_LETTERS_ARRAY[c]);
        }
        return s.toString();
    }

    static String toString(byte[] stream, int len) {
        return toString(stream, 0, len);
    }

    static String convertPlainToTelep(String plain) {
        StringBuilder t = new StringBuilder();
        boolean inShift = false;
        plain = plain.replaceAll("ß", "SS").toUpperCase();

        for (char plainC : plain.toCharArray()) {

            if ((int) plainC > 65000) {
                continue;
            }
//            if (plainC == ' ') {
//                t.append(SWEDISH_SP);
//                continue;
//            }

            int indexLegalPlain = LEGAL_PLAIN_ALPHABET.indexOf(plainC);
            if (indexLegalPlain == -1) {
                int indexAccented = ACCENTED_IN.indexOf(plainC);
                if (indexAccented != -1) {
                    indexLegalPlain = LEGAL_PLAIN_ALPHABET.indexOf(ACCENTED_TR.charAt(indexAccented));
                    char tr = LETTER_TELEP_ALPHABET_SWEDISH.charAt(indexLegalPlain);
                    t.append(tr);
                    t.append('E');
                }
                continue;
            }

            char letterTelepC = LETTER_TELEP_ALPHABET_SWEDISH.charAt(indexLegalPlain);
            if (letterTelepC != '$') {
                if (inShift) {
                    inShift = false;
                    t.append(SWEDISH_LS);
                }
            } else {
                if (!inShift) {
                    inShift = true;
                    t.append(SWEDISH_FS);
                }
                letterTelepC = SHIFT_TELEP_ALPHABET_SWEDISH.charAt(indexLegalPlain);
            }
            t.append(letterTelepC);
        }
        return t.toString();
    }

    static String toStringBritish(byte[] stream) {
        StringBuilder s = new StringBuilder();
        for (byte c : stream) {
            s.append(c == -1 ? CHAR_INVALID : BRITISH_ALPHABET_LETTERS_ARRAY[c]);
        }
        return s.toString();
    }

    static boolean isBritish(String s) {
        boolean british = s.contains("8") || s.contains("+") || s.contains("/")|| s.contains("9");
        boolean swedish = s.contains("1") || s.contains("2") || s.contains("6") || s.contains("5");

        if (british && !swedish) {
            return true;
        }
        if (!british && swedish) {
            return false;
        }
        throw new RuntimeException("Cannot determine if swedish or british: " + s);
    }

    final static int[][] raw = {
            {'/', 0, 0, 0, 0, 0},  // BL=6
            {'T', 0, 0, 0, 0, 1},  // T 5
            {'3', 0, 0, 0, 1, 0},  // CR=1
            {'O', 0, 0, 0, 1, 1},  // O 9
            {'9', 0, 0, 1, 0, 0},  // SP=5
            {'H', 0, 0, 1, 0, 1},  // H CS
            {'N', 0, 0, 1, 1, 0},  // N ,
            {'M', 0, 0, 1, 1, 1},  // M .

            {'4', 0, 1, 0, 0, 0},  // NL=2
            {'L', 0, 1, 0, 0, 1},  // L )
            {'R', 0, 1, 0, 1, 0},  // R 4
            {'G', 0, 1, 0, 1, 1},  // G CS
            {'I', 0, 1, 1, 0, 0},  // I 8
            {'P', 0, 1, 1, 0, 1},  // P 0
            {'C', 0, 1, 1, 1, 0},  // C :
            {'V', 0, 1, 1, 1, 1},  // V =

            {'E', 1, 0, 0, 0, 0},  // E 3
            {'Z', 1, 0, 0, 0, 1},  // Z +
            {'D', 1, 0, 0, 1, 0},  // D Who is there
            {'B', 1, 0, 0, 1, 1},  // B ?
            {'S', 1, 0, 1, 0, 0},  // S '
            {'Y', 1, 0, 1, 0, 1},  // Y 6
            {'F', 1, 0, 1, 1, 0},  // F CS
            {'X', 1, 0, 1, 1, 1},  // X /    (was 1)

            {'A', 1, 1, 0, 0, 0},  // A -
            {'W', 1, 1, 0, 0, 1},  // W 2
            {'J', 1, 1, 0, 1, 0},  // J Bell
            {'+', 1, 1, 0, 1, 1},  // FS=4   (was 5)
            {'U', 1, 1, 1, 0, 0},  // U 7
            {'Q', 1, 1, 1, 0, 1},  // Q 1
            {'K', 1, 1, 1, 1, 0},  // K (
            {'8', 1, 1, 1, 1, 1},  // LS=3
    };


    static final String test = "Diese Wahrscheinlichkeit lag um so näher, als sich in der am 21.\n" +
            "Dezember 1916 in Berlin überreichten Friedensnote der amerikanischen\n" +
            "Regierung der Passus fand, daß die Interessen der Vereinigten Staaten\n" +
            "durch den Krieg »ernstlich in Mitleidenschaft gezogen seien«, und daß\n" +
            "das Interesse der Union an einer baldigen Beendigung des Krieges sich\n" +
            "daraus ergebe, daß »sie offenkundig genötigt wäre, Bestimmungen über\n" +
            "den bestmöglichen Schutz ihrer Interessen zu treffen, falls der Krieg\n" +
            "fortdauern sollte«." +
            "\n" +
            "In  this  is  likewise  included  a  sum  of\n" +
            "9,721_l._  received  from  theatres,  fairs,  and  races.\n" +
            "Police  Force,  is  put  down  at  41,714_l._,  and  the  expenditure  at\n" +
            "41,315_l._,  the  gross  pay,  irrespective  of  other  charges  to  the  force,\n" +
            "amounting  to  29,800_l._\n" +
            "\n" +
            "";


    public static void main(String[] args) {


        String telep = convertPlainToTelep(test);
        byte[] text = fromString(telep);
        System.out.println(test);
        System.out.println(telep);
        System.out.println(toString(text));
        System.out.println(toStringBritish(text));
        System.out.println(toStringFormatted(text, true));

        String s = "545c354535sind5fuer545y35mast5444aq5i3rakq4343434333\n" +
                "33333und5weiter5in555555nenen555eb333333333335554553333die55uhr5\n" +
                "zeit5verlege5ich5vor5weil5ich5noch5alerhand5zu5verbessern5habe5\n" +
                "noch5ungefaehr54ep5333k15fehler5also5555ich55qs154c3333335425333\n" +
                "354qytt3333333355siebenzehn5ff5glyoo4z53333qrv4b35ja5gut5435dks\n" +
                "5453554535also54535wer5435hat5das54535kr5qey54b54b3555555glyf155\n" +
                "55ich5musz55jetzt55qrt5bis5nachher5ja5gut545355435ich5sage5435g\n" +
                "leich5cm4m35bexchnn5verm4m35be4ibzuijq2mj6a6603gingbeqingbep2hr\n" +
                "xjza6a66oxsxz2elden4j35ja5hier5vleak55hier5glyf15kr5qey5qev5glea\n" +
                "v55ev5gleav5kr5q5e5y55r5hier5gleav55hier5glyf15kr5qe55555qey555\n" +
                "55erh4m35sie5q5r5v54j35hier5alles5q5r5v5ve54b435sieimich5fortiwu\n" +
                "ch54b4b4355nicht5ganz5555bt5langsam5schr4m3555555umum4j3vevehzq\n" +
                "h6621vsf3434343333335auf5klar5auf5klar5auf5klar5mmzwbuf5klar5bt\n" +
                "5eb55rrr5bt5langsam5schr4m35sonst5nicht5klar5ve54j355gut5wird5ge\n" +
                "macht5eb5bt5rr35q5r5v55555umum55veve5apm2cpvfmklss1rvvlhc3dr162\n" +
                "e2iph65q1w14e5jli2tfrgbanhktqm6qh6oatgbz6dzdxd4va1dbcmwmqp4uuvo5\n" +
                "scb1uabuvaylihqzk4mf6og4w1536seyzpyhznkdyzhvdlqpanmme3fszp4ccoz\n" +
                "rtgozsfas5e1oqdz6xJyoo425kh4qfxq413rgiabef1iyzdftxxxxzqqjfzzqwoq\n" +
                "c1dis3cgzuu1k35qrv55umum55ve5ve5cmxaxkccxnhcx2r41zdssu5xcih4ffl\n" +
                "qx!n3xcutogua1ah24f1cwmkd3absxojtovwpavjfn1vwdevqi1yrwrdzsylig4\n" +
                "p5c4pxir2t36ibxeux1i5d6tmb14gsifgn25bb2h6gf16np53joima634up1pmi6\n" +
                "eljnxxduwdpuypc4rmeo4uoli31t6vwsa63t2arhbe3si32ty51p213zoyqfvak\n" +
                "hppakvlukjbwp1d4rnqdrxu2f40g3kdpwgtkwbrir32h41bozie15nd5cnp1vh3i\n" +
                "huluyqqojtlbwuebru6y4x3hty14yufz22iax2s3kkhzuyrde2swtz1ltrkrhr!\n" +
                "cmxzlhd6pk5dhbj5mhej1eyo3drsqt3owct5xmh6mbkyte5dniexy1erijlowdi\n" +
                "vxzhayvv5kpfkdkpwxugastjeexgtjmq6c4yr12wmyh31vsmmff2nflj3xgiels";

        byte[] p = fromString(s.toUpperCase().replaceAll("[^A-Z1-6]", ""));

        System.out.println(toStringFormatted(p, false));
        System.out.println(toStringFormatted(p, true));

    }


}
