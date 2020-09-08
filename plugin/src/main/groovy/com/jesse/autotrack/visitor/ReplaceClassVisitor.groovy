package com.jesse.autotrack.visitor

import org.objectweb.asm.*

class ReplaceClassVisitor extends ClassVisitor implements Opcodes {

    ReplaceClassVisitor(final ClassVisitor classVisitor) {
        super(Opcodes.ASM6, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        return new ReplaceMethodVisitor(methodVisitor)
    }

    static class ReplaceMethodVisitor extends MethodVisitor {

        ReplaceMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM6, mv)
        }

        @Override
        void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

            println("-----------ReplaceClassVisitor > visitMethodInsn-------------")

            if ('android/content/Intent' == owner && 'getStringExtra' == name) {
                println("-----------ReplaceClassVisitor > visitMethodInsn > android/content/Intent-------------")
                super.visitMethodInsn(INVOKESTATIC, "com/example/jesse/autotrackappclick/IntentUtil",
                        "getString", "(Landroid/content/Intent;Ljava/lang/String;)Ljava/lang/String;", false)
            } else if ('android/widget/Toast' == owner && 'show' == name) {
                println("-----------ReplaceClassVisitor > visitMethodInsn > android/widget/Toast-------------")
                super.visitMethodInsn(INVOKESTATIC, "com/example/jesse/autotrackappclick/ToastUtil",
                        "showToast", "(Landroid/widget/Toast;)V", false)
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf)
            }

        }
    }

}