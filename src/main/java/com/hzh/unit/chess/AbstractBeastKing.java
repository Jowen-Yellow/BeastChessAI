package com.hzh.unit.chess;

import com.hzh.game.GameBoard;
import com.hzh.game.GameContextHolder;
import com.hzh.unit.Unit;
import com.hzh.unit.UnitType;

import java.util.Set;

public abstract class AbstractBeastKing extends AbstractChess{
    public AbstractBeastKing(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public int[][] getDirections() {
        GameBoard gameBoard = GameContextHolder.getGameBoard();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] direction : directions) {
            int x=getX()+direction[0];
            int y=getY()+direction[1];

            Set<Unit> units = gameBoard.findUnitAtPoint(x, y);
            if(units==null){
                continue;
            }

            int offsetX=direction[0];
            int offsetY=direction[1];
            while(units!=null&&units.stream().anyMatch(unit -> unit.getUnitType().equals(UnitType.RIVER))){
                offsetX+=direction[0];
                offsetY+=direction[1];
                x+=direction[0];
                y+=direction[1];
                units = gameBoard.findUnitAtPoint(x, y);
            }
            direction[0]=offsetX;
            direction[1]=offsetY;
        }
        return directions;
    }
}
