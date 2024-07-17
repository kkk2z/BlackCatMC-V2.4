import java.util.Random;

public class TerrainGenerator {
    private final int width, height;
    private final double[][] heightMap;

    public TerrainGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.heightMap = new double[width][height];
    }

    public void generateTerrain(long seed) {
        Random rand = new Random(seed);
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                heightMap[x][z] = rand.nextDouble() * 10; // 簡単な高さ生成
            }
        }
    }

    public double getHeight(int x, int z) {
        return heightMap[x][z];
    }
}
