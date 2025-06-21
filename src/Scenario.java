import java.util.ArrayList;
import java.util.Random;

public class Scenario {

    private static final String SEPT_23_1942_WHEELS = "IV:1-2:7-8:II:3-4:9-10:III:V:5-6:I";
    private static final String SEPT_23_1942_QEK = "09:01:06:41:36";
    private String desc;
    Key key;
    private int offset;
    private String cipherText;

    private Scenario(String desc, Key key, int offset, String cipherText) {
        this.cipherText = cipherText;
        this.key = key;
        this.desc = desc;
        this.offset = offset;
    }

    static Sturgeon preparedScenario(int index, int start, int len, byte[][] plainArray, byte[][] cipherArray) {
        if (cipherArray.length > 1) {
            throw new RuntimeException("Invalid prepared scenario - depth not supported");
        }
        if (index >= 0 && index <= SCENARIOS.length) {
            Scenario scenario = SCENARIOS[index];

            String s = scenario.cipherText.replaceAll("[ \\n]", "").substring(start);
            len = Math.min(len, s.length());
            if (s.contains("+")) {
                cipherArray[0] = Alphabet.fromStringBritish(s.substring(0, len));
            } else {
                cipherArray[0] = Alphabet.fromString(s.substring(0, len));
            }
            plainArray[0] = new byte[0];

            Sturgeon sturgeon = new Sturgeon(scenario.key);
            System.out.printf("Loading scenario number %d: \"%s\", key: %s, length: %d\n\n", index, scenario.desc, scenario.key.toString(), len);
            scenario.printShifts(false);


            for (int w = 0; w < 10; w++) {
                sturgeon.key.wheelPositions[w] = (sturgeon.key.wheelPositions[w] + start) % Wheels.WHEEL_SIZES[w];
            }

            return sturgeon;
        }
        throw new RuntimeException("Invalid prepared scenario " + index + " (should be between 0 and " + SCENARIOS.length);
    }

    private void printShifts(boolean exit) {
        String s = cipherText.replaceAll(" ", "").toUpperCase();
        String[] split = s.split("\\n");
        int pos = offset;  // 201 for 9
        if (exit) {
            System.out.printf("Printing shifts for \"%s\" - key: %s\n", desc, key.toString());
        }
        for (String line : split) {
            System.out.printf("%4d %2d %s\n", pos, line.length(), line);
            pos += line.length();
        }
        System.out.printf("%4d\n\n", pos);
        if (exit) {
            System.exit(0);
        }
    }


