package com.company.gui;


import com.company.models.move.Position;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;


public class BoardSquare extends Button {
    private final Color GRAY  = Color.GRAY;
    private final Color WHITE = Color.WHITE;
    private final Color RED = Color.RED;
    private final String BASE_PATH = "/resources";
    private final Color squareColor;
    public static final int SIZE = 80;
    private final int r;
    private final int c;

    public BoardSquare(int r, int c){
        boolean light = (r+c)%2==0;
        this.r= r;
        this.c = c;
        this.squareColor = (light)? WHITE:GRAY;
        setDisable(light);
        setPrefSize(SIZE,SIZE);
        setMinSize(SIZE,SIZE);
    }
    public Position  getCoordinate(){
        return new Position(this.r,this.c);
    }
    public void setPiece(String piece){
        String[] pieces = piece.split(" ");
        String type = pieces[0];
        String owner = pieces[1];
        if(type.equalsIgnoreCase("King"))
            if (owner.equalsIgnoreCase("player1"))
                setGraphic(new ImageView(String.valueOf(getClass().getResource("/player1King.png"))));
            else
                setGraphic(new ImageView(String.valueOf(getClass().getResource("/player2King.png"))));

        else
        if(owner.equalsIgnoreCase("player1"))
            setGraphic(new ImageView(String.valueOf(getClass().getResource("/player1.png"))));
        else
            setGraphic(new ImageView(String.valueOf(getClass().getResource("/player2.png"))));

    }
    public void setPiece(){
        setGraphic(null);
    }

    public void setHighlight(boolean highlight){
        if(highlight)
           setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        else
            setBackground(new Background(new BackgroundFill(this.squareColor, CornerRadii.EMPTY, Insets.EMPTY)));

    }
}
