package vectordb.hnsw;

import vectordb.FlatIndex;
import vectordb.SearchResult;
import vectordb.VectorMath;

import java.util.*;

public class HnswIndex {
    Map<String, Node> nodesById;
    Node entryPoint;
    int maxLayer;
    int M = 16;
    double levelMultiplier = 1 / Math.log(M);

    public HnswIndex() {
        // initialize nodesById
        nodesById = new HashMap<>();

        // initialize entryPoint
        entryPoint = null;

        // initialize maxLayer
        maxLayer = -1;
    }

    private int randomLayer(){ return (int) Math.floor(-Math.log(Math.random()) * levelMultiplier); }

    public void insert(String id, double[] vector){
        Node newNode = new Node(id, vector, new HashMap<>());
        if (entryPoint == null){
            entryPoint = newNode;
            maxLayer = randomLayer();
            nodesById.put(id, newNode);
            return;
        }

        Node currentClosest = entryPoint;
        int newNodeLayer = randomLayer();

        for (int layer = maxLayer; layer > newNodeLayer; layer--) {
            // at this layer, search for the closest node to `vector`, starting from currentClosest
            // update currentClosest to be that result
            Set<Node> nodeSet = currentClosest.connections.getOrDefault(layer, new HashSet<>());
            double distance = Double.NEGATIVE_INFINITY;
            for (Node nodeCurrLayer : nodeSet){
                double currDistance = VectorMath.cosineSimilarity(nodeCurrLayer.vector, vector);
                if (distance < currDistance){
                    currentClosest = nodeCurrLayer;
                    distance = currDistance;
                }
            }
        }

        for (int layer = newNodeLayer; layer >= 0; layer--) {
            double distance = Double.NEGATIVE_INFINITY;
            Set<Node> connectionsNodeSet = currentClosest.connections.getOrDefault(layer, new HashSet<>());
            double currDistanceConnections = VectorMath.cosineSimilarity(currentClosest.vector, vector);
            Set<Node> newNodeSet = newNode.connections.getOrDefault(layer, new HashSet<>());
            if (distance < currDistanceConnections){
                distance = currDistanceConnections;
                connectionsNodeSet = currentClosest.connections.getOrDefault(layer, new HashSet<>());
                newNodeSet.add(currentClosest);
            }
            for (Node nodeCurrLayerConnections : connectionsNodeSet){
                if (distance < currDistanceConnections){
                    currentClosest = nodeCurrLayerConnections;
                    distance = currDistanceConnections;
                    connectionsNodeSet = currentClosest.connections.getOrDefault(layer, new HashSet<>());
                }
                currDistanceConnections = VectorMath.cosineSimilarity(nodeCurrLayerConnections.vector, vector);
            }

            newNodeSet.add(currentClosest);
            newNode.connections.put(layer, newNodeSet);
            connectionsNodeSet.add(newNode);
            currentClosest.connections.put(layer, connectionsNodeSet);
        }

        nodesById.put(id, newNode);

        if (maxLayer < newNodeLayer){
            maxLayer = newNodeLayer;
            entryPoint = newNode;
        }
    }

    public static void main(String[] args){
        HnswIndex hnswIndex = new HnswIndex();
//        int count = 30;
//        while (count != 0){
//            System.out.println(hnswIndex.randomLayer());
//            count--;
//        }

        hnswIndex.insert("a", new double[]{1, 0});
        hnswIndex.insert("b", new double[]{0, 1});
        hnswIndex.insert("c", new double[]{0.9, 0.1});
        hnswIndex.insert("d", new double[]{0.95, 0.05});
        for (Node node : hnswIndex.nodesById.values()) {
            System.out.println("id: " + node.id);
            for (Map.Entry<Integer, Set<Node>> entry : node.connections.entrySet()){
                System.out.println("  layer " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}

class Node {
    String id;
    double[] vector;
    Map<Integer, Set<Node>> connections;  // layer number -> neighbor set at that layer

    public Node(String id, double[] vector, Map<Integer, Set<Node>> connections){
        this.id = id;
        this.vector = vector;
        this.connections = connections;
    }

    @Override
    public String toString() {
        return "Node{id=" + id + "}";
    }
}
