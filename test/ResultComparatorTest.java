import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultComparatorTest {
    ResultComparator resultComparator;

    @BeforeEach
    void setUp() {
        resultComparator = new ResultComparator();
    }

    @Test
    void compare() {
        assertEquals(1, resultComparator.compare(new Tax("Copenhagen", "2022-02-19", "2022-02-19", "Weekly", (float) 0.1), new Tax("Copenhagen", "2022-02-19", "2022-02-19", "Daily", (float) 0.1)));
    }
}