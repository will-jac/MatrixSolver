import java.util.ArrayList;
/**
 * ADT used to hold a matrix
 * 
 * @author Jack Williams
 * @version 2018-05-15
 *
 */
public class Matrix
{
    /** The actual matrix, stored as an ArrayList of rows, which are Number[]'s */
    private ArrayList<Number[]> matrix;
    /** The number of rows in the matrix */
    private int numRows;
    /** The number of columns in the matrix */
    private int numColumns;
    /** The name of the matrix */
    private String name;
    
    /** The row delineator used in the string representation of the matrix*/
    final private String ROW_DELINEATOR = ";";
    /** The column delineator used in the string representation of the matrix*/
    final private String COLUMN_DELINEATOR = ",";

    /**
     * Constructor. Used to create a matrix from a string representation of that
     * matrix
     * 
     * @param stringMatrix
     * @param name
     */
    public Matrix(String stringMatrix, String name)
    {
        this.matrix = stringToMatrix(stringMatrix);
        this.name = name;
    }
    public Matrix(String stringMatrix)
    {
        this.matrix = stringToMatrix(stringMatrix);
        name = "";
    }

    public Matrix(ArrayList<Number[]> matrix, String name)
    {
        this.matrix = matrix;
        numRows = matrix.size();
        numColumns = matrix.get(0).length;
        this.name = name;
    }
    
    public Matrix(ArrayList<Number[]> matrix)
    {
        this.matrix = matrix;
        numRows = matrix.size();
        numColumns = matrix.get(0).length;
        name = "";
    }
    
    private ArrayList<Number[]> stringToMatrix(String toParse)
    {
        // split the arrays by the rows.
        String[] rows = toParse.split(ROW_DELINEATOR);
        this.numRows = rows.length;

        // create a two-dimensional String array, implemented as an ArrayList,
        // containing the second split (columns)
        ArrayList<String[]> splitArray = new ArrayList<>();
        for (String row : rows)
        {
            splitArray.add(row.split(COLUMN_DELINEATOR));
        }
        // make sure we figure out how many columns there are
        this.numColumns = splitArray.get(0).length;

        // convert the ArrayList<String[]> to an ArrayList<Number[]>
        ArrayList<Number[]> toReturn = new ArrayList<>();

        for (String[] strRow : splitArray)
        {
            Number[] rationalRow = new Number[strRow.length];

            // convert each string in the String[] in each index of the ArrayList to a
            // Number and put it in the row
            for (int index = 0; index < strRow.length; ++index)
            {
                Number number;
                // check what kind of number it is - for a double, it is parsed differently than
                // a rational
                if (strRow[index].contains("."))
                {
                    number = Double.parseDouble(strRow[index]);
                }
                else if (strRow[index].contains("/"))
                {
                    // split it so we have the numerator and denominator
                    String[] fractionString = strRow[index].split("/");
                    // convert the String[] to ints
                    int numerator = Integer.parseInt(fractionString[0]);
                    int denominator = Integer.parseInt(fractionString[1]);
                    number = new Rational(numerator, denominator);
                }
                else
                {
                    // otherwise, create a rational with the parsed number and 1 as the denominator
                    number = new Rational(Integer.parseInt(strRow[index]), 1);
                }

                rationalRow[index] = number;
            }
            toReturn.add(rationalRow);
        }
        return toReturn;
    }


    
    /**
     * @return a deep clone of this matrix
     */
    public Matrix clone()
    {
        ArrayList<Number[]> toReturn = new ArrayList<>();

        // create a copy of each element - requires two loops
        for (Number[] row : matrix)
        {
            Number[] newRow = new Number[numColumns];
            for (int j = 0; j < numColumns; ++j)
            {
                // makes a copy of the rationals
                if (row[j].getClass().equals(Rational.class))
                {
                    // typcasting is bad but we checked for it
                    Rational number = (Rational) row[j];
                    newRow[j] = number.clone();
                }
                else // otherwise, it's a double, so we don't need to do anything
                {
                    double number = (double) row[j];
                    newRow[j] = new Double(number);
                }
            }
            toReturn.add(newRow);
        }

        return new Matrix(toReturn, name);
    }
    
