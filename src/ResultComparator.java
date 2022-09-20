import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class ResultComparator implements Comparator<Tax> {
    List<String> periods = Arrays.asList(new String[]{"Daily","Weekly","Monthly","Yearly"});

    @Override
    public int compare(Tax a, Tax b) {
        return periods.indexOf(a.period) < periods.indexOf(b.period) ? -1 : periods.indexOf(a.period) == periods.indexOf(b.period) ? 0 : 1;
    }
}