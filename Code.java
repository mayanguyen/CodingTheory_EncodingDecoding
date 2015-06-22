/*
 * Van Mai Nguyen Thi <maya.nguyenthi@gmail.com>
 *
 * Coding Theory: Program to encode and decode data using a user-supplied
 *       generator or parity check matrix.
 */

import java.util.*;

public class Code {
    // k = dimension; height of generator matrix
    // n = width (length of codewords)
    // w = minimum weight/distance
    // t = number of error this code can correct
    // g = kxn generator matrix; has to be in STANDARD form!!!!!!!
    // h = (n-k)xn parity check matrices
    // table - matches syndromes with coset leaders
    public static int k, n, w, t;
    public static final int FIELD = 2; // field is F_2
    public static Matrix g, h;
    public static Hashtable<Integer, Matrix> table;
    public static int hashList[];
    
    public Code() {
        k = 0;
        n = 0;
        w = 0;
        t = 0;
        table = new Hashtable<Integer, Matrix>();
    }
    
    public Code(Matrix gen) {
        setG(gen);
    }
    
    public Code(Matrix gen, Matrix par) {
        setG(gen);
        setH(par);
        makeTable();
    }
    
    /*** GET METHODS ***/
    // get dimension k of this code
    public int getDimension() {
        return k;
    }
    
    // get length n of codewords
    public int getLength() {
        return n;
    }
    
    public int getWeight() {
        return w;
    }
    
    public int getT() {
        return t;
    }
    
    public Hashtable<Integer,Matrix> getTable() {
        return table;
    }
    
    // INCOMPLETE
    // now only showing second column = cosets
    /*public void showTable() {
        System.out.println("Syndrome\n  --");
        System.out.println("Coset leader\n---------\n");
        System.out.println(table.toString());
        for (int i=0; i<hashList.length; i++) {
            if ( hashList[i] != null ) ) {
                /*table[i][0].transpose().show();
                System.out.println("  ---");
                table[i][1].show();
                System.out.println("---------\n");
            }
        }
    }*/
    
    /*** SET METHODS ***/
    public void setG(Matrix gen) {
        g = gen;
        n = g.n;
        k = g.k;
        w = g.minWeight();
        t = (w - 1)/2;
        int size = (int)Math.pow(2,(n-k));
        table = new Hashtable<Integer, Matrix>(size);
        hashList = new int[size];
    }
    
    public void setH(Matrix par) {
        h = par;
    }
    
    // print info about this code
    // n, k, w, t, g, h
    public void show() {
        System.out.println("This is a ["+n+","+k+","+w+"] code, which can correct up to "+t+" error(s).");
        System.out.println("\nGenerator matrix:");
        g.show();
        System.out.println("\nParity check matrix:");
        h.show();
    }
    
    /*** COMPUTATIONS ***/
    
    // return encoded message (as a vector, i.e. 1xn matrix)
    // message length is k
    public Matrix encode(Matrix message) {
        if (message.k != 1)
            throw new Error("Message must be a vector.");
        if (message.n != k)
            throw new Error("Length of message must be the same as height of the generator matrix.");
        Matrix result;
        result = message.times(g);
        return result;
    }
    
    
    // return decoded code (as a vector, i.e. 1xk matrix)
    // message length is n
    public Matrix decode(Matrix message) {
        if (message.k != 1)
            throw new Error("Codeword must be a vector.");
        if (message.n != n)
            throw new Error("Length of codeword must be the same as length of codewords in your generator and parity check matrices.");
        
        Matrix zero = new Matrix(n-k); // zero matrix of length n
        Matrix syndrome = syn(message);
        
        if (syndrome.equals(zero.transpose())) // if syndrome is zero then no error
            return message;
        
        int hash = matrixToInt(syndrome.transpose());
        
        Matrix coset = table.get(hash); // this is the error
        message = message.plus(coset); // this is the decoded codeword
        
        Matrix decoded = new Matrix(k);
        for (int i=0; i<k; i++) { // copy k first entries from decoded codeword
            decoded.setEntry(0,i,message.getEntry(0,i));
        }
        return decoded;
    }
    
    // return syndrome of x with respect to parity check matrix h
    // syn(x) = vector of height n-k; syn(x) is vertical!!!
    public Matrix syn(Matrix x) {
        if (x.k != 1)
            throw new Error("Syndrome can only be computed from a vector.");
        if (x.n != n)
            throw new Error("Length of codeword must be the same as width of parity check matrix.");
        Matrix result = h.times(x.transpose());
        return result;
    }
    
    // syndrome height n-k; coset leaders length n
    public void makeTable() {
        String endStr = "";
        for (int i=0; i<n; i++) { // if endStr = "11...000..." with t ones
            if (i<t) endStr = endStr + "1";
            else endStr = endStr + "0";
        }
        int end = Integer.parseInt(endStr,2);
        Matrix coset = new Matrix(n); // zero coset; first coset in table
        makeTable(coset, end, 0);
    }
    
    // need to add cosets of weight >t
    // put all possible coset leaders to the second column of table
    // coset to be inserted to the table at hash row corresponding to syndrome
    public void makeTable(Matrix coset, int end, int hashListIndex) {
        Matrix syndrome;
        if (coset.weight() <= t) { // checking just in case
            syndrome = cosetToSyn(coset);
            int hash = matrixToInt(syndrome.transpose());
            table.put(hash, coset);
            hashList[hashListIndex] = hash;
        }
        int decimal = matrixToInt(coset); // decimal corresponding to recent coset
        // if not the end, search for next coset
        if (decimal != end) {
            do {
                decimal++; // corresponding to potential coset
                coset = intToMatrix(decimal); // potential coset
            } while (coset.weight() > t);
            
            makeTable(coset,end, hashListIndex+1);
        }
    }
    
    // takes valid coset leader
    // return corresponding syndrome
    // height of syndrome is n-k
    public Matrix cosetToSyn(Matrix coset) {
        Matrix syndrome = new Matrix(n-k);
        syndrome = syndrome.transpose(); // vertical zero vector height n-k
        // if i-th term of coset is 1, then add i-th column of H to syndrome
        for (int i=0; i<coset.n; i++) {
            if (coset.getEntry(0,i) == 1) {
                Matrix temp = h.getCol(i);
                syndrome = syndrome.plus(temp);
            }
        }
        return syndrome;
    }
    
    // used for coset -> int (decimal)
    // obtained by treating the coset as a binary number
    public int matrixToInt(Matrix coset) {
        String binStr = "";
        for (int i=0; i<coset.n; i++) { // put binary digits into string
            binStr = binStr + coset.getEntry(0,i);
        }
        int decimal = Integer.parseInt(binStr,2);
        return decimal;
    }
    
    // Used for int (decimal) -> coset.
    // Obtained by converting decimal to binary
    // and putting each digit of binary number into matrix
    public Matrix intToMatrix(int decimal) {
        Matrix coset = new Matrix(1,n); //this is used to create cosets => len. = n
        // put digits of binary number into matrix
        for (int i=n-1; i>=0; i--) {
            if (decimal>0) { // put bin digits in matrix starting from last digit
                coset.setEntry(0,i,decimal%2);
                decimal = decimal/2;
            }
            else // put 0 if binary number shorter than coset length
                coset.setEntry(0,i,0);
        }
        return coset;
    }
}
