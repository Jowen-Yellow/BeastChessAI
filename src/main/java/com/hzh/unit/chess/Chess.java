package com.hzh.unit.chess;


import com.hzh.game.Point;
import com.hzh.moving.MovingStrategy;
import com.hzh.unit.Unit;

import java.util.Iterator;
import java.util.List;

public interface Chess extends Unit {
    int getX();
    int getY();
    int value();
    boolean isMaximizer();
    ChessType getChessType();
    MovingStrategy getMovingStrategy();
    boolean isInDanger();
    List<Point> nextAvailableMoves();
    Chess move(int x, int y);
}
