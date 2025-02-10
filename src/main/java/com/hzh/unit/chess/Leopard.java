package com.hzh.unit.chess;

public class Leopard extends AbstractChess{
    public Leopard(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public ChessType getChessType() {
        return ChessType.LEOPARD;
    }
}
