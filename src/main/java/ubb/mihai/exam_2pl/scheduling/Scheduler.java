package ubb.mihai.exam_2pl.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ubb.mihai.exam_2pl.scheduling.domain.Command;
import ubb.mihai.exam_2pl.scheduling.domain.Transaction;
import ubb.mihai.exam_2pl.scheduling.exception.ConflictException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final LockTable lockTable;

    public void scheduleTransaction(Transaction transaction) {
        boolean scheduled = false;
        // Trying to execute the transaction until it can commit
        while (!scheduled) {
            int i = 0;
            List<Command> commands = transaction.getCommands();
            try {
                // Iterating over all commands of the transaction
                for (i = 0; i < transaction.getCommands().size(); i++) {
                    boolean lockAcquired = false;
                    // Waiting for the necessary lock to be acquired
                    while (!lockAcquired) {
                        // An exception can be thrown here in case a deadlock occurs while
                        // waiting for another transaction to finish
                        lockAcquired = lockTable.acquireLock(transaction, commands.get(i));
                        if (lockAcquired) {
                            // Lock necessary for command has been acquired, it can be executed
                            commands.get(i).doAction();
                        }
//                        Thread.sleep(10);
                    }
                }
                // All locks have been acquired and all operations have been performed
                // Releasing locks
                lockTable.releaseLocks(transaction);
                scheduled = true;
            } catch (ConflictException conflictException) {
                // Conflict detected, rollback needed
                i -= 1;
                for (; i >= 0; i--) {
                    commands.get(i).undoAction();
                }
                // Release all locks after performing rollback
                lockTable.releaseLocks(transaction);
            }
//            catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
}
