package com.checkers;


import com.checkers.controller.Game;
import com.checkers.controller.Tournament;
import com.checkers.models.exceptions.InValidMove;
import com.checkers.models.piece.Piece;
import com.checkers.models.players.AlphaBetaMinMaxAIPlayer;
import com.checkers.models.players.BackRowAIPlayer;
import com.checkers.models.players.mcts.MCTSPlayer;

import com.checkers.models.players.MinMaxAIPlayer;
import com.checkers.models.players.ReinforcedMinMax;


public class Main {

    public static void main(String[] args) throws InterruptedException, InValidMove, CloneNotSupportedException {


       // Game game = new Game(new MCTSPlayer("MCTS", Piece.PieceOwner.PLAYER1),new AlphaBetaMinMaxAIPlayer("AlphaBetaMinMax", Piece.PieceOwner.PLAYER2));


        //game.play();

       Tournament tr = new Tournament();
       System.out.println(tr.playOff(100));

    }
}