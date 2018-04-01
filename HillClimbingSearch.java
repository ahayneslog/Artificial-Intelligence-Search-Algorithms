import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * The Hill Climbing Search will follow the lowest path cost to the goal
 * and then the lowest path cost to the Entry/Exit point of the environment. 
 * @author Andrew C. Haynes
 */
public class HillClimbingSearch extends Thread {
    
    Agent agent;
    Block root;
    Block goal;
    LinkedList<Block> queue;
    int numOfBlocks;
    
    /**
     * Initiates the Agent to this search algorithm. 
     * @param agent The Agent that calls this search algorithm. 
     */
    public HillClimbingSearch(Agent agent) {
        this.agent = agent;
        queue = new LinkedList();
        numOfBlocks = 0;
    }
    
    /**
     * Finds the Entry/Exit Point for this search to work off of. 
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
     * Finds the Goal Block for us to use it's location for the 
     * heuristic function. 
     */
    public void findGoal() {
        int a = this.agent.length;
        int b = this.agent.width;
        
        for(int i = 0; i < a; i++) {
            for(int j = 0; j < b; j++) {
                if(this.agent.board[i][j].isBlockObjectGoal() == 1) {
                    goal = this.agent.board[i][j];
                }
            }
        }
    }
    
    /**
     * Climbs up the lowest cost node until it reaches the goal block. 
     * Once goal is found, restarts the heuristic function to assign new values
     * to the blocks and goes back to the Entry/Exit block. 
     */
    public void hillClimbing() {
        this.start(); 
    }
    
