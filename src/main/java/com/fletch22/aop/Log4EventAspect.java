package com.fletch22.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.command.MethodCallCommand;
import com.fletch22.orb.command.orbType.dto.MethodCallDto;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.command.processor.RedoAndUndoLogging;
import com.fletch22.orb.logging.EventLogCommandProcessPackageHolder;
import com.fletch22.util.IocUtil;

@Component
@Aspect
public class Log4EventAspect {
	
	Logger logger = LoggerFactory.getLogger(Log4EventAspect.class);
	
	public static boolean isInvokeFromSerializedMethod = false;
	public static boolean isPreventNextLineFromExecutingAndAddToUndoLog;
	
	@Pointcut("execution(@com.fletch22.aop.Loggable4Event * *(..))")
	private void redoLogger() {}
	
	@Around("redoLogger()")
	public Object loggingAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		EventLogCommandProcessPackageHolder packageHolder = getPackageHolder();
		
		Object retObject = null;
		if (isForUndoLog()) {
			undoLogProcessing(proceedingJoinPoint, packageHolder);
		} else {
			retObject = redoLogAndProcessInvocation(proceedingJoinPoint, packageHolder);
		}
		
		return retObject;
	}

	// NOTE: This captures the redo action for the redo log.
	private Object redoLogAndProcessInvocation(ProceedingJoinPoint proceedingJoinPoint, EventLogCommandProcessPackageHolder packageHolder) throws Throwable {
		Object retObject = null;
		
		boolean isInRestoreMode = packageHolder.getCommandProcessActionPackage().isInRestoreMode();
		
		logger.debug("Is in restore mode: {}", isInRestoreMode);
		logger.debug("hasInitialCommandActionBeenAdded: {}", packageHolder.hasInitialCommandActionBeenAdded());
		
		if (packageHolder.hasInitialCommandActionBeenAdded() || isInRestoreMode) {
			retObject = proceedingJoinPoint.proceed();
		} else {
			StringBuilder methodCallSerialized = convertCall(proceedingJoinPoint);
			
			logger.debug("redo log action: {}", methodCallSerialized);
			
			packageHolder.getCommandProcessActionPackage().setAction(methodCallSerialized);

			// NOTE: Since only "shouldBeLogged" methods will have the @Log4EventAspect annotation
			// We can assuredly set the "shouldBeLogged" to true here.
			// NOTE: Consider moving this to a factory method.
			OperationResult operationResult = new OperationResult(OpResult.IN_THE_MIDDLE, true);
			InternalIdGenerator internalIdGenerator = (InternalIdGenerator) getBean(InternalIdGenerator.class);
			operationResult.internalIdBeforeOperation = internalIdGenerator.getCurrentId();
			
			try {
				retObject = proceedingJoinPoint.proceed();
				operationResult.opResult = OpResult.SUCCESS;
			} catch (Exception e) {
				operationResult.opResult = OpResult.FAILURE;
				operationResult.operationResultException = e;
			}
			
			if (!isInvokeFromSerializedMethod) {
				operationResult.internalIdAfterOperation = internalIdGenerator.getCurrentId();
				operationResult.action = methodCallSerialized;

				logActions(packageHolder, operationResult);
			}
		
			packageHolder.cleanup();
			
			if (operationResult.opResult == OpResult.FAILURE) {
				throw new RuntimeException(operationResult.operationResultException);
			}
		}
		return retObject;
	}

	private void logActions(EventLogCommandProcessPackageHolder packageHolder, OperationResult operationResult) {
		RedoAndUndoLogging redoAndUndoLogging = (RedoAndUndoLogging) getBean(RedoAndUndoLogging.class);
		redoAndUndoLogging.logRedoAndUndo(packageHolder.getCommandProcessActionPackage(), operationResult);
	}

	private void undoLogProcessing(ProceedingJoinPoint proceedingJoinPoint, EventLogCommandProcessPackageHolder packageHolder) {

		// NOTE: This captures the undo action for the undo log.
		if (!packageHolder.getCommandProcessActionPackage().isInRestoreMode()) {
			StringBuilder methodCallSerialized = convertCall(proceedingJoinPoint);
			
			logger.debug("undolog MCS: {}", methodCallSerialized);
			
			BigDecimal tranDate = packageHolder.getCommandProcessActionPackage().getTranDate();
			packageHolder.getCommandProcessActionPackage().getUndoActionBundle().addUndoAction(methodCallSerialized, tranDate);
		}
		
		Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog = false;
	}

	private boolean isForUndoLog() {
		return Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog;
	}
	
	private EventLogCommandProcessPackageHolder getPackageHolder() {
		EventLogCommandProcessPackageHolder packageHolder = (EventLogCommandProcessPackageHolder) getBean(EventLogCommandProcessPackageHolder.class);
		
		// TODO convert to factory method
		if (packageHolder.getCommandProcessActionPackage() == null) {
			CommandProcessActionPackageFactory factory = (CommandProcessActionPackageFactory) getBean(CommandProcessActionPackageFactory.class);
			packageHolder.setCommandProcessActionPackage(factory.getInstanceForDirectInvocation());
		}
		
		return packageHolder;
	}
	
	@AfterThrowing(pointcut = "redoLogger()", throwing = "ex")
	public void handleException(JoinPoint joinPoint, Throwable ex) {
		Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog = false;
		Log4EventAspect.isInvokeFromSerializedMethod = false;
	}
	
	private StringBuilder convertCall(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		String methodName = method.getName();
		Type[] parametersTypes = method.getGenericParameterTypes();
		String clazzName = getIocUtil().getBeansSpringSingletonInterface(joinPoint.getTarget()).getName();
		
		StringBuilder sb = convertToJson(clazzName, methodName, parametersTypes, args);
		logger.debug("CN: {}: MN: {}, Nbr args: {}, json: {}", clazzName, methodName, args.length, sb);
		
		return sb;
	}
	
	private IocUtil getIocUtil() {
		return (IocUtil) getBean(IocUtil.class);
	}
	
	private Object getBean(Class<?> clazz) {
		return Fletch22ApplicationContext.getApplicationContext().getBean(clazz);
	}

	private StringBuilder convertToJson(String clazzName, String methodName, Type[] parameterTypes, Object[] args) {
		
		List<String> parameterTypeNames = new ArrayList<String>();
		for (Type type : parameterTypes) {
			parameterTypeNames.add(type.toString());
		}
		
		String[] paramTypeNames = new String[parameterTypeNames.size()];
		parameterTypeNames.toArray(paramTypeNames);
		
		MethodCallDto methodCallDto = new MethodCallDto(clazzName, methodName, paramTypeNames, args);
		return getMethodCallCommandBean().toJson(methodCallDto);
	}
	
	private MethodCallCommand getMethodCallCommandBean() {
		return Fletch22ApplicationContext.getApplicationContext().getBean(MethodCallCommand.class);
	}
	
	public static void preventNextLineFromExecutingAndLogTheUndoAction() {
		isPreventNextLineFromExecutingAndAddToUndoLog = true;
	}
}
