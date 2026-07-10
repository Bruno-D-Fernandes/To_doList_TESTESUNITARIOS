package edu.jUnitEMosquito.advice.beforeAdvice;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAdvice {

    @Before("execution(*criarGrupo(..))")
    public void loggingCreateGroup(){
        System.out.println("Requisição para criar grupo recebida.");
    }

}
