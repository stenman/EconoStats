package se.perfektum.econostats.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class AccountTransaction {
    private LocalDate date;
    private BigDecimal amount;
    private String sender = "";
    private String receiver = "";
    private String name = "";
    private String header = "";
    private BigDecimal balance;
    private String currency = "";
    private LocalDateTime stampInserted;
    private LocalDateTime stampChanged;
    @Deprecated
    private String category = "";

    public AccountTransaction() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getStampInserted() {
        return stampInserted;
    }

    public void setStampInserted(LocalDateTime stampInserted) {
        this.stampInserted = stampInserted;
    }

    public LocalDateTime getStampChanged() {
        return stampChanged;
    }

    public void setStampChanged(LocalDateTime stampChanged) {
        this.stampChanged = stampChanged;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public AccountTransaction(Builder b) {
        this.date = b.date;
        this.amount = b.amount;
        this.sender = b.sender;
        this.receiver = b.receiver;
        this.name = b.name;
        this.header = b.header;
        this.balance = b.balance;
        this.currency = b.currency;
        this.stampInserted = b.stampInserted;
        this.stampChanged = b.stampChanged;
        this.category = b.category;
    }

    public static class Builder {
        private LocalDate date;
        private BigDecimal amount;
        private String sender;
        private String receiver;
        private String name;
        private String header;
        private BigDecimal balance;
        private String currency;
        private LocalDateTime stampInserted;
        private LocalDateTime stampChanged;
        @Deprecated
        private String category;

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder receiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder header(String header) {
            this.header = header;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder stampInserted(LocalDateTime stampInserted) {
            this.stampInserted = stampInserted;
            return this;
        }

        public Builder stampChanged(LocalDateTime stampChanged) {
            this.stampChanged = stampChanged;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public AccountTransaction build() {
            return new AccountTransaction(this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((balance == null) ? 0 : balance.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((header == null) ? 0 : header.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
        result = prime * result + ((sender == null) ? 0 : sender.hashCode());
        result = prime * result + ((stampChanged == null) ? 0 : stampChanged.hashCode());
        result = prime * result + ((stampInserted == null) ? 0 : stampInserted.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountTransaction other = (AccountTransaction) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (balance == null) {
            if (other.balance != null)
                return false;
        } else if (!balance.equals(other.balance))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (header == null) {
            if (other.header != null)
                return false;
        } else if (!header.equals(other.header))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (receiver == null) {
            if (other.receiver != null)
                return false;
        } else if (!receiver.equals(other.receiver))
            return false;
        if (sender == null) {
            if (other.sender != null)
                return false;
        } else if (!sender.equals(other.sender))
            return false;
        if (stampChanged == null) {
            if (other.stampChanged != null)
                return false;
        } else if (!stampChanged.equals(other.stampChanged))
            return false;
        if (stampInserted == null) {
            if (other.stampInserted != null)
                return false;
        } else if (!stampInserted.equals(other.stampInserted))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AccountTransaction [date=" + date + ", amount=" + amount + ", sender=" + sender + ", receiver=" + receiver + ", name=" + name + ", header=" + header + ", balance=" + balance + ", currency=" + currency + ", stampInserted=" + stampInserted + ", stampChanged=" + stampChanged
                + ", category=" + category + "]";
    }

}
