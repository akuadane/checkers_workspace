package com.company.main;

import com.company.main.controller.Game;
import com.company.main.models.piece.PieceOwner;
import com.company.main.models.players.MinMaxAIPlayer;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        /*
        Board board = new Board();
        board.display();


        for(Move a:board.findLegalMoves(PieceOwner.PLAYER1)){
                System.out.println(String.valueOf(a));
        }
        for(Move a: board.jumpList){
            System.out.println(String.valueOf(a));
        }
        System.out.println("===================");
        board.makeMove(board.moveList.get(3));
        board.display();
        */


        Game game = new Game(new MinMaxAIPlayer("Aku", PieceOwner.PLAYER1), new MinMaxAIPlayer("AI", PieceOwner.PLAYER2));
        game.play();


    }
}