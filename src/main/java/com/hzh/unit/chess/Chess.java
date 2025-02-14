package com.hzh.unit.chess;


import com.hzh.moving.MovingStrategy;
import com.hzh.unit.Unit;

public interface Chess extends Unit {
    int getX();
    int getY();
    int value();
    boolean isMaximizer();
    ChessType getChessType();
    MovingStrategy getMovingStrategy();
    boolean isInDanger();
    int[][] nextAvailableMoves();
    Chess move(int x, int y);
}
