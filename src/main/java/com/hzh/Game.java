package com.hzh;

import com.hzh.ai.BeastChessAI;
import com.hzh.game.GameBoard;
import com.hzh.game.GameContextHolder;
import com.hzh.game.Point;
import com.hzh.unit.chess.Chess;
import com.hzh.unit.chess.ChessType;
import com.hzh.unit.chess.Lion;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.List;

public class Game {
    static int count=0;
    public static void main(String[] args) {
        BeastChessAI beastChessAI = new BeastChessAI();
        GameContextHolder.setBeastChessAI(beastChessAI);
        GameBoard gameBoard = GameBoard.INSTANCE;

//        Lion chess = (Lion)gameBoard.getChess(ChessType.LION, true);
//        int[][] directions = chess.getDirections();
//        for (int[] direction : directions) {
//            System.out.println(direction[0]+","+direction[1]);
//        }


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
//        TreeNode root=new TreeNode();
//        root.left=new TreeNode();
//        root.right=new TreeNode();
//
//        root.left.left=new TreeNode(5);
//        root.left.right=new TreeNode(3);
//
//        root.right.left=new TreeNode(2);
//        root.right.right=new TreeNode(9);
//
//        int result = minimax(root, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
//        System.out.println(result);
//        System.out.println(count);
    }

    static class TreeNode{
        int value;
        TreeNode left;
        TreeNode right;
        TreeNode(){

        }
        TreeNode(int value){
            this.value=value;
        }
    }

    private static int minimax(TreeNode root, int depth, int alpha, int beta, boolean maximizer) {
        count++;
        if (depth == 0 || root.left == null || root.right == null) {
            return root.value;
        }

        if (maximizer) {
            int max = Integer.MIN_VALUE;
            int v = minimax(root.left, depth - 1, alpha, beta, false);
            max = Math.max(max, v);
            alpha = Math.max(alpha, v);
            if (alpha >= beta) {
                return max;
            }
            v = minimax(root.right, depth - 1, alpha, beta, false);
            max = Math.max(max, v);
            alpha = Math.max(alpha, v);
            if (alpha >= beta) {
                return max;
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            int v = minimax(root.left, depth - 1, alpha, beta, true);
            min = Math.min(min, v);
            beta = Math.min(beta, v);
            if (alpha >= beta) {
                return min;
            }
            v = minimax(root.right, depth - 1, alpha, beta, true);
            min = Math.min(min, v);
            beta = Math.min(beta, v);
            if (alpha >= beta) {
                return min;
            }
            return min;
        }
    }
}
