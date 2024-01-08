import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.DynamicClassLoader;
import org.objectweb.asm.tree.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ListIterator;
import java.util.regex.Pattern;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ARETURN;

public class StringDeobfuscationProcessor implements InstructionProcessor {
  @Override
  public boolean canProcess(AbstractInsnNode node, MethodNode method, ClassNode cls) {
    return node instanceof LdcInsnNode ldc && ldc.cst instanceof String str && !Pattern.matches("-?\\d+", str);
  }

  @Override
  public boolean process(AbstractInsnNode node, MethodNode method, ClassNode cls, ListIterator<AbstractInsnNode> iterator) {
    var ldc = (LdcInsnNode) node;
    var str = (String) ldc.cst;
    var next = ldc.getNext();
    if (next instanceof InvokeDynamicInsnNode ivd) {
      var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
      final String className = cls.name;
      final String superClass = cls.superName;
      cw.visit(V11, ACC_PUBLIC, className, null, superClass, new String[0]);

      final String methodName = method.name;
      var mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, methodName,
          "()Ljava/lang/Object;", null, null);
      mv.visitCode();

      mv.visitLdcInsn(str);
      mv.visitInvokeDynamicInsn(ivd.name, ivd.desc, ivd.bsm, ivd.bsmArgs);

      mv.visitInsn(ARETURN);

      try {
        var loader = new DynamicClassLoader();
        final Class<?> cts = loader.defineClass(className.replace('/', '.'), cw.toByteArray()) /*Main.class.getClassLoader().loadClass("io.trickle.App")*/;
        final Method main = cts.getMethod(methodName);
        var deobfuscatedStr = main.invoke(null, new Object[0]);
        if (deobfuscatedStr != null) {
          ldc.cst = deobfuscatedStr;
//          method.instructions.remove(ivd);
//          return ivd.getNext();
          iterator.next();
          iterator.remove();
          return true;
        } else {
          System.out.println("WARN: unable to deobfuscate: " + str);
        }
      } catch (/*ClassNotFoundException | */NoSuchMethodException/* | NoSuchFieldException*/ | IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }

    }

//    return node;
    return false;
  }
}
