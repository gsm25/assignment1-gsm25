package dependenceAnalysis.analysis;

import dependenceAnalysis.util.cfg.CFGExtractor;
import dependenceAnalysis.util.cfg.Graph;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.InputStream;
import java.util.List;

/**
 * Created by neilwalkinshaw on 09/11/2017.
 */
public class SampleControlDependenceTreeTest {

    @Test
    public void computePostDom_AreaEquals() throws Exception {

        //Pick suitable ClassNode and MethodNode as test subjects.
        ClassNode cn = new ClassNode(Opcodes.ASM4);
        InputStream in=CFGExtractor.class.getResourceAsStream("/java/awt/geom/Area.class");
        ClassReader classReader=new ClassReader(in);
        classReader.accept(cn, 0);

        MethodNode target = null;
        for(MethodNode mn : (List<MethodNode>)cn.methods){
            if(mn.name.equals("isSingular")) //let's pick out the "equals" method as our subject
                target = mn;
        }


        dependenceAnalysis.analysis.assignment1.solution.ControlDependenceTree cdt = new dependenceAnalysis.analysis.assignment1.solution.ControlDependenceTree(cn,target);
        Graph tree = cdt.computeResult();

        //Print results for inspection (best visualised using GraphViz).
        System.out.println("ORIGINAL CFG: \n"+cdt.getControlFlowGraph()
                +"\n\nCFG TREE:\n"+tree);
    }

}