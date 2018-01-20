/**
 * Contains a test case information.
 */
public class TestCase {

    public final String name;
    public final State initialState;

    public TestCase(String name, State initialState) {
        this.name = name;
        this.initialState = initialState;
    }
}
