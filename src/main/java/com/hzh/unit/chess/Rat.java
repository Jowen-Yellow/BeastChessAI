package com.hzh.unit.chess;

public class Rat extends AbstractChess{

    public Rat(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public ChessType getChessType() {
        return ChessType.RAT;
    }
}
