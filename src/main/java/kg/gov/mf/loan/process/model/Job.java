package kg.gov.mf.loan.process.model;

import kg.gov.mf.loan.manage.model.GenericModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="job")
public class Job extends GenericModel{

    private String name;
    private String cronExpression;
    private boolean enabled;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCronExpression() {
        return cronExpression;
    }
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
