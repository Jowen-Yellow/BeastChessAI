package com.hzh.unit.chess;

import com.hzh.moving.CommonMovingStrategy;
import com.hzh.moving.LionMovingStrategy;
import com.hzh.moving.MovingStrategy;
import com.hzh.moving.RatMovingStrategy;
import lombok.Getter;

@Getter
public enum ChessType {
    RAT(100, "鼠", new RatMovingStrategy()),
    CAT(200, "猫"),
    DOG(300, "狗"),
    WOLF(400, "狼"),
    LEOPARD(500, "豹"),
    TIGER(600, "虎", new LionMovingStrategy()),
    LION(700, "狮", new LionMovingStrategy()),
    ELEPHANT(800, "象");

    private final int value;
    private final String name;
    private final MovingStrategy movingStrategy;

    ChessType(int value, String name){
        this.value = value;
        this.name = name;
        this.movingStrategy = new CommonMovingStrategy();
    }

    ChessType(int value, String name, MovingStrategy movingStrategy){
        this.value = value;
        this.name = name;
        this.movingStrategy = movingStrategy;
    }

    public static ChessType getChessType(int value){
        for(ChessType chessType : ChessType.values()){
            if(chessType.getValue() == value){
                return chessType;
            }
        }
        return null;
    }
}
