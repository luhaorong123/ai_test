import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class EmployeeSalaryAnalyzer {

    private static final List<String> DEPARTMENTS = List.of(
            "Engineering", "Sales", "Marketing", "HR", "Finance", "Operations", "Support", "R&D"
    );

    // 记录每个员工的完整信息
    static class Employee {
        int id;
        String department;
        int baseSalary;
        int bonus;
        double performance;
        int joinYear;

        int totalSalary() {
            return baseSalary + bonus;
        }
    }

    // 每个部门的统计数据
    static class DeptStats {
        long count = 0;
        long totalSalarySum = 0;
        List<Double> performances = new ArrayList<>();

        void add(Employee e) {
            count++;
            totalSalarySum += e.totalSalary();
            performances.add(e.performance);
        }

        long avgSalary() {
            return count == 0 ? 0 : totalSalarySum / count;
        }

        double medianPerformance() {
            if (performances.isEmpty()) return 0.0;
            List<Double> sorted = new ArrayList<>(performances);
            Collections.sort(sorted);
            int n = sorted.size();
            if (n % 2 == 1) {
                return sorted.get(n / 2);
            } else {
                double a = sorted.get(n / 2 - 1);
                double b = sorted.get(n / 2);
                return (a + b) / 2.0;
            }
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入CSV文件绝对路径: ");
        String path = scanner.nextLine().trim();
        scanner.close();
        long stime = System.currentTimeMillis();
        List<Employee> employees = new ArrayList<>(400000);

        // 第一遍：读取所有员工数据
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            br.readLine(); // 跳过表头
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Employee e = new Employee();
                e.id = Integer.parseInt(parts[0].trim());
                e.department = parts[1].trim();
                e.baseSalary = Integer.parseInt(parts[2].trim());
                e.bonus = Integer.parseInt(parts[3].trim());
                e.performance = Double.parseDouble(parts[4].trim());
                e.joinYear = Integer.parseInt(parts[5].trim());
                employees.add(e);
            }
        } catch (IOException ex) {
            System.err.println("读取文件失败: " + ex.getMessage());
            return;
        }

        // 部门统计
        Map<String, DeptStats> deptMap = new HashMap<>();
        for (String dept : DEPARTMENTS) {
            deptMap.put(dept, new DeptStats());
        }

        // Top10 总薪资员工
        PriorityQueue<Employee> top10 = new PriorityQueue<>(
                (a, b) -> a.totalSalary() != b.totalSalary()
                        ? Integer.compare(a.totalSalary(), b.totalSalary())
                        : Integer.compare(b.id, a.id)
        );

        // 新员工高绩效统计
        long newHighPerfCount = 0;
        long newEmployeeCount = 0;

        for (Employee e : employees) {
            DeptStats stats = deptMap.get(e.department);
            if (stats != null) {
                stats.add(e);
            }

            // 维护 Top10（小根堆，容量10）
            int total = e.totalSalary();
            if (top10.size() < 10) {
                top10.offer(e);
            } else if (total > top10.peek().totalSalary()) {
                top10.poll();
                top10.offer(e);
            } else if (total == top10.peek().totalSalary() && e.id < top10.peek().id) {
                top10.poll();
                top10.offer(e);
            }

            // 新员工高绩效统计
            if (e.joinYear >= 2020) {
                newEmployeeCount++;
                if (e.performance >= 4.5) {
                    newHighPerfCount++;
                }
            }
        }

        // 输出部门统计（按部门名字典序）
        System.out.println("部门统计:");
        System.out.println("部门名称 员工数 平均总薪资 中位数绩效");
        DEPARTMENTS.stream().sorted().forEach(dept -> {
            DeptStats s = deptMap.get(dept);
            if (s.count > 0) {
                System.out.printf("%-10s %6d %10d     %.2f%n",
                        dept, s.count, s.avgSalary(), s.medianPerformance());
            }
        });

        // 输出 Top10（转为列表后反转得到降序）
        System.out.println("\n总薪资Top10:");
        System.out.println("employee_id department total_salary");
        List<Employee> topList = new ArrayList<>();
        while (!top10.isEmpty()) {
            topList.add(top10.poll());
        }
        Collections.reverse(topList);
        for (Employee e : topList) {
            System.out.printf("%d %s %d%n", e.id, e.department, e.totalSalary());
        }

        // 新员工高绩效占比
        System.out.println();
        double ratio = newEmployeeCount == 0 ? 0.0 : (double) newHighPerfCount / newEmployeeCount * 100;
        System.out.printf("新员工高绩效占比: %.4f%%%n", ratio);

        // 找出平均总薪资最高的部门
        long maxAvg = deptMap.values().stream()
                .filter(s -> s.count > 0)
                .mapToLong(DeptStats::avgSalary)
                .max()
                .orElse(0);

        List<String> highestDepts = DEPARTMENTS.stream()
                .filter(d -> deptMap.get(d).count > 0 && deptMap.get(d).avgSalary() == maxAvg)
                .sorted()
                .collect(Collectors.toList());

        System.out.print("薪资最高部门: ");
        System.out.println(String.join(",", highestDepts));
        long etime = System.currentTimeMillis();
        System.out.printf("运行时间%dms",etime-stime);
    }
}