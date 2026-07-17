# 第三组：定期存款管理系统

系统使用固定长度对象数组储存 5 位客户的定期存款资料，并根据简单利息计算每位客户的利息和到期金额。本次版本是在保留原有功能的基础上完成的 Audit Compliance Hotfix。

系统中所有金额的货币单位统一为人民币 `RMB`。

## 计算公式

```text
利息 = 定期存款金额 × (年利率 ÷ 100) × 存款期限（年）
到期金额 = 定期存款金额 + 利息
```

金额使用 `BigDecimal` 计算并四舍五入到 2 位小数。年利率支持负数、零和正数，合理范围设为 `-20% 至 20%`；本金和期限必须大于 `0`。

负利率同样使用简单利息公式。例如，存款 `RMB 10,000`、年利率 `-1%`、期限 2 年：

```text
利息 = 10,000 × (-1 ÷ 100) × 2 = RMB -200
到期金额 = 10,000 - 200 = RMB 9,800
```

## 输入规则

- 客户姓名不能为空，也不能是 `123` 等纯数字。
- 姓名必须以字母开头。
- 支持不同语言的 Unicode 字母，包括英文、带重音英文字符和中文姓名。
- 支持常见英文姓名格式：空格、连字符 `-`、英文或弯曲撇号 `'` / `’`，以及缩写点 `.`。
- 合法示例：`Alice Tan`、`Anne-Marie O'Connor`、`J. R. Smith`、`José Álvarez`。
- 非法示例：`123`、`John3`、`Alice@Bank`。
- 存款金额和期限必须大于 `0`。
- 年利率必须介于 `-20%` 和 `20%`，包括两个边界值。

## Audit Compliance 选择

### Requirement 2 - Interface

选择原因：利率政策可能变化，不应直接写死在客户资料类中。

- `InterestCalculator` 定义统一利息计算规则。
- `SimpleInterestCalculator` 实现当前简单利息政策。
- `FixedDeposit` 通过构造方法接收计算器，未来更换政策不需要修改客户数据类。

### Requirement 4 - Exception Handling

选择原因：非法姓名、非数字金额及超出合理范围的利率不能导致系统崩溃。

- `InvalidDepositException` 表达不合法的定期存款资料。
- 输入程序捕获 `NumberFormatException` 和 `InvalidDepositException`，显示原因并要求重新输入。
- `FixedDeposit` 构造方法再次验证数据，防止其他调用方式绕过输入检查。
- 提前结束输入时，主程序安全退出，不输出未处理的异常。

### Requirement 6 - Unit Testing

选择原因：紧急修改必须可重复验证，避免破坏原有功能。

自动化测试覆盖：

- Normal Case：正常简单利息和到期金额。
- Boundary Case：最低 `-20%`、`0%` 和最高 `20%` 利率。
- Invalid Input：文字金额、非法姓名、零本金及超出范围的利率。
- Exception Handling：负数本金、纯数字姓名和非法利率。
- Interface：替换利息政策而不修改 `FixedDeposit`。
- Report：使用 RMB 显示超长姓名和超大金额。

## Code Review 改进

- 金额及计算由 `double` 改为 `BigDecimal`。
- 所有货币单位统一为 RMB。
- 对合理负利率、零利率、正利率及超出 `-20% 至 20%` 的利率分别处理。
- 客户姓名不再只检查空白，还会拒绝数字和非法符号，同时兼容常见英文姓名格式。
- 报告改为逐客户分块显示，不依赖固定表格列宽，可完整显示长姓名和大金额。
- 屏幕公式补充 `/ 100`，与实际百分比输入保持一致。

## 文件结构

```text
src/InterestCalculator.java               利息规则接口
src/SimpleInterestCalculator.java         简单利息实现
src/InvalidDepositException.java          业务数据异常
src/FixedDeposit.java                     客户定期存款资料
src/FixedDepositManagement.java           输入、异常处理和报告输出
test/FixedDepositTestEngineerSuite.java   自动化单元测试
sample-input.txt                          5 位客户演示资料
```

## 编译与运行

```bash
javac -d out src/*.java
java -cp out FixedDepositManagement
```

使用示例输入：

```bash
java -cp out FixedDepositManagement < sample-input.txt
```

## 运行自动化测试

```bash
javac -d out src/*.java test/*.java
java -cp out FixedDepositTestEngineerSuite
```

只有全部测试通过后，系统才符合本次部署条件。
