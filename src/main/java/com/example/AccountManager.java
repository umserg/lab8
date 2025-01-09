public class AccountManager {
    private Account account;
    private double companyOverdraftDiscount = 1;

    public AccountManager(Account account, double companyOverdraftDiscount) {
        this.account = account;
        this.companyOverdraftDiscount = companyOverdraftDiscount;
    }

    public AccountManager(Account account) {
        this.account = account;
    }

    public void withdraw(CustomerType customerType, double sum, String currency) {
        if (!account.getCurrency().equals(currency)) {
            throw new RuntimeException("Can't withdraw " + currency);
        }
        executeWithdrawalForCustomerType(customerType, sum);
    }

    private void executeWithdrawalForCustomerType(CustomerType customerType, double sum) {
        if (account.getType().isPremium()) {
            if (customerType == CustomerType.COMPANY) {
                account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, companyOverdraftDiscount / 2));
            } else {
                account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, 1));
            }
        } else {
            if (customerType == CustomerType.COMPANY) {
                account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, companyOverdraftDiscount));
            } else {
                account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, 1));
            }
        }
    }

    private double executeWithdrawal(double sum, boolean isInOverdraft, double overdraftDiscount) {
        if (isInOverdraft) {
            return (account.getMoney() - sum) - sum * account.overdraftFee() * overdraftDiscount;
        } else {
            return account.getMoney() - sum;
        }
    }
}
