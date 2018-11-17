package root;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ObjectInputStream;

import static org.testng.Assert.*;

public class AccountTest {

    @DataProvider(name = "deposits")
    public Object[][] deposits() {
        int bound = 1000000;
        return new Object[][]{
            // return, newBalance, blocked, bound, sum
            // normal cases
            {true, 1, false, bound, 1},
            {true, 0, false, bound, 0},
            {true, bound, false, bound, bound},
            {true, bound - 1, false, bound, bound - 1},
            // account is blocked
            {false, 0, true, bound, 100},
            {false, 0, true, bound, 1},
            {false, 0, true, bound, 0},
            {false, 0, true, bound, -1},
            // sum is below than zero
            {false, 0, false, bound, -3},
            {false, 0, false, bound, -1},
            // sum is more than bound
            {false, 0, false, bound, bound + 1}
        };
    }

    @Test(dataProvider = "deposits")
    public void testDeposit(boolean expected, int newBalance, boolean blocked, int bound, int sum) throws Exception {
        Account account = new Account();
        account.blocked = blocked;
        account.bound = bound;
        boolean success = account.deposit(sum);
        assertEquals(success, expected);
        assertEquals(account.balance, newBalance);
    }

    @DataProvider(name = "withdraws")
    public Object[][] withdraws() {
        int bound = 1000000;
        int balance = 0;
        int maxCredit = -1000;
        return new Object[][]{
            // return, newBalance, blocked, bound, balance, maxCredit, sum
            // normal cases
            {true, 0, false, bound, balance, maxCredit, 0},
            {true, -1, false, bound, balance, maxCredit, 1},
            {true, 0, false, bound, 3, maxCredit, 3},
            {true, 100, false, bound, bound, maxCredit, bound - 100},
            {true, 0, false, bound, bound, maxCredit, bound},
            {true, maxCredit + 1, false, bound, balance, maxCredit, balance - maxCredit - 1},
            // sum is below than zero
            {false, 0, false, bound, balance, maxCredit, -1},
            {false, 0, false, bound, balance, maxCredit, -100},
            // account is blocked
            {false, 0, true, bound, balance, maxCredit, 0},
            {false, 0, true, bound, balance, maxCredit, 100},
            {false, 0, true, bound, balance, maxCredit, -100},
            // sum is more than bound
            {false, 0, false, bound, balance, maxCredit, bound + 1},
            // case, when new balance will lower than maxCredit
            {false, balance, false, bound, balance, maxCredit, balance - maxCredit},
            {false, balance, false, bound, balance, maxCredit, balance - maxCredit + 1}
        };
    }

    @Test(dataProvider = "withdraws")
    public void testWithdraw(boolean expected, int newBalance, boolean blocked, int bound, int balance, int maxCredit, int sum) throws Exception {
        Account account = new Account();
        account.blocked = blocked;
        account.bound = bound;
        account.balance = balance;
        account.maxCredit = maxCredit;
        boolean success = account.withdraw(sum);
        assertEquals(success, expected);
        assertEquals(account.balance, newBalance);
    }

    @DataProvider(name = "balances")
    public Object[][] balances() {
        return new Object[][]{
            // expected return, balance
            {0, 0},
            {10, 10},
            {-10, -10},
        };
    }

    @Test(dataProvider = "balances")
    public void testGetBalance(int expected, int balance) throws Exception {
        Account account = new Account();
        account.balance = balance;
        assertEquals(account.getBalance(), expected);
    }

    @DataProvider(name = "maxCredits")
    public Object[][] maxCredits() {
        return new Object[][]{
            // expected return, max credit
            {-1, 1},
            {-10, 10}
        };
    }

    @Test(dataProvider = "maxCredits")
    public void testGetMaxCredit(int expected, int maxCredit) throws Exception {
        Account account = new Account();
        account.maxCredit = maxCredit;
        assertEquals(account.getMaxCredit(), expected);
    }

    @Test
    public void testIsBlocked() throws Exception {
        Account account = new Account();
        account.blocked = false;
        assertTrue(!account.isBlocked());
        account.blocked = true;
        assertTrue(account.isBlocked());
    }

    @Test
    public void testBlock() throws Exception {
        Account account = new Account();
        account.block();
        assertEquals(account.blocked, true);
    }

    @DataProvider(name = "unblockData")
    public Object[][] unblockData() {
        int balance = 0;
        int maxCredit = -1000;
        return new Object[][]{
            // expected return, newBlockStatus, blocked, balance, maxCredit
            // normal cases
            { true, false, true, balance, maxCredit},
            { true, false, false, balance, maxCredit},
            { true, false, true, balance + maxCredit, maxCredit},
            { true, false, false, balance + maxCredit, maxCredit},
            // balance is lower than maxCredit
            { false, true, true, balance + maxCredit - 1, maxCredit},
            { false, false, false, balance + maxCredit - 1, maxCredit},
        };
    }

    @Test(dataProvider = "unblockData")
    public void testUnblock(boolean expectedReturn, boolean newBlockStatus, boolean blocked, int balance, int maxCredit) throws Exception {
        Account account = new Account();
        account.blocked = blocked;
        account.balance = balance;
        account.maxCredit = maxCredit;
        boolean success = account.unblock();
        assertEquals(account.blocked, newBlockStatus);
        assertEquals(success, expectedReturn);
    }

    @DataProvider(name = "setMaxCreditData")
    public Object[][] setMaxCreditData(){
        int bound = 1000000;
        return new Object[][]{
                // expected result, blocked, maxCredit
                // normal cases
                {true, true, bound, 1},
                {true, true, bound, bound - 1},
                {true, true, bound, -bound},
                {true, true, bound, bound},
                // max credit more than bound or less than bound
                {false, true, bound, bound + 1},
                {false, true, bound, -bound - 1},
                // account is not blocked
                {false, false, bound, 10},
                {false, false, bound, -10},
                {false, false, bound, bound},
        };
    }

    @Test(dataProvider = "setMaxCreditData")
    public void testSetMaxCredit(boolean expectedResult, boolean blocked, int bound, int maxCredit) throws Exception {
        Account account = new Account();
        account.bound = bound;
        account.blocked = blocked;
        boolean result = account.setMaxCredit(maxCredit);
        assertEquals(result, expectedResult);
        if(result)
            assertEquals(account.maxCredit, -maxCredit);
    }



}