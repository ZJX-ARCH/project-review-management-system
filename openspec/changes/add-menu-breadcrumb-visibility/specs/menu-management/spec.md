# Menu Management - Breadcrumb Visibility Control

## ADDED Requirements

### Requirement: Breadcrumb Visibility Control

The system SHALL provide a configurable breadcrumb visibility field for menu items to control whether they appear in the breadcrumb navigation.

#### Scenario: Menu with breadcrumb enabled

- **GIVEN** a menu item with type = 2 (menu)
- **WHEN** `is_breadcrumb` is set to `true`
- **THEN** the menu item SHALL appear in the breadcrumb navigation

#### Scenario: Menu with breadcrumb disabled

- **GIVEN** a menu item with type = 2 (menu)
- **WHEN** `is_breadcrumb` is set to `false`
- **THEN** the menu item SHALL NOT appear in the breadcrumb navigation

#### Scenario: Default breadcrumb value for new menus

- **GIVEN** a new menu item is being created with type = 2 (menu)
- **WHEN** no `is_breadcrumb` value is specified
- **THEN** the system SHALL default to `is_breadcrumb = true`

### Requirement: Breadcrumb Field Restriction to Menu Type

The system SHALL only allow the breadcrumb visibility field to be configured for menu type items (type = 2), not for directories (type = 1) or buttons (type = 3).

#### Scenario: Breadcrumb field displayed for menu type

- **GIVEN** a user is creating or editing a menu item
- **WHEN** the menu type is set to 2 (menu)
- **THEN** the breadcrumb visibility toggle SHALL be displayed in the form

#### Scenario: Breadcrumb field hidden for directory type

- **GIVEN** a user is creating or editing a menu item
- **WHEN** the menu type is set to 1 (directory)
- **THEN** the breadcrumb visibility toggle SHALL NOT be displayed in the form

#### Scenario: Breadcrumb field hidden for button type

- **GIVEN** a user is creating or editing a menu item
- **WHEN** the menu type is set to 3 (button)
- **THEN** the breadcrumb visibility toggle SHALL NOT be displayed in the form

### Requirement: Database Schema Migration

The system SHALL provide a Liquibase changeset to add the `is_breadcrumb` column to the `sys_menu` table following project conventions.

#### Scenario: New column added with default value

- **GIVEN** the database migration is executed
- **WHEN** the changeset is applied
- **THEN** a new column `is_breadcrumb` SHALL be added to `sys_menu` table with type `bit(1)`, default value `b'1'` (true), and comment '是否显示面包屑'

#### Scenario: Existing menu data migrated with default

- **GIVEN** existing menu records in the database
- **WHEN** the changeset is applied
- **THEN** all existing menu records SHALL have `is_breadcrumb` set to `true` for backward compatibility

### Requirement: Backend Model Updates

The system SHALL update all backend menu models to include the breadcrumb visibility field with appropriate validation and documentation.

#### Scenario: MenuDO entity includes breadcrumb field

- **GIVEN** the MenuDO entity class
- **WHEN** reading or writing menu data
- **THEN** the entity SHALL include a `Boolean isBreadcrumb` field mapped to the database column

#### Scenario: MenuReq includes breadcrumb field with schema documentation

- **GIVEN** the MenuReq request class
- **WHEN** creating or updating a menu
- **THEN** the request SHALL include an optional `Boolean isBreadcrumb` field with Swagger schema description

#### Scenario: MenuResp includes breadcrumb field

- **GIVEN** the MenuResp response class
- **WHEN** returning menu data
- **THEN** the response SHALL include a `Boolean isBreadcrumb` field with Swagger schema description

### Requirement: Frontend Form Validation

The system SHALL ensure that the breadcrumb toggle in the frontend form is only rendered for menu type items (type = 2).

#### Scenario: Breadcrumb toggle shown for menu type

- **GIVEN** the menu form is rendered
- **WHEN** the menu type is 2 (menu)
- **THEN** the breadcrumb toggle switch SHALL be visible in the form

#### Scenario: Breadcrumb toggle hidden when type changes to directory

- **GIVEN** the menu form has type set to 2 (menu) with breadcrumb toggle visible
- **WHEN** the user changes type to 1 (directory)
- **THEN** the breadcrumb toggle SHALL be hidden

#### Scenario: Breadcrumb toggle hidden when type changes to button

- **GIVEN** the menu form has type set to 2 (menu) with breadcrumb toggle visible
- **WHEN** the user changes type to 3 (button)
- **THEN** the breadcrumb toggle SHALL be hidden
