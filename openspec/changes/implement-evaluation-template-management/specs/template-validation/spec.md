# Template Validation

Validation rules for ensuring data integrity and business rule compliance.

## ADDED Requirements

### Requirement: Total score dictionary validation

Template total score MUST come from predefined dictionary values.

#### Scenario: Select total score from dictionary

**Given** the dictionary "evaluation_template_total_score" has values: 100, 120, 150
**When** the user creates a template
**Then** the total score field is a dropdown/select
**And** only values 100, 120, 150 are available
**And** the user cannot enter custom values

#### Scenario: System admin can add new total score values

**Given** a system admin is logged in
**When** they add a new dictionary item "200分" to "evaluation_template_total_score"
**And** save the dictionary
**Then** the value 200 becomes available in template creation
**And** existing templates with other scores are not affected

### Requirement: Score sum validation

The sum of all scoring item max scores MUST exactly equal the template total score.

#### Scenario: Validate on save

**Given** template total score is 100
**And** scoring items sum to 98
**When** the user tries to save
**Then** a validation error is shown
**And** the error message is "评分项总分(98.00)必须等于模板总分(100.00)"
**And** the save operation is blocked

#### Scenario: Handle decimal precision in validation

**Given** template total score is 100.00
**And** items sum to 99.50 + 0.50 = 100.00
**When** validation runs
**Then** the scores are considered equal
**And** validation passes

### Requirement: Uniqueness validation

Template codes and names MUST be unique across all templates.

#### Scenario: Enforce code uniqueness on create

**Given** template with code "TPL_001" exists
**When** creating a new template with code "TPL_001"
**Then** validation error: "模板编码已存在"
**And** creation is blocked

#### Scenario: Enforce name uniqueness on create

**Given** template with name "技术创新模板" exists
**When** creating a new template with same name
**Then** validation error: "模板名称已存在"
**And** creation is blocked

#### Scenario: Allow same name/code on update of same record

**Given** template ID 123 has code "TPL_001" and name "Template A"
**When** updating template 123
**And** keeping the same code and name
**Then** validation passes
**And** update succeeds

### Requirement: Score range validation

Scoring item max scores MUST be within reasonable range.

#### Scenario: Reject negative scores

**Given** the user enters max score -5 for an item
**When** they try to save
**Then** validation error: "满分值必须大于0"
**And** save is blocked

#### Scenario: Reject unreasonably high scores

**Given** the user enters max score 1000 for an item
**And** template total score is 100
**When** they try to save
**Then** validation error: "满分值(1000)超过模板总分(100)"
**And** save is blocked

### Requirement: Required field validation

Essential fields MUST be provided and non-empty.

#### Scenario: Validate required basic info fields

**Given** the user is on step 1 (basic info)
**When** they leave template code empty
**Or** leave template name empty
**Or** leave total score unselected
**And** try to proceed to step 2
**Then** inline validation errors are shown
**And** navigation to step 2 is blocked

#### Scenario: Validate required category fields

**Given** the user adds a new category
**When** they leave category name empty
**And** try to save
**Then** validation error: "大类名称不能为空"
**And** save is blocked

#### Scenario: Validate required item fields

**Given** the user adds a scoring item
**When** they leave item name empty
**Or** leave max score empty
**And** try to save
**Then** inline validation errors are shown
**And** save is blocked
