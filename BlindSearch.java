import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Blind search randomly chooses a move until it finds the object and then home.
 * @author Andrew C. Haynes
 */
public class BlindSearch extends Thread {
    
    Agent agent;
    Random randSearch;
    
    /**
     * Blind Search Algorithm constructor
     * @param agent 
     */
    public BlindSearch(Agent agent) {
        this.agent = agent;
    }
    
    /**
     * Non-informed search or blind search. 
     */
    public void blindSearch() {
        this.start();
    }
    
    /**
     * Starts a thread that runs the blind (random) search.
     */
    public void run() {
        boolean search = false;
        while(!search) {
            try {
                setThreadSpeed();
                //check your block
                //if not destroyed, move!
                agent.checkBlock();
                clearCurrentSpot();
                if(agent.destroyed == 1){
                    //pop up saying agent died
                    JOptionPane.showMessageDialog(null, "ROBOT died.\n\nBlind Search Results:" + "\nAllowed Lifetime Steps: " + (agent.lifetime) + agent.performance());
                    search = true;
                }
                //total limit is length * width of the grid times ten! if robot still is unsucessful, mission failed
                else if(agent.totalSteps == ((agent.lifetime))) {
                    //pop up saying agent is taking too long to find object goal and return
                    JOptionPane.showMessageDialog(null, "ROBOT took too long to find goal object and return home.\n\nBlind Search Results: \nThe Agent failed.\n"
                        + "Allowed Lifetime Steps: " + ((agent.lifetime))
                        + agent.performance());
                    search = true;
                }
                //back at the starting block
                else if(agent.posX == agent.startX && agent.posY == agent.startY) {
                    //The object is found, end search!
                    if(agent.hasObject == 1) {
                        JOptionPane.showMessageDialog(null, "ROBOT retrieved object and went back home.\n\nBlind Search Results:" 
                            + "\nAllowed Lifetime Steps: " + (agent.lifetime) + agent.performance());
                        search = true;
                    }
                    else {
                        move();
                    }
                }
                else {
                    move();
                }
            }
            catch(Exception e) {
                
            }
        }
    }
    
    /**
     * States:
     * 0 -> Left
     * 1 -> Right
     * 2 -> Up
     * 3 -> Down
     * 
     * This function is called each time step after a block check determine fate of agent.
     * A random number generator will determine a number 0 - 3 to decide which move
     * the agent takes. previousState is here only to make sure the agent does not
     * get stuck in an obstacle. 
     * @return 
     */
    public void move() {
        randSearch = new Random();
        int move = randSearch.nextInt(((4))+0); //generate a number between 0 - 3
        //LEFT
        if(move == 0) {
            //check if boundary exist, 
            //if not, move left and record total steps
            if((agent.posY-1) >= 0) {
                agent.previousState = 0;
                agent.posY--;
                agent.totalSteps++;
                agent.board[agent.posX][agent.posY].iAmHere = true;
                agent.holdObjectMovement();
            }
            //If boundary exist, increase total steps
            else {
                agent.totalSteps++;
                agent.board[agent.posX][agent.posY].iAmHere = true;
                agent.holdObjectMovement();
            }
        }
        //RIGHT
        else if(move == 1) {
            //check if boundary exist
            //if not, move right and record total steps
            if((agent.posY+1) < agent.width) {
                agent.previousState = 1;
                agent.posY++;
                agent.totalSteps++;
                agent.board[agent.posX][agent.posY].iAmHere = true;
                agent.holdObjectMovement();
            }
            //if boundary exist, increase total steps
            else {
                agent.totalSteps++;
                agent.board[agent.posX][agent.posY].iAmHere = true;
                agent.holdObjectMovement();
            }
        }
        //UP
        else if(move == 2) {
            //check if boundary exist
            //if not, move up and record total steps
            if((agent.posX-1) >= 0) {
                agent.previousState = 2;
                agent.posX--;
                agent.totalSteps++;
                agent.board[agent.posX][agent.posY].iAmHere = true;
                agent.holdObjectMovement();
            }
            //if boundary exist, increase total steps
            else {
                agent.totalSteps++;
                agent.board[agent.posX][agent.posY].iAmHere = true;
                agent.holdObjectMovement();
            }
        }
        //DOWN
        else if(move == 3) {
            //check if boundary exist
            //if not, move down and record total steps
            if((agent.posX+1) < agent.length) {
                agent.previousState = 3;
                agent.posX++;
                agent.totalSteps++;
                agent.board[agent.posX][agent.posY].iAmHere = true;
                agent.holdObjectMovement();
            }
            //if boundary exist, increase total steps
            else {
                agent.totalSteps++;
                agent.board[agent.posX][agent.posY].iAmHere = true;
                agent.holdObjectMovement();
            }
        }
    }
    
    /**
     * Clears the current block that the agent is stepping on
     * for animation purposes. 
     */
    public void clearCurrentSpot() {
        for(int i = 0; i < agent.length; i++) {
            for(int y = 0; y < agent.width; y++) {
                if(agent.board[i][y].iAmHere) {
                    agent.board[i][y].iAmHere = false;
                    agent.board[i][y].repaint();
                }
            }
        }
    }
    
    /**
     * Sets the speed of the robot agent going through 
     * the environment. 
     * @throws Exception Thread interruption in sleep 
     */
    public void setThreadSpeed() throws Exception {
        if(agent.length < 10 || agent.width < 10) {
            Thread.sleep(200);
        }
        else if(agent.length >= 10 && agent.length < 15 
                || agent.width >= 10 && agent.width < 15) {
            Thread.sleep(100);
        }
        else {
            Thread.sleep(50);
        }
    }
}
