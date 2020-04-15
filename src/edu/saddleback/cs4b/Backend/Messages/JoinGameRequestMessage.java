package edu.saddleback.cs4b.Backend.Messages;

import javax.print.DocFlavor;

public class JoinGameRequestMessage extends BaseMessage
{
    private String gameID;

    public JoinGameRequestMessage()
    {
        this(null);
    }

    public JoinGameRequestMessage(String newGameID)
    {
        super(MsgTypes.JOIN_GAME_REQUEST);

        gameID = newGameID;
    }

    public String getGameID()
    {
        return gameID;
    }
}
