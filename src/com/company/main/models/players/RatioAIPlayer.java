package com.company.main.models.players;

import com.company.main.models.Board;
import com.company.main.models.move.Move;
import com.company.main.models.piece.PieceOwner;

public class RatioAIPlayer extends Player implements AIPlayer {
    RatioAIPlayer(String name, PieceOwner myTurn) {
        super(name, myTurn);
    }


    @Override
    public double evalBoard(Board board) {
        return 0;
    }

    @Override
    public Move makeMove(Board board) {
        return null;
    }

    private double min(Board prevBoard, double alpha, double beta) {
        return 0;
    }

    private double max(Board prevBoard, double alpha, double beta) {
        return 0;
    }
}
