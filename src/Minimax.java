import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Minimax {
    private final int maxDepth;
    public static class State {
        //The depth of the state in the search tree
        public int depth;
        //This boolean is 1 if the state is under agent's control (Maximizer),
        //0 if the state is under opponent's control (Minimizer).
        public boolean maxOrMin;
        //String of 42 char that represents the board
        //a stands for agent
        //o stands for opponent
        //# # # # # # # 5
        //# # # # # # # 4
        //# a # o # # # 3
        //# o # a a # # 2
        //o o # a o # # 1
        //o o a a o a # 0
        //0 1 2 3 4 5 6
        //first col from bottom to top + second col from bottom to top ...
        //index in the string = x + 6 * y
        public String boardState;

        public State(int depth, boolean maxOrMin, String boardState) {
            this.depth = depth;
            this.maxOrMin = maxOrMin;
            this.boardState = boardState;
        }
    }
    public Minimax(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * This function takes a board state and get a heuristic about winning/losing/tiling.
     * */
    private int heuristic(String boardState) {
        //A heuristic value to measure from where the state is close
        int heuristicVal = 0;
        //Get all the possible 4 neighbor cells in the grid
        Set<String> neighborCells = getNeighborCells(boardState);

        for (String neighborCell: neighborCells) {
            //Agent score increases by one if it has these sequences
            Set<String> agentSet = new HashSet<>(Arrays.asList("aaa#", "#aaa"));
            heuristicVal += (agentSet.contains(neighborCell) ? 1 : 0);

            //Agent score decreases by one if it has these sequences
            Set<String> opponentSet = new HashSet<>(Arrays.asList("ooo#", "#ooo"));
            heuristicVal -= (opponentSet.contains(neighborCell) ? 1 : 0);
        }

        if (heuristicVal == 0)
            return 0;
        return (heuristicVal > 0 ? 1 : -1);
    }

    /**
     * This function takes a board state and returns true if it's a terminal state and false otherwise.
     * */
    private int checkTerminalState(String boardState) {
        //Get all the possible 4 neighbor cells on the board
        Set<String> neighborCells = getNeighborCells(boardState);

        for (String neighborCell: neighborCells) {
            //Agent wins
            if (neighborCell.equals("aaaa"))
                return 1;

            //Agent loses
            if (neighborCell.equals("oooo"))
                return -1;
        }

        //Tile (there are no winners and the board is full)
        if (!boardState.contains("#"))
            return 0;

        //Not a terminal state (there are no winners and the board is not full yet)
        return 2;
    }

    /**
     * This function takes a board and return a set of all possible 4 consecutive char strings.
     * */
    private Set<String> getNeighborCells(String boardState) {
        Set<String> neighborCells = new HashSet<>();

        //Loop on rows
        for (int i = 0; i <= 5; i++) {
            //Loop on columns
            for (int j = 0; j <= 6; j++) {
                String x = "";
                String y = "";
                String rDiag = "";
                String lDiag = "";

                int k = -1;
                while (++k <= 3) {
                    if (j + k <= 6)
                        x = x.concat(String.valueOf(boardState.charAt(indexInString(i, j + k))));

                    if (i + k <= 5)
                        y = y.concat(String.valueOf(boardState.charAt(indexInString(i + k, j))));

                    if ((j + k <= 6) && (i + k <= 5))
                        rDiag = rDiag.concat(
                                String.valueOf(boardState.charAt(indexInString(i + k, j + k)))
                        );

                    if ((i + k <= 5) && (j - k >= 0))
                        lDiag = lDiag.concat(
                                String.valueOf(boardState.charAt(indexInString(i + k, j - k)))
                        );
                }
                neighborCells.addAll(Arrays.asList(x, y, rDiag, lDiag));
            }
        }
        return neighborCells;
    }



    /**
     * This function takes a state and get all its possible successive states.
     * */
    private ArrayList<State> getSuccessors(State state) {
        boolean maxOrMin = state.maxOrMin;
        String boardState = state.boardState;

        //If true: Maximizer (agent)
        //If false: Minimizer (opponent)
        char player = (maxOrMin ? 'a': 'o');

        ArrayList<State> successors = new ArrayList<>();
        //Loop on columns
        for (int i = 0; i <= 6; i++) {
            //Get the first empty place in the column
            int start = indexInString(0, i);
            int end = start + 6;
            int emptyPlaceIdx = boardState.substring(start, end).indexOf("#");

            //The case that the column is full
            if (emptyPlaceIdx == -1)
                continue;

            //Get the index of the empty place in the global boardState string (not the substring)
            emptyPlaceIdx += start;

            successors.add(
                    new State(state.depth + 1, !maxOrMin, boardState.substring(0, emptyPlaceIdx)
                            + player + boardState.substring(emptyPlaceIdx + 1))
            );
        }
        return successors;
    }

    /**
     * This function takes a point (x, y) on the 2D grid and return its index on the string representation.
     * */
    private int indexInString (int x, int y) {
        return x + 6 * y;
    }

    /**
     * This function is an interface between the maximizer and the minimizer, it also has all base cases.
     * It either calls maximizer or minimizer.
     * Base case 1: the depth exceeded the maximum specified depth.
     * Base case 2: the state is a terminal state.
     * */
    public int value(State state) {
        //Return a heuristic about possible wining/losing/tiling.
        if (state.depth == maxDepth)
            return heuristic(state.boardState);

        //Indicates if the state is a terminal state, also indicates the type of the terminal state
        int indicator = checkTerminalState(state.boardState);
        //Meaning that indicator either be -1 (lose), 0 (tile), or 1 (win)
        if (indicator != 2)
            return indicator;

        //Agent turn
        if(state.maxOrMin)
            return maximizer(new State(state.depth, true, state.boardState));

        //Opponent turn
        return minimizer(new State(state.depth, false, state.boardState));
    }

    /**
     * This function takes a state and calculate the maximum value for its successors.
     * */
    private int maximizer(State state) {
        int v = Integer.MIN_VALUE;
        ArrayList<State> successors = getSuccessors(state);
        for (State successor: successors) {
            v = Math.max(v, value(successor));
        }
        return v;
    }

    /**
     * This function takes a state and calculate the minimum value for its successors.
     * */
    private int minimizer(State state) {
        int v = Integer.MAX_VALUE;
        ArrayList<State> successors = getSuccessors(state);
        for (State successor: successors) {
            v = Math.min(v, value(successor));
        }
        return v;
    }

    public int abValue(State state, int alpha, int beta) {
        //Return a heuristic about possible wining/losing/tiling.
        if (state.depth == maxDepth)
            return heuristic(state.boardState);

        //Indicates if the state is a terminal state, also indicates the type of the terminal state
        int indicator = checkTerminalState(state.boardState);
        //Meaning that indicator either be -1 (lose), 0 (tile), or 1 (win)
        if (indicator != 2)
            return indicator;

        //Agent turn
        if(state.maxOrMin)
            return abMaximizer(state, alpha, beta);

        //Opponent turn
        return abMinimizer(state, alpha, beta);
    }

    private int abMaximizer(State state, int alpha, int beta) {
        int v = Integer.MIN_VALUE;
        ArrayList<State> successors = getSuccessors(state);
        for (State successor: successors) {
            v = Math.max(v, abValue(successor, alpha, beta));
            if (v >= beta)
                return v;
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    private int abMinimizer(State state, int alpha, int beta) {
        int v = Integer.MAX_VALUE;
        ArrayList<State> successors = getSuccessors(state);
        for (State successor: successors) {
            v = Math.min(v, abValue(successor, alpha, beta));
            if (v <= alpha)
                return v;
            beta = Math.min(beta, v);
        }
        return v;
    }
}