    /**
     * Runs the Hill-Climbing search threads. 
     */
    public void run() {
        this.findRoot();
        this.findGoal();
        this.setPathCostForGridGoal();
        Block currentBlock = root;
        double nextEval = 0;
        Block nextBlock = null;
        boolean done = false;
        while(!done) {
            try {
                setThreadSpeed();
                clearCurrentSpot();
                neighbors(currentBlock);
                nextEval = Integer.MAX_VALUE;
                for(Block x: queue) {
                    if(Double.compare(evaluation(x),nextEval) < 0) {
                        nextBlock = x;
                        nextEval = evaluation(x);
                    }
                }
                //check if goal
                if(currentBlock.isBlockObjectGoal() == 1) {
                    numOfBlocks++;
                    queue.clear();
                    agent.hasObject = 1;
                    agent.stepsToObject++;
                    agent.posX = currentBlock.getXPos();
                    agent.posY = currentBlock.getYPos();
                    traversingBlocks();
                    done = true;
                }
                //else if agent
                else if(currentBlock.isBlockAgent() == 1) {
                    numOfBlocks++;
                    clearCurrentSpot();
                    this.agent.board[agent.posX][agent.posY].setRobotDeathBlock();
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT died.\n\nDepth-First Search Results:" + 
                            "\nAllowed Lifetime Steps: " + (agent.lifetime) + 
                            agent.performance()+ "\nNumber of Blocks Used: " + numOfBlocks);
                    return;
                }
                else if(agent.totalSteps >= agent.lifetime){
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT took too long to find goal object and return home.\n\nBreadth-First Search Results: \nThe Agent failed.\n"
                            + "Allowed Lifetime Steps: " + (agent.lifetime)
                            + agent.performance()+ "\nNumber of Blocks Used: " + numOfBlocks);
                    return;
                }
                else {
                    if(Double.compare(nextEval,evaluation(currentBlock)) > 0 || 
                            Double.compare(nextEval,evaluation(currentBlock)) == 0) {
                        traversingBlocks();
                        agent.stepsToObject++;
                        agent.totalSteps++;
                        queue.clear();
                    }
                    //nextEval is greater and we have not reached life time amount of steps yet 
                    else {
                        numOfBlocks++;
                        traversingBlocks();
                        agent.stepsToObject++;
                        agent.totalSteps++;
                        currentBlock = nextBlock;
                        queue.clear();
                    }
                }
            }
            catch (Exception e) {}
        }
        /**
         * If we got to here, do hill climbing back to home block.
         */
        this.setPathCostForGridHome();
        currentBlock = agent.board[agent.posX][agent.posY];
        nextBlock = null;
        while(true) {
            try {
                setThreadSpeed();
                clearCurrentSpot();
                neighbors(currentBlock);
                nextEval = Integer.MAX_VALUE;
                for(Block x: queue) {
                    if(Double.compare(evaluation(x),nextEval) < 0) {
                        nextBlock = x;
                        nextEval = evaluation(x);
                    }
                }
                //check if goal
                if(currentBlock.home == 1) {
                    numOfBlocks++;
                    traversingBlocks();
                    clearCurrentSpot();
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT retrieved object and went back home.\n\nDepth-First Search Results:" 
                            + "\nAllowed Lifetime Steps: " + (agent.lifetime) + agent.performance()+ "\nNumber of Blocks Used: " + numOfBlocks);
                    return;
                }
                //else if agent
                else if(currentBlock.isBlockAgent() == 1) {
                    numOfBlocks++;
                    clearCurrentSpot();
                    this.agent.board[agent.posX][agent.posY].setRobotDeathBlock();
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT died.\n\nDepth-First Search Results:" + 
                            "\nAllowed Lifetime Steps: " + (agent.lifetime) + 
                            agent.performance()+ "\nNumber of Blocks Used: " + numOfBlocks);
                    return;
                }
                else if(agent.totalSteps >= agent.lifetime){
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT took too long to find goal object and return home.\n\nBreadth-First Search Results: \nThe Agent failed.\n"
                            + "Allowed Lifetime Steps: " + (agent.lifetime)
                            + agent.performance()+ "\nNumber of Blocks Used: " + numOfBlocks);
                    return;
                }
                else {
                    if(Double.compare(nextEval,evaluation(currentBlock)) > 0 || 
                            Double.compare(nextEval,evaluation(currentBlock)) == 0) {
                        traversingBlocks();
                        agent.totalSteps++;
                        agent.stepsHome++;
                        queue.clear();
                    }
                    //nextEval is greater and we have not reached life time amount of steps yet 
                    else {
                        numOfBlocks++;
                        traversingBlocks();
                        agent.stepsHome++;
                        agent.totalSteps++;
                        currentBlock = nextBlock;
                        queue.clear();
                    }
                }
            }
            catch (Exception e) {}
        }
    }
    
    /**
     * Puts neighbors into a queue lowest path cost evaluation. 
     * @param block a neighbor of the current block
     */
    public void neighbors(Block block) {
        agent.posX = block.getXPos();
        agent.posY = block.getYPos();
        //DOWN
        if(agent.posX+1 < agent.length) {
            //add to queue
            queue.add(this.agent.board[agent.posX+1][agent.posY]);
        }
        //UP
        if(agent.posX-1 >= 0) {
            //add to queue
            queue.add(this.agent.board[agent.posX-1][agent.posY]);
        }
        //RIGHT
        if(agent.posY+1 < agent.width) {
            //add to queue
            queue.add(this.agent.board[agent.posX][agent.posY+1]);
        }
        //LEFT
        if(agent.posY-1 >= 0) {
            //add to queue
            queue.add(this.agent.board[agent.posX][agent.posY-1]);
        }
    }
    
    /**
     * Returns the path cost of a Block. 
     * @param x block to evaluate.
     * @return Block's Path Cost
     */
    public double evaluation(Block x) {
        return x.pathCost;
    }
    
    /**
     * Heuristic Function that maps path cost to each Block towards the Goal Block.
     */
    public void setPathCostForGridGoal() {
        int a = agent.length;
        int b = agent.width;
        for(int i = 0; i < a; i++){
            for(int j = 0; j < b; j++) {
                if(agent.board[i][j].isBlockObstacle() != 1)
                    setPathCostGoal(agent.board[i][j]);
            }
        }
    }
    
    /**
     * Utility function for heuristic function. Sets the actual path
     * cost value for the block to the Goal Block. 
     * @param test Block to be setting up a cost for. 
     */
    public void setPathCostGoal(Block test) {
        int x = Math.abs(goal.getXPos() - test.getXPos());
        int y = Math.abs(goal.getYPos() - test.getYPos());
        test.pathCost = Math.sqrt((x*x)+(y*y));
    }
    
    /**
     * Heuristic Function that maps path cost to each Block towards the Entry/Exit Block.
     */
    public void setPathCostForGridHome() {
        int a = agent.length;
        int b = agent.width;
        for(int i = 0; i < a; i++){
            for(int j = 0; j < b; j++) {
                if(agent.board[i][j].isBlockObstacle() != 1)
                    setPathCostHome(agent.board[i][j]);
            }
        }
    }
    
    /**
     * Utility function for heuristic function. Sets the actual path
     * cost value for the block to the Entry/Exit Block. 
     * @param test Block to be setting up a cost for. 
     */
    public void setPathCostHome(Block test) {
        int x = Math.abs(root.getXPos() - test.getXPos());
        int y = Math.abs(root.getYPos() - test.getYPos());
        test.pathCost = Math.sqrt((x*x)+(y*y));
    }
    
    /**
     * Sets the state of each block the robot agent traverses. 
     * This also helps the coloring of the grid. 
     */
    public void traversingBlocks() {
        agent.board[agent.posX][agent.posY].iAmHere = true;
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
