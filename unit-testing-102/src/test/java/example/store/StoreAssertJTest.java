package example.store;

import example.account.AccountManagerImpl;
import example.account.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StoreAssertJTest {

    private Store store;
    private Customer customer;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        // Arrange
        AccountManagerImpl accountManager = new AccountManagerImpl();
        store = new StoreImpl(accountManager);

        customer = new Customer();
        customer.setBalance(1000);

        products = new ArrayList<>();
        Product product1 = new Product("Fridges", 200, 10);
        Product product2 = new Product("TVs", 100, 0); // Out of stock
        Product product3 = new Product("Phones", 50, 5);
        Product product4 = new Product("Thermal mugs", 0, 20); // Free product

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
        assertThatThrownBy(() -> store.buy(product, customer))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product out of stock");
    }

    @Test
    void givenInsufficientFunds_WhenBuy_ThenThrowException() {
        // Arrange
        Product product = products.get(0);
        customer.setBalance(100);

        // Act & Assert
        assertThatThrownBy(() -> store.buy(product, customer))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Payment failure: insufficient account balance");

        assertThat(product.getQuantity()).isEqualTo(10);
    }

    @Test
    void givenSuccessfulPurchase_WhenBuy_ThenReduceProductQuantity() {
        // Arrange
        Product product = products.get(0);

        // Act
        store.buy(product, customer);

        // Assert
        assertThat(product.getQuantity()).isEqualTo(9);
        assertThat(customer.getBalance()).isEqualTo(800);
    }

    @Test
    void givenNegativeProductQuantity_WhenBuy_ThenThrowException() {
        // Arrange
        Product product = new Product("Invalid Product", 100, -1);

        // Act & Assert
        assertThatThrownBy(() -> store.buy(product, customer))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product out of stock");
    }

    @Test
    void givenZeroPriceProduct_WhenBuy_ThenAllowPurchaseAndReduceQuantity() {
        // Arrange
        Product product = products.get(3);

        // Act
        store.buy(product, customer);

        // Assert
        assertThat(product.getQuantity()).isEqualTo(19);
        assertThat(customer.getBalance()).isEqualTo(1000);
    }

    @Test
    void givenMultiplePurchases_WhenBuy_ThenTrackBalancesAndQuantities() {
        // Arrange
        Product product1 = products.get(0);
        Product product3 = products.get(2);

        // Act
        store.buy(product1, customer);
        store.buy(product3, customer);

        // Assert
        assertThat(product1.getQuantity()).isEqualTo(9);
        assertThat(product3.getQuantity()).isEqualTo(4);
        assertThat(customer.getBalance()).isEqualTo(750);
    }
}
