package ubb.mihai.exam_2pl.scheduling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ubb.mihai.exam_2pl.scheduling.domain.Command;
import ubb.mihai.exam_2pl.scheduling.domain.Transaction;

import java.util.*;

@Component
@Getter
@Setter
@RequiredArgsConstructor
public class LockTable {
    // Map<table, Map<transactionID, lockType>>
    private Map<String, Map<Long, List<String>>> lockTable = new HashMap<>();

    private final ConflictGraph conflictGraph;


    public synchronized boolean acquireLock(Transaction transaction, Command command) {
        String table = command.getTable();
        String lockType = command.getMode();

        Map<Long, List<String>> locks;
        // There are other locks on the required table
        if (lockTable.containsKey(table)) {
            locks = lockTable.get(table);
            boolean canAcquire = true;
            for (Map.Entry<Long, List<String>> entry : locks.entrySet()) {
                Long holderId = entry.getKey();
                List<String> heldTypes = entry.getValue();

                // Other transactions have locks on that table
                if (!Objects.equals(holderId, transaction.getId())) {
                    // We want a readLock, so we can share the resource with others
                    if (Objects.equals(lockType, "R")) {
                        for (String heldType : heldTypes) {
                            //The locks are incompatible
                            if (Objects.equals(heldType, "W")) {
                                canAcquire = false;
                                // add to waitForGraph
                                conflictGraph.addDependency(transaction.getId(), holderId);
                            }
                            // If the locks are compatible, do nothing
                        }
                    }
                    // We want a writeLock so we can't share
                    else {
                        if (!CollectionUtils.isEmpty(heldTypes)) {
                            canAcquire = false;
                            // add to waitForGraph
                            conflictGraph.addDependency(transaction.getId(), holderId);
                        }
                    }
                }
                // Same transaction already has a lock
                else {
                    // If the transaction already has a lock of desired type
                    if (heldTypes.contains(lockType)) {
                        return true;
                    }
                    // If not, do nothing yet
                }
            }
            // Acquire the lock
            if (canAcquire) {
                // Add lock type to the list or create if this is the first lock to be acquired
                List<String> transactionLocks = !CollectionUtils.isEmpty(locks.get(transaction.getId())) ?
                        locks.get(transaction.getId()) : new ArrayList<>();
                transactionLocks.add(lockType);
                locks.put(transaction.getId(), transactionLocks);
                return true;
            }
        }
        // There are no locks on the required table
        else {
            locks = new HashMap<>();
            List<String> lockTypes = new ArrayList<>();
            lockTypes.add(command.getMode());
            locks.put(transaction.getId(), lockTypes);
            lockTable.put(table, locks);
            return true;
        }
        return false;
    }

    public synchronized void releaseLocks(Transaction transaction) {
        // Going through each table that has locks
        Iterator<Map.Entry<String, Map<Long, List<String>>>> tableIterator = lockTable.entrySet().iterator();
        while (tableIterator.hasNext()) {
            Map.Entry<String, Map<Long, List<String>>> tableLocks = tableIterator.next();
            String table = tableLocks.getKey();
            // Going through each transaction that has locks on that table
            Map<Long, List<String>> transactionLocks = tableLocks.getValue();

            // Remove all the locks of the desired transaction from that table
            transactionLocks.entrySet().removeIf(entry -> Objects.equals(entry.getKey(), transaction.getId()));

            // Removing that table as an entry if it does not have anymore locks
            if (transactionLocks.isEmpty()) {
                tableIterator.remove();
            }
        }
        conflictGraph.removeDependencies(transaction.getId());
    }


//    public synchronized void releaseLocks(Transaction transaction) {
//        // Going through each table that has locks
//        for (Map.Entry<String, Map<Long, List<String>>> tableLocks : lockTable.entrySet()) {
//            String table = tableLocks.getKey();
//            Map<Long, List<String>> transactionLocks = tableLocks.getValue();
//            // Going through each transaction that has locks on that table
//            for (Long transactionId : transactionLocks.keySet()) {
//                // Remove all the locks of the desired transaction from that table
//                if(Objects.equals(transactionId, transaction.getId())) {
//                    transactionLocks.remove(transactionId);
//                }
//            }
//            // Removing that table as an entry if it does not have anymore locks
//            if(CollectionUtils.isEmpty(tableLocks.getValue())){
//                lockTable.remove(table);
//            }
//        }
//    }
}
