# Evaluation Template CRUD

Core CRUD operations for managing evaluation templates with nested scoring items.

## ADDED Requirements

### Requirement: Create evaluation template with nested items

The system SHALL allow users to create a new evaluation template with multiple scoring categories and items through a step wizard interface.

#### Scenario: Create template with valid data

**Given** the user is on the template creation page
**When** they complete basic info (code, name, total score) in step 1
**And** add categories with scoring items in step 2
**And** the sum of item scores equals the template total score
**And** submit the form
**Then** the template and all items are saved to the database
**And** the user is redirected to the template list
**And** a success message is displayed

#### Scenario: Prevent creation with duplicate code

**Given** a template with code "TPL_001" exists
**When** the user tries to create a new template with code "TPL_001"
**Then** a validation error is shown: "模板编码已存在"
**And** the template is not created

#### Scenario: Prevent creation with score mismatch

**Given** the user sets total score to 100
**When** the sum of scoring items equals 95
**And** they try to save
**Then** a validation error is shown: "评分项总分(95.00)必须等于模板总分(100.00)"
**And** the template is not created

### Requirement: Update existing evaluation template

The system SHALL allow users to modify an existing template's basic information and scoring items.

#### Scenario: Update template successfully

**Given** a template with ID 123 exists
**When** the user edits the template
**And** modifies the name to "Updated Template"
**And** adds/removes/modifies scoring items
**And** the new item scores sum equals the total score
**And** submits the changes
**Then** the template is updated in the database
**And** existing items are deleted
**And** new items are inserted
**And** success message is displayed

#### Scenario: Prevent update with duplicate name

**Given** templates "Template A" and "Template B" exist
**When** editing "Template B"
**And** changing its name to "Template A"
**Then** a validation error is shown: "模板名称已存在"
**And** the update is rejected

### Requirement: View template details in read-only mode

The system SHALL allow users to view complete template information including all scoring items in read-only mode.

#### Scenario: View template detail page

**Given** a template with ID 123 exists with 3 categories and 10 items
**When** the user clicks "详情" on the template
**Then** a separate detail page opens (not drawer/modal)
**And** basic information is displayed (code, name, total score, status)
**And** all categories are listed with their items
**And** category subtotals are shown
**And** all fields are non-editable
**And** "返回列表" and "编辑" buttons are available

### Requirement: Delete evaluation templates

The system SHALL allow users to delete one or multiple templates, and MUST cascade delete all related scoring items.

#### Scenario: Delete single template

**Given** a template with ID 123 exists with 10 scoring items
**When** the user clicks delete on the template
**And** confirms the deletion
**Then** the template record is deleted
**And** all 10 related scoring items are deleted (cascade)
**And** the template is removed from the list
**And** success message is shown

#### Scenario: Batch delete templates

**Given** templates with IDs 1, 2, 3 exist
**When** the user selects all three templates
**And** clicks "批量删除"
**And** confirms the deletion
**Then** all three templates are deleted
**And** all their related items are deleted
**And** success message shows "已删除 3 个模板"

### Requirement: Auto-generate template code

The system SHALL provide functionality to auto-generate unique template codes following the TPL_XXX pattern.

#### Scenario: Generate code when no templates exist

**Given** no templates exist in the system
**When** the user clicks "自动生成" button
**Then** the code "TPL_001" is generated
**And** populated in the code field

#### Scenario: Generate incremental code

**Given** templates with codes "TPL_001", "TPL_002", "TPL_005" exist
**When** the user clicks "自动生成" button
**Then** the code "TPL_006" is generated
**And** populated in the code field
