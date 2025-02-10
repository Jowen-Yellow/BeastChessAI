package com.hzh.game;

import com.hzh.ai.BeastChessAI;
import lombok.Getter;
import lombok.Setter;

public class GameContextHolder {
    @Getter
    @Setter
    private static GameBoard gameBoard;

    @Getter
    @Setter
    public static BeastChessAI beastChessAI;
}
