import java.util.ArrayList;

public class Matrix2
{
    private ArrayList<Rational[]> matrix;
    private int numRows;
    private int numColumns;
    private String name;
    // finals to limit object creation
    private static final Rational ZERO = new Rational(0, 1);
    private static final Rational ONE = new Rational(1, 1);
    
    // All of these are for the printRowOperations method
    // and for better displaying the reduced matrix
    private enum rowOperation {
        swap,
        scale,
        add,
        none;
    }
    private rowOperation currentRowOperation;
    private Rational rationalScaleFactor;
    private boolean toPrint = false;

    
    public Matrix2(ArrayList<Rational[]> matrix, int rows, int columns, String name)
    {
        this.matrix = matrix;
        this.numRows = rows;
        this.numColumns = columns;
        this.name = name;
    }
    
    public Matrix2(String stringMatrix, String name) {
        this.matrix = stringToMatrix(stringMatrix);
        this.name = name;
    }
    
    private ArrayList<Rational[]> stringToMatrix(String toParse) {
        // create a String array containing the first split
        String[] rows = toParse.split(";");
        this.numRows = rows.length;
        
        // create a two-dimensional String array, implemented as an ArrayList, containing the second split
        ArrayList<String[]> splitArray = new ArrayList<>();
        for (int i = 0; i < rows.length; ++i) {
            splitArray.add(rows[i].split(","));
        }
        // make sure we figure our how many columns there are
        this.numColumns = splitArray.get(0).length;
        
        // convert the ArrayList<String[]> to an ArrayList<Rational[]>
        
        // first create the ArrayList
        ArrayList<Rational[]> toReturn = new ArrayList<>();
        // then convert each String[] in the ArrayList splitArray to a Rational[] and assign it at the end
        for (String[] strRow : splitArray) {
            // create our Rational array to hold the row
            Rational[] rationalRow = new Rational[strRow.length];
            // convert each string in the String[] in each index of the ArrayList to a Rational and put it in the row
            for (int index = 0; index < this.numColumns; ++index) {
                Rational number;
                // if we have a fraction at any index in the matrix
                if (strRow[index].contains("/")) {
                    // split it so we have the numerator and denominator
                    String[] fractionString = strRow[index].split("/");
                    // convert the String[] to ints
                    int numerator = Integer.parseInt(fractionString[0]);
                    int denominator = Integer.parseInt(fractionString[1]);
                    number = new Rational(numerator, denominator);
                } else {
                    // otherwise, create a rational with the parsed number and 1 as the denominator
                    number = new Rational(Integer.parseInt(strRow[index]), 1);
                }
                
                rationalRow[index] = number;
            }
            toReturn.add(rationalRow);
        }
        return toReturn;
    }
    // this can be made better by removing all the "toReduce" and changing that to matrix since they point at the same loc.
    public void rref(boolean toPrint)
    {
        if (toPrint) {
            this.toPrint = true;
        }
        
        // TODO: create a deep clone of the matrix
        ArrayList<Rational[]> toReduce = matrix;
        
        boolean reduced = checkIfReduced(toReduce);
        
        //create a counter so we know which rows and columns are covered
        int workingRowIndex = 0;
        int workingColumnIndex = 0;
        
        while (!reduced)
        {
            Rational[] workingRow = toReduce.get(workingRowIndex);
            
            // If the first number of the first row is zero, we need to swap some things
            if (workingRowIndex == 0 && workingRow[0].equals(ZERO)) {
                // find an appropriate row, then swap it
                currentRowOperation = rowOperation.swap;
                toReduce = swapRows(toReduce, 0, findRowsToSwap(toReduce, 0));
            }
            
            // this will make sense later
            int tempVariable = workingColumnIndex;
            
            // If the working term of the row is a zero and it's not the first row - 
            // that is, if the row reduction leads to a column without a leading one
            // but the working row has a one in the next column over ( or a couple columns over)
            // we need to increment workingColumnIndex until we hit a non-zero index so we don't divide by zero
            while (workingRow[workingColumnIndex].equals(ZERO)) 
            {   
                // make sure we don't continue doing this off the end of the matrix
                if (workingColumnIndex < numColumns) 
                {
                    ++workingColumnIndex;
                } 
                else
                {
                    // if it is off the end of the matrix, reset the column index and increment the row index
                    // we'll actually need to swap the rows, but I'll do that later
                    // TODO: add something to swap all zero rows to the bottom
                    workingColumnIndex = tempVariable;
                    ++workingRowIndex;
                    continue;
                }
            }
            
            
            // Now that the first (working) term is non-zero (hopefully), we can start doing things.
            // If the term is not a one, scale the row so that it is.
            // TODO: Add a check for if the entry is alread one for efficency reasons
            if (!workingRow[workingColumnIndex].equals(ZERO)) {
                
                // printing things out
                currentRowOperation = rowOperation.scale;
                displayRowOperations(workingRowIndex, 0); // the 0 doesn't alter it, just didn't want to deal with overloading

                Rational scaleFactor = findScaleFactor(workingRow[workingColumnIndex], ONE);
                
                workingRow = scaleRow(workingRow, scaleFactor);

                // make sure we actually put the altered row back in the ArrayList
                toReduce.set(workingRowIndex, workingRow);
            }
            
            // now that the term is a leading 1, reduce the rest of the column
            // passes workingRowIndex so that it we can call diplayRowOperations
            // this isn't the best way to do it, but it's *A* way to do it, and I'm lazy
            reduceColumn(toReduce, workingRowIndex, workingColumnIndex, workingRowIndex);
            

            
            // remove all the weird "negative zeros" 
            // toReduce = removeNegativeZeros(toReduce);
            
            // check if we should go further
            reduced = checkIfReduced(toReduce);

            ++workingColumnIndex;
            ++workingRowIndex;
                        
        }
        // Congrats, matrix is reduced!        
    }
    
    
    private void displayRowOperations(int row1, int row2) {
        if (!toPrint) {
            return;
        }
        
        switch (currentRowOperation) {
            case swap : System.out.println("R " + row1 + " <-> R" + row2);
                break;
            case scale : System.out.println(rationalScaleFactor + " R" + row1 + " -> R" + row1);
                break;
            case add : System.out.println(rationalScaleFactor + " R" + row1 + " + R" + row2 + " -> R" + row2);
                break;
            case none : break;
        }
    }
    private int findRowsToSwap(ArrayList<Rational[]> toSearch, int rowToSwap) {
        // Look for a row that doesn't start with a zero.
        // If we find one, swap it with the row in question.
        int i = 0;
        for (Rational[] toCheck : toSearch) {
            if (!toCheck[0].equals(ZERO)) {
                displayRowOperations(rowToSwap, i);
                return i;
            }
            ++i;
        }
        // if we get down here, there is no such row
        // use -1 as a sentinel / error reporter
        System.out.println("Error: cannot find a non-zero row to swap with. Are you sure you entered the matrix in correctly?");
        return -1;
    }
    private ArrayList<Rational[]> swapRows(ArrayList<Rational[]> toSwap, int row1, int row2) {
        // Untested. Might now work.
        Rational[] temp = toSwap.get(row1);
        toSwap.set(row1, toSwap.get(row2));
        toSwap.set(row2, temp);
        return toSwap;
    }
    private Rational findScaleFactor(Rational termToReduce, Rational toReduceTo) {
        Rational scaleFactor = toReduceTo.divides(termToReduce);
        rationalScaleFactor = scaleFactor;
        return scaleFactor;
    }
    private Rational[] scaleRow(Rational[] toScale, Rational scaleFactor) {
        Rational[] newRow = new Rational[toScale.length];
        int i = 0;
        for (Rational term : toScale) {
            newRow[i] = term.times(scaleFactor);
            ++i;
        }
        return newRow;
    }
    private Rational[] addRows(Rational[] toAdd, Rational[] toAddTo) {
        Rational[] newRow = new Rational[toAdd.length];
        for (int i = 0; i < newRow.length; ++i) {
            newRow[i] = toAdd[i].plus(toAddTo[i]);
        }
        return newRow;
    }
    private Rational[] scaleAndAddRows(Rational[] toScaleAndAdd, Rational[] toAddTo, int columnIndex, int toScaleAndAddRowNum,
            int toAddtoRowNum) {  
        
        currentRowOperation = rowOperation.add; 
        displayRowOperations(toScaleAndAddRowNum, toAddtoRowNum);
        
        // creates a the Rational that, when multiplied to toScaleAndAdd and then added to toAddTo, will make the column entry 0
        Rational scaleFactor = new Rational(-1, 1).times(findScaleFactor(toScaleAndAdd[columnIndex], toAddTo[columnIndex]));       
        Rational[] rowToAdd =  scaleRow(toScaleAndAdd, scaleFactor);
        Rational[] toReturn = addRows(rowToAdd, toAddTo);
        return toReturn;
    }
    private ArrayList<Rational[]> reduceColumn(ArrayList<Rational[]> toReduce, int rowIndex, int columnIndex, int workingRowIndex) {
        // for each row, find the scale factor then add the rows.
        // skip the current working row
        int modifyingRow;
        Rational[] workingRow = toReduce.get(rowIndex);
        for (int i = 0; i < numRows; ++i) {
            if (i == rowIndex) {
                continue;
            }
            modifyingRow = i;
            // get the row we're going to modify so that we can pass it to scaleAndAdd
            Rational[] rowToReduce = toReduce.get(i);
            // then reduce (aka scaleAndAdd) that row, then set the replace the old row with the new row in the matrix
            // passes in the row numbers so that displayRowOperations can be called from scaleAndAddRows
            toReduce.set(i, scaleAndAddRows(workingRow, rowToReduce, columnIndex, workingRowIndex, modifyingRow));
        }
        return toReduce;
    }

