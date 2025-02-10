package com.hzh.moving;

import com.hzh.game.GameBoard;
import com.hzh.game.GameContextHolder;
import com.hzh.unit.Cave;
import com.hzh.unit.Unit;
import com.hzh.unit.UnitType;
import com.hzh.unit.chess.Chess;

import java.util.Comparator;
import java.util.Set;

public class CommonMovingStrategy implements MovingStrategy {

    @Override
    public boolean canMove(Chess chess, int x, int y) {
        GameBoard gameBoard = GameContextHolder.getGameBoard();
        Comparator<Chess> chessComparator = gameBoard.chessComparator();
        Set<Unit> units = gameBoard.findUnitAtPoint(x, y);
        // 是空地，可以移动
        if (units == null) {
            return true;
        }

        /*
         不叠子的情况，陷阱，棋子，河流，洞穴，以下情况可以移动
         1. 下一个地点不是棋子，并且不是河流，且不是己方洞穴
         2. 下一个地点是棋子，并且比自己小
         */
        if (units.size() == 1) {
            Unit unit = units.iterator().next();
            boolean isRiver = unit.getUnitType().equals(UnitType.RIVER);
            boolean isCave = unit.getUnitType().equals(UnitType.CAVE) && ((Cave)unit).isMaximizer() == chess.isMaximizer();
            boolean isChess = unit.getUnitType().equals(UnitType.CHESS);
            if (!isChess && !isRiver && !isCave) return true;
            return isChess && chessComparator.compare(chess, (Chess) unit) >= 0;
        }

        /*
         叠子的情况，陷阱+棋子，河流+老鼠，以下情况可以移动
         1. 下一个地点不是河流，且是我方陷阱+敌方棋子
         */
        if (gameBoard.findUnitAtPoint(x, y).stream().anyMatch(unit -> unit.getUnitType().equals(UnitType.RIVER))) {
            return false;
        }

        return gameBoard.isChessTrapped(x, y, chess.isMaximizer());

    }
}
