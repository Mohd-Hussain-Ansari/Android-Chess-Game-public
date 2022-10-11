package com.hussain.chess.utils;


import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Spot;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(boolean white) {
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

        // A queen can move like a rook and bishop

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


        }

        // A bishop can move diagonally in any direction
        //up move
        else if (start.getX() > end.getX()) {
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
        return false;
    }

    @Override
    public List<String> ValidAvailableMove(Board board, Spot start) {
        List<String> validAvailableMove = new ArrayList<>();
        // A queen can move like rook and bishop
        //For black piece

        //up move
        if (start.getX() != 0) {
            // A rook can move vertically upward.
            for (int i = start.getX() - 1; i >= 0; i--) {
                if (board.boxes[i][start.getY()].getPiece() != null && board.boxes[i][start.getY()].getPiece().isWhite() == start.getPiece().isWhite()) {
                    break;
                }
                validAvailableMove.add("" + i + start.getY());

                if (board.boxes[i][start.getY()].getPiece() != null && board.boxes[i][start.getY()].getPiece().isWhite() != start.getPiece().isWhite()) {
                    break;
                }


            }
            // A bishop can move up left in diagonal.
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
            // A bishop can move up right in diagonal.
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
            // A rook can move vertically downward.
            for (int i = start.getX() + 1; i < 8; i++) {
                if (board.boxes[i][start.getY()].getPiece() != null && board.boxes[i][start.getY()].getPiece().isWhite() == start.getPiece().isWhite()) {
                    break;
                }
                validAvailableMove.add("" + i + start.getY());

                if (board.boxes[i][start.getY()].getPiece() != null && board.boxes[i][start.getY()].getPiece().isWhite() != start.getPiece().isWhite()) {
                    break;
                }

            }
            // A bishop can move down left in diagonal.
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
            // A bishop can move down right in diagonal.
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

        //Rook's horizontal movement
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


        return validAvailableMove;
    }
}

