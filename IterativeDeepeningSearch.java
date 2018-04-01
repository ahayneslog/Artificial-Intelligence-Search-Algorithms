import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * Iterative Deepening Search traverses the tree created to the depth limit
 * specified. Once the limit is reached the search is over, or the limit can be raised by 
 * an amount each time the search is over until the object is found or if the
 * Robot Agent dies. 
 * @author Andrew C. Haynes
 */
public class IterativeDeepeningSearch extends Thread {
    
    Agent agent;
    LinkedList<Block> queue;
    Block root;
    Block goal;
    int limit;
    boolean goalFound;
    int numOfBlocks;
    int maxBlocks;
    
    /**
     * Instantiates the Iterative Deepening Search with the Robot Agent 
     * passed to the search object.
     * @param agent The RObot agent committing the search to the environment
     */
    public IterativeDeepeningSearch(Agent agent) {
        this.agent = agent;
        queue = new LinkedList<>();
        limit = 0;
        goalFound = false;
        numOfBlocks = 0;
        maxBlocks = 0;
    }
    
    /**
     * Commits the Iterative Deepening Search to the grid
     * and finds the object and then goes back home.
     */
    public void idSearch() {
        this.start();
    }
    
    /**
     * Runs the IDS threads.
     */
    public void run() {
        /**
         * Object Search
         */
        boolean goalSearch = true;
        findRoot();
        setDepthOfBlocksFromHome();
        while(goalSearch) {
            //clear queue
            queue.clear();
            //clear states in grid
            clearGridStates();
            //build queue to the limit
            buildQueueFromRoot(limit++);
            maxLength(queue);
            goalSearch = movementToGoal();
        }
        /**
         * Home Search
         */
        if(goalFound) {
            limit = 0;
            boolean homeSearch = true;
            findGoal();
            setDepthofBlocksFromGoal();
            while(homeSearch) {
                //clear queue
                queue.clear();
                //clear states in grid
                clearGridStates();
                //build queue to the limit
                buildQueueFromGoal(limit++);
                maxLength(queue);
                homeSearch = movementToHome();
            }
        }
    }
    
    /**
     * Builds up a queue up to the limit specified by the parameter.
     * @param trueLimit the depth limit for the tree-like queue. 
     */
    public void buildQueueFromRoot(int trueLimit) {
        //Setting up queue before doing a loop queue builder
        queue.add(root);
        while(isQueueFinished()) {
            //sets current node to visited and grabs unvisited and under or at limit blocks to
            //put into the queue (in the right order, too!)
            expandQueue(findFirstUnvisitedNode(), trueLimit);
        }
    }
    
    /**
     * Determines if a block in the queue has the goal object, is free or is an agent that can kill
     * the robot agent.
     * @return true if queue didn't have any goal or agent
     */
    public boolean movementToGoal() {
        for(int i = 0; i < queue.size(); i++) {
            try  {
                Block test = queue.get(i);
                setThreadSpeed();
                clearCurrentSpot();
                if(test.isBlockObjectGoal() == 1) {
                    agent.stepsToObject++;
                    agent.totalSteps++;
                    numOfBlocks++;
                    goalFound = true;
                    return false;
                }
                else if(test.isBlockAgent() == 1) {
                    test.setRobotDeathBlock();
                    agent.stepsToObject++;
                    agent.totalSteps++;
                    numOfBlocks++;
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT died.\n\nIterative Deepening Search Results:" + 
                            "\nAllowed Lifetime Steps: " + (agent.lifetime) + 
                            agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks
                            + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return false;
                }
                else if(agent.totalSteps >= agent.lifetime) {
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT took too long to find goal object and return home.\n\nIterative-Deepening Search Results: \nThe Agent failed.\n"
                            + "Allowed Lifetime Steps: " + (agent.lifetime)
                            + agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks
                            + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return false;
                }
                else {
                    test.iAmHere = true;
                    test.setTraversed();
                    test.repaint();
                    agent.stepsToObject++;
                    agent.totalSteps++;
                    numOfBlocks++;
                }
            }
            catch (Exception e) {}
        }
        //return true since this queue does not have an agent nor a goal block in it
        return true;
    }
    
