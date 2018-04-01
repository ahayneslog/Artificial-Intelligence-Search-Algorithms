import java.awt.*;
import javax.swing.*;
/**
 * A JPanel that have various states such as traversed, agent, obstacle 
 * or a free block. It can paint itself to its appropriate state and show if the robot agent 
 * has died at this particular block.
 * @author Andrew C. Haynes
 */
public class Block extends JPanel {
    
    public int xPos;
    public int yPos;
    //Determines if this is the starting block for the robot agent
    public int home;
    //Robot agent has touched this block before picking up object
    public int traversed;
    //Robot agent has touched this block after picking up the object
    public int homeTraversed;
    //This Block is an obstacle and cannot be traversed
    public int obstacle;
    //This Block is agent and will destroy the robot agent if the robot agent traverses it
    public int agent;
    //This Block has the object goal for the robot agent to pick up
    public int goal;
    //This Block is where the robot agent died
    public int robotDiedHere;
    //Allows the Block to paint it self to its current state
    public Graphics2D g2;
    //Path cost for heuristic functions
    public double pathCost;
    //Path cost for path so far
    public double gCost;
    //used for graph-based search
    public int stateChecked;
    //the depth of this node in the grid
    public int depth;
    //current agent location
    public boolean iAmHere;
    
    /**
     * Creates a JPanel that have various states such as traversed, agent, obstacle 
     * or a free block. It can paint itself to its appropriate state and show if the robot agent 
     * has died at this particular block.
     * @param x
     * @param y
     */
    public Block(int x, int y){
        setBorder(BorderFactory.createLineBorder(Color.black));
        setPreferredSize(new Dimension(10,10));
        xPos = x;
        yPos = y;
        home = 0;
        traversed = 0;
        obstacle = 0;
        agent = 0;
        goal = 0;
        homeTraversed = 0;
        robotDiedHere = 0;
        pathCost = 0.0;
        gCost = 0.0;
        stateChecked = 0;
        depth = -1;
        iAmHere = false;
        setBackground(Color.white);
    }
    
    /**
     * Returns if block is free or not. 
     * If it is, return 1.
     * @return 0 if it is not free, 1 if it is
     */
    public int isBlockFree(){
        if(agent == 1 || obstacle == 1 || goal == 1 || home == 1)
            return 0;
        else
            return 1;
    }
    
    /**
     * For Performance Measure purposes, a similar function to isBlockFree was 
     * created; the difference is that it checks for traversed states instead of goal or home. 
     * @return 0 if it is not free, 1 if it is
     */
    public int isBlockFreeAfterSearch() {
        if(agent == 1 || obstacle == 1 || traversed == 1 || homeTraversed == 1)
            return 0;
        else
            return 1;
    }
    
    /**
     * Returns if block is agent or not. 
     * If it is, return 1. 
     * @return 0 if it is not an agent, 1 if it is
     */
    public int isBlockAgent() {
        if(agent == 1)
            return 1;
        else
            return 0;
    }
    
    /**
     * Returns if block is obstacle or not.
     * @return 0 if it is not an obstacle, 1 if it is
     */
    public int isBlockObstacle() {
        if(obstacle == 1)
            return 1;
        else
            return 0;
    }
    
    /**
     * Returns if block was traversed before object pick up or not.
     * @return 0 if it is not traversed, 1 if it is
     */
    public int isBlockTraversed() {
        if(traversed == 1)
            return 1;
        else
            return 0;
    }
    
    /**
     * Returns if block was traversed after object pick up or not.
     * @return 0 if it is not traversed, 1 if it is
     */
    public int isBlockHomeTraversed() {
        if(homeTraversed == 1)
            return 1;
        else
            return 0;
    }
    
    /**
     * Returns if block has the object goal or not.
     * @return 0 if it is not carrying the object goal, 1 if it is
     */
    public int isBlockObjectGoal() {
        if(goal == 1) 
            return 1;
        else
            return 0;
    }
    
    /**
     * Mutator to set the block as a starting block for the robot agent.
     */
    public void setHome() {
        home = 1;
        setBackground(Color.green);
    }
    
    /**
     * Mutator to set the block as an agent.
     */
    public void setAgent() {
         agent = 1;
         obstacle = 0;
         setBackground(Color.red);
    }
    
