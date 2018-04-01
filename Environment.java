import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
/**
 * JPanel that controls the board and input options from the user when trying out
 * the many searches the robot agent can do. 
 * @author Andrew C. Haynes
 */
public class Environment extends JPanel {
    
    //Searching Agent
    public Agent agent;
    
    //Grid for Board of Environment
    public Block[][] grid;
    //Length of Grid
    public int length;
    //Width of Grid
    public int width;
    //Obstacle Percentage
    public int gObstacle;
    //Agent Percentage
    public int gAgent;
    
    //JPanels of Environment
    public JPanel option;
    public JPanel board;
    
    //Option's Components
    public JTextField gridLengthSize;
    public JTextField gridWidthSize;
    public JTextField gridObstaclePercentage;
    public JTextField gridAgentPercentage;
    public JButton createGrid;
    public JButton saveGrid;
    public JButton loadGrid;
    public JButton start;
    public JButton help;
    
    //Action - Determines if Search Agent can act or not
    public int action;

    /**
     * Constructor for Environment and it puts an operational Option Panel 
     * and Board Panel on the Environment Panel.
     */
    public Environment() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800,400));
        this.initializeOption();
        add(option, BorderLayout.WEST);
        this.initializeBoard();
        add(board, BorderLayout.CENTER);
        action = 0;
        
        //action listener for create grid button, it sets the state for searching algorithm
        createGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gridRemoval();
                action = initializeGrid();
                repaint();
                board.revalidate();
                board.repaint();
                if(action == 1) {
                    //start a new agent here since a new board exists!
                    agent = new Agent(grid, length, width);
                    agent.startPosition();
                    randomObjectGoalBlock(); //creates a random location to have the object
                }
                
            }
        });
        //action listener for start button, if state is "1" from create grid, initiate search
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(action == 1) {
                    //let parameter determine which search algorithm to initiate
                    Object test = searchOptions();
                    agent.startSearch(test);
                    action = 0;
                    //clearSearchedGrid(); //fix this nice feature later
                }
                else {
                    JOptionPane.showMessageDialog(null, "Please create or load a grid.");
                }
            }
        });
        //action listener for help button. Upon click, a Key Pop-Up will appear. 
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel helpPanel = new JPanel();
                helpPanel.setLayout(new GridLayout(9,2,10,10)); //GridLayout(row, column, horizontalGap, verticalGap);
                //Column 1
                JLabel key = new JLabel("<html><u>KEY</u></html>");
                JLabel startEnd = new JLabel("Start/End: ");
                JLabel notCross = new JLabel("Not Traversed: ");
                JLabel beforeCross = new JLabel("<html>Traversed Before<br>Object Pick-up: </html>");
                JLabel afterCross = new JLabel("<html>Traversed After<br>Object Pick-up: </html>");
                JLabel baCross = new JLabel("<html>Traversed Before and After<br>Object Pick-up: </html>");
                JLabel ageBlock = new JLabel("Agent: ");
                JLabel rDeath = new JLabel("Robot Death: ");
                JLabel gBlock = new JLabel("Contains Goal Object: ");
                //Column 2
                Block startBlock = new Block(0,0);
                startBlock.setHome();
                Block defaultBlock = new Block(0,0);
                Block beforeBlock = new Block(0,0);
                beforeBlock.setTraversed();
                Block afterBlock = new Block(0,0);
                afterBlock.setHomeTraversed();
                Block baBlock = new Block(0,0);
                baBlock.setTraversed();
                baBlock.setHomeTraversed();
                Block agentBlock = new Block(0,0);
                agentBlock.setAgent();
                Block robotDBlock = new Block(0,0);
                robotDBlock.setAgent();
                robotDBlock.setRobotDeathBlock();
                Block goalBlock = new Block(0,0);
                goalBlock.setBlockObjectGoal();
                //It's horizontal, left-to-right for grid layout orientation
                helpPanel.add(key);
                helpPanel.add(new JPanel());
                helpPanel.add(startEnd);
                helpPanel.add(startBlock);
                helpPanel.add(notCross);
                helpPanel.add(defaultBlock);
                helpPanel.add(beforeCross);
                helpPanel.add(beforeBlock);
                helpPanel.add(afterCross);
                helpPanel.add(afterBlock);
                helpPanel.add(baCross);
                helpPanel.add(baBlock);
                helpPanel.add(ageBlock);
                helpPanel.add(agentBlock);
                helpPanel.add(rDeath);
                helpPanel.add(robotDBlock);
                helpPanel.add(gBlock);
                helpPanel.add(goalBlock);
                JOptionPane.showMessageDialog(null, helpPanel, "Help Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        saveGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //checks if grid is even there. 
                if(length > 0 && width > 0) {
                    saveThisGrid();
                }
            }
        });
        
        /**
         * removes the current grid and then initializes a new grid IF
         * the file selected is in the right grid file format.
         * If it is, the agent is created and is set at the home block.
         * Action is set 1 so that the start button will allow a search algorithm to run. 
         */
        loadGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //set up error case for grid in initGrid
                gridRemoval();
                repaint();
                int state = initGridFromLoad();
                repaint();
                if(state == 1) {
                    agent = new Agent(grid, length, width); 
                    agent.setStartPosition();
                    action = 1;
                }
            }
        });
        
    }
    
    /**
     * Initializes the Option Section of the Environment. 
     * Contains the obs %, agent %, length of grid and width of grid inputs as well
     * as the start, create grid, load grid, and help button. 
     */
    public void initializeOption(){
        option = new JPanel();
        option.setPreferredSize(new Dimension(120,180));
        option.setBackground(Color.white);
        JLabel gridL = new JLabel("Grid's Length");
        gridLengthSize = new JTextField(5);
        gridLengthSize.setText("5");
        JLabel gridW = new JLabel("Grid's Width");
        gridWidthSize = new JTextField(5);
        gridWidthSize.setText("5");
        JLabel gridO = new JLabel("Grid's Obstacle %");
        gridObstaclePercentage = new JTextField(5);
        gridObstaclePercentage.setText("20");
        JLabel gridA = new JLabel("Grid's Agent %");
        gridAgentPercentage = new JTextField(5);
        gridAgentPercentage.setText("10");
        createGrid = new JButton("Create Grid");
        saveGrid = new JButton("Save Grid");
        loadGrid = new JButton("Load Grid");
        start = new JButton("Start");
        help = new JButton("Help");
        length = 0;
        width = 0;
        gObstacle = 0;
        gAgent = 0;
        option.add(gridL);
        option.add(gridLengthSize);
        option.add(gridW);
        option.add(gridWidthSize);
        option.add(gridO);
        option.add(gridObstaclePercentage);
        option.add(gridA);
        option.add(gridAgentPercentage);
        option.add(createGrid);
        option.add(saveGrid);
        option.add(loadGrid);
        option.add(start);
        option.add(help);
    }

    /**
     * Creates a JPanel for the center of the Environment that holds the grid
     */
    public void initializeBoard(){
        board = new JPanel();
        board.setPreferredSize(new Dimension(100,100));
        board.setBackground(Color.white);
    }
    
    /**
     * Used when "Create Grid" button is clicked.
     * Returns 1 if grid parameters are legal and grid is created.
     * This is called in the action listener for "Create Grid" Button.
     * @return returnValue this value determines if grid exists or not
     */
    public int initializeGrid() {
        int returnValue = 0;
        /**
         * Error check that the user input a value and not an empty string, if an
         * empty string was there, don't create a grid!
         */
        if(gridLengthSize.getText().equals(""))
            length = 0;
        else
            length = Integer.parseInt(gridLengthSize.getText());
        if(gridWidthSize.getText().equals(""))
            width = 0;
        else
            width = Integer.parseInt(gridWidthSize.getText());
        if(gridObstaclePercentage.getText().equals(""))
            gObstacle = 0;
        else
            gObstacle = Integer.parseInt(gridObstaclePercentage.getText());
        if(gridAgentPercentage.getText().equals(""))
            gAgent = 0;
        else
            gAgent = Integer.parseInt(gridAgentPercentage.getText());
        //Do this error check!
        if(length < 3 || width < 3) {
            JOptionPane.showMessageDialog(null, "Please insert a value.\nLength > 2\nWidth > 2");
            //There is no performance gain for doing a 1x1 or 2x2 matrix
        }
        //set lower and upper limit percentages respectively: Obstacles -> 0 to 20, Agents -> 0 to 10
        //Percentages of blocks being obstacles or agents in grid
        else if((gObstacle < 0 || gObstacle > 20) || (gAgent < 0 || gAgent > 10)) { 
            JOptionPane.showMessageDialog(null, "Please insert a value.\nObstacle: 0 to 20\nAgent: 0 to 10");
        }
        else {
            /**
             * Create the grid and then create obs % and agent % of blocks in the grid!
             */
            grid = new Block[length][width]; 
            double obsChance = (double)gObstacle/100;
            double ageChance = (double)gAgent/100;
            int numObs = (int)Math.floor((length*width)*obsChance);
            int numAge = (int)Math.floor((length*width)*ageChance);
            //creates all of the blocks in the grid and then adds them to the board
            board.setLayout(new GridLayout(length, width));
            for(int i = 0; i < length; i++) {
                for(int j = 0; j < width; j++) {
                    grid[i][j] = new Block(i,j);
                    board.add(grid[i][j]);
                }
            }
            //creates the number of obstacles in accordance to obstacle per cent chance
            for(int i = 0; i < numObs; i++ ) {
                boolean done = false;
                while(!done) {    
                    Random rand1 = new Random();
                    Random rand2 = new Random();
                    int x = rand1.nextInt(length);
                    int y = rand2.nextInt(width);
                    if(grid[x][y].isBlockAgent() == 1 || grid[x][y].isBlockObstacle() == 1) {
                        //do nothing
                    }
                    else {
                        grid[x][y].setObstacle();
                        done = true;
                    }
                }
            }
            //creates the number of enemy agents in accordance to enemy agent per cent chance
            for(int i = 0; i < numAge; i++ ) {
                boolean done = false;
                while(!done) {    
                    Random rand1 = new Random();
                    Random rand2 = new Random();
                    int x = rand1.nextInt(length);
                    int y = rand2.nextInt(width);
                    if(grid[x][y].isBlockAgent() == 1 || grid[x][y].isBlockObstacle() == 1) {
                        //do nothing
                    }
                    else {
                        grid[x][y].setAgent();
                        done = true;
                    }
                }
            }
            //validates and repaints the board
            board.validate();
            returnValue = 1;
        }
        //if board was succesfully created, returns 1
        return returnValue;
    }
    
    /**
     * Allows user to select a grid file that follows the correct format to load 
     * a grid onto the GUI for searching purposes. 
     * If the user does not select a file, or a file that does not follow the 
     * correct format, the user will be notified of the action and nothing happens.
     * @return 
     */
    public int initGridFromLoad() {
        
        File loadedGrid = null;
        int yes = 0;
        int start = 0;
        int status = 0;
        ArrayList<String> gridList = new ArrayList<String>();
        JFileChooser choose = new JFileChooser(System.getProperty("user.dir"));
        int returnVal = choose.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            loadedGrid = choose.getSelectedFile();
            yes = 1;
        }
        else
            JOptionPane.showMessageDialog(null, "No file selected.");
        if(yes == 1) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(loadedGrid));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    gridList.add(line);
                }
                reader.close();
            } 
            catch (IOException x) {
                System.err.println(x);
            }
            //Check if loaded file follows grid list file format
            if(gridList.get(0).equals("BEGIN") && gridList.get(gridList.size()-1).equals("END")){
                int counter = 3;
                length = Integer.parseInt(gridList.get(1));
                width = Integer.parseInt(gridList.get(2));
                grid = new Block[length][width]; 
                //set board function here
                board.setLayout(new GridLayout(length,width));
                for(int i = 0; i < length; i++) {
                    for(int j = 0; j < width; j++) {
                        grid[i][j] = new Block(i, j); 
                        // 0|1|2|3|4 ::= 0 -> obstacle, 1 -> agent, 2 -> goal, 3-> home,  4-> free
                        if(Integer.parseInt(gridList.get(counter)) == 0) {
                            grid[i][j].obstacle = 1;
                            counter++;
                        }
                        else if(Integer.parseInt(gridList.get(counter)) == 1) {
                            grid[i][j].agent = 1;
                            counter++;
                        }
                        else if(Integer.parseInt(gridList.get(counter)) == 2) {
                            grid[i][j].setBlockObjectGoal();
                            counter++;
                        }
                        else if(Integer.parseInt(gridList.get(counter)) == 3) {
                            grid[i][j].home = 1;
                            counter++;
                        }
                        else if(Integer.parseInt(gridList.get(counter)) == 4) {
                            //do nothing
                            counter++;
                        }
                        else {
                            //it won't end up here. Hopefully. I doubt it. Too many error checks to stop program reaching here
                        }
                        //add the new block!
                        board.add(grid[i][j]);
                    }
                }
                board.validate();
                status = 1;
            }
            //File does not comply to grid file format, exit out
            else {
                JOptionPane.showMessageDialog(null, "This file does not comply to the grid file format."); 
            }
        }
        return status;
    }
    
    
    /**
     * Removes the previous grid on the board panel if one was already there. 
     */
    public void gridRemoval(){
        if(length > 0 && width > 0) {
            board.removeAll();
        }
    }

    /**
     * Selects a random block on the grid to be the goal block that has the object.
     * It does not care whether if block was originally agent or obstacle. Should I fix that? 
     */
    public void randomObjectGoalBlock() {
        boolean done = false;
        while(!done) {    
            Random rand1 = new Random();
            Random rand2 = new Random();
            int random1 = rand1.nextInt(((length))+0); //random1 is associated with length
            int random2 = rand2.nextInt(((width))+0); //random2 is associated with width
            if(grid[random1][random2].isBlockFree() == 1) {
                grid[random1][random2].setBlockObjectGoal();
                done = true;
            }
        }
    }
    
    /**
     * An option frame pops up and has search choices for the user. 
     * Build upon this as search options expand. 
     * @return 
     */
    public Object searchOptions(){
        String[] options = new String[]{"Random Search", 
            "Breadth-First Search [Tree Based]",
            "Breadth-First Search [Graph Based]",
            "Depth-First Search [Tree Based]",
            "Depth-First Search [Graph Based]",
            "Hill-Climbing Search", 
            "Random Restart Hill-Climbing Search",
            "Iterative Deepening Search", 
            "A* Search"};
        Object selectedSearch = JOptionPane.showInputDialog(null, "Select Search Algorithm", null,
                JOptionPane.INFORMATION_MESSAGE, 
                null, options, null);
        return selectedSearch;
    }
    
    /**
     * Opens a JFileBrowser and saves in a file the states of the grid. 
     * FILE FORMAT: 
     * length
     * width
     * 0|1|2|3|4 ::= 0 -> obstacle, 1 -> agent, 2 -> goal, 3-> home,  4-> free
     */
    public void saveThisGrid() {
        JFileChooser save = new JFileChooser(System.getProperty("user.dir"));
        int choice = save.showSaveDialog(null);
        if(choice == JFileChooser.APPROVE_OPTION) {
            File newGrid = new File(save.getSelectedFile().getName());
            try {
                PrintWriter sGrid = new PrintWriter(newGrid, "UTF-8");
                sGrid.println("BEGIN");
                sGrid.println(length);
                sGrid.println(width);
                for(int i = 0; i < length; i++){
                    for(int j = 0; j < width; j++) {
                        if(grid[i][j].isBlockObstacle() == 1)
                            sGrid.println(0);
                        else if(grid[i][j].isBlockAgent() == 1)
                            sGrid.println(1);
                        else if(grid[i][j].isBlockObjectGoal() == 1)
                            sGrid.println(2);
                        else if(grid[i][j].home == 1)
                            sGrid.println(3);
                        else if(grid[i][j].isBlockFree() == 1)
                            sGrid.println(4);
                    }
                }
                sGrid.println("END");
                sGrid.close();
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null,"The grid is not able to be saved. You somehow broke the save feature. I'm impressed.");
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "No grid file saved.");
        }
    }
    
    /**
     * Clears the searched grid for future grid uses. 
     */
    public void clearSearchedGrid() {
        for(int i = 0; i < agent.length; i++) {
            for(int j = 0; j < agent.width; j++) {
                if(agent.board[i][j].isBlockTraversed() == 1 || agent.board[i][j].isBlockHomeTraversed() == 1) {
                    agent.board[i][j].traversed = 0;
                    agent.board[i][j].homeTraversed = 0;
                }
            }
        }
    }
}
