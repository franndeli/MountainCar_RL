import java.util.Arrays;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;

public class DynamicProgrammingAgent {

    private static final int[] ACTIONS = {
            MountainCarEnv.REVERSE,
            MountainCarEnv.NOTHING,
            MountainCarEnv.FORWARD
    };

    private static final double DISCOUNT = 1.0;
    private static final double CONVERGENCE_TOLERANCE = 1e-6;
    private static final int MAX_ITERATIONS = 500;

    private final StateDiscretization discretization;
    private final double[][] valueFunction;
    private final int[][] policy;
    private final MountainCarEnv model;

    public DynamicProgrammingAgent() {
        this(new StateDiscretization());
    }

    public DynamicProgrammingAgent(StateDiscretization discretization) {
        this.discretization = discretization;
        this.valueFunction = new double[discretization.getPositionBins()][discretization.getVelocityBins()];
        this.policy = new int[discretization.getPositionBins()][discretization.getVelocityBins()];
        this.model = new MountainCarEnv();
        initializePolicy();
    }

    public static void main(String[] args) {
        DynamicProgrammingAgent agent = new DynamicProgrammingAgent();
        int iterations = agent.runValueIteration();
        boolean renderEpisode = args.length > 0 && "render".equalsIgnoreCase(args[0]);

        System.out.println("Value iteration converged in " + iterations + " iterations.");
        agent.printSampledActions();
        agent.showHeatMaps();
        agent.runEpisode(renderEpisode);
    }

    public int runValueIteration() {
        double[][] nextValues = new double[discretization.getPositionBins()][discretization.getVelocityBins()];

        for (int iteration = 1; iteration <= MAX_ITERATIONS; iteration++) {
            double maxDelta = 0.0;

            for (int positionIndex = 0; positionIndex < discretization.getPositionBins(); positionIndex++) {
                for (int velocityIndex = 0; velocityIndex < discretization.getVelocityBins(); velocityIndex++) {
                    double position = discretization.indexToPosition(positionIndex);
                    double velocity = discretization.indexToVelocity(velocityIndex);

                    if (isTerminal(position)) {
                        nextValues[positionIndex][velocityIndex] = 0.0;
                        policy[positionIndex][velocityIndex] = MountainCarEnv.NOTHING;
                        continue;
                    }

                    double bestValue = Double.NEGATIVE_INFINITY;
                    int bestAction = MountainCarEnv.NOTHING;

                    for (int action : ACTIONS) {
                        Transition transition = transitionFrom(position, velocity, action);
                        double candidateValue = transition.reward;

                        if (!transition.terminal) {
                            candidateValue += DISCOUNT
                                    * valueFunction[transition.positionIndex][transition.velocityIndex];
                        }

                        if (candidateValue > bestValue) {
                            bestValue = candidateValue;
                            bestAction = action;
                        }
                    }

                    nextValues[positionIndex][velocityIndex] = bestValue;
                    policy[positionIndex][velocityIndex] = bestAction;
                    maxDelta = Math.max(maxDelta, Math.abs(bestValue - valueFunction[positionIndex][velocityIndex]));
                }
            }

            copyValues(nextValues, valueFunction);
            if (maxDelta < CONVERGENCE_TOLERANCE) {
                return iteration;
            }
        }

        return MAX_ITERATIONS;
    }

    public int chooseAction(double position, double velocity) {
        return policy[discretization.positionToIndex(position)][discretization.velocityToIndex(velocity)];
    }

    public void runEpisode(boolean render) {
        boolean shouldRender = render && !GraphicsEnvironment.isHeadless();
        MountainCarEnv environment = shouldRender
                ? new MountainCarEnv(MountainCarEnv.RENDER)
                : new MountainCarEnv(MountainCarEnv.NONE);
        double[] state = environment.fixedReset();
        int steps = 0;
        double totalReward = 0.0;

        while (state[0] == 0 && steps < 10000) {
            int action = chooseAction(state[2], state[3]);
            state = environment.step(action);
            totalReward += state[1];
            steps++;
        }

        System.out.println("Episode finished in " + steps + " steps.");
        System.out.println("Total reward: " + totalReward);
        System.out.println("Final state: " + Arrays.toString(state));
    }

    private void initializePolicy() {
        for (int positionIndex = 0; positionIndex < policy.length; positionIndex++) {
            Arrays.fill(policy[positionIndex], MountainCarEnv.NOTHING);
        }
    }

    private Transition transitionFrom(double position, double velocity, int action) {
        double[] nextState = model.setState(position, velocity);
        nextState = model.step(action);

        boolean terminal = nextState[0] == 1.0;
        return new Transition(
                nextState[1],
                terminal,
                discretization.positionToIndex(nextState[2]),
                discretization.velocityToIndex(nextState[3]));
    }

    private boolean isTerminal(double position) {
        return position > MountainCarEnv.GOAL_POS;
    }

    private void copyValues(double[][] source, double[][] target) {
        for (int positionIndex = 0; positionIndex < source.length; positionIndex++) {
            System.arraycopy(source[positionIndex], 0, target[positionIndex], 0, source[positionIndex].length);
        }
    }

    private void printSampledActions() {
        double[] samplePositions = {-1.1, -0.75, -0.5, -0.2, 0.0, 0.45};
        double[] sampleVelocities = {-0.05, -0.02, 0.0, 0.02, 0.05};

        for (double position : samplePositions) {
            for (double velocity : sampleVelocities) {
                int action = chooseAction(position, velocity);
                System.out.println(String.format(
                        "policy(position=%.2f, velocity=%.2f) = %d",
                        position, velocity, action));
            }
        }
    }

    private void showHeatMaps() {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Skipping heat map display because no graphical environment is available.");
            return;
        }
        try {
            HeatMapWindow window = new HeatMapWindow(valueFunction, toDoublePolicy());
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(1100, 700);
            window.setVisible(true);
            window.update(valueFunction, toDoublePolicy());
        } catch (Exception e) {
            System.out.println("Unable to display heat maps: " + e.getMessage());
        }
    }

    private double[][] toDoublePolicy() {
        double[][] policyMap = new double[policy.length][policy[0].length];
        for (int positionIndex = 0; positionIndex < policy.length; positionIndex++) {
            for (int velocityIndex = 0; velocityIndex < policy[positionIndex].length; velocityIndex++) {
                policyMap[positionIndex][velocityIndex] = policy[positionIndex][velocityIndex];
            }
        }
        return policyMap;
    }

    private static class Transition {
        private final double reward;
        private final boolean terminal;
        private final int positionIndex;
        private final int velocityIndex;

        private Transition(double reward, boolean terminal, int positionIndex, int velocityIndex) {
            this.reward = reward;
            this.terminal = terminal;
            this.positionIndex = positionIndex;
            this.velocityIndex = velocityIndex;
        }
    }
}