    public static Scenario[] SCENARIOS = {
            // 0
            new Scenario("not working",
                    //[ 5]Change Type       [ 1365][  37807335][C:    5804] -   1.232050 -  ..:..:..:..:12:57:29:11:46:.. - ...:....:...:...:I:II:III:IV:V:... 1-4:2-10:3-7:5-8:6-9 1-4:2-10:3-7:5-8:I:II:III:IV:V:6-9 - Elapsed = 252668 ms Rate 149632.462362/sec
                    new Key("T52AB", "..:..:..:..:12:57:29:11:46:..", "1-4:2-10:3-7:5-8:I:II:III:IV:V:6-9"),
                    0,
                    "                             4jj3skwevyt3xpvuk1zv3b1vxw2vgt2xo3bdsms\n" +
                            "54o1vyfpgvclwdutrycqzvrpa2q3mry1xcbykeewcp4ulmqkv2oq6vbmwbs3ckxu534\n" +
                            "csmjlawim1tex64z3tz355qz3dtyhzbqco3xfq36jiqt5jvwqoogxcy3qkllknk3wwfb\n" +
                            "2vxcpzqjxjd326qvo24t3nb6q4sc3gbwmtz3g3245zql23m4pbmsqgh5m3xem1yv4xp\n" +
                            "kpd4jpuqqvsoev5qco6pqdt5bpwnwn3yndqhbevr2zl463xzwvbqpywsbq4vepj1mwx\n" +
                            "obwtfqtvlbb3qavrprf4dqgmtvwn34ppc4cypbxljby!xnvfp3jhw6xqycn2xynoo55\n" +
                            "3qpyn4xvyfbn3ypcvsyypzsmqwcqvqtiofgf23lv2qz4cyul4ambevmfy5vjvcmslzp4\n" +
                            "fsto3wop33vj6324ty63jp34fgrpgqzq3lxqpghx2tkq4wac14pfshxbzvzevrx3cyf\n" +
                            "ziovv4wsl3u4ygtmnn4im34dfksqdgjxdfplhqpf6wlxo3wipc4oms3oxav2vhm5xw5\n" +
                            "hgpxqwyrl363hllb3wm4x63jqhghqwlvtpbwzd3ff4dtowxmpzwlmfcfxmtkdoxbspuw\n" +
                            "e4txovd4rbgfoqfmawn4qmrftpwhi3xgl3vtmb6youxk6qoywq3gwzdqjlto2mxm2wu\n" +
                            "3vi4qrx5zxe3gz4n3xaw35vel4r46xtqm!xo33axsyjtozqlozmx31uwoit3vizm4gv"),
            // 1
            new Scenario("ciphertexts.pdf p2",
                    new Key("T52AB", SEPT_23_1942_QEK + ":46:31:25:16:06", SEPT_23_1942_WHEELS),
                    0,
                    "3OTUB\n" +
                            "S1L2MUFDMESQPYASAVAO5KWX6Q1SUEXUJNCX4DC4VCJ6CHF21OF2XX5SXUTPX24Z\n" +
                            "11AC4S2UKGDP15JLGVSTBFNWE12LWHRESWFRVQQXFPDUMZCRHMQZGCGABNYWUSYG\n" +
                            "1ZOXSSZLLCECICXZ4O2EG4PIMKJEHAGS45GJK5MEGWGUSTVMXEBIKQZMXUVPOZ6W\n" +
                            "53Q2CXUAUJ6AAALOFEHA46T1XLOUGSVRKM5BTJKHH1TUNKFVDO1SGS5W5EK5L16C\n" +
                            "Q4WT2K6VV4SDWAO6CRZ33TDWZNEZYDCWQUIB4EZQ53UR16EP3EJ5SKFZH5GFLRC\n" +
                            "EIK54TW6J4OVUCUT6PHUEFYLTCSVZTUZ5LYXANFRWW1UARD135OA5IEPZNJHJZOG\n" +
                            "LGVELA2MA1NEPBFC3S1AHT1RPCQMLMX1QEJXK6KQQGCZZFZTXGKPLKJMCQJGHXAP\n" +
                            "I2K2YMFOM5W6GRR6YX3MPCVNUDO2KIC1VWAJC43NBZRVUU3LKHXSKOEFOY43R3SD\n" +
                            "SLA5GAWHSHHHEF2QHUR4TVLQEPGM26OQVUZCJRB1UL4M1XA3GY1QGHOUT1L4OTZW\n" +
                            "Z1ZREA6B5TO5HWINDUNKVSNJ5UORGT43BMRXEXDNE4VORRVM2M5E5PJV2YAN6OK\n" +
                            "FLLFNZU3YELUD5CX1WQNWZWRXGHBX2D3PGKK5BKMWDYHM1HYI6C14M5G46JR2S2S\n" +
                            "HWHIUXCMBFI2SMR6TQH4GLGHGD11BPUB2IOUBJECJRAQGTZZT1DOFKOAVOYHE6XQ\n" +
                            "FCHBZE4C3IQ62ZC5BFBCFL6STDLAADMA1MZZFRHDOM1IMEYVEUMW6COHGL4FI2IH\n" +
                            "ZWZJXHP3O3WMHHT6RQ3KGYTJW2O53TWI5I2DANN6DMY3ZGHNYFX53D2SJ3E4EOO\n" +
                            "KBAM2KIZ1IJDHKPDPK6RMBMRIA5MBQ6J11IF2QWG6XREIBHSYIYOBXRQO6HZNGMY\n" +
                            "HB2CD426SZPSJ1FOSFZV55GLUODPA6GPLFO5KX1AGT2DBU3U5ECZ1ZROG1U1FJHK\n" +
                            "IXJL1M5NTXHV3WY3PSBSL6YNASTYQOYA13KYVBJKESOHM61RVG3PRSU2PIQRHTQR\n" +
                            "63X2S43WHSWM1YI15QMLBUOCEUZOOLQGV4HJFZYYQZBLOFVP6LR3COOYJZEETU5G\n" +
                            "MGM1BXI62GOA4OG3KORKP1TNNAPK1U6X6PFISIGHHL3PRC5OK12RAEHMZKBG2Z44\n" +
                            "YKNHTTOGBPHOGFXINLA6UCQ1V6XDZWZ6GDUQGXFK5GDE6VIXNS1BG44IL5KOWTDY\n" +
                            "GVGVUVARKSWIENHMCJ3T1XTKJQQCTTRGMQ1533QCRD3GBJAUP15U2SFLDBX5B5\n"),

            // 2
            new Scenario("ciphertexts.pdf p3",
                    new Key("T52AB", SEPT_23_1942_QEK + ":34:25:19:17:15", SEPT_23_1942_WHEELS),
                    0,
                    "UOXO1\n" +
                            "IQJHC1ESEYUWBB4NYVS5JQV1ZFLFS1RK24MVCD6BFVGATQLUKAZH4AFKXOTS3VUV\n" +
                            "HPEY64NRK6C2W3HE135BU22OSKALIRM2B6M5CPOS2C3T6IL5P2MG3VL53DVC44F\n" +
                            "5OCS63M6JQLTICMAQQATA631W3Q1QIATX6H1KVCKQ6HSVSVITWGLTTHNRVXBWDKV\n" +
                            "W2Y6QKTTNUCRPZTAO1JPBO4RYP3533HFHINZDOWESKVHW4ST1EPTZM4APFFWBCVE\n" +
                            "YPJTBGNAHUH3Q3MLVFVC1RBOMEHRX6DILF61G3SHWSKKF6H65UKVGV1AGVTXW5NO\n" +
                            "PX14RTUJNVNPEEIZFRWISOJWHMAWJTB5UCUBOZLFZKY2VM2VCSL1O1NHYHY6JCM\n" +
                            "6KPH4DKRFPRNWAE2S1HFZBOTQL5T2J3UTGTIDXKVUDIK2DMOEK5DN1QAF3DXZNO1\n" +
                            "XB64VKIZOQ2SBTLDVGI2DXQ3SC6ORGUMENEUNFOJ6K2UOPEJTMEFA21HLR2HKPQA\n" +
                            "VTF6USMMFYI6ZFUS2ZWTMOPYASYDFKTHE1KW1NY42KBUTE1M124JL6RGOQCJIM4H\n" +
                            "TFKAODHCLNPM1LE6CIZMDG3KCZIXOHSCANEQEGJM32QTZF2TNYH4WHHM5KCQA4NW\n" +
                            "VBYKR25S6GSVKXXHD4RMMX3D2ZJN2CCQST4UIV2VHHXQXA44YRYAU5VCOQOU6J5\n" +
                            "6OCIGJKBFLFW5PYIEUD3WDHRRRX5VVJVBV3ARGGUT5MREKSMETT2BMXY5OCRQASY\n" +
                            "BZXKHZ6COXERAVXIGX5CHBN4XGQT4GVBOWCEHW15EX3XF4AOZNWCF6JDYA153GKB\n" +
                            "6BRSPJDNWYLJBGAQBACRGR5TUX35AKSALPJ4CKSNGKKAE22UF31DT4HKHEGJ3ZS\n"),

            // 3
            new Scenario("stepwise",
                    new Key("T52AB", "05:69:18:07:28:63:08:03:52:06", "I:V:III:1-2:7-8:II:9-10:3-4:IV:5-6"),
                    0,
                    "                                                             gw4akuna614qy\n" +
                            "leuwhd1dffskkgoehwtqff6bun2ehol2aaip5ivfozxfiuov41d1yveivbufuj34c\n" +
                            "busmot6mbjmz5yhdlqyly6bq15jljuxcarxh3kig5sewjxu54ikykhl4brxcgl1wc\n" +
                            "o1jqjpud4ihf4ufdmrxtmjlctxih5zm4q4ppxkuk2hjz4yb2grppjzashizy6gdnwg\n" +
                            "aihpcpcznhntt564gjkfpls3dtiykl2qk6ckzw4oyyky6h4nchvfhyvc2hg1ku4jl\n" +
                            "cijykfd2ujgsovewojxzfyvogkenjqdx5l2mdg1kcqkz5rvcxypo11cfvxfdxyror\n" +
                            "tulhddxfwwun6fkuojpxb2m2d31lwkusxy11z4d42og2jmepu1iw16vztzru6aobbm\n" +
                            "3xz3hgwfixu3ax11avq5critbqgorwb1ut5kwgt6r5yhea3y5laeksknnggb6vbxq\n" +
                            "oisb3dnlp2d1ajp1llyslnjpajqnr4v61ufc5kgqt33hrjxe6hppdgs6au1ru5x1ad\n" +
                            "v4v41oz2hzjlq5ie4or4t3vuh6jpz6zsjjaxhqez4hpus6cnkmilwttq2jusgitqs\n" +
                            "n4n3rocvggnp64panzexisyrol6osubsf5glnzqkqtbtzz14pqv3rk5s23tfbe1tn\n" +
                            "rwvazdrr1u4jf31lazz2w31rxbvw5b3631ubgvsrtjqr3s3y33j4elqmvf1qu2yamq\n" +
                            "xvifysfz1xvt35ksuiplfginv1woictuwb61gxgvhdpnayk6kdbdxlwadookq6eks\n" +
                            "md2prikeyhm45vip5m2c1wkr2g6p2bafen6otpgqxz2ngrl53edui4sdgsjorjrrj\n" +
                            "tusggc36z6zuaso1mfduxod5kfdhaiifw5oxhh5xpdchjd5a434xqdt3swfx2wtk5\n" +
                            "x3xepx1vsbapmfekedl6ru2gy12ah1bal1imld14exbr1u5a1dmjoi\n"),


            // 4
            new Scenario("t52 no 1 p1-2",
                    new Key("T52AB", "45:67:54:34:24:16:14:52:51:40", SEPT_23_1942_WHEELS),
                    0,
                    "zrddulfh5fnyl3qu4an12dzu!\n" +
                            "sv25i3uaywetitoxenhtjju4t3mhtyw42rrebfu21q6dd4vcmukabe5ouovxllj!\n" +
                            "s4huhugrz6tfllvxskvrphy2t4m233dzpl5nghmcvj1qu3lck2eypsygoiy1y3l!\n" +
                            "b5q3uvabiwa3owbfedyp1vzuhcz3xszrx12ueotj3s4oluodpfta6ya1uddsdyz!\n" +
                            "uqu666wdbdd6nbntlh2x1vig2q1vzwptibsqze1jlavpmngrvdcxasosfehpb3j!\n" +
                            "du6fvs43yk12pxsravhzzrlwma42b4iqueunrgmub4ylgzy4sly4mcj5aygrd5z!\n" +
                            "zjed2tmx6tpxqr4c56zmc2jgdtjvuio25bcgg3im32bvqepqercxtlq3ymqooes!\n" +
                            "p6jfwfqgjerdviwfo1hwxwvcrw2eithjlmjhzqw3pw2fod6comhctr1qo62odqq\n" +
                            "!ybfyyjuht35emkknrpytmzl6pzxj6fhhrjfhochbge5uyxyxzqdkmzi3jgw4d3r\n" +
                            "vlai1qts1ay66vfbxyj1xhu1w13afeyu3ualydw2m42p14emakoxie52qqvwysv!\n" +
                            "3hhqg1uqws6k6yjpymay3exosi6qedg1a5whhugpum21uch1a4pyaympaxuxcdg\n" +
                            "!z2t6wmqaah2gj2ng142glatswkpm2dpgv1rwilpcfq3w3ztk5w2jbkk25ivcpzf\n" +
                            "g2hzihaxxrvwyghpauo3opotyhzij5ueqaxhehvyi2ya32xefb16iuhhg4ussdxt\n" +
                            "pgspz5f3lgl4hmxtis12isg6uv241cx3mqjs5damfr4yubk1ecpzq3siyiz11ue!\n" +
                            "4atscodgvkfbwdu15v62czfpfijctzsalgnedqixhber2yk65q5w3rkp11ke6jo!\n" +
                            "fcfn2yvmwsn6g3hulfuedgpn1bw66siddfjixpbre412xg6z2bxgkfn2hogjdec\n" +
                            "!1t532zyeohbt6wyphxb5ciwfot2f31zstcylakhucjih5krbk6y6wqkghx2qz3e!\n" +
                            "pfpdvuj4udtnilxei5ja4ocp553dv5webnhttoqpsxnyfyipgyp2jvodahaozli\n" +
                            "eqycyfctudcfmgbdmeivhvi43va6wpa3qptttv5p43zfl4cwm5xxyfpkzk12hqfi\n" +
                            "2pykijnlo5pmxb3fi3ahhd324sxqu43e5wmccwwirbog2qptcnslhil12vacnzf\n" +
                            "      nebwi4ialhw6uigyrnihjx3drtjuv5f2z25q56t3umrtgw4vjsqvcgo5u4\n" +
                            "yxxnv4tttmyfq2rw2yb3bwgtzdfdsa4mx6ig44otxjx1fsu1qih6q3qxvrk2oqs3\n" +
                            "xuwv1abcdo1odinbmwwmcu6ouleaon6alerzn3mfmw6m2y3a3xhxldbabv3uya1dk\n" +
                            "ahlu2c11wwfjpq452541scs3a6kjzl2rnkm5hxpnikjn2w16e2h4reknxq4516u\n" +
                            "wmhsret53lqhet3u61ggugg2chh5!k x42qkacye3vkjangjvikxhiaymez3eftykua\n" +
                            " 3loqe6owbiz6ytkzfz6zpbt1wn6ycui2bm2vkiidg25gkqjmaoqetb2dgylec2n\n" +
                            "exlvpcdgzcrcvsxh12dd5bq4416lys612bve2a55p1secv4qdij2j4yvlk2aadpn\n" +
                            "ufh3wplo6lfbueowyxc4xme2uga5jjqvwguxxasuye22fbcs2beqz2kqze2kyyhc\n" +
                            "iddx3ztrjgyohac2pmzk5jfmxyo1nd25dv5op43byy3dyzayq31afsbnmqgbuvmh\n" +
                            "s14tur22a3pytw646wjplosresu5ooeaazebehevvscxtj3odgp6bsa3stwntzs3\n" +
                            "eyfznjemcqt64z4u4if6cjw3d6h5q3z162gchuxrcutwqsnyr5pllgw5yfaw1pev\n" +
                            "53y3nq2o6esuy6og6v5b5325wwpvciw3t2bp6kfojpczx2aza45bww2rgqa6i6b2\n" +
                            "455egstudvnxu6efvpwx42gntxxsmjml6zk63navymlmy2nh2bzj3zob6tzhtqy\n" +
                            "e64g55sisai6cmmuorl642yu4d6xs4of6ip446hhavsbdflou23f2vav1e4mp61o\n" +
                            "dsvtl3yllm5rck6lllemtdqlp3fveu23dertgchgxs3xcfgp3we55v6wspi1yy5v\n" +
                            "t4ofhrkw5dfjs6iboxpbdh52jywamhvd6gfhv112tu6q2fkzu4pqojl1rkrxmaf3\n" +
                            "uhaqk5ns4ipkth6h1y5n5l5chj4xz1h4cfpnlypza5ipd63mxhubdcrq2d42rn12\n" +
                            "tihepinsthyn5maog6vr4x5wbfhzfp2deqnke3z1fwpz4rt6p6mosvvhzq1wxfty\n" +
                            "agadyiof5zs4itquniqanphkv3jn2el6zepvocz1us2qftaldkvbne4rjeg53krq\n" +
                            "zmmonrrco4fewgvuopl2dfaucxflaq5gxiqvtnqbyifrfiaxfnw4uvg24bmagc1\n" +
                            "y5frncgbdgf336phx5fv6hz5vb1akzpzn65mmu5zqxlwk24tzj3hysysgih5mhkq\n" +
                            "4t4q5k253bjzfkpoaodunqvitbikzdb3wkbemrtar1vhvxcjhohsf54qigeew12p\n" +
                            "ocogegbkop1bhuxet3ercqayogln4nzmmhncdhgbsreyirgghts6qfklzubyp6ie\n" +
                            "un1pndlmwrad3snztp63unaf1izeritjqqgsif3bbmnh1gyigrlok6hm3ko4vdjr\n" +
                            "uz3xebkchii5i2btubpsarlruwltkakbithf2fulh6a4sacq2pk6k64mcysbr6o\n" +
                            "!woe6x6ix2mioux5hkb13cx2v52vsef4hjgenwuamusrhdu42ynzbnagtjsetm5okn\n" +
                            "dewhayvpmpxvw6vmrd3kbhoq45dfzyln4smmimlo6r2unyqaq c d6twufu262uppo\n" +
                            "aqnoxbfbzxm3b4n5s3yauukhq1aujjczeic6nkgcvn4pgdfjz14bgphtoyzr1a!!\n" +
                            "2ukmqcngsibo22wuyii6tljimecczwim4fnyey3rzelvpeayaawqoobgopgrv5cd\n" +
                            "zdodadcojslw2ouv2tajj2ar1rrpzmy6hxp42ev2apivmmu63zii4ikpkkhnxcv6\n" +
                            "foqpocq2e46xdz6vn2axyasjusmyksmysshjkjrpn32snit4ziufihnvxqz1fxem\n" +
                            "hbg2o3v3w4ixvv3nxbwtfsme1g54ljsjsrtkeu2ghvpvit3qtwnwsgllrcoshmi3\n" +
                            "gl56ufuimp51ljq5htssxypcunaqfrd3m16oasmg6gozesssrugqvsun1xiqkdmy\n" +
                            "rnpxz4cku11mpfcvuvwxnir65zdnrjioho1h4vmyontv4joc2xk2exogqaoqnkzo\n" +
                            "jqweaglnacukdevtmqef1cxtuvpardeesw3dyhf2jaqqd2df1kjirbcg2yyx1gp3\n" +
                            "zszw3aqhig5na2zg1a3ofzp6dyvz4gem4u2crwhgworj5iambocsrdiyxnf65pcf\n" +
                            "2jz6tesigk6udolv 3 qxj1w3aed3gxm1cnxkwtadir3wd6mrbfjqv5rmvdsgi2kkq\n" +
                            "5qjs33oh2etzfqvqp6fe1wbvtsxp2nonqclwdrapk531uahjjv6t5wvrih1ux5m6\n" +
                            "g1me5mhhcrf6jv2nxx6b1le55chyznaaxwtetqiw5q4dbz1l4zx3bkfsgdsba6mg\n" +
                            "mxrrtjbtj6f3por5uxxv2ocm5kp2ighs1r53hedlts4jadiuhuhnzdkgpxnw42pz\n" +
                            "azaexy22aqykpgtd1n34v1lifjcfax43xdhstbrjyumbugexgwrdmt2qoxtmhu2r\n" +
                            "1foefj56hqdru5lg13cb4zjw2zkabdm4giavjl6dvpbm4ecboyrdke6ikszwwd4s\n" +
                            "b2cc2fhd3o2xxv6kuwxir2mxztemavotohumobudc42v2dbfucvlyvxzb3a5zh6k\n" +
                            "4joba2jn2ttcfilqlzxkwpreqghnjbk461ckpubw2xo3cbsyild3yahauznribh5\n" +
                            "au3z1yr4kdohqha55iqfubqxcirt5tq1td22lk155vistmja5z2cu4jxyr3bnbgt\n" +
                            "yfho26tye4mg6x5tutez6z4rdgbi2rcrg4owhqupzmqzkeeyu2fcigc2ky1jryb2\n" +
                            "sqe1tqid1skqlqa425mjhp5qtut6zyd4fip4jzmswyx5tuglcbpfjr53pn4qzzo4\n" +
                            "w1ij4mzm5y12rvqzdc5kd2qkycknqpbjtpjbrvrvnvcne6jw36p3kvvjmugh62zv\n" +
                            "vzqrl33cfthbkvjd5lwaxybay5yuoknqmftnttzfjmksbizurgcdzb65u1o61bg\n" +
                            "5xhbr1vu5r4elsvs2ksds3wolhp22khszoowksaeh1qobhcvcz36lvpgu3g5ats4\n" +
                            "tizyplk36pijoo1hdgxlxo23jixs2jual1t5c4ipf5nkqynjacawnlfn3kgi4i2\n" +
                            "   syqpwlpq4awew2qndnmivs5olzce126nggsyviu2ekrp3diqww1psubc2u6s2\n" +
                            "rkevyg5ibh14pubc5okqond14fw6nmjwvqi!yc1g15ksgpfxejd2f2o3w3h23bnph\n" +
                            " 4eolcoo4ckhqfy1upxbknhjhhwsoszqwin6ys5oi6ck463vf6syfmddg3iuecxz5\n" +
                            "bonvi5xr3pt6vuyudqxnu6okobz3ppfby1lzzruwwngs5vzelbjhomgv14kucdue\n" +
                            "xtlgl4soxpia2vte3znmi2pdphlc32e2zn4t6a3wkblvf4h46ej6c46v5bbq1twt\n" +
                            "4snp2b6jino22hgbchwx2pgxvkb56dahmunzqeg3mbyav6o2yvhex1cdt46bx1n\n" +
                            "jnjhse1jywdkzczibwpo3bdkzigqsxijb1k3bvt3"),

            // 5
            new Scenario("t52 no 1 p 4",
                    new Key("T52AB", SEPT_23_1942_QEK + ":34:25:29:17:16", SEPT_23_1942_WHEELS),
                    0,
                    "po2wmmshpmawu56gstxblqkrrgrup1fol!\n" +
                            "6zzrwbj62akkblev3bs3hrg3tzevpnpl6amdeexqxrsf6upbqale6mi13iu5gn5!\n" +
                            "5thbeumt5fu22n3642ylfye1exq6vosjo3jozhotvhj1i2lkc4t6rhoflw11tu\n" +
                            "!!itxs4vtw4txzu1mfwyamf2vwszakaameipalk3ydpiza1tm4w!eprqgqtqmzx5\n" +
                            "!!!fxfub6ofpfaxfle4sgcxem3gbf3hxaoaszhznhgcyqtyoimuthm3gvzbkng6t\n" +
                            "!!4a3eal61utrkwdn1m1epw6gphxe2k3yiwhfy61dld6zir4h3ucqu4gzasij64a\n" +
                            "!!rgun1ydwfgjz5aepcvh6yaupxqwbqi3sze1yxvlyjnn44nerir2c3idhjtwemk\n" +
                            "!!t6yhqtauggidoro4akriej3qibvtkewwdo1x6dlbujzff6fvbbpqxpusjjbudg\n" +
                            "!!lajof6tsfomrtt6jbi6iutrw1i3oibmwjjpgjngdbgfs65xiaula3xiderwsd2\n" +
                            "!!b6bbvxmsdz2zqxibo5ykbh5u3nox3vhvzgwikuw6mlsjwez5akcmot2x3s3m!q\n" +
                            "!!3ko1abjx1wvkjzdelwtqnevjuxjjmxhcfni6anndzmkbp3b26jkm4rhcaf5bso\n" +
                            "!!puaeuxk46hu4ea3ubyar6xili5jvodf33lo4gmqeh5lted3tdtwggeq2jeyda\n" +
                            "!!!kwjxhd3xu42oxa4kdyybnlzr4woerfrcfhz2e25u1yoezhy52dh4ac2t53h1g\n" +
                            "!!!bri3enied5usvwoi3dkqkgmjcvlae1gp6w5yinsy4n3pmacum662bp!c4j\n"),

            // 6
            new Scenario("t52 no 1 p 5 (continues to no 2, depth with no 1 p4",
                    new Key("T52AB", SEPT_23_1942_QEK + ":34:25:29:17:16", SEPT_23_1942_WHEELS),
                    0,
                    "porwmmshemawq5yy4sxqyekpwgrljwgkd4!2t5mve\n" +
                            "t6ubl14zvbst3m3xxegedutrgllth1v656lyo6upbqamd31bhmdcw2ll45c3gffq\n" +
                            "qhd5ssyz366czfvjqxwm3vpwveqh6ivu3l63ogm5koeafw5tyydfvtgolyje6zof\n" +
                            "wjoqsfoel31mtot2wcwinh2kgxnmjqiyrye6c4yzfhzby43hdd1bz1p3nuquqqry\n" +
                            "pynxhvxmeh3u5mw!3nz1m3acyrvauijtpbfsazmlctpqa36l16cwa3p4oa6ll64w\n" +
                            "62tb4p6ovdziuep6hptlloanzve6fhj6z2cfnvwqh3xqsanv3a5hz6wdnpaunpbq\n" +
                            "wvxcyqb5odrcw1s31uqiqqzke1zbc2t12e532j1gona2qtubs2fpd2nirqwyk4uf\n" +
                            "pv6p4jsbgi4xitbrznrkspdu4yh41neaxjaxnhtekhqtomd535owucgysk1uei1u\n" +
                            "34zk5vhrxia6mtrtebpu4og51bsyxc13vm23iqnes3sffwlhq3b4iizb35ens1g1\n" +
                            "crdrlms5yiiy5rybbackb5q4xtbfxwtk2jtxooj1ra5rzrgh1yejdq531kvnpipp\n" +
                            "u6aa1x123p61vvjuk3j23pz43znnnz3btyco2oco6gia5pi45ubwml1jamr4ruu\n" +
                            "!fu32acadgdyd2v1iws6eqp1s2ytuag1trhwqq5qs4ugxcar1tx42iyfosdlhsmk3\n" +
                            "uuv1gioio3ju6e36gd6cjwa4zhap2vlsibrbzyom6xhvzensj5rw2qqw4trl5r4\n" +
                            "svqi4z1w1gwfg6iejjnwday1x12t4tswccymuzugxvygtt4vqrre1ubjbd3le5n5\n" +
                            "du2vzhe6k1zvwzej4nupe6l6b4sffwkjqcndnjaoiqj5r2b5md4sz3ejcp5qzf5d\n" +
                            "ncoznuo2scjlui1rsl6yfqtguwedfrsabgcrvgvk4fctqvy125otdrgbpj3vmowo\n" +
                            "vpwyhykcl4i1nhj32tyxb3shp2dzg6ij4wfdmu21vhaui2e4bzb56hxbmprfvqj\n" +
                            "iten6frm124nf4ktbohvtbxoro5upn1qrmzohymamfvnd1zeimv4quth1kthc26d\n" +
                            "np26hs5jwslig13ulm6jsbol6afjdiuquptn42bcjbg253o4ps1uiw1sguho21d5\n" +
                            "jctypwtiizda4eqp6wqmhjmlykloxar3xpp41i6xmqwrz6hxn4bvzfqi46utda6v\n" +
                            "bwk6ng6vmm6ec3nb1umn63gguauuhvk65ijjz5lqlrp1qzch3cimezhkxucj6gne\n" +
                            "w3dhsedp46egekg6td5qazgdskmqxsdvzzxn1vkezimmm5f6snm3hfegll14bcw\n" +
                            "jwslktgb!gb3o6lybyafy2x2ukg3mrt231ltagpiuftkpm4um2tcbzc1x21r1h1u" +
                            "" +
                            "ZNZ65CX3ZCHJLF6YYAWSNHU34Z3SFVSY1XVQQYG6VG4TWUTJMAQD5RK4RE\n" +
                            "PIRSIQUV6XJABOI1SFMTUZP6CTWMBAGSHWEYMDYTZCFN6QJSHCW323TCP115SFZT\n" +
                            "K35AKOL1MCJP6DMQEPOWSWNDHR5ZPQ2U1IGTHIAHZGUATLF224HMZTUTKZNNJZIU\n" +
                            "AHZH5ZVUER1ARP6EU6OP6VMSLYSL14SADHMULEEYAZSSORU4TOUKFYYSIVIX6MEJ\n" +
                            "HT3ZWGHKKGKCF3OMLCT4HIMWCFNKSHBJUXKHVKWLDHST6K3SOV4XLSCAVISSFJYH\n" +
                            "I5DJN4N2YRqIYO1MO5HCF3ETH5GOVQXFMP5ITNAR12GE5XKSYD4GS3TCSRVUOA3D\n" +
                            "HSXNNIFXZ1RFEQESBPPKQ6UBTOB4M6OABOJpS23OIIHLHB5I6HZ26EG4XSSUCLE6\n" +
                            "6CKA5YXGNLMEWR3HCAA35OBJ3EIMNZSCFE5MVJCTSRJJWY2BZPF6MC4AWP65RH32\n" +
                            "GJ5ZSNF46NZLTMMOWSM56P4T366UF4YYIVFNIXZ2LLXM4FIECQSEIAAGFNLMqMEn\n" +
                            "PEBKE4HO6X6WdWC15M6VZU26LA43BO5HM3WMZVDSOGLBTJYEPDL5Y3TACNSYV2EZ\n" +
                            "CIVWSK43IMW2O6EEIHJB54FYNTLA2LGMASP2TF4VOJGV3B2n5AFXGZUJSGLQXVO3\n" +
                            "RM2VBCK6UM5ULYS6OKpWSNXBpHLKQKDXWM5Y1UK3VQYBBGFFOXFNDO3J2EpBUIIw\n" +
                            "A666N1MEZYYJM6KWSDG4Q1N1YpOELWNKGTWZ6G6JPHK24NU3BMCAXIINUqBK5T1\n" +
                            "XQNJNZ5WRW3CBAGM3MA2XWE6R1L4XVYVHLJYVXTMH4OXTNPZTQX3TLWU1I2QOZ5GN\n" +
                            "6eciqqf6jlfvojrd4hmuk6oubcizhgm1zhtrbon1yqsiozfojlrkmixhzsqzcric\n" +
                            "6yt3gghr1cd5ekz4dvnerkhhutwgwnoq5kpinftixzskynr3l6ygsiun4xmik4p\n" +
                            "yiury31jlt6ngkriauxoaeo5dncz2lfpyygqwozcxbznkvtfdhxdr6skq44ndwe6\n" +
                            "gttbpcygjwn1s14q2cnml5yaama5taz2pu1mi1skrte1qimbmivizz6vgdrwpqs4\n" +
                            "execbn6bwewrmmknzmebj2dt2umyulajjrcjsyvpeadhujvxlwufjfbttx5qhaql\n" +
                            "xpi4wvtrss65m1nn1wm1x1p2sytiebtvc53njguzxfjaik3obukaac1b5uof3nca\n" +
                            "nfv1stmgl3z3hzxyjdm35465gvrouiuaav6cazllrayq5bych3hskzx3vsbcdd3i\n" +
                            "bxsem66ebnkg6jnruatr6smgqpcm4gc4gh6tpn3rgytxayswgdsf1cjbq4nelvd\n" +
                            "m4nmcpuwi4xwwnt45kck1th4a5xhy5wxjls5n4eavfeoiqrouczokpwv2y1354sa\n" +
                            "rrymzjhu36m1odo1yofwl2or4vanu6oxmliguzs6yc!4ll5l3vwjkv2yrbr1ioutw\n" +
                            "xc31qcfps5rxpyghbvihwa6hguq2zwha56uzvnrlroxomqmcitdhczod5q3a6ba\n" +
                            "nvh12sgsq2xcbru6mo3bqokxmee3tvxgcyzohwx1mo4c6vvble2al6u3jda4re3e\n" +
                            "\n" +
                            "2p5ggleprhtjgpgvtaugzk1pj1n656u44to1ou135u5cy2e6qpewsag\n" +
                            "dijzpfu43emon64czu6hzxq2by2rpuht21ixlbqxpd64ahf51m1tuqt5bbj3pvbsa\n" +
                            "ht14zdlpc4ss6cfzzlh6hh2mkmme2agxjomgc1e3fsx54gkwbkbawoffiiv46b!e\n" +
                            "bydotmm2o2ono1mllgmveby1idgof25wq561md4j2egujxtptmk4h66lydbihucb\n" +
                            "zslucxb1zdgjzizqokohegwb2platqys6ksw36ehush6elpa3maj2fi4elozhele\n" +
                            "d1chsxvd4uq2owvhen5bmhzzvpidl3xm1o6mrwhb5joobbkynbazo1qykazfommm\n" +
                            "dgnw!3azzvsslfqhlcvovk116q4rtryvm351lqkkpiskssqi33usvuhbnhown53w\n" +
                            "ncmtwbnsvtpvoyv1hrom4zjq4syuemwhigmblm4dgu!gs5konzh5eunr1s6iccsx\n" +
                            "usxllvw6kbjtir6w5i46szcvywhz6teaaf2gc4jqbsozgf4u5idwcq1ckptx4ezw\n" +
                            "zhlrvyhzszcsj5omezfdp2z5gak35mowxd3pvbdg2lfkxqhv6vqc5l5xa5bheuc5\n" +
                            "2rdw3vje4mbkb36dfhvu365xlkhobergtnozeryvu5bpbacrweuaiakyowxyzve\n" +
                            "mmkjeqw2dobp2pebqtks6ejq24yfvhjeboosyvxz3iqhbzgog4lo4vro2kdtpwzi\n" +
                            "t6u23faf6pkhaieqlazwo5snltf4p3nvf4u425pqi5adyevaqfyaek6pqb5t54ya\n" +
                            "!3f6xznsoatbictbu2ozux6ebbqn2tzgu4iwz6etkamc2exh52veg2wqmokuu4an\n" +
                            "42q6q3oasc6ut3v2y6kpr2zqle4p2qbaehmwmpzgfhckg6ho6kr6dtqyisv5hb6y\n" +
                            "zq2ptj2lnwgfylbonc5nb2seqf4amwcrkr6ja4njsd6k1msq66ubp23bku6wtcb\n" +
                            " 5wybtetynm6xzys66vdcgsixwsohblycinnut4n6frttwl2kqonquwcvtxedztn3\n" +
                            "vq2gotkb5mes632xfkqiezuo6rl6bjsvftvwxefwsc4imcj6s6xz1ncwijesklr\n" +
                            "66wnth3abqfqznxhtvazxosgbbjssbc5qpaogc44tooxtzlpv26rgosy6ibjc2f2\n" +
                            "teaefvv5nuza6yd2wflww2d4ckl3mzcmlfecowa6sc4ifdlb4wqc5ziqbkhwch5y\n" +
                            "lqo!te1eepqumq5vvdc1hm4hubte1evyk31wnifhw3muzxyiaiybgtoiftjbodol\n" +
                            "kr5a4kqlyfuodeczbcnpoqynrofksnxy26sfi4jobcfeyewvo3jrtdj3znatsyh\n" +
                            "ktrzqhfexqqafsnecedfzovhm5nb5acoicrrzada4s1sklw5n4tm6tml2c5dny6u\n" +
                            "extmrgbzwqq6uaerxwohagxsmuzwctm2okuydvcp11eg365mqienrk3tbgsbcml\n" +
                            "\n" +
                            "p4o5daqxbq4rys3ef4t4p4i6g2jbjrrvtkajlc5mcgc4yola2baywnybwex\n" +
                            "3SUR4R1XV4ZpJJYSF5VSJLTVK3QZ5ZJ3H5534ICZHVVPI6DIHKX5TQ4RJ4CEHXST\n" +
                            "NMM2SG6ZW1ZHLO1AV6SHCLI4BVF4MSCLMKPDTV321THFhYGCNJ5BA2IUTO2WRSFU\n" +
                            "WE4DONCX1EFX25p4KUNQqSEOBq2A4GVGB1BRSBUZYBN6Y54UN2C1FEJQE2SI4pb!\n" +
                            "146vCC31LGPO11KTDBN3ZS25AEGXP4VPNGOWY3MWKLDp5SKKP6EW6HUXCWBAZ6Ad\n" +
                            "5EG!LJP4X2ZUNMZG3NKHCNOPENVDUCTQA2KJQCZ1G2pJS5ZWZNKCUpFYIM6UZEF\n" +
                            "iJLNBYAAMCHQWBRPMQ5UEVEVK26QYJKHVMFPqRCTTHHOWVZUQ5KNOZDEWIUL2LUKB\n" +
                            "S5OMWESYRXW3CDRKPJQY4LRHHU5JCIACEW3GASMTGIUGMRMJpZOAY4PBRNA2GTG\n" +
                            "uG5JWMSpTR3WEK4SN1K3oYN54Z4ZSHZY5HJHSDDJ5B2XQWFXX4CV5FVGLVUHKQ31v\n" +
                            "xmd5yw1radfbfvdg1currmxnzh5cp4plot5nvr1l23ao12k4tztz2nntyoidpso\n" +
                            "hXpIKBXBVXGXHS5FQGLYFOpFVYGSO34XTATGMXR2RNNERIRFR2DTMpJUFM46BZO3\n" +
                            "UJIABEUPZAYZ4M3L!4CAZMEFYD1URTGjX2Z55TDKTGWTZQQLOEJSANZ5ARKWH6KJ\n" +
                            "z1vzieqm2is31n55axyd2xxdxma5fn55lacylz41nrlp33c53aa25wbbyqtxma6m\n" +
                            "cvmj1xzrintlc2i3st1g41ku14nslkdgikl2u2nu5a6gwczsoq5cbceuk4com3m2\n" +
                            "ncywcyf4yzlvblmzl3u32a6hmwek6tejvvqjkfrwhab5rfcw1mnvjghfcidp6si\n" +
                            "rxwhrt422wozxxm24stlkbgq2rcxfyvrp63eomsyqgueh43z5mspdtxrn5clgrtn\n" +
                            "vq4foghjicmtddv2gkyhkefwzfwzdz2i1lspsczfenc5jnyrb6zfz13ayog12n52\n" +
                            "KI3XQJQV5AXKW6HRMMBE5BJBDWAPQ5UU1V3Y1JUKQTLVpCJJSTZSYEO2HUVYKQZa\n" +
                            "5ZUTLT3YJHDKUESMMUOB1W6QBUD3MGNJCCH2PXHCIR2VKW2AG33ETSPFOTF3NGC\n" +
                            "NNJFQ4OGCDO6ENSKQSWJ!YI2NMVDIE2WC3T3EB4VYCSG2p55U\n" +
                            "23GHVQUE5JFIG4qUJ4ZPIOHEW411MIFp4F3GSPVRNH1P1MCOK2ECTXDJ5ZH11JV\n" +
                            "3ZQGVNP15TQ55HKMvK5O636X4TRXPSETPH3CICS45L5OYQ25Z2UHE4XVD1REpKDQ\n" +
                            "SIGSTMPYLG2OWD633ZF31CAYCPIZ2Q5FVALZFSPIFCQWLpP1GAS42QXGH5ZINI2D\n" +
                            "BSB4BZMHTPEXDCRW2QFR6DHIUYTVHF4YQNoOJME4M5NTRqLDNZ5FTE62W5Q2YON\n" +
                            "NUZWJAUEPFGAMRU5VRAANXT1TC22RAAPRQX61YVNVT6RD3YIGpTDHB43I2VPAZpu\n" +
                            "\n" +
                            "6dd2lkead6vlj4efdfoshvwhkda5!xj1nyb11dgrjav5meno6fc\n" +
                            "ON3HODUQGpO2EUTJ3XEDGYY4IWR6GU2W4J1OZVYMWXVDOV24VA55RBKVND3QVWVP\n" +
                            "fmao56pzywnrnmywvozaddtvaz3hnqaryo42yv3pq1oiqxstpel256ponuwfrek4\n" +
                            "26pxqoifijyirmm5t3lhg3fgey4lzm5rn2etrzom3sovumpopbp5111abnr4dsp6\n" +
                            "c1eldvjvqr6sfykkjjt5bl5mvht5rn3vxze2hlp2ngj3li63bdrcmujcq2msp31v\n" +
                            "cjio15xa1h6g2vv1mx6aoq3ucvjosmxtr5l2l1rf5ahzxht6bwkj4ck6lmaar4cy\n" +
                            "62UNGS5URDUXSCZSFVULREJ3TY5UK2YYOJZAL36P6O6LYA3BUSMGIJUW22W3HL6X\n" +
                            "fah2euw6bi5z26fahtf6qghd4gqn2t4k5pct!mwmewex6vsqmhwy3e2wsjmvv3pc\n" +
                            "gb2jvlcauwtgoxoycci2lcos12jw4wap5lg4xhqmp5lhaxrcwnvnecrdiieg5ok6\n" +
                            "VWEEGY3BEU!353HAY4OEHMYYLCp2EF6J2JLT6OA6OOJILBPHBVFCXEAGA6YAB3ET\n" +
                            "EJqTE3UEX236N2LCEEAL50E1KNJAS34JURZQSQCJEZ!665TBUpTFpZRXGMqHLSA2\n" +
                            "URWpKYEXTOB4E266XPNCW2R5X5PBVqWDG2I5TON!XV2C5CJC65WW6XIWC4AOEFP\n" +
                            "VL2E4W!2OE1EWH25B3YPD3C32BOXSEIIWEU5LV3tWWGUq4EZCRB3AQROFF2M6S5H\n" +
                            "GBWS1WHCEVLLS5IDpM3ZWRZDWSOpBCGVJK5PWLXVDCQC1V1p4WQSREZAQ526PXIp\n" +
                            "a2rs4rnah2ay625adgt3muauqd4ve24s6zmke3xx1uc2ig3qolrq6ma6v3ihi2o\n" +
                            "bDLGS6UJRLCICT5SH5HUXFVURABJHCOAUA6cQ5SE4YZUCORZQEREEUV34J3SA2WJ\n" +
                            "fknb644hyb6tn4clnst2ytfew6hpyhlbuxaybnpxxd3uyk5zscmt51lv4dci4qj6\n" +
                            "2ppcazc3va613vg6d"),
            // 7
            new Scenario("t52 no 3 p 1 + 2",
                    new Key("T52AB", "62:31:17:37:21:59:06:01:19:25", SEPT_23_1942_WHEELS),
                    0,
                    "1clpsfnlwvxdli21aw25og1g6ybmubtkttut3r6jscsb5t3dak4n1hqrk\n" +
                            "1q5qon1gavaagsjfnkcg22xckhoavgl25qp5coksrajsobpq2zndapjlae1up1chy\n" +
                            "zx6zigatno4tgvdtbzesapzvrivcorso5em4vuiv2irfmdcvi1cbf2wc54w5anzo\n" +
                            "q6shjsfpfdgfgm6qvzspjjcpu5zz6ri5zj41yple5leee6pmwruwvfhopprybdiz\n" +
                            "162p2dfkyfk2gslpyxdthoxio5atjrlgdgz144caoxumqhpkcjjlh6jjuxs2sylo\n" +
                            "molduuo1npcuarv5o2c4qf1tyd6gnliltx1zh4jnk1uwgbf62m1h3q5izfcbyv1b\n" +
                            "dgi2ijnzs1q5tec434dnbwiazutklh6wbuc253yfusp4ndabkchomt2lyijfjego\n" +
                            "rfgst3s52gliy1bmz2afaalhfwzpi1spqo1m344dwv655dx2rz1n5w3nwnhytwdw\n" +
                            "egxvae1uyyccan22jdaoa4idsicubhr2vthvrwhk4grddo2ly54zr3zpakxxoudb\n" +
                            "21jzubjqyhd1qmy15w12qpvqswpjefkx2i3tunaydnabdchns3rnoa16levycbxa\n" +
                            "i6n5w4wzbcvtcua4qzahewj4z5omfh4e6tkty5ybru2mtrwze1t5fa1dc3yoyviz\n" +
                            "jtltjxnpt2du4nkqe1jdzeqsngmkluq23y6nglcw1lzd5qgeifqglcpjmmeae5mu\n" +
                            "z6pukeznjzbqc1j4t26c3eny1ttzo3j1gbpcdxin5x3ujspe3lney1hnq1nbnvz\n" +
                            "25zfjcdjxyuvmmtmwbx4mhnojyxc6rfqbk4nczpukuci1t3rlf2ut3j4vi2j4cew\n" +
                            "jwhzyupa5szvqenroxwexhqt2fsknkjoy6depyfhjid66a5tqcvw5tltezmm6tie\n" +
                            "n2rrlfrfajk2qag3tj1onujdkjjfkapuut2adlniku3igbsptbstrqfaizzfq6xf\n" +
                            "34ptpk1ea2grkmr4pxbma6h53ffar136lfs23l64h41figp2z3p6unxwrmjz5eef\n" +
                            "c4odpuckr6etim2trfu1zobt4raqrc5jrdyqy2pza6wdwwfo2ul663jco2vq53n\n" +
                            "rrq32ixsvupr1iywcgka52kqcth2vxi5dzwq15hxsxrugmsykxhbpq1bznfepcrb\n" +
                            "lacep5pl32a51bmk1aet6u1kpv6egnutcume5mh6pnohsh4j1ow44uqo6ok346qi\n" +
                            "4qiuejn6oo42rjvq35weqtj4kaevmlx42u1qodo6ji4larbvsdwrqosrqq4lif24\n" +
                            "5iea6cg1ku1hhnffqzsmifmgyhutsgnzvztcopcdlo4zp4bzcz55tokzvvxwjqw\n" +
                            "cty3sl6obxyf53jkyim5fsdgzn1ihjad55yzqvvagsky2kinglhclpjzcteqdtne\n" +
                            "gshm1ikart5tqylo4oly46awgwvm6h1luiuwuiqcy2nmxmgepbmh5af5vdksrue4\n" +
                            "qzb1tnvrdujoddmoyt11zodkwbzjfwstz4rvkbjodwr62pxmm3ajlvhyrggcjash\n" +
                            "2q2c6q5tjgwhran2355vuncunfohoxbmsh3z26lriryxrvywxepc2k5fbks1eox\n" +
                            "44lo31wk6z11rhsfnn4i4qb6gper1d3yegmwi1qvpz36hffg1soestmdjk5rzx2j\n" +
                            "xbxjv12b4cxyx1pdstan5xxxa2zqucz11jhzfksym211c6ujjkosjh3azardyesc\n" +
                            "ynmgquh3otekfko5xevvtj4zqbn4pph3zp5us451ecwumppzonaantblf2v1px16\n" +
                            "up4bwr5zodq4ewcm1o4ce4g4hdppj6vokentnfdjlbgzvgcuxakseh4hd56qz6bd\n" +
                            "22zck4bz6x44gaypxchtd2gw43g1cvx1rtyr3pjrrcwxhufgeinoabywzfj2pxq\n" +
                            "ixtpmtaodej6mlcbqvvafvh4iu6j3dp6bcravsjsnih5e4ttnbzsrnbirtpjgfiu\n" +
                            "blqwyfoly422qurdjem4qmtyrigrcxcuxtgq5rgxdlvol6vy5ercm4q6a3zujp3w\n" +
                            "2tgjs4pme6emazl1wqd1yeuugkygwww4k1tr3i4je1j3p4epwz5d3pnijrubcja3\n" +
                            "ezfiyfccrsv16jb6jvcoldbepfrydrbbwmvxbe42t2wkow1vrfxlzt5mz45uqpuf\n" +
                            "1inqbgkdz5amvuxpyomzrfmuge2sigmikzxx2ohgrjhkw13fd1gqxwftpan42ctc\n" +
                            "rclsh166mqpssqcbxolxarjicxepfou5235dajzrdi4s3nomw2h4ndz6fte5zkh\n" +
                            "t3efhhhdajwl4wotedvdhav6gf5xzfweueirsued2dwdsfemhp5ye5el2u6g6o6s\n" +
                            "bwye6s25dpqklltpxak3f2utoxcb4fnw3t1ca1tgfuz5bnwkpjskbuqgoq5erfed\n" +
                            "lf6gz5hfzxpqbcxbzrxxlrkvhfextudbz2z6ermzc6fkzo15x6ahmff1qtvh6upw\n" +
                            "d5ft5uxbyvofhnk4v2gawtylkusp6bb5u6pomca4qdtk21q4lx2xjfmfkioy1avk\n" +
                            "ryfxtlvbzjd2evls5xubek5wtown2deaq5h6vvmkvzfxnupkoni6for2ebn65jg\n" +
                            "mq5itxuhcy6jv4ysd1dfd1hrzxszzwhpsh3tt2dxr3vlfupn2f3xtjbgteehlsf3\n" +
                            "jzr2kdpjtlwz6gamdvbqjfyzfexrdwhzpeddanzr3amtg64if1nv4ppcc3oulaw\n" +
                            "lw1h315gcqsg4wq1uda4e6harqg6nycsvmfhqk6mtnxzrtk3bzfgoxnj2vs22fhe\n" +
                            "ehon6g4smk6cj365mpzts6usel2oklklegfsc15tjplrmyue3j2hpmnn5bcgkzfd\n" +
                            "1l4iahvi1zv1zv1f5agpbpwo6pck2apxuuk6ex5cz3x2ga55z3ltbm515bh53hls\n" +
                            "4v1rvkndjymqt6wbxuwenpk5rqyw1ejklqgfltxybzohaemu6xx22urhegxq5y3\n" +
                            "ymsuyjxo2aqckliwx5oclyjodbrwojoq1pzqarnvdwupttt5gjnmttc4wzu4utv2\n" +
                            "gj6uivcpw1m6pgckzr143dhrptculmtlkauvkakeotgqushbdj5av4gttilzncml\n" +
                            "j6f34e3odqfwep3pxkuur32rnonbkeg5tkj5j1drbsemnomlm4c3e35m1yyq2xb\n" +
                            "n6oowegzaftrc54hoayur2qtbnsklkmr4qtzzeis5tr45uh4hvorx2zlbfo4tch\n" +
                            "seksyafeig1da6qmr4vjhduy5javz3gn6vjfx6sbpim3gw3dyuo36giqsw4h5z5g\n" +
                            "rtu1x3mfqowtvs4gfhpu5jbncyxfafa5d5zmpmc31q1u6gnn6ol5f61fojb\n" +
                            "viwqgtpyvadlftjjsvtc4ib3iekphsz44g1jnf5j2hh5kesneblx3lh4pse56cf\n" +
                            "ker5hqmc13zvfaqdw5i223p46unupv55py1656ax3ver6u4qrvst55wzv6nxisfj\n" +
                            "hde1vvffvyg2vxqt6pl6vggjz22syqtsi3y1dvrahbxqhejiyhookzf1jkiyn4u2\n" +
                            "rcpgaomi3wbrqzroa42opjxthi6s4ubbeqf4qxsihsnp3k2m2fkoui6e2ajzhcv5\n" +
                            "kfvdqpy21zhwdukvwsryn4ys3bjrlekv21x5omvsnrxzj3ohanljtmaj3cst1cgm\n" +
                            "yqebf5akuvjro3o1ftlcu5kpn1ygzna4kyknrspdxohuihejiezlpab3g3r23bfo\n" +
                            "lvlb4t2v12n5vap21vkiek61ilqxjmbbql5vkcrdff2legycjvaoq2hbxfd2oef\n" +
                            "wzm2kzqedznzsboasttg11mm4y5g1kmhqawwf42btlteounykr2n4ucca1htvkhq\n" +
                            "zxkyuz36ungjff12gzh4eful5as4n1xejmfeilxjka65rl3d3hrmynm2hohepqp\n" +
                            "1z6bp2iriyv66dm34cnsxclweohyx4l5h4guxjq6cxhin6l4gc6jld2eorouceuf\n" +
                            "5qou4ssnn6vrlljwczkkrvtwk3e2vlg31h4ehz36j3bj2xmgw6g2cl6x1xkzlpx\n" +
                            "lf6lippphbarxxgwxzrwyqixlyt4qw22epshpgjtwshg5xi325ib13cdywlnsh4\n" +
                            "663j4e51ftqubdosuohtarc4mddod464t6dme5l61usxuborssclsix6ou1rh1s5\n" +
                            "cvyyxtoldwocelow3xjwax4cpj2cvlsg6xcne6a2jtz1khkljsezcrhzawebahmb\n" +
                            "gnsbgkijt3nirdfmy3ac5yg1ze4yh6a6vtsgi1v5nlse3hsitsjlh54finofwcbt\n" +
                            "e4uo5ut6ra3pcppifw2h1wbhbi4lixcmgrnq6f2knwxr3mnkc3v436fte4b6vawe\n" +
                            "bc63gwampjddejt6nqfxc3t6m1elyxzgkptuxjo2ipvwxihnslbnxe5hlwlqsks\n" +
                            "v2z4w4hmpfa65emijbettl4vk54pgqdv12ujcra2qvgtlzet5nnxsvebgshfvvei\n" +
                            "mbjivwyfyylzsjpu4awzjdurpeqkehc4ahkb134wvlkzbchqgiyukglbfws6sjg3\n" +
                            "5r6ah6lqxnm5pingxndparxv1o1wf6ny5jl5vt3zyth3phlzjayynrholy4ssddi\n" +
                            "mrcgqois16vhlhpiwikwjogx6k1oblvc1a1ywxn1edcouaydttyhyamtxrof1n22\n" +
                            "trji3iyw56ncupdmehstx4b4bu2wdgo51mhxwxrgw ! sfdz4m5kaoicro3rbxj6wi\n" +
                            "4lsszohx4wtci2jkpjtqzmjsfkirz4ohyoavl6mb5hm4vcfh43kev13aossydatd\n" +
                            "nytidgw1xmtcwfa4p6dm1jaoewjnexsa1rmhm6chi3lvfeso1mnkm3oya1jjdeq\n" +
                            "Emtjumphkdtahd521wzjdkfo6mtyfxpyclrdzhfgrxtxu3b5cxvpleri16izsx3f\n" +
                            "gksj5g64pdb4fiul15nzcyt4qggxe6ny2jeybei3au24ughdshrsqa2isocuhbpv\n"),
            // 8
            new Scenario("t52 no 4 p 1 + 2, depth with ciphertexts.pdf p3",
                    new Key("T52AB", SEPT_23_1942_QEK + ":34:25:19:17:15", SEPT_23_1942_WHEELS),
                    0,
                    "uo1tyxuievleyhyyg2xfjdtpmx4dvrf1zssrfxbqbcqqtltgtny1u35galii\n" +
                            "6ruhzybmtw1wrtuolt1zwqpnno35bd1ynzyil5jsj564idi2odwek3dwrzxm1vr6\n" +
                            "5zum64bdx3ch2mafbgfw4bpcu1ikbq3ewb5d6zx5za5ok4kw2phzwcqvvniitqgw\n" +
                            "5ypxvbjllxy3qusqjwmdpmguve214oohvtgkpbb5o5hj3buxsvbypmehbbjyk21x\n" +
                            "foawhkwhzd1ioyznzuml3amt5mc43ydua1xc35ybx4xq2p6s1sxus3hnklxgb6u\n" +
                            "r1lx2kwkdxfkzjcuawhw15eum4ysnitrn23es5cfglrrfcik1ctj2gbfhbggqrn4\n" +
                            "hkvf36zmukoh6cryx6nv4b1fw2jkcbozbpi4gt464aimzstw1hykvfn1ux2c1aow\n" +
                            "tx31m1uuqgixhzxem1lgbndfvfcxc2bqf1dungqirutoxzgm3kgsbs4dk3iqydce\n" +
                            "cxrmcfdtuh5posf4cufyraxdevwyyeeranvd6zibedhnkj6nwobec6fm6xlxhvhb\n" +
                            "c11fiyneb5y3udfifl6lwakfljeqnbhucciqcxzvrl1iwgngnt5weskqigwq43d\n" +
                            "yuvme31vb4pswwb4ptbye6y2hazdoaykow2ox2hfjyvw5ivme5tq5665hrr3kuln\n" +
                            "qg1ixik2oaz1sl5d1eu55tscqb2ctserl5cdtumvqe43nivs6jbj3btyt5riijr\n" +
                            "z4pemy22e333njl45odnecyp6jg4obedzx52fjf5aznivj6pvsei4h3ahqor64na\n" +
                            "m14tv3523z11wjrccyt2bymypjv5pn34oonsv4pod422ryejraqc4dfcw4icks46\n" +
                            "vz2e12ff4bayh5pzkzeam4mng3sjjrex1uuhc412hawozpposyig6s2tk6hkeuq\n" +
                            "kt2hebvfhnwdnznmgkr2flsfr2vjupi6g4nsrnd4hzbcrudyhigsfutkok5jh65c\n" +
                            "bkauqhrn4yetrjww4o4jn44d2p45zv52ifo66rs1k56vyymbhi1xe4so15bfcube\n" +
                            "rdwxlqavcgf6j5jkkbpxn5i1gm6yhp6r2tejxg3k3bgxqhiq5ose6uimha63ko3\n" +
                            "2el1fzofeq3kzd22qdnbqki4qoiwq26wds4rlfahzcvdtolbpvlray5xzwruetdz\n" +
                            "6glyun56fwdxo4xf646pwunec4x1i6ti23iqlbupdnxuxoms4bl1yrrdoaeqxn4\n" +
                            "jo26fzzwrt6k5lkaucmhgfu5ffytphrkuitu1wby52l4dndkzcpybg6f1h25yjjg\n" +
                            "a6oyjjiouos1g22uvabotcvma31svqqxtc35mhx616vm5tk6jbo2ytsrf6zuamq\n" +
                            "\n" +
                            "qruhjuozcrf2dbxuxekctu5q3rzdy61qsn5bidqg2w6oo526cv5chjyjewkcokus\n" +
                            "kacnzjwy3vibdg13fszijugvi5glph1ndhq4bnuh6y3crjonldo4v\n" +

                            "drraslnlrbnkqexp6e4val2ollwqjfnn5cxn1qrf2eezzsxwxjhk4345cf\n" +
                            "auubbjitbiws3nylikgp5frvw5kluaanshh4jlkppdzctga2hnuhr1cb5m26oqg6\n" +
                            "p1yj4k463onopfumaitdirtxpir61bqlzrxuj6itznpe6ugzuhxdcr2l1jfx6za4\n" +
                            "mouhdcpg1ux3xtpgnsvujmq3ewyprwwp23lkkipisrfh53565nuohfuxmafkt4bv\n" +
                            "5t3obj6maawkwecg6cuwdwtrtoq2fq1ocy6z2u5tsqmghjvs336bu2myscyd4u4\n" +
                            "f4io3pvssqkwe1z2yvwo4dfewcxucg4lzs5lgxnusy652nbtk3vyxosdtmfeocvr\n" +
                            "fpc5fqre5elxxcppi3lh4oy34jzoan3egljmfmlythvvjb4lo2c4!ka3vxpb135w\n" +
                            "sw4w55cfxtqfah1tzpo32ezf3hquughyswndsg1wdeq3hkorguvjalai3okitjac\n" +
                            "k6nkord3b453kkwpvo46rb3lwdar !!! r3yybkg3ffgxg6x3fd1uatelfebxyvfw3l3\n" +
                            "bvio6svayhy4cdy4i22kf3tvuy4pl2otvhahgk26jxqlx2dyl2v23j2t66551xe\n" +
                            "h1nlvl5qzrmr4hlkulm"),
            // 9
            new Scenario("t 52 no3 - p 4,5",
                    new Key("T52AB", "30:46:06:67:23:35:07:31:13:19", SEPT_23_1942_WHEELS),
                    0,
                    "lhyd352tgf1bz5ciib3q56rmqtk6joqo52vijkkdngwvsd3p54dpf4qlk2on\n" +
                            "wcfhnw2sc1aulwxg42lrzd3p4jho4axyfbwhfndmqt2jvljk2kmxqvqhnqz1evh6\n" +
                            "5qsvpeggkzw5ygquvod2pnjrocy55emukc1u4v6hndbuphw6whs1l3tukskxpqsg\n" +
                            "rq63zf6jijpig52i3zey5yoahkcef5defdx3nq3dmdyuvjss534q5tngokhx25ki\n" +
                            "yoy15x1trpuuzxnpruzuc4q2ugswjlmfq4usnkxhfjes6hm6a2iv2nqwpyanyrk3\n" +
                            "b3zepbt521fg6t56su6n4wqak261uvpfdnrctsimsysw3skiybhoazxc1q5sg2c\n" +
                            "6qbiu3v4gelxdmehxzp1qjj3z56frjbo2xuxaykcpin6hlbwdl14ckzrnfjaqzuq\n" +
                            "nuhcysh242svzabdujwucodon224m6eijkujncnti2jiwpokf4ta66h5bhiaagxra\n" +
                            "ol14g5aqe541s5yahfwcpzoq152t6!ww6awoi5b62yuaenri6cztouvswgieyk4\n" +
                            "6fcnvfku4yeiiytbuxzdlwdqeppo4nkzzx2sejjtqntdmnebbv1zwyhno66evp1i\n" +
                            "zap46shpnx2jnp2gasxtgazvcsbauk4twokmkgjohgoubh4pki1g3jxptmgvf1ed\n" +
                            "jwois5vplas5uyxso6gtigm2htqopnrirl5jyfcu5l45c2op6twgdkopdsl3f2f\n" +
                            "bo6duqiz3zrxdnasuzpq2oa4pyh1dib3x13r56iftwjjbbces13hngtesnqi4zmqk\n" +
                            "gnt6q6we63y1m6u5ihfwa6freq4ik24qh5qhkyedgw2wmhi2zrirt4z2bubkw33\n" +
                            "ms4rhjrt6pzgt6fxry6jilk5ysnaimt43mhjyuqx4x2duzqwexmky2f1ozokgfu\n" +
                            "dubrlzgs2syczglqg2cfflh14myrmiued6qd4w3d5b1sxb3xa5zuvwg1drhlhtlr\n" +
                            "4mmfotrry2u1qgkdf6epjqdi6d5xmlt5e2w6ka4c1k12ho54fxidldw6f1g14uoh\n" +
                            "ze1meusn51wfdsr2hvfr6ekqi4qgx2wmet23es2pzctivwoxhq345lsnzskdm4rz\n" +
                            "xupwvyegxips2t43ous45yw1leuajsdnszurysdhcpylgysyqubr3at3wmgtnd4\n" +
                            "w44yktr2fnjyk1cy4krpumiv2nh1n4i1ou5ywbv66xct3uc3izq6iyubmlkyis6q\n" +
                            "igwgdvc3146vioxbxx5flzw5mxcsxqady6yillv5gdu55jxno5ry3ycm4yv6bgq\n" +
                            "c4tkmox6lgim3h6qmfas3324unjfyf1bjqwg1tpp1dv5bzae6ktkoyhooywprkwbx\n" +
                            "znsp3tehjwe223q4wxmexzhnpfts3jiisab6cupce25ll1kmftj52xq53ypbcqo\n" +
                            "usktk4ykigzhq1qf62s1x2m5e4xkiixvwfukb3xuix2qjubggzqivntpvkonbt4v\n" +
                            "f3bm6xx34vghguv26hkdu2oiddrhqslqclqlsu43dtertt1uv14qfftx5mkgss6\n" +
                            "rj3gsqw2b1v513yxm5mft2zwrmmicl5n1mbtvjvsilogx1wsvdgxns1vsfoq6jeq\n" +
                            "ei1kshc3ta1hhbwt3cha236ohtj3jv23a4tbqeho63g5d61fhokmlvhz66vcfbpj\n" +
                            "aznke2fykooep54b553chahvcfh1m1ac3txasbrhb1go5xb1dfhwg6xolzkhy3fa\n" +
                            "njnl33lidgsmoz2yednnnlziyeyrjyvrq3vkdsh6a4l1ckf51arksftup6znjk6p\n" +
                            "3qlfbc4hgkn2cwuf1cbk3fbwjixnfrht2pxw4d6jznxbnfxt66zdcdvg3vfnrdln\n" +
                            "c5nr1b5o4lsngbepwpknsujy51egpagqpl5hcg3dyy1adooizk3rejeud35s32mv\n" +
                            "hvebfzscjhyirz2vdjfsmvquv4zotsw1ur3uxgrt1dz45xreftfa3hlvxrbhwq22\n" +
                            "fkq1qd6wzotnsgjlbwhceuaehmycf2hu4tneh5y2d2wgukt1lzufc3libxni2ka6\n" +
                            "wkxlzqqkndd6kadti5vaahd6mfape2ujhk2y2go2sffmt1qd1xfqriymbykrw5ws\n" +
                            "pb3e4htc5qapb1l34puhjsxu3uj56fyv2rg3mugcdivxi1pmcsxkvghpwarcrkpj\n" +
                            "2a1xjwprqxypsxrkuzo22smopelo4lniobwdfboeyiwuzlixv3qjn4auetp6guq\n" +
                            "!ikgwo5bkz3tol6eo2b4dpt2h2izqzrdyczvsvs3hahia1kkpih5mhm4no31jqpj\n" +
                            "t2loanqj1oat5iykqxsn1gki1zd2xttxgzizqqw3ggtt1o3ff3pyuyibikxgpeiw\n" +
                            "o3qsrkbsh4xwh3xjlvd5aiigmvptlxt4q5ut1pceuygmaoktd3xu4hqzujvhnyvp\n" +
                            "4ag62hncfspcnjmu3ubjw4gphumzpb2kx3p1txbagyugf2pxfbvln4tcgzkbpfgl\n" +
                            "cjydd5zgmjrsiw4cewqh6hbccfcivksb2ywjk6n6mil5udzduf3svrok5o3iivc\n" +
                            "4q1f51ozdifcjx3hqoj4m3ixuqnbwibn1fqfnp4wfkhptd2dyvq3mwzjlfu2r2s2\n" +
                            "aan6jimdo6kfcsz5vinfgyg"),

            // 10
            new Scenario("t 52 no4 - p 3 part 2",
                    new Key("T52AB", SEPT_23_1942_QEK + ":34:29:35:26:15", SEPT_23_1942_WHEELS),
                    0,
                    "ifp6gi5xspzcbtq56egmnjzai23khc2pshy1uyw34fjzkyux1n11p1u3arseoj1\n" +
                            "6szb6bybewyaltimyhk1p5tdhplmm2zjvq6gvt53rxomxahhilpz46nwpizlgqh1\n" +
                            "d3rvfqkvhy5qhnr5hr4yc6vd3u5cv2rxx3pzaslupkkcqsvrtqxvtuzotuuyipyc\n" +
                            "n1z4fdgsybfuikmlpuke1wactof6q4l4lui16af1wok4xyxhsssic3jgxpw5fjj\n" +
                            "k4uldhin5wwc2vp254farj1qaadx1iktl43py651j1tmanw3hpipj3ro54a3brkd\n" +
                            "vduaismw5y6n5abanb2zmefewnynx52th53nsigjywe3lpxgrgvwxitjm16131h6\n" +
                            "6xatsrz1vjzx1ofwdag1dbgm1zqtlu1xvyxkq6bh4jyhq1ojaoghk4nzx5p6bfb\n" +
                            "vqlsh3puzr15ouf5nozu2v3nug26srncqvfb6ljmw3m266"),

            // 11
            new Scenario("imcomplete boring...",
                    new Key("T52AB", "62:31:17:37:21:59:06:01:19:25", SEPT_23_1942_WHEELS),
                    0,
                    "1clpsfnlwvxdli21aw25og1g6ybmubtkttut3r6jscsb5t3dak4p1hqrk\n" +
                            "1q5qon1gavaagsjfnkcg22xckhoavgl25qp5coksrajsobpq2zndapjlae1up1ch\n" +
                            "yzx6zigatno4tgvdtbzesapzvrivcorso5em4vuiy2irfmdcvi1cbf2wc54w5anzo\n" +
                            "q6shjsfpfdgfgm6qvzspjjcpu5zz6ri5zj41yple5leee6pmwruwvfhopprybdiz\n" +
                            "l62p2dfkyfk2gslpyxdthoxio5atjrlgdgz144caoxumqhpkcjjlh6jjuxs2sylo\n" +
                            "mplduuo1npcuarv5o2c4qf1tyd6gnliltx1zh4jnk1uwgbf62m1h3q5izfcbyv1b\n" +
                            "dgi2ijnzs1q5tec434dnbwiazutklh6wbuc253yfusp4nqabkchomt21yijfjego\n" +
                            "rfgst3s52gliy1bmz2afaalhfwzpi1spqo1m344dwv655dx2rz1n5w3nwnhytwdw\n" +
                            "egxvae1uyyccan22jdaoa4udsioubbr2vthvrwhk4grddo21y54zr3zpakxxoudb\n" +
                            "21jzubjayhd1qmy15w12qpvqswpjefkx2i3tunaydnabdchns3rnoa16levycbxa\n" +
                            "i6n5w4wzbcvtcua4qzahewj4z5omfh4e6tkty5ybru2mtrwze1t5fa1dc3yoyviz\n" +
                            "jtltjxnpt2du4nkqe1jdzeqsngmk1uq23y6nglcw1lzd5qgeifqglcpjmmeae5mt"),

            // 12
            new Scenario("umum for ciphertexts.pdf p2",
                    new Key("T52AB", SEPT_23_1942_QEK + ":52:13:39:12:02", SEPT_23_1942_WHEELS),
                    0,
                    "fjzs43oneu2fkpzrfaht5b3swlsbxqkxqxjsafdtkmvcs!zz2k6hngbsrrl3ms4focqrmn2kh6wox6dspe211654b5y45v"),

            // 13
            new Scenario("umum for T52 no 1 p1",
                    new Key("T52AB", "46:68:29:23:10:24:30:53:52:41", SEPT_23_1942_WHEELS),
                    0,
                    "fwaukwrrwrphvoy4zxkjd6pur4fnsnox4opnhgrvrkxqufqn124s14hq"),

            // 14
            new Scenario("jf 7",
                    new Key("T52AB", "20:32:40:17:08:38:13:27:19:42", "1-9:I:IV:3-4:V:II:5-7:6-10:III:2-8"),
                    0,
                    "jtptvxbsyanydwtyczy3idd31yrlmto3crzc1ieobawigh4v25\n" +
                            "gp51yzztnkq1cyyyqtdhjhuobo1gybvtmyna6olnlinex3cyhv\n" +
                            "apkxwwbxrezkjstx4qkoyysl1alcnnjeuux43sxxvn5qmrstl2\n" +
                            "mudzltbqi1klwybuspoywxvtsvwdchychlp11q5ieuc1e25nmv\n" +
                            "bfx3bkaxuv2zrph6lrldct4l2dbadbeuyen4jvnksxbgod3ql4\n" +
                            "gd6tamlnbgfngx54iv2nqc2df63tl5fm2huylm2gz5xyteodpr\n" +
                            "gg5l1v1kpaa2j6ml3fdvprltd6lbpmcpaxcwunzt3q5vvsopcw\n" +
                            "dscniaw3mwkdqjvqqnehq5jmtqmrcvdla5g5aup555depaxg3w\n" +
                            "2lollcaruhxtnu4a4gjrnku6ylzwdwvazyyqjfbei5cgifov6a\n" +
                            "gahzpe2sz32uipgulfygupz3u5gdvgbtkqa1vyp2hbzfx3sqs2\n" +
                            "gm65sud3hcla6iis6oktzalebkzjnmrrtfm1asjap65tmoevrj\n" +
                            "xmhliaf5cjmi5ftlu3firbzd3u5yowtknx16ng5q4pbxxrxu5n\n" +
                            "jb5nc1zrxhiqbccon1g1ug3zoweqoyqjnzcwul6gwsxj4bdznl\n" +
                            "skq55qqkza4tqdys3rxkei5xw1usktomikg3mg2yiduuzjygk5\n" +
                            "ertv41ydrps6rettpkwayj3p3opxwldbq4iaxh116seh122m2b\n" +
                            "pdv352vmnyidh3fwesert5rsq2hfmu65nogvqzd4sizzbnfmbf\n" +
                            "bnv6rz6no2ks3q6mhbuzwqcoq34nvnf53ytlhs5ry2ep5pkpek\n" +
                            "bdlyfutk6ypy5xkwjenkeo4ccojwjhp4vxbtmfeprbjw4n2vza\n" +
                            "nsskwtr2nust1f51tr3uuqdcqt3h4sh2f1ieugr2xtebej1v3a\n" +
                            "gi45dvc43u36pb5atris1haljka2h24xjfhzmhlm43cgfbows3\n" +
                            "xn1qd2x6dgbtg6kzjuc4qswhn3mxbe6zreffijwniqk2fsc13t\n" +
                            "lebku1h3cpjyy5ukyv1wrewfmorpqfc351r5m6sof6vflsy2ng\n" +
                            "jtrjmta3axix3m5uorjwutpr26y6wdykpyvayh2oo2dxfnhhux\n" +
                            "gelpeczf3z6lod3uhkb6fcpmvjbk3rnfmkqpgv"),

            // 15
            new Scenario("jf 6",
                    new Key("T52AB", "12:21:18:30:42:21:02:35:25:30", "6-10:I:2-4:III:1-8:V:IV:5-9:3-7:II"),
                    0, "4tyjjpnxifxlnjz1ppn3b2jyqh2vvogs2a6px2wzfbzqejj123\n" +
                    "g2munkb2c1jniame4t5ngrlwne3h11mw4azewffk2hogcaco5h\n" +
                    "pmjd2atn5dlctktd4qwybekwbthrej2fhgfwt6wtt3uasmgzal\n" +
                    "5kj65lqavfqfiikvqmersntzbaj44paa23i4lt42vclbxzqxbp\n" +
                    "bmdff3vycw3tsfjrftbdbmy6npt5yx2nuq4kpwqoipm61amsye\n" +
                    "vwj5qvah6chbpz3dtnttp5pctdrytytjacn62e2aoyzg2ahvv5\n" +
                    "1xoqs1l3gyrkkyrhoxb3fmngj4wurmfldtvr2oh5tzhx4gzwmk\n" +
                    "emfn32jbflh2cpvr4ektllaxkiyge1nrfonxlybjyflvtbribp\n" +
                    "vp3e6bzgjldzvwjom4qsplsp55sse151oylvyttproegiorqnp\n" +
                    "bsk2kjwv362oteausbgcfqdnfnvzpdtjnu1kheirnzertq44yb\n" +
                    "2sawmkzfv65om6k5kws4orreg56zp2vteva4a4wjvx5qytkjou\n" +
                    "umsumj1oxxsxx2px1lukudjpdshunkcpwy1eoockpvtgytzgr6\n" +
                    "lipin5hwesg4dtthe4my2bk1necgwegxentmpzp2zjnmcd2wyp\n" +
                    "4hvvcw1kphseum1sobqv4mgaittqrs6wbmblelkzquwag44aiu\n" +
                    "rvv5zufqbgms43ngsoqa4xptfwqoxl4kv4kfnimmchne2t4sgx\n" +
                    "43f31l3ybl1hv4cpuywxrb2nnixyfeeveyc4nbs45mahwxctkp\n" +
                    "61iy4a2canviq4r66iobe52p1tnvibvd1q2g4klxp5dyf41jpy\n" +
                    "44poo4qqduay3j1bmjoae2tc4u3w2dqv4y1cj6k66yr2oni43h\n" +
                    "14tmophmc6xd3sl3tp6bdfwah3p6cjqqqbgfp1m3hndhy322kk\n" +
                    "oyiac5xzacdrfaykdnmjcw23scxipufi2euzofihjvmxz3ln26\n" +
                    "zh3p23daut2cad6qhcmwphqtwj1aq4av1xan1cqcq4qczqycuc\n" +
                    "xeuvgns4s3gny521djdf6ulwqy5yf4am5j1cod5zhoovjx1uvc\n" +
                    "k4ixsqgv2syg2epowy1rj5m3fh64fzy5zxjk45yihinsaqc2uc\n" +
                    "s5gjjeiv6ifmacaxbigtc21z2jiswvdcpqvygzpl2nokuffoy5\n" +
                    "u4qemivjksktyp5nmuflcuw4a1ippqndx1jtswodzgltxzxthh\n" +
                    "441qu5g3sn5theucgn334lngcrmkjmfh3qvojdxkfmy4ujnrfg\n" +
                    "msocc6or4m5mxf5mlh6ohwpnamo5t5z4mtfvyh35vhs1fc165i\n" +
                    "osh2qxa616oh4m6pik1kw1kwwmxevj5bhgjupisi6khswekwor\n" +
                    "vad1nl4vhzqoftzksncxmfnbp64krrratey2ned1v1va2k5lsd\n" +
                    "hvxabfoe46p4djydgy6denc6v5spzrbdcuvga2brlxb2jvr5py\n" +
                    "camubvl4xhs1swkba6apqxequhmnyfqw1e14op5skb6vmnvwjq\n" +
                    "6gxpy5mj6ch6h13rplgmasy41nenfyd2autg3cwogfezhrdugp\n" +
                    "pe1tfmjpcxld4funj3ul2eda16upki1onpuuasofahbdrai21f\n" +
                    "lalhinuanzbo3bppmnmpc5jlriaq5mcnlopavchjt2jjdkcprz\n" +
                    "xjyrrqx4xhadzqbw2qixzeowmbtvdrvmbgbxwevggmp3x2ydxi\n" +
                    "ldsazhwnqsdwtvbgrialcejctjs1mqy4qtkey55mtxcftklwvx\n" +
                    "4tope4xq6xhc4xbw1isnrz1fprufbtrwpn2ds423z444zos23a\n" +
                    "yciov1mmd3vv4lmf1otorqw3e36i2vbvcaqmma4vhcpqs612rb\n" +
                    "pgqmhurgb4csxnyxh12qvnqunfekgbmboyupifkwotxgerhdbn\n" +
                    "ldooxvqdu4skigxz15lqlmf3jzsrq5fqznhub1qn1xgyvo35ll\n" +
                    "xqolzhuo1qtjo2d14hh2o4r2ftlk12agwmwbt3yiodh221czjy\n" +
                    "azajpivuv2cxcaqr532si46zog4wrzmd6p1qfd5s16jflcq4mg\n" +
                    "l3emrxlth5naeyrdxlzbfh2nxw26wvxqans22dpvf4fb3huvba\n" +
                    "2p51rf1jt3yns5ttrfsc3cpm15yq4vy1fsqibydq4gc12eq5k6\n" +
                    "ee1htfjatkslv4htygqcdlpvopr33bdon1wt4p55mjh3uclceh\n" +
                    "iis4yur1vnvqeytjut56oy1v1hwdfvec6ul425pw2vtkg116rz\n" +
                    "hzrroog3jjpqv2dwbwrw35jrs4hvb61cstqs3kxxbvaztjg4lo\n" +
                    "kb14xq5gzxklkh5wxv6d4a1tjbvo1zve5suz3c4utfca3zj3bt\n" +
                    "2kvpi4ewtrq26exa4jiqlse4amettxjceqknsl1koymd1qtp2h\n" +
                    "2fp5qfsg6ocn6nirsgcs5gkpj2kbuo4c353h1ear4zfyfgroio\n" +
                    "hzkpwm363m4sbq4chhggjeywwnk1casmfm1cymzxhdpp6azq4q\n" +
                    "x4z1cmneftsp3fpmgx5bjxzide4treneqlltw1tvcsvmpkv4g5\n" +
                    "63lvnmfrcsvifjmebqtbjfjxzhhwrmhr41c6ioaxgza2omxgzp\n" +
                    "6enq4jd3n4srv1dnimdpyiyykiykfqotmn5vdc2fgu4j23kdkk\n" +
                    "3bq4pooaer5hr6ncqjp5l4ikzjbcltfjzn56qiy2n3jl5fsjh2\n" +
                    "5odr1mjfs3dcx43ky5gvkvetntzrb3op5hgmjjkwq6uy1mejfb\n" +
                    "ccfbcmjyn5xenybosegkhsbgtrno4zb1ldhvbw2irbya63oqgp\n" +
                    "c211mprey1do6xjgujrxbar1doxvkxi2yccmgod4hdxq1dtwgk\n" +
                    "xyorn1msdxuez22h44pnzwehlpk4egklsqnwoavspkapg1kr2q\n" +
                    "61yur6goeaqo3z611pdsmd3f5boall35ztkvijf41hwu5n4inu\n" +
                    "fura15yrpxvw4po6vvapvot6qmvh1czrfhsmlu2zfo1bpzof1s\n" +
                    "r1qlvgiacjc6c3vdsi415aypckf1w635gud6idoxykndlteaow\n" +
                    "n344vf4powj3gbth6iuxw4i23kga5tsysvgimsptqkquknqe3x\n" +
                    "ntmnstmwlnwdq2qyobkiga5v1l3oetrmqva2szf51niqcbjssf\n" +
                    "enqnfgkqyxnytqzef4lemwmpleyrmltnoscmgvltnz22d16jkg\n" +
                    "gv343od1vzrzndbrk6dzuorkjwbmkcpak56ilbs6jwfopmhosd\n" +
                    "nqrp6zzdjwotk4xvyq4644k5tiq1t3mxdzvmi31kwyxhr2qo1c\n" +
                    "6adomhebeebck4c4trgzkk3um4bajm5nzrrgmkoosviclua2o4\n" +
                    "hjhur3fssc32meauwbtn2izynelocrcpnxnnizaeojb1r5oew5\n" +
                    "e6jfvxh6xuxduwzter3defzxcz6a6iatrd5pfccgfim62y6ys2\n" +
                    "jgzhvzaojljrammbjb4veehk53iv1ecgp4comtvsqm2flqznhm\n" +
                    "345lqav3u1fykrehtu"),


            // 16
            new Scenario("jf 5",
                    new Key("T52AB", "25:17:20:38:17:03:16:10:01:43", "III:II:1-5:4-8:I:IV:6-9:3-10:V:2-7"),
                    0,
                    "fsepzyqkaxg1u3ngvg4zxsqfrna6her22jyoip1ritwn6z6czy\n" +
                            "24nfyhxuicqdfk4oa4kw1jddv2cye53pttsooxp6vbwopy3pbl\n" +
                            "evxf5t5zjd2u33kgyrmkiuichbdivxueohqgqt5hybtygqrlph\n" +
                            "ilq2n4nfvzemmyqxpk2bq4okcwpsp24zsjttapfgj5unbrc13q\n" +
                            "fz3s3jiyl4qmnrz5nzyud1ygivfevv21hqxhttzr6inoozrmio\n" +
                            "1rl4imxjtdczoi2be24olpg4n4ppx2hudd6tbvoh3cy1ereisu\n" +
                            "vqh3cgj3ysh6kwuzmbi6zgvwxtsmpmozcmsiawu64vav4sjpfp\n" +
                            "yhyhhy1aldh4tebmay6u52zi4hsjsa5sbdko5dietwcxwn1sws\n" +
                            "jvtbn1vm5flwahpk3rsegdc6hqmlhhmm6zd32jxxditt4um5ek\n" +
                            "xsbdyovkuwcol6zndlivl4ivc5gxqapcgkof2k3h4lopmpmers\n" +
                            "lvw3mabbyrflwegiz1bwhq52rrf23l1fl5nhro5scs2r13syjz\n" +
                            "oiaz2rvpiam4rqorclabblgqsq1hfdfx6ny6oaiyjxwchdcg33\n" +
                            "dfuba2vsdzuzjcruywhuc6hryy6zk1"),

            // 17
            new Scenario("jf 5 with special ! instead of ",
                    new Key("T52AB", "25:17:20:38:17:03:16:10:01:43", "III:II:1-5:4-8:I:IV:6-9:3-10:V:2-7"),
                    0, ("3haz4ol5o44x1wjjdnetkvedct\n" +
                    "l35bcaa1rjldvaun3g1gly3gj\n" +
                    "dkzbg5bpd434fvdsvnepzynkudtsc\n" +
                    "535bjhwgy1lxdylkf6\n" +
                    "lqeqjt5i6k5ax4dbefjjasnhdgb\n" +
                    "wf2mgcavq5mdthjunomgpvob\n" +
                    "33ybvjjidj3ziqqvsrinpwhapzxpu1r\n" +
                    "z3jd4jzchz5efgjy4roivy3m3gb\n" +
                    "lhazq1w5kglxd4bp3eyj3p\n" +
                    "xhakjnwbdasxxtobf6\n" +
                    "ec5zyca35znbeglvio22\n" +
                    "ly6n4ger6e5ax4cpvg16u2fw1q1bq1r\n" +
                    "dcehja45t4uubz2\n" +
                    "6kzibl1st43xs3jdchjdpiob\n" +
                    "n35u5w1kl3wefikzrir5athhdgb\n" +
                    "ecnbyaavd44c5vjbz2eyhygdfb4fq1r\n" +
                    "lfimbn3v6w5ax4dbci1ns5cwrall5m\n" +
                    "lqelaald1xnyc3unsoz6piywdgb\n" +
                    "lqkovlt52jkbfnfg32dqsmx\n" +
                    "n355cgerd1n64ytvixa6h43gj\n" +
                    "rq1f4es52jnysdubxasgkgtvvgb\n" +
                    "n355qtxctth1fvsycbjduqhffheoudvai1ixe\n" +
                    "dkzbe5tgd45ax4nf2i1cy\n" +
                    "51z4blnt6iuax5i4txhokivhwgb\n" +
                    "x3dvaznv6i1jdefocg5ipwnhsfsbv1r\n" +
                    "tpeodpc5xzmdrwj4b5ispiob\n" +
                    "66pvq1pi3jnlrwj4b5ispiob\n" +
                    "4hauqtwi2r1drd64vos6piywdpruuylroza\n" +
                    "33kibl1g21njd4jlsoy1iccxsaesc\n" +
                    "6q1cuas115mdtapox2pjsqv2b5ap151wg\n" +
                    "6q1cuas114ueffjwioy1tfpwddxsc\n" +
                    "6bq4qs3vbzpdzewvsdt").replaceAll("\\n", "!")),


            // 18
            new Scenario("jf 3",
                    new Key("T52AB", "31:28:40:16:10:16:08:21:39:44", "4-6:III:II:5-9:1-2:V:8-10:I:IV:3-7"),
                    0,
                    "i1alas4cru3ssxokounkpumg6e56tauwyvbctoaw4clc5sjgv1\n" +
                            "43ofrhb6xqsehzvbcful1npseuf1nja2yndsfvoqu5cnvev3zm\n" +
                            "npfvmyws2augd4kfwptkqju56k1nb6hf3s2dayqpo6nw65hjn4\n" +
                            "21azkx3fg6m2qkxv5fyf26xkqkygyyhzriomeeh25tm6puirn6\n" +
                            "fsut3afcot5naya5f164zfoutdand3nkainx1as63d3lumzsih\n" +
                            "m1loow1xf4xstpxo5dtb13hkxo3t5v65eweltd5vhgk2lyiam3\n" +
                            "a5gr45lyak3yox6c2rglww2kpu1li2xqpnj3"),
            // 19
            new Scenario("jf 2",
                    new Key("T52AB", "41:11:24:23:30:39:41:28:37:29", "6-8:IV:2-10:III:5-7:V:1-4:II:I:3-9"),
                    0,
                    Alphabet.toString(Alphabet.fromStringBritish("9M4OKZNM3SKEYBEUQ938RSV8W+HPH4KXQG8RJGALPHXOG8EQR9\n" +
                            "TH+I8D8NJYEK8LWOPIBWRM/B8OCQP8PGL9OBZW+XEFNFSWLPGE\n" +
                            "WI/4Q+XDF/4EUIZ+EA4PHPBHO/8QPP/3JYVYXSEHU9QFF4MTAT\n" +
                            "FG9VKR8Z3OBXV+IZ+OD/PDIKT8/KLWT39IDQTRC4E3ANCFNXGA\n" +
                            "3OSIQOY9WDCBDJMHQDTCZYK4WB/L8MACAGRNRN/FU/TQYG/TAO\n" +
                            "FMJIQFNMFA/JUTRL8T3DLHSK94IUVUIBKONRARFGY+/STZQZOB\n" +
                            "KFLFL3UU48AN8CVUSOXXPFONRT8SDSKNJUY+QVWZNBTW9S9JTB\n" +
                            "3+ARHQDFIECD8++AT3PESSI8XZKNULRCQAYN/MDSTRMASQ/HPK\n" +
                            "N+WQY4YPVC3STRIZQ+TRPMBC"))),
            // 20
            new Scenario("jf 1",
                    new Key("T52AB", "40:47:04:32:20:39:21:28:27:19", "4-6:1-2:IV:II:5-9:III:3-7:I:8-10:V"),
                    0,
                    Alphabet.toString(Alphabet.fromStringBritish("FLMXONVYXAXRXWIP8BXRZAMB9MNUCZ8MZE/WCMQN/NFWY43WXQ\n" +
                            "+ANOBU/DYIA8VXFBISLBDVZQLIHDN8FNVP+IJEBXG//WBKV+FT\n" +
                            "XWJR/GS+TJQPTDBBTCJ8PJP+PDYURFVD+FGPTZY3+IESKYL3LE\n" +
                            "3QBSBY4QOTHEH/AWNNCETXWWUS8FZ8VNVUS+XEDOM/B4DLF+M8\n" +
                            "DV99WUGFVOC4BVRH+GJRTLLEWLYPWDSF8ORMWXWVNBLWLCZBWQ\n" +
                            "FJWUFLKIZOPUCYZJQ+ZO/KQDKLRIDHH9KZBUCIVVFMDQ4CAYAW\n" +
                            "MVVUFGCOA8PFPUC/8/YVHCURLP/UTN+ZNUHJAXDYQT/DHS8J4P\n" +
                            "Q8RWRU3OKLVP+QM3X8/DY88EY3ST/ZCE"))),
            // 21
            new Scenario("jf 4",
                    new Key("T52AB", "41:11:24:23:30:39:41:28:37:29", "V:IV:6-8:III:5-7:4-10:I:3-9:II:1-2"),
                    0,
                    "vma1ddkhiihz5qm4kgv1jrfdibye52eb1s5ppa1zvvssrthudq\n" +
                            "id1th2snk1ijxu4xvr2gb1lg5nzdnafgn4swblqhewm2szi2gy\n" +
                            "owj6qsbsyy5cchebbfqkiqnjmzdjrayrjappow3qo5hawlg1dj\n" +
                            "53qviua3t1aq1raoe4gzyqpxsqskx44wjrtbo5byqxvg4rkf6k\n" +
                            "ppiwt2curzxztwgtpxltipl1bk3tvepe5wvopkeo5zbbpxrqun\n" +
                            "dlai436xeqwwtert4qd326ovmdavxcr6enfdwrd3fu2as42mom\n" +
                            "zwganyo4sml3nb24nkncb62rzg42h1b25htfihg6z5xhn4wy5k\n" +
                            "bezpodhvelrettcjpv2tri4awyxa2rlsmbkethedjygn6coxbd\n" +
                            "i46oaofcnwsa1y6hbecatcyk1g3undp3x1pjevazovfwwmmj3o\n" +
                            "6btxr1hicirflbbcjq5t5ixicoo625wgex4vyujvhyyp5blzrq\n" +
                            "sl4ol4yc3jazr62sioub6gh1b41fd5"),


            //[33:25:24:03:11:17:23:22:18:40] [V:1-9:IV:6-8:II:4-10:III:I:3-7:2-5]
            //[33:15:24:03:11:39:23:22:18:40] [V:9-10:III:6-8:II:1-4:I:IV:3-7:2-5]
            // 22
            new Scenario("jf 8",//                       --:58:--:03:--:13:--:--:18:40                   V:9-10:III:6-8:II:1-4:I:IV:3-7:2-5 - wrong
                    new Key("T52AB", "33:15:24:03:11:39:23:22:18:40", "V:1-8:III:4-6:IV:9-10:I:II:3-7:2-5"),
                    //V:9-10:III:4-6:IV:1-8:I:II:3-7:2-5
                    //V:1-8: III:4-6:IV:9-10:I:II:3-7:2-5
                    0,
                    "fzkxditgrecmofujbsspykn23tddvy5r6o2oyztpmo3uebqvwo\n" +
                            "qkuwcjx4xao4naefngkbizgmvtxjwfcmegvmkabzizkstoknsa\n" +
                            "nefgdahqlviuroz2yxkutd5jizyjphwpct6hkq5mqmh4chz6pn\n" +
                            "yi5n5kugpm244vjhxa4ypu24evgmwb66galzcepeygwgercqpt\n" +
                            "3kcxynnvrm246p4zvzwqg3pp1yztfjqpyjloepcgjzkgkz53f6\n" +
                            "pimluf6osra5x1p31gmmj14vhhjtelk5bfh2kmywjdrvtvygzp\n" +
                            "zlzgjkedcc4d1ws142upfrkp1haj45rbgyzz5tqqlutujkdsqh\n" +
                            "6zkgfbvna5klndev13kjrpx5ck2ienz3mlwka4ja6jlyiwce46\n" +
                            "ai5abmaru4zni2qyjp6vd5n1b5ma3sefxcfz4lzzpbdi6fgc6w\n" +
                            "mpzj1gq5zjci3sedylwfzw5uqncwabbeycxsgkekzarm6b3bvj\n" +
                            "gn4kfdqvlb6g2d6e31l3zz43wq64oqiqllroes4pkdfh456e6d\n" +
                            "uijtquv3k1zmp22etaevq2ktfrl2khp2xzx6bdztsik3tntgwk\n" +
                            "os265ivskq5kmv1jrqciwj2y5uh3ttn12ki1xm5hmstizi1qg4\n" +
                            "eeook3puucmf5pvlpakon2uz11lwzd5a55v5np42bkjyqwahs4\n" +
                            "hljlp2c23epsxwijruhfvi1ck1sb5phy3ktl6pje5kwnpjl4eq\n" +
                            "qkw3hg5txcvfymu5vjicglnam2du4bad2p5ksfoej6huyxchcb\n" +
                            "eaoda5v5umzdi"),

            // 23
            new Scenario("jfc 2",
                    new Key("T52C", "11:34:41:30:32:18:13:38:14:45", "I:9:III:1:IV:II:3:V:5:7"),
                    0,
                    "OLMNJAGHCORCTVUHEQVALSZVVKQXUULZMPAE+UMTJB+KBD8Y/4\n" +
                            "MWEJKS+B+OKIXIOQLSJYYJMY9L9CKAX3/FS/UJP9LSFEGOENSW\n" +
                            "DBROYT/WBMSHURPTAB+/9M+KTKTLYAG/CRQM9IL+GHX8ICZAKT\n" +
                            "VYMEMLHOEOS93TNKXKA+KUQSXGPPWJTCAVKS+QVWR+ZFC33GGL\n" +
                            "RK8N4LSURVA9AFQXEFYDRIURZITQNZL9LF9V+IBV3ROZAQWMVB\n" +
                            "T/V4NNMEBCHL+J+MTYEN3TAFGKX8ASUCTNMGLQI8TLC4QYQGSF\n" +
                            "WUPXXRFH/NDKQXDV+MPULMFT+U9XANFNCHH4Z3EBYF43AEKDYD\n" +
                            "/Z9PYGYC/RZPNR3PBXI3WHYVBPEITLR3+YMU33DD8WZMIYUYAE\n" +
                            "OIMPDG48DODCDY/VA4A8SITRRTXJ4JBD+DB/M/4MZ84OREOREN\n" +
                            "MGWGZR+3W/YTZF+L94CEVEN9+9P+NVD/QSQX4EQMMWUAJPSHX8\n" +
                            "HBRLFIBFVJ88Q8SHTKI/MKVFCWRQD4ILBGJQ+/LS/A4AQD9R/B\n" +
                            "38PIKHRV/OCOS+GFTGUVJUBBB/LAEX94ETZG4/NZCV4VLZC4VH\n" +
                            "IG9NJWDMAVYZE93+C/8S8PALKNQHQAXWTMEJMYV83OVV3HUDQ3\n" +
                            "CMFJZWTYDYOD9KNR4V3UBPRZA4NK3PCS/PYL4/UP4XWDHC8QT/\n" +
                            "IW9JHA9EI9HFFTZMCD3Z9JMJDIMWGDCZBDGUSDW3OLDOVJYJ+B\n" +
                            "NDE9+DWEMAQA4LBBCZXLF/QAGNLY8SC3WG9ZF+HTJQLEB4/AXJ\n" +
                            "D9GFIFFU8ASB98F8UJSM9IQZB/MI/WMERSWT4FRJXR4RZX9OUW\n" +
                            "UMXS+DDB+F+BNPBB4GON3+N3GLW8H3+MBSBGTVA+RSUGEZ9LGY\n" +
                            "Y/CVHB3QWPH8ZTCIPEFTH+MPDTZY93LJNDY+GSGH4GL//3PR3M\n" +
                            "NIYG84ZSB3/UV4QVTCL9HUD8VLXEKZQWDPQ4W+J9CPA34E8ZVE\n" +
                            "9EFEJ44A9KY8YCTJG8FHAOQM8P/ML4GAUC3FH/YP+A/AWTXSL/\n" +
                            "434QFHQELGEXHY3DKK9LTDPYAC/LS/TBH3CKIJXTYXBDD3/BXI\n" +
                            "JD+UFUDIB+P38+MIK83IVYO/BHFO+BYOYXIV/KKKGQECXOBMYG\n" +
                            "SK+XAMVY/V9X/UFFVMICWN+YJZTLQMSYM8Z/9//BMOCNI9MGP/\n" +
                            "MGFU8AEEPZ9GEVT+ZXXSFI9+WPG/P/ZT89T4AN8DDAY+RZH8YH\n" +
                            "MLAMXNGTB//P88"),

            // 24
            new Scenario("jfc 1",
                    new Key("T52C", "17:10:42:19:61:59:27:49:19:11", "3:1:II:V:9:7:I:IV:5:III", "ZXUSP"),
                    0,
                    "NEFERW3FHOM+FMB8W/MNUDFKZCPOML9HX+8IPDEF3ER9FYZIJW\n" +
                            "DQL+QWP8KE+SAJNAX3MHAWIKEFDW9SH9MABXOJT4+E9P+83PSO\n" +
                            "UM+G+M9POFHICOPBED4XMINQUGURU/OFBGE3SWLAT4UM9MGJ/O\n" +
                            "U9SVVRJANYTYGDZJPQULDGZOGTGDQ4BG+RCTNJ3US99BHO9YKN\n" +
                            "WFRVSGOVGYEYBWNOPNGCF94NQUZL4LVEWJXZ898+VBPZ/9NGHT\n" +
                            "LFGRDCJSXLB9DFLICXRPR+EDOLP9PLZ388RICYRH8EN3/LHYPC\n" +
                            "RVF43DPYPOPIR9NOBKHF3JXOAU8Y44N+KFF/LIQCPILGP+QVVR\n" +
                            "U3LRSBELXXPPAZUWI+MLBE83KMBBKY3ENSFLAMQYRLMILV9DLB\n" +
                            "9TOKPW+RFRTDEPZ3/EEHZTY/U3VJGB/UTTDQGEHIDSSRMDDDUR\n" +
                            "L448Z+4TWKLY3V4HY9QJDUZVBKG8MM"),

            // 25
            new Scenario("jf 2", //33,3,10,59,24,40,11,9,12,24//24:12:9:11:40:24:59:10:3:33; //1-9:II:IV:2-10:III:3-4:I:5-7:V:6-8
                    new Key("T52AB", "24:12:9:11:40:24:59:10:3:33", "6-8:IV:II:1-2:III:5-7:I:4-10:V:3-9"),
                    0,
                    "1whcufqm4ajebwe41rhurbbgdpxsefwbpm5dghdtrhu6foqsee\n" +
                            "osgwef4saxcyddqppiu5v4g32gxy2de4pndxhzgapffs13yrxn\n" +
                            "xrao5oqeapef3s31k3nvsirmfv3e1pxmxle3hldcwodlxsmsbd\n" +
                            "j4s3costoh32nmxn5eknkzmwmrrrg1jowdqhgiqjp2jqpcrdf4\n" +
                            "i1zmv1r3brshz63u4fuba6tqckl2cg5uik5tutvf41dzz5lohn\n" +
                            "uydwajlaatjwlnpwamopxu34stmfm4t1o3qlsedwbeo2sgomdt\n" +
                            "ar5gc4sbvkur45wu5pk6pr5bspzp6k3bc1tjf34mcrcqmzotyk\n" +
                            "ud1kijeusg4nunssfekdf4qh6qwipgw3yft5l24j435e5ic36q\n" +
                            "ujuk4lue4eusxp1p61iwt4mwm42mphleaw6or1b6yzpruacp5k\n" +
                            "yv4kvd1t5fnxndfycgq6wt6om1cowjq1flmk3deebsbcrdyfpl\n" +
                            "szxjo6h5bxbmlk1lx2"),


            // 26
            new Scenario("hard test from klaus.txt",
                    new Key("T52AB", "01:01:01:01:01:01:01:01:01:01", "III:1-8:II:7-9:I:2-5:4-6:V:IV:3-10"),
                    0,
                    "EPH6BBRQMUCQXJZ4VALJOFJ1F2X34K3YJNKOY513M2I1MGKGN3R345KRF4CZFI6QM4VMTF5OV4XQDE2COYPAQL2RUZWKN5YEOWHESKKK415JYTQUNJBTR4KK2FGTCKF6RBR4VO1A5VDCYFNSGNXOJLLSFAZP5X3SBIOVXC3XQMMVXVZVOAZLA4UFSK3HZNNKFBQSPV1SBL3A36HOSXGV1EMZQHH5GY65FUP4JDLWCU61Z4MF4XP3UBQ3EAFM6SSNGBOJNT552N3APTBZ13VVRUPWFCM45PRGGNTZAOXH4TH2XJMFZTSFUD56MORGGVUNECXASFGU243RSWYN65XHMPALACELZFZSN6NBJYNXHMH6ILTMLTPRKNVHY35P61LSD5TOOKOJGPOSYPWZBAL3TWC1TR2CAQYKXYFL3B21AW1FXTCZK5OWSX2LOESK3N1P1K3DOWMHUJTSPH1JTQQYMVUEQXLWN1XN3N5DSXFFY4DXM51CERRX5UEUZDU4XP6UJ6ACRSDFXLPPQCQ5MYSNSYHRYEK3R36NWROELU4PBKHUNQQBIVDUFMPIX5F4BXEXCQVMTJW4KJXATFUM51BL4ZN14WKYPKT4W1YMHIJLZGQAMT6RWECN24UQSLZMYLO33A3XA45P1QYQ5N1B3XVUWNYJYKORUX6MYG46QZVGUAIDDLUDUZF2YBOABM3HZ4XIHP4X5F2R1YJBUY6ZAQMSZWKYUELCBBQPWT22T5YYKMHZJPA623YRKXOZRK1GAJBB5ZF5QU1QS2Z1ODLXXC4PZ1DEZHMBAHDLEZ3IYNV1S5J3B"),
            // 27
            new Scenario("400 from klaus.txt",
                    new Key("T52AB", "01:01:01:01:01:01:01:01:01:01", "III:5-8:4-6:2-9:I:V:3-10:1-7:IV:II"),
                    0,
                    "EVJQVC32TLR66OBSQD1TTNJHRKPGRYQPXOEFBJ2POHEZEMCVEQGU1TR6Y55QMDOKXWCJN42FW3PMUPZQKPJPFAXNMLPQVOEQVAWELHJCT3E4O1AF1KX1CU4J2AFI4T5JNEEB6C3YEU6FGFUS534YXX1BVTF3WVQ56R6P44CKZVIUXE35S5CDWYDWWZWFDKNKLNMVRIJ4TMFVZAXT4Q4ZBLOWKAGQM2UOXKSGJW3Z3YMWR2NYULWNWYKVFLY4C4QBM1CEJFCOGU66SAKMLVSVWS1D5VSQPR4Z4QJVC2MN3123HJSI4LVOFAKJUU1IN4WYQV2IYVV33A26KFOSTXR4P1MO24GZYADZ3ESXAWFB6JF32D6MMPHNQGP3YJN2INCZ4PCKEY1PG3PM5OT5"),


            // 28
            new Scenario("jf11 588.txt",
                    new Key("T52AB", "44:18:50:21:22:19:47:08:39:10", "1-3:III:5-8:4-10:I:V:2-6:II:7-9:IV"),
                    0,
                    "1cfh341cew1qgrkr1amhtdetomw2viajchqxxbewaiagauubgx\n" +
                            "5qpdlbvs1xn52lxtr4k42b6my3ztdreyqhs6sfhwfvfk6qeill\n" +
                            "c2fs4chjz3blfmngtvx2taemcbsmpreiss4zpe31trygegjtfs\n" +
                            "3b2szoe2ev363vcza3bkp4fs5bmtbdgpplnakoj1mjjkttgiox\n" +
                            "iiwwwvzqaql33s21dx1k5nuvzlgx5ayndxxyn2oj6xljl2k65a\n" +
                            "u1dhppxgoywat1kxmzx1xtg3vsx6p1hliq2bvyizlq4hfqjewx\n" +
                            "ikbnpmb5ktqceypkqcrqcwxrgg2v1uwaavkgiyo43o6f5m3fv3\n" +
                            "xwo6gjwpisqdoan21drqfaf6lrkt6jlxxlzloqcrvj4whgadyp\n" +
                            "3iwpwfhjbvbmucx4rx3vm4sbo61lgptqjx4yu43auhs16pmjdn\n" +
                            "ulxhrl1xwx2ouf64nw3vibytsdlucqhmpyd3rm4snkptexcpnb\n" +
                            "vnld25ldfdcxc6d6z6vx64wcoifxfnp4qbmfnv2nuspgoko4wi\n" +
                            "infwr6x6mee5egkun1bkcz3aormhhvhrargxy"),

            // 29

            new Scenario("jf  T52ca 2 ",
                    new Key("T52CA", "17:46:42:04:11:10:31:35:45:23", "IV:9:I:III:5:1:V:7:3:II"),
                    0,
                    "LSUFQXPJZYVRAKUACC/BL/EFYZ+SITVDFU+E9W9Y9UU+VJXSO9\n" +
                            "S8KRCRQLAZHLHUPERIFCRWPNGYB39T8HSTZBTT/VIOM+KA8AGS\n" +
                            "/M8YTJGTEOVYXBZ+FYAWW9Q8MEUH4XSS8XKGHQKI/AKJDZ/4Q8\n" +
                            "UDHTSPGIVYYQT4MBJKY8PDRXQVBJLFI4RUKF8JPI4LDRQXWVKZ\n" +
                            "3HNJGKOXNUKZPJ+EQXJGPTNCQLDBWSQZYW4QRJIWDO8MCV9WYP\n" +
                            "SXYCGSSG9H9WZGPXWHDIIQOXLNUZHQNJBXXL9WCSVJCOXB4+YP\n" +
                            "TRUNEEU4MTIARKMUU3GEDPEKMEJJSVFZL8BBNJSGOTXXQ4KYK!\n" + //!!!
                            "THUBKHXNW/9K+SZZQY8ANMH3QUKR/WIP/+BNJZ9GHBZPFAS4LI\n" +
                            "+VRWA+GOVMVXVXEWAOTCLNJPRZIDPT9EADJVL8ITRUIW9SIAQQ\n" +
                            "C4CWEFCFRGIGV3HD4PBNVOBDOIZ+KBO49GQMPUINVTBGPAOT/J\n" +
                            "YV8+HWOSIJGBA8PW9LBFDSKJA93I8ARF8RDL9SUWQF3PXPA+IJ\n" +
                            "U/QVKOVTEONBZ8RCQK8KGCWXC9U49GVCPBNQSMWIWYHBAW9+AH\n" +
                            "QFB/4G3GTGEFGTZKMQYDQJRFQU3TJIP9CROLKBIPFGASDRLQ/Y\n" +
                            "IFIHF/V/A39VAFPR+CGCLYMU8NH/CQTSKSEWQKCJQZQMCK3R+C\n" +
                            "9QSXEU8HIPP/IZOKOLO4XBVBD4ZR3LRXYZLUIY/PWOEMPHZ/EP\n" +
                            "LPDCXSJV/KJVBHRKAGS3AHCEIR+SKQCYGTOOH98SD4EOFKAACJ\n" +
                            "BJTTEO8UZDDLEGC/NMWA8TTDX3AQLVWAFNIT8MMFFOWKKUOMTD\n" +
                            "8WVSSZGG4BG4GQXDMM88KN8TPIKXFKGCH9XC+HXHZGL8NT/KLG\n" +
                            "DTQWUKHXSHHLVUNF4KRFFQW+YXRH+EDAZMESJY8UN+XEXIGET8\n" +
                            "U8VJFCTDBK4I8G8WNB9URY+8OAYC/OQKITF8VIWO9PYVQTAT8M\n" +
                            "PTUSBTWLXZTOZQHCDERDG3GLCR4GNXJHCOX+FX+TMYL+9LFQ3N\n" +
                            "YXEGMEZIQVUQTQ4SPQU+SYPA3UXZSUB9/DIDJVWWKYPGO9PBBH\n" +
                            "98DQ+LPQPW3B4JCCEBH3I9ZECV9TPUPQJ/LUOOSB9V8LL/H4N4\n" +
                            "CJIEWABVIFBD/4/D+SYGE/33UUHOZT8K4YHOWU3EYCKXWBWQ/F\n" +
                            "3FDM9JFWTVE3QJYBR/OQMQQEBPTSBNRLM/GK/PGRZ/WY+TOEXX\n" +
                            "JRRVLTPDJKZO8GZKJKGE+QED3ZELNHLKE3TTRKL9SA/OLCI3Y9\n" +
                            "KMUNENOXVMSLABDVXQDQ4XYUQIVAOLFWA3IDXEQ4ACDK\n" +
                            ""),
            // 30

            new Scenario("jf  T52ca 4 ",
                    new Key("T52CA", "30:35:31:20:47:48:26:38:25:02", "3:V:1:I:II:IV:5:III:7:9", "XWZYU"),
                    //new Key("T52CA", "30:35:31:20:47:48:26:38:25:02", "5:I:1:III:9:3:II:V:IV:7"),
                    0,

                    "9GMERTTIXOSSA3AH/VKVPUPASZKTKFVEWW4OTQBXXQHC+G3W3K\n" +
                            "MLUVHAANBJ9N3OBHHTCVAV+IE3PY4ALRM3PYB4OVVWD/RI/HOW\n" +
                            "DBLFJIHZMZXSMLA4SC4GBY8PCW9VBMNBBI+FAJUC+/RDPLVEIT\n" +
                            "/ZZXVEBHCK9IOJ4XT38TAWSJYAZOH+CY3H4B+ZZO/WAI4DXTSK\n" +
                            "QRQ8/QEVRME8FXIPY+KCYDF44ACWV+QKPRZWHASJ8NEIQB+QJC\n" +
                            "ECUUQDKMEXVVXF49HN4EEKTYQH9W3TTD+/PGMOGJ/JSKGPJYA8\n" +
                            "YLQHAW8/9BANNU+K/UGRBCQ8M+XZGGGHKGCVCDOFRNT8UAGYKH\n" +
                            "RZH9OBQ/OFZT+8XFPQZ+S+FCXB+TKPBQAVO3OS+IGJSLDUXIGX\n" +
                            "4H9JJ3Z9E/ACVGTAW9IA9JGYDFMLGDSOS4P8G8A/9KCGFTHCFN\n" +
                            "Z3G/UH9BRKYXR8Q/X3MF3GKVG4KUJJSARDXQ+ZRS8QXWVDBYFH\n" +
                            "DYU9/MVJHAMY4KJB9SQ4TWKLC9Z4LXI+FGXCA8YBCQTR+N/HSJ\n" +
                            "XP/IGHKMBDVDPW+3E9B+CG3HH88VXOJQREV4ZOGCUMYHG3JPZD\n" +
                            "S9CX3EAPQAL8VDXWE+EECVSG4+Z8JZ9IVNLDO/ACI4QESFDCYM\n" +
                            "SIGRPZUW4+UXOKKMYGY99HZZU9GIWWEFRMI+H+V9I93I/W/SN4\n" +
                            "CLFVULOTRAQYFDZOV3YPXNWPXC+NPN4TIGNQUFZ9LCOFMVUUJA\n" +
                            "H3BW4KC3YSQIOPESEE/PY8D3UT8/GMPTOZY+T9JTZPVUK/NKUA\n" +
                            "3+YS34DQGNJJW3ZCZGATQFJESPIOVYLMQJIQ3VS+W88D3VHD+F\n" +
                            "OFM+JEXALUXDIX3TK/NFOKQXJEDFHASSMRJAQYDY8DSMYHUNPM\n" +
                            "9KUCCNH3KWYOLYTWVWBJ9WFVH8I4TIEFMQZBE4+WXCVHPMQLRF\n" +
                            "E4QMFWGTMO8TGP4BEUWZPXJ993YXBMBAAZOPVXO9WVBQRHF/9G\n" +
                            "K9TSKDVJLVP+SI/ZIOI/GVX9NBTSS3PG4MISF/HJGGAF/KH43B\n" +
                            "CFC9J9S/9V9H+IPWQOP4O" +
                            ""),

            // 31
            new Scenario("jf  T52ca 1 ",                //"11:19:49:27:59:61:19:42:10:17"
                    new Key("T52CA", "17:10:42:19:61:59:27:49:19:11", "3:1:II:V:9:7:I:IV:5:III", "ZXUSP"),
                    0,

                    "9DCG4A///9D3+CBPOQOAJDGGGWVJFOBRPQL/8QH3FXHATBNP/R\n" +
                            "FQZAKH/SASZ/UYIOXIUTJLYUU3QQXVL+XSF884OUT9CUT9BWOR\n" +
                            "UVVAQGG+3UVFSS/APCLTT4I9R4RCU9F3HOADIL9JTDRRJZSU4U\n" +
                            "A8/QMIRU/BUFD38ENMZQFSBHYKWSOROBR834BXZXTDI/N4QQMV\n" +
                            "U/4TGJDQHS+N49AEHISFG+REJJN8/WAFHC8+OUVOTVPVBCAAC/\n" +
                            "DE9ANWSTGKMMUXP9OIOD4LLV8THGQHP4PYWLDYPKMTG9QTXQ+Q\n" +
                            "P4ZT4C8B8OK484GTCDZNTWKLLGVDKTQWKQZVTXOCOGV3ZZ9AXK\n" +
                            "QYZTEK3QFQFC34CAPBRKJWD+FJG3FR//GMVCVPFJETNFHNWPB9\n" +
                            "+FBTF9FXVI+U9Z499XCNYE/D+BBLVIPCWXBYNZGAJULOPPSFN3\n" +
                            "KMLJ+Z383KCHRLYUIRNQ3TMEBOZOQYXJHB/8F+W"+
                            ""),


            // 32
            new Scenario("jf  T52d 1 ", //19, 27, 28, 21, 39, 20, 32, 4, 47, 40
                    new Key("T52D", "40:47:04:32:20:39:21:28:27:19", "4-6:1-2:IV:II:5-9:III:3-7:I:8-10:V"),
                    0,

                    "OBP3TJWSPDQU94CXDLYSS+E4NPDMFHTWRDXXRZQVTBO3U/XFJ3\n" +
                            "RKOIY9SDGSPFHN39JADMPMCAQLM9XFZBED++IBMLIUKKIYHWKE\n" +
                            "CBWNGFBVZXO8QAILFOX3TGDNKIBDLA9BSKQFFACTXXC9ZXRU+H\n" +
                            "G8DENUHHYJ/CCZ3MVVNHC3UDXHRT9BE8C8+P9DZNAABPY3T8K8\n" +
                            "TNEPHV9BPZ3ILDKUIGIQC4KWC/EAYFUOPBM3OIFHUFRH48ABZN\n" +
                            "R+TKOTHCYL/JJ/RJTBJ8TBFXCEL34BCT343NDFEKOFRW8HFN+R\n" +
                            "GZFXI+YTZXWMJ/U8F+CPDJDHQQSEMVEBCOO+HBHFIKJCYCGHQP\n" +
                            "JZQC3HAE+NDVJCYR4OUPATDWR8ZHHBHY93KRJBVFIR9BVFBLSY\n" +
                            "EK9QPGRLQ3WSNJRWKSNWQTGC8AJQTAT3"+
                            ""),


            // 33
            new Scenario("jf  T52e 1 ", //19, 27, 28, 21, 39, 20, 32, 4, 47, 40
                    new Key("T52E", "40:47:04:32:20:39:21:28:27:19", "3:1:II:V:9:7:I:IV:5:III"),
                    0,

                    "KSEVIDPY//AZOMSQGVMGJS/+HBKZYXMLNYOWIB+PBUZMCWJZCM\n" +
                            "SMHG9OOXVEMRCZ/TKQH8N3CV/GFFA/JZNWX3FQ4LVO+G9YE8TI\n" +
                            "XBUVMXG9LCIAGKK38ZZBYNHX4BE843TOMVM/TTUDOSBYPVIGYA\n" +
                            "RH8Y/3LEEQX9K+U4IEDFSZPWV/GAIEUXQCM+AZG/OFRHRZCWAC\n" +
                            "3SGS9UIV9G3K3JM/4ZUDO/FK/PZAH9ZQFQYOOBB4CMKKI+TTM/\n" +
                            "RL9SYMO/QSBGXYNN4GC//NMTG9ERH8YBIDQ+DH4TKD4FL3TNTS\n" +
                            "YCSYRIEU4QK4PIN+UEM/DCA8TPDPMBSQF44COMGWHGLBPZC9CK\n" +
                            "/E33BTX+CRAERWALI3EELD/T//UI3NZK4XUUMVUKK/8DXHEDHJ\n" +
                            "XWTAHZPZQDTEE8TQULXXK9MCC+OTYQSEAUNZ89XO8FIU8YQLBX\n" +
                            "CBHEVF4O3I48PHU4/DP4HF8UGDU8ORHMDR3ZIIU/4T4AJNOD3X\n" +
                            "MEOWQQMTEEJDHXL9VWJ9BQNBHU/NGGGIGDOIUKSRQB+FTCNTJH\n" +
                            "OWLQGQZ+H8SOB3EKYBWAU8YCYDWRPY8X8CALASH9O/TP+ABRCG\n" +
                            "JEVYWHIAI433FGJY8S4DC++/MDKU9X"+
                            ""),

            // 34
            new Scenario("george 1 ",
                    new Key("T52C", Wheels.DEFAULT_WHEEL_POSITIONS, Wheels.DEFAULT_PERM_AND_XOR_WHEELS),
                    0,
            "PKQFWRQJGOUNODOPM3KVE84Q/FEKC+IEDTJTHITLFLX998CESU\n" +
                    "QG4KASCI8RMKT/CCOSZ4QZTALQ33W94YPZES8LWZNFI3MTY3DQ\n" +
                    "DVF4PVQWNVJJH+V9TTIP9DAEEKGIGMQSVC8K3IY++RTW9XQEEU\n" +
                    "ZJIPYY9SFPXEE3YFLYY+9MN3PSDPTYLOX49QRBUWGNZKIMV3T/\n" +
                    "H+APYEU/BDWCS9QIYTDMG3/PMJAKHXQEWS/3WH8LC9DPCBPBRJ\n" +
                    "9VGPCIM9LNA+HMWYLICU8VBS+L3CXRXT3TZEVGSRFXJUVWACKC\n" +
                    "YJWVRY3BXG/DVYINIJNAYMP3V4TDYNN4KY8OCGBLM3SCB8XQBI\n" +
                    "+BU9Q3QFWE8ZIUQCMDEV3LOPENKO4XPVP+4ZERQIQDU/JYYGNB\n" +
                    "TD9PT8HEFG4FL8VMGRERC49BHV49FTYU9ZZD+TT/4M89A9FN33\n" +
                    "VNEIJL8WQGYY49WDLPT4ZBCI3TJ8G4CWTDPAELS3UGTYLO8A/Z\n" +
                    "UWA4W3QKHN3DIFUFFB/XE4EFHSLDIP+LGEGYUIEFMSPJH/ZO3W\n" +
                    "UR983DVBEP+NOTT8LYMM9UQOSGTSHH8RMQXEZPNYGFPQJ8UQ9C\n" +
                    "NFMPQS3WPDKYGJSARULYUN3AZCBZVYFYBPBHLCEB/4RATYUEY+\n" +
                    "S89LPPCPRVWQKF8MD+B4UUDWFFHKLGUKV4D8XX8+839/+FZLCM\n" +
                    "LW9IKQVRUFLSH8UEOWLUK/C4UXHGGDHAJNGC+3LXM89U9/D3EX\n" +
                    "OTFWQC+Z3EIBFQOAREPVROFVMCUTGEFIT9ZXRGPCH/TEE3TD+9\n" +
                    "POJLMPZPKLPUYK/8UCVO3DOVWWZJYXYCMHVY4LVALQC8BJ4RV9\n" +
                    "FRFA9NCVDM4G3FWPFWUCJVNVU8SNJQAG9WMWO+TS/F4PCEEICF\n" +
                    "QDMPIEYZVHEEAYJP/WBTS/9X9PWESQD8JKHFW8LCB9MWHQWQWW\n" +
                    "SVJG4+YEPNQD/U+LSNCBI3EWYO8WZN+EDK+MU4/H3CNXJY8JWR\n" +
                    "AW9J9MGV9CGKBIYZT4ZLCWFZHDGVWUQTEDJPU9ZXJ4XSDTP3KS\n" +
                    "PGMVQPDQBJEMYK9JJXKGYEAD3PBTNACSHGWY/9UCOY/9RAVPUT\n" +
                    "R/MYUSNVYQQEIO4TEBG9ACFBEYFNE3Z4CPM83VZ4TFQIO/GWXA\n" +
                    "UOJZFBQIGCE9M9+YBP84P8UR9NKFMTN/WRAYUYCYD+3NKIWW/8\n" +
                    "CZTSKJ+34OEBEWUPZLNPDN9999IT+ENQG+/4OGE8VB/MMRXJFW\n" +
                    "WWCKNPLWE9CPRP8GNOG9VRVIJBKRU4CBBOPULLL3THT4EVXIMU\n" +
                    "+BFU3QZH8FWE9/S3FQVNL4U9EBL3LTRXHN8CKSAYU/AJMPD9AR\n" +
                    "JPFCBSMTGKSGYVDYUBAVJU3RRYUYIQWZFGWGKJP4WT4R4AHDPM\n" +
                    "9XJV+JWYDRGFQFNJWNR3UEQ9YOWJ8JBO4YUAVF3ASFEBDSXPGJ\n" +
                    "+ZW9HFPQOBFERNCMLH++BZKR4CBTJUDBISX3U9HZOUU4SS4E/9\n" +
                    "VG9AQYFKSMDPTPECFFZBBVAHT3AE9C9SRGIGVPASIBFD/EMMWC\n" +
                    "BRRGPACBOQWT9CZRDUAYEOVETEHT3WKPQSEFUATKXACFK4IYPU\n" +
                    "UF8G8AF3WP+V8LMTPM9C/HGF3KJ9WXDOE4XIM44OM8/M+WVHG3\n" +
                    "3ZWED+9TOXI+M8J4C3LCV8BQBANGQWTZUEZPR333SL9JOJNV9F\n" +
                    "RW+DKB/ZWBM4RFCGDQPYAD3YUQ4ZT+4EB+G8A+SBFTKN8EQEGW\n" +
                    "MQHJQPJM3ELNCQ8EPZNRGUILGWGDRSIZU8YFYL3ZRRWMU4ALAB\n" +
                    "8W3AXFG333Q88//TLY3YF/AJUS3S3B8R3DPSCIT9JY9I4FP/R8\n" +
                    "9+IUT8ZATLCQ/SLSYJUHXW9KKTF/3SBJ4EGRKR83SO4KYXY83K\n" +
                    "ASPL3JC9FJKFLHZT83QOG8IZ3VRYE89H/3JJSZCIGSOIQ8YOEC\n" +
                    "ZVB4PU4P/UMSRSJ+IU3BY9NP3HIGSQ8FO4UVWIZGVMG3E+C+EM\n" +
                    "/HQWU+P"),
            // 35
            new Scenario("nils 2",
                    new Key("T52AB", "70:47:49:16:42:17:06:27:06:10", "4-8:3-6:V:1-7:9-10:IV:2-5:III:II:I"),
                    0,
                    "Z4P9XSHLBORN8+HUWX4+ISHRDR3ZJO4QKI4AXKJPRCC4LLKHNN4G3DXCKQUPAQS4E+EYYRLOSBW3M9WAQOR/FK+UEFJM3E4VWZ3JX+3N3VP/EQINREL9VWRIHXJQP+8DJCR9VXS/DNOBEVDPB3RNB3DQ4IE+9WKFCVSLHFV3N4SF93VMBAIOYN4HO3YJF3NOFFC8PQPM4I3KEJDBZXNRFNYEGRWMQTSGIGTQNEZIZA/3+KWSQZMN8Z3PCLJ3VBPSI34TUW+88K88XRDU8/EAU+TQ8QYDWCH3HPUR9AJNRLHEVWPUOLYB9PNQJNP/B8OAJ+9GHZEOAKSM/884TTIURJH/3RGFKMQAPOSOCRIBHFLPEGHGJ/YRYGP9/W4FGGWIFAA9GARHDGRQGD3/MHNLVBPMUZOIEOLEAEI3NASJQD9OYC3NBCYJC+W3VW8BRN3ZUIJKYNFNK3HFJ3RS4TUXYKH4MA3FR9MTFFP/IDYYAZGIGNGCVP9JI9GAW8ZDZA3JYN8C4TSD9KG/UE8AXWEQKMQAAEJKBHGCDGUEVBSO4CX+UOPLCWJGWTAVLCTR8RPDXO4GDV+BVIGG/WIHRDEYDPU9A/F9IJWJ9/ZLI9O4YTMG4XJ8QZ4D8EC9L4DZVM99VUT/BV/LDWTD+EAO/+B999TLBZUCN/QBBGDLB94PWV9L+/MFUBYLZ8GWYZZPWSYGGFJB4NKF8PRN+V+FFTKAUVWARWUDNKET8UA/PIJ8KA/Q4W9OW8WGIQK+HRJBVDAY4MG4/DMLM4FOCE/UYAH88K8KIOHDFQZ9CZ/REYDFKAWFQF8IX/3VHTVRTGERY4BVZCYKS3C+HC3NYMWT8N9QODMHXTK/Y4F9OHMCQ3PKVEOLNXR8RVOGZAABZQUU3WUE8CVZ4HJIEVBVYALWTZC4A9WBZWBG49WDMFCNSQOSLCOYJQFENQ+KL9LL/GTAB8XLKZRFFUBYDYH4YCNXRAASG98E3LQTPBS/CNIUGRULUY4READLSCLUVR/KFP+OCYKBXGIDGYVDB98WOO8RUBJ"
            ),
            // 36
            new Scenario("nils 3",
                    new Key("T52C", "54:53:47:55:33:12:61:32:02:47","5:I:7:9:1:V:IV:3:II:III"),
                    0,
                    "+OB393BRKWI3ZMEZNDCDNDFPQHWVNJJLVPROFM338RIKGNU/NZGVSWP8D8P9RUDDK83QD+N4FGC9KDBBDLK4G+PODR9SXC3TMD/RNGDHZ3CFVH+IS4M88GBMNLDZH94X/CXCANIRRGECUX+FLEZ+L4PG/Z8+JQVVCJ99AO3FUOMF3YIZBVWMSRB/F8NPUT4S+FY44EVI34SMYYGQK4AWOSUBQOOKZRTGBL9EYWQNFMU9G9C9S+H4LQMZC4IC9MTF9KHMWSJKD9NV9WMFKI+9AWHNNNU4KM4YSNMCNI93QBVLB+NUBG9E+NYYSG+O3YHDS4/4TMBG4XV49HB4LJTNVXJRYUBYYE/DGCHYZVJB8RIHJKMMYB9JZ/Y8R8XJ8BCRI+WQY34GYXL9GZUTTERAC+G3LAMSCSOIJK/JUGH3RQ4CFYRHY/9/RXYP8SQW/TNINETVEFHFQODYXMUFBAW+MTU44+J8GU4SNUS/NFZOWDINTNJ/XFC3FPS/9OZDY+O8O+8MAJQA4Y9YV+FBFV94JT++9LPLJTF3YJLHETLU4QLTGX3DXGJGP3VTOBFPSNZNQ3BJWTJQH9G9N+M8R+Z/VW3PY8EAC/3W8N4HQDO/ZGBW4GQZEGWIHMPHQ+LQFY83FFIKH8W4NMMWYJ9DNKCULNJUHN4MUKL9OXRTASFP8GNNPXOVY4WI/IMR3SIEULS3YTALEVIXJSLFE88MUNAODCYSNFJC/3O33NMYRYUVQRJ8ISK9Y98MMDXKVTP4VUXM3+9WMAKNL3EOVX83N/VYFM9XNC8OWTUBHXMOU4NVTHTZO/3NQYBYICZLJUT4NNFLJZQLGWGVNMK8/HX3FH48KEHIHWQ4BE3+E+PJBWGZY3GF4PSLJH9OJ4AHKWRSOUE+/9DKUWAQ8SXUEY3TRZZF+GNHZ9AK9EGZPPTLBDQI4JHIZ+P9UY+LTPXX8CIT3KOHIMEDDKFJOZK39DSTB//NDIAM3FB4GAA9MXP9P9SI9VYDTP9O38IWHXQYKQ9W3PKDMQCUWXLEA8RVSEXX3EWRMUBIAXEFW8JO/JCBHT+9GLGVRQSAC4FMLO+BBD98N3DID/V3E9B8RAW3EQMKF8YQDKIVLECM3/A/3JVZFGL/ZI+LPBJYZK39XMYLH8Y//IV9+GDC/GRHMM4NI+9HOZTN4F8944TS+SHBMSI/+PHQ3JY9H/IFZHF43HUA4RQ48YMZSEQC8PRRUNBAI+YCGS4PZR8BPHESZT8YG+LTLFNNRFBF+/YAGKRR9J+E9IWSHDFNMY9T+9/GGTGWINMYU44VKJA9LMU+AZWO+X3GD/T//EUZS9NDPJWGMSIRBQ4AZK8/KFJGDCEJOUHSN9WHSKHMCW9RUTTBDFWSNPW+LUPH/HPJWHSXTAIACZEFEC/OG4GXJPWSAXF9R/FWIOOLFEORC/AF9WW/SPL384LSUNWZZAGZHMMJDPGPKSDUBJ9TDZYPH4WPEBUASHFGKRB8VKWRRA/SSDUCCTCKSUAG938SDDG9S4MGPLCT4ITXGWNNVIQQ3483MWCCBPIFT/DIH+VWQVDUUUEVLQ9ELFNSXUSS/ZKIGALAT4B3XNNJWAPPBB+YMV4M43SA8WHS3SLZY/LBH3839JX/A4JMGIYPUWRWGHESNS/3IFUC+P+AUJFUYV9UGSM949RDENPT+/4DFHSATQLWGTX9SA8BWUVLE/9XS/G8MLXGR4L+N4GNBPX4KKVYLFT8JAER8Y3YURE4G3ZP8BJVYPMBQ8XBT3BKA9IBQAG4HDCRDU8YCLNCQFXH8RMAVEQOK/MV+NBQDS3GEJKLCRPTCUB44GADIFISMQFGS3PEA9XX+F+EFKNJPEFG8SNJNTPOATU9CEUQMYEDKHAQXTQ8LOZWEIPCHQLMUWGTDV3/PTLSFGSOT4+P/3AIDEAPLAH8HXUBHQQTEEEW/O3SPG/P4AQWLWDFRR/FEAWRO4YSPCZAZBBUGJ+KY/MDNK8SCLMHDQXFIIIML/QUK8QJ4OHXRSBLZZT4JJ"),

            //37 Ingrid
            new Scenario("nils 2",
                    new Key("T52AB", "21:40:61:47:36:22:45:39:22:47", "III:2-10:1-7:3-4:II:I:6-8:V:IV:5-9"),
                    0,
            "vxvic3nbjekqsbbqjypoqd3ex6ncpk5ypqs523yyem6ttuc" +
                    "zyylhbwotoy6r4ji6slw3yjklzdprj4sy2fmcdfwll64p4xcfdlbyypkj"+
                    "vagquphg54xkevn6wfgfeqxlvgneie4pitvhehwupytsxtfejio3cwllu"+
                    "f5lomca255eslcagunh6hddz6wpqcgllnlduqlqlx5bmuxfa5migo4obc"+
                    "c2dnan64af3xv6sbahrv5ynatlmcbvodqjghskp6mug5tecxcamfwzqaw"+
                    "qin6nusfsk5zuthmdy2kq3azhb4udgftlazq2trhb2rzzylaingp6526n"+
                    "stslt5gvgpojk52imsad5ltlrcumzy4lhwwlthoaye46krddcprcxobyn"+
                    "um5u3lplamtioqkykj3jfhoeobl3up4jtvckjyks2igdyelijf6iijysv"+
                    "6wfr5lz6lin66fsbofuyrahaglvegkn"),
            // 38
            new Scenario("t52 no 1 p 4",
                    new Key("T52AB", SEPT_23_1942_QEK + ":34:25:29:17:16", SEPT_23_1942_WHEELS),
                    0,
                    "po2wmmshpmawu56gstxblqkrrgrup1folp\n" + //34
                            "6zzrwbj62akkblev3bs3hrg3tzevpppl6amdeexqxrsf6upbqalemi13iu5gn5uc\n" + // 64 = 98
                            "bthbeumt5fu22h3642ylfye1exq6vosjo3jozhotvhj1l2lkc4t6rhoflw11tup5\n" + // 64 = 162
                            "itxs4vtw4txzu1mfwyamf2vwszakaameipalk3ydpiza1tm4wjenragqtqmzxr1v\n" + // 64 = 226
                            "hfxfub6ofpfaxfle4sgcxem3gbf3hxaoaszhznhgcyqtyoimuthm3gvzbkng6t56\n" + // 64 = 290
                            "4a3eal61utrkwdn1m1epw6gphxe2k3yiwhfy61dld6zir4h3ucgu4gzasij64azy\n" + // 64 = 354
                            "rgun1ydwfgjz5aencvh6yaupxqwbqi3sze1yxvlyjnn44nerir2o3idhjtwemki6\n" + // 64 = 418
                            "t6yhqtauggidoro4akriej3qibvtkewwdo1x6d1bujzff6fvbbpqxpusjjbudgjf\n" + // 64 = 482
                            "lajof6tsfomrtt6jbi6iutrw1i3oibmwjjpgjngdbgfs65xiaula3xiderwsd24o\n" + // 64 = 546
                            "o6bbvxmsdz2zqkibo5ykbh5u3nox3vhvzgwikuw6mlsjwez5akcmot2x3s3m1e3m\n" + // 64 = 610
                            "3ko1abjx1wvkjzdelwtqnevjuxjjmxhcfni6anndzmkbp3b26jkm4rhcaf5bsqik\n" + // 64 = 674
                            "buaeuxk46hu4ea3ubyar6xili5jvodf33lo4gmqeh5lted3tdtwggeq2jeydaejx\n" +
                            "kwjxhd3xu42oxa4kdyybnlzr4woerfrcfhz2e25u1yoezhy52dh4ac2t53h1g1bv\n" +
                            "pri3enied5usvwoi3dkqkgmjovlaeigp6w5yinsy4n3pmacum662bptc4jzxt2be"),
            // 39
            new Scenario("Bengt",
                    new Key("T52AB", "31:68:04:35:03:46:08:54:44:29", "I:3-4:7-8:9-10:1-2:II:5-6:III:IV:V"),
                    0,
                    "cmxaxkccxnhcx2r41zdssu5xcih4ffl\n" +
                            "qx!n3xcutogua1ah24f1cwmkd3absxojtovwpavjfn1vwdevqi1yrwrdzsylig4\n" +
                            "p5c4pxir2t36ibxeux1i5d6tmb14gsifgn25bb2h6gf16np53joima634up1pmi6\n" +
                            "eljnxxduwdpuypc4rmeo4uoli31t6vwsa63t2arhbe3si32ty51p213zoyqfvak\n" +
                            "hppakvlukjbwp1d4rnqdrxu2f4og3kdpwgtkwbrir32h41bozie15nd5cnp1vh3\n" +
                            "huluyqqojtlbwuebru6y4x3hty14yufz22iax2s3kkhzuyrde2swtz1ltrkrhr!\n" +
                            "cmxzlhd6pk5dhbj5mhej1eyo3drsqt3owct5xmh6mbkyte5dniexy1erijlowdi\n" +
                            "vxzhayvv5kpfkdkpwxugastjeexgtjmq6c4yr12wmyh31vsmmff2nflj3xgiels"),};

