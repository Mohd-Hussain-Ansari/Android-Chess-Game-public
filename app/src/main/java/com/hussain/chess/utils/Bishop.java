package com.hussain.chess.utils;


import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Spot;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(boolean white) {
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
        // A bishop can move diagonally in any direction
        //up move
        if (start.getX() > end.getX()) {
            int x = start.getX();
            int y = start.getY();
            //right move
            if (start.getY() < end.getY()) {
                while (true) {
                    x -= 1;
                    y += 1;
                    if (x == end.getX() && y == end.getY()) {
                        return true;
                    }
                    //piece was blocked by another piece
                    else if (board.boxes[x][y].getPiece() != null) {
                        return false;
                    } else if (x <= 0 || y >= 7) {
                        return false;
                    }

                }
            }

            //left move
            else if (start.getY() > end.getY()) {
                while (true) {
                    x -= 1;
                    y -= 1;
                    if (x == end.getX() && y == end.getY()) {
                        return true;
                    }
                    //piece was blocked by another piece
                    else if (board.boxes[x][y].getPiece() != null) {
                        return false;
                    } else if (x <= 0 || y <= 0) {
                        return false;
                    }
                }
            } else
                return false;
        }

        //down move
        else if (start.getX() < end.getX()) {
            int x = start.getX();
            int y = start.getY();
            //right move
            if (start.getY() < end.getY()) {
                while (true) {
                    x += 1;
                    y += 1;

                    if (x == end.getX() && y == end.getY()) {
                        return true;
                    }
                    //piece was blocked by another piece
                    else if (board.boxes[x][y].getPiece() != null) {
                        return false;
                    } else if (x >= 7 || y >= 7) {
                        return false;
                    }
                }
            }

            //left move
            else if (start.getY() > end.getY()) {
                while (true) {
                    x += 1;
                    y -= 1;
                    if (x == end.getX() && y == end.getY()) {
                        return true;
                    }
                    //piece was blocked by another piece
                    else if (board.boxes[x][y].getPiece() != null) {
                        return false;
                    } else if (x >= 7 || y <= 0) {
                        return false;
                    }
                }
            } else
                return false;
        } else {
            return false;
        }

    }

    @Override
    public List<String> ValidAvailableMove(Board board, Spot start) {
        List<String> validAvailableMove = new ArrayList<>();
        // A bishop can move diagonally in any direction

        //up move
        if (start.getX() != 0) {
            //left move
            if (start.getY() != 0) {
                for (int i = start.getX() - 1, j = start.getY() - 1; i >= 0 && j >= 0; i--, j--) {
                    if (board.boxes[i][j].getPiece() != null && board.boxes[i][j].getPiece().isWhite() == start.getPiece().isWhite()) {
                        break;
                    }
                    validAvailableMove.add("" + i + j);
                    if (board.boxes[i][j].getPiece() != null && board.boxes[i][j].getPiece().isWhite() != start.getPiece().isWhite()) {
                        break;
                    }
                }
            }
            //right move
            if (start.getY() != 7) {
                for (int i = start.getX() - 1, j = start.getY() + 1; i >= 0 && j <= 7; i--, j++) {
                    if (board.boxes[i][j].getPiece() != null && board.boxes[i][j].getPiece().isWhite() == start.getPiece().isWhite()) {
                        break;
                    }
                    validAvailableMove.add("" + i + j);
                    if (board.boxes[i][j].getPiece() != null && board.boxes[i][j].getPiece().isWhite() != start.getPiece().isWhite()) {
                        break;
                    }
                }
            }

        }

        //down move
        if (start.getX() != 7) {
            //left move
            if (start.getY() != 0) {
                for (int i = start.getX() + 1, j = start.getY() - 1; i <= 7 && j >= 0; i++, j--) {
                    if (board.boxes[i][j].getPiece() != null && board.boxes[i][j].getPiece().isWhite() == start.getPiece().isWhite()) {
                        break;
                    }
                    validAvailableMove.add("" + i + j);
                    if (board.boxes[i][j].getPiece() != null && board.boxes[i][j].getPiece().isWhite() != start.getPiece().isWhite()) {
                        break;
                    }
                }
            }
            //right move
            if (start.getY() != 7) {
                for (int i = start.getX() + 1, j = start.getY() + 1; i <= 7 && j <= 7; i++, j++) {
                    if (board.boxes[i][j].getPiece() != null && board.boxes[i][j].getPiece().isWhite() == start.getPiece().isWhite()) {
                        break;
                    }
                    validAvailableMove.add("" + i + j);
                    if (board.boxes[i][j].getPiece() != null && board.boxes[i][j].getPiece().isWhite() != start.getPiece().isWhite()) {
                        break;
                    }
                }
            }

        }


        return validAvailableMove;
    }

}


