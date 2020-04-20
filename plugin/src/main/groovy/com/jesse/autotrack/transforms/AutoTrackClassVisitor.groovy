package com.jesse.autotrack.transforms

import org.objectweb.asm.*

class AutoTrackClassVisitor extends ClassVisitor implements Opcodes {
    private final
    static String SDK_API_CLASS = "com/sensorsdata/analytics/android/sdk/SensorsDataAutoTrackHelper"
    private String[] mInterfaces
    private ClassVisitor classVisitor


    AutoTrackClassVisitor(final ClassVisitor classVisitor) {
        super(Opcodes.ASM6, classVisitor)
        this.classVisitor = classVisitor
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        mInterfaces = interfaces
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)

        String nameDesc = name + desc

        methodVisitor = new AutoTrackDefaultMethodVisitor(methodVisitor, access, name, desc) {

            boolean isSensorsDataTrackViewOnClickAnnotation = false

            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)

                if ((mInterfaces != null && mInterfaces.length > 0)) {
                    //Hook 普通的setOnClickListener中的onClick方法
                    if ((mInterfaces.contains('android/view/View$OnClickListener') && nameDesc == 'onClick(Landroid/view/View;)V')) {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                    }
                }

                //hook 注解SensorsDataTrackViewOnClick标识的方法，为了解决在xml中注册的方法无法在编译期间hook的问题，只能通过手动添加自定义注解
                if (isSensorsDataTrackViewOnClickAnnotation) {
                    if (desc == '(Landroid/view/View;)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                        return
                    }
                }
            }

            @Override
            AnnotationVisitor visitAnnotation(String s, boolean b) {
                if (s == 'Lcom/sensorsdata/analytics/android/sdk/SensorsDataTrackViewOnClick;') {
                    isSensorsDataTrackViewOnClickAnnotation = true
                }
                return super.visitAnnotation(s, b)
            }
        }
        return methodVisitor
    }

}