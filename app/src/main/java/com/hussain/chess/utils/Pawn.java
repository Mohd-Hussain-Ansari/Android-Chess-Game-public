package com.hussain.chess.utils;


import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Spot;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(boolean white) {
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
        // is white pawn
        if (isWhite()) {
            // is end position is vertically and no one is there in one move ahead
            if (start.getY() == end.getY() && board.boxes[start.getX() - 1][start.getY()].getPiece() == null) {
                // one move ahead
                if (end.getX() == start.getX() - 1) {
                    return true;
                }
                //in start pawn can move two move ahead
                else
                    return start.getX() == 6 && end.getX() == 4 && board.boxes[4][start.getY()].getPiece() == null;

            }
            // is end position one move ahead up right
            else if (start.getY() < 7 && end.getX() == start.getX() - 1 && end.getY() == start.getY() + 1 && board.boxes[start.getX() - 1][start.getY() + 1].getPiece() != null) {
                return true;
            }
            // is end position one move ahead up left
            else
                return start.getY() > 0 && end.getX() == start.getX() - 1 && end.getY() == start.getY() - 1 && board.boxes[start.getX() - 1][start.getY() - 1].getPiece() != null;
        }

        // black pawn
        else {
            // is end position is vertically and no one is there in one move ahead
            if (start.getY() == end.getY() && board.boxes[start.getX() + 1][start.getY()].getPiece() == null) {
                // one move ahead
                if (end.getX() == start.getX() + 1) {
                    return true;
                }
                //in start pawn can move two move ahead
                else
                    return start.getX() == 1 && end.getX() == 3 && board.boxes[3][start.getY()].getPiece() == null;

            }
            // is end position one move ahead up right
            else if (start.getY() < 7 && end.getX() == start.getX() + 1 && end.getY() == start.getY() + 1 && board.boxes[start.getX() + 1][start.getY() + 1].getPiece() != null) {
                return true;
            }
            // is end position one move ahead up left
            else
                return start.getY() > 0 && end.getX() == start.getX() + 1 && end.getY() == start.getY() - 1 && board.boxes[start.getX() + 1][start.getY() - 1].getPiece() != null;
        }


    }

    @Override
    public List<String> ValidAvailableMove(Board board, Spot start) {
        List<String> validAvailableMove = new ArrayList<>();

        // is white pawn
        if (isWhite()) {
            if (start.getX() > 0) {
                // is any piece is not present in  one move  ahead vertically
                if (board.boxes[start.getX() - 1][start.getY()].getPiece() == null) {
                    // one move ahead
                    validAvailableMove.add("" + (start.getX() - 1) + start.getY());
                    //in  pawn can move two move ahead
                    if (start.getX() == 6 && board.boxes[4][start.getY()].getPiece() == null) {
                        validAvailableMove.add("" + 4 + start.getY());
                    }

                }
                // is any black piece is  present in  one move  ahead up right
                if (start.getY() < 7 && board.boxes[start.getX() - 1][start.getY() + 1].getPiece() != null && !board.boxes[start.getX() - 1][start.getY() + 1].getPiece().isWhite()) {
                    validAvailableMove.add("" + (start.getX() - 1) + (start.getY() + 1));
                }
                // is any black piece is  present in  one move  ahead up left
                if (start.getY() > 0 && board.boxes[start.getX() - 1][start.getY() - 1].getPiece() != null && !board.boxes[start.getX() - 1][start.getY() - 1].getPiece().isWhite()) {
                    validAvailableMove.add("" + (start.getX() - 1) + (start.getY() - 1));
                }

            }

        }
        // black pawn
        else {
            if (start.getX() < 7) {
                // is any piece is not present in  one move  ahead vertically
                if (board.boxes[start.getX() + 1][start.getY()].getPiece() == null) {
                    // one move ahead
                    validAvailableMove.add("" + (start.getX() + 1) + start.getY());
                    //in  pawn can move two move ahead
                    if (start.getX() == 1 && board.boxes[3][start.getY()].getPiece() == null) {
                        validAvailableMove.add("" + 3 + start.getY());
                    }

                }
                // is any white piece is  present in  one move  ahead down right
                if (start.getY() < 7 && board.boxes[start.getX() + 1][start.getY() + 1].getPiece() != null && board.boxes[start.getX() + 1][start.getY() + 1].getPiece().isWhite()) {
                    validAvailableMove.add("" + (start.getX() + 1) + (start.getY() + 1));
                }
                // is any white piece is  present in  one move  ahead down left
                if (start.getY() > 0 && board.boxes[start.getX() + 1][start.getY() - 1].getPiece() != null && board.boxes[start.getX() + 1][start.getY() - 1].getPiece().isWhite()) {
                    validAvailableMove.add("" + (start.getX() + 1) + (start.getY() - 1));
                }

            }
        }

        return validAvailableMove;
    }
}

