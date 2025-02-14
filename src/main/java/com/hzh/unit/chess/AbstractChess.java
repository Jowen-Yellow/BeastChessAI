package com.hzh.unit.chess;

import com.hzh.game.GameBoard;
import com.hzh.moving.MovingStrategy;
import com.hzh.unit.AbstractUnit;
import com.hzh.unit.UnitType;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public abstract class AbstractChess extends AbstractUnit implements Chess {
    private final boolean maximizer;
    private boolean inDanger;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private int[][] cacheDirections;
    private int lastX=-1;
    private int lastY=-1;

    @Override
    public UnitType getUnitType() {
        return UnitType.CHESS;
    }

    public AbstractChess(int x, int y, boolean maximizer) {
        super(x, y);
        this.maximizer = maximizer;
    }

    @Override
    public int value() {
        return this.getChessType().getValue() * (maximizer ? 1 : -1);
    }

    @Override
    public int[][] nextAvailableMoves() {
        int validCount = 0;
        int[][] candidates = new int[4][];
        int[][] directions = getDirections();

        for (int[] direction : directions) {
            int x = getX() + direction[0];
            int y = getY() + direction[1];

            if (getMovingStrategy().canMove(this, x, y)) {
                candidates[validCount++] = new int[]{x, y};
            }
        }

        return Arrays.copyOf(candidates, validCount);
    }

    public int[][] getDirections() {
        if(getX()==lastX && getY()==lastY){
            return cacheDirections;
        }

        GameBoard gameBoard = GameBoard.INSTANCE;
        int validCount = 0;
        int[][] temp = new int[4][2];
        for (int[] direction : DIRECTIONS) {
            if(gameBoard.isValidPosition(getX() + direction[0], getY() + direction[1])){
                temp[validCount][0] = direction[0];
                temp[validCount][1] = direction[1];
                validCount++;
            }
        }

        cacheDirections = Arrays.copyOf(temp, validCount);
        lastX = getX();
        lastY = getY();
        return cacheDirections;
    }

    /**
     * 2
     * 移动，请先检查是否可以移动
     */
    @Override
    public Chess move(int x, int y) {
        GameBoard gameBoard = GameBoard.INSTANCE;

        Chess willBeEaten = null;
        if (gameBoard.hasChess(x, y)) {
            willBeEaten = gameBoard.getChess(x, y);
        }

        // 更新棋盘
        gameBoard.applyMove(getX(), getY(), x, y);

        // 更新棋子位置
        this.getPoint().setX(x);
        this.getPoint().setY(y);
        return willBeEaten;
    }

    @Override
    public MovingStrategy getMovingStrategy() {
        return getChessType().getMovingStrategy();
    }

    @Override
    public boolean isInDanger() {
        return false;
    }
}
