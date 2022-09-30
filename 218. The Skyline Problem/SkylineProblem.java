/**
A city's skyline is the outer contour of the silhouette formed by all the buildings in that city when viewed from a distance. Given the locations and heights of all the buildings, return the skyline formed by these buildings collectively.

The geometric information of each building is given in the array buildings where buildings[i] = [lefti, righti, heighti]:

lefti is the x coordinate of the left edge of the ith building.
righti is the x coordinate of the right edge of the ith building.
heighti is the height of the ith building.
You may assume all buildings are perfect rectangles grounded on an absolutely flat surface at height 0.

The skyline should be represented as a list of "key points" sorted by their x-coordinate in the form [[x1,y1],[x2,y2],...]. Each key point is the left endpoint of some horizontal segment in the skyline except the last point in the list, which always has a y-coordinate 0 and is used to mark the skyline's termination where the rightmost building ends. Any ground between the leftmost and rightmost buildings should be part of the skyline's contour.

Note: There must be no consecutive horizontal lines of equal height in the output skyline. For instance, [...,[2 3],[4 5],[7 5],[11 5],[12 7],...] is not acceptable; the three lines of height 5 should be merged into one in the final output as such: [...,[2 3],[4 5],[12 7],...]

 

Example 1:


Input: buildings = [[2,9,10],[3,7,15],[5,12,12],[15,20,10],[19,24,8]]
Output: [[2,10],[3,15],[7,12],[12,0],[15,10],[20,8],[24,0]]
Explanation:
Figure A shows the buildings of the input.
Figure B shows the skyline formed by those buildings. The red points in figure B represent the key points in the output list.
Example 2:

Input: buildings = [[0,2,3],[2,5,3]]
Output: [[0,3],[5,0]]

Constraint:
1 <= buildings.length <= 104
0 <= lefti < righti <= 231 - 1
1 <= heighti <= 231 - 1
buildings is sorted by lefti in non-decreasing order
 */

 class Solution {
    private class Item {
        int index;
        int coordinate;
        int height;
        boolean isStart;
        
        Item(int index, int coordinate, int height, boolean isStart) {
            this.index = index;
            this.coordinate = coordinate;
            this.height = height;
            this.isStart = isStart;
        }
    }
    
    // max-heap in terms of height, keep track of valid building as well (building has not ended)
    private class CustomisedHeightQueue {
        PriorityQueue<Integer[]> queue;
        Set<Integer> validBuildings;
        
        CustomisedHeightQueue() {
            this.queue = new PriorityQueue<>((a,b) -> {
                if (a[1] == b[1]) {
                    return a[0] - b[0]; // tie break by index (the earlybuilding is prioritized)
                } else {
                    return b[1] - a[1]; // order by height (descending order)
                }
            });
            
            this.validBuildings = new HashSet<>();
            
            // adding ground level
            queue.add(new Integer[] {-1, 0 });
            validBuildings.add(-1);
        }
        
        // add an item, starting point of a building
        void add(int index, int height) {
            validBuildings.add(index);
            Integer[] item = new Integer[] { index, height };
            queue.add(item);
        }
        
        // peek to return the highest building so far, also help to invalidate removed building
        int peek() {
            // ground level
            if (this.queue.size() == 1) {
                return 0;
            }
            
            while (!validBuildings.contains(this.queue.peek()[0])) {
                queue.poll();
            }
            
            return queue.peek()[1];
        }
        
        void remove(int index) {
            validBuildings.remove(index);
        }
        
    }
    public List<List<Integer>> getSkyline(int[][] buildings) {
        PriorityQueue<Item> pq = new PriorityQueue<>((a,b)-> {
            if (a.coordinate == b.coordinate) {
                if (a.isStart && !b.isStart) {
                    return -1;
                }
                
                if (b.isStart && !a.isStart) {
                    return 1;
                }
                if (a.height != b.height) {
                    if (a.isStart && b.isStart) {
                        return b.height - a.height;
                    } else if (!a.isStart && !b.isStart) {
                        return a.height - b.height;
                    } else {
                        return b.height - a.height;
                    }
                } else {
                    return 0; // the rest the same, equal
                }
            } else {
                return a.coordinate - b.coordinate;
            }
        });
        
        // populate the pq
        for (int i = 0; i < buildings.length; i++) {
            int index = i;
            int[] building = buildings[i];
            int start = building[0];
            int end = building[1];
            int height = building[2];
            
            // add start point
            Item starting = new Item(i, start, height, true);
            Item ending = new Item(i, end, height, false);
            
            pq.add(starting);
            pq.add(ending);
        }
        
        List<List<Integer>> res = new ArrayList<>();
        CustomisedHeightQueue highestSoFar = new CustomisedHeightQueue();
        // process each point one by one
        while (!pq.isEmpty()) {
            Item item = pq.poll();
            
            if (item.isStart) {
                if (item.height > highestSoFar.peek()) {
                    List<Integer> nextPoint = new ArrayList<>(Arrays.asList(item.coordinate, item.height));
                    res.add(nextPoint);
                }
                
                // add entry into the customised queue
                highestSoFar.add(item.index, item.height);
            } else {
                highestSoFar.remove(item.index);
                // will be a new point in res if the current building is higher than the rest
                if (item.height > highestSoFar.peek()) {
                    List<Integer> nextPoint = new ArrayList<>(Arrays.asList(item.coordinate, highestSoFar.peek()));
                    res.add(nextPoint);
                }
            }
        }
        
        return res;
    }
}

/**
buildings is sorted by lefti in non-decreasing order.

process each ending point (left n right) from the order from left to right, min heap
for each point, store the building index + the height + type of point (start/finish)

for instance, [2,9,10],[3,7,15]
entry = [index, x-coordinate, height]
queue = [[0,2,10],[0,9,10]] after processing first building
queue = [[0,2,10], [1,3,15],[1,7,15],[0,9,10]]

after populating the min-heap, process all the item in the queue
highestSoFar needs to be a queue, max heap ordered by the building's height

time complexity: O(nlogn), each building point is added and removed once from both pq
space consumption O(n): additional space for the 2 pq and the hash map

*/