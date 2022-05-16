/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package minesweeperworking;

import java.util.Scanner;

/**
 *
/**
 *
 * @author itamar
 */
public class MainGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        
        
        try {
            MineSweeper game;
            System.out.println("Sign Up press : 1\n LogIn press :2");
            Scanner myObj = new Scanner(System.in);
             int Pressed = myObj.nextInt();
            game = new  MineSweeper(9, 10, 10);
            if(Pressed ==1)
            game.SignUp();
            else{
                game.Login();
            }
            
            //game.game();
            
        } 
        catch (Exception ex ) {
            
            System.out.println(ex.getMessage());

        }

        
        

        // enter minecount colm count and row count 
    }

}
