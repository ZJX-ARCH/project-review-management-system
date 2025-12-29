# Template Export

Export evaluation templates to Excel with grouped formatting and category subtotals.

## ADDED Requirements

### Requirement: Export selected templates

The system SHALL allow users to select specific templates from the list and export them to Excel.

#### Scenario: Export single template

**Given** the user is on the template list page
**When** they select template "技术创新评审模板" (checkbox)
**And** click "导出选中"
**Then** an Excel file is downloaded
**And** the file contains one template's data
**And** items are grouped by category
**And** category subtotals are shown
**And** grand total is shown

#### Scenario: Export multiple templates

**Given** 3 templates exist in the list
**When** the user selects all 3 templates
**And** clicks "导出选中"
**Then** an Excel file is downloaded
**And** all 3 templates' data is included
**And** each template's items are grouped by category
**And** subtotals and totals are calculated for each

#### Scenario: Prevent export with no selection

**Given** no templates are selected
**When** the user clicks "导出选中"
**Then** an error message is shown: "请至少选择一个模板"
**And** no export occurs

### Requirement: Export all templates

The system SHALL allow users to export all templates matching current search criteria.

#### Scenario: Export all without filters

**Given** 10 templates exist in the system
**And** no search filters are applied
**When** the user clicks "导出全部"
**Then** all 10 templates are exported to Excel
**And** grouped format is applied

#### Scenario: Export all with filters

**Given** 10 templates exist
**And** the user filters by status "启用"
**And** 6 templates match the filter
**When** the user clicks "导出全部"
**Then** only the 6 filtered templates are exported
**And** grouped format is applied

### Requirement: Grouped Excel format with category subtotals

Exported Excel files MUST use grouped format with visual distinction for categories, subtotals, and totals.

#### Scenario: Excel structure for single template

**Given** template "技术创新" has:
- Category "技术创新性" with items: 技术先进性(15分), 创新突破性(10分)
- Category "应用价值" with items: 应用前景(15分), 预期效益(15分)
**When** exported to Excel
**Then** the file structure is:
```
Row 1: 模板编码 | 模板名称 | 大类名称 | 评分项名称 | 满分值 | 评分说明
Row 2: TPL_002 | 技术创新 | 技术创新性 | 技术先进性 | 15 | ...
Row 3: TPL_002 | 技术创新 | 技术创新性 | 创新突破性 | 10 | ...
Row 4: (gray bg) | | 技术创新性 小计 | | 25 | (subtotal row)
Row 5: TPL_002 | 技术创新 | 应用价值 | 应用前景 | 15 | ...
Row 6: TPL_002 | 技术创新 | 应用价值 | 预期效益 | 15 | ...
Row 7: (gray bg) | | 应用价值 小计 | | 30 | (subtotal row)
Row 8: (blue bg) | 技术创新 总计 | | | 55 | (grand total row)
```

#### Scenario: Subtotal row styling

**Given** a category "技术创新性" with 2 items
**When** exported
**Then** the subtotal row has:
- Gray background (GREY_25_PERCENT)
- Bold text
- Category name + " 小计" in category column
- Sum of item scores in score column

#### Scenario: Grand total row styling

**Given** a template with total score 100
**When** exported
**Then** the grand total row has:
- Blue background (LIGHT_BLUE)
- White bold text
- Template name + " 总计" in name column
- Template total score in score column

### Requirement: Handle decimal precision in export

Exported scores MUST maintain decimal precision (5,2) as defined in database schema.

#### Scenario: Export decimal scores

**Given** an item has max score 15.50
**When** exported to Excel
**Then** the Excel cell shows "15.50"
**And** the cell format is number with 2 decimal places
**And** subtotals calculate correctly with decimals
