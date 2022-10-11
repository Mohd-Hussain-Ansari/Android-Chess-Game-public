package com.hussain.chess.utils;

import com.hussain.chess.Model.ByStatus;
import com.hussain.chess.Model.GameStatus;
import com.hussain.chess.Model.Move;

public class OnlineGame extends Game {
    public Move getSecondLastMove() {
        try {
            return movesPlayed.get((movesPlayed.size() - 2));
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public void setDrawByAgreement() {
        gameStatus = GameStatus.DRAW;
        byStatus = ByStatus.Agreement;

    }
}
