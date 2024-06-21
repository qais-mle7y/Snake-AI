import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import za.ac.wits.snake.DevelopmentAgent;

 class Box {
	int row;
	int col;
	Vector<Box> neighbours = new Vector<Box>();
	public Box(){
		
	}
	
	public Box(int col, int row){
		this.col = col;
		this.row = row;
	}
	
	void setNeighbours(int[][] board, int rows, int cols){
		
		Box above = new Box();
		above.row = row-1;
		above.col = col;
		validateBox(above, board, rows, cols);
		
		Box right = new Box();
		right.col = col+1;
		right.row = row;
		validateBox(right, board, rows, cols);
				
		Box below = new Box();
		below.row = row+1;
		below.col = col;
		validateBox(below, board, rows, cols);
				
		Box left = new Box();
		left.col = col-1;
		left.row = row;
		validateBox(left, board, rows, cols);
	
	}
	
	void validateBox(Box tmp, int[][] board, int rows, int cols){
		if (tmp.col>=0 && tmp.col<cols && tmp.row>=0 && tmp.row<rows)
			if (board[tmp.row][tmp.col]==0)
				neighbours.add(tmp);
	}
}


public class MyAgent extends DevelopmentAgent {
	
	public static double appleValue = 5.0;
	
    public static void main(String args[]) {
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }
    