    /**
     * Mutator to set the block as an obstacle.
     */
    public void setObstacle() {
        obstacle = 1;
        agent = 0;
        pathCost = Double.POSITIVE_INFINITY;
        setBackground(Color.black);
    }
    
    /**
     * Mutator to set the block as the one to carry the object goal.
     */
    public void setBlockObjectGoal() {
        goal = 1;
        agent = 0;
        obstacle = 0;
        setBackground(Color.yellow);
    }
    
    /**
     * Mutator to set the block as traversed before object pick up.
     */
    public void setTraversed(){
        traversed = 1;
    }
    
    /**
     * Mutator to set the block as traversed after object pick up.
     */
    public void setHomeTraversed() {
        homeTraversed = 1;
    }
    
    /**
     * Mutator to set the block as the block where the robot agent died.
     */
    public void setRobotDeathBlock() {
        robotDiedHere = 1;
        repaint();
    }
    
    public int getXPos(){
        return xPos;
    }
    
    public int getYPos(){
        return yPos;
    }
    
    /**
     * Paints the block to it's appropriate state as an obstacle, home, goal, agent, or traversed.  
     * @param g the Java Graphics Context that helps paint the block in the grid
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2 = (Graphics2D)g;
        //Render the paintings
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(
            RenderingHints.KEY_COLOR_RENDERING,
            RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        //it has not been stepped on
        if(traversed == 0 && homeTraversed == 0) {
            if(obstacle == 1) {
                setBackground(Color.black);
            }
            else if(agent == 1) {
                setBackground(Color.red);
            }
            else if(goal == 1) {
                setBackground(Color.yellow);
            }
            else if(home == 1) {
                setBackground(Color.green);
            }
            else {
                //do nothing, it's already a white block!
            }
        }
        //it has been stepped on on the way to the object
        else if(traversed == 1 && homeTraversed == 0){
            if(obstacle == 1) {
                setBackground(Color.black);
            }
            else if(agent == 1) {
                setBackground(Color.red);
            }
            else if(goal == 1) {
                setBackground(Color.yellow);
            }
            else if(home == 1) {
                setBackground(Color.green);
            }
            else if(iAmHere) {
                setBackground(new Color(255, 0, 244));    
            }
            else {
                setBackground(Color.white);
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(0, 0, this.getWidth()/2, this.getHeight()); //xLoc,yLoc, height, width
            }
        }
        //it has been stepped on on the way back home
        else if(traversed == 0 && homeTraversed == 1) {
            if(obstacle == 1) {
                setBackground(Color.black);
            }
            else if(agent == 1) {
                setBackground(Color.red);
            }
            else if(goal == 1) {
                setBackground(Color.yellow);
            }
            else if(home == 1) {
                setBackground(Color.green);
            }
            else if(iAmHere) {
                setBackground(new Color(255, 0, 244));    
            }
            else {
                setBackground(Color.white);
                g2.setColor(Color.DARK_GRAY); //for return home
                g2.fillRect(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());  //xLoc ,yLoc, height, width
            }
        }
        //it has been stepped on both ways
        else {
            if(obstacle == 1) {
                setBackground(Color.black);
            }
            else if(agent == 1) {
                setBackground(Color.red);
            }
            else if(goal == 1) {
                setBackground(Color.yellow);
            }
            else if(home == 1) {
                setBackground(Color.green);
            }
            else if(iAmHere) {
                setBackground(new Color(255, 0, 244));    
            }
            else {
                g2.setColor(Color.LIGHT_GRAY); //for to object
                g2.fillRect(0, 0, this.getWidth()/2, this.getHeight()); //xLoc,yLoc, height, width
                g2.setColor(Color.DARK_GRAY); //for return home
                g2.fillRect(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());  //xLoc ,yLoc, height, width
            }
        }
        //doesn't need to be in above condition constraint
        if(robotDiedHere == 1){
            g2.setColor(Color.black);
            g2.drawLine(this.getWidth()/6, this.getHeight()/6, 5*this.getWidth()/6, 5*this.getHeight()/6); //initX, initY, endX, endY
            g2.drawLine(this.getWidth()/6, 5*this.getHeight()/6, 5*this.getWidth()/6, this.getHeight()/6); //initX, initY, endX, endY
        }
    }
}