    static int nils1() {

        String c =  "" +
                "FMFPGBYGQVTFR4BORV9BTK8BO4X+HKWVRVFOBJLQ4XSKDKAKVDEZHMWUXEYTONOSZ+/9ZNTZPDVJINW+K/89UU3HMPUKVK4JTCX4XRV3OKVECZPT9QAUA9MUHEN3WKLBTOH\n" +
                "ABWHFVJL3A/FR4BTCEOKVR3VSX8CN4LDSLO3EXWFXUVD+PDM8EZGNMWXJQYHOXW4AAW84PH3D3A+9HGYCVNRCHTYFXJXUYNBY/ZJLXEFGC4UX/ECE+CPI4X9JMR38VLIJACFIQAH\n" +
                "MFUHFVJ4GUT9GSGCOJ/KAN8F44DTREN3UKTFDYGP4ESNKDTYIBZTNVRT3PUDBYQSZ+/8I+34HI9/LQYDS4YPMU48PE8+F8FI+PE4XRVZ43AWE/9GF4TGKD+SLFRL/EWYFQRYSY//B/W3DFBFWVWM8DBU/4IHHH/MYB\n" +
                "3C8YCDU+EFCL4SG4P9JOUFP93UTK4X8VX4OCV93HYRAZVXEI9LF/ZR+FGCUHACEHBJZRRAARQC4GJQQXXWGQ8JCB3TIY489CFCIXV4RTGNSUVQAINFABRQ+/O9OPOPYCDFYGD+GVSF+H3PPHTQ9YBVS4PJZJ3LVZMH\n" +
                "8TSJ3OCM8T/TEBI9RINW+KG+L++UIENQV43G93PEKYQ3JIFYSV3VZR+W4S9+CANKUFFCBSFMVD+LJL4MMQY9ZAAJJZJWMIT9NVHQLOTKV3ZHVQAINFMORFF38EMSUX/Z+ORQYB4ETU+/MHM9YVW\n" +
                "ABTROZ3OXXVTEBSUC9EOUCYHF+9HPPNWM+DVV33HTM884BGNMN+399SZGJUXWVCUUSFFMWQUECN+9+RALG9VDV9AJ+JFM9+XZKZHQXYS+C8AB4EYAFMOU/W/OOO4OIY38K4PMMKBINSOYEPQWVLBPHPZ+HDRMXYDXCALS\n" +
                "IKKFK4H+AKDJQIZHU8V9ZRBKG+9HPPNOE/BID9WJ8SSKHB/K9VDV+D4TOC+ZZV/FV3SPJ8NRZD9TMGIVXDGHDN4+P3QVH8RJBE9/89Q/USSL/UCGD4BMR8TIHMSVHKJ/4TGTIHKGZD+FK8PXHBAWRPEHAKEQ\n" +
                "IE9GAC+EV4+EIPITB+99TUNQW++U3GVE9QVH/3P+3IPTDL9XEFPT98SZRJ39ZVBUUFFCBPTJ+IMJXN3GSIE8RHKYEBJZUWLB4/ZUV8LBUCAMIVVCFWMLJRC/9KLVAR3OSCYVDRBMV/D3W9K4JLZZOK8REDMP\n" +
                "NFUJXDIIVXL3M+BGAK+JSGNM4Q/QWC3IQXNYUMD/APLKOC4CIBUSBEARE8RE8URS8XPVOGTUHJ9LMYWCEHQ/CI98J+HJZVVIYCJXOZVWHO4A4URZVSGYRJYXJ+XJAOKGH+HUVR+LEH9ELT4JL\n"  +
                "+A+/CXYGYKVGEBIFBT99TCGACGBAWSIVM8LAEDPJ8J+TPLFWI8D/9ISHBCYN/CJRAXWCRLARD3JS+GFNHVMRF8XHXMIPWSKULEW/84YH/WIX4/EZA8WTYNH/OFDSUGJZRORZYSDEVUO";



        String p = "" +
                "TRIB+S89UTE+N89PAY+V89REQUITAL+M8999CASE+S89MENT+N89WINDOW+M8999PERCH+S89ED+N89ALIGHTED+M8999PLAINT+S89IVE+N89SORROWFUL+M8999AF9FRI\n" +
                "ANX+S89IOUS+N89VERY9DESIROUS+M8\n" +
                "MIS+S89CHIEV9OUS+N89HURTFUL+M89INJURIOUS+M8999PRAC+S89TI9CAL+N89PERTAINING9TO9PRACTICE+M8999DIS9TIN+S89GUISH9ED+N89CELEBRATED+M8999\n" +
                "PANGAEUS+N89MOUNT+N89I+M8\n" +
                "BOU9QUETS+SN89+KA8BOO9KAS+ANL89BUNCHES9OF9FLOWERS+M8999SULK+S89Y+N89MOROSE+M8999BOTH+S89ER9ING+N89PERPLEXING+M8999UN9WONT+S89ED+N89\n" +
                "AN+S89THEM+N89ODE+M89SONG+M8999DAUNT+S89LESS+N89BOLD+M89FEARLESS+M8999WAG+S89ED+N89CARRIED9ON+M8999UN9AW+S89ED+N89UNDISMAYED+M8999S\n" +
                "SU9PER+S89NAL+N89HEAVENLY+M8999COM9BINE+SN89UNITE+M89JOIN9TOGETHER+M8999RE9HEARS+S89AL+N89RECITAL+M89REPETITION+M8999BIG+S89OT9RY+N\n" +
                "SHAFT+N89ARROW+V89+A8HERE+AN89CARELESS9WORD+M8999MES+S89SEN9GERS+N89MESSAGE+A8BEARERS+M8999PANG+N89DISTRESS+M89ANGUISH+M8999SPELLS+\n" +
                "HIS9FEAR9MAKES9HIM9HIDE9FROM9THY9WRATH+M8\n" +
                "??????????????????????????????????????????????????????\n";

        String[] cc = c.split("\n");
        String[] pp = p.split("\n");
        byte[][] plainArray = new byte[pp.length][];
        byte[][] cipherArray = new byte[cc.length][];
        for (int i = 0; i < pp.length; i++) {
            plainArray[i] = Alphabet.fromStringBritish(pp[i].substring(0, 10));
            cipherArray[i] = Alphabet.fromStringBritish(cc[i]);
        }
        /*
        for (int i = 0; i < 10; i++) {
            for (byte s = 0; s < 32; s++) {
                plainArray[9][i] = s;
                ArrayList<String> v = TestPerm.findValidSwitchesAB_D(plainArray, cipherArray, false);
                if (!v.isEmpty()) {

                    break;
                }
            }
        }
        */
        CribAttack.solve(Model.T52D, cipherArray, plainArray, null, false, true);
        return plainArray[0].length;
    }

