/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minesweeperworking;





import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import static java.lang.System.in;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.util.Random;
import java.util.Scanner;
import javax.crypto.IllegalBlockSizeException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author itamar
 */
public class MineSweeper implements ActionListener {

    public final int MAXROWS = 50;
    public final int MAXCOULMN = 50;
    public final int MAXMINES = 40;
    private String userName;
    private int Password;
    public int rowCount;
    public int columnCount;
    public int Board[][];
    public int BoardShown[][];
    public int Game = 0;
    public int mineCount;
    public boolean isExist ;
    public boolean UserCreated;// pay attention that the meaning actually reversed. 
    public boolean LogedIn;

// constractor
    /**
     * constrictor of the game.
     *
     * @param mineCount count of mines on board to be set in the board
     * @param columnCount count of column in the board.
     * @param rowCount count of rows in the board.
     * @throws java.lang.Exception
     */
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("Connect");
            EntityManager em = emf.createEntityManager();
     UsersJpaController a=new UsersJpaController(emf);
    public MineSweeper(int mineCount, int columnCount, int rowCount) throws Exception {

        // validValues(mineCount, columnCount, rowCount);
        this.mineCount = mineCount;
        this.columnCount = columnCount;
        this.rowCount = rowCount;

    }

    /**
     * checks if the values are valid
     *
     * @param mineCount count of mines on board to be set in the board
     * @param columnCount count of column in the board.
     * @param rowCount count of rows in the board.
     */
    static public void validValues(int mineCount, int columCount, int rowCount) throws Exception {

        if (rowCount == 0 || columCount == 0 || mineCount == 0) {
            throw new ArithmeticException("you cannot enter zero ");
        }

        if (columCount * rowCount < mineCount) {
            throw new Exception("Too much mines Bro slow down ! ");
        }
        if (mineCount > 40) {
            throw new IllegalArgumentException("Too much mines Bro Slow Fucking down!!! step bro <3 ");
        }
        if (rowCount > 50) {
            throw new IllegalArgumentException("Bitch what ? are you eran catz?");
        }
        if (columCount > 50) {
            throw new IllegalArgumentException("Bitch what ? are you eran catz?");
        }

    }
    // Check neighbors

    /**
     *
     * @param PlaceX the place from the user that he want to check on the board
     * @param PlaceY "" "" "" "" "" "" "" "" ""
     * @return returns the number of mines that around the place that the user
     * chose
     */
    public int checkNoOfMines(int PlaceX, int PlaceY) {
        int count = 0;
        int MinesArround = 0;
        for (int currentCol = PlaceX - 1; currentCol <= PlaceX + 1; currentCol++) {
            for (int currentRow = PlaceY - 1; currentRow <= PlaceY + 1; currentRow++) {
                if (currentCol >= 0 && currentRow >= 0 && currentCol < Board.length && currentRow < Board.length) {
                    if (!(currentCol == PlaceX && currentRow == PlaceY)) {
                        count += Board[currentCol][currentRow];
                    }
                }
            }
        }
        while (count != 0) {
            MinesArround++;
            count++;
        }
        return MinesArround;
    }

    //the next step the "graphic"
    public void nextStep(int placeX, int placeY) {

        int MinesArround = checkNoOfMines(placeX, placeY);
        if (MinesArround != 0) {
            this.BoardShown[placeX][placeY] = MinesArround;
        }
        
    


    }

    /**
     *
     * the function create board of row count of row count
     */
    public void createBoard() {
        if (UserCreated == true||LogedIn==true)
        {
        int randx;
        int randy;
        // the board that we shows to the user 
        this.BoardShown = new int[rowCount][columnCount];
        // I chose the number 9 to make the nubmer 0 to be an Empty place 
        // while we chosing a place with zero mines.
        for (int i = 0; i < this.rowCount; i++) {
            for (int j = 0; j < this.columnCount; j++) {
                this.BoardShown[i][j] = 9;
            }
            System.out.println();
        }

        //
        Random rnd = new Random();
        this.Board = new int[rowCount][columnCount];
        int mineCounter = this.mineCount;
        while (mineCounter != 0) {

            randy = rnd.nextInt(rowCount);
            randx = rnd.nextInt(columnCount);
            if (this.Board[randx][randy] == -1) {

                continue;

            }
            Board[randx][randy] = -1;
            mineCounter--;
        }

    }
    
    else
    {
      return;
    }
}

