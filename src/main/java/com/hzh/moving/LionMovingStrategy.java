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

public class LionMovingStrategy implements MovingStrategy {

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
         不叠子的情况，陷阱，棋子，洞穴，以下情况可以移动
         1. 没跨河，下一个地点不是棋子，且不是己方洞穴
         2. 没跨河，下一个地点是棋子，并且比自己小
         3. 跨河，中间没有棋子（老鼠），下一个地点同上述规则
         */
        Point point = chess.getPoint();
        boolean crossRiver = Math.abs(point.getX() - x) + Math.abs(point.getY() - y) > 1;
        if (units.size() == 1) {
            Unit unit = units.iterator().next();
            if (!crossRiver) {
                return singleUnitStrategy(unit, chess, chessComparator);
            }

            if (ratInRiver(gameBoard, point, new Point(x, y))) {
                return false;
            }
            return singleUnitStrategy(unit, chess, chessComparator);
        }

        /*
         叠子的情况，陷阱+棋子(岸上)，以下情况可以移动
         1. 没跨河，可以移动
         1. 跨河且河中没有棋子
         */

        return !crossRiver || !ratInRiver(gameBoard, point, new Point(x, y));
    }

    /*
     1. 下一个地点不是棋子
     2. 下一个地点是棋子，并且比自己小
     */
    private boolean singleUnitStrategy(Unit unit, Chess chess, Comparator<Chess> chessComparator) {
        boolean isChess = unit.getUnitType().equals(UnitType.CHESS);
        boolean isCave = unit.getUnitType().equals(UnitType.CAVE) && ((Cave) unit).isMaximizer() == chess.isMaximizer();
        if (!isChess && !isCave) return true;
        return isChess && chessComparator.compare(chess, (Chess) unit) >= 0;
    }

    private boolean ratInRiver(GameBoard gameBoard, Point src, Point dst) {
        int[] direction = new int[]{0, 0};
        if (src.getX() > dst.getX()) {
            direction = new int[]{-1, 0};
        } else if (src.getX() < dst.getY()) {
            direction = new int[]{1, 0};
        } else if (src.getY() > dst.getY()) {
            direction = new int[]{0, -1};
        } else if (src.getY() < dst.getY()) {
            direction = new int[]{0, 1};
        }

        int nextX = src.getX() + direction[0];
        int nextY = src.getY() + direction[1];

        while (nextX != dst.getX() || nextY != dst.getY()) {
            Set<Unit> riverUnits = gameBoard.findUnitAtPoint(nextX, nextY);
            if (riverUnits.size() > 1) {
                return true;
            }
            nextX += direction[0];
            nextY += direction[1];
        }
        return false;
    }
}
