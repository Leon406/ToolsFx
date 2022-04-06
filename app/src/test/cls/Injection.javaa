package lianzhang;

import com.BurpSuiteLoader.Transformer;
import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

/* loaded from: BurpSuiteCnV2.0.jar:lianzhang/Injection.class */
public class Injection {
    static ClassPool classPool;
    static Map<String, InjectionMethod> injectionMethods;

    public static void premain(String agentArgs, Instrumentation instrumentation) throws Exception {
        instrumentation.addTransformer(new Transformer());
        classPool = ClassPool.getDefault();
        injectionMethods = new HashMap();
        injectionMethods.put("java/awt/Frame", new InjectionMethod("setTitle", 1));
        injectionMethods.put("java/awt/Dialog", new InjectionMethod("setTitle", 1));
        injectionMethods.put("javax/swing/JLabel", new InjectionMethod("setText", 1));
        injectionMethods.put("javax/swing/AbstractButton", new InjectionMethod("setText", 1));
        injectionMethods.put("javax/swing/text/JTextComponent", new InjectionMethod("setText", 1));
         //injectionMethods.put("javax/swing/text/AbstractDocument", new InjectionMethod("insertString", 2));
         injectionMethods.put("javax/swing/text/PlainDocument", new InjectionMethod("insertString", 2));
         injectionMethods.put("javax/swing/JComponent", new InjectionMethod("setToolTipText", 1));
         injectionMethods.put("javax/swing/JComboBox", new InjectionMethod("addItem", 1));
         injectionMethods.put("javax/swing/JOptionPane", new InjectionMethod("showOptionDialog", 3));
        instrumentation.addTransformer(new ClassFileTransformer() { // from class: lianzhang.Injection.1
            CtBehavior insertTranslateCommand(CtBehavior ctMethod, int n) throws Exception {
                StringBuilder inner = new StringBuilder();
                inner.append("{");
                inner.append("ClassLoader classLoader = ClassLoader.getSystemClassLoader();");
                inner.append("Class translator = classLoader.loadClass(\"lianzhang.Translator\");");
                inner.append("java.lang.reflect.Method method = translator.getDeclaredMethod(\"translate\", new Class[]{String.class, String.class});");
                inner.append(String.format("if($%d instanceof String){$%d = (String)method.invoke(null, new Object[]{\"cn\", $%d});}", Integer.valueOf(n), Integer.valueOf(n), Integer.valueOf(n)));
                inner.append("}");
                try {
                    ctMethod.insertBefore("if ((javax.swing.table.DefaultTableCellRenderer.class.isAssignableFrom($0.getClass())  && !sun.swing.table.DefaultTableCellHeaderRenderer.class.isAssignableFrom($0.getClass()))  || javax.swing.text.DefaultStyledDocument.class.isAssignableFrom($0.getClass())  || javax.swing.tree.DefaultTreeCellRenderer.class.isAssignableFrom($0.getClass())  || javax.swing.JTextArea.class.isAssignableFrom($0.getClass())  || java.lang.Iterable.class.isAssignableFrom($0.getClass())  || $0.getClass().getName().equals(\"javax.swing.plaf.synth.SynthComboBoxUI$SynthComboBoxRenderer\")) {} else" + inner.toString());
                } catch (Exception e) {
                    ctMethod.insertBefore(inner.toString());
                }
                return ctMethod;
            }

            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                try {
                    CtClass ctClass = Injection.classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    InjectionMethod method = Injection.injectionMethods.get(className);
	
                    if (method != null) {
                        insertTranslateCommand(ctClass.getDeclaredMethod(method.methodName), method.place);
                        return ctClass.toBytecode();
                    } else if (className.equals("javax/swing/JTabbedPane")) {
                        insertTranslateCommand(ctClass.getDeclaredMethod("addTab"), 1);
                        insertTranslateCommand(ctClass.getDeclaredMethod("insertTab"), 1);
                        return ctClass.toBytecode();
                    } else if (className.equals("javax/swing/JDialog")) {
                        insertTranslateCommand(ctClass.getDeclaredConstructor(new CtClass[]{Injection.classPool.get("java.awt.Frame"), Injection.classPool.get("java.lang.String"), CtClass.booleanType}), 2);
                        return ctClass.toBytecode();
                    } else if (!className.matches("java/lang/Class")) {
                        return null;
                    } else {
                        ctClass.instrument(new ResourceTransformer("cn"));
                        return ctClass.toBytecode();
                    }
                } catch (Exception ex) {
                    IllegalClassFormatException e = new IllegalClassFormatException();
                    e.initCause(ex);
                    throw e;
                }
            }
        }, true);
        Class<?>[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        for (Class<?> clazz : allLoadedClasses) {
            if (clazz.getName().equals("java.lang.Class")) {
                instrumentation.retransformClasses(new Class[]{clazz});
            }
        }
    }
}
