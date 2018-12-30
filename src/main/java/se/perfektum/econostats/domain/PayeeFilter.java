package se.perfektum.econostats.domain;

import javafx.scene.Parent;

import java.util.Objects;

public class PayeeFilter {
    private String payeeName;
    private String alias;
    private char group;
    private boolean varying;

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {

        this.payeeName = payeeName;
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
        return Objects.hash(getPayeeName(),
                getAlias(),
                getGroup(),
                isVarying());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayeeFilter)) return false;
        PayeeFilter pf = (PayeeFilter) o;
        return Objects.equals(getPayeeName(), pf.getPayeeName())
                && Objects.equals(getAlias(), pf.getAlias())
                && Objects.equals(getGroup(), pf.getGroup())
                && Objects.equals(isVarying(), pf.isVarying());
    }
}
