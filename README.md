This is a challenge I was randomly invited by Google while googling things for work, I found this problem quite interesting, so I decided to post my solution and explanation here.

Fuel Injection Perfection
=========================
Commander Lambda has asked for your help to refine the automatic quantum antimatter fuel injection system for the LAMBCHOP doomsday device. It's a great chance for you to get a closer look at the LAMBCHOP -- and maybe sneak in a bit of sabotage while you're at it -- so you took the job gladly. 

Quantum antimatter fuel comes in small pellets, which is convenient since the many moving parts of the LAMBCHOP each need to be fed fuel one pellet at a time. However, minions dump pellets in bulk into the fuel intake. You need to figure out the most efficient way to sort and shift the pellets down to a single pellet at a time. 

The fuel control mechanisms have three operations: 

1) Add one fuel pelletF
2) Remove one fuel pellet
3) Divide the entire group of fuel pellets by 2 (due to the destructive energy released when a quantum antimatter pellet is cut in half, the safety controls will only allow this to happen if there is an even number of pellets)

Write a function called solution(n) which takes a positive integer as a string and returns the minimum number of operations needed to transform the number of pellets to 1. The fuel intake control panel can only display a number up to 309 digits long, so there won't ever be more pellets than you can express in that many digits.

For example:  
solution(4) returns 2: 4 -> 2 -> 1  
solution(15) returns 5: 15 -> 16 -> 8 -> 4 -> 2 -> 1  

Languages
=========

To provide a Python solution, edit solution.py  
To provide a Java solution, edit Solution.java

Test cases
==========
Your code should pass the following test cases.  
Note that it may also be run against hidden test cases not shown here.

-- Python cases --  
Input:  
solution.solution('15')  
Output:  
&ensp;&ensp;5  

Input:  
solution.solution('4')  
Output:  
&ensp;&ensp;2  

-- Java cases --  
Input:  
Solution.solution('4')  
Output:  
&ensp;&ensp;2  

Input:  
Solution.solution('15')  
Output:  
&ensp;&ensp;5  

# Rephrase the problem
Basically, we have to find the minimum number of steps to reduce a number to 1, using only three operations: 
 - Add 1
 - Subtract 1
 - Divide by 2 (usable only on even numbers)

In other words, the question is: what is more convenient to do at each step? Add 1, subtract 1 or divide by 2?  
To answer this question, we can split the problem into two cases:
 1. What is more convenient to do when we have an **even** number?
   - Add 1?
   - Subtract 1?
   - Divide by 2?
 2.  What is more convenient to do when we have an **odd** number?
   - Add 1?
   - Subtract 1?

# Simplify the problem
The challenge requires you to find a solution that works with numbers up to 309 digits.  
To be able to handle such a large number, we would have to use a 1030-bit integer: $\large\lceil log_2(10^{310}-1)\rceil = 1030$.  
As far as I know, there is no language that supports such large primitive types; in Java, there is the `BigInteger` class that allows you to handle arbitrary-precision integers, so we're going to use this class to be able to write our solution.

Since Java does not support operator overloading, and therefore it is not possible to use basic operations (+, -, %, >>, etc.) without resorting to the methods provided by this class, which would make it difficult to write and read this document, we're going to write our solution using the primitive type `int` and then convert the algorithm using the `BigInteger` class.

# Solution
## Even number
**When we have an even number, the most convenient operation to do is always divide by 2.**  
I actually don't know how to prove this statement mathematically; in my head, to make sense of this statement, I thought that dividing a number by 2 gets me closer to 1 faster than adding or subtracting, but if you have a demonstration or some other thoughts about it, feel free to open a discussion in the [Issues](https://github.com/parmi93/fuel-injection-perfection/issues) section or send a [Pull request](https://github.com/parmi93/fuel-injection-perfection/pulls).

## Odd number
What is more convenient to do with an odd number? Add 1 or subtract 1?

To answer this question, let's take a step back to note that adding or subtracting 1 from an odd number will always result in an even number, so since we have just stated that the most convenient operation to do with an even number is to divide by 2, the question turns into:  

**Which even number is more convenient to reach, the one above or the one below?**  

The short answer is: the even number that can be divided by 2 the most times, but how do we figure out which of the two even numbers is divisible by 2 more times?

To answer this new question, we need to note one more thing:  
If we take two consecutive even numbers, we will notice that one of them can be divided by 2 only once before it becomes odd, while the other even number can be divided by two at least 2 times.  

Take a look at the following example:
| N | 2 | 4 | 6 | 8 | 10 | 12 | 14 | 16 | 18 | 20 | 22 | 24 | 26 |
| - | - | - | - | - | -- | -- | -- | -- | -- | -- | -- | -- | -- |
| /2| 1 | 2 | 3 | 4 |  5 |  6 |  7 |  8 |  9 | 10 | 11 | 12 | 13 |
| /4|   | 1 |   | 2 |    |  3 |    |  4 |    |  5 |    |  6 |    |
| /8|   |   |   | 1 |    |    |    |  2 |    |    |    |  3 |    |
|/16|   |   |   |   |    |    |    |  1 |    |    |    |    |    |

Note that the even numbers 2, 6, 10, 14, 18, 22, 26 and so on can be divided by 2 only once, while all the others can be divided by 2 more than once, which is equivalent to saying that **all other numbers can be divided by 4**.

So to answer our initial question: which even number is more convenient to reach?  
**The one that can be divided by 4.**

### 3 is the exception
There is an exception with the number 3; according to what we have said so far, we should add 1 (to get a number divisible by 4) and then divide by 2 a couple of times for a total of 3 operations, but this is not the fastest way to reduce 3 to 1, the most effective way is to subtract 1 and divide by 2, for a total of 2 operations.  
So we have to keep this exception in mind when we are going to write our algorithm.

## Step-by-step solutions
Now that we have all the information, we can write our first step-by-step solution.

Keep repeating the following steps until $N>3$
1. If $N$ is even, go to step 2, otherwise go to step 3
2. Divide $N$ by 2, count 1 operation and repeat step 1
3. If $N+1$ can be divided by 4, go to step 4, otherwise go to step 5
4. Add 1 to $N$, count 1 operation and repeat step 1
5. Subtract 1 from $N$, count 1 operation, and repeat step 1

Once we have completed all the steps, we only need to handle the case where $N$ is 3 or 2, so:
 - If $N$ is 3, count 2 operations (minus 1 and divided by 2)
 - If $N$ is 2, count 1 operation (minus 1)

Which is the same as saying count $N-1$ operations.

# Coding
## Time complexity
We're going to write different algorithms, gradually more performing, so to evaluate the efficiency of each algorithm, we'll need a comparison method; we're going to use time complexity to compare our solutions against each other.  
It should be noted that with time complexity, we do not mean Big $\mathcal{O}$; in fact, the Big $\mathcal{O}$ notation is not very useful when it comes to comparing the efficiency of two algorithms, the Big $\mathcal{O}$ notation could be a valuable tool to classify algorithms and comparing algorithms of different classes, but it is not applicable when it comes to comparing algorithms of the same complexity class.

## Binary operations
To analyze the time complexity, we will look at $N$ in its binary representation, so before we go any further, let's review the binary operations, particularly the subtraction, sum and division operations.

### Even and odd binary numbers
Probably not necessary, but just remember that when the least-significant bit is 0, then $N$ is even, otherwise odd.

