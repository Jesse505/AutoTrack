package com.jesse.autotrack.visitor

import com.jesse.autotrack.Log
import com.jesse.autotrack.ReplaceBuildConfig
import org.objectweb.asm.*

class ReplaceClassVisitor extends ClassVisitor implements Opcodes {

    ReplaceBuildConfig mReplaceBuildConfig

    ReplaceClassVisitor(final ClassVisitor classVisitor, ReplaceBuildConfig replaceBuildConfig) {
        super(Opcodes.ASM6, classVisitor)
        mReplaceBuildConfig = replaceBuildConfig
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        return new ReplaceMethodVisitor(methodVisitor, mReplaceBuildConfig)
    }

    static class ReplaceMethodVisitor extends MethodVisitor {

        ReplaceBuildConfig mReplaceBuildConfig

        ReplaceMethodVisitor(MethodVisitor mv, ReplaceBuildConfig replaceBuildConfig) {
            super(Opcodes.ASM6, mv)
            mReplaceBuildConfig = replaceBuildConfig
        }

        @Override
        void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

            if (!mReplaceBuildConfig.getmReplaceMents().isEmpty()) {
                for (int i = 0; i < mReplaceBuildConfig.getmReplaceMents().size(); i++) {
                    ReplaceBuildConfig.ReplaceMent replaceMent = mReplaceBuildConfig.getmReplaceMents().get(i)
                    if (owner == replaceMent.getSrcClass() && name == replaceMent.getSrcMethodName()
                            && desc == replaceMent.getSrcMethodDesc()) {
                        Log.i("ReplaceClassVisitor", "" + replaceMent.toString())
                        super.visitMethodInsn(INVOKESTATIC, replaceMent.getDstClass(),
                                replaceMent.getDstMethodName(), replaceMent.getDstMethodDesc(), false)
                        return
                    }
                }
            }

            super.visitMethodInsn(opcode, owner, name, desc, itf)
        }
    }

}