import java.util.*;
import javax.swing.JOptionPane;
/**
 * If the agent is provided with a 2-D array environment that is
 * filled with Block objects, this class can perform a Breadth-First Search
 * from the starting block to the goal block, and from the goal block to the 
 * starting block. The two searches will fail if an agent is encountered or
 * if the search took too long.
 * 
 * BFS [Graph]: 
 * add root node to queue, if goal/home, return success, if not expand that node
 * expand that node -> put children of root node into queue
 * repeat until queue is empty: 
 *          if current node is not goal/home, expand that node and put children at end of queue
 *          else if current node is goal/home, return success
 *          **this process allows us to look at each node in each row of the tree as BFS intends**
 * 
 *   ** STATE CHECKING IS INCLUDED IN THIS BFS. **
 * @author Andrew C. Haynes
 */
public class SCBreadthFirstSearch extends Thread {
    
    Agent agent;
    Block root;
    LinkedList<Block> queue;
    int numOfBlocks;
    int maxBlocks;
    
    /**
     * Default constructor for the BFS. 
     * @param agent the robot performing the search.
     */
    public SCBreadthFirstSearch(Agent agent){
        this.agent = agent;
        queue = new LinkedList();
        numOfBlocks = 0;
        maxBlocks = 0;
    }
    
    /**
     * Two Breadth-First Searches are done for our problem, where one is done from the home block
     * to the goal block, and the other search is done in reverse. 
     */
    public void breadthFirstSearch() {
        this.start();
    }
    
