package application.chess;

import application.boardgame.BoardException;

public class ChessException extends BoardException{

    public ChessException(String msg) {
        super(msg);
    }
}
