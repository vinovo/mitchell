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
                sb.append(Constants.DOUBLE_QUOTES_CHR).append(lower).append(Constants.DOUBLE_QUOTES_CHR);
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
                sb.append(Constants.DOUBLE_QUOTES_CHR).append(upper).append(Constants.DOUBLE_QUOTES_CHR);
            } else {
                sb.append(upper);
            }
        }

        sb.append(')');

        return sb.toString();
    }
}
