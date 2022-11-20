package com.hussain.chess.utils;


import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Spot;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Spot start,
                           Spot end) {
        // we can't move the piece to a spot that has
        // a piece of the same colour
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()) {
            return false;
        }
        // A knight can move in L shape
        // checking whether L shape is created or not
        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());
        return x * y == 2;
    }

    @Override
    public List<String> ValidAvailableMove(Board board, Spot start) {
        List<String> validAvailableMove = new ArrayList<>();

        // A knight can move in L shape in any direction

        int x, y;
        // up movement
        if (start.getX() > 1) {
            // up right
            if (start.getY() < 7) {
                x = start.getX() - 2;
                y = start.getY() + 1;
                if (board.boxes[x][y].getPiece() == null || board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite()) {
                    validAvailableMove.add("" + x + y);
                }

            }
            //up left
            if (start.getY() > 0) {
                x = start.getX() - 2;
                y = start.getY() - 1;
                if (board.boxes[x][y].getPiece() == null || board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite()) {
                    validAvailableMove.add("" + x + y);
                }

            }

        }
        // down movement
        if (start.getX() < 6) {
            // down right
            if (start.getY() < 7) {
                x = start.getX() + 2;
                y = start.getY() + 1;
                if (board.boxes[x][y].getPiece() == null || board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite()) {
                    validAvailableMove.add("" + x + y);
                }

            }
            //down left
            if (start.getY() > 0) {
                x = start.getX() + 2;
                y = start.getY() - 1;
                if (board.boxes[x][y].getPiece() == null || board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite()) {
                    validAvailableMove.add("" + x + y);
                }

            }
        }

        // right movement
        if (start.getY() < 6) {
            // right down
            if (start.getX() < 7) {
                x = start.getX() + 1;
                y = start.getY() + 2;
                if (board.boxes[x][y].getPiece() == null || board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite()) {
                    validAvailableMove.add("" + x + y);
                }
            }
            //right up
            if (start.getX() > 0) {
                x = start.getX() - 1;
                y = start.getY() + 2;
                if (board.boxes[x][y].getPiece() == null || board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite()) {
                    validAvailableMove.add("" + x + y);
                }
            }
        }
        // left movement
        if (start.getY() > 1) {
            // left down
            if (start.getX() < 7) {
                x = start.getX() + 1;
                y = start.getY() - 2;
                if (board.boxes[x][y].getPiece() == null || board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite()) {
                    validAvailableMove.add("" + x + y);
                }
            }
            //left up
            if (start.getX() > 0) {
                x = start.getX() - 1;
                y = start.getY() - 2;
                if (board.boxes[x][y].getPiece() == null || board.boxes[x][y].getPiece().isWhite() != start.getPiece().isWhite()) {
                    validAvailableMove.add("" + x + y);
                }
            }
        }


        return validAvailableMove;
    }
}


