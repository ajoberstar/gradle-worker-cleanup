package com.example;

import java.util.UUID;
import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerExecutor;

public class MyTask extends DefaultTask {
  private final WorkerExecutor workerExecutor;
  private int num;

  @Inject
  public MyTask(WorkerExecutor workerExecutor) {
    this.workerExecutor = workerExecutor;
  }

  public void setNum(int num) {
    this.num = num;
  }

  @TaskAction
  public void run() {
    workerExecutor.submit(MyWork.class, worker -> {
      worker.setIsolationMode(IsolationMode.PROCESS);
      worker.params(num);
      worker.getForkOptions().jvmArgs("-Xms2G", "-Xmx2G");
      // use different sysprop to force different worker processes for each
      worker.getForkOptions().systemProperty("my-worker-id", UUID.randomUUID().toString());
    });
  }

  public static class MyWork implements Runnable {
    private final int num;

    @Inject
    public MyWork(int num) {
      this.num = num;
    }

    public void run() {
      System.out.println("#" + num + ": running");
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("#" + num + ": shutting down");
      }));
    }
  }
}
