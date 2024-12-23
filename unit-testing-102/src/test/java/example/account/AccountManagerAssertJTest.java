package example.account;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountManagerAssertJTest {

    Customer customer = new Customer();
    AccountManager accountManager = new AccountManagerImpl();

    @Test
    void givenAmountExceedsMaxCreditForNonVip_WhenWithdraw_ThenReturnMessageWithoutSubtractingFromBalance() {
        // Arrange
        customer.setBalance(100);
        customer.setCreditAllowed(true);
        customer.setVip(false);
        // Act
        String result = accountManager.withdraw(customer, 1200);
        // Assert
        assertThat(customer.getBalance()).isEqualTo(100).isNotNegative();
        assertThat(result).isEqualTo("maximum credit exceeded").startsWith("maximum").endsWith("exceeded");
    }

    @Test
    void givenAmountExceedsMaxCreditForVip_WhenWithdraw_ThenSubtractAmountFromBalance() {
        // Arrange
        customer.setBalance(100);
        customer.setCreditAllowed(true);
        customer.setVip(true);
        // Act
        String result = accountManager.withdraw(customer, 1200);
        // Assert
        assertThat(customer.getBalance()).isEqualTo(-1100).isNegative();
        assertThat(result).isEqualTo("success").contains("success");
    }

    @Test
    void givenExactBalance_WhenWithdraw_ThenSetBalanceToZero() {
        // Arrange
        customer.setBalance(500);
        customer.setCreditAllowed(false);
        // Act
        String result = accountManager.withdraw(customer, 500);
        // Assert
        assertThat(customer.getBalance()).isZero();
        assertThat(result).isEqualTo("success").doesNotContain("fail");
    }

    @Test
    void givenNegativeAmount_WhenWithdraw_ThenDoNothingAndReturnSuccess() {
        // Arrange
        customer.setBalance(500);
        customer.setCreditAllowed(true);
        // Act
        String result = accountManager.withdraw(customer, -50);
        // Assert
        assertThat(customer.getBalance()).isEqualTo(500).isPositive();
        assertThat(result).isEqualTo("success").containsOnlyOnce("success");
    }

    @Test
    void givenZeroAmount_WhenWithdraw_ThenDoNothingAndReturnSuccess() {
        // Arrange
        customer.setBalance(300);
        // Act
        String result = accountManager.withdraw(customer, 0);
        // Assert
        assertThat(customer.getBalance()).isEqualTo(300).isGreaterThan(0);
        assertThat(result).isEqualTo("success").contains("success");
    }

    @Test
    void givenZeroAmount_WhenDeposit_ThenDoNothing() {
        // Arrange
        customer.setBalance(200);
        // Act
        accountManager.deposit(customer, 0);
        // Assert
        assertThat(customer.getBalance()).isEqualTo(200).isNotNegative();
    }

    @Test
    void givenNegativeAmount_WhenDeposit_ThenDoNothing() {
        // Arrange
        customer.setBalance(150);
        // Act
        accountManager.deposit(customer, -50);
        // Assert
        assertThat(customer.getBalance()).isEqualTo(150).isGreaterThan(0);
    }

    @Test
    void givenVipCustomerWithNoBalance_WhenWithdrawWithinMaxCredit_ThenAllowWithdrawal() {
        // Arrange
        customer.setBalance(0);
        customer.setCreditAllowed(true);
        customer.setVip(true);
        // Act
        String result = accountManager.withdraw(customer, 900);
        // Assert
        assertThat(customer.getBalance()).isEqualTo(-900).isLessThan(0);
        assertThat(result).isEqualTo("success").contains("success");
    }

    @Test
    void givenNonVipCustomerWithNegativeBalance_WhenDeposit_ThenIncreaseBalance() {
        // Arrange
        customer.setBalance(-800);
        customer.setCreditAllowed(true);
        customer.setVip(false);
        // Act
        accountManager.deposit(customer, 500);
        // Assert
        assertThat(customer.getBalance()).isEqualTo(-300).isNegative();
    }
}
