import java.util.Random;
/**
 * The robot agent has several search agent it can initiate depending on which one
 * the user selects in the search simulator GUI. It is here that these search algorithms are implemented!
 * @author Andrew C. Haynes
 */
public class Agent {

    //Environment is boad, which is a 2D array of Blocks that have state info
    Block[][] board;
    //properties of Environment
    int length;
    int width;
    //declares if Agent (Robot) is destroyed in search
    int destroyed;
    //Performance Measure parameters
    int stepsToObject;
    int stepsHome;
    int hasObject;
    int totalSteps;
    //Start and finish X and Y positions
    int startX;
    int startY;
    //current X and Y positions
    int posX;
    int posY;
    //this is only for moving back from an obstacle
    int previousState; 
    
    //Life time steps
    int lifetime = 0;
    
    //For starting position
    Random rand1;
    Random rand2;
    
    /**
     * Creates a robot agent that takes in the Environment and it's parameters.
     * @param grid a 2D Array Environment
     * @param len length of 2D Array
     * @param wid width of 2D Array
     */
    public Agent(Block[][] grid, int len, int wid) {
        
        board = grid;
        length = len;
        width = wid;
        lifetime = len*wid*50;
        destroyed = 0;
        stepsToObject = 0;
        stepsHome = 0;
        hasObject = 0;
        totalSteps = 0;
        previousState = 0;
        startX = 0;
        startY = 0;
        posX = 0;
        posY = 0;
    }
    
    /**
     * Starts the robot agent on a random boundary block of the grid.  
     */
    public void startPosition() {
        boolean entryPointFound = false;
        while(!entryPointFound) {
            rand1 = new Random();
            rand2 = new Random();
            int random1 = rand1.nextInt(((length))+0); //random1 is associated with length
            int random2 = rand2.nextInt(((width))+0);  //random2 is associated with width
            if(random1 == 0 || random2 == 0){
                //this one checks first row and first column
                if(board[random1][random2].isBlockFree() == 1) {
                    board[random1][random2].setTraversed();
                    posX = random1;
                    posY = random2;
                    startX = random1;
                    startY = random2;
                    entryPointFound = true;
                }
            }
            else if(random1 == (length-1) || random2 == (width-1)){
                //this one checks last row and last column
                if(board[random1][random2].isBlockFree() == 1) {
                    board[random1][random2].setTraversed();
                    posX = random1;
                    posY = random2;
                    startX = random1;
                    startY = random2;
                    entryPointFound = true;
                }
            }
        }
        board[startX][startY].setHome();
    }
    
    /**
    * Find the home block on the board and tell the agent it's location
    * so that it starts there when the grid is loaded up from file. 
    */
    public void setStartPosition() {
        for(int i = 0; i < length; i++) {
            for(int j = 0; j < width; j++) {
                if(board[i][j].home == 1) {
                    posX = i;
                    posY = j;
                    startX = i; 
                    startY = j;
                }
            }
        }
    }
    
    /**
     * This function is for if you need to find a new home block
     * for a random restart algorithm. 
     */
    public void clearHome() {
        for(int i = 0; i < length; i++) {
            for(int j = 0; j < width; j++) {
                if(board[i][j].home == 1)
                    board[i][j].home = 0;
            }
        }
    }
    
