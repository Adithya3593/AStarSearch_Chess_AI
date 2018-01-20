import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Main app file. Manages I/O and runs the test cases.
 */
public class Main {

    private static String inputFile = "";
    private static String outputFile = "";
    private static boolean outputOnConsole = false;

    public static void main(String[] args) {
        // Read parameters on input
        try {
            inputFile = args[0];
            outputFile = args[1];
        } catch (Exception e) {
            System.out.println("Usage: Main inputfile outputfile [--v]\n--v specifies console output.");
        }
        if (args.length > 2 && args[2].equals("--v"))
            outputOnConsole = true;

        // Read test cases from file.
        TestCase[] testCases = null;
        try {
            testCases = readInputFile();
        } catch (FileNotFoundException e) {
            System.out.println("Error: could not read input file.");
            System.exit(1);
        }

        // Run the algorithm.
        Solution[] solutions = new Solution[testCases.length];
        SearchEngine se = new SearchEngine();
        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = se.solve(testCases[i]);
        }

        // Write out solutions.
        try {
            writeOutputFile(solutions);
        } catch (FileNotFoundException e) {
            System.out.println("Error: could not write output file.");
        }
        if (outputOnConsole) {
            for (int i = 0; i < solutions.length; i++) {
                System.out.println(solutions[i].toString());
                System.out.println();
            }
        }
    }

    // Reads an input file in a test case array.
    private static TestCase[] readInputFile() throws FileNotFoundException {
        Scanner in = new Scanner(new File(inputFile));
        int n = in.nextInt();
        TestCase[] testCases = new TestCase[n];
        for (int i = 0; i < testCases.length; i++) {
            in.nextLine();
            String name = in.nextLine();
            String position = "";
            for (int j = 0; j < 8; j++) {
                position += in.nextLine() + "\n";
            }
            testCases[i] = new TestCase(name, State.fromString(position.trim()));
        }
        in.close();
        return testCases;
    }

    // Writes an array of solutions on file.
    private static void writeOutputFile(Solution[] solutions) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(outputFile);
        for (int i = 0; i < solutions.length; i++) {
            pw.println(solutions[i].toString());
            pw.println();
        }
        pw.close();
    }

}
