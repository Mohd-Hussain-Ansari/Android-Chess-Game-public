package com.hussain.chess.utils;


import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Spot;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    // false means no piece has been move from start position
    // 0 for king
    // 1 for left rook
    // 2 for right rook
    public King(boolean white) {
        super(white);
    }


    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        // we can't move the piece to a Spot that
        // has a piece of the same color
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()) {
            return false;
        }
        // A king can move in one position ahead in any direction (left,right,diagonally)
        // x+y==1 for one step left or right movement
        //(x==1 && y==1) for diagonally one step movement
        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());
        return x + y == 1 || (x == 1 && y == 1);
    }


    @Override
    public List<String> ValidAvailableMove(Board board, Spot start) {

        // A king can move in one position ahead in any direction (left,right,diagonally)
        List<String> validAvailableMove = new ArrayList<>();
        int x, y;
        // down move
        if (start.getX() < 7) {
            x = start.getX() + 1;
            y = start.getY();
            if (board.boxes[x][y].getPiece() == null || (board.boxes[x][y].getPiece() != null && board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite())) {
                validAvailableMove.add("" + x + y);
            }
            // down right move
            if (start.getY() < 7) {
                y = start.getY() + 1;
                if (board.boxes[x][y].getPiece() == null || (board.boxes[x][y].getPiece() != null && board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite())) {
                    validAvailableMove.add("" + x + y);
                }
            }

            // down left move
            if (start.getY() > 0) {
                y = start.getY() - 1;
                if (board.boxes[x][y].getPiece() == null || (board.boxes[x][y].getPiece() != null && board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite())) {
                    validAvailableMove.add("" + x + y);
                }
            }

        }


        // up move
        if (start.getX() > 0) {
            x = start.getX() - 1;
            y = start.getY();
            if (board.boxes[x][y].getPiece() == null || (board.boxes[x][y].getPiece() != null && board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite())) {
                validAvailableMove.add("" + x + y);
            }
            // up right move
            if (start.getY() < 7) {
                y = start.getY() + 1;
                if (board.boxes[x][y].getPiece() == null || (board.boxes[x][y].getPiece() != null && board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite())) {
                    validAvailableMove.add("" + x + y);
                }
            }

            // up left move
            if (start.getY() > 0) {
                y = start.getY() - 1;
                if (board.boxes[x][y].getPiece() == null || (board.boxes[x][y].getPiece() != null && board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite())) {
                    validAvailableMove.add("" + x + y);
                }
            }

        }
        // right move
        if (start.getY() < 7) {
            x = start.getX();
            y = start.getY() + 1;
            if (board.boxes[x][y].getPiece() == null || (board.boxes[x][y].getPiece() != null && board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite())) {
                validAvailableMove.add("" + x + y);
            }
        }

        // left move
        if (start.getY() > 0) {
            x = start.getX();
            y = start.getY() - 1;
            if (board.boxes[x][y].getPiece() == null || (board.boxes[x][y].getPiece() != null && board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite())) {
                validAvailableMove.add("" + x + y);
            }
        }


        return validAvailableMove;
    }


}


