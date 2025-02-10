package com.hzh.unit.chess;

public class Cat extends AbstractChess{

    public Cat(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public ChessType getChessType() {
        return ChessType.CAT;
    }
}
