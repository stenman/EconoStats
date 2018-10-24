package se.perfektum.econostats.domain;

public class PayeeFilter {
    private int userId;
    private int accountId;
    private String payee;
    private String alias;
    private char group;
    private boolean varying;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {

        this.payee = payee;
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
