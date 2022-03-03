package com.company;

import java.util.ArrayList;

public class Board {
    Piece[][] board = new Piece[8][8];
    ArrayList<int[]> jumpList = new ArrayList<>();
    ArrayList<int[]> moveList = new ArrayList<>();
    ArrayList<int[]> removingMoveList = new ArrayList<>();


    public Board(){
        this.resetBoard();
    }

    public int[][] findLegalMoves(PieceOwner owner){

        for(int r=0;r<this.board.length;r++){
            for(int c=(1-r%2);c<this.board.length;c+=2){ // Makes sure the Pieces are placed on squares where i+j is odd
                Piece piece = this.board[r][c];
                if(piece!=null && piece.owner==owner){
                        this.findMoves(piece,r,c);
                }



            }
        }

        return new int[48][12];
    }
    private void findJumps(PieceOwner owner,int r, int c){}
    private void findKingJumps(PieceOwner owner,int r, int c){}

    private void findMoves(Piece piece,int r, int c){
        int[][] moveDir;

        if(piece.type==PieceType.PAWN){
            if(piece.owner==PieceOwner.PLAYER1 )
                moveDir= new int[][]{{-1,-1},{-1,1}}; // allow player1's pawns to move towards the top of the board only
            else
                moveDir= new int[][]{{1,-1},{1,1}};  // allow player2's pawns to move towards the bottom of the board only
        }
        else
            moveDir= new int[][]{{1,-1},{1,1},{-1,1},{-1,-1}}; // allow kings to move to every direction

        for(int i=0;i< moveDir.length;i++){
            int newR = r+moveDir[i][0];
            int newC = c+moveDir[i][1];

            //Check if the new row and column are inside the board
            if(newR<0 || newR>=this.board.length || newC<0 || newC>=this.board.length)
                continue;

            Piece init = this.board[r][c];
            Piece newPos = this.board[newR][newC];

            // check if the adjacent square is empty
            if(newPos==null)
                this.moveList.add(new int[]{r, c, newR, newC});
            // check if the adjacent square is occupied by our piece, if so continue
            else if(newPos.owner==init.owner)
                continue;
            // check if the second adjacent square is occupied or not
            else{
                int nextNewR = newR + moveDir[i][0];
                int nextNewC = newC + moveDir[i][1];

                if(nextNewR<0 || nextNewR>=this.board.length || nextNewC<0 || nextNewC>=this.board.length)
                    continue;
                Piece nextNewPos = this.board[nextNewR][nextNewC];

                // if the second adjacent square isn't occupied  this is a valid removing move
                if(nextNewPos==null)
                    this.removingMoveList.add(new int[]{r, c, nextNewR, nextNewC});

        }
    }

    }
    //private void findKingMoves(PieceOwner owner,int r, int c){}
    public void makeMove(int[] move){}
    public void makeJump(int[] jump){}

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