    static int nils5() {

        String c =  "PNE/SIDCM4+UVNEQ9WU9OBKQ3UDNR+9EH++KRTZ8+TBXBQ/H4MU4EMPSVQGB/ZMWU9C4OXGKQNH4OKGGREFBL9VJIRDJAE4G/MLNEUYE9JYINMLHHK/NA\n" +
                "Y3HYG+FVMWB3HZA49L8FXG+JAFATI3N3HJUQVOTLT9O3NJZ++FKVGGSBMUH/APPFODZV/PIJUJJFSM8GY8W+HR9L\n" +
                "PN/SHAO9+IG/O+CZB+I+RW3WCUGQZE++8PLUSVHFKAJ9UJEFT3V3RG+9RHB4TDKESGCLGAPSJDUM3GZVDGJUBW9IIBNUM/\n" +
                "/34ULMEX+FBE+WTM9YQ/RFL3HDVEH+/AEZU3EHRDWT3POEG4+HMAMR8/3US4TLMARBKMW9XJP89T\n" +
                "BA4ULMQR+GVW/VCIBDAOGCVIFUIHU99PJH+XEHRDWTBPYZHPELUYEGCYR/VAVPQLTWFEDFIMUCA\n" +
                "AQG39UPJMK9WSNPWVUMUAW3WCUHNAQSY4JAXEPRZ8AMC4DKS89N3OWNYTFVWVMI8DRVFSOS+NH+MWS+9D/4LXRF9MU3F33J/B9Q\n";
        String p = "" +
                "+AQ89V+MA89IT9IS9AS9\n" +
                "RUMI+A8NAUI+M8439WIT\n" +
                "+A8UY+A89+S89B+A8UY+\n" +
                "FU+S89GI9TIVE+N89RUN\n" +
                "IN+S89NO9CENCE+N89FR\n" +
                "MOR+S89TAL+N89DEADLY\n";




        String[] cc = c.split("\n");
        String[] pp = p.split("\n");

        byte[][] plainArray = new byte[pp.length][];
        byte[][] cipherArray = new byte[cc.length][];
        for (int i = 0; i < pp.length; i++) {
            plainArray[i] = Alphabet.fromStringBritish(pp[i]);
            cipherArray[i] = Alphabet.fromStringBritish(cc[i]);
        }
        /*
        for (int i = 0; i < 10; i++) {
            for (byte s = 0; s < 32; s++) {
                plainArray[9][i] = s;
                ArrayList<String> v = TestPerm.findValidSwitchesAB_D(plainArray, cipherArray, false);
                if (!v.isEmpty()) {

                    break;
                }
            }
        }
        */
        CribAttack.solve(Model.T52D, cipherArray, plainArray, null, false, true);
        return plainArray[0].length;
    }
    static int nils4() {

        String c =  "ZFIMOC9CINROJAPCGNTSCLPYRUAAXQ93APUFJT/+PYKSAZDADQVUWPVFH8/O3ANYUACKG8HCNQ8EQS4TGZ+NPJ/SSTDR9G4B/LPH3LJ9D9C9DWO8ZJRCXRNKWG+FSAYVVYH/VJRI+4BEAOAT84VLX3CPMVYC9\n" +
                "JQ9VANUC9MGP4IBDFX9M+B/JPOW3BGDF3N/QLRGPCWQ3M9GOTWS/CNE3X/ZKIELNTHY4SFP8VMFLXOGBRW+FDIG8POY49BPENNEWYIBRYLEQ9WOGV4CR\n" +
                "WH3CO8MJBNOPG8AV3PIPATXVK3NJFC3WPSAO4SGPCB+QPMFG8/S3NXWCUJ/SFEP+UGAU+RIGFDW4RSDR9AQRBZMAHBDJUG+DYXAAG8F8YLQCWW3TPNZYHR+RR8VB+CQJKKTMXNYL/NUWBOF\n" +
                "+ZIMHDDJZAEKN+DQ8WYRG4RZF8TXEESFVWWFYOU494UEA9A3DQVUMGOLB9USEG+ERKNX9HYX4TJ4MWDVDPRPKWMUILDX8/W/AKYZK9FSWJU\n" +
                "FOAVJALJZM8Y8IBD9VSOW/PZ+MMPXLKQYKVCVXHU/+4QA4KOUNK4BSPAIJ/P3GQZNQUVHRQ8IBJVXBGBRWAFNPMAUMDCRVHGRUYAKCIFRC4+Q9YI+RKNK4TDQIZDU3HELJXT+9YVO4BOVM\n" +
                "KK3NJ9MKOABKN+PKGRZZOMNORZBS4+WB3/+AZXL/ZWHKKNXH3PHRTSPAIEHGEIMDKV3MGBXPQAKXLRFZBJWZDZBMNJCPUUTS/NG/M/KLJXVI/UKD+38WTKQAJLLIRFSCUWC8ZLY/WPZPWWR";



        String p = "" +
                "RE9HEARS+S89AL+\n" +
                "PRI9ME+S89VAL+N\n" +
                "CORNELIA9SCARCE\n" +
                "IN9HU9MAN+S89I9\n" +
                "THE9OCEAN9OLD+N\n" +
                "HARMODIUS+N89I+";

        String[] cc = c.split("\n");
        String[] pp = p.split("\n");
        byte[][] plainArray = new byte[pp.length][];
        byte[][] cipherArray = new byte[cc.length][];
        for (int i = 0; i < pp.length; i++) {
            plainArray[i] = Alphabet.fromStringBritish(pp[i]);
            cipherArray[i] = Alphabet.fromStringBritish(cc[i]);
        }
        /*
        for (int i = 0; i < 10; i++) {
            for (byte s = 0; s < 32; s++) {
                plainArray[9][i] = s;
                ArrayList<String> v = TestPerm.findValidSwitchesAB_D(plainArray, cipherArray, false);
                if (!v.isEmpty()) {

                    break;
                }
            }
        }
        */
        CribAttack.solve(Model.T52E, cipherArray, plainArray, "9:5:V:7:II:IV:3:I:III:1", false, true);
        return plainArray[0].length;
    }
    static int nils6() {

/*
Key Match:      0 (False:  7,596) IC: 0.066015 Steps:               0 1011111110 0111101110: T52D 01:47:63:26:63:52:55:44:25:25 I:2-4:1-5:IV:6-8:V:7-10:3-9:II:III KTF
IC:   0.066015
Freq: 0.066015
In:   RES5PI5RA4S35TION4N3
In:   WL4Y2MVB3KYGOGMQNBWOZKVSXNS5QYWB11QCDOY2EPZB35AAPCBJ6LHKLEZ1RJX4UANKTFCJ2LBO3QXEFCVNA5JXVK6BCF3XVVWB13QDAEEW
Out:  WL4Y2MVB3KYGOGMQNBWOZKVSXNS5QYWB11QCDOY2EPZB35AAPCBJ6LHKLEZ1RJX4UANKTFCJ2LBO3QXEFCVNA5JXVK6BCF3XVVWB13QDAEEW
Out:  RES5PI5RA4S35TION4N35ACT5OF5BREATHING4M3215ZE4S35NITH4N35POINT5IN5THE5HEAVENS5DIRECTLY5OVER5HEAD444OOYOUY333
.Out:  RES PI RA<'> TION<,> ACT OF BREATHING<.>   ZE<'> NITH<,> POINT IN THE HEAVENS DIRECTLY OVER HEAD<<<996976>>>

 */

        String c =  "WL+Y4MVB8KYGOGMQNBWOZKVSXNS9QYWB33QCDOY4EPZB89AAPCBJ/LHKLEZ3RJX+UANKTFCJ4LBO8QXEFCVNA9JXVK/BCF8XVVWB38QDAEEW";


        String p = "RES9PI9RA+S89TION+N8";

        String[] cc = c.split("\n");
        String[] pp = p.split("\n");
        byte[][] plainArray = new byte[pp.length][];
        byte[][] cipherArray = new byte[cc.length][];
        for (int i = 0; i < pp.length; i++) {
            plainArray[i] = Alphabet.fromStringBritish(pp[i]);
            cipherArray[i] = Alphabet.fromStringBritish(cc[i]);
        }

        CribAttack.solve(Model.T52D, cipherArray, plainArray, "I:2-4:1-5:IV:6-8:V:7-10:3-9:II:III", true, true);
        return plainArray[0].length;
    }
    static int nils7() {

        /*
        Key Match:      0 (False:      0) IC: 0.063563 Steps:  11,837,516,856  : T52AB 24:60:64:11:20:61:25:46:30:43 4-6:5-8:7-10:2-9:1-3:IV:V:I:III:II
IC:   0.063563
Freq: 0.063563
In:   THE5CACTI4KYIL35ON5THE5MOUNTAINS5SMOKE4N3215E4S3EN
In:   ZMRXCXEZBOOVYMZZNSQLMDB2C5VZMQ5RBKHXGNDCEQZNRYZV63AMSIQANKXLHNNJTQ1NLMIY3NRJMSNAHWTHGNNE6Q6X
Out:  ZMRXCXEZBOOVYMZZNSQLMDB2C5VZMQ5RBKHXGNDCEQZNRYZV63AMSIQANKXLHNNJTQ1NLMIY3NRJMSNAHWTHGNNE6Q6X
Out:  THE5CACTI4KYIL35ON5THE5MOUNTAINS5SMOKE4N3215E4S3EN5NOW5THE5FORTRESS5IS5IN5FLAMES444YYUWQY333
Out:  THE CACTI<(68)> ON THE MOUNTAINS SMOKE<,>   E<'>EN NOW THE FORTRESS IS IN FLAMES<<<667216>>>
         */


        String c =  "ZMRXCXEZBOOVYMZZNSQLMDB4C9VZMQ9RBKHXGNDCEQZNRYZV/8AMSIQANKXLHNNJTQ3NLMIY8NRJMSNAHWTHGNNE/Q/X";

        String p = "THE9CACTI+KYIL89ON9THE9MOUNTAINS9SMOKE+N8439E+S8EN";

        String[] cc = c.split("\n");
        String[] pp = p.split("\n");
        byte[][] plainArray = new byte[pp.length][];
        byte[][] cipherArray = new byte[cc.length][];
        for (int i = 0; i < pp.length; i++) {
            plainArray[i] = Alphabet.fromStringBritish(pp[i]);
            cipherArray[i] = Alphabet.fromStringBritish(cc[i]);
        }

        CribAttack.solve(Model.T52AB, cipherArray, plainArray, null, false, true);
        return plainArray[0].length;
    }
    //ELDER+M89+A8SAMBUCUS9NIGRA+MA89BERRIES+M8439PRIVET
    static int nils9() {

/*
Key Match:      0 (False: 520,881) IC: 0.056293 Steps:               0 111111110 011010110: T52E 29:08:47:25:05:32:30:19:08:09 9:5:I:IV:II:V:3:III:1:7
IC:   0.056293
Freq: 0.056293
In:   A4MA35BERRIES4M3215PRIVET
In:   XSKYZOTPMMTL2YQHQQ65BNVUHXP1FBQ13WDZF12ANHEWHIMHPXAN3A6LSPS2RV3WNWBVIQ2ZBNID4E6ZO5KG5SAASDXBTAX646B3VGEXG334ZQOLMIL4MLIEEZXRBYQWPDHUFJMKYZ1JFNLLEYAULE15LNGU3ERP42JWF6M55L2GJT6LVRIWLKASEOR1MI2XZNEJOK32LPDTACC4YNTXEKSYOTSJJDOO6XEYUBG1BMCKUMJVUEGALNKXQCHWBKKS35CBNPGNYJAAKWWXHEMDLE4RENGKCZOQBTLBTQFU5HA6BWRVHISYTANRAPZNF2AH4T6SQUX6JT4M2PUGTHCFV5ILM5G3CT6SZL6DEMWWXLROS4EXDERY5F65BYIZLE56WJHXKJKJTZE5QIA1BIGFIPF4OED6LW4RFDWPBFGEW42CAYRHK2LIIKQFBH55VZXAPBWXUE6TMHBY2FUP53LKCSLKYUDMV6BCNCUDPEY4E4MZMUB4A4CJOJKGFQHAKCOBCTFMGJJRF6DISZ2S4KTJQTCIK3E4NGEQGJKLAP63IE133NNTYRFW2BSVXQI1VAKXAALMH35I21NZ54UY1OOIMQJQ5JU52VSB1DP5WAQQOWKMOI5AOZVGBNFD2TE4EMQ5XMOVJKIWBHEG1TMKRODNWQ64URRHDTXCFXX25FCFPRNPIDN1XY2HOMWXOAPRWNVJ5XDSJHAWQHDH1MUHQJWBFBHYWW6QBQEDUXYUSDD6E5LHTGPKOPE6RHDDWVIV4EY2DACIPE6NWZDA4W5XX54PHYSWJ2WCAOMRKSNSYB5L1ZQSJBQSF4MY4GYSYJNGJLZDJC2CW15YFBSJTREOTCHLCCSO6VORAXQOORVWIQWNFUUEJBT4BGYJBFE1BVOLERMDVUN3TM5ANJFBQEZ1Q3U5KXJFSHDEXCM1YUJNKOAFJLUQXTPYZ6DM1GL1OO224RJGZCMHQSPGHRBY3TZLSNPBVRDTTGSWPAIN1S313JDKD5E5AX4YD5GYMQT2YXSDVFWTSWHQ11ZS2HA6SIP5NEXIR5I3WTQVBRVKFKLFHH1IBB5GTM6YDVNMH4JU5ME5OXA6COMWNEWJLSKOAAWLKUEEPC3TTZMVL6XAYD61M4B6PK2JX3QK4VR4IXUWYHEZGWD466U6JMSPE4OZTFSFDAAUQP1QY4NJ1PTDPY51ON3SGVUHIDJKBLSOZYLWPQEBXBX6C154QGHBZ3KXZN1JPUXNGK4UYL6GDO1G4HXX6YK26MMXWCXBM5XFWS55X34JNPGAIKSKP5DGCKQDKB2SBPY6YUPUXWAXOUC4TMCCOMSZVGNHMOZ512XAKGAKDC4KFLJ4KOETSXHKZOYAORDWCRFTZHFZRHAZS4NAL1GDKK3DGRMBABRIQRR3LWM225QWOARC5M3W2GUD2TVJRGTW4ELQ2Z1LV2OQXJU5G4XFCIA2YFWKWIV3J64EXKOV3STXVX3PEAEUGV4MJ1C1PECDRXZFEOE1WWOZ22P44FWXQP5SB5U5XS6MZDESNGO2TV5UFSDJ3SGUFMWHEOZU3OJVQVO512ZFBR42Y4KLJKEJEODDYPJGWCEHCVEDR2G54YR5HXA1QDVKHTAHXCB6EHKVPI1SAKNYFNTROF26HYBQIV5VWEZ2JSPTKDCFTRVXJJD4QCO3FIGGRKOMHY1NL6IUUGMVXV2YRXKVM6HIY6FL4APR633D22JELWHXKTAZ3YS36YXIEQHTDKJWC3KCTXT13Z3UKDBBT1F6N51XLAALYCMTXGAB3624LVM1GNYJ1GZ3ATHOT31YPYDFAWTKYOHUSMZKUGQLF21OZYLYI3XOWTRMEG5PLCLBCPNXOHRQM1F4FB5VSOAD2WIGX3K1R1MSGBAXCEY3GK4FG4G1SM22WYDMLLYWZ42BRHFSKA2NUQ1IGGH1WHLKYKO5YURNJUWI4DVZWCNQIGBVHDQTSDQD1MN5GC42UYOUHKEOQ2FF1ONRDMRA3DJ2PARTED5N2EEVC3EUEMB24T66UJQ2TQPTPWE1QJCCGQURM3PNL6RXJ3H6OPFXBOC15OANP4GGMF5FRM3Z62SVGSSAZNBR1I6OV1NHE6U32HU5MT5TFP5PIMYHTWEKTLCBJ6MLPULERILULE54WSZTEHGIQDCY656KGEFBXBVF2EZYSQESTCV1BLSMRJUZ1MTS6RDIIIBS2STAKGMK1ICTAQFPKPXOODRPF2HXZMYK6UUH3H6JH6DWS3YB6FKO3B1NKAGJ3VQJMN2FHI1KKZIUHLU5HRRP2K4P55SJOMWVR1BHA4ZA53HOJJP1PIZKED5EFZ2XU3A4ES5JVRYL5GFGDOAG5YMJ2RQUHLK45GSQ5OGP2Q1UO1VEIWULIKJELUE2BEH3XZC5E12P6D5YIYKB3RESTLY4OPKN6M3L2GL4O54OV42CLE3I4NP3DLFVVT42QZXRXBQQBP3VT3EFDGBDUBLSLHSKCOOUCS4VIDAPH5JM6Z6TN5PAEVQOFEPSR65K5SS6FIOILFX352F3BZDFVQ1B31D2KCRSQGDYFWJ32LUGIB5PL6BZYOOV5P5KPPXMD123BMZ2AQMAMDH46J4N2LDMAT4T63YEDISOUZMUWKHHOPFL2ZF2JV5QCLI16GZF33L22WOIEHRABKPOFZQ1HVD1OVRCNHPEP4Y6MCJ61WH4OFDQBBDHIXUITGB1152Z3QNQKGRYPFWWZ4B4SX5HTDZJYT5PK66IJDBLVBXVH6SBSJDJUU6PSIZYA3BD3IUOHRCA2XGX4O2ZQT6PVOLURTU1XVZCJ1BCCERCJ5CAXZZJJESGCECFDGTAMVYFVN3FDI3USXABMGUOHLGC4CK3IVEBC5M4IFJU23JJ16QXX6UH3UQWEOIXVECXCMYA5NIRSXNRNYQL1HEV4WBKLDOESNT5KIEB65BW2ICA1LC1ILERKI6KIQ6AHZ6GETJ6XOUBOIRAODTCCFRBNMNLST5C64JDZ2RRMFONAYKCUVLBBS1HWYR5EQQH4PH433HOUBJHBFYXEHMZBET5DPWLB2B1NJEZC3FNJ11LJO16T4HNNRQD5TJS22RNSBY
Out:  XSKYZOTPMMTL2YQHQQ65BNVUHXP1FBQ13WDZF12ANHEWHIMHPXAN3A6LSPS2RV3WNWBVIQ2ZBNID4E6ZO5KG5SAASDXBTAX646B3VGEXG334ZQOLMIL4MLIEEZXRBYQWPDHUFJMKYZ1JFNLLEYAULE15LNGU3ERP42JWF6M55L2GJT6LVRIWLKASEOR1MI2XZNEJOK32LPDTACC4YNTXEKSYOTSJJDOO6XEYUBG1BMCKUMJVUEGALNKXQCHWBKKS35CBNPGNYJAAKWWXHEMDLE4RENGKCZOQBTLBTQFU5HA6BWRVHISYTANRAPZNF2AH4T6SQUX6JT4M2PUGTHCFV5ILM5G3CT6SZL6DEMWWXLROS4EXDERY5F65BYIZLE56WJHXKJKJTZE5QIA1BIGFIPF4OED6LW4RFDWPBFGEW42CAYRHK2LIIKQFBH55VZXAPBWXUE6TMHBY2FUP53LKCSLKYUDMV6BCNCUDPEY4E4MZMUB4A4CJOJKGFQHAKCOBCTFMGJJRF6DISZ2S4KTJQTCIK3E4NGEQGJKLAP63IE133NNTYRFW2BSVXQI1VAKXAALMH35I21NZ54UY1OOIMQJQ5JU52VSB1DP5WAQQOWKMOI5AOZVGBNFD2TE4EMQ5XMOVJKIWBHEG1TMKRODNWQ64URRHDTXCFXX25FCFPRNPIDN1XY2HOMWXOAPRWNVJ5XDSJHAWQHDH1MUHQJWBFBHYWW6QBQEDUXYUSDD6E5LHTGPKOPE6RHDDWVIV4EY2DACIPE6NWZDA4W5XX54PHYSWJ2WCAOMRKSNSYB5L1ZQSJBQSF4MY4GYSYJNGJLZDJC2CW15YFBSJTREOTCHLCCSO6VORAXQOORVWIQWNFUUEJBT4BGYJBFE1BVOLERMDVUN3TM5ANJFBQEZ1Q3U5KXJFSHDEXCM1YUJNKOAFJLUQXTPYZ6DM1GL1OO224RJGZCMHQSPGHRBY3TZLSNPBVRDTTGSWPAIN1S313JDKD5E5AX4YD5GYMQT2YXSDVFWTSWHQ11ZS2HA6SIP5NEXIR5I3WTQVBRVKFKLFHH1IBB5GTM6YDVNMH4JU5ME5OXA6COMWNEWJLSKOAAWLKUEEPC3TTZMVL6XAYD61M4B6PK2JX3QK4VR4IXUWYHEZGWD466U6JMSPE4OZTFSFDAAUQP1QY4NJ1PTDPY51ON3SGVUHIDJKBLSOZYLWPQEBXBX6C154QGHBZ3KXZN1JPUXNGK4UYL6GDO1G4HXX6YK26MMXWCXBM5XFWS55X34JNPGAIKSKP5DGCKQDKB2SBPY6YUPUXWAXOUC4TMCCOMSZVGNHMOZ512XAKGAKDC4KFLJ4KOETSXHKZOYAORDWCRFTZHFZRHAZS4NAL1GDKK3DGRMBABRIQRR3LWM225QWOARC5M3W2GUD2TVJRGTW4ELQ2Z1LV2OQXJU5G4XFCIA2YFWKWIV3J64EXKOV3STXVX3PEAEUGV4MJ1C1PECDRXZFEOE1WWOZ22P44FWXQP5SB5U5XS6MZDESNGO2TV5UFSDJ3SGUFMWHEOZU3OJVQVO512ZFBR42Y4KLJKEJEODDYPJGWCEHCVEDR2G54YR5HXA1QDVKHTAHXCB6EHKVPI1SAKNYFNTROF26HYBQIV5VWEZ2JSPTKDCFTRVXJJD4QCO3FIGGRKOMHY1NL6IUUGMVXV2YRXKVM6HIY6FL4APR633D22JELWHXKTAZ3YS36YXIEQHTDKJWC3KCTXT13Z3UKDBBT1F6N51XLAALYCMTXGAB3624LVM1GNYJ1GZ3ATHOT31YPYDFAWTKYOHUSMZKUGQLF21OZYLYI3XOWTRMEG5PLCLBCPNXOHRQM1F4FB5VSOAD2WIGX3K1R1MSGBAXCEY3GK4FG4G1SM22WYDMLLYWZ42BRHFSKA2NUQ1IGGH1WHLKYKO5YURNJUWI4DVZWCNQIGBVHDQTSDQD1MN5GC42UYOUHKEOQ2FF1ONRDMRA3DJ2PARTED5N2EEVC3EUEMB24T66UJQ2TQPTPWE1QJCCGQURM3PNL6RXJ3H6OPFXBOC15OANP4GGMF5FRM3Z62SVGSSAZNBR1I6OV1NHE6U32HU5MT5TFP5PIMYHTWEKTLCBJ6MLPULERILULE54WSZTEHGIQDCY656KGEFBXBVF2EZYSQESTCV1BLSMRJUZ1MTS6RDIIIBS2STAKGMK1ICTAQFPKPXOODRPF2HXZMYK6UUH3H6JH6DWS3YB6FKO3B1NKAGJ3VQJMN2FHI1KKZIUHLU5HRRP2K4P55SJOMWVR1BHA4ZA53HOJJP1PIZKED5EFZ2XU3A4ES5JVRYL5GFGDOAG5YMJ2RQUHLK45GSQ5OGP2Q1UO1VEIWULIKJELUE2BEH3XZC5E12P6D5YIYKB3RESTLY4OPKN6M3L2GL4O54OV42CLE3I4NP3DLFVVT42QZXRXBQQBP3VT3EFDGBDUBLSLHSKCOOUCS4VIDAPH5JM6Z6TN5PAEVQOFEPSR65K5SS6FIOILFX352F3BZDFVQ1B31D2KCRSQGDYFWJ32LUGIB5PL6BZYOOV5P5KPPXMD123BMZ2AQMAMDH46J4N2LDMAT4T63YEDISOUZMUWKHHOPFL2ZF2JV5QCLI16GZF33L22WOIEHRABKPOFZQ1HVD1OVRCNHPEP4Y6MCJ61WH4OFDQBBDHIXUITGB1152Z3QNQKGRYPFWWZ4B4SX5HTDZJYT5PK66IJDBLVBXVH6SBSJDJUU6PSIZYA3BD3IUOHRCA2XGX4O2ZQT6PVOLURTU1XVZCJ1BCCERCJ5CAXZZJJESGCECFDGTAMVYFVN3FDI3USXABMGUOHLGC4CK3IVEBC5M4IFJU23JJ16QXX6UH3UQWEOIXVECXCMYA5NIRSXNRNYQL1HEV4WBKLDOESNT5KIEB65BW2ICA1LC1ILERKI6KIQ6AHZ6GETJ6XOUBOIRAODTCCFRBNMNLST5C64JDZ2RRMFONAYKCUVLBBS1HWYR5EQQH4PH433HOUBJHBFYXEHMZBET5DPWLB2B1NJEZC3FNJ11LJO16T4HNNRQD5TJS22RNSBY
Out:  A4MA35BERRIES4M3215PRIVET4M354A3LIGUSTRUM5VULGARE4MA35BERRIES4N35WITH5ALUM5AND5SALT4M32154KWL3SLOE4M354A3PRUNUS5COMMUNIS4MA35FRUIT4M3215RED5BEARBERRY4M354A3ARCTOSTAPHYLOS5UVA4A3URSI4MA3215DOGS5MERCURY4M354A3MERCURIALIS5PERENNIS4MA3215YELLOW5IRIS4M354A3IRIS5PSEUDACORUS4MA35ROOT4M3215DEVIL4S3S5BIT4M354A3SCABIOSA5SUCCISA4MA35LEAVES5PREPARED5LIKE5WOAD4M321PLANTS5WHICH5DYE5YELLOW4MAA3215WELD4M354A3RESEDA5LUTEOLA4MA3215MEADOW5RUE4M354A3THALICTRUM5FLAVUM4MA35ROOTS4M3215MARSH5MARIGOLD4M354A3CALTHA5PALUSTRIS4MA35FLOWERS4M3215S4M35JOHN4S3S5WORT4M354A3HYPERICUM5PERFORATUM4MA3215HEATH4M354A3ERICA5VULGARIS4MA35WITH5ALUM4M3215SPINDLE5TREE4M354A3EUONYMUS5EUROPA4X3US4MA3215BUCKTHORN4M354A3RHAMNUS5FRANGULA4A35AND54A3R4M35CATHARTICA4MA35BERRIES5AND215BARK4M32154KEL3DYER4S3S5GREENWOOD4M354A3GENISTA5TINCTORIA4MA35YOUNG5SHOOTS5AND5LEAVES4M3215KIDNEY5VETCH4M354A3ANTHYLLIS5VULNARARIA4MA3215MARSH5POTENTIL4M354A3POTENTILLA5COMARUM4MA3215LING4M354A3CALLUNA5VULGARIS4MA3215YELLOW5CENTAURY4M354A3CHLORA5PERFOLIATA4MA3215HORNBEAM4M354A3CARPINUS5BETULUS4MA35BARK4M3215HEDGE5STACHYS4M354A3STACHYS5PALUSTRIS4MA3215POLYGONUM5PERSECARIA4M3215POLYGONUM5HYDROPIPER4M3215HOP4M354A3HUMULUS5LUPULUS4MA3215STINKING5WILLY4N35OR5RAGWEED4M354A3SENECIO5JACOBA4X3A4MA3215YELLOW5CAMOMILE4M354A3ANTHEMIS5TINCTORIA4MA3215COMMON5DOCK4M354A3RUMEX5OBTUSIFOLIUS4MA35ROOT4M32154KRL3SAWWORT4M32154A3SERRATULA5TINCTORIA4MA3215GORSE4M354A3ULEX5EUROPA4X3US4MA35BARK4N35FLOWERS5AND5YOUNG5SHOOTS4M3215BROOM4M354A3SAROTHAMNUS5SCOPARIUS4MA3215BRACKEN4M354A3PTERIS5AQUILINA4MA35ROOTS4M35ALSO5YOUNG5TOPS4M3215WAY4A3FARING5TREE4M354A3VIBURNUM5LANTANA4MA35LEAVES4N35WITH5ALUM4M3215BRAMBLE4M354A3RUBUS5FRUCTICOSUS4MA3215NETTLE4M354A3URTICA4MA35WITH5ALUM4M3215BOG5MYRTLE5OR5SWEET5GALE4M354A3MYRICA5GALE4MA3215TEASEL4M354A3DIPSACUS5SYLVESTRIS4MA3215SUNDEW4M354A3DROSERA4MA3215BARBERRY4M354A3BERBERIS5VULGARIS4MA35STEM5AND5ROOT4M3215BOG5ASPHODEL4M354A3NARTHECIUM5OSSIFRAGUM4MA3215AGRIMONY4M354A3AGRIMONIA5EUPATORIA4MA3215YELLOW5CORYDAL4M354A3CORYDALIS5LUTEA4MA3215PRIVET4M354A3LIGUSTRUM5VULGARE4MA35LEAVES4M3215CRAB5APPLE4M354A3PYRUS5MALUS4MA35FRESH5INNER5BARK4M3215ASH4M354A3FRAXINUS5EXCELSIOR4MA35FRESH5INNER5BARK4M3215PEAR4M35LEAVES4M3215POPLAR4M35LEAVES4M3215PLUM4M35LEAVES4M3215BIRCH4M35LEAVES4M32154KTL3WILLOW4M35LEAVES4M321PLANTS5WHICH5DYE5GREEN4MAA3215PRIVET4M354A3LIGUSTRUM5VULGARE4MA35BERRIES5AND5LEAVES4N35WITH215ALUM4M3215FLOWERING5REED4M354A3PHRAGMITES5COMMUNIS4MA35FLOWERING5TOPS4N3215WITH5COPPERAS4M3215ELDER4M354A3SAMBUCUS5NIGRA4MA35LEAVES5WITH5ALUM4M3215NETTLE4M354A3URTICA5DIOICA4A35AND54A3U4M35URENS4AM3215LILY5OF5THE5VALLEY4M354A3CONVALARIA5MAJALIS4MA35LEAVES4M3215LARCH4M35BARK4N35WITH5ALUM4M321PLANTS5WHICH5DYE5BROWN4MAA3215WHORTLEBERRY4M354A3VACCINIUM5MYRTILLUS4MA35YOUNG5SHOOTS4N35WITH215NUT5GALLS4M3215LARCH4M35PINE5NEEDLES4N35COLLECTED5IN5AUTUMN4M3215WALNUT4M35ROOT5AND5GREEN5HUSKS5OF5NUT4M3215WATER5LILY4M354A3NYMPHA4X3A5ALBA4MA35ROOT4M3215ALDER4M354A3ALNUS5GLUTINOSA4MA35BARK4M3215BIRCH4M354A3BETULA5ALBA4MA35BARK4M3215OAK4M354A3QUERCUS5ROHUR4MA35BARK4M3215RED5CURRANTS4N35WITH5ALUM444ROOIRT333
Out:  A<.-> BERRIES<.>   PRIVET<.> <->LIGUSTRUM VULGARE<.-> BERRIES<,> WITH ALUM AND SALT<.>   <(2)>SLÖe<.> <->PRUNUS COMMUNIS<.-> FRUIT<.>   RED BEARBERRY<.> <->ARCTOSTAPHYLOS UVA<->URSI<.->   DOGS MERCURY<.> <->MERCURIALIS PERENNIS<.->   YELLOW IRIS<.> <->IRIS PSEUDACORUS<.-> ROOT<.>   DEVIL<'>S BIT<.> <->SCABIOSA SUCCISA<.-> LEAVES PREPARED LIKE WOAD<.>  PLANTS WHICH DYE YELLOW<.-->   WELD<.> <->RESEDA LUTEOLA<.->   MEADOW RÜe<.> <->THALICTRUM FLAVUM<.-> ROOTS<.>   MARSH MARIGOLD<.> <->CALTHA PALUSTRIS<.-> FLOWERS<.>   S<.> JOHN<'>S WORT<.> <->HYPERICUM PERFORATUM<.->   HEATH<.> <->ERICA VULGARIS<.-> WITH ALUM<.>   SPINDLE TREE<.> <->EUONYMUS EUROPA</>US<.->   BUCKTHORN<.> <->RHAMNUS FRANGULA<-> AND <->R<.> CATHARTICA<.-> BERRIES AND   BARK<.>   <(3)>DYER<'>S GREENWOOD<.> <->GENISTA TINCTORIA<.-> YOUNG SHOOTS AND LEAVES<.>   KIDNEY VETCH<.> <->ANTHYLLIS VULNARARIA<.->   MARSH POTENTIL<.> <->POTENTILLA COMARUM<.->   LING<.> <->CALLUNA VULGARIS<.->   YELLOW CENTAURY<.> <->CHLORA PERFOLIATA<.->   HORNBEAM<.> <->CARPINUS BETULUS<.-> BARK<.>   HEDGE STACHYS<.> <->STACHYS PALUSTRIS<.->   POLYGONUM PERSECARIA<.>   POLYGONUM HYDROPIPER<.>   HOP<.> <->HUMULUS LUPULUS<.->   STINKING WILLY<,> OR RAGWEED<.> <->SENECIO JACOBA</>A<.->   YELLOW CAMOMILE<.> <->ANTHEMIS TINCTORIA<.->   COMMON DOCK<.> <->RUMEX OBTUSIFOLIUS<.-> ROOT<.>   <(4)>SAWWORT<.>   <->SERRATULA TINCTORIA<.->   GORSE<.> <->ULEX EUROPA</>US<.-> BARK<,> FLOWERS AND YOUNG SHOOTS<.>   BROOM<.> <->SAROTHAMNUS SCOPARIUS<.->   BRACKEN<.> <->PTERIS AQUILINA<.-> ROOTS<.> ALSO YOUNG TOPS<.>   WAY<->FARING TREE<.> <->VIBURNUM LANTANA<.-> LEAVES<,> WITH ALUM<.>   BRAMBLE<.> <->RUBUS FRUCTICOSUS<.->   NETTLE<.> <->URTICA<.-> WITH ALUM<.>   BOG MYRTLE OR SWEET GALE<.> <->MYRICA GALE<.->   TEASEL<.> <->DIPSACUS SYLVESTRIS<.->   SUNDEW<.> <->DROSERA<.->   BARBERRY<.> <->BERBERIS VULGARIS<.-> STEM AND ROOT<.>   BOG ASPHODEL<.> <->NARTHECIUM OSSIFRAGUM<.->   AGRIMONY<.> <->AGRIMONIA EUPATORIA<.->   YELLOW CORYDAL<.> <->CORYDALIS LUTEA<.->   PRIVET<.> <->LIGUSTRUM VULGARE<.-> LEAVES<.>   CRAB APPLE<.> <->PYRUS MALUS<.-> FRESH INNER BARK<.>   ASH<.> <->FRAXINUS EXCELSIOR<.-> FRESH INNER BARK<.>   PEAR<.> LEAVES<.>   POPLAR<.> LEAVES<.>   PLUM<.> LEAVES<.>   BIRCH<.> LEAVES<.>   <(5)>WILLOW<.> LEAVES<.>  PLANTS WHICH DYE GREEN<.-->   PRIVET<.> <->LIGUSTRUM VULGARE<.-> BERRIES AND LEAVES<,> WITH   ALUM<.>   FLOWERING REED<.> <->PHRAGMITES COMMUNIS<.-> FLOWERING TOPS<,>   WITH COPPERAS<.>   ELDER<.> <->SAMBUCUS NIGRA<.-> LEAVES WITH ALUM<.>   NETTLE<.> <->URTICA DIOICA<-> AND <->U<.> URENS<-.>   LILY OF THE VALLEY<.> <->CONVALARIA MAJALIS<.-> LEAVES<.>   LARCH<.> BARK<,> WITH ALUM<.>  PLANTS WHICH DYE BROWN<.-->   WHORTLEBERRY<.> <->VACCINIUM MYRTILLUS<.-> YOUNG SHOOTS<,> WITH   NUT GALLS<.>   LARCH<.> PINE NEEDLES<,> COLLECTED IN AUTUMN<.>   WALNUT<.> ROOT AND GREEN HUSKS OF NUT<.>   WATER LILY<.> <->NYMPHA</>A ALBA<.-> ROOT<.>   ALDER<.> <->ALNUS GLUTINOSA<.-> BARK<.>   BIRCH<.> <->BETULA ALBA<.-> BARK<.>   OAK<.> <->QÜeRCUS ROHUR<.-> BARK<.>   RED CURRANTS<,> WITH ALUM<<<499845>>>

 */

        String c =  "+JNTHTTSL/T/P3DVOJISVTQO4XSKYZOTPMMTL4YQHQQ/9BNVUHXP3FBQ38WDZF34ANHEWHIMHPXAN8A/LSPS4RV8WNWBVIQ4ZBNID+E/ZO9KG9SAASDXBTAX/+/B8VGEXG88+ZQOLMIL+MLIEEZXRBYQWPDHUFJMKYZ3JFNLLEYAULE39LNGU8ERP+4JWF/M99L4GJT/LVRIWLKASEOR3MI4XZNEJOK84LPDTACC+YNTXEKSYOTSJJDOO/XEYUBG3BMCKUMJVUEGALNKXQCHWBKKS89CBNPGNYJAAKWWXHEMDLE+RENGKCZOQBTLBTQFU9HA/BWRVHISYTANRAPZNF4AH+T/SQUX/JT+M4PUGTHCFV9ILM9G8CT/SZL/DEMWWXLROS+EXDERY9F/9BYIZLE9/WJHXKJKJTZE9QIA3BIGFIPF+OED/LW+RFDWPBFGEW+4CAYRHK4LIIKQFBH99VZXAPBWXUE/TMHBY4FUP98LKCSLKYUDMV/BCNCUDPEY+E+MZMUB+A+CJOJKGFQHAKCOBCTFMGJJRF/DISZ4S+KTJQTCIK8E+NGEQGJKLAP/8IE388NNTYRFW4BSVXQI3VAKXAALMH89I43NZ9+UY3OOIMQJQ9JU94VSB3DP9WAQQOWKMOI9AOZVGBNFD4TE+EMQ9XMOVJKIWBHEG3TMKRODNWQ/+URRHDTXCFXX49FCFPRNPIDN3XY4HOMWXOAPRWNVJ9XDSJHAWQHDH3MUHQJWBFBHYWW/QBQEDUXYUSDD/E9LHTGPKOPE/RHDDWVIV+EY4DACIPE/NWZDA+W9XX9+PHYSWJ4WCAOMRKSNSYB9L3ZQSJBQSF+MY+GYSYJNGJLZDJC4CW39YFBSJTREOTCHLCCSO/VORAXQOORVWIQWNFUUEJBT+BGYJBFE3BVOLERMDVUN8TM9ANJFBQEZ3Q8U9KXJFSHDEXCM3YUJNKOAFJLUQXTPYZ/DM3GL3OO44+RJGZCMHQSPGHRBY8TZLSNPBVRDTTGSWPAIN3S838JDKD9E9AX+YD9GYMQT4YXSDVFWTSWHQ33ZS4HA/SIP9NEXIR9I8WTQVBRVKFKLFHH3IBB9GTM/YDVNMH+JU9ME9OXA/COMWNEWJLSKOAAWLKUEEPC8TTZMVL/XAYD/3M+B/PK4JX8QK+VR+IXUWYHEZGWD+//U/JMSPE+OZTFSFDAAUQP3QY+NJ3PTDPY93ON8SGVUHIDJKBLSOZYLWPQEBXBX/C39+QGHBZ8KXZN3JPUXNGK+UYL/GDO3G+HXX/YK4/MMXWCXBM9XFWS99X8+JNPGAIKSKP9DGCKQDKB4SBPY/YUPUXWAXOUC+TMCCOMSZVGNHMOZ934XAKGAKDC+KFLJ+KOETSXHKZOYAORDWCRFTZHFZRHAZS+NAL3GDKK8DGRMBABRIQRR8LWM449QWOARC9M8W4GUD4TVJRGTW+ELQ4Z3LV4OQXJU9G+XFCIA4YFWKWIV8J/+EXKOV8STXVX8PEAEUGV+MJ3C3PECDRXZFEOE3WWOZ44P++FWXQP9SB9U9XS/MZDESNGO4TV9UFSDJ8SGUFMWHEOZU8OJVQVO934ZFBR+4Y+KLJKEJEODDYPJGWCEHCVEDR4G9+YR9HXA3QDVKHTAHXCB/EHKVPI3SAKNYFNTROF4/HYBQIV9VWEZ4JSPTKDCFTRVXJJD+QCO8FIGGRKOMHY3NL/IUUGMVXV4YRXKVM/HIY/FL+APR/88D44JELWHXKTAZ8YS8/YXIEQHTDKJWC8KCTXT38Z8UKDBBT3F/N93XLAALYCMTXGAB8/4+LVM3GNYJ3GZ8ATHOT83YPYDFAWTKYOHUSMZKUGQLF43OZYLYI8XOWTRMEG9PLCLBCPNXOHRQM3F+FB9VSOAD4WIGX8K3R3MSGBAXCEY8GK+FG+G3SM44WYDMLLYWZ+4BRHFSKA4NUQ3IGGH3WHLKYKO9YURNJUWI+DVZWCNQIGBVHDQTSDQD3MN9GC+4UYOUHKEOQ4FF3ONRDMRA8DJ4PARTED9N4EEVC8EUEMB4+T//UJQ4TQPTPWE3QJCCGQURM8PNL/RXJ8H/OPFXBOC39OANP+GGMF9FRM8Z/4SVGSSAZNBR3I/OV3NHE/U84HU9MT9TFP9PIMYHTWEKTLCBJ/MLPULERILULE9+WSZTEHGIQDCY/9/KGEFBXBVF4EZYSQESTCV3BLSMRJUZ3MTS/RDIIIBS4STAKGMK3ICTAQFPKPXOODRPF4HXZMYK/UUH8H/JH/DWS8YB/FKO8B3NKAGJ8VQJMN4FHI3KKZIUHLU9HRRP4K+P99SJOMWVR3BHA+ZA98HOJJP3PIZKED9EFZ4XU8A+ES9JVRYL9GFGDOAG9YMJ4RQUHLK+9GSQ9OGP4Q3UO3VEIWULIKJELUE4BEH8XZC9E34P/D9YIYKB8RESTLY+OPKN/M8L4GL+O9+OV+4CLE8I+NP8DLFVVT+4QZXRXBQQBP8VT8EFDGBDUBLSLHSKCOOUCS+VIDAPH9JM/Z/TN9PAEVQOFEPSR/9K9SS/FIOILFX894F8BZDFVQ3B83D4KCRSQGDYFWJ84LUGIB9PL/BZYOOV9P9KPPXMD348BMZ4AQMAMDH+/J+N4LDMAT+T/8YEDISOUZMUWKHHOPFL4ZF4JV9QCLI3/GZF88L44WOIEHRABKPOFZQ3HVD3OVRCNHPEP+Y/MCJ/3WH+OFDQBBDHIXUITGB3394Z8QNQKGRYPFWWZ+B+SX9HTDZJYT9PK//IJDBLVBXVH/SBSJDJUU/PSIZYA8BD8IUOHRCA4XGX+O4ZQT/PVOLURTU3XVZCJ3BCCERCJ9CAXZZJJESGCECFDGTAMVYFVN8FDI8USXABMGUOHLGC+CK8IVEBC9M+IFJU48JJ3/QXX/UH8UQWEOIXVECXCMYA9NIRSXNRNYQL3HEV+WBKLDOESNT9KIEB/9BW4ICA3LC3ILERKI/KIQ/AHZ/GETJ/XOUBOIRAODTCCFRBNMNLST9C/+JDZ4RRMFONAYKCUVLBBS3HWYR9EQQH+PH+88HOUBJHBFYXEHMZBET9DPWLB4B3NJEZC8FNJ33LJO3/T+HNNRQD9TJS44RNSBY";
        String p = "ELDER+M89+A8SAMBUCUS9NIGRA+MA89BERRIES+M8439PRIVET";
        int offset = 25;

        c = c.substring(offset);
        p = p.substring(offset);


        String[] cc = c.split("\n");
        String[] pp = p.split("\n");
        byte[][] plainArray = new byte[pp.length][];
        byte[][] cipherArray = new byte[cc.length][];
        for (int i = 0; i < pp.length; i++) {
            plainArray[i] = Alphabet.fromStringBritish(pp[i]);
            cipherArray[i] = Alphabet.fromStringBritish(cc[i]);
            System.out.println(Alphabet.toStringFormatted(plainArray[i], true));
            System.out.println(Alphabet.toStringFormatted(plainArray[i], false));
        }

        CribAttack.solve(Model.T52E, cipherArray, plainArray, "9:5:I:IV:II:V:3:III:1:7", false, true);
        return plainArray[0].length;
    }