    // checks if there is a mine 
    public void boom(int placeX, int placeY) {
        if (Board[placeX][placeY] == -1) {
            System.out.println("           0");
            System.out.println("          | |");
            System.out.println("          | |");
            System.out.println("          | |");
            System.out.println("the end   0 0");
            Game = 1;
        }

    }

    /**
     * all the marks lines its a test of "try again after the exeption
     * the function create scanner that gets from the user x and y.
     */
    public void getPlace() throws Exception {
        int PlaceX=0;//default
        int PlaceY=0;//default
        boolean ResultOfVars = false;
        int numOfmines;
        System.out.println("Enter X and Y of place ");
        Scanner scan = new Scanner(System.in);
         //while(ResultOfVars == false )
        // {
        PlaceX = scan.nextInt();
        PlaceY = scan.nextInt();
       if (PlaceX > rowCount - 1) {
            throw new IllegalBlockSizeException("you entered worng number\n"
                    + " please try to enter number \n "
                    + "between the rowCount and columns Count you entered ");
      }
       // else ResultOfVars = true;
      if (PlaceY > columnCount - 1) {
            throw new IllegalBlockSizeException("you entered worng number\n"
                    + " please try to enter number \n "
                    + "between the rowCount and columns Count you entered ");
        }
        // else ResultOfVars = true;
       // }
         
        boom(PlaceX, PlaceY);
        if (Game == 0) {
            nextStep(PlaceX, PlaceY);
            numOfmines = checkNoOfMines(PlaceX, PlaceY);
            System.out.println("mines arround you " + numOfmines);
        }

    }

    /**
     * the main function
     */
    public void game() throws Exception {
        createBoard();
        while (Game != 1) {
            printBoard();
            getPlace();

        }

    }

    /**
     * the function prints the board.
     */
    public void printBoard() {
        for (int i = 0; i < this.rowCount; i++) {
            for (int j = 0; j < this.columnCount; j++) {
                System.out.print(" " + this.BoardShown[i][j]);
            }
            System.out.println();
        }
    }

// getters and setters
    public int getMineCount() {
        return mineCount;
    }

    public void setMineCount(int mineCount) {
        this.mineCount = mineCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int[][] getBoard() {
        return Board;
    }

    public void setBoard(int[][] Board) {
        this.Board = Board;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void SignUp(){
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
    System.out.println("Enter username");
    String UserName = myObj.nextLine();
    System.out.println("Enter password");
    String Password = myObj.nextLine();
    
    SqlMineAccountConnect(UserName,Password);
    
       
        
    }
    public void Login()
    {
       Scanner myObj = new Scanner(System.in);  // Create a Scanner object
    System.out.println("Enter username");
    String UserName = myObj.nextLine();
    System.out.println("Enter password");
    String Password = myObj.nextLine();
    for (int i = 0; i < a.getUsersCount(); i++) {
           Users x= a.findUsersEntities().get(i);
           if(x.getUserName().equals(UserName)&&(x.getPassword().equals(Password))){
               LogedIn=true;
               System.out.println("Loging In...");
               
               
    
    }
    }
    if(!LogedIn)
            System.out.println("Worng password or username please try again");
           
    
    }
    

    public void SqlMineAccountConnect(String UserName,String Password)
    {
        
        
            Users user=new Users();
           user.setUserName(UserName);
           user.setPassword(Password);
          
           
           
          
            
       
         
          for (int i = 0; i < a.getUsersCount(); i++) {
           Users x= a.findUsersEntities().get(i);
           if(x.getUserName().equalsIgnoreCase(user.getUserName())){
               System.out.println("Username exist please try another user name");
               isExist=true;
               UserCreated =false;
               break;
           }
          }
    

           if(!isExist)
           {
                System.out.println("User Created");
               a.create(user);
               UserCreated = true;
               
           }
        }
           
    }


