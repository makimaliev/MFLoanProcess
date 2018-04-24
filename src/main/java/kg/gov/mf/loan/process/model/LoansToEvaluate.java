package kg.gov.mf.loan.process.model;

import kg.gov.mf.loan.manage.model.GenericModel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name="loansToEvaluate")
public class LoansToEvaluate extends GenericModel {

    private long loanId;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date onDate;

    public LoansToEvaluate()
    {

    }

    public LoansToEvaluate(long loanId, String description, Date onDate) {
        this.loanId = loanId;
        this.description = description;
        this.onDate = onDate;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getOnDate() {
        return onDate;
    }

    public void setOnDate(Date onDate) {
        this.onDate = onDate;
    }
}
