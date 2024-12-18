package example.store;

import example.account.AccountManagerImpl;
import example.account.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoreTest {

    private Store store;
    private Customer customer;
    private List<Product> products;

    @BeforeEach
    void setUp() {
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
        Product product = products.get(1);

        Exception exception = assertThrows(RuntimeException.class, () -> store.buy(product, customer));
        assertEquals("Product out of stock", exception.getMessage());
    }

    @Test
    void givenInsufficientFunds_WhenBuy_ThenThrowException() {
        Product product = products.get(0);
        customer.setBalance(100);

        Exception exception = assertThrows(RuntimeException.class, () -> store.buy(product, customer));
        System.out.println(exception.getMessage());
        assertEquals("Payment failure: insufficient account balance", exception.getMessage());
        assertEquals(10, product.getQuantity());
    }

    @Test
    void givenSuccessfulPurchase_WhenBuy_ThenReduceProductQuantity() {
        Product product = products.get(0);

        store.buy(product, customer);

        assertEquals(9, product.getQuantity());
        assertEquals(800, customer.getBalance());
    }

    @Test
    void givenNegativeProductQuantity_WhenBuy_ThenThrowException() {
        Product product = new Product("Invalid Product", 100, -1);

        Exception exception = assertThrows(RuntimeException.class, () -> store.buy(product, customer));
        assertEquals("Product out of stock", exception.getMessage());
    }

    @Test
    void givenZeroPriceProduct_WhenBuy_ThenAllowPurchaseAndReduceQuantity() {
        Product product = products.get(3);

        store.buy(product, customer);

        assertEquals(19, product.getQuantity());
        assertEquals(1000, customer.getBalance());
    }

    @Test
    void givenMultiplePurchases_WhenBuy_ThenTrackBalancesAndQuantities() {
        Product product1 = products.get(0);
        Product product3 = products.get(2);

        store.buy(product1, customer);
        store.buy(product3, customer);

        assertEquals(9, product1.getQuantity());
        assertEquals(4, product3.getQuantity());
        assertEquals(750, customer.getBalance());
    }
}
