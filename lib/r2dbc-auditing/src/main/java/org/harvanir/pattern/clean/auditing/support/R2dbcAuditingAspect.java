package org.harvanir.pattern.clean.auditing.support;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/** @author Harvan Irsyadi */
@Slf4j
@Setter
@Aspect
public class R2dbcAuditingAspect {

  private AuditingHandler auditingHandler;

  public R2dbcAuditingAspect(Class<?>[] basePackageClasses) {
    initialize(basePackageClasses);
  }

  private void initialize(Class<?>[] basePackageClasses) {
    register(loadClasses(basePackageClasses));
  }

  private List<Class<?>> loadClasses(Class<?>[] basePackageClasses) {
    if (log.isDebugEnabled()) {
      log.debug("basePackageClasses: {}", Arrays.toString(basePackageClasses));
    }

    ArrayList<Class<?>> classes = new ArrayList<>();
    Arrays.stream(basePackageClasses)
        .forEach(
            aClass -> classes.addAll(ClassUtils.getClassByPackage(aClass.getPackage().getName())));

    return classes;
  }

  private void register(List<Class<?>> classes) {
    auditingHandler = new AuditingHandler();

    classes.forEach(aClass -> auditingHandler.register(aClass));
  }

  @Around(
      "execution(* org.springframework.data.repository.reactive.ReactiveCrudRepository.save*(..))")
  Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    log.debug("Intercepting: {}", joinPoint.getSignature());

    for (Object o : joinPoint.getArgs()) {
      if (isSingle(o)) {
        auditingHandler.auditing(o);
      } else {
        iterate(o, o1 -> auditingHandler.auditing(o1));
      }
    }

    return joinPoint.proceed();
  }

  private boolean isSingle(Object o) {
    return !(o instanceof Iterable);
  }

  private void iterate(Object object, Consumer<Object> consumer) {
    for (Object o : (Iterable<?>) object) {
      consumer.accept(o);
    }
  }
}
