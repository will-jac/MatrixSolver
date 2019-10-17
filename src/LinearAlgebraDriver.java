import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LinearAlgebraDriver
{

    // for the menu
    private static final int ENTER_MATRIX = 1;
    private static final int RREF = 2;
    private static final int IDENTITY = 3;
    private static final int LIST = 4;
    private static final int ADD = 5;
    private static final int MULTIPLY = 6;
    private static final int SETTINGS = 7;
    private static final int QUIT = 8;
    
    // to hold the matrices
    public ArrayList<Matrix> matrices = new ArrayList<>();
    
    // settings
    public boolean toPrint = false;
    public boolean toClone = true;
    
    // Basically, the constructor runs the menu loop 
    public LinearAlgebraDriver()
    {
        Scanner keyboard = new Scanner(System.in);
        
        int menuChoice = 0;
        while (menuChoice != QUIT)
        {
            menuChoice = menu(keyboard);
            
            if (menuChoice == ENTER_MATRIX)
                enterMatrix(keyboard);
            else if (menuChoice == RREF)
                rref(keyboard);
            else if (menuChoice == IDENTITY)
                identity(keyboard);
            else if (menuChoice == LIST) 
                list(keyboard);
            else if (menuChoice == SETTINGS)
                settings(keyboard);
            else if (menuChoice == ADD)
                add(keyboard);
            else if (menuChoice == MULTIPLY)
                multiply(keyboard);
            else if (menuChoice == QUIT) {
                System.out.println("Goodbye!");
            } else
                System.out.println("Unanticipated case");
        }
        
        keyboard.close();
        
       // debug();
    }
    
    public int menu(Scanner keyboard) {
        System.out.println("Enter your choice below" + "\n"
                + ENTER_MATRIX  + ". Enter new matrix" + "\n"
                + RREF          + ". Row Reduce a matrix" + "\n"
                + IDENTITY      + ". Find the identity of a matrix" + "\n"
                + LIST          + ". Print out a matrix" + "\n"
                + ADD           + ". Add two previously entered matrices" + "\n"
                + MULTIPLY      + ". Multiply two previously entered matrices" + "\n"
                + LIST          + ". Print out a matrix" + "\n"
                + SETTINGS      + ". Settings" + "\n"
                + QUIT          + ". Quit"
                );
        String userInput = keyboard.next();
        int value = 0;
        try 
        {
            value = Integer.valueOf(userInput);
        } catch (NumberFormatException e)
        {
            System.out.println("Oops, that's not a number! Try again");
            return menu(keyboard);
        }
        if (value < ENTER_MATRIX || value > QUIT) {
            System.out.println("Not a legal value. Please try again");
            return menu(keyboard);      // recursion
        } else {                        // it was legal
            return value;
        }
    }
    private String enterMatrix(Scanner keyboard) {
        System.out.println("Enter the name of your matrix, then press enter.");
        String name = keyboard.next();
        // check if the matrices list contains a matrix with that name already
        for (Matrix matrix : matrices) 
        {
            if (matrix.getName().equals(name))
            {
                System.out.println("A Matrix with that name already exists! Try Again!");
                return enterMatrix(keyboard);   // recursion
            }
        }
        System.out.println("Type in the matrix, with columns seperated by commas, and rows by semicolons." + "\n"
                + "Do not use spaces. Press enter when finished");
        String userEntry = keyboard.next();
        Matrix matrix = new Matrix(userEntry);
        matrix.setName(name);
        matrices.add(matrix);
        return name;
    }
    private String enterMatrix(String name, Scanner keyboard) {
        System.out.println("Type in the matrix, with columns seperated by commas, and rows by semicolons." + "\n"
                + "Do not use spaces. Press enter when finished");
        String userEntry = keyboard.next();
        Matrix matrix = new Matrix(userEntry, name);
        matrices.add(matrix);
        
        matrix.toString();
        
        return name;
    }
    private void rref(Scanner keyboard) {
        System.out.print("Select a matrix from the list to row reduce, ");
        Matrix matrix = selectMatrix(keyboard);
        Matrix toReduce;
        if (toClone)
            toReduce = MatrixMath.rref(matrix.clone());
        else
            toReduce = MatrixMath.rref(matrix);
        
        System.out.println(toReduce.toString());
    }
    private void identity(Scanner keyboard) {
        System.out.print("Select a matrix from the list to find the identity of, ");
        Matrix workingMatrix = selectMatrix(keyboard);
        
        //workingMatrix.addIdentity();
        try
        {
            Matrix toReturn = MatrixMath.identity(workingMatrix);
            System.out.println(toReturn.toString());

        }
        catch (MatrixException e)
        {
            System.out.println(e.getMessage());
        }
        
        /*
        // creates a new matrix object from the returned matrix 
        // - they currently have the same name so we don't add it to our matrices list
        Matrix refactoredMatrix = workingMatrix.rowsToColumns();
        System.out.println(refactoredMatrix.printRefactoredMatrix());
        refactoredMatrix.addIdentity();
        workingMatrix = refactoredMatrix.columnsToRows();
        System.out.println(workingMatrix.printMatrix());
        refactoredMatrix.rref(toPrint);
        System.out.println(workingMatrix.printMatrix());
        //System.out.println(refactoredMatrix.printMatrix());
         * */
         
    }
    private void list(Scanner keyboard) {
        System.out.print("Select a matrix from the list:");
        Matrix matrix = selectMatrix(keyboard);
        
        System.out.println(matrix.toString());
    }
    private void add(Scanner keyboard) {
        System.out.print("Select the matrix to add to, ");
        Matrix one = selectMatrix(keyboard).clone();
        System.out.print("Select the matrix to be added, ");
        Matrix two = selectMatrix(keyboard);
        try
        {
            one.add(two);
        }
        catch (MatrixException e)
        {
            System.out.println(e.getMessage());
        }
        System.out.println(one);
    }
    private void multiply(Scanner keyboard) {
        System.out.print("Select the matrix to multiply to, ");
        Matrix one = selectMatrix(keyboard).clone();
        System.out.print("Select the matrix to be multiplied, ");
        Matrix two = selectMatrix(keyboard);
        try
        {
            one.multiply(two);
        }
        catch (MatrixException e)
        {
            System.out.println(e.getMessage());
        }
        System.out.println(one);
    }
    private Object settings(Scanner keyboard) {
        System.out.println("Select a setting to change:" +"\n" 
                + "1. Print Row Operations. (current: "+ toPrint + ")\n"
                + "2. Do not change entered matrices. (current: " + toClone + ")\n"
                + "3. Import matrices\n"
                + "4. export matrices\n"
                + "5. Quit");
        int entry = keyboard.nextInt();
        
        if (entry == 1) {
            toPrint = !toPrint;
            return settings(keyboard); // recursion
        }
        else if (entry == 2) {
            toClone = !toClone;
            return settings(keyboard); // recursion
        }
        else if (entry == 3) {
            importMatrices(keyboard);
            return settings(keyboard);
        }
        else if (entry == 4) {
            export(keyboard);
            return settings(keyboard);
        }
        else if (entry == 5) {
            return null;
        } else {
            System.out.println("Invalid entry. Try again.");
            return settings(keyboard); // recursion
        }
    }
    private void export(Scanner keyboard) {
        System.out.println("Would you like to save the matrices? (y/n)");
        String userInput = keyboard.next();
        if (userInput.equalsIgnoreCase("y")) {
            try
            {
                saveMatrices(keyboard);
            }
            catch (IOException e)
            {
               System.out.println("Error printing to file!");
                e.printStackTrace();
            }
        }
    }
    private Matrix selectMatrix(Scanner keyboard) {
        System.out.println("or enter a new name to create a new matrix with that name");
        for (Matrix matrix : matrices) {
            System.out.print(matrix.getName() + ", ");
        }
        System.out.println("");
        String entry = keyboard.next();
        for (Matrix matrix : matrices) {
            if (matrix.getName().equals(entry)) {
                return matrix;
            }
        }
        // if we get down here, we don't have a valid name
        System.out.println("Creating a new Matrix with that name");
        enterMatrix(entry, keyboard);
        for (Matrix matrix : matrices) {
            if (matrix.getName().equals(entry)) {
                return matrix;
            }
        }
        // if we get down here, something's gone horribly wrong.
        System.out.println("oops! Try again");
        return selectMatrix(keyboard);
    }
    private void saveMatrices(Scanner keyboard) throws IOException {
        System.out.println("Enter a name for the matrices");
        String fileName = keyboard.next();
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName + ".txt"));
        String strMatrix = "";
        for (Matrix matrix : matrices) {
           strMatrix += String.format("%s%n", matrix.getName());
           strMatrix += String.format("%s%n", matrix.getStringVerision());
        }
        bw.write(strMatrix);
        bw.flush();
        bw.close();
    }
    private void importMatrices(Scanner keyboard) {
        System.out.println("Select a file to import. Type the full name, for example, \"fileName.txt\"");
        // from https://stackoverflow.com/questions/15482423/how-to-list-the-files-in-current-directory
        File curDir = new File(".");
        File[] filesList = curDir.listFiles();
        for (File f : filesList){
            if (f.isFile()){
                System.out.println(f.getName());
            }
        }
        // get the file the user wants to import
        String userInput = keyboard.next();
        for(File f : filesList) {
            if (f.getName().equals(userInput)) {
                try
                {
                    System.out.println("Trying to read " + f.getName());
                    readFromFile(f);
                    return;
                }
                catch (FileNotFoundException e)
                {
                    System.out.println("Error: could not find the file!");
                    e.printStackTrace();
                    return;
                }
                catch (IOException e)
                {
                    System.out.println("Error reading from the file!");
                    e.printStackTrace();
                    return;
                }
            }
        }
        
        // if we got here, we didn't find the file
        System.out.println("Oops! Couldn't find that file!");
        return;
    }
    /**
     * Reads in a file and parses it to create a list of matrix objects
     * then adds those objects to the matrix list
     * 
     * @param file The file containing the name and string form of a matrix, in the form 
     * "name1 \n stringMatrix 2 \n name2 \n stringMatrix2". stringMatrices are of the form "1,2,3;4,5,6",
     * where commas seperate values and semicolons seperate rows.
     * 
     * @throws IOException if the file is not found or cannot be read.
     */
    private void readFromFile(File file) throws IOException {
        // read the file
        BufferedReader br = new BufferedReader(new FileReader(file));
        
        String name = br.readLine();
        String strMatrix = br.readLine();
        
        boolean renamed = false;
        
        while(name != null && strMatrix != null) {
           
            // check if the matrices list contains a matrix with that name already
            for (Matrix existinMatrix : matrices) 
            {
                int i = 1;
                // while loop to add a number to the new matrix in case it's already named a1 or something like that
                while (existinMatrix.getName().equals(name))
                {
                    renamed = true;
                    name += i + "";
                    ++i;
                }
            }

            // put the matrix in the list
            Matrix matrix = new Matrix(strMatrix, name);
            matrices.add(matrix);
            
            // finish out the priming read
            name = br.readLine();
            strMatrix = br.readLine();
        }
        // tell the user if we had to rename anything
        if (renamed) {
            System.out.println("One or more of the matrices had to be renamed");
        }
        br.close();
    }
    public static void debug() {
        // reduced == true
        String matrix0 = "1,0,0;0,0,0";
        String matrix1 = "1,0,0;0,0,0;0,0,0";   // a 0 matrix still fails
        String matrix2 = "1,0;0,1";             // 2x2 identity matrix
        String matrix3 = "1,0,0,3,2;0,1,0,0,0;0,0,1,2,3";
        String matrix4 = "0,0,0;0,0,0;0,0,0";
        String matrix5 = "0,1,0;0,0,1";     
        // reduced == false
        String matrix6 = "0,0,1;1,0,0;0,1,0";
        String matrix7 = "1,2,3;0,0,0;1,0,0";
        String matrix8 = "1,0,1,0;0,0,2,3;1,2,3,4";
        String matrix9 = "1,0,3.5;5/6,1,0";
        
        // matrix6 evaluates true.. more debugging:
        String a = "1,2,3;0,0,0;1,0,0"; //passes
        String b = "1,0,0;0,0,0;1,0,0"; //passes
        String c = "1,2,3;0,1,0;0,0,0"; //passes - recognizes as not rref bc of the one in the second column?
        String d = "1,2,3;1,0,0;1,0,0"; //passes
        String e = "1,2,3;1,0,0";       //passes ( analog to a, just with a row removed )
        String f = "1,0,1;0,0,1";       //fails  ( analog to e, without the 1 2 3 distraction )
        
        String[] testingArray1 = {matrix0, matrix1, matrix2, matrix3, matrix4, matrix5, matrix6, matrix7, matrix8, matrix9 };
        String[] testingArray2 = {a, b, c, d, e, f};
        System.out.println("Beginning testing Array 1");
        Matrix[] testingArray3 = new Matrix[testingArray1.length];
        int i = 0;
        for (String str : testingArray1) {
            Matrix matrix = new Matrix(str);
            if (i < 6 && !MatrixMath.checkIfReduced(matrix.getMatrix())) {
                System.out.println("Test " + i + " failed! Should have been true!");
            } else if (i >= 6 && MatrixMath.checkIfReduced(matrix.getMatrix())) {
                System.out.println("Test " + i + " failed! Should have been false!");
            }
            testingArray3[i] = matrix;
            ++i;
        }
        System.out.println("Testing Array 1 complete");
        System.out.println("Beginning testing Array 2");
        i = 0;
        for (String str : testingArray2) {
            Matrix matrix = new Matrix(str);
            if (MatrixMath.checkIfReduced(matrix.getMatrix())) {
                System.out.println("Test " + i + " failed! Should have been false!");
            }
            ++i;
        }
        System.out.println("Testing Array 2 complete");
        
        //Matrix I3 = new Matrix("1,0,0;0,1,0;0,0,1");
        //Matrix I2 = new Matrix("1,0;0,1");
        i = 0;
        for (Matrix matrix : testingArray3)
        {
            System.out.println("The matrix: \n" + matrix);
            Matrix reduced = MatrixMath.rref(matrix.clone());
            System.out.println("Reduced to: \n" + reduced);
        }
        
        System.out.println("All test complete");
    }
}
