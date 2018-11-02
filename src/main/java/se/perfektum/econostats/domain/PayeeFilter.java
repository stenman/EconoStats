package se.perfektum.econostats.domain;

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
}
