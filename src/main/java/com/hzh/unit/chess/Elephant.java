package com.hzh.unit.chess;

public class Elephant extends AbstractChess{

    public Elephant(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public ChessType getChessType() {
        return ChessType.ELEPHANT;
    }
}
