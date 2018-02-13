package kg.gov.mf.loan.process.model;

import kg.gov.mf.loan.manage.model.loan.Loan;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="accrue")
public class Accrue extends GenericModel {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(nullable=false)
    private Date fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(nullable=false)
    private Date toDate;

    private int daysInPeriod;

    @Column(precision = 12, scale = 5)
    private Double interestAccrued;

    @Column(precision = 12, scale = 5)
    private Double penaltyAccrued;

    @Column(precision = 12, scale = 5)
    private Double penaltyOnPrincipalOverdue;

    @Column(precision = 12, scale = 5)
    private Double penaltyOnInterestOverdue;

    private boolean lastInstPassed;

    @ManyToOne(targetEntity=Loan.class, fetch = FetchType.EAGER)
    @JoinColumn(name="loanId")
    Loan loan;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public int getDaysInPeriod() {
        return daysInPeriod;
    }

    public void setDaysInPeriod(int daysInPeriod) {
        this.daysInPeriod = daysInPeriod;
    }

    public Double getInterestAccrued() {
        return interestAccrued;
    }

    public void setInterestAccrued(Double interestAccrued) {
        this.interestAccrued = interestAccrued;
    }

    public Double getPenaltyAccrued() {
        return penaltyAccrued;
    }

    public void setPenaltyAccrued(Double penaltyAccrued) {
        this.penaltyAccrued = penaltyAccrued;
    }

    public Double getPenaltyOnPrincipalOverdue() {
        return penaltyOnPrincipalOverdue;
    }

    public void setPenaltyOnPrincipalOverdue(Double penaltyOnPrincipalOverdue) {
        this.penaltyOnPrincipalOverdue = penaltyOnPrincipalOverdue;
    }

    public Double getPenaltyOnInterestOverdue() {
        return penaltyOnInterestOverdue;
    }

    public void setPenaltyOnInterestOverdue(Double penaltyOnInterestOverdue) {
        this.penaltyOnInterestOverdue = penaltyOnInterestOverdue;
    }

    public boolean isLastInstPassed() {
        return lastInstPassed;
    }

    public void setLastInstPassed(boolean lastInstPassed) {
        this.lastInstPassed = lastInstPassed;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}
