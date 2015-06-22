/*
 * Van Mai Nguyen Thi <maya.nguyenthi@gmail.com>
 * Coding Theory: Test Program
 */

import java.util.*;

class CTest {
    public static void main(String args[]) {
        // generator matrix
        Matrix g = new Matrix(4, 8, new int[][] {{1,0,0,0,0,1,1,1},
                                                 {0,1,0,0,1,0,1,1},
                                                 {0,0,1,0,1,1,0,1},
                                                 {0,0,0,1,1,1,1,0}} );
        
        // parity check matrix
        Matrix h = new Matrix(4, 8, new int[][] {{0,1,1,1,1,0,0,0},
                                                 {1,0,1,1,0,1,0,0},
                                                 {1,1,0,1,0,0,1,0},
                                                 {1,1,1,0,0,0,0,1}} );
        
        // x = message of length n-k
        Matrix x = new Matrix( 1, 4, new int[] {0,1,1,0} );
        
        // x2 = (received) code of length n
        Matrix x2 = new Matrix( 1, 8, new int[] {0,1,1,0,1,1,1,0} );
        
        // make the CODE
        Code c = new Code(g,h);
        c.show(); // print info about this code
        c.makeTable(); // make the syndrome-coset leader table (this is not necessary bc
        
        // encode x
        Matrix co = c.encode(x);
        System.out.println("\nx is:");
        x.show();
        System.out.println("\nEncoded x = "+co.k+"x"+co.n+" vector:");
        co.show();
        
        // get syndrome of x2 (this step not necessary to decode)
        Matrix s = c.syn(x2);
        System.out.println("\nx2 is");
        x2.show();
        System.out.println("\nSyndrome of x2 = "+s.k+"x"+s.n+" vector:");
        s.show();
        
        // decode x2
        s = c.decode(x2);
        System.out.println("\nDecoded x2 = "+s.k+"x"+s.n+" vector:");
        s.show();
    }
}
