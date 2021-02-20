package com.example.testplugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * @author 18964
 */
public class TestMethodVisitor extends AdviceAdapter {

    private String mClassName;
    private String mSuperName;

    protected TestMethodVisitor(int asm5, MethodVisitor methodVisitor, int access, String name, String descriptor, String className, String superName) {
        super(asm5, methodVisitor, access, name, descriptor);
        mClassName = className;
        mSuperName = superName;
        System.out.println("MethodVisitor Constructor mClassName = " + mClassName + " , mSuperName = " + mSuperName);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        System.out.println("MethodVisitor onMethodEnter() visitCode========");
        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn(mClassName + "---->" + mSuperName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
    }

    @Override
    protected void onMethodExit(int opcode) {
        System.out.println("MethodVisitor onMethodExit() visitCode========");
        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn("this is end");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
        super.onMethodExit(opcode);
    }
}