    /**
     * Agent (Robot) calls this function start specified search algorithm. 
     * Modifiable to add more search options
     * @param searchType is a state mechanism that controls which search algorithm to implement
     */
    public void startSearch(Object searchType) {
        //0 is blind search or non-informed search
        if(searchType.equals("Random Search")) {
            BlindSearch bs = new BlindSearch(this);
            bs.blindSearch();
        }
        else if(searchType.equals("Breadth-First Search [Tree Based]")) {
            BreadthFirstSearch bfs = new BreadthFirstSearch(this);
            bfs.breadthFirstSearch();
        }
        else if(searchType.equals("Breadth-First Search [Graph Based]")) {
            SCBreadthFirstSearch scbfs = new SCBreadthFirstSearch(this);
            scbfs.breadthFirstSearch();
        }
        else if(searchType.equals("Depth-First Search [Tree Based]")) {
            DepthFirstSearch dfs = new DepthFirstSearch(this);
            dfs.depthFirstSearch();
        }
        else if(searchType.equals("Depth-First Search [Graph Based]")) {
            SCDepthFirstSearch scdfs = new SCDepthFirstSearch(this);
            scdfs.depthFirstSearch();
        }
        else if(searchType.equals("Hill-Climbing Search")) {
            HillClimbingSearch hss = new HillClimbingSearch(this);
            hss.hillClimbing();
        }
        else if(searchType.equals("Random Restart Hill-Climbing Search")) {
            RRHillClimbingSearch rrhss = new RRHillClimbingSearch(this);
            rrhss.hillClimbing();
        }
        else if(searchType.equals("Iterative Deepening Search")) {
            IterativeDeepeningSearch ids = new IterativeDeepeningSearch(this);
            ids.idSearch();
        }
        else if(searchType.equals("A* Search")) {
            AStarSearch as = new AStarSearch(this);
            as.asSearch();
        }
        else {
            //do nothing
        }
            
    }
    
    /**
     * Checks state of block.
     * NOTE: This was used in Blind Search only. 
     */
    public void checkBlock(){
        //Is this block an agent?
        if(board[posX][posY].isBlockAgent() == 1) {
            destroyed = 1;
            board[posX][posY].setRobotDeathBlock();
            board[posX][posY].setTraversed();
        }
        else if(board[posX][posY].isBlockObstacle() == 1){
            if(previousState == 0) {
                //go right
                posY++;
            }
            else if(previousState == 1) {
                //go left
                posY--;
            }
            else if(previousState == 2) {
                //go down
                posX++;
            }
            else if(previousState == 3) {
                //go up
                posX--;
            }
            else {
                //How did you get here?
            }
        }
        else if(board[posX][posY].isBlockObjectGoal() == 1){
            hasObject = 1;
            board[posX][posY].setTraversed();
        }
    }
    
    /**
     * Determines if robot has picked up object or not. If it has,
     * start setting the other traversal state of the blocks in the grid!
     * NOTE: This is only used in Blind Search.
     */
    public void holdObjectMovement() {    
        if(hasObject == 1) {
            stepsHome++;
            board[posX][posY].setHomeTraversed();
            board[posX][posY].repaint();
        }
        else {
            stepsToObject++;
            board[posX][posY].setTraversed();
            board[posX][posY].repaint();
        }
    }

    /**
     * Determines the performance of the robot through the grid. 
     * @return a string that reports a series of data that shows the robot performance with a search algorithm
     */
    public String performance(){
        int free = 0;
        int untouchable = 0;
        int touched = 0;
        int numOfSquares = length*width;
        
        //Record number of squares touched before picking up object, after picking up object and which
        //squares were touched twice
        for(int i = 0; i < length; i++) {
            for(int j = 0; j < width; j++) {
                if(board[i][j].isBlockObstacle() == 1 || board[i][j].isBlockAgent() == 1)
                    untouchable++;
                if(board[i][j].isBlockFreeAfterSearch() == 1)
                    free++;        
            }
        }
        //determines how many squares were traversed
        touched = numOfSquares - free - untouchable;
        //if it touched an agent, that is included in the total numbers of squares traversed
        if(destroyed == 1)
            touched++;
        //determine the percentage of squares touched
        double numOfSqPercent = ((double)touched/numOfSquares)*100;
        //Add this percentage to System Output
        String performanceMeasure = "\nSteps Taken Before Object Retrieved: " + stepsToObject
                            + "\nSteps Taken with Object: " + stepsHome
                            + "\nSteps Total: " + totalSteps
                            + "\n% of Blocks Traversed: " + numOfSqPercent + "%";
        return performanceMeasure;
    }
}
