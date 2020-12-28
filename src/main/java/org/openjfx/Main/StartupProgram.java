package org.openjfx.Main;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.update4j.Configuration;
import org.update4j.service.UpdateHandler;

public class StartupProgram implements UpdateHandler {

    private Configuration config;

    private static Process running;

    //ProcessHelper processHelper = new ProcessHelper();

    public StartupProgram(Configuration config) {
        System.out.println("StartupProgram");
        this.config = config;
    }

    public Supplier<Boolean> checkUpdates() {
        System.out.println("checkUpdates");
//        return new FutureTask<>() {
//
//            @Override
//            protected Boolean call() throws Exception {
//                return config.requiresUpdate();
//            }

//        };
        return new Supplier<>() {
            @Override
            public Boolean get() {

                try {
                    return config.requiresUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }

    public void launch() {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(checkUpdates());

        future.thenApplyAsync(result -> {
            Thread run = new Thread(() -> {
                // config.launch(this);
                System.out.println("launch business");
                //running = processHelper.startNewJavaProcess("-cp \".;business/*\"", "net.ccfish.upd4j.business.BusinessApplication");
            });
            if (result) {
                // 执行更新
                if (running != null) {
                    //processHelper.killProcess(running);
                    running.onExit().whenComplete((value, t) -> {
                        boolean rs = config.update(this);
                        if (rs) {
                            run.start();
                        }
                    });
                } else {
                    boolean rs = config.update(this);
                    if (rs) {
                        run.start();
                    }
                }
            } else {
                System.out.println("No updates found");
                if (running == null) {
                    run.start();
                }
            }
            return false;
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable t) {
        System.err.println("：" + t.getMessage());
    }


    @Override
    public void succeeded() {
        System.out.println("");
    }
    @Override
    public void stop() {
        System.out.println("stop");
    }

    private int getRunningId() {
        return 0;
    }
}