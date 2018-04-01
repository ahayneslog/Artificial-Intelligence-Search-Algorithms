import java.util.LinkedList;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Min-Conflictis a CSP problem. The constraints are
 * no obstacles and agents in the shortest-path found to the treasure
 * and back to the starting point. 
 * @author Andrew C. Haynes
 */
public class MinConflict extends Thread {
    
    Agent agent;
    Block root;
    LinkedList<Block> neighbors;
    //for space, the max number of blocks is 4
    int numOfBlocks;
    
    /**
     * A Constraint-Satisfaction Problem Algorithm that searches the grid by
     * following the constraint of never stepping onto an Obstacle and an Agent. 
     * The Goal is to get to the goal block and back. The movement is dictated by
     * a random number generator selecting a block in the neighbor queue. The Neighbor
     * queue does not include visited blocks, agents or obstacles. 
     * @param agent 
     */
    public MinConflict(Agent agent) {
        this.agent = agent;
        neighbors = new LinkedList<>();
        numOfBlocks = 0;
    }
    
    /**
     * Performs a Min-Conflict Search.
     */
    public void minConflictSearch() {
        findRoot();
        this.start();
    }
    
    /**
     * Starts the thread that runs the Min-Conflict Search. 
     */
    public void run() { 
        Block currentBlock = root;
        agent.posX = root.xPos;
        agent.posY = root.yPos;
        boolean homeSearch = false;
        while(true) {
            try {
                setThreadSpeed();
                if(currentBlock.isBlockObjectGoal() == 1 && homeSearch == false) {
                    agent.hasObject = 1;
                    homeSearch = true;
                    clearStateCheckedBlocks();
                }
                if(currentBlock.home == 1 && homeSearch == true) {
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT retrieved object and went back home.\n\nMin-Conflict Search Results:" 
                            + "\nAllowed Lifetime Steps: " + (agent.lifetime) + agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks);
                    return;
                }
                if(currentBlock.isBlockAgent() == 1) {
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT died.\n\nMin-Conflict Search Results:" + 
                            "\nAllowed Lifetime Steps: " + (agent.lifetime) + 
                            agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks);
                    return;
                }
                if(agent.totalSteps >= agent.lifetime) {
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT took too long to find goal object and return home.\n\nMin-Conflict Search Results: \nThe Agent failed.\n"
                            + "Allowed Lifetime Steps: " + (agent.lifetime)
                            + agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks);
                    return;
                }
                clearCurrentSpot();
                currentBlock.stateChecked = 1;
                numOfBlocks++;
                if(homeSearch == false) {
                    agent.stepsToObject++;
                    agent.totalSteps++;
                }
                else {
                    agent.stepsHome++;
                    agent.totalSteps++;
                }
                grabNeighbors();
                if(neighbors.isEmpty()) {
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT is unable to move due to the nature of the algorithm.\n\nMin-Conflict Search Results: \nThe Agent failed.\n"
                            + "Allowed Lifetime Steps: " + (agent.lifetime)
                            + agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks);
                    return;
                }
                currentBlock = nextCurrentBlock();
                traversingBlocks();
            }
            catch(Exception e) {}
        }
    }
    
    /**
     * Finds the home block in the grid and references it to the root of our tree.
     */
    public void findRoot() {
        int a = this.agent.length;
        int b = this.agent.width;
        
        for(int i = 0; i < a; i++) {
            for(int j = 0; j < b; j++) {
                if(this.agent.board[i][j].home == 1) {
                    root = this.agent.board[i][j];
                }
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
     * Discovers the children of the current node in the queue if current node is not goal, home, or agent.
     */
    public void grabNeighbors(){
        //DOWN
        if(agent.posX+1 < agent.length) {
            if(this.agent.board[agent.posX+1][agent.posY].stateChecked == 0
               && this.agent.board[agent.posX+1][agent.posY].obstacle == 0
               && this.agent.board[agent.posX+1][agent.posY].agent == 0) {
                //add to queue
                neighbors.addFirst(this.agent.board[agent.posX+1][agent.posY]);
            }
        }
        //UP
        if(agent.posX-1 >= 0) {
            if(this.agent.board[agent.posX-1][agent.posY].stateChecked == 0
               && this.agent.board[agent.posX-1][agent.posY].obstacle == 0
               && this.agent.board[agent.posX-1][agent.posY].agent == 0) {
                //add to queue
                neighbors.addFirst(this.agent.board[agent.posX-1][agent.posY]);
            }
        }
        //RIGHT
        if(agent.posY+1 < agent.width) {
            if(this.agent.board[agent.posX][agent.posY+1].stateChecked == 0
               && this.agent.board[agent.posX][agent.posY+1].obstacle == 0
               && this.agent.board[agent.posX][agent.posY+1].agent == 0) {
                //add to queue
                neighbors.addFirst(this.agent.board[agent.posX][agent.posY+1]);
            }
        }
        //LEFT
        if(agent.posY-1 >= 0) {
            if(this.agent.board[agent.posX][agent.posY-1].stateChecked == 0
               && this.agent.board[agent.posX][agent.posY-1].obstacle == 0
               && this.agent.board[agent.posX][agent.posY-1].agent == 0) {
                //add to queue
                neighbors.addFirst(this.agent.board[agent.posX][agent.posY-1]);
            }
        }
    }
    
    /**
     * Grabs the next node to visit from the neighbor queue. 
     * @return Block new current node
     */
    public Block nextCurrentBlock() {
        Block temp;
        int size = neighbors.size();
        Random randSearch = new Random();
        int move = randSearch.nextInt(((size))+0); //generate a number between 0 - 3
        temp = neighbors.get(move);
        agent.posX = temp.xPos;
        agent.posY = temp.yPos;
        //clear neighbors after selecting next node
        neighbors.clear();
        return temp;
    }
    
    /**
     * Sets the state of each block the robot agent traverses. 
     * This also helps the coloring of the grid. 
     */
    public void traversingBlocks() {
        this.agent.board[agent.posX][agent.posY].iAmHere = true;
        if(agent.hasObject == 1) {
            agent.board[agent.posX][agent.posY].setHomeTraversed();
            agent.board[agent.posX][agent.posY].repaint();
        }
        else {
            agent.board[agent.posX][agent.posY].setTraversed();
            agent.board[agent.posX][agent.posY].repaint();
        }
    }
    
    /**
     * Clears the state check variable on all blocks in the grid.
     */
    public void clearStateCheckedBlocks() {
        for(int i = 0; i < agent.length; i++) {
            for(int j = 0; j < agent.width; j++) {
                agent.board[i][j].stateChecked = 0;
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