package entity;

public class SqlCondition {
    private char expression;

    private String value;

    public SqlCondition(char expression, String value) {
        this.expression = expression;
        this.value = value;
    }

    public char getExpression() {
        return expression;
    }

    public void setExpression(char expression) {
        this.expression = expression;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
