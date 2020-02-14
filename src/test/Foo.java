/**
 * This class is created for testing
 */
package test;

import db.DatabaseHelper;
import model.Table;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Foo implements Table {
    Integer foo = 0;
    String bar = "bar";

    private static final DatabaseHelper db = DatabaseHelper.getDatabaseHelper();

    public Foo(int id) {
        this.foo = id;
    }

    @Override
    public String getTableName() {
        return "foo";
    }

    @Override
    public String getTableSchema() {
        return "foo integer, bar text";
    }

    @Override
    public String getTablePrimaryKey() {
        return "foo";
    }

    @Override
    public Object getPrimaryKeyValue() {
        return foo;
    }

    @Override
    public Map<String, Object> getTuples() {
        HashMap<String, Object> tuples = new HashMap<>();
        tuples.put("foo", foo);
        tuples.put("bar", bar);
        return tuples;
    }

    @Override
    public void writeToDb() throws SQLException {
        db.insert(this);
    }
}
