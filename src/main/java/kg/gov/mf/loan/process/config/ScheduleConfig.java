package kg.gov.mf.loan.process.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//@Configuration
//@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {
    /*
    @Bean
    public JobA jobA()
    {
        return new JobA();
    }

    @Bean
    public JobCreateDummyOrderState jobDummy()
    {
        return new JobCreateDummyOrderState();
    }
    */

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
    {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }
}
