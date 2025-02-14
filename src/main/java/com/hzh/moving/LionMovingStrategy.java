package com.hzh.moving;

import com.hzh.game.GameBoard;
import com.hzh.unit.chess.Chess;
import com.hzh.unit.chess.ChessType;

public class LionMovingStrategy implements MovingStrategy {

    @Override
    public boolean canMove(Chess chess, int x, int y) {
        GameBoard gameBoard = GameBoard.INSTANCE;

        /*
         可以移动的情况
         1. 没跨河，下一个地点是空地、陷阱、敌方洞穴
         2. 没跨河，下一个地点包含敌方棋子，且比自己小
         3. 跨河，中间没有棋子（老鼠），下一个地点同上述规则
         */
        boolean crossRiver = Math.abs(chess.getX() - x) + Math.abs(chess.getY() - y) > 1;
        boolean ratInRiver = ratInRiver(gameBoard, chess.getX(), chess.getY(), x, y);
        boolean noCrossRiverMove = noCrossRiverMove(gameBoard, chess, x, y);

        return crossRiver ? (!ratInRiver && noCrossRiverMove) : noCrossRiverMove;
    }

    /*
     1. 下一个地点是空地、陷阱、敌方洞穴
     2. 下一个地点包含敌方棋子，且比自己小
     */
    private boolean noCrossRiverMove(GameBoard gameBoard, Chess chess, int x, int y) {
        boolean blankOrTrapOrCave = gameBoard.isBlank(x, y)
                || gameBoard.isTrap(x, y)
                || gameBoard.isCave(x, y, !chess.isMaximizer());

        boolean smallerChess = gameBoard.hasChess(x, y, !chess.isMaximizer())
                && gameBoard.chessCompare(chess, gameBoard.getChess(x, y)) >= 0;

        return blankOrTrapOrCave || smallerChess;
    }

    private boolean ratInRiver(GameBoard gameBoard, int srcX, int srcY, int dstX, int dstY) {
        Chess rat1 = gameBoard.getChess(ChessType.RAT, true);
        Chess rat2 = gameBoard.getChess(ChessType.RAT, false);

        int minX = Math.min(srcX, dstX);
        int maxX = Math.max(srcX, dstX);
        int minY = Math.min(srcY, dstY);
        int maxY = Math.max(srcY, dstY);

        return checkRatInRiver(gameBoard, rat1, minX, maxX, minY, maxY) || checkRatInRiver(gameBoard, rat2, minX, maxX, minY, maxY);
    }

    private boolean checkRatInRiver(GameBoard gameBoard, Chess rat, int minX, int maxX, int minY, int maxY) {
        if (gameBoard.chessDied(rat) || !gameBoard.isRiver(rat.getX(), rat.getY())) {
            return false;
        }
        return (rat.getY() == minY && rat.getX() > minX && rat.getX() < maxX) || (rat.getX() == minX && rat.getY() > minY && rat.getY() < maxY);
    }
}