    public boolean checkIfReduced(ArrayList<Rational[]> toCheck) {
        
        //create a counter so we know which rows are covered
        int workingRowIndex = 0;

        // implement a for loop to check the columns
        for (int j = 0; j < this.numColumns; ++j)
        {
            // start at the top of the first uncovered row each time
             
            // check what the first entry in the column is
            Rational[] workingRow = toCheck.get(workingRowIndex);
            if (workingRow[j].equals(ZERO)) {
                // If the first entry in the matrix is zero, it's likely not in RREF
                // FIXME: will return false if it is a zero matrix, add a check for that
                if (j == 0) {
                    return false;
                } else {
                    // I think this should fix the issue of a column of non-leading ones causing it to return true
                    // Basically, if we don't have a leading one in the "correct" place - as in along the diagonal
                    // just move along the row further, that is, start at the top of the loop again.
                    continue;
                }
            } else if (workingRow[j].equals(ONE)) {
                // check the rest of the column to see if there are any non-zero entries
                // if there are, it is not in RREF, and we return false
                for (int i = 0; i < numRows; ++i) {
                    // check every entry in the column except the one in workingRow
                    if (i == workingRowIndex) {
                        continue;
                    }
                    Rational[] checkingRow = toCheck.get(i);
                    if (!checkingRow[j].equals(ZERO)) {
                        return false;
                    }
                }
            } else {
                return false;
            }
            if (workingRowIndex + 1 < numRows )
                ++workingRowIndex;
            else {
                break;
            }
        }

        return true;
    }
    public boolean checkIfReduced() {
        
        //create a counter so we know which rows are covered
        int workingRowIndex = 0;

        // implement a for loop to check the columns
        for (int j = 0; j < this.numColumns; ++j)
        {
            // start at the top of the first uncovered row each time
             
            // check what the first entry in the column is
            Rational[] workingRow = this.matrix.get(workingRowIndex);
            if (workingRow[j].equals(ZERO)) {
                // If the first entry in the matrix is zero, it's likely not in RREF
                // FIXME: will return false if it is a zero matrix, add a check for that
                // FIXME: should also return false if there is a column without a leading one
                if (j == 0)
                    return false;

            } else if (workingRow[j].equals(ONE)) {
                // check the rest of the column to see if there are any non-zero entries
                // if there are, it is not in RREF, and we return false
                for (int i = 0; i < numRows; ++i) {
                    // check every entry in the column except the one in workingRow
                    if (i == workingRowIndex) {
                        continue;
                    }
                    Rational[] checkingRow = this.matrix.get(i);
                    if (!checkingRow[j].equals(ZERO)) {
                        return false;
                    }
                }
            } else {
                return false;
            }
            if (workingRowIndex + 1 < numRows )
                ++workingRowIndex;
            else {
                break;
            }
        }
        // if we made it down here, it's in RREF
        return true;
    }
  
