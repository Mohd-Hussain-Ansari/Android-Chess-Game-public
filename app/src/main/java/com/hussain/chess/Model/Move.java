package com.hussain.chess.Model;


import java.util.List;

public class Move {
    private Player player;
    private final Spot start;
    private final Spot end;
    private final Piece pieceMoved;


    private Piece pieceKilled;

    private Piece revivingPiece;

    private boolean castlingMove = false;
    private boolean enpersandMove = false;

    private boolean revivingMove = false;


    private List<String> afterMovingPosition;
    private List<String> beforeMovingPosition;

    // 0- king ,  pawn being attacked
    // 1 rook   ,  attacking pawn
    public Move(Player player, Spot start, Spot end) {
        this.player = player;
        this.start = start;
        this.end = end;
        this.pieceMoved = start.getPiece();
    }

    // computer test
    public Move(Spot start, Spot end) {
        this.start = start;
        this.end = end;
        this.pieceMoved = start.getPiece();
    }

    public boolean isCastlingMove() {
        return this.castlingMove;
    }

    public void setCastlingMove(boolean castlingMove) {
        this.castlingMove = castlingMove;
    }

    public Spot getStart() {
        return this.start;
    }

    public Spot getEnd() {
        return this.end;
    }

    public void setPieceKilled(Piece destPiece) {
        this.pieceKilled = destPiece;
    }

    public Piece getPieceKilled() {
        return pieceKilled;
    }

    public Piece getPieceMoved() {
        return pieceMoved;
    }

    public List<String> getAfterMovingPosition() {
        return afterMovingPosition;
    }

    public void setAfterMovingPosition(List<String> afterMovingPosition) {
        this.afterMovingPosition = afterMovingPosition;
    }


    public List<String> getBeforeMovingPosition() {
        return beforeMovingPosition;
    }

    public void setBeforeMovingPosition(List<String> beforeMovingPosition) {
        this.beforeMovingPosition = beforeMovingPosition;
    }

    public boolean isEnpersandMove() {
        return enpersandMove;
    }

    public void setEnpersandMove(boolean enpersandMove) {
        this.enpersandMove = enpersandMove;
    }


    public boolean isRevivingMove() {
        return revivingMove;
    }

    public void setRevivingMove(boolean revivingMove) {
        this.revivingMove = revivingMove;
    }

    public Piece getRevivingPiece() {
        return revivingPiece;
    }

    public void setRevivingPiece(Piece revivingPiece) {
        this.revivingPiece = revivingPiece;
    }
}
