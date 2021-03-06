package com.jesse.autotrack.transforms


import com.jesse.autotrack.ReplaceBuildConfig
import com.jesse.autotrack.extension.ReplaceExtension
import com.jesse.autotrack.visitor.ReplaceClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class ReplaceTransform extends BaseTransform {

    private HashSet<String> exclude = new HashSet<>()
    private ReplaceExtension replaceExtension
    private ReplaceBuildConfig replaceBuildConfig

    ReplaceTransform(ReplaceExtension replaceExtension) {
        this.replaceExtension = replaceExtension
    }

    @Override
    boolean isShouldModify(String className) {
        exclude.add('android.support')
        exclude.add('androidx')

        Iterator<String> iterator = exclude.iterator()
        while (iterator.hasNext()) {
            String packageName = iterator.next()
            if (className.startsWith(packageName)) {
                return false
            }
        }

        if (className.contains('R$') ||
                className.contains('R2$') ||
                className.contains('R.class') ||
                className.contains('R2.class') ||
                className.contains('BuildConfig.class')) {
            return false
        }

        for (int i = 0; i < replaceBuildConfig?.mReplaceMents?.size(); i++) {
            if (replaceBuildConfig?.mReplaceMents?.get(i)?.dstClass ==
                    className.replace(".class", "")) {
                return false
            }
        }

        return true
    }

    @Override
    byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new ReplaceClassVisitor(classWriter, replaceBuildConfig)
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES)
        return classWriter.toByteArray()
    }

    @Override
    String getName() {
        return "ReplaceTransform"
    }

    @Override
    void onBeforeTransform() {
        super.onBeforeTransform()
        final ReplaceBuildConfig replaceConfig = initConfig()
        replaceConfig.parseReplaceFile()
        replaceBuildConfig = replaceConfig
    }

    @Override
    boolean isModifyEnable() {
        return replaceExtension.enable
    }

    private ReplaceBuildConfig initConfig() {
        return new ReplaceBuildConfig(replaceExtension.replaceListDir)
    }
}