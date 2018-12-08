package kg.gov.mf.loan.process.model;

import kg.gov.mf.loan.task.model.GenericModel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="fixed_date")
public class FixedDate extends GenericModel {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(nullable=false)
    private Date date;

    @Column(columnDefinition = "int default 1")
    private int status = 1;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
