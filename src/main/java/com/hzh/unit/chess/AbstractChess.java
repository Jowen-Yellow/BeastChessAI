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
        return this.getChessType().getValue();
    }

    @Override
    public List<Point> nextAvailableMoves() {
        GameBoard board = GameContextHolder.getGameBoard();
        List<Point> candidates = new ArrayList<>(4);

        int[][] directions = getDirections();

        for (int[] direction : directions) {
            int x=getX()+direction[0];
            int y=getY()+direction[1];

            if (!board.isValidPosition(x, y)){
                continue;
            }

            if(getMovingStrategy().canMove(this,x,y)){
                candidates.add(new Point(x, y));
            }
        }

        return candidates;
    }

    public int[][] getDirections(){
        return new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    }

    /**
     * 移动，请先检查是否可以移动
     */
    @Override
    public void move(int x, int y, Iterator<Chess> iterator) {
        GameBoard gameBoard = GameContextHolder.getGameBoard();
        // 删除当前位置棋子
        Set<Unit> currentUnits = gameBoard.findUnitAtPoint(getX(), getY());

        if(currentUnits.size()==1){
            gameBoard.removeUnitsAtPoint(getX(), getY());
        }else{
            currentUnits.removeIf(unit -> unit.getUnitType().equals(UnitType.CHESS));
        }

        if(iterator == null){
            if(maximizer){
                gameBoard.maximizerChessMap.remove(new Point(x, y));
            }else{
                gameBoard.minimizerChessMap.remove(new Point(x, y));
            }
        }else {
            iterator.remove();
        }
        gameBoard.getChessMap().remove(this.getPoint());

        Point point = this.getPoint();
        point.setX(x);
        point.setY(y);

        // 添加到新位置
        Set<Unit> nextUnits = gameBoard.findUnitAtPoint(x, y);
        if(nextUnits != null){
            nextUnits.removeIf(unit -> unit.getUnitType().equals(UnitType.CHESS)); // 移除棋子
            nextUnits.add(this);
        }else {
            gameBoard.getBoard().put(new Point(x, y), new HashSet<>(Collections.singleton(this)));
        }
        gameBoard.getChessMap().put(new Point(x, y), this);
        if(this.isMaximizer()){
            gameBoard.maximizerChessMap.put(new Point(x, y), this);
        }else{
            gameBoard.minimizerChessMap.put(new Point(x, y), this);
        }
    }

    @Override
    public MovingStrategy getMovingStrategy() {
        return getChessType().getMovingStrategy();
    }

    @Override
    public boolean isInDanger(){
        GameBoard gameBoard = GameContextHolder.getGameBoard();
        Comparator<Chess> chessComparator = gameBoard.chessComparator();

        List<Chess> list = gameBoard.chessMap.values().stream()
                .filter(chess -> chess.isMaximizer() != this.isMaximizer())
                .filter(chess -> chessComparator.compare(chess, this) >= 0)
                .filter(chess -> chess.nextAvailableMoves().stream().anyMatch(point -> point.equals(this.getPoint())))
                .toList();

        return !list.isEmpty();
    }
}