    public void addIdentity() {
        // for each row in the matrix
        for (int i = 0; i < numRows; ++i) {
            Rational[] row = matrix.get(i);
            
            // create a new row with double the number of columns
            Rational[] newRow = new Rational[2 * numColumns];
            
            // copy over all the data that exists in the old row
            for (int j = 0; j < numColumns; ++j) {
                newRow[j] = row[j];
            }
            // add on the identity to the row
            for (int j = 0 ; j < numColumns; ++j) {
                // if the row is the same as the column (on the diagonal)
                if (j == i) {
                    newRow[j + numColumns] = ONE;
                } else {
                    newRow[j + numColumns] = ZERO;
                }
                
            }
            // put the new row in the matrix
            matrix.set(i, newRow);
        }
        // update the columnns
        numColumns = 2 * numColumns;
    }
    @Override
    public String toString() {
        String stringMatrix = "";
        for (Rational[] row : this.matrix) {
            for (int j = 0; j < this.numColumns; ++j) {
                String strRow = row[j].toString();                
                stringMatrix = stringMatrix + String.format("%-6s", strRow);
            }
            stringMatrix += "\n";
        }
        return stringMatrix;
    }
    public String getStringVerision() {
        String strMatrix = "";
        for (Rational[] row : matrix) {
            for (int i = 0; i < row.length; ++i) {
                strMatrix += row[i] + ",";
            }
            // remove the extra comma at the end of the line
            int last = strMatrix.length() - 1;
            strMatrix = strMatrix.substring(0, last);
            // adds a ";" to indicate the end of the row
            strMatrix += ";";
        }
        // remove the extra semicolon at the end of the matrix
        int last = strMatrix.length() - 1;
        strMatrix = strMatrix.substring(0, last);
        
        return strMatrix;
    }
    public ArrayList<Rational[]> getMatrix() {
        return this.matrix;
    }
    public int getRows() {
        return this.numRows;
    }
    public int getColumns() {
        return this.numColumns;
    }
    public String getName() {
        return this.name;
    }
}