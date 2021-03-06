package com.checkers.ReinforcementLearning;

import com.checkers.models.Board;
import com.checkers.models.exceptions.InValidMove;
import com.checkers.models.move.Move;
import com.checkers.models.piece.King;
import com.checkers.models.piece.Pawn;
import com.checkers.models.piece.Piece;
import com.checkers.models.players.Player;

import java.util.List;
import java.util.Random;

public class CheckersEnvironment{
    public static final double LOWEST_REWARD = 0;
    public static double HIGHEST_REWARD = 2;
    private Board state;
    private final Player player1;
    private int steps = 0;

    public CheckersEnvironment(Player player1){
        this.player1 = player1;
    }

    public ActionResult takeAction(Move mv) throws InValidMove, CloneNotSupportedException {
        double reward ;
        this.state.makeMove(mv);


        reward = this.rewardFunc();

        Piece.PieceOwner winner = this.state.isGameOver();
        if(winner!=null && winner!=player1.myTurn){
            return new ActionResult(new Board(this.state),HIGHEST_REWARD,winner,true);
        }

        Move p2Move = this.player1.makeMove(new Board(this.state));
        this.state.makeMove(p2Move);


        reward = (reward + this.rewardFunc())/2;
        winner = this.state.isGameOver();
        if(winner!=null && winner==player1.myTurn){
            return new ActionResult(new Board(this.state),LOWEST_REWARD,winner,true);
        }

        return new ActionResult(new Board(this.state),reward,null,false);
    }
    public Board reset(){
        this.state = new Board();
        try {
            Move mv = this.player1.makeMove(new Board(this.state));
            this.state.makeMove(mv);
        } catch (InValidMove | CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return new Board(state);
    }
    public Board randomReset(int n){
        this.state = new Board();
        this.state.board = new Piece[Board.BOARD_SIZE][Board.BOARD_SIZE];

        Random random = new Random();
        int tempN = n;
        while(tempN>0){
                 int r = random.nextInt(0,Board.BOARD_SIZE);
                 int c = random.nextInt(0,Board.BOARD_SIZE);

                 if((r+c)%2==0 || !this.state.isEmpty(r,c))
                     continue;

                 Piece piece = null;
                 Piece.PieceOwner turn = null;

                if(random.nextBoolean())
                    turn = Piece.PieceOwner.PLAYER1;
                else
                    turn = Piece.PieceOwner.PLAYER2;

                 if(random.nextBoolean() | r==0 || r==Board.BOARD_SIZE-1)
                     piece = new King(turn);
                 else
                     piece = new Pawn(turn);

                this.state.setPiece(r,c,piece);
                tempN--;
        }
        if(this.state.isGameOver()!=null)  // Makes sure the random game generated isn't over
            return this.randomReset(n);

        try {
            Move mv= this.player1.makeMove(new Board(this.state));
            this.state.makeMove(mv);
        } catch (InValidMove e) {
            throw new RuntimeException(e);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }


        if(this.state.isGameOver()!=null)  // Makes sure the random game generated isn't over
            return this.randomReset(n);

        return new Board(this.state);
    }

    public double rewardFunc(){
        double reward = ( HIGHEST_REWARD +LOWEST_REWARD )/2;
        int[] myCurrPieces = new int[2]; // Pawns on index 0 and kings on index 1
        int[] oppCurrPieces = new int[2];
        int[] myPrevPieces = new int[2];
        int[] oppPrevPieces = new int[2];

        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = (1 - i % 2); j < Board.BOARD_SIZE; j += 2) {
                Piece currP = this.state.getPiece(i,j);
                Piece prevP = this.state.prevBoard[i][j];

                if(currP!=null){
                    if(currP.owner == player1.myTurn){
                        if(currP instanceof Pawn)
                            oppCurrPieces[0]++;
                        else
                            oppCurrPieces[1]++;
                    }else{
                        if(currP instanceof Pawn)
                            myCurrPieces[0]++;
                        else
                            myCurrPieces[1]++;
                    }
                }
                if(prevP!=null){
                    if(prevP.owner == player1.myTurn){
                        if(prevP instanceof Pawn)
                            oppPrevPieces[0]++;
                        else
                            oppPrevPieces[1]++;
                    }else{
                        if(prevP instanceof Pawn)
                            myPrevPieces[0]++;
                        else
                            myPrevPieces[1]++;
                    }
                }
            }
        }

        if(oppCurrPieces[0]-oppPrevPieces[0]<0) // when we jump over opponent's pawn
            reward= reward + reward*0.20*(oppPrevPieces[0]-oppCurrPieces[0]);
        if(oppCurrPieces[1]-oppPrevPieces[1]<0)
            reward= reward + reward*0.25*(oppPrevPieces[1]-oppCurrPieces[1]); // when we jump over opponent's king

        if(myCurrPieces[0]-myPrevPieces[0]<0) // When our pawn gets jumped over
            reward*=0.6*(1/(myPrevPieces[0]-myCurrPieces[0]));
        if(myCurrPieces[1]-myPrevPieces[1]<0) // When our king gets jumped over
            reward*=0.5*(1/(myPrevPieces[1]-myCurrPieces[1]));
        if(myCurrPieces[1]-myPrevPieces[1]>0) // when we become a king
            reward = reward + reward * 0.20;

        if(reward>HIGHEST_REWARD)
            reward = HIGHEST_REWARD;

        if(oppCurrPieces[0]+oppCurrPieces[1]==oppPrevPieces[0]+oppPrevPieces[1])
            reward*=0.9;

        return reward;
    }

    public void display(){
        this.state.display();
    }
    public List<Move> getActions(){
        return this.state.reachablePositionsByPlayer(this.state.getTurn());
    }

    public Piece.PieceOwner isGameOver(){
        return this.state.isGameOver();
    }

}