    /**
     * Finds out if any block in the queue has not been visited yet, 
     * if so, return true. It's counter-intuitive, I know. 
     * @return true if there are still unvisited blocks in the queue.
     */
    public boolean isQueueFinished() {
        for(int i = 0; i < queue.size(); i++) {
            if(queue.get(i).stateChecked == 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Searches the queue linearly for a block that has not 
     * been visited and returns the reference to it. 
     * @return the unvisited block (it can be null)
     */
    public Block findFirstUnvisitedNode() {
        Block returnMe = null; 
        for(Block a: queue) {
            if(a.stateChecked == 0)
                returnMe = a;
        }
        return returnMe;
    }
    
    /**
     * Discovers the children of the current node in the queue and adds the children based on
     * a few conditions.
     * @param test
     * @param trueLimit
     */
    public void expandQueue(Block test, int trueLimit){
        int counter = queue.indexOf(test) + 1;
        test.stateChecked = 1;
        //DOWN
        if(test.xPos+1 < agent.length) {
            Block addMe = this.agent.board[test.xPos+1][test.yPos];
            if(addMe.depth <= trueLimit && addMe.stateChecked == 0 && addMe.isBlockObstacle() != 1) {
                queue.add(counter++, addMe);
            }
        }
        //UP
        if(test.xPos-1 >= 0) {
            Block addMe = this.agent.board[test.xPos-1][test.yPos];
            if(addMe.depth <= trueLimit && addMe.stateChecked == 0 && addMe.isBlockObstacle() != 1) {
                queue.add(counter++, addMe);
            }
        }
        //RIGHT
        if(test.yPos+1 < agent.width) {
            Block addMe = this.agent.board[test.xPos][test.yPos+1];
            if(addMe.depth <= trueLimit && addMe.stateChecked == 0 && addMe.isBlockObstacle() != 1) {
                queue.add(counter++, addMe);
            }
        }
        //LEFT
        if(test.yPos-1 >= 0) {
            Block addMe = this.agent.board[test.xPos][test.yPos-1];
            if(addMe.depth <= trueLimit && addMe.stateChecked == 0 && addMe.isBlockObstacle() != 1) {
                queue.add(counter++, addMe);
            }
        }
    }
    
    /**
     * Finds the home block.
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
     * Finds the Goal Block. 
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
     * Sets the depth level for each node from the home block.
     */
    public void setDepthOfBlocksFromHome() {
        for(int i = 0; i < agent.length; i++) {
            for(int j = 0; j < agent.width; j++) {
                int x = Math.abs(root.getXPos() - agent.board[i][j].getXPos());
                int y = Math.abs(root.getYPos() - agent.board[i][j].getYPos());
                agent.board[i][j].depth = x + y;
            }
        }
    }
    
    /**
     * Builds up a queue up to the limit specified by the parameter.
     * @param trueLimit the depth limit for the tree-like queue. 
     */
    public void buildQueueFromGoal(int trueLimit) {
        //Setting up queue before doing a loop queue builder
        queue.add(goal);
        while(isQueueFinished()) {
            //sets current node to visited and grabs unvisited and under or at limit blocks to
            //put into the queue (in the right order, too!)
            expandQueue(findFirstUnvisitedNode(), trueLimit);
        }
    }
    
    /**
     * Determines if a block in the queue has the goal object, is free or is an agent that can kill
     * the robot agent.
     * @return true if queue didn't have any goal or agent
     */
    public boolean movementToHome() {
        for(int i = 0; i < queue.size(); i++) {
            try {
                Block test = queue.get(i);
                setThreadSpeed();
                clearCurrentSpot();
                if(test.home == 1) {
                    agent.stepsHome++;
                    agent.totalSteps++;
                    numOfBlocks++;
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT retrieved object and went back home.\n\nItertive Deepning Search Results:" 
                            + "\nAllowed Lifetime Steps: " + (agent.lifetime) + agent.performance()
                             + "\nNumber of Blocks Used: " + numOfBlocks
                            + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return false;
                }
                else if(test.isBlockAgent() == 1) {
                    test.setRobotDeathBlock();
                    agent.stepsHome++;
                    agent.totalSteps++;
                    numOfBlocks++;
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT died.\n\nIterative Deepening Search Results:" + 
                            "\nAllowed Lifetime Steps: " + (agent.lifetime) + 
                            agent.performance() + "\nNumber of Blocks Used: " + numOfBlocks
                            + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return false;
                }
                else if(agent.totalSteps >= agent.lifetime) {
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT took too long to find goal object and return home.\n\nIterative-Deepening Search Results: \nThe Agent failed.\n"
                            + "Allowed Lifetime Steps: " + (agent.lifetime)
                            + agent.performance()  + "\nNumber of Blocks Used: " + numOfBlocks
                            + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return false;
                }
                else {
                    test.iAmHere = true;
                    test.setHomeTraversed();
                    test.repaint();
                    agent.stepsHome++;
                    agent.totalSteps++;
                    numOfBlocks++;
                }
            }
            catch (Exception e) {}
        }
        //return true since this queue does not have an agent nor a goal block in it
        return true;
    }
    
    /**
     * Sets the depth level for each node from the goal block.
     */
    public void setDepthofBlocksFromGoal() {
        for(int i = 0; i < agent.length; i++) {
            for(int j = 0; j < agent.width; j++) {
                int x = Math.abs(goal.getXPos() - agent.board[i][j].getXPos());
                int y = Math.abs(goal.getYPos() - agent.board[i][j].getYPos());
                agent.board[i][j].depth = x + y;
            }
        }
    }
    
    /**
     * Clears the state check variable on all blocks in the grid.
     */
    public void clearGridStates() {
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
     * @param q queue to find max size of
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
