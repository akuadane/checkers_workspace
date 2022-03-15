package com.company.main.models;

import com.company.main.models.exceptions.InValidMove;
import com.company.main.models.move.Move;
import com.company.main.models.move.Jump;
import com.company.main.models.piece.Piece;
import com.company.main.models.piece.PieceOwner;
import com.company.main.models.piece.PieceType;

import java.util.*;

public class Board {
    public Piece[][] board = new Piece[8][8];
    private Piece[][] prevBoard;
    List<Move> possibleMovements = new ArrayList<>();

    public Board(){
        this.resetBoard();
    }

    /**
     * Locate Pieces owned by inTurnPlayer and find the possible moves.
     *Goes through the entire board and if there is a piece on a square which is owned by the
     * inTurnPlayer, we call findJumps followed by findMoves. To make sure that the possible
     * list of moves only contains jumps if the exists, we only add moveList to possibleMovements only
     * if jumpList is empty.
     *
     * @param inTurnPlayer the owner of the pieces whose turn it is
     *
     * @return list of possible movements
     *
     * */
    public List<Move> findLegalMoves(PieceOwner inTurnPlayer){
        List<Move> moveList = new ArrayList<>();
        List<Jump> jumpList = new ArrayList<>();

        for(int r=0;r<this.board.length;r++){
            for(int c=(1-r%2);c<this.board.length;c+=2){ // Makes sure the Pieces are placed on squares where i+j is odd
                Piece piece = this.board[r][c];
                if(piece!=null && piece.owner==inTurnPlayer){
                        jumpList.addAll(findJumps(piece,r,c)); // add all possible jumps

                        if(jumpList.size()==0) // If there is no jump, then look for normal moves
                            moveList.addAll(findMoves(piece,r,c));
                }
            }
        }
        possibleMovements.addAll(jumpList);
        if(possibleMovements.size()==0)
            possibleMovements.addAll(moveList);

        return possibleMovements;
    }

