import java.util.*;

public class Minimax {
    private final int maxDepth;
    public static class State {

        public State next;
        //The depth of the state in the search tree
        private final int depth;
        //This boolean is 1 if the state is under agent's control (Maximizer),
        //0 if the state is under opponent's control (Minimizer).
        private final boolean maxOrMin;
        //String of 42 char that represents the board
        //a stands for agent
        //o stands for opponent
        //# stands for empty place
        //index in the string = x + 6 * y
        public final String boardState;

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
     * This function takes a board state and returns its heuristic score.
     * */
    private int heuristicScore(String boardState) {
        //Get all the possible 4 neighbor cells in the grid
        List<String> neighborCells = getNeighborCells(boardState);

        int heuristicScore = 0, agCount, oppCount;
        for (String neighborCell: neighborCells) {
            agCount = (int) neighborCell.chars().filter(ch -> ch == 'a').count();
            oppCount = (int) neighborCell.chars().filter(ch -> ch == 'o').count();
            if (oppCount == 0 && agCount != 0)
                heuristicScore += (int) Math.pow(10, agCount);
            else if (agCount == 0 && oppCount != 0)
                heuristicScore -= (int) Math.pow(10, oppCount);
        }

        return heuristicScore;
    }

    /**
     * This function takes a board and return a set of all possible 4 consecutive char strings.
     * */
    public List<String> getNeighborCells(String boardState) {
        List<String> neighborCells = new ArrayList<>();

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
                if (x.length() == 4) neighborCells.add(x);
                if (y.length() == 4) neighborCells.add(y);
                if (rDiag.length() == 4) neighborCells.add(rDiag);
                if (lDiag.length() == 4) neighborCells.add(lDiag);
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
            return heuristicScore(state.boardState);

        //Agent turn
        if(state.maxOrMin)
            return maximizer(state);

        //Opponent turn
        return minimizer(state);
    }

    /**
     * This function takes a state and calculate the maximum value for its successors.
     * */
    private int maximizer(State state) {
        int v = Integer.MIN_VALUE;
        ArrayList<State> successors = getSuccessors(state);
        for (State successor: successors) {
            int newV = value(successor);
            if (newV > v) {
                v = newV;
                state.next = successor;
            }
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
            int newV = value(successor);
            if (newV < v) {
                v = newV;
                state.next = successor;
            }
        }
        return v;
    }

    public int abValue(State state, int alpha, int beta) {
        //Return a heuristic about possible wining/losing/tiling.
        if (state.depth == maxDepth)
            return heuristicScore(state.boardState);

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
            int newV = abValue(successor, alpha, beta);
            if (newV > v) {
                v = newV;
                state.next = successor;
            }
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
            int newV = abValue(successor, alpha, beta);
            if (newV < v) {
                v = newV;
                state.next = successor;
            }
            if (v <= alpha)
                return v;
            beta = Math.min(beta, v);
        }
        return v;
    }
}
