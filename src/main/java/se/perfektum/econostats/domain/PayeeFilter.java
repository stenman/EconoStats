package se.perfektum.econostats.domain;

import java.util.List;
import java.util.Objects;

public class PayeeFilter {
    private List<String> payees;
    private List<String> excludePayees;
    private String alias;

    public List<String> getPayees() {
        return payees;
    }

    public void setPayees(List<String> payees) {
        this.payees = payees;
    }

    public List<String> getExcludePayees() {
        return excludePayees;
    }

    public void setExcludePayees(List<String> excludePayees) {
        this.excludePayees = excludePayees;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPayees(),
                getExcludePayees(),
                getAlias());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayeeFilter)) return false;
        PayeeFilter pf = (PayeeFilter) o;
        return Objects.equals(getPayees(), pf.getPayees())
                && Objects.equals(getExcludePayees(), pf.getExcludePayees())
                && Objects.equals(getAlias(), pf.getAlias());
    }
}
