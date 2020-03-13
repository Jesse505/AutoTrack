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

