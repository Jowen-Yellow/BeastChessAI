package com.hzh.test;

import java.util.Random;

public class TicTacToe {
    private final long[][][] zobristTable = new long[3][3][3];
    private long playerHash;

    /*
    0: empty
    1: X
    2: O
     */
    private final int[][] board = {
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0}
    };

    public TicTacToe(){
        Random random = new Random(0xDEADBEEF);

        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
                    zobristTable[i][j][k] = random.nextLong();
                }
            }
        }

        playerHash = random.nextLong();
    }

    public long computeHash(int[][] board, boolean isXTurn){
        long hash = 0;
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                hash ^= zobristTable[i][j][board[i][j]];
            }
        }
        return isXTurn ? hash : hash ^ playerHash;
    }

    public void move(int x, int y, int player){
        board[x][y] = player;
        playerHash ^= zobristTable[x][y][player];
    }
}
