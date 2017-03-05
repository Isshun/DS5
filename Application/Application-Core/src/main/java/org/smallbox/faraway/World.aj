package org.smallbox.faraway;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

public aspect World {

    //Defines a pointcut that we can use in the @Before,@After, @AfterThrowing, @AfterReturning,@Around specifications
    //The pointcut is a catch all pointcut with the scope of execution
    @Pointcut("execution(* *(..))")
    public void atExecution(){}

    @Before("atExecution()")
//    @Before("atExecutionOfMethodsOfAnnotatedClass() && @annotation(myMethodAnnotation)")
    //JointPoint = the reference of the call to the method
    public void printNewLine(JoinPoint joinPoint, JoinPoint.EnclosingStaticPart enclosingStaticPart) {
        //Just prints new lines after each method that's executed in
        System.out.println(enclosingStaticPart.getSignature().getDeclaringTypeName() + "." + enclosingStaticPart.getSignature().getName());
    }

}