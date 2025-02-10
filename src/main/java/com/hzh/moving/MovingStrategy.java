package com.hzh.moving;

import com.hzh.unit.chess.Chess;

public interface MovingStrategy {
    boolean canMove(Chess chess, int x, int y);
}
