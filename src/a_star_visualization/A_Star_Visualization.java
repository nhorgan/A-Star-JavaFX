//Imports and Packages:
package a_star_visualization;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import java.util.ArrayList;


//******************************************************************************
//Entry Point Class:
//Contains Logic For A*, Building the Window, and Handling Data: 
//******************************************************************************
public class A_Star_Visualization  extends Application {

  
//******************************************************************************
// Member Vars of Class:
// Note: A_Star_Visualization cannot be instantiated:
//******************************************************************************    
    
    //Main Grid:
    public GridPane root;
    
    //Constants Grid:
    public int NUMBER_OF_ROWS;
    public int NUMBER_OF_COLS;
    
    //Constants Screen:
    public int SCENE_X_SIZE;
    public int SCENE_Y_SIZE;
    
    //Constants Tile Size:
    public int Y_PIXEL_PER_TILE;
    public int X_PIXEL_PER_TILE;
    
    
    
    
    //Tracks Player Loc, used for A*
    public int Player_X_Pos;
    public int Player_Y_Pos;
    
    //Tracks Goal Loc, used for A*
    public int Goal_X_Pos;
    public int Goal_Y_Pos;
    
    
    //Changes the Map based on State:
    public Map_State ms;
    
    //JavaFX lacks an easy way to traverse a grid.
    //Adding Imageviews to this for easy access: 
    public ImageView[][] IVA;
    
    //A* Grid under the Hood:
    public Cell[][] CA;
    
    
    //
    public boolean placement;
    
    
    /*
    
        The goal node is denoted by node_goal and the source node is denoted by node_start
    
        We maintain two lists: OPEN and CLOSE:
        OPEN consists on nodes that have been visited but not expanded (meaning that sucessors have not been
        explored yet). This is the list of pending tasks.
    
    
        CLOSE consists on nodes that have been visited and expanded (sucessors have been explored already and
        included in the open list, if this was the case).
    
    */
    private ArrayList<Cell> Open;
    private ArrayList<Cell> Closed;
    
    
//******************************************************************************
//Entry Point Window, Calls launch:
//Hello World to Test Compilation:
//******************************************************************************    
    
    public static void main(String[] args) {
        
        System.out.println("Hello World");
        Application.launch(args);
    }


 
//******************************************************************************
//Launches the JavaFX window: 
//
//
//******************************************************************************    
    
