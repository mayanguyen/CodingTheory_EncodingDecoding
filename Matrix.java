/*
 * Van Mai Nguyen Thi
 * Coding Theory: Final Project
 *
 * Task: Write a computer program to encode and decode data using a user-supplied
 *       generator or parity check matrix.
 *
 * November 20: One-page outline due, describing what will be in your paper and
 *              presentation.
 * December 11: Rough draft of your paper due.
 * December 18: Final draft of your paper due.
 * December 18, 20: Presentations in class.
 *
 */

public class Matrix {
    
/***** ATTRIBUTES ***********************************************/
    
    public int k, n; // k = height (# of rows); n = width (# of columns; length of codewords)
    public static final int FIELD = 2; // which means F_2
    public int matrix[][];
    
    
    
/***** CONSTRUCTORS ***********************************************/
    
    // default constructor: creates empty matrix
    public Matrix() {
        k = 0;
        n = 0;
        matrix = new int[k][n];
    }
    
    // constructor: choose size; entries = 0
    public Matrix(int height, int width) {
        k = height;
        n = width;
        matrix = new int[k][n];
    }
    
    // constructor: choose size and entries[]
    public Matrix(int height, int width, int entries[]) {
        if (entries.length != height*width) {
            throw new Error("Incorrect number of entries.");
        }
        k = height;
        n = width;
        matrix = new int[k][n];
        for (int i=0; i<k; i++) { // put entries in our matrix
            for (int j=0; j<n; j++) {
                matrix[i][j] = entries[i*n+j] % FIELD;
            }
        }
    }
    
    // constructor: choose size and entries[][]
    public Matrix(int height, int width, int entries[][]) {
        if (entries.length != height) {
            throw new Error("Incorrect number of rows.");
        }
        k = height;
        n = width;
        matrix = new int[k][n];
        for (int i=0; i<k; i++) { // put entries in our matrix
            if (entries[i].length != width) {
                throw new Error("Incorrect length of codeword in row "+(i+1)+".");
            }
            for (int j=0; j<n; j++) {
                matrix[i][j] = entries[i][j] % FIELD;
            }
        }
    }
    
    // zero matrix of length width
    public Matrix(int width) {
        k = 1;
        n = width;
        matrix = new int[k][n];
        for (int i=0; i<n; i++)
            matrix[0][i] = 0;
    }
    
/***** METHODS ***********************************************/
    
    
    /******************** basic ***************************/
    
    public int getHeight() {
        return k;
    }
    
    public int getWidth() {
        return n;
    }
    
    public Matrix getRow(int r) {
        Matrix row = new Matrix (1,n);
        int temp;
        for (int i=0; i<n; i++) {
            temp = this.getEntry(r,i);
            row.setEntry(0,i,temp);
        }
        return row;
    }
    
    public Matrix getCol(int r) {
        Matrix col = new Matrix (k,1);
        int temp;
        for (int i=0; i<k; i++) {
            temp = getEntry(i,r);
            col.setEntry(i,0,temp);
        }
        return col;
    }
    
    public int[][] getArray() {
        return matrix;
    }
    
    public int getEntry(int row, int column) {
        if (row >= k || column >= n) {
            throw new Error("Requested entry does not exist. Verify matrix size.");
        }
        return matrix[row][column];
    }
    
    // return weight if this is a vector (k == 1)
    public int weight() {
        if (this.k != 1)
            throw new Error("Weight can only be calculated form a vector.");
        int w=0;
        for (int i=0; i<n; i++) {
            w += this.getEntry(0,i);
        }
        return w;
    }
    
    // weight of row r
    // 0-base
    // maybe this is not necessary; DELETE THIS FUNCTION!!!
    public int rowWeight(int r) {
        int wei=0;
        for (int i=0; i<n; i++) {
            wei += this.getEntry(r,i);
        }
        return wei;
    }
    
