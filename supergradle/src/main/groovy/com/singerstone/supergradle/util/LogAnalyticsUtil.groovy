package com.singerstone.supergradle.util

import jdk.internal.org.objectweb.asm.Opcodes
import org.objectweb.asm.MethodVisitor

public class LogAnalyticsUtil implements Opcodes {
    private static final HashSet<String> targetFragmentClass = new HashSet()

    static {

        /**
         * Fragment
         */
        targetFragmentClass.add('android/support/v4/app/Fragment')
        targetFragmentClass.add('android/support/v4/app/ListFragment')
        targetFragmentClass.add('android/support/v4/app/DialogFragment')
        targetFragmentClass.add('android/app/Fragment')

        /**
         * For AndroidX Fragment
         */
        targetFragmentClass.add('androidx/fragment/app/Fragment')
        targetFragmentClass.add('androidx/fragment/app/ListFragment')
        targetFragmentClass.add('androidx/fragment/app/DialogFragment')
    }

    static boolean isSynthetic(int access) {
        return (access & ACC_SYNTHETIC) != 0
    }

    static boolean isPrivate(int access) {
        return (access & ACC_PRIVATE) != 0
    }

    static boolean isPublic(int access) {
        return (access & ACC_PUBLIC) != 0
    }

    static boolean isStatic(int access) {
        return (access & ACC_STATIC) != 0
    }


    static boolean isTargetFragmentClass(String className) {
        return targetFragmentClass.contains(className)
    }

    static boolean isInstanceOfFragment(String superName) {
        return targetFragmentClass.contains(superName)
    }


    static void visitMethodWithLoadedParams(MethodVisitor methodVisitor, int opcode, String owner, String methodName, String methodDesc, int start, int count, List<Integer> paramOpcodes) {
        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes[i - start], i)
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false)
    }
}
