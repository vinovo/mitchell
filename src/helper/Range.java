package helper;

public class Range {
    private String lower;
    private String upper;

    public Range(String lower, String upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public String generateConstraintString(String key, boolean isString) {
        StringBuilder sb = new StringBuilder("(");

        if (lower != null) {
            sb.append(key).append(" >= ");

            if (isString) {
                sb.append(Constants.CHAR_DOUBLE_QUOTES).append(lower).append(Constants.CHAR_DOUBLE_QUOTES);
            } else {
                sb.append(lower);
            }
        }

        if (upper != null) {
            if (lower != null) {
                sb.append(" AND ");
            }

            sb.append(key).append(" <= ");

            if (isString) {
                sb.append(Constants.CHAR_DOUBLE_QUOTES).append(upper).append(Constants.CHAR_DOUBLE_QUOTES);
            } else {
                sb.append(upper);
            }
        }

        sb.append(')');

        return sb.toString();
    }
}
