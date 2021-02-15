    
//******************************************************************************
//Imports and Packages: 
//******************************************************************************

package a_star_visualization;
import static a_star_visualization.A_Star_Visualization.Calculate_Heuristic;
import static a_star_visualization.A_Star_Visualization.Goal_X_Pos;
import static a_star_visualization.A_Star_Visualization.Goal_Y_Pos;
import static a_star_visualization.A_Star_Visualization.Place_Grass_In_Old_Location;
import static a_star_visualization.A_Star_Visualization.Player_X_Pos;
import static a_star_visualization.A_Star_Visualization.Player_Y_Pos;
import static a_star_visualization.Map_State.PLACE_GRASS;
import static a_star_visualization.Map_State.PLACE_ROCK;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



    
//*****************************************************************************
// Class Definition:
// Class Designed to Control A* behind the scenes:
// Contains references to its x coord, y coord, parent that traversed it, & distance values:
//******************************************************************************

public class Cell {
    
//******************************************************************************
//Class Vars:
//******************************************************************************
    
    //Location in Grid: 
    private int x_loc;
    private int y_loc;
    
    
    //Used to Draw Path: 
    private Cell parent;
    
    //Cost values used to evaluate path:
    private double hCost;
    private double fCost;    
    private double gCost;
    
    
    //Booleans to Control Algo:
    private boolean onPath;
    private boolean canTraverse;
    
    
    //Graphics to Display in JavaFX:
    private Image I;
    private ImageView IV;
   
    //Used to determine if map can be modified:
    //map cannot be modified during A*;
    public static boolean placement;
    
        
    //Changes the Map based on State:
    public static Map_State ms;
    
    
    
//******************************************************************************
//Constructor: 
//******************************************************************************
    public Cell(int x, int y){
        
        //X, Y Coord:
        this.Set_X(x);
        this.Set_Y(y);
        
        //Heuristic Calculators:
        hCost = 0;
        gCost = 0;
        fCost = 0;
        parent = null;
        
        //Draws Yellow Line:
        onPath = false;
        
        //For Rocks: 
        canTraverse = true;
        
        //For Graphics: 
        //this.I = new Image();
        this.IV = new ImageView();
    }
    
//******************************************************************************
//Getters: 
//******************************************************************************
    
    
    public int Get_X(){
        return this.x_loc;
    }
    
    public int Get_Y(){
        return this.y_loc;
    }
    
    public Cell Get_Parent(){
        return this.parent;
    }
    
    public double Get_G_Cost() {
        return this.gCost;
    }
    
    public double Get_H_Cost(){
        return this.hCost;
    }
    
    
    public double Get_F_Cost(){
        return this.fCost;
    }
    
    public boolean Get_On_Path(){
        return this.onPath;
    }
    
    public boolean Get_Can_Traverse(){
        return this.canTraverse;
    }
    
    public Image Get_Image(){
        return this.I;
    }
    
    public ImageView Get_ImageView(){
        return this.IV;
    }
    
//******************************************************************************
//Setters: 
//******************************************************************************
    
    public final void Set_X(int x){
        this.x_loc = x;
    }
    
    public final void Set_Y(int y){
        this.y_loc = y;
    }
    
    public void Set_Parent(Cell parent){
        this.parent = parent;
    }
    
    public void Set_H_Cost(double h){
        this.hCost = h;
    }
    
    public void Set_F_Cost(double f){
        this.fCost = f;
    }
    
    
    public void Set_On_Path(boolean op){
        this.onPath = op;
    }
    
    public void Set_Can_Traverse(boolean ct){
        this.canTraverse = ct;
    }
    
    public void Set_G_Cost(double successor_curr_cost) {
        this.gCost = successor_curr_cost;
    }
    
    public void Set_Image(Image I){
        this.I = I;
    }
    
