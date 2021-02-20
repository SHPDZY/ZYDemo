package com.example.testplugin;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author 18964
 */
public class TestClassVisitor extends ClassVisitor {

    private String mClassName;
    private String mSuperName;

    TestClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        System.out.println("ClassVisitor visit() mClassName = " + mClassName + " , mSuperName = " + mSuperName);
        mClassName = name;
        mSuperName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("ClassVisitor visitMethod() name:"+name);
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mClassName.equals("com/example/zydemo2/BaseActivity") && mSuperName.equals("androidx/appcompat/app/AppCompatActivity")) {
            if (name.startsWith("onCreate")) {
                return new TestMethodVisitor(Opcodes.ASM5, methodVisitor, access, name, descriptor, mClassName, mSuperName);
            }
        }
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        System.out.println("ClassVisitor visitEnd()");
    }
}