    static int andresPlaintextCiphertextStepwise(byte[][] plainArray, byte[][] cipherArray) {
        String plaintextStr = "ABTEILUNGSVERMITLUN!";
        String ciphertextStr = "H5XPDCHJD5A434XQDT!S";
        plainArray[0] = Alphabet.fromString(plaintextStr);
        cipherArray[0] = Alphabet.fromString(ciphertextStr);
        return plainArray[0].length;
    }
    static int jfT52ca3(byte[][] plainArray, byte[][] cipherArray) {
        String plaintextStr = "VON5GENERAL5GUENTHER5VON5KLUGE";
        String ciphertextStr = "8FMY/U4DVZ98KAFUYZUTGDVU4DNCZNIS8WKCQ/O/LGTCRBIGZVGJ9HY9SYFJVY+9LNKRPR4GMVHTJPMIWS4PB3M8VR//GRWEIXKLZ3N/UEBDPST3/AQCXMDBSBAONHWYW9BMYQVQKEXF8OGFEBPMTDXSYD88ZJNYRYJJSB9QJFYGMQT4Q+DC4FHS/FXG3TITKGZNJNZ9HAD/ZYW/FYRQRJNQV3J4L/3FJ4MCM49DHKFP3AC9H3U8+";
        plainArray[0] = Alphabet.fromString(plaintextStr);
        cipherArray[0] = Alphabet.fromStringBritish(ciphertextStr);
        return plainArray[0].length;
    }
    static int jfT52ca4(byte[][] plainArray, byte[][] cipherArray) {
        //"3,V,1,I,II,IV,5,III,7,9"
        String plaintextStr = "VON5VON5KLUGE";
        String ciphertextStr = "TUKE3W3LZCIDK/GOEA9DPWKZBETPTCEIYUIE9MZFXJHZUE4ZALG9EWNL/QYXZAZWVUK/DRTCKV+/L+ZMOICGYW/OJC/LB+9WMBIJS/3WXOD9/VY4B9MGYPSYPSPT/TZ4FYMDXYNHVBQJZAFV3QPEPDQB/49YKNRR";
        plainArray[0] = Alphabet.fromString(plaintextStr);
        cipherArray[0] = Alphabet.fromStringBritish(ciphertextStr);
        return plainArray[0].length;
    }
    static String jfT52debug(byte[][] plainArray, byte[][] cipherArray) {

        String plaintextStr = "/XOKG/UJLTL4UK3OMK/OBKEAODG9SN9C/+PUAMUCNOSZXWC+KDVN9XIWWG+4RDBG3Z9+PQQ3EPPMZXKRVJXXVT/QM83A83IE+MFNVIRI/F8";
        String ciphertextStr = "WHERE9IS9MY9FATHER9A";
        plainArray[0] = Alphabet.fromStringBritish(plaintextStr);
        System.out.printf("%s\n", plaintextStr);
        System.out.printf("IC: %f\n", Stats.computeIc(Alphabet.fromStringBritish(plaintextStr).length, Alphabet.fromStringBritish(plaintextStr)));
        cipherArray[0] = Alphabet.fromStringBritish(ciphertextStr);
        return "II:V:1:5:3:7:I:IV:III:9";
    }
    static int jfT52d2(byte[][] plainArray, byte[][] cipherArray) {
        //"III:1-7:V:2-5:IV:8-10:I:4-6:II:3-9"
        String plaintextStr  = "von5General5von5Guenther5Kluge5an5Hauptquartier".toUpperCase();
        String ciphertextStr = "N+/RUMLU/BXDVMHX98LC3Y34Q/OECH48HG+E99GE+X3ZVR4NYL\n" +
                "GSDVBB+AOKLT9DTO8AXS/8TIHM4ENJEJJUT+CO8UZUXV4WQYDU\n" +
                "XQQI9V3VBKMDFTE49CGQSDIDZVNKIU9MTM9KW+VFE+D3+IHC3L\n" +
                "39+LZVLGIJM8YFY/ABYIIZNBTSKE4CB8AD98MJ+BXTRKR8JQYT\n" +
                "S9LJFTRZ8YMHJE4RHUFF8UPPR8NMHHB+TIY4SXGZLGWR8RVTBH\n" +
                "3943LBFQFJEUXDY94AOO394FHTRFL/3GYASGNAPYOQPMCW8YOX\n" +
                "R8WJAMVQJPHBFVS/VCAZJVXXPLOIS4PVFHK3TF4AIADGFDN+YP\n" +
                "RXHC+ZYOXRH9CIQHFGNHQ9RGOA+O/ERUWCWTBD+PO8MGMVY8CA\n" +
                "QHZTUR3OEMVAP3MR/LH3D4LQVR9EO8HE/A/KPKE/M+WBIANPRI\n" +
                "VMY4LTYFGDJUZSXEZJDE8Q4MTS4IFYJ8HXC3P9JP+JXRDQDUIA\n" +
                "VHM8K/E98ZNDVLSU9V/VTQKDO3/UIPCRLHRL+DWTRLXNKFEDID\n" +
                "LN+VK3PX4WPW84SNONAE8EQDMBBOH+KPTUHKXQBCR43PQUPJBO\n" +
                "ICIELMVHOJELFFEXOVW3BEME+FNI9OP8HYX9TOWLQP94JL8PI+\n" +
                "3J/LM4ZKNHVSBLU+3WHXQJXVKOSZ/HIVYDJAPFMJZ+YFYD/XHR\n" +
                "NHXDFACD8RUFO3KHYEZODVAKCYMPTME4FTN9ORFUHM84CNC+SK\n" +
                "Y9ZRMX4PNMWKWHLQV4G9JEM3+GDNMXYAI8R49MQLO9GS";
        plainArray[0] = Alphabet.fromString(plaintextStr);
        cipherArray[0] = Alphabet.fromStringBritish(ciphertextStr.replaceAll("\n", ""));
        return plainArray[0].length;
    }
    static String jfT52d4(byte[][] plainArray, byte[][] cipherArray) {

        String plaintextStr  = "AN5ROMMEL5VON";
        String ciphertextStr = "DXE4AMVASTLIQHMDYYLAKGTWIXRQYWHLXGXYLOZSFGJOLOBODO\n" +
                "\n" +
                "TD3TALU3CHLZUIBTQPOMXP9XGC3NPMKGQPSCAY8ROLGYUD83R9\n" +
                "\n" +
                "CLQFVSLOQFEVKD+KCIJZ/SQ3HNVXIPDC8HUAW++WH4YWRVWGLH\n" +
                "\n" +
                "SYHBH9Z4SUQA4IXERK4VX+AISQOFKIQOLRA9+HSN94Y+DADDSZ\n" +
                "\n" +
                "R8WJI/UZXRLFT/FE/FQWVT/9/FQWPSKJ3N+YRIYPKFZ9+AG/YL\n" +
                "\n" +
                "4IOMXQN8M3ZKL+YJA4CEUB4DCUXJHQEUGVMAAJ4GN3CANIKMSO\n" +
                "\n" +
                "3OBP+G3KVFHN9RVIGVRE/SG9DL4OILZ8S4SBQ3Z8MVLMLS/V9W\n" +
                "\n" +
                "UI9AUAFCG4+ZNASVUXNLMNREVAMEN+LGOAYMZO9VHF/TY9++8S\n" +
                "\n" +
                "G3XH/RLNKXMMMPPXIEMIXNDCZDPUDROXE8XVJH3VWIB+Y4EQGQ\n" +
                "\n" +
                "XLAXSGRY3S/GIXURVJ4OFV4TV9ZWOW/HHINWZKSAKRF3/NX33D\n" +
                "\n" +
                "K+HKE4STVWBT8LHHCMD+T4C8TGF/K9JEURSPZS/BPTQMCBGLGB\n" +
                "\n" +
                "L4/HEZVXX4SDXJP8DAN9DTJEBQRHX4SB3IAHG9SCCERFBVRIBT\n" +
                "\n" +
                "LYUKLKHRRPVEHYUKZ/WYLYHBP+9IV33RAYHVEERKJYE3IHQKIU\n" +
                "\n" +
                "ZQELKJHPRK+QIBKJLAF8CVCMZ44MPWUN+DINM+Y8YWAQM4DVFK\n" +
                "\n" +
                "AAI+OAH9/MPDWU9YDAG3TPISFES/QY38ORFEF3IM3E3TVY4VCT\n" +
                "\n" +
                "HRB8NUQ//RULXJMHPOCF49+8FJMF3R/D8Q9IRFED4RMQ3OPJIK\n" +
                "\n" +
                "I3UF/SSQW/IEJB/QWFGUEDONOWOTXZNP8SP8ICNYWWUD/FJ4NS\n" +
                "\n" +
                "P/AY/9/8FSU9AF3DPORA89PKI8G+N4YU8CRKXXVWFIGRA9SECY\n" +
                "\n" +
                "FQN8WUGH44K+HJP3+ALHHLIXI8PQSBVWMCG4BD/I4UVLOPLH+K\n" +
                "\n" +
                "ZX3VZWE9SW+RXMBB/83NPX+I8JOQEZ33HDNCRXPTCPP+HQCMFS\n" +
                "\n" +
                "/SCH+JZWKMBDRQCJ/DAZEKOQTHBK+4WEV4CMSNP88DNAT+DWUB\n" +
                "\n" +
                "T3PAOFXPH9BBQE3BNGO/LIWYJ+VQVO48XDJGG";
        plainArray[0] = Alphabet.fromString(plaintextStr);
        cipherArray[0] = Alphabet.fromStringBritish(ciphertextStr.replaceAll("\n", ""));
        return "V:IV:3-8:III:1-6:4-10:I:2-5:II:7-9";
    }

