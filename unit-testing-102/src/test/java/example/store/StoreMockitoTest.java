package example.store;

import example.account.AccountManager;
import example.account.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StoreMockitoTest {

    private Store store;
    private AccountManager accountManager;
    private Customer customer;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        accountManager = mock(AccountManager.class);
        store = new StoreImpl(accountManager);

        customer = new Customer();
        customer.setBalance(1000);

        products = new ArrayList<>();
        Product product1 = new Product("Fridges", 200, 10);
        Product product2 = new Product("TVs", 100, 0);
        Product product3 = new Product("Phones", 50, 5);
        Product product4 = new Product("Thermal mugs", 0, 20);

        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
    }

    @Test
    void givenProductOutOfStock_WhenBuy_ThenThrowException() {
        // Arrange
        Product product = products.get(1);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> store.buy(product, customer));
        assertEquals("Product out of stock", exception.getMessage());

        verifyNoInteractions(accountManager);
    }

    @Test
    void givenInsufficientFunds_WhenBuy_ThenThrowException() {
        // Arrange
        Product product = products.get(0);
        customer.setBalance(100);
        when(accountManager.withdraw(customer, product.getPrice())).thenReturn("insufficient funds");

        // Act
        Exception exception = assertThrows(RuntimeException.class, () -> store.buy(product, customer));
        assertEquals("Payment failure: insufficient funds", exception.getMessage());
        // Assert
        verify(accountManager).withdraw(customer, product.getPrice());
        assertEquals(10, product.getQuantity());
    }

    @Test
    void givenSuccessfulPurchase_WhenBuy_ThenReduceProductQuantity() {
        // Arrange
        Product product = products.get(0);
        when(accountManager.withdraw(customer, product.getPrice())).thenReturn("success");

        // Act
        store.buy(product, customer);

        // Assert
        verify(accountManager).withdraw(customer, product.getPrice());
        assertEquals(9, product.getQuantity());
    }

    @Test
    void givenAccountManagerFails_WhenBuy_ThenThrowException() {
        // Arrange
        Product product = products.get(0);
        when(accountManager.withdraw(customer, product.getPrice())).thenReturn("transaction error");

        // Act
        Exception exception = assertThrows(RuntimeException.class, () -> store.buy(product, customer));
        assertEquals("Payment failure: transaction error", exception.getMessage());
        // Assert
        verify(accountManager).withdraw(customer, product.getPrice());
        assertEquals(10, product.getQuantity());
    }

    @Test
    void givenNegativeProductQuantity_WhenBuy_ThenThrowException() {
        // Arrange
        Product product = new Product("Invalid Product", 100, -1);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> store.buy(product, customer));
        assertEquals("Product out of stock", exception.getMessage());

        verifyNoInteractions(accountManager);
    }

    @Test
    void givenZeroPriceProduct_WhenBuy_ThenAllowPurchaseAndReduceQuantity() {
        // Arrange
        Product product = products.get(3);
        when(accountManager.withdraw(customer, product.getPrice())).thenReturn("success");

        // Act
        store.buy(product, customer);

        // Assert
        verify(accountManager).withdraw(customer, product.getPrice());
        assertEquals(19, product.getQuantity());
    }

    @Test
    void givenMultiplePurchases_WhenBuy_ThenTrackBalancesAndQuantities() {
        // Arrange
        Product product1 = products.get(0);
        Product product3 = products.get(2);
        when(accountManager.withdraw(customer, product1.getPrice())).thenReturn("success");
        when(accountManager.withdraw(customer, product3.getPrice())).thenReturn("success");

        // Act
        store.buy(product1, customer);
        store.buy(product3, customer);

        // Assert
        verify(accountManager).withdraw(customer, product1.getPrice());
        verify(accountManager).withdraw(customer, product3.getPrice());
        assertEquals(9, product1.getQuantity());
        assertEquals(4, product3.getQuantity());
    }
}

