package kg.gov.mf.loan.process.model;

import kg.gov.mf.loan.task.model.GenericModel;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="jobItem")
public class JobItem extends GenericModel{

    private String name;
    private String cronExpression;
    private boolean enabled;

    @OneToMany(mappedBy = "jobItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    @OrderBy("onDate")
    private Set<OnDate> onDates = new HashSet<OnDate>();

    private int active=0;

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

    public Set<OnDate> getOnDates() {
        return onDates;
    }

    public void setOnDates(Set<OnDate> onDates) {
        this.onDates = onDates;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