    static String jfT52e3(byte[][] plainArray, byte[][] cipherArray) {

        String plaintextStr  = "De Mr le Ministre Robert Schuman".toUpperCase().replaceAll(" ","5");
        String ciphertextStr = "BHYSD94VGANLDRR8IX44WCDKY3LFHHGZGMNW+44WKFJYGHQ3SK\n" +
                "XTCAVHKAYCLDTPE8QGS3MDLBPVMJG9T8WWTJEEZXN3PPEAZ8BT\n" +
                "UZMKCVSBYVZDKQQGFCBOS++RDJXZ+G/NJMQNNR3BO/E3MIJ9MF\n" +
                "URXKRVMDMBPZREFPAZ/49DEA/HOSSO3//+G8WAZK4E3ETKCJFF\n" +
                "83DCZWB3+9MDDIAKTVTUYMMEM+WUYOSQFPFYT9H4I+SZLC4F3C\n" +
                "XZAVZBIR3XQEAPH+CS8N+D/WJLA4SUAO+KPBBJHGNPHPT9/+LI\n" +
                "VDXR98+YE4BVMELY9/EJQYVCVZOCKGAL3BLFWVY43GWDGHPGLQ\n" +
                "/TZEOISRE8V4PS3KQ3TJCTW+X+SPQWZ9KMKMOBNMNY9/LLQNIQ\n" +
                "TYAPDYRKFAQZC/ZP4XAEP/SYWYERDFOU8LD49AKFGJMUAYIOHW\n" +
                "B/BDWZ94LB9AD/CUICEF9HRA3LJXNULD/PZA8LOJCZCTSXVAUE\n" +
                "DOHHG8FIXABEELJQANAJL4IWQSB/EBX9FDL3YK44DFBQ4OYZXN\n" +
                "A9BTK/DLA/BP4HGQAENKBY+JXABGJB4WLGE3QS49J4KFXEOKXJ\n" +
                "LC+BLT4CPIFN9OMK/TTKXWYJNK4KPD/N3K8HOA+GWINQ8RZZHD\n" +
                "K+VP4ZGZEXKBYABB8UX3GLDMXLB9DNICTQ/MWF9MWP4OO8JN4I\n" +
                "EI8GPN9V/VBSPPQWHVCGE/JV+4Q+VA4KP/BK8UAU8UPHKQ+3UW\n" +
                "AR3JXV8RKMXRTGGSN+9GRJ9Z4UX+AZTNIBTMBOYQH8Q+FPJYKE\n" +
                "K4TCICJ3KYWWUEHK+WP+PJGIY8NYG/FKE344ZOAYVD4S+4NYD/\n" +
                "HG3BUVGPWFQNY98F3GHBX8U9YWXNHZPW+MA/LXMSICQTZA8FJ+\n" +
                "I/NXG+R4VXTYLS+OJJ3Q9TU9T9W93X8IXQ3MTMIYIPHKCY3ZEN\n" +
                "D/R83UVKRHJ8H++NSKCKQLWJPIAB8HW/8KZZ8TFO+VNOJ9M4BG\n" +
                "VYO3TRVTSG8NNSUHWBH9+KD8AAF4CXJGBK8OLFWRMEDBYRZJOU\n" +
                "TDO4JV8ZP3Z8WCY9/338XIEGQEH9QROWGPPWGRZARTWCELVCV8\n" +
                "DORUHZ8PNS/ZBS8HXVVTSR/G8BU4+IJWJ+SPASQC83PJRQOB4G\n" +
                "DZYTGCEFW8S8O8N4XFUFOAHBXSHR8VVN9DRHMMPQFPPY9HYF4F\n" +
                "Q8YLQHHFCDODRMMRYXJ4TQO8RD9/QJH38BSP+3/MYWUEAN33CU\n" +
                "AMP8RB+IU3FUDKJIJOH8A/4JP/AZCFJZBLX3+ENDVXWYY+WEDL\n";
        plainArray[0] = Alphabet.fromString(plaintextStr);
        cipherArray[0] = Alphabet.fromStringBritish(ciphertextStr.replaceAll("\n", ""));
        return "II:5:I:9:V:IV:1:7:III:3";
    }

