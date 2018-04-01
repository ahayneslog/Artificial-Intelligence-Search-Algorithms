import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * A* Search finds the shortest path by always selecting
 * the shortest cost to the object as it moves from one 
 * area to another. 
 * (Note: There are instances where no paths exist.)
 * @author Andrew C. Haynes
 */
public class AStarSearch extends Thread {
    Agent agent;
    LinkedList<Block> queue;
    Block root;
    Block goal;
    int maxBlocks;
    Thread t;
    
    /**
     * Instantiates the A* Search by connecting the robot agent to the
     * search. 
     * @param agent Robot agent committing the search 
     */
    public AStarSearch(Agent agent) {
        this.agent = agent;
        queue = new LinkedList<>();
        t = new Thread();
    }
    
    /**
     * Performs an A* Search where the fitness cost of every node is the cost thus far plus the straight line
     * distance to the goal. 
     */
    public void asSearch() {
        findRoot();
        findGoal();
        setPathCostForGridGoal();
        this.start();
    }
    
    /**
     * Starts the thread that runs the A* Search. 
     */
    public void run() {
        Block currentNode = root;
        while(true) {            
            try {
                setThreadSpeed();
                //color in the current block we are at
                clearCurrentSpot();
                traversingBlocks();
                if(currentNode.isBlockObjectGoal() == 1) {
                    agent.hasObject = 1;
                    setPathCostForGridHome(); //changes the h function costs so that we don't revisit this node
                    clearGCost(); //clears g function costs
                    queue.clear(); //clears queue
                }
                else if(currentNode.isBlockAgent() == 1) {
                    currentNode.setRobotDeathBlock();
                    clearCurrentSpot();
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT died.\n\nA* Search Results:" + 
                            "\nAllowed Lifetime Steps: " + (agent.lifetime) + agent.performance()
                            + "\nNumber of Blocks Used: " + agent.totalSteps
                            + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return;
                }
                else if(agent.totalSteps >= agent.lifetime) {
                    JOptionPane.showMessageDialog(null, 
                            "ROBOT took too long to find goal object and return home.\n\nA* Search Results: \nThe Agent failed.\n"
                            + "Allowed Lifetime Steps: " + (agent.lifetime) + agent.performance()
                            + "\nNumber of Blocks Used: " + agent.totalSteps
                            + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return;
                }
                else if(currentNode.home == 1 && agent.hasObject == 1) {
                    clearCurrentSpot();
                    //report success
                    JOptionPane.showMessageDialog(null,"ROBOT retrieved object and went back home.\n\nA* Search Results:" 
                            + "\nAllowed Lifetime Steps: " + (agent.lifetime) + agent.performance()
                            + "\nNumber of Blocks Used: " + agent.totalSteps + "\nMax Number of Blocks during Runtime: " + maxBlocks);
                    return;
                }
                neighbors(currentNode); //grab neighbors
                currentNode = findMin();
                //Calculate steps for performance measure
                if(agent.hasObject == 1) {
                    agent.stepsHome++;
                    agent.totalSteps++;
                }
                else {
                    agent.stepsToObject++;
                    agent.totalSteps++;
                }
                maxLength(queue);
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
     * Puts neighbors into a queue lowest path cost evaluation. 
     * @param block a neighbor of the current block
     */
    public void neighbors(Block block) {
        agent.posX = block.getXPos();
        agent.posY = block.getYPos();
        //DOWN
        if(agent.posX+1 < agent.length) {
            if(queue.contains(this.agent.board[agent.posX+1][agent.posY])) {
                this.agent.board[agent.posX+1][agent.posY].gCost += 1;
            }
            else {
                this.agent.board[agent.posX+1][agent.posY].gCost += 1;
                //add to queue
                queue.add(this.agent.board[agent.posX+1][agent.posY]);
            }
        }
        //UP
        if(agent.posX-1 >= 0) {
            if(queue.contains(this.agent.board[agent.posX-1][agent.posY])) {
                this.agent.board[agent.posX-1][agent.posY].gCost += 1;
            }
            else {
                this.agent.board[agent.posX-1][agent.posY].gCost += 1;
                //add to queue
                queue.add(this.agent.board[agent.posX-1][agent.posY]);
            }
        }
        //RIGHT
        if(agent.posY+1 < agent.width) {
            if(queue.contains(this.agent.board[agent.posX][agent.posY+1])) {
                this.agent.board[agent.posX][agent.posY+1].gCost += 1;
            }
            else {
                this.agent.board[agent.posX][agent.posY+1].gCost += 1;
                //add to queue
                queue.add(this.agent.board[agent.posX][agent.posY+1]);
            }
        }
        //LEFT
        if(agent.posY-1 >= 0) {
            if(queue.contains(this.agent.board[agent.posX][agent.posY-1])) {
                this.agent.board[agent.posX][agent.posY-1].gCost += 1;
            }
            else {
                this.agent.board[agent.posX][agent.posY-1].gCost += 1;
                //add to queue
                queue.add(this.agent.board[agent.posX][agent.posY-1]);
            }
        }
    }
    
    /**
     * Finds the node in the queue with the current lowest fitness cost. 
     * @return the block with the shortest cost to the object
     */
    public Block findMin() {
        Block test;
        test = queue.getFirst();
        for(Block what: queue) {
            //grab the minimum
            if(Double.compare(costFunction(what),costFunction(test)) < 0
                    || Double.compare(costFunction(what),costFunction(test)) == 0) {
                test = what;
            }
        }
        queue.remove(test); //path already explored
        return test;
    }
    
    /**
     * Overall cost of the possible next node to travel to. 
     * @param finish next node to travel to
     * @return the fitness cost to the object/home
     */
    public double costFunction(Block finish) {
        return finish.gCost + finish.pathCost;
    }
    
    /**
     * Clear the g function cost for the grid. 
     */
    public void clearGCost() {
        for(int x = 0; x < this.agent.length; x++) {
            for(int y = 0; y < this.agent.width; y++) {
                this.agent.board[x][y].gCost = 0;
            }
        }
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
