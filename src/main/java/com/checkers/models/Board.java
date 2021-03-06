package com.checkers.models;

import com.checkers.models.exceptions.InValidMove;
import com.checkers.models.move.Jump;
import com.checkers.models.move.Move;
import com.checkers.models.move.Position;
import com.checkers.models.piece.King;
import com.checkers.models.piece.Pawn;
import com.checkers.models.piece.Piece;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;

public class Board {
    public static final int BOARD_SIZE = 8;

    public Piece[][] board;
    public Piece[][] prevBoard;
    private Piece[][] futureBoard;
    private Piece.PieceOwner turn;
    public int prevPlayer1Score;
    public int prevPlayer2Score;

    public int futurePlayer1Score;
    public int futurePlayer2Score;

    public final IntegerProperty player1Score = new SimpleIntegerProperty(0);
    public final IntegerProperty player2Score = new SimpleIntegerProperty(0);


    public Board() {
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
        this.prevBoard = null;
        this.turn = Piece.PieceOwner.PLAYER1;
        this.resetBoard();
    }

    public Board(Board b) {
        this.prevBoard = cloneBoardArray(b.prevBoard);
        this.board = cloneBoardArray(b.board);
        this.turn = b.turn;
    }

    public Piece[][] getBoard() {
        return this.cloneBoardArray(this.board);
    }

    public void setPiece(Position pos, Piece p) {
        this.board[pos.getRow()][pos.getColumn()] = p;
    }

    public void setPiece(int r, int c, Piece p) {
        this.board[r][c] = p;
    }

    /**
     * Moves a piece on the board
     * Move a piece to a new square on the board, and makes
     * its previous spot null. We also check if the piece is at
     * an edge row and make the piece a king if so. If the move is a jump,
     * we iterate through the toBeRemoved ArrayList and make sure all the pieces
     * in there are null so that they are removed.
     */
    public Move makeMove(Move mv) throws InValidMove {
        if (mv == null) throw new InValidMove("Move object can't be null.");

        if (this.getPiece(mv.getOrigin()).owner != this.turn) throw new InValidMove("Not your turn.");

        for (Move move : this.reachablePositionsByPlayer(this.getPiece(mv.getOrigin()).owner)) {
            if (mv.equals(move)) {
                prevBoard = cloneBoardArray(board);
                prevPlayer2Score = this.getPlayer2Score();
                prevPlayer1Score = this.getPlayer1Score();
                int initR = move.getOrigin().getRow();
                int initC = move.getOrigin().getColumn();
                int newR = move.getDestination().getRow();
                int newC = move.getDestination().getColumn();

                // Put the piece onto its new destination
                board[newR][newC] = board[initR][initC].clone();
                board[initR][initC] = null;  // Make the previous position empty

                if (newR == 0 && this.turn == Piece.PieceOwner.PLAYER1) board[newR][newC] = new King(this.turn);

                if (newR == BOARD_SIZE - 1 && this.turn == Piece.PieceOwner.PLAYER2)
                    board[newR][newC] = new King(this.turn);

                if (move instanceof Jump) {
                    int score = 0;
                    for (Position remove : ((Jump) move).toBeRemoved) {
                        board[remove.getRow()][remove.getColumn()] = null;  // Remove all piece that are jumped over

                        if (this.getPiece(remove) instanceof King) score += 2;
                        else score++;

                    }

                    if (this.turn == Piece.PieceOwner.PLAYER1)
                        this.player1Score.setValue(this.player1Score.getValue() + score);
                    else this.player2Score.setValue(this.player2Score.getValue() + score);

                }

                this.turn = (this.turn == Piece.PieceOwner.PLAYER1) ? Piece.PieceOwner.PLAYER2 : Piece.PieceOwner.PLAYER1;
                return move;
            }
        }
        throw new InValidMove("No such move.");

    }

