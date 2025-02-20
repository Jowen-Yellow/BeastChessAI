package com.hzh;

import com.hzh.game.GameBoard;
import com.hzh.game.GameContext;

public class Game {
    public static void main(String[] args) {
        GameBoard gameBoard = GameContext.GAME_BOARD;
        // 对面蓝方
        GameBoard.isMaximizer=false;
        GameBoard.isMyTurn=true;

//        // 对面红方
//        GameBoard.isMaximizer=false;
//        GameBoard.isMyTurn=false;

        while (!gameBoard.gameOver()) {
            gameBoard.printBoard();
            gameBoard.nextTurn();
        }
//
////        if(gameBoard.win()){
////            System.out.println("你赢了");
////        }else{
////            System.out.println("你输了");
////        }
    }
}
