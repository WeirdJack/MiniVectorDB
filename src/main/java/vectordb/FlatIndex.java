package vectordb;

import java.util.*;

public class FlatIndex {

    private Map<String, double[]> storedVectorMap = new HashMap<>();

    public void insert(String id, double[] vector){
        storedVectorMap.put(id, vector);
    }

    public List<SearchResult> search(double[] queryVector, int k){

        PriorityQueue<SearchResult> minHeap = new PriorityQueue<SearchResult>(
                (a,b) -> Double.compare(a.score(), b.score())
        );
        for (Map.Entry<String, double[]> entry : storedVectorMap.entrySet()) {
            double currScore = VectorMath.cosineSimilarity(queryVector, entry.getValue());
            if (minHeap.size() < k)
                minHeap.offer(new SearchResult(entry.getKey(), currScore));
            else{
                if (minHeap.peek().score() < currScore){
                    minHeap.poll();
                    minHeap.offer(new SearchResult(entry.getKey(), currScore));
                }
            }

        }

        List<SearchResult> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(minHeap.poll());  // comes out smallest-first
        }
        Collections.reverse(result);

        return result;
    }
}
