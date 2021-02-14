    
//******************************************************************************
//Imports and Packages: 
//******************************************************************************

package a_star_visualization;



    
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
    private int x;
    private int y;
    
    
    //Used to Draw Path: 
    private Cell parent;
    
    //Cost values used to evaluate path:
    private double hCost;
    private double fCost;    
    private double gCost;
    
    
    //Booleans to Control Algo:
    private boolean onPath;
    private boolean canTraverse;
    
    
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
    }
    
    
    
//******************************************************************************
//Getters: 
//******************************************************************************
    
    
    public int Get_X(){
        return this.x;
    }
    
    
    
    public int Get_Y(){
        return this.y;
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
    
//******************************************************************************
//Setters: 
//******************************************************************************
    
    public final void Set_X(int x){
        this.x = x;
    }
    
    public final void Set_Y(int y){
        this.y = y;
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
    
//******************************************************************************
//To String For Debug Purposes:
//******************************************************************************
    
    
    @Override
    public String toString(){
        return "Cell Stats: X Coord: " + this.x + " Y Coord: " + this.y + " H Cost: " + this.hCost;
    }

    
    
//******************************************************************************
} //End of Class:
//******************************************************************************