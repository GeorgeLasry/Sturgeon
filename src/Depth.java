
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Depth {


    public static void main(String[] args) {

        ReferenceText.create(false, "nils");
        Stats.buildQuadGrams(Alphabet.fromString(ReferenceText.referenceText), ReferenceText.referenceText.length());

        String allCiphersStringT52D =   "URXMJ4BIN8VOKX983RTCO/FCBQBERKEC4PPCOI+CUTF+XO83HPU/AF99/PL+3UNMUBVJFNAS39AQURXDVGRWXPACMGVOX+I4IHPRW4ECQX+ZB9QVJ4T8UBJAC/VI3ZJFV9ZYU+CV+3KHCYHWIKXQIABUFTVWFGI3NJEDGE+AFNE93F3OU\n" +
                "CSPIO3AQXAVGEX3QOXYIBCDKPXUZYQZD9NEW4NKBV8LHGLFPXZ+CQFT4FKIHQMZHLSNABWNAMSTIINGEIELSE/+OWCGN3DPJ4RCZMBNQPFSCLGBMMGIB\n" +
                "AWSBJOG/DJ9VIB84ITAGHJTOXEHTRKEWWLBOSRWVVQ3MCLUZWZV8Z+UERPYCCVNRXRNSQDCUAZOXP8EI3/TW9G9RQVX3YDTSWMDZNEJYVADCZDBNMZ+CUIJL9DILIK8UYDSSKMOXOYVMSA+B9ENCID4JKQKIFNI3DRBIGZZTDV\n" +
                "U3JLJ+AQXAVGEX3QOXY3XCMWGATZGIELKLJ+IE3EY//IR/NZ4CY9HBXCREP3VCG+INNP8K9HQ9TM3XLE9GIVF4PI/K8XTER9YDRYRNEVAFZRQL+FUUYMO8SRB9/KBSASK/DQY4FMSLR3VA+B9E3GRAAQKLYJCCLJL9H/GAXJ8UA3ZKLGFIASZSO9CG4BRJLXPB4HMIJ4K/SFCFJY/AAT3AI3+INV/XBI83RTNOG/NRLFMYVLXALKJVIW3RZSM/GZ9NW9M4GO3Y\n" +
                "SWJFGEUM+IP98WI4VH+XQLDVJG+VR9NC4PPCOI+CUTGNW+FGZRRC+PK9VIRCRVFTIDMJAW9PKDOJNHQ9MRBWI83+AWWX/PECORGZHCSYQDHOCJFDJNSCUYJLH4/+9ZCZCO9A8\n" +
                "G3STG3DM+IP9SD3JVVUYFNDVJG+VR+BDHYZX4NOCNOCNFB/LPCRH+PRO4O/J3NTV4LJMXZ9D/JTINK/BVHPZ+SPI/K8XT+/RSLK9HFZTEX9ZVJU9RUGT+L\n";
        String allPlainsStringT52D =  "HELLEBARDE9VON9GENERAL9HAUSSER9AN9GENERAL9VON9KLUGE+M98ICH9HABE9MICH9VON9MOR!!!\n" +
                "SPECHT9VON9GENERAL9GUENTHER9VON9KLUGE9AN9WESTERN9ARMY9GROUP+M98SIE9MUESSEN9!!!!\n" +
                "PORZELLAN9VON9GENERAL9GUENTHER9VON9KLUGE9AN9GENERAL9PAUL9HAUSSER+M98DIE9VI!!!!!\n" +
                "HAMMER9VON9GENERAL9DIETRICH9AN9GENERAL9VON9KLUGE+M98WENN9DIE9FRONT9DER9V+M98!!!\n" +
                "TOMATEN9VON9GENERAL9VON9KLUGE9ZAN9GENERAL9DIETRICH+M98SIE9MUESSEN9IHRE9POSITION\n" +
                "KAROTTE9VON9DIETRICH9AN9KLUGE+M98EINE9NACHRICHT9AM+Q98ZEIGT9AN+N98DASS9DIE9D!!!";

        String allCiphersStringT52E =
                    "NYDQ3WDKQV8I8JJBY+DJE33NKKZ+DRKIZT+ZP\n" +
                            "O+LWEG+BBLFEU+JHCQSHPCPAKK4++D3O3JUHC/TQVSJIXPDQX\n" +
                            "NYEEPVEQ4SDEEGJZ999WUSJVHRK8T33RCH/X\n" +
                            "NZZQCRBQP4/T3WXZAOOWUM/WYZUXYWFN8R9QLLFGH\n" +
                            "+CLAHER3QDDDQN/3AEDJY4RGJZUX4ZXJZTLHBMQIRXICAZHX\n" +
                            "O+LATFMS8T/KC3DHIWAJR33NEUD8QD3Y/KXMVYA8V+WWWNZZFDOONXJ/RSI8ECMCQVTDNZ9T8RT9RSVPRJYPBSVQ4F8+HHTPZBBJZI3JPKMXAOPJE9YYYL8C4UAGPWB9CKGJUTDARD/CGYQ34B4UW/9YUFWEALECKZFXKMU+DS8+F8KB3JYFHRQIGCTBLFRN/LSINK3+RH+SWAKTJXHJGQYZLO8L4GCCFA+VLDY8WA+XP+KFE4YYK3XMJKMTD8ZKFPBVFLOWDH3CIAEMPYLYHZRUAAW3TSG+BHCKXZSXGKUW4XZGWSLHGHVCZDYD9IHACQADKPQ84F8TD8C+CSAWCFTBDYJ4S";
        String allPlainsStringT52E =
//                  "NYDQ3WDKQV8I8JJBY+DJE33NKKZ+DRKIZT+ZP!!!!!!!!!!!!!\n" +
                    "ALBERT9EST+A8CE9QUE9TU9ME9RECOIT9+B43\n" +
//                  "O+LWEG+BBLFEU+JHCQSHPCPAKK4++D3O3JUHC/TQVSJIXPDQX\n" +
                    "DE9ALBERT9A9PIERRE9+C98JE9TE9RECOIS9TRES9BIEN+M43\n" +
//                  "NYEEPVEQ4SDEEGJZ999WUSJVHRK8T33RCH/X!!!!!!!!!!!!!!\n" +
                    "ALORS+N98JE9T9ENVOIE9LE9MESSAGE9+B43\n" +
//                  "NZZQCRBQP4/T3WXZAOOWUM/WYZUXYWFN8R9QLLFGH!!!!!!!!!\n" +
                    "ATTENDS9UN9PEU+N98JE9CHANGE9LE9PAPIER+M43\n" +
//                  "+CLAHER3QDDDQN/3AEDJY4RGJZUX4ZXJZTLHBMQIRXICAZHX!!\n" +
                    "OK9JE9T+S8ENVOIE9LE9MESSAGE9DANS9+T98MINUTES+M43\n" +
//                  "O+LATFMS8T/KC3DHIWAJR33NEUD8QD3Y/KXMVYA8V+WWWNZZFD";
                    "DE9JACQUES9DUMARCAY9AU9MINISTRE+M98NOTRE9PROJET9";

        String allCipherStringT52AB = "CUASGEQBGTPVZLCYKDKSBUL4JSIKCH9QDA4RP/MD+LS3V/E/P/JTKFEQ3F4ERZU383EGMKX3AU9S/TPSZUKS8/K+UZ3WSJTX/CZ4ZLFVVHC\n" +
                "Z3ESMFAFDXLY93/XINNUYNXOUAHHZMUXHA4FWVMVFQFDAMZKPWPDHDRHUXLXLEQO9VPKGPXK3C/4UBE/MUUUG/KJUDKG9IU3YCZ4\n" +
                "RB3SYKC+FGPZCAKXOB3T89U4D4TGCFGXTA4AVPDT8QK+WCEKKWCDLUFK88PERNY/JVAQX4CE8CAS9KSB89P4D4WFYINGRYTK/CZ4\n" +
                "WWF8LPYIQDX4KESG4NFNBQFUMNFZZKUEOA9VWVMCOPRROIK+TN/GSCM9VREV4PFMGXOXZ84W+4WXUIES8MZCD/FKUJR8DUBKWCZ4\n" +
                "HTYTRWQEGKPZZMHCATYI4MTUANGECKCE//NLXAMRKLSL4+UOWLOHH8HIUKO+3E/4GI4ZO8CE4UOF43VT+IAP4BYEZKRFS3OD/CZ4\n" +
                "IB+N3ORD9KRTZTB8YDBTQQZSYXTUOYV/VMTTQWMPEEEJALX8ONIUBXY3UHHST3DRSGT8MLXK3C/4ZHMVUFEUC9T4G4RUI9SO3CZ4\n" +
                "9U38LPYIQYU8Y8ZF3PS+XTAZOOYDZVUFFA3I+TDLKBW3PAKO+LOHHRZETSKE/JXCSGT8M9VINUFFFVEOMUT4DHB//Z38SQX8ZCZ4\n" +
                "BIVSYUC+KKLZF94XBNRTCHUK3XUJKOXY3TFHX4TZP8S44CECKDWLXA/QOENTRQZWGCC8LN8H/UTFZFPOFDYVP9HPYSGQ8F3OSCZ4\n" +
                "ELYS8LF/8T/VBTHSCAPS8TFU+FHMFWVYQ+DTQWM3/IUS4HCMAAZC4ZSOKAMT3WCRJVAV4+ASUQA/THKZUQJP//RKXZVNC4X9ZCZ4\n" +
                "W+QOKUXFXKD4PEOFHDAI8ZCDEXN/JXEKC/XNVJZVEQIPOPZRPKLLWYRGTSKEPXCKJVIEGWVDEZT/NRN8IUARDXRIFZ/+CSTVZCZ4";

        String allCipherStringT52CA = "BXXGBNVYRJFA4WPFTG/9VXA8+DGPYHLQ3TN8TTKBZ/Q+RCXWLFW9JAOIYPIEQH3WGUDXEBV+E9KRPZB394VGDHA3SNKZNBZSLDWX43Q9N/3TSA8UHR/LRJN9UPRHNVXG+M4PTAX4KR\n" +
                "UDFU9PYOOB/TU8VHJQ+8LJH8+DGPYHLQ3LJVIQNS/+GKPYDLDXQNULHJRX4NKOJF+YU/UM+QSQFP9B+NCOWGKGUGZNWOEDE8N4J34+/VSY3ISXZYPGSJINSL3ZXHPBWIG9\n" +
                "ZUXBD3APQT4UQ9HKIWUBSHNZPWDLRLKFLPDID4EF4+VWQZYPL9WNEKH+HSDLDPWHFNTYTWVSA9YDK+QH\n" +
                "UDFGBNVYRJFA4WPFTG/9V4M+/G+TENRLEZZVFBOUDRWFL4Z/VFWAN98WMI44JH3UFSMQE3LSL/4VX/VXUTWVHIU+WNQ+JZHDCLG/WCAKQ/RR4DQYPGTPW9/MNV9K+RFN\n" +
                "BXXGUZYVBWC3DJ+OGGNO4XA8+DGPYHLQ3MPJ/GNEJPTAQH9YTN3CMUVGNIK3LOFKAEDXKEGS4S8G8+JI8LECDHA3VUE4J3LWEENRINGIP";

        String e = "XOVZQDSKY+DEB9CYI3AREKBFVOOV3AHV8VBSXRE+XWR4W3HWEDXHRKFMSZPAD\n" +
                "XVMVD+M4UMRWTFU/A3MKYE4HDQZ9G+CEIFWSXB/FC9CITQJCEDXZEXUPKG3R\n" +
                "XVMVPE+KHH+U8Z3/UXHRANYJNTY/3ANV+3N8DTD+KLFIUUO9VC8N\n" +
                "BVVBF9F4+R4XKWCNXQK/UJYNPHOIXUC9CT+M/CG+FEI4/WFFEXM/9X/DZV+CO4PNZCXWLI/PFMQQCH4LZFE9CJUBNII4ESCBI+E/UCC/UVGQGG+CDGHZSGXDSRTI/ZWA4BFLLYOS4HXHZXJNCVXDT3NIPYQBBFECKXXJZBKHSRBWKR9VLZ3PGRN3ZOUX\n" +
                "BVVBF9F4TLIUOMCNNJKJ/N8VTBGR9SL3SF/D/B/XOZXLMKM/FKMFFK+TQZSXZ9BIMA4/BA8/NKBRXNRU\n" +
                "F4HGJES3L4PC9+IIUSU+T4X8GXNUXF4MKGMY9QF+FVRLOR/T4+OQDHDMO49URPAUE9BZTVYPNRXMEFGESIIE+4ZLUGUKOROXAVAEOKAY3RVUNXQBRBVYNQ99S+QTAPBTSY3OHC8MQJ8GLD3EX3QITFD4PAN4USANNMLJ3FO4MGSTIRE4ENBVIEXC9ADSB4G+8/EC4Z/OHLTUKCGZDJPEWBGKST8NHKCIDHJQ9QB4BNRZNLF/4QSCPFX8YKRESRL99";
        String ep = "" +
                "DU9GENERAL9REVERS9AU9MINISTRE9DE9LA9DEFENSE9PAUL9\n" +
                "DES9MON9RETOUR9EN9METROPOLE9JE9VOUS9DONNERAI9MON9\n" +
                "DES9A9PRESENT9JE9VOUS9ADRESSE9SES9GRANDES9LIGNES+\n" +
                "JE9VOUS9PROPOSE9L+S8EVACUATION9IMMEDIATE9DU9NORD9\n" +
                "JE9VOUS9SIGNALE9AUSSI9QUE9LA9CORRUPTION9REGNE9AUT\n" +
                "ENFIN9ET9C+S8EST9LE9PLUS9IMPORTANT+N98JE9PENSE9QU";

        String c = "ZWYIXNSHOVI/H9K8UAJZBXFLYZAFVZQP/4QGZYMY/VKFYXKX8EHYA+NQQ9E94WJD3MBX4KRUQZV4XZ9UMDMTQQ3TN3IRIOJZXQUO\n" +
                "GM+MIHEN3W4VRYVCCWKEFEDB3ZA4WAPDZDYN++BGR3VUI/KT+GDODVNQ8U9MWQHYGTMGO9XIR/SKPZ3ASLH8YMWLVMBYOC9QXFVN\n" +
                "NWM9/SOPDEG3XB9JA/9NKPQSPS/F/PROHFLSCIA4RNONVC9FELGYDCVSBD/LLSQCPUSOJJ+8GBEZ+T83SKHBHS38WYLQXFNPDU3C\n" +
                "MGEFOBHKPQFXZYEVXAHHIGZTSCICGSBKAK43CNDBGW+EVS4ZJ3B9XHM9T/J/4L8KPOORO4HGD49TERN9OQ/8V3SVADDVK+Q3YC3C\n" +
                "WEMZ4RTY8V/XI9/8C+PE4FB+XLGRONYK4NIS9LBSDHN+YHKFP8AQWEHRVS9C/MUUZRYHNN8UOQS4VTMMLL/EZX/PX9KIX93L3JH8\n" +
                "XWFIBQ34DEG3EUIMLU8SH/PVXZGSJLUEW8TNQVPIWYXLGQ4I+SJJZMZAQQKISPTULYSZDUXWWKQ8+QMKYOZNYRYRSQEYVZISOBHW\n" +
                "RQO4NQVD+GXCBKCA/AGBH/PWIZNQZLIOAQYQ/FQDCWC+O3UQVTZMYUFM3HGFX+8FUABEN3O8ONQKBHEGMM4EFGYAG9+KXP+4OB/N\n" +
                "IBEF/FEN3WXVFKFFMUZSHZPXATGTH4ROHYYH8M4X3CKLZLUETCJ9WEHRVS9IWL/PP9LLFKENQP9TPRN9YVFQE8/MZIZVK+FPDEPI\n" +
                "9VAMNEWJH9FC+UO8HGUXIOHRPOV+W4PDR+QKKIBSOHYAECZJRGHZGVBP83+KVLPKSYYNWUICYIM94UJBNB/LS8/ZJGVQXS4LXJ3I\n" +
                "KF/FFI8PJV/XBUVBE9ASWJ4J8CICGJ4J3DTNLC/QIHMAEFZ4ZOUXZN3HU+KA4VZUVAZET3XM8WDV+QMKMN3AYJ4LEQD+H+LF3QNS";

        String nils1 = "FMFPGBYGQVTFR4BORV9BTK8BO4X+HKWVRVFOBJLQ4XSKDKAKVDEZHMWUXEYTONOSZ+/9ZNTZPDVJINW+K/89UU3HMPUKVK4JTCX4XRV3OKVECZPT9QAUA9MUHEN3WKLBTOH\n" +
                "ABWHFVJL3A/FR4BTCEOKVR3VSX8CN4LDSLO3EXWFXUVD+PDM8EZGNMWXJQYHOXW4AAW84PH3D3A+9HGYCVNRCHTYFXJXUYNBY/ZJLXEFGC4UX/ECE+CPI4X9JMR38VLIJACFIQAH\n" +
                "MFUHFVJ4GUT9GSGCOJ/KAN8F44DTREN3UKTFDYGP4ESNKDTYIBZTNVRT3PUDBYQSZ+/8I+34HI9/LQYDS4YPMU48PE8+F8FI+PE4XRVZ43AWE/9GF4TGKD+SLFRL/EWYFQRYSY//B/W3DFBFWVWM8DBU/4IHHH/MYB\n" +
                "3C8YCDU+EFCL4SG4P9JOUFP93UTK4X8VX4OCV93HYRAZVXEI9LF/ZR+FGCUHACEHBJZRRAARQC4GJQQXXWGQ8JCB3TIY489CFCIXV4RTGNSUVQAINFABRQ+/O9OPOPYCDFYGD+GVSF+H3PPHTQ9YBVS4PJZJ3LVZMH\n" +
                "8TSJ3OCM8T/TEBI9RINW+KG+L++UIENQV43G93PEKYQ3JIFYSV3VZR+W4S9+CANKUFFCBSFMVD+LJL4MMQY9ZAAJJZJWMIT9NVHQLOTKV3ZHVQAINFMORFF38EMSUX/Z+ORQYB4ETU+/MHM9YVW\n" +
                "ABTROZ3OXXVTEBSUC9EOUCYHF+9HPPNWM+DVV33HTM884BGNMN+399SZGJUXWVCUUSFFMWQUECN+9+RALG9VDV9AJ+JFM9+XZKZHQXYS+C8AB4EYAFMOU/W/OOO4OIY38K4PMMKBINSOYEPQWVLBPHPZ+HDRMXYDXCALS\n" +
                "IKKFK4H+AKDJQIZHU8V9ZRBKG+9HPPNOE/BID9WJ8SSKHB/K9VDV+D4TOC+ZZV/FV3SPJ8NRZD9TMGIVXDGHDN4+P3QVH8RJBE9/89Q/USSL/UCGD4BMR8TIHMSVHKJ/4TGTIHKGZD+FK8PXHBAWRPEHAKEQ\n" +
                "IE9GAC+EV4+EIPITB+99TUNQW++U3GVE9QVH/3P+3IPTDL9XEFPT98SZRJ39ZVBUUFFCBPTJ+IMJXN3GSIE8RHKYEBJZUWLB4/ZUV8LBUCAMIVVCFWMLJRC/9KLVAR3OSCYVDRBMV/D3W9K4JLZZOK8REDMP"
                +"\n" + "NFUJXDIIVXL3M+BGAK+JSGNM4Q/QWC3IQXNYUMD/APLKOC4CIBUSBEARE8RE8URS8XPVOGTUHJ9LMYWCEHQ/CI98J+HJZVVIYCJXOZVWHO4A4URZVSGYRJYXJ+XJAOKGH+HUVR+LEH9ELT4JL"
                + "'\n"  +  "+A+/CXYGYKVGEBIFBT99TCGACGBAWSIVM8LAEDPJ8J+TPLFWI8D/9ISHBCYN/CJRAXWCRLARD3JS+GFNHVMRF8XHXMIPWSKULEW/84YH/WIX4/EZA8WTYNH/OFDSUGJZRORZYSDEVUO";


        int depth = 8;
        int len = 131;
        byte[][] plainArray = new byte[depth][];
        byte[][] cipherArray = new byte[depth][];

        //ReferenceText.create("shakespeare.txt", false);

        //Sturgeon.r.setSeed(1000);
        Sturgeon sturgeon = ReferenceText.simulation(Model.T52D, depth, len, plainArray, cipherArray, true, false);

        depth = parse(plainArray, cipherArray, nils1, nils1, 0, 0); plainArray = Arrays.copyOf(plainArray, depth); cipherArray = Arrays.copyOf(cipherArray, depth);
        //depth = parse(plainArray, cipherArray, allCipherStringT52AB, allCipherStringT52AB, 0, 0); plainArray = Arrays.copyOf(plainArray, depth); cipherArray = Arrays.copyOf(cipherArray, depth);

        //TestPerm.findValidSwitchesAB_D(plainArray, cipherArray, true);

        //TestPerm.testPermE(plainArray, cipherArray, true);
        //CribAttack.solve(Model.T52D, cipherArray, plainArray, null, "4-8:3-5:IV:I:1-6:III:V:2-10:II:7-9", false, true);
        //CribAttack.solve(Model.T52E, cipherArray, plainArray, null, false, true);
        //CribAttack.solve(Model.T52E, cipherArray, plainArray, null, sturgeon.key.getWheelSettingsString(), false, true);

        //System.exit(0);

        //hc(Model.T52E, "1-9:2-5:3-7:4-8:6-10:I:II:III:IV:V", 100, cipherArray, plainArray);
        hc(Model.T52D, "1-8:2-7:3-4:5-9:6-10:I:II:III:IV:V", len, cipherArray, plainArray);
        //hc(Model.T52D, null, len, cipherArray, plainArray);
        //hc(sturgeon.key.model, null, len, cipherArray, plainArray);
        //hc(sturgeon.key.model, sturgeon.key.getWheelSettingsString(), len, cipherArray, plainArray);

        //sturgeon = new Sturgeon(new Key("T52D", "4-8:3-5:IV:I:1-6:III:V:2-10:II:7-9"));
        //simpleTest(plainArray, cipherArray);
        depthTest(sturgeon, plainArray, cipherArray);

        reconstruct(sturgeon, plainArray, cipherArray, 0);
        reconstructSimple(plainArray, cipherArray, 0, true);
    }

    private static int parse(byte[][] plainArray, byte[][] cipherArray, String allCiphersString, String allPlainsString, int start, int length) {
        if (length <= 0) {
            length = 10000;
        }
        String[] ciphersString = allCiphersString.split("\n");
        String[] plainsString = allPlainsString.split("\n");
        int depth = Math.min(Math.min(Math.min(plainArray.length, cipherArray.length), ciphersString.length), plainsString.length);
        for (int d = 0; d < depth; d++) {
            String cs = ciphersString[d];
            if (start >= cs.length()) {
                throw new RuntimeException("Cipher too short ");
            }
            cs = cs.substring(start, Math.min(start + length, cs.length()));
            String ps = plainsString[d];
            if (start >= ps.length()) {
                throw new RuntimeException("Plaintext too short ");
            }
            ps = ps.substring(start, Math.min(start + length, ps.length()));
            if (Alphabet.isBritish( cs + ps)){
                cipherArray[d] = Alphabet.fromStringBritish(cs);
                plainArray[d] = Alphabet.fromStringBritish(ps);
            } else {
                cipherArray[d] = Alphabet.fromString(cs);
                plainArray[d] = Alphabet.fromString(ps);
            }
        }
        return depth;
    }

    private static void simpleTest(byte[][] plainArray, byte[][] cipherArray) {
        for (int d0 = 0; d0 < plainArray.length; d0++) {
            for (int d1 = d0 + 1; d1 < plainArray.length; d1++) {
                for (int i = 0; i < Math.min(Math.min(Math.min(cipherArray[d1].length, plainArray[d1].length), cipherArray[d0].length), plainArray[d0].length); i++) {
                    byte p0 = plainArray[d0][i];
                    byte c0 = cipherArray[d0][i];
                    byte p1 = plainArray[d1][i];
                    byte c1 = cipherArray[d1][i];
                    if (p0 != -1 && p1 != -1 && c0 != -1 && c1 != -1) {
                        if (!FiveBits.compareBitCounts((byte) (p0 ^ p1), (byte) (c0 ^ c1))) {
                            System.out.printf("[%d %d] %2d\n", d0, d1, i);
                        }
                    }
                }
            }
        }
    }

    private static void depthTest(Sturgeon sturgeon, byte[][] plainArray, byte[][] cipherArray) {
        boolean[] valid = sturgeon.checkDepth(plainArray, cipherArray, -1);
        int allErrors = 0;
        for (boolean b : valid) {
            System.out.print(b ? "_" : "!");
            if (!b) {
                allErrors++;
            }
        }
        if (allErrors > 0) {
            System.out.printf(" %d conflicts\n\n", allErrors);
        } else {
            System.out.println(" No conflicts\n");
        }
        if (allErrors > 0) {
            for (int except = 0; except < plainArray.length; except++) {
                String s = "";

                boolean[] validExcept = sturgeon.checkDepth(plainArray, cipherArray, except);
                int errors = 0;
                for (int i = 0; i < validExcept.length; i++) {
                    boolean b = validExcept[i];
                    if (!b) {
                        errors++;
                    }
                    s += (b && !valid[i])? "?" : (b ? "_" : "!");
               }
                if (errors < allErrors) {
                    if (errors > 0) {
                        System.out.printf("%s %d conflicts without %d\n", s, errors, except);
                    } else {
                        System.out.printf("%s No conflicts without %d\n", s, except);
                    }
                    reconstruct(sturgeon, plainArray, cipherArray, except);
                }

            }
        }
        System.out.println();
    }

    private static void reconstruct(Sturgeon sturgeon, byte[][] plainArray, byte[][] cipherArray, int index) {
        int len = Math.min(plainArray[index].length, cipherArray[index].length);
        String[] options = new String[len];
        for (int i = 0; i < len; i++) {
            boolean[] valid = sturgeon.getDepthOptions(plainArray, cipherArray, i, index);
            options[i] = "";
            for (byte p0 = 0; p0 < 32; p0++) {
                if (valid[p0]) {
                    options[i] += Alphabet.toStringBritish(new byte[] {p0});
                }
            }
        }
        for (int l = 0; l < 32; l++) {
            boolean stop = true;
            for (int i = 0; i < len; i++) {
                if (options[i].length() > l) {
                    System.out.print(options[i].charAt(l));
                    stop = false;
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
            if (stop) {
                break;
            }
        }
    }



    private static void reconstructSimple(byte[][] plainArray, byte[][] cipherArray, int index, boolean erase) {
        int len = Math.min(plainArray[index].length, cipherArray[index].length);
        String[] options = new String[len];
        for (int i = 0; i < len; i++) {
            options[i] = "";
            byte c1 = cipherArray[index][i];
            byte p1 = erase ? -1 : plainArray[index][i];
            if (p1 != -1) {
                continue;
            }
            for (p1 = 0; p1 < 32; p1++) {
                boolean good = true;
                for (int d0 = 0; d0 < plainArray.length; d0++) {
                    if (d0 == index) {
                        continue;
                    }
                    if (i >= Math.min(plainArray[d0].length, cipherArray[d0].length)) {
                        continue;
                    }
                    byte p0 = plainArray[d0][i];
                    byte c0 = cipherArray[d0][i];

                    if (p0 != -1) {
                        if (!FiveBits.compareBitCounts((byte) (p0 ^ p1), (byte) (c0 ^ c1))) {
                            good = false;
                            break;
                        }
                    }
                }
                if (good) {
                    plainArray[index][i] = p1;
                    options[i] += Alphabet.toStringBritish(new byte[] {p1});
                }
            }
        }
        for (int l = 0; l < 32; l++) {
            boolean stop = true;
            for (int i = 0; i < len; i++) {
                if (options[i].length() > l) {
                    System.out.print(options[i].charAt(l));
                    stop = false;
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
            if (stop) {
                break;
            }
        }
    }

    private static void hc(Model model, String wheelSettingsString, int len, byte[][] cipherArray, byte[][] plainArray) {
        int depth = Math.min(plainArray.length, cipherArray.length);
        byte[][] plainArray2 = new byte[depth][];
        for (int d = 0; d < depth; d++) {
            plainArray2[d] = new byte[len];
        }

        long[] scores = new long[len];
        int[] control = new int[len];

        double bestOverall = 0.0;

        int shift = CommonRandom.r.nextInt(ProgrammablePermutations.validSettings().length);
        for (int index = 0;;index += 1) {
            int permIndex = (index  + shift) % ProgrammablePermutations.validSettings().length;
            Sturgeon sturgeon;
            if (wheelSettingsString != null) {
                sturgeon = new Sturgeon(new Key(model.toString(), wheelSettingsString));
            } else if (model.hasProgrammablePermSwitches()) {
                sturgeon = new Sturgeon(model);
                System.arraycopy(ProgrammablePermutations.validSettings()[permIndex], 0, sturgeon.key.wheelSettings, 0, 5);
                sturgeon.key.wheelSettings[5] = WheelSetting.parse("I", sturgeon.key.model);
                sturgeon.key.wheelSettings[6] = WheelSetting.parse("II", sturgeon.key.model);
                sturgeon.key.wheelSettings[7] = WheelSetting.parse("III", sturgeon.key.model);
                sturgeon.key.wheelSettings[8] = WheelSetting.parse("IV", sturgeon.key.model);
                sturgeon.key.wheelSettings[9] = WheelSetting.parse("V", sturgeon.key.model);
                sturgeon.resetPositionsAndComputeMappings();
            } else {
                sturgeon = new Sturgeon(model);
            }

            ArrayList<Integer> uniqueControls = sturgeon.uniqueControls();

            for (int i = 0; i < len; i++) {
                control[i] = uniqueControls.get(CommonRandom.r.nextInt(uniqueControls.size()));
                for (int d = 0; d < depth; d++) {
                    plainArray2[d][i] = sturgeon.inverseAlphabet[control[i]][cipherArray[d][i]];
                }
            }
            double bestScore = Stats.evalQuad(plainArray2, 0, len);

            int sub = 0;
            boolean improved;
            do {
                improved = false;
                for (int i = 0; i < len; i++) {
                    for (int c : uniqueControls) {
                        int keep = control[i];
                        control[i] = c;
                        for (int d = 0; d < depth; d++) {
                            plainArray2[d][i] = sturgeon.inverseAlphabet[control[i]][cipherArray[d][i]];
                        }
                        double score = Stats.evalQuad(plainArray2, 0, len);
                        if (score > bestScore) {
                            bestScore = score;
                            improved = true;
                            if (bestScore > bestOverall) {
                                bestOverall = score;
                                System.out.printf("1: %5d-%3d %f %s\n", index, sub, score, sturgeon.key.getWheelSettingsString());
                                for (int d = 0; d < depth; d++) {
                                    System.out.printf("%s\n", Alphabet.toStringFormatted(plainArray2[d], true));
                                    //System.out.printf("%s\n", Alphabet.toStringFormatted(plainArray[d], true));
                                    //System.out.printf("%s\n", Alphabet.toStringBritish(plainArray2[d]));
                                    //System.out.printf("%s\n", Alphabet.toStringBritish(plainArray[d]));
                                    //System.out.println();
                                }
                                Stats.evalQuad(plainArray2, 0, len, scores);
                                for (int z = 0; z < len; z++) {
                                    long sc = scores[z];
                                    System.out.print(sc == 0 ? "_" : (sc <= 4 ? "." : (sc <= 6 ? "x" : "X")));
                                }
                                System.out.println();
                                System.out.printf("1: %5d-%3d %f %s\n", index, sub, score, sturgeon.key.getWheelSettingsString());
                            }
                        } else {
                            control[i] = keep;
                            for (int d = 0; d < depth; d++) {
                                plainArray2[d][i] = sturgeon.inverseAlphabet[control[i]][cipherArray[d][i]];
                            }
                        }
                    }
                }


                if (!improved && (sub++ < ((model.hasProgrammablePermSwitches() && wheelSettingsString == null) ? 10: 150))) {
                    Stats.evalQuad(plainArray2, 0, len, scores);
                    int lastGood = 10000;
                    for (int i = len - 1; i >= 3; i--) {
                        long s = scores[i];

                        if (s <= 4 && (lastGood - i) > 3) {
                            control[i] = uniqueControls.get(CommonRandom.r.nextInt(uniqueControls.size()));
                            for (int d = 0; d < depth; d++) {
                                plainArray2[d][i] = sturgeon.inverseAlphabet[control[i]][cipherArray[d][i]];
                            }
                        } else if (s > 6) {
                            lastGood = i;
                        }
                    }
                    bestScore = Stats.evalQuad(plainArray2, 0, len, scores);
                    improved = true;
                }
            } while (improved);

        }
    }


}
