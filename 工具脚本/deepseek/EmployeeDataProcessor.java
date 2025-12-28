import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDataProcessor {

    // 部门统计数据结构
    private static class DepartmentStats {
        private final List<Double> performanceScores = new ArrayList<>();
        private long totalSalarySum = 0;
        private int employeeCount = 0;

        public void addEmployee(double performanceScore, int totalSalary) {
            performanceScores.add(performanceScore);
            totalSalarySum += totalSalary;
            employeeCount++;
        }

        public int getAverageSalary() {
            return (int) (totalSalarySum / employeeCount);
        }

        public double getMedianPerformance() {
            Collections.sort(performanceScores);
            int size = performanceScores.size();
            if (size % 2 == 0) {
                return (performanceScores.get(size / 2 - 1) + performanceScores.get(size / 2)) / 2.0;
            } else {
                return performanceScores.get(size / 2);
            }
        }

        public int getEmployeeCount() {
            return employeeCount;
        }
    }

    // 员工数据结构
    private static class Employee {
        private final int id;
        private final String department;
        private final int baseSalary;
        private final int bonus;
        private final double performanceScore;
        private final int joinYear;
        private final int totalSalary;

        public Employee(String[] data) {
            this.id = Integer.parseInt(data[0].trim());
            this.department = data[1].trim();
            this.baseSalary = Integer.parseInt(data[2].trim());
            this.bonus = Integer.parseInt(data[3].trim());
            this.performanceScore = Double.parseDouble(data[4].trim());
            this.joinYear = Integer.parseInt(data[5].trim());
            this.totalSalary = this.baseSalary + this.bonus;
        }

        public int getId() { return id; }
        public String getDepartment() { return department; }
        public int getTotalSalary() { return totalSalary; }
        public double getPerformanceScore() { return performanceScore; }
        public int getJoinYear() { return joinYear; }
    }

    // 用于Top10比较的Comparator
    private static class TopEmployeeComparator implements Comparator<Employee> {
        @Override
        public int compare(Employee e1, Employee e2) {
            // 首先按总薪资升序排列（最小堆）
            int salaryCompare = Integer.compare(e1.getTotalSalary(), e2.getTotalSalary());
            if (salaryCompare != 0) {
                return salaryCompare;
            }
            // 总薪资相同，按id降序排列
            return Integer.compare(e2.getId(), e1.getId());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入CSV文件路径: ");
        String filePath = scanner.nextLine();
        long stime = System.currentTimeMillis();
        try {
            processEmployeeData(filePath);
        } catch (IOException e) {
            System.err.println("处理文件时出错: " + e.getMessage());
        }
        System.out.printf("运行时间%dms",System.currentTimeMillis()-stime);
    }

    private static void processEmployeeData(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // 用于存储部门统计信息
        Map<String, DepartmentStats> departmentStats = new HashMap<>();

        // 用于计算新员工高绩效占比
        int newEmployeeCount = 0;
        int highPerformanceNewEmployeeCount = 0;

        // 使用最小堆来维护Top10（堆顶是当前10个中最小的）
        PriorityQueue<Employee> top10Heap = new PriorityQueue<>(new TopEmployeeComparator());

        // 读取并处理文件
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            // 跳过表头
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length != 6) continue;

                Employee employee = new Employee(data);

                // 更新部门统计
                departmentStats.computeIfAbsent(employee.getDepartment(),
                                k -> new DepartmentStats())
                        .addEmployee(employee.getPerformanceScore(), employee.getTotalSalary());

                // 更新Top10堆
                if (top10Heap.size() < 10) {
                    top10Heap.offer(employee);
                } else if (top10Heap.size() == 10) {
                    Employee minEmployee = top10Heap.peek();
                    if (employee.getTotalSalary() > minEmployee.getTotalSalary() ||
                            (employee.getTotalSalary() == minEmployee.getTotalSalary() &&
                                    employee.getId() < minEmployee.getId())) {
                        top10Heap.poll();
                        top10Heap.offer(employee);
                    }
                }

                // 统计新员工高绩效占比
                if (employee.getJoinYear() >= 2020) {
                    newEmployeeCount++;
                    if (employee.getPerformanceScore() >= 4.5) {
                        highPerformanceNewEmployeeCount++;
                    }
                }
            }
        }

        // 输出结果
        outputResults(departmentStats, top10Heap, newEmployeeCount, highPerformanceNewEmployeeCount);
    }

    private static void outputResults(Map<String, DepartmentStats> departmentStats,
                                      PriorityQueue<Employee> top10Heap,
                                      int newEmployeeCount,
                                      int highPerformanceNewEmployeeCount) {

        // 1. 部门统计
        System.out.println("部门统计:");
        System.out.println("部门名称 员工数 平均总薪资 中位数绩效");

        // 按部门名称排序
        List<String> sortedDepartments = departmentStats.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        NumberFormat performanceFormat = new DecimalFormat("0.00");

        // 用于存储部门平均薪资，以便后续找出最高平均薪资部门
        Map<String, Integer> deptAvgSalaries = new HashMap<>();

        for (String dept : sortedDepartments) {
            DepartmentStats stats = departmentStats.get(dept);
            int avgSalary = stats.getAverageSalary();
            deptAvgSalaries.put(dept, avgSalary);
            System.out.printf("%s %d %d %s%n",
                    dept,
                    stats.getEmployeeCount(),
                    avgSalary,
                    performanceFormat.format(stats.getMedianPerformance())
            );
        }

        // 2. 总薪资Top10
        System.out.println("\n总薪资Top10:");
        System.out.println("employee_id department total_salary");

        // 从堆中提取并排序（按总薪资降序，id升序）
        List<Employee> top10List = new ArrayList<>(top10Heap);
        top10List.sort((e1, e2) -> {
            // 首先按总薪资降序
            int salaryCompare = Integer.compare(e2.getTotalSalary(), e1.getTotalSalary());
            if (salaryCompare != 0) {
                return salaryCompare;
            }
            // 总薪资相同，按id升序
            return Integer.compare(e1.getId(), e2.getId());
        });

        for (Employee emp : top10List) {
            System.out.printf("%d %s %d%n",
                    emp.getId(),
                    emp.getDepartment(),
                    emp.getTotalSalary()
            );
        }

        // 3. 新员工高绩效占比
        double highPerformanceRatio = 0.0;
        if (newEmployeeCount > 0) {
            highPerformanceRatio = (double) highPerformanceNewEmployeeCount / newEmployeeCount * 100;
        }

        DecimalFormat ratioFormat = new DecimalFormat("0.0000");
        System.out.printf("\n新员工高绩效占比: %s%%%n", ratioFormat.format(highPerformanceRatio));

        // 4. 薪资最高部门
        System.out.print("\n薪资最高部门: ");

        // 找出最高平均薪资
        int maxAvgSalary = -1;
        for (Map.Entry<String, Integer> entry : deptAvgSalaries.entrySet()) {
            maxAvgSalary = Math.max(maxAvgSalary, entry.getValue());
        }

        // 由于maxAvgSalary在lambda中会被使用，我们需要使用final变量
        final int finalMaxAvgSalary = maxAvgSalary;

        // 找出所有薪资等于最高平均薪资的部门
        List<String> highestSalaryDepts = deptAvgSalaries.entrySet().stream()
                .filter(entry -> entry.getValue() == finalMaxAvgSalary)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());

        System.out.println(String.join(",", highestSalaryDepts));
    }
}