package vectordb;

public class VectorMath {

    double currentMagnitude = 0.0;
    double currentCosineSimilarity = 0.0;

    public double dotProduct(double[] a, double[] b){
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "Vectors must have the same length: got " + a.length + " and " + b.length);
        }
        double currentDotProduct = 0.0;
        for(int i = 0; i < a.length; i++) currentDotProduct += a[i] * b[i];
        return currentDotProduct;
    }

    public double magnitude(double[] x){
        return Math.sqrt(dotProduct(x,x));
    }

    public double cosineSimilarity(double[] x, double[] y){
        if (magnitude(x) == 0 || magnitude(y) == 0) {
            throw new IllegalArgumentException(
                    "Vectors must have non zero magnitude: got magnitude(x): " + magnitude(x) + " and magnitude(y): " + magnitude(y));
        }
        return dotProduct(x, y) / (magnitude(x) * magnitude(y));
    }
}
