import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountTransaction {
    private int userId;
    private int accountId;
    private LocalDate date;
    private String name;
    private int categoryId;
    private int amount;
    private int balance;
    private LocalDateTime stampInserted;
    private LocalDateTime stampChanged;

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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
}
