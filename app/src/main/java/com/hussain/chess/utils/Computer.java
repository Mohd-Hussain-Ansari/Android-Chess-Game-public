package com.hussain.chess.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hussain.chess.Model.GameStatus;
import com.hussain.chess.Model.Move;
import com.hussain.chess.Model.Piece;
import com.hussain.chess.Model.Spot;
import com.hussain.chess.database.AppDatabase;
import com.hussain.chess.database.TempSavedComputerGame;
import com.hussain.chess.database.TempSavedComputerGameDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Computer extends Game {

    private boolean isUserBlack;

    long timerLimit;
    long blackTimer;
    long whiteTimer;
    boolean isShowAvailableMove;
    boolean isShowLastMove;
    boolean isSound;
    boolean isUndoRedo;

    boolean isChangeSettings;


    public boolean isChangeSettings() {
        return isChangeSettings;
    }

    public long getTimerLimit() {
        return timerLimit;
    }


    public long getBlackTimer() {
        return blackTimer;
    }


    public long getWhiteTimer() {
        return whiteTimer;
    }

    public boolean isShowAvailableMove() {
        return isShowAvailableMove;
    }

    public boolean isShowLastMove() {
        return isShowLastMove;
    }

    public boolean isSound() {
        return isSound;
    }

    public boolean isUndoRedo() {
        return isUndoRedo;
    }

    public void setUserBlack(boolean userBlack) {
        isUserBlack = userBlack;
    }


    public boolean isUserBlack() {
        return isUserBlack;
    }

    protected Move getBestProtectionMove(List<String> kingProtectionMove, boolean pieceColor) throws Exception {
        Move bestMove = null;
        List<String> availableMove;
        // traversing through each spot
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // get spot
                Spot spot = board.boxes[i][j];
                // get current piece of that spot
                Piece piece = spot.getPiece();
                // is current piece is computer's piece and is not a king
                if (piece != null && piece.isWhite() == pieceColor && !(piece instanceof King)) {
                    // get available move of current piece
                    availableMove = piece.ValidAvailableMove(board, spot);
                    // get protection move of current piece
                    availableMove.retainAll(kingProtectionMove);

                    // is current piece can protect the king
                    if (availableMove.size() == 1) {
                        // get protection string position
                        String protectionPosition = availableMove.get(0);
                        // get protection spot
                        Spot protectionSpot = board.getBox(Integer.parseInt(String.valueOf(protectionPosition.charAt(0))), Integer.parseInt(String.valueOf(protectionPosition.charAt(1))));
                        //Spot protectionSpot=new Spot(Integer.parseInt(String.valueOf(protectionPosition.charAt(0))),   Integer.parseInt(String.valueOf(protectionPosition.charAt(1))),piece);
                        // is best move is null
                        if (bestMove == null) {
                            // add move to the best move
                            bestMove = new Move(spot, protectionSpot);
                        }
                        //  best move is not null
                        else {
                            // get minimum lost piece
                            bestMove = minPointMove(bestMove, new Move(spot, protectionSpot));
                        }

                    }
                }
            }
        }


        return bestMove;
    }


    //  logic can be added :  is any opponent  piece will going to kill both available piece after moving
    //  logic can be added :  give priority to  not  kill the piece using king (for Castling)
    //  logic added :  is any opponent  piece will going to kill the piece after moving
    // give minimum lost between two move
    private Move minPointMove(Move move1, Move move2) {

        boolean isMove1PieceKill = false;
        boolean isMove2PieceKill = false;

        String move1EndLocation = "" + move1.getEnd().getX() + move1.getEnd().getY();
        String move2EndLocation = "" + move2.getEnd().getX() + move2.getEnd().getY();

        // traversing through each spot
        traversingLoop:
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // get spot
                Spot spot = board.boxes[i][j];
                // get piece from that spot
                Piece piece = spot.getPiece();
                // is piece is there and is a opponent piece
                if (piece != null && piece.isWhite() != move1.getPieceMoved().isWhite()) {
                    // is move1 piece is not kill
                    if (!isMove1PieceKill) {
                        // is piece available move contain end move1 piece
                        isMove1PieceKill = piece.ValidAvailableMove(board, spot).contains(move1EndLocation);
                    }
                    // is move2 piece is not kill
                    if (!isMove2PieceKill) {
                        // is piece available move contain end move2 piece
                        isMove2PieceKill = piece.ValidAvailableMove(board, spot).contains(move2EndLocation);
                    }
                    // is both move piece is  kill
                    if (isMove1PieceKill && isMove2PieceKill) {
                        break traversingLoop;
                    }
                }
            }
        }

        // is both move piece is not kill
        if (!isMove1PieceKill && !isMove2PieceKill) {

            // is move1 piece is pawn
            if (move1.getPieceMoved() instanceof Pawn) {
                return move1;
            }
            // is move2 piece is pawn
            else if (move2.getPieceMoved() instanceof Pawn) {
                return move2;
            }

            // is move1 piece is Knight
            else if (move1.getPieceMoved() instanceof Knight) {
                return move1;
            }
            // is move2 piece is Knight
            else if (move2.getPieceMoved() instanceof Knight) {
                return move2;
            }
            // is move1 piece is Bishop
            else if (move1.getPieceMoved() instanceof Bishop) {
                return move1;
            }
            // is move2 piece is Bishop
            else if (move2.getPieceMoved() instanceof Bishop) {
                return move2;
            }
            // is move1 piece is Rook
            else if (move1.getPieceMoved() instanceof Rook) {
                return move1;
            }
            // is move2 piece is Rook
            else if (move2.getPieceMoved() instanceof Rook) {
                return move2;
            }

            // is move1 piece is Queen
            else if (move1.getPieceMoved() instanceof Queen) {
                return move1;
            }
            // is move2 piece is Queen
            else if (move2.getPieceMoved() instanceof Queen) {
                return move2;
            }
        } else {
            //  // is move1 piece is  kill
            if (isMove1PieceKill) {
                // move2 is better
                return move2;
            }
            //move2 piece is  kill
            else {
                // move1 is better
                return move1;
            }
        }


        return move1;
    }

    public Move computerMove() throws Exception {

        // is current turn is computer
        if (getCurrentTurn().isComputer()) {
            Spot attackingPieceSpot;
            Spot kingSpot;
            // is user is black
            if (isUserBlack) {
                // get attacking spot when king is attacked by someone
                attackingPieceSpot = isCurrentTurnKingBeingAttack(whiteKing);
                kingSpot = board.getBox(Integer.parseInt(String.valueOf(whiteKing.charAt(0))), Integer.parseInt(String.valueOf(whiteKing.charAt(1))));

                //kingSpot=new Spot(Integer.parseInt(String.valueOf(whiteKing.charAt(0))),Integer.parseInt(String.valueOf(whiteKing.charAt(1))),new King(true));

            } else {
                // get attacking spot when king is attacked by someone
                attackingPieceSpot = isCurrentTurnKingBeingAttack(blackKing);
                kingSpot = board.getBox(Integer.parseInt(String.valueOf(blackKing.charAt(0))), Integer.parseInt(String.valueOf(blackKing.charAt(1))));
                //kingSpot=new Spot(Integer.parseInt(String.valueOf(blackKing.charAt(0))),Integer.parseInt(String.valueOf(blackKing.charAt(1))),new King(false));

            }
            // is computer king being attack
            if (attackingPieceSpot != null) {
                // is king can kill the piece
                if (isKingCanKillThePiece(attackingPieceSpot, kingSpot)) {
                    Move move = new Move(kingSpot, attackingPieceSpot);
                    //move.setPieceKilled(attackingPieceSpot.getPiece());


                    // is computer can make that move
                    if (makeMove(move, getCurrentTurn())) {
                        // return king killing move
                        return move;
                    } else {
                        return computerMove();
                    }


                }
                // king can"t kill the piece
                else {
                    // get King protection move
                    List<String> kingProtectionMove = getKingProtectionMove(attackingPieceSpot, kingSpot);
                    // get best king protection move
                    Move move = getBestProtectionMove(kingProtectionMove, getCurrentTurn().isWhiteSide());

                    // is move is null
                    if (move == null) {
                        // get king available move
                        List<String> availableMove = kingSpot.getPiece().ValidAvailableMove(board, kingSpot);

                        boolean isKingCanMove;
                        // traversing through each king move
                        for (String kingMove : availableMove) {
                            // get king move spot
                            Spot kingMoveSpot = board.getBox(Integer.parseInt(String.valueOf(kingMove.charAt(0))), Integer.parseInt(String.valueOf(kingMove.charAt(1))));
                            // make copy of tha king spot
                            Spot kingSpotCopy = new Spot(kingSpot.getX(), kingSpot.getY(), kingSpot.getPiece());
                            // move king to that location
                            kingSpot.setPiece(null);
                            kingMoveSpot.setPiece(kingSpotCopy.getPiece());

                            // check is king not being attack in its new location and store into variable
                            isKingCanMove = isCurrentTurnKingBeingAttack(kingMove) == null;

                            // rollback king to its previous location
                            kingMoveSpot.setPiece(null);
                            kingSpot.setPiece(kingSpotCopy.getPiece());
                            // is king can move to new location
                            if (isKingCanMove) {
                                // set that move
                                move = new Move(kingSpot, kingMoveSpot);
                                break;
                            }

                        }
                    }

                    // is computer can make that move
                    assert move != null;
                    if (makeMove(move, getCurrentTurn())) {
                        // return piece killing move
                        return move;
                    }
                    // stack overflow exception
                    else {
                        return computerMove();
                    }


                }
            }
            // computer king not being attack
            else {

                // get best kill piece move
                Move bestKillMove = getBestKillMove(getCurrentTurn().isWhiteSide());
                // is best kill move is null
                if (bestKillMove == null) {
                    Move move = randomMove(getCurrentTurn().isWhiteSide());

                    // is computer can make that move
                    if (makeMove(move, getCurrentTurn())) {
                        // return piece killing move
                        return move;
                    } else {
                        return computerMove();
                    }
                }
                //best kill move is not null
                else {
                    //bestKillMove.setPieceKilled(bestKillMove.getEnd().getPiece());


                    // is computer can make that move
                    if (makeMove(bestKillMove, getCurrentTurn())) {
                        // return piece killing move
                        return bestKillMove;
                    } else {
                        return computerMove();
                    }


                }


            }
        } else {
            return null;
        }


    }

    // logic left is after moving king being attack
    private Move randomMove(boolean colorPiece) throws Exception {
        List<Move> moves = new ArrayList<>();
        // traversing through each spot
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // get spot
                Spot spot = board.boxes[i][j];
                // get piece at that spot
                Piece piece = spot.getPiece();
                // is current spot is not empty and current spot has a computer piece
                if (piece != null && piece.isWhite() == colorPiece) {
                    // traversing through each available move
                    for (String movePosition : piece.ValidAvailableMove(board, spot)) {
                        // create move spot from the move position
                        Spot moveSpot = board.getBox(Integer.parseInt(String.valueOf(movePosition.charAt(0))), Integer.parseInt(String.valueOf(movePosition.charAt(1))));


                        // create move and add in the list
                        moves.add(new Move(spot, moveSpot));

                    }
                }
            }
        }


        // logic: is piece will kill after moving
        while (true) {
            // get random move from the moves list
            Move move = moves.get(new Random().nextInt(moves.size()));

            boolean isKill = false;

            // traversing through each spot
            traversingLoop:
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    // get spot
                    Spot spot1 = board.boxes[x][y];
                    // get piece from that spot
                    Piece piece1 = spot1.getPiece();
                    // is piece is there and is an opponent piece
                    if (piece1 != null && piece1.isWhite() != move.getPieceMoved().isWhite()) {
                        isKill = true;
                        break traversingLoop;
                    }
                }
            }
            // is piece will kill
            if (isKill) {
                // check whether it is only piece which can move
                // traversing through each loop

                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        // get spot
                        Spot spot1 = board.boxes[k][l];
                        // get piece
                        Piece piece1 = spot1.getPiece();
                        // is piece is computer piece
                        if (piece1 != null && piece1.isWhite() == move.getPieceMoved().isWhite()) {

                            // return  move
                            return move;

                        }
                    }
                }

            }
            // piece will not kill
            else {
                // return  move
                return move;
            }
        }


    }

    //  logic can be added :  is any opponent  piece will going to kill the piece after moving
    // logic can be added : only check last move position of each piece except knight
    // logic left is king being attack after move
    private Move getBestKillMove(boolean pieceColor) throws Exception {
        Move bestMove = null;
        List<String> availableMove;
        Move move;
        // traversing to each spots
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // get spot
                Spot spot = board.boxes[i][j];
                // get piece of that spot
                Piece piece = spot.getPiece();
                // is current piece is a computer's piece
                if (piece != null && piece.isWhite() == pieceColor) {


                    // get available move of a piece
                    availableMove = piece.ValidAvailableMove(board, spot);


                    // traversing through each available Move
                    for (String movePosition : availableMove) {
                        // get spot of that move
                        Spot moveSpot = board.getBox(Integer.parseInt(String.valueOf(movePosition.charAt(0))), Integer.parseInt(String.valueOf(movePosition.charAt(1))));
                        // is spot have a opponent piece
                        if (moveSpot.getPiece() != null) {


                            // set move for that spot
                            move = new Move(spot, moveSpot);

                            // is piece is a king
                            if (piece instanceof King) {
                                // is king not getting check after killing the piece
                                if (isKingCanKillThePiece(moveSpot, spot)) {
                                    // is best move is null
                                    if (bestMove == null) {
                                        // set best move to current move
                                        bestMove = move;
                                    } else {
                                        // get minimum lost piece move
                                        bestMove = minPointMove(bestMove, move);
                                    }
                                }

                            }
                            //  piece is not a king
                            else {
                                // is best move is a null
                                if (bestMove == null) {
                                    // set best move to a current piece move
                                    bestMove = move;
                                } else {
                                    // set best move to a minimum lost piece move
                                    bestMove = minPointMove(bestMove, move);
                                }
                            }


                        }

                    }


                }
            }
        }
        return bestMove;
    }


    public boolean isUserCanSaveGame() {
        // if user made minimum 3 move
        return gameStatus == GameStatus.ACTIVE && movesPlayed.size() > 2;
    }


    public void tempSavedComputerGame(Context context, long timerLimit, long blackTimer, long whiteTimer,
                                      boolean isShowAvailableMove, boolean isShowLastMove, boolean isSound, boolean isUndoRedo,
                                      boolean isChangeSettings, boolean isUserBlack) {

        this.timerLimit = timerLimit;
        this.blackTimer = blackTimer;
        this.whiteTimer = whiteTimer;
        this.isShowAvailableMove = isShowAvailableMove;
        this.isShowLastMove = isShowLastMove;
        this.isSound = isSound;
        this.isUndoRedo = isUndoRedo;
        this.isChangeSettings = isChangeSettings;
        this.isUserBlack = isUserBlack;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Piece.class, new InterfaceAdapter<Piece>());
        Gson gson = gsonBuilder.create();


        AppDatabase db = AppDatabase.getDbInstance(context);

        TempSavedComputerGameDao tempSavedComputerGameDao = db.tempSavedComputerGameDao();
        TempSavedComputerGame tempSavedComputerGame = new TempSavedComputerGame();
        // removing back (undo) move of a game
        //movesPlayed.clear();
        // removing forward (redo) move of a game
        forwardMovesPlayed.clear();
        tempSavedComputerGame.id = 1;
        tempSavedComputerGame.gameObject = gson.toJson(this);

        tempSavedComputerGameDao.insertAll(tempSavedComputerGame);

    }


}



