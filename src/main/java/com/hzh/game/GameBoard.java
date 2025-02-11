package com.hzh.game;

import com.hzh.unit.*;
import com.hzh.unit.chess.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class GameBoard {
    private Comparator<Chess> chessComparator;
    private boolean isMyTurn = true;
    public static final int BOARD_WIDTH = 7;
    public static final int BOARD_HEIGHT = 9;
    public static final GameBoard INSTANCE = new GameBoard();
    @Getter
    @Setter
    private int evaluation = 0;

    private int[][] chess = {
            {-700, 0, 0, 0, 0, 0, -600},
            {0, -300, 0, 0, 0, -200, 0},
            {-100, 0, -500, 0, -400, 0, -800},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {800, 0, 400, 0, 500, 0, 100},
            {0, 200, 0, 0, 0, 300, 0},
            {600, 0, 0, 0, 0, 0, 700}
    };

    private int[][] river = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 0, 1, 1, 0},
            {0, 1, 1, 0, 1, 1, 0},
            {0, 1, 1, 0, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
    };

    private int[][] traps = {
            {0, 0, 1, 0, 1, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 1, 0, 1, 0, 0}
    };

    private int[][] caves = {
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0}
    };

    public final Map<Integer, Chess> chessMap = new HashMap<>(16);

    public GameBoard() {
        initialize();
        this.chessComparator = chessComparator();
    }

    public boolean isBlank(int x, int y) {
        return chess[x][y] == 0 && river[x][y] == 0 && traps[x][y] == 0 && caves[x][y] == 0;
    }

    public boolean isRiver(int x, int y) {
        return river[x][y] == 1;
    }

    public boolean isCave(int x, int y) {
        return caves[x][y] == 1;
    }

    public boolean isCave(int x, int y, boolean maximizer) {
        boolean xInPlace = maximizer ? x == BOARD_HEIGHT - 1 : x == 0;
        return xInPlace && caves[x][y] == 1;
    }

    public boolean isTrap(int x, int y) {
        return traps[x][y] == 1;
    }

    public boolean isTrap(int x, int y, boolean maximizer) {
        boolean xInPlace = maximizer ? x >= BOARD_HEIGHT-2: x < 2;
        return xInPlace && traps[x][y] == 1;
    }

    public boolean isChessTrapped(int x, int y, boolean maximizer) {
        boolean xInPlace = maximizer ? x < 2 : x > 6;
        return xInPlace && chess[x][y] % 100 != 0;
    }

    public boolean hasChess(int x, int y) {
        return chess[x][y]!=0;
    }

    public boolean hasChess(int x, int y, boolean maximizer) {
        return chess[x][y] != 0 && (maximizer ? chess[x][y] > 0 : chess[x][y] < 0);
    }

    public boolean isChess(int x, int y) {
        return traps[x][y]==0 && caves[x][y]==0 && river[x][y]==0 && chess[x][y] != 0;
    }

    public boolean isChess(int x, int y, boolean maximizer) {
        return traps[x][y]==0 && caves[x][y]==0 && river[x][y]==0 && (maximizer ? chess[x][y] > 0 : chess[x][y] < 0);
    }

    public Chess getChess(int x, int y) {
        return chessMap.get(chess[x][y]);
    }
    public Chess getChess(ChessType chessType, boolean maximizer) {
        return chessMap.get(chessType.getValue() * (maximizer ? 1 : -1));
    }

    public boolean chessDied(Chess c) {
        return chess[c.getX()][c.getY()] != c.value();
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < BOARD_HEIGHT && y >= 0 && y < BOARD_WIDTH;
    }

    public int chessCompare(Chess chess1, Chess chess2) {
        return chessComparator.compare(chess1, chess2);
    }
    public void applyMove(Point src, Point dst){
        chess[dst.getX()][dst.getY()] = chess[src.getX()][src.getY()];
        chess[src.getX()][src.getY()] = 0;
    }
    public void recoverChess(Chess c){
        if(c==null)return;
        chess[c.getX()][c.getY()] = c.value();
    }
    public int evaluate() {
        int score = 0;
        for(int i=0; i<BOARD_HEIGHT; i++){
            for(int j=0; j<BOARD_WIDTH; j++){
                if(hasChess(i,j)){
                    Chess chess = getChess(i, j);
                    score += this.chess[i][j];
                    if (chess.getChessType().equals(ChessType.RAT)) {
                        score += chess.isMaximizer() ? 800 : -800;
                    }
                }
            }
        }
        return score;
    }


    public boolean win(boolean maximizer) {
        return maximizer ? chess[0][3] !=0 : chess[8][3] !=0;
    }

    public boolean lose(boolean maximizer) {
        return maximizer ? chess[8][3] !=0 : chess[0][3] !=0;
    }

    public boolean gameOver() {
        return chess[0][3] != 0 || chess[8][3] != 0;
    }

    public void printBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                String name="";
                String color="\033[0m";
                if(hasChess(i,j)){
                    Chess chess = getChess(i, j);
                    color = chess.isMaximizer() ? "\033[34m" : "\033[31m";
                    name = chess.getChessType().getName();
                }else if(isRiver(i,j)){
                    name = "河";
                }else if(isCave(i,j)){
                    color = i==0 ? "\033[31m" : "\033[34m";
                    name = "穴";
                }else if(isTrap(i,j)){
                    color = i<BOARD_HEIGHT/2 ? "\033[31m" : "\033[34m";
                    name = "陷";
                }else if(isBlank(i,j)){
                    name = "地";
                }
                System.out.print(color + name + "\t");
            }
            System.out.println();
        }

        System.out.print("\033[0m");
        System.out.println("当前评分：" + evaluate());
        System.out.println("--------------------------------------------");
    }

    public void nextTurn() {
//        if (!isMyTurn) {
//            GameContextHolder.beastChessAI.move();
//            isMyTurn = true;
//            return;
//        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("1:红方");
        System.out.println("2:蓝方");
        System.out.print("请选择阵营：");
        int camp = scanner.nextInt();
        boolean isMaximizer = camp == 2;
        for (int i = 0; i < ChessType.values().length; i++) {
            System.out.println(i + 1 + ":" + ChessType.values()[i].getName());
        }
        System.out.print("请选择棋子：");
        int chessType = scanner.nextInt();
        Chess chess = getChess(ChessType.values()[chessType-1], isMaximizer);
        if(chessDied(chess)){
            System.out.println("该棋子已被吃掉");
            return;
        }

        List<Point> points = chess.nextAvailableMoves();
        Map<String, Point> pointMap = new HashMap<>();
        for (Point point : points) {
            if (point.getX() < chess.getPoint().getX()) {
                pointMap.put("上", point);
            } else if (point.getX() > chess.getPoint().getX()) {
                pointMap.put("下", point);
            } else if (point.getY() < chess.getPoint().getY()) {
                pointMap.put("左", point);
            } else if (point.getY() > chess.getPoint().getY()) {
                pointMap.put("右", point);
            }
        }
        List<String> directions = pointMap.keySet().stream().toList();
        if (directions.isEmpty()) {
            System.out.println("无可移动方向");
            return;
        }
        for (int i = 0; i < directions.size(); i++) {
            System.out.println(i + 1 + ":" + directions.get(i));
        }
        System.out.print("请选择方向：");
        int direction = scanner.nextInt();
        chess.move(pointMap.get(directions.get(direction - 1)).getX(), pointMap.get(directions.get(direction - 1)).getY());
        isMyTurn = false;
    }

    private void initialize() {
        final List<Chess> chessList = new ArrayList<>(16);

        // 红方棋子
        chessList.add(new Lion(0, 0, false));
        chessList.add(new Rat(2, 0, false));
        chessList.add(new Dog(1, 1, false));
        chessList.add(new Leopard(2, 2, false));
        chessList.add(new Wolf(2, 4, false));
        chessList.add(new Cat(1, 5, false));
        chessList.add(new Tiger(0, 6, false));
        chessList.add(new Elephant(2, 6, false));
        // 蓝方棋子
        chessList.add(new Lion(8, 6, true));
        chessList.add(new Rat(6, 6, true));
        chessList.add(new Dog(7, 5, true));
        chessList.add(new Leopard(6, 4, true));
        chessList.add(new Wolf(6, 2, true));
        chessList.add(new Cat(7, 1, true));
        chessList.add(new Tiger(8, 0, true));
        chessList.add(new Elephant(6, 0, true));

        chessList.forEach(chess -> {
            if (chess.isMaximizer()) {
                chessMap.put(chess.value(), chess);
            } else {
                chessMap.put(-chess.value(), chess);
            }
        });
    }
    private Comparator<Chess> chessComparator() {
        return (o1, o2) -> {
            if (o1.isMaximizer() == o2.isMaximizer()) {
                return -1;
            }
            if (o1 instanceof Rat && o2 instanceof Elephant) {
                return 1;
            } else if (o1 instanceof Elephant && o2 instanceof Rat) {
                return -1;
            }
            return Integer.compare(o1.value(), o2.value());
        };
    }
}
