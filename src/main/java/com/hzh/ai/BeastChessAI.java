package com.hzh.ai;

import com.hzh.game.GameBoard;
import com.hzh.game.Point;
import com.hzh.unit.chess.Chess;

import java.util.List;

public class BeastChessAI {
    private final GameBoard gameBoard= GameBoard.INSTANCE;
    public int minimax(int depth, int alpha, int beta, boolean maximizer) {
        if (depth == 0 || gameBoard.gameOver()) {
            return gameBoard.evaluate();
        }

        if (maximizer) {
            int max= Integer.MIN_VALUE;
            for(int i=0;i<GameBoard.BOARD_HEIGHT;i++){
                for(int j=0;j<GameBoard.BOARD_WIDTH;j++){
                    if(gameBoard.hasChess(i,j, true)){
                        Chess chess = gameBoard.getChess(i, j);
                        int originalX= chess.getPoint().getX();
                        int originalY= chess.getPoint().getY();
                        List<Point> points = chess.nextAvailableMoves();
                        for (Point point : points) {
                            // 执行移动
                            Chess willBeEaten = chess.move(point.getX(), point.getY());
                            int value = minimax(depth - 1, alpha, beta, false);
                            // 撤销移动 并恢复被吃的棋子
                            chess.move(originalX, originalY);
                            gameBoard.recoverChess(willBeEaten);

                            max= Math.max(max, value);
                            alpha= Math.max(alpha, value);
                            if(alpha>=beta){
                                return max;
                            }
                        }
                    }
                }
            }
            return max;
        }else{
            int min= Integer.MAX_VALUE;
            for(int i=0;i<GameBoard.BOARD_HEIGHT;i++){
                for(int j=0;j<GameBoard.BOARD_WIDTH;j++){
                    if(gameBoard.hasChess(i,j, false)){
                        Chess chess = gameBoard.getChess(i, j);
                        int originalX= chess.getPoint().getX();
                        int originalY= chess.getPoint().getY();
                        List<Point> points = chess.nextAvailableMoves();
                        for (Point point : points) {
                            // 执行移动
                            Chess willBeEaten = chess.move(point.getX(), point.getY());
                            int value = minimax(depth - 1, alpha, beta, true);
                            // 撤销移动 并恢复被吃的棋子
                            chess.move(originalX, originalY);
                            gameBoard.recoverChess(willBeEaten);

                            min= Math.min(min, value);
                            beta= Math.min(beta, value);
                            if(alpha>=beta){
                                return min;
                            }
                        }
                    }
                }
            }
            return min;
        }
    }

    public void move() {
        int max= Integer.MIN_VALUE;
        Chess bestChess= null;
        Point bestPoint= null;
        for(int i=0;i<GameBoard.BOARD_HEIGHT;i++){
            for(int j=0;j<GameBoard.BOARD_WIDTH;j++){
                if(gameBoard.hasChess(i,j, true)){
                    Chess chess = gameBoard.getChess(i, j);
                    int originalX= chess.getPoint().getX();
                    int originalY= chess.getPoint().getY();
                    List<Point> points = chess.nextAvailableMoves();
                    for (Point point : points) {
                        // 执行移动
                        Chess willBeEaten = chess.move(point.getX(), point.getY());
                        System.out.println("AI移动："+chess.getChessType().getName()+"->"+"("+point.getX()+","+point.getY()+")");
                        gameBoard.printBoard();
                        int value = minimax(10, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                        if(value>max){
                            max= value;
                            bestPoint= point;
                            bestChess= chess;
                        }
                        // 撤销移动 并恢复被吃的棋子
                        chess.move(originalX, originalY);
                        gameBoard.recoverChess(willBeEaten);
                        System.out.println("AI撤销："+chess.getChessType().getName()+"->"+"("+originalX+","+originalY+")");
                        gameBoard.printBoard();
                    }
                }
            }
        }
        if(bestPoint != null){
            bestChess.move(bestPoint.getX(), bestPoint.getY());
            System.out.println("AI移动："+bestChess.getChessType().getName()+"->"+"("+bestPoint.getX()+","+bestPoint.getY()+")");
        }
    }
}
