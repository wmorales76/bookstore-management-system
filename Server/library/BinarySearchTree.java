package Server.library;

// Node class represents each node in the binary search tree
class Node {
    int key;       // The key or value contained in the node
    Node left;     // Pointer to the left child node
    Node right;    // Pointer to the right child node

    // Constructor to create a new node with a given key
    public Node(int item) {
        key = item;     // Set the key of the node
        left = right = null;  // Initially, left and right children are set to null
    }
}

// BinarySearchTree class manages all operations on the binary search tree
class BinarySearchTree {
    Node root;  // The root node of the tree, initially null

    // Constructor for the BinarySearchTree, initializes an empty tree
    BinarySearchTree() {
        root = null;  // Root is null indicating the tree is empty
    }

    // Public method to insert a new key into the BST
    void insert(int key) {
        root = insertRec(root, key);  // Start the recursive insertion from the root
    }

    // Helper recursive method to insert a new key in the BST starting from a given node
    Node insertRec(Node root, int key) {
        // If the starting node is null, we've found the place to create a new node
        if (root == null) {
            root = new Node(key);  // Create a new node with the key
            return root;           // Return the new node to be linked by the calling parent
        }

        // Decide to insert the key in the left or right subtree based on its value
        if (key < root.key) {
            root.left = insertRec(root.left, key);  // Insert in the left subtree
        } else if (key > root.key) {
            root.right = insertRec(root.right, key);  // Insert in the right subtree
        }

        // Return the unchanged node pointer to the parent after recursive insertion
        return root;
    }

    // Public method to start an inorder traversal of the BST
    void inorder() {
        inorderRec(root);  // Start recursive inorder traversal from the root
    }

    // Helper recursive method to perform inorder traversal of the BST
    void inorderRec(Node root) {
        if (root != null) {
            inorderRec(root.left);  // Traverse the left subtree
            System.out.print(root.key + " ");  // Visit the root node (print key)
            inorderRec(root.right);  // Traverse the right subtree
        }
    }

    // Utility function to search for a given key in the BST starting from a given node
    public Node search(Node root, int key) {
        // Base case: if the node is null or the node's key matches the search key
        if (root == null || root.key == key) {
            return root;  // Return the node if found, or null if not found
        }

        // Recursive search in either the left or right subtree based on the key's value
        if (key < root.key) {
            return search(root.left, key);  // Search in the left subtree
        } else {
            return search(root.right, key);  // Search in the right subtree
        }
    }

    // Main method to run the BST operations
    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();  // Create a new BST

        // Inserting nodes into the BST
        tree.insert(50);
        tree.insert(30);
        tree.insert(20);
        tree.insert(40);
        tree.insert(70);
        tree.insert(60);
        tree.insert(80);

        // Output the inorder traversal of the BST which shows keys in sorted order
        tree.inorder();
    }
}
