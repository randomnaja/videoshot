package videoshot.webapp.service;

import com.hazelcast.core.IQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Service;
import videoshot.webapp.job.IJob;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class JobRunnerService implements ApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(JobRunnerService.class);

    @Autowired
    private HazelcastEntryService hazelcastEntryService;

    private ExecutorService jobControllerExec = Executors.newCachedThreadPool();

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            log.info("Spring Context started, running job runner controllers");
            // start the runner controller
            jobControllerExec.submit(new JobRunnerController(hazelcastEntryService.getChopVideoQueue()));
            jobControllerExec.submit(new JobRunnerController(hazelcastEntryService.getUploadVideoQueue()));
        } else if (event instanceof ContextClosedEvent) {
            // stop the timer
            jobControllerExec.shutdown();
            try {
                log.info("Shutting down jobControllerExecutorService");
                jobControllerExec.awaitTermination(120, TimeUnit.SECONDS);
                log.info("Successfully shutting down jobControllerExecutorService");
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted via shutting down itself");
            }
        }
    }

    private class JobRunnerController implements Runnable {

        private IQueue<? extends IJob> blockingQueue;

        private ExecutorService exec = Executors.newFixedThreadPool(4);

        private JobRunnerController(IQueue<? extends IJob> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            try {
                while(true) {
                    log.info("Taking one job from queue...");
                    IJob job = blockingQueue.take();
                    log.info("Got 1 job, submitting the job runner");

                    exec.submit(new JobRunner(job));
                }
            } catch (InterruptedException e) {
                log.info("Interrupted, will quite this " + this.getClass());
                exec.shutdown();
                try {
                    log.info("Shutting down ExecutorService, wait for maximum 60 seconds");
                    exec.awaitTermination(60, TimeUnit.SECONDS);
                    log.info("Shutdown completed");
                } catch (InterruptedException e1) {
                    throw new RuntimeException("Too many interrupted");
                }
            }
        }
    }

    private class JobRunner implements Runnable {

        private IJob job;

        private JobRunner(IJob job) {
            this.job = job;
        }

        @Override
        public void run() {
            try {
                log.info("Running job " + job);
                long start = System.currentTimeMillis();
                job.runJob();
                log.info("Finish job {} in {}ms", job, (System.currentTimeMillis() - start));
            } catch (Exception e) {
                log.error("Job " + job + " is failed, exception = " + e.getMessage(), e);
            }
        }
    }
}
