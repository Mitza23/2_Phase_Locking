package ubb.mihai.exam_2pl.scheduling;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ubb.mihai.exam_2pl.scheduling.exception.ConflictException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
@NoArgsConstructor
public class ConflictGraph {
    //Map<requestingTransactionId, List<holdingTransactionId>>
    private final Map<Long, List<Long>> dependencyGraph = new HashMap<>();

    public synchronized void addDependency(Long requestingId, Long holderId) {
        // Checking for possible cycles
        List<Long> holders = dependencyGraph.get(holderId);
        if (holders != null && holders.contains(requestingId)) {
            throw new ConflictException(STR."Cycle dependency between \{requestingId} and \{holderId}");
        }
        // The requesting transaction already has dependencies
        if (dependencyGraph.containsKey(requestingId)) {
            if (!dependencyGraph.get(requestingId).contains(holderId)) {
                dependencyGraph.get(requestingId).add(holderId);
            }
        }
        // The requesting transaction did not have any dependencies yet
        else {
            List<Long> holdersList = new ArrayList<>();
            holdersList.add(holderId);
            dependencyGraph.put(requestingId, holdersList);
        }
    }

    public synchronized void removeDependencies(Long transactionId) {
        dependencyGraph.remove(transactionId);
        for (List<Long> list : dependencyGraph.values()) {
            list.remove(transactionId);
        }
    }
}
