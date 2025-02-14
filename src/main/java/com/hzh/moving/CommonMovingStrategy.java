package com.hzh.moving;

import com.hzh.game.GameBoard;
import com.hzh.unit.chess.Chess;

public class CommonMovingStrategy implements MovingStrategy {

    @Override
    public boolean canMove(Chess chess, int x, int y) {
        GameBoard gameBoard = GameBoard.INSTANCE;

        /*
        可以移动的情况
        1. 下一个地点是空地、陷阱、敌方洞穴
        2. 下一个地点包含敌方棋子，且不在河中，且比自己小
         */
        boolean blankOrTrapOrCave = gameBoard.isBlank(x, y)
                                 || gameBoard.isTrap(x, y)
                                 || gameBoard.isCave(x, y, !chess.isMaximizer());

        boolean smallerChessAndNotRiver = !gameBoard.isRiver(x, y)
                                        && gameBoard.hasChess(x, y, !chess.isMaximizer())
                                        && gameBoard.chessCompare(chess, gameBoard.getChess(x, y)) >= 0;

        return blankOrTrapOrCave || smallerChessAndNotRiver;
    }
}
