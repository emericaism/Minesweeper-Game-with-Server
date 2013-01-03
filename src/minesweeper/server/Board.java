package minesweeper.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

/*
 * Argument for Thread-Safety
 * The methods which mutate the board acquire a lock during the whole method. Flag, deflag, dig.
 */
public class Board {

    private File file;
    private final int dimension;
    private String[][] board;

    // constructor for Board with just size
    public Board(int s, boolean debug) {
        synchronized (this) {
            this.board = new String[s][s];
            this.dimension = s;
            for (int x = 0; x < this.dimension; x++) {
                for (int y = 0; y < this.dimension; y++) {
                    if (Math.random() > 0.25) {
                        this.board[x][y] = "-";
                    } else {
                        this.board[x][y] = "bomb";
                    }
                }
            }
            checkRI();
        }
    }

    // constructor for Board with board given as String[][]
    public Board(int s, String[][] board, boolean debug) {
        synchronized (this) {
            this.dimension = s;
            this.board = board;
            checkRI();

        }
    }

    public Board(File file, boolean debug) throws FileNotFoundException,
            IOException {

        synchronized (this) {
            int curr_row = 0;
            int num_rows = 0;
            BufferedReader in1 = new BufferedReader(new FileReader(file));
            for (String line = in1.readLine(); line != null; line = in1
                    .readLine()) {
                num_rows++;
            }
            this.dimension = num_rows;
            int len_row;
            String[][] zeroOneBoard = new String[num_rows][num_rows];
            String[][] board = new String[num_rows][num_rows];

            BufferedReader in2 = new BufferedReader(new FileReader(file));
            for (String line = in2.readLine(); line != null; line = in2
                    .readLine()) {
                String[] row = line.split("\\s");
                zeroOneBoard[curr_row] = row;
                len_row = row.length;
                if(len_row!=num_rows){
                    throw new RuntimeException("Board from file is not a square.");
                }
                curr_row++;
            }

            int curr_col = 0;

            curr_row = 0;
            for (String[] roow : zeroOneBoard) {
                curr_col = 0;
                for (String a : roow) {
                    if (a.equals("0")) {
                        board[curr_row][curr_col] = "-";
                    }
                    if (a.equals("1")) {
                        board[curr_row][curr_col] = "bomb";
                    }
                    curr_col++;
                }
                curr_row++;
            }


            

            this.board = board;

            checkRI();
        }
    }

    /*
     * This method returns a string showing the board's current state.
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    synchronized public String toString() {
        String bStr = "";
        for (int x = 0; x < this.dimension; ++x) {
            for (int y = 0; y < this.dimension; ++y) {
                String loc = board[x][y];
                if (loc.equals("-")) {
                    bStr += "-";
                } else if (loc.equals("bomb")) {
                    bStr += "-";
                } else if (loc.equals("dug")) {
                    int val = bombNeighbors(x, y);
                    if (val == 0) {
                        bStr += " ";
                    } else if (val > 0) {
                        bStr += Integer.toString(val);
                    }
                } else if (loc.equals("FlagBomb")) {
                    bStr += "F";
                } else {
                    bStr += board[x][y];
                }
                if (y < this.dimension - 1) {
                    bStr += " ";
                }
            }
            if(x<this.dimension-1){
                bStr += "\n";
            } 
        }
        return bStr;
    }

    /*
     * This method checks that the Rep invariant is not violated. In this case,
     * the Rep Invariant is that the board must be a square.
     */
    private void checkRI() {
        assert okayBoard();
    }

    /*
     * This method checks to ensure that the board is a square.
     * 
     * @return boolean determining whether or not it is an OK board.
     */
    private boolean okayBoard() {
        if (!(this.dimension == this.board.length)) {
            return false;
        }
        for (int x = 0; x < this.dimension; ++x) {
            if (!(this.board[x].length == this.dimension)) {// checks length
                                                            // of each
                                                            // row x
                return false;
            }
        }
        return true;

    }

