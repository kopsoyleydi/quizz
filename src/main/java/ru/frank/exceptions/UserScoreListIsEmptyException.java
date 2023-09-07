package ru.frank.exceptions;

import java.io.Serial;

/**
 * Created by sbt-filippov-vv on 08.02.2018.
 */
public class UserScoreListIsEmptyException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -5126598134158106651L;

    public UserScoreListIsEmptyException(String message){
        super(message);
    }

}
