package com.hzh.unit.chess;

public class Wolf extends AbstractChess{

    public Wolf(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public ChessType getChessType() {
        return ChessType.WOLF;
    }
}