    static int jf8Generic(String crib, int pos, byte[][] plainArray, byte[][] cipherArray) {
        String ciphertextStr = "FZKXDITGRECMOFUJBSSPYKN23TDDVY5R6O2OYZTPMO3UEBQVWOQKUWCJX4XAO4NAEFNGKBIZGMVTXJWFCMEGVMKABZIZKSTOKNSANEFGDAHQLVIUROZ2YXKUTD5JIZYJPHWPCT6HKQ5MQMH4CHZ6PNYI5N5KUGPM244VJHXA4YPU24EVGMWB66GALZCEPEYGWGERCQPT3KCXYNNVRM246P4ZVZWQG3PP1YZTFJQPYJLOEPCGJZKGKZ53F6PIMLUF6OSRA5X1P31GMMJ14VHHJTELK5BFH2KMYWJDRVTVYGZPZLZGJKEDCC4D1WS142UPFRKP1HAJ45RBGYZZ5TQQLUTUJKDSQH6ZKGFBVNA5KLNDEV13KJRPX5CK2IENZ3MLWKA4JA6JLYIWCE46AI5ABMARU4ZNI2QYJP6VD5N1B5MA3SEFXCFZ4LZZPBDI6FGC6WMPZJ1GQ5ZJCI3SEDYLWFZW5UQNCWABBEYCXSGKEKZARM6B3BVJGN4KFDQVLB6G2D6E31L3ZZ43WQ64OQIQLLROES4PKDFH456E6DUIJTQUV3K1ZMP22ETAEVQ2KTFRL2KHP2XZX6BDZTSIK3TNTGWKOS265IVSKQ5KMV1JRQCIWJ2Y5UH3TTN12KI1XM5HMSTIZI1QG4EEOOK3PUUCMF5PVLPAKON2UZ11LWZD5A55V5NP42BKJYQWAHS4HLJLP2C23EPSXWIJRUHFVI1CK1SB5PHY3KTL6PJE5KWNPJL4EQQKW3HG5TXCVFYMU5VJICGLNAM2DU4BAD2P5KSFOEJ6HUYXCHCBEAODA5V5UMZDI";
        plainArray[0] = Alphabet.fromString(crib);
        cipherArray[0] = Alphabet.fromString(ciphertextStr.substring(pos));
        return plainArray[0].length;
    }

