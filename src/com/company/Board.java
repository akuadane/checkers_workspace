package com.company;

import com.company.move.Move;
import com.company.move.RemovingMove;

import java.util.ArrayList;
import java.util.List;

public class Board {
    Piece[][] board = new Piece[8][8];
    List<Move> moveList = new ArrayList<>();

    public Board(){
        this.resetBoard();
    }

    public int[][] findLegalMoves(PieceOwner owner){

        for(int r=0;r<this.board.length;r++){
            for(int c=(1-r%2);c<this.board.length;c+=2){ // Makes sure the Pieces are placed on squares where i+j is odd
                Piece piece = this.board[r][c];
                if(piece!=null && piece.owner==owner){

                        for (Move a: findMoves(piece,r,c))
                            moveList.add(a);
                }
            }
        }

        return new int[48][12];
    }
    private List<Move> findJumps(Piece piece,int r, int c){


        List<Move> jumpList = new ArrayList<>();

        for(Move mv: findMoves(piece,r,c)){
            if(!(mv instanceof RemovingMove))
                continue;




        }



        return jumpList;
    }
    private List<Move> findMoves(Piece piece,int r, int c){
        int[][] moveDir;
        List<Move> tempMoveList = new ArrayList<>();

        if(piece.type==PieceType.PAWN){
            if(piece.owner==PieceOwner.PLAYER1 )
                moveDir= new int[][]{{-1,-1},{-1,1}}; // allows player1's pawns to move towards the top of the board only
            else
                moveDir= new int[][]{{1,-1},{1,1}};  // allows player2's pawns to move towards the bottom of the board only
        }
        else
            moveDir= new int[][]{{1,-1},{1,1},{-1,1},{-1,-1}}; // allows kings to move to every direction

        for(int i=0;i< moveDir.length;i++){
            int newR = r+moveDir[i][0];
            int newC = c+moveDir[i][1];

            //Check if the new row and column are inside the board
            if(newR<0 || newR>=this.board.length || newC<0 || newC>=this.board.length)
                continue;


            Piece newPos = this.board[newR][newC];

            // check if the adjacent square is empty
            if(newPos==null) {
                Move mv = new Move(new int[]{r, c, newR, newC});
                tempMoveList.add(mv);

            }
            // check if the adjacent square is occupied by our piece, if so continue
            else if(newPos.owner==piece.owner)
                continue;
            // check if the second adjacent square is occupied or not
            else if(piece.type==PieceType.KING || newPos.type==PieceType.PAWN) { // Makes sure a PAWN doesn't take a KING
                int nextNewR = newR + moveDir[i][0];
                int nextNewC = newC + moveDir[i][1];

                if(nextNewR<0 || nextNewR>=this.board.length || nextNewC<0 || nextNewC>=this.board.length)
                    continue;
                Piece nextNewPos = this.board[nextNewR][nextNewC];

                // if the second adjacent square isn't occupied  this is a valid removing move
                if(nextNewPos==null){
                    RemovingMove rm = new RemovingMove(new int[]{r, c, nextNewR, nextNewC});
                    rm.addToBeRemovedSquare(new int[]{newR,newC});

                    tempMoveList.add(rm);
                }


        }
    }
        return tempMoveList;
    }

    /**
     * Moves a piece on the board
     * */
    public void makeMove(Move move){
        int initR = move.movement[0];
        int initC = move.movement[1];
        int newR = move.movement[2];
        int newC = move.movement[3];

        board[newR][newC] = board[initR][initC].copy();
        board[initR][initC] = null;

        if(move instanceof RemovingMove){
            for(int[] remove: ((RemovingMove) move).toBeRemoved){
                board[remove[0]][remove[1]]=null;
            }
        }
    }

    /**
     * Resets the board to its original state.
     * */
    public void resetBoard(){

        for(int i=0;i<this.board.length;i++){
            for(int j=(1-i%2);j<this.board.length;j+=2){ // Makes sure the Pieces are placed on squares where i+j is odd
                if(i<=2){  //place player two's pawns in their starting place
                    this.board[i][j]= new Piece(PieceType.PAWN,PieceOwner.PLAYER2);
                }else if(i<=4){ // makes the middle two rows empty
                    this.board[i][j]=null;
                }else{ //place player one's pawns in their starting place
                    this.board[i][j]= new Piece(PieceType.PAWN,PieceOwner.PLAYER1);
                }

            }
        }

    }

    public void display(){
        for(int i=0;i<this.board.length;i++){
            for(int j=0;j<this.board.length;j++){
                Piece piece = this.board[i][j];
                char p = ' ';
                if(piece!=null){
                    if(piece.owner==PieceOwner.PLAYER1){
                        p='a';
                    }else{
                        p='b';
                    }
                    if(piece.type==PieceType.KING)
                        p = Character.toUpperCase(p);
                }
                System.out.print("|"+p);
            }
            System.out.println();
        }
    }

}
