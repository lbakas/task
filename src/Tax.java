public class Tax {
    String municipality;
    String startDate;
    String endDate;
    String period;
    float rate;

    Tax(String municipality, String startDate, String endDate, String period, float rate) {
        this.municipality = municipality;
        this.startDate = startDate;
        this.endDate = endDate;
        this.period = period;
        this.rate = rate;
    }
}
