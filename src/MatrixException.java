/**
 * Exception class for the Matrix classes.
 * Used to throw an exception, ie "Identity not exist"
 * 
 * @author Jack Williams
 * @version 2018-05-21
 *
 */
public class MatrixException extends Exception
{
    /**
     * Serial UID, v1
     */
    private static final long serialVersionUID = 5993473702666926827L;
    /**
     * Constructor.
     * @param message
     */
    public MatrixException(String message) {
        super(message);
    }
}
