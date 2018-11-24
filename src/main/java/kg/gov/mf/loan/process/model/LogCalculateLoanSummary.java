package kg.gov.mf.loan.process.model;

import kg.gov.mf.loan.task.model.GenericModel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="logCalculateLoanSummary")
public class LogCalculateLoanSummary extends GenericModel {

    private long loanId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date onDate;

    private String message;

    public LogCalculateLoanSummary()
    {

    }

    public LogCalculateLoanSummary(long loanId, Date onDate, String message) {
        this.loanId = loanId;
        this.onDate = onDate;
        this.message = message;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public Date getOnDate() {
        return onDate;
    }

    public void setOnDate(Date onDate) {
        this.onDate = onDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
