/**
 * This class creates a B-tree that stores aBall objects
 * 
 * @author tamara
 *
 */

public class bTree {
	//Instance variables and parameters
	aBall root=null;   															
    private double DeltaSize=0.1;												
	double lastSize=0;
	double X;
	double Y;
	aBall ball;
	
/**
 * 	addNode method - Wrapper method for makeNode and enter
 * adds aBall as a node in the tree
 * @param iBall
 */
	
	public void addNode(aBall iBall) {	
		//if the tree is empty create a node and set it as the root
		if (root == null) {
			root = makeNode(iBall);			
		}		
		else {
		//if the tree is not empty, add the ball created
			enter(iBall);			
		}
	}

/**
 * The makeNode method makes the aBall a node
 * @param iBall
 * @return the new node (tNode)
 */
	public aBall makeNode( aBall iBall) {										
		aBall tNode= iBall;
		tNode.right= null; 							//sets the right successor of the node to null
		tNode.left= null;							//sets the left successor of the node to null
		return tNode;			 
	}
/**
 * The method enter inserts the aBalls created to the tree 
 * depending on their size 	
 * @param iBall
 */
	public void enter (aBall iBall) {											 
		ball = root; 										
		while (true) {										//loop to store the nodes in the tree depending on their ball sizes
			if (iBall.getbSize() < ball.getbSize()) {		//if the data of the new node is less than the data of the root,
				if (ball.left==null) {						//traverse to the left and store the new node
					ball.left=makeNode(iBall);
					break;
				}
				else {
					ball = ball.left;						
				}
			}
			else {										//if the data of the new node is greater than or equal to the data of the
				if(ball.right==null){					//root, then traverse to the right and store the new node
					ball.right= makeNode(iBall);
					break;
				}
				else { 
					ball = ball.right;
				}
			}
		}
	}
			
/**
 * inorder method - inorder traversal via call to recursive method
 */
	
	public void inorder() {														// Hides recursion from user
		traverse_inorder(root);
	}
	
/**
 * traverse_inorder method - recursively traverses tree in order (LEFT-Root-RIGHT)
 */
	
	private void traverse_inorder(aBall root) {
		if (root.left != null) traverse_inorder(root.left);
		if ((root.getbSize()-this.lastSize)> DeltaSize) { 					//condition to create a new Stack
			X+= lastSize +root.getbSize();									//X position for a new stack of balls
			lastSize=root.getbSize();										//update last size
			Y=root.getbSize();												//change of Y for different balls in the same stack 
			root.moveTo(X,Y);		
		}
		else {																//if the stack exists already
			Y=Y+root.getbSize() + lastSize;									//Stacking the balls
			lastSize= root.getbSize();
			root.moveTo(X,Y);			
		}
		if (root.right != null) traverse_inorder(root.right);
	}
/**
 * isRunning is the Wrapper method for isRunningRec
 * @return isRunningRec(this.root)
 */
	boolean isRunning() {													//determines if the simulation is running (if the balls stopped or no)
		return isRunningRec(this.root);		
	}
	
	public boolean isRunningRec (aBall root) { 								//check if the simulation is running by checking
		boolean right= false;												//the nodes in the tree
		boolean left= false;
	
		if (root.left!= null) {
			left = isRunningRec (root.left);
		}
		if (root.SIMRunning == true) {
			return true;
		}
		if (root.right != null) {
			right=isRunningRec(root.right);		
		}
		return (right||left);
	}
	
	//Stacking the bSim balls method 
	public void stackBalls(bSim link) {											
		traverse_inorder (link.myTree.root);	
		X = 0;
		Y = 0;
		lastSize = 0;
	}
/**
 * the pauseTraversal method traverses the tree and stops the balls from
 * moving. It changes the root(ball)'s state to false 
 * @param root
 */

	public void pauseTraversal(aBall root) {
		if (root.left != null) pauseTraversal(root.left);
		root.setBallState(false);
		if (root.right != null) pauseTraversal(root.right);
	}
/**
 * stopSim() is the Wrapper method for pauseTraversal
 * it hides the recursion from the user	
 */
	public void stopSim() {
		this.pauseTraversal(this.root);
	}
}

//nested class
class bNode {
	double Size;
	bNode left;
	bNode right;
}




