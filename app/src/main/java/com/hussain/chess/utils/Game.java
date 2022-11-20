package com.hussain.chess.utils;


import com.hussain.chess.Model.ByStatus;
import com.hussain.chess.Model.GameStatus;
import com.hussain.chess.Model.Move;
import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Player;
import com.hussain.chess.Model.Spot;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Game {
    private Player[] players;
    Board board;
    private Player currentTurn;
    GameStatus gameStatus;

    ByStatus byStatus;
    final Stack<Move> movesPlayed = new Stack<>();
    final Stack<Move> forwardMovesPlayed = new Stack<>();
    String blackKing = "04";
    String whiteKing = "74";
    private final boolean[] whiteCastling = {false, false, false};
    private final boolean[] blackCastling = {false, false, false};
    // false means no piece has been move from start position
    // 0 for king
    // 1 for left rook
    // 2 for right rook
    private boolean isCheck;


    public void initialize(Player p1, Player p2) {
        players = new Player[2];
        players[0] = p1;
        players[1] = p2;

        board = new Board();
        board.resetBoard();
        if (p1.isWhiteSide()) {
            this.currentTurn = p1;
        } else {
            this.currentTurn = p2;
        }
        this.gameStatus = GameStatus.ACTIVE;
        movesPlayed.clear();
        forwardMovesPlayed.clear();


    }


    public String getPlayerName(int index) {
        return players[index].name;
    }

    public Player getPlayer(int index) {
        return players[index];
    }

    public boolean isEnd() {
        return this.getGameStatus() != GameStatus.ACTIVE;
    }

    public GameStatus getGameStatus() {
        return this.gameStatus;
    }


    public ByStatus getByStatus() {
        return byStatus;
    }


    public boolean playerMove(Player player, int startX,
                              int startY, int endX, int endY) throws Exception {
        Spot startBox = board.getBox(startX, startY);
        Spot endBox = board.getBox(endX, endY);
        Move move = new Move(player, startBox, endBox);
        return this.makeMove(move, player);
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentTurn() {
        return currentTurn;
    }

    public List<String> getValidAvailableMove(Spot spot) {
        int x;
        int y;
        // getting valid move of a piece
        List<String> availableMove = spot.getPiece().ValidAvailableMove(board, spot);
        List<String> validAvailableMove = new ArrayList<>(availableMove);

        //show castling move
        if (spot.getPiece() instanceof King) {
            boolean[] castling;
            if (currentTurn.isWhiteSide()) {
                castling = whiteCastling;
            } else {
                castling = blackCastling;
            }

            // is king can do castling means he didn't move from start position
            if (!castling[0]) {

                // checking king is in correct position
                if (spot.getY() == 4 && (spot.getX() == 0 || spot.getX() == 7)) {
                    if (isCurrentTurnKingBeingAttack("" + spot.getX() + spot.getY()) == null) {
                        // castling with left rook
                        // checking rook is in correct position
                        if (!castling[1] && board.boxes[spot.getX()][0].getPiece() instanceof Rook) {
                            // checking is no pieces is there in between rook and king. And No opponent piece are attacking in between rook and king.
                            if (board.boxes[spot.getX()][3].getPiece() == null && board.boxes[spot.getX()][2].getPiece() == null &&
                                    board.boxes[spot.getX()][1].getPiece() == null && isCurrentTurnKingBeingAttack("" + spot.getX() + 3) == null &&
                                    isCurrentTurnKingBeingAttack("" + spot.getX() + 2) == null) {

                                validAvailableMove.add("" + spot.getX() + 2);
                            }
                        }
                        // castling with right rook
                        // checking rook is in correct position
                        if (!castling[2] && board.boxes[spot.getX()][7].getPiece() instanceof Rook) {
                            // checking is no pieces is there in between rook and king. And No opponent piece are attacking in between rook and king.
                            if (board.boxes[spot.getX()][6].getPiece() == null && board.boxes[spot.getX()][5].getPiece() == null &&
                                    isCurrentTurnKingBeingAttack("" + spot.getX() + 6) == null && isCurrentTurnKingBeingAttack("" + spot.getX() + 5) == null &&
                                    isCurrentTurnKingBeingAttack("" + spot.getX() + 4) == null) {

                                validAvailableMove.add("" + spot.getX() + 6);
                            }
                        }
                    }

                }
            }

        }

        //show En passant move
        else if (spot.getPiece() instanceof Pawn) {
            try {
                Move previousMove = movesPlayed.get(movesPlayed.size() - 1);
                Spot previousMoveEnd = previousMove.getEnd();
                if (spot.getX() == previousMoveEnd.getX() && Math.abs(previousMoveEnd.getX() - previousMove.getStart().getX()) == 2) {

                    if (previousMoveEnd.getY() == spot.getY() + 1 || previousMoveEnd.getY() == spot.getY() - 1) {
                        Piece enPassantPiece = previousMoveEnd.getPiece();
                        previousMoveEnd.setPiece(spot.getPiece());
                        board.boxes[spot.getX()][spot.getY()].setPiece(null);
                        if (currentTurn.isWhiteSide() && isCurrentTurnKingBeingAttack(whiteKing) == null) {
                            validAvailableMove.add("" + (previousMoveEnd.getX() - 1) + previousMoveEnd.getY());

                        } else if (!currentTurn.isWhiteSide() && isCurrentTurnKingBeingAttack(blackKing) == null) {
                            validAvailableMove.add("" + (previousMoveEnd.getX() + 1) + previousMoveEnd.getY());
                        }
                        board.boxes[spot.getX()][spot.getY()].setPiece(previousMoveEnd.getPiece());
                        previousMoveEnd.setPiece(enPassantPiece);

                    }


                }
            } catch (IndexOutOfBoundsException ex) {

            }

        }
        for (String move : availableMove) {
            x = Integer.parseInt(Character.toString(move.charAt(0)));
            y = Integer.parseInt(Character.toString(move.charAt(1)));
            Piece piece = board.boxes[x][y].getPiece();
            // check if this move will not result in the king  being attacked
            board.boxes[x][y].setPiece(spot.getPiece());
            board.boxes[spot.getX()][spot.getY()].setPiece(null);
            Spot pieceSpot;
            Piece currentPiece;
            String king;
            if (spot.getPiece() instanceof King) {
                king = "" + x + y;
            } else {
                king = currentTurn.isWhiteSide() ? whiteKing : blackKing;

            }
            boolean flag = false;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    pieceSpot = board.boxes[i][j];
                    currentPiece = pieceSpot == null ? null : pieceSpot.getPiece();

                    // is any piece giving a check to a current turn king
                    if (currentPiece != null && currentPiece.isWhite() != currentTurn.isWhiteSide() && currentPiece.ValidAvailableMove(board, pieceSpot).contains(king)) {
                        // remove that move from a validAvailableMove
                        validAvailableMove.remove(move);
                        flag = true;
                        break;

                    }
                }
                if (flag) {
                    break;
                }

            }
            // roll back previous move
            board.boxes[x][y].setPiece(piece);
            board.boxes[spot.getX()][spot.getY()].setPiece(spot.getPiece());

        }
        return validAvailableMove;
    }

    boolean makeMove(Move move, Player player) {
        Piece sourcePiece = move.getStart().getPiece();
        if (sourcePiece == null) {
            return false;
        }

        // valid player
        if (player.isWhiteSide() != currentTurn.isWhiteSide()) {
            return false;
        }

        if (sourcePiece.isWhite() != player.isWhiteSide()) {
            return false;
        }


        // valid move?
        if (!sourcePiece.canMove(board, move.getStart(),
                move.getEnd())) {

            if (move.getStart().getPiece() instanceof King) {
                boolean[] castling;
                if (currentTurn.isWhiteSide()) {
                    castling = whiteCastling;
                } else {
                    castling = blackCastling;
                }
                // is king can do castling means he didn't move from start position
                if (!castling[0]) {
                    Spot start = move.getStart();
                    Spot end = move.getEnd();
                    // checking king is in correct position
                    if (start.getY() == 4 && (start.getX() == 0 || start.getX() == 7)) {
                        if (isCurrentTurnKingBeingAttack("" + start.getX() + start.getY()) == null) {
                            List<String> afterCastlingPosition = new ArrayList<>();
                            List<String> beforeCastlingPosition = new ArrayList<>();
                            // castling with left rook
                            // checking rook is in correct position
                            if (!castling[1] && end.getY() == 2 && board.boxes[start.getX()][0].getPiece() instanceof Rook) {
                                // checking is no pieces is there in between rook and king. And No opponent piece are attacking in between rook and king.
                                if (board.boxes[start.getX()][3].getPiece() == null && board.boxes[start.getX()][2].getPiece() == null &&
                                        board.boxes[start.getX()][1].getPiece() == null && isCurrentTurnKingBeingAttack("" + start.getX() + 3) == null &&
                                        isCurrentTurnKingBeingAttack("" + start.getX() + 2) == null) {
                                    move.setCastlingMove(true);
                                    afterCastlingPosition.add("" + start.getX() + (start.getY() - 2));
                                    afterCastlingPosition.add("" + start.getX() + (start.getY() - 1));

                                    beforeCastlingPosition.add("" + start.getX() + 4);
                                    beforeCastlingPosition.add("" + start.getX() + 0);

                                    move.setAfterMovingPosition(afterCastlingPosition);
                                    move.setBeforeMovingPosition(beforeCastlingPosition);
                                }
                            }
                            // castling with right rook
                            // checking rook is in correct position
                            else if (!castling[2] && end.getY() == 6 && board.boxes[start.getX()][7].getPiece() instanceof Rook) {
                                // checking is no pieces is there in between rook and king. And No opponent piece are attacking in between rook and king.
                                if (board.boxes[start.getX()][6].getPiece() == null && board.boxes[start.getX()][5].getPiece() == null &&
                                        isCurrentTurnKingBeingAttack("" + start.getX() + 6) == null && isCurrentTurnKingBeingAttack("" + start.getX() + 5) == null &&
                                        isCurrentTurnKingBeingAttack("" + start.getX() + 4) == null) {

                                    move.setCastlingMove(true);
                                    afterCastlingPosition.add("" + start.getX() + (start.getY() + 2));
                                    afterCastlingPosition.add("" + start.getX() + (start.getY() + 1));

                                    beforeCastlingPosition.add("" + start.getX() + 4);
                                    beforeCastlingPosition.add("" + start.getX() + 7);

                                    move.setAfterMovingPosition(afterCastlingPosition);
                                    move.setBeforeMovingPosition(beforeCastlingPosition);
                                }
                            }
                        }

                    }
                }


            } else if (move.getStart().getPiece() instanceof Pawn) {
                Spot start = move.getStart();
                Spot end = move.getEnd();
                if (start.getY() != end.getY() && end.getPiece() == null) {
                    Move previousMove = movesPlayed.get(movesPlayed.size() - 1);
                    // is previous move  piece is pawn and is pawn move two move ahead from start position.
                    if (previousMove.getEnd().getPiece() instanceof Pawn && Math.abs(previousMove.getEnd().getX() - previousMove.getStart().getX()) == 2) {
                        // is previous move pawn is in same row with current pawn. and in the both will come in same column.
                        if (start.getX() == previousMove.getEnd().getX() && end.getY() == previousMove.getEnd().getY()) {
                            List<String> beforeEnPassantPosition = new ArrayList<>();
                            List<String> afterEnPassantPosition = new ArrayList<>();
                            beforeEnPassantPosition.add("" + previousMove.getEnd().getX() + previousMove.getEnd().getY());
                            beforeEnPassantPosition.add("" + start.getX() + start.getY());
                            afterEnPassantPosition.add(beforeEnPassantPosition.get(0));
                            if (this.currentTurn.isWhiteSide() && start.getPiece().isWhite()) {
                                if (end.getX() == start.getX() - 1 && (end.getY() == start.getY() + 1 || end.getY() == start.getY() - 1)) {
                                    move.setEnpersandMove(true);
                                    afterEnPassantPosition.add("" + (previousMove.getEnd().getX() - 1) + previousMove.getEnd().getY());
                                    move.setBeforeMovingPosition(beforeEnPassantPosition);
                                    move.setAfterMovingPosition(afterEnPassantPosition);
                                    move.setPieceKilled(previousMove.getPieceMoved());

                                }
                            } else if (!this.currentTurn.isWhiteSide() && !start.getPiece().isWhite()) {
                                if (end.getX() == start.getX() + 1 && (end.getY() == start.getY() + 1 || end.getY() == start.getY() - 1)) {
                                    move.setEnpersandMove(true);
                                    afterEnPassantPosition.add("" + (previousMove.getEnd().getX() + 1) + previousMove.getEnd().getY());
                                    move.setBeforeMovingPosition(beforeEnPassantPosition);
                                    move.setAfterMovingPosition(afterEnPassantPosition);
                                }
                                move.setPieceKilled(previousMove.getPieceMoved());
                            }
                        }
                    }


                }
            }
            if (!move.isCastlingMove() && !move.isEnpersandMove()) {
                return false;
            }

        }


        // kill?
        Piece destPiece = move.getEnd().getPiece();
        if (destPiece != null) {
            destPiece.setKilled(true);
            move.setPieceKilled(destPiece);
        }


        String king;
        if (move.getStart().getPiece() instanceof King) {
            king = "" + move.getEnd().getX() + move.getEnd().getY();
        } else {
            king = currentTurn.isWhiteSide() ? whiteKing : blackKing;
        }
        // move piece from the start box to end box
        move.getEnd().setPiece(move.getStart().getPiece());
        move.getStart().setPiece(null);
        Piece enPassantPiece = null;
        Spot enPassantSpot = null;
        // is move is a En passant move
        if (move.isEnpersandMove()) {
            enPassantSpot = movesPlayed.get(movesPlayed.size() - 1).getEnd();
            enPassantPiece = enPassantSpot.getPiece();
            enPassantSpot.setPiece(null);
        }

        // is move is a castling move
        if (move.isCastlingMove()) {
            // change rook position

            Spot spot = board.boxes[move.getStart().getX()][Integer.parseInt(String.valueOf(move.getBeforeMovingPosition().get(1).charAt(1)))];
            board.boxes[move.getStart().getX()][Integer.parseInt(String.valueOf(move.getAfterMovingPosition().get(1).charAt(1)))].setPiece(spot.getPiece());
            spot.setPiece(null);
        } else {
            // check if this move will not result in the king  being attacked
            if (isCurrentTurnKingBeingAttack(king) != null) {
                // rollback previous move
                move.getStart().setPiece(move.getEnd().getPiece());
                move.getEnd().setPiece(null);
                // is move is a En passant move
                if (move.isEnpersandMove()) {
                    assert enPassantSpot != null;
                    enPassantSpot.setPiece(enPassantPiece);
                }
                return false;
            }

        }


        Piece piece = move.getEnd().getPiece();

        // is current turn is white piece player
        if (currentTurn.isWhiteSide()) {
            // is moving piece is a king
            if (piece instanceof King) {
                // change white king value to a moving piece end position
                whiteKing = king;
                // change value of whiteCastling to know king   has been moved from start position
                whiteCastling[0] = true;

            }
            // is white king does not move anywhere
            else if (!whiteCastling[0]) {
                if (piece instanceof Rook) {
                    if (move.getStart().getY() == 0) {
                        // change value of whiteCastling to know left rook  has been moved from start position
                        whiteCastling[1] = true;
                    } else {
                        // change value of whiteCastling to know right rook  has been moved from start position
                        whiteCastling[2] = true;
                    }
                }
            }

        }
        //  current turn is black piece player
        else {
            if (piece instanceof King) {
                // change black king value to a moving piece end position
                blackKing = king;
                // change value of blackCastling to know king has changed position
                blackCastling[0] = true;
            }
            // is white king does not move anywhere
            else if (!blackCastling[0]) {
                if (piece instanceof Rook) {
                    if (move.getStart().getY() == 0) {
                        // change value of blackCastling to know left rook  has been moved from start position
                        blackCastling[1] = true;
                    } else {
                        // change value of blackCastling to know right rook  has been moved from start position
                        blackCastling[2] = true;
                    }
                }

            }
        }

        if (piece instanceof Pawn) {
            if (currentTurn.isWhiteSide() && move.getEnd().getX() == 0) {
                move.setRevivingMove(true);
            } else if (!currentTurn.isWhiteSide() && move.getEnd().getX() == 7) {
                move.setRevivingMove(true);
            }
        }
        // store the move
        movesPlayed.add(move);
        forwardMovesPlayed.clear();


        // set the current turn to the other player
        if (this.currentTurn.isWhiteSide() == players[0].isWhiteSide()) {
            this.currentTurn = players[1];
        } else {
            this.currentTurn = players[0];
        }

        checkGameEndCondition();
        return true;

    }


    public boolean isValidMove(Move move, Player player) {
        Piece sourcePiece = move.getStart().getPiece();
        if (sourcePiece == null) {
            return false;
        }


        if (sourcePiece.isWhite() != player.isWhiteSide()) {
            return false;
        }


        // valid move?
        if (!sourcePiece.canMove(board, move.getStart(),
                move.getEnd())) {

            if (move.getStart().getPiece() instanceof King) {
                boolean[] castling;
                if (currentTurn.isWhiteSide()) {
                    castling = whiteCastling;
                } else {
                    castling = blackCastling;
                }
                // is king can do castling means he didn't move from start position
                if (!castling[0]) {
                    Spot start = move.getStart();
                    Spot end = move.getEnd();
                    // checking king is in correct position
                    if (start.getY() == 4 && (start.getX() == 0 || start.getX() == 7)) {
                        if (isCurrentTurnKingBeingAttack("" + start.getX() + start.getY()) == null) {
                            List<String> afterCastlingPosition = new ArrayList<>();
                            List<String> beforeCastlingPosition = new ArrayList<>();
                            // castling with left rook
                            // checking rook is in correct position
                            if (!castling[1] && end.getY() == 2 && board.boxes[start.getX()][0].getPiece() instanceof Rook) {
                                // checking is no pieces is there in between rook and king. And No opponent piece are attacking in between rook and king.
                                if (board.boxes[start.getX()][3].getPiece() == null && board.boxes[start.getX()][2].getPiece() == null &&
                                        board.boxes[start.getX()][1].getPiece() == null && isCurrentTurnKingBeingAttack("" + start.getX() + 3) == null &&
                                        isCurrentTurnKingBeingAttack("" + start.getX() + 2) == null) {
                                    move.setCastlingMove(true);
                                    afterCastlingPosition.add("" + start.getX() + (start.getY() - 2));
                                    afterCastlingPosition.add("" + start.getX() + (start.getY() - 1));

                                    beforeCastlingPosition.add("" + start.getX() + 4);
                                    beforeCastlingPosition.add("" + start.getX() + 0);

                                    move.setAfterMovingPosition(afterCastlingPosition);
                                    move.setBeforeMovingPosition(beforeCastlingPosition);
                                }
                            }
                            // castling with right rook
                            // checking rook is in correct position
                            else if (!castling[2] && end.getY() == 6 && board.boxes[start.getX()][7].getPiece() instanceof Rook) {
                                // checking is no pieces is there in between rook and king. And No opponent piece are attacking in between rook and king.
                                if (board.boxes[start.getX()][6].getPiece() == null && board.boxes[start.getX()][5].getPiece() == null &&
                                        isCurrentTurnKingBeingAttack("" + start.getX() + 6) == null && isCurrentTurnKingBeingAttack("" + start.getX() + 5) == null &&
                                        isCurrentTurnKingBeingAttack("" + start.getX() + 4) == null) {

                                    move.setCastlingMove(true);
                                    afterCastlingPosition.add("" + start.getX() + (start.getY() + 2));
                                    afterCastlingPosition.add("" + start.getX() + (start.getY() + 1));

                                    beforeCastlingPosition.add("" + start.getX() + 4);
                                    beforeCastlingPosition.add("" + start.getX() + 7);

                                    move.setAfterMovingPosition(afterCastlingPosition);
                                    move.setBeforeMovingPosition(beforeCastlingPosition);
                                }
                            }
                        }

                    }
                }


            } else if (move.getStart().getPiece() instanceof Pawn) {
                Spot start = move.getStart();
                Spot end = move.getEnd();
                if (start.getY() != end.getY() && end.getPiece() == null) {
                    Move previousMove = movesPlayed.get(movesPlayed.size() - 1);
                    // is previous move  piece is pawn and is pawn move two move ahead from start position.
                    if (previousMove.getEnd().getPiece() instanceof Pawn && Math.abs(previousMove.getEnd().getX() - previousMove.getStart().getX()) == 2) {
                        // is previous move pawn is in same row with current pawn. and in the both will come in same column.
                        if (start.getX() == previousMove.getEnd().getX() && end.getY() == previousMove.getEnd().getY()) {
                            List<String> beforeEnPassantPosition = new ArrayList<>();
                            List<String> afterEnPassantPosition = new ArrayList<>();
                            beforeEnPassantPosition.add("" + previousMove.getEnd().getX() + previousMove.getEnd().getY());
                            beforeEnPassantPosition.add("" + start.getX() + start.getY());
                            afterEnPassantPosition.add(beforeEnPassantPosition.get(0));
                            if (this.currentTurn.isWhiteSide() && start.getPiece().isWhite()) {
                                if (end.getX() == start.getX() - 1 && (end.getY() == start.getY() + 1 || end.getY() == start.getY() - 1)) {
                                    move.setEnpersandMove(true);
                                    afterEnPassantPosition.add("" + (previousMove.getEnd().getX() - 1) + previousMove.getEnd().getY());
                                    move.setBeforeMovingPosition(beforeEnPassantPosition);
                                    move.setAfterMovingPosition(afterEnPassantPosition);
                                    move.setPieceKilled(previousMove.getPieceMoved());

                                }
                            } else if (!this.currentTurn.isWhiteSide() && !start.getPiece().isWhite()) {
                                if (end.getX() == start.getX() + 1 && (end.getY() == start.getY() + 1 || end.getY() == start.getY() - 1)) {
                                    move.setEnpersandMove(true);
                                    afterEnPassantPosition.add("" + (previousMove.getEnd().getX() + 1) + previousMove.getEnd().getY());
                                    move.setBeforeMovingPosition(beforeEnPassantPosition);
                                    move.setAfterMovingPosition(afterEnPassantPosition);
                                }
                                move.setPieceKilled(previousMove.getPieceMoved());
                            }
                        }
                    }


                }
            }
            if (!move.isCastlingMove() && !move.isEnpersandMove()) {
                return false;
            }

        }


        // kill?
        Piece destPiece = move.getEnd().getPiece();
        if (destPiece != null) {
            destPiece.setKilled(true);
            move.setPieceKilled(destPiece);
        }


        String king;
        if (move.getStart().getPiece() instanceof King) {
            king = "" + move.getEnd().getX() + move.getEnd().getY();
        } else {
            king = currentTurn.isWhiteSide() ? whiteKing : blackKing;
        }
        // move piece from the start box to end box
        move.getEnd().setPiece(move.getStart().getPiece());
        move.getStart().setPiece(null);
        // check if this move will not result in the king  being attacked
        if (isCurrentTurnKingBeingAttack(king) != null) {
            // rollback previous move
            move.getStart().setPiece(move.getEnd().getPiece());
            move.getEnd().setPiece(null);
            return false;
        }

        // rollback previous move
        move.getStart().setPiece(move.getEnd().getPiece());
        move.getEnd().setPiece(null);


        Piece piece = move.getEnd().getPiece();


        if (piece instanceof Pawn) {
            if (currentTurn.isWhiteSide() && move.getEnd().getX() == 0) {
                move.setRevivingMove(true);
            } else if (!currentTurn.isWhiteSide() && move.getEnd().getX() == 7) {
                move.setRevivingMove(true);
            }
        }

        return true;

    }

    // check all the end condition of the game
    private void checkGameEndCondition() {
        // is checkmate
        if (isCheckmate()) {

            if (currentTurn.isWhiteSide()) {
                gameStatus = GameStatus.BLACK_WINS;
            } else {
                gameStatus = GameStatus.WHITE_WINS;
            }
            byStatus = ByStatus.CHECKMATE;
        }
        // is stalemate
        else if (isStalemate()) {
            gameStatus = GameStatus.DRAW;
            byStatus = ByStatus.STALEMATE;
        }
        // is repetition draw
        else if (isRepetitionDraw()) {
            gameStatus = GameStatus.DRAW;
            byStatus = ByStatus.Repetition;
        }

        // is fifty moves  draw
        else if (isFiftyMoveDraw()) {
            gameStatus = GameStatus.DRAW;
            byStatus = ByStatus.Without_Capturing_Or_Moving_A_Pawn_By_Both_Players;
        } else if (inSufficientMaterialDraw()) {
            gameStatus = GameStatus.DRAW;
            byStatus = ByStatus.Insufficient_Material;
        }
    }

    // check is current turn king being attack by opponent piece
    protected Spot isCurrentTurnKingBeingAttack(String king) {

        Spot pieceSpot;
        Piece piece;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieceSpot = board.boxes[i][j];
                piece = pieceSpot == null ? null : pieceSpot.getPiece();

                // is any piece attack on  a current turn king
                if (piece != null && piece.isWhite() != currentTurn.isWhiteSide() && piece.ValidAvailableMove(board, pieceSpot).contains(king)) {
                    // return that attacking spots
                    return pieceSpot;

                }
            }


        }
        return null;
    }

    // check is king can kill the piece
    protected boolean isKingCanKillThePiece(Spot attackingSpot, Spot kingSpot) {
        boolean isKingCanProtect = true;
        Piece attackingPiece = attackingSpot.getPiece();
        Piece kingPiece = kingSpot.getPiece();

        //  is king have a move to kill the piece
        if (kingPiece.ValidAvailableMove(board, kingSpot).contains("" + attackingSpot.getX() + attackingSpot.getY())) {
            // move king to attacking position
            attackingSpot.setPiece(kingPiece);
            kingSpot.setPiece(null);

            String king = "" + attackingSpot.getX() + attackingSpot.getY();
            for (int row = 0; row < 8; row++) {
                for (int column = 0; column < 8; column++) {
                    Spot Spot = board.boxes[row][column];
                    Piece piece = Spot == null ? null : Spot.getPiece();
                    // after moving is any piece giving a check to a king
                    if (piece != null && piece.isWhite() != currentTurn.isWhiteSide() && piece.ValidAvailableMove(board, Spot).contains(king)) {
                        isKingCanProtect = false;
                        break;
                    }
                }
                if (!isKingCanProtect) {
                    break;
                }
            }
            // rollback king and attacking piece position
            attackingSpot.setPiece(attackingPiece);
            kingSpot.setPiece(kingPiece);

        } else {
            return false;
        }
        return isKingCanProtect;
    }

    // give other piece move to protect the piece
    protected List<String> getKingProtectionMove(Spot attackingSpot, Spot kingSpot) {
        int attackingSpotX = attackingSpot.getX();
        int attackingSpotY = attackingSpot.getY();
        int kingSpotX = kingSpot.getX();
        int kingSpotY = kingSpot.getY();

        Piece attackingPiece = attackingSpot.getPiece();

        List<String> kingProtectionMove = new ArrayList<>();

        kingProtectionMove.add("" + attackingSpotX + attackingSpotY);
        // finding location of the piece who attack an opponent king  with respect to opponent king
        if (!(attackingPiece instanceof Knight)) {
            // attacking piece is horizontal to a king
            if (attackingSpotX == kingSpotX) {
                //attacking piece is in left position to a king
                if (attackingSpotY < kingSpotY) {
                    for (int i = attackingSpotY + 1; i < kingSpotY; i++) {
                        kingProtectionMove.add("" + kingSpotX + i);
                    }
                }
                //attacking piece is in right position to a king
                if (attackingSpotY > kingSpotY) {
                    for (int i = attackingSpotY - 1; i > kingSpotY; i--) {
                        kingProtectionMove.add("" + kingSpotX + i);
                    }
                }
            }
            // attacking piece is vertical to a king
            else if (attackingSpotY == kingSpotY) {
                //attacking piece is in upward position to a king
                if (attackingSpotX < kingSpotX) {
                    for (int i = attackingSpotX + 1; i < kingSpotX; i++) {
                        kingProtectionMove.add("" + i + kingSpotY);
                    }
                }
                //attacking piece is in downward position to a king
                if (attackingSpotX > kingSpotX) {
                    for (int i = attackingSpotX - 1; i > kingSpotX; i--) {
                        kingProtectionMove.add("" + i + kingSpotY);
                    }
                }
            }

            // check piece is up  diagonal to a king
            else if (attackingSpotX < kingSpotX) {
                // up right diagonal
                if (attackingSpotY > kingSpotY) {
                    for (int i = attackingSpotX + 1, j = attackingSpotY - 1; i < kingSpotX; i++, j--) {
                        kingProtectionMove.add("" + i + j);
                    }
                }

                // up left diagonal
                else {
                    for (int i = attackingSpotX + 1, j = attackingSpotY + 1; i < kingSpotX; i++, j++) {
                        kingProtectionMove.add("" + i + j);
                    }
                }

            }
            // check piece is down  diagonal to a king
            else {
                // down right diagonal
                if (attackingSpotY > kingSpotY) {
                    for (int i = attackingSpotX - 1, j = attackingSpotY - 1; i > kingSpotX; i--, j--) {
                        kingProtectionMove.add("" + i + j);
                    }
                }

                //  down left  diagonal
                else {
                    for (int i = attackingSpotX - 1, j = attackingSpotY + 1; i > kingSpotX; i--, j++) {
                        kingProtectionMove.add("" + i + j);
                    }

                }

            }
        }

        return kingProtectionMove;
    }

    // check is other piece can protect king
    private boolean isPieceCanProtectKing(List<String> kingProtectionMove) {
        Spot spot;
        Piece piece;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                spot = board.boxes[i][j];
                piece = spot == null ? null : spot.getPiece();
                if (piece != null && !(piece instanceof King) && piece.isWhite() == currentTurn.isWhiteSide()) {


                    List<String> protectMove = new ArrayList<>(kingProtectionMove);
                    //protectMove.addAll(kingProtectionMove);

                    protectMove.retainAll(piece.ValidAvailableMove(board, spot));

                    if (protectMove.size() != 0) {
                        return true;
                    }

                }
            }

        }
        return false;
    }

    // check is king can move to any location
    private boolean isKingCanMove(Spot kingSpot, String attackingPiece) {
        Piece kingPiece = kingSpot.getPiece();
        if (kingPiece != null) {
            List<String> kingAvailableMove = kingPiece.ValidAvailableMove(board, kingSpot);
            Spot pieceSpot;
            Piece piece;
            boolean flag;
            if (attackingPiece != null) {
                kingAvailableMove.remove(attackingPiece);
            }
            for (String kingMove : kingPiece.ValidAvailableMove(board, kingSpot)) {
                flag = false;
                for (int pieceX = 0; pieceX < 8; pieceX++) {
                    for (int pieceY = 0; pieceY < 8; pieceY++) {
                        pieceSpot = board.boxes[pieceX][pieceY];
                        piece = pieceSpot == null ? null : pieceSpot.getPiece();
                        // is any piece attack on a  king available move location
                        if (piece != null && piece.isWhite() != currentTurn.isWhiteSide() && piece.ValidAvailableMove(board, pieceSpot).contains(kingMove)) {
                            // remove that king  move, from a king
                            kingAvailableMove.remove(kingMove);
                            flag = true;
                            break;
                        }

                    }
                    if (flag) {
                        break;
                    }

                }


            }

            return kingAvailableMove.size() == 0;
        }
        return true;
    }

    // check is is current turn piece can move to any location except king
    private boolean isCurrentTurnPieceCanMove() {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Spot spot = board.boxes[i][j];
                Piece piece = spot.getPiece();
                if (piece != null && !(piece instanceof King) && piece.isWhite() == currentTurn.isWhiteSide() && piece.ValidAvailableMove(board, spot).size() != 0) {
                    // set this piece can move to any location
                    return true;
                }
            }
        }
        return false;
    }


    public Move getLastMove() {
        try {
            return movesPlayed.get((movesPlayed.size() - 1));
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }


    public boolean changePiece(int x, int y, String tag) {
        Spot spot = board.boxes[x][y];
        boolean isWhite = spot.getPiece().isWhite();
        Piece piece;
        if (tag.equalsIgnoreCase("queen")) {
            piece = new Queen(isWhite);
        } else if (tag.equalsIgnoreCase("rook")) {
            piece = new Rook(isWhite);
        } else if (tag.equalsIgnoreCase("knight")) {
            piece = new Knight(isWhite);
        } else {
            piece = new Bishop(isWhite);
        }
        if (!movesPlayed.isEmpty()) {
            movesPlayed.peek().setRevivingPiece(piece);
        }
        spot.setPiece(piece);

        checkGameEndCondition();

        return isWhite;
    }


    public void undoRevivingMove() {
        if (!movesPlayed.isEmpty()) {
            Move move = movesPlayed.pop();
            board.boxes[move.getStart().getX()][move.getStart().getY()].setPiece(move.getEnd().getPiece());
            move.getEnd().setPiece(move.getPieceKilled());
            // set the current turn to the other player
            if (this.currentTurn == players[0]) {
                this.currentTurn = players[1];
            } else {
                this.currentTurn = players[0];
            }
        }

    }

    public Move undoMove() {
        if (!movesPlayed.isEmpty()) {

            Move move = movesPlayed.pop();
            board.boxes[move.getStart().getX()][move.getStart().getY()].setPiece(move.getEnd().getPiece());
            if (move.isEnpersandMove()) {
                board.boxes[move.getEnd().getX()][move.getEnd().getY()].setPiece(null);

                String killedPiecePosition = move.getBeforeMovingPosition().get(0);
                board.boxes[Integer.parseInt(String.valueOf(killedPiecePosition.charAt(0)))]
                        [Integer.parseInt(String.valueOf(killedPiecePosition.charAt(1)))].setPiece(move.getPieceKilled());
            } else {
                board.boxes[move.getEnd().getX()][move.getEnd().getY()].setPiece(move.getPieceKilled());
                if (move.isCastlingMove()) {
                    String castlingRook = move.getAfterMovingPosition().get(1);
                    Spot rookSpot = board.boxes[Integer.parseInt(String.valueOf(castlingRook.charAt(0)))][Integer.parseInt(String.valueOf(castlingRook.charAt(1)))];
                    Piece rookPiece = rookSpot.getPiece();
                    rookSpot.setPiece(null);
                    castlingRook = move.getBeforeMovingPosition().get(1);
                    board.boxes[Integer.parseInt(String.valueOf(castlingRook.charAt(0)))][Integer.parseInt(String.valueOf(castlingRook.charAt(1)))].setPiece(rookPiece);

                    boolean isRookLeftSide = Integer.parseInt(String.valueOf(castlingRook.charAt(1))) == 0;
                    if (move.getPieceMoved().isWhite()) {
                        whiteCastling[0] = false;
                        whiteKing = "74";
                        if (isRookLeftSide) {
                            whiteCastling[1] = false;
                        } else {
                            whiteCastling[2] = false;
                        }
                    } else {
                        blackCastling[0] = false;
                        blackKing = "04";
                        if (isRookLeftSide) {
                            blackCastling[1] = false;
                        } else {
                            blackCastling[2] = false;
                        }
                    }
                } else if (move.isRevivingMove()) {
                    board.boxes[move.getStart().getX()][move.getStart().getY()].setPiece(move.getPieceMoved());
                }
            }

            // set the current turn to the other player
            if (this.currentTurn == players[0]) {
                this.currentTurn = players[1];
            } else {
                this.currentTurn = players[0];
            }
            forwardMovesPlayed.push(move);
            return move;
        }
        return null;
    }


    public Move redoMove() {
        if (!forwardMovesPlayed.isEmpty()) {
            Move move = forwardMovesPlayed.pop();
            move.getStart().setPiece(null);
            if (move.isRevivingMove()) {
                move.getEnd().setPiece(move.getRevivingPiece());

            } else {
                move.getEnd().setPiece(move.getPieceMoved());

            }

            if (move.isCastlingMove()) {
                String castlingRook = move.getBeforeMovingPosition().get(1);
                Spot rookSpot = board.boxes[Integer.parseInt(String.valueOf(castlingRook.charAt(0)))][Integer.parseInt(String.valueOf(castlingRook.charAt(1)))];
                Piece rookPiece = rookSpot.getPiece();
                rookSpot.setPiece(null);
                castlingRook = move.getAfterMovingPosition().get(1);
                board.boxes[Integer.parseInt(String.valueOf(castlingRook.charAt(0)))][Integer.parseInt(String.valueOf(castlingRook.charAt(1)))].setPiece(rookPiece);

                boolean isRookLeftSide = Integer.parseInt(String.valueOf(castlingRook.charAt(1))) == 0;
                if (move.getPieceMoved().isWhite()) {
                    whiteCastling[0] = true;
                    whiteKing = "" + move.getEnd().getX() + move.getEnd().getY();
                    if (isRookLeftSide) {
                        whiteCastling[1] = true;
                    } else {
                        whiteCastling[2] = true;
                    }
                } else {
                    blackCastling[0] = true;
                    blackKing = "" + move.getEnd().getX() + move.getEnd().getY();
                    if (isRookLeftSide) {
                        blackCastling[1] = true;
                    } else {
                        blackCastling[2] = true;
                    }
                }
            }
            // set the current turn to the other player
            if (this.currentTurn == players[0]) {
                this.currentTurn = players[1];
            } else {
                this.currentTurn = players[0];
            }

            movesPlayed.push(move);
            return move;
        }
        return null;
    }

    public void setCurrentPlayerResign() {
        byStatus = ByStatus.RESIGNATION;
        // is current turn is white piece
        if (currentTurn.isWhiteSide()) {
            // is current turn is computer
            if (currentTurn.isComputer()) {
                // white wins since  computer is white
                gameStatus = GameStatus.WHITE_WINS;
            } else {
                // black wins
                gameStatus = GameStatus.BLACK_WINS;
            }

        } else {
            // is current turn is computer
            if (currentTurn.isComputer()) {
                // black wins since  computer is black
                gameStatus = GameStatus.BLACK_WINS;
            } else {
                // white wins
                gameStatus = GameStatus.WHITE_WINS;
            }

        }


    }

    public void setPlayerResign(boolean isPlayerWhite) {
        byStatus = ByStatus.RESIGNATION;

        // is resign player is white
        if (isPlayerWhite) {
            gameStatus = GameStatus.BLACK_WINS;
        } else {
            // black wins
            gameStatus = GameStatus.WHITE_WINS;
        }

    }


    private boolean isCheckmate() {
        List<Object> currentTurnKingAndSpot = getCurrentTurnKingAndSpot();
        Spot kingSpot = (Spot) currentTurnKingAndSpot.get(1);
        // is current turn king being attack by opponent king
        Spot attackingSpot = isCurrentTurnKingBeingAttack(currentTurnKingAndSpot.get(0).toString());
        if (attackingSpot != null) {
            isCheck = true;
            // check is king can kill the piece
            if (!isKingCanKillThePiece(attackingSpot, kingSpot)) {
                // check is king can't move in any location
                if (isKingCanMove(kingSpot, "" + attackingSpot.getX() + attackingSpot.getY())) {
                    // find Protection move for the king
                    List<String> kingProtectionMove = getKingProtectionMove(attackingSpot, kingSpot);
                    // check is other piece can protect king
                    return !isPieceCanProtectKing(kingProtectionMove);


                }
            }

        } else {
            isCheck = false;
        }

        return false;
    }

    private boolean isStalemate() {
        if (gameStatus == GameStatus.ACTIVE) {
            Spot kingSpot = (Spot) getCurrentTurnKingAndSpot().get(1);
            // check is king can move to any location
            if (isKingCanMove(kingSpot, null)) {
                return !isCurrentTurnPieceCanMove();
            }
        }

        return false;
    }

    private List<Object> getCurrentTurnKingAndSpot() {
        List<Object> objectList = new ArrayList<>();
        String king = currentTurn.isWhiteSide() ? whiteKing : blackKing;
        int x = Integer.parseInt(Character.toString(king.charAt(0)));
        int y = Integer.parseInt(Character.toString(king.charAt(1)));
        objectList.add(king);
        objectList.add(board.boxes[x][y]);

        return objectList;

    }

    private boolean isRepetitionDraw() {
        boolean isRepetitionDraw = false;
        int size = movesPlayed.size();

        if (size > 9) {
            Move move1, move2, opponentMove1, opponentMove2;
            move1 = movesPlayed.get(size - 1);
            opponentMove1 = movesPlayed.get(size - 2);
            int moveIndex = size - 3;
            for (int i = 0; i < 2; i++) {
                move2 = movesPlayed.get(moveIndex);
                if (move1.getEnd().getX() == move2.getStart().getX() && move1.getEnd().getY() == move2.getStart().getY() &&
                        move1.getPieceMoved().equals(move2.getPieceMoved())) {
                    opponentMove2 = movesPlayed.get(moveIndex - 1);
                    if (opponentMove1.getEnd().getX() == opponentMove2.getStart().getX() &&
                            opponentMove1.getEnd().getY() == opponentMove2.getStart().getY() &&
                            opponentMove1.getPieceMoved().equals(opponentMove2.getPieceMoved())) {
                        isRepetitionDraw = true;
                        moveIndex = (moveIndex == 3) ? 0 : (moveIndex - 4);
                    }


                } else {
                    return false;
                }
            }


        }

        return isRepetitionDraw;
    }

    // check  is any piece is available for winning a game by any opponent
    private boolean inSufficientMaterialDraw() {
        boolean[] isKnight = {false, false};
        boolean[] isBishop = {false, false};
        boolean isBishopBlack = false;
        boolean[][] cellColor = new boolean[32][32];
        boolean isCellColorBlack = false;
        // store each game board box color
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // set game board color
                cellColor[i][j] = isCellColorBlack;
                // is not last box
                if (j != 7) {
                    // is black color then  set light color
                    // else set black color
                    isCellColorBlack = !isCellColorBlack;
                }

            }
        }
        //0 white
        //1 black
        // traversing in each box of a game board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.boxes[i][j].getPiece();
                // is piece is null
                if (piece != null) {
                    // is piece is Pawn,Rook or Queen
                    if (piece instanceof Pawn || piece instanceof Rook || piece instanceof Queen) {
                        // game can't be draw so return false
                        return false;
                    }
                    // is piece is Knight
                    else if (piece instanceof Knight) {
                        // is Bishop or Knight  is already there in the game board
                        if (isKnight[0] || isKnight[1] || isBishop[0] || isBishop[1]) {
                            // game can't be draw so return false
                            return false;
                        }
                        // is piece is white side
                        else if (piece.isWhite()) {
                            // set white knight is there in the game board
                            isKnight[0] = true;
                        }
                        // piece is white side
                        else {
                            // set black knight is there in the game board{
                            isKnight[1] = true;
                        }

                    }
                    // is piece is Bishop
                    else if (piece instanceof Bishop) {
                        // is Knight  is already there in the game board
                        if (isKnight[0] || isKnight[1]) {
                            return false;
                        }

                        // is piece is white side
                        else if (piece.isWhite()) {
                            if (isBishop[0]) {
                                return false;
                            } else if (isBishop[1]) {
                                return isBishopBlack == cellColor[i][j];
                            }
                            // set white Bishop is there in the game board
                            isBishop[0] = true;
                            isBishopBlack = cellColor[i][j];

                        }
                        // piece is white side
                        else {
                            if (isBishop[1]) {
                                return false;
                            } else if (isBishop[0]) {
                                return isBishopBlack == cellColor[i][j];
                            }
                            // set black Bishop is there in the game board
                            isBishop[1] = true;
                            isBishopBlack = cellColor[i][j];
                        }
                    }

                }
            }
        }
        return true;
    }

    // check is any piece killed or pawn move in last 50 moves
    // if not then match will draw
    private boolean isFiftyMoveDraw() {
        int size = movesPlayed.size();
        // is players play move more than 49 moves
        if (size > 49) {
            // traversing through last 50 moves
            for (int i = 0, index = size - 1; i < 50; i++, index--) {

                Move move = movesPlayed.get(index);
                // is any piece killed in the move or pawn move
                if (move.getPieceKilled() == null || move.getPieceMoved() instanceof Pawn) {
                    // game can't be daw so return false
                    return false;
                }

            }
            // no piece is killed or pawn move on last 50 moves
            return true;
        }

        return false;
    }

    public void setCurrentTurnPlayerLostByTimer() {
        byStatus = ByStatus.Timeout;
        if (currentTurn.isWhiteSide()) {
            gameStatus = GameStatus.BLACK_WINS;
        } else {
            gameStatus = GameStatus.WHITE_WINS;
        }
    }


    public List<Piece> getAllKilledPiece() {
        List<Piece> pieces = new ArrayList<>();
        for (Move move : movesPlayed) {
            Piece pieceKilled = move.getPieceKilled();
            if (pieceKilled != null) {
                pieces.add(pieceKilled);
            }
        }
        return pieces;
    }


    public boolean isCheck() {
        return isCheck;
    }


}




