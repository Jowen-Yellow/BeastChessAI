import com.hzh.game.GameBoard;
import com.hzh.unit.chess.Chess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BasicTests {
    private final int[][] chessBoard = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {-100, 0, 0, 0, 0, 0, 0},
            {800, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
    };
    private final GameBoard gameBoard= new GameBoard(chessBoard);
    @Test
    public void testApplyMoveAndUndoMove() {
        Chess rat = gameBoard.getChess(4, 0);
        Chess elephant = gameBoard.getChess(5, 0);
        assertNotNull(rat, "Rat should not be null");
        assertNotNull(elephant, "Elephant should not be null");

        Chess willBeEaten = gameBoard.applyMove(rat, 5, 0);
        assertEquals(0, chessBoard[4][0], "Rat should be removed from the original position");
        assertEquals(-100, chessBoard[5][0], "Rat should be moved to the new position");

        Assertions.assertTrue(gameBoard.chessDied(elephant));

        gameBoard.undoMove(rat, 4, 0, willBeEaten);
        assertNotNull(gameBoard.getChess(4, 0), "Rat should be moved back to the original position");
        assertNotNull(gameBoard.getChess(5, 0), "Elephant should be revived");
        assertEquals(chessBoard[4][0], -100, "Rat should be moved back to the original position");
        assertEquals(chessBoard[5][0], 800, "Elephant should be revived");
    }

    @Test
    public void testDetectRepetition(){
        Chess rat = gameBoard.getChess(4, 0);
        Chess elephant = gameBoard.getChess(5, 0);

        gameBoard.applyMove(rat,3,0);
        gameBoard.applyMove(elephant, 6, 0);
        gameBoard.applyMove(rat,4,0);
        gameBoard.applyMove(elephant,5,0);
        gameBoard.applyMove(rat,3,0);
        gameBoard.applyMove(elephant,6,0);
        gameBoard.applyMove(rat,4,0);

        assertTrue(gameBoard.detectRepetition());
    }
}
