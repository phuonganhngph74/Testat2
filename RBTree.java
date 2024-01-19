import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

// Class: RBTree
public class RBTree<T extends Comparable<T>> {

    private static final boolean RED = false;
    private static final boolean BLACK = true;

    // Class: RBNode
    class RBNode<T extends Comparable<T>> {
        public T key;
        public RBNode<T> parent;
        public RBNode<T> left;
        public RBNode<T> right;
        public boolean color;

        public RBNode() {
            this.parent = null;
            this.left = null;
            this.right = null;
            this.color = BLACK;
        }

        public RBNode(T key) {
            this();
            this.key = key;
        }
    }

    // Root initialized to nil
    private RBNode<T> nil = new RBNode<T>();
    private RBNode<T> root = nil;

    // Constructor:
    public RBTree() {
        root.left = nil;
        root.right = nil;
        root.parent = nil;
    }

    /**leftRotate: Takes a node x and rotates it left, 
    *@param node: The node to rotate left
    */
    private void leftRotate(RBNode<T> node) {
        RBNode<T> parent = node.parent;
        RBNode<T> rightChild = node.right;

        node.right = rightChild.left;
        if (!isNil(rightChild.left)) {
            rightChild.left.parent = node;
        }

        rightChild.left = node;
        node.parent = rightChild;

        replaceParentsChild(parent, node, rightChild);
    }
       

    /**rightRotate: Takes a node x and rotates it right
    *@param x: The node to rotate right
    */

    private void rightRotate(RBNode<T> node) {
        RBNode<T> parent = node.parent;
        RBNode<T> leftChild = node.left;

        node.left = leftChild.right;
        if (!isNil(leftChild.right)) {
            leftChild.right.parent = node;
        }

        leftChild.right = node;
        node.parent = leftChild;

        replaceParentsChild(parent, node, leftChild);
    }

    /**replaceParentsChild: Replaces the parent's child with the new child
     * @param parent: The parent of the node
     * @param oldChild: The old child of the parent
     * @param newChild: The new child of the parent
     */

    private void replaceParentsChild(RBNode<T> parent, RBNode<T> oldChild, RBNode<T> newChild) {
        if (isNil(parent)) {
            root = newChild;
        } else if (parent.left == oldChild) {
            parent.left = newChild;
        } else if (parent.right == oldChild){
            parent.right = newChild;
        } else {
            throw new RuntimeException("Parent does not have child");
        }

        if (!isNil(newChild)) {
            newChild.parent = parent;
        }
    }

    /**insert: Inserts the node into the tree
     * @param T: The key of the node to insert
     */
    public void insert(T key) {
        RBNode<T> node = root;
        RBNode<T> parent = nil;

        System.out.println("newNode before fixed " +key);
        //traverse the tree to find the insertion point
        while(!isNil(node)) {
            parent = node;
            if(key.compareTo(node.key) < 0) {
                node = node.left;
            } else if (key.compareTo(node.key) > 0) {
                node = node.right;
            } else {
                throw new RuntimeException("BST already contains a node with key" + node.key);
            }
        }

        //set parent and insert node
        RBNode<T> newNode = new RBNode<T>(key);
        newNode.color = RED;

        if (isNil(parent)) {
            root = newNode;
        } else if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else if (key.compareTo(parent.key) > 0){
            parent.right = newNode;
        }
 
        newNode.parent = parent;
        if (isNil(newNode.left)) {
            newNode.left = nil;
        }

        if (isNil(newNode.right)) {
            newNode.right = nil;
        }
        
        fixRedBlackPropertiesAfterInsert(newNode);

       /*  List<RBNode<T>> nodeList = bfsTraversal(root);
        System.out.println("BFS Traversal: ");
        for (RBNode<T> n : nodeList) {
            System.out.println(n.key);
        } */
    }

