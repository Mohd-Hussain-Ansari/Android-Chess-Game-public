package com.hussain.chess.utils;


import com.hussain.chess.Model.Spot;

public class Board {
    public Spot[][] boxes;

    public Board() {
        this.resetBoard();
    }

    public Spot getBox(int x, int y) throws Exception {

        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new Exception("Index out of bound");
        }

        return boxes[x][y];
    }

    public void resetBoard() {

        boxes = new Spot[32][32];

        // initialize black pieces

        boxes[0][0] = new Spot(0, 0, new Rook(false));
        boxes[0][1] = new Spot(0, 1, new Knight(false));
        boxes[0][2] = new Spot(0, 2, new Bishop(false));
        boxes[0][3] = new Spot(0, 3, new Queen(false));
        boxes[0][4] = new Spot(0, 4, new King(false));
        boxes[0][5] = new Spot(0, 5, new Bishop(false));
        boxes[0][6] = new Spot(0, 6, new Knight(false));
        boxes[0][7] = new Spot(0, 7, new Rook(false));

        boxes[1][0] = new Spot(1, 0, new Pawn(false));
        boxes[1][1] = new Spot(1, 1, new Pawn(false));
        boxes[1][2] = new Spot(1, 2, new Pawn(false));
        boxes[1][3] = new Spot(1, 3, new Pawn(false));
        boxes[1][4] = new Spot(1, 4, new Pawn(false));
        boxes[1][5] = new Spot(1, 5, new Pawn(false));
        boxes[1][6] = new Spot(1, 6, new Pawn(false));
        boxes[1][7] = new Spot(1, 7, new Pawn(false));

        // initialize white pieces

        boxes[7][0] = new Spot(7, 0, new Rook(true));
        boxes[7][1] = new Spot(7, 1, new Knight(true));
        boxes[7][2] = new Spot(7, 2, new Bishop(true));
        boxes[7][3] = new Spot(7, 3, new Queen(true));
        boxes[7][4] = new Spot(7, 4, new King(true));
        boxes[7][5] = new Spot(7, 5, new Bishop(true));
        boxes[7][6] = new Spot(7, 6, new Knight(true));
        boxes[7][7] = new Spot(7, 7, new Rook(true));

        boxes[6][0] = new Spot(6, 0, new Pawn(true));
        boxes[6][1] = new Spot(6, 1, new Pawn(true));
        boxes[6][2] = new Spot(6, 2, new Pawn(true));
        boxes[6][3] = new Spot(6, 3, new Pawn(true));
        boxes[6][4] = new Spot(6, 4, new Pawn(true));
        boxes[6][5] = new Spot(6, 5, new Pawn(true));
        boxes[6][6] = new Spot(6, 6, new Pawn(true));
        boxes[6][7] = new Spot(6, 7, new Pawn(true));


        // initialize remaining boxes without any piece
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                boxes[i][j] = new Spot(i, j, null);
            }
        }


        // test for stalemate
/*
        boxes[0][4] = new Spot(0, 4, new King(false));
        boxes[7][1] = new Spot(7, 1, new Rook(true));
        boxes[7][5] = new Spot(7, 5, new Queen(true));
        boxes[3][2] = new Spot(3, 2, new Bishop(true));
        boxes[7][4] = new Spot(7, 4, new King(true));
        boxes[6][0] = new Spot(6, 0, new Pawn(true));
        int[][] test = {{0, 4}, {7, 1},{7,5},{3,2},{7,4},{6,0}};
        boolean flag;
         for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                flag=false;
               int [] arr= {i,j};
                for (int[] element:
                test) {
                    if(element[0]==arr[0] && element[1]==arr[1]){
                        flag=true;
                        break;
                    }
                }
                if(!flag){
                    boxes[i][j] = new Spot(i, j, null);
                }

            }


        }
        */

        // test for insufficient material

        // test for Knight
        //true
       /* boxes[0][4] = new Spot(0, 4, new King(false));
        boxes[7][1] = new Spot(7, 1, new Knight(true));
        boxes[0][3] = new Spot(0, 3, new Queen(true));
        boxes[7][4] = new Spot(7, 4, new King(true));

        //false 2 knight
        boxes[0][4] = new Spot(0, 4, new King(false));
        boxes[7][1] = new Spot(7, 1, new Knight(true));
        boxes[0][3] = new Spot(0, 3, new Knight(true));
        boxes[7][4] = new Spot(7, 4, new King(true));


        //false 2 opposite knight
        boxes[0][4] = new Spot(0, 4, new King(false));
        boxes[7][1] = new Spot(7, 1, new Knight(true));
        boxes[0][3] = new Spot(0, 3, new Knight(false));
        boxes[7][4] = new Spot(7, 4, new King(true));
        int[][] test = {{0, 4}, {7, 1},{0,3},{7,4}};*/

        // test for Bishop
        //true
       /* boxes[0][4] = new Spot(0, 4, new King(false));
        boxes[7][1] = new Spot(7, 1, new Bishop(true));
        boxes[0][3] = new Spot(0, 3, new Queen(true));
        boxes[7][4] = new Spot(7, 4, new King(true));

        // false 2 Bishop
        boxes[0][4] = new Spot(0, 4, new King(false));
        boxes[7][1] = new Spot(7, 1, new Bishop(true));
        boxes[0][3] = new Spot(0, 3, new Bishop(true));
        boxes[7][4] = new Spot(7, 4, new King(true));
        int[][] test = {{0, 4}, {7, 1},{0,3},{7,4}};*/

        // test for two Bishop

        //true
       /* boxes[0][4] = new Spot(0, 4, new King(false));
        boxes[7][1] = new Spot(7, 1, new Bishop(true));
        boxes[0][2] = new Spot(0, 2, new Bishop(false));
        boxes[7][4] = new Spot(7, 4, new King(true));
        int[][] test = {{0, 4}, {7, 1},{0,2},{7,4}};

        //false
        boxes[0][4] = new Spot(0, 4, new King(false));
        boxes[7][1] = new Spot(7, 1, new Bishop(true));
        boxes[0][3] = new Spot(0, 3, new Bishop(false));
        boxes[7][4] = new Spot(7, 4, new King(true));
        int[][] test = {{0, 4}, {7, 1},{0,3},{7,4}};*/

        /*boolean flag;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                flag=false;
                int [] arr= {i,j};
                for (int[] element:
                        test) {
                    if(element[0]==arr[0] && element[1]==arr[1]){
                        flag=true;
                        break;
                    }
                }
                if(!flag){
                    boxes[i][j] = new Spot(i, j, null);
                }

            }


        }*/

    }
}

