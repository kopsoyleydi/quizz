package ru.frank.exceptions;

import java.io.Serial;

public class UserScoreListIsEmptyException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -5126598134158106651L;

    public UserScoreListIsEmptyException(String message){
        super(message);
    }

}
