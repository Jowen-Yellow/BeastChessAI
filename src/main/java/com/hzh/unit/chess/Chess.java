package com.hzh.unit.chess;


import com.hzh.moving.MovingStrategy;

public interface Chess{
    int getX();
    int getY();
    void setX(int x);
    void setY(int y);
    int value();
    boolean isMaximizer();
    ChessType getChessType();
    MovingStrategy getMovingStrategy();
    boolean isInDanger();
    int[][] nextAvailableMoves();
}
