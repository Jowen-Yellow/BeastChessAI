package com.hzh.game;

import com.hzh.ai.BeastChessAI;
import com.hzh.unit.*;
import com.hzh.unit.chess.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class GameBoard {
    public final boolean maximizer;
    public static final int BOARD_WIDTH = 7;
    public static final int BOARD_HEIGHT = 9;
    @Getter
    @Setter
    private int evaluation = 0;

    @Getter
    public final Map<Point, Set<Unit>> board = new HashMap<>(36);

    @Getter
    public final Map<Point, Chess> chessMap = new HashMap<>(16);
    public final Map<Point, Chess> maximizerChessMap = new HashMap<>(8);
    public final Map<Point, Chess> minimizerChessMap = new HashMap<>(8);

    public GameBoard(boolean maximizer) {
        this.maximizer = maximizer;
        initialize();
    }

    public Set<Unit> findUnitAtPoint(int x, int y) {
        return board.get(new Point(x, y));
    }
    public void removeUnitsAtPoint(int x, int y) {
        board.remove(new Point(x, y));
    }

    public boolean isChessTrapped(int x, int y, boolean maximizer) {
        Set<Unit> units = board.get(new Point(x, y));
        Iterator<Unit> iterator = units.iterator();
        Unit unit1 = iterator.next();
        Unit unit2 = iterator.next();
        if (unit1.getUnitType().equals(UnitType.TRAP) && unit2.getUnitType().equals(UnitType.CHESS)) {
            return ((Trap) unit1).isMaximizer() == maximizer
                    && ((Chess) unit2).isMaximizer() != maximizer;
        } else if (unit2.getUnitType().equals(UnitType.TRAP) && unit1.getUnitType().equals(UnitType.CHESS)) {
            return ((Trap) unit2).isMaximizer() == maximizer
                    && ((Chess) unit1).isMaximizer() != maximizer;
        }
        return false;
    }

    public boolean isNextToRiver(int x, int y) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] direction : directions) {
            int px = x + direction[0];
            int py = y + direction[1];

            if (isValidPosition(px, py) && board.get(new Point(px, py)) != null) {
                Set<Unit> units = board.get(new Point(px, py));
                if (units.stream().anyMatch(unit -> unit.getUnitType().equals(UnitType.RIVER))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < BOARD_HEIGHT && y >= 0 && y < BOARD_WIDTH;
    }

    public Comparator<Chess> chessComparator() {
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

    public int evaluate() {
        int score = 0;
        for (Set<Unit> units : this.board.values()) {
            Chess chess = switch (units.size()) {
                case 1 -> {
                    Unit unit = units.iterator().next();
                    if (unit.getUnitType().equals(UnitType.CHESS)) {
                        yield (Chess) unit;
                    }
                    yield null;
                }
                case 2 -> {
                    Unit unit = units.stream().filter(u -> u.getUnitType().equals(UnitType.CHESS)).findFirst().orElse(null);
                    yield (Chess) unit;
                }
                default -> throw new IllegalStateException("Unexpected value: " + units.size());
            };

            if (chess == null) {
                continue;
            }
            if (chess.getChessType().equals(ChessType.RAT)) {
                int ratScore = chess.isInDanger() ? chess.value() + 800 : chess.value();
                score += chess.isMaximizer() ? ratScore : -ratScore;
            } else {
                score += chess.isMaximizer() ? chess.value() : -chess.value();
            }
        }
        return score;
    }


    public boolean win() {
        return maximizer ? board.get(new Point(0, 3)).size() == 2 : board.get(new Point(8, 3)).size() == 2;
    }

    public boolean lose() {
        return maximizer ? board.get(new Point(8, 3)).size() == 2 : board.get(new Point(0, 3)).size() == 2;
    }

    public boolean gameOver(){
        return findUnitAtPoint(0, 3).size()==2|| findUnitAtPoint(8,3).size()==2;
    }

    public void printBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Set<Unit> units = this.findUnitAtPoint(i, j);
                if (units == null) {
                    System.out.print(" \033[0m地 ");
                } else {
                    String name = switch (units.size()) {
                        case 1 -> {
                            Unit unit = units.iterator().next();
                            yield getName(unit);
                        }
                        case 2 -> {
                            Unit unit = units.stream().filter(u -> u.getUnitType().equals(UnitType.CHESS)).findFirst().orElse(null);
                            assert unit != null;
                            yield getName(unit);
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + units.size());
                    };
                    System.out.print(" " + name + " ");
                }
            }
            System.out.println();
        }

        System.out.print("\033[0m");
        System.out.println("当前评分：" + evaluate());
        System.out.println("--------------------------------------------");
    }

    public void nextTurn(boolean isMyTurn) {
        if(!isMyTurn){
            GameContextHolder.beastChessAI.move();
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("1:红方");
        System.out.println("2:蓝方");
        System.out.print("请选择阵营：");
        int camp = scanner.nextInt();
        for (int i = 0; i < ChessType.values().length; i++) {
            System.out.println(i + 1 + ":" + ChessType.values()[i].getName());
        }
        System.out.print("请选择棋子：");
        int chessType = scanner.nextInt();
        Chess chess = chessMap.values().stream().filter(value -> {
            boolean isMaximizer = camp == 2;
            return value.isMaximizer() == isMaximizer && value.getChessType().equals(ChessType.getChessType(chessType * 100));
        }).findFirst().orElse(null);

        assert chess != null;
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
        chess.move(pointMap.get(directions.get(direction - 1)).getX(), pointMap.get(directions.get(direction - 1)).getY(),null);
    }

    private static String getName(Unit unit) {
        String name = "";
        String color = "";
        name = switch (unit.getUnitType()) {
            case CAVE -> {
                color = ((Cave) unit).isMaximizer() ? "\033[34m" : "\033[31m";
                yield color + unit.getUnitType().getName();
            }
            case TRAP -> {
                color = ((Trap) unit).isMaximizer() ? "\033[34m" : "\033[31m";
                yield color + unit.getUnitType().getName();
            }
            case RIVER -> "\033[0m" + unit.getUnitType().getName();
            case CHESS -> {
                color = ((Chess) unit).isMaximizer() ? "\033[34m" : "\033[31m";
                yield color + ((Chess) unit).getChessType().getName();
            }
        };
        return name;
    }

    private void initialize() {
        final List<Cave> caves = new ArrayList<>(2);
        final List<Trap> traps = new ArrayList<>(6);
        final List<River> rivers = new ArrayList<>(12);
        final List<Chess> chessList = new ArrayList<>(16);

        // 红方洞穴
        caves.add(new Cave(0, 3, !maximizer));
        // 蓝方洞穴
        caves.add(new Cave(8, 3, maximizer));

        // 红方陷阱
        traps.add(new Trap(0, 2, !maximizer));
        traps.add(new Trap(0, 4, !maximizer));
        traps.add(new Trap(1, 3, !maximizer));
        // 蓝方陷阱
        traps.add(new Trap(8, 2, maximizer));
        traps.add(new Trap(8, 4, maximizer));
        traps.add(new Trap(7, 3, maximizer));

        // 红方棋子
        chessList.add(new Lion(0, 0, !maximizer));
        chessList.add(new Rat(2, 0, !maximizer));
        chessList.add(new Dog(1, 1, !maximizer));
        chessList.add(new Leopard(2, 2, !maximizer));
        chessList.add(new Wolf(2, 4, !maximizer));
        chessList.add(new Cat(1, 5, !maximizer));
        chessList.add(new Tiger(0, 6, !maximizer));
        chessList.add(new Elephant(2, 6, !maximizer));
        // 蓝方棋子
        chessList.add(new Lion(8, 6, maximizer));
        chessList.add(new Rat(6, 6, maximizer));
        chessList.add(new Dog(7, 5, maximizer));
        chessList.add(new Leopard(6, 4, maximizer));
        chessList.add(new Wolf(6, 2, maximizer));
        chessList.add(new Cat(7, 1, maximizer));
        chessList.add(new Tiger(8, 0, maximizer));
        chessList.add(new Elephant(6, 0, maximizer));

        // 河流
        rivers.add(new River(3, 1));
        rivers.add(new River(3, 2));
        rivers.add(new River(4, 1));
        rivers.add(new River(4, 2));
        rivers.add(new River(5, 1));
        rivers.add(new River(5, 2));
        rivers.add(new River(3, 4));
        rivers.add(new River(3, 5));
        rivers.add(new River(4, 4));
        rivers.add(new River(4, 5));
        rivers.add(new River(5, 4));
        rivers.add(new River(5, 5));

        // 缓存
        caves.forEach(cave -> board.put(cave.getPoint(), new HashSet<>(Collections.singletonList(cave))));
        traps.forEach(trap -> board.put(trap.getPoint(), new HashSet<>(Collections.singletonList(trap))));
        rivers.forEach(river -> board.put(river.getPoint(), new HashSet<>(Collections.singletonList(river))));
        chessList.forEach(chess -> {
            board.put(chess.getPoint(), new HashSet<>(Collections.singletonList(chess)));
            chessMap.put(chess.getPoint(), chess);
            if (chess.isMaximizer()) {
                maximizerChessMap.put(chess.getPoint(), chess);
            } else {
                minimizerChessMap.put(chess.getPoint(), chess);
            }
        });
    }
}
