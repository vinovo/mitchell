package test;

import db.DatabaseHelper;
import model.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseHelperTest {
    private DatabaseHelper db;

    @BeforeEach
    void setUp() {
        db = DatabaseHelper.getDatabaseHelperInTestMode();

        assertDoesNotThrow(() -> {
            db.createTable("foo", "foo integer PRIMARY KEY, bar text");

            for (int i = -1000; i < 10000; i++) {
                db.insert(new Foo(i));
            }
        });
    }


    @AfterEach
    void cleanUp() throws SQLException {
        db.closeOngoingConnection();
    }

    @Test
    void isTableExisted() {
        assertTrue(db.isTableExisted("foo"));
        assertFalse(db.isTableExisted("bar"));
        assertFalse(db.isTableExisted(""));
        assertFalse(db.isTableExisted(null));
    }

    @Test
    void createTable() {
        assertThrows(SQLException.class, () -> db.createTable(null, null));

        assertThrows(SQLException.class, () -> db.createTable(null, ""));

        assertThrows(SQLException.class, () -> db.createTable("", null));

        assertThrows(SQLException.class, () -> db.createTable("", ""));

        assertThrows(SQLException.class, () -> db.createTable("ba ra ra ho ho", "foo integer"));

        assertDoesNotThrow(() -> db.createTable("bar", "foo integer, bar text"));

        assertDoesNotThrow(() -> db.createTable("foo", "foo integer, bar text"));

    }

    @Test
    void insert() {
        assertThrows(SQLException.class, () -> db.insert(new Foo(123)));

        assertThrows(SQLException.class, () -> db.insert(Vehicle.create(214, 2020, "foo", "bar")));

        assertDoesNotThrow(() -> {
            for (int i = 10000; i < 12000; i++) {
                db.insert(new Foo(i));
                assertEquals(getRowCount(db.select("foo", "*", null)), i + 1001);
            }
        });

        assertDoesNotThrow(() -> {
            db.insert(new Foo(2103808929));
        });

        assertDoesNotThrow(() -> db.insert(new Foo(-1500)));
    }

    @Test
    void select() {
        assertThrows(SQLException.class, () -> db.select("vehicle", "*", null));

        assertThrows(SQLException.class, () -> db.select(null, "*", null));

        assertThrows(SQLException.class, () -> db.select("", "*", null));

        assertThrows(SQLException.class, () -> db.select("foo", "*", ""));

        assertThrows(SQLException.class, () -> db.select("foo", "", "model = 1500"));

        assertThrows(SQLException.class, () -> db.select("foo", "model", null));

        assertDoesNotThrow(() -> {
            assertEquals(11000, getRowCount(db.select("foo", null, null)));

            for (int i = 0; i < 10000; i++) {
                assertEquals(i + 1000, getRowCount(db.select("foo", "foo", "foo < " + i)));
            }

            assertEquals(1000, getRowCount(db.select("foo", "bar", "foo < -500 OR foo >= 9500")));
        });
    }

    @Test
    void update() {
        assertDoesNotThrow(() -> {
            db.update(null, new Foo(0));
            assertEquals(11000, getRowCount(db.select("foo", null, null)));
        });

        assertDoesNotThrow(() -> {
            for (int i = -1000; i < 10000; i++) {
                db.update("" + i, new Foo(i-1));
            }

            assertEquals(11000, getRowCount(db.select("foo", null, null)));
        });
    }

    @Test
    void delete() {
        assertDoesNotThrow(() -> {
            db.delete("foo", null);
            db.delete(null, "foo = 1");
            assertEquals(11000, getRowCount(db.select("foo", null, null)));

        });

        assertThrows(SQLException.class, () -> db.delete("", "foo = 0"));


        assertDoesNotThrow(() -> {
            for (int i = -1000; i < 0; i++) {
                db.delete("foo", "foo = " + i);

                assertEquals(11000 - (i + 1001) , getRowCount(db.select("foo", null, null)));
            }

            db.delete("foo", "foo > 0 AND foo < 9999 OR foo = 0");
            assertEquals(1, getRowCount(db.select("foo", null, null)));
        });
    }

    @Test
    void drop() {
        assertDoesNotThrow(() -> {
            db.drop(null);
            db.drop("vehicle");
            db.drop("foo");

            assertFalse(db.isTableExisted("foo"));
        });
    }

    private int getRowCount(ResultSet result) throws SQLException {
        int count = 0;

        while (result.next()) {
            count ++;
        }

        return count;
    }


}