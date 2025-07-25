package com.lavy.pixus.log.annotation.processor;

import com.lavy.pixus.log.annotation.LogBundle;
import com.lavy.pixus.log.annotation.LogMessage;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@SupportedAnnotationTypes({"com.lavy.pixus.log.annotation.LogBundle"})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class LogAnnotationProcessor extends AbstractProcessor {

    private static final boolean IS_DEBUG;

    static {
        IS_DEBUG = LogAnnotationProcessor.class.getClassLoader()
                .getResource("enable-debug-annotation-processor") != null;
    }

    private static void debug(String text) {
        if (IS_DEBUG) {
            System.out.println(text);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<Integer, String> processed = new HashMap<>();

        try {
            for (TypeElement annotation : annotations) {
                for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                    TypeElement annotated = (TypeElement) element;
                    LogBundle bundle = annotated.getAnnotation(LogBundle.class);

                    verifyDeprecateIDValid(annotated, bundle);

                    // sort deprecated ids for useful binary search in later verifies
                    Arrays.sort(bundle.deprecatedIDs());

                    String fullClassName = annotated.getQualifiedName() + "_impl";
                    String interfaceName = annotated.getSimpleName().toString();
                    String simpleClassName = interfaceName + "_impl";

                    JavaFileObject file = processingEnv.getFiler().createSourceFile(fullClassName);
                    debug("");
                    debug("--------------------------------------");
                    debug("processing " + fullClassName + ", generating: " + file.getName());

                    Writer writer = file.openWriter();

                    // header
                    writer.write("/* This class is generated by " + LogAnnotationProcessor.class.getCanonicalName() + " */");
                    writer.write("\n");

                    // declaring package
                    writer.write("package " + annotated.getEnclosingElement() + ";");
                    writer.write("\n");

                    // importing block
                    writer.write("\n" + "import org.slf4j.Logger;");
                    writer.write("\n" + "import org.slf4j.LoggerFactory;");
                    writer.write("\n");

                    // opening class
                    writer.write("\n" + "// " + bundle);
                    writer.write("\n" + "public class " + simpleClassName + " implements " + interfaceName);
                    writer.write(" {");

                    // declare fields
                    writer.write("\n\t" + "private final Logger logger;");
                    writer.write("\n");

                    // declare constructor
                    writer.write("\n\t" + "public " + simpleClassName + "(Logger logger) {");
                    writer.write("\n\t\t" + "this.logger = logger;");
                    writer.write("\n\t" + "}");

                    for (Element el : annotated.getEnclosedElements()) {
                        if (el.getKind() != ElementKind.METHOD) {
                            continue;
                        }

                        ExecutableElement executable = (ExecutableElement) el;
                        LogMessage logMessage = el.getAnnotation(LogMessage.class);

                        if (logMessage != null) {
                            debug("...generating " + executable);

                            verifyIDWithRegex(logMessage.id(), bundle.regexID(), executable);
                            debug("...annotated with " + logMessage);
                            generateLogMessage(writer, bundle, logMessage, executable, processed);
                        }
                    }

                    // closing class
                    writer.write("\n}");
                    // flush in ending of class
                    writer.flush();
                    writer.close();

                    debug("processed " + fullClassName);
                    debug("--------------------------------------");
                    debug("");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            return false;
        }

        return true;
    }

    private static void generateLogMessage(Writer writer, LogBundle bundle, LogMessage logMessage,
                                           ExecutableElement executable, Map<Integer, String> processed) throws IOException {
        verifyIdNotDeprecatedOrProcessed(bundle, executable, logMessage.id(), processed);
        verifyMessagePlaceholder(logMessage.message(), executable);

        processed.put(logMessage.id(), logMessage.message());

        final String loggerFieldName = "logger";

        writer.write("\n\t\t" + "// " + sanitizeChars(logMessage.message()));
        writer.write("\n\t\t" + "@Override");
        writer.write("\n\t\t" + "public void " + executable.getSimpleName() + "(");
        List<? extends VariableElement> paramList = executable.getParameters();

        Iterator<? extends VariableElement> it = paramList.iterator();

        boolean hasParam = it.hasNext();
        VariableElement exceptionParam = null;

        while (it.hasNext()) {
            VariableElement param = it.next();
            boolean isException = verifyIfExceptionArgument(executable, param, it.hasNext(), exceptionParam != null);
            if (isException) {
                exceptionParam = param;
            }

            writer.write(param.asType() + " " + param.getSimpleName());
            if (it.hasNext()) {
                writer.write(", ");
            }
        }
        writer.write(") {");

        StringBuilder callList = new StringBuilder();
        if (hasParam) {
            it = paramList.iterator();
            while (it.hasNext()) {
                VariableElement param = it.next();
                callList.append(param.getSimpleName());
                if (it.hasNext()) {
                    callList.append(", ");
                }
            }
        }

        final String isEnabledMethodCallName = getIsEnabledCallName(logMessage);
        final String logMethodCallName = getLoggerCallName(logMessage);
        final String formatMessage = sanitizeChars(bundle.code() + logMessage.id() + ": " + logMessage.message());

        writer.write("\n\t\t\t" + "if (" + loggerFieldName + "." + isEnabledMethodCallName + "()) {");
        if (hasParam) {
            writer.write("\n\t\t\t\t" + loggerFieldName + "." + logMethodCallName + "(\"" + formatMessage + "\", " + callList + ");");
        } else {
            writer.write("\n\t\t\t\t" + loggerFieldName + "." + logMethodCallName + "(\"" + formatMessage + "\");");
        }
        writer.write("\n\t\t\t" + "}");
        writer.write("\n\t\t" + "}");
    }

    private static String sanitizeChars(String s) {
        return s.replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\");
    }

    private static void verifyDeprecateIDValid(TypeElement annotated, LogBundle bundle) {
        int[] deprecatedIDs = bundle.deprecatedIDs();
        if (deprecatedIDs.length == 0) {
            return;
        }

        String regex = bundle.regexID();
        for (int id : deprecatedIDs) {
            if (!isAllowID(id, regex)) {
                throw new IllegalArgumentException("Illegal deprecated id for " + id + " of " + annotated);
            }
        }
    }

    private static void verifyIDWithRegex(int id, String regexID, ExecutableElement executable) {
        if (!isAllowID(id, regexID)) {
            String enclosingClass = executable.getEnclosingElement().toString();
            throw new IllegalArgumentException(enclosingClass + ": Code " + id + " does not match with regex on bundle " + regexID);
        }
    }

    private static void verifyMessagePlaceholder(String message, ExecutableElement executable) {
        Objects.requireNonNull(message, "message could not be null");
        tupples(message, '{', '}', (tupple) -> {
            if (!tupple.isEmpty()) {
                throw new IllegalArgumentException("Illegal placeholder argument {" + tupple + "} on message " +
                        "\"" + message + "\" in holder " + executable);
            }
        });

        if (message.contains("%s") || message.contains("%d")) {
            throw new IllegalArgumentException("Illegal using %s, %d in placeholder on message \"" + message + "\"");
        }
    }

    private static void verifyIdNotDeprecatedOrProcessed(LogBundle bundle, ExecutableElement executable,
                                                         int id, Map<Integer, String> processed) {
        boolean isDeprecated = isDeprecatedID(bundle, id);
        if (isDeprecated || processed.containsKey(id)) {
            StringBuilder failure = new StringBuilder();
            failure.append(executable.getEnclosingElement().toString()).append(": ");

            if (processed.containsKey(id)) {
                String prevMessage = processed.get(id);
                failure.append("ID ").append(id)
                        .append(" was already used to define message \"").append(prevMessage).append("\"");
            } else if (isDeprecated) {
                failure.append("ID ").append(id)
                        .append(" was deprecated");
            }

            throw new IllegalArgumentException(failure.toString());
        }
    }

    private static boolean verifyIfExceptionArgument(ExecutableElement executable, VariableElement param,
                                                     boolean hasMoreParam, boolean existingException) {
        boolean isException = isException(param.asType(), param);
        debug("... ...parameter " + param + (isException ? " is" : " is not") + " an exception");

        if (isException) {
            if (hasMoreParam) {
                throw new IllegalArgumentException("Exception argument " + param + " must be the last argument on the " + executable);
            }
            if (existingException) {
                throw new IllegalArgumentException("Can only have one exception argument on the " + executable);
            }
        }

        return isException;
    }

    private static void tupples(String arg, char open, char close, Consumer<String> consumer) {
        int openAt = -1;
        for (int i = 0; i < arg.length(); i++) {
            char c = arg.charAt(i);
            if (c == open) {
                openAt = i;
            } else if (c == close) {
                if (openAt >= 0) consumer.accept(arg.substring(openAt + 1, i));
                openAt = -1;
            }
        }
    }

    private static boolean isAllowID(int id, String regexID) {
        if (regexID != null && !regexID.isEmpty()) {
            return Integer.toString(id).matches(regexID);
        }

        return true;
    }

    private static boolean isDeprecatedID(LogBundle bundle, int id) {
        return Arrays.binarySearch(bundle.deprecatedIDs(), id) >= 0;
    }

    private static boolean isException(TypeMirror paramType, VariableElement methodParam) {
        if (paramType == null) {
            return false;
        }

        if (methodParam != null) {
            debug("...checking if parameter \"" + paramType + " " + methodParam + "\" is an exception");
        }

        String paramClazz = paramType.toString();
        if (paramClazz.equals("java.lang.Throwable") || paramClazz.endsWith("Exception")) {
            debug("... Class " + paramType + " is consider as an exception");
            return true;
        }

        switch (paramClazz) {
            case "java.lang.Object":
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Double":
            case "java.lang.Float":
            case "java.lang.String":
            case "java.lang.Number":
            case "java.lang.Thread":
            case "java.lang.ThreadGroup":
            case "com.lavy.pixus.core.SpanString":
            case "none":
                debug("..." + paramClazz + " is a known type, not an exception");
                return false;
        }

        if (paramType instanceof DeclaredType declaredType) {
            if (declaredType.asElement() instanceof TypeElement element) {
                debug("... ... recursively inspecting super class for exception on " + paramClazz);
                return isException(element.getSuperclass(), null);
            }
        }

        return false;
    }

    private static String getLoggerCallName(LogMessage logMessage) {
        return switch (logMessage.level()) {
            case ERROR -> "error";
            case WARN -> "warn";
            case INFO -> "info";
        };
    }

    private static String getIsEnabledCallName(LogMessage logMessage) {
        return switch (logMessage.level()) {
            case ERROR -> "isErrorEnabled";
            case WARN -> "isWarnEnabled";
            case INFO -> "isInfoEnabled";
        };
    }

}
