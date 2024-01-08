import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.analysis.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ListIterator;

public class Main {
  public static void main(String[] args) throws IOException, AnalyzerException {
    final String classFilePath = "D:\\Workspace\\CenturionLabs\\Trickle\\StringDeobfuscator\\trickle\\io\\trickle\\task\\sites\\yeezy\\YeezyAPI.class";
    final String outputClassFilePath = "D:\\Workspace\\CenturionLabs\\Trickle\\StringDeobfuscator\\YeezyAPI.processed.class";
//    final String classFilePath = "D:\\ArithmeticPlayground2.class";
    var reader = new ClassReader(Files.readAllBytes(Paths.get(classFilePath)));
    var node = new ClassNode();
    reader.accept(node, ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES);
//
//    var analyzer = new Analyzer<BasicValue>(new BasicInterpreter());
//    var frames = analyzer.analyze("gg/centurion/ArithmeticPlayground", node.methods.get(0));
//    node.methods.get(0).instructions.add(new InsnNode(Opcodes.RETURN));
    var processors = new InstructionProcessor[]{
      new StringDeobfuscationProcessor(),
        new LongToIntFolderProcessor(),
 /* //        new DupXXFolderProcessor(),
//        new Dup_XXFolderProcessor(),
        new IConstProcessor(),
//        new IXorFolderProcessor(),
        new IAddFolderProcessor(),
        new SwapFolderProcessor(),
        new IAndFolderProcessor(),
        new PopFolderProcessor(),
        new ISubFolderProcessor(),
        new IOrFolderProcessor(),
        new INegFolderProcessor(),*/
    };
    for (var m : node.methods) {
      boolean processingFinished;
      do {
        processingFinished = true;
        ListIterator<AbstractInsnNode> iterator = m.instructions.iterator();
        while (iterator.hasNext()) {
          AbstractInsnNode ins = iterator.next();
          if (Arrays.stream(processors)
              .filter(p -> p.canProcess(ins, m, node))
              .anyMatch(p -> p.process(ins, m, node, iterator)) && processingFinished) {
            processingFinished = false;
          }
        }
      }
      while (!processingFinished);
    }

    var writer = new ClassWriter(0);

    node.accept(writer);
    Files.write(Paths.get(outputClassFilePath), writer.toByteArray());
  }


  public void aLotOfOps() {
    int var10000 = (-1212029647 + (-558927150 & ~-1212029647) | (-1212029647 | ~-558927150) - ~-558927150) - ((-1212029647 | ~-558927150) - ~-558927150);
    int var10001 = 786376062 - (1892359854 & 786376062) + (1892359854 - (786376062 & 1892359854) & ~(786376062 - (1892359854 & 786376062))) + ((-1423140770 + -1 + (-1876076341 + -1 & ~(-1423140770 + -1)) | ~(1876076341 + (1423140770 & ~1876076341))) - ~(1876076341 + (1423140770 & ~1876076341)) & ~(786376062 - (1892359854 & 786376062) + (1892359854 - (786376062 & 1892359854) & ~(786376062 - (1892359854 & 786376062))))) + ~((786376062 - (1892359854 & 786376062) + (1892359854 - (786376062 & 1892359854) & ~(786376062 - (1892359854 & 786376062))) | ~((-1423140770 + -1 + (-1876076341 + -1 & ~(-1423140770 + -1)) | ~(1876076341 + (1423140770 & ~1876076341))) - ~(1876076341 + (1423140770 & ~1876076341)))) - ~((-1423140770 + -1 + (-1876076341 + -1 & ~(-1423140770 + -1)) | ~(1876076341 + (1423140770 & ~1876076341))) - ~(1876076341 + (1423140770 & ~1876076341)))) + 1;
  }
}
