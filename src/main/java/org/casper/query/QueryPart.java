package org.casper.query;


public class QueryPart {
    private Command command;
    private String field;
    private Object value;

    public QueryPart(Command command) {
        this.command = command;
    }

    public QueryPart(Command command, String field, Object value) {
        this.command = command;
        this.field = field;
        this.value = value;
    }

    public QueryPart(Command command, Object value) {
        this.command = command;
        this.value = value;
    }

    public Command getCommand() {
        return command;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public enum Command {
        AND, AND_FIELD,
        OR, OR_FIELD,
        WHERE, WHERE_FIELD,
        EQ, EQ_FIELD,
        NEQ, NEQ_FIELD,
        LIKE, LIKE_FIELD,
        LT, LT_FIELD,
        GT, GT_FIELD,
        IN, IN_FIELD,
        BETWEEN, BETWEEN_FIELD,
        NOT, LIMIT
    }
}