   @Override
   public void start(Stage stage) throws FileNotFoundException 
   {

       //Constants for Grid: 
       NUMBER_OF_ROWS = 40; 
       NUMBER_OF_COLS = 40;
        
        
       
       //Instantiate all Grid Objects: Arrays are used to help control A* and Grid Graphics:
       IVA = new ImageView[NUMBER_OF_ROWS][NUMBER_OF_COLS]; 
       CA = new Cell[NUMBER_OF_ROWS][NUMBER_OF_COLS];
              
       //Grid Instantiation:
       root = new GridPane(); 
       root.setGridLinesVisible(true);
        
        
       //Boolean used to Control Placing of objects: Cannot Place Objects as A* runs:
       //Once finished, A* can be run again and objects placed is the goal:
       //TODO expand that functionality:
       placement = true;
       
       //Map State that controls what object is placed:
       ms = Map_State.PLACE_ROCK;
     
        
       
       //Controls window size:
       //I have other projects where you can read these values from a file or DB:
       //Might be useful to load them there instead of hardcoding: 
       SCENE_X_SIZE = 1000; 
       SCENE_Y_SIZE = 1000;
        
       
        //Constants for Movement: 
        //Pythag theorem:
        //Horizontal movement == vertical: 1 ^ 2 + 1 ^ 2 = c ^ 2
        // c = 2 * sqrt(2) approx 1.4:
        //Multiply by 10 for integer values:
        //DIAG_COST = 14;
        //HORI_COST = 10;
        
        
        //Unneeded, store as doubles:
        
        
        //Open and Closed ArrayLists:
        Open = new ArrayList();
        Closed = new ArrayList();
        
       
       //Column Constraints: Col Size needs to remain same: 
       for (int i = 0; i < NUMBER_OF_COLS; i++) {
           
           ColumnConstraints colConst = new ColumnConstraints();
           colConst.setPercentWidth(100.0 / NUMBER_OF_COLS); 
           root.getColumnConstraints().add(colConst);
        
       }
        
        
        
       //Row Constraints: Rows need to remain same size: 
       for (int i = 0; i < NUMBER_OF_ROWS; i++) {
           
           RowConstraints rowConst = new RowConstraints();
           rowConst.setPercentHeight(100.0 / NUMBER_OF_ROWS); 
           root.getRowConstraints().add(rowConst);         
        }
        
        
        
        
       //How large each tile is in pixels:
       X_PIXEL_PER_TILE = SCENE_X_SIZE/NUMBER_OF_ROWS;
       Y_PIXEL_PER_TILE = SCENE_Y_SIZE/NUMBER_OF_COLS;
        
        
       
       //Displays Data to the user via the console:
       System.out.println("A* Visual:");
       System.out.println("Up Arrow to Change Tile you can place");
       System.out.println("Enter to Run A*");
       
       System.out.println("X Pixels per Tile: " + X_PIXEL_PER_TILE);
       System.out.println("Y Pixels Per Tile: " + Y_PIXEL_PER_TILE);
       System.out.println("*****************************************");
        
       
       //Building the GUI Grid:
       Init_GUI_Grid();
        
       //Code that handles the A* Distance from Goal to Specific Tile:
       Calculate_Heuristic();
       
       ///Building the Scene: 
       Scene scene = new Scene(root, SCENE_X_SIZE, SCENE_Y_SIZE);
       
       
       //Setting up the Scene Keyboard Listeners: 
       scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                System.out.println("Changing Placement Mode:");
                switch(ms){
                    case PLACE_ROCK: ms = Map_State.PLACE_GOAL; break;
                    case PLACE_GRASS: ms = Map_State.PLACE_PLAYER; break;
                    case PLACE_PLAYER: ms = Map_State.PLACE_ROCK; break;
                    case PLACE_GOAL: ms = Map_State.PLACE_GRASS; break;
                    default: System.err.println("Error: Unsupported Enum"); System.exit(1);
                }
                System.out.println("New Map State: " + ms);
                System.out.println("*****************************************");
            }
            
            
            if(e.getCode() == KeyCode.ENTER){
                try {
                    Run_A_Star_Algo();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(A_Star_Visualization.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(A_Star_Visualization.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
       });
       
       
       //Show the Scene via the JavaFX window: 
       stage.setScene(scene);
       stage.setTitle("A* Visualization in JavaFX:"); 
       stage.show();
        
   
   
   }

//******************************************************************************
// Initializes the Grid, and underlying arrays that control A*.
// Also initializes mouse listeners that control placing tiles: 
//******************************************************************************
    public void Init_GUI_Grid() throws FileNotFoundException {
        
        for(int i = 0; i < NUMBER_OF_COLS; i++){
            for(int j = 0; j < NUMBER_OF_ROWS; j++){
                if(i == 0 && j == 0){
                    Player_X_Pos = 0;
                    Player_Y_Pos = 0;
                    InputStream Player = new FileInputStream("data\\Player.png");
                    ImageView IVP = new ImageView();
                    Image I = new Image(Player);
                    IVP.setImage(I);
                    IVP.setFitHeight(Y_PIXEL_PER_TILE);
                    IVP.setFitWidth(X_PIXEL_PER_TILE);
                    IVP.setOnMousePressed(eh -> {try {
                        Handle_Mouse_Press(IVP);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(A_Star_Visualization.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    root.add(IVP, i, j);
                    IVA[i][j] = IVP;
                    CA[i][j] = new Cell(i, j);
                    continue;
                }
                
                if(i == NUMBER_OF_COLS - 1 && j == NUMBER_OF_ROWS - 1){
                    Goal_X_Pos = NUMBER_OF_COLS - 1;
                    Goal_Y_Pos = NUMBER_OF_ROWS - 1;
                    InputStream Goal = new FileInputStream("data\\Goal.png");
                    ImageView IVG = new ImageView();
                    Image I = new Image(Goal);
                    IVG.setImage(I);
                    IVG.setFitHeight(Y_PIXEL_PER_TILE);
                    IVG.setFitWidth(X_PIXEL_PER_TILE);
                    IVG.setOnMousePressed(eh -> {try {
                        Handle_Mouse_Press(IVG);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(A_Star_Visualization.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    root.add(IVG, i, j);
                    IVA[i][j] = IVG;
                    CA[i][j] = new Cell(i, j);
                    continue;
                }
               
                
                ImageView IV = new ImageView();
                InputStream grass = new FileInputStream("data\\Grass.png");
                Image I = new Image(grass);
                IV.setImage(I);
                IV.setFitHeight(Y_PIXEL_PER_TILE);
                IV.setFitWidth(X_PIXEL_PER_TILE);
                IV.setOnMousePressed(eh -> {try {
                    Handle_Mouse_Press(IV);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(A_Star_Visualization.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                root.add(IV, i, j);
                IVA[i][j] = IV;
                CA[i][j] = new Cell(i, j);
            }
        
       }
        
    }
    
//******************************************************************************    
//Handles mouse presses: 
//******************************************************************************
    
    
    private void Handle_Mouse_Press(ImageView IV) throws FileNotFoundException {
        
        int x = GridPane.getColumnIndex(IV);
        int y = GridPane.getRowIndex(IV);
        System.out.println("Clicked On: X Coord: " + x + " Y Coord: " + y);
        
        //Prevent Map Modification while A* is running: 
        //Todo add option for allowing user to run A* while modifying map. 
        if(placement == false){return;}
        
        switch(ms){
            case PLACE_ROCK: Place_Rock(x, y, IV); break;
            case PLACE_GRASS: Place_Grass(x, y, IV); break;
            case PLACE_PLAYER: Place_Player(x, y, IV); break;
            case PLACE_GOAL: Place_Goal(x, y, IV); break;
            default: System.err.println("Error: Unsupported Enum"); System.exit(1);
        }
        
    }
    
    
//******************************************************************************
//Terrain Modifiers:
//******************************************************************************
    private void Place_Rock(int x, int y, ImageView IV) throws FileNotFoundException {
        
        if(x == Player_X_Pos && y == Player_Y_Pos){
            return;
        }
        
        if(x == Goal_X_Pos && y == Goal_Y_Pos){
            return;
        }
        
        CA[x][y].Set_Can_Traverse(false);
        InputStream Rock = new FileInputStream("data\\Rock.png");
        Image value = new Image(Rock);
        IV.setImage(value);    
    }

    private void Place_Grass(int x, int y, ImageView IV) throws FileNotFoundException {
        
        
        if(x == Player_X_Pos && y == Player_Y_Pos){
            return;
        }
        
        if(x == Goal_X_Pos && y == Goal_Y_Pos){
            return;
        }
        
        CA[x][y].Set_Can_Traverse(true);
        InputStream Grass = new FileInputStream("data\\Grass.png");
        Image value = new Image(Grass);
        IV.setImage(value);
        
    }

    private void Place_Goal(int x, int y, ImageView IV) throws FileNotFoundException {
        
        if(x == Player_X_Pos && y == Player_Y_Pos){
            return;
        }
        
        if(x == Goal_X_Pos && y == Goal_Y_Pos){
            return;
        }
        
        int tmp_x = Goal_X_Pos;
        int tmp_y = Goal_Y_Pos;
        
                
        System.out.println("Temp x: " + tmp_x);
        System.out.println("Tmp y: " + tmp_y);
        
        Goal_X_Pos = x;
        Goal_Y_Pos = y;
        CA[Goal_X_Pos][Goal_Y_Pos].Set_Can_Traverse(true);
        InputStream Goal = new FileInputStream("data\\Goal.png");
        Image value = new Image(Goal);
        IV.setImage(value);
        
        Place_Grass(tmp_x, tmp_y);
        Calculate_Heuristic();
        
    }

    private void Place_Player(int x, int y, ImageView IV) throws FileNotFoundException {
        
        if(x == Player_X_Pos && y == Player_Y_Pos){
            return;
        }
        
        if(x == Goal_X_Pos && y == Goal_Y_Pos){
            return;
        }
        
        int tmp_x = Player_X_Pos;
        int tmp_y = Player_Y_Pos;
        
        System.out.println("Temp x: " + tmp_x);
        System.out.println("Tmp y:" + tmp_y);
        
        //Change to new X, Y:
        Player_X_Pos = x;
        Player_Y_Pos = y;
        CA[Player_X_Pos][Player_Y_Pos].Set_Can_Traverse(true);
        InputStream P = new FileInputStream("data\\Player.png");
        Image value = new Image(P);
        IV.setImage(value);
        
        Place_Grass(tmp_x, tmp_y);
        Calculate_Heuristic();   
    }

//******************************************************************************
//Overloaded Function: 
//Called by Place Goal / Player: Can only be one Goal or Player on the stage:
//******************************************************************************    
    
    private void Place_Grass(int tmp_x, int tmp_y) throws FileNotFoundException {
        
        ImageView IV = getImageViewFromGridPane(tmp_x, tmp_y);
        InputStream Grass = new FileInputStream("data\\Grass.png");
        Image value = new Image(Grass);
        IV.setImage(value);
        CA[tmp_x][tmp_y].Set_Can_Traverse(true);
        
    
    }
    
//******************************************************************************
//Helper Function:
//******************************************************************************    
    
    public ImageView getImageViewFromGridPane(int col, int row) {
        return IVA[col][row];
    }
    
//******************************************************************************
//The Star of the Show: 
//A* Uses 2 Lists, Open and Closed:
//Using them, as well as data gathered from the Tiles (Distance between goal & Tile)
//A* can generate the shortest path to the Goal!
//Used in many cool games, like Starcraft :D
//******************************************************************************

    private void Run_A_Star_Algo() throws FileNotFoundException, InterruptedException {
        
       
       
        System.out.println("Executing A*");
        System.out.println("Check Yellow Path for Shortest Direction:");
        System.out.println("*****************************************");
        
        //Bookkeeping First:
        placement = false;
        Cell Start =    CA[Player_X_Pos][Player_Y_Pos];
        Cell End =      CA[Goal_X_Pos][Goal_Y_Pos];
        
        Open.clear();
        Closed.clear();
        
        Clear_G_Values();
        Clear_F_Values();
        Clear_Parents();
        Clear_OnPath();
        
        //A* starts here:
        //SOURCE DOCUMENT: 
        //https://mat.uab.cat/~alseda/MasterOpt/AStar-Algorithm.pdf
        //https://www.researchgate.net/profile/Peter_Hufnagl/publication/232085273/figure/fig8/AS:214001028997142@1428033229986/A-search-algorithm-Pseudocode-of-the-A-search-algorithm-operating-with-open-and-closed.png
        
        //1. Put node_start in the OPEN list with f(node_start) = h(node_start) (initialization)
        Start.Set_F_Cost(Start.Get_H_Cost());
        Open.add(Start);
        
        //2. while the OPEN list is not empty
        while(!Open.isEmpty()){
            
            //3: Set F cost in Open List:
            //3A: Find Cell to Process:
            Set_F_Cost_In_Open_List();
            int CellToProcessIndex = Find_Minimum_Index();
            Cell node_curr = Open.get(CellToProcessIndex);
            
            
            
            //5. if node_current is node_goal we have found the solution; break
            if(node_curr.Get_X() == Goal_X_Pos && node_curr.Get_Y() == Goal_Y_Pos){break; }
            

            
            //6. Generate each state node_successor that come after node_current
            ArrayList<Cell> SuccessorList = Generate_Successor_List(node_curr.Get_X(), node_curr.Get_Y());
            //7. for each node_successor of node_current
            for(int j = 0; j < SuccessorList.size(); j++){
                
                
                Cell cell_child = SuccessorList.get(j);
                boolean isInOpen = Check_Is_In_Open(cell_child);
                boolean isInClosed = Check_Is_In_Closed(cell_child);
                
                //8. Set successor_current_cost = g(node_current) + w(node_current, node_successor)
                double child_curr_cost = node_curr.Get_G_Cost() + Get_Dist_Between_Nodes(node_curr, cell_child);

                if(isInOpen){
                    if(cell_child.Get_G_Cost() <= child_curr_cost){
                        continue;
                    }
                }
                
                if(isInClosed){
                    if(cell_child.Get_G_Cost() <= child_curr_cost){ continue;}
                    Closed.remove(cell_child);
                    Open.add(cell_child);
                }
                
                if(!isInOpen && !isInClosed){
                    Open.add(cell_child);
                    double h_dist = Get_Dist_Between_Nodes(End, cell_child);
                    cell_child.Set_H_Cost(h_dist);
                }
                
                cell_child.Set_G_Cost(child_curr_cost);
                cell_child.Set_On_Path(true);
                cell_child.Set_Parent(node_curr);

                
            }
            
            Open.remove(node_curr);
            Closed.add(node_curr);
        }
                
            
               
        
        
        Draw_Path();
        placement = true;
    }
    
//******************************************************************************
//Calculates the Heuristic for each Cell: 
//Ran after Goal or Player is placed: 
//Really only needs to be run after goal, but better safe then sorry: 
//******************************************************************************       
    
    public void Calculate_Heuristic(){

        System.out.println("Recalculating Heuristics:");
        //
        for(int i = 0; i < NUMBER_OF_ROWS; i++){
            for(int j = 0; j < NUMBER_OF_COLS; j++){
                
                
                //Distance Formula:
                //SQRT(x_dist^2 + y_dist^2);
                double hc = 0;
                
                double x_dist = Goal_X_Pos - i;
                double y_dist = Goal_Y_Pos - j;
                
                x_dist = x_dist * x_dist;
                y_dist = y_dist * y_dist;
                
                double inner = x_dist + y_dist;
                
                hc = Math.sqrt(inner);
                
                CA[i][j].Set_H_Cost(hc);
                System.out.println(CA[i][j].toString());
                System.out.println("*****************************************");
            }
        }  
    }
        
//******************************************************************************
// Ensures the F values are Cleared before any A* is done:
// F score value is calculated by weighting distance to end node and distance from current node:
// F score allows A* to pick a best node to try and look: 
//******************************************************************************
    private void Clear_F_Values() {
    
        for(int i = 0; i < NUMBER_OF_ROWS; i++){
            for(int j = 0; j < NUMBER_OF_COLS; j++){
                CA[i][j].Set_F_Cost(0.0);
            }
        }
    }
    
//******************************************************************************
//Clears G Cost: 
//Any leftover values need to be purged so algo can start fresh: 
//******************************************************************************
    private void Clear_G_Values() {
    
        for(int i = 0; i < NUMBER_OF_ROWS; i++){
            for(int j = 0; j < NUMBER_OF_COLS; j++){
                CA[i][j].Set_G_Cost(0.0);
            }
        }
    }    
    
//******************************************************************************
//Clears Parents: 
//Any leftover values need to be purged so algo can start fresh: 
//******************************************************************************    
    
    private void Clear_Parents() {
        for(int i = 0; i < NUMBER_OF_ROWS; i++){
            for(int j = 0; j < NUMBER_OF_COLS; j++){
                CA[i][j].Set_Parent(null);
            }
        }
    }
    
    private void Clear_OnPath() {
        for(int i = 0; i < NUMBER_OF_ROWS; i++){
            for(int j = 0; j < NUMBER_OF_COLS; j++){
                CA[i][j].Set_On_Path(false);
            }
        }
    }
    
//******************************************************************************
//Finds the Cell with the Smallest F value in the open list:
//******************************************************************************
    
    private int Find_Minimum_Index() {
        
        int retval = 0;
        double data = Open.get(0).Get_F_Cost();
        for(int i = 0; i < Open.size(); i++){
            if(Open.get(i).Get_F_Cost() < data){
                retval = i;
                data = Open.get(i).Get_F_Cost();
            }
        }    
        return retval;
    }
    
    
//******************************************************************************
//Checks if a Node is in Open or Closed List:
//******************************************************************************    
    
    private boolean Check_Is_In_Open(Cell C){
        
        boolean retval = false;
        int x = C.Get_X();
        int y = C.Get_Y();
        
        for(int i = 0; i < Open.size(); i++){
            if(x == Open.get(i).Get_X() && y == Open.get(i).Get_Y()){
                return true;
            }
        }
        
        return retval;
    }
    
        
    private boolean Check_Is_In_Closed(Cell C) {
    
        boolean retval = false;
        int x = C.Get_X();
        int y = C.Get_Y();
        
        for(int i = 0; i < Closed.size(); i++){
            if(x == Closed.get(i).Get_X() && y == Closed.get(i).Get_Y()){
                return true;
            }
        }
        
        return retval;
    
    }
    
//******************************************************************************
// Generates an Arraylist of Children Cells from a Parent we are working on:
// This is where traversal booleans come in, as well as OOB checks:
//******************************************************************************
    private ArrayList<Cell> Generate_Successor_List(int x, int y) {
        
        //List we are returning:
        ArrayList AL = new ArrayList();
        
        int tmp_x = 0;
        int tmp_y = 0;
        //8 possible descendents, but watch for OOB 
        //Also watch for non traversable terrain:
        //Recall that javafx does Y down, whereas we do normal coords y up:
        //1     //-1, +1
        //2     //+0, +1
        //3     //+1, +1
        //4     //-1, +0
        //5     //+1, +0
        //6     //-1, -1
        //7     //+0, -1
        //8     //+1, -1
        
        
        
        
        //1
        tmp_x = x - 1;
        tmp_y = y + 1;
        
        if(tmp_x >= 0 && tmp_x <= NUMBER_OF_ROWS - 1){
            if(tmp_y >= 0 && tmp_y <= NUMBER_OF_COLS - 1){
                if(CA[tmp_x][tmp_y].Get_Can_Traverse()){
                    AL.add(CA[tmp_x][tmp_y]);
                }
            }
        }
        

        //2
        tmp_x = x;
        tmp_y = y + 1;
        
        if(tmp_x >= 0 && tmp_x <= NUMBER_OF_ROWS - 1){
            if(tmp_y >= 0 && tmp_y <= NUMBER_OF_COLS - 1){
                if(CA[tmp_x][tmp_y].Get_Can_Traverse()){
                    AL.add(CA[tmp_x][tmp_y]);
                }
            }
        }
        
        //3
        tmp_x = x + 1;
        tmp_y = y + 1;
        
        if(tmp_x >= 0 && tmp_x <= NUMBER_OF_ROWS - 1){
            if(tmp_y >= 0 && tmp_y <= NUMBER_OF_COLS - 1){
                if(CA[tmp_x][tmp_y].Get_Can_Traverse()){
                    AL.add(CA[tmp_x][tmp_y]);
                }
            }
        }
        
        //4
        tmp_x = x - 1;
        tmp_y = y;
        
        if(tmp_x >= 0 && tmp_x <= NUMBER_OF_ROWS - 1){
            if(tmp_y >= 0 && tmp_y <= NUMBER_OF_COLS - 1){
                if(CA[tmp_x][tmp_y].Get_Can_Traverse()){
                    AL.add(CA[tmp_x][tmp_y]);
                }
            }
        }
        
        //5
        tmp_x = x - 1;
        tmp_y = y - 1;
        
        if(tmp_x >= 0 && tmp_x <= NUMBER_OF_ROWS - 1){
            if(tmp_y >= 0 && tmp_y <= NUMBER_OF_COLS - 1){
                if(CA[tmp_x][tmp_y].Get_Can_Traverse()){
                    AL.add(CA[tmp_x][tmp_y]);
                }
            }
        }
        
        //6
        tmp_x = x;
        tmp_y = y - 1;
        
        if(tmp_x >= 0 && tmp_x <= NUMBER_OF_ROWS - 1){
            if(tmp_y >= 0 && tmp_y <= NUMBER_OF_COLS - 1){
                if(CA[tmp_x][tmp_y].Get_Can_Traverse()){
                    AL.add(CA[tmp_x][tmp_y]);
                }
            }
        }
        
        //7
        tmp_x = x + 1;
        tmp_y = y - 1;
        
        if(tmp_x >= 0 && tmp_x <= NUMBER_OF_ROWS - 1){
            if(tmp_y >= 0 && tmp_y <= NUMBER_OF_COLS - 1){
                if(CA[tmp_x][tmp_y].Get_Can_Traverse()){
                    AL.add(CA[tmp_x][tmp_y]);
                }
            }
        }
        
        //8
        tmp_x = x + 1;
        tmp_y = y;
        
        if(tmp_x >= 0 && tmp_x <= NUMBER_OF_ROWS - 1){
            if(tmp_y >= 0 && tmp_y <= NUMBER_OF_COLS - 1){
                if(CA[tmp_x][tmp_y].Get_Can_Traverse()){
                    AL.add(CA[tmp_x][tmp_y]);
                }
            }
        }
        
        //End of Method:
        return AL;        
    
    }
   
//******************************************************************************
//Distance between 2 nodes: 
//Used for Parent Child Distance Calc:
//******************************************************************************
    private double Get_Dist_Between_Nodes(Cell node_curr, Cell cs) {

        int x_1 = node_curr.Get_X();
        int y_1 = node_curr.Get_Y();
        int x_2 = cs.Get_X();
        int y_2 = cs.Get_Y();
        
        int x_dist = x_1 - x_2;
        int y_dist = y_1 - y_2;
        
        x_dist = x_dist * x_dist;
        y_dist = y_dist * y_dist;
        
        double inner = x_dist + y_dist;
        return Math.sqrt(inner);
        
    }
//******************************************************************************
//Sets the fCost in the OpenList; 
//FCost is Used to find new parents:
//******************************************************************************
    
    private void Set_F_Cost_In_Open_List() {

        for(int i = 0; i < Open.size(); i++){
            Cell C = Open.get(i);
            double h = C.Get_H_Cost();
            double g = C.Get_G_Cost();
            double f = h + g;
            C.Set_F_Cost(f);
        }
    
    }
    
//******************************************************************************
//Draws the Path for the GUI:
//
//******************************************************************************
    
    private void Draw_Path() throws FileNotFoundException, InterruptedException {
    
        //Linked List Traversal: 
        Cell P = CA[Goal_X_Pos][Goal_Y_Pos];
        P = P.Get_Parent();
        if(P == null){System.out.println("NO PATH FOUND! :D");}
        while(P != null){
            int x = P.Get_X();
            int y = P.Get_Y();
            ImageView IV = IVA[x][y];
            InputStream Path = new FileInputStream("data\\Path.png");
            Image value = new Image(Path);
            IV.setImage(value);
            P = P.Get_Parent();
        }
    }
    
//******************************************************************************
} //End of Class:
//******************************************************************************
