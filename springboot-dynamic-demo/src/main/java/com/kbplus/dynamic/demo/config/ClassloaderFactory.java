package com.kbplus.dynamic.demo.config;

import com.kbplus.dynamic.demo.core.ModuleApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
public class ClassloaderFactory {
    private static Logger logger = LoggerFactory.getLogger(ClassloaderFactory.class);

    private ClassloaderFactory(){}

    private Map<String, ModuleApplication> factoryMap = new ConcurrentHashMap<>();

    public void addModuleApplication(String moduleName, ModuleApplication moduleApplication){
        factoryMap.put(moduleName,moduleApplication);
    }

    public boolean containsClassLoader(String key){
        return factoryMap.containsKey(key);
    }

    public ModuleApplication getClassLoader(String key){
        return factoryMap.get(key);
    }

    private static class ClassloaderResponsityHodler{
        private static ClassloaderFactory instance = new ClassloaderFactory();
    }

    public static ClassloaderFactory getInstance(){
        return ClassloaderResponsityHodler.instance;
    }

}
