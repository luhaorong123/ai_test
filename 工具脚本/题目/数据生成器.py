import csv
import random
import sys
from collections import defaultdict, Counter
import heapq

# 设置随机种子，确保每次生成数据和答案完全相同
random.seed(42)

DEPARTMENTS = ["Engineering", "Sales", "Marketing", "HR", "Finance", "Operations", "Support", "R&D"]
NUM_EMPLOYEES = 399999

# 用于同步统计的结构
dept_employee_count = Counter()                    # 部门 -> 员工数
dept_total_salary_sum = defaultdict(int)           # 部门 -> 总薪资之和
dept_perf_scores = defaultdict(list)               # 部门 -> 所有绩效分数列表（只为中位数保留）
all_total_salaries_heap = []                      # 小顶堆，维护总薪资最高的10名（-total, eid, dept）
new_employee_count = 0                            # 2020年及以后入职员工数
new_high_perf_count = 0                            # 2020年及以后入职且绩效 >=4.5 的人数

print("正在生成数据并同步统计...", file=sys.stderr)

for eid in range(1, NUM_EMPLOYEES + 1):
    dept = random.choice(DEPARTMENTS)
    base = random.randint(80000, 400000)
    bonus = random.randint(0, 200000)
    total = base + bonus
    perf = round(random.uniform(1.0, 5.0), 1)
    join_year = random.randint(2015, 2025)
    
    # 同步更新统计
    dept_employee_count[dept] += 1
    dept_total_salary_sum[dept] += total
    dept_perf_scores[dept].append(perf)
    
    # 维护总薪资 Top10（使用小顶堆，堆大小不超过10）
    if len(all_total_salaries_heap) < 10:
        heapq.heappush(all_total_salaries_heap, (total, eid, dept))
    elif total > all_total_salaries_heap[0][0]:
        heapq.heapreplace(all_total_salaries_heap, (total, eid, dept))
    
    # 新员工高绩效统计
    if join_year >= 2020:
        new_employee_count += 1
        if perf >= 4.5:
            new_high_perf_count += 1

# 写入 CSV（这一步仍然需要，因为题目要求提供数据文件供 Java 程序读取）
with open("employee_data.csv", "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["employee_id", "department", "base_salary", "bonus", "performance_score", "join_year"])
    
    # 重新生成一遍数据来写入文件（因为我们没有保存整行数据）
    # 注意：由于 random.seed(42) 已固定，第二次循环会生成完全相同的数据序列
    random.seed(42)
    for eid in range(1, NUM_EMPLOYEES + 1):
        dept = random.choice(DEPARTMENTS)
        base = random.randint(80000, 400000)
        bonus = random.randint(0, 200000)
        perf = round(random.uniform(1.0, 5.0), 1)
        join_year = random.randint(2015, 2025)
        writer.writerow([eid, dept, base, bonus, perf, join_year])

print("数据生成完成：employee_data.csv（{} 行数据）".format(NUM_EMPLOYEES + 1), file=sys.stderr)

# ==================== 直接输出正确答案（无需二次遍历） ====================
print("\n=== 本测试数据的正确答案 ===")

# 1. 部门统计（按部门名字典序）
print("部门统计:")
print("部门名称 员工数 平均总薪资 中位数绩效")
for dept in sorted(dept_employee_count.keys()):
    count = dept_employee_count[dept]
    avg_salary = int(round(dept_total_salary_sum[dept] / count))
    
    # 中位数需要排序绩效列表
    perfs = sorted(dept_perf_scores[dept])
    n = len(perfs)
    if n % 2 == 1:
        median_perf = perfs[n // 2]
    else:
        median_perf = (perfs[n // 2 - 1] + perfs[n // 2]) / 2
    median_perf = round(median_perf, 2)
    
    print(f"{dept} {count} {avg_salary} {median_perf:.2f}")

# 2. 总薪资 Top10（从堆中提取并正确排序）
top10_raw = [(total, eid, dept) for total, eid, dept in all_total_salaries_heap]
top10 = sorted(top10_raw, key=lambda x: (-x[0], x[1]))  # 总薪资降序，eid升序
print("\n总薪资Top10:")
print("employee_id department total_salary")
for total, eid, dept in top10:
    print(f"{eid} {dept} {total}")

# 3. 新员工高绩效占比
if new_employee_count > 0:
    ratio = new_high_perf_count / new_employee_count * 100
else:
    ratio = 0.0
print(f"\n新员工高绩效占比: {ratio:.4f}%")

# 4. 薪资最高部门
dept_avg = {dept: dept_total_salary_sum[dept] / dept_employee_count[dept] 
            for dept in dept_employee_count}
max_avg = max(dept_avg.values())
highest_depts = [dept for dept, avg in dept_avg.items() if avg == max_avg]
highest_depts.sort()
print("\n薪资最高部门:", ",".join(highest_depts))