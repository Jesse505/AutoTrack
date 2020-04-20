
package com.jesse.autotrack.visitor

import jdk.internal.org.objectweb.asm.Opcodes

class AutoTrackHookConfig {
    /**
     * android.gradle 3.2.1 版本中，针对 Lambda 表达式处理
     */

    public final static HashMap<String, AutoTrackMethodCell> LAMBDA_METHODS = new HashMap<>()
    static {
        AutoTrackMethodCell onClick = new AutoTrackMethodCell(
                'onClick',
                '(Landroid/view/View;)V',
                'Landroid/view/View$OnClickListener;',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                1, 1,
                [Opcodes.ALOAD])
        LAMBDA_METHODS.put(onClick.parent + onClick.name + onClick.desc, onClick)

        // Todo: 扩展
    }
}