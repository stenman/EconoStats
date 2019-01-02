package se.perfektum.econostats.domain;

import java.util.List;
import java.util.Objects;

public class PayeeFilter {
    private List<String> payees;
    private String alias;
    private char group;
    private boolean varying;

    public List<String> getPayees() {
        return payees;
    }

    public void setPayees(List<String> payees) {

        this.payees = payees;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public char getGroup() {
        return group;
    }

    public void setGroup(char group) {
        this.group = group;
    }

    public boolean isVarying() {
        return varying;
    }

    public void setVarying(boolean varying) {
        this.varying = varying;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPayees(),
                getAlias(),
                getGroup(),
                isVarying());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayeeFilter)) return false;
        PayeeFilter pf = (PayeeFilter) o;
        return Objects.equals(getPayees(), pf.getPayees())
                && Objects.equals(getAlias(), pf.getAlias())
                && Objects.equals(getGroup(), pf.getGroup())
                && Objects.equals(isVarying(), pf.isVarying());
    }
}
