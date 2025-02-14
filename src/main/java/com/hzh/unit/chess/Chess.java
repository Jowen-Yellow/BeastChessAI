package com.hzh.unit.chess;


import com.hzh.moving.MovingStrategy;

public interface Chess{
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
