import java.util.*;

/**
 * Implementation of A* search with pruning of losing states.
 */
public class SearchEngine {

    // Comparator to order the priority queue by f score.
    class StateComparator implements Comparator<State> {

        @Override
        public int compare(State o1, State o2) {
            return Double.compare(o1.cost + o1.heuristic(), o2.cost + o2.heuristic());
        }
    }

    // Max states expanded before stopping the search.
    public static final int MAX_EXPLORED_STATES = 10000;

    // Solves a test case using A*.
    public Solution solve(TestCase testCase) {
        // Initialize data structures.
        PriorityQueue<State> pq = new PriorityQueue<>(new StateComparator());
        Set<Integer> visited = new HashSet<>();
        Collection<Integer> generatedStates = new LinkedList<>();
        pq.add(testCase.initialState);

        while (pq.size() > 0) {
            State current = pq.poll();
            visited.add(current.toInt()); // Add it to closed set

            // Return if we found a solution.
            if (current.terminal)
                return new Solution(testCase.name, buildSequence(current),
                        current.cost, generatedStates);

            // Expand the search to successors.
            for (State successor : current.getSuccessors()) {
                // Add to set of generated states
                generatedStates.add(successor.toInt());

                // Don't revisit states already explored.
                if (visited.contains(successor.toInt())) continue;

                // Put successor in queue estimating its cost.
                pq.add(successor);

            }

            // If reached limit, fail.
            if (generatedStates.size() >= MAX_EXPLORED_STATES)
                break;
        }

        return new Solution(testCase.name, buildSequence(testCase.initialState),
                0, generatedStates);
    }

    // Builds a sequence of States leading from initial state to the given state.
    public State[] buildSequence(State state) {
        State[] path = new State[state.cost + 1];
        path[path.length - 1] = state;
        for (int i = path.length - 2; i >= 0; i--) {
            path[i] = path[i+1].pred;
        }
        return path;
    }


}
