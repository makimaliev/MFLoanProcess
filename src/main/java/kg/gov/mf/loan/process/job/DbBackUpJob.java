package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.admin.sys.model.SystemFile;
import kg.gov.mf.loan.admin.sys.service.SystemFileService;
import kg.gov.mf.loan.process.model.JobItem;
import org.apache.commons.lang3.SystemUtils;
import org.quartz.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Transactional
@Component
public class DbBackUpJob implements Job {

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.url}")
    private String dbName;

    @Autowired
    SystemFileService systemFileService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobItem job = getJobItemFromJobCaller(context);
        if(job == null) throw new NullPointerException(); //Write Custom Exception
        dbBackupper();
    }

    private static String UPLOADED_FOLDER =  SystemUtils.IS_OS_LINUX ? "/home/eridan/" : "c://temp//";


    private void dbBackupper(){
        String database=((dbName.split("/"))[3].split("[?]"))[0];

        Process p = null;
        try {

            Date today = new Date();


            SimpleDateFormat df2 = new SimpleDateFormat("dd_MM_yy_HH_mm_ss");
            String dateText = df2.format(today);
            System.out.println(dateText);

            String savePath = UPLOADED_FOLDER + database+"_"+dateText+".sql";
            String fileName = database+"_"+dateText;

            String executeCmd = "mysqldump -u "+username+"  -p"+password+"  "+database+" -r "+savePath;


            p = Runtime.getRuntime().exec(executeCmd);
            int processComplete = p.waitFor();
            System.out.println("============================================================================================");
            System.out.println(processComplete);
            System.out.println("============================================================================================");

            if (processComplete == 0) {
                SystemFile dbBackupFile = new SystemFile();
                Path path = Paths.get(UPLOADED_FOLDER + fileName);
                dbBackupFile.setPath(path.toString());
                dbBackupFile.setName(fileName);

                this.systemFileService.create(dbBackupFile);

                System.out.println("Backup created successfully!");

            } else {
                System.out.println("Backup NOT created !");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private JobItem getJobItemFromJobCaller(JobExecutionContext context)
    {
        JobItem job = null;

        try
        {
            SchedulerContext schedulerContext = context.getScheduler().getContext();
            job = (JobItem) schedulerContext.get("jobItem");

        }
        catch(SchedulerException ex)
        {
            ex.printStackTrace();
        }

        return job;
    }
}
