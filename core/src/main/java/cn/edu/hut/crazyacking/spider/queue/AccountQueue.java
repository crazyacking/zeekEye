package cn.edu.hut.crazyacking.spider.queue;

import cn.edu.hut.crazyacking.spider.parser.bean.Account;

import java.util.LinkedList;

public class AccountQueue {
    private static final LinkedList<Account> accountQueue = new LinkedList<Account>();

    public synchronized static void addElement(Account account) {
        accountQueue.add(account);
    }

    public synchronized static Account outElement() {
        return accountQueue.removeFirst();
    }

    public synchronized static boolean isEmpty() {
        return accountQueue.isEmpty();
    }

    public static int size() {
        return accountQueue.size();
    }

    public static boolean isContains(Account account) {
        return accountQueue.contains(account);
    }
}
