package com.hzh.ai;

import com.hzh.game.GameBoard;
import com.hzh.game.GameContextHolder;
import com.hzh.game.Point;
import com.hzh.unit.chess.Chess;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class BeastChessAI {
    private final GameBoard gameBoard= GameContextHolder.getGameBoard();
    public int minimax(int depth, int alpha, int beta, boolean maximizer) {
        if (depth == 0 || gameBoard.gameOver()) {
            return gameBoard.evaluate();
        }

        if (maximizer) {
            int max= Integer.MIN_VALUE;
            Iterator<Chess> iterator = gameBoard.maximizerChessMap.values().iterator();
            while(iterator.hasNext()){
                Chess chess = iterator.next();
                // 记录当前坐标
                int originX = chess.getPoint().getX();
                int originY = chess.getPoint().getY();
                List<Point> points = chess.nextAvailableMoves();
                for (Point point : points) {
                    // 执行移动
                    chess.move(point.getX(), point.getY(),iterator);
                    int value = minimax(depth - 1, alpha, beta, false);
                    // 撤销移动
                    chess.move(originX, originY,iterator);

                    max= Math.max(max, value);
                    alpha= Math.max(alpha, value);
                    if(alpha>=beta){
                        return max;
                    }
                }
            }
            return max;
        }else{
            int min= Integer.MAX_VALUE;
            Iterator<Chess> iterator = gameBoard.minimizerChessMap.values().iterator();
            while(iterator.hasNext()){
                Chess chess = iterator.next();
                List<Point> points = chess.nextAvailableMoves();
                for (Point point : points) {
                    // 执行移动
                    chess.move(point.getX(), point.getY(),iterator);
                    int value = minimax(depth - 1, alpha, beta, true);
                    // 撤销移动
                    chess.move(chess.getPoint().getX(), chess.getPoint().getY(),iterator);

                    min= Math.min(min, value);
                    beta= Math.min(beta, value);
                    if(alpha>=beta){
                        return min;
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
        Iterator<Chess> iterator = gameBoard.maximizerChessMap.values().iterator();
        while(iterator.hasNext()){
            Chess chess = iterator.next();
            List<Point> points = chess.nextAvailableMoves();
            for (Point point : points) {
                // 执行移动
                chess.move(point.getX(), point.getY(),iterator);
                int value = minimax(2, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                if(value>max){
                    max= value;
                    bestPoint= point;
                    bestChess= chess;
                }
                // 撤销移动
                chess.move(chess.getPoint().getX(), chess.getPoint().getY(),iterator);
            }
        }
        if(bestPoint != null){
            bestChess.move(bestPoint.getX(), bestPoint.getY(),iterator);
            System.out.println("AI移动："+bestChess.getChessType().getName()+"->"+"("+bestPoint.getX()+","+bestPoint.getY()+")");
        }
    }
}
