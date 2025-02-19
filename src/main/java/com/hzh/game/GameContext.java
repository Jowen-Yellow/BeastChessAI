package com.hzh.game;

public class GameContext {
    public static GameBoard GAME_BOARD = new GameBoard();
    public static void setGameBoard(GameBoard gameBoard){
        GAME_BOARD=gameBoard;
    }
}
