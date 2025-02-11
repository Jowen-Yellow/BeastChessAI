package com.hzh;

import com.hzh.ai.BeastChessAI;
import com.hzh.game.GameBoard;
import com.hzh.game.GameContextHolder;

public class Game {
    public static void main(String[] args) {
        BeastChessAI beastChessAI = new BeastChessAI();
        GameContextHolder.setBeastChessAI(beastChessAI);
        GameBoard gameBoard = GameBoard.INSTANCE;

        while (!gameBoard.gameOver()) {
            gameBoard.printBoard();
            gameBoard.nextTurn();
//            isMyTurn = !isMyTurn;
        }

//        if(gameBoard.win()){
//            System.out.println("你赢了");
//        }else{
//            System.out.println("你输了");
//        }
    }

}
