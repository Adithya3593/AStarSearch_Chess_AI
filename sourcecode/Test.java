/**
 * 
 */
public class Test {

    public static void main(String[] args) {
        String t =  "BK -- -- -- -- -- -- --\n" +
                    "-- WR -- -- -- -- -- --\n" +
                    "-- -- -- -- -- -- -- --\n" +
                    "-- -- -- -- -- -- -- --\n" +
                    "-- -- -- -- -- -- -- --\n" +
                    "-- -- -- -- -- -- -- --\n" +
                    "-- -- -- -- -- -- -- --\n" +
                    "-- -- -- -- -- -- -- WK";
        SearchEngine se = new SearchEngine();
        System.out.println(se.solve(new TestCase("Test", State.fromString(t))));
    }

}
