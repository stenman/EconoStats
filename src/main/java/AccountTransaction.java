import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class AccountTransaction {
    private int userId;
    private int accountId;
    private LocalDate date;
    private String name;
    private String category;
    private int amount;
    private int balance;
    private LocalDateTime stampInserted;
    private LocalDateTime stampChanged;

    public AccountTransaction(Builder b) {
        this.userId = b.userId;
        this.accountId = b.accountId;
        this.date = b.date;
        this.name = b.name;
        this.category = b.category;
        this.amount = b.amount;
        this.balance = b.balance;
        this.stampInserted = b.stampInserted;
        this.stampChanged = b.stampChanged;
    }

    public AccountTransaction() {
    }

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
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

    public static class Builder {
        private int userId;
        private int accountId;
        private LocalDate date;
        private String name;
        private String category;
        private int amount;
        private int balance;
        private LocalDateTime stampInserted;
        private LocalDateTime stampChanged;

        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder accountId(int accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder balance(int balance) {
            this.balance = balance;
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

        public AccountTransaction build() {
            return new AccountTransaction(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountTransaction at = (AccountTransaction) o;
        return userId == at.userId &&
                accountId == at.accountId &&
                Objects.equals(name, at.name) &&
                Objects.equals(category, at.category) &&
                amount == at.amount &&
                balance == at.balance &&
                Objects.equals(stampInserted, at.stampInserted) &&
                Objects.equals(stampChanged, at.stampChanged);
    }

}
