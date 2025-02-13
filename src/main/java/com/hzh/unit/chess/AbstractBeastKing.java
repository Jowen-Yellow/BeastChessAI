package com.hzh.unit.chess;

import com.hzh.game.GameBoard;

public abstract class AbstractBeastKing extends AbstractChess{
    private int[][] cacheDirections;

    public AbstractBeastKing(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public int[][] getDirections() {
        if(getX()==getLastX() && getY()==getLastY()){
            return cacheDirections;
        }

        GameBoard gameBoard = GameBoard.INSTANCE;
        int[][] directions = super.getDirections();
        for (int[] direction : directions) {
            int x=getX()+direction[0];
            int y=getY()+direction[1];

            int offsetX=direction[0];
            int offsetY=direction[1];
            while(gameBoard.isRiver(x, y)){
                offsetX+=direction[0];
                offsetY+=direction[1];
                x+=direction[0];
                y+=direction[1];
            }
            direction[0]=offsetX;
            direction[1]=offsetY;
        }

        cacheDirections=directions;
        setLastX(getX());
        setLastY(getY());
        return cacheDirections;
    }
}