    /**
     * Find all possible jumps from the square [r,c].
     * We call the findMoves method to see all the possible moves from that square.
     * If there is any jump from the returned list, we use BreadthFirst search
     * to look for any other jumps from the new position.
     *
     * @param piece the piece that is on the square
     * @param r the row at which the piece is located
     * @param c the column at which the piece is located
     * @return list of all possible jumps from that square
     * */
    private List<Jump> findJumps(Piece piece, int r, int c){

        List<Jump> jumpList = new ArrayList<>();
        List<int[]> tempPieces = new ArrayList<>();

        for(Move mv: findMoves(piece,r,c)){  // Use BreadthFirst search to locate all possible jumps from this point
            if(!(mv instanceof Jump))
                continue;
            Queue<Jump> queue = new LinkedList<>();
            queue.add((Jump) mv);

            while(queue.size()>0){
                Jump x = queue.remove();
                boolean addToJumpList = true;

                for(Move nMove: findMoves(piece,x.movement[2],x.movement[3])){
                    if(!(nMove instanceof Jump))
                        continue;

                    Jump rm = new Jump(new int[]{x.movement[0],x.movement[1],nMove.movement[2],nMove.movement[3]});
                    rm.toBeRemoved.addAll(x.toBeRemoved);
                    rm.toBeRemoved.addAll(((Jump) nMove).toBeRemoved);
                    board[x.movement[2]][x.movement[3]]= new Piece(piece.type,piece.owner); // place temporary pieces to avoid infinite recursion with Kings
                    tempPieces.add(new int[]{x.movement[2],x.movement[3]});
                    queue.add(rm);
                    addToJumpList=false;
                }
                if(addToJumpList) // if the move doesn't have any more jumps add it to jumpList
                    jumpList.add(x);
            }

        }

        for (int[] tempPiece :
                tempPieces) {
            board[tempPiece[0]][tempPiece[1]] = null;  // Removes the temporary pieces
        }

        return jumpList;
    }
    /**
     * Finds all possible moves and jumps from the square [r,c] in one-step range.
     * First we set the proper movement direction for each piece. After that we iterate
     * through each movement direction and check if the new square is inside the board. If
     * that condition is satisfied we check if we have a legal movement from that square and return
     * a list of all possible moves from the square.
     *
     * @param piece the piece that is on the square
     * @param r the row at which the piece is located
     * @param c the column at which the piece is located
     *
     * @return list of all possible moves from that square
     * */
    private List<Move> findMoves(Piece piece,int r, int c){
        int[][] moveDir;
        List<Move> tempMoveList = new ArrayList<>();

        if(piece.type== PieceType.PAWN){
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
                    Jump rm = new Jump(new int[]{r, c, nextNewR, nextNewC});
                    rm.addToBeRemovedSquare(new int[]{newR,newC});

                    tempMoveList.add(rm);
                }


        }
    }
        return tempMoveList;
    }

    /**
     * Moves a piece on the board
     * Move a piece to a new square on the board, and makes
     * its previous spot null. We also check if the piece is at
     * an edge row and make the piece a king if so. If the move is a jump,
     * we iterate through the toBeRemoved list and make sure all the pieces
     * in there are null so that they are removed.
     * */
    public void makeMove(Move move, PieceOwner playerInTurn) throws InValidMove {

        if(move==null)
            throw new InValidMove("Move object can't be null.");

        prevBoard = Arrays.copyOf(board,board.length);

        int initR = move.movement[0];
        int initC = move.movement[1];
        int newR = move.movement[2];
        int newC = move.movement[3];

        // Put the piece onto its new destination
        board[newR][newC] = board[initR][initC].clone();
        board[initR][initC] = null;  // Make the previous position empty

        if(newR==0 && playerInTurn==PieceOwner.PLAYER1)
            board[newR][newC].type = PieceType.KING;

        if(newR==board.length-1 && playerInTurn==PieceOwner.PLAYER2)
            board[newR][newC].type = PieceType.KING;

        if(move instanceof Jump){
            for(int[] remove: ((Jump) move).toBeRemoved){
                board[remove[0]][remove[1]]=null;  // Remove all piece that are jumped over
            }
        }
    }

    /**
     * Resets the board to its original state.
     * We iterate through the board skipping a square. The first 3 rows
     * are for player 2 and the last three row are for player 1 initially.
     * The rows in between are empty.
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

    /**
     * Restores the previous state of the board
     * prevBoard holds the board state just before the last move
     * */
    public void undo(){
        if(prevBoard!=null)
            board = Arrays.copyOf(prevBoard,prevBoard.length);
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
                String box = "|"+p + ((j==board.length-1)? "|":"");

                System.out.print(box);
            }
            System.out.println();

        }
    }

    /**
     * Iterates through the board looking at if each player has at least one possible movement.
     * If each player has at least one move, we return null symbolizing no-one is a winner here.
     * If one of players has possible moves and the other doesn't we return that player.
     *
     * @return PieceOwner type of the winning player or null if the game hasn't ended yet.
     * */
    public PieceOwner isGameOver(){
        boolean p1HasMoves=false,p2HasMoves=false;

        for(int r=0;r<this.board.length;r++){
            for(int c=(1-r%2);c<this.board.length;c+=2){ // Makes sure the Pieces are placed on squares where i+j is odd
                Piece piece = this.board[r][c];
                if(piece!=null){
                   if(findMoves(piece,r,c).size()!=0){
                       if(piece.owner==PieceOwner.PLAYER1)
                           p1HasMoves=true;
                       else
                           p2HasMoves=true;

                       if(p1HasMoves && p2HasMoves)
                           return null;
                   }

                }
            }
        }

        if(p1HasMoves)
            return PieceOwner.PLAYER1;

        return PieceOwner.PLAYER2;
    }

    @Override
    public Board clone(){
        Board tempBoard = new Board();

        for(int i=0;i<this.board.length;i++){
            for(int j=(1-i%2);j<this.board.length;j+=2){
                if(board[i][j]!=null)
                    tempBoard.board[i][j] = board[i][j].clone();
                else
                    tempBoard.board[i][j]=null;
            }}
        return tempBoard;
    }
}