package edu.saddleback.cs4b.Backend.Messages;

public enum MsgTypes {


    DEACTIVATION("Deactivation"),
    ACTIVE_USER("Active User"),
    ACTIVE_USER_RESPONSE("Active User Response"),




    ACTIVE_GAMES("Active_Game"),

    AUTHENTICATION("Authentication"),
    CREATE_GAME("Create Game"),

    DENIED("Denied"),
    DISCONNECTION("Disconnection"),
    GAME_HISTORY_REQUEST("Request Game History"),
    GAME_HISTORY_RESPONSE("Game History Response"),
    GAME_RESULT("Game Result"),
    INVALID_MOVE("Invalid Move"),
    JOIN_GAME_REQUEST("Request to Join Game"),
    MOVE("Move"),
    PROFILE("Profile"),
    REG_ERROR("Registration Error"),
    SIGN_IN("Sign-in"),
    SIGN_OUT("Sign-out"),
    SUCCESS_REG("Successful Registration"),
    VIEW_GAME_REQUEST("View Game Request");


    private String type;
    private MsgTypes(String type) {
        this.type = type;
    }

    public String getType() { return type; }
}
