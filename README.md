# 第三组：定期存款管理系统

这是一个符合题目限制的基础 Java 命令行程序。系统使用对象数组储存固定 5 位客户的定期存款资料，并用简单利息计算每位客户的利息和到期金额。

## 计算公式

```text
利息 = 定期存款金额 × (年利率 ÷ 100) × 存款期限（年）
到期金额 = 定期存款金额 + 利息
```

例如：本金 RM 10,000、年利率 3.5%、期限 2 年：

```text
利息 = 10,000 × 0.035 × 2 = RM 700
到期金额 = 10,000 + 700 = RM 10,700
```

## 文件结构

```text
src/FixedDeposit.java            一位客户的定期存款资料及计算方法
src/FixedDepositManagement.java  主程序、输入验证、数组储存和报告输出
sample-input.txt                 可直接用于演示的 5 位客户资料
```

## 编译与运行

请在项目根目录执行：

```bash
javac -d out src/FixedDeposit.java src/FixedDepositManagement.java
java -cp out FixedDepositManagement
```

使用示例数据快速演示：

```bash
java -cp out FixedDepositManagement < sample-input.txt
```

## 题目要求对应

- 数组：`FixedDeposit[]` 固定储存 5 个对象。
- Scanner 输入：录入客户姓名、本金、年利率和期限。
- if / else：检查数字是否合法，并处理过长姓名。
- 循环：录入 5 位客户、逐位计算和展示结果。
- 类与对象：`FixedDeposit` 表示一笔客户定期存款。
- 方法：分别负责输入、验证、利息计算、到期金额计算和报告展示。
- 未使用继承、多态、接口、集合框架或数据库。

金额统一以 RM 展示。程序允许期限使用小数，例如 `0.5` 表示半年；年利率允许输入 `0`，本金和期限必须大于 `0`。
