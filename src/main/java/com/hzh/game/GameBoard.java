package com.hzh.game;

import com.hzh.ai.BeastChessAI;
import com.hzh.unit.chess.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class GameBoard {
    private final Comparator<Chess> chessComparator;
    public static boolean isMyTurn = true;
    public static boolean isMaximizer = true;
    public static final int BOARD_WIDTH = 7;
    public static final int BOARD_HEIGHT = 9;
    private final long[][][] zobristTable = new long[BOARD_HEIGHT][BOARD_WIDTH][17];
    private long playerHash;
    private long currentHash;
    private Chess repetitionChess;
    private final List<Long> hashHistory = new ArrayList<>();

    private final int[][] DEFAULT_CHESS = {
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

    private final int[][] chess;

    private final int[][] river = {
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

    private final int[][] traps = {
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

    private final int[][] caves = {
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
        this.chess = DEFAULT_CHESS;
        initialize();
        this.chessComparator = chessComparator();
    }

    public GameBoard(int[][] chess) {
        this.chess = chess;
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
        boolean xInPlace = maximizer ? x >= BOARD_HEIGHT - 2 : x < 2;
        return xInPlace && traps[x][y] == 1;
    }

    public boolean isChessTrapped(int x, int y, boolean maximizer) {
        boolean xInPlace = maximizer ? x < 2 : x > 6;
        return xInPlace && chess[x][y] % 100 != 0;
    }

    public boolean hasChess(int x, int y) {
        return chess[x][y] != 0;
    }

    public boolean hasChess(int x, int y, boolean maximizer) {
        return chess[x][y] != 0 && (maximizer ? chess[x][y] > 0 : chess[x][y] < 0);
    }

    public boolean isChess(int x, int y) {
        return traps[x][y] == 0 && caves[x][y] == 0 && river[x][y] == 0 && chess[x][y] != 0;
    }

    public boolean isChess(int x, int y, boolean maximizer) {
        return traps[x][y] == 0 && caves[x][y] == 0 && river[x][y] == 0 && (maximizer ? chess[x][y] > 0 : chess[x][y] < 0);
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

    public void applyMove(int srcX, int srcY, int dstX, int dstY) {
        chess[dstX][dstY] = chess[srcX][srcY];
        chess[srcX][srcY] = 0;
    }

    public Chess applyMove(Chess c, int dstX, int dstY) {
        int srcX = c.getX();
        int srcY = c.getY();

        Chess willBeEaten = null;
        // 记录被吃的棋子
        if (this.hasChess(dstX, dstY)) {
            willBeEaten = this.getChess(dstX, dstY);
        }

        // 更新棋盘
        chess[dstX][dstY] = c.value();
        chess[srcX][srcY] = 0;

        // 更新棋子位置
        c.setX(dstX);
        c.setY(dstY);

        // 移动后记录棋局状态
        currentHash ^= zobristTable[srcX][srcY][convertValue(c.value())];
        currentHash ^= zobristTable[dstX][dstY][convertValue(c.value())];
        hashHistory.add(currentHash);

        return willBeEaten;
    }

    public void undoMove(Chess c, int srcX, int srcY, Chess isEaten) {
        int dstX = c.getX();
        int dstY = c.getY();

        // 恢复棋局状态
        currentHash ^= zobristTable[dstX][dstY][convertValue(c.value())];
        currentHash ^= zobristTable[srcX][srcY][convertValue(c.value())];
        hashHistory.remove(hashHistory.size() - 1);

        // 更新棋盘
        chess[srcX][srcY] = c.value();
        chess[dstX][dstY] = 0;

        // 更新棋子位置
        c.setX(srcX);
        c.setY(srcY);

        // 恢复被吃的棋子
        if (isEaten == null) return;
        chess[isEaten.getX()][isEaten.getY()] = isEaten.value();
    }

    // 长捉检测，
    public boolean detectRepetition() {
        int size = hashHistory.size();
        if (size < 8) {
            return false;
        }
        return  hashHistory.get(size - 1).equals(hashHistory.get(size - 5)) &&
                hashHistory.get(size - 2).equals(hashHistory.get(size - 6)) &&
                hashHistory.get(size - 3).equals(hashHistory.get(size - 7)) &&
                hashHistory.get(size - 4).equals(hashHistory.get(size - 8));
    }

    public int evaluate() {
        int score = 0;
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (hasChess(i, j)) {
                    Chess chess = getChess(i, j);
                    score += this.chess[i][j];
                    // 前进奖励
                    if (chess.isMaximizer()) {
                        score += (BOARD_HEIGHT - i - 1) * (chess.value() / 10);
                    } else {
                        score += i * (chess.value() / 10);
                    }
                    if (chess.getChessType().equals(ChessType.RAT)) {
                        // 控制河流
                        if (isRiver(i, j)) {
                            score += chess.isMaximizer() ? 100 : -100;
                        }
//                        // 优先保护老鼠
//                        if (chess.isInDanger()) {
//                            score += chess.isMaximizer() ? 500 : -500;
//                        }
                    }
                    // 优先攻占敌方洞穴
                    if (isCave(i, j)) {
                        score += chess.isMaximizer() ? 2000 : -2000;
                    }
//                    // 优先占领我方陷阱位置
//                    if (isTrap(i, j)) {
//                        score += chess.isMaximizer() ? 50 : -50;
//                    }
                }
            }
        }
        return score;
    }


    public boolean win(boolean maximizer) {
        return maximizer ? chess[0][3] != 0 : chess[8][3] != 0;
    }

    public boolean lose(boolean maximizer) {
        return maximizer ? chess[8][3] != 0 : chess[0][3] != 0;
    }

    public boolean gameOver() {
        return chess[0][3] != 0 || chess[8][3] != 0;
    }

    public void printBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                String name = "";
                String color = "\033[0m";
                if (hasChess(i, j)) {
                    Chess chess = getChess(i, j);
                    color = chess.isMaximizer() ? "\033[34m" : "\033[31m";
                    name = chess.getChessType().getName();
                } else if (isRiver(i, j)) {
                    name = "河";
                } else if (isCave(i, j)) {
                    color = i == 0 ? "\033[31m" : "\033[34m";
                    name = "穴";
                } else if (isTrap(i, j)) {
                    color = i < BOARD_HEIGHT / 2 ? "\033[31m" : "\033[34m";
                    name = "陷";
                } else if (isBlank(i, j)) {
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

    public void selectSide() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1:红方");
        System.out.println("2:蓝方");
        System.out.print("请选择阵营：");
        int camp = scanner.nextInt();
        if (camp != 1 && camp != 2) {
            System.out.println("请选择正确的阵营");
            return;
        }
        isMaximizer = camp == 2;
    }

    public void nextTurn() {
        if (!isMyTurn) {
            BeastChessAI.INSTANCE.move(!isMaximizer);
            isMyTurn = true;
            return;
        }

        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < ChessType.values().length; i++) {
            System.out.println(i + 1 + ":" + ChessType.values()[i].getName());
        }
        System.out.print("请选择棋子：");
        int chessType = scanner.nextInt();
        Chess chess = getChess(ChessType.values()[chessType - 1], isMaximizer);
        if (chessDied(chess)) {
            System.out.println("该棋子已被吃掉");
            return;
        }else if(chess==repetitionChess){
            System.out.println("禁止长捉");
            return;
        }

        int[][] moves = chess.nextAvailableMoves();
        if (moves == null) {
            System.out.println("无可移动方向");
            return;
        }
        Map<String, int[]> pointMap = new LinkedHashMap<>();
        for (int[] move : moves) {
            if (move[0] < chess.getX()) {
                pointMap.put("上", move);
            } else if (move[0] > chess.getX()) {
                pointMap.put("下", move);
            } else if (move[1] < chess.getY()) {
                pointMap.put("左", move);
            } else if (move[1] > chess.getY()) {
                pointMap.put("右", move);
            }
        }

        List<String> directions = pointMap.keySet().stream().toList();
        for (int i = 0; i < directions.size(); i++) {
            System.out.println(i + 1 + ":" + directions.get(i));
        }
        System.out.print("请选择方向：");
        int direction = scanner.nextInt();
        if (direction < 1 || direction > directions.size()) {
            System.out.println("请选择正确的方向");
            return;
        }
        repetitionChess=null;
        applyMove(chess, pointMap.get(directions.get(direction - 1))[0], pointMap.get(directions.get(direction - 1))[1]);
        if(detectRepetition()){
            repetitionChess=chess;
        }
        isMyTurn = false;
    }

    private void initialize() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Chess c = switch (chess[i][j]) {
                    case 100 -> new Rat(i, j, true);
                    case 200 -> new Cat(i, j, true);
                    case 300 -> new Dog(i, j, true);
                    case 400 -> new Wolf(i, j, true);
                    case 500 -> new Leopard(i, j, true);
                    case 600 -> new Tiger(i, j, true);
                    case 700 -> new Lion(i, j, true);
                    case 800 -> new Elephant(i, j, true);
                    case -100 -> new Rat(i, j, false);
                    case -200 -> new Cat(i, j, false);
                    case -300 -> new Dog(i, j, false);
                    case -400 -> new Wolf(i, j, false);
                    case -500 -> new Leopard(i, j, false);
                    case -600 -> new Tiger(i, j, false);
                    case -700 -> new Lion(i, j, false);
                    case -800 -> new Elephant(i, j, false);
                    default -> null;
                };
                if (c != null) {
                    chessMap.put(c.value(), c);
                }
            }
        }

        Random random = new Random(0xDEADBEEF);
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                for (int k = 0; k < 16; k++) {
                    zobristTable[i][j][k] = random.nextLong();
                }
            }
        }

        playerHash = random.nextLong();

        currentHash = computeHash(isMaximizer);

        hashHistory.add(currentHash);

        GameContext.setGameBoard(this);
    }

    private long computeHash(boolean isMaximizer) {
        long hash = 0;
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                hash ^= zobristTable[i][j][convertValue(chess[i][j])];
            }
        }
        return isMaximizer ? hash : hash ^ playerHash;
    }

    private int convertValue(int value) {
        return switch (value) {
            case 100 -> 1;
            case 200 -> 2;
            case 300 -> 3;
            case 400 -> 4;
            case 500 -> 5;
            case 600 -> 6;
            case 700 -> 7;
            case 800 -> 8;
            case -100 -> 9;
            case -200 -> 10;
            case -300 -> 11;
            case -400 -> 12;
            case -500 -> 13;
            case -600 -> 14;
            case -700 -> 15;
            case -800 -> 16;
            default -> 0;
        };
    }

    private Comparator<Chess> chessComparator() {
        return (o1, o2) -> {
            if (o1.isMaximizer() == o2.isMaximizer()) {
                return -1;
            }
            if (o1.getChessType().equals(ChessType.RAT) && o2.getChessType().equals(ChessType.ELEPHANT)) {
                return 1;
            } else if (o1.getChessType().equals(ChessType.ELEPHANT) && o2.getChessType().equals(ChessType.RAT)) {
                return -1;
            }
            return Integer.compare(Math.abs(o1.value()), Math.abs(o2.value()));
        };
    }
}
