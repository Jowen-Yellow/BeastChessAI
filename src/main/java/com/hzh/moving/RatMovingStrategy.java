package com.hzh.moving;

import com.hzh.game.GameBoard;
import com.hzh.game.GameContextHolder;
import com.hzh.game.Point;
import com.hzh.unit.Cave;
import com.hzh.unit.Unit;
import com.hzh.unit.UnitType;
import com.hzh.unit.chess.Chess;

import java.util.Comparator;
import java.util.Set;

public class RatMovingStrategy implements MovingStrategy {

    @Override
    public boolean canMove(Chess chess, int x, int y) {
        GameBoard gameBoard = GameBoard.INSTANCE;

        /*
         可以移动的情况
         1. 下一个地点是空地、陷阱、敌方洞穴
         2. 自己不在河中，下一个地点包含敌方棋子，且不在河中，且比自己小
         3. 自己不在河中，下一个地点是河中，且不能有任何棋子
         4. 自己在河中，下一个地点也在河中
         */
        boolean isInRiver = gameBoard.isRiver(chess.getX(), chess.getY());
        boolean isRiver = gameBoard.isRiver(x, y);
        boolean blankOrTrapOrCave = gameBoard.isBlank(x, y)
                || gameBoard.isTrap(x, y)
                || gameBoard.isCave(x, y, !chess.isMaximizer());
        boolean smallerChessAndNotRiver = !isRiver
                && gameBoard.hasChess(x, y, !chess.isMaximizer())
                && gameBoard.chessCompare(chess, gameBoard.getChess(x, y)) >= 0;

        return blankOrTrapOrCave || (!isInRiver && smallerChessAndNotRiver)
                || (!isInRiver && isRiver && !gameBoard.hasChess(x,y)) || (isInRiver && isRiver);
    }
}
