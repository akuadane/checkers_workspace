package com.company.models.players.mcts;

import com.company.models.Board;
import com.company.models.piece.Piece;

public class NodeState {
    private Board board;
    private Piece.PieceOwner myTurn;
    private int wins;
    private int plays;

    public NodeState(Board board){
        this.board = board;
        this.wins = 0;
        this.plays = 0;
        this.myTurn = Piece.PieceOwner.PLAYER1;
    }

    public Board getBoard() {
        return board;
    }

    public Piece.PieceOwner getMyTurn() {
        return myTurn;
    }

    public long getWins() {
        return wins;
    }

    public int getPlays() {
        return plays;
    }
}
