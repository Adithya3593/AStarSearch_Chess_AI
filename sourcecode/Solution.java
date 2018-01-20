import java.util.Collection;

/**
 * Solution object to contain a solution to a test case.
 */
public class Solution {

    public final String name;
    public final State[] solution;
    public final int moves;
    public final Collection<Integer> generatedStates;

    public Solution(String name, State[] solution, int moves, Collection<Integer> generatedStates) {
        this.name = name;
        this.solution = solution;
        this.moves = moves;
        this.generatedStates = generatedStates;
    }

    @Override
    public String toString() {
        if (moves == 0) {
            return name + "\n" + "NA";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name).append('\n')
          .append("Moves to solution: ").append(moves).append('\n')
          .append("Generated states: ").append(generatedStates.size()).append('\n');
        for (int i = 0; i < solution.length; i++) {
            sb.append(i).append(" (").append(solution[i].toInt())
              .append("):\n").append(solution[i].toString()).append('\n');
        }
        sb.append("\nAll Generated States:\n");
        int i = 0;
        for (Integer vs : generatedStates) {
            sb.append(vs).append(' ');
            if (i++ == 6) {
                sb.append('\n');
                i = 0;
            }
        }
        return sb.toString();
    }
}
