# Proposal: Add Menu Breadcrumb Visibility Control

## Summary

Add a menu management option to control whether a route appears in the breadcrumb, so transient pages (e.g., create/edit/detail) can be excluded without page-level custom logic.

## Problem Statement

The UI breadcrumb currently derives from route meta (`meta.breadcrumb !== false`). However:

- Backend menu management does not provide a field to control breadcrumb visibility.
- Teams must implement page-level workarounds (manual closing tabs or custom logic) for transient pages.
- Behavior is inconsistent across modules and easy to regress.

## Proposed Solution

### What Changes

- **Backend**
  - Add a boolean field on menu records (e.g. `showBreadcrumb`) to control breadcrumb visibility.
  - Include this field in the route tree returned by `getUserRoute`.
  - Default behavior: if not configured, fall back to `!isHidden` (hidden menus usually should not appear in breadcrumb).

- **Frontend**
  - Extend `RouteItem` typing to include breadcrumb visibility.
  - Map backend field into `route.meta.breadcrumb` so `Breadcrumb/index.vue` can use it directly.
  - Add a safe fallback: `meta.breadcrumb = item.showBreadcrumb ?? !item.isHidden` to preserve behavior for older backend versions.

- **Menu Management UI**
  - Add a switch/checkbox "显示面包屑" (default: on) in menu create/edit form.
  - Persist and load this setting.

### Non-Goals

- Redesigning breadcrumb component or route building pipeline.
- Changing how active menu highlighting works.

## Impact

- Affected backend: menu entity/schema, menu CRUD, route tree API.
- Affected frontend: route store mapping (`src/stores/modules/route.ts`) and menu management UI.
- Backward compatibility: frontend fallback keeps current behavior when backend field is absent.

