package br.com.infnet.mslogistics.exception;

public class InvalidTeamException extends RuntimeException {

    public InvalidTeamException(String teamId) {
        super("teamId is not a valid FIFA selection registered in ms-core-data: " + teamId);
    }
}
