# Scoring Items Management

Nested management of scoring categories and items within evaluation templates.

## ADDED Requirements

### Requirement: Manage scoring categories

The system SHALL allow users to create, edit, delete, and reorder scoring categories within a template.

#### Scenario: Add new category

**Given** the user is editing a template
**And** on the scoring items step
**When** they click "添加大类"
**And** enter category name "技术创新性"
**Then** a new category is added to the list
**And** the category panel shows it
**And** category sort is set automatically

#### Scenario: Delete category with items

**Given** a category "技术创新性" exists with 3 scoring items
**When** the user clicks delete on the category
**And** confirms deletion
**Then** the category is removed
**And** all 3 items in the category are removed
**And** total score is recalculated

#### Scenario: Reorder categories

**Given** categories "A", "B", "C" exist with sorts 1, 2, 3
**When** the user moves category "B" up
**Then** the order becomes "B" (sort 1), "A" (sort 2), "C" (sort 3)
**And** the category panel reflects the new order

### Requirement: Manage scoring items within categories

The system SHALL allow users to add, edit, delete, and reorder scoring items within each category.

#### Scenario: Add scoring item to category

**Given** the user selects category "技术创新性"
**When** they click "添加评分项"
**And** enter item name "技术先进性"
**And** enter max score 15
**And** enter description "评价技术是否处于行业领先"
**And** save
**Then** the item is added to the category
**And** the category subtotal increases by 15
**And** the template total score increases by 15

#### Scenario: Edit scoring item

**Given** an item "技术先进性" with max score 15 exists
**When** the user edits it
**And** changes max score to 20
**And** saves
**Then** the item's score is updated
**And** category subtotal increases by 5
**And** template total score increases by 5

#### Scenario: Delete scoring item

**Given** an item "技术先进性" with max score 15 exists
**When** the user deletes it
**And** confirms
**Then** the item is removed from the category
**And** category subtotal decreases by 15
**And** template total score decreases by 15

### Requirement: Real-time score calculation and validation

The system MUST calculate and validate total scores in real-time as users modify scoring items.

#### Scenario: Display score summary

**Given** the user is on step 2 (scoring items)
**And** template total score is 100
**When** items are added/modified
**Then** the score summary component displays:
- Template total score: 100
- Current calculated sum: (sum of all items)
- Validation status (green checkmark if equal, red X if not)
- Breakdown by category

#### Scenario: Visual feedback for score mismatch

**Given** template total score is 100
**When** current item sum is 95
**Then** the score summary shows red warning
**And** displays message "当前总分 95 分，还需 5 分"
**And** "下一步" button is disabled

#### Scenario: Allow progression when scores match

**Given** template total score is 100
**When** current item sum equals 100
**Then** the score summary shows green checkmark
**And** "保存" button is enabled
