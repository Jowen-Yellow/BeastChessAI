package com.hzh;

import com.hzh.game.GameBoard;
import com.hzh.game.GameContext;

import java.util.ArrayDeque;
import java.util.Deque;

public class Game {
    static int count=0;
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
        Deque<Integer> deque = new ArrayDeque<>();
        deque.push(1);
        deque.push(2);
        deque.push(3);



        System.out.println(deque.peek());

    }
}