    public void Update_ImageView(){
        this.IV.setImage(this.I);
    }
    
    
//******************************************************************************
//To String For Debug Purposes:
//******************************************************************************
    
    
    @Override
    public String toString(){
        return "Cell Stats: X Coord: " + this.x_loc + " Y Coord: " + this.y_loc + " H Cost: " + this.hCost;
    }

    
    
//******************************************************************************
//Sets the Constraints so the Images can fit the grid view:
//******************************************************************************
    public void Set_Constraints_For_IV(int x, int y) {
        IV.setFitWidth(x);
        IV.setFitHeight(y);
    }
    
//******************************************************************************
//Creates a Listener for the ImageView: 
//******************************************************************************
    
    public void Create_Mouse_Listener() {
        
        this.Get_ImageView().setOnMousePressed(eh -> {try {
            Cell.Handle_Mouse_Press(this);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, null, ex);
            }       
        }); 
    }
    
    
//******************************************************************************    
//Handles mouse presses: 
//******************************************************************************
    
    
    public static void Handle_Mouse_Press(Cell C) throws FileNotFoundException {
        
        int x = C.Get_X();
        int y = C.Get_Y();
        System.out.println("Clicked On: X Coord: " + x + " Y Coord: " + y);
        
        //Prevent Map Modification while A* is running: 
        //Todo add option for allowing user to run A* while modifying map. 
        if(Cell.placement == false){return;}
        
        switch(Cell.ms){
            case PLACE_ROCK: Place_Rock(C); break;
            case PLACE_GRASS: Place_Grass(C); break;
            case PLACE_PLAYER: Place_Player(C); break;
            case PLACE_GOAL: Place_Goal(C); break;
            default: System.err.println("Error: Unsupported Enum"); System.exit(1);
        }
        
    }
    
    
//******************************************************************************
//Terrain Modifiers:
//******************************************************************************
    private static void Place_Rock(Cell C) throws FileNotFoundException {

        int x = C.Get_X();
        int y = C.Get_Y();
        
        if(x == Player_X_Pos && y == Player_Y_Pos){
            return;
        }
        
        if(x == Goal_X_Pos && y == Goal_Y_Pos){
            return;
        }
        
        C.Set_Can_Traverse(false);
        InputStream Rock = new FileInputStream("data\\Rock.png");
        Image value = new Image(Rock);
        C.Set_Image(value);
        C.Update_ImageView();
    }

    private static void Place_Grass(Cell C) throws FileNotFoundException {
        
        int x = C.Get_X();
        int y = C.Get_Y();
        if(x == Player_X_Pos && y == Player_Y_Pos){
            return;
        }
        
        if(x == Goal_X_Pos && y == Goal_Y_Pos){
            return;
        }
        
        C.Set_Can_Traverse(true);
        InputStream Grass = new FileInputStream("data\\Grass.png");
        Image value = new Image(Grass);
        C.Set_Image(value);
        C.Update_ImageView();
        
    }

    private static void Place_Goal(Cell C) throws FileNotFoundException {
        
        int x = C.Get_X();
        int y = C.Get_Y();
        
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
        
        
        InputStream Goal = new FileInputStream("data\\Goal.png");
        Image value = new Image(Goal);
        C.Set_Image(value);
        C.Update_ImageView();
        C.Set_Can_Traverse(true);
        Place_Grass_In_Old_Location(tmp_x, tmp_y);
        
        
        Calculate_Heuristic();
        
    }

    private static void Place_Player(Cell C) throws FileNotFoundException {
        
        int x = C.Get_X();
        int y = C.Get_Y();
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
        InputStream P = new FileInputStream("data\\Player.png");
        Image value = new Image(P);
        C.Set_Image(value);
        C.Update_ImageView();
        C.Set_Can_Traverse(true);
        Place_Grass_In_Old_Location(tmp_x, tmp_y);
        Calculate_Heuristic();   
    }

    
    
    
//******************************************************************************
} //End of Class:
//******************************************************************************