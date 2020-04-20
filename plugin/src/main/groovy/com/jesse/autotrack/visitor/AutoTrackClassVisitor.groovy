package com.jesse.autotrack.visitor

import com.jesse.autotrack.utils.AutoTrackUtils
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class AutoTrackClassVisitor extends ClassVisitor implements Opcodes {
    private final
    static String SDK_API_CLASS = "com/sensorsdata/analytics/android/sdk/SensorsDataAutoTrackHelper"
    private String[] mInterfaces
    private ClassVisitor classVisitor

    private HashMap<String, AutoTrackMethodCell> mLambdaMethodCells = new HashMap<>()


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
            AnnotationVisitor visitAnnotation(String s, boolean b) {
                //扫描到自定义注解
                if (s == 'Lcom/sensorsdata/analytics/android/sdk/SensorsDataTrackViewOnClick;') {
                    isSensorsDataTrackViewOnClickAnnotation = true
                }
                return super.visitAnnotation(s, b)
            }

            /**
             * 访问INVOKEDYNAMIC指令，访问lambda表达式的时候调用
             */
            @Override
            void visitInvokeDynamicInsn(String name1, String desc1, Handle bsm, Object... bsmArgs) {
                super.visitInvokeDynamicInsn(name1, desc1, bsm, bsmArgs)

                try {
                    String desc2 = (String) bsmArgs[0]
                    //获取内部类方法的hook对象，比如onClick
                    AutoTrackMethodCell sensorsAnalyticsMethodCell = AutoTrackHookConfig.LAMBDA_METHODS.get(Type.getReturnType(desc1).getDescriptor() + name1 + desc2)
                    if (sensorsAnalyticsMethodCell != null) {
                        Handle it = (Handle) bsmArgs[1]
                        //将hook对象缓存到map里，key值为lambda方法，例如lambda$onCreate$0
                        mLambdaMethodCells.put(it.name + it.desc, sensorsAnalyticsMethodCell)
                    }
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }

            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)

                //hook 注解SensorsDataTrackViewOnClick标识的方法，为了解决在xml中注册的方法无法在编译期间hook的问题，只能通过手动添加自定义注解
                if (isSensorsDataTrackViewOnClickAnnotation) {
                    if (desc == '(Landroid/view/View;)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                        return
                    }
                }

                //处理lambda表达式的hook
                AutoTrackMethodCell lambdaMethodCell = mLambdaMethodCells.get(nameDesc)
                if (lambdaMethodCell != null) {
                    Type[] types = Type.getArgumentTypes(lambdaMethodCell.desc)
                    int length = types.length
                    Type[] lambdaTypes = Type.getArgumentTypes(desc)
                    int paramStart = lambdaTypes.length - length
                    //过滤参数不一致的方法
                    if (paramStart < 0) {
                        return
                    } else {
                        for (int i = 0; i < length; i++) {
                            if (lambdaTypes[paramStart + i].descriptor != types[i].descriptor) {
                                return
                            }
                        }
                    }
                    //加载参数，类似mv.visitVarInsn(ALOAD, 0)
                    boolean isStaticMethod = AutoTrackUtils.isStatic(access)
                    for (int i = paramStart; i < paramStart + lambdaMethodCell.paramsCount; i++) {
                        methodVisitor.visitVarInsn(lambdaMethodCell.opcodes.get(i - paramStart), AutoTrackUtils.getVisitPosition(lambdaTypes, i, isStaticMethod))
                    }
                    //加载方法
                    methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, lambdaMethodCell.agentName, lambdaMethodCell.agentDesc, false)
                    return
                }

                //Hook 普通的setOnClickListener中的onClick方法
                if ((mInterfaces != null && mInterfaces.length > 0)) {
                    if ((mInterfaces.contains('android/view/View$OnClickListener') && nameDesc == 'onClick(Landroid/view/View;)V')) {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                    }
                }
            }


            @Override
            void visitEnd() {
                super.visitEnd()
                //在访问结束后，清除缓存
                if (mLambdaMethodCells.containsKey(nameDesc)) {
                    mLambdaMethodCells.remove(nameDesc)
                }
            }

        }
        return methodVisitor
    }

}