    /*
     * This method digs at square (x,y).
     * 
     * @param x, row x
     * 
     * @param y, column y
     * 
     * @return string showing the current board state.
     */
    public String dig(int x, int y) {
        synchronized (this) {
            if (isValidSquare(x, y)) {
                if (this.board[x][y].equals("-")) {
                    this.board[x][y] = "dug";
                    digRecurse(x, y);
                } else if (this.board[x][y].equals("bomb")) {
                    this.board[x][y] = "dug";
                    digRecurse(x, y);
                    return "BOOM!";
                }
            }
        }
        return this.toString();
    }

    /*
     * Recurses the dig operation onto all the neighbors of (x,y). If they do
     * not have bombs, it will recurse on the neighbors' neighbors.
     * 
     * @param x, row x
     * 
     * @param y, column y
     */
    private void digRecurse(int x, int y) {
        if (bombNeighbors(x, y) == 0 && isValidSquare(x, y)) {
            int[] xcoords = { x - 1, x, x + 1 };
            int[] ycoords = { y - 1, y, y + 1 };
            for (int i : xcoords) {
                for (int j : ycoords) {
                    if ((i == x) && (j == y)) {
                        continue;
                    }
                    if (isValidSquare(i, j)) {
                        if (this.board[i][j].equals("-")) {
                            this.board[i][j] = "dug";
                            digRecurse(i, j);
                        }
                    }
                }
            }
        }

    }

    /*
     * checks if a square contains a bomb or FlagBomb
     * 
     * @param x, row x
     * 
     * @param y, column y
     * 
     * @return Boolean of whether or not square at (x,y) contains a bomb
     */
    private boolean hasBomb(int x, int y) {
        if (board[x][y].equals("bomb")) {
            return true;
        } else if (board[x][y].equals("FlagBomb")) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * checks if a square is located within the valid board space
     * 
     * @param x, row x
     * 
     * @param y, column y
     * 
     * @return Boolean of whether or not square at (x,y) is within board space
     */
    private boolean isValidSquare(int x, int y) {
        if (x < 0 || y < 0 || x > this.dimension - 1 || y > this.dimension - 1) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * checks if a square is both within the boardspace and contains a bomb.
     * 
     * @param x, row x
     * 
     * @param y, column y
     * 
     * @return Boolean of whether or not square at (x,y) is within boardspace and
     * contains a bomb.
     */
    private boolean validBomb(int x, int y) {
        if (isValidSquare(x, y) && hasBomb(x, y)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * finds the number of bombs surrounding a given square.
     * 
     * @param x, row x
     * 
     * @param y, column y
     * 
     * @return Integer number of bombs neighboring square (x,y)
     */
    private int bombNeighbors(int x, int y) {
        int bNeighbors = 0;
        int[] xcoords = { x - 1, x, x + 1 };
        int[] ycoords = { y - 1, y, y + 1 };
        for (int i : xcoords) {
            for (int j : ycoords) {
                if (validBomb(i, j)) {
                    bNeighbors++;
                }
            }
        }
        return bNeighbors;
    }

    /*
     * Places a flag at the square whose coordinate is (x,y).
     * 
     * @param x, row x
     * 
     * @param y, column y
     * 
     * @return String showing the current board state.
     */
    public String flag(int x, int y) {
        synchronized (this) {
            if (isValidSquare(x, y)) {
                if (this.board[x][y].equals("-")) {
                    this.board[x][y] = "F";
                } else if (this.board[x][y].equals("bomb")) {
                    this.board[x][y] = "FlagBomb";
                }
            }
        }
        return this.toString();
    }

    /*
     * Deflags the square whose coordinate is (x,y) if it contains a flag.
     * 
     * @param x, row x
     * 
     * @param y, column y
     * 
     * @return String showing the current board state.
     */
    public String deflag(int x, int y) {
        synchronized (this) {
            if (isValidSquare(x, y)) {
                if (this.board[x][y].equals("F")) {
                    this.board[x][y] = "-";
                } else if (this.board[x][y].equals("FlagBomb")) {
                    this.board[x][y] = "bomb";
                }
            }
        }
        return this.toString();
    }

}
