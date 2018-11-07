package dependenceAnalysis.analysis.assignment1.solution;

import dependenceAnalysis.util.cfg.Graph;

public interface ControlDependenceTree {
    void setControlFlowGraph(Graph controlFlowGraph);

    Graph computeResult();

    String getControlFlowGraph();
}
