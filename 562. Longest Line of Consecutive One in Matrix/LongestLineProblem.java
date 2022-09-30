/**
 Given an m x n binary matrix mat, return the length of the longest line of consecutive one in the matrix.

The line could be horizontal, vertical, diagonal, or anti-diagonal.

 

Example 1:


Input: mat = [[0,1,1,0],[0,1,1,0],[0,0,0,1]]
Output: 3
Example 2:


Input: mat = [[1,1,1,1],[0,1,1,0],[0,0,0,1]]
Output: 4
 

Constraints:

m == mat.length
n == mat[i].length
1 <= m, n <= 104
1 <= m * n <= 104
mat[i][j] is either 0 or 1.
 */

class Solution {
    
    private class NodeInfo {
        int diagonalLength;
        int verticalLength;
        int antiDiagonalLength;
        int horizontalLength;
        
        NodeInfo(int diagonalLength, int verticalLength, int antiDiagonalLength, int horizontalLength) {
            this.diagonalLength = diagonalLength;
            this.verticalLength = verticalLength;
            this.antiDiagonalLength = antiDiagonalLength;
            this.horizontalLength = horizontalLength;
        }
        
        int getIndex(int k) {
            switch(k) {
                case 0:
                    return diagonalLength;
                case 1:
                    return verticalLength;
                case 2:
                    return antiDiagonalLength;
                case 3:
                    return horizontalLength;
                default:
                    return -1;
            }
        }
    }
    
    int[] dx = new int[] { -1, -1, -1, 0 };
    int[] dy = new int[] { -1, 0, 1, -1 };
    
    // check for a grid that is 1) within the matrix and 2) its value == 1
    private boolean isValidOnes(int[][] mat, int r, int c) {
        if (r < 0 || r >= mat.length || c < 0 || c >= mat[0].length) {
            return false;
        }
        
        return mat[r][c] == 1;
    }
    
    private int maxOfFour(int[] arr) {
        return Math.max(arr[0], Math.max(arr[1], Math.max(arr[2], arr[3])));
    }
    
    public int longestLine(int[][] mat) {
        int rows = mat.length;
        int cols = mat[0].length;
        
        NodeInfo[][] dp = new NodeInfo[rows][cols];
        
        int res = 0; // starting result, in case there is no 1s in the matrix
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (mat[i][j] == 1) {
                    // look at the 4 directions
                    int[] lengths =  new int[] { 1, 1, 1, 1};
                    for (int k = 0; k < 4; k++) {
                        int newRow = i + dx[k];
                        int newCol = j + dy[k];
                        if (isValidOnes(mat, newRow, newCol)) {
                            NodeInfo neighbour = dp[newRow][newCol];
                            lengths[k] = Math.max(lengths[k], neighbour.getIndex(k) + 1);
                        }
                    }
                    
                    NodeInfo curr = new NodeInfo(lengths[0], lengths[1], lengths[2], lengths[3]);
                    dp[i][j] = curr;
                    res = Math.max(res, maxOfFour(lengths));
                }
            }
        }
        
        
        return res;
    }
}

/**
DP idea:
Use a 2D matrix, dp[][]

dp[i][j] := info on the longest line of consecutive one ending at (i,j)

Since a grid can be a part of a horizontal, vertical, diagonal or anti-diagonal,
which can be of different length up to the element at (i,j)
--> each dp[i][j] needs to store info on the length of these 4 directions

--> Design a special class containing info on the longest line of 1) horizontal, 2) vertial, 3) diagonal and 4) anti-diagonal , ending at element (i,j)

for any random node (i, j):
- need to look at:
    (i - 1, j - 1) : diagonal
    (i - 1, j): vertical
    (i - 1, j + 1) : anti-diagonal
    (i, j - 1): horizontal
- and update along

time complexity: O(mn)
space consumption: O(4mn) = O(mn)



*/