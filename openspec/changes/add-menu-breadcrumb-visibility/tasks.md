## 1. Implementation

- [ ] Add `showBreadcrumb` field to menu table/entity and related DTOs
- [ ] Update menu management create/edit endpoints to persist the field
- [ ] Include the field in `getUserRoute` response and route tree building
- [ ] Update frontend `RouteItem` typing and map to `meta.breadcrumb`
- [ ] Add "显示面包屑" switch in menu management UI form
- [ ] Verify: hidden menus and transient pages can be excluded from breadcrumb

## 2. Validation

- [ ] `openspec validate add-menu-breadcrumb-visibility --strict`
- [ ] Backend: menu CRUD + route tree response contains `showBreadcrumb`
- [ ] Frontend: breadcrumb reflects `meta.breadcrumb` and fallback works

