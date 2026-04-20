public class StateDiscretization {

    public static final int DEFAULT_POSITION_BINS = 2000;
    public static final int DEFAULT_VELOCITY_BINS = 2000;

    private final int positionBins;
    private final int velocityBins;
    private final double[][] values;

    public StateDiscretization() {
        this(DEFAULT_POSITION_BINS, DEFAULT_VELOCITY_BINS);
    }

    public StateDiscretization(int positionBins, int velocityBins) {
        this.positionBins = positionBins;
        this.velocityBins = velocityBins;
        this.values = new double[positionBins][velocityBins];
    }

    public int getPositionBins() {
        return positionBins;
    }

    public int getVelocityBins() {
        return velocityBins;
    }

    public double[][] rawValues() {
        return values;
    }

    public double getValue(double position, double velocity) {
        return values[positionToIndex(position)][velocityToIndex(velocity)];
    }

    public double getValue(int positionIndex, int velocityIndex) {
        return values[positionIndex][velocityIndex];
    }

    public void putValue(double position, double velocity, double value) {
        values[positionToIndex(position)][velocityToIndex(velocity)] = value;
    }

    public void putValue(int positionIndex, int velocityIndex, double value) {
        values[positionIndex][velocityIndex] = value;
    }

    public int positionToIndex(double position) {
        return clampIndex(toIndex(position, MountainCarEnv.MIN_POS, MountainCarEnv.MAX_POS, positionBins), positionBins);
    }

    public int velocityToIndex(double velocity) {
        return clampIndex(toIndex(velocity, -MountainCarEnv.MAX_SPEED, MountainCarEnv.MAX_SPEED, velocityBins), velocityBins);
    }

    public double indexToPosition(int index) {
        return toCenter(index, MountainCarEnv.MIN_POS, MountainCarEnv.MAX_POS, positionBins);
    }

    public double indexToVelocity(int index) {
        return toCenter(index, -MountainCarEnv.MAX_SPEED, MountainCarEnv.MAX_SPEED, velocityBins);
    }

    private static int toIndex(double value, double min, double max, int bins) {
        double normalized = (value - min) / (max - min);
        return (int) Math.floor(normalized * bins);
    }

    private static double toCenter(int index, double min, double max, int bins) {
        double width = (max - min) / bins;
        return min + (index + 0.5) * width;
    }

    private static int clampIndex(int index, int bins) {
        if (index < 0) {
            return 0;
        }
        if (index >= bins) {
            return bins - 1;
        }
        return index;
    }
}
