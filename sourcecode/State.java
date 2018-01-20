import java.util.ArrayList;
import java.util.List;

/**
 * Represents a board state and allows state manipulation.
 */
public class State {

    // Immutable int coordinates (x = row, bottom to top; y = col, left to right).
    public final int wk_x;
    public final int wk_y;
    public final int wr_x;
    public final int wr_y;
    public final int bk_x;
    public final int bk_y;
    public final boolean terminal; // true if mate (stalemates are not returned as successors)
    public final State pred; // predecessor
    public final int cost;

    // Constructor.
    public State(int wk_x, int wk_y, int wr_x, int wr_y, int bk_x, int bk_y, State pred, boolean terminal) {
        this.wk_x = wk_x;
        this.wk_y = wk_y;
        this.wr_x = wr_x;
        this.wr_y = wr_y;
        this.bk_x = bk_x;
        this.bk_y = bk_y;
        this.terminal = terminal;
        this.pred = pred;
        this.cost = (pred == null ? 0 : pred.cost + 1); // cost is pred cost, incremented by one
    }

    /*
     * Methods to translate to and from representation of State.
     */

    // Return a int representation of the state (made by coordinates of WK, WR, and BK).
    public int toInt() {
        return Integer.parseInt(String.format("%d%d%d%d%d%d", wk_x, wk_y, wr_x, wr_y, bk_x, bk_y));
    }

    // Parses a test case grid to State.
    public static State fromString(String testCase) {
        String[] tokens = testCase.split("\\s+");
        int wk_x = 0, wk_y = 0, bk_x = 0, bk_y = 0, wr_x = 0, wr_y = 0;
        int index = 0;
        for (int i = 8; i >= 1; i--) {
            for (int j = 1; j <= 8; j++) {
                if (tokens[index].equals("WK")) {
                    wk_x = i; wk_y = j;
                } else if (tokens[index].equals("BK")) {
                    bk_x = i; bk_y = j;
                } else if (tokens[index].equals("WR")) {
                    wr_x = i; wr_y = j;
                }
                index++;
            }
        }
        return new State(wk_x, wk_y, wr_x, wr_y, bk_x, bk_y, null, false);
    }

