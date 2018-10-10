import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {
    public static String filename;
    public static boolean testBruteForce;
    public static boolean testBruteForce_r;
    public static boolean testGS_h;
    public static boolean testGS_r;

    public static void main(String[] args) throws Exception {
        parseArgs(args);

        Matching problem = parseMatchingProblem(filename);
        testRun(problem);
    }

    private static void usage() {
        System.err.println("usage: java Driver [-gh] [-gr] [-b] [-br] <filename>");
        System.err.println("\t-b\tTest brute force implementation");
        System.err.println("\t-br\tTest brute force resident optimal implementation");
        System.err.println("\t-gh\tTest Gale-Shapley hospital optimal implementation");
        System.err.println("\t-gr\tTest Gale-Shapley resident optimal implementation");
        System.exit(1);
    }

    public static void parseArgs(String[] args) {
        if (args.length == 0) {
            usage();
        }

        filename = "";
        testBruteForce = false;
        testBruteForce_r = false;
        testGS_h = false;
        testGS_r = false;
        boolean flagsPresent = false;

        for (String s : args) {
            if (s.equals("-gh")) {
                flagsPresent = true;
                testGS_h = true;
            } else if (s.equals("-gr")) {
                flagsPresent = true;
                testGS_r = true;
            } else if (s.equals("-b")) {
                flagsPresent = true;
                testBruteForce = true;
            } else if (s.equals("-br")) {
                flagsPresent = true;
                testBruteForce_r = true;
            }else if (!s.startsWith("-")) {
                filename = s;
            } else {
                System.err.printf("Unknown option: %s\n", s);
                usage();
            }
        }

        if (!flagsPresent) {
            testBruteForce_r = true;
            testBruteForce = true;
            testGS_h = true;
            testGS_r = true;
        }
    }

    public static Matching parseMatchingProblem(String inputFile) throws Exception {
        int m = 0;
        int n = 0;
        ArrayList<ArrayList<Integer>> hospitalPrefs, residentPrefs;
        ArrayList<Integer> hospitalSlots;

        Scanner sc = new Scanner(new File(inputFile));
        String[] inputSizes = sc.nextLine().split(" ");

        m = Integer.parseInt(inputSizes[0]);
        n = Integer.parseInt(inputSizes[1]);
        hospitalSlots = readSlotsList(sc, m);
        hospitalPrefs = readPreferenceLists(sc, m);
        residentPrefs = readPreferenceLists(sc, n);

        Matching problem = new Matching(m, n, hospitalPrefs, residentPrefs, hospitalSlots);

        return problem;
    }

    private static ArrayList<Integer> readSlotsList(Scanner sc, int m) {
        ArrayList<Integer> hospitalSlots = new ArrayList<Integer>(0);

        String[] slots = sc.nextLine().split(" ");
        for (int i = 0; i < m; i++) {
            hospitalSlots.add(Integer.parseInt(slots[i]));
        }

        return hospitalSlots;
    }

    private static ArrayList<ArrayList<Integer>> readPreferenceLists(Scanner sc, int m) {
        ArrayList<ArrayList<Integer>> preferenceLists;
        preferenceLists = new ArrayList<ArrayList<Integer>>(0);

        for (int i = 0; i < m; i++) {
            String line = sc.nextLine();
            String[] preferences = line.split(" ");
            ArrayList<Integer> preferenceList = new ArrayList<Integer>(0);
            for (Integer j = 0; j < preferences.length; j++) {
                preferenceList.add(Integer.parseInt(preferences[j]));
            }
            preferenceLists.add(preferenceList);
        }

        return preferenceLists;
    }

    public static void testRun(Matching problem) {
        Program1 program = new Program1();
        boolean isStable;

        if (testGS_h) {
            Matching GSMatching = program.stableMarriageGaleShapley_hospitaloptimal(problem);
            System.out.println(GSMatching);
            isStable = program.isStableMatching(GSMatching);
            System.out.printf("%s: stable? %s\n", "Gale-Shapley Hospital Optimal", isStable);
            System.out.println();
        }

        if (testGS_r) {
            Matching GSMatching = program.stableMarriageGaleShapley_residentoptimal(problem);
            System.out.println(GSMatching);
            isStable = program.isStableMatching(GSMatching);
            System.out.printf("%s: stable? %s\n", "Gale-Shapley Resident Optimal", isStable);
            System.out.println();
        }

        if (testBruteForce) {
            Matching BFMatching = program.stableMarriageBruteForce(problem);
            System.out.println(BFMatching);
            isStable = program.isStableMatching(BFMatching);
            System.out.printf("%s: stable? %s\n", "Brute Force", isStable);
            System.out.println();
        }

        if (testBruteForce_r) {
            Matching BFMatching = program.stableMarriageBruteForce_residentoptimal(problem);
            System.out.println(BFMatching);
            isStable = program.isStableMatching(BFMatching);
            System.out.printf("%s: stable? %s\n", "Brute Force Resident Optimal", isStable);
            System.out.println();
        }
    }
}
