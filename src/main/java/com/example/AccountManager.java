public class AccountManager {
    private Account account;
    private double companyOverdraftDiscount = 1;
    private AccountHandler accountHandler;

    public AccountManager(Account account, double companyOverdraftDiscount) {
        this.account = account;
        this.companyOverdraftDiscount = companyOverdraftDiscount;
        setUpAccountHandler();
    }

    public AccountManager(Account account) {
        this.account = account;
        setUpAccountHandler();
    }

    // Ініціалізує відповідний обробник рахунку на основі типу клієнта
    private void setUpAccountHandler() {
        if (account.getCustomer().getCustomerType() == CustomerType.PERSON) {
            this.accountHandler = new PersonAccountHandler(account);
        } else {
            this.accountHandler = new CompanyAccountHandler(account, companyOverdraftDiscount);
        }
    }

    /**
     * Знімає певну суму грошей з рахунку у вказаній валюті.
     * Викидає виняток, якщо валюта не співпадає з валютою рахунку.
     *
     * @param sum сума для зняття
     * @param currency валюта для зняття
     */
    public void withdraw(double sum, String currency) {
        // Перевірка, чи валюта співпадає з валютою рахунку
        if (!account.getCurrency().equals(currency)) {
            throw new RuntimeException("Can't withdraw " + currency);
        }
        // Виконати зняття через відповідний обробник
        accountHandler.withdraw(sum);
    }

    // Абстрактний клас для обробки рахунків
    private abstract class AccountHandler {
        protected Account account;

        public AccountHandler(Account account) {
            this.account = account;
        }

        public abstract void withdraw(double sum);
    }

    // Обробник рахунків для фізичних осіб
    private class PersonAccountHandler extends AccountHandler {
        public PersonAccountHandler(Account account) {
            super(account);
        }

        @Override
        public void withdraw(double sum) {
            double balance = account.getMoney();

            if (balance < 0) {
                account.setMoney(balance - sum - sum * account.overdraftFee());
            } else {
                account.setMoney(balance - sum);
            }
        }
    }

    // Обробник рахунків для компаній
    private class CompanyAccountHandler extends AccountHandler {
        private double companyOverdraftDiscount;

        public CompanyAccountHandler(Account account, double companyOverdraftDiscount) {
            super(account);
            this.companyOverdraftDiscount = companyOverdraftDiscount;
        }

        @Override
        public void withdraw(double sum) {
            double balance = account.getMoney();
            // Додали перевірку на преміум-рахунок
            if (account.getType().isPremium()) {
                if (balance < 0) {
                    account.setMoney(balance - sum - sum * account.overdraftFee() * companyOverdraftDiscount / 2);
                } else {
                    account.setMoney(balance - sum);
                }
            } else {
                if (balance < 0) {
                    account.setMoney(balance - sum - sum * account.overdraftFee() * companyOverdraftDiscount);
                } else {
                    account.setMoney(balance - sum);
                }
            }
        }
    }
}
