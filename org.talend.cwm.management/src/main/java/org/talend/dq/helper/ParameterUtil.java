// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dq.analysis.parameters.DBConnectionParameter;

/**
 * DOC zshen class global comment. Detailled comment.
 */
public final class ParameterUtil {

    private ParameterUtil() {

    }
    private static Logger log = Logger.getLogger(ParameterUtil.class);

    private static Map<String, String> toMap(Object parameter) {
        Class<?> parClass = parameter.getClass();
        Map<String, String> parameterMap = new HashMap<String, String>();
        List<Field> fieldList=new ArrayList<Field>();
        fieldList.addAll(Arrays.asList(parClass.getDeclaredFields()));
        for (Field theField : fieldList) {
            String methodName = theField.getName();
            if (methodName != null && methodName.length() > 0) {
                if (theField.getType().equals(String.class)) {
                    methodName = "get" + methodName.toUpperCase().substring(0, 1) + methodName.substring(1);//$NON-NLS-1$
                } else if (theField.getType().equals(boolean.class) || theField.getType().equals(Boolean.class)) {
                    methodName = "is" + methodName.toUpperCase().substring(0, 1) + methodName.substring(1);//$NON-NLS-1$
                } else {
                    continue;
                }
                try {
                    Object parameterValue = parClass.getMethod(methodName).invoke(parameter);
                    parameterMap.put(theField.getName(), parameterValue == null ? null : parameterValue.toString());
                } catch (IllegalArgumentException e) {
                    log.error(e, e);
                } catch (SecurityException e) {
                    log.error(e, e);
                } catch (IllegalAccessException e) {
                    log.error(e, e);
                } catch (InvocationTargetException e) {
                    log.error(e, e);
                } catch (NoSuchMethodException e) {
                    log.error(e, e);
                }

            }
        }

        

        return parameterMap;
    }

    /**
     * 
     * zshen Comment method "toMap".
     * 
     * @param parameter the object which pass to parameter infomation.It can't be null.
     * @return a map which contain both name of attribute and the it's value.return a null when parameter is null.
     */
    public static Map<String, String> toMap(DBConnectionParameter parameter) {
        if (parameter == null) {
            return null;
        }
        Map<String, String> parameterMap = toMap((Object) parameter);
        parameterMap.put("name", parameter.getName());
        parameterMap.put("purpose", parameter.getPurpose());
        parameterMap.put("description", parameter.getDescription());
        parameterMap.put("author", parameter.getAuthor());
        parameterMap.put("status", parameter.getStatus());
        parameterMap.put("version", parameter.getVersion());
        parameterMap.put("universe", parameter.getParameters().getProperty(TaggedValueHelper.UNIVERSE));
        parameterMap.put("datafilter", parameter.getParameters().getProperty(TaggedValueHelper.DATA_FILTER));
        parameterMap.put(TaggedValueHelper.PASSWORD, parameter.getParameters().getProperty(TaggedValueHelper.PASSWORD));
        parameterMap.put(TaggedValueHelper.USER, parameter.getParameters().getProperty(TaggedValueHelper.USER));
        return parameterMap;
    }

    /**
     * 
     * zshen Comment method "main". a test for the method toMap.
     * 
     * @param args
     */
    private static void main(String[] args) {
        DBConnectionParameter dbParameter = new DBConnectionParameter();
        dbParameter.setParameters(new Properties());
        Map<String, String> parameterMap = ParameterUtil.toMap(dbParameter);
        IMetadataConnection metadataConnection = MetadataFillFactory.getDBInstance().fillUIParams(parameterMap);
        System.out.println(metadataConnection);
    }
}
