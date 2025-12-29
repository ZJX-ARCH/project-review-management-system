# Change: Add Breadcrumb Visibility Control for Menu Items

## Why

Many form pages (add, edit, view details) do not need to appear in breadcrumb navigation, cluttering the UI unnecessarily. Currently, there is no way to control which menu items should be displayed in the breadcrumb. This change adds a configurable field to allow system administrators to control breadcrumb visibility on a per-menu basis, improving navigation clarity and user experience.

## What Changes

- Add `is_breadcrumb` boolean field to `sys_menu` database table
- Update backend MenuDO entity, MenuReq, and MenuResp models to include breadcrumb visibility field
- Update frontend menu form to show breadcrumb toggle **only for menu type (type=2)**, removing it from directory type (type=1)
- Add validation logic to ensure breadcrumb field is only applicable to menus, not directories or buttons
- Add Liquibase changelog to manage database schema migration
- Update existing menu data with default `is_breadcrumb = true` for backward compatibility

## Impact

- Affected specs: `menu-management`
- Affected code:
  - Database: `continew-admin/continew-server/src/main/resources/db/changelog/mysql/main_table.sql` (new changeset)
  - Backend:
    - `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/entity/MenuDO.java`
    - `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/req/MenuReq.java`
    - `continew-admin/continew-system/src/main/java/top/continew/admin/system/model/resp/MenuResp.java`
  - Frontend:
    - `continew-admin-ui/src/views/system/menu/AddModal.vue` (restrict breadcrumb toggle to menu type only)
    - `continew-admin-ui/src/apis/system/type.ts` (already has isBreadcrumb field)
