package ali.wordRecords;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.*;

public class CountWord {
    private ConcurrentLinkedQueue<RandomAccessFile> filesQueue;
    private final static Integer SORTED_NUMBER = 4;

    public CountWord(ConcurrentLinkedQueue<RandomAccessFile>  files) {
        this.filesQueue = files;
    }

    public List<Map.Entry>  countWordForFile() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        List<Future<Map>> futureList = new LinkedList<>();

        for (int i = 0; i < 5 ; i++) {
            Future<Map> resultList = fixedThreadPool.submit(new CalculateWords(filesQueue));
            futureList.add(resultList);
        }

        try {
            // waiting at least one thread finish job
            Thread.sleep(3 * 1000);
        } catch (Exception e) {

        }

        System.out.println("Main thread will try to get result:");

        Map<String, Integer> res = getFutureRes(futureList);

        return getSortedListByCount(res);
    }

    private Map<String, Integer> getFutureRes(List<Future<Map>> futureList) {
        Map<String, Integer> calResult = new HashMap<>();
        while(!futureList.isEmpty()) {
            Iterator<Future<Map>> iterator = futureList.iterator();
            try {
                while (iterator.hasNext()) {
                    Future<Map> futureRes = iterator.next();
                    if (futureRes.isDone()) {
                        System.out.println("get one future date successfully");
                        Map<String, Integer> result = futureRes.get();
                        for(String word : result.keySet()) {
                            if (calResult.containsKey(word)) {
                                Integer count = calResult.get(word) + result.get(word);
                                calResult.put(word, count);
                            } else {
                                calResult.put(word, result.get(word));
                            }
                        }
                        iterator.remove();
                    }
                }
            }catch (Exception e) {
                System.out.println("Ops! Here is an exception" + e.getStackTrace());
            }
        }
        return calResult;
    }

    private List getSortedListByCount(Map<String, Integer> res) {
        List<Map.Entry<String,Integer>> list = new ArrayList<>();
        list.addAll(res.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue() - o1.getValue());

        List<Map.Entry>  sortedListRes = new ArrayList<>();
        int count = 0;
        while(count < SORTED_NUMBER) {
            sortedListRes.add(list.get(count));
            count++;
        }
        return sortedListRes;
    }

    public static void main(String[] args) throws Exception{

        ConcurrentLinkedQueue<RandomAccessFile> files = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < 6; i++) {
            String filePath = "./files/article" + i + ".txt";
            File file = new File(filePath);
            files.add(new RandomAccessFile(file,"r"));
        }

        CountWord countWord = new CountWord(files);

        List<Map.Entry> res = countWord.countWordForFile();

        for (int i = 0; i < res.size() ; i++) {
            System.out.println(res.get(i).getKey() + ":" + res.get(i).getValue());
        }

    }

}
