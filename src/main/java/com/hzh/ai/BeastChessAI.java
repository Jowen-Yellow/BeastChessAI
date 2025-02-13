package com.hzh.ai;

import com.hzh.game.GameBoard;
import com.hzh.game.Point;
import com.hzh.unit.chess.Chess;

public class BeastChessAI {
    private final GameBoard gameBoard= GameBoard.INSTANCE;
    private int count =0;

    /**
     * 估值函数
     * @param depth 深度
     * @param alpha alpha剪枝，表示当前节点的最大值
     * @param beta
     * @param maximizer
     * @return
     */
    public int minimax(int depth, int alpha, int beta, boolean maximizer) {
        count++;
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
                        int[][] moves = chess.nextAvailableMoves();
                        for (int[] move : moves) {
                            // 执行移动
                            Chess willBeEaten = chess.move(move[0], move[1]);
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
                        int[][] moves = chess.nextAvailableMoves();
                        for (int[] move : moves) {
                            // 执行移动
                            Chess willBeEaten = chess.move(move[0], move[1]);
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
        Point bestPoint= new Point(-1,-1);
        for(int i=0;i<GameBoard.BOARD_HEIGHT;i++){
            for(int j=0;j<GameBoard.BOARD_WIDTH;j++){
                if(gameBoard.hasChess(i,j, true)){
                    Chess chess = gameBoard.getChess(i, j);
                    int originalX= chess.getPoint().getX();
                    int originalY= chess.getPoint().getY();
                    int[][] moves = chess.nextAvailableMoves();
                    for (int[] move : moves) {
                        // 执行移动
                        Chess willBeEaten = chess.move(move[0], move[1]);
                        int value = minimax(8, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                        if(value>max){
                            max= value;
                            bestPoint.setX(move[0]);
                            bestPoint.setY(move[1]);
                            bestChess= chess;
                        }
                        // 撤销移动 并恢复被吃的棋子
                        chess.move(originalX, originalY);
                        gameBoard.recoverChess(willBeEaten);
                    }
                }
            }
        }
        if(bestPoint.getX()!=-1 && bestPoint.getY()!=-1){
            bestChess.move(bestPoint.getX(), bestPoint.getY());
            System.out.println("AI移动："+bestChess.getChessType().getName()+"->"+"("+bestPoint.getX()+","+bestPoint.getY()+")");
        }
        System.out.println("AI计算次数："+count);
    }
}
