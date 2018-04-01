import javax.swing.*;
/**
 * The Search Simulator starts here as the JFrame is constructed and takes in the 
 * two JPanels that have the options to manipulate a search grid and display it. 
 * @author Andrew C. Haynes
 */
public class Head extends JFrame{

    //JPanel that holds the grid and grid options for the search simulator
    public Environment env;
    
    /**
     * Creates a JFrame and controls the search simulator from here. 
     */
    public Head(){
        setTitle("Search Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        env = new Environment();
        getContentPane().add(env);
        pack();
        setVisible(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Search Simulator starts here!
        Head head = new Head();
    } 
}
