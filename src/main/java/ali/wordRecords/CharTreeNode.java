package ali.wordRecords;

public class CharTreeNode {
    public int count = 0;
    public CharTreeNode[] children;

    public CharTreeNode() {
        count = 0;
        children = new CharTreeNode[26];
    }
}
