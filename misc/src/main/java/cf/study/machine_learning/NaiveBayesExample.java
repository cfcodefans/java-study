package cf.study.machine_learning;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NaiveBayesExample {
    private static final Logger log = LogManager.getLogger(NaiveBayesExample.class);

    static List<List<Double>> load(String pathStr) {
        try (InputStream is = NaiveBayesExample.class.getResourceAsStream(pathStr)) {
            List<String> lines = IOUtils.readLines(is);
            List<List<Double>> collected = lines.stream().map((String line) -> line.split(","))
                .map((String[] strs) -> Stream.of(strs).map(Double::parseDouble).collect(Collectors.toList()))
                .collect(Collectors.toList());
            return collected;
        } catch (Exception e) {
            log.error(String.format("can not load from %s", pathStr), e);
        }
        return Collections.emptyList();
    }

    static Pair<List<List<Double>>, List<List<Double>>> split(List<List<Double>> rows, double ratio) {
        int size = rows.size();
        int divide = (int) Math.floor(size * ratio);
        return new ImmutablePair<>(rows.subList(0, divide), rows.subList(divide, size));
    }

    static List<ImmutablePair<Double, Double>> summarize(List<List<Double>> rows) {
        List<List<Double>> cols = IntStream.range(0, rows.get(0).size())
            .parallel()
            .mapToObj(i -> rows.stream().map(row -> row.get(i)).collect(Collectors.toList()))
            .collect(Collectors.toList());

        return cols.subList(0, cols.size() - 1).stream().map(col -> {
            double mean = col.stream().mapToDouble(Double::doubleValue).summaryStatistics().getAverage();
            double variance = col.stream().mapToDouble(Double::doubleValue).map(val -> Math.pow(val - mean, 2)).sum() / (col.size() - 1);
            return new ImmutablePair<>(mean, Math.sqrt(variance));
        }).collect(Collectors.toList());
    }

    static Map<Double, List<ImmutablePair<Double, Double>>> summarizeByClass(List<List<Double>> rows) {
        Map<Double, List<List<Double>>> map = new HashMap<>();

        rows.forEach((List<Double> row) -> map.compute(row.get(row.size() - 1), (Double key, List<List<Double>> value) -> {
            List<List<Double>> lists = Optional.ofNullable(value).orElseGet(ArrayList::new);
            lists.add(row);
            return lists;
        }));

        return map.entrySet()
            .stream()
            .map((entry) -> new ImmutablePair<>(entry.getKey(), summarize(entry.getValue())))
            .collect(Collectors.toMap((p) -> p.left, (p) -> p.right));
    }

    static double calculateGaussianProbability(double value, double mean, double stdev) {
        double exponent = Math.exp( -Math.pow(value - mean, 2) / (2 * Math.pow(stdev, 2)));
        return (1 / (Math.sqrt(2 * Math.PI) * stdev)) * exponent;
    }

    static Map<Double, Double> calculateClassProbability(Map<Double, List<ImmutablePair<Double, Double>>> summary, List<Double> sample) {
        return summary.entrySet().stream().map(entry -> {
            Double clz = entry.getKey();
            List<ImmutablePair<Double, Double>> meanAndStdevs = entry.getValue();
            double reduced = IntStream.range(0, meanAndStdevs.size()).mapToDouble(i -> {
                ImmutablePair<Double, Double> p = meanAndStdevs.get(i);
                return calculateGaussianProbability(sample.get(i), p.left, p.right);
            }).reduce(1.0, (v1, v2) -> v1 * v2);
            return new ImmutablePair<>(clz, reduced);
        }).collect(Collectors.toMap(p -> p.left, p -> p.right));
    }

    public static void main(String[] args) {
        List<List<Double>> rows = load("/" + NaiveBayesExample.class.getPackage().getName().replace('.', '/') + "/pima-indians-diabetes.data");
        log.info(String.format("loaded %d rows", rows.size()));

        Collections.shuffle(rows);
        Pair<List<List<Double>>, List<List<Double>>> trainAndTest = split(rows, 0.67);
        List<List<Double>> train = trainAndTest.getLeft();
        List<List<Double>> test = trainAndTest.getRight();

        Map<Double, List<ImmutablePair<Double, Double>>> summary = summarizeByClass(train);
        log.info(summary);

        int correctCnt = 0;
        for (List<Double> row : test) {
            int control = row.get(row.size() - 1).intValue();
            Map<Double, Double> result = calculateClassProbability(summary, row);
//            log.info("{}\t{}", control, result);
            Double p0 = result.get(0.0d);
            Double p1 = result.get(1.0d);
            if ((control == 1 && p1 > p0) || (control == 0 && p1 < p0)) {
                correctCnt++;
            }
        }
        log.info("total\t{}\tcorrect\t{}", test.size(), correctCnt);
    }
}
