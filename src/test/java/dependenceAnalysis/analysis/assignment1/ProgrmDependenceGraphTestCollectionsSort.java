package dependenceAnalysis.analysis.assignment1;

import dependenceAnalysis.analysis.ProgramDependenceGraph;
import dependenceAnalysis.util.cfg.CFGExtractor;
import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.Node;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by neilwalkinshaw on 13/11/2017.
 */
public class ProgrmDependenceGraphTestCollectionsSort {
    Graph submission,solution;
    ProgramDependenceGraph pdg;
    dependenceAnalysis.analysis.assignment1.solution.ProgramDependenceGraph pdgSol;

    @Before
    public void setup()throws IOException {
        //Pick suitable ClassNode and MethodNode as test subjects.
        ClassNode cn = new ClassNode(Opcodes.ASM4);
        InputStream in=CFGExtractor.class.getResourceAsStream("/java/util/Collections.class");
        ClassReader classReader=new ClassReader(in);
        classReader.accept(cn, 0);

        MethodNode target = null;
        for(MethodNode mn : (List<MethodNode>)cn.methods){
            if(mn.name.equals("sort")) //let's pick out the "equals" method as our subject
                target = mn;
        }

        //Run the post dominator tree generation code.
        pdg = new ProgramDependenceGraph(cn,target);
        submission = pdg.computeResult();
        pdgSol = new dependenceAnalysis.analysis.assignment1.solution.ProgramDependenceGraph(cn,target);
        pdgSol.setControlFlowGraph(pdg.getControlFlowGraph());
        solution = pdgSol.computeResult();
    }

    @Test
    public void testAreaEquals() {
        double tp = 0D;
        double fp = 0D;
        double fn = 0D;
        for(Node n : solution.getNodes()){
            Collection<Node> solSuccs = solution.getSuccessors(n);
            Collection<Node> subSuccs = null;
            if(submission.getNodes().contains(n)){
                subSuccs = submission.getSuccessors(n);
            }
            else {
                subSuccs = new HashSet<Node>();
            }
            for(Node s : solSuccs){
                if(subSuccs.contains(s)) {
                    tp++;
                }
                else{
                    fn++;
                }
            }
            subSuccs.removeAll(solSuccs);
            fp = fp + subSuccs.size();
        }
        double precision = tp / (tp + fp);
        double recall = tp / (tp + fn);
        System.out.println("Collections PDG: Precision - "+precision+", Recall - "+recall);
        writeToFile(submission,"collectionsSubmissionPDG.dot");
        writeToFile(solution,"collectionsSolutionPDG.dot");
    }

    @Test
    public void testAllSlices(){
        double tp = 0D;
        double fp = 0D;
        double fn = 0D;
        double exceptions = 0D;
        for(Node n : solution.getNodes()){
            Collection<Node> slice = pdgSol.backwardSlice(n);
            Collection<Node> sliceSub = new HashSet<Node>();
            if(submission.getNodes().contains(n)) {
                try {
                    sliceSub = pdg.backwardSlice(n);
                }
                catch(Exception e){
                    exceptions++;
                }
            }
            Collection<Node> intersection = new HashSet<Node>();
            Collection<Node> sliceOnly = new HashSet<Node>();
            sliceOnly.addAll(sliceSub);
            intersection.addAll(slice);
            intersection.retainAll(sliceSub);
            tp = tp + intersection.size();
            sliceSub.removeAll(slice);
            fp = fp + sliceSub.size();
            slice.removeAll(sliceOnly);
            fn = fn + slice.size();
        }
        double precision = tp / (tp + fp);
        double recall = tp / (tp + fn);
        System.out.println("Collections Slices: Precision - "+precision+", Recall - "+recall+", Exceptions: "+exceptions);
    }

    @Test
    public void testOverlap(){
        double solOverlap = pdgSol.computeOverlap();
        double subOverlab = pdg.computeOverlap();
        System.out.println("Overlap: Solution: "+solOverlap+", Submission: "+subOverlab+" (Difference: )"+Math.abs(solOverlap-subOverlab));
    }

    private void writeToFile(Graph submission, String name) {
        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter( new FileWriter(name));
            writer.write( submission.toString());

        }
        catch ( IOException e)
        {
        }
        finally
        {
            try
            {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e)
            {
            }
        }
    }

}
