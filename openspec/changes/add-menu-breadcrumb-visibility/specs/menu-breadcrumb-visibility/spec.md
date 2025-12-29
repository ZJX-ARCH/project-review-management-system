## ADDED Requirements

### Requirement: Menu breadcrumb visibility flag
The system SHALL allow administrators to control whether a menu/route is displayed in the breadcrumb via menu management.

#### Scenario: Hide breadcrumb for transient page
- **GIVEN** a menu is configured with breadcrumb visibility disabled
- **WHEN** a user navigates to that menu route
- **THEN** the breadcrumb SHALL NOT include that route entry

### Requirement: Route meta propagation
The backend SHALL include the menu breadcrumb visibility flag in the route tree response used by the frontend.

#### Scenario: Frontend receives breadcrumb meta
- **WHEN** the frontend requests the user route tree
- **THEN** each route node SHALL include a breadcrumb visibility field

### Requirement: Backward compatible default
If the backend does not provide a breadcrumb visibility field, the frontend SHALL default to hiding breadcrumb for hidden menus and showing it otherwise.

#### Scenario: Backend field absent
- **GIVEN** a route node does not contain breadcrumb visibility data
- **WHEN** the frontend builds route meta
- **THEN** `meta.breadcrumb` SHALL default to `!isHidden`

