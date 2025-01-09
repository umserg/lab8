public class Customer {

    private String name;
    private String surname;
    private String email;
    private CustomerType customerType;
    private Account account;
    private double companyOverdraftDiscount = 1;

    public Customer(String name, String surname, String email, CustomerType customerType, Account account) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.customerType = customerType;
        this.account = account;
    }

    public Customer(String name, String email, Account account, double companyOverdraftDiscount) {
        this.name = name;
        this.email = email;
        this.customerType = CustomerType.COMPANY;
        this.account = account;
        this.companyOverdraftDiscount = companyOverdraftDiscount;
    }

    public void withdraw(double sum, String currency) {
        if (!account.getCurrency().equals(currency)) {
            throw new RuntimeException("Can't withdraw " + currency);
        }
        if (account.getType().isPremium()) {
            switch (customerType) {
                case COMPANY:
                    account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, companyOverdraftDiscount / 2));
                    break;
                case PERSON:
                    account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, 1));
                    break;
            }
        } else {
            switch (customerType) {
                case COMPANY:
                    account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, companyOverdraftDiscount));
                    break;
                case PERSON:
                    account.setMoney(executeWithdrawal(sum, account.getMoney() < 0, 1));
                    break;
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

    public String getFullName() {
        return name + " " + surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String printCustomerDaysOverdrawn() {
        String fullName = getFullName() + " ";
        String accountDescription = "Account: IBAN: " + account.getIban() + ", Days Overdrawn: " + account.getDaysOverdrawn();
        return fullName + accountDescription;
    }

    public String printCustomerMoney() {
        String fullName = getFullName() + " ";
        String accountDescription = "Account: IBAN: " + account.getIban() + ", Money: " + account.getMoney();
        return fullName + accountDescription;
    }

    public String printCustomerAccount() {
        return "Account: IBAN: " + account.getIban() + ", Money: " + account.getMoney() + ", Account type: " + account.getType();
    }
}
