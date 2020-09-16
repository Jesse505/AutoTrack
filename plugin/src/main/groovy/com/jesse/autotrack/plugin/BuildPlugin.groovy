package com.jesse.autotrack.plugin


import com.jesse.autotrack.Log
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

class BuildPlugin implements Plugin<Project> {

    //用来记录 task 的执行时长等信息
    Map<String, TaskExecTimeInfo> timeCostMap = new HashMap<>()
    //用来按顺序记录执行的 task 名称
    List<String> taskPathList = new ArrayList<>()

    @Override
    void apply(Project project) {
        Log.i("zyf", "-----BuildPlugin apply-----")

        //监听每个task的执行
        project.getGradle().addListener(new TaskExecutionListener() {
            @Override
            void beforeExecute(Task task) {
                //task开始执行之前搜集task的信息
                TaskExecTimeInfo timeInfo = new TaskExecTimeInfo()
                //记录开始时间
                timeInfo.start = System.currentTimeMillis()
                timeInfo.path = task.getPath()
                timeCostMap.put(task.getPath(), timeInfo)
                taskPathList.add(task.getPath())
            }

            @Override
            void afterExecute(Task task, TaskState taskState) {
                //task执行完之后，记录结束时的时间
                TaskExecTimeInfo timeInfo = timeCostMap.get(task.getPath())
                timeInfo.end = System.currentTimeMillis()
                //计算该 task 的执行时长
                timeInfo.total = timeInfo.end - timeInfo.start
            }
        })

        //编译结束之后：
        project.getGradle().addBuildListener(new BuildListener() {
            @Override
            void buildStarted(Gradle gradle) {

            }

            @Override
            void settingsEvaluated(Settings settings) {

            }

            @Override
            void projectsLoaded(Gradle gradle) {

            }

            @Override
            void projectsEvaluated(Gradle gradle) {

            }

            @Override
            void buildFinished(BuildResult buildResult) {
                println "---------------------------------------"
                println "---------------------------------------"
                println "build finished, now println all task execution time:"
                long total = 0
                //按 task 执行顺序打印出执行时长信息
                for (String path : taskPathList) {
                    long t = timeCostMap.get(path).total
                    println("${path}  [${t}ms]")
                    total+=t
                }
                println "all task execution total time: ${total}"
                println "---------------------------------------"
                println "---------------------------------------"
            }
        })

    }

    //关于 task 的执行信息
    static class TaskExecTimeInfo {
        long total      //task执行总时长

        String path
        long start      //task 执行开始时间
        long end        //task 结束时间
    }
}