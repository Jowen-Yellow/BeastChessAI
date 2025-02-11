package com.hzh.unit.chess;

import com.hzh.game.GameBoard;
import com.hzh.game.GameContextHolder;
import com.hzh.game.Point;
import com.hzh.moving.MovingStrategy;
import com.hzh.unit.AbstractUnit;
import com.hzh.unit.Unit;
import com.hzh.unit.UnitType;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public abstract class AbstractChess extends AbstractUnit implements Chess {
    private final boolean maximizer;
    private boolean inDanger;

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
    public List<Point> nextAvailableMoves() {
        GameBoard board = GameBoard.INSTANCE;
        List<Point> candidates = new ArrayList<>(4);

        int[][] directions = getDirections();

        for (int[] direction : directions) {
            int x = getX() + direction[0];
            int y = getY() + direction[1];

            if (!board.isValidPosition(x, y)) {
                continue;
            }

            if (getMovingStrategy().canMove(this, x, y)) {
                candidates.add(new Point(x, y));
            }
        }

        return candidates;
    }

    public int[][] getDirections() {
        GameBoard gameBoard = GameBoard.INSTANCE;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        return Arrays.stream(directions)
                .filter(direction -> gameBoard.isValidPosition(getX() + direction[0], getY() + direction[1]))
                .toArray(int[][]::new);
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
        gameBoard.applyMove(this.getPoint(), new Point(x, y));

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
