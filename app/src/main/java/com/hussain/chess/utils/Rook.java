package com.hussain.chess.utils;


import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Spot;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(boolean white) {
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
        // A Rook can move in straight line in its row and column

        //horizontal movement
        else if (end.getX() == start.getX()) {
            // is end position is left  from a start
            if (end.getY() < start.getY()) {
                for (int i = start.getY() - 1; i >= 0; i--) {
                    if (i == end.getY()) {
                        return true;
                    }
                    // is any piece occur in their path
                    else if (board.boxes[start.getX()][i].getPiece() != null) {
                        // rook can't move to that position
                        return false;
                    }


                }
            }
            // right move
            else {
                for (int i = start.getY() + 1; i < 8; i++) {
                    if (i == end.getY()) {
                        return true;
                    }
                    // is any piece occur in their path
                    else if (board.boxes[start.getX()][i].getPiece() != null) {
                        // rook can't move to that position
                        return false;
                    }


                }
            }


        }

        //vertical movement
        else if (start.getY() == end.getY()) {

            // is end position is up  from a start
            if (end.getX() < start.getX()) {
                for (int i = start.getX() - 1; i >= 0; i--) {
                    if (i == end.getX()) {
                        return true;
                    }
                    // is any piece occur in their path
                    else if (board.boxes[i][start.getY()].getPiece() != null) {
                        // rook can't move to that position
                        return false;
                    }


                }
            }

            // down move
            else {
                for (int i = start.getX() + 1; i < 8; i++) {
                    if (i == end.getX()) {
                        return true;
                    }
                    // is any piece occur in their path
                    else if (board.boxes[i][start.getY()].getPiece() != null) {
                        // rook can't move to that position
                        return false;
                    }


                }
            }


        } else {
            return false;
        }

        return false;
    }


    @Override
    public List<String> ValidAvailableMove(Board board, Spot start) {
        List<String> validAvailableMove = new ArrayList<>();
        // A Rook can move in straight line in its row and column

        //horizontal movement
        // left move
        if (start.getY() != 0) {
            for (int i = start.getY() - 1; i >= 0; i--) {

                if (board.boxes[start.getX()][i].getPiece() != null && board.boxes[start.getX()][i].getPiece().isWhite() == start.getPiece().isWhite()) {
                    break;
                }
                validAvailableMove.add("" + start.getX() + i);
                if (board.boxes[start.getX()][i].getPiece() != null && board.boxes[start.getX()][i].getPiece().isWhite() != start.getPiece().isWhite()) {
                    break;
                }
            }
        }

        // right move
        if (start.getY() != 7) {
            for (int i = start.getY() + 1; i < 8; i++) {
                if (board.boxes[start.getX()][i].getPiece() != null && board.boxes[start.getX()][i].getPiece().isWhite() == start.getPiece().isWhite()) {
                    break;
                }
                validAvailableMove.add("" + start.getX() + i);
                if (board.boxes[start.getX()][i].getPiece() != null && board.boxes[start.getX()][i].getPiece().isWhite() != start.getPiece().isWhite()) {
                    break;
                }


            }
        }

        //vertical movement
        //up move
        if (start.getX() != 0) {
            for (int i = start.getX() - 1; i >= 0; i--) {
                if (board.boxes[i][start.getY()].getPiece() != null && board.boxes[i][start.getY()].getPiece().isWhite() == start.getPiece().isWhite()) {
                    break;
                }
                validAvailableMove.add("" + i + start.getY());

                if (board.boxes[i][start.getY()].getPiece() != null && board.boxes[i][start.getY()].getPiece().isWhite() != start.getPiece().isWhite()) {
                    break;
                }


            }
        }
        // down move
        if (start.getX() != 7) {
            for (int i = start.getX() + 1; i < 8; i++) {
                if (board.boxes[i][start.getY()].getPiece() != null && board.boxes[i][start.getY()].getPiece().isWhite() == start.getPiece().isWhite()) {
                    break;
                }
                validAvailableMove.add("" + i + start.getY());

                if (board.boxes[i][start.getY()].getPiece() != null && board.boxes[i][start.getY()].getPiece().isWhite() != start.getPiece().isWhite()) {
                    break;
                }

            }
        }


        return validAvailableMove;
    }


}


