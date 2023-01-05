package application.chess;

import application.boardgame.Position;

public class ChessPosition {
    private char column;
    private int row;

    public ChessPosition(char column, int row) {
        if(column < 'a' || column > 'h' || row < 1 || row > 8){
            throw new ChessException("Invalid values. Column must be from A to H and row from 1 to 8.");
        }
        this.column = column;
        this.row = row;
    }

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    protected Position toPosition(){
        return new Position(8 - row, column - 'a');
    }

    protected static ChessPosition fromPosition(Position position){
        return new ChessPosition((char) (position.getColumn()-'a'), position.getRow() - 8);
    }

    @Override
    public String toString() {
        return "" + column + row;

        // precisa da string vazia pra entender que é concatenacao de string
    }
}
