package ali.wordRecords;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CalculateWords implements Callable<Map>
{

    private ConcurrentLinkedQueue<RandomAccessFile> fileQueue;
    private CharTreeNode root;

    public CalculateWords(ConcurrentLinkedQueue<RandomAccessFile> fileQueue){
        this.fileQueue = fileQueue;
    }

    public Map call() {
        System.out.println(Thread.currentThread().getName() + " start to count words");
        return calculate();
    }

    public Map calculate() {
        try {
            while (!fileQueue.isEmpty()) {
                RandomAccessFile curFile = fileQueue.poll();
                FileChannel channel = curFile.getChannel();
                long size = channel.size();
                ByteBuffer data = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
                buildCharTree(data);
            }
        } catch (Exception e) {

        }
        Map result = new HashMap();
        getWordCountFromCharTree(result, root);
        return result;
    }

    private CharTreeNode buildCharTree(ByteBuffer data){
        if (root == null) {
            root = new CharTreeNode();
        }
        CharTreeNode node = root;
        char curChar = ' ';
        while (data.hasRemaining()) {
            curChar = (char)data.get();
            if(curChar >= 'A' && curChar <= 'Z') {
                curChar = Character.toLowerCase(curChar);
            }
            if(curChar >= 'a' && curChar <= 'z'){
                if(node.children[curChar-'a'] == null)
                    node.children[curChar-'a'] = new CharTreeNode();
                node = node.children[curChar-'a'];
            } else {
                node.count++;
                node = root;
            }
        }

        if(curChar >= 'a' && curChar <= 'z')
            node.count++;
        return root;
    }

    private static void getWordCountFromCharTree(Map result, CharTreeNode root){
        getWordCountFromCharTree(result,root,new char[100],0);
    }

    private static void getWordCountFromCharTree(Map result, CharTreeNode root, char[] buffer, int pos){
        for(int i = 0; i < 26; ++i){
            if(root.children[i] != null){
                buffer[pos] = (char)(i + 'a');
                if(root.children[i].count > 0){
                    result.put(String.valueOf(buffer,0,pos+1), root.children[i].count);
                }
                getWordCountFromCharTree(result,root.children[i],buffer,pos+1);
            }
        }
    }

}
