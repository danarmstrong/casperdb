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
        And, AndField,
        Or, OrField,
        Where, WhereField,
        Eq, EqField,
        Ne, NeField,
        Lt, LtField,
        Le, LeField,
        Gt, GtField,
        Ge, GeField,
        Like, LikeField,
        In, InField,
        Between, BetweenField,
        Not, Limit
    }
}
