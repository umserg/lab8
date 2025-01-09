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

    /**
     * Знімає певну суму грошей з рахунку у вказаній валюті.
     * Викидає виняток, якщо валюта не співпадає з валютою рахунку.
     *
     * @param customerType тип клієнта (COMPANY або PERSON)
     * @param sum сума для зняття
     * @param currency валюта для зняття
     */
    public void withdraw(CustomerType customerType, double sum, String currency) {
        // Перевірка, чи валюта співпадає з валютою рахунку
        if (!account.getCurrency().equals(currency)) {
            throw new RuntimeException("Can't withdraw " + currency);
        }
        // Здійснити зняття залежно від типу клієнта та типу рахунку
        executeWithdrawalForCustomerType(customerType, sum);
    }

    /**
     * Виконує зняття грошей для певного типу клієнта.
     *
     * @param customerType тип клієнта (COMPANY або PERSON)
     * @param sum сума для зняття
     */
    private void executeWithdrawalForCustomerType(CustomerType customerType, double sum) {
        if (account.getType().isPremium()) {
            if (customerType == CustomerType.COMPANY) {
                // Преміум рахунок для компанії
                // Застосувати 50% знижку на овердрафт, якщо є овердрафт
                account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, companyOverdraftDiscount / 2));
            } else {
                // Преміум рахунок для фізичної особи
                account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, 1));
            }
        } else {
            if (customerType == CustomerType.COMPANY) {
                // Звичайний рахунок для компанії
                // Повна ставка на овердрафт, якщо є овердрафт
                account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, companyOverdraftDiscount));
            } else {
                // Звичайний рахунок для фізичної особи
                account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, 1));
            }
        }
    }

    /**
     * Допоміжний метод для фактичного зняття коштів.
     *
     * @param sum сума для зняття
     * @param isInOverdraft чи є овердрафт
     * @param overdraftDiscount знижка на овердрафт
     * @return новий баланс рахунку після зняття коштів
     */
    private double executeWithdrawal(double sum, boolean isInOverdraft, double overdraftDiscount) {
        if (isInOverdraft) {
            return (account.getMoney() - sum) - sum * account.overdraftFee() * overdraftDiscount;
        } else {
            return account.getMoney() - sum;
        }
    }
}
