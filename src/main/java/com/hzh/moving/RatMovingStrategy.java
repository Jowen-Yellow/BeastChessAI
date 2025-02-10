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
        GameBoard gameBoard = GameContextHolder.getGameBoard();
        Comparator<Chess> chessComparator = gameBoard.chessComparator();
        Set<Unit> units = gameBoard.findUnitAtPoint(x, y);
        // 是空地，可以移动
        Point point = chess.getPoint();
        if (units == null) {
            return true;
        }

        boolean isInRiver = gameBoard.findUnitAtPoint(point.getX(), point.getY()).stream().anyMatch(unit -> unit.getUnitType().equals(UnitType.RIVER));

        /*
         不叠子的情况，陷阱，棋子，洞穴，河流，以下情况可以移动
         1. 自己在河中，下一个地点不是棋子，也不是己方洞穴
         2. 自己不在河中，下一个地点不是棋子，也不是己方洞穴
         3. 自己不在河中，下一个地点是棋子，且比自己小
         */
        if (units.size() == 1) {
            Unit unit = units.iterator().next();
            boolean isChess = unit.getUnitType().equals(UnitType.CHESS);
            boolean isCave = unit.getUnitType().equals(UnitType.CAVE) && ((Cave) unit).isMaximizer() == chess.isMaximizer();
            if (isInRiver) return !isChess && !isCave;

            if (!isChess && !isCave) return true;
            return isChess && chessComparator.compare(chess, (Chess) unit) >= 0;
        }

        /*
         叠子的情况，陷阱+棋子(岸上)，河流+老鼠，以下情况可以移动
         1. 自己在河中，下一个地点不是岸上
         2. 自己不在河中，下一个地点是 我方陷阱+敌方棋子
         */
        boolean isRiver = gameBoard.findUnitAtPoint(x, y).stream().anyMatch(unit -> unit.getUnitType().equals(UnitType.RIVER));
        if (isInRiver && !isRiver) {
            return false;
        }

        return gameBoard.isChessTrapped(x, y, chess.isMaximizer());
    }
}