    /**fixRedBlackPropertiesAfterInsert: Fixes the red black properties after inserting a node
     * @param node: The node to fix
     */
    private void fixRedBlackPropertiesAfterInsert(RBNode<T> node) {
        RBNode<T> parent = node.parent;

        //Case 1: Parent is null, newNode is the node, end of recursion
        if (isNil(parent)) {
            node.color = BLACK;
            //System.out.println("Case 1: Parent is null, newNode is the node, end of recursion");
            return;
        }
        //Parent is black, no need to fix
        if (parent.color == BLACK) {
            //System.out.println("Case 1.2: Parent is black, no need to fix");
            return;
        }

        // Parent is red, need to fix
        RBNode<T> grandparent = parent.parent;
        //Case 2:Parent is red and the root
        if (isNil(grandparent)) {
            parent.color = BLACK;
            //System.out.println("Case 2: Parent is red and the root");
            return;
        }

        //Case 3: Parent and uncle are red --> recolor parent, uncle, and grandparent
        RBNode<T> uncle = getUncle(node.parent);
        if(!isNil(uncle) && uncle.color == RED) {
            parent.color = BLACK;
            uncle.color = BLACK;
            grandparent.color = RED;
            fixRedBlackPropertiesAfterInsert(grandparent);
            /* System.out.println("Case 3: Parent and uncle are red --> recolor parent, uncle, and grandparent"); */
        
        //parent is left child of grandparent
        } else if (parent == grandparent.left) {
        //Case 4a: Uncle is black and node is left to right inner child of its grandparent
            if (node == parent.right) {
                leftRotate(parent);
                parent = node;
                /* System.out.println("Case 4a: Uncle is black and node is left to right inner child of its grandparent"); */
            }
            //Case 5a: Uncle is black and node is left to left inner child of its grandparent
            rightRotate(grandparent);
            parent.color = BLACK;
            grandparent.color = RED;
            //System.out.println("Case 5a: Uncle is black and node is left to left inner child of its grandparent");
        
        //parent is right child of grandparent
        } else {
            //Case 4b: Uncle is black and node is right to left inner child of its grandparent
            if (node == parent.left) {
                rightRotate(parent);
                parent = node;
                //System.out.println("Case 4b: Uncle is black and node is right to left inner child of its grandparent");
            }
            //Case 5b: Uncle is black and node is right to right inner child of its grandparent
            leftRotate(grandparent);
            parent.color = BLACK;
            grandparent.color = RED;
            //System.out.println("Case 5b: Uncle is black and node is right to right inner child of its grandparent");
        }
    }

    /**getUncle: Returns the uncle of the node
     * @param node: The node to get the uncle of
     * @return RBNode<T>: The uncle of the node
     */
    private RBNode<T> getUncle(RBNode<T> parent) {
        RBNode<T> grandparent = parent.parent;
        if (grandparent.left == parent) {
            return grandparent.right;
        } else if (grandparent.right == parent){
            return grandparent.left;
        } else {
            throw new RuntimeException("The node has no grandparent");
        }
    }

    /**isNil: Returns true if the node is nil
     * @param node: The node to check
     * @return boolean: True if the node is nil
     */
    private boolean isNil(RBNode<T> node) {
        return node == nil || node == null;
    }

    /**countNILNodes: Counts the number of nil nodes in the tree
     * @return int: The number of nil nodes in the tree
     */
    public int countNILNodes(){
        return countNILNodes(root);
    }

    /**countNILNodes: Counts the number of nil nodes in the tree
     * @param node: The node to start counting from
     * @return int: The number of nil nodes in the tree
     */
    private int countNILNodes(RBNode<T> node){
        if(isNil(node)){
            return 1;
        }

        int count = 0;

        if (isNil(node.left)){
            count++;
        } else {
            count += countNILNodes(node.left);
        }

        if (isNil(node.right)){
            count++;
        } else {
            count += countNILNodes(node.right);
        }
        return count;
    }


