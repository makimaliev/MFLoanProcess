package kg.gov.mf.loan.process.model;

import kg.gov.mf.loan.task.model.GenericModel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="onDate")
public class OnDate extends GenericModel {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(nullable=false)
    private Date onDate;

    @ManyToOne(targetEntity=JobItem.class, fetch = FetchType.EAGER)
    @JoinColumn(name="jobItemId")
    JobItem jobItem;

    public Date getOnDate() {
        return onDate;
    }

    public void setOnDate(Date onDate) {
        this.onDate = onDate;
    }

    public JobItem getJobItem() {
        return jobItem;
    }

    public void setJobItem(JobItem jobItem) {
        this.jobItem = jobItem;
    }
}
