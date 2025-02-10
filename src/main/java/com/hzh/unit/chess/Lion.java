package com.hzh.unit.chess;

public class Lion extends AbstractBeastKing{

    public Lion(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public ChessType getChessType() {
        return ChessType.LION;
    }
}
