# Implementation Tasks

## 1. Database Migration

- [x] 1.1 Add Liquibase changeset to `continew-admin/continew-server/src/main/resources/db/changelog/mysql/main_table.sql`
  - Add `-- changeset` header following project convention (format: `-- changeset [author]:[incrementing-number]`)
  - Add `-- comment` describing the change
  - Add `ALTER TABLE sys_menu ADD COLUMN is_breadcrumb bit(1) DEFAULT b'1' COMMENT '是否显示面包屑';` statement
  - Ensure proper SQL formatting and indentation
- [x] 1.2 Verify Liquibase changeset syntax matches existing patterns in the file
- [ ] 1.3 Test database migration locally to ensure no errors

## 2. Backend Model Updates

- [x] 2.1 Update `MenuDO.java` entity
  - Add `private Boolean isBreadcrumb;` field with JavaDoc comment "是否显示面包屑"
  - Place field after `isHidden` field to maintain logical grouping
- [x] 2.2 Update `MenuReq.java` request class
  - Add `@Schema(description = "是否显示面包屑", example = "true")` annotation
  - Add `private Boolean isBreadcrumb;` field
  - Place field after `isHidden` field
- [x] 2.3 Update `MenuResp.java` response class
  - Add `@Schema(description = "是否显示面包屑", example = "true")` annotation
  - Add `private Boolean isBreadcrumb;` field
  - Place field after `isHidden` field

## 3. Frontend Updates

- [x] 3.1 Update `AddModal.vue` form component
  - Modify breadcrumb form item visibility condition from `v-if="[1, 2].includes(form.type)"` to `v-if="form.type === 2"`
  - Ensure breadcrumb toggle is only shown for menu type (type = 2)
  - Verify existing `v-model="form.isBreadcrumb"` binding works correctly
  - Test type switching behavior: breadcrumb toggle should hide when switching from menu to directory or button
- [x] 3.2 Verify `type.ts` already has `isBreadcrumb?: boolean` field in `MenuResp` interface (already present)

## 4. Backend Route Mapping

- [x] 4.1 Add `breadcrumb` field to `RouteResp.java` response class
  - Add `@Schema(description = "是否显示面包屑", example = "true")` annotation
  - Add `private Boolean breadcrumb;` field
- [x] 4.2 Update `AuthServiceImpl.java` to map `isBreadcrumb` to `breadcrumb`
  - Add `tree.putExtra("breadcrumb", m.getIsBreadcrumb());` in route building logic
- [x] 4.3 Fix database migration to update existing records
  - Add `UPDATE sys_menu SET is_breadcrumb = b'1' WHERE is_breadcrumb IS NULL;` statement
  - Ensures existing menu records have default value (not just new inserts)
- [x] 4.4 Fix route.ts default value handling
  - Change from `item.breadcrumb ?? !item.isHidden` to `item.breadcrumb !== undefined ? item.breadcrumb : true`
  - Ensures explicit true default when breadcrumb is undefined

## 5. Testing and Validation

- [ ] 5.1 Test creating new menu item with breadcrumb enabled
- [ ] 5.2 Test creating new menu item with breadcrumb disabled
- [ ] 5.3 Test editing existing menu item to change breadcrumb setting
- [ ] 5.4 Test type switching: verify breadcrumb toggle hides/shows correctly based on type
- [ ] 5.5 Test that directory items do not show breadcrumb toggle
- [ ] 5.6 Test that button items do not show breadcrumb toggle
- [ ] 5.7 Verify API request/response includes `breadcrumb` field
- [ ] 5.8 Verify database column is created with correct default value
- [ ] 5.9 Verify existing menu records have `is_breadcrumb = true` after migration
- [ ] 5.10 Verify breadcrumb actually hides when `breadcrumb = false` in menu settings

## 6. Validation

- [x] 6.1 Run `openspec validate add-menu-breadcrumb-visibility --strict` to ensure proposal is valid
- [ ] 6.2 Build backend to verify no compilation errors
- [ ] 6.3 Build frontend to verify no TypeScript errors
- [ ] 6.4 Start application to verify Liquibase migration executes successfully
- [ ] 6.5 Verify no Liquibase errors in application logs

## Notes

- **Liquibase Convention**: Each changeset must follow the format `-- changeset author:number` where number increments sequentially. Check the last changeset number in `main_table.sql` before adding.
- **Backward Compatibility**: Default value `b'1'` (true) ensures existing menus continue to show in breadcrumb navigation.
- **Type Restriction**: Breadcrumb control is only meaningful for menu type (2), as directories (1) and buttons (3) do not typically appear in breadcrumb navigation in this system.
