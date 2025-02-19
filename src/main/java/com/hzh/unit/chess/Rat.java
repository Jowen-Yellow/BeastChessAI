package com.hzh.unit.chess;

import com.hzh.game.GameBoard;
import com.hzh.game.GameContext;

public class Rat extends AbstractChess{

    public Rat(int x, int y, boolean maximizer) {
        super(x, y, maximizer);
    }

    @Override
    public boolean isInDanger() {
        GameBoard gameBoard = GameContext.GAME_BOARD;
        boolean inDanger=false;

        // 判断四个方向有没有威胁
        int[][] directions = getDirections();
        for(int[] direction : directions){
            int x = getX() + direction[0];
            int y = getY() + direction[1];

            if(gameBoard.hasChess(x,y,!isMaximizer())){
                Chess chess = gameBoard.getChess(x, y);
                if(gameBoard.chessCompare(this, chess)<=0){
                    inDanger=true;
                    break;
                }
            }
        }
        if(inDanger){
            return true;
        }

        // 判断敌方狮虎有没有威胁
        Chess enemyTiger = gameBoard.getChess(ChessType.TIGER, !isMaximizer());
        Chess enemyLion = gameBoard.getChess(ChessType.LION, !isMaximizer());
        boolean tigerThreaten = !gameBoard.chessDied(enemyTiger) || enemyTiger.getMovingStrategy().canMove(enemyTiger, getX(), getY());

        return tigerThreaten || !gameBoard.chessDied(enemyLion) || enemyLion.getMovingStrategy().canMove(enemyLion, getX(), getY());
    }

    @Override
    public ChessType getChessType() {
        return ChessType.RAT;
    }
}
