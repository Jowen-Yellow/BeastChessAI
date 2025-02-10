package com.hzh.unit.chess;

public class Tiger extends AbstractBeastKing{

    public Tiger(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public ChessType getChessType() {
        return ChessType.TIGER;
    }
}
