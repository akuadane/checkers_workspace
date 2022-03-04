package com.company;

public abstract class Player {
    String name;
    PieceOwner myTurn;
    Player(String name,PieceOwner myTurn){
        this.name = name;
        this.myTurn = myTurn;
    }

    abstract int[] makeMove(Board board);

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                '}';
    }
}