    /**
     * Provides a pretty, printable string version of this matrix
     * @return the printable matrix
     */
    @Override
    public String toString() {
        String stringMatrix = "";
        for (Number[] row : this.matrix) {
            for (Number number : row) {
                if (number.getClass().equals(Rational.class))
                {
                    String strNumber = number.toString();  
                    stringMatrix = stringMatrix + String.format("%-6s", strNumber);
                }
                else
                {
                    stringMatrix += String.format("%-6.2f", number);
                }
            }
            stringMatrix += "\n";
        }
        return stringMatrix;
    }
    
    /**
     * Provides a string matrix that can be used to store the matrix in a file
     * @return
     */
    public String getStringVerision() {
        String strMatrix = "";
        for (Number[] row : matrix) {
            for (int i = 0; i < row.length; ++i) {
                strMatrix += row[i] + COLUMN_DELINEATOR;
            }
            // remove the extra comma at the end of the line
            int last = strMatrix.length() - 1;
            strMatrix = strMatrix.substring(0, last);
            // adds a ";" to indicate the end of the row
            strMatrix += ROW_DELINEATOR;
        }
        // remove the extra semicolon at the end of the matrix
        int last = strMatrix.length() - 1;
        strMatrix = strMatrix.substring(0, last);
        
        return strMatrix;
    }
    /** @return the matrix */
    public ArrayList<Number[]> getMatrix() {
        return this.matrix;
    }
    /** @return the number of rows of the matrix */
    public int getRows() {
        return this.numRows;
    }
    /** @return the number of columns of the matrix */
    public int getColumns() {
        return this.numColumns;
    }
    /** @return the name of the matrix */
    public String getName() {
        return this.name;
    }
    /** renames the matrix
     * @param name the new name for the matrix
     */
    public void setName(String name) {
        this.name = name;
    }
    public void add(Matrix matrix) throws MatrixException {
        if (numRows != matrix.getRows() || numColumns != matrix.getColumns())
            throw new MatrixException("Cannot add, matrices are not the same size");
        ArrayList<Number[]> toAdd = matrix.getMatrix();
        for (int i = 0; i < numRows; ++i) {
            Number[] row = this.matrix.get(i);
            Number[] rowToAdd = toAdd.get(i);
            for (int j = 0; j < numColumns; ++j) {
                if (row[j].getClass().equals(Rational.class) && rowToAdd[j].getClass().equals(Rational.class))
                    row[j] = ((Rational) row[j]).plus((Rational) rowToAdd[j]);
                else
                    row[j] = row[j].doubleValue() + rowToAdd[j].doubleValue();
            }
        }
    }
    public void multiply(Matrix matrix) throws MatrixException {
        if (numColumns != matrix.getRows())
            throw new MatrixException("Cannot multiple, matrices do not have corresponding dimmensions");
        ArrayList<Number[]> toMultiply = matrix.getMatrix();
        // multiply columns of toMuliply by the rows of this
        for (int i = 0; i < matrix.getRows(); ++i) {
            Number[] row = this.matrix.get(i);
            Number[] rowToBuild;
            for (int j = 0; j < matrix.getColumns(); ++j) {
                rowToBuild = new Number[row.length];
                // build rowToBuild
                for (int k = 0; k < rowToBuild.length; ++k) {
                    rowToBuild[k] = Rational.zero;
                } 
                // build the column to do the dot product over
                Number[] columnToMultiply = new Number[matrix.getRows()];
                for (int k = 0; k < matrix.getRows(); ++k ) {
                    columnToMultiply[k] = toMultiply.get(k)[j];
                }
                
                // do the dot product on the row / column
                for (int k = 0; k < columnToMultiply.length; ++k) {
                    if (row[j].getClass().equals(Rational.class) 
                            && columnToMultiply[k].getClass().equals(Rational.class)
                            && rowToBuild[k].getClass().equals(Rational.class))
                        rowToBuild[k] = ((Rational)rowToBuild[k])
                                .plus(
                                        ((Rational) row[k]).times((Rational) columnToMultiply[k]));
                    else
                        rowToBuild[k] = rowToBuild[k].doubleValue() 
                                + ( row[k].doubleValue() * columnToMultiply[k].doubleValue());
                }               
            }
        }
    }
}