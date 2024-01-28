package ubb.mihai.exam_2pl.scheduling.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
