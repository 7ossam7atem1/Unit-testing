
package example.account;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountManagerTest {


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
        int expectedBalance = customer.getBalance();
        Assertions.assertEquals(100, expectedBalance);
        Assertions.assertEquals("maximum credit exceeded", result);
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
        int expectedBalance = customer.getBalance();
        Assertions.assertEquals(-1100, expectedBalance);
        Assertions.assertEquals("success", result);
    }

    @Test
    void givenExactBalance_WhenWithdraw_ThenSetBalanceToZero() {
        // Arrange
        customer.setBalance(500);
        customer.setCreditAllowed(false);
        // Act
        String result = accountManager.withdraw(customer, 500);
        // Assert
        int expectedBalance = customer.getBalance();
        Assertions.assertEquals(0, expectedBalance);
        Assertions.assertEquals("success", result);
    }

    @Test
    void givenNegativeAmount_WhenWithdraw_ThenDoNothingAndReturnSuccess() {
        // Arrange
        customer.setBalance(500);
        customer.setCreditAllowed(true);
        // Act
        String result = accountManager.withdraw(customer, -50);
        // Assert
        int expectedBalance = customer.getBalance();
        Assertions.assertEquals(500, expectedBalance);
        Assertions.assertEquals("success", result);
    }

    @Test
    void givenZeroAmount_WhenWithdraw_ThenDoNothingAndReturnSuccess() {
        // Arrange
        customer.setBalance(300);
        // Act
        String result = accountManager.withdraw(customer, 0);
        // Assert
        int expectedBalance = customer.getBalance();
        Assertions.assertEquals(300, expectedBalance);
        Assertions.assertEquals("success", result);
    }

    @Test
    void givenZeroAmount_WhenDeposit_ThenDoNothing() {
        // Arrange
        customer.setBalance(200);
        // Act
        accountManager.deposit(customer, 0);
        // Assert
        Assertions.assertEquals(200, customer.getBalance());
    }

    @Test
    void givenNegativeAmount_WhenDeposit_ThenDoNothing() {
        // Arrange
        customer.setBalance(150);
        // Act
        accountManager.deposit(customer, -50);
        // Assert
        Assertions.assertEquals(150, customer.getBalance());
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
        Assertions.assertEquals(-900, customer.getBalance());
        Assertions.assertEquals("success", result);
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
        Assertions.assertEquals(-300, customer.getBalance());
    }

}
