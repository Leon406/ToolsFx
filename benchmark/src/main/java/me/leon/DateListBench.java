package me.leon;

import static cn.hutool.core.date.DatePattern.NORM_MONTH_FORMAT;
import static cn.hutool.core.date.DatePattern.NORM_MONTH_FORMATTER;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@Warmup(iterations = 3)
@Measurement(iterations = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@Fork(1)
public class DateListBench {
    @Benchmark
    public void baseline() {
        // do nothing
    }

    @Benchmark
    public void oneMonth(Blackhole blackhole) {
        blackhole.consume(getOneMonth());
    }

    @Benchmark
    public void last12(Blackhole blackhole) {
        blackhole.consume(last12Month(new Date()));
    }

    @Benchmark
    public void last12Two(Blackhole blackhole) {
        blackhole.consume(last12Month2());
    }

    public static List<String> last12Month(Date date) {
        if (date.getDate() == 1) {
            date = DateUtil.offsetDay(date, -1);
        }
        List<String> list = new ArrayList<>(12);
        for (int i = 11; i >= 0; i--) {
            DateTime dateTime = DateUtil.offsetMonth(date, -i);
            list.add(NORM_MONTH_FORMAT.format(dateTime));
        }
        return list;
    }

    public static List<String> last12Month2() {
        return last12Month2(LocalDate.now());
    }

    public static List<String> last12Month2(LocalDate localDate) {

        if (localDate.getDayOfMonth() == 1) {
            localDate = localDate.minus(1, ChronoUnit.DAYS);
        }
        List<String> list = new ArrayList<>(12);
        for (int i = 11; i >= 0; i--) {
            list.add(localDate.minus(i, ChronoUnit.MONTHS).format(NORM_MONTH_FORMATTER));
        }
        return list;
    }

    /** 获取最近12个月月份 */
    public static List<String> getOneMonth() {
        List<String> monthList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // 1号就从上个月开始算
        int num = 1;
        if (isFirstDayOfMonth(calendar)) {
            num = 0;
        }
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + num);
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, -1); // 1个月前
            String month =
                    calendar.get(Calendar.YEAR) + "-" + fillZero(calendar.get(Calendar.MONTH) + 1);
            monthList.add(month);
        }
        Collections.reverse(monthList);
        return monthList;
    }

    /**
     * 判断今天是否是1号
     *
     * @param calendar 日历对象
     * @return 是否第一天
     */
    public static boolean isFirstDayOfMonth(Calendar calendar) {
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        if (calendar.get(Calendar.DAY_OF_MONTH) == 2) {
            return true;
        } else {
            return false;
        }
    }

    /** 格式化月份 */
    public static String fillZero(int i) {
        return i < 10 ? "0" + i : String.valueOf(i);
    }
}
