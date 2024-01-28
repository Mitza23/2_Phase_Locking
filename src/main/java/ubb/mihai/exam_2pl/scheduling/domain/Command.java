package ubb.mihai.exam_2pl.scheduling.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Command {
    private String table;
    private String mode;
    private Runnable doAction;
    private Runnable undoAction;

    public void doAction() {
        doAction.run();
    }

    public void undoAction() {
        undoAction.run();
    }
}
