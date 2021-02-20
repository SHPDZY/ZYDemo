package com.example.testplugin

import com.android.build.api.transform.*
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

public class TestTransfrom extends Transform {
    @Override
    String getName() {
        return "TestPluginDemo"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.ContentType> getOutputTypes() {
        return EnumSet.of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return EnumSet.of(QualifiedContent.Scope.PROJECT)
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        //删除之前的输出
        if (outputProvider != null)
            outputProvider.deleteAll()
        inputs.each { TransformInput input ->

            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectoryInput(directoryInput, outputProvider)
            }

            //遍历jarInputs
            input.jarInputs.each { JarInput jarInput ->
                handleJarInputs(jarInput, outputProvider)
            }
        }
    }

    static void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        //是否是目录
        if (directoryInput.file.isDirectory()) {
            directoryInput.file.eachFileRecurse { File file ->
                println("find class: " + file.name)
                if (checkClassFile(file.name)) {
                    //对class文件进行读取
                    ClassReader classReader = new ClassReader(file.bytes)
                    //对class文件的写入
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    //访问class文件相应的内容，解析到某一个结构就会通知到classVisitor相应的方法
                    println("before visit")
                    ClassVisitor visitor = new TestClassVisitor(classWriter)
                    println("after visit")
                    //依次调用ClassVisitor接口的各个方法
                    classReader.accept(visitor, ClassReader.EXPAND_FRAMES)
                    println("after accept")
                    //toByteArray方法会将最终修改的字节码以byte数组形式返回
                    byte[] bytes = classWriter.toByteArray()
                    //通过文件流写入方式覆盖掉原先的内容，实现class文件的改写
                    FileOutputStream fos = new FileOutputStream(
                            file.parentFile.absolutePath + File.separator + file.name)
                    fos.write(bytes)
                    fos.close()
                }
            }
        }
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    /**
     * 处理Jar中的class文件
     */
    static void handleJarInputs(JarInput jarInput, TransformOutputProvider outputProvider) {
        println("handleJarInputs jar:" + jarInput.file.path)
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            //重名名输出文件,因为可能同名,会覆盖
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
//            JarFile jarFile = new JarFile(jarInput.file)
//            Enumeration enumeration = jarFile.entries()
//            File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
//            //避免上次的缓存被重复插入
//            if (tmpFile.exists()) {
//                tmpFile.delete()
//            }
//            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
//            //用于保存
//            while (enumeration.hasMoreElements()) {
//                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
//                String entryName = jarEntry.getName()
//                ZipEntry zipEntry = new ZipEntry(entryName)
//                InputStream inputStream = jarFile.getInputStream(jarEntry)
//                //插桩class
//                if (checkClassFile(entryName)) {
//                    //class文件处理
//                    println '----------- deal with "jar" class file <' + entryName + '> -----------'
//                    jarOutputStream.putNextEntry(zipEntry)
//                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
//                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
//                    ClassVisitor cv = new TestClassVisitor(classWriter)
//                    classReader.accept(cv, EXPAND_FRAMES)
//                    byte[] code = classWriter.toByteArray()
//                    jarOutputStream.write(code)
//                } else {
//                    jarOutputStream.putNextEntry(zipEntry)
//                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
//                }
//                jarOutputStream.closeEntry()
//            }
//            //结束
//            jarOutputStream.close()
//            jarFile.close()
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            println("handleJarInputs dest:" + dest.path)
            FileUtils.copyFile(jarInput.file, dest)
        }
    }

    /**
     * 检查class文件是否需要处理
     * @param fileName
     * @return
     */
    static boolean checkClassFile(String name) {
        //只处理需要的class文件
        return (name.endsWith(".class") && !name.startsWith("R\$")
                && !"R.class".equals(name) && !"BuildConfig.class".equals(name)
                && "BaseActivity.class".equals(name))
    }
}
