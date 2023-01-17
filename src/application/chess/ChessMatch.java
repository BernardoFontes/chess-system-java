package application.chess;

import application.UI;
import application.boardgame.Board;
import application.boardgame.BoardException;
import application.boardgame.Piece;
import application.boardgame.Position;
import application.chess.pieces.King;
import application.chess.pieces.Pawn;
import application.chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ChessMatch {
    private Board board;
    private int turn;
    private boolean check;
    private boolean checkmat;
    private Color currentPlayer;
    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public int getTurn() {
        return turn;
    }

    public boolean isCheck() {
        return check;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    private void nextTurn() {
        turn++;
        currentPlayer = currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    public boolean isCheckmat() {
        return checkmat;
    }

    private Color opponent(Color color) {
        if (currentPlayer == Color.BLACK){
            color = Color.WHITE;
        } else {
            color = Color.BLACK;
        }
        return color;
    }

    private ChessPiece king(Color color) {
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list){
            if (p instanceof King){
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There's no" + color + "king on the board");
    }

    private boolean testCheck(Color color) {
        ChessPosition kingPosition = king(color).getChessPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() != color).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            if (p.possibleMove(kingPosition.toPosition())) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckmat(Color color) {
        if (!testCheck(color)) {
            return false;
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for (int i=0; i<board.getRows(); i++) {
                for (int j=0; j<board.getColumns(); j++) {
                    if (mat[i][j]) {
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!testCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public ChessMatch() {
        board = new Board(8,8);
        initialSetup();
        turn = 1;
        currentPlayer = Color.WHITE;
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i=0; i< board.getRows();i++){
            for(int j=0;j< board.getColumns();j++){
                mat[i][j] = (ChessPiece) board.piece((char) i,j);
            }
        }
        return mat;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }


    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);
        if (testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check!");
        }

        if (testCheck(opponent(currentPlayer))) {
            check = true;
        } else {
            check = false;
        }

        if (testCheckmat(opponent(currentPlayer))) {
            checkmat = true;

        } else {
            checkmat = false;
        }
        if(!checkmat){
            nextTurn();
        }
        return (ChessPiece)capturedPiece;
    };

    private Piece makeMove(Position source, Position target){
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);
        if(capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece){
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);

        if(capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
    }
    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    private void validateSourcePosition(Position position){
        if (!board.thereIsAPiece(position)){
            throw new ChessException("There is no piece on selected position");
        }
        if (currentPlayer != ((ChessPiece) board.piece(position)).getColor())
            throw new ChessException("You can't move opponent pieces!");
        if (!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessException("There is no possible moves for this piece");
        }
    }

    private void validateTargetPosition(Position source, Position target){
        if(!board.piece(source).possibleMove(target)){
            throw new ChessException("The chosen piece can't move to target position");
        }
    }

    private void initialSetup(){
        placeNewPiece('h', 7, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 8, new Rook(board, Color.BLACK));
        placeNewPiece('e', 1, new King(board, Color.WHITE));
        placeNewPiece('a', 8, new King(board, Color.BLACK));
        placeNewPiece('e', 5, new Pawn(board, Color.WHITE));
        placeNewPiece('d', 4, new Pawn(board, Color.WHITE));
        placeNewPiece('f', 3, new Pawn(board, Color.WHITE));
        placeNewPiece('e', 3, new Pawn(board, Color.WHITE));
        placeNewPiece('d', 5, new Pawn(board, Color.BLACK));
        placeNewPiece('b', 7, new Rook(board, Color.BLACK));
    }
}
