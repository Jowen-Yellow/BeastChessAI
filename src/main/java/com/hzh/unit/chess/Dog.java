package com.hzh.unit.chess;

public class Dog extends AbstractChess{

    public Dog(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public ChessType getChessType() {
        return ChessType.DOG;
    }
}
