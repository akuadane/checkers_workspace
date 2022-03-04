package com.company;

import com.company.move.Move;
import com.company.move.RemovingMove;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Board board = new Board();
        board.display();
        board.findLegalMoves(PieceOwner.PLAYER1);

        for(Move a: board.moveList){
                System.out.println(String.valueOf(a));

        }
        board.makeMove(board.moveList.get(0));
        board.display();

    }
}