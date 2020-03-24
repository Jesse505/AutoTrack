## 一、自定义Gradle插件

1. 新建一个module，将build.gradle改为以下代码：

```groovy
apply plugin: 'groovy'
apply plugin: 'maven'

dependencies {
    compile gradleApi()     //gradle sdk
    compile localGroovy()   //groovy sdk
    compileOnly 'com.android.tools.build:gradle:3.5.3'
}

repositories {
    jcenter()
}

//主要用于上传插件
uploadArchives {
    repositories.mavenDeployer {
        //本地仓库路径，以放到项目根目录下的 repo 的文件夹为例
        repository(url: uri('../repo'))

        //groupId ，自行定义
        pom.groupId = 'com.jesse'

        //artifactId，自行定义
        pom.artifactId = 'autotrack.android'

        //插件版本号
        pom.version = '1.0.0'
    }
}
```

2. 创建plugin所需要的目录

   - 删除src目录下的所有文件，然后再src/main目录下创建groovy目录，然后在该目录下新建一个package，例如com.jesse.autotrack.plugin
   - 创建properties文件，在src/main目录下依次创建目录resources/META-INF/gradle-plugins，然后在该目录下创建一个后缀名为properties的文件，用来声明插件的名称以及对应插件的包名和类名，比如我们新建com.jesse.testplugin.properties

3. 实现插件

   在src/main/groovy目录的package下新建一个TestPlugin.groovy类，该类主要实现了plugin接口，内容很简单就是打印了一句话：

   ```groovy
   class TestPlugin implements Plugin<Project> {
   
       @Override
       void apply(Project project) {
           def config = project.extensions.create('config', PluginConfig)
           project.task('testTask') {
               doLast {
                   println('hello testTask ' + config.debug)
               }
           }
       }
   }
   ```

其中的config字段就是Extension，说白了就是用于在gradle中传递参数给插件用的，PluginConfig就是这个字段的所属类，我们这里也需要新建这个类，可以看到这个类里面就写了一个属性debug

```groovy
package com.jesse.autotrack

class PluginConfig {
    boolean debug
}
```

4. 修改com.jesse.testplugin.properties

   在其中添加`implementation-class=com.jesse.autotrack.plugin.TestPlugin`

5. ./gradlew uploadArchives编译发布插件

6. 使用插件

   - 在project根目录的build.gradle文件中引入插件
   - 在app/build.gradle中引入插件

7. ./gradlew testTask 执行task，打印如下

   > Task :app:testTask
   > hello testTask true

## 二、自定义Transform

#### 定义

​	Transform 是Android 官方提供给开发者在项目编译阶段，也就是.class到.dex转换期间，用来修改.class的一套标准API，目前比较经典的就是字节码插桩和代码注入，概括的说就是**把输入的.class文件转换为目标字节码**

#### 主要方法

```groovy
    /**
     * 需要处理的数据类型，有两种枚举类型
     * CLASSES 代表处理的 java 的 class 文件，RESOURCES 代表要处理 java 的资源
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指 Transform 要操作内容的范围，官方文档 Scope 有 7 种类型：
     * 1. EXTERNAL_LIBRARIES        只有外部库
     * 2. PROJECT                   只有项目内容
     * 3. PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * 4. PROVIDED_ONLY             只提供本地或远程依赖项
     * 5. SUB_PROJECTS              只有子项目。
     * 6. SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * 7. TESTED_CODE               由当前变量(包括依赖项)测试的代码
     * @return
     */
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 是否是增量编译
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 字节码转换的主要逻辑
     * @param transformInvocation
     * @throws TransformException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException,InterruptedException, IOException {
        println("-----------transform >>>>>>>>> 开始-----------" + getName())
        _transform(transformInvocation.context, transformInvocation.inputs,
                transformInvocation.outputProvider, transformInvocation.incremental)
    }
  
```

#### 主要流程

![transform](./transform.jpg)

可以看到我们需要**重写transform()方法，在其中我们首先需要判断是否是增量更新，如果不是增量更新，也就是全量更新的话，需要先删除全部输出outputProvider.deleteAll()，接着再去遍历输入，输入主要分为目录和Jar文件的遍历，最终的处理方式都是一样的，找到合适的hook点，通过ASM的方式修改.class文件，最后再拷贝回去**，根据Transform的处理流程，我抽象出来了一个BaseTransform类，以后自定义Transform只需要继承这个类的三个方法，不需要关注中间的文件的处理过程：

```groovy
class TestTransform extends BaseTransform {
    /**
     * 过滤需要修改的class文件
     * @param className
     * @return
     */
    @Override
    boolean isShouldModify(String className) {
        return false
    }
		/**
     * 修改class文件
     * @param srcClass 源class
     * @return 目标class
     * @throws IOException
     */
    @Override
    byte[] modifyClass(byte[] srcClass) throws IOException {
        return srcClass
    }

    @Override
    String getName() {
        return "TestTransform"
    }
}
```

#### 注册Transform

自定义一个gradle插件，在apply方法中，通过AppExtension的registerTransform来注册Transform

```groovy
AppExtension appExtension = project.extensions.findByType(AppExtension.class)
appExtension.registerTransform(new TestTransform())
```

## 三、ASM

