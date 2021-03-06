package com.jsoniter.output;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class CodegenImplMap {
    public static String genMap(Class clazz, Type[] typeArgs) {
        Type keyType = String.class;
        Type valueType = Object.class;
        if (typeArgs.length == 0) {
            // default to Map<String, Object>
        } else if (typeArgs.length == 2) {
            keyType = typeArgs[0];
            valueType = typeArgs[1];
        } else {
            throw new IllegalArgumentException(
                    "can not bind to generic collection without argument types, " +
                            "try syntax like TypeLiteral<Map<String, String>>{}");
        }
        if (keyType != String.class) {
            throw new IllegalArgumentException("map key must be String");
        }
        if (clazz == Map.class) {
            clazz = HashMap.class;
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        append(lines, "if (obj == null) { stream.writeNull(); return; }");
        append(lines, "java.util.Map map = (java.util.Map)obj;");
        append(lines, "java.util.Iterator iter = map.entrySet().iterator();");
        append(lines, "if(!iter.hasNext()) { stream.writeEmptyObject(); return; }");
        append(lines, "java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();");
        append(lines, "stream.writeObjectStart();");
        append(lines, "stream.writeObjectField((String)entry.getKey());");
        append(lines, "{{op}}");
        append(lines, "while(iter.hasNext()) {");
        append(lines, "entry = (java.util.Map.Entry)iter.next();");
        append(lines, "stream.writeMore();");
        append(lines, "stream.writeObjectField((String)entry.getKey());");
        append(lines, "{{op}}");
        append(lines, "}");
        append(lines, "stream.writeObjectEnd();");
        append(lines, "}");
        return lines.toString()
                .replace("{{clazz}}", clazz.getName())
                .replace("{{op}}", CodegenImplNative.genWriteOp("entry.getValue()", valueType));
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
