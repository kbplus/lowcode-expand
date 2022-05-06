package com.kbplus.dynamic.demo.core;

import com.kbplus.dynamic.demo.entity.DbInfo;
import com.kbplus.dynamic.demo.service.DbInfoService;
import com.kbplus.dynamic.demo.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 动态加载外部jar包的自定义类加载器
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
public class ModuleClassLoader extends URLClassLoader {

    private Logger logger = LoggerFactory.getLogger(ModuleClassLoader.class);

    private final static String CLASS_SUFFIX = ".class";

    private final static String XML_SUFFIX = ".xml";

    private final static String YML_SUFFIX = ".yml";

    private final static String APPLICATION_SUFFIX = ".application";

    private final static String MAPPER_SUFFIX = "mapper/";

    //属于本类加载器加载的jar包
    private JarFile jarFile;

    private Map<String, byte[]> classBytesMap = new HashMap<>();

    private Map<String, Class<?>> classesMap = new HashMap<>();

    private Map<String, byte[]> xmlBytesMap = new HashMap<>();

    public ModuleClassLoader(ClassLoader classLoader, URL... urls) {
        super(urls, classLoader);
        URL url = urls[0];
        String path = url.getPath();
        try {
            jarFile = new JarFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] buf = classBytesMap.get(name);
        if (buf == null) {
            return super.findClass(name);
        }
        if(classesMap.containsKey(name)) {
            return classesMap.get(name);
        }
        /**
         * 这里应该算是骚操作了，我不知道市面上有没有人这么做过，反正我是想了好久，遇到各种因为spring要生成代理对象
         * 在他自己的AppClassLoader找不到原对象导致的报错，注意如果你限制你的扩展包你不会有AOP触碰到的类或者@Transactional这种
         * 会产生代理的类，那么其实你不用这么骚，直接在这里调用defineClass把字节码装载进去就行了，不会有什么问题，最多也就是
         * 在加载mybatis的xml那里前后加三句话，
         * 1、获取并使用一个变量保存当前线程类加载器
         * 2、将自定义类加载器设置到当前线程类加载器
         * 3、还原当前线程类加载器为第一步保存的类加载器
         * 这样之后mybatis那些xml里resultType，resultMap之类的需要访问扩展包的Class的就不会报错了。
         * 不过直接用现在这种骚操作，更加一劳永逸，不会有mybatis的问题了
         */
        return loadClass(name,buf);
    }