$$
\large
\begin{array}{ll}
{{\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}~ {\color{#2FF781}0}}_2 = 38\_{10} \rightarrow \\text{\normalsize{Even number}}  \\
{{\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}~ {\color{#2FF781}1}}_2 = 39\_{10} \rightarrow \\text{\normalsize{Odd number}}   \\
{{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ 0~ {\color{#2FF781}0}}_2 = 40\_{10}                  \rightarrow \\text{\normalsize{Even number}}  \\
\\text{\normalsize{and so on...}}
\end{array}
$$

### Binary division
Dividing a number by 2 is equivalent to right-shifting the number by one position; in other words, it eliminates the least-significant bit of $N$; look at the following example:
$$\large {{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2/2\_{10} = 0~ {{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}}_2$$
More generally, dividing $N$ by a power of two that we will call $X$, is equivalent to right-shifting $N$ by the logarithm to the base 2 of $X$, in simple terms:

$$
\large
\begin{align}
{{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2/2\_{10} &= {{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2>>1\_{10} = {0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}}_2 \\
{{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2/4\_{10} &= {{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2>>2\_{10} = {0~ 0~ {\color{#2F81F7}1}~ 0}_2 \\
{{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2/8\_{10} &= {{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2>>3\_{10} = {0~ 0~ 0~ {\color{#2F81F7}1}}_2 \\
{{\color{#2F81F7}1~} 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2/{16}\_{10} &= {{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2>>4\_{10} = {0~ 0~ 0~ 0}_2
\end{align} 
$$

### Sum 1 to a group of bits
We explain the property of adding 1 to a group of bits set to 1, then extend the concept to a more general case.

If the two least-significant bits of $N$ are 01, then adding 1 will set the least-significant bit to 0 and the second least-significant to 1; look at the following example:

$$
\large
\begin{array}{ll}
{{\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1}}_2~ + \\
\underline{~ ~ ~ ~ ~ ~ ~ ~ ~ {\color{#2F81F7}1}_2} = \\
{{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0}_2
\end{array}
$$

We can extend this concept to a group of bits set to 1; if the least-significant bits of $N$ is a group of bits set to 1, then by adding 1 to $N$, we will set the least-significant 0 bit of $N$ to 1, and all bits in the group will be set to 0; look at the following example:

$$
\large
\begin{array}{ll}
{{\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}~ {\color{#2F81F7}1}}_2~ + \\
\underline{~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ {\color{#2F81F7}1}_2}~ {=} \\
{{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ 0~ 0}_2~
\end{array}
$$

This concept is also valid when there are multiple groups; if we add 1 to each group of bits set to 1, then we will set all the bits of the groups to 0 and the bits immediately after the groups to 1; look at the following example:

$$
\large
\begin{array}{ll}
{~ ~ ~ \overbracket{\color{#2F81F7}1~ 1~ 1~ 1}^{G_4}~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{G_3}~ 0~ \overbracket{\color{#2F81F7}1}^{\mathclap{G_2}}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1~ 1}^{G_1}}_2~ + \\
~ ~ ~ \underline{~ ~ ~ ~ ~ ~ ~ ~ ~ {\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1}_2}~ {=} \\
\underbracket{{\color{#2F81F7}1}~ 0~ 0~ 0~ 0}\_{G_4+1}~ \underbracket{{\color{#2F81F7}1}~ 0~ 0}\_{G_3+1}~ \underbracket{{\color{#2F81F7}1}~ 0}\_{\mathclap{\normalsize{G_2+1}}}~ 0~ \underbracket{{\color{#2F81F7}1}~ 0~ 0~ 0_2}\_{G_1+1}
\end{array}
$$

In this last example, there are 4 groups of bits set to 1; as we can see, all groups have disappeared to zero, resulting in a new bit set to 1 immediately after each group, we could say that adding 1 to a group of bits set to 1, is like shifting the group to the left by one place inside a single bit, or we could say that we are compressing the group into a single bit.

### Sum an odd number to a group of bits
Another case that is worth bringing to our attention is a variation of the previous case.

Starting with an example to explain this case study will be easier:

$$
\large
\begin{array}{ll}
{{\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}^{G_1}}_2~ {+} \\
\underline{0~ 0~ 0~ \underbracket{0~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}~ 0~ 0~ {\color{#2F81F7}1}_2}\_{A_1}}~ {=} \\
{\color{#2F81F7}1~} 0~ \underbracket{{\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}~ 0~ 0~ 0_2}\_{G_1+A_1}
\end{array}
$$

In the previous paragraph, we analyzed the properties of the sum between a group of bits set to 1 and 1; in this case, we have not only added 1 but also added other bits to $G_1$, and we can see that all the bits set to 1 of $A_1$ (with the exception of the least-significant bit) are present in the result of the sum.  
This behaviour can be easily explained by splitting the sum operation into two sums, that is $(G1 + 1) + (A_1-1)$, which is equal to $G1 + A_1$.  
Basically, it is as if we had added 1 to $G_1$ and then added $A - 1$ to the result of the previous sum.

So, $G_1+1$:

$$
\large
\begin{array}{ll}
{{\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}^{G_1}}_2~ {+} \\
\underline{0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1}_2}~ {=}                          \\
{\color{#2F81F7}1~} 0~ \underbracket{{\color{#2F81F7}1}~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0_2}\_{G_1+1}
\end{array}
$$

Then $(G_1+1) + (A_1-1)$:

$$
\large
\begin{array}{ll}
{\color{#2F81F7}1~} 0~ \overbracket{{\color{#2F81F7}1}~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0_2}^{G_1+1}~ {+}               \\
\underline{0~ 0~ 0~ \underbracket{0~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}~ 0~ 0~ 0_2}\_{A_1-1}}~ {=} \\
{\color{#2F81F7}1~} 0~ \underbracket{{\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}~ 0~ 0~ 0_2}\_{G_1+A_1}
\end{array}
$$

This last sum is equivalent to doing a **bitwise OR** between $G_1+1$ and $A_1-1$ because all the bits of $G_1+1$ (except the most-significant bit) are at zero.

We can say that basically, adding an odd number ($A_1$) to a group of bits set to 1 ($G_1$), is equivalent to adding 1 to $G_1$, and doing a bitwise OR between $G_1+1$ and $A_1-1$.

$$
\large
\begin{array}{ll}
G_1 + A_1 = (G_1 + 1)~ |~ (A_1 - 1) \\
\\text{\normalsize{where $G_1$ is a group of bits set to 1, and $A_1$ an odd number $\le G_1$}}
\end{array}
$$

And as before, this property remains valid even when we are dealing with multiple groups:

$$
\large
\begin{array}{ll}
0~ \overbracket{\color{#2F81F7}1~ 1~ 1}^{G_3}~ 0~ \overbracket{{\color{#2F81F7}1~ 1}}^{G_2}~ 0~ {\overbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}}^{G_1}}_2~ {+}                                                                      \\
\underline{0~ \underbracket{{\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~}}\_{A_3} 0~ \underbracket{{\color{#2F81F7}1~ 1}}\_{A_2}~ 0~ \underbracket{0~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}~ 0~ 0~ {\color{#2F81F7}1}_2}\_{A_1}}~ {=} \\
\underbracket{{\color{#2F81F7}1~ 1~} 0~ 0}\_{G_3+A_3}~ \underbracket{{\color{#2F81F7}1~ 1~} 0}\_{G_2+A_2}~ \underbracket{{\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}~ 0~ 0~ 0_2}\_{G_1+A_1}
\end{array}
$$

We will come back to this property later because is very useful for optimizing our algorithm and will allow us to write an $\mathcal{O}(1)$ solution.

### Binary subtraction
We won't do a deep analysis of subtraction, as we won't use subtraction in our $\mathcal{O}(1)$ algorithm.  
The only case that interests us is when we subtract 1 from $N$ when the least-significant bit of $N$ is 1, which leads to converting the aforementioned bit from 1 to 0; look at the following example:

$$
\large
\begin{array}{ll}
{{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}}_2~ - \\
\underline{~ ~ ~ ~ ~ ~ ~ ~ ~ {\color{#2F81F7}1}_2}~ {=} \\
{{\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0}_2
\end{array}
$$

## $\mathcal{O}(\log_{2}n)$ algorithm

We can code our step-by-step solution easily into an algorithm:
```java
int Solution(int n) {
    int count = 0;     //1 Operation (assignment)
    
    while(n > 3) {     //2 Operations (comparison and jump)
        if(n % 2 == 0) //3 Operations (modulo, comparison and jump)
            n /= 2;    //2 Operations (divide and assignment)
        else if((n + 1) % 4 == 0) //4 Operations (sum, modulo, comparison and jump)
            n += 1;    //2 Operations (sum and assignment)
        else
            n -= 1;    //2 Operations (subtraction and assignment)
        
        count++;       //2 Operations (sum and assignment)
    }
    count += n-1; //3 Operations (subtraction, sum and assignment)
    
    return count;
}
```
### Time complexity analysis
#### Best case scenario
The best scenario is when $N$ is a power of 2 because, in that case, we can keep dividing $N$ by 2 until it becomes 2.

We will use the following example to understand how many operations are required to reduce $N$ to 1 when $N$ is a power of 2:

$$
\large {{\color{#2F81F7}1}~~ 0~ 0~ 0~ 0~~ 0~ 0~ 0~ 0~}_2=256\_{10}
$$

In this example, we have to keep dividing by 2 for 7 times which corresponds to $\large log_2(256)-1$ (which is also the number of loops to execute), and 9 operations are performed at each cycle, precisely `while(n > 3)`, `if(n % 2 == 0)`, `n /= 2;` and `count++;`, and finally we need to add another 6 operations that are always performed (`int count = 0;`, `while(n > 3)` and `count += n-1;`) for a total of 69 operations.

So the time complexity when $N$ is a power of 2 is:

$$
\large
9(\lfloor log_2(N)\rfloor - 1) + 6
$$

which can be simplified to:

$$
\large
9\lfloor log_2(N)\rfloor - 3
$$

please note that the notation $\large \lfloor x\rfloor$ means round $x$ down.

#### Worst-case scenario
When $N$ is not a power of 2, then it's a bit trickier, so for simplicity, we will analyze only the worst case; from the above algorithm, it can be seen that the worst case is when $N$ can be divided by 2 the least number of times, to understand better how it works, we have to look again at $N$ in its binary representation, below we have a "worst case" example:

$$
\large
{{\color{#2F81F7}1~ 1~ 1~} 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}}_2=469\_{10}
$$

The worst case is always a number which, in its binary form, has the 3 most-significant bits set to 1, while all other bits are set to 1 alternately.  
In the example above, we have to subtract 1 (to get a number divisible by 4), divide by 2 a couple of times, subtract 1 again, divide by 2 a couple more times, and so on, until we reach the group of bits $\large {\color{#2F81F7}1~ 1~ 1}$, then we have to add 1 and divide by 2 one last couple of times, below are the steps that our `while` loop will do to reduce 469 to 2:

$$
\large
\begin{alignat}{3}
&0~ {{\color{#2F81F7}1~ 1~ 1~} 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}}_2 &&=469\_{10} \\qquad &&\\text{\\normalsize{\[Subtract 1\]}}           \\
&0~ {{\color{#2F81F7}1~ 1~ 1~} 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ 0}_2                  &&=468\_{10}         &&\\text{\\normalsize{\[Divide by 2 twice\]}}    \\
&0~ {{\color{#2F81F7}1~ 1~ 1~} 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}}_2                        &&=117\_{10}         &&\\text{\\normalsize{\[Subtract 1\]}}           \\
&0~ {{\color{#2F81F7}1~ 1~ 1~} 0~ {\color{#2F81F7}1}~ 0~ 0}_2                                         &&=116\_{10}         &&\\text{\\normalsize{\[Divide by 2 twice\]}}    \\
&0~ {{\color{#2F81F7}1~ 1~ 1~} 0~ {\color{#2F81F7}1}}_2                                               &&=29\_{10}          &&\\text{\\normalsize{\[Subtract 1\]}}           \\
&0~ {{\color{#2F81F7}1~ 1~ 1~} 0~ 0}_2                                                                &&=28\_{10}          &&\\text{\\normalsize{\[Divide by 2 twice\]}}    \\
&0~ {{\color{#2F81F7}1~ 1~ 1}}_2                                                                      &&=7\_{10}           &&\\text{\\normalsize{\[Sum 1\]}}                \\
&{{\color{#2F81F7}1}~ 0~ 0~ 0}_2                                                                      &&=8\_{10}           &&\\text{\\normalsize{\[Divide by 2 twice\]}}    \\
&{{\color{#2F81F7}1}~ 0}_2                                                                            &&=2\_{10}           &&                                               \\
\end{alignat}
$$

We can therefore deduce the time complexity by taking into account that, the following operations were performed:
 - 8 divisions, which is equivalent to $\large \lfloor log_2(469)\rfloor$, we already know that each division costs 9 operations, so: $\large 9\lfloor log_2(469)\rfloor$
 - 3 subtractions, which is equivalent to $\large \Big\lfloor\frac{log_2(469)}{2}\Big\rfloor - 1$, subtraction cost 13 operations, so: $\large 13\left(\Big\lfloor\frac{log_2(469)}{2}\Big\rfloor - 1\right)$
 - 1 sum, which costs like subtraction, 13 operations
 - Plus, the usual 6 operations that are always performed

So the time complexity in the worst case is:

$$
\large
9\lfloor log_2(N)\rfloor + 13\left(\bigg\lfloor\frac{log_2(N)}{2}\bigg\rfloor - 1\right) + 13 + 6 \qquad N \geq 7
$$

which can be simplified to:

$$
\large
9\lfloor log_2(N)\rfloor + 13\bigg\lfloor\frac{log_2(N)}{2}\bigg\rfloor + 6 \qquad N \geq 7
$$

So we don't know the exact time complexity, but we do know that it lies somewhere in between:

$$
\large
9\lfloor log_2(N)\rfloor - 3 \leq {\color{#2FF781}T_c}	\leq 9\lfloor log_2(N)\rfloor + 13\bigg\lfloor\frac{log_2(N)}{2}\bigg\rfloor + 6 \qquad N \geq 7
$$

## Another $\mathcal{O}(\log_{2}n)$ algorithm

Some of you may have already noticed several optimizations that could be used to improve the efficiency of our first solution

```java
int Solution(int n) {
    int count = 0;
    
    while(n > 3) {
        int zeros = Integer.numberOfTrailingZeros(n); //Count how many low-significant zero bits there are
        if(zeros > 0) {                               //If n is even
            n >>= zeros;                              //n / (2*zeros)
            count += zeros;
        } else if((n & 0x2) == 0x2) { //If at least two least-significant bits are set to 1
            n++;                      //The first group of bits 1 become 0

            /* We just added 1, so "n" is still greater than 3, and we also know that 
             * n is definitely even, so we can avoid executing the next cycle; it would be 
             * useless as we already have all the information we need. */
            zeros = Integer.numberOfTrailingZeros(n);
            n >>= zeros;
            count += zeros + 1;
        } else { //n is odd, and, the two least-significant bits are "01"
            /* We know that the two least-significant bits are "01", and to remove these bits,
             * 3 operations are required: -1, /2 and /2, so potentially we can avoid running 
             * another unnecessary loop. */
            n >>= 2;
            count += 3; //3 operations required to remove the 2 least-significant bits: -1, /2 and /2 
        }
    }
    count += n-1;
    
    return count;
}
```
_Just for reference: [`Integer.numberOfTrailingZeros(...)`](https://github.com/openjdk/loom/blob/b2a1d0bb8454a9d0d0f3f068b9793185bbde2410/src/java.base/share/classes/java/lang/Integer.java#L1673-L1683) source code_

This implementation is very similar to the previous one, but here avoids using division and modules and, on average, needs fewer `while` loops as it counts trailing zeros.

This time I won't do a deep time complexity analysis, but I simply report the time complexity that I calculated:

$$
\large
36 \leq {\color{#2FF781}T_c}	\leq 44\bigg\lfloor\frac{log_2(N)}{2}\bigg\rfloor+10 \qquad N \geq 7
$$

It can be seen that this algorithm has a much better time complexity for the best-case scenario than the previous algorithm, but for the worst-case scenario, the time complexity increases by about 42%, calculated as $\large\frac{\frac{44}{2}}{9+\frac{13}{2}}$.  
A more in-depth analysis should be done to give a better answer on the efficiency of these two algorithms, perhaps by calculating the average time complexity?  
In any case, we stop here with the time complexity and continue towards a more interesting solution in $\mathcal{O}(1)$.

This $T_c$ has been obtained taking into account that:
 - In Java, the `Integer.numberOfTrailingZeros(int i)` function requires from 21 to 37 operations when `i>0`
 - The best case is when $N$ is a power of two
 - The worst-case scenario is when the 2 least-significant bits and the 2 most-significant bits are 1, and the intermediate bits set to 0 and 1 alternately, for example, $\large {\color{#2F81F7}1~ 1~} 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}$

## One step before the $\mathcal{O}(1)$ solution
The idea behind the $\mathcal{O}(1)$ solution is that we don't have to reduce $N$ to 1 to find how many operations are needed to reduce $N$ to 1; in other words, we don't have to show anyone the steps required to reduce $N$ to 1, what we really care about are:
 - How many "+1" operations are needed?
 - How many "-1" operations are needed?
 - How many "divided by 2" operations are needed?

### Count the "+1" and "-1" operations
Some of you may have noticed that to reduce $N$ to 1, we are simply adding 1 to each group of bits set to 1 (["compressing them into a single bit"](#sum-1-to-a-group-of-bits)), and the isolated bits are removed by subtracting 1 from them.

Let's make an example:

$$
\large
\begin{align}
&{\overbracket{{\color{#2F81F7}1~ 1}}^{G_4}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1~ 1}^{G_3}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1~ 1~ 1}^{G_2}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{G_1}}_2 = {422\\,867}\_{10}~ \\
&\\text{\normalsize{With the next step we add 1 to each group $G_n$}}
\end{align}
$$

$$
\large
\begin{alignat}{2}
&0~ \overbracket{{\color{#2F81F7}1~ 1}}^{G_5}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1~ 1}^{G_3}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1~ 1~ 1}^{G_2}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{G_1}~ {+}           \qquad &&\\text{\normalsize{\[N\]}} \\
&\underline{0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1}}~                                                                                {=}                  &&\\text{\normalsize{\[A\]}} \\
&\underbracket{{\color{#2F81F7}1~} 0~ 0}\_{G_4+1}~ 0~ \underbracket{{\color{#2F81F7}1~} 0~ 0~ 0}\_{G_3+1}~ 0~ \underbracket{{\color{#2F81F7}1~} 0~ 0~ 0~ 0}\_{G_2+1}~ 0~ {\color{#2F81F7}1~} 0~ \underbracket{{\color{#2F81F7}1~} 0~ 0}\_{G_1+1} &&\\text{\normalsize{\[R\]}}\\
\end{alignat}
$$

If we pay attention to what we have added ($A$) and the result ($R$) obtained, we can see that we already know how many additions and subtractions are needed to remove all bits set to 1 from $N$.  
Inside $A$ there are 4 bits to 1 and $R$ contains 5 bits to 1 (not counting the most-significant set bit), so we know that 4 "+1" operations and 5 "-1" operations are needed to clear all the bits set to 1 in $N$ (except for the most-significant bit).

So we just need to find a way to create $A$; in other words, we need to find where the groups start, which can be done with some basic binary operations:
```java
//This instruction looks for bit pattern "110" in n
int startGroups = (n >> 1) & n & ~(n << 1);
```
Next, we need to calculate $R$:
```java
int subtractionOperations = n + startGroups;
```
Now that we have $A$ and $R$, we need to count how many 1 bits they contain, which can be done in $\mathcal{O}(1)$ with the [Hamming weight algorithm](https://en.wikipedia.org/wiki/Hamming_weight):
```java
//Count the set bits in A
int countSumOperations = Integer.bitCount(startGroups);

//Count the set bits in R (excluding the most-significant set bit)
int countSubtractionOperations = Integer.bitCount(subtractionOperations) - 1;
```
which can be done in one shot because the bits set to 1 in $A$ and $R$ can never overlap:
```java
int countSumAndSubtractionOperations = Integer.bitCount(startGroups | subtractionOperations) - 1;
```
_[`Integer.bitCount(...)`](https://github.com/openjdk/loom/blob/b2a1d0bb8454a9d0d0f3f068b9793185bbde2410/src/java.base/share/classes/java/lang/Integer.java#L1696-L1704) uses Hamming weight algorithm to count the set bits_

### Count the "divided by 2" operations
Finally, we need to find how many operations "divided by 2" are required to reduce $N$ to 1, which basically means counting how many bits there are to the right of the most-significant 1 bit:

$$
\large
{\color{#2F81F7}1~} \underbracket{0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ 0}\_{{\\text{\LARGE 19 bits, which means that 19 divided by 2} \atop \\text{\LARGE operations are required}}} \qquad \\text{\normalsize{\[R\]}}\\
$$

which can again be done in $\mathcal{O}(1)$ with some binary operations (which we won't discuss here):
```java
//Clear all bits to the right of the most-significant 1 bit
int highestOneBit = Integer.highestOneBit(n);

//Counts how many zeros there are to the right of the most-significant bit
int countDividedBy2Operations = Integer.numberOfTrailingZeros(highestOneBit);
```
_Just for reference: [`Integer.highestOneBit(...)`](https://github.com/openjdk/loom/blob/b2a1d0bb8454a9d0d0f3f068b9793185bbde2410/src/java.base/share/classes/java/lang/Integer.java#L1602-L1604) and [`Integer.numberOfTrailingZeros(...)`](https://github.com/openjdk/loom/blob/b2a1d0bb8454a9d0d0f3f068b9793185bbde2410/src/java.base/share/classes/java/lang/Integer.java#L1673-L1683) source code_

### First draft of the algorithm
```java
int count = 0;
int startGroups = (n >> 1) & n & ~(n << 1);       //Looks for bit pattern "110" in n
int subtractionOperations = n + startGroups;      //Do all "+1" operations

count += Integer.bitCount(startGroups | subtractionOperations) - 1; //Count all the "+1" and "-1" operations
count += Integer.numberOfTrailingZeros(Integer.highestOneBit(n));   //Count all the "/2" operations

return count;
```
The problem with this first draft is that, in reality, it doesn't work, because it doesn't take into account the generation of new groups; this code only works when $N$ only contain "isolated groups", and we also haven't dealt with the exception of number 3 yet, in the next paragraph, we will analyze these cases.

### Generation of new groups
It can happen that when we add 1 to a group, a new group is generated which did not exist before, or the bit resulting from the sum ($G_n+1$) merges into a group immediately after it.

Let's take a new example:

$$
\large
{{\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1}^{E_1}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{C_1}~ 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1~ 1}^{B_1}~ {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1}^{A_1}}_2 = {90\\,610\\,363}\_{10}~
$$

As we can see in this example, we cannot directly add 1 to $B_1$ and $C_1$ groups, because these groups are under the "influence" of $A_1$, and also adding 1 to $E_1$ will generate a new group, so now we will analyze these cases.

### Adjust $N$ to get isolated groups
Basically, we have two problems to solve; we can see these problems in our example:
 1. The $C_1$ group will be modified by the presence of $B_1$, which in turn is modified by the $A_1$ group ($B_1$ and $C_1$ are under the influence of $A_1$)
 2. Adding 1 to $E_1$ will generate a new group that didn't exist before, and then adding 1 to the newly generated group will generate another group and so on

Only the group $D_1$ is not under the influence of any other group; also, adding 1 to $D_1$ will not generate or modify any other group because, to the left of this group, there are at least two zeros; we can imagine $D_1$ as an "isolated group".

So if we can "fix $N$" in such a way that these two types of problems are eliminated, we can go back to using our [draft algorithm](#first-draft-of-the-algorithm); in other words, we have to "adjust $N$" in a way that we only have to deal with "isolated groups".  
This can be done by finding and converting to 1 some particular zero bits in $N$; that is, all the zero bits that have on the right side a group of bits set to 1 and on the left side have at least one bit set to 1, basically we have to convert to 1 all the zero bits highlighted in green, which can be done with some basic binary operations:
```java
//This instruction looks for bit pattern "1011" in n (finding the bits in green)
int zerosBetweenGroups = ((n >> 1) & ~n & (n << 1) & (n << 2));
```
next, we have to bitwise OR between `zerosBetweenGroups` and `n` in order to convert to 1 the "zeros between groups" that we just found:
```java
n |= zerosBetweenGroups;
```

Let's visualize the code with our example:

$$
\large
\begin{alignat}{3}
& \quad &&{\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1}^{E_1}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{C_1}~ 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1~ 1}^{B_1}~ {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1}^{A_1} \qquad &&\\text{\normalsize{\[N\]}} \\
\\text{\normalsize{OR}}& &&\underline{0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0} &&\\text{\normalsize{\[zerosBetweenGroups\]}}\\
\\text{\normalsize{=}}& &&{\color{#2F81F7}1~} {\color{#2FF781}0}~ \underbracket{{\color{#2F81F7}1~ 1~ 1~ 1}}\_{E_2}~ 0~ 0~ \underbracket{\color{#2F81F7}1~ 1}\_{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \underbracket{\color{#2F81F7}1~ 1}\_{C_1}~ 0~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \underbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}}\_{A_2} &&\\text{\normalsize{\[new N\]}}
\end{alignat}
$$

it can be noted that after these operations, two new groups have been created ($A_2$ and $E_2$), and we have to repeat the binary operations on the new $N$ because, there are some new zero bits that have on the right side a group of bits set to 1 and on the left side at least one bit set to 1, so we have to move our code into a loop to find all the zeros between the groups:
```java
int zerosBetweenGroups_old = 0;
int zerosBetweenGroups = 0;

//This loop adjusts 'n' so that we only have "isolated groups" of bits set to 1
do {
    zerosBetweenGroups_old = zerosBetweenGroups;
    zerosBetweenGroups |= ((n >> 1) & ~n & (n << 1) & (n << 2)); //Looks for bit pattern "1011" in n
    n |= zerosBetweenGroups; //Fix 'n' by converting to 1 the "zeros between groups" we just found
}
while(zerosBetweenGroups_old != zerosBetweenGroups); //Stop when no more new "zeros between groups" are found
```

These are the steps that are performed by the code above:

$$
\large
\begin{alignat}{3}
& \quad &&{\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1}^{E_1}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{C_1}~ 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1~ 1}^{B_1}~ {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1}^{A_1} \qquad &&\\text{\normalsize{\[N\]}} \\
\\text{\normalsize{OR}}& &&\underline{0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0} &&\\text{\normalsize{\[zerosBetweenGroups\]}}\\
\\text{\normalsize{=}}& &&{\color{#2F81F7}1~} 0~ \underbracket{{\color{#2F81F7}1~ 1~ 1~ 1}}\_{E_2}~ 0~ 0~ \underbracket{\color{#2F81F7}1~ 1}\_{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \underbracket{\color{#2F81F7}1~ 1}\_{C_1}~ 0~ {\color{#2F81F7}1~} 0~ \underbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}}\_{A_2} &&\\text{\normalsize{\[new N\]}}
\end{alignat}
$$

$$
\large
\begin{alignat}{3}
& \quad &&{\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{{\color{#2F81F7}1~ 1~ 1~ 1}}^{E_2}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{C_1}~ 0~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}}^{A_2} \qquad &&\\text{\normalsize{\[N\]}} \\
\\text{\normalsize{OR}}& &&\underline{0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0} &&\\text{\normalsize{\[zerosBetweenGroups\]}} \\
\\text{\normalsize{=}}& &&\underbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1}}\_{E_3}~ 0~ 0~ \underbracket{\color{#2F81F7}1~ 1}\_{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \underbracket{\color{#2F81F7}1~ 1}\_{C_1}~ 0~ \underbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}}\_{A_3} &&\\text{\normalsize{\[new N\]}}
\end{alignat}
$$

$$
\large
\begin{alignat}{3}
& \quad &&\overbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1}}^{E_3}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{C_1}~ {\color{#2FF781}0}~ \overbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}}^{A_3} \qquad &&\\text{\normalsize{\[N\]}} \\
\\text{\normalsize{OR}}& &&\underline{0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0} &&\\text{\normalsize{\[zerosBetweenGroups\]}} \\
\\text{\normalsize{=}}& &&\underbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1}}\_{E_3}~ 0~ 0~ \underbracket{\color{#2F81F7}1~ 1}\_{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \underbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}}\_{A_4} &&\\text{\normalsize{\[new N\]}}
\end{alignat}
$$

the while loop above allowed us to find and convert to 1 all the "zeros between groups", this can be seen more easily with the following representation, where we can compare the original `n` with `zerosBetweenGroups` calculated with the loop:

$$
\large
\begin{alignat}{3}
& \quad &&{\color{#2F81F7}1~} {\color{#2FF781}0}~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1}^{E_1}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{C_1}~ {\color{#2FF781}0}~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ {\color{#2F81F7}1~} {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1~ 1}^{B_1}~ {\color{#2FF781}0}~ \overbracket{\color{#2F81F7}1~ 1}^{A_1} \qquad &&\\text{\normalsize{\[original N\]}} \\
\\text{\normalsize{OR}}& &&\underline{0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0} &&\\text{\normalsize{\[zerosBetweenGroups\]}} \\
\\text{\normalsize{=}}& &&\underbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1}}\_{E_3}~ 0~ 0~ \underbracket{\color{#2F81F7}1~ 1}\_{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \underbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}}\_{A_4} &&\\text{\normalsize{\[final N\]}}
\end{alignat}
$$

One thing to note is that each bit set to 1 in `zerosBetweenGroups` means a "+1" operation, so we have to remember to add to our count the number of bits set to 1 in `zerosBetweenGroups`

Now that we have "fixed $N$" and are only dealing with "isolated groups", we can look for the start of these "isolated groups" ([as we had already seen previously](#count-the-1-and--1-operations)), and then sum `startGroups` to `n` in this way we "remove" all the "+1" operations from `n`, notice that after that operation `n` will contain the result of all "+1" operations, which means that each remaining bit set to 1 in `n` is equivalent to a "-1" operation, except for the most-significant bit set to 1 because we have to reduce `n` to 1 and not to 0.
```java
//Find the start of the "isolated groups" by looking for bit pattern "110" in n
int startGroups = (n >> 1) & n & ~(n << 1);

//Do all the remaining "+1" operations; each remaining set bit means a "-1" operation, except for the most-significant set bit
n += startGroups;
```

Let's visualize the above code with our usual example:

$$
\large
\begin{alignat}{2}
&0~ \overbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1}}^{E_3}~ 0~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{D_1}~ 0~ {\color{#2F81F7}1~} 0~ 0~ \overbracket{{\color{#2F81F7}1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ 1}}^{A_4}~ {+}    \qquad &&\\text{\normalsize{\[N\]}}           \\
&\underline{0~ 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ {\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2F81F7}1}}~                                                                {=}           &&\\text{\normalsize{\[startGroups\]}} \\
&\underbracket{{\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0}\_{E_3+1}~ 0~ \underbracket{{\color{#2F81F7}1~} 0~ 0}\_{D_1+1}~ 0~ {\color{#2F81F7}1~} 0~ \underbracket{{\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0}\_{A_4+1}  &&\\text{\normalsize{\[subtraction operations\]}}
\end{alignat}
$$

Note that "subtraction operations" is the result of all "+1" operations.

### 3 is the exception
As we already know, the number 3 is an exception, so it's time to take care of it.

To figure out if we are dealing with the exception of number 3, we need to check if we have added 1 to 3 by mistake (instead of subtracting 1), so first, we need to identify all "+1" operations, which is not a problem, because we have already calculated `zerosBetweenGroups` and `startGroups` which contain all "+1" operations, and since the bits set to 1 in these two variables can never overlap we can put them together:
```java
//Each bit set to 1 in 'plus1Operations' means a "+1" operation
int plus1Operations = zerosBetweenGroups | startGroups;
```
and in the end, we have to somehow compare `plus1Operations` with the result of all sums `n += startGroups` basically, we have to compare a specific bit of `n` with a specific bit of `plus1Operations` in particular, we have to check if the most-significant bit set to 1 in `n` is equal to the bit that is two positions behind in `plus1Operations`, so if these two bits are equal, then it means that we have mistakenly added 1 to 3 (instead of subtracting 1), thus counting 3 operations ("+1", "/2" and "/2") instead of 2 operations ("-1" and "/2"), basically we counted one operation more than necessary, so we just need to subtract 1 from our count.

Translating into code:
```java
//If by mistake we used the "+1" operation with 3
if(Integer.highestOneBit(n) == (Integer.highestOneBit(plus1Operations) << 2)) {
    count = -1; //Start counting from -1
}
```

### $\mathcal{O}(\log_{2}n)$ solution using binary operations

This is the full algorithm:
```java
int Solution(int n)
{
    int count = 0;
    int zerosBetweenGroups_old = 0;
    int zerosBetweenGroups = 0;

    //This loop adjusts 'n' so that we only have "isolated groups" of bits set to 1
    do {
        zerosBetweenGroups_old = zerosBetweenGroups;
        zerosBetweenGroups |= ((n >> 1) & ~n & (n << 1) & (n << 2)); //Looks for bit pattern "1011" in n
        n |= zerosBetweenGroups; //Fix 'n' by converting to 1 the "zeros between groups" we just found
    }
    while(zerosBetweenGroups_old != zerosBetweenGroups); //Stop when no more new "zeros between groups" are found

    //Find the start of the "isolated groups" by looking for bit pattern "110" in n
    int startGroups = (n >> 1) & n & ~(n << 1);

    //From now on, each bit set to 1 in 'n' means a "-1" operation, except for the most-significant bit set to 1
    n += startGroups;

    //Each bit set to 1 in 'plus1Operations' means a "+1" operation
    int plus1Operations = zerosBetweenGroups | startGroups;

    //If by mistake we used the "+1" operation with 3
    if(Integer.highestOneBit(n) == (Integer.highestOneBit(plus1Operations) << 2)) {
        count = -1; //Start counting from -1
    }

    count += Integer.bitCount(plus1Operations | n) - 1;               //Count all "+1" and "-1" operations
    count += Integer.numberOfTrailingZeros(Integer.highestOneBit(n)); //Count all "/2" operations
    
    return count;
}
```
#### Time complexity analysis
I will not do an in-depth time complexity analysis but only report the best and worst cases.

The best case is when we are dealing with an $N$ which contains only isolated groups of bits set to 1, because, in that case, the while loop is only executed once.

The worst case is when the 2 least-significant bits of $N$ are both set to 1, and all other bits are set to 1 and 0 alternately (for example: $\large {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~} 0~ {\color{#2F81F7}1~ 1}$), so in the worst case, the time complexity is: $\large 14\Big\lfloor\frac{log_2(N)}{2}\Big\rfloor+203$, which means that the Big O time complexity is still $\large \mathcal{O}(\log_{2}n)$.

We haven't reached the $\mathcal{O}(1)$ solution yet, but we're getting close; I wanted to propose this solution because I think it will help to understand more easily the $\mathcal{O}(1)$ solution.  
Also, this is the solution I sent to the Google Foobar challenge, with the only difference that I used the ```BigInteger``` type instead of ```int``` in order to be able to handle numbers up to 309 digits (as required by the challenge).  
Unfortunately, in that period, I didn't have enough time to devote to finding a more efficient solution; anyway, after a couple of days, I managed to find an algorithm with time complexity $\mathcal{O}(1)$.

## $\mathcal{O}(1)$ solution

I'll start right away by providing the $\mathcal{O}(1)$ algorithm, and then we'll try to understand how it works.
```java
int Solution(int n) {
    int count = 0;
    int plus1Operations = ((n >> 1) & n) | ((n >> 1) & ~n); //(Over)estimate how many "+1" operations are required
    plus1Operations &= ~(n + plus1Operations);              //Adjust the estimate

    if(plus1Operations > (n >> 2))                          //If by mistake we used the "+1" operation with 3
        count = -1;                                         //Start counting from -1

    //Do all "+1" operations; each remaining bit set to 1 in 'n' means a "-1" operation, except for the most-significant bit set to 1
    n += plus1Operations;

    count += Integer.bitCount(plus1Operations | n);               //Count all "+1" and "-1" operations
    count += Integer.SIZE - Integer.numberOfLeadingZeros(n) - 2;  //Count all "/2" operations
    
    return count;
}
```
This solution is very similar to the previous one, with the difference that the "while loop" has been replaced with two instructions for about 10 binary operations.  
To better understand what is behind these binary operations, we are going to break down these instructions with several simpler operations.

### Estimate of "+1" operations
The first instruction we are going to break is:
```java
int plus1Operations = ((n >> 1) & n) | ((n >> 1) & ~n);
```

into:
```java
int startIsolatedGroups = ((n >> 1) & n);  //Looks for bit pattern "11" in n
int zerosBetweenGroups = ((n >> 1) & ~n);  //Looks for bit pattern "10" in n

int plus1Operations = startIsolatedGroups | zerosBetweenGroups; //startIsolatedGroups and zerosBetweenGroups can never overlap, so put them together
```
As the name suggests, `startIsolatedGroups` is a placeholder indicating the start of all isolated groups of bits set to 1 in `n`, but as you can easily guess, `startIsolatedGroups` not only indicates the start of the isolated groups but also pick up the non-isolated groups and the intermediate bits within the groups, it's basically wrong, but it doesn't matter, we'll think about correcting this mistake later, the thing that matters most to us is that `startIsolatedGroups` indicates at least the start of all the isolated groups, even if it contains some "false positives" (that we will remove later), we can think of `startIsolatedGroups` as an overestimation.

`zerosBetweenGroups` is another placeholder that indicates all the "zeros between groups", but again this placeholder is also wrong; it is an overestimation of the "zeros between groups", but it doesn't matter; we'll think about correcting this mistake later, the thing that matters most to us is that `zerosBetweenGroups` indicates at least all the "zeros between groups", even if it contains some "false positives" (that we will remove later).

Before going to analyze the next instruction, we will make an example to better understand the code:

$$
\large
\begin{alignat}{2}
&{\color{#2F81F7}1~ 1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}~ 0~ 0~ {\color{#2F81F7}1~ 1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1~ 1~ 1~ 1}~ 0~ {\color{#2F81F7}1~ 1~ 1~} 0~ {\color{#2F81F7}1~ 1} \qquad &&\\text{\normalsize{\[n\]}} \\
&0~ {\color{#C04545}1}~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#C04545}1~ 1~ 1}~ 0~ 0~ {\color{#C04545}1~ 1}~ 0~  0~ {\color{#2FF781}1} &&\\text{\normalsize{\[startIsolatedGroups\]}} \\
&0~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ 0~ {\color{#C04545}1}~ 0~ 0~ 0~ {\color{#C04545}1}~ 0~ {\color{#C04545}1}~ 0~ {\color{#C04545}1}~ 0~ 0~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0 &&\\text{\normalsize{\[zerosBetweenGroups\]}} \\
&0~ {\color{#C04545}1}~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ {\color{#C04545}1}~ 0~ 0~ {\color{#2FF781}1}~ {\color{#C04545}1}~ 0~ {\color{#C04545}1}~ 0~ {\color{#C04545}1}~ 0~ 0~ {\color{#C04545}1~ 1~ 1}~ {\color{#2FF781}1}~ 0~ {\color{#C04545}1~ 1}~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1} &&\\text{\normalsize{\[plus1Operations\]}} 
\end{alignat}
$$

All correct flags (bits) are indicated in green, that is, bits that are really "zeros between groups" and "start of isolated groups", and, with red colour, all wrong flags which will have to be corrected by converting them to zero.

### Fix the overestimation
With the next operation, we're going to fix both `startIsolatedGroups` and `zerosBetweenGroups` in one go, by removing all the wrong (red) flags:
```java
plus1Operations &= ~(n + plus1Operations);
```
which is a simplification of the following operations:
```java
int wrongFlags = n | zerosBetweenGroups;
wrongFlags = wrongFlags + startIsolatedGroups;

plus1Operations = plus1Operations & ~wrongFlags;
```
in practice, we fixed the overestimation in 3 steps, where we first identified all the wrong placeholders (flags), then removed them from `plus1Operations`.

#### First step
To find the wrong flags, we first do a bitwise OR:
```java
int wrongFlags = n | zerosBetweenGroups;
```
this operation is the first step to create `wrongFlags`; we removed all "zeros between groups" that were present in `n`, and bring into `wrongFlags` all the wrong bits (in red), plus the right ones (in green), which will be removed with the next step.

$$
\large
\begin{alignat}{3}
& \quad &&{\color{#2F81F7}1~ 1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1~ 1}~ 0~ 0~ {\color{#2F81F7}1~ 1}~ 0~ {\color{#2F81F7}1}~ 0~ {\color{#2F81F7}1}~ 0~ 0~ {\color{#2F81F7}1~ 1~ 1~ 1}~ 0~ {\color{#2F81F7}1~ 1~ 1~} 0~ {\color{#2F81F7}1~ 1} \qquad &&\\text{\normalsize{\[n\]}} \\
\\text{\normalsize{OR}}& &&\underline{0~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ 0~ {\color{#C04545}1}~ 0~ 0~ 0~ {\color{#C04545}1}~ 0~ {\color{#C04545}1}~ 0~ {\color{#C04545}1}~ 0~ 0~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0} &&\\text{\normalsize{\[zerosBetweenGroups\]}} \\
\\text{\normalsize{=}}& &&{\color{#2F81F7}1~ 1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1~ 1}~ {\color{#C04545}1}~ 0~ {\color{#2F81F7}1~ 1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ 0~ {\color{#2F81F7}1~ 1~ 1~ 1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1~ 1~ 1~} {\color{#2FF781}1}~ {\color{#2F81F7}1~ 1} &&\\text{\normalsize{\[wrongFlags\]}}
\end{alignat}
$$

one thing to note is that doing the bitwise OR or adding `zerosBetweenGroups` to `n` is the same thing because the bits set to 1 between these two variables never overlap, so:
```java
int wrongFlags = n + zerosBetweenGroups; //is equal to "n | zerosBetweenGroups"
```

#### Second step
```java
wrongFlags = wrongFlags + startIsolatedGroups;
```

$$
\large
\begin{alignat}{3}
&0~ \overbracket{{\color{#2F81F7}1~ 1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1~ 1}}^{G_3}~ {\color{#C04545}1}~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{G_2}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ 0~ \overbracket{{\color{#2F81F7}1~ 1~ 1~ 1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1~ 1~ 1~} {\color{#2FF781}1}~ {\color{#2F81F7}1~ 1}}^{G_1}~ {+} \\qquad &&\\text{\normalsize{\[wrongFlags\]}} \\
&\underline{0~ 0~ {\color{#C04545}1}~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#C04545}1~ 1~ 1}~ 0~ 0~ {\color{#C04545}1~ 1}~ 0~ 0~ {\color{#2FF781}1}}~ {=} &&\\text{\normalsize{\[startIsolatedGroups\]}}\\
&{\color{#2F81F7}1~} 0~ {\color{#C04545}1}~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#C04545}1}~ {\color{#2F81F7}1~} 0~ 0~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ {\color{#2F81F7}1~} 0~ {\color{#C04545}1~ 1~ 1}~ 0~ 0~ {\color{#C04545}1~ 1}~ 0~ 0~ 0 &&\\text{\normalsize{\[wrongFlags\]}}
\end{alignat}
$$

With this sum, we have done 3 things:
 1. Remove all the green bits from `wrongFlags` (which indicated the "zeros between groups") by exploiting the "[Sum 1 to a group of bits](#sum-1-to-a-group-of-bits)" property.
 2. Bring into `wrongFlags` all the wrong (red) bits that were present in `startIsolatedGroups` by exploiting the "[Sum an odd number to a group of bits](#sum-an-odd-number-to-a-group-of-bits)" property.
 3. Keeps wrong (red) bits present in `wrongFlags`.

Actually, in `wrongFlags` there are other bits set to 1 (those in blue); some were generated by the sum we just made, and some were present in the original `n`, but this doesn't interest us because these bits are irrelevant in the next and last step.

To better understand how the two properties of the sum have been exploited, we can split the sum above into two steps.

The first thing to do is split `startIsolatedGroups` into two pieces as follows: $\\text{\normalsize{\[startIsolatedGroups\]}} = 270\\;664\\;601 = 2\\;228\\;225 + 268\\;436\\;376$

First sum, with which we exploit the "[Sum 1 to a group of bits](#sum-1-to-a-group-of-bits)" property by removing all the green bits (zeros between groups):

$$
\large
\begin{alignat}{3}
&0~ \overbracket{{\color{#2F81F7}1~ 1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1~ 1}}^{G_3}~ {\color{#C04545}1}~ 0~ \overbracket{\color{#2F81F7}1~ 1}^{G_2}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ 0~ \overbracket{{\color{#2F81F7}1~ 1~ 1~ 1}~ {\color{#2FF781}1}~ {\color{#2F81F7}1~ 1~ 1~} {\color{#2FF781}1}~ {\color{#2F81F7}1~ 1}}^{G_1}~ {+} \\qquad &&\\text{\normalsize{\[wrongFlags\]}} \\
&\underline{0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2FF781}1}}~ {=} &&\\text{\normalsize{\[2\\;228\\;225\]}}\\
&\underbracket{{\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0}\_{G_3+1}~ {\color{#C04545}1}~ \underbracket{{\color{#2F81F7}1~} 0~ 0}\_{G_2+1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ \underbracket{{\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0}\_{G_1+1} &&
\end{alignat}
$$

Second sum, with which we exploit the "[Sum an odd number to a group of bits](#sum-an-odd-number-to-a-group-of-bits)" property by bringing into `wrongFlags` all the wrong (red) bits that were present in `startIsolatedGroups`.

$$
\large
\begin{alignat}{3}
&\overbracket{{\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0}^{G_3+1}~ {\color{#C04545}1}~ \overbracket{{\color{#2F81F7}1~} 0~ 0}^{G_2+1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ \overbracket{{\color{#2F81F7}1~} 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0}^{G_1+1}~ {+} \\qquad && \\
&\underline{0~ 0~ {\color{#C04545}1}~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#C04545}1~ 1~ 1}~ 0~ 0~ {\color{#C04545}1~ 1}~ 0~ 0~ 0}~ {=} &&\\text{\normalsize{\[268\\;436\\;376\]}} \\
&{\color{#2F81F7}1~} 0~ {\color{#C04545}1}~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#C04545}1}~ {\color{#2F81F7}1~} 0~ 0~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ {\color{#2F81F7}1}~ {\color{#C04545}1}~ {\color{#2F81F7}1~} 0~ {\color{#C04545}1~ 1~ 1}~ 0~ 0~ {\color{#C04545}1~ 1}~ 0~ 0~ 0 &&\\text{\normalsize{\[wrongFlags\]}}
\end{alignat}
$$

#### Third step
```java
plus1Operations = plus1Operations & ~wrongFlags;
```
In this last step, we remove all the wrong (red) bits, leaving in `plus1Operations` only the green bits that indicate the "+1" operations.

$$
\large
\begin{alignat}{3}
& &&0~ 0~ {\color{#C04545}1}~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ {\color{#C04545}1}~ 0~ 0~ {\color{#2FF781}1}~ {\color{#C04545}1}~ 0~ {\color{#C04545}1}~ 0~ {\color{#C04545}1}~ 0~ 0~ {\color{#C04545}1~ 1~ 1}~ {\color{#2FF781}1}~ 0~ {\color{#C04545}1~ 1}~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1} \qquad &&\\text{\normalsize{\[plus1Operations\]}} \\
\\text{\normalsize{AND}}& \quad &&\underline{{\color{#2F81F7}0~} 1~ {\color{#C04545}0}~ 1~ 1~ 1~ 1~ 1~ 1~ 1~ {\color{#C04545}0}~ {\color{#2F81F7}0~} 1~ 1~ {\color{#C04545}0}~ {\color{#2F81F7}0}~ {\color{#C04545}0}~ {\color{#2F81F7}0}~ {\color{#C04545}0}~ {\color{#2F81F7}0~} 1~ {\color{#C04545}0~ 0~ 0}~ 1~ 1~ {\color{#C04545}0~ 0}~ 1~ 1~ 1} &&\\text{\normalsize{\[NOT wrongFlags\]}} \\
& &&0~ 0~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ 0~ 0~ {\color{#2FF781}1}~ 0~ {\color{#2FF781}1}~ \qquad &&\\text{\normalsize{\[plus1Operations\]}} \\
\end{alignat}
$$

### The exception of number 3
Again to handle exception number 3, we used the same method we used [previously](#3-is-the-exception); the only difference is that, in this case, the implementation is more efficient; we no longer extrapolate the most-significant bit set to 1 of `plus1Operations` and `n`, instead we shift `n` two places to the right and comparing the result with `plus1Operations`.
```java
if(plus1Operations > (n >> 2))    //If by mistake we used the "+1" operation with 3
    count = -1;                   //Start counting from -1
```

### Count all required operations
Here we count all the "+1", "-1", and "/2" operations required to reduce `n` to 1.
```java
count += Integer.bitCount(plus1Operations | n);               //Count all "+1" and "-1" operations
count += Integer.SIZE - Integer.numberOfLeadingZeros(n) - 2;  //Count all "/2" operations
```

#### Count "+1" and "-1" operations
The [Hamming weight algorithm](https://en.wikipedia.org/wiki/Hamming_weight) is used to count the set bits in `plus1Operations` and `n`
```java
count += Integer.bitCount(plus1Operations | n);               //Count all "+1" and "-1" operations
```
actually, we should subtract 1 from the count to avoid counting the most-significant bit set to 1 of `n`, but to make the algorithm more efficient; we move this subtraction to the next instruction.


#### Count "divided by 2" operations
As we did [previously](#count-the-divided-by-2-operations), to count how many "divide by 2" operations are required, we count how many bits are to the right of the most-significant bit set to 1 of `n`, the only difference is that this implementation is more efficient.
```java
count += Integer.SIZE - Integer.numberOfLeadingZeros(n) - 2;  //Count all "/2" operations
```
instead of isolating the most-significant bit set to 1 and then counting the trailing zeros, we just count how many zeros are to the left (leading zeros) of the most-significant set to 1.

### $\mathcal{O}(1)$ algorithm without optimizations
This is the same algorithm without the optimizations; it should be slightly easier to understand.
```java
int Solution(int n) {
    int count = 0;
    int startIsolatedGroups = ((n >> 1) & n);  //Looks for bit pattern "11" in n
    int zerosBetweenGroups = ((n >> 1) & ~n);  //Looks for bit pattern "10" in n

    //The set bits of startIsolatedGroups and zerosBetweenGroups can never overlap, so put them together
    int plus1Operations = startIsolatedGroups | zerosBetweenGroups; 

    //These 2 instructions allow us to find all the wrong flags in plus1Operations
    int wrongFlags = n | zerosBetweenGroups;
    wrongFlags = wrongFlags + startIsolatedGroups;
    
    plus1Operations = plus1Operations & ~wrongFlags;        //Adjust the estimate by removing the wrongFlags

    if(plus1Operations > (n >> 2))                          //If by mistake we used the "+1" operation with 3
        count = -1;                                         //Start counting from -1

    //Do all "+1" operations; each remaining bit set to 1 in 'n' means a "-1" operation, except for the most-significant bit set to 1
    n += plus1Operations;
    
    count += Integer.bitCount(plus1Operations);                  //count all the "+1" operations
    count += Integer.bitCount(n) - 1;                            //Count all the "-1" operations (except the most significant bit set)
    count += Integer.SIZE - Integer.numberOfLeadingZeros(n) - 1; //Count all the "/2" operations
    
    return count;
}
```

### Optimize the Hamming weight algorithm
We could go one step further by optimizing the Hamming weight algorithm to count the bits set in `n` and `plus1Operations`.  

We know that to reduce $N$ to 1, we can only use 3 operations (+1, -1, /2); furthermore, we know that we will never use the operations "+1" and "-1" consecutively, because, after each addition or subtraction, we will have an even number which will always be divided by 2.
Since bits set to 1 in `plus1Operations` indicate the "+1" operations and bits set to 1 in `n` indicate "-1" operations, we know that there can never be two consecutive bits set to 1 in `plus1Operations | n`, so knowing this, we can take advantage of this information and write a variation of the Hamming weight algorithm optimized for our case study:
```java
int bitCount(int i) {
    i = i - ((i >>> 1) & 0x55555555);
    i = i + (i >>> 2);
    i = (i & 0x03030303) + ((i >>> 4) & 0x03030303);
    i = i + (i >>> 8);
    i = i + (i >>> 16);
    return (int)i & 0x3f;
}
```
The [original algorithm](https://github.com/openjdk/loom/blob/b2a1d0bb8454a9d0d0f3f068b9793185bbde2410/src/java.base/share/classes/java/lang/Integer.java#L1696-L1704) requires 15 operations, while our solution requires 14 operations.

But in any case, we are pushing beyond our scope, so we stop here with the optimizations.

### Handle 309-digit numbers
The last step to do is to rewrite our $\mathcal{O}(1)$ algorithm using the `BigInteger` to be able to handle numbers up to 309 digits; this can be done easily; look in [Solution.java](https://github.com/parmi93/fuel-injection-perfection/blob/main/Solution.java).

One thing to note is that the `BigInteger` class is quite heavy, especially because it triggers a memory allocation every time we have to perform any operation on it, we could write our own much more performant class, but again this is beyond our scope.
<br/>

**If you have suggestions, improvements or want some clarification, feel free to open a discussion in the [Issues](https://github.com/parmi93/fuel-injection-perfection/issues) section!**
