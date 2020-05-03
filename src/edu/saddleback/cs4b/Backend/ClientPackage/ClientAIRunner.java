package edu.saddleback.cs4b.Backend.ClientPackage;

import edu.saddleback.cs4b.Backend.Messages.*;
import edu.saddleback.cs4b.Backend.Objects.Coordinate;
import edu.saddleback.cs4b.Backend.Objects.TTTToken;
import edu.saddleback.cs4b.Backend.Objects.Token;
import edu.saddleback.cs4b.Backend.PubSub.MessageEvent;
import edu.saddleback.cs4b.Backend.PubSub.Observer;
import edu.saddleback.cs4b.Backend.PubSub.SystemEvent;
import edu.saddleback.cs4b.Backend.Utilitys.TTTUser;
import edu.saddleback.cs4b.Backend.Utilitys.User;
import edu.saddleback.cs4b.UI.UIEventLog;
import edu.saddleback.cs4b.UI.Util.GameManager;

/**
 * This class will be used to run the AI for users who wish to play a
 * single player game. It will sign into the system using its own
 * universal login
 */
public final class ClientAIRunner implements Observer, Runnable {
    private static int MAX_MOVES = 9;
    private volatile static ClientAIRunner aiRunner = null;
    private User aiAcct = new TTTUser("HardAI", "AI", "Hard", "Homi Bodhanwala");
    private GameManager gameManager;
    private Token[][] board; // todo -- will we want to use our interface?
    private UIEventLog eventLog;
    private HardAI ai;
    private Token userToken;

    // state that controls the game flow
    private volatile int movesMade;
    private volatile boolean isActive;
    private volatile int gameId;
    private volatile boolean gameFound;
    private volatile Coordinate cachedCoordinate;

    private void initRunningState() {
        this.isActive = false;
        this.gameId = -1;
        this.gameFound = false;
        this.cachedCoordinate = null;
        this.movesMade = 0;
        this.board = new Token[3][3];
    }

    private ClientAIRunner() {

        // listen for the messages to come back
        ClientEventLog.getInstance().addObserver(this);
        GameManager.getInstance().addObserver(this);
        this.gameManager = GameManager.getInstance();
        this.eventLog = UIEventLog.getInstance();
        this.userToken = new TTTToken("1");
        this.ai = new HardAI(userToken);
        initRunningState();
    }

    public static ClientAIRunner getInstance() {
        if (aiRunner == null) {
            synchronized (ClientAIRunner.class) {
                if (aiRunner == null) {
                    aiRunner = new ClientAIRunner();
                }
            }
        }
        return aiRunner;
    }

    @Override
    public void update(SystemEvent e) {
        //todo make sure that we have message for game id
        if (e instanceof MessageEvent) {
            BaseMessage bm = ((MessageEvent) e).getMessage();
            if (bm instanceof AvailableGameMessage) {
                // todo -- verify the message is for this game
                gameFound = true;
                System.out.println("found");
            } else if (bm instanceof GameResultMessage) {
                // todo -- verify its the ai game and not something else
               initRunningState();
            } else if (bm instanceof ValidMoveMessage) {
                // todo -- verify that the move is for this game
                System.out.println("move");
                if (!((ValidMoveMessage) bm).getUser().equals(aiAcct.getUsername())) {
                    cachedCoordinate = ((ValidMoveMessage) bm).getCoordinate();
                }
            }
        }
    }

    @Override
    public void run() {
        // send the sign-in message
        Coordinate aiPos = null;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SignInMessage signIn = new SignInMessage(aiAcct);
        UIEventLog.getInstance().notifyObservers(new MessageEvent(signIn));

        while (true) {
            //listenForGameId();
            // send a join game message

            JoinGameRequestMessage joinMsg = new JoinGameRequestMessage();
            //joinMsg.setGameID(Integer.toString(gameId));
            joinMsg.setGameID("1");
            eventLog.notifyObservers(new MessageEvent(joinMsg));

            listenForStart();
            isActive = true;
            while (isActive) {
                listenForMove();

                // set the values of the board equal to the token
                board[cachedCoordinate.getXCoord()][cachedCoordinate.getYCoord()] = userToken;
                movesMade++;

                // reset after placement to listen for the next one
                cachedCoordinate = null;

                if (movesMade >= 9) {
                    break;
                }

                // call minimax on the newBoard
                aiPos = ai.getPlay(board, MAX_MOVES - movesMade);
                System.out.println(aiPos);
                board[aiPos.getXCoord()][aiPos.getYCoord()] = ai.getAiToken();
                movesMade++;

                MoveMessage moveMsg = new MoveMessage();
                moveMsg.setGameId("1");
                moveMsg.setCoordinate(aiPos);
                //moveMsg.setCoordinate(pos);
                eventLog.notifyObservers(new MessageEvent(moveMsg));
            }
        }
    }

    private void listenForGameId() {
        while(gameId == -1) {};
    }

    private void listenForStart() {
        while (!gameFound) {}
    }

    private void listenForMove() {
        while(cachedCoordinate == null) {};
    }
}
