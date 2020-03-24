package com.jesse.autotrack.transforms

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class AutoTrackTransform extends BaseTransform {

    private HashSet<String> exclude = new HashSet<>()

    @Override
    boolean isShouldModify(String className) {
        exclude.add('android.support')
        exclude.add('com.sensorsdata.analytics.android.sdk')

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
        return true
    }

    @Override
    byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new AutoTrackClassVisitor(classWriter)
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES)
        return classWriter.toByteArray()
    }

    @Override
    String getName() {
        return "AutoTrackTransform"
    }
}