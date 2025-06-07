package linguistic.summary;

import java.util.Map;

public interface LinguisticSummaryBase {
    String toString();
    double getQualityMeasure(String name);
    void setQualityMeasure(String name, double value);
}