    // print out this matrix
    public void show() {
        for (int i=0; i<k; i++) {
            for (int j=0; j<n; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
    
    public void setEntry(int row, int column, int entry) {
        if (row >= k || column >= n) {
            throw new Error("Requested entry does not exist. Verify matrix size.");
        }
        this.matrix[row][column] = entry % FIELD;
    }
    
    
    // substitute this matrix with "other"; overwrites all entries of this matrix
    public void copy(Matrix other) {
        this.k = other.getHeight();
        this.n = other.getWidth();
        this.matrix = new int[k][n];
        for (int i=0; i<k; i++) { // put entries in our matrix
            for (int j=0; j<n; j++) {
                matrix[i][j] = other.getEntry(i,j);
            }
        }
    }
    
    // return true if size, and entries are all the same
    public boolean equals(Matrix other) {
        //compare size
        if (n != other.n || k != other.k) return false;
        
        //compare entries
        for (int i=0; i<k; i++) {
            for (int j=0; j<n; j++) {
                if (matrix[i][j] != other.getEntry(i,j)) return false;
            }
        }
        return true;
    }
    
    
    // return min weight of code generated by this generating matrix
    public int minWeight() {
        if (this.k == 1) return this.weight();
        
        int[] array = new int[k]; //current/latest combo of rows
        array[0] = 0;
        for (int i=1; i<k; i++)
            array[i] = 2*k; // k would be enough, but 2k just in case
        
        Matrix sum = this.getRow(0);
        
        int minW = rowWeight(0);
        minW = this.minWeight(array,sum,minW);
        
        return minW;
    }
    
    // I think it's correct
    // listing entries as: 0, 01, 012, 0123, 01234, 0124, 013, 0134, 014, 02, 023, 0234, 024, 03, 034, 04, 1, 12, 123, 1234, 124, 13, 134, 14, 2, 23, 234, 24, 3, 34, 4, ...;
    public int minWeight(int array[], Matrix sum, int min) {
        //search for last row added
        for (int l = array.length - 1; l>=0; l--) {
            if (array[l] == k-1) {
                if (l == 0) {
                    return min;
                }
                else {
                    array[l] = 2*k;
                    sum = sum.plus(getRow(k-1));
                    sum = sum.plus(getRow(array[l-1]));
                    array[l-1] = array[l-1] + 1;
                    sum = sum.plus(getRow(array[l-1]));
                }
                break;
            }
            else if (array[l] < k-1) { // use else just in case; just if is enough
                if (l==array.length - 1) {
                    sum = sum.plus(getRow(array[l]));
                    array[l] = array[l] + 1;
                    sum = sum.plus(getRow(array[l]));
                }
                else {
                    array[l+1] = array[l] + 1;
                    sum = sum.plus(getRow(array[l+1]));
                }
                break;
            }
        }
        
        if (sum.weight() < min)
            min = sum.weight();
        
        return minWeight(array,sum,min);
    }
    
    
    
    /******************** computations ***************************/
    
    
    // return new matrix = sum of this and other; NO change to this or other
    // notice that minus and plus are the same in F_2
    // e.g. m1 = m1.plus(m2);
    public Matrix plus(Matrix other) {
        if (this.k != other.k || this.n != other.n)
            throw new Error("Sizes of matrices must match.");
        
        int[][] entries = new int[this.k][other.n]; // all 0s
        
        for (int i=0; i<k; i++) {
            for (int j=0; j<n; j++) {
                entries[i][j] = (this.getEntry(i,j)+other.getEntry(i,j)) % FIELD;
            }
        }
        
        Matrix result = new Matrix(k, n, entries);
        return result;
    }
    
    // return new matrix = product of this and other; NO change to this or other
    public Matrix times(Matrix other) {
        if (this.n != other.k)
            throw new Error("Width of 1st matrix must match height of 2nd matrix");
        
        int[][] entries = new int[this.k][other.n]; // all 0s
        
        for (int i=0; i<this.k; i++) { // for each row of this
            for (int j=0; j<other.n; j++) { // for each column of other
                for (int r=0; r<this.n; r++) {
                    entries[i][j] = (entries[i][j]+this.getEntry(i,r)*other.getEntry(r,j)) % FIELD;
                }
            }
        }
        
        Matrix result = new Matrix(this.k, other.n, entries);
        
        return result;
    }
    
    
    
    // return the transposed matrix (as a new matrix); NO change to this matrix
    public Matrix transpose() {
        int newK = this.n;
        int newN = this.k;
        int[][] entries = new int[newK][newN]; // all 0s
        for (int i=0; i<newK; i++) { // put entries in our return matrix
            for (int j=0; j<newN; j++) {
                entries[i][j] = this.getEntry(j,i);
            }
        }
        
        Matrix result = new Matrix(newK, newN, entries);
        
        return result;
    }
    
    
    // INCOMPLETE
    // return true if this matrix is linearly independent
    public boolean isLinIndep() {
        return true;
    }
    
    // INCOMPLETE
    public Matrix ref() {
        for (int i=0; i<k-1; i++) {
            for (int j=i+1; j<k; j++) {
                // do sth
            }
        }
        
        Matrix result = new Matrix();
        return result;
    }
    
    // INCOMPLETE
    public Matrix rref() {
        Matrix result = new Matrix();
        return result;
    }
    
    // swap rows r1 and r2 in this matrix; 0-base
    public void swapRows(int r1, int r2) {
        if (r1 >= k || r2 >= k)
            throw new Error("Could not find requested rows.");
        
        if (r1==r2) return;
        
        int temp;
        for (int i=0; i<n; i++) {
            temp = matrix[r1][i];
            matrix[r1][i] = matrix[r2][i];
            matrix[r2][i] = temp;
        }
    }
    
    // swap columns r1 and r2 in this matrix; 0-base
    public void swapCols(int r1, int r2) {
        if (r1 >= n || r2 >= n) {
            throw new Error("Could not find requested columns.");
        }
        if (r1==r2) return;
        
        int temp;
        for (int i=0; i<k; i++) {
            temp = matrix[i][r1];
            matrix[i][r1] = matrix[i][r2];
            matrix[i][r2] = temp;
        }
    }
    
    
    // n times each entry in matrix[row]
    // ZERO BASE!!!!!
    // changes this matrix
    private void rowTimesN(int row, int n) {
        for (int i=0; i<n; i++) {
            matrix[row][i] = (matrix[row][i] * n) % FIELD;
        }
    }
    
    // add r2 to r1; matrix[r1] += matrix[r2], matrix[r2] no change
    // ZERO BASE!!!!!
    // changes this matrix
    private void rowPlusRow(int r1, int r2) {
        for (int i=0; i<n; i++) {
            matrix[r1][i] = (matrix[r1][i] + matrix[r2][i]) % FIELD;
        }
    }
    
}

