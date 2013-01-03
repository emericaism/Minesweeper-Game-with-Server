package minesweeper.server;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BoardTest {

    @Test
    public void test1() {
        String[][] b1 = { { "bomb", "-", "-", "-" },
                { "bomb", "-", "bomb", "-" }, { "-", "F", "F", "FlagBomb" },
                { "-", "-", "-", "-" } };
        Board board = new Board(4, b1, false);
        String s1 = "- - - - \n" + "- - - - \n" + "- F F F \n" + "- - - - \n";
        assertTrue(board.toString().equals(s1));
        board.dig(0, 1);
        String s2 = "- 3 - - \n" + "- - - - \n" + "- F F F \n" + "- - - - \n";
        System.out.println(board.toString());
        assertTrue(board.toString().equals(s2));

    }
    
    @Test
    public void deFlagTest(){
        String[][] b1 = { { "bomb", "-", "-", "-" },
                { "bomb", "-", "bomb", "-" }, { "-", "F", "F", "FlagBomb" },
                { "-", "-", "-", "-" } };
        Board board = new Board(4, b1, false);
        String s1 = "- - - - \n" + "- - - - \n" + "- F F F \n" + "- - - - \n";
        board.deflag(2, 2);
        String s2 = "- - - - \n" + "- - - - \n" + "- F - F \n" + "- - - - \n";
        assertTrue(board.toString().equals(s2));
    }
    
    @Test
    public void flagTest(){
        String[][] b1 = { { "bomb", "-", "-", "-" },
                { "bomb", "-", "bomb", "-" }, { "-", "F", "F", "FlagBomb" },
                { "-", "-", "-", "-" } };
        Board board = new Board(4, b1, false);
        String s1 = "- - - - \n" + "- - - - \n" + "- F F F \n" + "- - - - \n";
        board.flag(0, 0);
        String s2 = "F - - - \n" + "- - - - \n" + "- F F F \n" + "- - - - \n";
        System.out.println(board.toString());
        assertTrue(board.toString().equals(s2));
    }
    
    @Test
    public void examineDig(){
        String[][] b1 = { { "-", "-", "-", "-" },
                { "bomb", "-", "-", "-" }, { "-", "F", "F", "F" },
                { "-", "-", "-", "-" } };
        Board board=new Board(4, b1, false);
        board.dig(0, 3);
        System.out.println(board.toString());
    }
    
    

}