package com.hzh.ai;

import com.hzh.game.GameBoard;
import com.hzh.game.GameContext;
import com.hzh.unit.chess.Chess;

import java.util.List;

public class BeastChessAI {
    public static final BeastChessAI INSTANCE = new BeastChessAI();
    private final GameBoard gameBoard = GameContext.GAME_BOARD;

    private final int MAX_DEPTH = 6;
    private int count = 0;

    /**
     * 估值函数
     *
     * @param depth     深度
     * @param alpha     alpha剪枝，表示当前节点的最大值
     * @param beta      beta剪枝，表示当前节点的最小值
     * @param maximizer 是否是最大值节点
     * @return 估值
     */
    public int minimax(int depth, int alpha, int beta, boolean maximizer) {
        count++;
        if (depth == 0 || gameBoard.gameOver()) {
            return gameBoard.evaluate();
        }

        if (maximizer) {
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < GameBoard.BOARD_HEIGHT; i++) {
                for (int j = 0; j < GameBoard.BOARD_WIDTH; j++) {
                    if (gameBoard.hasChess(i, j, true)) {
                        Chess chess = gameBoard.getChess(i, j);
                        int originalX = chess.getX();
                        int originalY = chess.getY();
                        int[][] moves = chess.nextAvailableMoves();
                        for (int[] move : moves) {
                            // 执行移动
                            Chess willBeEaten = gameBoard.applyMove(chess, move[0], move[1]);
                            int value = minimax(depth - 1, alpha, beta, false);
                            // 撤销移动 并恢复被吃的棋子
//                            gameBoard.undoMove(chess, originalX, originalY);
                            gameBoard.applyMove(chess, originalX, originalY);
                            gameBoard.recoverChess(willBeEaten);
                            max = Math.max(max, value);
                            alpha = Math.max(alpha, value);
                            if (alpha >= beta) {
                                return max;
                            }
                        }
                    }
                }
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < GameBoard.BOARD_HEIGHT; i++) {
                for (int j = 0; j < GameBoard.BOARD_WIDTH; j++) {
                    if (gameBoard.hasChess(i, j, false)) {
                        Chess chess = gameBoard.getChess(i, j);
                        int originalX = chess.getX();
                        int originalY = chess.getY();
                        int[][] moves = chess.nextAvailableMoves();
                        for (int[] move : moves) {
                            // 执行移动
                            Chess willBeEaten = gameBoard.applyMove(chess, move[0], move[1]);
                            int value = minimax(depth - 1, alpha, beta, true);
                            // 撤销移动 并恢复被吃的棋子
//                            gameBoard.undoMove(chess, originalX, originalY);
                            gameBoard.applyMove(chess, originalX, originalY);
                            gameBoard.recoverChess(willBeEaten);
                            min = Math.min(min, value);
                            beta = Math.min(beta, value);
                            if (alpha >= beta) {
                                return min;
                            }
                        }
                    }
                }
            }
            return min;
        }
    }

    public void move(boolean maximizer) {
        Chess bestChess = null;
        int[] bestPoint = {-1, -1};
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < GameBoard.BOARD_HEIGHT; i++) {
            for (int j = 0; j < GameBoard.BOARD_WIDTH; j++) {
                if (gameBoard.hasChess(i, j, maximizer)) {
                    Chess chess = gameBoard.getChess(i, j);
                    int originalX = chess.getX();
                    int originalY = chess.getY();
                    int[][] moves = chess.nextAvailableMoves();
                    for (int[] move : moves) {
                        // 执行移动
//                        gameBoard.applyMove(chess, move[0], move[1]);
                        Chess willBeEaten = gameBoard.applyMove(chess, move[0], move[1]);
                        int value = minimax(MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, !maximizer);
                        if (maximizer && value > max) {
                            max = value;
                            bestPoint[0] = move[0];
                            bestPoint[1] = move[1];
                            bestChess = chess;
                        } else if (!maximizer && value < min) {
                            min = value;
                            bestPoint[0] = move[0];
                            bestPoint[1] = move[1];
                            bestChess = chess;
                        }
                        // 撤销移动 并恢复被吃的棋子
//                        gameBoard.undoMove(chess, originalX, originalY);
                        gameBoard.applyMove(chess, originalX, originalY);
                        gameBoard.recoverChess(willBeEaten);
                    }

                }
            }
        }

        if (bestPoint[0] != -1 && bestPoint[1] != -1) {
            gameBoard.applyMove(bestChess, bestPoint[0], bestPoint[1]);
            System.out.println("AI移动：" + bestChess.getChessType().getName() + "->" + "(" + bestPoint[0] + "," + bestPoint[1] + ")");
        }
        System.out.println("AI计算次数：" + count);
    }

    private static boolean isRepetitive(List<int[]> moves) {
        if (moves.size() < 6) {
            return false;
        }
        int size = moves.size();
        return moves.get(size - 1)[0] == moves.get(size - 3)[0] && moves.get(size - 1)[1] == moves.get(size - 3)[1] &&
                moves.get(size - 2)[0] == moves.get(size - 4)[0] && moves.get(size - 2)[1] == moves.get(size - 4)[1] &&
                moves.get(size - 3)[0] == moves.get(size - 5)[0] && moves.get(size - 3)[1] == moves.get(size - 5)[1];
    }
}