    public ArrayList<Move> reachablePositions(Position position) {
        if (position == null || this.getPiece(position) == null) return null;
        return this.getPiece(position).generateMoves(new Board(this), position);
    }

    /**
     * Returns all possible move a player can take. Iteratively calls <code>reachablePositions</code> to get
     * the moves a piece can make and adds to an ArrayList. If there are Jump type moves, those are the only moves returned.
     *
     * @param owner - the player we want to generate the list of moves for
     * @return the possible moves for a player
     */
    public ArrayList<Move> reachablePositionsByPlayer(Piece.PieceOwner owner) {
        ArrayList<Move> jumps = new ArrayList<>();
        ArrayList<Move> moves = new ArrayList<>();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = (1 - i % 2); j < BOARD_SIZE; j += 2) {
                Piece piece = this.getPiece(i, j);
                if (piece != null && piece.owner == owner) {
                    ArrayList<Move> tempMoves = this.reachablePositions(new Position(i, j));

                    if (tempMoves.size() == 0) continue;

                    else if (tempMoves.get(0) instanceof Jump) jumps.addAll(tempMoves);
                    else moves.addAll(tempMoves);
                }
            }
        }
        if (jumps.size() != 0) return jumps;
        return moves;
    }


    /**
     * Returns the possible moves of the current player.
     *
     * @return ArrayList of moves
     */
    public ArrayList<Move> reachablePositionsByPlayer() {
        return this.reachablePositionsByPlayer(this.turn);
    }

    /**
     * Returns whose turn it is.
     *
     * @return the PieceOwner type whose turn it is.
     */

    public Piece.PieceOwner getTurn() {
        return this.turn;
    }

    public void setTurn(Piece.PieceOwner owner) {
        this.turn = owner;
    }

    /**
     * Returns player one's score
     *
     * @return an integer representing player one's score.
     */
    public int getPlayer1Score() {
        return player1Score.get();
    }

    /**
     * Returns player two's score
     *
     * @return an integer representing player two's score.
     */
    public int getPlayer2Score() {
        return player2Score.get();
    }

    /**
     * Resets the board to its original state.
     * We iterate through the board skipping a square. The first 3 rows
     * are for player 2 and the last three row are for player 1 initially.
     * The rows in between are empty.
     */
    public void resetBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = (1 - i % 2); j < BOARD_SIZE; j += 2) { // Makes sure the Pieces are placed on squares where i+j is odd
                if (i <= 2) {  //place player two's pawns in their starting place
                    this.board[i][j] = new Pawn(Piece.PieceOwner.PLAYER2);
                } else if (i <= 4) { // makes the middle two rows empty
                    this.board[i][j] = null;
                } else { //place player one's pawns in their starting place
                    this.board[i][j] = new Pawn(Piece.PieceOwner.PLAYER1);
                }
            }
        }
        turn = Piece.PieceOwner.PLAYER1;
        this.player2Score.setValue(0);
        this.player1Score.setValue(0);
    }

    /**
     * Returns the Piece object at the give position.
     *
     * @param pos - the position of the Piece required.
     * @return Piece - returns the piece at the specified position.
     */
    public Piece getPiece(Position pos) {
        int r = pos.getRow();
        int c = pos.getColumn();
        if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE) return null;

        return board[r][c];

    }

    /**
     * Returns the Piece object at the give row and column.
     *
     * @param r - the row of the Piece required.
     * @param c - the column of the Piece required.
     * @return Piece - returns the piece at the specified position.
     */
    public Piece getPiece(int r, int c) {
        if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE) return null;
        return board[r][c];

    }

    public boolean isEmpty(int r, int c) {
        return this.getPiece(r, c) == null;
    }

    public boolean isEmpty(Position pos) {
        return this.getPiece(pos) == null;
    }

    /**
     * Restores the previous state of the board
     * prevBoard holds the board state just before the last move
     */
    public boolean undo() {
        if (prevBoard != null) {
            futureBoard = cloneBoardArray(board);
            futurePlayer1Score = this.getPlayer1Score();
            futurePlayer2Score = this.getPlayer2Score();
            board = cloneBoardArray(prevBoard);
            player2Score.setValue(prevPlayer2Score);
            player1Score.setValue(prevPlayer1Score);
            prevBoard = null;
            this.turn = (this.turn == Piece.PieceOwner.PLAYER1) ? Piece.PieceOwner.PLAYER2 : Piece.PieceOwner.PLAYER1;
            return true;
        }
        return false;

    }

    /**
     * Restores the state of the board before the undo.
     * futureBoard holds the board state just before the last undo
     */
    public boolean redo() {
        if (futureBoard != null) {
            prevBoard = cloneBoardArray(board);
            prevPlayer1Score = getPlayer1Score();
            prevPlayer2Score = getPlayer2Score();
            board = cloneBoardArray(futureBoard);
            player2Score.setValue(futurePlayer2Score);
            player1Score.setValue(futurePlayer1Score);
            futureBoard = null;
            this.turn = (this.turn == Piece.PieceOwner.PLAYER1) ? Piece.PieceOwner.PLAYER2 : Piece.PieceOwner.PLAYER1;
            return true;
        }
        return false;
    }


    /**
     * Iterates through the board looking at if each player has at least one possible movement.
     * If each player has at least one move, we return null symbolizing no-one is a winner here.
     * If one of players has possible moves and the other doesn't we return that player.
     *
     * @return PieceOwner type of the winning player or null if the game hasn't ended yet.
     */
    public Piece.PieceOwner isGameOver() {
        boolean p1HasMoves = false, p2HasMoves = false;

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = (1 - r % 2); c < BOARD_SIZE; c += 2) { // Makes sure the Pieces are placed on squares where i+j is odd
                Piece piece = this.board[r][c];
                if (piece != null) {
                    if (piece.generateMoves(new Board(this), new Position(r, c)).size() != 0) {
                        if (piece.owner == Piece.PieceOwner.PLAYER1) p1HasMoves = true;
                        else p2HasMoves = true;

                        if (p1HasMoves && p2HasMoves) return null;
                    }

                }
            }
        }
        if (p1HasMoves) return Piece.PieceOwner.PLAYER1;

        return Piece.PieceOwner.PLAYER2;
    }


    public Piece[][] cloneBoardArray(Piece[][] b) {
        if (b == null) return b;
        Piece[][] newBoard = new Piece[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = (1 - i % 2); j < BOARD_SIZE; j += 2) {
                if (b[i][j] != null) newBoard[i][j] = b[i][j].clone();
                else newBoard[i][j] = null;
            }
        }
        return newBoard;
    }

    public void display() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Piece piece = this.board[i][j];
                char p = ' ';
                if (piece != null) {
                    if (piece.owner == Piece.PieceOwner.PLAYER1) {
                        p = 'a';
                    } else {
                        p = 'b';
                    }
                    if (piece instanceof King) p = Character.toUpperCase(p);
                }
                String box = "|" + p + ((j == BOARD_SIZE - 1) ? "|" : "");

                System.out.print(box);
            }
            System.out.println();

        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Board)) return false;

        Board b = (Board) obj;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                {
                    Position pos = new Position(i, j);
                    if (b.getPiece(pos) == null && this.getPiece(pos) == null) continue;
                    if (!b.getPiece(pos).equals(this.getPiece(pos))) return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String boardStr = "";
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = (1 - r % 2); c < BOARD_SIZE; c += 2) {
                Piece piece = this.board[r][c];
                char p = '-';
                if (piece != null) {
                    if (piece.owner == Piece.PieceOwner.PLAYER1) {
                        p = 'a';
                    } else {
                        p = 'b';
                    }
                    if (piece instanceof King)
                        p = Character.toUpperCase(p);
                }
                boardStr += p;
            }
        }
        return boardStr;
    }
}