    public static double calculateappleValue(int moves) {
        double decayRate = 0.1;
        double decayValue = Math.ceil(decayRate * moves);
        appleValue -= decayValue;        
        return appleValue;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
        	// setup game board
            String[] temp = br.readLine().split(" ");
            int nSnakes = Integer.parseInt(temp[0]);
            int width = Integer.parseInt(temp[1]);
            int height = Integer.parseInt(temp[2]);
            int[][] board = new int[height][width];
            int numObstacles = 3;
            int myMaxLength = 0;
            int moves = 0;

            while (true) {
            	// set the board to empty state
            	for (int i=0; i<height; i++)
        			for (int j=0; j<width; j++)
        				board[i][j] = 0;
            	
            	String[] appleInfo = br.readLine().split(" ");
            	
                if (appleInfo[0].contains("Game Over")) {
                	System.out.println("log " + Integer.toString(myMaxLength));
                    break;
                }
                Box apple = new Box( Integer.parseInt(appleInfo[0]) , Integer.parseInt(appleInfo[1]) );
        		board[apple.row][apple.col] = 0;
        		
        		double currententappleValue = calculateappleValue(moves); // 0 moves at the start
//        	    if (currententappleValue <0) {
//        	    	myMaxLength--;
//        	    	if(currententappleValue <= -4.0) {
//        	    	    Box respawnPosition = generateRandomPosition(board,width,height);
//        	    	    start = respawnPosition;
//        	    		myMaxLength =0;
//        	    	}
//        	    } else {
//        	        System.out.println("log " + Integer.toString(myMaxLength) + " " + String.valueOf(currententappleValue));
//        	    }
        	    moves++;
                
        		Box[] ObstacleHeads = new Box[numObstacles];
        		Box[] ObstacleTail = new Box[numObstacles];
        		
                for (int j=0; j<numObstacles; j++) {
                	String obsLine = br.readLine();
                	board = drawSnake(obsLine,j+5,board);
                	String[] SplitobsLine = obsLine.split(" ");
                	ObstacleHeads[j] = new Box (Integer.parseInt(SplitobsLine[0].split(",")[0]),Integer.parseInt(SplitobsLine[0].split(",")[1]));
                	ObstacleTail[j] = new Box (Integer.parseInt(SplitobsLine[1].split(",")[0]),Integer.parseInt(SplitobsLine[1].split(",")[1]));
                }
                
                int mySnakeNum = Integer.parseInt(br.readLine());
                LinkedList<Box> snakeHeads = new LinkedList<Box>();
                Box start = new Box();

                
                for (int i = 0; i < nSnakes; i++) {
                    String snakeLine = br.readLine();
                    if (snakeLine.contains("alive")){
        				int spaces = 0;
        				int index = 0;
        				for (int j=7; j<snakeLine.length(); j++){
        					if ( snakeLine.charAt(j) == ' ' )
        						spaces = spaces +1;
        					if (spaces == 2){
        						index = j+1;
        						break;
        					}
        				}
        				board = drawSnake(snakeLine.substring(index), i+1, board);
	                    if (i == mySnakeNum) {
	                    	int length = Integer.parseInt(snakeLine.split(" ")[1]);
                	       //System.out.println("log " + "1-length: " + Integer.toString(length));
	                    	if (currententappleValue <0) {
	                	    	length--;
	                	    	//System.out.println("log " + "2-length: " + Integer.toString(length));
	                	    	if(currententappleValue <= -4.0) {
	                	    		length =0;
//	                	    	    System.out.println("log " + "1-length: " + Integer.toString(length));
//		                	        System.out.println("log " + Integer.toString(myMaxLength) + " " + String.valueOf(currententappleValue));
	                	    	}
//	                	    } else {
//	                	        System.out.println("log " + Integer.toString(myMaxLength) + " " + String.valueOf(currententappleValue));
	                	    }
	                    	
        					if (length>=myMaxLength)
        						myMaxLength = length;
        					String[] meSnake = snakeLine.substring(index).split(" ");
        					start = new Box( Integer.parseInt(meSnake[0].split(",")[0]), Integer.parseInt(meSnake[0].split(",")[1]) );
	                    }
	                    if (i!=mySnakeNum){
	        				String[] SplitsnakeLine = snakeLine.substring(index).split(" ");
	        				snakeHeads.add( new Box( Integer.parseInt(SplitsnakeLine[0].split(",")[0]), Integer.parseInt(SplitsnakeLine[0].split(",")[1])));
        				}
	                }
                }
                
                for (Box o : ObstacleHeads){
        			o.setNeighbours(board, height, width);
        			for (Box b: o.neighbours)        				
        				board[b.row][b.col] = 8;
        		}
        		        		
        		for (Box s : snakeHeads){
        			s.setNeighbours(board, height, width);
        			for (Box n : s.neighbours) 
        				board[n.row][n.col] = 8;
        		}
        		board[apple.row][apple.col] = 0;
        		
        		int[][] board2 = new int[height][width];
        		for (int i=0; i<height; i++)
        			for (int j=0; j<width; j++)
        				board2[i][j] = board[i][j];
        		
        		
        		
                //finished reading, calculate move:
                Stack<Box> Path = BFS(board,width,height,start,apple,ObstacleHeads);
                if ( !Path.isEmpty() ){
	        		Box next = Path.peek();	        		
					printmove(next, start);
	        	}
	        	else{
	        		for (Box s : snakeHeads){
	        			s.neighbours.clear();
	        			s.setNeighbours(board2, height, width);
	        			for (Box o : s.neighbours)
	        				if (board2[o.row][o.col]==8)
	        					board2[o.row][o.col]=0;
	        		}
	        		start.neighbours.clear();
	        		start.setNeighbours(board2, height, width);
	        		if (!start.neighbours.isEmpty())
	        			printmove(start.neighbours.get(0), start);
		        	else
		        		System.out.println(5);
	        	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static int[][] drawSnake(String snake, int number, int[][] area){
		String[] coords = snake.split(" ");
		for (int i = 0; i < coords.length-1; i++) {
			area = drawLine(area, coords[i], coords[i+1], number);
		}
		return area;
	}
	
	static int[][] drawLine(int[][] area, String pointa, String pointb, int number){
		int maxRow, minRow, maxCol, minCol;
		String a[] = pointa.split(","), b[] = pointb.split(",");
		
		// get max and min of x
		if (Integer.parseInt(a[0]) >= Integer.parseInt(b[0])){
			maxCol = Integer.parseInt(a[0]);
			minCol = Integer.parseInt(b[0]);
		}
		else{
			maxCol = Integer.parseInt(b[0]);
			minCol = Integer.parseInt(a[0]);
		}
		
		// get max and min of y
		if (Integer.parseInt(a[1]) >= Integer.parseInt(b[1])){
			maxRow = Integer.parseInt(a[1]);
			minRow = Integer.parseInt(b[1]);
		}
		else{
			maxRow = Integer.parseInt(b[1]);
			minRow = Integer.parseInt(a[1]);
		}
		
		// fill up spaces in play area
		for (int i = minRow; i <= maxRow; i++)
			for (int j = minCol; j <= maxCol; j++)
				area[i][j] = number;
		
		return area;
	}
	
	
	static Stack<Box> BFS(int[][] board, int width, int height, Box start, Box apple, Box[] ObstacleHeads){
		
		Queue<Box> q = new LinkedList<>();
		Stack<Box> s = new Stack<>();
		Box[][] parent = new Box[width][height];
		int[][] distance = new int[width][height];
		Box current = new Box();
		
		distance[start.row][start.col] = 0;
		q.add(start);
		while( (!q.isEmpty()) || (board[apple.row][apple.col]==0)){
			if (q.isEmpty())
				return s;
			
			current = q.remove();
			current.setNeighbours(board, width, height);
			for (Box node : current.neighbours){
					board[node.row][node.col] = 8;
					parent[node.row][node.col] = current;
					distance[node.row][node.col] = distance[current.row][current.col] + 1;
					q.add(node);
			}
		}
		
		if ((board[apple.row][apple.col]==0)){
			return s;
		}
		else{
			Boolean isapple = false;
			int count = 0;
			
			for (Box o : ObstacleHeads){
				for (Box b : o.neighbours){
					if (b.row==apple.row && b.col==apple.col){
						isapple = true;
						break;
					}
				}
			}

			if (!isapple){
				current = apple;
				count = distance[apple.row][apple.col];
			}
		
			
			for (int i=1; i<count; i++){
				s.push(current);
				current = parent[current.row][current.col];
			}
			s.push(current);
			return s;
		}
	}
	
//	static Box generateRandomPosition(int[][] board, int height, int width) {
//	    Random random = new Random();
//	    int maxAttempts = height * width;
//	    int attempt = 0;
//	    while (attempt < maxAttempts) {
//	        int randomRow = random.nextInt(height);
//	        int randomCol = random.nextInt(width);
//	        if (board[randomRow][randomCol] == 0) {
//	            return new Box(randomCol, randomRow);
//	        }
//	        attempt++;
//	    }
//	    return new Box(-1, -1);
//	}
			
//		Queue<Box> q = new LinkedList<>();
//	    Box[][] parent = new Box[rows][cols];
//	    boolean[][] visited = new boolean[rows][cols];
//	
//	    q.add(start);
//	    visited[start.row][start.col] = true;
//	
//	    while (!q.isEmpty()) {
//	        Box currentent = q.remove();
//	
//	        if (currentent.row == apple.row && currentent.col == apple.col) {
//	            // apple reached, construct path
//	            Stack<Box> path = new Stack<>();
//	            while (currentent != start) {
//	                path.push(currentent);
//	                currentent = parent[currentent.row][currentent.col];
//	            }
//	            return path;
//	        }
//	
//	        currentent.setNeighbours(board, rows, cols);
//	        for (Box neighbor : currentent.neighbours) {
//	            if (!visited[neighbor.row][neighbor.col]) {
//	                visited[neighbor.row][neighbor.col] = true;
//	                parent[neighbor.row][neighbor.col] = currentent;
//	                q.add(neighbor);
//	            }
//	        }
//	    }
//	
//	    // If no path is found, return an empty stack
//	    return new Stack<>();
			
	
	
	void printmove(Box go, Box start){
		int move = 0;
		if (go.row>start.row)
			move = 1;
		else if (go.row<start.row)
			move = 0;
		else if (go.col<start.col)
			move = 2;
		else if (go.col>start.col)
			move = 3;
		System.out.println(move);
	}

}