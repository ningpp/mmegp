/*
 *    Copyright 2021-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the License);
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an AS IS BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package me.ningpp.mmegp.annotationparser.handler;

import java.util.Set;
import java.util.stream.Collectors;

public interface JavaLangClasses {

    Set<Class<?>> CLASSES = Set.of(
        AbstractMethodError.class,
        Appendable.class,
        ArithmeticException.class,
        ArrayIndexOutOfBoundsException.class,
        ArrayStoreException.class,
        AssertionError.class,
        AutoCloseable.class,
        Boolean.class,
        BootstrapMethodError.class,
        Byte.class,
        CharSequence.class,
        Character.class,
        Class.class,
        ClassCastException.class,
        ClassCircularityError.class,
        ClassFormatError.class,
        ClassLoader.class,
        ClassNotFoundException.class,
        ClassValue.class,
        CloneNotSupportedException.class,
        Cloneable.class,
        Comparable.class,
        Deprecated.class,
        Double.class,
        Enum.class,
        EnumConstantNotPresentException.class,
        Error.class,
        Exception.class,
        ExceptionInInitializerError.class,
        Float.class,
        FunctionalInterface.class,
        IllegalAccessError.class,
        IllegalAccessException.class,
        IllegalArgumentException.class,
        IllegalCallerException.class,
        IllegalMonitorStateException.class,
        IllegalStateException.class,
        IllegalThreadStateException.class,
        IncompatibleClassChangeError.class,
        IndexOutOfBoundsException.class,
        InheritableThreadLocal.class,
        InstantiationError.class,
        InstantiationException.class,
        Integer.class,
        InternalError.class,
        InterruptedException.class,
        Iterable.class,
        LayerInstantiationException.class,
        LinkageError.class,
        Long.class,
        Math.class,
        Module.class,
        ModuleLayer.class,
        NegativeArraySizeException.class,
        NoClassDefFoundError.class,
        NoSuchFieldError.class,
        NoSuchFieldException.class,
        NoSuchMethodError.class,
        NoSuchMethodException.class,
        NullPointerException.class,
        Number.class,
        NumberFormatException.class,
        Object.class,
        OutOfMemoryError.class,
        Override.class,
        Package.class,
        Process.class,
        ProcessBuilder.class,
        ProcessHandle.class,
        Readable.class,
        Record.class,
        ReflectiveOperationException.class,
        Runnable.class,
        Runtime.class,
        RuntimeException.class,
        RuntimePermission.class,
        SafeVarargs.class,
        SecurityException.class,
        Short.class,
        StackOverflowError.class,
        StackTraceElement.class,
        StackWalker.class,
        StrictMath.class,
        String.class,
        StringBuffer.class,
        StringBuilder.class,
        StringIndexOutOfBoundsException.class,
        SuppressWarnings.class,
        System.class,
        Thread.class,
        ThreadDeath.class,
        ThreadGroup.class,
        ThreadLocal.class,
        Throwable.class,
        TypeNotPresentException.class,
        UnknownError.class,
        UnsatisfiedLinkError.class,
        UnsupportedClassVersionError.class,
        UnsupportedOperationException.class,
        VerifyError.class,
        VirtualMachineError.class,
        Void.class
    );

    Set<String> NAMES = CLASSES.stream().map(Class::getName).collect(Collectors.toSet());

    Set<String> SIMPLE_NAMES = CLASSES.stream().map(Class::getSimpleName).collect(Collectors.toSet());

}
