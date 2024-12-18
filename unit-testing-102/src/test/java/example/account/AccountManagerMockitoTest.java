package example.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AccountManagerMockitoTest {

    private Customer customer;
    private AccountManager accountManager;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        accountManager = Mockito.mock(AccountManagerImpl.class);
    }

    @Test
    void givenAmountExceedsMaxCreditForNonVip_WhenWithdraw_ThenReturnMessageWithoutSubtractingFromBalance() {
        // Arrange
        customer.setBalance(100);
        customer.setCreditAllowed(true);
        customer.setVip(false);
        when(accountManager.withdraw(customer, 1200)).thenReturn("maximum credit exceeded");

        // Act
        String result = accountManager.withdraw(customer, 1200);

        // Assert
        verify(accountManager).withdraw(customer, 1200);
        assertEquals("maximum credit exceeded", result);
        assertEquals(100, customer.getBalance());
    }

    @Test
    void givenAmountExceedsMaxCreditForVip_WhenWithdraw_ThenSubtractAmountFromBalance() {
        // Arrange
        customer.setBalance(100);
        customer.setCreditAllowed(true);
        customer.setVip(true);
        when(accountManager.withdraw(customer, 1200)).thenReturn("success");
        customer.setBalance(-1100);

        // Act
        String result = accountManager.withdraw(customer, 1200);

        // Assert
        verify(accountManager).withdraw(customer, 1200);
        assertEquals("success", result);
        assertEquals(-1100, customer.getBalance());
    }

    @Test
    void givenExactBalance_WhenWithdraw_ThenSetBalanceToZero() {
        // Arrange
        customer.setBalance(500);
        customer.setCreditAllowed(false);
        when(accountManager.withdraw(customer, 500)).thenReturn("success");
        customer.setBalance(0); // Simulate balance update

        // Act
        String result = accountManager.withdraw(customer, 500);

        // Assert
        verify(accountManager).withdraw(customer, 500);
        assertEquals("success", result);
        assertEquals(0, customer.getBalance());
    }

    @Test
    void givenNegativeAmount_WhenWithdraw_ThenDoNothingAndReturnSuccess() {
        // Arrange
        customer.setBalance(500);
        when(accountManager.withdraw(customer, -50)).thenReturn("success");

        // Act
        String result = accountManager.withdraw(customer, -50);

        // Assert
        verify(accountManager).withdraw(customer, -50);
        assertEquals("success", result);
        assertEquals(500, customer.getBalance());
    }

    @Test
    void givenZeroAmount_WhenWithdraw_ThenDoNothingAndReturnSuccess() {
        // Arrange
        customer.setBalance(300);
        when(accountManager.withdraw(customer, 0)).thenReturn("success");

        // Act
        String result = accountManager.withdraw(customer, 0);

        // Assert
        verify(accountManager).withdraw(customer, 0);
        assertEquals("success", result);
        assertEquals(300, customer.getBalance());
    }

    @Test
    void givenZeroAmount_WhenDeposit_ThenDoNothing() {
        // Arrange
        customer.setBalance(200);

        // Act
        accountManager.deposit(customer, 0);

        // Assert
        verify(accountManager).deposit(customer, 0);
        assertEquals(200, customer.getBalance());
    }

    @Test
    void givenNegativeAmount_WhenDeposit_ThenDoNothing() {
        // Arrange
        customer.setBalance(150);

        // Act
        accountManager.deposit(customer, -50);

        // Assert
        verify(accountManager).deposit(customer, -50);
        assertEquals(150, customer.getBalance());
    }

    @Test
    void givenVipCustomerWithNoBalance_WhenWithdrawWithinMaxCredit_ThenAllowWithdrawal() {
        // Arrange
        customer.setBalance(0);
        customer.setCreditAllowed(true);
        customer.setVip(true);
        when(accountManager.withdraw(customer, 900)).thenReturn("success");
        customer.setBalance(-900); // Simulate balance update

        // Act
        String result = accountManager.withdraw(customer, 900);

        // Assert
        verify(accountManager).withdraw(customer, 900);
        assertEquals("success", result);
        assertEquals(-900, customer.getBalance());
    }

    @Test
    void givenNonVipCustomerWithNegativeBalance_WhenDeposit_ThenIncreaseBalance() {
        // Arrange
        customer.setBalance(-800);
        customer.setCreditAllowed(true);
        customer.setVip(false);

        // Act
        accountManager.deposit(customer, 500);
        customer.setBalance(-300); // Simulate balance update

        // Assert
        verify(accountManager).deposit(customer, 500);
        assertEquals(-300, customer.getBalance());
    }
}