    /**
     * Runs the BFS threads. 
     */
    public void run() {
        //first, add root to queue
        this.findRoot();
        boolean done = false;
        queue.add(root);
        while(!done){
            if(!queue.isEmpty()) {
                try {
                    setThreadSpeed();
                    clearCurrentSpot();
                    int movement = this.goalMovement(queue.pollFirst());
                    maxLength(queue);
                    //goal is found and picked up
                    if(movement == 1) {
                        done = true;
                    }
                    //agent killed the robot
                    else if(movement == 2) {
                        clearCurrentSpot();
                        JOptionPane.showMessageDialog(null, 
                                "ROBOT died.\n\nBreadth-First Search Results:" + "\nAllowed Lifetime Steps: " + 
                                        agent.lifetime + agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks
                                + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                        return;
                    }
                    else if(agent.totalSteps >= agent.lifetime) {
                        JOptionPane.showMessageDialog(null, 
                              "ROBOT took too long to find goal object and return home.\n\nBreadth-First Search Results: \nThe Agent failed.\n"
                            + "Allowed Lifetime Steps: " + (agent.lifetime)
                            + agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks
                            + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                        return;
                    }
                    //if state 1 or state 2 is not reached, expand current node and push it's offsprings onto queue!
                    this.expandNode();
                }
                catch (Exception e) {}
            }
        }
        /**
         * The Robot agent made it to this point and found the object,
         * now make it do another BFS and go home.
         */
        clearStateCheckedBlocks();
        queue.clear(); //restart queue
        queue.add(this.agent.board[agent.posX][agent.posY]); //start the new BFS at the goal block!
        while(!queue.isEmpty()){
            try {
                setThreadSpeed();
                clearCurrentSpot();
                int movement = this.homeMovement(queue.pollFirst());
                //robot agent made it home, report success, then return
                if(movement == 1) {
                    clearCurrentSpot();
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT retrieved object and went back home.\n\nBreadth-First Search Results:" 
                            + "\nAllowed Lifetime Steps: " + (agent.lifetime) + agent.performance() +
                                    "\nNumber of Blocks Used: " + numOfBlocks
                            + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return;
                }
                //agent killed the robot
                else if(movement == 2) {
                    clearCurrentSpot();
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT died.\n\nBreadth-First Search Results:" + 
                            "\nAllowed Lifetime Steps: " + (agent.lifetime) + 
                            agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks
                        + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return;
                }
                else{
                    if(agent.totalSteps >= agent.lifetime) {
                        JOptionPane.showMessageDialog(null, 
                          "ROBOT took too long to find goal object and return home.\n\nBreadth-First Search Results: \nThe Agent failed.\n"
                        + "Allowed Lifetime Steps: " + (agent.lifetime)
                        + agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks
                        + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                        return;
                    }
                }
                //if state 1 or state 2 is not reached, expand current node and push it's offsprings onto queue!
                this.expandNode();
            }
            catch (Exception e) {}
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
     * Discovers the children of the current node in the queue if current node is not goal, home, or agent.
     */
    public void expandNode() {
        //DOWN
        if(agent.posX+1 < agent.length) {
            if(this.agent.board[agent.posX+1][agent.posY].stateChecked == 0) {
                //add to queue
                queue.addLast(this.agent.board[agent.posX+1][agent.posY]);
            }
        }
        //UP
        if(agent.posX-1 >= 0) {
            if(this.agent.board[agent.posX-1][agent.posY].stateChecked == 0) {
                //add to queue
                queue.addLast(this.agent.board[agent.posX-1][agent.posY]);
            }
        }
        //RIGHT
        if(agent.posY+1 < agent.width) {
            if(this.agent.board[agent.posX][agent.posY+1].stateChecked == 0) {
                //add to queue
                queue.addLast(this.agent.board[agent.posX][agent.posY+1]);
            }
        }
        //LEFT
        if(agent.posY-1 >= 0) {
            if(this.agent.board[agent.posX][agent.posY-1].stateChecked == 0) {
                //add to queue
                queue.addLast(this.agent.board[agent.posX][agent.posY-1]);
            }
        }
    }
    
    /**
     * Helps the robot do a BFS to the goal block to pick up the object.
     * @param spartacus the current block being examined in the queue
     * @return 0 if free or obstacle, 1 if goal, 2 if agent
     */
    public int goalMovement(Block spartacus) {
            if(spartacus.isBlockObstacle() == 1){
                //just calculate the move
                int xMove = Math.abs(agent.posX - spartacus.getXPos());
                int yMove = Math.abs(agent.posY - spartacus.getYPos());
                agent.stepsToObject += (xMove + yMove);
                agent.totalSteps += (xMove + yMove);
                numOfBlocks++;
                return 0;
            }
            else if(spartacus.isBlockAgent() == 1) {
                int xMove = Math.abs(agent.posX - spartacus.getXPos());
                int yMove = Math.abs(agent.posY - spartacus.getYPos());
                agent.stepsToObject += (xMove + yMove);
                agent.totalSteps += (xMove + yMove);
                agent.posX = spartacus.getXPos();
                agent.posY = spartacus.getYPos();
                agent.board[agent.posX][agent.posY].setRobotDeathBlock();
                numOfBlocks++;
                return 2;
            }
            else if(spartacus.isBlockObjectGoal() == 1) {
                agent.hasObject = 1;
                int xMove = Math.abs(agent.posX - spartacus.getXPos());
                int yMove = Math.abs(agent.posY - spartacus.getYPos());
                agent.stepsToObject += (xMove + yMove);
                agent.totalSteps += (xMove + yMove);
                //Now let the agent have the current block's position for coloring the graph
                agent.posX = spartacus.getXPos();
                agent.posY = spartacus.getYPos();
                traversingBlocks();
                numOfBlocks++;
                return 1;
            }
            else { //free block! 
                if(spartacus.stateChecked == 0) {
                    traversingBlocks(); //color the current block!
                    //How many spaces did the robot agent move?
                    int xMove = Math.abs(agent.posX - spartacus.getXPos());
                    int yMove = Math.abs(agent.posY - spartacus.getYPos());
                    agent.stepsToObject += (xMove + yMove);
                    agent.totalSteps += (xMove + yMove);
                    //Now let the agent have the current block's position for coloring the graph
                    agent.posX = spartacus.getXPos();
                    agent.posY = spartacus.getYPos();
                    agent.board[agent.posX][agent.posY].stateChecked = 1;
                    numOfBlocks++;
                }
                return 0;
            }
    }
    
    /**
     * Helps the robot do a BFS back to the home block after picking up the object.
     * @param spartacus the current block that is being looked at in the queue
     * @return 0 if free or obtacle, 1 if goal, 2 if agent
     */
    public int homeMovement(Block spartacus) {
            if(spartacus.isBlockObstacle() == 1){
                //just calculate the move
                int xMove = Math.abs(agent.posX - spartacus.getXPos());
                int yMove = Math.abs(agent.posY - spartacus.getYPos());
                agent.stepsHome += (xMove + yMove); 
                agent.totalSteps += (xMove + yMove);
                numOfBlocks++;
                return 0;
            }
            else if(spartacus.isBlockAgent() == 1) {
                int xMove = Math.abs(agent.posX - spartacus.getXPos());
                int yMove = Math.abs(agent.posY - spartacus.getYPos());
                agent.stepsHome += (xMove + yMove); 
                agent.totalSteps += (xMove + yMove);
                agent.posX = spartacus.getXPos();
                agent.posY = spartacus.getYPos();
                agent.board[agent.posX][agent.posY].setRobotDeathBlock();
                numOfBlocks++;
                return 2;
            }
            else if(spartacus.home == 1) {
                agent.hasObject = 1;
                int xMove = Math.abs(agent.posX - spartacus.getXPos());
                int yMove = Math.abs(agent.posY - spartacus.getYPos());
                agent.stepsHome += (xMove + yMove);
                agent.totalSteps += (xMove + yMove);
                //Now let the agent have the current block's position for coloring the graph
                agent.posX = spartacus.getXPos();
                agent.posY = spartacus.getYPos();
                traversingBlocks();
                numOfBlocks++;
                return 1;
            }
            else { //free block! 
                if(spartacus.stateChecked == 0) {
                    traversingBlocks(); //color the current block!
                    //How many spaces did the robot agent move?
                    int xMove = Math.abs(agent.posX - spartacus.getXPos());
                    int yMove = Math.abs(agent.posY - spartacus.getYPos());
                    agent.stepsHome += (xMove + yMove); 
                    agent.totalSteps += (xMove + yMove);
                    //Now let the agent have the current block's position for coloring the graph
                    agent.posX = spartacus.getXPos();
                    agent.posY = spartacus.getYPos();
                    agent.board[agent.posX][agent.posY].stateChecked = 1;
                    numOfBlocks++;
                }
                return 0;
            }
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
     * Sets the new maximum amount of blocks in queue during search.
     * @param q 
     */
    public void maxLength(LinkedList q){
        if(maxBlocks <= q.size())
            maxBlocks = q.size();
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