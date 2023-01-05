package application.boardgame;

import application.chess.ChessMatch;
import application.chess.ChessPosition;

public class Board {
    private int rows;
    private int columns;
    private Piece[][] pieces;

    public Board(int rows, int columns) {
        if(rows < 1 && columns < 1) {
            throw new BoardException("Error creating board: There must be at least 1 row and 1 column");
        }
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];
    }

    @Override
    public String toString() {
        return
                 rows +
                ", " + columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Piece piece(int row, int column){
        if (!positionExists(new Position(row, column))){
            throw new BoardException("Invalid position");
        }
        return pieces[row][column];
    }

    public void placePiece(Piece piece, Position position){
        if (thereIsAPiece(position)) {
            throw new BoardException("There is a piece on position " + position);
        }
        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }

    public boolean positionExists(Position position){
        if (position.getColumn() >= 0 && position.getColumn() <= columns && position.getRow() >= 0 && position.getRow() <= rows){
            return true;
        } else {
            return false;
        }
    }

    public boolean thereIsAPiece(Position position){
        return piece(position) != null;
    }

    public Piece piece (Position position){
        return pieces[position.getRow()][position.getColumn()];
    }
}
