import java.math.BigInteger;

public class Solution {
    /* Solution that uses the BigInteger class to handle arbitrary-precision integers, 
     * the algorithm is the same as the algorithm in the solution(int n) method. */
    public static int solution(String n) {
        int count = 0;
        BigInteger N = new BigInteger(n);
        BigInteger plus1Operations = N.shiftRight(1).and(N).or(N.shiftRight(1).and(N.not()));   //(Over)estimate how many "+1" operations are required
        plus1Operations = plus1Operations.and(N.add(plus1Operations).not());                    //Adjust the estimate
        
        if(plus1Operations.compareTo(N.shiftRight(2)) == 1)     //If by mistake we used the "+1" operation with 3
            count = -1;                                         //Start counting from -1
        
        //Do all operations "+1"; each remaining bit set to 1 in 'n' means a "-1" operation, except for the most-significant bit set to 1
        N = N.add(plus1Operations);
        
        count += N.or(plus1Operations).bitCount();              //Count all "+1" and "-1" operations
        count += N.bitLength() - 2;                             //Count all "/2" operations
        
        return count;
    }
    
    public static int solution(int n) {
        int count = 0;
        int plus1Operations = ((n >> 1) & n) | ((n >> 1) & ~n); //(Over)estimate how many "+1" operations are required
        plus1Operations &= ~(n + plus1Operations);              //Adjust the estimate
    
        if(plus1Operations > (n >> 2))                          //If by mistake we used the "+1" operation with 3
            count = -1;                                         //Start counting from -1
    
        //Do all operations "+1"; each remaining bit set to 1 in 'n' means a "-1" operation, except for the most-significant bit set to 1
        n += plus1Operations;
    
        count += Integer.bitCount(n | plus1Operations);               //Count all "+1" and "-1" operations
        count += Integer.SIZE - Integer.numberOfLeadingZeros(n) - 2;  //Count all "/2" operations
        
        return count;
    }
}
