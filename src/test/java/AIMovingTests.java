import com.hzh.ai.BeastChessAI;
import com.hzh.game.GameBoard;
import com.hzh.unit.chess.Chess;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AIMovingTests {
    private final int[][] chessBoard = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {-100, 0, 0, 0, 0, 0, 0},
            {0, 800, 0, 0, 0, 0, 0},
            {600, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
    };

    private final GameBoard gameBoard= new GameBoard(chessBoard);
    private final BeastChessAI beastChessAI = BeastChessAI.INSTANCE;

    @Test
    public void testAIApplyMoveAndUndoMove() {
        Chess rat = gameBoard.getChess(5, 0);
        Chess elephant = gameBoard.getChess(6, 1);
        beastChessAI.move(true);

        assertFalse(gameBoard.chessDied(rat));
        assertFalse(gameBoard.chessDied(elephant));
    }
}