    static int jf8(byte[][] plainArray, byte[][] cipherArray) {
        //String plaintextStr = "!DEUTSCHE!REGIERUNG";
        //String ciphertextStr = "XDITGRECMOFUJBSSPYKN23TDDVY5R6O2OYZTPMO3UEBQVWOQKUWCJX4XAO4NAEFNGKBIZGMVTXJWFCMEGVMKABZIZKSTOKNSANEFGDAHQLVIUROZ2YXKUTD5JIZYJPHWPCT6HKQ5MQMH4CHZ6PNYI5N5KUGPM244VJHXA4YPU24EVGMWB66GALZCEPEYGWGERCQPT3KCXYNNVRM246P4ZVZWQG3PP1YZTFJQPYJLOEPCGJZKGKZ53F6PIMLUF6OSRA5X1P31GMMJ14VHHJTELK5BFH2KMYWJDRVTVYGZPZLZGJKEDCC4D1WS142UPFRKP1HAJ45RBGYZZ5TQQLUTUJKDSQH6ZKGFBVNA5KLNDEV13KJRPX5CK2IENZ3MLWKA4JA6JLYIWCE46AI5ABMARU4ZNI2QYJP6VD5N1B5MA3SEFXCFZ4LZZPBDI6FGC6WMPZJ1GQ5ZJCI3SEDYLWFZW5UQNCWABBEYCXSGKEKZARM6B3BVJGN4KFDQVLB6G2D6E31L3ZZ43WQ64OQIQLLROES4PKDFH456E6DUIJTQUV3K1ZMP22ETAEVQ2KTFRL2KHP2XZX6BDZTSIK3TNTGWKOS265IVSKQ5KMV1JRQCIWJ2Y5UH3TTN12KI1XM5HMSTIZI1QG4EEOOK3PUUCMF5PVLPAKON2UZ11LWZD5A55V5NP42BKJYQWAHS4HLJLP2C23EPSXWIJRUHFVI1CK1SB5PHY3KTL6PJE5KWNPJL4EQQKW3HG5TXCVFYMU5VJICGLNAM2DU4BAD2P5KSFOEJ6HUYXCHCBEAODA5V5UMZDI";
        String plaintextStr = "5vereinigte!5staaten".toUpperCase();
        //                   VFN KNIGTENENYAAT N
        plaintextStr = "!VFN!KNIGTENENYAAT5N";
        String ciphertextStr = "CXYNNVRM246P4ZVZWQG3PP1YZTFJQPYJLOEPCGJZKGKZ53F6PIMLUF6OSRA5X1P31GMMJ14VHHJTELK5BFH2KMYWJDRVTVYGZPZLZGJKEDCC4D1WS142UPFRKP1HAJ45RBGYZZ5TQQLUTUJKDSQH6ZKGFBVNA5KLNDEV13KJRPX5CK2IENZ3MLWKA";
        //String plaintextStr = "!e!ident5roosevelt5";
        //String ciphertextStr = "H3TTN12KI1XM5HMSTIZI1QG4EEOOK3PUUCMF5PVLPAKON2UZ11LWZD5A55V5NP42BKJYQWAHS4HLJLP2C23EPSXWIJRUHFVI1CK1SB5PHY3KTL6PJE5KWNPJL4EQQKW3HG5TXCVFYMU5VJI";
        plainArray[0] = Alphabet.fromString(plaintextStr);
        cipherArray[0] = Alphabet.fromString(ciphertextStr.substring(0, Math.min(200, ciphertextStr.length())));
        return plainArray[0].length;
    }

    static int jf10(byte[][] plainArray, byte[][] cipherArray) {
/*
An5General5Nikolaus5von5Falkenhorst


x16prvndjft2oozpda4mgcoeufnmnrd2yzjzjoja1icfwnjzrjlaku3rsvonasobwmx51d3xigzhiwater3a5rkpevqu1qeciydy3p2ozco6tqtwl1ziklllpdjvbd3wocni1qtnj3vj2jgnsstpw55tf4kfitrtslkb3m422f43n3zzq3orjpjaagf1rczuczjyxhmb
 */

        String plaintextStr = "An5General5Nikolaus5von5Falkenhorst";  //4-6:III:II:5-9:1-2:V:8-10:I:IV:3-7
        String ciphertextStr = "x16prvndjft2oozpda4mgcoeufnmnrd2yzjzjoja1icfwnjzrjlaku3rsvonasobwmx51d3xigzhiwater3a5rkpevqu1qeciydy3p2ozco6tqtwl1ziklllpdjvbd3wocni1qtnj3vj2jgnsstpw55tf4kfitrtslkb3m422f43n3zzq3orjpjaagf1rczuczjyxhmb";
        plainArray[0] = Alphabet.fromString(plaintextStr);
        cipherArray[0] = Alphabet.fromString(ciphertextStr);
        //cipherArray[0] = Alphabet.fromStringBritish(c);
        System.out.printf("%s\n%s\n", Alphabet.toString(cipherArray[0]), Alphabet.toString(plainArray[0]));
        return plainArray[0].length;
    }

    /*
    "Von General Johannes Blaskowitz an Furher"

    DLQF3JLMD/D9GSHXSSYLGWVVL/+3IGH9ZX93BL+PIWNAQNTTLK
    CDP/+GBTY8K83FEJIRRP3KFALYVBPMG++3PYANMMU+XGGBGOHV
    CODSGFNWANCS83XQIC+8+YSSCRN8SFTOWBK4NG9P/POVCMIGTH
    NTZJOPITLQSAPLBXJY+EKIMTOLKOH+FJ44LMZKJAIGDN89OTUW
    JVI3CBGDWHFHJTBHW+3AUMY8DVQS/FNJZXBXIT/FQ+4X9YUKH3
    GR4OKXGZ/L+TUPVTDGJW
     */
    static int jfc3(byte[][] plainArray, byte[][] cipherArray) {
/*
"Von General Johannes Blaskowitz an Furher"

DLQF3JLMD/D9GSHXSSYLGWVVL/+3IGH9ZX93BL+PIWNAQNTTLKCDP/+GBTY8K83FEJIRRP3KFALYVBPMG++3PYANMMU+XGGBGOHVCODSGFNWANCS83XQIC+8+YSSCRN8SFTOWBK4NG9P/POVCMIGTHNTZJOPITLQSAPLBXJY+EKIMTOLKOH+FJ44LMZKJAIGDN89OTUWJVI3CBGDWHFHJTBHW+3AUMY8DVQS/FNJZXBXIT/FQ+4X9YUKH3GR4OKXGZ/L+TUPVTDGJW
 */

        String plaintextStr = "Von5General5Johannes5Blaskowitz5an5Furher";  //4-6:III:II:5-9:1-2:V:8-10:I:IV:3-7
        String ciphertextStr = "DLQF3JLMD/D9GSHXSSYLGWVVL/+3IGH9ZX93BL+PIWNAQNTTLKCDP/+GBTY8K83FEJIRRP3KFALYVBPMG++3PYANMMU+XGGBGOHVCODSGFNWANCS83XQIC+8+YSSCRN8SFTOWBK4NG9P/POVCMIGTHNTZJOPITLQSAPLBXJY+EKIMTOLKOH+FJ44LMZKJAIGDN89OTUWJVI3CBGDWHFHJTBHW+3AUMY8DVQS/FNJZXBXIT/FQ+4X9YUKH3GR4OKXGZ/L+TUPVTDGJW";
        plainArray[0] = Alphabet.fromString(plaintextStr);
        cipherArray[0] = Alphabet.fromStringBritish(ciphertextStr);
        System.out.printf("%s\n%s\n", Alphabet.toString(cipherArray[0]), Alphabet.toString(plainArray[0]));
        return plainArray[0].length;
    }

    static void swedishFilmScenario(int len, int depth, byte[][] plainArray, byte[][] cipherArray) {
        String[] film = {
                //"3DRINGEND4--535DIE535BIS535ZUM5414/",
                "3DRINGEND4AA535DIE535BIS535ZUM54QRX",
                "J1LVHVMDLXWWC5WANX2YJ2WUSRL5QQG6KG1",

                //"AFENSCHUTZFL4=45?56.55BOENIG5VON452",
                "AFENSCHUTZFL4V45BTYMT5BOENIG5VON45W",
                "NOCHOKL53QOTZNN5WVTUC3NG65GXXYQXIAH",

                "NNEN535AUS535NORWEGENBEREICH535NUR5",
                "ZBCHM4THEAHXCVVDGXFGD2KM6M4EXXGXJGA",

                //"UNNKSLELLE35F43333K44.35MSS3533335H",
                "UNNKSLELLE35F43333K44M35MSS3533335H",
                "1BQNOMMLXWRIU6F3JAVHHGHQKT2OXXFEUAE",

                //"+()!!)949!15D?!!!!PX2N!3STPQ5356D!M",
                "ZKLTJLO4OS1TDB26G!PX2NN3STPQ5356DDM",
                "ITOWWMQQVAX6A2T63!1Q2V265QOTXXGV6CD",

                "3333333335GLTD35S35ADM35NVW4V35NN35",
                "JHE124JU52KTGLF56A2OOGHQFCNM5XGXZHA",

                "LLNG3535N3IKLIGSOELLEN5NN3535BEI35O",
                "VUQB2SJ1RFP3OXMIQXDNGVAPFRLOX42QUAB",

                "GL45AA45Y35FASZ45AAA35335VGL35IM35L",
                "3U6P41K1UFHKDQI45Y4OY3H6SCXYIIOBUAY",
        };


        for (String f : film) {
            System.out.printf("%s\n%s %s\n%s\n\n",
                    f,
                    Alphabet.convertToStringWithoutSpecialCharacters(f),
                    Alphabet.convertToStringWithoutSpecialCharacters(f).equals(f),
                    Alphabet.convertToStringSpecialCharacters(f));
        }

        for (int d = 0; d < depth; d++) {
            plainArray[d] = Alphabet.fromString(film[2 * d], len);
            cipherArray[d] = Alphabet.fromString(film[2 * d + 1], len);
        }
        for (int d0 = 0; d0 < depth; d0++) {
            for (int d1 = d0 + 1; d1 < depth; d1++) {

                for (int i = 0; i < Math.min(Math.min(Math.min(Math.min(len, plainArray[d0].length), plainArray[d1].length), cipherArray[d0].length), cipherArray[d0].length); i++) {
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

    static void bengtBookp82(int len, int depth, byte[][] plainArray, byte[][] cipherArray, int garbles) {
        String[] film = {
                "5R5V4b35kk5ja5!",
                "26kpgxjbp6fwwsp",
                "55nochmal55von5",
                "2GCWnU2rjjfk1f!",
                "5555gearbeitet5",
                "2gkkozx66gn1zzo",


        };


        for (int d = 0; d < depth; d++) {
            plainArray[d] = Alphabet.fromString(film[2 * d], len);
            cipherArray[d] = Alphabet.fromString(film[2 * d + 1], len);
            System.out.printf("%s %s\n", Alphabet.toString(plainArray[d]), Alphabet.toString(cipherArray[d]));
        }

        for (int i = 0; i < garbles; i++) {
            plainArray[CommonRandom.r.nextInt(depth)][CommonRandom.r.nextInt(plainArray[0].length)] = -1;
        }
        for (int d = 0; d < depth; d++) {
            System.out.printf("%s %s\n", Alphabet.toString(plainArray[d]), Alphabet.toString(cipherArray[d]));
        }

        for (int d0 = 0; d0 < depth; d0++) {
            for (int d1 = d0 + 1; d1 < depth; d1++) {

                for (int i = 0; i < len; i++) {
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

    static byte[] lorenzPlaintext(boolean rand) {
        String s = "LFR889SCHERENFERNROHREN955QR889Z55955K88SF55L88SALS3NFCHTSEFERUNG9FUER9JAD9NN99FUER9JARFUPSERC1WEI8AEKANN9WBHOWKDO58M889NACH9H55M889PZ55M889N55M889ZA55M889MAGDEBURG55N889GERL/TELEMEK3ZERNER9INIZARSCH9QESETTPJWERDEN5Q5VG889OKH55M188OBDEGGM1889AMA55M1889IN955YM855189Z9WUG955K88V55L889N955M9YYEM18895549MV9V55M88955QUMWMRTM889I55M88A55M889FECHNER9MAJOR559K88W55LZZ98VBERICHTIGUNG99IM9INH55M88999VGL559AA889GERAETELAGER9ZIESAR95VAA8899BD9BDBDBDBD99555Z9EWWW889A9LBFU955YPYY9SJWMHKKPGKV88955KK8899NN9559V88955K88HZPH55188FF58Q9QUOQUL889OBKDO55H889H55M88GRG5M889KURLAND55M19889M55M88QU55N1PD9ROEM955T9K8IO88955EMVV889BET55MC889DORT55M889SSD55M88JV55M88955WOM8855QM9RA889MATR55MCW8/VLGWN3889ERSATZEIN9G83ZI4SMEREITS9FERNSCHR55M889GEMELDET55N889BEFIFDET9SICH9OBLTN55M889GOMMERT9BEI9PZ55MA8ERSATZTEILLAGER9STEZZIN9MIT9SAEMTL55M889UNTERLAGEN5M889DIEE9ANGEFORDERTE9LISTE9KANN9EJST9NACH9EINGANG9DIESER9UNTERLAGEN9VORGELEGT9WERDEN55MVVV89KOMM55M889KP55M889H55MB8GR55M889KURLAND55M889B55M889ZEL955WOL8K9BGR55N583I4TM88955STI55QQ8/BC1WGV889ZEIDLER9HPTM55M889U55MV89KP55MI8IFH55M889B/LGRAD5588LALALALALLA55ZEWWE8889A9HOUA955RUQ9QUMV55WM9QOPPVV889AN99WN955QOPPMVV9K88HZPH55188FF955Q9QUOQYL889AN9FT55RPD//NUQF1MFV55W5ML8OVRGOOGQIDC4W55M8M9GR55M889KURLAND55MVVV89VORG555M889DORT55M88SCHR55M889AZ55M9WQ9K88ROEM955W9L889V55M889G5EMWMRTMA889OA9EIN9DKR55MN889D55M88DIV55M889NACHSCH55M889TR55M9WJY889NUR9B/I9VEER955WRYM889VOLKS55MA88GREN55M88DIV55M889BEKANNZ9IST55G889VERBLE9BT9ES9DEI9DE59MIT9OKH55M1889FZ55M889IN9ABTGGM9W889A5JNO889A955WQ889C955QYQP9TQOWM19RT889V955EQMQMRTMA8V4GETROFFENEN9REGELUNG55MVV889D55M88H55M88FELEZEUGMEISTER955LQ889C955QYQW889OKH5591889FZ55M889IN9ABT55M955W889A955M18A9V5519QUMWMRT889GEZ559M889RIEGER9OBSTLT55M889U55M889GR55M889LTR55S889BERICHTIGUNWCPG8899ACF55MV8T1NH55M8893FI955AA889NUR9BEI9DEI9GGLRYM88955AA889ZAZAZAZAZA955Z9EWWR9Z988HCPO955QPEI9QIMWM9QPTTMVVSC88MZPH55188FF55Q9QUOQTLV889STABSOFFZ55M889LW55M889VROP55M889B551G89GO88TEYKSKDO5559QN889KURLAND5MVV889ZUSTIMMUNG9FUER9DREIWOECYUGEN9SONDEREINSATZ9LT55M889SEIDAT9IN9OSTPREUSZEN9AUF9ANTRAG9F4CHPRUEFER9WORT9ERBETEN955MVV889OKW55M188WPR55M9K88ROEM955Q889OG59I55M8QA55M889MAJOR9GREISINGEER55ZZ88SONST9GEHTS9G9EIN9LEID9LENK9P1YKAL9TRAURIG9BIST955Z88S9HKHB955PWQPT9QY1W9QYPP9K88359HZPH5518FF955Q9PTWUTLV988NACHR55C988INT9H9GR9KURLAND55V955AA88GEH/EIM555AA988D955SS88WESERWEHR55SS988AE9HVHA9HAMBIRG955QY1W9RT988MITSG588V55MA988ZUG9ROEM955J1RORWHFQU988T9MUND5ZMAJUWS5OAOGZ1GGKUZ1JFZ9/WCIDB4BTSORBH555MA988V55A88ZUG955W1RPWR9K88TEIL55L9QWYSV8T9BACKMATE9GAZ1MZITQWOCT9KUZR9ALLEE9ZUEGE955SS88NAEHMASCHINE55SSV89NACH9KURLAND988F55M988L55M/K3ZG5M9QU9TTRV988HAMBURG55N9QY1W9RT9VVWV9ROEM955KP988C9ROEM955WNQ9888NR955YIT1RT988G9IA9GEZ9BUNIENBUR555A88STABSINT5WZ889IM9PRATER9DA9BLUEH/NN9WEITER9NICHTS9IST55ZZ/WWT9Z988AMTS9HOKA9J5WYYE9QIMWM9QYPPVV88955K88HZPH55188FF955QUOWQLV8V9HMI155K88FF55MVV889ADA55M1889HMI155188FF955RT9QUMWM9WEETMAOVSAN9HRR155188FUE55MA8895588FTB55M889AMTS9VON9HOKW9NICHT9ABSELZBARGE5LW4JS9HRR155188FUEHUNDEKANNT55MZZZ889NA9DA9HASTENN9JEMAND9EINE9JEISECOJ1SSM9KAIN9ER9WAS9ERZAEHLEN955ZOOOEVOOOOOOOOOOOOOOOOOOOOOOOOOOOBOOOOOOOOOOOJ8FF955Q9PTEQULV98855AA88GEHEIM55AA988AN9H9GR9KURLAN355A88O9QU55V988BEZUGG5C955QML988OKHA1U8GENSTDH551885EN9QU555M88QU9555W988NR9ROEM955Q1RPPO1RT988GEH9VOM955EP1Q/WBIA988955WML9883OS/UZCHREIBEN55188O9QU551889QU955Q988NR955QVPN1RT988GEH9VOM95GR1W9RTMA988SAN9KP9ROEM955Q1QWY988IST9ZUR9IINGLEIDERUNG9IN9DIE9Y/ERESVERSORGUNGSTRFAPEN9VORGESEHEN55MA988SAN9KP9IST9IN9DIE9ER9TE9HAELFTE9DER9VORGESEHENEN9ABTRANSPORTE9VON9HEERESVERSORGINGSTRUPPERMYWNZUGLIEDERN9UND9ABZUTRANSPORTIERN55V988MKH55188GENSZ3Y55188GEN9QU5518LGU9555W9A8NR9ROEM955Q1YEQW1RT988GEH9VOM955GQI1W9RT988IA9HERTH55N988OBERSTLT9I9G9UND9GR9LTR5555Z88N1R9NICHZS4US9IEBE9WEINE9ES9GIEBT9AUF9ERDEN9NTER9DER9ROTEN9LATERNE9VONZST9QAULI9DA9STEHT9EIN955Z88HPC1C5S188FU955ERI9QY1W9QOWP9K88DG9HZPH551VVNC4/5Q9QSOY4LV98P5SEREREINGUNG9PQI1SM/88AN9AOK9555QYV988BEZUG55CSV8AOK955QY988ROEM9ZWEI9A9VCM955O1W9RTMA988DIV9BITTET9RITTERC5KUTCORSCHLAG9OBERLT9PREUSS9WEITEJZUREICHEN55MA988FUER9WM9SANDER94YR955QOEN988WIRD9VORSCHLAG9AUF9NENNUNG9IM9EHRENBLATT9DES9DEUTSKYEN9HEERES9EINGERIECHT9WERDEN55V988955OEM988INF9DIV9ROEM9ZWEISA9GET9GEIER/9WM9SANDER9A9R955QOEN988WIRD9VORSCHLAG9AUF9NENNUN59IM9EHRENPLATT9DES9DEUTSCHEN9HEERES9EINGEREICHT9WERDEN55V988955BEM988INF9D4V9ROEM9ZWEI9A9GEB9GEIER9MAJORNUND9STELLV9ADJ55Z88DA99ZEH9ICH9NUN9ICH9ARMER9THOR9SND9/DUS9HOECHSTE9GLUECK9AUF9ERDEN9LUEGT9AUF9DEM9RUECKEN9DER9PFERDE955Z88HDVO955EPW9QI1W9WWEPK8VHZPH55188FF955Q9QUOIRLV988AN9EISENBAHNPIONIER55A88KOMMANDEIJ9BEI9H9GR9KURLAND55V988VERSEZUNG9OBLT55M89D55M88R55M98VHANS9KOM1ER55N988KP55M9YYN988ZUM9STAB9ROEM955QM1RM889NICHT9WIRKSAM9GEWOR3EN55M988MIT9WIRKUNG9VOM955QPMWMRT9WIRD9K55M98OAUM9FELDBAHNOTL55M88STAB955T3Q988VERSETZT55M988INMARSCHSETZUNG9FINTR9NN9UNMARSCHSETZUNR9IST9VERANLASZT55MA988EINTREFFEN9MELDEN55V98V5EN55M88D55M88EISB55M88TR55M88ROEM955W988A5988NR55M988R55Q1RT9V88IA9GEZ55M988SCHULZU9HPTMU5Z988BER55C988OBLT55M88D55M8AR55MVVMAMJ9KUMMER99VGL955AA88KUMMER555AA988SLSLSLSLSL955Z88HBL1C955IW9QH1W9PIEPK8BER55C988OBLTZWM88D5AY88U55MC5TANS9KVMMCR99VGL955AAQ8KUM1ER55AA988JLSLSLSLSL955ZBLSC95S1DQOKNTT8VLV44RFF3RN8J4C9MZHHFJ39OIELVSAA88NEUDURCHGABE55AA988AN9GEN9KDO55M88ROEM955R988AK9ABT55M88ROEM9G5W988A9UEBER9AOK955QYV988ERBITTE9FERNSCHRIFTLICHE9METTEILUNG5CF988WANN9UND9WOFIN9ASS55M88ARZT9HELMUTH9HO8FMANN9VERSETZT9WORD/F9IST55V988GEZ55M88HEINZ55N988OBERSTABHRI8CHTER551988GERICHT9DERSG5WE988INF55M88DIV55Z988RERERERERE955Z88TKE955QWRU9QI1W9CQURPLK88HZPH55188FF955Q95VOIWLV9888889AN9H55M889NNN9AN9BV55M889TJSP55M889OFFZ55M889HE55M889GR55M889KURLAND55VV9AA889BEZUG55K4A9WWW889ROEM9EINS9A9NR55M9P1ROQE988V55M9QUMWMRT9MA88955AA889NIVOP88EA9W1A89E9JD3V435BFKFLK14UZ559Q889N9B889NN9B55L888955QD89BFFZ55M9QYP889MANN94EN55M889M55M889KP55M889ROEM955QW1WYE1EQLS14V89C55L9KTL889GFM955KQL889G55MA889D5L8889RUFSTELLUNGSSTAB955YP9QQW9MAV89E55L9YP9QQW9MA889W55L9WTQ9MA889G55LT89UNBEKANNT55MA889H55L9WWMW19WRMPP9MA889I55L9UR889STD55M889NACH9ABRUF9IN9GANG55MA889L55LV89ZU9SATZVERPFL55M889ER13RDERLICH55MA889GLEICHES9AN55C9WWUNSWREN9WTQ9M9VV889TRSP55M889KBTR5JM889KARLSRUHE9ROEM9EINS9ATHNQV88SA55C889BLUMENOBZM5JM889U55M889OVD5519EU8899BBV9KARLSRUHE9BUETTFER955ZZ8889AU9JA9DIES9MACHEN9WIR9DANN9SO9ICES9FRUEHLING9WIR3955Z8SHAS955RWW955QIMWM955QEPP955K88HZPH55188SF955Q889NR955QIPWR55W555VTD8899955AA88TM955E955AA8889AN89GEN55M88KDO55M88ROEM9595H55M88SS55A888FRW55M88KORPS89ROEM9SECHS89A55N889H55M88GR55M88KURLAFD89AOK955QI55188ROEM955TP55M88AK555MA8889AN9955QU55M588ARFSEF558YWJA955M8KBAWBRM888D5LHO8SS555K8RTZSTN5KA88NR955338L889R3ON9OYNUHS3DDMZU33UT55198GR8W818KURLAND55188AOK955QY55MA88889AN955RT55M8V8VGD89NSFO9H55M8JGR55M88KURLAND551889ROEM955I5M88AK555MAK555MAV955VV8889FUEHRUNGSHINWEISE8955QI55M88FEBR55M85QORT55MA888955QMLVV9AUS89DEM89AUFRUF89DES89RF55A88SS8955A889ALS89OBERBEFEHLSHABERVSDER89HEERESGRUPPE9WEICHSEL9AN89ALLE89OFFIZIERE89DER89HEERESGRIPPE89WEICHSEL55R55MA88955SS889ICH9ERWARTE89VON9JEDOV89OFFIZIERG5M889DASZ89ER89EIN89VORBILD89AN89TAPFERKEIT89UND89FESTIGKEIT89U9T89UND898ASS89ER89UNSEAW189BRAVEN89SCLDITEN89VORANGEH8VOM889WENNVSIER3ZJBJN9DER9OFFIZIER89VORN9IST55N8889VERLAESZT89IINZ9AUCH89SECMI/4NN89NICHZ8NN9NICHT55M8889IN89DAN9FAELLEN55N8889IN9DENEN8ZOEDBCH89MENSCHLICHE89SCHWACHHEIT8555N889FEIGHEIT89ODER89AUGENBLICKLUCHE89PANIK89DLN89EINEN89ODER89ANDEREN89ANFALLEN55N889ZEIGE89SUCHB9DER8OFFIZIER55N8DES9IHM89VERLIEHENEN89RANGES89UNLASEINER9SKYILTERSTUECKE89WUERDIG555M55SS55MA83955WML889DIE89BOLSCHEWUSTEN8SBEGINNEN89BEREITS89JETZT555N889AUS89DEN89DEUTSCHEN9OSTGEBIEZ/9N89DEZTSCHE9ARBEITER55N889SOWEIT89SIE89DEREN89HABHAFT89WERDEDSKONNTEN9NACH89DEM89INNERN89DER89SO5JETUNION89ZU9VERSCHLEPPEN5GM8889DAMIT9BESTAETIGT89SICH89DIE89IMMER89WIEDER89WIEDERYOLZ/89FESTSTELLUNG89DER89BOLSCHEWISTESCHEN89VERSKLAVUNGSPLAENE555888H899F9D9R89GEZ55M88KOENIG955A8889SS55A889HAUPTAMT955E8EJ4S9DIESGOLDNE9MORGANS//////////9/////E4E//////NN9WERD9ICH9WIEDER9BEI9DIR9SEIF955ZZ889HZPH955TYU1RT9QI1W9QYPP9K8889HZPH55188FF955Q9QUOTUL9VV8V4F9GERICHT9DER95WRM889INF55M889DIV55M9VV889BETR55C8899HYK1CG5188FU955TRWI9EPMQMWQII9MA9ZU9STM55M889L55M9AA9MA889DER9KARL9SIQPEL9GEB559AM955WEMRM9QOWY9889IN9HOENEBACH9IST9AUSWEISLICQ9STRAD1RR5STERS9BEI9DER999KAATSANWULTSCHAFT9KARMEZ5W/HAHZ1RGT9B/TMAFT5GC9MA889DIEOSTAHLS9ZU955T889MTNATEN9JUGENDGEFAENGNIS55M9VV889W55MV89STTODKWVM9DER9BEI9DER9DV55A889ZEP8/L/N955ZZ8889UWUWUWUWUWUL955ZZD89HZPH955TIT1RT9QI1W9QYPP9R8889HZPH55188FF955Q9QUOTYL9VV8VSAN9GERICHT9BERI55QRM889PL55855M889DIV55M9VV889BETR5C889HCO1KG5188QU9559QPRR9QMWM9PQPP9MA889ZU9ST55M889L55M9W1RE889DER91BELLER9G8UENTHER9EB55M899955QPMOM9QOQO9MA889IN9CRAWINKEL9KRS55M889GOTYA55MA19IST9AUSWEISLICH9DES9STRAFREGISTERS9BEIDER9STAATSANWALZ9CHAFT/GOTHA9WIEDFOLGT9B/STRAFT55C9MA889AM955UMQWM9RE88D55M8895ERICHT9DER955QR889PZ55M889DIV55M889W55M889VOLLTRUNKENHEIT9TU955Y889W9GESCHAEFTEN9ARREST9STR55M889L55M889R5AM9WOE1RE88SSUSGESETZT9BIS9KRIEGSENDE955QEMQWMRE9VV889WEHRMACHTSTRAFREIGISTERS1/I9DER9DV55A889ZEPPELIN98YTZ8889EIEIEIEIEI955ZZ889HZPHH5RHHMRT9MI1W9OCIMMFU89HZPH55188FF955Q9QUOTTL9VV889AN98GERICHT9DER955RMV89PZ55M889DIV55M9VV889BETR55C889HEO1C55188FU955WP9QUMQM9QQPT9MA889ZUSST55M889L559AA9MA889DER9WALTER9SCHULER9GEB55M889AM9K3PVORM9QOWE88TAN9MIENCHEN9IST9AUSWEISLICH9DES9STRAFREGISTERS9BEI9DER9STAATSACWALTHG9UFT5WC889MUENCHEN9WIE9FOLGT9BESTRAFT55C88955QML9QMUM9RETV9GER55M889D55M889DIENSTST55M889FPMR55M9EW9IWW9889ST55M889L55M9PTI15Z88955E889WOCHEN9GESCH55M889OD1889NN955QTI1RE9889UNVORSG1889BEHANDLG55M889V55M889WAFFEN9955E889WOCHEN9GESCH55M889ODG5M889MUNITION9PARAGR55M9QRI89MSTGR55M8B9ARREST55MA889DIE9STRAFVBWLSTRECKG55M889IST9ANGEORDNET55M9VV889WEHRMACHTSTZAFREGISTER9OEI9DER9DV55A889ZEPPELIN555ZZ8889CUCCU3UCUCUCUCU95ZZ889A9LBNO95GERR9QT1W9QYRT9K8889HZPH55188FF955Q9QUOTIL9VV889AN9GEN55M889KDOG5M889COEM955Y889SS955A889FRW55M889AK9RO83955W889A95A8E9SFAER9H55M8895C55M889KRULAND55M9VVQ9KURIEREING55M88HOKW955QIMWM9QQQP9VV889BETRGC889DT55M88FS";
        s = (rand ? s.substring(CommonRandom.r.nextInt(s.length()) - 10) : s) + s + s + s + s + s + s + s + s + s + s + s + s + s;
        return Alphabet.fromStringBritish(s);
    }

}
