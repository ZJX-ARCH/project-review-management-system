# Step Wizard UI

User interface for creating and editing evaluation templates using a step-by-step wizard approach.

## ADDED Requirements

### Requirement: Two-step wizard for template creation/editing

The system SHALL provide a two-step wizard interface for users to create or edit templates.

#### Scenario: Navigate through wizard steps

**Given** the user is creating a new template
**When** they land on the create page
**Then** step 1 "基本信息" is active
**And** step 2 "评分项配置" is inactive/disabled
**When** they complete step 1 and click "下一步"
**Then** step 2 becomes active
**And** step 1 validation is complete
**When** they click "上一步"
**Then** step 1 becomes active again
**And** previously entered data is preserved

#### Scenario: Prevent navigation without validation

**Given** the user is on step 1
**And** template code is empty
**When** they click "下一步"
**Then** validation errors are shown
**And** navigation to step 2 is blocked

### Requirement: Left-right panel layout for scoring items

Step 2 MUST use a split panel layout with categories on the left and items on the right.

#### Scenario: Select category to view items

**Given** the user is on step 2
**And** categories "A", "B", "C" exist
**When** they click on category "A" in the left panel
**Then** category "A" is highlighted
**And** the right panel shows items for category "A"
**When** they click on category "B"
**Then** category "B" is highlighted
**And** the right panel shows items for category "B"

#### Scenario: Add item to selected category

**Given** category "技术创新性" is selected
**When** the user clicks "添加评分项" in the right panel
**And** fills in item details
**And** saves
**Then** the item is added to category "技术创新性"
**And** appears in the right panel
**And** category subtotal updates

### Requirement: Responsive and accessible UI

The wizard interface MUST be responsive and work on various screen sizes.

#### Scenario: Mobile view adaptation

**Given** the user is on a mobile device (width < 768px)
**When** they view the template creation page
**Then** the left-right panel layout stacks vertically
**And** categories appear on top
**And** items appear below
**And** all functionality remains accessible

#### Scenario: Long text handling

**Given** a template has a long description (500 characters)
**When** displayed in the list table
**Then** the text is truncated with ellipsis
**And** hovering shows a tooltip with full text
**And** the table layout doesn't break

### Requirement: Visual score indicator

The system MUST provide real-time visual feedback on score totals and validation status.

#### Scenario: Score summary display

**Given** the user is on step 2
**And** template total score is 100
**When** scoring items sum to 85
**Then** the score summary shows:
- "模板总分: 100"
- "当前总分: 85"
- Yellow/warning indicator
- Message: "还需 15 分"

#### Scenario: Score validation success

**Given** template total score is 100
**When** scoring items sum to exactly 100
**Then** the score summary shows:
- Green checkmark icon
- "总分正确: 100"
- "保存" button is enabled

#### Scenario: Score validation error

**Given** template total score is 100
**When** scoring items sum to 105
**Then** the score summary shows:
- Red X icon
- "超出总分: +5"
- "保存" button is disabled
