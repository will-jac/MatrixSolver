import java.util.ArrayList;

/**
 * Class used to do complicated math with matrices, ie row reduction, finding
 * the identity, eigenvalues, and eigenvectors
 * 
 * @author Jack Williams
 * @version 2018-05-15
 *
 */
public class MatrixMath
{
    /**
     * Finds and returns the identity of the matrix
     * Does not change the original matrix
     * 
     * @return The identity (if it exists) of the matrix
     */
    public static Matrix identity(Matrix matrix) throws MatrixException {
        // check if the identity CAN exist:
        if (matrix.getColumns() != matrix.getRows())
            throw new MatrixException("The matrix is not square, so the identity does not exist!");
        if (!isIdentity(rref(matrix.clone())))
            throw new MatrixException("The identity does not exist!");
        return removeIdentity(rref(addIdentity(matrix.clone())));        
    }
    
    private static Matrix removeIdentity(Matrix matrix) {
        ArrayList<Number[]> toRemove = matrix.getMatrix();
        // for the first half of the columns of the matrix, remove the column
        for (int index = 0; index < toRemove.size(); ++index) {
            Number[] row = toRemove.get(index);
            Number[] newRow = new Number[row.length / 2];
            for (int i = row.length / 2, j = 0; i < row.length; ++i, ++j) {
                newRow[j] = row[i];
            }
            toRemove.set(index, newRow);
        }
        return new Matrix(toRemove);
    }
    
    private static Matrix addIdentity(Matrix matrix) {
        ArrayList<Number[]> toAdd = matrix.getMatrix();
        // for each row in the matrix
        for (int i = 0; i < toAdd.size(); ++i) {
            Number[] row = toAdd.get(i);
            
            // create a new row with double the number of columns
            Number[] newRow = new Number[2 * matrix.getColumns()];
            
            // copy over all the data that exists in the old row
            for (int j = 0; j < matrix.getColumns(); ++j) {
                newRow[j] = row[j];
            }
            // add on the identity to the row
            for (int j = 0 ; j < matrix.getColumns(); ++j) {
                // if the row is the same as the column (on the diagonal)
                if (j == i) {
                    newRow[j + matrix.getColumns()] = Rational.one;
                } else {
                    newRow[j + matrix.getColumns()] = Rational.zero;
                }
                
            }
            // put the new row in the matrix
            toAdd.set(i, newRow);
        }
        return new Matrix(toAdd);
    }
    
