/**
 * Represents a move.
 */
public class Move {

    // Deltas on X and Y axis for Kings moves (start from upwards move, then clockwise)
    public static final int[] KDX = {+1, +1,  0, -1, -1, -1,  0, +1};
    public static final int[] KDY = { 0, +1, +1, +1,  0, -1, -1, -1};

    // Possible move displacement for white and black. They get generated only once and reused.
    public static final Move[] WM;
    public static final Move[] BM;

    static {
        // Black king's move.
        BM = new Move[8];
        for (int i = 0; i < KDX.length; i++) {
            BM[i] = new Move(Piece.BK, KDX[i], KDY[i]);
        }

        // Moves for white pieces.
        int i = 0;
        WM = new Move[8 + 7 * 4];
        for (; i < KDX.length; i++) {
            WM[i] = new Move(Piece.WK, KDX[i], KDY[i]);
        }
        // Generate all possible rook move in a direction: 4 direction, 7 possible steps on each.
        for (int j = 1; j <= 7; j++) {
            WM[i++] = new Move(Piece.WR,  j,  0);
            WM[i++] = new Move(Piece.WR, -j,  0);
            WM[i++] = new Move(Piece.WR,  0,  j);
            WM[i++] = new Move(Piece.WR,  0, -j);
        }
    }

    public enum Piece {
        WK, BK, WR
    }

    public final Piece piece;
    public final int delta_x;
    public final int delta_y;

    public Move(Piece piece, int delta_x, int delta_y) {
        this.piece = piece;
        this.delta_x = delta_x;
        this.delta_y = delta_y;
    }

}