    // Returns the State as a board.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 8; i >= 1; i--) {
            for (int j = 1; j <= 8; j++) {
                if (wk_x == i && wk_y == j) {
                    sb.append("WK");
                } else if (wr_x == i && wr_y == j) {
                    sb.append("WR");
                } else if (bk_x == i && bk_y == j) {
                    sb.append("BK");
                } else sb.append("--");
                if (j < 8) sb.append(" ");
            }
            if (i > 1) sb.append("\n");
        }
        return sb.toString();
    }

    /*
     * State methods used by SearchEngine.
     */

    // Returns a list of successor states. Doesn't return unacceptable state (illegal, draws, stalemates).
    // Stalemates and draws are always avoidable in KR vs K with white to move, if correct play.
    public List<State> getSuccessors() {
        List<State> successors = new ArrayList<>();
        for (int i = 0; i < Move.WM.length; i++) {
            State s = apply(Move.WM[i]);
            if (s != null)
                successors.add(s);
        }
        return successors;
    }

    // Returns an heuristic evaluation of the minimum number of moves left to checkmate.
    public double heuristic() {
        // To checkmate with a rook, it's always necessary to have the black king at the edge
        // of the board. Driving the king to the edge of the board requires the combined effort
        // of the rook and king. Therefore, it makes sense to estimate the cost of the checkmate
        // using the distance between the black king and the edge (as it has to get there) and
        // the distance between kings (as the white king has to actively oppose the black king).
        if (terminal) return 0;
        double h = 0.0;
        // Add minimum distance of BK to the edge of the board.
        h += Math.min(Math.min(bk_x - 1, 8 - bk_x), Math.min(bk_y - 1, 8 - bk_y));
        // Add distance between BK and WK, - 1 as they can never by adjacent.
        h += Math.sqrt((Math.abs(bk_x - wk_x)*Math.abs(bk_x - wk_x))
                     + (Math.abs(bk_y - wk_y)*Math.abs(bk_y - wk_y))) - 1;
        return h;
    }

    /*
     * Internal methods used to generate successors.
     */

    // Tries to apply a move to the state and returns the result of w/b moves, or null if the move is rejected.
    // A move is reject if illegal or if it leads to unacceptable state (stalemate, loss of rook).
    public State apply(Move move) {
        // Check if state is already terminal. If so, there are no successors.
        if (terminal) return null;

        // Check if move is illegal. If so, return null.
        if (!isLegalMovement(move)) return null;

        // Apply move on this state.
        State afterWhiteMove = applyDelta(move, this, false);

        // Check if resulting state loses rook. If so, return null.
        if (afterWhiteMove.isRookInDanger()) return null;

        // Try legal black moves, check if acceptable, and calculate the best scores.
        State bestBlackOption = null;
        double bestScore = 30.0; // The function is never above 30 on the board.
        for (Move blackMove : Move.BM) {
            // Check legality
            if (!afterWhiteMove.isLegalMovement(blackMove)) continue;

            // Apply the black move.
            State afterBlackMove = afterWhiteMove.applyDelta(blackMove, this, false);

            // Check that it doesn't put/leave the King in check.
            if (afterBlackMove.isKingInCheck()) continue;

            // Keep track of the best black move by score.
            if (afterBlackMove.blackFunc() < bestScore) {
                bestBlackOption = afterBlackMove;
                bestScore = afterBlackMove.blackFunc();
            }
        }

        // Now return the best option for black. If no option was found, it's either mate or stalemate.
        if (bestBlackOption == null && afterWhiteMove.isKingInCheck())
            return applyDelta(move, this, true);
        else return bestBlackOption; // Stalemate is rejected and null is returned.
    }

    // Returns a new state with changed coordinates to reflect the move. Does *no* error checking.
    public State applyDelta(Move move, State pred, boolean terminal) {
        return new State((move.piece == Move.Piece.WK ? wk_x + move.delta_x : wk_x),
                (move.piece == Move.Piece.WK ? wk_y + move.delta_y : wk_y),
                (move.piece == Move.Piece.WR ? wr_x + move.delta_x : wr_x),
                (move.piece == Move.Piece.WR ? wr_y + move.delta_y : wr_y),
                (move.piece == Move.Piece.BK ? bk_x + move.delta_x : bk_x),
                (move.piece == Move.Piece.BK ? bk_y + move.delta_y : bk_y), pred, terminal);
    }

    /*
     * Helper methods for board interpretation.
     */

    // Check if the given move is possible in the State.
    // This method checks:
    // - if the move is impeded by a piece in the way or by a friendly piece in the arrival place.
    // - if a king move puts it adjacent to another king
    //   (which is illegal as can never happen with right play)
    public boolean isLegalMovement(Move move) {
        // Calculate new coordinates.
        int new_x = move.delta_x;
        int new_y = move.delta_y;
        if (move.piece == Move.Piece.WK)      { new_x += wk_x; new_y += wk_y; }
        else if (move.piece == Move.Piece.WR) { new_x += wr_x; new_y += wr_y; }
        else if (move.piece == Move.Piece.BK) { new_x += bk_x; new_y += bk_y; }
        // Check if the new coordinates are in bounds.
        if (new_x < 1 || new_x > 8 || new_y < 1 || new_y > 8)
            return false;
        // Check kings moves: not adjacent to other king, not on friendly rook place.
        if (move.piece == Move.Piece.BK) {
            if (Math.abs(new_x - wk_x) <= 1 && Math.abs(new_y - wk_y) <= 1)
                return false;
        }
        if (move.piece == Move.Piece.WK) {
            if (Math.abs(new_x - bk_x) <= 1 && Math.abs(new_y - bk_y) <= 1)
                return false;
            if (new_x == wr_x && new_y == wr_y)
                return false;
        }
        // Check the rook for pieces in the way, or on arrival (WR can never actually capture the BK).
        if (move.piece == Move.Piece.WR) {
            // Check for vertical moves.
            for (int i = Math.min(wr_x, new_x); i <= Math.max(wr_x, new_x); i++) {
                if (i == wk_x && wr_y == wk_y) return false;
                if (i == bk_x && wr_y == bk_y) return false;
            }
            // Check for horizontal moves.
            for (int i = Math.min(wr_y, new_y); i <= Math.max(wr_y, new_y); i++) {
                if (i == wk_y && wr_x == wk_x) return false;
                if (i == bk_y && wr_x == bk_x) return false;
            }
        }
        return true;
    }

    // Checks if the WR is currently en-prise by the BK (*and* unprotected by the WK).
    public boolean isRookInDanger() {
        return     (Math.abs(wr_x - bk_x) <= 1 && Math.abs(wr_y - bk_y) <= 1)
               && !(Math.abs(wr_x - wk_x) <= 1 && Math.abs(wr_y - wk_y) <= 1);
    }

    // Checks if the BK is checked by the Rook.
    public boolean isKingInCheck() {
        // Case: same X (check on column)
        if (wr_x == bk_x) {
            // Check that the wk is not between them.
            if (wk_x == wr_x) return  !(wk_y < wr_y && bk_y < wk_y)  // not bk - wk - wr
                                   && !(wk_y > wr_y && bk_y > wk_y); // not wr - wk - bk
            else return true;

        } else if (wr_y == bk_y) {
            // Check that the wk is not between them.
            if (wk_y == wr_y) return !(wk_x < wr_x && bk_x < wk_x)  // not bk - wk - wr
                                  && !(wk_x > wr_x && bk_x > wk_x); // not wr - wk - bk
            else return true;
        }
        return false;
    }

    // Computes the black king's function to minimize for move selection.
    public double blackFunc() {
        return (Math.abs(bk_x - 5) * 5) + (Math.abs(bk_y - 5) * 3) + ((bk_x + bk_y) * 0.1);
    }

}