    /**
     * 使用反射强行将类装载的归属给当前类加载器的父类加载器也就是AppClassLoader，如果报ClassNotFoundException
     * 则递归装载
     * @param name
     * @param bytes
     * @return
     */
    private Class<?> loadClass(String name, byte[] bytes) throws ClassNotFoundException {

        Object[] args = new Object[]{name, bytes, 0, bytes.length};
        try {
            /**
             * 拿到当前类加载器的parent加载器AppClassLoader
             */
            ClassLoader parent = this.getParent();
            /**
             * 首先要明确反射是万能的，仿造org.springframework.cglib.core.ReflectUtils的写法，强行获取被保护
             * 的方法defineClass的对象，然后调用指定类加载器的加载字节码方法，强行将加载归属塞给它，避免被spring的AOP或者@Transactional
             * 触碰到的类需要生成代理对象，而在AppClassLoader下加载不到外部的扩展类而报错，所以这里强行将加载外部扩展包的类的归属给
             * AppClassLoader，让spring的cglib生成代理对象时可以加载到原对象
             */
            Method classLoaderDefineClass = (Method) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                @Override
                public Object run() throws Exception {
                    return ClassLoader.class.getDeclaredMethod("defineClass",
                            String.class, byte[].class, Integer.TYPE, Integer.TYPE);
                }
            });
            if(!classLoaderDefineClass.isAccessible()) {
                classLoaderDefineClass.setAccessible(true);
            }
            return (Class<?>)classLoaderDefineClass.invoke(parent,args);
        } catch (Exception e) {
            if(e instanceof InvocationTargetException) {

                if (((InvocationTargetException) e).getTargetException().getCause() != null) {
                    String message = ((InvocationTargetException) e).getTargetException().getCause().toString();

                    /**
                     * 无奈，明明ClassNotFoundException是个异常，非要抛个InvocationTargetException，导致
                     * 我这里一个不太优雅的判断
                     */
                    if (message.startsWith("java.lang.ClassNotFoundException")) {
                        String notClassName = message.split(":")[1];
                        if (StringUtils.isEmpty(notClassName)) {
                            throw new ClassNotFoundException(message);
                        }
                        notClassName = notClassName.trim();
                        byte[] bytes1 = classBytesMap.get(notClassName);
                        if (bytes1 == null) {
                            throw new ClassNotFoundException(message);
                        }
                        /**
                         * 递归装载未找到的类
                         */
                        Class<?> notClass = loadClass(notClassName, bytes1);
                        if (notClass == null) {
                            throw new ClassNotFoundException(message);
                        }
                        classesMap.put(notClassName, notClass);
                        return loadClass(name, bytes);
                    }
                }
                } else {
                    logger.error("", e);
                }
        }
        return null;
    }

    public Map<String,byte[]> getXmlBytesMap() {
        return xmlBytesMap;
    }


    /**
     * 方法描述 初始化类加载器，保存字节码
     */
    public Map<String, Class<?>> load() {

        Map<String, Class<?>> cacheClassMap = new HashMap<>();

        DbInfoService dbInfoService = SpringUtils.getBean(DbInfoService.class);

        //解析jar包每一项
        Enumeration<JarEntry> en = jarFile.entries();
        InputStream input = null;
        ByteArrayOutputStream baos =null;
        try {
            while (en.hasMoreElements()) {
                JarEntry je = en.nextElement();
                String name = je.getName();
                //这里添加了路径扫描限制
                if (name.endsWith(CLASS_SUFFIX)) {
//                    //将controller单独拿出来存库//todo
//                    if(name.contains("Controller")){
//                        System.out.println("get controller");
//                        //去掉后缀.class
////                        String className = name.substring(0,name.length()-6).replace("/", ".");
//                        String className = name.replace(CLASS_SUFFIX, "").replaceAll("/", ".");
//                        Class<?> myclass = loadClass(className);
//                        //打印类名
//                        System.out.println("*****************************");
//                        System.out.println("全类名:" + className);
//
//                        Annotation[] annotations = myclass.getAnnotations();
//                        for (Annotation annotation : annotations) {
//                            if(annotation instanceof RequestMapping){
//                                RequestMapping api = (RequestMapping) annotation;
//                                System.out.println("请求地址："+ api.value()[0]);
//                                InvocationHandler h = Proxy.getInvocationHandler(api);
//                                Field hField = h.getClass().getDeclaredField("memberValues");
//                                hField.setAccessible(true);
//                                Map memberValues = (Map) hField.get(h);
//                                String arr[] = {"/test"+api.value()[0]};
//
//                                memberValues.put("value", arr);
//                                System.out.println("请求地址："+ api.value()[0]);
//                            }
//                        }
                        //得到类中包含的属性
//                        Method[] methods = myclass.getMethods();
//                        for (Method method : methods) {
//                            String methodName = method.getName();
//                            System.out.println("方法名称:" + methodName);
//                            Class<?>[] parameterTypes = method.getParameterTypes();
//                            for (Class<?> clas : parameterTypes) {
//                                // String parameterName = clas.getName();
//                                String parameterName = clas.getSimpleName();
//                                System.out.println("参数类型:" + parameterName);
//                            }
//                            Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
//                            for (Annotation declaredAnnotation : declaredAnnotations) {
//                                declaredAnnotation.toString();
//                                System.out.println("获取到方法注解:"+declaredAnnotation);
//                            }
//                            System.out.println("==========================");
//                        }
//                        input = jarFile.getInputStream(je);
//                        baos = new ByteArrayOutputStream();
//                        int bufferSize = 4096;
//                        byte[] buffer = new byte[bufferSize];
//                        int bytesNumRead = 0;
//                        while ((bytesNumRead = input.read(buffer)) != -1) {
//                            baos.write(buffer, 0, bytesNumRead);
//                        }
//                        byte[] classBytes = baos.toByteArray();
//                        classBytesMap.put(className, classBytes);
//                    }else {

                        String className = name.replace(CLASS_SUFFIX, "").replaceAll("/", ".");
                        input = jarFile.getInputStream(je);
                        baos = new ByteArrayOutputStream();
                        int bufferSize = 4096;
                        byte[] buffer = new byte[bufferSize];
                        int bytesNumRead = 0;
                        while ((bytesNumRead = input.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesNumRead);
                        }
                        byte[] classBytes = baos.toByteArray();
                        classBytesMap.put(className, classBytes);
//                    }
                } else if(name.endsWith(XML_SUFFIX) && name.startsWith(MAPPER_SUFFIX)) {
                    input = jarFile.getInputStream(je);
                    baos = new ByteArrayOutputStream();
                    int bufferSize = 4096;
                    byte[] buffer = new byte[bufferSize];
                    int bytesNumRead = 0;
                    while ((bytesNumRead = input.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesNumRead);
                    }
                    byte[] xmlBytes = baos.toByteArray();
                    xmlBytesMap.put(name, xmlBytes);
                }else if(name.endsWith(YML_SUFFIX)||name.endsWith(APPLICATION_SUFFIX)){
                    //处理配置文件获取数据源存入数据库
                    input = jarFile.getInputStream(je);
                    Yaml yaml = new Yaml();
                    Map map = yaml.loadAs(input, Map.class);
                    String username = ((Map<String, Object>)((Map<String, Object>) map.get("spring")).get("datasource")).get("username").toString();
                    String password = ((Map<String, Object>)((Map<String, Object>) map.get("spring")).get("datasource")).get("password").toString();
                    String url = ((Map<String, Object>)((Map<String, Object>) map.get("spring")).get("datasource")).get("url").toString();
                    String driverClassName = ((Map<String, Object>)((Map<String, Object>) map.get("spring")).get("datasource")).get("driver-class-name").toString();

                    DbInfo dbInfo = new DbInfo();
                    if(!dbInfoService.queryTenantExist("test2")){
                        dbInfo.setExpandJarName(jarFile.getName());
                        dbInfo.setJarPath(jarFile.getComment());
                        dbInfo.setTenantId("test2");
                        dbInfo.setUsername(username);
                        dbInfo.setPassword(password);
                        dbInfo.setUrl(url);
                        dbInfo.setDriverClassName(driverClassName);
                        dbInfoService.insert(dbInfo);
                    }

                    System.out.println(username);
                    System.out.println(password);
                    System.out.println(url);
                    System.out.println(driverClassName);

//                    Properties properties = new Properties();
//                    properties.load(input);
                }
            }
//        } catch (IOException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
        } catch (IOException e) {
            logger.error("",e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(jarFile!=null){
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //将jar中的每一个class字节码进行Class载入
        for (Map.Entry<String, byte[]> entry : classBytesMap.entrySet()) {
            String key = entry.getKey();
            Class<?> aClass = null;
            try {
                aClass = loadClass(key);
                if(key.contains("Controller")) {
                    System.out.println("全类名:" + key);

                    Annotation[] annotations = aClass.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof RequestMapping) {
                            RequestMapping api = (RequestMapping) annotation;
                            String lastMapping= api.value()[0];
                            String[] split = lastMapping.split("/");
                            String nowMapping="test/"+split[split.length-1];
                            System.out.println("之前请求地址：" + lastMapping);
                            System.out.println("之后请求地址：" + nowMapping);
                            InvocationHandler h = Proxy.getInvocationHandler(api);
                            Field hField = h.getClass().getDeclaredField("memberValues");
                            hField.setAccessible(true);
                            Map memberValues = (Map) hField.get(h);
                            String arr[] = {nowMapping};
                            memberValues.put("value", arr);
                            System.out.println("请求地址：" + api.value()[0]);
                        }
                    }
                }
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                logger.error("",e);
            }
            cacheClassMap.put(key, aClass);
        }
        return cacheClassMap;

    }

    public Map<String, byte[]> getClassBytesMap() {
        return classBytesMap;
    }
}