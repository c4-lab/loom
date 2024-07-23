quartz {
    autoStartup = true
    jdbcStore = false
    waitForJobsToCompleteOnShutdown = true
    exposeSchedulerInRepository = false

    props {
        scheduler.instanceName = 'MyAppScheduler'
        threadPool.class = 'org.quartz.simpl.SimpleThreadPool'
        threadPool.threadCount = 5
        threadPool.threadPriority = 5
        jobStore.class = 'org.quartz.simpl.RAMJobStore'
    }
}

environments {
    test {
        quartz {
            autoStartup = false
        }
    }
}