    private static boolean isIdentity(Matrix matrix) {
        ArrayList<Number[]> toCheck = matrix.getMatrix();
        for (int i = 0; i < toCheck.size(); ++i) {
            for (int j = 0; j < toCheck.size(); ++j) {
                if (i == j && Double.compare(toCheck.get(i)[i].doubleValue(), 1.0) != 0) { // true iff the value at A[ii] != 1
                    System.out.println("[" + i + ", " + j + " ] = " + toCheck.get(i)[i] + "!= 1  !!!");
                    return false; 
                }
                else if (i != j && Double.compare(toCheck.get(i)[j].doubleValue(), 0.0) != 0) {// true iff the value at A[ij] != 0
                    System.out.println("[" + i + ", " + j + "] = " + toCheck.get(i)[j] + "!= 0  !!!");
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Row-reduces the matrix
     * 
     * @param matrix
     *            The matrix to convert to it's row-reduced echelon form
     * @return
     */
    public static Matrix rref(Matrix matrix)
    {
        int numCols = matrix.getColumns();
        int numRows = matrix.getRows();
        
        ArrayList<Number[]> toReduce = matrix.getMatrix();
        
        /*
         * if (toClone) { toReduce = matrix.clone(); } else { toReduce =
         * matrix.getMatrix(); }
         */
        boolean reduced = checkIfReduced(toReduce);

        // create a counter so we know which rows and columns are covered
        int workingRowIndex = 0;
        int workingColumnIndex = 0;

        while (!reduced)
        {
            Number[] workingRow = toReduce.get(workingRowIndex);

            // first, check for proper cascading. Determine the leading terms (first non-zero terms)
            // of each row, then put them in an ArrayList. If it isn't sorted, swap rows so that it is.
            ArrayList<Integer> leadingTermList = new ArrayList<>();
            for (int i = 0; i < numRows; ++i) {
                for (int j = 0; j <= numCols; ++j) {
                    if (j == numCols) {
                        leadingTermList.add(numCols + 1); // if it's a row of all zeros, this will move it to the bottom
                    }
                    else if (!(Double.compare(toReduce.get(i)[j].doubleValue(), 0.0) == 0)) { // if the number != 0
                        leadingTermList.add(j);
                        break;
                    }
                }
            }
            // cascade the rows
            cascadeRows(toReduce, leadingTermList);
            
            // need to find the index of the leading term
            int leadingTermIndex = -1;
            for (int i = 0; i < workingRow.length; ++i) {
                if (!(Double.compare(workingRow[i].doubleValue(), 0.0) == 0)) {
                    leadingTermIndex = i;
                    break;
                }
            }
            
            workingColumnIndex = leadingTermIndex;
            
            /*
            // this will make sense later
            int tempVariable = workingColumnIndex;
            
            
            // If the working term of the row is a zero and it's not the first row -
            // that is, if the row reduction leads to a column without a leading one
            // but the working row has a one in the next column over ( or a couple columns
            // over)
            // we need to increment workingColumnIndex, then workingRow until we hit a non-zero index so we
            // don't divide by zero
            while (Double.compare(workingRow[workingColumnIndex].doubleValue(), 0.0) == 0)
            {
                // make sure we don't continue doing this off the end of the matrix
                // if we hit go past the end and haven't found anything, it's a zero row, break
                // the while loop
                if (workingColumnIndex + 1 < numCols)
                    ++workingColumnIndex;
                else
                {
                    // if it is off the end of the matrix, reset the column index and increment the
                    // row index
                    // swap rows so that the zero row is at the bottom - this will cause problems if
                    // the
                    // matrix has multiple zero rows
                    workingColumnIndex = tempVariable;
                    ++workingRowIndex;
                    // swapRows(toReduce, workingRowIndex, numRows - 1);
                }
            }
            */
            

            // Now that the first (working) term is non-zero (hopefully), we can start doing
            // things.
            // If the term is not a one, scale the row so that it is.
            if (!(Double.compare(workingRow[workingColumnIndex].doubleValue(), 1.0) == 0))
            {

                // printing things out
                // currentRowOperation = rowOperation.scale;
                // displayRowOperations(workingRowIndex, 0); // the 0 doesn't alter it, just
                // didn't want to deal with overloading

                Number scaleFactor = findScaleFactor(workingRow[workingColumnIndex], new Rational(1, 1));
                
                if (!(Double.compare(scaleFactor.doubleValue(), 0.0) == 0))
                {
                    workingRow = scaleRow(workingRow, scaleFactor);
                    toReduce.set(workingRowIndex, workingRow);
                }   
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
        return new Matrix(toReduce);
    }

    private static void cascadeRows(ArrayList<Number[]> toSwap, ArrayList<Integer> order) {
        for (int i = 0; i < order.size() - 1; ++i) {
            if (order.get(i) > order.get(i + 1)) {
                // search for the appropriate place to swap the row into
                for (int j = i; j < order.size(); ++j) {
                    if (order.get(j) < order.get(i)) {
                        swapRows(toSwap, j , i);
                        // swap them in order
                        int temp = order.get(i);
                        order.set(i, order.get(j));
                        order.set(j, temp);
                        i = -1; // reset the search
                        break;
                    }
                }
            }
        }
    }

    private static ArrayList<Number[]> swapRows(ArrayList<Number[]> toSwap, int row1, int row2)
    {
        Number[] temp = toSwap.get(row1);
        toSwap.set(row1, toSwap.get(row2));
        toSwap.set(row2, temp);
        return toSwap;
    }

    private static Number findScaleFactor(Number termToReduce, Number toReduceTo)
    {
        // check that termToReduce isn't already zero - we don't need to do anything in that case
        if (Double.compare(termToReduce.doubleValue(), 0.0) == 0) {
           return Rational.zero;  // do nothing
        }
        // it's prefered that everything is a rational.
        if (termToReduce.getClass().equals(Rational.class) && toReduceTo.getClass().equals(Rational.class))
        {
            return (Rational) ((Rational) toReduceTo).divides((Rational) termToReduce);
        }
        // if both terms are not rationals, convert them to doubles and deal with it
        return toReduceTo.doubleValue() / termToReduce.doubleValue();
    }

    private static Number[] scaleRow(Number[] toScale, Number scaleFactor)
    {
        Number[] newRow = new Number[toScale.length];
        int i = 0;
        if (scaleFactor.getClass().equals(Rational.class))
        {
            for (Number term : toScale)
            {
                // still have to check that every term is a rational - if it isn't, use doubles
                if (term.getClass().equals(Rational.class))
                {
                    newRow[i] = ((Rational) term).times((Rational) scaleFactor);
                    ++i;
                }
                else
                {
                    newRow[i] = term.doubleValue() * scaleFactor.doubleValue();
                    ++i;
                }
            }
        }
        else // if the scaleFactor isn't a rational, be sad and move on.
        {
            for (Number term : toScale)
            {
                newRow[i] = term.doubleValue() * scaleFactor.doubleValue();
                ++i;
            }
        }
        return newRow;
    }

    private static ArrayList<Number[]> reduceColumn(ArrayList<Number[]> toReduce, int rowIndex, int columnIndex,
            int workingRowIndex)
    {
        // for each row, find the scale factor then add the rows.
        // skip the current working row
        int numRows = toReduce.size();
        int modifyingRow;
        Number[] workingRow = toReduce.get(rowIndex);
        for (int i = 0; i < numRows; ++i)
        {
            if (i == rowIndex)
            {
                continue;
            }
            modifyingRow = i;
            // get the row we're going to modify so that we can pass it to scaleAndAdd
            Number[] rowToReduce = toReduce.get(i);
            // then reduce (aka scaleAndAdd) that row, then set the replace the old row with
            // the new row in the matrix
            // passes in the row numbers so that displayRowOperations can be called from
            // scaleAndAddRows
            toReduce.set(i, scaleAndAddRows(workingRow, rowToReduce, columnIndex, workingRowIndex, modifyingRow));
        }
        return toReduce;
    }

    private static Number[] scaleAndAddRows(Number[] toScaleAndAdd, Number[] toAddTo, int columnIndex,
            int toScaleAndAddRowNum, int toAddtoRowNum)
    {

        // TODO
        // currentRowOperation = rowOperation.add;
        // displayRowOperations(toScaleAndAddRowNum, toAddtoRowNum);
        Number scaleFactor;
        if (toScaleAndAdd[columnIndex].getClass().equals(Rational.class)
                && toAddTo[columnIndex].getClass().equals(Rational.class))
        {
            // creates a the Rational that, when multiplied to toScaleAndAdd and then added
            // to toAddTo,
            // will make the column entry 0
            Number rationalScaleFactor = 
                    findScaleFactor((Rational) toScaleAndAdd[columnIndex], (Rational) toAddTo[columnIndex]);
            if (rationalScaleFactor.getClass().equals(Rational.class)) { // this if is technically unecessary but java hates me
                scaleFactor = new Rational(-1, 1).times((Rational) rationalScaleFactor);
            }
            else {
                scaleFactor = Rational.zero; // sentinal(?)
            }
        }
        else
        { // use doubles and be sad
            scaleFactor = -1 * findScaleFactor(toScaleAndAdd[columnIndex], toAddTo[columnIndex]).doubleValue();
        }
        Number[] rowToAdd = scaleRow(toScaleAndAdd, scaleFactor);
        Number[] toReturn = addRows(rowToAdd, toAddTo);
        return toReturn;

    }

    private static Number[] addRows(Number[] toAdd, Number[] toAddTo)
    {
        Number[] newRow = new Number[toAdd.length];
        for (int i = 0; i < newRow.length; ++i)
        {
            if (toAdd[i].getClass().equals(Rational.class) && toAddTo[i].getClass().equals(Rational.class))
            {
                newRow[i] = ((Rational) toAdd[i]).plus((Rational) toAddTo[i]);
            }
            else
            { // use doubles and be sad
                newRow[i] = toAdd[i].doubleValue() + toAddTo[i].doubleValue();
            }
        }
        return newRow;
    }

    /**
     * Checks if the matrix is in row-reduced echolon form. Note that a matrix can
     * be row-reduced, without being in row-reduced echolon form
     * 
     * @param toCheck
     * @return
     */
    /* one way this could be made cooler and likely faster is through a hashmap / enummap,
     * namelly, associate each Number with a enum of leading one / zero / nonzero.
     * 
     * alternatively, it could be a hashmap with each Number associated with it's index, 
     * for super easy moving though the ArrayList.
     */
    public static boolean checkIfReduced(ArrayList<Number[]> toCheck)
    {

        int numRows = toCheck.size();
        int numCols = toCheck.get(0).length;

        int workingRowIndex = 0;

        // check that each non-zero row first contains a leading 1
        for (int i = 0; i < numRows; ++i)
        {
            for (int j = 0; j < numCols; ++j)
            {
                Number num = toCheck.get(i)[j];
                if (Double.compare(num.doubleValue(), 1.0) == 0)
                {
                    // if we haven't exited yet, it's got a leading one
                    // so we're good
                    break;
                }
                else if (Double.compare(num.doubleValue(), 0.0) == 0)
                {
                    // if it's a zero, keep looking for a one
                    continue;
                }
                else // if it's not a one or a zero and there's not a leading one
                {
                    return false;
                }
            }
        }

        // Checks that each column with a leading one is otherwise empty (zero)
        // Also checks that the leading ones are "cascading", that is, rows with a leading one
        // closer to the left (lower column index) are higher up (lower row index).
        // This is done via the ArrayList below - if it's not sorted at the end, numbers aren't in order
        ArrayList<Integer> leadingOneIndexList = new ArrayList<>();
        for (int i = 0; i < numCols; ++i)
        {
            // start at the top of the first "uncovered" row each time
            // check what the first (uncovered) entry in the column is
            Number[] workingRow = toCheck.get(workingRowIndex);
            // find the index of the leading one on that row - guaranteed to exist if the
            // row is non-zero by the check above
            int leadingOneIndex = -1; // sentinal
            for (int index = 0; index < numCols; ++index)
            {
                if (Double.compare(workingRow[index].doubleValue(), 1.0) == 0)
                {
                    leadingOneIndex = index;
                    break;
                }
            }
            // if it's a row of all zeros, then leadingOneIndex never got updated, so go to the next row to check.
            if (leadingOneIndex == -1) {
                continue;
            }
            // check for cascading leading ones
            leadingOneIndexList.add(leadingOneIndex);
            
            // check the rest of the column to see if there are any non-zero entries
            // if there are, it is not in RREF, and we return false
            for (int j = 0; j < numRows; ++j)
            {
                // check every entry in the column except the one in workingRow
                if (j == workingRowIndex)
                {
                    continue;
                }
                Number[] checkingRow = toCheck.get(j);
                if (!(Double.compare(checkingRow[leadingOneIndex].doubleValue(), 0.0) == 0))
                    return false;
            }

            if (workingRowIndex + 1 < numRows)
                ++workingRowIndex;
            else
            {
                break;
            }
        }
        // check leadingOneIndexList is sorted
        for (int i = 0; i < leadingOneIndexList.size() - 1; ++i)
        {
            if (leadingOneIndexList.get(i) >= leadingOneIndexList.get(i + 1))
                return false;
        }
        
        return true;
    }

}
