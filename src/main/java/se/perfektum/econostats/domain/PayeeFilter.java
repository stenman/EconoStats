package se.perfektum.econostats.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PayeeFilter {
    private List<String> payees;
    private List<String> excludedPayees;
    private String alias;
    private Boolean active;

    public PayeeFilter(List<String> payees, List<String> excludedPayees, String alias, Boolean active) {
        this.payees = payees == null ? Collections.emptyList() : payees;
        this.excludedPayees = excludedPayees == null ? Collections.emptyList() : excludedPayees;
        this.alias = alias == null ? "" : alias;
        this.active = active == null ? true : active;
    }

    public List<String> getPayees() {
        return payees;
    }

    public void setPayees(List<String> payees) {
        this.payees = payees;
    }

    public List<String> getExcludedPayees() {
        return excludedPayees;
    }

    public void setExcludedPayees(List<String> excludedPayees) {
        this.excludedPayees = excludedPayees;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPayees(),
                getExcludedPayees(),
                getAlias(),
                isActive());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayeeFilter)) return false;
        PayeeFilter pf = (PayeeFilter) o;
        return Objects.equals(getPayees(), pf.getPayees())
                && Objects.equals(getExcludedPayees(), pf.getExcludedPayees())
                && Objects.equals(getAlias(), pf.getAlias())
                && Objects.equals(isActive(), pf.isActive());
    }
}