    /**getRedNodes: Returns a string of all red nodes in the tree
     * @return String: A string of all red nodes in the tree
     */
    public String getRedNodes() {
        List<T> redNodes = new ArrayList<>();
        collectRedNodes(root, redNodes);
        return redNodes.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

    /**collectRedNodes: Collects all red nodes in the tree
     * @param node: The node to start collecting from
     * @param redNodes: The list of red nodes
     */
    private void collectRedNodes(RBNode<T> node, List<T> redNodes) {
        if (!isNil(node)) {
            if (node.color == RED) {
                redNodes.add(node.key);
            }
            collectRedNodes(node.left, redNodes);
            collectRedNodes(node.right, redNodes);
        }
    }

    /**bfsTraversal: Returns a list of nodes in breadth-first order
     * @param root: The root of the tree
     * @return List<RBNode<T>>: A list of nodes in breadth-first order
     */
    public List<RBNode<T>> bfsTraversal(RBNode<T> root) {
        List<RBNode<T>> result = new ArrayList<>();

        LinkedList<RBNode<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            RBNode<T> currentNode = queue.poll();
            result.add(currentNode);

            if (!isNil(currentNode.left)) {
                queue.add(currentNode.left);
            }

            if (!isNil(currentNode.right)) {
                queue.add(currentNode.right);
            }
        } 
        return result;
    }

    /**printDOT: Prints the tree in DOT format
     * @param filename: The name of the file to print to
     */
    public void printDOT(String filename){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("digraph RBTree {\n");
            writer.write("\tgraph [ratio=.48];\n");
            writer.write("\tnode [style=filled, color=black, shape=circle, width=.6, \n\t\tfontname=Helvetica, fontweight=bold, fontcolor=white, \n\t\tfontsize=24, fixedsize=true];\n\n");

            List<RBNode<T>> nodeList = bfsTraversal(root);
            //System.out.println(nodeList);

            // write nodes into file and connect them with their children
            int count = 1;
            for (RBNode<T> n : nodeList) {
                //System.out.println(n.key);
                if (!isNil(n.left)) {
                    writer.write("\t" + n.key + " -> " + n.left.key + ";\n");
                } else {
                    writer.write("\t" + n.key + " -> " + "n" + count + ";\n");
                    count++;
                }

                if (!isNil(n.right)) {
                    writer.write("\t" + n.key + " -> " + n.right.key + ";\n");
                } else {
                    writer.write("\t" + n.key + " -> " + "n" + count + ";\n");
                    count++;
                }
            }
            

            //get red nodes and color them red
            String redNodes = getRedNodes();
            writer.write("\t"+ redNodes + "\n");
            writer.write("\t[fillcolor=red];\n\n");

            // write nil nodes: first get number of nil nodes, then write them to file with label NIL and properties of theirs features
            int nilCount = 0;
            String nilNodes = "";
            for (RBNode<T> n : nodeList) {                
                if (isNil(n.right)) {
                    nilCount++;
                    nilNodes += "n" + nilCount + ", ";
                }

                if (isNil(n.left)) {
                    nilCount++;
                    nilNodes += "n" + nilCount + ", ";
                }
            }
            nilNodes = nilNodes.substring(0, nilNodes.length() - 2);
            writer.write("\t"+ nilNodes + "\n\t[label=\"NIL\", shape=record, width=.4, height=.25, fontsize=16];\n\n");
            
            writer.write("}\n");

            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    /**generatePDF: Generates a PDF from a dot file
     * @param dotFile: The dot file to convert
     * @param pdfFile: The name of the output PDF file
     */
    public void generatePDF (String dotFile, String pdfFile) {
        String command = "dot -Tpdf " + dotFile + " -o " + pdfFile;

        //execute command
        try {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
            Process process = builder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        //tree with characters
        RBTree<Character> tree = new RBTree<>();
        char[] arr = {'e','g','i','l','v'};
		for(int i=0;i<arr.length;i++) {
			tree.insert(arr[i]);
		}
        tree.printDOT("rbtree.dot");
        tree.generatePDF("rbtree.dot", "output.pdf");
        
        // tree with integers
        RBTree<Integer> tree2 = new RBTree<>();
        int[] arr2 = {81,3,80,0,26,25,30,82,53,73,27,65,49,88};
        for(int i=0;i<arr2.length;i++) {
            tree2.insert(arr2[i]);
        }
        tree2.printDOT("rbtree2.dot");
        tree2.generatePDF("rbtree2.dot", "output2.pdf");
    }

}
    


