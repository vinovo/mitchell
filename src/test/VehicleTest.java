package test;

import db.DatabaseHelper;
import helper.Range;
import model.Vehicle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {
    private static DatabaseHelper db;

    @BeforeAll
    static void init() {
        db = DatabaseHelper.getDatabaseHelperInTestMode();

        assertDoesNotThrow(() -> {
            for (int i = 0; i <= 100; i++) {
                Vehicle.create(i, 1950 + i, "make" + (i % 5), "model" + (i % 5)).writeToDb();
            }
        });
    }

    @AfterAll
    static void clean() throws SQLException {
        db.closeOngoingConnection();
    }

    @Test
    void isTableExist() {
        assertTrue(Vehicle.isTableExist());
    }

    @Test
    void all() throws SQLException {
        assertEquals(101, Vehicle.all().size());
    }

    @Test
    void findById() {
        assertDoesNotThrow(() -> {
            assertNull(Vehicle.findById(-150));

            for (int i = 0; i <= 100; i++) {
                Vehicle vehicle = Vehicle.findById(i);
                assertEquals(i, vehicle.getId());
                assertEquals(i+1950, vehicle.getYear());
                assertEquals("make"+(i%5), vehicle.getMake());
                assertEquals("model"+(i%5), vehicle.getModel());
            }

            assertEquals(101, Vehicle.all().size());
        });
    }

    @Test
    void findByConstraints() {
        assertThrows(SQLException.class, () -> Vehicle.findByConstraints(null));
        assertThrows(SQLException.class, () -> Vehicle.findByConstraints(new HashMap<>()));
        HashMap<String, List<Range>> constraints = new HashMap<>();
        constraints.put("id", null);
        assertThrows(SQLException.class, () -> Vehicle.findByConstraints(constraints));
        constraints.clear();
        ArrayList<Range> ranges = new ArrayList<>();
        ranges.add(null);
        constraints.put("year", ranges);
        assertThrows(SQLException.class, () -> Vehicle.findByConstraints(constraints));

        // custom test
        assertDoesNotThrow(() -> {
            constraints.clear();
            ArrayList<Range> idRange = new ArrayList<>();
            idRange.add(new Range("" + 0, "" + 19));
            idRange.add(new Range("" + 81, "" + 100));
            constraints.put("id", idRange);
            assertEquals(40, Vehicle.findByConstraints(constraints).size());

            ArrayList<Range> yearRange = new ArrayList<>();
            yearRange.add(new Range(""+1950, ""+1969));
            constraints.put("year", yearRange);
            assertEquals(20, Vehicle.findByConstraints(constraints).size());

            ArrayList<Range> makeRange = new ArrayList<>();
            makeRange.add(new Range("make2", "make4"));
            constraints.put("make", makeRange);
            assertEquals(12, Vehicle.findByConstraints(constraints).size());

            ArrayList<Range> modelRange = new ArrayList<>();
            modelRange.add(new Range("model2", "model2"));
            modelRange.add(new Range("model3", "model3"));
            constraints.put("model", modelRange);
            assertEquals(8, Vehicle.findByConstraints(constraints).size());
        });

        // exhaustive test up to 2-d
        assertDoesNotThrow(() -> {

            for (int i = 0; i <= 100; i++) {
                for (int j = i; j <= 100; j++) {
                    constraints.clear();
                    ArrayList<Range> idRange = new ArrayList<>();
                    idRange.add(new Range("" + i, "" + j));
                    ArrayList<Range> yearRange = new ArrayList<>();
                    yearRange.add(new Range("" + (1950 + i), "" + (1950 + j)));
                    constraints.put("id", idRange);
                    constraints.put("year", yearRange);
                    assertEquals(j - i + 1, Vehicle.findByConstraints(constraints).size());

                    for (int k = 0; k <= 5; k++) {
                        for (int l = k; l <= 5; l++) {
                            ArrayList<Range> makeRange = new ArrayList<>();
                            makeRange.add(new Range("make"+k, "make"+l));
                            ArrayList<Range> modelRange = new ArrayList<>();
                            modelRange.add(new Range("model"+k, "model"+l));
                            constraints.put("make", makeRange);
                            constraints.put("model", modelRange);

                            int count = 0;
                            for (int m = i; m <= j; m++) {
                                if (m % 5 >= k && m % 5 <= l) {
                                    count += 1;
                                }
                            }

                            assertEquals(count, Vehicle.findByConstraints(constraints).size());
                        }
                    }
                    constraints.remove("make");
                    constraints.remove("model");

                    // This is set to 10. Running too slow if set to 100
                    for (int k = j + 1; k <= 10; k++) {
                        for (int l = k; l <= 10; l++) {
                            idRange.clear();
                            yearRange.clear();
                            idRange.add(new Range("" + i, "" + j));
                            idRange.add(new Range(""+k, ""+l));
                            yearRange.add(new Range("" + (1950+i), "" + (1950+j)));
                            yearRange.add(new Range(""+(1950+k), ""+(1950+l)));

                            assertEquals(j-i + l-k + 2, Vehicle.findByConstraints(constraints).size());
                        }
                    }
                }
            }
        });
    }

    @Test
    void deleteById() {
        assertDoesNotThrow(() -> {
            Vehicle.deleteById(-1);

            for (int i = 0; i <= 100; i++) {
                Vehicle.deleteById(i);
                assertEquals(101-i-1, Vehicle.all().size());
            }
        });
    }

    @Test
    void updateById() {
        assertThrows(SQLException.class, () -> Vehicle.updateById(1, Vehicle.create(0, 1950, "make", "model")));
        assertDoesNotThrow(() -> {
            Vehicle.updateById(-1, Vehicle.create(-1, 1950, "make", "model"));
            assertEquals(101, Vehicle.all().size());

            for (int i = 0; i <= 100; i++) {
                Vehicle.updateById(i, Vehicle.create(i, 1950, "foo", "bar"));
            }

            assertEquals(101, Vehicle.all().size());

            Vehicle.updateById(0, Vehicle.create(-1, 1951, "bar", "foo"));
        });
    }

    @Test
    void writeToDb() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i <= 100; i++) {
                Vehicle.create(i, 1950, "make", "model").writeToDb();
            }

            assertEquals(101, Vehicle.all().size());

            for (int i = -100; i < 0; i++) {
                Vehicle.create(i, 1950 - i, "foo", "bar").writeToDb();
                assertEquals(101 + (i+101), Vehicle.all().size());
            }
        });

        // restore original db
        assertDoesNotThrow(() -> {
            Vehicle.deleteAll();

            for (int i = 0; i <= 100; i++) {
                Vehicle.create(i, 1950 + i, "make" + (i % 5), "model" + (i % 5)).writeToDb();
            }
        });
    }

}