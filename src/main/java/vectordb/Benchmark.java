package vectordb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Benchmark {

    int dimensions = 128;
    int k = 10;
    Random rand = new Random(42); // fixed seed - reproducible across runs

    public List<double[]> generateRandomVectors(int count, int dimensions){
        List<double[]> randomVectors = new ArrayList<>();

        while (count != 0){
            double[] d = new double[dimensions];
            for (int i = 0; i < dimensions; i++){
                d[i] = rand.nextDouble();
            }
            randomVectors.add(d);
            count--;
        }

        return randomVectors;
    }

    public void measureLatency(FlatIndex index, List<double[]> queryVectors, int k){
        int size = queryVectors.size();
        long[] latencies = new long[size];
        long latencySum = 0;
        for (double[] x : queryVectors){
            long startTime = System.nanoTime();
            index.search(x, k);
            long endTime = System.nanoTime();
            long latency = endTime - startTime;
            latencies[--size] = latency;
            System.out.println("latency: " + latency);
        }

        Arrays.sort(latencies);
        System.out.println("max: " + latencies[queryVectors.size() - 1] + " min: " + latencies[0]);
        System.out.println("Average Latency: " + (double) latencySum/latencies.length);
    }

    public static void main(String[] args){
        Benchmark benchmark = new Benchmark();
        List<double[]> queryVectorsList = benchmark.generateRandomVectors(20, benchmark.dimensions);
        List<double[]> storedVectorsList = benchmark.generateRandomVectors(100, benchmark.dimensions);

        FlatIndex flatIndex = new FlatIndex();
        for (int i = 0; i < storedVectorsList.size(); i++){
            flatIndex.insert(String.valueOf(i), storedVectorsList.get(i));
        }

        benchmark.measureLatency(flatIndex, queryVectorsList, benchmark.k);
    }
}
