# Frontend Mock Layout

This directory is the unified place for frontend mock data and mock services.

## Structure

```text
src/mock/
├── core/      # login, tenant, captcha, user info, menu bootstrap mocks
└── modules/   # business and prototype mock modules, grouped by domain
```

## Rules

- Keep seed data and mock service functions under `src/mock/modules/<domain>/`.
- Pages import mock services with `#/mock/modules/...`.
- Do not store long-lived mock data in `src/views/**`.
- Keep `src/mock/index.ts` as the stable core mock export for existing API code.
- Mock data must be easy to replace with real API calls during handoff.
